/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 Georgios Gousios <gousiosg@gmail.com>
 *
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

package eu.sqooss.impl.metrics.wc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.lib.result.Result;
import eu.sqooss.lib.result.ResultEntry;
import eu.sqooss.metrics.wc.Wc;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.scheduler.Scheduler;

public class WcImplementation extends AbstractMetric implements Wc {

    List<Metric> supportedMetrics;
    
    public WcImplementation(BundleContext bc) {
        super(bc);        
    }

    public boolean install() {
        boolean result = super.install();
        if (result) {
            result &= super.addSupportedMetrics(
                    this.getDescription(),
                    MetricType.Type.SOURCE_CODE);
        }
        return result;
    }

    public boolean remove() {

        return false;
    }

    public boolean update() {

        return remove() && install(); 
    }

    public Result getResult(ProjectFile a) {
        Result result = null;

        // Get a DB session
        Session s = db.getSession(this);

        // Search for a matching project file measurement
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectFile", a);
        /* TODO: getResult() itself must contain a metric descriptor as a
         *       parameter. Otherwise, if this plug-in has registered more
         *       than one metric descriptors (of the same type), then it will
         *       be impossible to find out which measurement result needs to
         *       be retrieved.
         */
        filter.put("metric", this.getSupportedMetrics().get(0));
        List<ProjectFileMeasurement> measurement =
            db.findObjectByProperties(s, ProjectFileMeasurement.class, filter);

        // Convert the measurement into a result object
        if (! measurement.isEmpty()) {
            // There is only one measurement per metric and project file
            Integer value = new Integer(measurement.get(0).getResult());
            // ... and therefore only one result entry
            ArrayList<ResultEntry> entries = new ArrayList<ResultEntry>();
            /* TODO: The above mentioned problem has its influence here too.
             *       If a plug-in supports more than one metric of the same
             *       type, each providing its own metric job, then how do we
             *       know:
             *       1. Which metric calculated this measurement (what we know
             *          from this measurement is the metric descriptor only)?
             *       2. What is the concrete result type of this measurement
             *          (Long, Integer ...)? In a Measurement DB entry the
             *          result is always stored as String and we don't have a
             *          hint about which metric (job) calculated it.
             */
            ResultEntry entry =
                new ResultEntry(value, ResultEntry.MIME_TYPE_TYPE_INTEGER);
            entries.add(entry);
            result = new Result();
            result.addResultRow(entries);
        }

        // Free the DB session
        db.returnSession(s);

        return result;
    }

    public void run(ProjectFile a) {
        try {
            WcJob w = new WcJob(this, a);

            ServiceReference serviceRef = null;
            serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
            Scheduler s = ((AlitheiaCore) bc.getService(serviceRef)).getScheduler();

            s.enqueue(w);
        } catch (Exception e) {
            log.error("Could not schedule wc job for project file: " + ((ProjectFile)a).getName());
        }
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
