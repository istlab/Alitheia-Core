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

import eu.sqooss.ws.client.datatypes.WSStoredProject;

/**
 * This class represents a project that has been evaluated by the SQO-OSS
 * framework.
 * <br/>
 * It provides access to the project metadata, files and versions in this
 * project. In addition it provides information about the metrics that have
 * been applied to this project and its resources.
 */
public class Project extends WebuiItem {

    // Project metadata
    private String  bts;
    private String  repository;
    private String  mail;
    private String  contact;
    private String  website;

    // Holds the version number of the currently selected version
    private Long currentVersionId;

    // Holds the number of versions in this project
    Long versionsCount = null;

    // Versions cache
    private Version firstVersion = null;
    private Version lastVersion = null;
    private Version currentVersion = null;

    // A cache for all metrics that have been evaluated on this project
    private List<Metric> metrics = new ArrayList<Metric>();

    // Stores the list of Ids of all selected metrics
    private List<Long> selectedMetrics = new ArrayList<Long>();

    /**
     * Instantiates a new <code>Project</code> object, without initializing
     * its data. The object can be initialized later on by calling its
     * <code>initProject()</code> method.
     */
    public Project () {
        setServletPath("/projects.jsp");
        reqParName = "pid";
    }

    /**
     * Instantiates a new <code>Project</code> object, and initializes it with
     * the data stored in the given <code>WStoredProject</code> object.
     * 
     * @param p the project object
     */
    public Project (WSStoredProject p) {
        setServletPath("/projects.jsp");
        reqParName = "pid";
        initProject(p);
   }

    /**
     * Initializes or updates this object with the metadata provided from the
     * given <code>WStoredProject</code> object.
     * 
     * @param p the project object
     */
    private void initProject(WSStoredProject p) {
        id = p.getId();
        name = p.getName();
        bts = p.getBugs();
        repository = p.getRepository();
        mail = p.getMail();
        contact = p.getContact();
        website = p.getWebsite();
    }

    /**
     * Copies the target object's metadata into this object.
     * 
     * @param p the target <code>Project</code> instance
     */
    public void copyFrom (Project p) {
        id = p.getId();
        name = p.getName();
        bts = p.getBugs();
        repository = p.getRepository();
        mail = p.getMail();
        contact = p.getContact();
        website = p.getWebsite();
    }

    /**
     * Gets the project's homepage.
     * 
     * @return the project's homepage, or <code>null</code> when the
     *   project is not yet initialized.
     */
    public String getWebsite () {
        return website;
    }

    /**
     * Gets the project's contact email
     * 
     * @return the project's contact email, or <code>null</code> when the
     *   project is not yet initialized.
     */
    public String getContact () {
        return contact;
    }

    /**
     * Gets the project's mailing list.
     * 
     * @return the project's mailing list, or <code>null</code> when the
     *   project is not yet initialized.
     */
    public String getMail () {
        return mail;
    }

    /**
     * Gets the project's BTS URL.
     * 
     * @return the project's BTS URL, or <code>null</code> when the
     *   project is not yet initialized.
     */
    public String getBugs() {
        return bts;
    }

    /**
     * Gets the project's source repository.
     * 
     * @return the project's source repository, or <code>null</code> when the
     *   project is not yet initialized.
     */
    public String getRepository() {
        return repository;
    }

    /**
     * Retrieves all the data that is required by this object from the
     * SQO-OSS framework, unless the cache contains some data already.
     * 
     * @param terrier the <code>Terrier<code> instance
     */
    public void retrieveData(Terrier terrier) {
        if ((terrier == null) || (id == null))
            return;
        setTerrier(terrier);
        retrieveMetrics();
    }

    /**
     * Flushes all the data that is cached by this object.
     */
    public void flushData() {
        currentVersionId = null;
        versionsCount = null;
        firstVersion = null;
        lastVersion = null;
        currentVersion = null;
        metrics = null;
        selectedMetrics = new ArrayList<Long>();
    }

    /**
     * Retrieves the list of all metrics that have been evaluated on this
     * project from the SQO-OSS framework, unless the cache contains some
     * data already.
     * 
     * @return the list of metrics evaluated on this project, or empty list
     *   when none are found.
     */
    public List<Metric> retrieveMetrics() {
        if (metrics == null)
            metrics = terrier.getMetricsForProject(id);
        return metrics;
    }

