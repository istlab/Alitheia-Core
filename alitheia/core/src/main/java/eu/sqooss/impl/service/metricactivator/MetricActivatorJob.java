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

import java.util.List;

import org.hibernate.exception.LockAcquisitionException;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlreadyProcessingException;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.scheduler.Job;

/**
 * Generic metric job. Manages database sessions and job restarts
 * on interesting exceptions.
 */
public class MetricActivatorJob extends Job {

    private Logger logger;
    private DBService dbs;
    private MetricActivator ma;
    private Long daoID;
    private AbstractMetric metric;
    private long priority;
    Class<? extends DAObject> daoType;
    private boolean fastSync = false; 
    
    MetricActivatorJob(AbstractMetric m, Long daoID, Logger l,
            Class<? extends DAObject> daoType, long priority, 
            boolean fastSync) {
    	this.metric = m;
        this.logger = l;
        this.daoID = daoID;
        this.daoType = daoType;
        this.dbs = AlitheiaCore.getInstance().getDBService();
        this.ma = AlitheiaCore.getInstance().getMetricActivator(); 
        this.priority = priority;
        this.fastSync = fastSync;
    }
    
    @Override
    public long priority() {
        return priority;
    }

    @Override
    protected void run() throws Exception {
        dbs.startDBSession();
        metric.setJob(this);
        DAObject obj = dbs.findObjectById(daoType, daoID);

        try {
            if (fastSync) {
                /*
                * This reduces the number of queries performed when triggering
                * synchronization of metrics on large databases. We trust that
                * if there is a value in the database for one of the metric a
                * plug-in provides, there will be a value for all metrics. For
                * example, on the size (wc) metric this will save 5-6 queries
                * per projectfile. If the metric syncs 20M files
                * this optimisation prevents 100M queries from being executed.
                */
                List<Metric> supported = metric.getSupportedMetrics(obj.getClass());
                metric.getResult(obj, supported.subList(0, 1));
            } else {
                metric.getResult(obj, metric.getSupportedMetrics(obj.getClass()));
            }
        } catch (MetricMismatchException e) {
            logger.warn("Metric " + metric.getName() + " failed");
        } catch (AlreadyProcessingException ape) {
            logger.warn("DAO id " + daoID + " is locked, job has been " +
                    "rescheduled");
            dbs.rollbackDBSession();
            return;
        } catch (LockAcquisitionException lae) {
            dbs.rollbackDBSession();
        }

        if (!dbs.commitDBSession()) {
            logger.warn("commit failed - restarting metric job");
            restart();
        }
    }

    @Override
    public String toString() {
        return "MetricActivatorJob: Metric:{" + metric.getName() +"} Activator:{" + daoType.getSimpleName() + "} DAO:{" + daoID + "}";
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

