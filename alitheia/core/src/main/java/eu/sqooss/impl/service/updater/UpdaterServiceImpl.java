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

package eu.sqooss.impl.service.updater;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.cluster.ClusterNodeActionException;
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
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.util.BidiMap;
import eu.sqooss.service.util.GraphTS;

public class UpdaterServiceImpl implements UpdaterService, JobStateListener {

    private Logger logger = null;
    private AlitheiaCore core = null;
    private BundleContext context;
    private DBService dbs = null;
    
    /* Maps project-ids to the jobs that have been scheduled for 
     * each update target*/
    private ConcurrentMap<Long,Map<Updater, UpdaterJob>> scheduledUpdates;
    
    /* List of registered updaters */
    private BidiMap<Updater, Class<? extends MetadataUpdater>> updaters;

    /* UpdaterService interface methods*/
    /** {@inheritDoc} */
    @Override
    public void registerUpdaterService(Class<? extends MetadataUpdater> clazz) {

        Updater u = clazz.getAnnotation(Updater.class);

        if (u == null) {
            logger.error("Class " + clazz + " is missing required annotation" +
            		" @Updater");
            return;
        }

        if (getUpdaterByMnemonic(u.mnem()) != null) {
            logger.error("Mnemonic already used by updater " 
                    + updaters.get(getUpdaterByMnemonic(u.mnem())));
            return;
        }
        
        
        
        updaters.put(u, clazz);
            
        logger.info("Registering updater class " + clazz.getCanonicalName() + 
                " for protocols (" + Arrays.toString(u.protocols()) +
                ") and stage " + u.stage());
    }

    /** {@inheritDoc} */
    @Override
    public void unregisterUpdaterService(Class<? extends MetadataUpdater> clazz) {
        updaters.remove(updaters.getKey(clazz));
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
        Updater u = getUpdaterByMnemonic(updater);
        if (u == null) {
            logger.warn("No such updater: " + updater);
            return false;
        }
        return update(sp, u);
    }

    /**{@inheritDoc}*/
    @Override
    public Set<Updater> getUpdaters(StoredProject project) {
        Set<Updater> upds = new HashSet<Updater>();
        TDSService tds = AlitheiaCore.getInstance().getTDSService();
        ProjectAccessor pa = tds.getAccessor(project.getId());
        Set<URI> schemes = new HashSet<URI>();

        //Import phase updaters
        try {
            schemes.addAll(pa.getSCMAccessor().getSupportedURLSchemes());
            schemes.addAll(pa.getBTSAccessor().getSupportedURLSchemes());
            schemes.addAll(pa.getMailAccessor().getSupportedURLSchemes());
        } catch (InvalidAccessorException e) {
            logger.warn("Project " + project
                    + " does not include a Mail accessor: " + e.getMessage());
        }

        for (URI uri : schemes) {
            upds.addAll(getUpdatersByProtocol(uri.getScheme()));
        }
        
        //Other updaters
        upds.addAll(getUpdatersByStage(UpdaterStage.PARSE));
        upds.addAll(getUpdatersByStage(UpdaterStage.INFERENCE));
        upds.addAll(getUpdatersByStage(UpdaterStage.DEFAULT));
        
        return upds;
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
        Map<Updater, UpdaterJob> m = scheduledUpdates.get(p.getId());
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
       
        /* Get a reference to the core service*/
        ServiceReference serviceRef = null;
        serviceRef = context.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) context.getService(serviceRef);
        if (logger != null) {
            logger.info("Got a valid reference to the logger");
        } else {
            System.out.println("ERROR: Updater got no logger");
        }
        
        dbs = core.getDBService();
        
        updaters = new BidiMap<Updater, Class<? extends MetadataUpdater>>();
        scheduledUpdates = new ConcurrentHashMap<Long, Map<Updater, UpdaterJob>>();
        
