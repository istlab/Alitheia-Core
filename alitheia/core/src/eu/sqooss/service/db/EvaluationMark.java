/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Paul J. Adams <paul.adams@siriusit.co.uk>
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

package eu.sqooss.service.db;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import eu.sqooss.core.AlitheiaCore;

/**
 * An evaluation mark records that a given metric (from a plug-in)
 * has evaluated and stored *something* about a given project,
 * as well as information on when that evaluation was run.
 * Only projects with evaluation marks will be displayed to
 * the user in the public interface. Metrics should record
 * evaluation marks for themselves.
 * 
 * An evaluation mark may be associated with a project version;
 * this may be used by a plug-in to describe things like "the
 * latest version to be evaluated was V", at its discretion.
 * The associated version may be null.
 */
public class EvaluationMark extends DAObject {
    /**
     * An evaluation mark is specific to one metric.
     * Each matric in a plug-in should write its own
     * evaluation mark, but the plugin is considered to
     * be evaluated against a project if any of its
     * metrics is evaluated.
     */
    private Metric metric;
    /**
     * Project the mark is for.
     */
    private StoredProject storedProject;
    /**
     * Timestamp (in SQL notation) of when the mark was
     * placed -- this is independent of what it was set
     * against, though.
     *
     * An evaluation mark is set at some specific time; this is
     * informational for the users and does not affect the mark
     * itself. It can be used to display to the user when a particular
     * project/metric combination was last updated.
     * 
     * @return the date this metric/project combo was last evaluated.
     */
    private Timestamp whenRun;
    /**
     * An evaluation mark *may* be connected to a project version.
     * It does not have to be, though. If it is, the mark says something
     * like "this project is evaluated all the way through to version V."
     * The exact meaning is up to the metric, of course.
     */
    private ProjectVersion version;
    
    public EvaluationMark() {
        super();
    }

    /**
     * Convenience constructor for setting some of the fields of
     * an evaluation mark in one go. The timestamp is set to now;
     * the version may be null to accomodate evaluation marks that
     * do not refer to a specific version.
     * 
     * @param m Metric the evaluation is for
     * @param p Project the evaluation is for
     */
    public EvaluationMark(Metric m, StoredProject p) {
        super();
        this.metric = m;
        this.storedProject = p;
        this.version = null;
        this.whenRun = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Convenience constructor for setting some of the fields of
     * an evaluation mark in one go. The timestamp is set to now;
     * the version may be null to accomodate evaluation marks that
     * do not refer to a specific version.
     * 
     * @param m Metric the evaluation is for
     * @param v Project version that the evaluation is for (may be null)
     */
    public EvaluationMark(Metric m, ProjectVersion v) {
        this(m, v.getProject());
        this.version = v;
    }
    
    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public StoredProject getStoredProject() {
        return storedProject;
    }

    public void setStoredProject(StoredProject storedProject) {
        if ((null != version) && !version.getProject().equals(storedProject)) {
            // Mismatch between project and associated version.
            // TODO: complain loudly.
        }
        this.storedProject = storedProject;
    }

    public Timestamp getWhenRun() {
    	return whenRun;
    }
    
    public void setWhenRun(Timestamp w) {
    	whenRun = w;
    }

    /**
     * Convenience method to set the whenRun property to "now".
     */
    public void updateWhenRunToNow() {
        this.whenRun = new Timestamp(System.currentTimeMillis());
    }
    
    public ProjectVersion getVersion() {
        return version;
    }
    
    public void setVersion(ProjectVersion v) {
        if (null != storedProject) {
            if ((null != v) && !storedProject.equals(v.getProject())) {
                // Mismatch between version and projet
                // TODO: complain loudly.
            } else {
                // Either v is null (no problem) or the projects
                // are equal (no problem either).
            }
        }
        version = v;
        // TODO: maybe set project from the version as well?
    }

    /**
     * Convenience method to look up an evaluation mark for a given
     * (metric,project) pair; there should be (but this is not enforced)
     * only one mark per par. If there are more than one pair, returns
     * the first one in some arbitrary ordering; if there are none, 
     * return null.
     * 
     * @param m Metric to look up
     * @param p Project to look up
     * @return First mark for this particular (metric,project) pair, or
     *         null if there is none.
     */
    public static EvaluationMark getEvaluationMarkByMetricAndProject(Metric m, StoredProject p) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        HashMap<String,Object> parameters = new HashMap<String,Object>(2);
        parameters.put("metric", m);
        parameters.put("storedProject", p);

        List<EvaluationMark> l = dbs.findObjectsByProperties(EvaluationMark.class,
                parameters);
        if ((null != l) && !l.isEmpty()) {
            if (l.size()>1) {
                // TODO: We would want to warn about this, but then
                //       we need to get a suitable logger from somewhere.
            }
            return l.get(0);
        } else {
            return null;
        }
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

