/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Georgios Gousios <gousiosg@gmail.com>
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
import java.util.Set;
import java.util.Map;

import eu.sqooss.core.AlitheiaCore;

/**
 * This class represents the data relating to a directory within an
 * inserted project's SVN tree, stored inthe database
 */
public class Directory extends DAObject {
    /**
     * Semi-fake representation of a SVN root
     */
    public static String SCM_ROOT = "/";

    /**
     * The path within the SVN repo
     */
    private String path;
    
    /**
     * A set representing the files within this path
     */
    private Set<ProjectFile> files;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }    
    
    public Set<ProjectFile> getFiles() {
        return files;
    }

    public void setFiles(Set<ProjectFile> files) {
        this.files = files;
    }
    
    public boolean isSubDirOf(Directory d) {
        //
        return false;
    }

    /**
     * Return the entry in the Directory table that corresponds to the
     * passed argument. If the entry does not exist, it will optionally be 
     * created and saved, depending on the second parameter
     *  
     * @param path The path of the Directory to search for
     * @param create Whether or not the directory entry will be created if
     * not found. If true, it will be created.
     * @return A Directory record for the specified path or null on failure
     */
    public static synchronized Directory getDirectory(String path, boolean create) {
        
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("path", path);
        
        List<Directory> dirs = dbs.findObjectsByProperties(Directory.class,
                parameterMap);
        
        /* Dir path in table, return it */
        if ( !dirs.isEmpty() ) {
            return dirs.get(0);
        }
        
        if (create) {
            /* Dir path not in table, create it */ 
            Directory d = new Directory();
            d.setPath(path);
            if (!dbs.addRecord(d)) {
                return null;
            }
        
            return d;
        }
        //Dir not found and not created
        return null;
    }
    
    public String toString() {
        return this.path;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

