/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.impl.service.webadmin;

import java.util.HashMap;
import java.util.List;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.InvocationRule;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectConfig;
import eu.sqooss.service.scheduler.Job;

public class ProjectDeleteJob extends Job {

	private StoredProject sp;
    private AlitheiaCore core;

    ProjectDeleteJob(AlitheiaCore core, StoredProject sp) {
        this.sp = sp;
        this.core = core;
    }

    @Override
    public long priority() {
        return 0xff;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void run() throws Exception {
        DBService dbs = core.getDBService();

        if (!dbs.isDBSessionActive()) {
            dbs.startDBSession();
        }

        sp = dbs.attachObjectToDBSession(sp);
        // Delete any associated invocation rules first
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("project", sp);
        List<?> assosRules = dbs.findObjectsByProperties(InvocationRule.class,
                properties);
        if ((assosRules != null) && (assosRules.size() > 0)) {
            for (Object nextDAO : assosRules) {
                InvocationRule.deleteInvocationRule(dbs, 
                        core.getMetricActivator(), (InvocationRule) nextDAO);
            }
        }
        
        //Cleanup plugin results
        List<Plugin> ps = (List<Plugin>) dbs.doHQL("from Plugin");        
        
        for (Plugin p : ps ) {
            AlitheiaPlugin ap = core.getPluginAdmin().getPlugin(core.getPluginAdmin().getPluginInfo(p.getHashcode()));
            ap.cleanup(sp);
        }
        
        boolean success = true;
        
        // Delete project version's parents.
        List<ProjectVersion> versions = sp.getProjectVersions();
        
        for (ProjectVersion pv : versions) {
           /* Set<ProjectVersionParent> parents = pv.getParents();
            for (ProjectVersionParent pvp : parents) {
                
            }*/
            pv.getParents().clear();
        }
               
        //Delete the project's config options
        List<StoredProjectConfig> confParams = StoredProjectConfig.fromProject(sp);
        if (!confParams.isEmpty()) {
        	success &= dbs.deleteRecords(confParams);
        }
        
        // Delete the selected project
        success &= dbs.deleteRecord(sp);

        if (success) {
            dbs.commitDBSession();
        } else {
            dbs.rollbackDBSession();
        }

    }
    
    @Override
    public String toString() {
        return "ProjectDeleteJob - Project:{" + sp +"}";
    }
}
