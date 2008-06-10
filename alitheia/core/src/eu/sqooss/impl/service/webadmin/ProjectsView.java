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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.db.InvocationRule;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.InvocationRule.ActionType;
import eu.sqooss.service.db.InvocationRule.ScopeType;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.updater.UpdaterService.UpdateTarget;

public class ProjectsView extends AbstractView{

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

        // Get the required resource bundles
        ResourceBundle resLbl = getLabelsBundle(req.getLocale());
        ResourceBundle resErr = getErrorsBundle(req.getLocale());
        ResourceBundle resMsg = getMessagesBundle(req.getLocale());

        // Request parameters
        String reqParAction        = "action";
        String reqParProjectId     = "projectId";
        // Recognized "action" parameter's values
        String actReqAddProject    = "reqAddProject";
        String actReqRemProject    = "reqRemProject";
        String actConRemProject    = "conRemProject";
        String actConUpdAll        = "conUpdateAll";
        String actConUpdCode       = "conUpdateCode";
        String actConUpdMail       = "conUpdateMail";
        String actConUpdBugs       = "conUpdateBugs";
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
            reqValAction = req.getParameter(reqParAction);
            if (reqValAction == null) {
                reqValAction = "";
            };

            // Retrieve the selected user's DAO (if any)
            reqValProjectId = fromString(req.getParameter(reqParProjectId));
            if (reqValProjectId != null) {
                selProject = sobjDB.findObjectById(
                        StoredProject.class, reqValProjectId);
            }

            // ---------------------------------------------------------------
            // Remove project
            // ---------------------------------------------------------------
            if (reqValAction.equals(actConRemProject)) {
                if (selProject != null) {
                    // Delete any associated invocation rules first
                    HashMap<String,Object> properties =
                        new HashMap<String, Object>();
                    properties.put("project", selProject);
                    List<?> assosRules = sobjDB.findObjectsByProperties(
                            InvocationRule.class, properties);
                    if ((assosRules != null) && (assosRules.size() > 0)) {
                        for (Object nextDAO: assosRules) {
                            InvocationRule.deleteInvocationRule(
                                    sobjDB, compMA, (InvocationRule) nextDAO);
                        }
                    }
                    // Delete the selected project
                    if (sobjDB.deleteRecord(selProject)) {
                        selProject = null;
                    }
                    else {
                        e.append(sp(in) + resErr.getString("e0033")
                                + " " + resMsg.getString("m0001")
                                + "<br/>\n");
                    }
                }
                else {
                    e.append(sp(in) + resErr.getString("e0034")
                            + "<br/>\n");
                }
            }
            // ---------------------------------------------------------------
            // Trigger code update
            // ---------------------------------------------------------------
            else if (reqValAction.equals(actConUpdCode)) {
                if (sobjUpdater.update(
                        selProject, UpdateTarget.CODE, null) == false) {
                    e.append(sp(in) + resErr.getString("e0035")
                            + " " + resMsg.getString("m0010")
                            + "<br/>\n");
                }
            }
            // ---------------------------------------------------------------
            // Trigger mailing list(s) update
            // ---------------------------------------------------------------
            else if (reqValAction.equals(actConUpdMail)) {
                if (sobjUpdater.update(
                        selProject, UpdateTarget.MAIL, null) == false) {
                    e.append(sp(in) + resErr.getString("e0036")
                            + " " + resMsg.getString("m0010")
                            + "<br/>\n");
                }
            }
            // ---------------------------------------------------------------
            // Trigger bugs list(s) update
            // ---------------------------------------------------------------
            else if (reqValAction.equals(actConUpdBugs)) {
                if (sobjUpdater.update(
                        selProject, UpdateTarget.BUGS, null) == false) {
                    e.append(sp(in) + resErr.getString("e0037")
                            + " " + resMsg.getString("m0010")
                            + "<br/>\n");
                }
            }
            // ---------------------------------------------------------------
            // Trigger update on all resources for that project
            // ---------------------------------------------------------------
            else if (reqValAction.equals(actConUpdAll)) {
                if (sobjUpdater.update(
                        selProject, UpdateTarget.ALL, null) == false) {
                    e.append(sp(in) + resErr.getString("e0038")
                            + " " + resMsg.getString("m0010")
                            + "<br/>\n");
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
        // "Add project" editor
        // ===================================================================
        if (reqValAction.equals(actReqAddProject)) {
            
        }
        // ===================================================================
        // "Delete project" confirmation view
        // ===================================================================
        else if ((reqValAction.equals(actReqRemProject))
                && (selProject != null)) {
            b.append(sp(in++) + "<fieldset>\n");
            b.append(sp(in) + "<legend>" + resLbl.getString("l0059")
                    + ": " + selProject.getName()
                    + "</legend>\n");
            b.append(sp(in++) + "<table class=\"borderless\">");
            // Confirmation message
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in) + "<td class=\"borderless\">"
                    + "<b>" + resMsg.getString("m0007") + "</b>"
                    + "</td>\n");
            // Affected invocation rules
            HashMap<String,Object> properties = new HashMap<String, Object>();
            properties.put("project", selProject);
            List<?> affectedRules = sobjDB.findObjectsByProperties(
                    InvocationRule.class, properties);
            if ((affectedRules != null) && (affectedRules.size() > 0)) {
                b.append(sp(in++) + "<tr>\n");
                b.append(sp(in) + "<td class=\"borderless\">"
                        + resMsg.getString("m0008") + ": "
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
                    + " value=\"" + resLbl.getString("l0006") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + reqParAction + "').value='"
                    + actConRemProject + "';"
                    + "document.projects.submit();\">\n");
            // Cancel button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + resLbl.getString("l0004") + "\""
                    + " onclick=\"javascript:"
                    + "document.projects.submit();\">\n");
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
                    + resLbl.getString("l0072")
                    + "</legend>\n");

