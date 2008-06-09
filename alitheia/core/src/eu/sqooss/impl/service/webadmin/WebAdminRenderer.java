/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 by Paul J. Adams <paul.adams@siriusit.co.uk>
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.TDAccessor;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.updater.UpdaterService.UpdateTarget;
import eu.sqooss.service.util.StringUtils;
import eu.sqooss.service.webadmin.WebadminService;

/**
 * The WebAdminRender class provdies functions for rendering content
 * to be displayed within the WebAdmin interface.
 *
 * @author, Paul J. Adams <paul.adams@siriusit.co.uk>
 * @author, Boryan Yotov <b.yotov@prosyst.com>
 */
public class WebAdminRenderer  extends AbstractView {
    // Current time
    private static long startTime = new Date().getTime();

    public WebAdminRenderer(BundleContext bundlecontext, VelocityContext vc) {
        super(bundlecontext, vc);
    }

    public static String renderJobFailStats() {
        StringBuilder result = new StringBuilder();
        HashMap<String,Integer> fjobs = sobjSched.getSchedulerStats().getFailedJobTypes();
        result.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n");
        result.append("\t<thead>\n");
        result.append("\t\t<tr>\n");
        result.append("\t\t\t<td>Job Type</td>\n");
        result.append("\t\t\t<td>Num Jobs Failed</td>\n");
        result.append("\t\t</tr>\n");
        result.append("\t</thead>\n");
        result.append("\t<tbody>\n");

        String[] jobfailures = fjobs.keySet().toArray(new String[1]);
        for(String key : jobfailures) {
            result.append("\t\t<tr>\n\t\t\t<td>");
            result.append(key==null ? "No failures" : key);
            result.append("</td>\n\t\t\t<td>");
            result.append(key==null ? "&nbsp;" : fjobs.get(key));
            result.append("\t\t\t</td>\n\t\t</tr>");
        }
        result.append("\t</tbody>\n");
        result.append("</table>");
        return result.toString();
    }

    public static String renderWaitJobs() {
        StringBuilder result = new StringBuilder();
        Job[] jobs = sobjSched.getWaitQueue();
        result.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n");
        result.append("\t<thead>\n");
        result.append("\t\t<tr>\n");
        result.append("\t\t\t<td>Queue pos</td>\n");
        result.append("\t\t\t<td>Job Type</td>\n");
        result.append("\t\t\t<td>Job depedencies</td>\n");
        result.append("\t\t</tr>\n");
        result.append("\t</thead>\n");
        result.append("\t<tbody>\n");

        int i = 0;
        for(Job j: jobs) {
            i++;
            result.append("\t\t<tr>\n\t\t\t<td>");
            result.append(i);
            result.append("</td>\n\t\t\t<td>");
            result.append(j.getClass().toString());
            result.append("</td>\n\t\t\t<td>");
            Iterator<Job> ji = j.dependencies().iterator();

            while(ji.hasNext()) {
                result.append(ji.next().getClass().toString());
                if(ji.hasNext())
                    result.append(",");
            }
            result.append("</td>\n\t\t\t<td>");

            result.append("\t\t\t</td>\n\t\t</tr>");
        }

        result.append("\t</tbody>\n");
        result.append("</table>");

        return result.toString();
    }

