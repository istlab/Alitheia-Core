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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.ohloh.exceptions.UpdaterException;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Job.State;
import eu.sqooss.service.scheduler.JobStateListener;
import eu.sqooss.service.tds.InvalidAccessorException;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService;

/**
 * Implements an UpdaterService to retrieve Ohloh
 * developer information.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * @author Igor Levaja
 * @author Quinten Stokkink
 */
public class UpdaterServiceImpl implements UpdaterService, JobStateListener {

	/**
	 * The Alitheia logger to use
	 */
	private Logger logger = null;

	/**
	 * A reference to the Alitheia core singleton
	 */
	private AlitheiaCore core = null;

	/**
	 * The Alitheia Database Service
	 */
	private DBService dbs = null;

	/** 
	 * Maps project-ids to the jobs that have been scheduled for 
	 * each update target
	 */
	private UpdateScheduler scheduledUpdates;

	/**
	 * Maps updaters to their annotated properties
	 */
	private UpdaterManager manager;

	@Override
	/**
	 * Set the logger, the BundleContext is ignored
	 * 
	 * @param bc The ignored BundleContext
	 * @param l The logger to use
	 */
	public void setInitParams(BundleContext bc, Logger l) {
		this.logger = l;
	}

	@Override
	/** {@inheritDoc} */
	public void shutDown() {

	}

