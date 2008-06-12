/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Sebastian Kuegler <sebas@kde.org>
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

package eu.sqooss.webui;

import java.util.*;

import eu.sqooss.scl.WSException;

import eu.sqooss.ws.client.datatypes.WSStoredProject;
import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;

/**
 * This class represents a project that has been evaluated by Alitheia.
 * It provides access to project metadata, files and versions in this
 * project and holds metrics that have been applied to this project.
 *
 * The Project class is part of the high-level webui API.
 */
public class Project extends WebuiItem {

    //private Long    id;
    private String  name;
    private String  bts;
    private String  repository;
    private String  mail;
    private String  contact;
    private String  website;

    // Contains the version number of the last selected version
    private Long currentVersionId;

    private Version currentVersion;
    private SortedMap<Long, Metric> metrics;

    /**
     * Empty constructor so we can create this object without having
     * any further knowledge. You'll need to initProject() manually,
     * providing a WSProject your self..
     */
    public Project () {

    }

    /**
     * Constructor that initialises the Project and its data from a WStoredProject.
     */
    public Project (WSStoredProject p, Terrier t) {
        terrier = t;
        initProject(p);
   }

    /**
     * Initialises the Project from a WSStoredProject and fills it with
     * relevant data. This function is automatically called from the non-empty
     * ctor and needs to be manually called if the empty ctor is used,
     */
    private void initProject(WSStoredProject p) {
        page = "/projects.jsp";
        reqName = "pid";
        id = p.getId();
        name = p.getName();
        bts = p.getBugs();
        repository = p.getRepository();
        mail = p.getMail();
        contact = p.getContact();
        website = p.getWebsite();
        currentVersion = getLastVersion();
    }

    /**
     * Fetches the Project's data from the SCL by ID.
     */
    public void retrieveData() {
        if (id == null) {
            return;
        }
        WSStoredProject[] storedProjects;
        try {
            storedProjects = terrier.connection().getProjectAccessor().getProjectsByIds(new long[] {id});
            if (storedProjects.length != 0) {
                initProject(storedProjects[0]);
            } else {
                addError("The project does not exist!");
            }
        } catch (WSException wse) {
            addError("Could not retrieve the project:" + wse.getMessage());
        }
    }

    /**
     *Fetches the Metrics that have been applied to this project through the SCL.
     */
    public void retrieveMetrics() {
        WSMetric[] wsmetrics = terrier.getMetricsForProject(id);
        if (wsmetrics == null) {
            return;
        }
        for (int i = 0; i < wsmetrics.length; i++ ) {
            metrics.put(wsmetrics[i].getId(), new Metric(wsmetrics[i], terrier));
        }
    }

    public void setTerrier(Terrier t) {
        terrier = t;
    }

    public Long getId () {
        return id;
    }

    public String getName () {
        return name;
    }

    public String getWebsite () {
        return website;
    }

    public String getMail () {
        return mail;
    }

    public String getContact () {
        return contact;
    }

    public String getBts() {
        return bts;
    }

    public String getRepository() {
        return repository;
    }

    /** Returns an HTML table with meta information about this project.
     */
    public String getInfo() {
        StringBuilder html = new StringBuilder();
        html.append("\n<table class=\"projectinfo\">\n\t<tr>\n\t\t<td>");
        html.append("<strong>Website:</strong> \n\t\t</td><td>\n"
                + (getWebsite() != null
                        ? "<a href=\"" + getWebsite() + "\">" + getWebsite() + "</a>"
                        : "<i>undefined</i>"));
        html.append("\n\t\t</td>\n\t</tr>\n\t<tr>\n\t\t<td>");
        html.append(icon("mail-message-new") + "<strong>Contact:</strong> \n\t\t</td><td>\n"
                + (getContact() != null
                        ? "<a href=\"" + getContact() + "\">" + getContact() + "</a>"
                        : "<i>undefined</i>"));
        html.append("\n\t\t</td>\n\t</tr>\n\t<tr>\n\t\t<td>");
        html.append(icon("vcs_status") + "<strong>SVN Mirror:</strong> \n\t\t</td><td>\n"
                + (getRepository() != null
                        ? "<a href=\"files.jsp" + getId() + "\">" + getRepository() + "</a>"
                        : "<i>undefined</i>"));
        html.append("\n\t\t</td>\n\t</tr>\n\t<tr>\n\t\t<td>");
        html.append(icon("kbugbuster") + "<strong>Bug Data:</strong> \n\t\t</td><td>\n"
                + (getBts() != null
                        ? getBts()
                        : "<i>undefined</i>"));
        html.append("\n\t\t</td>\n\t</tr>\n</table>");
        return html.toString();
    }

    /** Returns an HTML representation of the project, mainly the name.
     */
    public String getHtml() {
        StringBuilder html = new StringBuilder("<!-- Project -->\n");
        html.append("<h2>" + getName() + " (" + getId() + ")</h2>");
        html.append(getInfo());
        return html.toString();
    }

    /** Returns a short HTML representation of the project, mainly the name.
     */
    public String shortName() {
        return getName();
    }

    /** Returns an HTML table (in fact a MetricsView of the metrics applied to this Project.
     */
    public String showMetrics() {
        StringBuilder html = new StringBuilder();
        MetricsTableView metricsView = terrier.getMetrics4Project(id);
        if (metricsView != null ) {
            html.append(metricsView.getHtml());
        } else {
            html.append("<strong>Could not retrieve metrics for this project.</strong> The following errors were reported:<br />");
            html.append(terrier.getError());
        }
        return html.toString();
    }

    /**
     * Gets the first known version of this project.
     *
     * @return the version number, or null if the project has no version.
     */
    public Version getFirstVersion() {
        // FIXME: first != current
        return currentVersion;
    }

    /**
     * Gets the last known version of this project.
     *
     * @return the version number, or null if the project has no version.
     */
    public Version getLastVersion() {
        return terrier.getLastProjectVersion(getId());
    }

    /**
     * Gets a version by its ID.
     *
     * @return The Version under that id.
     */
    public Version getVersion(Long versionId) {
        return terrier.getVersion(id, versionId);
    }

    /**
     * Gets the current version.
     *
     * @return The Version under that id.
     */
    public Version getCurrentVersion() {
        return currentVersion;
    }

    /**
     * Returns the last selected version of this project.
     *
     * @return the version number, or null if there is no selected version.
     */
    public Long getCurrentVersionId() {
        try {
            if ( currentVersionId == null ) {
                return getLastVersion().getId();
            }
            return currentVersionId;
        } catch (NullPointerException e) {
            terrier.addError("Could not retrieve current version.");
            return null;
        }
    }

    public int countVersions() {
        // FIXME: Correctly count ...
        return 1337;
    }

    /**
     * Sets the specified version as selected version for this project
     * @param versionNumber the version number
     */
    public void setCurrentVersionId(Long versionNumber) {
        currentVersionId = versionNumber;
    }
}
