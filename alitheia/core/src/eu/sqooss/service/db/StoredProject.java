/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Adriaan de Groot <groot@kde.org>
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

import eu.sqooss.impl.service.CoreActivator;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;


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

    public StoredProject() {
    }

    public StoredProject(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return websiteUrl;
    }

    public void setWebsite(String url) {
        this.websiteUrl = url;
    }

    public String getContact() {
        return contactUrl;
    }

    public void setContact(String url) {
        this.contactUrl = url;
    }

    public String getBugs() {
        return btsUrl;
    }

    public void setBugs(String url) {
        this.btsUrl = url;
    }

    public String getRepository() {
        return scmUrl;
    }

    public void setRepository(String url) {
        this.scmUrl = url;
    }

    public String getMail() {
        return mailUrl;
    }

    public void setMail(String url) {
        this.mailUrl = url;
    }

    public static StoredProject getProjectByName(String name, Logger logger) {
        StoredProject project = null;
        DBService dbs = CoreActivator.getDBService();

        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("name",name);
        List prList = dbs.doHQL("from StoredProject where PROJECT_NAME=:name",parameterMap);
        if ((prList == null) || (prList.size() != 1)) {
            logger.error("The requested project was not found");
            return null;
        }

        project = (StoredProject) prList.get(0);
        return project;
    }

    public static ProjectVersion getLastProjectVersion(StoredProject project, Logger logger) {
        ProjectVersion lastVersion = null;
        DBService dbs = CoreActivator.getDBService();

        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("sp", project);
        List pvList = dbs.doHQL("from ProjectVersion pv where pv.project=:sp"
                + " and pv.version = (select max(pv2.version) from "
                + " ProjectVersion pv2 where pv2.project=:sp)",
                parameterMap);

        if ((pvList == null) || (pvList.size()==0)) {
            logger.warn("No last stored version of project " + project.getName());
            lastVersion = new ProjectVersion();
            lastVersion.setProject(project);
            lastVersion.setVersion(0);
            return lastVersion;
        }
        
        for (Object o : pvList) {
            if (o instanceof ProjectVersion) {
                ProjectVersion op = (ProjectVersion)o;
                logger.info("Found last project version " + op.getVersion());
            }
        }
        if (pvList.size() > 1) {
            logger.warn("Found " + pvList.size() + " 'last' versions. Using the first one.");
        }
        
        lastVersion = (ProjectVersion) pvList.get(0);
        return lastVersion;
    }

    public List<ProjectVersion> getProjectVersions() {
        //TODO: implement
        return null;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

