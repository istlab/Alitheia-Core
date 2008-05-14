/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 Georgios Gousios <gousiosg@gmail.com>
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;

public class MetricActivatorImpl implements MetricActivator {

    /** The parent bundle's context object. */
    private BundleContext bc;

    private AlitheiaCore core;
    private Logger logger;
    private PluginAdmin pa;
    private Scheduler sched;
    
    public MetricActivatorImpl(BundleContext bc, Logger logger) {
        this.bc=bc;

        ServiceReference serviceRef = null;
        serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) bc.getService(serviceRef);
        
        this.logger = logger;
        this.pa = core.getPluginAdmin();
        this.sched = core.getScheduler();
    }
    
    public <T extends DAObject> void runMetrics(Class<T> clazz,
            SortedSet<Long> objectIDs) {
        // Get a list of all metrics that support the given activation type
        List<PluginInfo> metrics = null;
        metrics = core.getPluginAdmin().listPluginProviders(clazz);
        
        if (metrics == null || metrics.size() == 0) {
            logger.warn("No metrics found for activation type " + clazz.getName());
            return;
        }
        
        // Start a job per processor to schedule metrics
       int cpus = Runtime.getRuntime().availableProcessors();
       int sliceSize = (objectIDs.size() / cpus);
       Long[] entries = objectIDs.toArray(new Long[] {}); 
       
       for (int i = 0; i < cpus; i++) {
           Long [] slice = null;
           if (i < cpus - 1) {
               slice = new Long[sliceSize]; 
               System.arraycopy(entries, i*sliceSize, slice, 0, sliceSize);
           } else {
               int remainder = entries.length - i * sliceSize;
               slice = new Long[remainder];
               System.arraycopy(entries, i*sliceSize, slice, 0, remainder);
           }
           
           MetricActivatorJob maj = new MetricActivatorJob(metrics, clazz,
                    slice, logger, bc);
            
            try {
                sched.enqueue(maj);
            } catch (SchedulerException e) {
                logger.error("Error creating scheduler job:" + e.getMessage());
            }
        }
    }

    public void syncMetric(AlitheiaPlugin m, StoredProject sp) {
        PluginInfo mi = pa.getPluginInfo(m);
        
    }

    public <T extends DAObject> void syncMetrics(StoredProject sp) {
        
    }

    public ProjectVersion getLastAppliedVersion(AlitheiaPlugin m, StoredProject sp) {
        PluginInfo mi = pa.getPluginInfo(m);
        
        Map<String, Object> properties = new HashMap<String, Object>();
     //   mi.
       // List<Metric> metrics = dbs.findObjectsByProperties(Metric.class , properties);
        return null;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
