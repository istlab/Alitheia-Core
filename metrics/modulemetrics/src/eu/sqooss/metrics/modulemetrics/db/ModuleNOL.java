/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.metrics.modulemetrics.db;

import java.util.HashMap;
import java.util.List;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;

public class ModuleNOL extends DAObject {
    /**
     * Stores the metric calculation result.
     */
    private String result;

    /**
     * The folder against which this measurement was performed.
     */
    private ProjectFile projectFile;

    /**
     * The version against which this measurement was performed.
     */
    private ProjectVersion projectVersion;

    /**
     * Simple constructor. Instantiates a new <code>ModuleNOL</code> object.
     */
    public ModuleNOL() {
        super();
    }

    /**
     * This constructor instantiates a new <code>ModuleNOL</code> and
     * initializes it with the given parameters.
     * 
     * @param pf <code>ProjectFile</code> DAO, representing the folder, where
     *   this measurement was calculated.
     * @param pv <code>ProjectVersion</code> DAO, representing the version,
     *   where this measurement was calculated.
     * @param result the <code>String</code> value of this measurement.
     */
    public ModuleNOL(ProjectFile pf, ProjectVersion pv, String result) {
        super();
        setProjectFile(pf);
        setProjectVersion(pv);
        setResult(result);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ProjectFile getProjectFile() {
        return projectFile;
    }

    public void setProjectFile(ProjectFile pf) {
        projectFile = pf;
    }

    public ProjectVersion getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(ProjectVersion pv) {
        projectVersion = pv;
    }

    public static String getResult(ProjectFile pf, ProjectVersion pv) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramFile = "paramFile"; 
        String paramTimestamp = "paramTimestamp";

        String query = "select res "
            + " from ModuleNOL res"
            + " where res.projectFile = :" + paramFile
            + " and res.projectVersion.timestamp <= :" + paramTimestamp
            + " order by res.projectVersion.timestamp desc";

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(paramFile, pf);
        parameters.put(paramTimestamp, pv.getTimestamp());

        List<?> answer = dbs.doHQL(query, parameters);
        if (answer.size() > 0)
            return answer.get(0).toString();

        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return result;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

