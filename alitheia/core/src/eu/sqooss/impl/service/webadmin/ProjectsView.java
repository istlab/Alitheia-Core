/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 *
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
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.ClusterNodeProject;
import eu.sqooss.service.db.InvocationRule;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.updater.UpdaterService.UpdateTarget;

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
    private static String ACT_CON_UPD_CODE      = "conUpdateCode";
    private static String ACT_CON_UPD_MAIL      = "conUpdateMail";
    private static String ACT_CON_UPD_BUGS      = "conUpdateBugs";

    // Servlet parameters
    private static String REQ_PAR_ACTION        = "action";
    private static String REQ_PAR_PROJECT_ID    = "projectId";
    private static String REQ_PAR_PRJ_NAME      = "projectName";
    private static String REQ_PAR_PRJ_WEB       = "projectHomepage";
    private static String REQ_PAR_PRJ_CONT      = "projectContact";
    private static String REQ_PAR_PRJ_BUG       = "projectBL";
    private static String REQ_PAR_PRJ_MAIL      = "projectML";
    private static String REQ_PAR_PRJ_CODE      = "projectSCM";
    private static String REQ_PAR_SYNC_PLUGIN   = "reqParSyncPlugin";

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
    public static String render(HttpServletRequest req) {
        // Stores the assembled HTML content
        StringBuilder b = new StringBuilder("\n");
        // Stores the accumulated error messages
        StringBuilder e = new StringBuilder();
        // Indentation spacer
        long in = 6;

        // Create a DB session
        sobjDB.startDBSession();

        // Initialize the resource bundles with the request's locale
        initResources(req.getLocale());

        // Request values
        String reqValAction        = "";
        Long   reqValProjectId     = null;
        String reqValPrjName       = null;
        String reqValPrjWeb        = null;
        String reqValPrjContact    = null;
        String reqValPrjBug        = null;
        String reqValPrjMail       = null;
        String reqValPrjCode       = null;
        String reqValSyncPlugin    = null;

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
            if (reqValAction == null) {
                reqValAction = "";
            };

            // Retrieve the selected project's DAO (if any)
            reqValProjectId = fromString(req.getParameter(REQ_PAR_PROJECT_ID));
            if (reqValProjectId != null) {
                selProject = sobjDB.findObjectById(
                        StoredProject.class, reqValProjectId);
            }

            // Retrieve the selected plug-in's hash-code
            reqValSyncPlugin = req.getParameter(REQ_PAR_SYNC_PLUGIN);

            // ---------------------------------------------------------------
            // Add project
            // ---------------------------------------------------------------
            if (reqValAction.equals(ACT_CON_ADD_PROJECT)) {
                // Retrieve the specified project properties
                reqValPrjName = req.getParameter(REQ_PAR_PRJ_NAME);
                reqValPrjWeb = req.getParameter(REQ_PAR_PRJ_WEB);
                reqValPrjContact = req.getParameter(REQ_PAR_PRJ_CONT);
                reqValPrjBug = req.getParameter(REQ_PAR_PRJ_BUG);
                reqValPrjMail = req.getParameter(REQ_PAR_PRJ_MAIL);
                reqValPrjCode = req.getParameter(REQ_PAR_PRJ_CODE);
                // Checks the validity of the project properties
                boolean valid = true;
                if (checkProjectName(reqValPrjName) == false) {
                    valid = false;
                    e.append(sp(in) + "Invalid project name!" + "<br/>\n");
                }
                if (checkEmail(reqValPrjContact) == false) {
                    valid = false;
                    e.append(sp(in) + "Invalid contact e-mail!" + "<br/>\n");
                }
                if (checkUrl(reqValPrjWeb, "http|https") == false) {
                    valid = false;
                    e.append(sp(in) + "Invalid homepage URL!" + "<br/>\n");
                }
                if ((reqValPrjBug != null)
                        && (reqValPrjBug.length() > 0)) {
                    if (checkUrl(reqValPrjBug, "http|https") == false) {
                        valid = false;
                        e.append(sp(in) + "Invalid bug database URL!"
                                + "<br/>\n");
                    }
                }
                else if ((reqValPrjMail != null)
                        && (reqValPrjMail.length() > 0)) {
                    if (checkUrl(reqValPrjMail, "maildir") == false) {
                        valid = false;
                        e.append(sp(in) + "Invalid mailing list URL!"
                                + "<br/>\n");
                    }
                }
                else if ((reqValPrjCode != null)
                        && (reqValPrjCode.length() > 0)) {
                    if (checkUrl(reqValPrjCode, "http|https|svn|file") == false) {
                        valid = false;
                        e.append(sp(in) + "Invalid source code URL!"
                                + "<br/>\n");
                    }
                }
                else {
                    valid = false;
                    e.append(sp(in)
                            + "At least one resource source must be specified!"
                            + "<br/>\n");
                }
                // Proceed upon valid project properties
                if (valid) {
                    // Check if a project with the same name already exists
                    HashMap<String, Object> params =
                        new HashMap<String, Object>();
                    params.put("name", reqValPrjName);
                    if (sobjDB.findObjectsByProperties(
                            StoredProject.class, params).size() > 1) {
                        e.append(sp(in) + getErr("prj_exists")
                                + "<br/>\n");
                    }
                    // Create the new project's DAO
                    else {
                        StoredProject p = new StoredProject();
                        p.setName(reqValPrjName);
                        p.setWebsite(reqValPrjWeb);
                        p.setContact(reqValPrjContact);
                        p.setBugs(reqValPrjBug);
                        p.setMail(reqValPrjMail);
                        p.setRepository(reqValPrjCode);

                        // Try to add the DAO to the DB
                        if (sobjDB.addRecord(p) == true) {
                            selProject = p;
                            // register the new project in the TDS
                            sobjTDS.addAccessor(p.getId(), p.getName(), p.getBugs(), 
                                    p.getMail(), p.getRepository());
                        }
                        else {
                            e.append(sp(in)
                                    + getErr("prj_add_failed")
                                    + " " + getMsg("m0001")
                                    + "<br/>\n");
                        }
                    }
                }
                // Return to the "Add project" view upon failure
                if (e.length() > 0) {
                    reqValAction = ACT_REQ_ADD_PROJECT;
                }
            }
            // ---------------------------------------------------------------
            // Remove project
            // ---------------------------------------------------------------
            else if (reqValAction.equals(ACT_CON_REM_PROJECT)) {
                if (selProject != null) {
                	//Deleting large projects in the foreground is
                	//very slow
                    ProjectDeleteJob pdj = new ProjectDeleteJob(sobjCore, selProject);
                    try {
						sobjSched.enqueue(pdj);
					} catch (SchedulerException e1) {
						e.append(sp(in) + getErr("e0034")
	                            + "<br/>\n");
					}
                    selProject = null;
                }
                else {
                	e.append(sp(in) + getErr("e0034")
                            + "<br/>\n");
                }
            }
            // ---------------------------------------------------------------
            // Trigger code update
            // ---------------------------------------------------------------
            else if (reqValAction.equals(ACT_CON_UPD_CODE)) {
                if (sobjUpdater.update(
                        selProject, UpdateTarget.CODE, null) == false) {
                    e.append(sp(in) + getErr("e0035")
                            + " " + getMsg("try_again")
                            + "<br/>\n");
                }
            }
            // ---------------------------------------------------------------
            // Trigger mailing list(s) update
            // ---------------------------------------------------------------
            else if (reqValAction.equals(ACT_CON_UPD_MAIL)) {
                if (sobjUpdater.update(
                        selProject, UpdateTarget.MAIL, null) == false) {
                    e.append(sp(in) + getErr("e0036")
                            + " " + getMsg("try_again")
                            + "<br/>\n");
                }
            }
            // ---------------------------------------------------------------
            // Trigger bugs list(s) update
            // ---------------------------------------------------------------
            else if (reqValAction.equals(ACT_CON_UPD_BUGS)) {
                if (sobjUpdater.update(
                        selProject, UpdateTarget.BUGS, null) == false) {
                    e.append(sp(in) + getErr("e0037")
                            + " " + getMsg("try_again")
                            + "<br/>\n");
                }
            }
            // ---------------------------------------------------------------
            // Trigger update on all resources for that project
            // ---------------------------------------------------------------
            else if (reqValAction.equals(ACT_CON_UPD_ALL)) {
                if (sobjUpdater.update(
                        selProject, UpdateTarget.ALL, null) == false) {
                    e.append(sp(in) + getErr("e0038")
                            + " " + getMsg("try_again")
                            + "<br/>\n");
                }
            }
            // ---------------------------------------------------------------
            // Trigger synchronize on the selected plug-in for that project
            // ---------------------------------------------------------------
            if ((reqValSyncPlugin != null) && (selProject != null)) {
                PluginInfo pInfo = sobjPA.getPluginInfo(reqValSyncPlugin);
                if (pInfo != null) {
                    AlitheiaPlugin pObj = sobjPA.getPlugin(pInfo);
                    if (pObj != null) {
                        compMA.syncMetric(pObj, selProject);
                        sobjLogger.debug("Syncronise plugin ("
                                + pObj.getName()
                                + ") on project ("
                                + selProject.getName() + ").");
                    }
                }
            }
        }

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
        List<StoredProject> projects = sobjDB.findObjectsByProperties(
                StoredProject.class, new HashMap<String, Object>());
        Collection<PluginInfo> metrics = sobjPA.listPlugins();

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
                    "Homepage", selProject.getWebsite(), in));
            b.append(normalInfoRow(
                    "Contact e-mail", selProject.getContact(), in));
            b.append(normalInfoRow(
                    "Bug database", selProject.getBugs(), in));
            b.append(normalInfoRow(
                    "Mailing list", selProject.getMail(), in));
            b.append(normalInfoRow(
                    "Source code", selProject.getRepository(), in));

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
            b.append(sp(in++) + "<fieldset>\n");
            b.append(sp(in) + "<legend>"
                    + getLbl("add_project")
                    + "</legend>\n");
            b.append(sp(in++) + "<span style=\"width: 100%;\">\n");
            b.append(sp(in++) + "<span style=\"width: 40%; float: left;\">\n");
            b.append(sp(in++) + "<table class=\"borderless\">\n");
            // Create the input fields
            b.append(normalInputRow(
                    "Project name", REQ_PAR_PRJ_NAME, reqValPrjName, in));
            b.append(normalInputRow(
                    "Homepage", REQ_PAR_PRJ_WEB, reqValPrjWeb, in));
            b.append(normalInputRow(
                    "Contact e-mail", REQ_PAR_PRJ_CONT, reqValPrjContact, in));
            b.append(normalInputRow(
                    "Bug database", REQ_PAR_PRJ_BUG, reqValPrjBug, in));
            b.append(normalInputRow(
                    "Mailing list", REQ_PAR_PRJ_MAIL, reqValPrjMail, in));
            b.append(normalInputRow(
                    "Source code", REQ_PAR_PRJ_CODE, reqValPrjCode, in));

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
                    + " value=\"" + getLbl("l0003") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + REQ_PAR_ACTION + "').value='"
                    + ACT_CON_ADD_PROJECT + "';"
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
            b.append(sp(--in) + "</table>\n");
            // Context help
            b.append(sp(--in) + "</span>\n");
            b.append(sp(in++) + "<span"
                    + " style=\"width: 60%; float: right;;\">\n");
            b.append(sp(in++) + "<fieldset"
                    + " style=\"margin-top: -5px; background-color: white;\""
                    + ">\n");
            b.append(sp(in) + "<legend>" + "Help" + "</legend>\n");
            b.append(sp(in) + getMsg("project_help") + "\n");
            b.append(sp(--in) + "</fieldset>\n");
            b.append(sp(--in) + "</span>\n");
            b.append(sp(--in) + "</span>\n");
            b.append(sp(--in) + "</fieldset>\n");
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
            // Affected invocation rules
            HashMap<String,Object> properties = new HashMap<String, Object>();
            properties.put("project", selProject);
            List<?> affectedRules = sobjDB.findObjectsByProperties(
                    InvocationRule.class, properties);
            if ((affectedRules != null) && (affectedRules.size() > 0)) {
                b.append(sp(in++) + "<tr>\n");
                b.append(sp(in) + "<td class=\"borderless\">"
                        + getMsg("delete_assoc_rules") + ": "
                        + affectedRules.size()
                        + "</td>\n");
            }
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
            // Create the field-set
            b.append(sp(in++) + "<fieldset>\n");
            b.append(sp(in) + "<legend>"
                    + getLbl("l0072")
                    + "</legend>\n");

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
                    if (StoredProject.getLastProjectVersion(nextPrj) != null) {
                        lastVersion = new Long(
                                StoredProject.getLastProjectVersion(
                                        nextPrj).getVersion()).toString();
                    }
                    b.append(sp(in) + "<td class=\"trans\">"
                            + lastVersion
                            + "</td>\n");
                    // Date of the last known email
                    b.append(sp(in) + "<td class=\"trans\">"
                            + getLbl("l0051")
                            + "</td>\n");
                    // Date of the last known bug entry
                    b.append(sp(in) + "<td class=\"trans\">"
                            + getLbl("l0051")
                            + "</td>\n");
                    // Evaluation state
                    String evalState = getLbl("l0007");
                    if ((nextPrj.getEvaluationMarks() != null)
                            && (nextPrj.getEvaluationMarks().isEmpty()
                                    == false)) {
                        evalState = getLbl("l0006");
                    }
                    b.append(sp(in) + "<td class=\"trans\">"
                            + evalState
                            + "</td>\n");
                    
                    //Cluster node
                	String nodename = null;
                	if (null != ClusterNodeProject.getProjectAssignment(nextPrj)) {
                		if (null != ClusterNodeProject.getProjectAssignment(nextPrj).getNode()) {
                			nodename = ClusterNodeProject.getProjectAssignment(nextPrj).getNode().getName();
                		}
                	}
                	if (null == nodename) {
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
            b.append(sp(in++) + "<tr class=\"subhead\">\n");
            b.append(sp(in++) + "<td colspan=\"7\">\n");
            // Refresh button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("l0008") + "\""
                    + " onclick=\"javascript:"
                    + "window.location='/projects"
                    + ((selProject != null)
                            ? "?" + REQ_PAR_PROJECT_ID + "="
                                    + selProject.getId()
                            : "")
                    +"';\""
                    + ">\n");
            // Add project button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("add_project") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + REQ_PAR_ACTION + "').value='"
                    + ACT_REQ_ADD_PROJECT + "';"
                    + SUBMIT + "\">\n");
            // Remove project button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("l0059") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + REQ_PAR_ACTION + "').value='"
                    + ACT_REQ_REM_PROJECT + "';"
                    + SUBMIT + "\""
                    + ((selProject != null) ? "" : " disabled")
                    + ">\n");
            // Trigger source update
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("l0061") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + REQ_PAR_ACTION + "').value='"
                    + ACT_CON_UPD_CODE + "';"
                    + SUBMIT + "\""
                    + (((selProject != null) && (sobjUpdater.isUpdateRunning(
                            selProject, UpdateTarget.CODE) == false))
                            ? "" : " disabled")
                    + ">\n");
            // Trigger mailing list update
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("l0062") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + REQ_PAR_ACTION + "').value='"
                    + ACT_CON_UPD_MAIL + "';"
                    + SUBMIT + "\""
                    + (((selProject != null) && (sobjUpdater.isUpdateRunning(
                            selProject, UpdateTarget.MAIL) == false))
                            ? "" : " disabled")
                    + ">\n");
            // Trigger bugs list update
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("l0063") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + REQ_PAR_ACTION + "').value='"
                    + ACT_CON_UPD_BUGS + "';"
                    + SUBMIT + "\""
                    + (((selProject != null) && (sobjUpdater.isUpdateRunning(
                            selProject, UpdateTarget.BUGS) == false))
                            ? "" : " disabled")
                    + ">\n");
            // Trigger all updates
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + getLbl("l0064") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + REQ_PAR_ACTION + "').value='"
                    + ACT_CON_UPD_ALL + "';"
                    + SUBMIT + "\""
                    + (((selProject != null) && (sobjUpdater.isUpdateRunning(
                            selProject, UpdateTarget.ALL) == false))
                            ? "" : " disabled")
                    + ">\n");
            b.append(sp(--in) + "</td>\n");
            b.append(sp(--in) + "</tr>\n");
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
        // "Action type" input field
        b.append(sp(in) + "<input type=\"hidden\""
                + " id=\"" + REQ_PAR_ACTION + "\""
                + " name=\"" + REQ_PAR_ACTION + "\""
                + " value=\"\">\n");
        // "Project Id" input field
        b.append(sp(in) + "<input type=\"hidden\""
                + " id=\"" + REQ_PAR_PROJECT_ID + "\""
                + " name=\"" + REQ_PAR_PROJECT_ID + "\""
                + " value=\""
                + ((selProject != null) ? selProject.getId() : "")
                + "\">\n");
        // "Plug-in hashcode" input field
        b.append(sp(in) + "<input type=\"hidden\""
                + " id=\"" + REQ_PAR_SYNC_PLUGIN + "\""
                + " name=\"" + REQ_PAR_SYNC_PLUGIN + "\""
                + " value=\"\">\n");

        // ===============================================================
        // Close the form
        // ===============================================================
        b.append(sp(--in) + "</form>\n");

        // Close the DB session
        sobjDB.commitDBSession();

        return b.toString();
    }


    private static void showLastAppliedVersion(
            StoredProject project,
            Collection<PluginInfo> metrics,
            StringBuilder b) {
        for(PluginInfo m : metrics) {
            if (m.installed) {
                b.append("<tr>\n");
                b.append(sp(1) + "<td colspan=\"6\""
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
                        + ">"
                        + "&nbsp;"
                        + m.getPluginName()
                        + "</td>\n");
                b.append("</tr>\n");
            }
        }
    }

    private static void addHeaderRow(StringBuilder b, long in) {
        //----------------------------------------------------------------
        // Create the header row
        //----------------------------------------------------------------
        b.append(sp(in++) + "<table>\n");
        b.append(sp(in++) + "<thead>\n");
        b.append(sp(in++) + "<tr class=\"head\">\n");
        b.append(sp(in) + "<td class=\"head\""
                + " style=\"width: 10%;\">"
                + getLbl("l0066")
                + "</td>\n");
        b.append(sp(in) + "<td class=\"head\""
                + " style=\"width: 35%;\">"
                + getLbl("l0067")
                + "</td>\n");
        b.append(sp(in) + "<td class=\"head\""
                + " style=\"width: 15%;\">"
                + getLbl("l0068")
                + "</td>\n");
        b.append(sp(in) + "<td class=\"head\""
                + " style=\"width: 15%;\">"
                + getLbl("l0069")
                + "</td>\n");
        b.append(sp(in) + "<td class=\"head\""
                + " style=\"width: 15%;\">"
                + getLbl("l0070")
                + "</td>\n");
        b.append(sp(in) + "<td class=\"head\""
                + " style=\"width: 10%;\">"
                + getLbl("l0071")
                + "</td>\n");
        b.append(sp(in) + "<td class=\"head\""
                + " style=\"width: 10%;\">"
                + getLbl("l0073")
                + "</td>\n");
        b.append(sp(--in) + "</tr>\n");
        b.append(sp(--in) + "</thead>\n");
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

