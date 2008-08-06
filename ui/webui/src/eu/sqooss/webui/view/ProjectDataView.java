/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Sebastian Kuegler <sebas@kde.org>
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

package eu.sqooss.webui.view;

import eu.sqooss.webui.Functions;
import eu.sqooss.webui.ListView;
import eu.sqooss.webui.Metric;
import eu.sqooss.webui.Project;

/**
 * The class <code>ProjectDataView</code> renders an HTML sequence that
 * presents an overview information for a single project.
 */
public class ProjectDataView extends ListView {
    /** Holds the project object. */
    private Project project;

    /**
     * Instantiates a new project info view, and initializes it with the
     * given project object.
     * 
     * @param project the project
     */
    public ProjectDataView(Project project) {
        super();
        this.project = project;
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.ListView#getHtml(long)
     */
    public String getHtml(long in) {
        return null;
    }

    public String getCodeInfo(long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));
        StringBuilder b = new StringBuilder();
        b.append(sp(in++) + "<table>\n");
        // Project versions
        b.append(sp(in++) + "<tr>\n");
        b.append(sp(in) + "<td><b>Versions:</b></td>"
                + "<td>" + project.getVersionsCount() + "</td>\n");
        b.append(sp(--in) + "</tr>\n");
        // Files in the latest version
        b.append(sp(in++) + "<tr>\n");
        Long filesCount = project.getLastVersion().getFilesCount();
        if (filesCount == null)
            filesCount = new Long(0);
        b.append(sp(in) + "<td><b>Files:</b></td>"
                + "<td>" + filesCount + "</td>\n");
        b.append(sp(--in) + "</tr>\n");

        //====================================================================
        // Metric specific information
        //====================================================================

        // Number of classes in the latest version
        Metric metricNOCL =
            project.getEvaluatedMetrics().getMetricByMnemonic("NOCL");
        if (metricNOCL != null) {
            
        }
        b.append(sp(--in) + "</table>\n");
        return b.toString();
    }
}