    /**
     * Creates and HTML table with information about the jobs that
     * failed and the recorded exceptions
     * @return
     */
    public static String renderFailedJobs() {
        StringBuilder result = new StringBuilder();
        Job[] jobs = sobjSched.getFailedQueue();
        result.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n");
        result.append("\t<thead>\n");
        result.append("\t\t<tr>\n");
        result.append("\t\t\t<td>Job Type</td>\n");
        result.append("\t\t\t<td>Exception type</td>\n");
        result.append("\t\t\t<td>Exception text</td>\n");
        result.append("\t\t\t<td>Exception backtrace</td>\n");
        result.append("\t\t</tr>\n");
        result.append("\t</thead>\n");
        result.append("\t<tbody>\n");

        if ((jobs != null) && (jobs.length > 0)) {
            for(Job j: jobs) {
                if (j == null) continue;
                result.append("\t\t<tr>\n\t\t\t<td>");
                if (j.getClass() != null) {
                    try {
                        result.append(j.getClass().getPackage().getName());
                        result.append(". " + j.getClass().getSimpleName());
                    }
                    catch (NullPointerException ex) {
                        result.append("<b>NA<b>");
                    }
                }
                else {
                    result.append("<b>NA<b>");
                }
                result.append("</td>\n\t\t\t<td>");
                Exception e = j.getErrorException();
                if (e != null) {
                    try {
                        result.append(e.getClass().getPackage().getName());
                        result.append(". " + e.getClass().getSimpleName());
                    }
                    catch (NullPointerException ex) {
                        result.append("<b>NA<b>");
                    }
                }
                else {
                    result.append("<b>NA</b>");
                }
                result.append("</td>\n\t\t\t<td>");
                try {
                    result.append(e.getMessage());
                }
                catch (NullPointerException ex) {
                    result.append("<b>NA<b>");
                }
                result.append("</td>\n\t\t\t<td>");
                if ((e != null)
                        && (e.getStackTrace() != null)) {
                    for(StackTraceElement m: e.getStackTrace()) {
                        if (m == null) continue;
                        result.append(m.getClassName());
                        result.append(". ");
                        result.append(m.getMethodName());
                        result.append("(), (");
                        result.append(m.getFileName());
                        result.append(":");
                        result.append(m.getLineNumber());
                        result.append(")<br/>");
                    }
                }
                else {
                    result.append("<b>NA</b>");
                }
                result.append("\t\t\t</td>\n\t\t</tr>");
            }
        }
        else {
            result.append ("<tr><td colspan=\"4\">No failed jobs.</td></tr>");
        }
        result.append("\t</tbody>\n");
        result.append("</table>");

        return result.toString();
    }

    public static String renderLogs() {
        String[] names = sobjLogManager.getRecentEntries();

        if ((names != null) && (names.length > 0)) {
            StringBuilder b = new StringBuilder();
            for (String s : names) {
                b.append("\t\t\t\t\t<li>" + StringUtils.makeXHTMLSafe(s) + "</li>\n");
            }

            return b.toString();
        } else {
            return "\t\t\t\t\t<li>&lt;none&gt;</li>\n";
        }
    }

    public static String getSchedulerDetails(String attribute) {
        if (attribute.equals("WAITING")) {
            return String.valueOf(sobjSched.getSchedulerStats().getWaitingJobs());
        }
        else if (attribute.equals("RUNNING")) {
            return String.valueOf(sobjSched.getSchedulerStats().getRunningJobs());
        }
        else if (attribute.equals("WORKER")) {
            return String.valueOf(sobjSched.getSchedulerStats().getWorkerThreads());
        }
        else if (attribute.equals("FAILED")) {
            return String.valueOf(sobjSched.getSchedulerStats().getFailedJobs());
        }
        else if (attribute.equals("TOTAL")) {
            return String.valueOf(sobjSched.getSchedulerStats().getTotalJobs());
        }

        return "";
    }

    /**
     * Returns a string representing the uptime of the Alitheia core
     * in dd:hh:mm:ss format
     */
    public static String getUptime() {
        long remainder;
        long currentTime = new Date().getTime();
        long timeRunning = currentTime - startTime;

        // Get the elapsed time in days, hours, mins, secs
        int days = new Long(timeRunning / 86400000).intValue();
        remainder = timeRunning % 86400000;
        int hours = new Long(remainder / 3600000).intValue();
        remainder = remainder % 3600000;
        int mins = new Long(remainder / 60000).intValue();
        remainder = remainder % 60000;
        int secs = new Long(remainder / 1000).intValue();

        return String.format("%d:%02d:%02d:%02d", days, hours, mins, secs);
    }