    /**
     * Renders the metadata stored in this project's object into HTML.
     * 
     * @param in indentation depth of the generated HTML content.
     * 
     * @return The HTML content.
     */
    public String getInfo(long in) {
        StringBuilder html = new StringBuilder();
        html.append(sp(in++) + "<table class=\"projectinfo\">\n");
        // Project website
        html.append(sp(in++) + "<tr>\n");
        html.append(sp(in) + "<td>"
                + "<strong>Website:</strong>"
                + "</td>\n");
        html.append(sp(in) + "<td>"
                + (getWebsite() != null
                        ? "<a href=\"" + getWebsite() + "\">"
                                + getWebsite() + "</a>"
                        : "<i>undefined</i>")
                + "</td>\n");
        html.append(sp(--in) + "</tr>\n");
        // Project contact address
        html.append(sp(in++) + "<tr>\n");
        html.append(sp(in) + "<td>"
                + icon("mail-message-new")
                + "<strong>Contact:</strong>"
                + "</td>\n");
        html.append(sp(in) + "<td>"
                + (getContact() != null
                        ? "<a href=\"" + getContact() + "\">"
                                + getContact() + "</a>"
                        : "<i>undefined</i>")
                        + "</td>\n");
        html.append(sp(--in) + "</tr>\n");
        // Project's source repository
        html.append(sp(in++) + "<tr>\n");
        html.append(sp(in) + "<td>"
                + icon("vcs_status")
                + "<strong>SVN Mirror:</strong>"
                + "</td>\n");
        html.append(sp(in) + "<td>"
                + (getRepository() != null
                        ? "<a href=\"files.jsp" + getId() + "\">"
                                + getRepository() + "</a>"
                        : "<i>undefined</i>")
                + "</td>\n");
        html.append(sp(--in) + "</tr>\n");
        // Project's BTS
        html.append(sp(in++) + "<tr>\n");
        html.append(sp(in) + "<td>"
                + icon("kbugbuster") + "<strong>Bug Data:</strong>"
                + "</td>\n");
        html.append(sp(in) + "<td>"
                + (getBugs() != null
                        ? getBugs()
                        : "<i>undefined</i>")
                + "</td>\n");
        html.append(sp(--in) + "</tr>\n");
        html.append(sp(--in) + "</table>");
        return html.toString();
    }

