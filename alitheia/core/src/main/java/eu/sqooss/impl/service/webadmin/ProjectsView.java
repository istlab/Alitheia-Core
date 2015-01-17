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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.service.admin.actions.UpdateProject;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;
import eu.sqooss.service.util.StringUtils;

public class ProjectsView extends AbstractView {
    
    private ArrayList<String> errors = new ArrayList<String>();
    private StoredProject selProject;
    
	/**
     * Instantiates a new projects view.
     *
     * @param bundlecontext the <code>BundleContext</code> object
     * @param vc the <code>VelocityContext</code> object
     */
    public ProjectsView(BundleContext bundlecontext, VelocityContext vc) {
        super(bundlecontext, vc);
    }
    
    public StoredProject getSelProject() {
		return selProject;
	}
    
    public void exec(HttpServletRequest req) {

        // Initialize the resource bundles with the request's locale
        initResources(req.getLocale());
        
        // Request values
        String reqValAction        = "";
        Long   reqValProjectId     = null;
        
        //Delete old errors
    	errors.clear();


        // Retrieve the selected editor's action (if any)
        reqValAction = req.getParameter(WebAdminConstants.REQ_PAR_ACTION);
        
        // Retrieve the selected project's DAO (if any)
        selProject = null;
        reqValProjectId = fromString(req.getParameter(WebAdminConstants.REQ_PAR_PROJECT_ID));
        if (reqValProjectId != null) {
            selProject = sobjDB.findObjectById(StoredProject.class, reqValProjectId);
        }
        
        if (reqValAction != null) {
        	if (reqValAction.equals(WebAdminConstants.ACT_CON_ADD_PROJECT)) {
        		selProject = addProject(req);
	        } else if (reqValAction.equals(WebAdminConstants.ACT_CON_REM_PROJECT)) {
	        	removeProject(selProject);
	        	this.selProject = null;
	        } else if (reqValAction.equals(WebAdminConstants.ACT_CON_UPD)) {
	        	triggerUpdate(selProject, req.getParameter(WebAdminConstants.REQ_PAR_UPD));
	        } else if (reqValAction.equals(WebAdminConstants.ACT_CON_UPD_ALL)) {
	        	triggerUpdate(selProject);
	        } else if (reqValAction.equals(WebAdminConstants.ACT_CON_UPD_ALL_NODE)) {
	        	triggerAllUpdateNode();
	        } else {
	        	// Retrieve the selected plug-in's hash-code
	    		String reqValSyncPlugin = req.getParameter(WebAdminConstants.REQ_PAR_SYNC_PLUGIN);
	    		syncPlugin(selProject, reqValSyncPlugin);
	        }
        }
    }

    /**
     * Creates a new stored project based on the request parameters
     * 
     * @param request Request sent to the server
     * @return the newly created stored project
     */
    private StoredProject addProject(HttpServletRequest request) {
    	
        AdminService as = sobjCore.getAdminService();
    	AdminAction aa = as.create(AddProject.MNEMONIC);
    	aa.addArg("scm", request.getParameter(WebAdminConstants.REQ_PAR_PRJ_CODE));
    	aa.addArg("name", request.getParameter(WebAdminConstants.REQ_PAR_PRJ_NAME));
    	aa.addArg("bts", request.getParameter(WebAdminConstants.REQ_PAR_PRJ_BUG));
    	aa.addArg("mail", request.getParameter(WebAdminConstants.REQ_PAR_PRJ_MAIL));
    	aa.addArg("web", request.getParameter(WebAdminConstants.REQ_PAR_PRJ_WEB));
    	as.execute(aa);
    	
    	if (aa.hasErrors()) {
            vc.put("RESULTS", aa.errors());
            return null;
    	} else { 
            vc.put("RESULTS", aa.results());
            return StoredProject.getProjectByName(request.getParameter(WebAdminConstants.REQ_PAR_PRJ_NAME));
    	}		
    }
    
    /**
     * Queues a job in the scheduler for deleting a project
     * 
     * @param project the project to be deleted
     */
    private void removeProject(StoredProject project) {
    	if (project != null) {
			// Deleting large projects in the foreground is very slow
			ProjectDeleteJob pdj = new ProjectDeleteJob(sobjCore, project);
			try {
				sobjSched.enqueue(pdj);
			} catch (SchedulerException e1) {
				errors.add(getErr("e0034"));
			}
		} else {
			errors.add(getErr("e0034"));
		}
    }


    /**
     * Triggers an update on a project
     * 
     * @param project the project which to update
     * @param mnem string determining the update to perform
     */
	private void triggerUpdate(StoredProject project, String mnem) {
		AdminService as = sobjCore.getAdminService();
		AdminAction aa = as.create(UpdateProject.MNEMONIC);
		aa.addArg("project", project.getId());
		if(mnem != null)
			aa.addArg("updater", mnem);
		as.execute(aa);

		if (aa.hasErrors()) {
            vc.put("RESULTS", aa.errors());
        } else { 
            vc.put("RESULTS", aa.results());
        }
	}

