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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
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

public class ProjectsView extends AbstractView {
    // Script for submitting this page
    private static String SUBMIT = "document.projects.submit();";

    // Action parameter's values
    private static String ACT_REQ_ADD_PROJECT   = "reqAddProject";
    private static String ACT_CON_ADD_PROJECT   = "conAddProject";
    private static String ACT_REQ_REM_PROJECT   = "reqRemProject";
    private static String ACT_CON_REM_PROJECT   = "conRemProject";
    private static String ACT_REQ_SHOW_PROJECT  = "conShowProject";
    private static String ACT_CON_UPD_ALL       = "conUpdateAll";
    private static String ACT_CON_UPD           = "conUpdate";
    private static String ACT_CON_UPD_ALL_NODE  = "conUpdateAllOnNode";

    // Servlet parameters
    private static String REQ_PAR_ACTION        = "reqAction";
    private static String REQ_PAR_PROJECT_ID    = "projectId";
    private static String REQ_PAR_PRJ_NAME      = "projectName";
    private static String REQ_PAR_PRJ_WEB       = "projectHomepage";
    private static String REQ_PAR_PRJ_CONT      = "projectContact";
    private static String REQ_PAR_PRJ_BUG       = "projectBL";
    private static String REQ_PAR_PRJ_MAIL      = "projectML";
    private static String REQ_PAR_PRJ_CODE      = "projectSCM";
    private static String REQ_PAR_SYNC_PLUGIN   = "reqParSyncPlugin";
    private static String REQ_PAR_UPD           = "reqUpd";
    
    /**
     * Instantiates a new projects view.
     *
     * @param bundlecontext the <code>BundleContext</code> object
     * @param vc the <code>VelocityContext</code> object
     */
    public ProjectsView(BundleContext bundlecontext, VelocityContext vc) {
        super(bundlecontext, vc);
    }

    /**
     * Renders the various project's views.
     *
     * @param req the servlet's request object
     *
     * @return The HTML presentation of the generated view.
     * TODO rename this function to its new function:
     *  - rendering well be performed through velocity templates
     *  - this method will be used to setup all the variables and put them into VelocityContext vc
     */
    public String render(HttpServletRequest req) {
        // Stores the assembled HTML content
        StringBuilder b = new StringBuilder("\n");
        // Stores the accumulated error messages
        StringBuilder e = new StringBuilder();
        // Indentation spacer
        int in = 6;

        // Request values
        String reqValAction        = "";
        Long   reqValProjectId     = null;

        // Selected project
        StoredProject selProject = null;

        // ===============================================================
        // Parse the servlet's request object
        // ===============================================================
        if (req != null) {
            
        	// Initialize the resource bundles with the request's locale
            initResources(req.getLocale());
            // DEBUG: Dump the servlet's request parameter
            if (DEBUG) {
                b.append(debugRequest(req));
            }

            // Retrieve the selected editor's action (if any)
            reqValAction = req.getParameter(REQ_PAR_ACTION);
            
            // Retrieve the selected project's DAO (if any)
            reqValProjectId = fromString(req.getParameter(REQ_PAR_PROJECT_ID));
            if (reqValProjectId != null) {
                selProject = sobjDB.findObjectById(
                        StoredProject.class, reqValProjectId);
            }
            
            if (reqValAction == null) {
                reqValAction = "";
            } else if (reqValAction.equals(ACT_CON_ADD_PROJECT)) {
            	selProject = addProject(e, req, in);
            } else if (reqValAction.equals(ACT_CON_REM_PROJECT)) {
            	selProject = removeProject(e, selProject, in);
            } else if (reqValAction.equals(ACT_CON_UPD)) {
            	triggerUpdate(e, selProject, in, req.getParameter(REQ_PAR_UPD));
            } else if (reqValAction.equals(ACT_CON_UPD_ALL)) {
            	triggerAllUpdate(e, selProject, in);
            } else if (reqValAction.equals(ACT_CON_UPD_ALL_NODE)) {
            	triggerAllUpdateNode(e, selProject, in);
            } else {
            	// Retrieve the selected plug-in's hash-code
        		String reqValSyncPlugin = req.getParameter(REQ_PAR_SYNC_PLUGIN);
        		syncPlugin(e, selProject, reqValSyncPlugin);
            }
        }
        
        // add variables needed in templates to vc
        // @TODO perhaps these vars must be grouped 
        // for example:
        // - a group for Action parameter's values
        // - a group for Servlet parameters
        // 
//        vc.put("projectsView", this); //already in AdminServlet as 'projects'
        vc.put("reqValAction", reqValAction);
        vc.put("selProject", selProject);
        vc.put("ACT_REQ_SHOW_PROJECT",ACT_REQ_SHOW_PROJECT);
        vc.put("ACT_REQ_ADD_PROJECT", ACT_REQ_ADD_PROJECT);
        vc.put("SUBMIT", SUBMIT);
        vc.put("REQ_PAR_PRJ_NAME",REQ_PAR_PRJ_NAME);
        vc.put("REQ_PAR_PRJ_WEB",REQ_PAR_PRJ_WEB);
        vc.put("REQ_PAR_PRJ_CONT",REQ_PAR_PRJ_CONT);
        vc.put("REQ_PAR_PRJ_BUG",REQ_PAR_PRJ_BUG);
        vc.put("REQ_PAR_PRJ_MAIL",REQ_PAR_PRJ_MAIL);
        vc.put("REQ_PAR_PRJ_CODE",REQ_PAR_PRJ_CODE);
        vc.put("REQ_PAR_PROJECT_ID",REQ_PAR_PROJECT_ID);
        vc.put("REQ_PAR_ACTION",REQ_PAR_ACTION);
        createForm(b, e, selProject, reqValAction , in);
        return b.toString();
    }
  
