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

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.GroupPrivilege;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.db.Privilege;
import eu.sqooss.service.db.PrivilegeValue;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.User;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.TDAccessor;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.util.StringUtils;
import eu.sqooss.service.webadmin.WebadminService;

public class WebAdminRenderer {
    // Core components
    private static AlitheiaCore sobjAlitheiaCore = null;
    private static ServiceReference srefCore = null;

    // Critical logging components
    private static LogManager sobjLogManager = null;
    private static Logger sobjLogger = null;

    // Service components
    private static DBService sobjDB = null;
    private static MetricActivator sobjMetricActivator = null;
    private static PluginAdmin sobjPluginAdmin = null;
    private static Scheduler sobjSched = null;
    private static TDSService sobjTDS = null;
    private static UpdaterService sobjUpdater = null;
    private static SecurityManager sobjSecurity = null;

    // Velocity stuff
    private VelocityContext vc = null;

    // Current time
    private static long startTime = new Date().getTime();

    // Debug flag
    private static boolean DEBUG = false;

    public WebAdminRenderer(BundleContext bundlecontext, VelocityContext vc) {
        srefCore = bundlecontext.getServiceReference(AlitheiaCore.class.getName());
        this.vc = vc;

        if (srefCore != null) {
            sobjAlitheiaCore = (AlitheiaCore) bundlecontext.getService(srefCore);
        }
        else {
            System.out.println("AdminServlet: No Alitheia Core found.");
        }

        if (sobjAlitheiaCore != null) {
            //Get the LogManager and Logger objects
            sobjLogManager = sobjAlitheiaCore.getLogManager();
            if (sobjLogManager != null) {
                sobjLogger = sobjLogManager.createLogger(Logger.NAME_SQOOSS_WEBADMIN);
            }

            // Get the DB Service object
            sobjDB = sobjAlitheiaCore.getDBService();
            if (sobjDB != null) {
                sobjLogger.debug("WebAdmin got DB Service object.");
            }

            // Get the Plugin Administration object
            sobjPluginAdmin = sobjAlitheiaCore.getPluginAdmin();
            if (sobjPluginAdmin != null) {
                sobjLogger.debug("WebAdmin got Plugin Admin object.");
            }

            // Get the scheduler
            sobjSched = sobjAlitheiaCore.getScheduler();
            if (sobjSched != null) {
                sobjLogger.debug("WebAdmin got Scheduler Service object.");
            }

            // Get the metric activator, whatever that is
            sobjMetricActivator = sobjAlitheiaCore.getMetricActivator();
            if (sobjMetricActivator != null) {
                sobjLogger.debug("WebAdmin got Metric Activator object.");
            }

            // Get the TDS Service object
            sobjTDS = sobjAlitheiaCore.getTDSService();
            if (sobjTDS != null) {
                sobjLogger.debug("WebAdmin got TDS Service object.");
            }

            // Get the Updater Service object
            sobjUpdater = sobjAlitheiaCore.getUpdater();
            if (sobjUpdater != null) {
                sobjLogger.debug("WebAdmin got Updater Service object.");
            }

            // Get the Security Manager's object
            sobjSecurity = sobjAlitheiaCore.getSecurityManager();
            if (sobjSecurity != null) {
                sobjLogger.debug("WebAdmin got the Security Manager's object.");
            }
        }

        // Do some stuffing
        Stuffer myStuffer = new Stuffer(sobjDB, sobjLogger, sobjTDS);
        myStuffer.run();
    }

