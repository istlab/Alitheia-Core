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

import java.util.List;
import java.util.HashMap;

import eu.sqooss.service.tds.ProjectRevision;
import eu.sqooss.core.AlitheiaCore;

/**
 * Instances of this class represent the result of measurements made
 * against ProjectVersions as stored in the database
 */
public class ProjectVersionMeasurement extends MetricMeasurement {
    /**
     * The ProjectVersion to which the instance relates
     */
    private ProjectVersion projectVersion;

    public ProjectVersionMeasurement() {
        super();
    }

    /**
     * Convenience constructor that sets all of the fields in the
     * measurement at once, saving a few (explicit) method calls. 
     * @param m Metric the measurement is for
     * @param p Project version the metric was applied to
     * @param v Resulting value
     */
    public ProjectVersionMeasurement(Metric m, ProjectVersion p, long v) {
        this();
        setMetric(m);
        setProjectVersion(p);
        setResult(String.valueOf(v));
    }
    
    public ProjectVersion getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(ProjectVersion projectVersion) {
        this.projectVersion = projectVersion;
    }
    
    /**
     * For a given metric and project, return the latest version of that
     * project that was actually measured.  If no measurements have been made, 
     * it returns revision 0. It will not return null. For the returned revision
     * greater than 0, there is a measurement in the database.
     * 
     * @param m Metric to look for
     * @param p Project to look for
     * @return Last version measured, or revision 0.
     */
    public static ProjectVersion getLastMeasuredVersion(Metric m, StoredProject p) {
        String query = "select pvm from ProjectVersionMeasurement pvm, ProjectVersion pv" +
           " where pvm.projectVersion = pv" +
           " and pvm.metric = :metric and pv.project = :project" +
           " order by pv.timestamp desc";

        HashMap<String, Object> params = new HashMap<String, Object>(4);
        params.put("metric", m);
        params.put("project", p);
        List<?> pvmList = AlitheiaCore.getInstance().getDBService().doHQL( query, params, 1);
	    
        ProjectVersion previous = pvmList.isEmpty() ? 
                ProjectVersion.getVersionByRevision(p, new ProjectRevision(0)) :
                ((ProjectVersionMeasurement) pvmList.get(0)).getProjectVersion();
        return previous;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab

