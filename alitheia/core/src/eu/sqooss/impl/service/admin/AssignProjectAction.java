/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,
 *                Athens, Greece.
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

import java.util.Map;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.admin.ActionParam;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;

/**
 * Administrative action that assigns a project to a node in a 
 * cluster installation. Takes the following parameters:
 * 
 * <ul>
 *      <li></li>
 *      <li></li>
 * </ul>
 *
 */
public class AssignProjectAction extends ActionBase {
    
    public AssignProjectAction() {
        registerParam(ActionParam.PROJECT_NAME, true);
        registerParam(ActionParam.PROJECT_ID, false);
        registerParam(ActionParam.CLUSTERNODE_NAME, true);
    }
    
    public String getActionName() {
        return "assignpr";
    }
    
    public String getActionDescr() {
        return "Assign a project to a cluster node";
    }
    
    @Override
    public boolean execute(Map<ActionParam, Object> params) {
        
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        StoredProject sp = null;
        if (!validateParams(params))
            return false;
        return false;
     /**   if (validateParam(ActionParam.PROJECT_ID))
            sp = StoredProject.loadDAObyId(, StoredProject.class);
            if(validateParam(ActionParam.PROJECT_NAME))
                sp = StoredProject.getProjectByName(projectname); */

    }
}
