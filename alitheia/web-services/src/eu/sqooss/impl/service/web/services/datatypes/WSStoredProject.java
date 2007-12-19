/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.impl.service.web.services.datatypes;

import java.util.List;

import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

/**
 * This class wraps the <code>eu.sqooss.service.db.StoredProject</code>
 * with the <code>eu.sqooss.service.db.ProjectVersion</code>s.
 * It is useful because we can have a project with many versions.  
 */
public class WSStoredProject {
    
    private StoredProject storedProject;
    private WSProjectVersion[] projectVersions;

    public WSStoredProject(StoredProject storedProject, ProjectVersion projectVersion) {
        this.storedProject = storedProject;
        this.projectVersions = wrapProjectVerions(new ProjectVersion[] {projectVersion});
    }
    
    public WSStoredProject(StoredProject storedProject, List<ProjectVersion> projectVersions) {
        this.storedProject = storedProject;
        this.projectVersions = wrapProjectVerions(projectVersions);
    }
    
    public WSStoredProject(StoredProject storedProject, ProjectVersion[] projectVersions) {
        this.storedProject = storedProject;
        this.projectVersions = wrapProjectVerions(projectVersions);
    }
    
    public String getName() {
        return storedProject.getName();
    }

    public String getWebsite() {
        return storedProject.getWebsite();
    }

    public String getContact() {
        return storedProject.getContact();
    }

    public String getBugs() {
        return storedProject.getBugs();
    }

    public String getRepository() {
        return storedProject.getRepository();
    }

    public String getMail() {
        return storedProject.getMail();
    }

    public long getId() {
        return storedProject.getId();
    }

    /**
     * @return the projectVersions
     */
    public WSProjectVersion[] getProjectVersions() {
        return projectVersions;
    }
    
    private WSProjectVersion[] wrapProjectVerions(ProjectVersion[] projectVersions) {
        int projectVersionsLength = projectVersions.length;
        WSProjectVersion[] wrappedProjectVersions = new WSProjectVersion[projectVersionsLength];
        for (int i = 0; i < projectVersionsLength; i++) {
            wrappedProjectVersions[i] = new WSProjectVersion(projectVersions[i]);
        }
        return wrappedProjectVersions;
    }
    
    private WSProjectVersion[] wrapProjectVerions(List<ProjectVersion> projectVersions) {
        int projectVersionsLength = projectVersions.size();
        WSProjectVersion[] wrappedProjectVersions = new WSProjectVersion[projectVersionsLength];
        for (int i = 0; i < projectVersionsLength; i++) {
            wrappedProjectVersions[i] = new WSProjectVersion(projectVersions.get(i));
        }
        return wrappedProjectVersions;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