    public static String renderProjects(HttpServletRequest req) {
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
                    // Delete the selected project
                    if (sobjDB.deleteRecord(selProject)) {
                        selProject = null;
                    }
                    else {
                        e.append(sp(in) + "Can not remove this project!"
                                + " " + resMsg.getString("m0001")
                                + "<br/>\n");
                    }
                }
                else {
                    e.append(sp(in) + "You must select a project first!"
                            + "<br/>\n");
                }
            }
            // ---------------------------------------------------------------
            // Trigger code update
            // ---------------------------------------------------------------
            else if (reqValAction.equals(actConUpdCode)) {
                if (sobjUpdater.update(
                        selProject, UpdateTarget.CODE, null) == false) {
                    e.append(sp(in) + "Can not start a source code update!"
                            + " " + "Try again later!"
                            + "<br/>\n");
                }
            }
            // ---------------------------------------------------------------
            // Trigger mailing list(s) update
            // ---------------------------------------------------------------
            else if (reqValAction.equals(actConUpdMail)) {
                if (sobjUpdater.update(
                        selProject, UpdateTarget.MAIL, null) == false) {
                    e.append(sp(in) + "Can not start a mailing list update!"
                            + " " + "Try again later!"
                            + "<br/>\n");
                }
            }
            // ---------------------------------------------------------------
            // Trigger bugs list(s) update
            // ---------------------------------------------------------------
            else if (reqValAction.equals(actConUpdBugs)) {
                if (sobjUpdater.update(
                        selProject, UpdateTarget.BUGS, null) == false) {
                    e.append(sp(in) + "Can not start a bug list update!"
                            + " " + "Try again later!"
                            + "<br/>\n");
                }
            }
            // ---------------------------------------------------------------
            // Trigger update on all resources for that project
            // ---------------------------------------------------------------
            else if (reqValAction.equals(actConUpdAll)) {
                if (sobjUpdater.update(
                        selProject, UpdateTarget.ALL, null) == false) {
                    e.append(sp(in) + "Can not start a complete update!"
                            + " " + "Try again later!"
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
        // "Delete project" confirmation
        // ===================================================================
        else if ((reqValAction.equals(actReqRemProject))
                && (selProject != null)) {
            b.append(sp(in++) + "<fieldset>\n");
            b.append(sp(in) + "<legend>" + resLbl.getString("l0059")
                    + ": " + selProject.getName()
                    + "</legend>\n");
            b.append(sp(in++) + "<table class=\"borderless\">");
            // Group name
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in) + "<td class=\"borderless\">"
                    + "<b>" + resMsg.getString("m0007") + "</b>"
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
        // Projects list
        // ===================================================================
        else {
            // Create the field-set
            b.append(sp(in++) + "<fieldset>\n");
            b.append(sp(in) + "<legend>All projects</legend>\n");

            //----------------------------------------------------------------
            // Create the header row
            //----------------------------------------------------------------
            b.append(sp(in++) + "<table>\n");
            b.append(sp(in++) + "<thead>\n");
            b.append(sp(in++) + "<tr class=\"head\">\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 5%;\">"
                    + "Id</td>\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 35%;\">"
                    + "Name</td>\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 15%;\">"
                    + "Last Version</td>\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 15%;\">"
                    + "Last Email</td>\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 15%;\">"
                    + "Last Bug</td>\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 15%;\">"
                    + "Evaluated</td>\n");
            b.append(sp(--in) + "</tr>\n");
            b.append(sp(--in) + "</thead>\n");

            if (projects.isEmpty()) {
                b.append(sp(in++) + "<tr>\n");
                b.append(sp(in) + "<td colspan=\"6\" class=\"noattr\">\n"
                        + "No projects found."
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
                                                ? "at version" + " "
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
                    + " value=\"" + "Refresh" + "\""
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
                    + " value=\"" + "Add project" + "\""
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
                    + " value=\"" + "Delete project" + "\""
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
                    + " value=\"" + "Update source" + "\""
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
                    + " value=\"" + "Update emails" + "\""
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
                    + " value=\"" + "Update bugs" + "\""
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
                    + " value=\"" + "Update all" + "\""
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

//        if (projects == null || metrics == null) {
//            sobjDB.commitDBSession();
//            return "<b>No projects installed</b>";
//        }
//
//        b.append("<table border=\"1\">");
//        b.append("<tr>");
//        b.append("<td><b>Project</b></td>");
//
//        for(PluginInfo m : metrics) {
//            if(m.installed) {
//                b.append("<td><b>");
//                b.append(m.getPluginName());
//                b.append("</b></td>");
//            }
//        }
//        b.append("</tr>\n");
//
//        for (int i=0; i<projects.size(); i++) {
//            b.append("\t<tr>\n");
//            StoredProject p = projects.get(i);
//            b.append("\t\t<!--project--><td><font size=\"-2\"><b>");
//            b.append(p.getName());
//            b.append("</b> ([id=");
//            b.append(p.getId());
//            b.append("]) <br/>\nUpdate:");
//            for (String updTarget: UpdaterService.UpdateTarget.toStringArray()) {
//                b.append("<a href=\"updater?project=");
//                b.append(p.getName());
//                b.append("&target=");
//                b.append(updTarget);
//                b.append("\" title=\"Tell the updater to check for new data in this category.\">");
//                b.append(updTarget);
//                b.append("</a>&nbsp");
//            }
//            b.append("<br/>Sites: <a href=\"");
//            b.append(p.getWebsite());
//            b.append("\">Website</a>&nbsp;Alitheia Reports");
//            b.append("</font></td>\n");
//            for(PluginInfo m : metrics) {
//                if(m.installed) {
//                    b.append("\n<td>\n");
//                    b.append(compMA.getLastAppliedVersion(sobjPA.getPlugin(m), p));
//                    b.append("\n</td>\n");
//                }
//            }
//            b.append("</tr>\n");
//        }
//        b.append("</table>");

        // Close the DB session
        sobjDB.commitDBSession();

        return b.toString();
    }

    public void addProject(HttpServletRequest request) {

        String name = request.getParameter("name");
        String website = request.getParameter("website");
        String contact = request.getParameter("contact");
        String bts = request.getParameter("bts");
        String mail = request.getParameter("mail");
        String scm = request.getParameter("scm");

        addProject(name, website, contact, bts, mail, scm);
    }

    private void addProject(String name, String website, String contact,
            String bts, String mail, String scm) {
        final String tryAgain = "<p><a href=\"/projects\">Try again</a>.</p>";
        final String returnToList = "<p><a href=\"/projects\">Try again</a>.</p>";

        // Avoid missing-entirely kinds of parameters.
        if ( (name == null) ||
             (website == null) ||
             (contact == null) ||
             /*  (bts == null) || FIXME: For now, BTS and Mailing lists can be empty
                 (mail == null) || */
             (scm == null) ) {
            vc.put("RESULTS",
                   "<p>Add project failed because some of the required information was missing.</p>"
                   + tryAgain);
            return;
        }

        // Avoid adding projects with empty names or SVN.
        if (name.trim().length() == 0 || scm.trim().length() == 0) {
            vc.put("RESULTS", "<p>Add project failed because the project name or Subversion repository were missing.</p>"
                   + tryAgain);
            return;
        }

        if (sobjDB != null && sobjDB.startDBSession()) {
            StoredProject p = new StoredProject();
            p.setName(name);
            p.setWebsite(website);
            p.setContact(contact);
            p.setBugs(bts);
            p.setRepository(scm);
            p.setMail(mail);

            sobjDB.addRecord(p);
            sobjDB.commitDBSession();

            /* Run a few checks before actually storing the project */
            //1. Duplicate project
            sobjDB.startDBSession();
            HashMap<String, Object> pname = new HashMap<String, Object>();
            pname.put("name", (Object)p.getName());
            if(sobjDB.findObjectsByProperties(StoredProject.class, pname).size() > 1) {
                //Duplicate project, remove
                sobjDB.deleteRecord(sobjDB.findObjectById(StoredProject.class, p.getId()));
                sobjDB.commitDBSession();
                sobjLogger.warn("A project with the same name already exists");
                vc.put("RESULTS","<p>ERROR: A project" +
                       " with the same name (" + p.getName() + ") already exists. " +
                       "Project not added.</p>" + tryAgain);
                return;
            }

            //2. Add accessor and try to access project resources
            sobjTDS.addAccessor(p.getId(), p.getName(), p.getBugs(),
                                p.getMail(), p.getRepository());
            TDAccessor a = sobjTDS.getAccessor(p.getId());

            try {
                a.getSCMAccessor().getHeadRevision();
                //FIXME: fix this when we have a proper bug accessor
                if(bts != null) {
                    //Bug b = a.getBTSAccessor().getBug(1);
                }
                if(mail != null) {
                    //FIXME: fix this when the TDS supports returning
                    // list information
                    //a.getMailAccessor().getNewMessages(0);
                }
            } catch (InvalidRepositoryException e) {
                sobjLogger.warn("Error accessing repository. Project not added");
                vc.put("RESULTS","<p>ERROR: Can not access " +
                                         "repository: &lt;" + p.getRepository() + "&gt;," +
                                         " project not added.</p>" + tryAgain);
                //Invalid repository, remove and remove accessor
                sobjDB.deleteRecord(sobjDB.findObjectById(StoredProject.class, p.getId()));
                sobjDB.commitDBSession();
                sobjTDS.releaseAccessor(a);
                return;
            }

            sobjTDS.releaseAccessor(a);

            // 3. Call the updater and check if it starts
            if (sobjUpdater.update(p, UpdaterService.UpdateTarget.ALL, null)) {
                sobjLogger.info("Added a new project <" + name + "> with ID " +
                                p.getId());
                vc.put("RESULTS",
                                         "<p>New project added successfully.</p>" +
                                         returnToList);
            }
            else {
                sobjLogger.warn("The updater failed to start while adding project");
                sobjDB.deleteRecord(sobjDB.findObjectById(StoredProject.class, p.getId()));
                vc.put("RESULTS","<p>ERROR: The updater failed " +
                                         "to start while adding project. Project was not added.</p>" +
                                         tryAgain);
            }
            sobjDB.commitDBSession();
        }
    }

    public void addProjectDir(HttpServletRequest request) {
        String info = request.getParameter("info");

        if(info == null || info.length() == 0) {
            vc.put("RESULTS",
                    "<p>Add project failed because some of the required information was missing.</p>"
                    + "<b>" + info + "</b>");
            return;
        }

        if (!info.endsWith("info.txt")) {
            vc.put("RESULTS",
                    "<p>The entered path does not include an info.txt file</p> <br/>"
                    + "<b>" + info + "</b>");
            return;
        }

        File f = new File(info);

        if (!f.exists() || !f.isFile()) {
            vc.put("RESULTS",
                    "<p>The provided path does not exist or is not a file</p> <br/>"
                    + "<b>" + info + "</b>");
            return;
        }

        String name = f.getParentFile().getName();
        String bts = "bts:" + f.getParentFile().getAbsolutePath() + "/bugs";
        String mail = "maildir:" + f.getParentFile().getAbsolutePath() + "/mail";
        String scm = "file://" + f.getParentFile().getAbsolutePath() + "/svn";

        Pattern wsPattern = Pattern.compile("^Website:?\\s*(http.*)$");
        Pattern ctnPattern = Pattern.compile("^Contact:?\\s*(http.*)$");

        String website = "", contact = "";

        try {
            LineNumberReader lnr = new LineNumberReader(new FileReader(f));
            String line = null;
            while((line = lnr.readLine()) != null) {
                Matcher m = wsPattern.matcher(line);
                if(m.matches()){
                    website = m.group(1);
                }
                m = ctnPattern.matcher(line);
                if(m.matches()){
                    contact = m.group(1);
                }
            }
        } catch (FileNotFoundException fnfe) {
            vc.put("RESULTS",
                    "<p>Error opeing file info.txt, file vanished?</p> <br/>"
                    + "<b>" + fnfe.getMessage() + "</b>");
            return;
        } catch (IOException e) {
            vc.put("RESULTS",
                    "<p>The provided path does not exist or is not a file</p> <br/>"
                    + "<b>" + info + "</b>");
            return;
        }

        addProject(name, website, contact, bts, mail, scm);
    }

    public void setMOTD(WebadminService webadmin, HttpServletRequest request) {
        webadmin.setMessageOfTheDay(request.getParameter("motdtext"));
        vc.put("RESULTS",
               "<p>The Message Of The Day was successfully updated with: <i>" +
               request.getParameter("motdtext") + "</i></p>");
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
