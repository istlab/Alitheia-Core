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

import org.hibernate.QueryException;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ProjectVersionMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.tds.ProjectRevision;

import eu.sqooss.metrics.mde.db.MDEDeveloper;
import eu.sqooss.metrics.mde.db.MDEWeek;

public class MDEImplementation extends AbstractMetric implements ProjectVersionMetric {
    /** This is the name of the non-adjusted dev(total) ancilliary metric. */
    private static final String MNEMONIC_MDE_DEVTOTAL = "MDE.dt";
    private static final String MNEMONIC_MDE_DEVACTIVE = "MDE.da";
    
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
            result &= super.addSupportedMetrics(
                    "Mean Developer Engagement: Ancilliary dev(active)",
                    MNEMONIC_MDE_DEVACTIVE,
                    MetricType.Type.SOURCE_CODE);
        }
        return result;
    }

    public List<ResultEntry> getResult(ProjectVersion a, Metric m) {
        // Search for a matching project file measurement
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectVersion", a);
        filter.put("metric", m);
        List<ProjectVersionMeasurement> measurement =
            db.findObjectsByProperties(ProjectVersionMeasurement.class, filter);
        if ((null != measurement) && (measurement.size()>0)) {
            return convertVersionMeasurements(measurement, m.getMnemonic());
        } else {
            // Fill up the DB
            run(a);
            ProjectVersionMeasurement result = null;
            ProjectVersionMeasurement r = null;

            Metric stm = Metric.getMetricByMnemonic(MNEMONIC_MDE_DEVTOTAL);
            if (null == stm) {
                log.warn("Metric dev(total) was not registered");
            } else {
                r = recordDevTotal(a,stm);
                if (MNEMONIC_MDE_DEVTOTAL.equals(m.getMnemonic())) {
                    result = r;
                }
            }
            
            stm = Metric.getMetricByMnemonic(MNEMONIC_MDE_DEVACTIVE);
            if (null == stm) {
                log.warn("Metric dev(active) was not registered");
            } else {
                r = recordDevActive(a, stm);
                if (MNEMONIC_MDE_DEVACTIVE.equals(m.getMnemonic())) {
                    result = r;
                }
            }
            
            if (null != result) {
                return convertVersionMeasurement(result, m.getMnemonic());
            } else {
                log.warn("The requested metric " + m.getMnemonic() + " did not match any recording function.");
                return null;
            }
        }
    }

    public void run(ProjectVersion pv) {
        Object projectLockObject = null;
        synchronized (projectLocks) {
            projectLockObject = projectLocks.get(pv.getProject().getId());
            if (null == projectLockObject) {
                projectLockObject = new Object();
                projectLocks.put(pv.getProject().getId(),projectLockObject);
            }
        }
        if (null == projectLockObject) {
            log.error("Failed to get lock object for project " + pv.getProject().getName());
            return;
        }

        synchronized (projectLockObject) {
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
            db.commitDBSession();
            db.startDBSession();
        }
    }

    private ProjectVersionMeasurement recordDevTotal(ProjectVersion a, Metric m) throws QueryException {
        HashMap<String, Object> parameterMap = new HashMap<String, Object>(2);
        parameterMap.put("timestamp", a.getTimestamp());
        parameterMap.put("project", a.getProject());
        List<?> pvList = db.doHQL("select count(*) from MDEDeveloper m where m.start <= :timestamp and m.developer.storedProject = :project", parameterMap);
        if ((null == pvList) || (pvList.size() == 0)) {
            // The caller will print a warning that getResult isn't
            // returning a value.
            return null;
        }
        String s = pvList.get(0).toString();
        ProjectVersionMeasurement result = new ProjectVersionMeasurement(m, a, s);
        db.addRecord(result);
        return result;
    }

    private ProjectVersionMeasurement recordDevActive(ProjectVersion a, Metric m) throws QueryException {
        Integer weeknumber = new Integer(convertToWeekOffset(a));
        HashMap<String, Object> parameterMap = new HashMap<String, Object>(2);
        parameterMap.put("week", weeknumber);
        parameterMap.put("project", a.getProject());
        List<?> pvList = db.doHQL("select count(*) from MDEWeek m where m.week = :week and m.developer.storedProject = :project", parameterMap);
        if ((null == pvList) || (pvList.size() == 0)) {
            // The caller will print a warning that getResult isn't
            // returning a value.
            return null;
        }
        String s = pvList.get(0).toString();
        ProjectVersionMeasurement result = new ProjectVersionMeasurement(m, a, s);
        db.addRecord(result);
        return result;
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
        log.debug("Updating from " +
                ((null == start) ? "null" : start.toString()) + "-" +
                ((null == end) ? "null" : end.toString()));
        if ((null == start) || (null == end)) {
            log.warn("Range of versions for runDevTotal is broken.");
            return;
        }
        if (!start.lte(end)) {
            log.info("Range " + start.toString() + "-" + end.toString() + " is backwards.");
            return;
        }

        StoredProject p = start.getProject();
        ProjectVersion projectStartVersion = ProjectVersion.getVersionByRevision(p, new ProjectRevision(1));
        if (null == projectStartVersion) {
            log.warn("Project <" + p.getName() + "> has no revision 1.");
            return;
        }
        long projectStartTime = projectStartVersion.getTimestamp();

        ProjectVersion c = start;
        while ((null != c ) && c.lte(end)) {
            memoDeveloper(projectStartTime, c);
            memoWeek(projectStartTime, c);
            c = c.getNextVersion();
        }
    }

    private void memoDeveloper(long projectStartTime, ProjectVersion c) {
        MDEDeveloper d = MDEDeveloper.find(c.getCommitter());
        if (null != d) {
            // Know this developer, so just leave them.
        } else {
            d = new MDEDeveloper(c.getCommitter());
            d.setStart(c.getTimestamp());
            d.setStartWeek(convertToWeekOffset(projectStartTime, c.getTimestamp()));
            db.addRecord(d);
        }
    }

    private void memoWeek(long projectStartTime, ProjectVersion c) {
        int thisweek = convertToWeekOffset(projectStartTime, c.getTimestamp());

        MDEWeek d = MDEWeek.find(c.getCommitter(), thisweek);
        if (null != d) {
            // Know this developer, so just leave them.
        } else {
            d = new MDEWeek(c.getCommitter(), thisweek, true);
            db.addRecord(d);
        }
    }


    private static final long MILLISECONDS_PER_WEEK =
        1000 * 60 * 60 * 24 * 7;

    public static int convertToWeekOffset(ProjectVersion v) {
        return convertToWeekOffset(v.getProject(), v.getTimestamp());
    }
    
    public static int convertToWeekOffset(StoredProject p, long timestamp) {
        ProjectVersion start = ProjectVersion.getVersionByRevision(p, new ProjectRevision(1));
        if (null == start) {
            // There was no project revision 1, so it must not have started yet.
            return 0;
        }
        return convertToWeekOffset(start.getTimestamp(),timestamp);
    }

    public static int convertToWeekOffset(long start, long timestamp) {
        return (int) ((timestamp - start) / MILLISECONDS_PER_WEEK);
    }

    public Object selfTest() {
        MDEDeveloper d = new MDEDeveloper();
        System.out.println(d);
        return null;
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