        logger.info("Succesfully started updater service");
        return true;
    }

    @Override
    public void setInitParams(BundleContext bc, Logger l) {
        this.context = bc;
        this.logger = l;
    }

    /*Private service methods*/
    private List<Updater> getUpdatersByProtocol(String protocol) {
        List<Updater> upds = new ArrayList<Updater>();
        
        for (Updater u : updaters.keySet()) {
            for (String p : u.protocols()) {
                if (protocol.equals(p)) {
                    upds.add(u);
                    break;
                }
            }
        }
        
        return upds;
    }
 
    private List<Updater> getUpdatersByStage(UpdaterStage u) {
        List<Updater> upds = new ArrayList<Updater>();
       
        for (Updater upd : updaters.keySet()) {
            if (upd.stage().equals(u))
                upds.add(upd);
        }
        
        return upds;
    }
    
    private Updater getUpdaterByMnemonic(String updater) {
        for (Updater upd : updaters.keySet()) {
            if (upd.mnem().equals(updater))
                return upd;
        }
        return null;
    }

    private boolean checkDependencies(Updater upd) {
        boolean met = true;
        for (String dep : upd.dependencies()) {
            boolean found = false;
            for (Updater other : updaters.keySet()) {
                if (dep.equals(other.mnem())) {
                    if (other.stage().equals(upd.stage())) {
                        found = true;
                        break;
                    } else {
                        logger.error("Updater <" + upd.mnem() + ">-" + 
                                upd.stage() + 
                                " depends on other stage updater <" 
                                + other.mnem() + ">-" + other.stage());
                        return false;
                    }
                }
            }
            met &= found;
        }
        return met;
    }
    
    /**
     * Add an update job of the given type or the specific updater for the project. 
     */
    private boolean update(StoredProject project, UpdaterStage stage, Updater updater) {
        
        ClusterNodeService cns = null;
        
        if (project == null) {
            logger.info("Bad project name for update.");
            return false;
        }     
        
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
       
        logger.info("Request to update project:" + project.getName()  
                + " stage:" + (stage == null?stage:"all") 
                + " updater:" + (updater == null?updater:"all"));
        
        //Construct a list of updater stages to iterate later
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
                
                // Topologically sort updaters within the same stage
                List<Updater> updForStage = new ArrayList<Updater>();
                updForStage.addAll(getUpdaters(project, us));
                GraphTS<Updater> graph = 
                    new GraphTS<Updater>(updForStage.size());
                BidiMap<Updater, Integer> idx = 
                    new BidiMap<Updater, Integer>();

                //Construct a adjacency matrix for dependencies
                for (Updater u : updForStage) {
                    if (!checkDependencies(u))
                        return false;
                    if (!idx.containsKey(u)) {
                        int n = graph.addVertex(u);
                        idx.put(u, n);
                    }

                    for (String dependency : u.dependencies()) {
                        Updater dep = getUpdaterByMnemonic(dependency);

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
                    MetadataUpdater upd = updaters.get(u).newInstance();
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
                        uj = scheduledUpdates.get(project.getId()).get(u);
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
                        dependencies.add(updaters.get(getUpdaterByMnemonic(s)));
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
            List<Job> toQueue = new ArrayList<Job>();
            for (Job job : jobs) {
                if (!scheduledUpdates.containsKey(project.getId()))
                    scheduledUpdates.put(project.getId(),
                            new HashMap<Updater, UpdaterJob>());

                //Don't schedule a job that has been scheduled before
                Collection<UpdaterJob> schedJobs = scheduledUpdates.get(project.getId()).values();
                boolean dontSchedule = false;
                for (Job j : schedJobs) {
                    if (job.equals(j)) {
                        dontSchedule = true; 
                        break;
                    }
                }

                if (dontSchedule) {
                    logger.warn("Job " + job + " has been scheduled before, ignoring");
                    continue;
                }
                toQueue.add(job);
                //DependencyJobs don't need to be tracked
                if (!(job instanceof UpdaterJob))
                    continue;
                scheduledUpdates.get(project.getId()).put(
                        toSchedule.getKey(job), (UpdaterJob)job);
            }
            AlitheiaCore.getInstance().getScheduler().enqueueBlock(toQueue);
        } catch (SchedulerException e) {
            logger.error("Cannot schedule update job(s):" + e.getMessage(), e);
            return false;
        } catch (InstantiationException e) {
            logger.error("Cannot instantiate updater:" + e.getMessage(), e);
            return false;
        } catch (IllegalAccessException e) {
            logger.error("Cannot load updater class:" + e.getMessage(), e);
            return false;
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
        
        Map<Updater, UpdaterJob> m = scheduledUpdates.get(p.getId());
        if (m != null) {
            m.remove(u);
        }
    }

    /**
     * Does a bit of clean up when a job has finished (either by error or
     * normally)
     */
    public synchronized void jobStateChanged(Job j, State newState) {

        Long projectId = null;

        for (Long pid : scheduledUpdates.keySet()) {
            if (scheduledUpdates.get(pid).containsValue(j)) {
                projectId = pid;
                break;
            }
        }

        Map<Updater, UpdaterJob> updates = scheduledUpdates.get(projectId);
        Updater ut = null;
        for (Updater t : updates.keySet()) {
            if (updates.get(t).equals(j)) {
                ut = t;
                break;
            }
        }

        if (newState.equals(State.Error) || newState.equals(State.Finished)) {
            if (ut == null) {
                logger.error("Update job finished with state " + newState
                        + " but was not scheduled. That's weird...");
                return;
            }

            if (!dbs.isDBSessionActive())
                dbs.startDBSession();
            StoredProject sp = StoredProject.loadDAObyId(projectId, StoredProject.class);
            removeUpdater(sp, ut);

            if (newState.equals(State.Error)) {
                logger.warn(ut + " updater job for project " + sp
                        + " did not finish properly");
            }
            dbs.commitDBSession();
        }
    }
    
    /*Dummy jobs to ensure correct sequencing of jobs within updater stages */
    private class DependencyJob extends Job {
        private String name;
        private DependencyJob(){};
        public DependencyJob(String name) { this.name = name;}
        public long priority() {return 0;}
        protected void run() throws Exception {}
        
        @Override
        public String toString() {
            return "Dependency Job: " + name;
        }
    }
}
