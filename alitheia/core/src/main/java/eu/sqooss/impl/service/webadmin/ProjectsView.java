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
     * Setup variables needed for templates and
     * put them into VelocityContext
     *
     * @param req the servlet's request object
     *
     * @return The HTML presentation of the generated view. Used for test cases.
     */
    public String setupVelocityContext(HttpServletRequest req) {
        // Stores the assembled HTML content 
    	// (only used for test-cases at the moment)
        StringBuilder testBuilder = new StringBuilder("\n");
        
        // Clear error message buffer first
    	this.errorMessages.clear();
    	
    	// Clear debug messages
    	this.debugMessages.clear();
        
        // Request values
        String reqValAction = "";
        Long reqValProjectId = null;

        // Selected project
        StoredProject selProject = null;

        // ===============================================================
        // Parse the servlet's request object
        // ===============================================================
        if (req != null) {

        	// Initialize the resource bundles with the request's locale
            initErrorResources(req.getLocale()); //TODO check
            // DEBUG: Dump the servlet's request parameter
            if (DEBUG) {
                testBuilder.append(debugRequest(req));//TODO do something with this
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
            	selProject = addProject(req);
            } else if (reqValAction.equals(ACT_CON_REM_PROJECT)) {
            	selProject = removeProject(selProject);
            } else if (reqValAction.equals(ACT_CON_UPD)) {
            	triggerUpdate(selProject, req.getParameter(REQ_PAR_UPD));
            } else if (reqValAction.equals(ACT_CON_UPD_ALL)) {
            	triggerAllUpdate(selProject);
            } else if (reqValAction.equals(ACT_CON_UPD_ALL_NODE)) {
            	triggerAllUpdateNode(selProject);
            } else {
            	// Retrieve the selected plug-in's hash-code
        		String reqValSyncPlugin = req.getParameter(REQ_PAR_SYNC_PLUGIN);
        		syncPlugin(selProject, reqValSyncPlugin);
            }
        }

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

        // setup form variables
        setupForm(testBuilder, selProject, reqValAction);
        
        // return testBuilder for the test cases
        return testBuilder.toString();
    }

    private StoredProject addProject(HttpServletRequest r) {
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
    private StoredProject removeProject(StoredProject selProject) {
    	if (selProject != null) {
			// Deleting large projects in the foreground is
			// very slow
			ProjectDeleteJob pdj = new ProjectDeleteJob(sobjCore, selProject);
			try {
				sobjSched.enqueue(pdj);
			} catch (SchedulerException e1) {
				errorMessages.add(getErr("e0034"));
			}
			selProject = null;
		} else {
			errorMessages.add(getErr("e0034"));
		}
    	return selProject;
    }

	// ---------------------------------------------------------------
	// Trigger an update
	// ---------------------------------------------------------------
	private void triggerUpdate(StoredProject selProject, String mnem) {
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
	private void triggerAllUpdate(StoredProject selProject) {
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
    private void triggerAllUpdateNode(StoredProject selProject) {
		Set<StoredProject> projectList = ClusterNode.thisNode().getProjects();

		for (StoredProject project : projectList) {
			triggerAllUpdate(project);
		}
	}

	// ---------------------------------------------------------------
	// Trigger synchronize on the selected plug-in for that project
	// ---------------------------------------------------------------
    private void syncPlugin(StoredProject selProject, String reqValSyncPlugin) {
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
     * Setup the variables needed in the template 
     */
    private void setupForm(StringBuilder b, StoredProject selProject, String reqValAction) {
        // ===============================================================
        // Display the accumulated error messages (if any)
        // ===============================================================

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
            vc.put("infoRows", rows);
            vc.put("onClickInstall", SUBMIT);
            vc.put("currentProjectTemplate", "ProjectInformation.html");

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
            vc.put("formFields", fields);
            vc.put("currentProjectTemplate", "addProjectEditor.html");
        }
        // ===================================================================
        // "Delete project" confirmation view
        // ===================================================================
        else if ((reqValAction.equals(ACT_REQ_REM_PROJECT))
                && (selProject != null)) {
        	//concat with empty string to convert null value to "null" string.
        	vc.put("projectName", ""+selProject.getName() );
            vc.put("onClickDelete","document.getElementById('"+
            								REQ_PAR_ACTION + "').value='"+
            								ACT_CON_REM_PROJECT + "';"+
            								SUBMIT);
            vc.put("onClickCancel",SUBMIT);
            vc.put("currentProjectTemplate", "confirmProjectDelete.html");
        }
        // ===================================================================
        // Projects list view
        // ===================================================================
        else {
            vc.put("noProjectsAvailable",projects.isEmpty());

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
                String lastVersion = null;
                ProjectVersion v = ProjectVersion.getLastProjectVersion(nextPrj);
                if (v != null) {
                    lastVersion = String.valueOf(v.getSequence()) + "(" + v.getRevisionId() + ")";
                }
                // Date of the last known email
                MailMessage mm = MailMessage.getLatestMailMessage(nextPrj);
                String lastMail = null;
                if (mm != null) {
                    lastMail = mm.getSendDate()+"";
                }

                // ID of the last known bug entry
                Bug bug = Bug.getLastUpdate(nextPrj);

                String lastBug = null;
                if (bug != null) {
                    lastBug = bug.getBugID()+"";
                }

                // Evaluation state
                String evalState = nextPrj.isEvaluated()+"";

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

            vc.put("projectList", projectRows);


            //----------------------------------------------------------------
            // Tool-bar
            //----------------------------------------------------------------
            addToolBar(selProject);
            vc.put("currentProjectTemplate", "projectList.html");
        }

        // ===============================================================
        // INPUT FIELDS
        // ===============================================================
        // "Action type" input field
    	vc.put("REQ_PAR_ACTION",REQ_PAR_ACTION);

        // "Project Id" input field
    	vc.put("REQ_PAR_PROJECT_ID",REQ_PAR_PROJECT_ID);
    	vc.put("selectedProjectId", ((selProject != null) ? selProject.getId() : ""));

    	// "Plug-in hashcode" input field
    	vc.put("REQ_PAR_SYNC_PLUGIN", REQ_PAR_SYNC_PLUGIN);

    	// test partial template for testcases
    	b.append(velocityContextToString("projectsView.html"));

    	// nothing to return
    }

    private void addToolBar(StoredProject selProject) {

    	String postArgument = "";
    	if(selProject != null){
    		postArgument = "?" + REQ_PAR_PROJECT_ID + "=" + selProject.getId();
    	}
    	vc.put("postArgument",postArgument);
    	vc.put("removeDisabled", ((selProject != null) ? "" : " disabled"));
    	vc.put("onClickSubmit", SUBMIT);
    	vc.put("REQ_PAR_ACTION", ProjectsView.REQ_PAR_ACTION);
    	vc.put("addProjectValue", ACT_REQ_ADD_PROJECT);
    	vc.put("removeProjectValue", ACT_REQ_REM_PROJECT);
    	vc.put("REQ_PAR_UPD", ProjectsView.REQ_PAR_UPD);
    	vc.put("ACT_CON_UPD_ALL_NODE", ProjectsView.ACT_CON_UPD_ALL_NODE);
    	vc.put("clusterName", sobjClusterNode.getClusterNodeName());
    	vc.put("projectSelected",selProject != null);
    	vc.put("ACT_CON_UPD",ACT_CON_UPD);
    	vc.put("ACT_CON_UPD_ALL",ACT_CON_UPD_ALL);

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
            vc.put("importUpdaters", importUpdaters);
            vc.put("parseUpdaters",parseUpdaters);
            vc.put("inferenceUpdaters",inferenceUpdaters);
            vc.put("defaultUpdaters", defaultUpdaters);
        }
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