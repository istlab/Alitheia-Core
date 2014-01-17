/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

package eu.sqooss.impl.service.metricactivator;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import eu.sqooss.service.abstractmetric.InvocationOrder;
import org.osgi.framework.BundleContext;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.SchedulerHints;
import eu.sqooss.service.cluster.ClusterNodeActionException;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.EncapsulationUnit;
import eu.sqooss.service.db.ExecutionUnit;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.MailingListThread;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.MetricType.Type;
import eu.sqooss.service.db.NameSpace;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.util.GraphTS;

@Singleton
public class MetricActivatorImpl  implements MetricActivator {

    /** The parent bundle's context object. */
    private BundleContext bc;

    private Logger logger;
    private PluginAdmin pa;
    private DBService dbs;
    private Scheduler sched;
    private ClusterNodeService cns;
    private MetricActivatorJobFactory maJobFactory;
    
    private boolean fastSync = false;

    private AtomicLong priority;
    
    private HashMap<MetricType.Type, Class<? extends DAObject>> metricTypesToActivators;
    
    @Inject
    public MetricActivatorImpl(PluginAdmin pa, DBService dbs, 
            Scheduler sched, ClusterNodeService cns, MetricActivatorJobFactory maJobFactory) {
        this.pa = pa;
        this.dbs = dbs;
        this.sched = sched;
        this.cns = cns;
        this.maJobFactory = maJobFactory;
    }

    @Override
	public <T extends DAObject> void runMetric(T resource, AlitheiaPlugin ap) {
    	Class<? extends DAObject> activator = resource.getClass();
    	Job j = maJobFactory.create((AbstractMetric)ap, resource.getId(), logger, 
                metricTypesToActivators.get(activator),
                priority.incrementAndGet(),
                fastSync);
    	try {
            sched.enqueue(j);
        } catch (SchedulerException e) {
            logger.error("Could not start metric scheduler job");
        }
	}

    /**{@inheritDoc}*/
    @Override
    public void syncMetrics(StoredProject sp, Class<? extends DAObject> actType) {
    	if (!canRunOnHost(sp))
            return;
        
        List<PluginInfo> plugins = pa.listPluginProviders(actType);
        
        if (plugins == null || plugins.size() == 0) {
            logger.warn("No metrics found for activation type " 
                    + actType.getName());
            return;
        }
        
        /* Fire up plug-ins */
        for (PluginInfo pi : plugins) {
           AbstractMetric m = (AbstractMetric) bc.getService(pi.getServiceRef());
           try {
               sched.enqueue(new MetricSchedulerJob(m, sp));
           } catch (SchedulerException e) {
               logger.error("Could not start metric scheduler job");
           }
        }
    }

