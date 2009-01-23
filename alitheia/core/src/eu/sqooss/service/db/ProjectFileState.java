/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2009 - Organization for Free and Open Source Software,  
 *                 Athens, Greece.
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
 * A representation of the status of the file in this revision:
 * <ul>
 * <li>ADDED</li>
 * <li>MODIFIED</li>
 * <li>DELETED</li>
 * <li>REPLACED</li>
 * </ul>
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public class ProjectFileState extends DAObject {
    
    // File status constants
    public static final int STATE_ADDED = 0x1;
    public static final int STATE_MODIFIED = 0x2;
    public static final int STATE_DELETED  = 0x4;
    public static final int STATE_REPLACED  = 0x8;
    
    private int status;
    
    private Set<ProjectFile> files;
    
    public String toString() {
        switch (status) {
        case STATE_ADDED:
            return "ADDED";
        case STATE_MODIFIED:
            return "MODIFIED";
        case STATE_DELETED:
            return "DELETED";
        case STATE_REPLACED:
            return "REPLACED";
        }
        return null;
    }
    
    public static ProjectFileState added() {
        return fromStatus(STATE_ADDED);
    }
    
    public static ProjectFileState modified() {
        return fromStatus(STATE_MODIFIED);
    }
    
    public static ProjectFileState deleted() {
        return fromStatus(STATE_DELETED);
    }
    
    public static ProjectFileState replaced() {
        return fromStatus(STATE_REPLACED);
    }
    
    public static ProjectFileState fromStatus(int status) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
	if (!dbs.isDBSessionActive())
            return null;

	Map<String, Object> params = new HashMap<String, Object>();
        params.put("status", status);
        List<ProjectFileState> pfs = dbs.findObjectsByProperties(ProjectFileState.class, params);
        
        if (!pfs.isEmpty()) {            
            return pfs.get(0);
        }
       	
        ProjectFileState state = new ProjectFileState();
        state.setStatus(status);
        
        dbs.addRecord(state);
        
        return fromStatus(status);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Set<ProjectFile> getFiles() {
        return files;
    }

    public void setFiles(Set<ProjectFile> files) {
        this.files = files;
    }
}