    private StoredProject addProject(StringBuilder e, HttpServletRequest r, int indent) {
        AdminService as = AlitheiaCore.getInstance().getAdminService();
    	AdminAction aa = as.create(AddProject.MNEMONIC);
    	aa.addArg("scm", r.getParameter(REQ_PAR_PRJ_CODE));
    	aa.addArg("name", r.getParameter(REQ_PAR_PRJ_NAME));
    	aa.addArg("bts", r.getParameter(REQ_PAR_PRJ_BUG));
    	aa.addArg("mail", r.getParameter(REQ_PAR_PRJ_MAIL));
    	aa.addArg("web", r.getParameter(REQ_PAR_PRJ_WEB));
    	as.execute(aa);
    	
    	if (aa.hasErrors()) {
            vc.put("RESULTS", aa.errors());
            return null;
    	} else { 
            vc.put("RESULTS", aa.results());
            return StoredProject.getProjectByName(r.getParameter(REQ_PAR_PRJ_NAME));
    	}
    }
    
    // ---------------------------------------------------------------
    // Remove project
    // ---------------------------------------------------------------
    private StoredProject removeProject(StringBuilder e, 
    		StoredProject selProject, int indent) {
    	if (selProject != null) {
			// Deleting large projects in the foreground is
			// very slow
			ProjectDeleteJob pdj = new ProjectDeleteJob(sobjCore, selProject);
			try {
				sobjSched.enqueue(pdj);
			} catch (SchedulerException e1) {
				e.append(sp(indent)).append(getErr("e0034")).append("<br/>\n");
			}
			selProject = null;
		} else {
			e.append(sp(indent) + getErr("e0034") + "<br/>\n");
		}
    	return selProject;
    }

	// ---------------------------------------------------------------
	// Trigger an update
	// ---------------------------------------------------------------
	private void triggerUpdate(StringBuilder e,
			StoredProject selProject, int indent, String mnem) {
		AdminService as = AlitheiaCore.getInstance().getAdminService();
		AdminAction aa = as.create(UpdateProject.MNEMONIC);
		aa.addArg("project", selProject.getId());
		aa.addArg("updater", mnem);
		as.execute(aa);

		if (aa.hasErrors()) {
            vc.put("RESULTS", aa.errors());
        } else { 
            vc.put("RESULTS", aa.results());
        }
	}

	// ---------------------------------------------------------------
	// Trigger update on all resources for that project
	// ---------------------------------------------------------------
	private void triggerAllUpdate(StringBuilder e,
			StoredProject selProject, int indent) {
	    AdminService as = AlitheiaCore.getInstance().getAdminService();
        AdminAction aa = as.create(UpdateProject.MNEMONIC);
        aa.addArg("project", selProject.getId());
        as.execute(aa);

        if (aa.hasErrors()) {
            vc.put("RESULTS", aa.errors());
        } else { 
            vc.put("RESULTS", aa.results());
        }
	}
	
	// ---------------------------------------------------------------
	// Trigger update on all resources on all projects of a node
	// ---------------------------------------------------------------
    private void triggerAllUpdateNode(StringBuilder e,
			StoredProject selProject, int in) {
		Set<StoredProject> projectList = ClusterNode.thisNode().getProjects();
		
		for (StoredProject project : projectList) {
			triggerAllUpdate(e, project, in);
		}
	}
	