    public static String renderMetrics(HttpServletRequest request) {
        // Stores the assembled HTML content
        StringBuilder b = new StringBuilder();
        // Indentation spacer
        String IN = "            ";

        // Create a DB session
        sobjDB.startDBSession();
        // Retrieve information for all registered metric plug-ins
        Collection<PluginInfo> l = sobjPluginAdmin.listPlugins();

        // Request parameters
        String reqParAction         = "metricAction";
        String reqParNumber         = "metricNumber";
        // Request values
        String reqValInstall        = "install";
        String reqValUninstall      = "uninstall";
        if (request != null) {
            String metricAction = request.getParameter(reqParAction);
            String metricNumber = request.getParameter(reqParNumber);
            if (metricAction != null) {
                if (metricNumber != null) {
                    // Metric install request
                    if (metricAction.equalsIgnoreCase(reqValInstall)) {
                        if (sobjPluginAdmin.installPlugin(metricNumber)) {
                            b.append("Metric successfuly installed.");
                        }
                        else {
                            b.append("Metric can not be installed.");
                            b.append(" Check log for details.");
                        }
                    }
                    // Metric un-install request
                    else if (metricAction.equalsIgnoreCase(reqValUninstall)) {
                        if (sobjPluginAdmin.uninstallPlugin(metricNumber)) {
                            b.append("Metric successfuly uninstalled.");
                        }
                        else {
                            b.append("Metric can not be uninstalled.");
                            b.append(" Check log for details.");
                        }
                    }
                }
            }
        }

        if (l.isEmpty()) {
            b.append("No metrics found!");
        }
        else {
            // Create the form
            b.append("<form id=\"metrics\" name=\"metrics\" method=\"post\" action=\"/index\">\n");
            // Create the table
            b.append("<table>\n");
            b.append("<thead>\n");
            b.append("<tr class=\"head\">\n");
            b.append("<td class=\"head\" style=\"width: 80px;\">Status</td>\n");
            b.append("<td class=\"head\" style=\"width: 30%;\">Name</td>\n");
            b.append("<td class=\"head\" style=\"width: 40%;\">Class</td>\n");
            b.append("<td class=\"head\">Version</td>\n");
            b.append("</tr>\n");
            b.append("</thead>\n");
            b.append("<tbody>\n");
            // Push the metrics info
            // Not-installed plug-ins first
            for(PluginInfo i : l) {
                if (i.installed == false) {
                    b.append("<tr class=\"uninstalled\">");
                    
                    // Command bar
                    b.append("<td style=\"padding: 0;\">");
                    b.append("<input class=\"install\""
                            + " type=\"button\""
                            + " value=\"INSTALL\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('" + reqParAction + "').value='" + reqValInstall + "';"
                            + "document.getElementById('" + reqParNumber +"').value='" + i.getHashcode() + "';"
                            + "document.metrics.submit();\""
                            + ">");
                    b.append("</td>\n");
                    
                    // Info bar
                    b.append("<td>" + i.getPluginName() + "</td>\n");
                    b.append("<td>"
                            + StringUtils.join((String[]) (
                                    i.getServiceRef().getProperty(
                                            Constants.OBJECTCLASS)),",")
                            + "</td>\n");
                    b.append("<td>" + i.getPluginVersion() + "</td>\n");
                    b.append("</tr>\n");
                    
                    // Configuration bar
                    b.append(renderMetricAttributes(i));
                }
            }
            // Installed plug-ins
            for(PluginInfo i : l) {
                if (i.installed) {
                    b.append("<tr>");
                    
                    // Command bar
                    b.append("<td style=\"padding: 0;\">");
                    b.append("<input class=\"uninstall\""
                            + " type=\"button\""
                            + " value=\"UNINSTALL\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('" + reqParAction + "').value='" + reqValUninstall  +"';"
                            + "document.getElementById('" + reqParNumber +"').value='" + i.getHashcode() + "';"
                            + "document.metrics.submit();\""
                            + ">");
                    b.append("</td>\n");
                    
                    // Info bar
                    b.append("<td>" + i.getPluginName() + "</td>\n");
                    b.append("<td>"
                            + StringUtils.join((String[]) (
                                    i.getServiceRef().getProperty(
                                            Constants.OBJECTCLASS)),",")
                            + "</td>\n");
                    b.append("<td>" + i.getPluginVersion() + "</td>\n");
                    b.append("</tr>\n");
                    
                    // Configuration bar
                    b.append(renderMetricAttributes(i));
                }
            }
            // Close the table
            b.append("</tbody>\n");
            b.append("</table>\n");
            b.append("<input type=\"hidden\" id=\"" + reqParAction + "\" name=\"metricAction\" value=\"\">\n");
            b.append("<input type=\"hidden\" id=\"" + reqParNumber + "\" name=\"metricNumber\" value=\"\">\n");
            // Close the form
            b.append("</form>\n");
        }

        // Close the DB session
        sobjDB.commitDBSession();
        return b.toString();
    }