            //----------------------------------------------------------------
            // Create the header row
            //----------------------------------------------------------------
            b.append(sp(in++) + "<table>\n");
            b.append(sp(in++) + "<thead>\n");
            b.append(sp(in++) + "<tr class=\"head\">\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 10%;\">"
                    + resLbl.getString("l0066")
                    + "</td>\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 35%;\">"
                    + resLbl.getString("l0067")
                    + "</td>\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 15%;\">"
                    + resLbl.getString("l0068")
                    + "</td>\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 15%;\">"
                    + resLbl.getString("l0069")
                    + "</td>\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 15%;\">"
                    + resLbl.getString("l0070")
                    + "</td>\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 10%;\">"
                    + resLbl.getString("l0071")
                    + "</td>\n");
            b.append(sp(--in) + "</tr>\n");
            b.append(sp(--in) + "</thead>\n");

            if (projects.isEmpty()) {
                b.append(sp(in++) + "<tr>\n");
                b.append(sp(in) + "<td colspan=\"6\" class=\"noattr\">\n"
                        + resMsg.getString("m0009")
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
                            + reqParProjectId + "').value='"
                            + ((selected) ? "" : nextPrj.getId())
                            + "';"
                            + "document.projects.submit();\""
                            + ">\n");
                    // Project Id
                    b.append(sp(in) + "<td class=\"trans\">"
                            + nextPrj.getId()
                            + "</td>\n");
                    // Project name
                    b.append(sp(in) + "<td class=\"trans\">"
                            + "<img src=\"/edit.png\" alt=\"[Edit]\"/>"
                            + "&nbsp;"
                            + nextPrj.getName()
                            + "</td>\n");
                    // Last project version
                    String lastVersion = resLbl.getString("l0051");
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
                            + resLbl.getString("l0051")
                            + "</td>\n");
                    // Date of the last known bug entry
                    b.append(sp(in) + "<td class=\"trans\">"
                            + resLbl.getString("l0051")
                            + "</td>\n");
                    // Evaluation state
                    b.append(sp(in) + "<td class=\"trans\">"
                            + ((nextPrj.getEvaluationMarks().isEmpty())
                                    ? resLbl.getString("l0007")
                                            : resLbl.getString("l0006"))
                                            + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                    if ((selected) && (metrics.isEmpty() == false)) {
                        for(PluginInfo m : metrics) {
                            if (m.installed) {
                                ProjectVersion lastVer =
                                    compMA.getLastAppliedVersion(
                                            sobjPA.getPlugin(m), nextPrj);
                                b.append("\n</td>\n");
                                b.append(sp(in++) + "<tr>\n");
                                b.append(sp(in) + "<td colspan=\"6\""
                                        + " class=\"noattr\">\n"
                                        + m.getPluginName()
                                        + ": "
                                        + ((lastVer != null)
                                                ? resLbl.getString("l0065")
                                                        + " "
                                                        + lastVer.getVersion()
                                                : resLbl.getString("l0051"))
                                        + "</td>\n");
                            }
                        }
                    }
                }
            }
            //----------------------------------------------------------------
            // Tool-bar
            //----------------------------------------------------------------
            b.append(sp(in++) + "<tr class=\"subhead\">\n");
            b.append(sp(in++) + "<td colspan=\"6\">\n");
            // Refresh button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + resLbl.getString("l0008") + "\""
                    + " onclick=\"javascript:"
                    + "window.location='/projects"
                    + ((selProject != null)
                            ? "?" + reqParProjectId + "="
                                    + selProject.getId()
                            : "")
                    +"';\""
                    + ">\n");
            // Add project button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + resLbl.getString("l0060") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + reqParAction + "').value='"
                    + actReqAddProject + "';"
                    + "document.projects.submit();\""
                    + " disabled"
                    + ">\n");
            // Remove project button
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + resLbl.getString("l0059") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + reqParAction + "').value='"
                    + actReqRemProject + "';"
                    + "document.projects.submit();\""
                    + ((selProject != null) ? "" : " disabled")
                    + ">\n");
            // Trigger source update
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + resLbl.getString("l0061") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + reqParAction + "').value='"
                    + actConUpdCode + "';"
                    + "document.projects.submit();\""
                    + (((selProject != null) && (sobjUpdater.isUpdateRunning(
                            selProject, UpdateTarget.CODE) == false))
                            ? "" : " disabled")
                    + ">\n");
            // Trigger mailing list update
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + resLbl.getString("l0062") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + reqParAction + "').value='"
                    + actConUpdMail + "';"
                    + "document.projects.submit();\""
                    + (((selProject != null) && (sobjUpdater.isUpdateRunning(
                            selProject, UpdateTarget.MAIL) == false))
                            ? "" : " disabled")
                    + ">\n");
            // Trigger bugs list update
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + resLbl.getString("l0063") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + reqParAction + "').value='"
                    + actConUpdBugs + "';"
                    + "document.projects.submit();\""
                    + (((selProject != null) && (sobjUpdater.isUpdateRunning(
                            selProject, UpdateTarget.BUGS) == false))
                            ? "" : " disabled")
                    + ">\n");
            // Trigger all updates
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"" + resLbl.getString("l0064") + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + reqParAction + "').value='"
                    + actConUpdAll + "';"
                    + "document.projects.submit();\""
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
                + " id=\"" + reqParAction + "\""
                + " name=\"" + reqParAction + "\""
                + " value=\"\">\n");
        // "Project Id" input field
        b.append(sp(in) + "<input type=\"hidden\""
                + " id=\"" + reqParProjectId + "\""
                + " name=\"" + reqParProjectId + "\""
                + " value=\""
                + ((selProject != null) ? selProject.getId() : "")
                + "\">\n");

        // ===============================================================
        // Close the form
        // ===============================================================
        b.append(sp(--in) + "</form>\n");

        // Close the DB session
        sobjDB.commitDBSession();

        return b.toString();
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
