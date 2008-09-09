/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008-2008 by Sebastian Kuegler <sebas@kde.org>
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import eu.sqooss.webui.ListView;
import eu.sqooss.webui.Metric;
import eu.sqooss.webui.Project;
import eu.sqooss.webui.Result;
import eu.sqooss.webui.Metric.MetricActivator;
import eu.sqooss.webui.Metric.MetricType;
import eu.sqooss.webui.datatype.AbstractDatatype;
import eu.sqooss.webui.datatype.File;
import eu.sqooss.webui.widgets.WinIcon;
import eu.sqooss.ws.client.datatypes.WSMetricsResultRequest;

public class FileListView extends ListView {
    /**
     * Static file type definition for files of type regular file.
     */
    public static int FILES     = 2;

    /**
     * Static file type definition for files of type folder.
     */
    public static int FOLDERS   = 4;

    // Contains the list of files that will be presented by this view
    private SortedMap<String, File> files = new TreeMap<String, File>();

    // Holds the selected project's object
    private Project project;

    // Contains the Id of the selected project's version (if any)
    private Long versionId;

    // Contains the selected file type
    private int type = 0;

    // Contains the view's status line
    private String status = "";

    /**
     * Instantiates a new <code>FileListView</code> object and initializes it
     * with the given collection of project files. The files stored in the
     * given collection are filtered according to the selected file type
     * (either <code>FILES</code> or <code>FOLDERS</code>), thus this view
     * will only show files which are of the same type as the one specified.
     *
     * @param filesList the list of project files
     *
     */
    public FileListView (Collection<File> filesList, int type) {
        this.type = type;
        for (File nextFile : filesList)
            addFile(nextFile);
    }

    /**
     * Return the number of files that are stored in this object.
     *
     * @return The number of files.
     */
    public Integer size() {
        return files.size();
    }

    /**
     * Adds a single file to the stored files list. If the file object is
     * <code>null</code> or doesn't match the selected type, then it will be
     * skipped.
     *
     * @param file the file object
     */
    public void addFile(File file) {
        if (file != null) {
            if (file.getIsDirectory()) {
                if (type == FOLDERS)
                    files.put(file.getShortName(), file);
            }
            else {
                if (type == FILES)
                    files.put(file.getShortName(), file);
            }
        }
    }

    /**
     * Initializes this object with a new files collection. If the collection
     * object is <code>null</code>, then it will be skipped, and the current
     * one kept intact.
     *
     * @param filesList the list of files to substitute the current one
     */
    public void setFiles(Collection<File> filesList) {
        if (filesList != null)
            for (File nextFile : filesList)
                addFile(nextFile);
    }

    /**
     * Sets the project, that is associated with this view.
     *
     * @param projectId the project's object
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Sets the Id of the project's version that is associated with this view.
     *
     * @param versionId the version Id
     */
    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getStatus() {
        return status;
    }

    // TODO: This method can filter out some of the files from the given list,
    // by using the specified set of filters.
    public List<File> filterFiles (List<File> filesList) {
        List<File> result = new ArrayList<File>();
        if (filesList != null)
            result = filesList;
        return result;
    }

