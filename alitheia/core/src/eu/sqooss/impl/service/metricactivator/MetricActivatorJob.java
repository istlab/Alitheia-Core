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

import java.util.List;

import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Job;

/**
 * Schedule metric jobs in parallel. Used to overcome DB I/O waits
 * when scheduling jobs on large multiprocessor systems.
 * 
 * @author Georgios Gousios
 */
public class MetricActivatorJob extends Job {

    private Long[] objectIDs;
    private Logger logger;
    private DBService dbs;
    private Class<? extends DAObject> clazz;
    private BundleContext bc;
    private List<PluginInfo> pli;
    
    MetricActivatorJob(List<PluginInfo> pi,
            Class<? extends DAObject> clazz,
            Long[] objectIDs, 
            Logger l, BundleContext bc) {
        this.clazz = clazz;
        this.objectIDs = objectIDs;
        this.logger = l;
        this.pli = pi;
        this.bc = bc;
        this.dbs = ((AlitheiaCore)bc.getService(bc.getServiceReference(AlitheiaCore.class.getName()))).getDBService();
    }
    
    @Override
    public int priority() {
        return 0xada;
    }

    @Override
    protected void run() throws Exception {
         
        dbs.startDBSession();
        for(Long i : objectIDs) {
            for (PluginInfo pi : pli) {
                // Get the metric plug-in that installed this metric
                AlitheiaPlugin m =
                    (AlitheiaPlugin) bc.getService(pi.getServiceRef());
                if (m != null) {
                    try {
                        // Retrieve the resource object's DAO from the
                        // database and run the metric on it
                        m.run(dbs.findObjectById(clazz, i));
                    } catch (MetricMismatchException e) {
                        logger.warn("Metric " + m.getName() + " failed");
                    }
                }
            }
        }
        dbs.commitDBSession();
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

