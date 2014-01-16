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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.ohloh.exceptions.DependencyException;
import eu.sqooss.impl.service.ohloh.exceptions.UpdaterException;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Job.State;
import eu.sqooss.service.scheduler.JobStateListener;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.tds.InvalidAccessorException;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.util.BidiMap;
import eu.sqooss.service.util.GraphTS;

public class UpdaterServiceImpl implements UpdaterService, JobStateListener {

    private Logger logger = null;
    private AlitheiaCore core = null;
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

    /* UpdaterService interface methods*/
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

    /**{@inheritDoc}*/
    @Override
    public Set<Updater> getUpdaters(StoredProject project) {
        Set<Updater> upds = getUpdatersForScheme(project);
        
        //Other updaters
        upds.addAll(manager.getUpdatersByStage(UpdaterStage.PARSE));
        upds.addAll(manager.getUpdatersByStage(UpdaterStage.INFERENCE));
        upds.addAll(manager.getUpdatersByStage(UpdaterStage.DEFAULT));
        
        return upds;
    }

	private Set<Updater> getUpdatersForScheme(StoredProject project) {
		Set<Updater> upds = new HashSet<Updater>();
		Set<URI> schemes = getAllSupportedURLSchemes(project);

        for (URI uri : schemes) {
            upds.addAll(manager.getUpdatersByProtocol(uri.getScheme()));
        }
        
        return upds;
	}

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
    
    /* AlitheiaCoreService interface methods*/
    @Override
    public void shutDown() {
        
    }

    @Override
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

    @Override
    public void setInitParams(BundleContext bc, Logger l) {
        this.logger = l;
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
    	for (Updater other : manager.getUpdaters()) {
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
    
    /**
     * Add an update job of the given type or the specific updater for the project. <br>
     * 
     * 1. If project is null log an info message and return false <br>
     * 2. If the global ClusterNodeServer is null or there is no ClusterNode for this project, log a warning <br>
     * 2.a. Otherwise if this is not assigned to the known node, log a warning and return true <br>
     * 3. Log an info message that this project is being updated <br>
     * 4. If the updater is not null, add the updater stage to the stages <br>
     * 4.a. Otherwise if the stage is not null, add the stage to the stages <br>
     * 4.a.a. Otherwise add all possible updater stages to the stages <br>
     * 5. Schedule all the jobs in correct order, regarding dependencies (see code for detailed description)
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
        
        /*
         * For each update stage add updaters in topologically sorted order. Add
         * dependencies to jobs to serialize execution between updaters in the
         * same stage and add fake dependency jobs to serialise execution among
         * stages. The result of this loop is a list of jobs with properly set
         * dependencies to ensure correct execution.
         */
        List<Job> jobs = new LinkedList<Job>();
        BidiMap<Updater, Job> toSchedule = new BidiMap<Updater, Job>();
        DependencyJob oldDepJob = null;
        try {
            for (UpdaterStage us : stages) {
                
                List<Updater> updForStage = topoSort(getUpdaters(project, us));

                // We now have updaters in correct execution order
                DependencyJob depJob = new DependencyJob(us.toString());

                List<String> deps = new ArrayList<String>();
                if (updater != null)
                    deps = Arrays.asList(updater.dependencies());

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
                    MetadataUpdater upd = manager.getMetadataUpdater(u);
                    upd.setUpdateParams(project, logger);

                    UpdaterJob uj = null;
                    /*
                     * If an update has already been scheduled for a specific
                     * updater, just re-use this job for dependency tracking.
                     * Also put the job in the queue of jobs that are about to
                     * be scheduled to allow other jobs to declare dependencies
                     * to it. If in the mean time the dependent job finishes
                     * execution, the dependee will just continue execution.
                     */
                    if (isUpdateRunning(project, u)) {
                        uj = scheduledUpdates.getJobFor(project, u);
                    } else {
                        uj = new UpdaterJob(upd);
                        uj.addJobStateListener(this);
                        toSchedule.put(u, uj);
                    }

                    // Add dependency to stage level job
                    depJob.addDependency(uj);
                    jobs.add(uj);
                    
                    if (isUpdateRunning(project, u))
                        continue;
                    
                    //Add dependency to previous stage dependency job
                    if (oldDepJob != null)
                        uj.addDependency(oldDepJob);

                    // Add dependencies to previously scheduled jobs
                    // within the same stage
                    List<Class<? extends MetadataUpdater>> dependencies = 
                        new ArrayList<Class<? extends MetadataUpdater>>();

                    for (String s : u.dependencies()) {
                        dependencies.add(manager.getMetadataUpdaterByMnemonic(s));
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

                if (oldDepJob != null)
                    depJob.addDependency(oldDepJob);

                jobs.add(depJob);
                oldDepJob = depJob;
            }

            //Enqueue jobs
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

	private List<Updater> topoSort(Set<Updater> updaters) throws DependencyException {
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
		        Updater dep = manager.getUpdaterByMnemonic(dependency);

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

}