    public void fetchFilesResults (List<String> mnemonics) {
        if ((mnemonics == null) || (mnemonics.isEmpty())) return;
        if (files.isEmpty()) return;
        HashMap<Long, AbstractDatatype> items =
            new HashMap<Long, AbstractDatatype>();
        for (AbstractDatatype nextItem : files.values())
            items.put(nextItem.getId(), nextItem);
        // Create an results request object
        WSMetricsResultRequest request = new WSMetricsResultRequest();
        request.setProjectFile(true);
        // Set the Id of the selected items (DAOs)
        int index = 0;
        long[] itemIds = new long[items.size()];
        for (Long nextItemId : items.keySet())
            itemIds[index++] = nextItemId;
        request.setDaObjectId(itemIds);
        // Set the mnemonics of the selected metrics
        request.setMnemonics(mnemonics.toArray(new String[mnemonics.size()]));
        // Retrieve the evaluation result from the SQO-OSS framework
        for (Result nextResult : terrier.getResults(request)) {
            if (items.containsKey(nextResult.getId()))
                items.get(nextResult.getId()).addResult(nextResult);
        }
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.ListView#getHtml(long)
     */
    public String getHtml(long in) {
        StringBuffer html = new StringBuffer();
        List<String> mnemonics = new ArrayList<String>();
        int numFiles = 0;
        int numFolders = 0;
        if (size() > 0) {
            // Display all folders
            if (type == FOLDERS) {
                // Fetch evaluation results for the selected resources
                mnemonics.clear();
                mnemonics = new ArrayList<String>(
                        project.getSelectedMetrics().getMetricMnemonics(
                                MetricActivator.PROJECTFILE,
                                MetricType.SOURCE_FOLDER).values());
                fetchFilesResults(mnemonics);
                // Display the list of folders
                for (File nextFile : files.values()) {
                    if (nextFile.getIsDirectory()) {
                        if ((settings.getShowFileResultsOverview()
                                && (mnemonics.size() > 0))
                                && (numFolders == 0)) {
                            html.append(sp(in++) + "<table class=\"def\""
                                    + " style=\"width: "
                                    + (20 + mnemonics.size() * 10)
                                    + "em;\">\n");
                            html.append(sp(in++) + "<thead>\n");
                            html.append(sp(in++) + "<tr>\n");
                            html.append(sp(in) + "<td class=\"def_head\""
                                    + " style=\"width: 20em;\">"
                                    + "Folder name" + "</td>\n");
                            WinIcon icoClose = new WinIcon();
                            icoClose.setPath(getServletPath());
                            icoClose.setParameter("deselectMetric");
                            icoClose.setImage("/img/icons/16x16/application-exit.png");
                            icoClose.setAlt("Deselect metric");
                            for (String nextMnemonic : mnemonics) {
                                Metric nextMetric =
                                    project.getEvaluatedMetrics()
                                        .getMetricByMnemonic(nextMnemonic);
                                icoClose.setValue(nextMetric.getId().toString());
                                html.append(sp(in) + "<td class=\"def_head\""
                                    + " style=\"width: 10em;\""
                                    + " title=\"" 
                                    + nextMetric.getDescription()
                                    + "\""
                                    + ">"
                                    + "<div class=\"def_head\">"
                                    + nextMnemonic
                                    + "<div class=\"def_head_bar\">"
                                    + icoClose.render()
                                    + "</div>"
                                    + "</div>"
                                    + "</td>\n");
                            }
                            html.append(sp(--in) + "</tr>\n");
                            html.append(sp(--in) + "</thead>\n");
                        }
                        numFolders++;
                        nextFile.setSettings(settings);
                        if ((settings.getShowFileResultsOverview())
                                && (mnemonics.size() > 0))
                            html.append((nextFile != null)
                                    ? sp(in) + nextFile.getHtml(versionId, mnemonics)
                                    : "");
                        else
                            html.append((nextFile != null)
                                    ? sp(in) + "<div>" 
                                            + nextFile.getHtml(versionId)
                                            + "</div>\n"
                                    : "");
                    }
                }
                if ((settings.getShowFileResultsOverview())
                        && (mnemonics.size() > 0)
                        && (numFolders > 0)) {
                    html.append(sp(--in) + "</table>\n");
                }
            }
            // Display all files
            if (type == FILES) {
                // Fetch evaluation results for the selected resources
                mnemonics.clear();
                mnemonics = new ArrayList<String>(
                        project.getSelectedMetrics().getMetricMnemonics(
                                MetricActivator.PROJECTFILE,
                                MetricType.SOURCE_CODE).values());
                fetchFilesResults(mnemonics);
                // Display the list of files
                for (File nextFile : files.values()) {
                    if (nextFile.getIsDirectory() == false) {
                        if ((settings.getShowFileResultsOverview()
                                && (mnemonics.size() > 0))
                                && (numFiles == 0)) {
                            html.append(sp(in++) + "<table class=\"def\""
                                    + " style=\"width: "
                                    + (20 + mnemonics.size() * 10)
                                    + "em;\">\n");
                            html.append(sp(in++) + "<thead>\n");
                            html.append(sp(in++) + "<tr>\n");
                            html.append(sp(in) + "<td class=\"def_head\""
                                    + " style=\"width: 20em;\">"
                                    + "File name" + "</td>\n");
                            WinIcon icoClose = new WinIcon();
                            icoClose.setPath(getServletPath());
                            icoClose.setParameter("deselectMetric");
                            icoClose.setImage("/img/icons/16x16/application-exit.png");
                            icoClose.setAlt("Deselect metric");
                            for (String nextMnemonic : mnemonics) {
                                Metric nextMetric =
                                    project.getEvaluatedMetrics()
                                        .getMetricByMnemonic(nextMnemonic);
                                icoClose.setValue(nextMetric.getId().toString());
                                html.append(sp(in) + "<td class=\"def_head\""
                                    + " style=\"width: 10em;\""
                                    + " title=\"" 
                                    + nextMetric.getDescription()
                                    + "\""
                                    + ">"
                                    + "<div class=\"def_head\">"
                                    + nextMnemonic
                                    + "<div class=\"def_head_bar\">"
                                    + icoClose.render()
                                    + "</div>"
                                    + "</div>"
                                    + "</td>\n");
                            }
                            html.append(sp(--in) + "</tr>\n");
                            html.append(sp(--in) + "</thead>\n");
                        }
                        numFiles++;
                        nextFile.setSettings(settings);
                        if ((settings.getShowFileResultsOverview())
                                && (mnemonics.size() > 0))
                            html.append((nextFile != null)
                                    ? sp(in) + nextFile.getHtml(versionId, mnemonics)
                                    : "");
                        else
                            html.append((nextFile != null)
                                    ? sp(in) + "<div>" 
                                            + nextFile.getHtml(versionId)
                                            + "</div>\n"
                                    : "");
                    }
                }
                if ((settings.getShowFileResultsOverview())
                        && (mnemonics.size() > 0)
                        && (numFiles > 0)) {
                    html.append(sp(--in) + "</table>\n");
                }
            }
        }
        else {
            if (type == (FOLDERS | FILES))
                html.append("<i>This folder contains no sub-folders nor files.</i>");
            else if (type == FOLDERS)
                html.append("<i>This folder contains no sub-folder.</i>");
            else if (type == FILES)
                html.append("<i>This folder contains no files.</i>");
        }
        // Construct the status line's messages
        if (type == (FOLDERS | FILES))
            status = (sp(in) + "Total: "
                    + ((numFolders > 0)
                            ? ((numFolders > 1)
                                    ? numFolders + " folders"
                                    : "one folder")
                            : "")
                    + (((numFolders > 0) && (numFiles > 0)) ? " and " : "")
                    + ((numFiles > 0)
                            ? ((numFiles > 1)
                                    ? numFiles + " files"
                                    : "one file")
                            : "")
                    + " found");
        else if (type == FOLDERS)
            status = (sp(in) + "Total: "
                    + ((numFolders > 0)
                            ? ((numFolders > 1)
                                    ? numFolders + " folders"
                                    : "one folder")
                            : "zero folders")
                    + " found");
        else if (type == FILES)
            status = (sp(in) + "Total: "
                    + ((numFiles > 0)
                            ? ((numFiles > 1)
                                    ? numFiles + " files"
                                    : "one file")
                            : "zero files")
                    + " found");
        return html.toString();
    }

}
