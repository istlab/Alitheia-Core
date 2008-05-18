/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

public class ProjectFile extends DAObject{
    private String name;
    private ProjectVersion projectVersion;
    private String status;
    private Boolean isDirectory;
    private Directory dir;


    public ProjectFile() {
        // Nothing to see here
        isDirectory = false; //By default, all entries are files
    }

    public ProjectFile(ProjectVersion pv) {
        this.projectVersion = pv;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setProjectVersion(ProjectVersion projectVersion ) {
        this.projectVersion = projectVersion;
    }

    public ProjectVersion getProjectVersion() {
        return projectVersion;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getIsDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(Boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public Directory getDir() {
        return dir;
    }

    public void setDir(Directory dir) {
        this.dir = dir;
    }
    
    /**
     * Returns the full path to the file, relative to the repository root
     * @return 
     */
    public String getFileName() {
        return dir.getPath() + "/" + name;
    }
    
    public static ProjectFile getPreviousFileVersion(ProjectFile pf) {
        DBService dbs = CoreActivator.getDBService();
        
        String paramFile = "paramFile"; 
        String paramVersion = "paramVersion"; 
        
        String query = "select pf from ProjectVersion pv, ProjectFile pf where pv.timestamp in (" +
        		"select max(pv2.timestamp) " +
        		"from ProjectVersion pv2, ProjectFile pf2 " +
        		"where pv2.timestamp < :" + paramVersion +
        		" and pf2.projectVersion = pv2.id" +
        		" and pf2.name = :" + paramFile +
        		" and pv2.project = pv.project )" +
        		"and pf.projectVersion = pv.id and pf.name = :" + paramFile;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramFile, pf.name);
        parameters.put(paramVersion, pf.getProjectVersion().getTimestamp());

        List<?> projectFiles = dbs.doHQL(query, parameters);
        
        if(projectFiles == null || projectFiles.size() == 0) {
            return null;
        }else {
            return (ProjectFile) projectFiles.get(0);
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab

