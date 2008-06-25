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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.sqooss.webui.File;
import eu.sqooss.webui.Functions;
import eu.sqooss.webui.ListView;
import eu.sqooss.webui.Metric;
import eu.sqooss.webui.Project;
import eu.sqooss.webui.Result;

/**
 * The class <code>VerboseFileView</code> renders an HTML sequence that
 * verbosely presents the metric evaluation result of a single file in a
 * specific project version. In addition it provides mean for comparing the
 * results against the results calculated on this file in another project
 * revision.
 */
public class VerboseFileView extends ListView {
    // Holds the project object
    private Project project;

    // Hold the file Id
    private Long fileId;

    /**
     * Instantiates a new <code>VerboseFileView</code> object, and initializes
     * it with the given project object and file Id.
     * 
     * @param project the project object
     * @param fileId the file Id
     */
    public VerboseFileView(Project project, Long fileId) {
        super();
        this.project = project;
        this.fileId = fileId;
    }

    public String getHtml(long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));
        StringBuilder b = new StringBuilder("");
        File selFile = null;
        List<Result> selFileResults = new ArrayList<Result>();
        if (fileId != null)
            selFile = project.getCurrentVersion().getFile(fileId);
        if (selFile != null)
            selFileResults = selFile.getResults();
        if (selFile == null) {
            b.append(sp(in) + Functions.error("File not found!"));
        }
        else if (selFileResults.isEmpty()) {
            b.append(sp(in) + Functions.warning("No evaluation result."));
        }
        else {
            //================================================================
            // File information
            //================================================================
            // File name
            String fileName = selFile.getName();
            // Adjust the file name length
            if (selFile.getShortName().length() <= maxStrLength) {
                while (fileName.length() > maxStrLength) {
                    fileName = fileName.substring(
                            fileName.indexOf('/') + 1, fileName.length());
                }
                if (fileName.length() < selFile.getName().length())
                    fileName = ".../" + fileName;
            }
            else {
                fileName = ".../" + adjustRight(selFile.getShortName(), "...");
            }
            // Display the file name
            b.append(sp (in++) + "<form method=\"GET\" action=\""
                    + getServletPath() + "\">\n");
            b.append(sp(in) + "<span"
                    + " style=\"float: left; width: 60%; text-align:left;\">"
                    + "<b>Name: </b> " + fileName
                    + "</span>\n");
            // Display the "Compare against another version" field
            b.append(sp(in++) + "<span"
                    + " style=\"float: right; width: 40%; text-align:right;\">\n"
                    + sp(in) + "<b>Compare with:</b>\n");
            b.append(sp(in++) + "<select name=\"cvid\" size=\"1\""
                    + " style=\"width:70px;\">\n");
            b.append(sp(--in) + "</select>\n");
            b.append(sp(in) + "<input type=submit class=\"submit\""
                    + " value=\"Apply\">\n"
                    + sp (--in) + "</span>\n");
            b.append(sp(--in) + "</form>\n");
            b.append(sp(in) + "<br/>\n");
            b.append(sp(in) + "<br/>\n");
            //================================================================
            // Results table
            //================================================================
            b.append(sp(in++) + "<div id=\"table\">\n");
            b.append(sp(in++) + "<table>\n");
            // Table header
            b.append(sp(in++) + "<thead>\n");
            b.append(sp(in++) + "<tr class=\"head\">\n");
            b.append(sp(in) + "<td class=\"head\" style=\"width: 15%;\">"
                    + "Metric</td>\n");
            b.append(sp(in) + "<td class=\"head\" style=\"width: 50%;\">"
                    + "Description</td>\n");
            b.append(sp(in) + "<td class=\"head\" style=\"width: 35%;\">"
                    + "Result</td>\n");
            b.append(sp(--in) + "</tr>\n");
            b.append(sp(--in) + "</thead>\n");
            // Display all available results
            HashMap<String, Metric> mnemToMetric =
                new HashMap<String, Metric>();
            for (Result nextResult : selFileResults) {
                String mnemonic = nextResult.getMnemonic();
                Metric metric = null;
                if (mnemToMetric.containsKey(mnemonic)) {
                    metric = mnemToMetric.get(mnemonic);
                }
                else {
                    for (Metric nextMetric : project.retrieveMetrics())
                        if (nextMetric.getMnemonic().equals(mnemonic)) {
                            mnemToMetric.put(mnemonic, nextMetric);
                            metric = nextMetric;
                        }
                }
                // Display the metric statistic's row
                b.append(sp(in++) + "<tr>\n");
                b.append(sp(in) + "<td class=\"name\">"
                        + metric.getMnemonic()
                        + "</td>\n");
                b.append(sp(in) + "<td>"
                        + metric.getDescription()
                        + "</td>\n");
                b.append(sp(in) + "<td>"
                        + nextResult.getString()
                        + "</td>\n");
                b.append(sp(--in) + "</tr>\n");
            }
            b.append(sp(--in) + "</table>\n");
            b.append(sp(--in) + "</div>\n");
        }
        return b.toString();
    }

}
