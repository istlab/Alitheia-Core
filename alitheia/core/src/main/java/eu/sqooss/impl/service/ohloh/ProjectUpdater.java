/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,
 *                 Athens, Greece.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.impl.service.ohloh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.ohloh.exceptions.DependencyException;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;
import eu.sqooss.service.util.BidiMap;
import eu.sqooss.service.util.GraphTS;

/**
 * Updates a single StoredProject + Updater combination.
 * 
 * @author Igor Levaja
 * @author Quinten Stokkink
 */
public class ProjectUpdater {

	/**
	 * The project in question
	 */
	private StoredProject project;

	/**
	 * The Updater in question
	 */
	private Updater updater;

	/**
	 * The service's scheduler
	 */
	private UpdateScheduler scheduledUpdates;

	/**
	 * The service itself
	 */
	private UpdaterServiceImpl service;

	/**
	 * The Alitheia core Logger
	 */
	private Logger logger;

	/**
	 * The list of jobs we produce
	 */
	List<Job> jobs;

	/**
	 * The jobs that need scheduling
	 */
	BidiMap<Updater, Job> toSchedule;

	/**
	 * The previous DependencyJob 
	 * (used in UpdateStages and jobFromStaged)
	 */
	DependencyJob oldDepJob;

	/**
	 * Create a new ProjectUpdater belonging to a certain
	 * project and a certain updater, given a scheduler, 
	 * service and logger.
	 * 
	 * @param project The project
	 * @param updater The updater
	 * @param scheduledUpdates The update scheduler
	 * @param service The service implementation
	 * @param logger The logger
	 */
	public ProjectUpdater(StoredProject project, 
			Updater updater,
			UpdateScheduler scheduledUpdates,
			UpdaterServiceImpl service,
			Logger logger){
		this.project = project;
		this.updater = updater;
		this.scheduledUpdates = scheduledUpdates;
		this.service = service;
		this.logger = logger;

		this.jobs = new LinkedList<Job>();
		this.toSchedule = new BidiMap<Updater, Job>();
		this.oldDepJob = null;
	}

	/**
	 * Update our project over the supplied stages.
	 * 
	 * @param stages The stages to iterate over
	 * @return True iff the update was successful
	 */
	public boolean update(List<UpdaterStage> stages){
		try {
			updateStages(stages);
			enqueueJobs(project, toSchedule, jobs);
		} catch (SchedulerException e) {
			logger.error("Cannot schedule update job(s):" + e.getMessage(), e);
			return false;
		} catch (InstantiationException e) {
			logger.error("Cannot instantiate updater:" + e.getMessage(), e);
			return false;
		} catch (IllegalAccessException e) {
			logger.error("Cannot load updater class:" + e.getMessage(), e);
			return false;
		} catch (DependencyException d) {
			return false;
		}
		return true;
	}

	/**
	 * Iterate over stages to update our project with.
	 * 
	 * @param stages The stages to iterate over
	 * @throws InstantiationException If a MetadataUpdater could not be initialized
	 * @throws IllegalAccessException If a MetadataUpdater could not be accessed for initialization
	 * @throws DependencyException If a job dependency was not met
	 * @throws SchedulerException If the scheduler could not schedule our job
	 */
	private void updateStages(List<UpdaterStage> stages) throws InstantiationException, IllegalAccessException, DependencyException, SchedulerException{
		for (UpdaterStage us : stages) {
			List<Updater> updForStage = topoSort(service.getUpdaters(project, us));

			DependencyJob depJob = new DependencyJob(us.toString());

			List<String> deps = new ArrayList<String>();
			if (updater != null)
				deps = Arrays.asList(updater.dependencies());

			jobFromStaged(updForStage, deps, depJob);

			if (oldDepJob != null)
				depJob.addDependency(oldDepJob);

			jobs.add(depJob);
			oldDepJob = depJob;
		}
	}

	/**
	 * Given a list of stages updaters with a list of their dependencies,
	 * update the DependencyJob with this information.
	 * 
	 * @param updForStage The staged Updaters
	 * @param deps Their dependencies
	 * @param depJob The job to update
	 * @throws InstantiationException If a MetadataUpdater could not be initialized
	 * @throws IllegalAccessException If a MetadataUpdater could not be accessed for initialization
	 * @throws SchedulerException If the scheduler could not schedule our job
	 */
	private void jobFromStaged(List<Updater> updForStage, List<String> deps, DependencyJob depJob) throws InstantiationException, IllegalAccessException, SchedulerException{
		for (Updater u : updForStage) {
			/*
			 * Ignore the current in case we have an updater specified
			 * as argument unless the updater is the same as the
			 * argument of the current updater is a dependency to the
			 * one we have as argument :-)
			 */
			if (updater != null &&
					!updater.equals(u) &&
					!deps.contains(u.mnem())) {
				continue;
			}

			// Create an updater job
			UpdaterJob uj = makeJobFor(u);

			// Add dependency to stage level job
			depJob.addDependency(uj);
			jobs.add(uj);

			if (service.isUpdateRunning(project, u))
				continue;

			//Add dependency to previous stage dependency job
			if (oldDepJob != null)
				uj.addDependency(oldDepJob);

			// Add dependencies to previously scheduled jobs
			// within the same stage
			addDependenciesToScheduled(u, uj);
		}
	}