	/**
	 * Trigger update on all resources for that project
	 * 
	 * @param project the project to trigger the update on
	 */
	private void triggerUpdate(StoredProject project) {
		triggerUpdate(project, null);
	}
	
	/**
	 * Trigger update on all resources on all projects of this node
	 */
    private void triggerAllUpdateNode() {
		Set<StoredProject> projectList = ClusterNode.thisNode().getProjects();
		
		for (StoredProject project : projectList) {
			triggerUpdate(project);
		}
	}
	
    /**
     * Trigger synchronize on the selected plug-in for that project
     * 
     * @param project the selected project
     * @param reqValSyncPlugin the plugin to trigger the update on
     */
    private void syncPlugin(StoredProject project, String reqValSyncPlugin) {
		if ((reqValSyncPlugin != null) && (project != null)) {
			PluginInfo pInfo = sobjPA.getPluginInfo(reqValSyncPlugin);
			if (pInfo != null) {
				AlitheiaPlugin pObj = sobjPA.getPlugin(pInfo);
				if (pObj != null) {
					compMA.syncMetric(pObj, project);
					sobjLogger.debug("Syncronise plugin (" + pObj.getName()
							+ ") on project (" + project.getName() + ").");
				}
			}
		}
    }
	
    /**
     * Retrieves the errors encountered during the processing of the request
     * 
     * @return list with errors
     */
    public List<String> getErrors() {
    	ArrayList<String> htmlErrors = new ArrayList<String>();
    	for (String error : errors)
    		htmlErrors.add(StringUtils.makeXHTMLSafe(error));
    			
    	return htmlErrors;
    }
    
    /**
     * Retrieves all the projects on the local node
     * 
     * @return set with projects
     */
    public static Set<StoredProject> getProjects() {
    	return ClusterNode.thisNode().getProjects();
    }
    
    /**
     * Retrieves the latest project version of a project
     * 
     * @param project
     * @return String representation of the latest version
     */
    public static String getLastProjectVersion(StoredProject project) {
    	String lastVersion = getLbl("l0051");
    	if(project != null) {
	        ProjectVersion v = ProjectVersion.getLastProjectVersion(project);
	        if (v != null) {
	            lastVersion = String.valueOf(v.getSequence()) + "(" + v.getRevisionId() + ")";
	        }
    	}
        return lastVersion;
    }
    
    /**
     * Retrieves the latest email date of a project
     * 
     * @param project
     * @return String representation of the latest date an email was sent
     */
    public static String getLastEmailDate(StoredProject project) {
    	String lastDate = getLbl("l0051");
    	if(project != null) {
	        MailMessage mm = MailMessage.getLatestMailMessage(project);
	        if (mm != null) {
	        	lastDate = mm.getSendDate().toString();
	        }
    	}
        return lastDate;
    }
    
    /**
     * Retrieves the last bug of a project
     * 
     * @param project
     * @return String representation of the ID of the last bug filed for the project
     */
    public static String getLastBug(StoredProject project) {
    	String lastBug = getLbl("l0051");
    	if(project != null) {
	    	Bug bug = Bug.getLastUpdate(project);
	        if (bug != null) {
	        	lastBug = bug.getBugID();
	        }
    	}
        return lastBug;
    }
    
    /**
     * Retrieves the current state of evaluation of a project
     * 
     * @param project
     * @return String representation of the project evaluation state
     */
    public static String getEvalState(StoredProject project) {
        String evalState = getLbl("project_not_evaluated");
    	if(project != null) {
	        if (project.isEvaluated()) {
	        	evalState = getLbl("project_is_evaluated");
	        }
    	}
        return evalState;
    }
    
    /**
     * Retrieves the node on which a project runs
     * 
     * @param project
     * @return name of the cluster node (String)
     */
    public static String getClusternode(StoredProject project) {
	    String nodename = getLbl("l0051");
    	if(project != null) {
		    if (project.getClusternode() != null) {
		        nodename = project.getClusternode().getName();
		    } else {
		        nodename = "(local)";
		    }
    	}
	    return nodename;
    }

    /**
     * Retrieves the updaters of a specific project
     * 
     * @param project
     * @param updaterStage desired state of the updater 
     * @return a set with all updaters of a specific project in a specific state
     */
    public static Set<Updater> getUpdaters(StoredProject project, String updaterStage) {
    	Set<Updater> updaters;
    	 
    	UpdaterStage stage;
    	if(updaterStage == "inference")
    		stage = UpdaterStage.INFERENCE;
    	else if(updaterStage == "import")
    		stage = UpdaterStage.IMPORT;
    	else if(updaterStage == "parse")
    		stage = UpdaterStage.PARSE;
		else
    		stage = UpdaterStage.DEFAULT;

    	if(project != null)
    		updaters = sobjUpdater.getUpdaters(project, stage);
    	else
    		updaters =  Collections.emptySet();
    	return updaters;
    }
    
    /**
     * Retrieves the name of this cluster
     * 
     * @return String with the name
     */
    public static String getClusterName() {
    	return sobjClusterNode.getClusterNodeName();
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

