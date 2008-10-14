/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 Athens University of Economics and Business
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

import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlreadyProcessingException;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.InvocationRule.ActionType;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.scheduler.Job;

/**
 * Schedule metric jobs in parallel. Used to overcome DB I/O waits
 * when scheduling jobs on large multiprocessor systems.
 * 
 * @author Georgios Gousios
 */
public class MetricActivatorJob extends Job {

    private Logger logger;
    private DBService dbs;
    private MetricActivator ma;
    private Long daoID;
    private AbstractMetric metric;
    Class<? extends DAObject> daoType;
    
    MetricActivatorJob(AbstractMetric m, Long daoID, Logger l,
            Class<? extends DAObject> daoType) {
    	this.metric = m;
        this.logger = l;
        this.daoID = daoID;
        this.daoType = daoType;
        this.dbs = AlitheiaCore.getInstance().getDBService();
        this.ma = AlitheiaCore.getInstance().getMetricActivator();   
    }
    
    @Override
    public int priority() {
        return 0xada;
    }

    @Override
    protected void run() throws Exception {
         
        dbs.startDBSession();
        DAObject obj = dbs.findObjectById(daoType, daoID);
        
        // trigger calculation of the metric
        if (ma.matchRules(metric,obj) == ActionType.EVAL) {
            try {
                metric.getResult(obj, metric.getSupportedMetrics());
            } catch (MetricMismatchException e) {
                logger.warn("Metric " + metric.getName() + " failed");
            } catch (AlreadyProcessingException ape) {
                logger.warn("DAO id " + daoID + " is locked, job has been " +
                		"rescheduled");
                dbs.rollbackDBSession();
                return;
            } 
        }
        if(!dbs.commitDBSession()) {
            logger.warn("commit failed - restarting metric job");
            restart();
        }
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

