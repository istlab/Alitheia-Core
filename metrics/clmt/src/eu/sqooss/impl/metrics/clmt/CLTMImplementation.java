/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.impl.metrics.clmt;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.metrics.clmt.CLMT;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.scheduler.Scheduler;


public class CLTMImplementation extends AbstractMetric implements CLMT {
    public CLTMImplementation(BundleContext bc) {
        super(bc);      
        this.addActivationType(ProjectVersion.class);
    }
    
    public boolean install() {
        boolean result = super.install();
        if (result) {
            result &= super.addSupportedMetrics(
                    "Number of Classes",
                    "NOCL",
                    MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Number of Public Attributes",
                    "NOPA",
                    MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Number of Children",
                    "NOC",
                    MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Number of Public Methods",
                    "NPM",
                    MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Number of Projected Methods",
                    "NOPRM",
                    MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Weighted Methods per Class",
                    "WMC",
                    MetricType.Type.SOURCE_CODE);
            
        }
        return result;
    }
    
    public void run(ProjectVersion v) {
        CLTMJob w = null;
        try {
            w = new CLTMJob(this, v);

            ServiceReference serviceRef = null;
            serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
            Scheduler s = ((AlitheiaCore) bc.getService(serviceRef)).getScheduler();
            s.enqueue(w);
        } catch (Exception e) {
            log.error("Could not schedule "+ w.getClass().getName() + 
                    " for project version: " + (v.getVersion()));
        }
    }
    
    public Result getResult(ProjectVersion a, Metric m) {
        
        return null;
    }    
}

// vi: ai nosi sw=4 ts=4 expandtab

