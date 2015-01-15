/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
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

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.VelocityContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.service.admin.actions.UpdateProject;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;

public class ProjectsController extends ActionController {
   
    /**
     * Instantiates a new projects view.
     *
     * @param bundlecontext the <code>BundleContext</code> object
     * @param vc the <code>VelocityContext</code> object
     */
    public ProjectsController() {
        super("projects.html");
    }
    
    private StoredProject selProject = null;
    
    protected void beforeAction(Map<String, String> requestParameters, VelocityContext velocityContext) {
        selProject = null;
        
        DBService databaseService = AlitheiaCore.getInstance().getDBService();
        
        // Retrieve the selected project's DAO (if any)
        String projectIdString = requestParameters.get("projectId");

        Long projectId = null;
        try {
            projectId = new Long(projectIdString);
        } catch (NumberFormatException ex) {
            projectId = null;
        }

        if (projectId != null) {
            selProject = databaseService.findObjectById(StoredProject.class, projectId);
            velocityContext.put("project", selProject);
        }
        
        Set<StoredProject> projects = ClusterNode.thisNode().getProjects();
        velocityContext.put("projects", projects);      
    }
    
    @Action
    public void home(Map<String, String> requestParameters, VelocityContext velocityContext) {
    }
    
    @Action("showProject")
    public String showProject(Map<String, String> requestParameters, VelocityContext velocityContext) {
        if(null == selProject) {
            return null;
        }
        
        // Fill projects with only the 1 selected project
        ArrayList<StoredProject> projects= new ArrayList<>();
        projects.add(selProject);
        velocityContext.put("projects", projects);
        
        return "project.html";
    }
    
    @Action("showAddProject")
    public String showAddProject(Map<String, String> requestParameters, VelocityContext velocityContext) {
        velocityContext.put("project", selProject);
        return "add_project.html";
    }
    
    @Action("addProject")
    public void addProject(Map<String, String> requestParameters, VelocityContext velocityContext) {
        AdminService adminService = AlitheiaCore.getInstance().getAdminService();
        AdminAction adminAction = adminService.create(AddProject.MNEMONIC);
        adminAction.addArg("scm", requestParameters.get("scm"));
        adminAction.addArg("name", requestParameters.get("name"));
        adminAction.addArg("bts", requestParameters.get("bts"));
        adminAction.addArg("mail", requestParameters.get("mail"));
        adminAction.addArg("web", requestParameters.get("web"));
        adminService.execute(adminAction);
        
        if (adminAction.hasErrors()) {
            addWarning(Localization.getErr("project_install_failed"));
        } else { 
                                            addSuccess(Localization.getMsg("project_installed"));
        }
    }
    
    @Action("addProjectDir")
    public void dirAddProject(Map<String, String> requestParameters, VelocityContext velocityContext) {
        AdminService adminService = AlitheiaCore.getInstance().getAdminService();
        AdminAction adminAction = adminService.create(AddProject.MNEMONIC);
        adminAction.addArg("dir", requestParameters.get("projectDir"));
        adminService.execute(adminAction);
        
        if (adminAction.hasErrors()) {
            addWarning(Localization.getErr("project_install_failed"));
        } else {
            addSuccess(Localization.getMsg("project_installed"));
        }
    }

    
    @Action("removeProject")
    public void removeProject(Map<String, String> requestParameters, VelocityContext velocityContext) {
        AlitheiaCore core = AlitheiaCore.getInstance();
        Scheduler scheduler = core.getScheduler();
                
        if (selProject != null) {
            // Deleting large projects in the foreground is
            // very slow
            ProjectDeleteJob pdj = new ProjectDeleteJob(core, selProject);
            try {
                scheduler.enqueue(pdj);
            } catch (SchedulerException e1) {
                addDanger(Localization.getErr("e0034"));
            }
            selProject = null;
        } else {
            addDanger(Localization.getErr("e0034"));
        }
    }
    
    @Action("updateProject")
    public String updateProject(Map<String, String> requestParameters, VelocityContext velocityContext) {
        if (null != selProject) {
            AdminService as = AlitheiaCore.getInstance().getAdminService();
            AdminAction aa = as.create(UpdateProject.MNEMONIC);
            
            aa.addArg("project", selProject.getId());
            aa.addArg("updater", requestParameters.get("reqUpd"));
            as.execute(aa);
    
            if (aa.hasErrors()) {
                velocityContext.put("RESULTS", aa.errors());
            } else { 
                velocityContext.put("RESULTS", aa.results());
            }
            
            return showProject(requestParameters, velocityContext);
        } else {
            addDanger(Localization.getErr("e0034"));
            return null;
        }        
    }
    
    @Action("updateAllProjects")
    public void updateAllProjects(Map<String, String> requestParameters, VelocityContext velocityContext) {
        if (null != selProject) {
            AdminService as = AlitheiaCore.getInstance().getAdminService();
            AdminAction aa = as.create(UpdateProject.MNEMONIC);
            
            aa.addArg("project", selProject.getId());
            as.execute(aa);

            if (aa.hasErrors()) {
                velocityContext.put("RESULTS", aa.errors());
            } else { 
                velocityContext.put("RESULTS", aa.results());
            }
        } else {
            addDanger(Localization.getErr("e0034"));
        }
    }
    
    @Action("updateAllProjectsOnNode")
    public void updateAllProjectsNode(Map<String, String> requestParameters, VelocityContext velocityContext) {
        Set<StoredProject> projectList = ClusterNode.thisNode().getProjects();
        
        for (StoredProject project : projectList) {
            selProject = project;
            updateAllProjects(null, velocityContext);
        }
    }
    
    @Action("syncProject")
    public String syncProject(Map<String, String> requestParameters, VelocityContext velocityContext) {
        if(null == selProject) {
            return null;
        }
                
        String pluginHashcode = requestParameters.get("pluginHashcode");
        PluginAdmin pluginAdmin = AlitheiaCore.getInstance().getPluginAdmin();
        
        if ((pluginHashcode != null) ) {
            PluginInfo pluginInfo = pluginAdmin.getPluginInfo(pluginHashcode);
            if (null != pluginInfo) {
                AlitheiaPlugin plugin = pluginAdmin.getPlugin(pluginInfo);
                
                if (plugin != null) {
                    AlitheiaCore.getInstance().getMetricActivator().syncMetric(plugin, selProject);
                }
            }
        }
        
        return showProject(requestParameters, velocityContext);
    }
}