	/**
	 * Add all the dependencies of an Updater to an UpdaterJob
	 * 
	 * @param u The Updater to get the dependencies from
	 * @param uj The UpdaterJob to update
	 * @throws SchedulerException If the dependency could not be added
	 */
	private void addDependenciesToScheduled(Updater u, UpdaterJob uj) throws SchedulerException{
		List<Class<? extends MetadataUpdater>> dependencies = 
				new ArrayList<Class<? extends MetadataUpdater>>();

		for (String s : u.dependencies()) {
			dependencies.add(service.getMetadataUpdater(s));
		}

		for (Class<? extends MetadataUpdater> d : dependencies) {
			for (Job j : jobs) {
				if (!(j instanceof UpdaterJob))
					continue;
				if (((UpdaterJob) j).getUpdater().getClass().equals(d)) {
					uj.addDependency(j);
				}
			}
		}
	}

	/**
	 * If an update has already been scheduled for a specific
	 * updater, just re-use this job for dependency tracking.
	 * Also put the job in the queue of jobs that are about to
	 * be scheduled to allow other jobs to declare dependencies
	 * to it. If in the mean time the dependent job finishes
	 * execution, the dependee will just continue execution.
	 * 
	 * @param u The Updater specifics
	 * @param upd The MetaUpdater to use
	 * @return The UpdaterJob to use (either reused or new)
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private UpdaterJob makeJobFor(Updater u) throws InstantiationException, IllegalAccessException{
		UpdaterJob uj = null;
		MetadataUpdater upd = service.metadataUpdaterForUpdater(project, u);

		if (service.isUpdateRunning(project, u)) {
			uj = scheduledUpdates.getJobFor(project, u);
		} else {
			uj = new UpdaterJob(upd);
			uj.addJobStateListener(service);
			toSchedule.put(u, uj);
		}

		return uj;
	}

	/**
	 * Feed jobs to the scheduler (if they haven't been scheduled yet and
	 * if they are valid jobs).
	 * 
	 * @param project The project the jobs come from
	 * @param toSchedule The job to updater mapping
	 * @param jobs Another copy of the list of jobs
	 * @throws SchedulerException
	 */
	private void enqueueJobs(StoredProject project, BidiMap<Updater, Job> toSchedule, List<Job> jobs) throws SchedulerException{
		List<Job> toQueue = new ArrayList<Job>();

		for (Job job : jobs) {
			//Don't schedule a job that has been scheduled before
			if (!shouldSchedule(job, project)) {
				logger.warn("Job " + job + " has been scheduled before, ignoring");
				continue;
			}

			toQueue.add(job);
			//DependencyJobs don't need to be tracked
			if (!(job instanceof UpdaterJob))
				continue;

			scheduledUpdates.addUpdater(project, toSchedule.getKey(job), (UpdaterJob) job);
		}
		AlitheiaCore.getInstance().getScheduler().enqueueBlock(toQueue);
	}

	/**
	 * Check if a job is hasn't been scheduled yet.
	 * 
	 * @param job The job to check
	 * @param project The project it belongs to
	 * @return Whether or not the job still has to be scheduled for this project
	 */
	private boolean shouldSchedule(Job job, StoredProject project){
		Collection<UpdaterJob> schedJobs = scheduledUpdates.getJobsFor(project);
		for (Job j : schedJobs) {
			if (job.equals(j)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Topologically sort a set of updaters.
	 * 
	 * @param updaters The updaters to sort
	 * @return A list containing the topologically sorted updaters
	 * @throws DependencyException If an updater is missing dependencies
	 */
	public List<Updater> topoSort(Set<Updater> updaters) throws DependencyException {
		// Topologically sort updaters within the same stage
		List<Updater> updForStage = new ArrayList<Updater>();
		updForStage.addAll(updaters);
		GraphTS<Updater> graph = 
				new GraphTS<Updater>(updForStage.size());
		BidiMap<Updater, Integer> idx = 
				new BidiMap<Updater, Integer>();

		//Construct a adjacency matrix for dependencies
		for (Updater u : updForStage) {
			if (!checkDependencies(u))
				throw new DependencyException("Dependencies of an updater do not exist");  
			if (!idx.containsKey(u)) {
				int n = graph.addVertex(u);
				idx.put(u, n);
			}

			for (String dependency : u.dependencies()) {
				Updater dep = service.getUpdater(dependency);

				// Updaters are allowed to introduce self depedencies
				if (u.equals(dep)) {
					continue;
				}

				if (!idx.containsKey(dep)) {
					int n = graph.addVertex(dep);
					idx.put(dep, n);
				}
				graph.addEdge(idx.get(u), idx.get(dep));
			}
		}

		// Topo-sort
		updForStage = graph.topo();
		return updForStage;
	}

	/**
	 * Check if all the dependencies of an updater exist 
	 *  
	 * @param upd The Updater to have its dependencies checked
	 * @return True if, and only if all of the dependencies of upd are contained in our set of updaters 
	 */
	private boolean checkDependencies(Updater upd) {
		for (String dep : upd.dependencies()) {
			if (!dependencyExists(dep, upd))
				return false;
		}
		return true;
	}

	/**
	 * Check if a mnemonic corresponds to a known updater, given
	 * a source updater.
	 * 
	 * @param dependency The mnemonic to check the existance of
	 * @param updater The updater the dependency mnemonic belongs to
	 * @return Whether this dependency mnemonic is known
	 */
	private boolean dependencyExists(String dependency, Updater updater){
		for (Updater other : service.getUpdaters()) {
			if (dependency.equals(other.mnem())) {
				if (!other.stage().equals(updater.stage())) {
					logger.error("Updater <" + updater.mnem() + ">-" + 
							updater.stage() + 
							" depends on other stage updater <" 
							+ other.mnem() + ">-" + other.stage());
					return false;
				}
				return true;
			}
		}
		return false;
	}

}