	// ---------------------------------------------------------------
	// Trigger synchronize on the selected plug-in for that project
	// ---------------------------------------------------------------
    private void syncPlugin(StringBuilder e, StoredProject selProject, String reqValSyncPlugin) {
		if ((reqValSyncPlugin != null) && (selProject != null)) {
			PluginInfo pInfo = sobjPA.getPluginInfo(reqValSyncPlugin);
			if (pInfo != null) {
				AlitheiaPlugin pObj = sobjPA.getPlugin(pInfo);
				if (pObj != null) {
					compMA.syncMetric(pObj, selProject);
					sobjLogger.debug("Syncronise plugin (" + pObj.getName()
							+ ") on project (" + selProject.getName() + ").");
				}
			}
		}
    }
    
    /*
     * @author elwin
     * Function for template to get a set of projects. 
     * @return Set<StoredProject> set of stored projects for current node
     */
    public Set<StoredProject> getProjects() {
    	return ClusterNode.thisNode().getProjects();
    }
    
    /* 
     * TODO this function which renders the main form for the list of projects is
     * going to be replaced by projectlist velocity template
     * == to be deprecated ==
     */
    private void createForm(StringBuilder b, StringBuilder e, 
    		StoredProject selProject, String reqValAction, int in) {
        // ===============================================================
        // Display the accumulated error messages (if any)
        // ===============================================================
        b.append(errorFieldset(e, ++in));
    	
    	VelocityContext vcLocal = new VelocityContext();



        // Get the complete list of projects stored in the SQO-OSS framework
        Set<StoredProject> projects = ClusterNode.thisNode().getProjects();
        Collection<PluginInfo> metrics = sobjPA.listPlugins();

        // ===================================================================
        // "Show project info" view
        // ===================================================================
        if ((reqValAction.equals(ACT_REQ_SHOW_PROJECT))
                && (selProject != null)) {

        	// Create the field-container
          	List<Map<String,String>> rows = new ArrayList<Map<String,String>>();
            // Create the input fields
            rows.add(normalInfoRowMap("Project name", selProject.getName()));
            rows.add(normalInfoRowMap("Homepage", selProject.getWebsiteUrl()));
            rows.add(normalInfoRowMap("Contact e-mail", selProject.getContactUrl()));
            rows.add(normalInfoRowMap("Bug database", selProject.getBtsUrl()));
            rows.add(normalInfoRowMap("Mailing list", selProject.getMailUrl()));
            rows.add(normalInfoRowMap("Source code", selProject.getScmUrl()));
            //add info fields to velocity context
            vcLocal.put("infoRows", rows);
            vcLocal.put("backButtonText", getLbl("btn_back"));
            vcLocal.put("onClickInstall", SUBMIT);
            vcLocal.put("currentProjectTemplate", "ProjectInformation.html");

        }
        // ===================================================================
        // "Add project" editor
        // ===================================================================
        else if (reqValAction.equals(ACT_REQ_ADD_PROJECT)) {
        	// Create the field-container
        	List<Map<String,String>> fields = new ArrayList<Map<String,String>>();
            // Create the input fields
            fields.add(normalInputRowMap("Project name", REQ_PAR_PRJ_NAME, ""));
            fields.add(normalInputRowMap("Homepage", REQ_PAR_PRJ_WEB, ""));
            fields.add(normalInputRowMap("Contact e-mail", REQ_PAR_PRJ_CONT, ""));
            fields.add(normalInputRowMap("Bug database", REQ_PAR_PRJ_BUG, ""));
            fields.add(normalInputRowMap("Mailing list", REQ_PAR_PRJ_MAIL, ""));
            fields.add(normalInputRowMap("Source code", REQ_PAR_PRJ_CODE, ""));
            vcLocal.put("formFields", fields);
            vcLocal.put("currentProjectTemplate", "addProjectEditor.html");
        }
        // ===================================================================
        // "Delete project" confirmation view
        // ===================================================================
        else if ((reqValAction.equals(ACT_REQ_REM_PROJECT))
                && (selProject != null)) {
        	vcLocal.put("deleteLabel", getLbl("l0059"));
        	//concat with empty string to convert null value to "null" string.
        	vcLocal.put("projectName", ""+selProject.getName() );
            vcLocal.put("message", getMsg("delete_project"));
            vcLocal.put("onClickDelete","document.getElementById('"+ 
            								REQ_PAR_ACTION + "').value='"+ 
            								ACT_CON_REM_PROJECT + "';"+ 
            								SUBMIT);
            vcLocal.put("onClickCancel",SUBMIT);
            vcLocal.put("confirmValue",getLbl("l0006"));
            vcLocal.put("cancelValue", getLbl("l0004"));
            vcLocal.put("currentProjectTemplate", "confirmProjectDelete.html");
        }
        // ===================================================================
        // Projects list view
        // ===================================================================
        else {
            vcLocal.put("idHeader",getLbl("l0066"));
            vcLocal.put("nameHeader",getLbl("l0067"));
            vcLocal.put("versionHeader",getLbl("l0068"));
            vcLocal.put("emailHeader",getLbl("l0069"));
            vcLocal.put("bugHeader",getLbl("l0070"));
            vcLocal.put("evaluatedHeader",getLbl("l0071"));
            vcLocal.put("hostHeader",getLbl("l0073"));
            vcLocal.put("no_projects", getMsg("no_projects"));
            vcLocal.put("noProjectsAvailable",projects.isEmpty());
            
            //------------------------------------------------------------
            // Create the content rows
            //------------------------------------------------------------
        	List<Map<String,String>> projectRows = new ArrayList<Map<String,String>>();

            for (StoredProject nextPrj : projects) {
                Map<String,String> projectRow = new HashMap<String,String>(9);
            	boolean selected = false;
                if ((selProject != null)
                        && (selProject.getId() == nextPrj.getId())) {
                    selected = true;
                }
                
                // Last project version             
                String lastVersion = getLbl("l0051");
                ProjectVersion v = ProjectVersion.getLastProjectVersion(nextPrj);
                if (v != null) {
                    lastVersion = String.valueOf(v.getSequence()) + "(" + v.getRevisionId() + ")";
                }
                // Date of the last known email                
                MailMessage mm = MailMessage.getLatestMailMessage(nextPrj);
                String lastMail = (mm == null)?getLbl("l0051"):mm.getSendDate()+"";
                
                // ID of the last known bug entry
                Bug bug = Bug.getLastUpdate(nextPrj);
                String lastBug = (bug == null)?getLbl("l0051"):bug.getBugID()+"";

                // Evaluation state
                String evalState = getLbl("project_not_evaluated");
                if (nextPrj.isEvaluated()) {
                	evalState = getLbl("project_is_evaluated");
                }
                
                // Cluster node
                String nodeName = null;
                if (null != nextPrj.getClusternode()) {
                    nodeName = nextPrj.getClusternode().getName()+"";
                } else {
                    nodeName = "(local)";
                }
                
                projectRow.put("cssClass", (selected) ? "selected" : "edit");
                projectRow.put("onClickSelectElementId",REQ_PAR_PROJECT_ID);
                projectRow.put("onClickSelectProjectId",selected ? "" : nextPrj.getId()+"");
                projectRow.put("onClickSelectSubmit",SUBMIT);		
                projectRow.put("projectId",nextPrj.getId()+"");
                projectRow.put("projectName",nextPrj.getName()+"");
                projectRow.put("projectVersion",lastVersion);
                projectRow.put("projectLastMailDate",lastMail);
                projectRow.put("projectLastBugId",lastBug);
                projectRow.put("projectEvaluationState",evalState);
                projectRow.put("projectNode",nodeName);
                
                if(selected){
                	projectRow.put("selected","selected");
                	projectRow.put("selectedProjectAction", REQ_PAR_ACTION);
                    projectRow.put("selectedProjectActionValue", ACT_REQ_SHOW_PROJECT);
                    projectRow.put("selectedProjectSubmitValue",getLbl("btn_info"));
                    projectRow.put("selectedProjectHasMetrics",!metrics.isEmpty() ? "metrics" : "");
                    if(!metrics.isEmpty()){
                    	List<Map<String,String>> metricRows = new ArrayList<Map<String,String>>();
                        boolean showMetric = false;
                    	for(PluginInfo m : metrics) {
                            if (m.installed) {
                            	showMetric = true;
                            	Map<String,String>metricRow = new HashMap<String,String>(2);
                            	metricRow.put("name", m.getPluginName());
                            	metricRow.put("hashCode", m.getHashcode());
                            }
                        }
                    	vc.put("showMetric", showMetric);
                    	if(showMetric){
                    		vc.put("metricList", metricRows);
                    		vc.put("onClickSubmit",SUBMIT);
                    		vc.put("metricElementId",REQ_PAR_SYNC_PLUGIN);
                    	}   	
                    }
                }
                projectRows.add(projectRow);
            }
            
            vcLocal.put("projectList", projectRows);
            
            
            //----------------------------------------------------------------
            // Tool-bar
            //----------------------------------------------------------------
            addToolBar(selProject,vcLocal);
            vcLocal.put("currentProjectTemplate", "projectList.html");
        }

        // ===============================================================
        // INPUT FIELDS
        // ===============================================================
        // "Action type" input field
    	vcLocal.put("REQ_PAR_ACTION",REQ_PAR_ACTION);
    	
        // "Project Id" input field
    	vcLocal.put("REQ_PAR_PROJECT_ID",REQ_PAR_PROJECT_ID);
    	vcLocal.put("selectedProjectId", ((selProject != null) ? selProject.getId() : ""));
        
    	// "Plug-in hashcode" input field
    	vcLocal.put("REQ_PAR_SYNC_PLUGIN", REQ_PAR_SYNC_PLUGIN);
        
        //create actual string.
        b.append(velocityContextToString(vcLocal, "projectsView.html"));
    }

    
    private VelocityContext addToolBar(StoredProject selProject, VelocityContext origin) {
    	VelocityContext vcLocal = origin;

    	String postArgument = "";
    	if(selProject != null){
    		postArgument = "?" + REQ_PAR_PROJECT_ID + "=" + selProject.getId();
    	}
    	vcLocal.put("postArgument",postArgument);
    	vcLocal.put("removeDisabled", ((selProject != null) ? "" : " disabled"));
    	vcLocal.put("onClickSubmit", SUBMIT);
    	vcLocal.put("refreshButton", getLbl("l0008"));
    	vcLocal.put("addProjectButton", getLbl("add_project"));
    	vcLocal.put("deleteProjectButton", getLbl("l0059"));
    	vcLocal.put("REQ_PAR_ACTION", ProjectsView.REQ_PAR_ACTION);
    	vcLocal.put("addProjectValue", ACT_REQ_ADD_PROJECT);
    	vcLocal.put("removeProjectValue", ACT_REQ_REM_PROJECT);
    	vcLocal.put("REQ_PAR_UPD", ProjectsView.REQ_PAR_UPD);
    	vcLocal.put("ACT_CON_UPD_ALL_NODE", ProjectsView.ACT_CON_UPD_ALL_NODE);
    	vcLocal.put("clusterName", sobjClusterNode.getClusterNodeName());
    	vcLocal.put("projectSelected",selProject != null);
    	vcLocal.put("ACT_CON_UPD",ACT_CON_UPD);
    	vcLocal.put("ACT_CON_UPD_ALL",ACT_CON_UPD_ALL);
        
        if (selProject != null) {
        	List<Map<String,String>> importUpdaters = new ArrayList<Map<String,String>>();
        	List<Map<String,String>> parseUpdaters = new ArrayList<Map<String,String>>();
        	List<Map<String,String>> inferenceUpdaters = new ArrayList<Map<String,String>>();
        	List<Map<String,String>> defaultUpdaters = new ArrayList<Map<String,String>>();
            for (Updater u : sobjUpdater.getUpdaters(selProject, UpdaterStage.IMPORT)) {
                importUpdaters.add(getUpdaterOptionMap(u));
            }
            for (Updater u : sobjUpdater.getUpdaters(selProject, UpdaterStage.PARSE)) {
                parseUpdaters.add(getUpdaterOptionMap(u));
            }
            for (Updater u : sobjUpdater.getUpdaters(selProject, UpdaterStage.INFERENCE)) {
                inferenceUpdaters.add(getUpdaterOptionMap(u));
            }
            for (Updater u : sobjUpdater.getUpdaters(selProject, UpdaterStage.DEFAULT)) {
                defaultUpdaters.add(getUpdaterOptionMap(u));
            }
            vcLocal.put("importUpdaters", importUpdaters);
            vcLocal.put("parseUpdaters",parseUpdaters);
            vcLocal.put("inferenceUpdaters",inferenceUpdaters);
            vcLocal.put("defaultUpdaters", defaultUpdaters);
        }
        return vcLocal;
    }
    
    /**
     * This function creates a map with the values that 
     * the velocity template expects from an updater.
     * @param u The updater from which values will be grabed and put in a map
     * @return A map with values that are known by the template that uses it.
     */
    private Map<String,String> getUpdaterOptionMap(Updater u){
    	Map<String,String> updaterMap = new HashMap<String,String>(2);
    	updaterMap.put("mnem", u.mnem());
    	updaterMap.put("description",u.descr());
    	return updaterMap;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

