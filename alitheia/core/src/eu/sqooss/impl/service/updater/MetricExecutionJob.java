/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 Georgios Gousios <gousiosg@aueb.gr>
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

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.FileGroupMetric;
import eu.sqooss.service.abstractmetric.Metric;
import eu.sqooss.service.abstractmetric.MetricResult;
import eu.sqooss.service.abstractmetric.MetricResultEntry;
import eu.sqooss.service.abstractmetric.ProjectFileMetric;
import eu.sqooss.service.abstractmetric.ProjectVersionMetric;
import eu.sqooss.service.abstractmetric.StoredProjectMetric;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.FileGroup;
import eu.sqooss.service.db.Measurement;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.updater.UpdaterException;

public class MetricExecutionJob extends Job {

    private AlitheiaCore core;
    private BundleContext context;
    private List<Metric> metricPlugins;
    private DAObject updateTarget;
    private DBService dbs;

    public MetricExecutionJob(DAObject updateTarget, AlitheiaCore core,
            BundleContext bc) throws Exception {
        this.core = core;
        this.context = bc;
        this.updateTarget = updateTarget;

        String targetClassName = null;
        if (updateTarget instanceof StoredProject)
            targetClassName = StoredProjectMetric.class.getName();
        else if (updateTarget instanceof ProjectVersion)
            targetClassName = ProjectVersionMetric.class.getName();
        else if (updateTarget instanceof ProjectFile)
            targetClassName = ProjectFileMetric.class.getName();
        else if (updateTarget instanceof FileGroup)
            targetClassName = FileGroupMetric.class.getName();
        else
            throw new UpdaterException("Unsupported argument type");
        
        metricPlugins = new ArrayList<Metric>();
        // get a list of the registered metric plugins that can handle this DAObject
        ServiceReference[] refs = context.getServiceReferences(targetClassName, null);
        for (int i = 0; i < refs.length; i++) {
            metricPlugins.add((Metric) context.getService(refs[i]));
        }
    }

    public int priority() {
        return 2;
    }

    protected void run() throws Exception {
        if(metricPlugins.size() == 0)
            return;
        
        dbs = core.getDBService();
        // run each metric plugin for the updateTarget, get the results, store them in the db
        for (Metric plugin: metricPlugins) {
            plugin.run(updateTarget);
            MetricResult result = plugin.getResult(updateTarget);
            storeMetricResults(updateTarget, result);
        }
    }
    
    private void storeMetricResults(DAObject o, MetricResult result) throws Exception {
        if(!(o instanceof ProjectVersion)) {
            //the current db schema allows only ProjectVersion measurements
            return;
        }
        
        Measurement m = null;
        for(ArrayList<MetricResultEntry> mreList : result) {
            for(MetricResultEntry mre : mreList) {
                m = new Measurement();
                eu.sqooss.service.db.Metric metric = new eu.sqooss.service.db.Metric();
                //TODO: load the MetricType and set in metric
                m.setMetric(metric.getId());
                m.setResult(mre.toString()); //TODO: use xml form?
                m.setWhenRun(new Time((new Date()).getTime()));
                dbs.addRecord(m);
            }
        }
    }
}
