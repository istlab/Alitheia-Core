/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,  
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

package eu.sqooss.impl.service.admin;

import java.util.HashMap;
import java.util.Map;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.admin.ActionParam;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminActionError;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;

/**
 * Base class for administrative action classes, to be inherited
 * by all implementations. Most actions process more or less the same 
 * parameters; this class encapsulates all parameters that can be processed
 * by an action along with validation and error reporting services for 
 * sub-classes.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 *
 */
public abstract class ActionBase implements AdminAction {
    
    private Map<ActionParam, Boolean> supportedParams = new HashMap<ActionParam, Boolean>();
    private AdminActionError e;
    private String result;
    
    /**{@inheritDoc}*/
    public abstract String getActionName();
    
    /**{@inheritDoc}*/
    public abstract String getActionDescr();
    
    /**{@inheritDoc}*/
    public abstract boolean execute(Map<ActionParam, Object> opts);
    
    /**{@inheritDoc}*/
    public String getResult() {
        return result;
    }
    
    /**{@inheritDoc}*/
    public Map<String, String> getHelp() {
        Map<String, String> help = new HashMap<String, String>();
        
        for (ActionParam p : supportedParams.keySet()) {
            help.put(p.getName(), p.getHelp());
        }
        
        help.put(getActionName(), getActionDescr());
        
        return help;
    }
 
    /**{@inheritDoc}*/
    public AdminActionError getError() {
        return e;
    }
    
    /** Register an action parameter. */
    protected void registerParam(ActionParam ap, Boolean required) {
        if (!supportedParams.containsKey(ap))
            supportedParams.put(ap, required);
    }
    
    protected boolean validateParam(ActionParam ap, String value) {
        return validateParam(ap.getName(), value);
    }
    
    /**
     * Validate all parameters in the provided map. If a parameter is marked as
     * optional, the result of validation is not affected, however validation is
     * still performed.
     */
    protected boolean validateParams(Map<ActionParam, Object> params) {
        for (ActionParam param : params.keySet()) {
            boolean result = validateParam(param, (String)params.get(param));
            if (!result && supportedParams.get(param)) 
                return false;
        }   
        return true;
    }
    
    private boolean validateParam(String param, String value) {

        switch (ActionParam.fromString(param)) {
        case PROJECT_NAME:
            return validateProjectName(value);
        case PROJECT_ID:
            return validateProjectId(value);
        case CLUSTERNODE_NAME:
            return validateClusterNodeName(value);
        default:
            return false;
        }
    }

    private final boolean validateClusterNodeName(String nodeName) {
        DBService db = AlitheiaCore.getInstance().getDBService();
        ClusterNode cn = ClusterNode.getClusteNodeByName(nodeName);
        if (cn == null) {
            e = AdminActionError.ENOCLUSTERNODE;
            return false;
        }
        return true;
    }

    private final boolean validateProjectId(String id) {
        DBService db = AlitheiaCore.getInstance().getDBService();
        
        try {
            Integer i = Integer.parseInt(id);
        } catch (NumberFormatException nfe) {
            e = AdminActionError.ENOINT;
            return false;
        }
        
        StoredProject sp = StoredProject.loadDAObyId(Integer.parseInt(id), 
                StoredProject.class);
        if (sp == null) {
            e = AdminActionError.EPROJID;
            return false;
        }
        return true;
    }

    private final boolean validateProjectName(String param) {
        DBService db = AlitheiaCore.getInstance().getDBService();
        StoredProject sp = StoredProject.getProjectByName(param);
        if (sp == null) {
            e = AdminActionError.EPROJECT;
            return false;
        }
        return true;
    }   
}
