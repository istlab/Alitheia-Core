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

import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Iterator;
import java.util.Vector;

import eu.sqooss.ws.client.datatypes.WSStoredProject;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;


public class Project {

    private Long    id;
    private String  name;
    private String  bts;
    private String  repository;
    private String  mail;
    private String  contact;
    private String  website;
    private Integer fileCount;

    // Contains the version number of the last selected version
    private Long currentVersionId;

    // Contains a sorted list of all project versions mapped to their ID.
    private SortedMap<Long, Version> versions;
    private Terrier terrier;

    public Project () {

    }

    public Project (WSStoredProject p, Terrier t) {
        id = p.getId();
        terrier = t;
        name = p.getName();
        bts = p.getBugs();
        repository = p.getRepository();
        mail = p.getMail();
        contact = p.getContact();
        website = p.getWebsite();
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

    /** Returns an HTML table with files in the current project version.
     *
     * @param versionId The ID field of the version
     */
    public String listFiles() {
        FileListView f = terrier.getFiles4ProjectVersion(currentVersionId);
        return f.getHtml();
    }

    public void setFileCount(Integer n) {
        fileCount = n;
    }

    public String showVersions() {
        StringBuilder html = new StringBuilder();
        html.append("\n<table class=\"projectversions\">");
        html.append("\n\t<tr>\n\t\t<td><strong>version</strong></td>\n\t</tr>" );
        for (Version v: versions.values()) {
            html.append("\n\t<tr>\n\t\t<td><strong>No: " + v.getNumber() + "</strong></td>\n\t</tr>" );
        }
        html.append("\n</table");
        return html.toString();
    }

    public String getInfo() {
        StringBuilder html = new StringBuilder();
        html.append("\n<table class=\"projectinfo\">\n\t<tr>\n\t\t<td>");
        html.append("Website: \n\t\t</td><td>\n"
                + (getWebsite() != null 
                        ? "<a href=\"" + getWebsite() + "\">" + getWebsite() + "</a>"
                        : "<i>undefined</i>"));
        html.append("\n\t\t</td>\n\t</tr>\n\t<tr>\n\t\t<td>");
        html.append("Contact: \n\t\t</td><td>\n"
                + (getContact() != null 
                        ? "<a href=\"" + getContact() + "\">" + getContact() + "</a>"
                        : "<i>undefined</i>"));
        html.append("\n\t\t</td>\n\t</tr>\n\t<tr>\n\t\t<td>");
        html.append("SVN Mirror: \n\t\t</td><td>\n"
                + (getRepository() != null 
                        ? "<a href=\"files.jsp" + getId() + "\">" + getRepository() + "</a>"
                        : "<i>undefined</i>"));
        html.append("\n\t\t</td>\n\t</tr>\n\t<tr>\n\t\t<td>");
        html.append("Bug Data: \n\t\t</td><td>\n"
                + (getBts() != null 
                        ? "<a href=\"" + getBts() + "\">" + getBts() + "</a>"
                        : "<i>undefined</i>"));
        html.append("\n\t\t</td>\n\t</tr>\n</table>");
        return html.toString();
    }

    public String getHtml() {
        StringBuilder html = new StringBuilder("<!-- Project -->\n");
        html.append("<h2>" + getName() + " (" + getId() + ")</h2>");
        html.append(getInfo());
        return html.toString();
    }

    /**
     * Gets the first known version of this project.
     * 
     * @return the version number, or null if the project has no version.
     */
    public Version getFirstVersion() {
        if ((versions != null) && (versions.size() > 0)) {
            return versions.get(versions.firstKey());
        }
        return null;
    }

    /**
     * Gets the last known version of this project.
     * 
     * @return the version number, or null if the project has no version.
     */
    public Version getLastVersion() {
        if ((versions != null) && (versions.size() > 0)) {
            return versions.get(versions.lastKey());
        }
        return null;
    }

    /**
     * Gets a list of all known project version numbers.
     *
     * @return the list of version numbers, or null if the project has no
     * version.
     */
    public Set<Long> getVersions() {
        if (versions != null) {
            return versions.keySet();
        }
        return null;
    }

    /**
     * Gets a version by its ID.
     *
     * @return The Version under that id.
     */
    public Version getVersion(Long versionId) {
        return terrier.getVersionById(id, versionId);
    }

    /**
     * Gets the current version.
     *
     * @return The Version under that id.
     */
    public Version getCurrentVersion() {
        return terrier.getVersionById(id, getCurrentVersionId());
    }

    /**
     * Sets the list of all known project versions. The first field in each
     * version token must be the version number. The second field must be the
     * corresponding version ID.
     *
     * @param versions the list of project versions
     */
    public void setVersions(SortedMap<Long, Long> vs) {
        for (Long nextVersion: vs.values()) {
            try {
            Version v = terrier.getVersionById(id, nextVersion); // This is horribly inefficient
            versions.put(nextVersion, v);
            } catch (NullPointerException e) {
                terrier.addError("Couldn't add Version" + nextVersion);
            }
        }
        //setCurrentVersion(getLastVersion()); //FIXME
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
    /**
     * Sets the specified version as selected version for this project
     * @param versionNumber the version number
     */
    public void setCurrentVersionId(Long versionNumber) {
        this.currentVersionId = versionNumber;
    }
}
