/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 by Paul J. Adams <paul.adams@siriusit.co.uk>
 * Copyright 2008 by Athens University of Economics and Business
 *     Author Adriaan de Groot <groot@kde.org>
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

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ProjectVersionMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.EvaluationMark;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.StoredProject;

import eu.sqooss.metrics.mde.db.MDEDeveloper;
import eu.sqooss.metrics.mde.db.MDEWeek;

public class MDEImplementation extends AbstractMetric implements ProjectVersionMetric {
    /** This is the name of the non-adjusted dev(total) ancilliary metric. */
    private static final String MNEMONIC_MDE_DEVTOTAL = "MDE.dt";
    private static final String MNEMONIC_MDE_DEVACTIVE = "MDE.da";
    private static final String MNEMONIC_MDE_RAW = "MDE.raw";

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
            result &= super.addSupportedMetrics(
                    "Mean Developer Engagement: Raw ratio per week",
                    MNEMONIC_MDE_RAW,
                    MetricType.Type.SOURCE_CODE);
        }
        return result;
    }

    public List<ResultEntry> getResult(ProjectVersion a, Metric m) {
        if (isKnown(a,m)) {
            // This means we *should* know about the result. But it is
            // possible to have gaps in the record, or also
            // to request a new metric for which we haven't recorded
            // results.
            ProjectVersionMeasurement r = null;
            boolean metricFound = false;

            if (MNEMONIC_MDE_DEVTOTAL.equals(m.getMnemonic())) {
                r = recordDevTotal(a,m);
                metricFound = true;
            } else if (MNEMONIC_MDE_DEVACTIVE.equals(m.getMnemonic())) {
                r = recordDevActive(a,m);
                metricFound = true;
            } else if (MNEMONIC_MDE_RAW.equals(m.getMnemonic())) {
                float v = getRawEngagement(a,m);
                metricFound = true;
                if (0.0 <= v) {
                    // This takes special handling, since it is a float
                    List<ResultEntry> results = new ArrayList<ResultEntry>(1);
                    results.add(new ResultEntry(v, ResultEntry.MIME_TYPE_TYPE_FLOAT, m.getMnemonic()));
                    return results;
                }
            }

            if (null != r) {
                return convertVersionMeasurement(r, m.getMnemonic());
            }

            // So either we didn't recognize the metric, or didn't find
            // a result. Warn appropriately:
            if (!metricFound) {
                // This takes priority: we didn't match any of the metric
                // names, so we can't possibly return a meaningful value.
                log.error("Request for unknown metric " + m.getMnemonic());
                return null;
            } else {
                log.warn("Result " + a + " should be known, but is not. Recalculating.");
            }

        }

        // At this point, we realise we do not know this version yet.
        // So we need to run the measurements, record this as a known version
        // and then return the new results.
        run(a);
        if (!isKnown(a,m)) {
            log.error("After run, the result for " + a.toString() +
                    " was still not known.");
            return null;
        }
        // This looks recursive, but it's going to terminate immediately
        // due to isKnown().
        return getResult(a,m);
    }

    public void run(ProjectVersion pv) {
        if (null == pv) {
            log.warn("Null revision passed to run.");
            return;
        }
        if (null == pv.getProject()) {
            log.warn("Project is null in " + pv);
            return;
        }
        // Find the starting timestamp of the project. This goes via
        // revision 1 of the project.
        ProjectVersion projectFirstVersion = ProjectVersion.getVersionByRevision(
                pv.getProject(), 
                ProjectVersion.getFirstProjectVersion(pv.getProject()).getRevisionId());
        if (null == projectFirstVersion) {
            log.warn("Project <" + pv.getProject().getName() + "> has no revision 1.");
            return;
        }
        long projectStartTime = projectFirstVersion.getTimestamp();

        // See documentation on projectLocks; we're going to get access
        // to the hash table to look up the lock object for the given project.
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

        // See documentation on projectLocks; we're going to lock
        // for one project.
        synchronized (projectLockObject) {
            // Find the latest ProjectVersion for which we have data;
            // we will use DEVTOTAL as the metric to measure by.
            Metric mDevTotal = Metric.getMetricByMnemonic(MNEMONIC_MDE_DEVTOTAL);
            Metric mDevActive = Metric.getMetricByMnemonic(MNEMONIC_MDE_DEVACTIVE);
            ProjectVersion previous =
                    ProjectVersion.getLastMeasuredVersion(mDevTotal, pv.getProject());
            if (null == previous) {
                // We've got no measurements at all, so start at revision 1
                previous = ProjectVersion.getVersionByRevision(pv.getProject(), 
                        ProjectVersion.getFirstProjectVersion(pv.getProject()).getRevisionId());
                if (null == previous) {
                    log.warn("Project " + pv.getProject().getName() + " has no revision 1.");
                    return;
                }
            }
            log.debug("Updating from " + previous.toString() + "-" + pv.toString());
            if (!previous.lte(pv)) {
                log.info("Range " + previous.toString() + "-" + pv.toString() + " is backwards.");
                return;
            }

            // We have now established a sensible range to iterate over.
            ProjectVersion c = previous;
            while ((null != c) && c.lte(pv)) {
                // For each revision, record information for the green blob
                // graph (dev(active)) and record each developer as he or
                // she shows up (dev(total)).
                memoDeveloper(projectStartTime, c);
                memoWeek(projectStartTime, c);
                c = c.getNextVersion();
            }

            // TODO: Overload this for setting the version, too.
            markEvaluation(mDevTotal,pv);
            markEvaluation(mDevActive,pv);
            db.commitDBSession();
            db.startDBSession();
        }
    }

    /**
     * This plug-in stores evaluation marks to record that
     * it has done calculations (per metric) right through to project version V;
     * use this to calculate whether the given project version
     * has been evaluated (i.e. <= V) or not.
     *
     * @param m Metric to check against
     * @param v Version to check for
     * @return true if that version has been evaluated already
     */
    private boolean isKnown(ProjectVersion v, Metric m) {
        EvaluationMark mark = EvaluationMark.getEvaluationMarkByMetricAndProject(m,v.getProject());
        if (null == mark) {
            return false;
        }
        if (null == mark.getVersion()) {
            return false;
        }
        return v.lte(mark.getVersion());
    }

    /**
     * Determine the measurement for dev(total) for a given version,
     * by mapping it to a week and then querying the MDE internal data
     * tables for answers.
     *
     * @param a Project version
     * @param m Metric (for label purposes only)
     * @return Measurement, or null if there is none.
     *
     * @throws org.hibernate.QueryException
     */
    private int getDevTotal(ProjectVersion a) throws QueryException {
        HashMap<String, Object> parameterMap = new HashMap<String, Object>(2);
        parameterMap.put("timestamp", a.getTimestamp());
        parameterMap.put("project", a.getProject());
        List<?> pvList = db.doHQL("select count(*) from MDEDeveloper m where m.start <= :timestamp and m.developer.storedProject = :project", parameterMap);
        if ((null == pvList) || (pvList.size() == 0)) {
            // The caller will print a warning that getResult isn't
            // returning a value.
            return -1;
        }
        Long s = (Long) pvList.get(0);
        return s.intValue();
    }

    private ProjectVersionMeasurement recordDevTotal(ProjectVersion a, Metric m)
        throws QueryException {
        ProjectVersionMeasurement result = new ProjectVersionMeasurement(m,a,String.valueOf(getDevTotal(a)));
        return result;
    }

    /**
     * Query the internal MDE tables to get a raw dev(active) value
     * for the week around the given version.
     *
     * @param a Project version to query
     * @param m Metric (for labeling purposes)
     * @return Measurement, or null if there is none
     *
     * @throws org.hibernate.QueryException
     */
    private int getDevActive(ProjectVersion a) throws QueryException {
        Integer weeknumber = new Integer(convertToWeekOffset(a));
        HashMap<String, Object> parameterMap = new HashMap<String, Object>(2);
        parameterMap.put("week", weeknumber);
        parameterMap.put("project", a.getProject());
        List<?> pvList = db.doHQL("select count(*) from MDEWeek m where m.week = :week and m.developer.storedProject = :project", parameterMap);
        if ((null == pvList) || (pvList.size() == 0)) {
            // The caller will print a warning that getResult isn't
            // returning a value.
            return -1;
        }
        Long s = (Long) pvList.get(0);
        return s.intValue();
    }

    private ProjectVersionMeasurement recordDevActive(ProjectVersion a, Metric m)
        throws QueryException {
        ProjectVersionMeasurement result = new ProjectVersionMeasurement(m,a,String.valueOf(getDevActive(a)));
        return result;
    }
    
    private float getRawEngagement(ProjectVersion a, Metric m) 
        throws QueryException {
        int act = getDevActive(a);
        int tot = getDevTotal(a);
        if ((act < 0) || (tot < 0)) {
            return (float)-1.0;
        }
        float v = (float)act / (float)tot;
        return v;
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
        ProjectVersion start = ProjectVersion.getVersionByRevision(p, ProjectVersion.getFirstProjectVersion(p).getRevisionId());
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
        return null;
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

