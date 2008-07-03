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

import eu.sqooss.webui.datatypes.Developer;
import eu.sqooss.ws.client.datatypes.WSStoredProject;

/**
 * This class represents a project that has been evaluated by the SQO-OSS
 * framework.
 * <br/>
 * It provides access to the project metadata, versions and files.
 * In addition it provides information about the metrics that have been
 * applied to this project and its resources.
 */
public class Project extends WebuiItem {

    /*
     *  Project meta-data fields
     */
    private String bts;
    private String repository;
    private String mail;
    private String contact;
    private String website;
    private long[] developersIds;

    /** Holds the version number of the currently selected version. */
    private Long currentVersionId;

    /** Holds the number of versions in this project. */
    Long versionsCount = null;

    /*
     * Versions cache
     */
    private Version firstVersion = null;
    private Version lastVersion = null;
    private Version currentVersion = null;

    // A cache for all metrics that have been evaluated on this project
    private List<Metric> metrics = new ArrayList<Metric>();

    // Stores the list of Ids of all selected metrics
    private List<Long> selectedMetrics = new ArrayList<Long>();

    /** Developers cache. */
    HashMap<Long, Developer> developers = new HashMap<Long, Developer>();

    /** Stores the list of Ids of all selected developers */
    private List<Long> selectedDevelopers = new ArrayList<Long>();

    // Stores all project versions that were retrieved from this object
    private HashMap<Long, Version> versions = new HashMap<Long, Version>();

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
        if (p != null) {
            id = p.getId();
            name = p.getName();
            bts = p.getBugs();
            repository = p.getRepository();
            mail = p.getMail();
            contact = p.getContact();
            website = p.getWebsite();
            developersIds = p.getDevelopers();
        }
    }

    /**
     * Copies the target object's metadata into this object.
     * 
     * @param p the target <code>Project</code> instance
     */
    public void copy (Project p) {
        if (p != null) {
            id = p.getId();
            name = p.getName();
            bts = p.getBugs();
            repository = p.getRepository();
            mail = p.getMail();
            contact = p.getContact();
            website = p.getWebsite();
            developersIds = p.getDevelopersIds();
        }
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

    public long[] getDevelopersIds() {
        return developersIds;
    }

    public Collection<Developer> getDevelopers() {
        if ((isValid()) && (terrier != null) && (developers.isEmpty())){
            if (developersIds != null) {
                for (Developer nextDev : terrier.getDevelopers(developersIds))
                    this.developers.put(nextDev.getId(), nextDev);
            }
        }
        return developers.values();
    }

    /**
     * Retrieves all the data that is required by this object from the
     * SQO-OSS framework, unless the cache contains some data already
     * or this project is not yet initialized.
     * 
     * @param terrier the <code>Terrier<code> instance
     */
    public void retrieveData(Terrier terrier) {
        if (terrier == null)
                return;
        if (isValid()) {
            setTerrier(terrier);
            retrieveMetrics();
        }
        else
            terrier.addError("Invalid project!");
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
        versions.clear();
        metrics.clear();
        developers.clear();
        selectedMetrics = new ArrayList<Long>();
    }

    /**
     * Flushes the list of evaluated metrics that is cached by this object.
     */
    public void flushMetrics() {
        metrics.clear();
    }

    /**
     * Retrieves the list of all metrics that have been evaluated on this
     * project from the SQO-OSS framework, unless the cache contains some
     * data already.
     * 
     * @return the list of metrics evaluated on this project, or empty list
     *   when none are found or this project is not yet initialized.
     */
    public List<Metric> retrieveMetrics() {
        if (terrier == null)
            return metrics;
        if (isValid()) {
            if (metrics.isEmpty())
                metrics = terrier.getMetricsForProject(id);
        }
        else
            terrier.addError("Invalid project!");
        return metrics;
    }

    /**
     * Sets the first version of this project.
     * 
     * @param version the version object
     */
    public void setFirstVersion(Version version) {
        if (version != null)
            firstVersion = version;
    }

    /**
     * Gets the first known version of this project.
     *
     * @return the first version's object, or <code>null<code> if the project
     *   has no versions at all.
     */
    public Version getFirstVersion() {
        if (isValid()) {
            if ((firstVersion == null) && (terrier != null))
                firstVersion = terrier.getFirstProjectVersion(getId());
        }
        else
            terrier.addError("Invalid project!");
        return firstVersion;
    }

    /**
     * Sets the last version of this project.
     * 
     * @param version the version object
     */
    public void setLastVersion(Version version) {
        if (version != null)
            lastVersion = version;
    }

    /**
     * Gets the last known version of this project.
     *
     * @return the last version's object, or <code>null</code> if the project
     *   has no versions at all.
     */
    public Version getLastVersion() {
        if (isValid()) {
            if ((lastVersion == null) && (terrier != null))
                lastVersion = terrier.getLastProjectVersion(getId());
        }
        else
            terrier.addError("Invalid project!");
        return lastVersion;
    }

    /**
     * Sets the current version.
     *
     * @param version the version object
     */
    public void setCurrentVersion(Version version) {
        if ((version != null) && (version.isValid())){
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
            if (currentVersionId == null) {
                return getLastVersion().getId();
            }
            return currentVersionId;
        } catch (NullPointerException e) {
            terrier.addError("Could not retrieve current version.");
            return null;
        }
    }

    // TODO: Document and integrate in the other memeber methods.
    public Version getVersionById (Long versionId) {
        Version result = null;
        // Check in the versions cache first
        if (versions.containsKey(versionId))
            result = versions.get(versionId);
        // Otherwise retrieve it from the SQO-OSS framework
        else {
            result = terrier.getVersion(this.getId(), versionId);
            if (result != null)
                versions.put(result.getId(), result);
        }
        return result;
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

    //========================================================================
    // METRIC SELECTION METHODS
    //========================================================================

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
     * Retrieve the list of Ids of all metrics that were selected for this
     * project.
     * 
     * @return the list of selected metric Ids
     */
    public List<Long> getSelectedMetrics () {
        return selectedMetrics;
    }

    /**
     * Gets the mnemonic names of the currently selected metrics, indexed by
     * metric Id.
     * 
     * @return The list of mnemonic names, or an empty list when none are
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

    //========================================================================
    // DEVELOPER SELECTION METHODS
    //========================================================================

    /**
     * Adds the developer with the given Id to the list of selected
     * developers.
     * 
     * @param id the developer Id
     */
    public void selectDeveloper (Long id) {
        if (id != null)
            selectedDevelopers.add(id);
    }

    /**
     * Removes the developer with the given Id from the list of selected
     * developers
     * 
     * @param id the developer Id
     */
    public void deselectDeveloper (Long id) {
        if (id != null)
            selectedDevelopers.remove(id);
    }

    /**
     * Retrieve the list of Ids of all develoepers that were selected for this
     * project.
     * 
     * @return the list of selected developers Ids
     */
    public List<Long> getSelectedDevelopersIds() {
        return selectedDevelopers;
    }

    /**
     * Gets the list of the currently selected developers, indexed by
     * developer Id.
     * 
     * @return The list of selected developers, or an empty list when none
     *   are selected.
     */
    public Map<Long, Developer> getSelectedDevelopers() {
        Map<Long, Developer> result = new HashMap<Long, Developer>();
        for (Long nextId : selectedDevelopers) {
            for (Developer nextDeveloper : developers.values())
                if (nextDeveloper.getId().longValue() == nextId)
                    result.put(nextId, nextDeveloper);
        }
        return result;
    }

    //========================================================================
    // RESULTS RENDERING METHODS
    //========================================================================

    /* (non-Javadoc)
     * @see eu.sqooss.webui.WebuiItem#getHtml(long)
     */
    public String getHtml(long in) {
        if (isValid() == false)
            return(sp(in) + Functions.error("Invalid project!"));
        StringBuilder html = new StringBuilder("");
        html.append(sp(in) + "<h2>"
                + getName() + " (" + getId()+ ")"
                + "</h2>");
        //html.append(getInfo(in++));
        return html.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
