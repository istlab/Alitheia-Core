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

import java.util.Collection;
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
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

public class ProjectsView extends AbstractView {
    // Script for submitting this page
    protected static String SUBMIT = "document.projects.submit();";

    // Action parameter's values
    protected static String ACT_REQ_ADD_PROJECT   = "reqAddProject";
    private static String ACT_CON_ADD_PROJECT   = "conAddProject";
    protected static String ACT_REQ_REM_PROJECT   = "reqRemProject";
    private static String ACT_CON_REM_PROJECT   = "conRemProject";
    private static String ACT_REQ_SHOW_PROJECT  = "conShowProject";
    protected static String ACT_CON_UPD_ALL       = "conUpdateAll";
    protected static String ACT_CON_UPD           = "conUpdate";
    protected static String ACT_CON_UPD_ALL_NODE  = "conUpdateAllOnNode";

    // Servlet parameters
    protected static String REQ_PAR_ACTION        = "reqAction";
    protected static String REQ_PAR_PROJECT_ID    = "projectId";
    protected static String REQ_PAR_PRJ_NAME      = "projectName";
    protected static String REQ_PAR_PRJ_WEB       = "projectHomepage";
    private static String REQ_PAR_PRJ_CONT      = "projectContact";
    protected static String REQ_PAR_PRJ_BUG       = "projectBL";
    protected static String REQ_PAR_PRJ_MAIL      = "projectML";
    protected static String REQ_PAR_PRJ_CODE      = "projectSCM";
    protected static String REQ_PAR_SYNC_PLUGIN   = "reqParSyncPlugin";
    protected static String REQ_PAR_UPD           = "reqUpd";
    
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
     */
    public String render(HttpServletRequest req) {
        // Stores the assembled HTML content
        StringBuilder b = new StringBuilder("\n");
        // Stores the accumulated error messages
        StringBuilder e = new StringBuilder();
        // Indentation spacer
        int in = 6;

        // Initialize the resource bundles with the request's locale
        initResources(req.getLocale());

        // Request values
        String reqValAction        = "";
        Long   reqValProjectId     = null;

        // Selected project
        StoredProject selProject = null;

        // ===============================================================
        // Parse the servlet's request object
        // ===============================================================
        if (req != null) {
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
        createForm(b, e, selProject, reqValAction , in);
        return b.toString();
    }
  
    protected StoredProject addProject(StringBuilder e, HttpServletRequest r, int indent) {
        AdminService as = getAdminService();
    	AdminAction aa = as.create(AddProject.MNEMONIC);
    	aa.addArg("scm", r.getParameter(REQ_PAR_PRJ_CODE));
    	aa.addArg("name", r.getParameter(REQ_PAR_PRJ_NAME));
    	aa.addArg("bts", r.getParameter(REQ_PAR_PRJ_BUG));
    	aa.addArg("mail", r.getParameter(REQ_PAR_PRJ_MAIL));
    	aa.addArg("web", r.getParameter(REQ_PAR_PRJ_WEB));
    	as.execute(aa);
    	
    	if (aa.hasErrors()) {
            getVelocityContext().put("RESULTS", aa.errors());
            return null;
    	} else { 
            getVelocityContext().put("RESULTS", aa.results());
            return getProjectByName(r.getParameter(REQ_PAR_PRJ_NAME));
    	}
    }

	protected StoredProject getProjectByName(String parameter) {
		return StoredProject.getProjectByName(parameter);
	}
    
    // ---------------------------------------------------------------
    // Remove project
    // ---------------------------------------------------------------
    protected StoredProject removeProject(StringBuilder e, 
    		StoredProject selProject, int indent) {
    	if (selProject != null) {
			// Deleting large projects in the foreground is
			// very slow
			ProjectDeleteJob pdj = new ProjectDeleteJob(sobjCore, selProject);
			try {
				getScheduler().enqueue(pdj);
			} catch (SchedulerException e1) {
				e.append(sp(indent)).append(getErr("e0034")).append("<br/>\n");
			}
			selProject = null;
		} else {
			e.append(sp(indent) + getErr("e0034") + "<br/>\n");
		}
    	return selProject;
    }

	protected ProjectDeleteJob createProjectDeleteJob(StoredProject selProject) {
		return new ProjectDeleteJob(sobjCore, selProject);
	}

	protected Scheduler getScheduler() {
		return sobjSched;
	}

	// ---------------------------------------------------------------
	// Trigger an update
	// ---------------------------------------------------------------
	protected void triggerUpdate(StringBuilder e,
			StoredProject selProject, int indent, String mnem) {
		AdminService as = getAdminService();
		AdminAction aa = as.create(UpdateProject.MNEMONIC);
		aa.addArg("project", selProject.getId());
		aa.addArg("updater", mnem);
		as.execute(aa);

		if (aa.hasErrors()) {
            getVelocityContext().put("RESULTS", aa.errors());
        } else { 
            getVelocityContext().put("RESULTS", aa.results());
        }
	}

	protected VelocityContext getVelocityContext() {
		return vc;
	}

	protected AdminService getAdminService() {
		return AlitheiaCore.getInstance().getAdminService();
	}

	// ---------------------------------------------------------------
	// Trigger update on all resources for that project
	// ---------------------------------------------------------------
	protected void triggerAllUpdate(StringBuilder e,
			StoredProject selProject, int indent) {
	    AdminService as = getAdminService();
        AdminAction aa = as.create(UpdateProject.MNEMONIC);
        aa.addArg("project", selProject.getId());
        as.execute(aa);

        if (aa.hasErrors()) {
            getVelocityContext().put("RESULTS", aa.errors());
        } else { 
            getVelocityContext().put("RESULTS", aa.results());
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
    protected void syncPlugin(StringBuilder e, StoredProject selProject, String reqValSyncPlugin) {
		if ((reqValSyncPlugin != null) && (selProject != null)) {
			PluginInfo pInfo = getPluginAdmin().getPluginInfo(reqValSyncPlugin);
			if (pInfo != null) {
				AlitheiaPlugin pObj = getPluginAdmin().getPlugin(pInfo);
				if (pObj != null) {
					getMetricActivator().syncMetric(pObj, selProject);
					getLogger().debug("Syncronise plugin (" + pObj.getName()
							+ ") on project (" + selProject.getName() + ").");
				}
			}
		}
    }

	protected Logger getLogger() {
		return sobjLogger;
	}

	protected MetricActivator getMetricActivator() {
		return compMA;
	}

	protected PluginAdmin getPluginAdmin() {
		return sobjPA;
	}
    
    private void createForm(StringBuilder b, StringBuilder e, 
    		StoredProject selProject, String reqValAction, int in) {

        // ===============================================================
        // Create the form
        // ===============================================================
        b.append(sp(in) + "<form id=\"projects\""
                + " name=\"projects\""
                + " method=\"post\""
                + " action=\"/projects\">\n");

        // ===============================================================
        // Display the accumulated error messages (if any)
        // ===============================================================
        b.append(errorFieldset(e, ++in));

        // Get the complete list of projects stored in the SQO-OSS framework
        Set<StoredProject> projects = ClusterNode.thisNode().getProjects();
        Collection<PluginInfo> metrics = getPluginAdmin().listPlugins();

        // ===================================================================
        // "Show project info" view
        // ===================================================================
        if ((reqValAction.equals(ACT_REQ_SHOW_PROJECT))
                && (selProject != null)) {
            // Create the field-set
            b.append(sp(in++) + "<fieldset>\n");
            b.append(sp(in) + "<legend>"
                    + "Project information"
                    + "</legend>\n");
            b.append(sp(in++) + "<table class=\"borderless\">\n");
            // Create the input fields
            b.append(normalInfoRow(
                    "Project name", selProject.getName(), in));
            b.append(normalInfoRow(
                    "Homepage", selProject.getWebsiteUrl(), in));
            b.append(normalInfoRow(
                    "Contact e-mail", selProject.getContactUrl(), in));
            b.append(normalInfoRow(
                    "Bug database", selProject.getBtsUrl(), in));
            b.append(normalInfoRow(
                    "Mailing list", selProject.getMailUrl(), in));
            b.append(normalInfoRow(
                    "Source code", selProject.getScmUrl(), in));

            //------------------------------------------------------------
            // Tool-bar
            //------------------------------------------------------------
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in++)
                    + "<td colspan=\"2\" class=\"borderless\">\n");
            // Back button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("btn_back") + "\""
                    + " onclick=\"javascript:"
                    + SUBMIT + "\">\n");
            b.append(sp(--in) + "</td>\n");
            b.append(sp(--in) + "</tr>\n");
            b.append(sp(--in) + "</table>\n");
            b.append(sp(--in) + "</fieldset>\n");
        }
        // ===================================================================
        // "Add project" editor
        // ===================================================================
        else if (reqValAction.equals(ACT_REQ_ADD_PROJECT)) {
            // Create the field-set
            b.append(sp(in++) + "<table class=\"borderless\" width='100%'>\n");
            // Create the input fields
            b.append(normalInputRow(
                    "Project name", REQ_PAR_PRJ_NAME, "", in));
            b.append(normalInputRow(
                    "Homepage", REQ_PAR_PRJ_WEB, "", in));
            b.append(normalInputRow(
                    "Contact e-mail", REQ_PAR_PRJ_CONT, "", in));
            b.append(normalInputRow(
                    "Bug database", REQ_PAR_PRJ_BUG, "", in));
            b.append(normalInputRow(
                    "Mailing list", REQ_PAR_PRJ_MAIL, "", in));
            b.append(normalInputRow(
                    "Source code", REQ_PAR_PRJ_CODE, "", in));

            //------------------------------------------------------------
            // Tool-bar
            //------------------------------------------------------------
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in++)
                    + "<td colspan=\"2\" class=\"borderless\">\n");
            // Apply button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("project_add") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + REQ_PAR_ACTION + "').value='"
                    + ACT_CON_ADD_PROJECT + "';"
                    + SUBMIT + "\">\n");
            // Cancel button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("cancel") + "\""
                    + " onclick=\"javascript:"
                    + SUBMIT + "\">\n");
            b.append(sp(--in) + "</td>\n");
            b.append(sp(--in) + "</tr>\n");
            b.append(sp(--in) + "</table>\n");
        }
        // ===================================================================
        // "Delete project" confirmation view
        // ===================================================================
        else if ((reqValAction.equals(ACT_REQ_REM_PROJECT))
                && (selProject != null)) {
            b.append(sp(in++) + "<fieldset>\n");
            b.append(sp(in) + "<legend>" + getLbl("l0059")
                    + ": " + selProject.getName()
                    + "</legend>\n");
            b.append(sp(in++) + "<table class=\"borderless\">");
            // Confirmation message
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in) + "<td class=\"borderless\">"
                    + "<b>" + getMsg("delete_project") + "</b>"
                    + "</td>\n");

            b.append(sp(--in) + "</tr>\n");
            //------------------------------------------------------------
            // Tool-bar
            //------------------------------------------------------------
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in++)
                    + "<td class=\"borderless\">\n");
            // Confirm button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("l0006") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + REQ_PAR_ACTION + "').value='"
                    + ACT_CON_REM_PROJECT + "';"
                    + SUBMIT + "\">\n");
            // Cancel button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("l0004") + "\""
                    + " onclick=\"javascript:"
                    + SUBMIT + "\">\n");
            b.append(sp(--in) + "</td>\n");
            b.append(sp(--in) + "</tr>\n");
            b.append(sp(--in) + "</table>");
            b.append(sp(in) + "</fieldset>\n");
        }
        // ===================================================================
        // Projects list view
        // ===================================================================
        else {
            addHeaderRow(b,in);

            if (projects.isEmpty()) {
                b.append(sp(in++) + "<tr>\n");
                b.append(sp(in) + "<td colspan=\"6\" class=\"noattr\">\n"
                        + getMsg("no_projects")
                        + "</td>\n");
                b.append(sp(--in) + "</tr>\n");
            }
            else {
                //------------------------------------------------------------
                // Create the content rows
                //------------------------------------------------------------
                b.append(sp(in++) + "<tbody>\n");
                for (StoredProject nextPrj : projects) {
                    boolean selected = false;
                    if ((selProject != null)
                            && (selProject.getId() == nextPrj.getId())) {
                        selected = true;
                    }
                    b.append(sp(in++) + "<tr class=\""
                            + ((selected) ? "selected" : "edit") + "\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + REQ_PAR_PROJECT_ID + "').value='"
                            + ((selected) ? "" : nextPrj.getId())
                            + "';"
                            + SUBMIT + "\">\n");
                    // Project Id
                    b.append(sp(in) + "<td class=\"trans\">"
                            + nextPrj.getId()
                            + "</td>\n");
                    // Project name
                    b.append(sp(in) + "<td class=\"trans\">"
                            + ((selected)
                                    ? "<input type=\"button\""
                                        + " class=\"install\""
                                        + " style=\"width: 100px;\""
                                        + " value=\""
                                        + getLbl("btn_info")
                                        + "\""
                                        + " onclick=\"javascript:"
                                        + "document.getElementById('"
                                        + REQ_PAR_ACTION + "').value='" 
                                        + ACT_REQ_SHOW_PROJECT + "';"
                                        + SUBMIT + "\">"
                                    : "<img src=\"/edit.png\""
                                        + " alt=\"[Edit]\"/>")
                            + "&nbsp;"
                            + nextPrj.getName()
                            + "</td>\n");
                    // Last project version
                    String lastVersion = getLbl("l0051");
                    ProjectVersion v = ProjectVersion.getLastProjectVersion(nextPrj);
                    if (v != null) {
                        lastVersion = String.valueOf(v.getSequence()) + "(" + v.getRevisionId() + ")";
                    }
                    b.append(sp(in) + "<td class=\"trans\">"
                            + lastVersion
                            + "</td>\n");
                    // Date of the last known email
                    MailMessage mm = MailMessage.getLatestMailMessage(nextPrj);
                    b.append(sp(in) + "<td class=\"trans\">"
                            + ((mm == null)?getLbl("l0051"):mm.getSendDate())
                            + "</td>\n");
                    // ID of the last known bug entry
                    Bug bug = Bug.getLastUpdate(nextPrj);
                    b.append(sp(in) + "<td class=\"trans\">"
                            + ((bug == null)?getLbl("l0051"):bug.getBugID())
                            + "</td>\n");
                    // Evaluation state
                    String evalState = getLbl("project_not_evaluated");
                    if (nextPrj.isEvaluated()) {
                    	evalState = getLbl("project_is_evaluated");
                    }
                    b.append(sp(in) + "<td class=\"trans\">"
                            + evalState
                            + "</td>\n");
                    
                    // Cluster node
                    String nodename = null;
                    if (null != nextPrj.getClusternode()) {
                        nodename = nextPrj.getClusternode().getName();
                    } else {
                        nodename = "(local)";
                    }
                    b.append(sp(in) + "<td class=\"trans\">" + nodename + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                    if ((selected) && (metrics.isEmpty() == false)) {
                        showLastAppliedVersion(nextPrj, metrics, b);
                    }
                }
            }
            //----------------------------------------------------------------
            // Tool-bar
            //----------------------------------------------------------------
            addToolBar(selProject,b,in);

            //----------------------------------------------------------------
            // Close the table
            //----------------------------------------------------------------
            b.append(sp(--in) + "</tbody>\n");
            b.append(sp(--in) + "</table>\n");
            b.append(sp(--in) + "</fieldset>\n");
        }

        // ===============================================================
        // INPUT FIELDS
        // ===============================================================
        addHiddenFields(selProject,b,in);

        // ===============================================================
        // Close the form
        // ===============================================================
        b.append(sp(--in) + "</form>\n");
    }


    protected void addHiddenFields(StoredProject selProject,
            StringBuilder b,
            long in) {
        // "Action type" input field
        b.append(sp(in) + "<input type='hidden' id='" + REQ_PAR_ACTION + 
                "' name='" + REQ_PAR_ACTION + "' value=''>\n");
        // "Project Id" input field
        b.append(sp(in) + "<input type='hidden' id='" + REQ_PAR_PROJECT_ID +
                "' name='" + REQ_PAR_PROJECT_ID +
                "' value='" + ((selProject != null) ? selProject.getId() : "") +
                "'>\n");
        // "Plug-in hashcode" input field
        b.append(sp(in) + "<input type='hidden' id='" + REQ_PAR_SYNC_PLUGIN +
                "' name='" + REQ_PAR_SYNC_PLUGIN + 
                "' value=''>\n");
    }
    
    protected void addToolBar(StoredProject selProject,
            StringBuilder b,
            long in) {
        b.append(sp(in++) + "<tr class=\"subhead\">\n");
        b.append(sp(in++) + "<td>View</td><td colspan=\"6\">\n");
        // Refresh button
        b.append(sp(in) + "<input type=\"button\"" + " class=\"install\"" + " style=\"width: 100px;\"" + " value=\"" + getLbl("l0008") + "\"" + " onclick=\"javascript:" + "window.location='/projects" + ((selProject != null)
                ? "?" + REQ_PAR_PROJECT_ID + "=" + selProject.getId()
                : "") + "';\"" + ">");
        b.append("</td></tr><tr class=\"subhead\"><td>Manage</td><td colspan='6'>\n");
        // Add project button
        b.append(sp(in) + "<input type=\"button\"" + " class=\"install\"" + " style=\"width: 100px;\"" + " value=\"" + getLbl("add_project") + "\"" + " onclick=\"javascript:" + "document.getElementById('" + REQ_PAR_ACTION + "').value='" + ACT_REQ_ADD_PROJECT + "';" + SUBMIT + "\">\n");
        // Remove project button
        b.append(sp(in) + "<input type=\"button\"" + " class=\"install\"" + " style=\"width: 100px;\"" + " value=\"" + getLbl("l0059") + "\"" + " onclick=\"javascript:" + "document.getElementById('" + REQ_PAR_ACTION + "').value='" + ACT_REQ_REM_PROJECT + "';" + SUBMIT + "\"" + ((selProject != null) ? "" : " disabled") + ">");
        b.append("</td></tr><tr class='subhead'><td>Update</td><td colspan='4'>\n");
        
        if (selProject != null) {
        	// RENG: the ternary operator can only take one side because of the if.
            b.append(sp(in) + "<select name=\"" + REQ_PAR_UPD + "\" id=\"" + REQ_PAR_UPD + "\" " + ((selProject != null) ? "" : " disabled=\"disabled\"") + ">\n");
            b.append(sp(in) + "<optgroup label=\"Import Stage\">");
            for (Updater u : getUpdaters(selProject, UpdaterStage.IMPORT)) {
                b.append("<option value=\"").append(u.mnem()).append("\">").append(u.descr()).append("</option>");
            }
            b.append(sp(in) + "</optgroup>");
            b.append(sp(in) + "<optgroup label=\"Parse Stage\">");
            for (Updater u : getUpdaters(selProject, UpdaterStage.PARSE)) {
                b.append("<option value=\"").append(u.mnem()).append("\">").append(u.descr()).append("</option>");
            }
            b.append(sp(in) + "</optgroup>");
            b.append(sp(in) + "<optgroup label=\"Inference Stage\">");
            for (Updater u : getUpdaters(selProject, UpdaterStage.INFERENCE)) {
                b.append("<option value=\"").append(u.mnem()).append("\">").append(u.descr()).append("</option>");
            }
            b.append(sp(in) + "</optgroup>");
            b.append(sp(in) + "<optgroup label=\"Default Stage\">");
            for (Updater u : getUpdaters(selProject, UpdaterStage.DEFAULT)) {
                b.append("<option value=\"").append(u.mnem()).append("\">").append(u.descr()).append("</option>");
            }
            b.append(sp(in) + "</optgroup>");
            b.append(sp(in) + "</select>");
        }

        // Trigger updater
        b.append(sp(in) + "<input type=\"button\" class=\"install\" value=\"Run Updater\" onclick=\"javascript:document.getElementById('" + REQ_PAR_ACTION + "').value='" + ACT_CON_UPD + "';" + SUBMIT + "\"" + ((selProject != null)? "" : " disabled") + ">\n");
        // Trigger all updates
        b.append(sp(in) + "<input type=\"button\"" + " class=\"install\"" + " value=\"Run All Updaters\" onclick=\"javascript:document.getElementById('" + REQ_PAR_ACTION + "').value='" + ACT_CON_UPD_ALL + "';" + SUBMIT + "\"" + (((selProject != null))
                ? "" : " disabled") + ">\n");
        b.append(sp(--in) + "</td>\n");
        b.append(sp(--in) + "<td colspan=\"2\" align=\"right\">\n");
     // Trigger updates on host
        b.append(sp(in) + "<input type=\"button\"" + " class=\"install\" value=\"Update all on "+ getClusterNodeName() +"\"" + " onclick=\"javascript:" + "document.getElementById('" + REQ_PAR_ACTION + "').value='" + ACT_CON_UPD_ALL_NODE + "';" + SUBMIT + "\">\n");
        b.append(sp(--in) + "</td>\n");
        b.append(sp(--in) + "</tr>\n");
    }

	protected Set<Updater> getUpdaters(StoredProject selProject,
			UpdaterStage importStage) {
		return sobjUpdater.getUpdaters(selProject, importStage);
	}

	protected String getClusterNodeName() {
		return sobjClusterNode.getClusterNodeName();
	}
    
    protected void showLastAppliedVersion(
            StoredProject project,
            Collection<PluginInfo> metrics,
            StringBuilder b) {
        for(PluginInfo m : metrics) {
            if (m.installed) {
                b.append("<tr>\n");
                b.append(sp(1) + "<td colspan=\"7\""
                        + " class=\"noattr\">\n"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Synchronise\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + REQ_PAR_SYNC_PLUGIN + "').value='"
                        + m.getHashcode() + "';"
                        + SUBMIT + "\""
                        + "/>"
                        + "&nbsp;"
                        + m.getPluginName()
                        + "</td>\n");
                b.append("</tr>\n");
            }
        }
    }

    protected void addHeaderRow(StringBuilder b, long in) {
        //----------------------------------------------------------------
        // Create the header row
        //----------------------------------------------------------------
    	
    	// RENG: This method opens table, but doesn't close it. Move this out?
        b.append(sp(in++) + "<table>\n");
        b.append(sp(in++) + "<thead>\n");
        b.append(sp(in++) + "<tr class=\"head\">\n");
        b.append(sp(in) + "<td class='head'  style='width: 10%;'>"
                + getLbl("l0066")
                + "</td>\n");
        b.append(sp(in) + "<td class='head' style='width: 35%;'>"
                + getLbl("l0067")
                + "</td>\n");
        b.append(sp(in) + "<td class='head' style='width: 15%;'>"
                + getLbl("l0068")
                + "</td>\n");
        b.append(sp(in) + "<td class='head' style='width: 15%;'>"
                + getLbl("l0069")
                + "</td>\n");
        b.append(sp(in) + "<td class='head' style='width: 15%;'>"
                + getLbl("l0070")
                + "</td>\n");
        b.append(sp(in) + "<td class='head' style='width: 10%;'>"
                + getLbl("l0071")
                + "</td>\n");
        b.append(sp(in) + "<td class='head' style='width: 10%;'>"
                + getLbl("l0073")
                + "</td>\n");
        b.append(sp(--in) + "</tr>\n");
        b.append(sp(--in) + "</thead>\n");
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

