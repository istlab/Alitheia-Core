/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Christoph Schleifenbaum <christoph@kdab.net>
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

package eu.sqooss.service.fds;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.fds.InMemoryCheckout;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

import eu.sqooss.impl.service.CoreActivator;

public class InMemoryDirectory {
    
    private InMemoryCheckout checkout;
    private InMemoryDirectory parentDirectory;
   
    private String name;
    
    private List<String> files;
    private List<InMemoryDirectory> directories;
   
    public InMemoryDirectory() {
        name = new String();
        files = new ArrayList<String>();
        directories = new ArrayList<InMemoryDirectory>();
    }
    
    public InMemoryDirectory(String name) {
        this();
        this.name = name;
    }
    
    public InMemoryDirectory(InMemoryCheckout checkout) {
        this("");
        this.checkout = checkout;
    }
    
    public InMemoryDirectory(InMemoryDirectory parent, String name) {
        this(name);
        this.parentDirectory = parent;
    }
    
    /**
     * Returns the name of the directory.
     * Returns an empty string for the project's root directory.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the complete path of this directory.
     */
    public String getPath() {
        return parentDirectory == null ? getName() : parentDirectory.getPath() + "/" + getName();
    }
   
    /**
     * Returns this directory's parent directory.
     * Might be null, if this is the root directory.
     */
    public InMemoryDirectory getParentDirectory() {
        return parentDirectory;
    }
    
    /**
     * Returns the checkout this directory belongs to.
     */
    public InMemoryCheckout getCheckout() {
        return checkout == null ? parentDirectory.getCheckout() : checkout;
    }

    /**
     * Returns the list of subdirectories this directory has.
     */
    public List<InMemoryDirectory> getSubDirectories() {
        return directories;
    }

    /**
     * Returns the list of files this directory contains.
     */
    public List<ProjectFile> getFiles() {
        ArrayList<ProjectFile> result = new ArrayList<ProjectFile>();

        DBService dbs = CoreActivator.getDBService();
      
        StoredProject project = getCheckout().getProject();
        ProjectVersion version = ProjectVersion.getVersionByRevision(project, getCheckout().getRevision() );
        
        String paramRevision = "project_revision";
        String paramProjectId = "project_id";
        String paramName = "file_name";

        String query = "select pf " +
                       "from ProjectFile pf " +
                       "where pf.projectVersion.version<=:" + paramRevision + "and " +
                       "pf.name=:" + paramName + "and " +
                       "pf.projectVersion.id=:" + paramProjectId + " " +
                       "order by pf.projectVersion.version desc";

        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramRevision, version.getVersion() );
        parameters.put(paramProjectId, project.getId() );

        for (String file: files) {
            parameters.put(paramName, file);
        
            List<?> projectFiles = dbs.doHQL(query, parameters);
            if (projectFiles != null && projectFiles.size() != 0) {
                result.add((ProjectFile)projectFiles.get(0));
            }
        }
        
        return result;
    }

    public void addFile(String path) {
        if (path.indexOf('/') == -1 ) {
            files.add(path);
        } else {
            String pathName = path.substring(0, path.indexOf('/'));
            String fileName = path.substring(path.indexOf('/') + 1);
            InMemoryDirectory dir = getSubdirectoryByName(pathName);
            dir.addFile(fileName);
        }
    }

    protected InMemoryDirectory getSubdirectoryByName(String name) {
        for (InMemoryDirectory dir : directories) {
            if (dir.getName().equals(name) ) {
                return dir;
            }
        }

        // not found? create it
        InMemoryDirectory dir = new InMemoryDirectory(this, name);
        directories.add(dir);
        return dir;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
