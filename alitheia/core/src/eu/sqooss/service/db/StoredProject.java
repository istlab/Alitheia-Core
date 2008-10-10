/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007,2008 Athens University of Economics and Business
 *     Author Adriaan de Groot <groot@kde.org>
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.sqooss.core.AlitheiaCore;

/**
 * This class represents a project that Alitheia "knows about".
 * These projects are the ones that are examined by the cruncher.
 * Basically, if the cruncher is operating on a project, there
 * has to be a record of this type in the system.
 */
public class StoredProject extends DAObject {
    /**
     * Public, human-readable name of the project (e.g. Evolution,
     * GNOME, Catalina, Sciplot). Used for display purposes.
     */
    private String name;
    /**
     * URL of the public, human-readable project website.
     */
    private String websiteUrl;
    /**
     * URL (generally mailto:) of the project contact person.
     */
    private String contactUrl;
    /**
     * This is information for accessing the BTS system
     * via the TDS. Consider it write-once when the project
     * is added to the system by the administrator.
     */
    private String btsUrl;
    /**
     * Access to the SCM via the TDS. @see btsUrl.
     */
    private String scmUrl;
    /**
     * Access to the mail store via the TDS. @see btsUrl.
     */
    private String mailUrl;
    /**
     * The versions that this project contains
     */
    private List<ProjectVersion> projectVersions;
    
    private Set<Developer> developers;
    private Set<MailingList> mailingLists;
    private Set<StoredProjectMeasurement> measurements;
    private Set<EvaluationMark> evaluationMarks;
    private Set<Bug> bugs;

    public StoredProject() {
        super();
    }

    public StoredProject(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String url) {
        this.websiteUrl = url;
    }

    public String getContactUrl() {
        return contactUrl;
    }

    public void setContactUrl(String url) {
        this.contactUrl = url;
    }

    public String getBtsUrl() {
        return btsUrl;
    }

    public void setBtsUrl(String url) {
        this.btsUrl = url;
    }

    public String getScmUrl() {
        return scmUrl;
    }

    public void setScmUrl(String url) {
        this.scmUrl = url;
    }

    public String getMailUrl() {
        return mailUrl;
    }

    public void setMailUrl(String url) {
        this.mailUrl = url;
    }
    
    public List<ProjectVersion> getProjectVersions() {
        return projectVersions;
    }

    // TODO: this seems kind of inefficient
    public List<ProjectVersion> getTaggedVersions() {
        List<ProjectVersion> versions = getProjectVersions();
        for (ProjectVersion version : versions)
            if (version.getTags() == null)
                versions.remove(version);
        return versions;
    }
    
    public void setProjectVersions(List<ProjectVersion> projectVersions) {
        this.projectVersions = projectVersions;
    }

    public Set<Developer> getDevelopers() {
        return developers;
    }

    public void setDevelopers(Set<Developer> developers) {
        this.developers = developers;
    }

    public Set<MailingList> getMailingLists() {
        return mailingLists;
    }

    public void setMailingLists(Set<MailingList> mailingLists) {
        this.mailingLists = mailingLists;
    }

    public Set<StoredProjectMeasurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Set<StoredProjectMeasurement> measurements) {
        this.measurements = measurements;
    }

    /**
     * Get the evaluation marks for this project; this includes
     * all of the plugins that have ectually stored any results
     * related to the project.
     * 
     * @return set of evaluation marks
     */
    public Set<EvaluationMark> getEvaluationMarks() {
        return evaluationMarks;
    }

    public void setEvaluationMarks(Set<EvaluationMark> evaluationMarks) {
        this.evaluationMarks = evaluationMarks;
    }

    /**
     * Is the project evaluated? A project is considered evaluated if
     * at least one metric has stored at least one measurement; metric
     * plug-ins are expected to explicitly flag that they have stored a 
     * result by storing an evaluation mark as well.
     * 
     * @return is the project evaluated?
     */
    public boolean isEvaluated() {
        Set<EvaluationMark> marks = getEvaluationMarks();
        if ((marks != null) && (marks.size() > 0))
            for (EvaluationMark nextMark : marks)
                if (nextMark.getWhenRun() != null)
                    return true;
        return false;
    }

    //================================================================
    // Static table information accessors
    //================================================================
    

    /**
     * Convenience method to retrieve a stored project from the
     * database by name; this is different from the constructor
     * that takes a name parameter. This method actually searches
     * the database, whereas the constructor makes a new project
     * with the given name.
     * 
     * @param name Name of the project to search for
     * @return StoredProject object or null if not found
     */
    public static StoredProject getProjectByName(String name) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("name",name);
        List<StoredProject> prList = dbs.findObjectsByProperties(StoredProject.class, parameterMap);
        return (prList == null || prList.isEmpty()) ? null : prList.get(0);
    }

    /**
     * Count the total number of projects in the database.
     * 
     * @return number of stored projects in the database
     */
    public static int getProjectCount() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        List<?> l = dbs.doHQL("SELECT COUNT(*) FROM StoredProject");
        if ((l == null) || (l.size() < 1)) {
            return 0;
        }
        Long i = (Long) l.get(0);
        return i.intValue();
    }

    /**
     * Returns the total number of versions for the project with the given Id.
     *
     * @param projectId - the project's identifier
     *
     * @return The total number of version for that project.
     */
    public static long getVersionsCount(Long projectId) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("pid", projectId);
        List<?> pvList = dbs.doHQL("select count(*)"
                + " from ProjectVersion pv"
                + " where pv.project.id=:pid",
                parameterMap);

        return (pvList == null || pvList.isEmpty()) ? 0 : (Long) pvList.get(0);
    }

    public Set<Bug> getBugs() {
        return bugs;
    }

    public void setBugs(Set<Bug> bugs) {
        this.bugs = bugs;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

