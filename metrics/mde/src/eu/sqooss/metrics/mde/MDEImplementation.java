/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Paul J. Adams <paul.adams@siriusit.co.uk>
 * Copyright 2008 by Adriaan de Groot <groot@kde.org>
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

package eu.sqooss.metrics.mde;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ProjectVersionMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.tds.ProjectRevision;

import eu.sqooss.metrics.mde.db.MDEDeveloper;

public class MDEImplementation extends AbstractMetric implements ProjectVersionMetric {
    /** This is the name of the non-adjusted dev(total) ancilliary metric. */
    private static final String MNEMONIC_MDE_DEVTOTAL = "MDE.dt";
    
    HashMap<Long,Object> projectLocks;
    
    public MDEImplementation(BundleContext bc) {
        super(bc);
        super.addActivationType(ProjectVersion.class);
        super.addMetricActivationType(MNEMONIC_MDE_DEVTOTAL, ProjectVersion.class);
        projectLocks = new HashMap<Long,Object>();
    }

    public boolean install() {
        boolean result = super.install();
        if (result) {
            result &= super.addSupportedMetrics(
                    "Mean Developer Engagement: Ancilliary dev(total)",
                    MNEMONIC_MDE_DEVTOTAL,
                    MetricType.Type.SOURCE_CODE);
        }
        return result;
    }

    public List<ResultEntry> getResult(ProjectVersion a, Metric m) {
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();
        // Search for a matching project file measurement
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectVersion", a);
        filter.put("metric", m);
        List<ProjectVersionMeasurement> measurement =
            db.findObjectsByProperties(ProjectVersionMeasurement.class, filter);
        if ((null != measurement) && (measurement.size()>0)) {
            return convertVersionMeasurements(measurement,m.getMnemonic());
        } else {
            run(a);
            HashMap<String,Object> parameterMap = new HashMap<String,Object>(2);
            parameterMap.put("timestamp", a.getTimestamp());
            parameterMap.put("project", a.getProject());
            List<?> pvList = db.doHQL("select count(*) from MDEDeveloper m " +
                "where m.start <= :timestamp " +
                "and m.developer.storedProject = :project",
                parameterMap);
            if ((null == pvList) || (pvList.size() == 0)) {
                // The caller will print a warning that getResult isn't 
                // returning a value.
                return null;
            }
            String s = pvList.get(0).toString();
            ProjectVersionMeasurement result = new ProjectVersionMeasurement(m,a,s);
            db.addRecord(result);
            return convertVersionMeasurement(result,m.getMnemonic());
        }
    }

    public void run(ProjectVersion pv) {
        Object o = null;
        synchronized (projectLocks) {
            o = projectLocks.get(pv.getId());
            if (null == o) {
                o = new Object();
                projectLocks.put(pv.getId(),o);
            }
        }
        if (null == o) {
            log.error("Failed to get lock object for project " + pv.getProject().getName());
            return;
        }
        
        synchronized(o) {
            // Find the latest ProjectVersion for which we have data
            Metric m = Metric.getMetricByMnemonic(MNEMONIC_MDE_DEVTOTAL);
            ProjectVersion previous = 
                    ProjectVersionMeasurement.getLastMeasuredVersion(m, pv.getProject());
            if (null == previous) {
                // We've got no measurements at all, so start at revision 1
                previous = ProjectVersion.getVersionByRevision(pv.getProject(), new ProjectRevision(1));
            }
            // It's safe to run this on a revision twice
            runDevTotal(previous, pv);
        }
    }

    /*
     * Find the total number of devleopers in the project for every revision
     * for which the data is unknown from start to end; store a measurement
     * for each one. If a revision already has a measurement stored, it
     * is ignored. This method fills in the MDEDeveloper table, from which
     * other results are calculated.
     * 
     * @param start Starting revision
     * @param end Ending revision
     */
    private void runDevTotal(ProjectVersion start, ProjectVersion end) {
        log.info("Updating from " + 
                ((null == start) ? "null" : start.toString()) + "-" +
                ((null == end) ? "null" : end.toString()));

        if ((null == start) || (null == end)) {
            log.warn("Range of versions for runDevTotal is broken.");
            return;
        }
        
        ProjectVersion c = start;
        while ((null != c ) && c.lte(end)) {
            MDEDeveloper d = MDEDeveloper.find(c.getCommitter());
            if (null != d) {
                // Know this developer, so leave him alone
                // TODO: update developer stats
            } else {
                d = new MDEDeveloper(c.getCommitter());
                d.setStart(c.getTimestamp());
                // TODO: sensible initial values for service and active
                db.addRecord(d);
            }
            
            c = c.getNextVersion();
        }
    }
    
    public Object selfTest() {
        MDEDeveloper d = new MDEDeveloper();
        System.out.println(d);
        return null;
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