    /**
     * TODO: Add a method description.
     * 
     * @param fileId the file Id
     * @param in the indentation depth
     * 
     * @return The rendered HTML content.
     */
    public String renderFileVerbose(Long fileId, long in) {
        StringBuilder b = new StringBuilder("");
        File selFile = null;
        List<Result> selFileResults = new ArrayList<Result>();
        if (fileId != null)
            selFile = currentVersion.getFile(fileId);
            if (selFile != null)
                selFileResults = selFile.getResults();
        if (selFile == null) {
            b.append(sp(in) + Functions.error("File not found!"));
        }
        else if (selFileResults.isEmpty()) {
            b.append(sp(in) + Functions.warning("No evaluation result."));
        }
        else {
            // File name
            int maxNameLength = 50;
            String fileName = selFile.getName();
            if (selFile.getShortName().length() <= maxNameLength) {
                while (fileName.length() > maxNameLength) {
                    fileName = fileName.substring(
                            fileName.indexOf('/') + 1, fileName.length());
                }
                fileName = ".../" + fileName;
            }
            else {
                fileName = selFile.getShortName();
                fileName = fileName.substring(0, maxNameLength - 1);
            }
            b.append(sp(in) + "<span"
                    + " style=\"float: left; width: 60%; text-align:left;\">"
                    + "<b>File:</b> " + fileName
                    + "</span>");
            // "Compare against another version" field
            b.append(sp(in) + "<span"
                    + " style=\"float: right; width: 40%; text-align:right;\">"
                    + "<b>Compare with:</b> "
                    + "<input type=\"select\">"
                    + "</input>"
                    + "</span>");
            b.append(sp(in) + "<br/>\n");
            b.append(sp(in++) + "<table style=\"width: 100%;\">\n");
            // Table header
            b.append(sp(in++) + "<thead>\n");
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in) + "<td style=\"text-align: left; width: 15%;\">"
                    + "Metric</td>\n");
            b.append(sp(in) + "<td style=\"text-align: left; width: 50%;\">"
                    + "Description</td>\n");
            b.append(sp(in) + "<td style=\"text-align: left; width: 35%;\">"
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
                    for (Metric nextMetric : metrics)
                        if (nextMetric.getMnemonic().equals(mnemonic)) {
                            mnemToMetric.put(mnemonic, nextMetric);
                            metric = nextMetric;
                        }
                }
                // Display the metric statistic's row
                b.append(sp(in++) + "<tr>\n");
                b.append(sp(in) + "<td style=\"text-align: left;\">"
                        + metric.getMnemonic()
                        + "</td>\n");
                b.append(sp(in) + "<td style=\"text-align: left;\">"
                        + metric.getDescription()
                        + "</td>\n");
                b.append(sp(in) + "<td style=\"text-align: left;\">"
                        + nextResult.getString()
                        + "</td>\n");
                b.append(sp(in++) + "</tr>\n");
            }
            b.append(sp(--in) + "</table>\n");
        }
        b.append(sp(in) + "<br/>\n");
        b.append(sp(in) + "<a href=\"/files.jsp\""
                + " class=\"button\">"
                + "Back</a>\n");
        return b.toString();
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.WebuiItem#getHtml(long)
     */
    public String getHtml(long in) {
        StringBuilder html = new StringBuilder("");
        html.append(sp(in) + "<h2>" + getName() + " (" + getId() + ")</h2>");
        html.append(getInfo(in++));
        return html.toString();
    }

    /**
     * Sets the first version of this project.
     * 
     * @param version the version object
     */
    public void setFirstVersion(Version version) {
        firstVersion = version;
    }

    /**
     * Gets the first known version of this project.
     *
     * @return the first version's object, or <code>null<code> if the project
     *   has no versions at all.
     */
    public Version getFirstVersion() {
        if ((firstVersion == null) && (terrier != null))
            firstVersion = terrier.getFirstProjectVersion(getId());
        return firstVersion;
    }

    /**
     * Sets the last version of this project.
     * 
     * @param version the version object
     */
    public void setLastVersion(Version version) {
        lastVersion = version;
    }

    /**
     * Gets the last known version of this project.
     *
     * @return the last version's object, or <code>null</code> if the project
     *   has no versions at all.
     */
    public Version getLastVersion() {
        if ((lastVersion == null) && (terrier != null))
            lastVersion = terrier.getLastProjectVersion(getId());
        return lastVersion;
    }

    /**
     * Sets the current version.
     *
     * @param version the version object
     */
    public void setCurrentVersion(Version version) {
        if (version != null) {
            currentVersion = version;
            currentVersionId = version.getId();
        }
    }

    /**
     * Gets the current version.
     *
     * @return The version object.
     */
    public Version getCurrentVersion() {
        if (currentVersion == null)
            currentVersion = getLastVersion();
        return currentVersion;
    }

    /**
     * Sets the version with the given number as current.
     * 
     * @param versionNumber the version number
     */
    public void setCurrentVersionId(Long versionNumber) {
        currentVersionId = versionNumber;
    }

    /**
     * Returns the number of the current version.
     *
     * @return the version number, or <code>null</code> if a current version
     *   is not yet defined.
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

    /**
     * Retrieves a version by its Id from the SQO-OSS framework.
     *
     * @return The Version under that id.
     */
    public Version getVersion(Long versionId) {
        return terrier.getVersion(id, versionId);
    }

    /**
     * Returns the total number of versions in this project
     *
     * @return The number of version in this project.
     */
    public long countVersions() {
        if (versionsCount == null)
            versionsCount = terrier.getVersionsCount(id);
        if (versionsCount != null)
            return versionsCount;
        else
            return 0;
    }

    /**
     * Adds the metric with the given Id to the list of metrics which results
     * will be displayed for this project in the various WebUI views.
     * 
     * @param id the metric Id
     */
    public void selectMetric (Long id) {
        if (id != null)
            selectedMetrics.add(id);
    }

    /**
     * Removes the metric with the given Id from the list of metrics which
     * results will be displayed for this project in the various WebUI views.
     * 
     * @param id the metric Id
     */
    public void deselectMetric (Long id) {
        if (id != null)
            selectedMetrics.remove(id);
    }

    /**
     * Retrieve the list of Ids for all metrics that were selected for this
     * project.
     * 
     * @return the list of selected metric Ids
     */
    public List<Long> getSelectedMetrics () {
        return selectedMetrics;
    }

    /**
     * Gets the mnemonic names of the currently selected metrics.
     * 
     * @return The list mnemonic names, or an empty list when none are
     *   selected.
     */
    public Map<Long, String> getSelectedMetricMnemonics() {
        Map<Long, String> result = new HashMap<Long, String>();
        for (Long nextId : selectedMetrics) {
            for (Metric nextMetric : metrics)
                if (nextMetric.getId().longValue() == nextId)
                    result.put(nextId, nextMetric.getMnemonic());
        }
        return result;
    }

    /**
     * Verifies if this object is equal to the given <code>Project</code>
     * object.
     * 
     * @param target the target <code>Project</code> object
     * 
     * @return <code>true<code>, if this <code>Project</code> is equal to the
     *   given <code>Project</code> object, or <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object target) {
        if (this == target) return true;
        if (target == null) return false;
        if (getClass() != target.getClass()) return false;
        Project project = (Project) target;
        if (getId().equals(project.getId()))
            if (getName().equals(project.getId()))
                return true;
        return false;
    }
}