    /**
     * Creates a set of table rows populated with the attributes and their
     * values of the given <code>PluginInfo</code> object
     * 
     * @param pluginInfo a <code>PluginInfo</code> object
     */
    private static String renderMetricAttributes(PluginInfo pluginInfo) {
        // Retrieve the configuration set of this plug-in
        List<PluginConfiguration> l =  pluginInfo.getConfiguration();

        // Skip metric plug-ins that are registered but not installed
        if (pluginInfo.installed == false) {
            return "";
        }
        // Skip plug-ins that aren't configured or don't have configuration
        else if ((l == null) || (l.isEmpty())) {
            return ("<tr>"
                    + "<td>&nbsp;</td>\n"
                    + "<td colspan=\"3\" class=\"noattr\">"
                    + "This metric plug-in has no configurable attributes."
                    + "</td>\n"
                    + "</tr>\n");
        }
        else {
            StringBuilder b = new StringBuilder();
            
            // List the metric plug-in's configuration attributes
            for (PluginConfiguration c : l) {
                b.append("<tr>");
                b.append("<td>&nbsp;</td>\n");
                b.append("<td colspan=\"3\" class=\"attr\">"
                        + "<b>Attribute:</b> " + c.getName()
                        + "&nbsp;<b>Type:</b> " + c.getType()
                        + "&nbsp;<b>Value:</b> " + c.getValue()
                        + "</td>\n");
                b.append("</tr>\n");
            }
            
            // List the metric plug-in's activator types
            List<Class<? extends DAObject>> activationTypesList =
                pluginInfo.getActivationTypes();
            if (activationTypesList != null) {
                for (Class<? extends DAObject> activationType : activationTypesList) {
                    b.append("<tr>");
                    b.append("<td>&nbsp;</td>\n");
                    b.append("<td colspan=\"3\" class=\"attr\">"
                            + "<b>Activator:</b> "
                            + activationType.getName()
                            + "</td>");
                    b.append("</tr>\n");
                }
            }
            
            return b.toString();
        }
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

        for(String key : fjobs.keySet().toArray(new String[1])) {
            result.append("\t\t<tr>\n\t\t\t<td>");
            result.append(key);
            result.append("</td>\n\t\t\t<td>");
            result.append(fjobs.get(key));
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

        Class<?> tmpClass;
        for(Job j: jobs) {
            result.append("\t\t<tr>\n\t\t\t<td>");
            tmpClass = j.getClass();
            result.append(tmpClass.getPackage().getName());
            result.append(". " + tmpClass.getSimpleName());
            result.append("</td>\n\t\t\t<td>");
            if (j.getErrorException().getClass().toString() != null) {
                tmpClass = j.getErrorException().getClass();
                result.append(tmpClass.getPackage().getName());
                result.append(". " + tmpClass.getSimpleName());
                result.append("</td>\n\t\t\t<td>");
            } else {
                result.append("null");
                result.append("</td>\n\t\t\t<td>");    
            }
            result.append(j.getErrorException().getMessage());
            result.append("</td>\n\t\t\t<td>");
            for(StackTraceElement m: j.getErrorException().getStackTrace()) {
                result.append(m.getClassName());
                result.append(". ");
                result.append(m.getMethodName());
                result.append("(), (");
                result.append(m.getFileName());
                result.append(":");
                result.append(m.getLineNumber());
                result.append(")<br/>");
            }

            result.append("\t\t\t</td>\n\t\t</tr>");
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

    public static String renderProjects() {
        sobjDB.startDBSession();
        List projects = sobjDB.doHQL("from StoredProject");
        Collection<PluginInfo> metrics = sobjPluginAdmin.listPlugins();

        if (projects == null || metrics == null) {
            sobjDB.commitDBSession();
            return "<li>Nothing to display.</li>";
        }

        StringBuilder s = new StringBuilder();
        
        s.append("<table border=\"1\">");
        s.append("<tr>");
        s.append("<td><b>Project</b></td>");
        
        for(PluginInfo m : metrics) {
            if(m.installed) {
                s.append("<td><b>");
                s.append(m.getPluginName());
                s.append("</b></td>");
            }
        }
        s.append("</tr>");
       
        for (int i=0; i<projects.size(); i++) {
            s.append("<tr>");
            StoredProject p = (StoredProject) projects.get(i);
            s.append("<td><font size=\"-2\"><b>");
            s.append(p.getName());
            s.append("</b> ([id=");
            s.append(p.getId());
            s.append("]) <br/>Update:");
            for (String updTarget: UpdaterService.UpdateTarget.toStringArray()) {
                s.append("<a href=\"http://localhost:8088/updater?project=");
                s.append(p.getName());
                s.append("&target=");
                s.append(updTarget);
                s.append("\" title=\"Tell the updater to check for new data in this category.\">");
                s.append(updTarget);
                s.append("</a>&nbsp");
            }
            s.append("<br/>Sites: <a href=\"");
            s.append(p.getWebsite());
            s.append("\">Website</a>&nbsp;Alitheia Reports");
            s.append("</font></td>");
            for(PluginInfo m : metrics) {
                if(m.installed) {
                    s.append("<td>");
                    s.append(sobjMetricActivator.getLastAppliedVersion(sobjPluginAdmin.getPlugin(m), p));
                    s.append("</td>");
                }
            }
            s.append("</tr>");
        }
        s.append("</table>");
        sobjDB.commitDBSession();
        return s.toString();
    }

    /**
     * Converts a <code>String</code> into a <code>Long</code>,
     * while handling internally any thrown exception.
     * 
     * @param value the <code>String</code> value
     * 
     * @return The <code>Long</code> value.
     */
    private static Long fromString (String value) {
        try {
            return (new Long(value));
        }
        catch (NumberFormatException ex){
            return null;
        }
    }

    private static String debugRequest (HttpServletRequest request) {
        StringBuilder b = new StringBuilder();
        Enumeration<?> e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            b.append(key + "=" + request.getParameter(key) + "<br/>\n");
        }
        return b.toString();
    }

    private static String sp (long num) {
        StringBuilder b = new StringBuilder();
        String space = "  ";
        for (long i = 0; i < num; i++) {
            b.append(space);
        }
        return b.toString();
    }

    /**
     * Renders the various user views of the SQO-OSS WebAdmin UI:
     * <ul>
     *   <li>Users viewer
     *   <li>User editor
     *   <li>Group editor
     *   <li>Privilege editor
     * </ul>
     * 
     * @param request the servlet's request object
     * 
     * @return The current view.
     */
    public static String renderUsers(HttpServletRequest request) {
        // Stores the assembled HTML content
        StringBuilder b = new StringBuilder("\n");
        // Indentation spacer
        long in = 6;
        // Store here all known users
        User[] users = null;
        // Store here all known groups
        Group[] groups = null;

        // Create a DB session
        sobjDB.startDBSession();
        // Retrieve information for all registered users
        users = sobjSecurity.getUserManager().getUsers();
        if ((users != null) && (users.length > 0)) {
            // Request parameters
            String reqParAction        = "action";
            String reqParUserId        = "userId";
            String reqParGroupId       = "groupId";
            String reqParRightId       = "rightId";
            // Action values
            String actValAddToGroup    = "addToGroup";
            String actValRemFromGroup  = "removeFromGroup";
            // Request values
            Long reqValUserId          = null;
            Long reqValGroupId         = null;
            Long reqValRightId         = null;
            String reqValAction        = null;
            // Selected user
            User selUser = null;
            // Selected group;
            Group selGroup = null;

            // Parse the servlet's request object
            if (request != null) {
                // DEBUG: Dump the servlet's request parameter
                if (DEBUG) {
                    b.append(debugRequest(request));
                }
                // Retrieve the selected user Id (if any)
                if ((request.getParameter(reqParUserId) != null) &&
                    (request.getParameter(reqParUserId) != "")) {
                    // Get the selected user's object
                    reqValUserId =
                        fromString(request.getParameter(reqParUserId));
                    if (reqValUserId != null) {
                        selUser = sobjSecurity.getUserManager()
                            .getUser(reqValUserId);
                    }
                    // Retrieve information for all registered groups
                    groups = sobjSecurity.getGroupManager().getGroups();
                }
                // Retrieve the selected group Id (if any)
                if ((request.getParameter(reqParGroupId) != null) &&
                        (request.getParameter(reqParGroupId) != "")) {
                    // Get the selected group's object
                    reqValGroupId =
                        fromString(request.getParameter(reqParGroupId));
                    if (reqValGroupId != null) {
                        selGroup = sobjSecurity.getGroupManager()
                            .getGroup(reqValGroupId);
                    }
                }
                // Retrieve the selected editor's action
                reqValAction = request.getParameter(reqParAction);
                if ((reqValAction != null) && (reqValAction != "")) {
                    // Add the selected user to the selected group
                    if (reqValAction.equalsIgnoreCase(actValAddToGroup)) {
                        if ((selUser != null) && (selGroup != null)) {
                            sobjSecurity.getGroupManager()
                            .addUserToGroup(
                                    selGroup.getId(),
                                    selUser.getId());
                        }
                    }
                    // Remove the selected user from the selected group
                    else if (reqValAction.equalsIgnoreCase(actValRemFromGroup)) {
                        if ((selUser != null) && (selGroup != null)) {
                            sobjSecurity.getGroupManager()
                            .deleteUserFromGroup(
                                    selGroup.getId(),
                                    selUser.getId());
                        }
                    }
                }
            }

            // Create the form
            b.append(sp(in) + "<form id=\"users\""
                    + " name=\"users\""
                    + " method=\"post\""
                    + " action=\"/users\">\n");
            // Create the "user" fieldset
            b.append(sp(++in) + "<fieldset>\n");
            b.append(sp(++in) + "<legend>"
                    + ((selUser != null)
                            ? "User " + selUser.getName()
                            : "All users")
                    + "</legend\n>");
            // Create the table
            b.append(sp(in) + "<table>\n");
            b.append(sp(++in) + "<thead>\n");
            b.append(sp(++in) + "<tr class=\"head\">\n");

            // ---( HEADER ROWS )--------------------------------------------
            // User editor - header row
            if (selUser != null) {
                b.append(sp(++in) + "<td class=\"head\""
                        + " style=\"width: 40%;\">"
                        + "Account Details</td>\n");
                b.append(sp(in) + "<td class=\"head\" style=\"width: 30%;\">"
                        + "Member Of</td>\n");
                b.append(sp(in) + "<td class=\"head\" style=\"width: 30%;\">"
                        + "Available Groups</td>\n");
            }
            // Users list - header row
            else {
                b.append(sp(++in) + "<td class=\"head\" style=\"width: 10%;\">"
                        + "User Id</td>\n");
                b.append(sp(in) + "<td class=\"head\" style=\"width: 30%;\">"
                        + "User Name</td>\n");
                b.append(sp(in) + "<td class=\"head\" style=\"width: 30%;\">"
                        + "User Email</td>\n");
                b.append(sp(in) + "<td class=\"head\" style=\"width: 30%;\">"
                        + "Created</td>\n");
            }
            b.append(sp(--in) + "</tr>\n");
            b.append(sp(--in) + "</thead>\n");
            b.append(sp(in) + "<tbody>\n");

            // ---( TABLE ROWS )---------------------------------------------
            // User editor - table rows
            if (selUser != null) {
                b.append(sp(++in) + "<tr>\n");
                b.append(sp(++in) + "<td>\n");
                b.append(sp(++in) + "<table>\n");
                b.append(sp(++in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"name\">User Id</td>\n"
                        + sp(in) + "<td>&nbsp;"
                        + selUser.getId() + "</td>\n"
                        + sp(--in) + "</tr>\n");
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"name\">User Name</td>\n"
                        + sp(in) + "<td>&nbsp;"
                        + selUser.getName() + "</td>\n"
                        + sp(--in) + "</tr>\n");
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"name\">User Email</td>\n"
                        + sp(in) + "<td>&nbsp;"
                        + selUser.getEmail() + "</td>\n"
                        + sp(--in) + "</tr>\n");
                DateFormat date = DateFormat.getDateInstance();
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"name\">Created</td>\n"
                        + sp(in) + "<td>&nbsp;"
                        + date.format(selUser.getRegistered()) + "</td>\n"
                        + sp(--in) + "</tr>\n");
                b.append(sp(--in) + "</table>\n");
                b.append(sp(--in) + "</td>\n");
                // Display all groups where the selected user is a member
                b.append(sp(in) + "<td>\n");
                b.append(sp(++in) + "<select"
                        + " size=\"4\" style=\"width: 100%; border: 0;\">\n");
                sp(++in);
                for (Object memberOf : selUser.getGroups()) {
                    Group group = (Group) memberOf;
                    b.append(sp(in) + "<option"
                            + " value=\"" + group.getId() + "\""
                            + " onclick=\"javascript:"
                            + " document.getElementById('"
                                 + reqParGroupId + "').value='"
                                 + group.getId() + "';"
                            + "document.users.submit();\""
                            + (((selGroup != null)
                                    && (selGroup.getId() == group.getId()))
                                    ? " selected" : "")
                            + ">"
                            + group.getDescription()
                            + "</option>\n");
                }
                b.append(sp(--in) + "</select>\n");
                b.append(sp(--in) + "</td>\n");
                // Display all group where the selected user is not a member
                b.append(sp(in) + "<td>\n");
                b.append(sp(++in) + "<select"
                        + " size=\"4\" style=\"width: 100%; border: 0;\">\n");
                sp(++in);
                for (Group group : groups) {
                    // Skip groups where this user is already a member 
                    if (selUser.getGroups().contains(group) == false) {
                        b.append(sp(in) + "<option"
                                + " value=\"" + group.getId() + "\""
                                + " onclick=\"javascript:"
                                + " document.getElementById('"
                                    + reqParGroupId + "').value='"
                                    + group.getId() + "';"
                                + "document.users.submit();\""
                                + (((selGroup != null)
                                        && (selGroup.getId() == group.getId()))
                                        ? " selected" : "")
                                + ">"
                                + group.getDescription()
                                + "</option>\n");
                    }
                }
                b.append(sp(--in) + "</select>\n");
                b.append(sp(--in) + "</td>\n");
                b.append(sp(--in) + "</tr>\n");
            }
            // User list -table rows
            else {
                for (User nextUser : users) {
                    String htmlEditUser = "<td class=\"edit\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                            + reqParUserId + "').value='"
                            + nextUser.getId() + "';"
                            + "document.users.submit();\">"
                        + "<img src=\"/edit.png\" alt=\"[Edit]\"/>"
                        + nextUser.getName()
                        + "</td>\n";
                    b.append(sp(++in) + "<tr>\n");
                    b.append(sp(++in) + "<td>" + nextUser.getId() + "</td>\n");
                    b.append(sp(in) + htmlEditUser);
                    b.append(sp(in) + "<td>" + nextUser.getEmail() + "</td>\n");
                    DateFormat date = DateFormat.getDateInstance();
                    b.append(sp(in) + "<td>"
                            + date.format(nextUser.getRegistered())
                            + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                }
            }

            // ---( COMMAND ROWS )-------------------------------------------
            if (selUser != null) {
                b.append(sp(in) + "<tr>\n");

                b.append(sp(++in) + "<td>\n");
                b.append(sp(++in) + "&nbsp;\n");
                b.append(sp(--in) + "</td>\n");

                b.append(sp(in) + "<td style=\"padding: 0;\">\n");
                if ((selGroup != null)
                        && (selUser.getGroups().contains(selGroup) == true)) {
                    b.append(sp(++in) + "<input class=\"install\""
                            + " type=\"button\""
                            + " value=\"Remove\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValRemFromGroup + "';"
                            + "document.users.submit();\""
                            + ">\n");
                }
                b.append(sp(--in) + "</td>\n");

                b.append(sp(in) + "<td style=\"padding: 0;\">\n");
                if ((selGroup != null)
                        && (selUser.getGroups().contains(selGroup) == false)) {
                    b.append(sp(++in) + "<input class=\"install\""
                            + " type=\"button\""
                            + " value=\"Add\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValAddToGroup + "';"
                            + "document.users.submit();\""
                            + ">\n");
                }
                b.append(sp(--in) + "</td>\n");

                b.append(sp(--in) + "</tr>\n");
            }
            // Close the table
            b.append(sp(--in) + "</tbody>\n");
            b.append(sp(--in) + "</table>\n");
            b.append(sp(--in) + "</fieldset>\n");

            // ---( GROUP ROWS )---------------------------------------------
            if (selGroup != null) {
                b.append(sp(in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>Group "
                        + selGroup.getDescription() + "</legend\n>");
                b.append(sp(in) + "<table>\n");

                b.append(sp(++in) + "<thead>\n");
                b.append(sp(++in) + "<tr class=\"head\">\n");
                b.append(sp(++in) + "<td class=\"head\""
                        + " style=\"width: 15%;\">"
                        + "Actions</td>\n");
                b.append(sp(in) + "<td class=\"head\""
                        + " style=\"width: 55%;\">"
                        + "Services</td>\n");
                b.append(sp(in) + "<td class=\"head\""
                        + " style=\"width: 15%;\">"
                        + "Privileges</td>\n");
                b.append(sp(in) + "<td class=\"head\""
                        + " style=\"width: 15%;\">"
                        + "Rights</td>\n");
                b.append(sp(--in) + "</tr>\n");
                b.append(sp(--in) + "</thead>\n");

                b.append(sp(in) + "<tbody>\n");

                // "Assigned rights" header
                if (selGroup.getGroupPrivileges().isEmpty() == false) {
                    b.append(sp(++in) + "<tr class=\"subhead\">\n");
                    b.append(sp(++in) + "<td class=\"subhead\" colspan=\"4\">"
                            + "Assigned</td>");
                    b.append(sp(--in) + "</tr>\n");
                }
                // "Assigned rights" list
                for (Object privilege : selGroup.getGroupPrivileges()) {
                    b.append(sp(in) + "<tr>\n");
                    // Action bar
                    b.append(sp(++in) + "<td>"
                            + "&nbsp;"
                            + "</td>\n");
                    // Attached service
                    b.append(sp(++in) + "<td>"
                            + ((GroupPrivilege) privilege).getUrl().getUrl()
                            + "</td>\n");
                    // Assigned privilege
                    b.append(sp(++in) + "<td>"
                            + ((GroupPrivilege) privilege).getPv().getPrivilege().getDescription()
                            + "</td>\n");
                    // Granted right
                    b.append(sp(++in) + "<td>"
                            + ((GroupPrivilege) privilege).getPv().getValue()
                            + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                }

                // "Available rights" header
                if (selGroup.getGroupPrivileges().isEmpty() == false) {
                    b.append(sp(in) + "<tr class=\"subhead\">\n");
                    b.append(sp(++in) + "<td class=\"subhead\" colspan=\"4\">"
                            + "Available</td>");
                    b.append(sp(--in) + "</tr>\n");
                }
                // "Available rights" list
                for (Privilege privilege : sobjSecurity.getPrivilegeManager().getPrivileges()) {
                    b.append(sp(in) + "<tr>\n");
                    // Action bar
                    b.append(sp(++in) + "<td>"
                            + "&nbsp;"
                            + "</td>\n");
                    // Available services
                    b.append(sp(++in) + "<td>"
                            + "&nbsp;"
                            + "</td>\n");
                    // Available privileges
                    b.append(sp(++in) + "<td>"
                            + privilege.getDescription()
                            + "</td>\n");
                    // Available rights
                    b.append(sp(++in) + "<td>\n");
                    PrivilegeValue[] values =
                        sobjSecurity.getPrivilegeManager()
                            .getPrivilegeValues(privilege.getId());
                    if ((values != null) && (values.length > 0)) {
                        b.append(sp(++in) + "<select"
                                + " style=\"width: 100%; border: 0;\">\n");
                        for (PrivilegeValue value : values) {
                            b.append(sp(in) + "<option"
                                    + " value=\"" + value.getId() + "\""
                                    + " onclick=\"javascript:"
                                    + " document.getElementById('"
                                        + reqParRightId + "').value='"
                                        + value.getId() + "';"
                                    + "document.users.submit();\""
                                    + ">"
                                    + value.getValue()
                                    + "</option>\n");
                        }
                        b.append(sp(--in) + "</select>");
                    }
                    b.append(sp(++in) + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                }

                b.append(sp(--in) + "</tbody>\n");

                b.append(sp(--in) + "</table>\n");
                b.append(sp(--in) + "</fieldset>\n");
            }

            // ---( INPUT FIELD )--------------------------------------------
            // "Action type" input field
            b.append(sp(in) + "<input type=\"hidden\""
                    + " id=\"" + reqParAction + "\"" 
                    + " name=\"" + reqParAction + "\""
                    + " value=\"\">\n");
            // "User Id" input field
            b.append(sp(in) + "<input type=\"hidden\""
                    + " id=\"" + reqParUserId + "\"" 
                    + " name=\"" + reqParUserId + "\""
                    + " value=\""
                    + ((selUser != null) ? selUser.getId() : "")
                    + "\">\n");
            // "Group Id" input field
            b.append(sp(in) + "<input type=\"hidden\""
                    + " id=\"" + reqParGroupId + "\"" 
                    + " name=\"" + reqParGroupId + "\""
                    + " value=\""
                    + ((selGroup != null) ? selGroup.getId() : "")
                    + "\">\n");
            // "Right Id" input field
            b.append(sp(in) + "<input type=\"hidden\""
                    + " id=\"" + reqParRightId + "\"" 
                    + " name=\"" + reqParRightId + "\""
                    + " value=\"\">\n");

            // Close the form
            b.append(sp(--in) + "</form>\n");
        }
        else {
            b.append("No users found!");
        }
        // Close the DB session
        sobjDB.commitDBSession();

        return b.toString();
    }

    public void addProject(HttpServletRequest request) {        
        final String tryAgain = "<p><a href=\"/projects\">Try again</a>.</p>";
        final String returnToList = "<p><a href=\"/projects\">Try again</a>.</p>";
        
        String name = request.getParameter("name");
        String website = request.getParameter("website");
        String contact = request.getParameter("contact");
        String bts = request.getParameter("bts");
        String mail = request.getParameter("mail");
        String scm = request.getParameter("scm");
        
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

    public void setMOTD(WebadminService webadmin, HttpServletRequest request) {
        webadmin.setMessageOfTheDay(request.getParameter("motdtext"));
        vc.put("RESULTS", 
               "<p>The Message Of The Day was successfully updated with: <i>" +
               request.getParameter("motdtext") + "</i></p>");
    }

    public static void logRequest(String request) {
        sobjLogger.info(request);
    }
}