    /**{@inheritDoc}*/
    @SuppressWarnings("unchecked")
    @Override
    public void syncMetrics(AlitheiaPlugin ap) {
        List<StoredProject> lp = 
            (List<StoredProject>) dbs.doHQL("from StoredProject");
        
        for(StoredProject sp : lp) {
            syncMetric(ap, sp);
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void syncMetrics(StoredProject sp) {
        Collection<PluginInfo> plugins = pa.listPlugins();
        
        for(PluginInfo p : plugins) {
            AlitheiaPlugin ap = 
                (AlitheiaPlugin) bc.getService(p.getServiceRef());
            syncMetric(ap, sp);
        }
    }

    /**{@inheritDoc}*/
    @Override
    public void syncMetric(AlitheiaPlugin m, StoredProject sp) {
        if (!canRunOnHost(sp))
            return;
        Set<AlitheiaPlugin> deps = new HashSet<AlitheiaPlugin>();
        deps.add(m);
        
        for (String s: m.getDependencies()) {
        	deps.add(pa.getImplementingPlugin(s));
        }
        
        List<AlitheiaPlugin> toExec = getExecutionOrder(deps);
        
        try {
            Collections.reverse(toExec);
        	List<Job> jobs = new ArrayList<Job>();
        	Job old = null;
        	for (AlitheiaPlugin a : toExec) {
        		Job j = new MetricSchedulerJob(a, sp);
        		jobs.add(j);
        		if (old != null) {
        			old.addDependency(j);
        		}
        		old = j;
        	}
        	for (Job j : jobs) {
        		sched.enqueue(j);
        	}
        } catch (SchedulerException e) {
            logger.error("Could not start metric scheduler job");
        }
    }

    private boolean canRunOnHost(StoredProject sp) {
        if (cns == null) {
            logger.warn("ClusterNodeService reference not found " +
            		"- ClusterNode assignment checks will be ignored");
            return true;
        } else {
            ClusterNode node = sp.getClusternode();
            
            if (node == null) {
                // project is not assigned yet to any ClusterNode, assign it
                // here by-default
                try {
                    cns.assignProject(sp);
                } catch (ClusterNodeActionException ex) {
                    logger.warn("Couldn't assign project " + sp.getName()
                            + " to ClusterNode " + cns.getClusterNodeName());
                    return false;
                }
            } else {
                // project is somewhere assigned , check if it is assigned to
                // this Cluster Node
                if (!cns.isProjectAssigned(sp)) {
                    logger.warn("Project " + sp.getName() + " is not assigned" +
                        " to this ClusterNode - Ignoring Metric synchronization");
                    return false;
                }
            }
        }
        // Done with ClusterNode Checks
        return true;
    }
    
    private List<AlitheiaPlugin> getExecutionOrder(Set<AlitheiaPlugin> unordered) {
    	Map<AlitheiaPlugin, Integer> idx = new HashMap<AlitheiaPlugin, Integer>();
    	Map<Integer, AlitheiaPlugin> invidx = new HashMap<Integer, AlitheiaPlugin>();
    	
    	GraphTS<AlitheiaPlugin> graph = new GraphTS<AlitheiaPlugin>(unordered.size());
    	
    	//Build the adjacency matrix
    	for (AlitheiaPlugin p : unordered) {
    		if (!idx.containsKey(p)) {
        		int n = graph.addVertex(p);
	    		idx.put(p, n);
	    		invidx.put(n, p);
	    	}
    		
    	    Set<String> deps = p.getDependencies();
    	    for (String metric : deps) {
    	    	AlitheiaPlugin dep = pa.getImplementingPlugin(metric);
    	    	
    	    	//Metrics are allowed to introduce self depedencies
    	    	if (p.equals(dep)) {
    	    	    continue;
    	    	}
    	    	
    	    	if (!idx.containsKey(dep)) {
    	    		int n = graph.addVertex(dep);
    	    		idx.put(dep, n);
    	    		invidx.put(n, dep);
    	    	}
    	    	graph.addEdge(idx.get(p), idx.get(dep));
    	    }
    	}
    	
    	List<AlitheiaPlugin> sorted = graph.topo();
    	
    	logger.debug("Calculated metric order:");
    	for (AlitheiaPlugin p : sorted) {
    		logger.debug("  " + p.getName());
    	}
    	
    	return sorted;
    }
    
    /**
     * Job that creates metric jobs. Used to avoid blocking the UI or user
     * scipts while scheduling large metric updates. Its priority ensures
     * that it will not fill up queues while updater jobs are running, 
     * leaving memory free till it is really required. 
     */
    private class MetricSchedulerJob extends Job {

        private AlitheiaPlugin m;
        private StoredProject sp;
        
        public MetricSchedulerJob(AlitheiaPlugin m, StoredProject sp) {
            super(dbs);
            this.m = m;
            this.sp = sp;
        }
        
        @Override
        public long priority() {
            return 0x2;
        }

        @Override
        protected void run() throws Exception {
            dbs.startDBSession();
            sp = DAObject.loadDAObyId(sp.getId(), StoredProject.class);
            PluginInfo mi = pa.getPluginInfo(m);
            Set<Class<? extends DAObject>> actTypes = mi.getActivationTypes();
            
            if ((actTypes == null) || actTypes.isEmpty()) {
                logger.error("Plugin " + mi.getPluginName() +
                            " has no activation types");
                return;
            }

            List<Metric> metrics = pa.getPlugin(mi).getAllSupportedMetrics();
            
            Map<MetricType.Type, TreeSet<Long>> objectIds = new HashMap<MetricType.Type, TreeSet<Long>>();

            for (Metric m : metrics) {
            	Map<MetricType.Type, SortedSet<Long>> IDs = 
            		pa.getImplementingPlugin(m.getMnemonic()).getObjectIdsToSync(sp, m);
            	for (MetricType.Type t : IDs.keySet()) {
            		
            		if (objectIds.get(t) == null) {
                    	objectIds.put(t, new TreeSet<Long>());	
                    }
                    
                    objectIds.get(t).addAll(IDs.get(t));
            	}
            }
            
            AbstractMetric metric = 
                (AbstractMetric) bc.getService(mi.getServiceRef());
            HashSet<Job> jobs = new HashSet<Job>();
            
            /*Check what is the default activation ordering as suggested by the metric*/
            Class<? extends DAObject>[] activOrder;
            InvocationOrder invOrder;
            SchedulerHints hints = metric.getClass().getAnnotation(SchedulerHints.class);

            if (hints == null) {
                activOrder = (Class<? extends DAObject>[])
            		SchedulerHints.class.getMethod("activationOrder").getDefaultValue();
                invOrder = (InvocationOrder)
                       SchedulerHints.class.getMethod("invocationOrder").getDefaultValue();
            } else {
                activOrder = hints.activationOrder();
                invOrder = hints.invocationOrder();
            }

			/*
			 * Iterate over all activation types but only create a job when
			 * there exists stuff to recalculate the metric on.
			 */
            for (Class<? extends DAObject> activator : activOrder) {
            	MetricType.Type actType = MetricType.fromActivator(activator);
            	if (!objectIds.keySet().contains(actType))
            		continue;

                //We assume that resource IDs increase monotonically
                TreeSet<Long> ids = objectIds.get(actType);
                TreeSet<Long> tmp = null;
                if (invOrder.equals(InvocationOrder.NEWFIRST)) {
                    tmp = new TreeSet<Long>(new DecreasingLongComparator());
                } else if (invOrder.equals(InvocationOrder.RANDOM)) {
                    tmp = new TreeSet<Long>(new RandomizedComparator());
                }

                if (tmp != null) {
                    tmp.addAll(ids);
                    ids = tmp;
                }

                for (Long l : ids) {
                    jobs.add(maJobFactory.create(metric, l, logger, 
                        metricTypesToActivators.get(actType),
                        priority.incrementAndGet(),
                        fastSync));
                }
            }
            sched.enqueueNoDependencies(jobs);
            dbs.commitDBSession();
        }
        
        @Override
        public String toString() {
            return "MetricSchedulerJob - Project:{" + sp + "} Metric:{" + m + "}";
        }
    }

    class DecreasingLongComparator implements Comparator<Long> {
        @Override
        public int compare(Long a, Long b) {
            if (a > b)
                return -1;
            else if (a < b)
                return 1;
            return 0;
        }
    }

    class RandomizedComparator implements Comparator<Long> {
        Random r = new Random();
        @Override
        public int compare(Long a, Long b) {
            if(r.nextBoolean())
                return -1;
            else
                return 1;
        }
    }

	@Override
	public void setInitParams(BundleContext bc, Logger l) {
		this.bc = bc;
		this.logger = l;
		
        metricTypesToActivators = new HashMap<Type, Class<? extends DAObject>>();
        metricTypesToActivators.put(Type.NAMESPACE, NameSpace.class);
        metricTypesToActivators.put(Type.ENCAPSUNIT, EncapsulationUnit.class);
        metricTypesToActivators.put(Type.EXECUNIT, ExecutionUnit.class);
        metricTypesToActivators.put(Type.SOURCE_DIRECTORY, ProjectFile.class);
        metricTypesToActivators.put(Type.SOURCE_FILE, ProjectFile.class);
        metricTypesToActivators.put(Type.BUG, Bug.class);
        metricTypesToActivators.put(Type.PROJECT_VERSION, ProjectVersion.class);
        metricTypesToActivators.put(Type.MAILING_LIST, MailingList.class);
        metricTypesToActivators.put(Type.MAILMESSAGE, MailMessage.class);
        metricTypesToActivators.put(Type.MAILTHREAD, MailingListThread.class);
	}

	@Override
	public void shutDown() {
	}

	@Override
	public boolean startUp() {
        priority = new AtomicLong();
        //Lower priorities are reserved for updater jobs
        priority.set(0x1000);
        
        String sync = bc.getProperty("eu.sqooss.metricactivator.sync");
        
        if (sync != null && sync.equalsIgnoreCase("fast"))
            this.fastSync = true;
	
        return true;
	}
}

//vi: ai nosi sw=4 ts=4 expandtab
