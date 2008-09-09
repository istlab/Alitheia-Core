/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Paul J. Adams <paul.adams@siriusit.co.uk>
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.service.CoreActivator;

public class FileForVersion implements Serializable {
    private static final long serialVersionUID = 1;
    
    private ProjectFile file;
    private ProjectVersion version;
    
    public FileForVersion() {
        
    }
    
    /* Needed by Hibernate to handle the composite key
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof FileForVersion) {
            FileForVersion ffv = (FileForVersion) obj;
            return ffv.file.getId() == this.file.getId()
                && ffv.version.getId() == this.version.getId();
        }
        return false;
    }

    /* Needed by Hibernate to handle the composite key
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        
        return (int)(file.getId() * version.getId());
    }

    public FileForVersion (ProjectFile from, ProjectVersion pv) {
        file = from;
        version = pv;
    }
    
    public ProjectFile getFile() {
        return file;
    }
    
    public void setFile(ProjectFile file) {
        this.file = file;
    }
    
    public ProjectVersion getVersion() {
        return version;
    }
    
    public void setVersion(ProjectVersion version) {
        this.version = version;
    }
        
    public static List<ProjectFile> getFilesForVersion(ProjectVersion pv) {
        
        DBService dbs = CoreActivator.getDBService();
        String paramVersion = "paramVersion";
        
        String query = "select pf " +
            "from ProjectFile pf, ProjectVersion pv, FileForVersion ffv" +
            " where pf = ffv.file " +
            " and pv = ffv.version " +
            " and ffv.version = :" + paramVersion; 

        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramVersion, pv);
        
        return (List<ProjectFile>) dbs.doHQL(query, parameters);

    }
    
    public String toString() {
        return file.toString();
    }
}