	@Override
	/** {@inheritDoc} */
	public boolean startUp() {
		core = AlitheiaCore.getInstance();
		if (logger != null) {
			logger.info("Got a valid reference to the logger");
		} else {
			System.out.println("ERROR: Updater got no logger");
			return false;
		}

		dbs = core.getDBService();

		manager = new UpdaterManager();
		scheduledUpdates = new UpdateScheduler();

		logger.info("Succesfully started updater service");
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public void registerUpdaterService(Class<? extends MetadataUpdater> clazz) {
		try {
			Updater u = manager.addUpdater(clazz);
			logger.info("Registering updater class " + clazz.getCanonicalName() + 
					" for protocols (" + Arrays.toString(u.protocols()) +
					") and stage " + u.stage());
		} catch (UpdaterException e) {
			logger.error(e.getMessage());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void unregisterUpdaterService(Class<? extends MetadataUpdater> clazz) {
		manager.removeUpdater(clazz);
		logger.info("Unregistering updater class " + clazz.getCanonicalName());
	}

	/**
	 * Retrieve an Updater by a mnemonic
	 * 
	 * @param mnemonic The mnemonic to match
	 * @return The matching Updater, otherwise null
	 */
	public Updater getUpdater(String mnemonic){
		return manager.getUpdaterByMnemonic(mnemonic);
	}

	/**
	 * Retrieve a MetadataUpdater class by a mnemonic
	 * 
	 * @param mnemonic The mnemonic to match
	 * @return The matching MetadataUpdater, otherwise null
	 */
	public Class<? extends MetadataUpdater> getMetadataUpdater(String mnemonic){
		return manager.getMetadataUpdaterByMnemonic(mnemonic);
	}

	/** 
	 * Retrieve all known Updaters
	 * 
	 * @return All updaters
	 */
	public Set<Updater> getUpdaters(){
		return manager.getUpdaters();
	}

	/**{@inheritDoc}*/
	@Override
	public Set<Updater> getUpdaters(StoredProject sp, UpdaterStage st) {
		Set<Updater> upd = new HashSet<Updater>();

		for (Updater updater : getUpdaters(sp)) {
			if (updater.stage().equals(st))
				upd.add(updater);
		}
		return upd;
	}

	/**{@inheritDoc}*/
	@Override
	public Set<Updater> getUpdaters(StoredProject project) {
		Set<Updater> upds = getUpdatersForProject(project);

		//Other updaters
		upds.addAll(manager.getUpdatersByStage(UpdaterStage.PARSE));
		upds.addAll(manager.getUpdatersByStage(UpdaterStage.INFERENCE));
		upds.addAll(manager.getUpdatersByStage(UpdaterStage.DEFAULT));

		return upds;
	}

	/** 
	 * Retrieve all known Updaters for a specific project.
	 * Utilizes the specified schemes.
	 * 
	 * @return All updaters 
	 */
	private Set<Updater> getUpdatersForProject(StoredProject project) {
		Set<Updater> upds = new HashSet<Updater>();
		Set<URI> schemes = getAllSupportedURLSchemes(project);

		for (URI uri : schemes) {
			upds.addAll(manager.getUpdatersByProtocol(uri.getScheme()));
		}

		return upds;
	}

	/**
	 * Retrieve supported URL Schemes for a project.
	 * 
	 * @param project The project to retrieve the schemes from
	 * @return A set of URI's that are supported by the project
	 */
	private Set<URI> getAllSupportedURLSchemes(StoredProject project){
		Set<URI> schemes = new HashSet<URI>();
		ProjectAccessor pa = AlitheiaCore.getInstance().getTDSService().getAccessor(project.getId());
		try {
			//Import phase updaters
			schemes.addAll(pa.getSCMAccessor().getSupportedURLSchemes());
			schemes.addAll(pa.getBTSAccessor().getSupportedURLSchemes());
			schemes.addAll(pa.getMailAccessor().getSupportedURLSchemes());
		} catch (InvalidAccessorException e) {
			logger.warn("Project " + project
					+ " does not include a Mail accessor: " + e.getMessage());
		}

		return schemes;
	}

	/**
	 * Removes an earlier jobs scheduled through addUpdate(). Multiple calls are
	 * made to release all the claims in the set.
	 * 
	 * @param p project to release claims for
	 * @param t set of targets to release
	 */
	private synchronized void removeUpdater(StoredProject p, Updater u) {

		if (p == null) {
			logger.warn("Cannot remove an update job for a null project");
			return;
		}

		Map<Updater, UpdaterJob> m = scheduledUpdates.getScheduleFor(p);
		if (m != null) {
			m.remove(u);
		}
	}

	/** {@inheritDoc}}*/
	public synchronized boolean isUpdateRunning(StoredProject p, Updater u) {
		Map<Updater, UpdaterJob> m = scheduledUpdates.getScheduleFor(p);
		if (m == null) {
			// Nothing in progress
			return false;
		}

		if (m.keySet().contains(u)) {
			return true;
		}
		return false;
	}

	/**
	 * Create the physical MetadataUpdater for a certain 
	 * project + updater combination.
	 * 
	 * @param sp The project
	 * @param u The updater
	 * @return The corresponding physical MetadataUpdater
	 * @throws InstantiationException If the MetadataUpdater could not be initialized
	 * @throws IllegalAccessException If the MetadataUpdater could not be accessed for initialization
	 */
	public MetadataUpdater metadataUpdaterForUpdater(StoredProject sp, Updater u) throws InstantiationException, IllegalAccessException{
		MetadataUpdater upd = manager.getMetadataUpdater(u);
		upd.setUpdateParams(sp, logger);
		return upd;
	}

	/**
	 * Does a bit of clean up when a job has finished (either by error or
	 * normally)
	 */
	public synchronized void jobStateChanged(Job j, State newState) {

		if (newState.equals(State.Error) || newState.equals(State.Finished)) {
			Updater ut = scheduledUpdates.getUpdaterFor(j);

			if (ut == null) {
				logger.error("Update job finished with state " + newState
						+ " but was not scheduled. That's weird...");
				return;
			}

			if (!dbs.isDBSessionActive())
				dbs.startDBSession();
			StoredProject sp = StoredProject.loadDAObyId(scheduledUpdates.getProjectFor(j), StoredProject.class);
			removeUpdater(sp, ut);

			if (newState.equals(State.Error)) {
				logger.warn(ut + " updater job for project " + sp
						+ " did not finish properly");
			}
			dbs.commitDBSession();
		}
	}

	/**{@inheritDoc}*/
	@Override
	public boolean update(StoredProject project) {
		return update(project, null, null);
	}

	/**{@inheritDoc}*/
	@Override
	public boolean update(StoredProject project, UpdaterStage stage) {
		return update(project, stage, null);
	}

	/**{@inheritDoc}*/
	@Override
	public boolean update(StoredProject sp, Updater u) {
		if (!getUpdaters(sp).contains(u))
			return false;
		return update(sp, null, u);
	}

	/**{@inheritDoc}*/
	@Override
	public boolean update(StoredProject sp, String updater) {
		Updater u = manager.getUpdaterByMnemonic(updater);
		if (u == null) {
			logger.warn("No such updater: " + updater);
			return false;
		}
		return update(sp, u);
	}

	/**
	 * Schedule an UpdateJob for the Alitheia scheduler for a tuple of
	 * a project, updater stage and updater.
	 * 
	 * @param project The project to update (not null)
	 * @param stage Updater stage to use if updater is null (updates all stages if null)
	 * @param updater The Updater to receive the UpdaterStage from (uses stage if null) 
	 * @return True if, and only if all the jobs for all the stages have been scheduled correctly (otherwise supplies a log entry)
	 */
	private boolean update(StoredProject project, UpdaterStage stage, Updater updater) {

		Boolean returnValue = returnValueForUpdate(project, stage, updater);
		if (returnValue != null)
			return returnValue;

		logger.info("Request to update project:" + project.getName()  
				+ " stage:" + (stage == null?stage:"all") 
				+ " updater:" + (updater == null?updater:"all"));

		//Construct a list of updater stages to iterate later
		List<UpdaterStage> stages = getUpdaterStagesFor(stage, updater);

		ProjectUpdater pu = new ProjectUpdater(project, updater, scheduledUpdates, this, logger);

		return pu.update(stages);
	}

	/**
	 * Check a combination of a StoredProject, an UpdaterStage and
	 * an Updater for mismatches.
	 * 
	 * @param project The project
	 * @param stage The stage to use
	 * @param updater The updater to use with it
	 * @return The return value to return with or null if the update can continue
	 */
	private Boolean returnValueForUpdate(StoredProject project, UpdaterStage stage, Updater updater){

		if (project == null) {
			logger.info("Bad project name for update.");
			return false;
		} 

		ClusterNodeService cns = null;
		/// ClusterNode Checks - Clone to MetricActivatorImpl
		cns = core.getClusterNodeService();

		if (cns==null) {
			logger.warn("ClusterNodeService reference not found " +
					"- ClusterNode assignment checks will be ignored");
		} else {            

			ClusterNode node = project.getClusternode();

			if (node == null) {
				logger.warn("Project " + project + 
						" not assigned to any cluster node");
			} else { 
				// project is assigned , check if it is assigned to this Node
				if (!cns.isProjectAssigned(project)) {
					logger.warn("Project " + project.getName() + 
							" is not assigned to this ClusterNode - Ignoring update");
					// TODO: Clustering - further implementation:
					// If needed, forward Update to the appropriate ClusterNode!
					return true;   
				}                
			}
		} 

		return null;
	}

	/**
	 * Construct a list of stages to iterate over for a certain
	 * Updater + UpdaterStage combination.
	 * 
	 * @param stage The stage to use if updater is null, will use all if null
	 * @param updater The updater to infer the stage from
	 * @return A list of stages to check for this combination
	 */
	private List<UpdaterStage> getUpdaterStagesFor(UpdaterStage stage, Updater updater){
		List<UpdaterStage> stages = new ArrayList<UpdaterStage>(); 

		if (updater == null) {
			if (stage == null) {
				stages.add(UpdaterStage.IMPORT);
				stages.add(UpdaterStage.PARSE);
				stages.add(UpdaterStage.INFERENCE);
				stages.add(UpdaterStage.DEFAULT);
			} else {
				stages.add(stage);
			}
		} else {
			stages.add(updater.stage());
		}

		return stages;
	}

}
