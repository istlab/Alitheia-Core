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
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import eu.sqooss.service.db.ServiceUrl;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.User;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.pa.PluginInfo.ConfigurationType;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.security.GroupManager;
import eu.sqooss.service.security.PrivilegeManager;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.security.ServiceUrlManager;
import eu.sqooss.service.security.UserManager;
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
    private static PluginAdmin sobjPA = null;
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
            sobjPA = sobjAlitheiaCore.getPluginAdmin();
            if (sobjPA != null) {
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

    /**
     * Verifies, if the specified configuration parameter exist in the
     * given plug-in's information object.
     * 
     * @param selPI the plug-in's information object
     * @param name the parameter's name
     * @param type the parameter's type
     * 
     * @return <code>true</code>, if such parameter is found,
     *   or <code>false</code> otherwise.
     */
    public static boolean paramExist (
            PluginInfo selPI,
            String name,
            String type) {
        if ((selPI == null) || (name == null) || (type == null)) {
            return false;
        }
        for (PluginConfiguration param : selPI.getConfiguration()) {
            if ((param.getName().equals(name))
                    && (param.getType().equals(type))) {
                return true;
            }
        }
        return false;
    }

    public static String renderMetrics(HttpServletRequest req) {
        // Stores the assembled HTML content
        StringBuilder b = new StringBuilder("\n");
        // Stores the accumulated error messages
        StringBuilder e = new StringBuilder();
        // Indentation spacer
        long in = 6;

        // Create a DB session
        sobjDB.startDBSession();

        // Request parameters
        String reqParAction        = "action";
        String reqParHashcode      = "pluginHashcode";
        String reqParAttrName      = "attributeName";
        String reqParAttrDescr     = "attributeDescription";
        String reqParAttrType      = "attributeType";
        String reqParAttrValue     = "attributeValue";
        // Recognized "action" parameter's values
        String actValInstall       = "installPlugin";
        String actValUninstall     = "uninstallPlugin";
        String actValReqAddAttr    = "requestAttribute";
        String actValReqUpdAttr    = "updateAttribute";
        String actValConAddAttr    = "confirmAttribute";
        // Request values
        String reqValAction        = "";
        String reqValHashcode      = null;
        boolean reqValShowAttr     = true;
        boolean reqValShowActv     = true;
        String reqValAttrName      = null;
        String reqValAttrDescr     = null;
        String reqValAttrType      = null;
        String reqValAttrValue     = null;
        // Info object of the selected plug-in
        PluginInfo selPI           = null;

        // Proceed only when at least one plug-in is registered
        if (sobjPA.listPlugins().isEmpty()) {
            b.append(normalFieldset(
                    "All plug-ins",
                    null,
                    new StringBuilder("<span>"
                            + "No plug-ins found!&nbsp;"
                            + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"Refresh\""
                            + " onclick=\"javascript:"
                            + "window.location.reload(true);"
                            + "\">"
                            + "</span>"),
                    in));
        }
        else {
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
                }
                // Retrieve the selected configuration parameter's values
                if ((reqValAction.equals(actValConAddAttr))
                        || (reqValAction.equals(actValReqUpdAttr))) {
                    reqValAttrName = req.getParameter(reqParAttrName);
                    reqValAttrDescr = req.getParameter(reqParAttrDescr);
                    reqValAttrType = req.getParameter(reqParAttrType);
                    reqValAttrValue = req.getParameter(reqParAttrValue);
                }
                // Retrieve the selected plug-in's hash code and info object
                reqValHashcode = req.getParameter(reqParHashcode);
                if (reqValHashcode != null) {
                    selPI = sobjPA.getPluginInfo(reqValHashcode);
                }
                if (reqValHashcode != null) {
                    // Plug-in install request
                    if (reqValAction.equals(actValInstall)) {
                        if (sobjPA.installPlugin(reqValHashcode) == false) {
                            e.append("Plug-in can not be installed!"
                                    + " Check log for details.");
                        }
                    }
                    // Plug-in un-install request
                    else if (reqValAction.equals(actValUninstall)) {
                        if (sobjPA.uninstallPlugin(reqValHashcode) == false) {
                            e.append("Plug-in can not be uninstalled."
                                    + " Check log for details.");
                        }
                    }
                    // Plug-in's configuration parameter create/update
                    else if (reqValAction.equals(actValConAddAttr)) {
                        // Check for a parameter update
                        boolean update = paramExist(
                                selPI, reqValAttrName, reqValAttrType);
                        if (update) {
                            try {
                                if (selPI.updateConfigEntry(
                                        sobjDB,
                                        reqValAttrName,
                                        reqValAttrValue)) {
                                    // Update the Plug-in Admin's information
                                    sobjPA.pluginUpdated(
                                            sobjPA.getPlugin(selPI));
                                    // Reload the PluginInfo object
                                    selPI =
                                        sobjPA.getPluginInfo(reqValHashcode);
                                }
                                else {
                                    e.append("Configuration update"
                                            + " has failed."
                                            + " Check log for details.");
                                }
                            }
                            catch (Exception ex) {
                                e.append(ex.getMessage());
                            }
                        }
                    }
                }
            }

            // ===============================================================
            // Create the form
            // ===============================================================
            b.append(sp(in++) + "<form id=\"metrics\""
                    + " name=\"metrics\""
                    + " method=\"post\""
                    + " action=\"/index\">\n");

            // ===============================================================
            // Display the accumulated error messages (if any)
            // ===============================================================
            b.append(errorFieldset(e, in));

            // ===============================================================
            // "Update/New configuration parameter" editor
            // ===============================================================
            if ((selPI != null)
                    && ((reqValAction.equals(actValReqAddAttr))
                            || (reqValAction.equals(actValReqUpdAttr)))) {
                // Field value
                String value = null;
                // Create the field-set
                b.append(sp(in) + "<fieldset>\n");
                // Check for a parameter update
                boolean update = paramExist(
                        selPI, reqValAttrName, reqValAttrType);
                b.append(sp(++in) + "<legend>"
                        + ((update)
                                ? "Update parameter of "
                                : "New parameter for ")
                        + selPI.getPluginName()
                        + "</legend>\n");
                b.append(sp(in) + "<table class=\"borderless\">");
                // Parameter's name
                value = ((reqValAttrName != null) ? reqValAttrName : "");
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Name</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">"
                        + ((update) ? value
                                : "<input type=\"text\""
                                    + " class=\"form\""
                                    + " id=\"" + reqParAttrName + "\""
                                    + " name=\"" + reqParAttrName + "\""
                                    + " value=\"" + value + "\">")
                                    + "</td>\n"
                                    + sp(--in) + "</tr>\n");
                // Parameter's description
                value = ((reqValAttrDescr != null) ? reqValAttrDescr : "");
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Description</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">"
                        + ((update) ? value
                                : "<input type=\"text\""
                                    + " class=\"form\""
                                    + " id=\"" + reqParAttrDescr + "\""
                                    + " name=\"" + reqParAttrDescr + "\""
                                    + " value=\"" + value + "\">")
                                    + "</td>\n"
                                    + sp(--in) + "</tr>\n");
                // Parameter's type
                value = ((reqValAttrType != null) ? reqValAttrType : "");
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Type</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">\n"
                        + sp(++in));
                if (update) {
                    b.append(value);
                }
                else {
                    b.append("<select class=\"form\""
                            + " id=\"" + reqParAttrType + "\""
                            + " name=\"" + reqParAttrType + "\">\n");
                    for (ConfigurationType type : ConfigurationType.values()) {
                        boolean selected = type.toString().equals(value);
                        b.append(sp(in) + "<option"
                                + " value=\"" + type.toString() + "\""
                                + ((selected) ? " selected" : "")
                                + ">"
                                + type.toString()
                                + "</option>\n");
                    }
                    b.append(sp(in) + "</select>\n");
                }
                b.append(sp(--in)
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                // Attribute's value
                value = ((reqValAttrValue != null) ? reqValAttrValue : "");
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Value</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">"
                        + "<input type=\"text\""
                        + " class=\"form\""
                        + " id=\"" + reqParAttrValue + "\""
                        + " name=\"" + reqParAttrValue + "\""
                        + " value=\"" + value +"\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                // Tool-bar
                value = ((update) ? "Update" : "Add");
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td colspan=\"2\" class=\"borderless\">"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"" + value + "\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValConAddAttr + "';"
                        + "document.metrics.submit();\">"
                        + "&nbsp;"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Cancel\""
                        + " onclick=\"javascript:"
                        + "document.metrics.submit();\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                b.append(sp(--in) + "</table>");
                b.append(sp(--in) + "</fieldset>\n");
            }
            // ===============================================================
            // "Plug-in editor" view
            // ===============================================================
            else if (selPI != null) {
                // Create the plug-in field-set
                b.append(sp(in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>"
                        + selPI.getPluginName()
                        + "</legend>\n");
                // Create the parameters field-set
                b.append(sp(++in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>"
                        + "Configuration parameters"
                        + "</legend>\n");
                // Create the parameters table
                b.append(sp(in) + "<table>\n");
                b.append(sp(++in) + "<thead>\n");
                b.append(sp(++in) + "<tr class=\"head\">\n");
                b.append(sp(++in) + "<td class=\"head\""
                        + " style=\"width: 30%;\">"
                        + "Name</td>\n");
                b.append(sp(in) + "<td class=\"head\""
                        + " style=\"width: 20%;\">"
                        + "Type</td>\n");
                b.append(sp(in) + "<td class=\"head\""
                        + " style=\"width: 50%;\">"
                        + "Value</td>\n");
                b.append(sp(--in) + "</tr>\n");
                b.append(sp(--in) + "</thead>\n");
                // Display the configuration set of this plug-in
                b.append(sp(in++) + "<tbody>\n");
                // Get the plug-in's configuration set
                List<PluginConfiguration> config = selPI.getConfiguration();
                if (config.isEmpty()) {
                    b.append(sp(in++) + "<tr>");
                    b.append(sp(in) + "<td colspan=\"3\" class=\"noattr\">"
                            + "This plug-in has no configurable parameters."
                            + "</td>\n");
                    b.append(sp(--in)+ "</tr>\n");
                }
                else {
                    for (PluginConfiguration param : config) {
                        b.append(sp(in++) + "<tr>\n");
                        String htmlEditParam = "<td class=\"edit\""
                            + " title=\""
                            + ((param.getMsg() != null)
                                    ? param.getMsg()
                                    : "No description available.")
                            + "\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValReqUpdAttr + "';"
                            + "document.getElementById('"
                            + reqParAttrName + "').value='"
                            + param.getName() + "';"
                            + "document.getElementById('"
                            + reqParAttrType + "').value='"
                            + param.getType() + "';"
                            + "document.getElementById('"
                            + reqParAttrDescr + "').value='"
                            + param.getMsg() + "';"
                            + "document.getElementById('"
                            + reqParAttrValue + "').value='"
                            + param.getValue() + "';"
                            + "document.metrics.submit();\">"
                            + "<img src=\"/edit.png\" alt=\"[Edit]\"/>"
                            + param.getName()
                            + "</td>\n";
                        b.append(sp(in)
                                + htmlEditParam
                                + sp(in) + "<td>"
                                + param.getType()
                                + "</td>\n"
                                + sp(in) + "<td>"
                                + param.getValue()
                                + "</td>\n");
                        b.append(sp(--in)+ "</tr>\n");
                    }
                }
                // Create the parameter's tool-bar
                b.append(sp(in) + "<tr>\n");
                b.append(sp(++in) + "<td colspan=\"3\">\n");
                b.append(sp(++in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Plug-ins list\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParHashcode + "').value='';"
                        + "document.metrics.submit();\""
                        + ">\n");
                b.append(sp(in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Add parameter\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValReqAddAttr + "';"
                        + "document.metrics.submit();\""
                        + ">\n");
                b.append(sp(--in) + "</td>\n");
                b.append(sp(--in) + "</tr>\n");
                // Close the parameters table
                b.append(sp(--in) + "</tbody>\n");
                // Close the parameters table
                b.append(sp(--in) + "</table>\n");
                // Close the parameters field-set
                b.append(sp(--in) + "</fieldset>\n");
                // Close the plug-in field-set
                b.append(sp(--in) + "</fieldset>\n");
            }
            // ===============================================================
            // "Plug-ins list" view
            // ===============================================================
            else {
                // Create the field-set
                b.append(sp(++in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>All plug-ins</legend>\n");
                // Retrieve information for all registered metric plug-ins
                Collection<PluginInfo> l = sobjPA.listPlugins();
                // Create the header row
                b.append("<table>\n");
                b.append("<thead>\n");
                b.append("<tr class=\"head\">\n");
                b.append("<td class=\"head\" style=\"width: 80px;\">"
                        + "Status</td>\n");
                b.append("<td class=\"head\" style=\"width: 30%;\">"
                        + "Name</td>\n");
                b.append("<td class=\"head\" style=\"width: 40%;\">"
                        + "Class</td>\n");
                b.append("<td class=\"head\">Version</td>\n");
                b.append("</tr>\n");
                b.append("</thead>\n");
                b.append("<tbody>\n");
                // Push the metrics info
                // Not-installed plug-ins first
                for(PluginInfo i : l) {
                    String htmlEditPlugin = "<td class=\"edit\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParHashcode + "').value='"
                        + i.getHashcode() + "';"
                        + "document.metrics.submit();\">"
                        + "<img src=\"/edit.png\" alt=\"[Edit]\"/>"
                        + i.getPluginName()
                        + "</td>\n";
                    if (i.installed == false) {
                        b.append("<tr class=\"uninstalled\">");
                        
                        // Command bar
                        b.append("<td style=\"padding: 0;\">");
                        b.append("<input class=\"install\""
                                + " type=\"button\""
                                + " value=\"INSTALL\""
                                + " onclick=\"javascript:"
                                + "document.getElementById('" + reqParAction + "').value='" + actValInstall + "';"
                                + "document.getElementById('" + reqParHashcode +"').value='" + i.getHashcode() + "';"
                                + "document.metrics.submit();\""
                                + ">");
                        b.append("</td>\n");
                        
                        // Info bar
                        b.append(htmlEditPlugin);
                        b.append("<td>"
                                + StringUtils.join((String[]) (
                                        i.getServiceRef().getProperty(
                                                Constants.OBJECTCLASS)),",")
                                                + "</td>\n");
                        b.append("<td>" + i.getPluginVersion() + "</td>\n");
                        b.append("</tr>\n");
                        
                        // Configuration bar
                        b.append(renderMetricAttributes(
                                i, reqValShowAttr, reqValShowActv, in));
                    }
                }
                // Installed plug-ins
                for(PluginInfo i : l) {
                    String htmlEditPlugin = "<td class=\"edit\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParHashcode + "').value='"
                        + i.getHashcode() + "';"
                        + "document.metrics.submit();\">"
                        + "<img src=\"/edit.png\" alt=\"[Edit]\"/>"
                        + i.getPluginName()
                        + "</td>\n";
                    if (i.installed) {
                        b.append("<tr>");
                        
                        // Command bar
                        b.append("<td style=\"padding: 0;\">");
                        b.append("<input class=\"uninstall\""
                                + " type=\"button\""
                                + " value=\"UNINSTALL\""
                                + " onclick=\"javascript:"
                                + "document.getElementById('" + reqParAction + "').value='" + actValUninstall  +"';"
                                + "document.getElementById('" + reqParHashcode +"').value='" + i.getHashcode() + "';"
                                + "document.metrics.submit();\""
                                + ">");
                        b.append("</td>\n");
                        
                        // Info bar
                        b.append(htmlEditPlugin);
                        b.append("<td>"
                                + StringUtils.join((String[]) (
                                        i.getServiceRef().getProperty(
                                                Constants.OBJECTCLASS)),",")
                                                + "</td>\n");
                        b.append("<td>" + i.getPluginVersion() + "</td>\n");
                        b.append("</tr>\n");
                        
                        // Configuration bar
                        b.append(renderMetricAttributes(
                                i, reqValShowAttr, reqValShowActv, in));
                    }
                }
                // Close the table
                b.append("</tbody>\n");
                b.append("</table>\n");
                // Close the fieldset
                b.append(sp(--in) + "</fieldset>\n");
            }

            // ===============================================================
            // INPUT FIELDS
            // ===============================================================
            // "Action type" input field
            b.append("<input type=\"hidden\""
                    + " id=\"" + reqParAction + "\""
                    + " name=\"" + reqParAction + "\""
                    + " value=\"\">\n");
            // "Selected plug-in's hash code" input field
            b.append("<input type=\"hidden\""
                    + " id=\"" + reqParHashcode + "\""
                    + " name=\"" + reqParHashcode + "\""
                    + " value=\""
                    + ((reqValHashcode != null) ? reqValHashcode : "")
                    + "\">\n");
            // "Configuration attribute's name" input field
            b.append("<input type=\"hidden\""
                    + " id=\"" + reqParAttrName + "\""
                    + " name=\"" + reqParAttrName + "\""
                    + " value=\""
                    + ((reqValAttrName != null) ? reqValAttrName : "")
                    + "\">\n");
            // "Configuration attribute's description" input field
            b.append("<input type=\"hidden\""
                    + " id=\"" + reqParAttrDescr + "\""
                    + " name=\"" + reqParAttrDescr + "\""
                    + " value=\""
                    + ((reqValAttrDescr != null) ? reqValAttrDescr : "")
                    + "\">\n");
            // "Configuration attribute's type" input field
            b.append("<input type=\"hidden\""
                    + " id=\"" + reqParAttrType + "\""
                    + " name=\"" + reqParAttrType + "\""
                    + " value=\""
                    + ((reqValAttrType != null) ? reqValAttrType : "")
                    + "\">\n");
            // "Configuration attribute's value" input field
            b.append("<input type=\"hidden\""
                    + " id=\"" + reqParAttrValue + "\""
                    + " name=\"" + reqParAttrValue + "\""
                    + " value=\""
                    + ((reqValAttrValue != null) ? reqValAttrValue : "")
                    + "\">\n");

            // ===============================================================
            // Close the form
            // ===============================================================
            b.append(sp(--in) + "</form>\n");
        }

        // Close the DB session
        sobjDB.commitDBSession();

        return b.toString();
    }

    /**
     * Creates a set of table rows populated with the metric attributes and
     * their values as found in the given <code>PluginInfo</code> object
     * 
     * @param pluginInfo a <code>PluginInfo</code> object
     * @param showAttributes
     * @param showActivators
     * @param in indentation space
     * 
     * @return The table as HTML snippet.
     */
    private static String renderMetricAttributes(
            PluginInfo pluginInfo,
            boolean showAttributes,
            boolean showActivators,
            long in) {
        // Retrieve the configuration set of this plug-in
        List<PluginConfiguration> l =  pluginInfo.getConfiguration();

        // Skip metric plug-ins that are registered but not installed
        if (pluginInfo.installed == false) {
            return "";
        }
        // Skip plug-ins that aren't configured or don't have configuration
        else if ((l == null) || (l.isEmpty())) {
            return (sp(in++) + "<tr>\n"
                    + sp(in) + "<td>&nbsp;</td>\n"
                    + sp(in) + "<td colspan=\"3\" class=\"noattr\">"
                    + "This plug-in has no configurable attributes."
                    + "</td>\n"
                    + sp(--in)+ "</tr>\n");
        }
        // Display all configuration attributes and activation types
        else {
            StringBuilder b = new StringBuilder();
            // List the metric plug-in's configuration attributes
            if (showAttributes) {
                for (PluginConfiguration c : l) {
                    b.append(sp(in++) + "<tr>");
                    b.append(sp(in) + "<td>&nbsp;</td>\n");
                    b.append(sp(in) + "<td colspan=\"3\" class=\"attr\">"
                            + "<b>Attribute:</b> " + c.getName()
                            + "&nbsp;<b>Type:</b> " + c.getType()
                            + "&nbsp;<b>Value:</b> " + c.getValue()
                            + "</td>\n");
                    b.append(sp(--in)+ "</tr>\n");
                }
            }
            // List the metric plug-in's activator types
            if (showActivators) {
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
        Collection<PluginInfo> metrics = sobjPA.listPlugins();

        if (projects == null || metrics == null) {
            sobjDB.commitDBSession();
            return "<b>No projects installed</b>";
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
        s.append("</tr>\n");
       
        for (int i=0; i<projects.size(); i++) {
            s.append("\t<tr>\n");
            StoredProject p = (StoredProject) projects.get(i);
            s.append("\t\t<!--project--><td><font size=\"-2\"><b>");
            s.append(p.getName());
            s.append("</b> ([id=");
            s.append(p.getId());
            s.append("]) <br/>\nUpdate:");
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
            s.append("</font></td>\n");
            for(PluginInfo m : metrics) {
                if(m.installed) {
                    s.append("\n<td>\n");
                    s.append(sobjMetricActivator.getLastAppliedVersion(sobjPA.getPlugin(m), p));
                    s.append("\n</td>\n");
                }
            }
            s.append("</tr>\n");
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

    private static boolean checkName (String text) {
        Pattern p = Pattern.compile("[a-zA-Z0-9]*");
        return p.matcher(text).matches();
    }

    private static boolean checkEmail (String text) {
        // Check for adjacent dot signs
        Pattern p = Pattern.compile("\\.\\.");
        if (p.matcher(text).matches()) return false;
        // Split into local and domain part
        String parts[] = text.split("@");
        if (parts.length != 2) return false;
        // Check for head or foot occurrence of dot signs
        p = Pattern.compile("^[.].*");
        if (p.matcher(parts[0]).matches()) return false;
        if (p.matcher(parts[1]).matches()) return false;
        p = Pattern.compile(".*[.]$");
        if (p.matcher(parts[0]).matches()) return false;
        if (p.matcher(parts[1]).matches()) return false;
        // Local part regexp
        Pattern l = Pattern.compile("^[a-zA-Z0-9!#$%*/?|^{}`~&'+-=_.]+$");
        // Domain part regexp
        Pattern d = Pattern.compile("^[a-zA-Z0-9.-]+[.][a-zA-Z]{2,4}$");
        // Match both parts
        return ((l.matcher(parts[0]).matches())
                && (d.matcher(parts[1]).matches()));
    }

    /**
     * Produces an HTML <code>fieldset</code> presenting the HTML content
     * stored in the given <code>StringBuilder</code>.
     * 
     * @param name the fieldset legend's name
     * @param css the CSS class name to use
     * @param content the HTML content
     * @param in the indentation length (<i>rendered into *2 spaces</i>)
     * 
     * @return The HTML presentation.
     */
    private static String normalFieldset (
            String name,
            String css,
            StringBuilder content,
            long in) {
        if ((content != null) && (content.toString().length() > 0)) {
            return (sp(in) + "<fieldset"
                    + ((css != null) ? "class=\"" + css + "\"": "")
                    + ">\n"
                    + sp(++in) + "<legend>"
                    + ((name != null) ? name : "NONAME")
                    + "</legend>\n"
                    + content.toString()
                    + sp(--in) + "</fieldset>\n");
        }
        return ("");
    }

    /**
     * Produces an HTML <code>fieldset</code> presenting all errors stored in
     * the given <code>StringBuilder</code>.
     * 
     * @param errors the list of errors
     * @param in the indentation length (<i>rendered into *2 spaces</i>)
     * 
     * @return The HTML presentation.
     */
    private static String errorFieldset (StringBuilder errors, long in) {
        return normalFieldset("Errors", null, errors, in);
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
     * @param req the servlet's request object
     * 
     * @return The current view.
     */
    public static String renderUsers(HttpServletRequest req) {
        // Stores the assembled HTML content
        StringBuilder b = new StringBuilder("\n");
        // Stores the accumulated error messages
        StringBuilder e = new StringBuilder();
        // Indentation spacer
        long in = 6;

        // Create a DB session
        sobjDB.startDBSession();
        // Get the various security managers
        UserManager secUM = sobjSecurity.getUserManager();
        GroupManager secGM = sobjSecurity.getGroupManager();
        PrivilegeManager secPM = sobjSecurity.getPrivilegeManager();
        ServiceUrlManager secSU = sobjSecurity.getServiceUrlManager();

        // Proceed only when at least the system user is available
        if (secUM.getUsers().length > 0) {
            // Request parameters
            String reqParAction        = "action";
            String reqParUserId        = "userId";
            String reqParGroupId       = "groupId";
            String reqParRightId       = "rightId";
            String reqParGroupName     = "newGroupName";
            String reqParUserName      = "userName";
            String reqParUserEmail     = "userEmail";
            String reqParUserPass      = "userPass";
            String reqParPassConf      = "passConf";
            String reqParViewList      = "showList";
            // Recognized "action" parameter's values
            String actValAddToGroup    = "addToGroup";
            String actValRemFromGroup  = "removeFromGroup";
            String actValReqNewGroup   = "reqNewGroup";
            String actValAddNewGroup   = "addNewGroup";
            String actValReqRemGroup   = "reqRemGroup";
            String actValConRemGroup   = "conRemGroup";
            String actValConEditGroup  = "conEditGroup";
            String actValReqNewUser    = "reqNewUser";
            String actValAddNewUser    = "addNewUser";
            String actValReqRemUser    = "reqRemUser";
            String actValConRemUser    = "conRemUser";
            String actValConEditUser   = "conEditUser";
            String actValReqService    = "reqService";
            String actValAddService    = "addService";
            // Request values
            Long   reqValUserId        = null;
            Long   reqValGroupId       = null;
            Long   reqValRightId       = null;
            String reqValGroupName     = null;
            String reqValUserName      = null;
            String reqValUserEmail     = null;
            String reqValUserPass      = null;
            String reqValPassConf      = null;
            String reqValViewList      = "users";
            String reqValAction        = "";
            // Selected user
            User selUser = null;
            // Selected group;
            Group selGroup = null;
            // Current colspan (max columns)
            long maxColspan = 1;

            // ===============================================================
            // Parse the servlet's request object
            // ===============================================================
            if (req != null) {
                // DEBUG: Dump the servlet's request parameter
                if (DEBUG) {
                    b.append(debugRequest(req));
                }
                // Retrieve the requested list view (if any)
                reqValViewList = req.getParameter(reqParViewList);
                if (reqValViewList == null) {
                    reqValViewList = "";
                }
                // Retrieve the selected user's DAO (if any)
                reqValUserId = fromString(req.getParameter(reqParUserId));
                if (reqValUserId != null) {
                    selUser = secUM.getUser(reqValUserId);
                }
                // Retrieve the selected group's DAO (if any)
                reqValGroupId = fromString(req.getParameter(reqParGroupId));
                if (reqValGroupId != null) {
                    selGroup = secGM.getGroup(reqValGroupId);
                }
                // Retrieve the selected editor's action
                reqValAction = req.getParameter(reqParAction);
                if (reqValAction == null) {
                    reqValAction = "";
                }
                else if (reqValAction != "") {
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
                    // Add new group to the system
                    else if (reqValAction.equalsIgnoreCase(actValAddNewGroup)) {
                        reqValAction = actValReqNewGroup;
                        // Retrieve the selected group name
                        reqValGroupName =
                            req.getParameter(reqParGroupName);
                        // Create the new group
                        if ((reqValGroupName != null)
                                && (reqValGroupName != "")) {
                            if (checkName(reqValGroupName) == false) {
                                e.append(sp(in)
                                        + "<b>Incorrect syntax:</b>"
                                        + "&nbsp;"
                                        + reqValGroupName
                                        + "<br/>\n");
                            }
                            else if (secGM.getGroup(reqValGroupName) == null) {
                                Group group =
                                    secGM.createGroup(reqValGroupName);
                                if (group != null) {
                                    selGroup = group;
                                    reqValAction = actValAddNewGroup;
                                }
                                else {
                                    e.append(sp(in)
                                            + "<b>Can not create group:</b>"
                                            + "&nbsp;"
                                            + reqValGroupName
                                            + "<br/>\n");
                                }
                            }
                            else {
                                e.append(sp(in)
                                        + "<b>This group already exists:</b>"
                                        + "&nbsp;"
                                        + reqValGroupName
                                        + "<br/>\n");
                            }
                        }
                        else {
                            e.append(sp(in)
                                    + "<b>You must specify a group name!</b>"
                                    + "<br/>\n");
                        }
                    }
                    // Remove existing group from the system
                    else if (reqValAction.equalsIgnoreCase(actValConRemGroup)) {
                        // Remove the selected group
                        if (selGroup != null) {
                            // Check (ignore case) if this is the system group
                            if (selGroup.getDescription().equalsIgnoreCase(
                                    sobjSecurity.getSystemGroup())) {
                                e.append(sp(in)
                                        + "<b>Denied system group removal!</b>"
                                        + "<br/>\n");
                            }
                            // Delete the selected group
                            else  {
                                if (secGM.deleteGroup(selGroup.getId())) {
                                    selGroup = null;
                                }
                                else {
                                    e.append(sp(in)
                                            + "<b>Can not remove group:</b>"
                                            + "&nbsp;"
                                            + reqValGroupName
                                            + "<br/>\n");
                                }
                            }
                        }
                        else {
                            e.append(sp(in)
                                    + "<b>You must select a group name!</b>"
                                    + "<br/>\n");
                        }
                    }
                    // Add new user to the system
                    else if (reqValAction.equalsIgnoreCase(actValAddNewUser)) {
                        reqValAction = actValReqNewUser;
                        // Retrieve the selected user parameters
                        reqValUserName =
                            req.getParameter(reqParUserName);
                        reqValUserEmail =
                            req.getParameter(reqParUserEmail);
                        reqValUserPass =
                            req.getParameter(reqParUserPass);
                        reqValPassConf =
                            req.getParameter(reqParPassConf);

                        // Check the user name
                        if ((reqValUserName == null)
                                || (reqValUserName.length() == 0)) {
                            e.append(sp(in)
                                    + "<b>You must specify an user name!</b>"
                                    + "<br/>\n");
                        }
                        else if (checkName(reqValUserName) == false) {
                            e.append(sp(in)
                                    + "<b>Incorrect syntax:</b>"
                                    + "&nbsp;"
                                    + reqValUserName
                                    + "<br/>\n");
                        }
                        // Check the email address
                        if ((reqValUserEmail == null)
                                || (reqValUserEmail.length() == 0)) {
                            e.append(sp(in)
                                    + "<b>You must specify an email address!</b>"
                                    + "<br/>\n");
                        }
                        else if (checkEmail(reqValUserEmail) == false) {
                            e.append(sp(in)
                                    + "<b>Incorrect syntax:</b>"
                                    + "&nbsp;"
                                    + reqValUserEmail
                                    + "<br/>\n");
                        }
                        // Check the passwords
                        if ((reqValUserPass == null)
                                || (reqValUserPass.length() == 0)) {
                            e.append(sp(in)
                                    + "<b>You must specify an account password!</b>"
                                    + "<br/>\n");
                        }
                        else if ((reqValPassConf == null)
                                || (reqValPassConf.length() == 0)) {
                            e.append(sp(in)
                                    + "<b>You must specify a confirmation password!</b>"
                                    + "<br/>\n");
                        }
                        else if (reqValUserPass.equals(reqValPassConf) == false) {
                            e.append(sp(in)
                                    + "<b>Both passwords do not match!</b>"
                                    + "<br/>\n");
                            reqValUserPass = null;
                            reqValPassConf = null;
                        }

                        // Create the new user
                        if (e.toString().length() == 0) {
                            if (secUM.getUser(reqValUserName) == null) {
                                User user =
                                    secUM.createUser(
                                            reqValUserName,
                                            reqValUserPass,
                                            reqValUserEmail);
                                if (user != null) {
                                    selUser = user;
                                    reqValAction = actValAddNewUser;
                                }
                                else {
                                    e.append(sp(in)
                                            + "<b>Can not create user:</b>"
                                            + "&nbsp;"
                                            + reqValUserName
                                            + "<br/>\n");
                                }
                            }
                            else {
                                e.append(sp(in)
                                        + "<b>Such user already exists:</b>"
                                        + "&nbsp;"
                                        + reqValUserName
                                        + "<br/>\n");
                            }
                        }
                    }
                    // Remove existing user from the system
                    else if (reqValAction.equalsIgnoreCase(actValConRemUser)) {
                        // Remove the selected user
                        if (selUser != null) {
                            // Check if this is the system user
                            if (selUser.getName().equals(
                                    sobjSecurity.getSystemUser())) {
                                e.append(sp(in)
                                        + "<b>Denied system user removal!</b>"
                                        + "<br/>\n");
                            }
                            // Delete the selected user
                            else  {
                                if (secUM.deleteUser(selUser.getId())) {
                                    selUser = null;
                                }
                                else {
                                    e.append(sp(in)
                                            + "<b>Can not remove user:</b>"
                                            + "&nbsp;"
                                            + reqValUserName
                                            + "<br/>\n");
                                }
                            }
                        }
                        else {
                            e.append(sp(in)
                                    + "<b>You must select an user name!</b>"
                                    + "<br/>\n");
                        }
                    }
                }
            }

            // ===============================================================
            // Create the form
            // ===============================================================
            b.append(sp(in) + "<form id=\"users\""
                    + " name=\"users\""
                    + " method=\"post\""
                    + " action=\"/users\">\n");

            // ===============================================================
            // Display the accumulated error messages (if any)
            // ===============================================================
            b.append(errorFieldset(e, ++in));

            // ===============================================================
            // "New group" editor
            // ===============================================================
            if ((reqValAction != null)
                    && (reqValAction.equalsIgnoreCase(actValReqNewGroup))) {
                b.append(sp(in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>New group" + "</legend>\n");
                b.append(sp(in) + "<table class=\"borderless\">");
                // Group name
                b.append(sp(++in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Group name</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">\n"
                        + sp(++in)
                        + "<input type=\"text\""
                        + " class=\"form\""
                        + " id=\"" + reqParGroupName + "\""
                        + " name=\"" + reqParGroupName + "\""
                        + " value=\"\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                // Toolbar
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td colspan=\"2\" class=\"borderless\">"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Apply\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValAddNewGroup + "';"
                        + "document.users.submit();\">"
                        + "&nbsp;"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Cancel\""
                        + " onclick=\"javascript:"
                        + "document.users.submit();\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                b.append(sp(--in) + "</table>");
                b.append(sp(--in) + "</fieldset>\n");
            }
            // ===============================================================
            // "Remove group" editor
            // ===============================================================
            else if ((reqValAction != null)
                    && (reqValAction.equalsIgnoreCase(actValReqRemGroup))) {
                b.append(sp(in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>Remove group" + "</legend>\n");
                b.append(sp(in) + "<table class=\"borderless\">");
                // Group name
                b.append(sp(++in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Group name</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">\n"
                        + sp(++in)
                        + "<select class=\"form\""
                        + " id=\"removeGroup\""
                        + " name=\"removeGroup\">\n");
                for (Group group : secGM.getGroups()) {
                    // Do not display the SQO-OSS system group
                    if (group.getDescription().equalsIgnoreCase(
                            sobjSecurity.getSystemGroup()) == false) {
                        b.append(sp(in) + "<option"
                                + " value=\"" + group.getId() + "\""
                                + (((selGroup != null)
                                        && (selGroup.getId() == group.getId()))
                                        ? " selected"
                                        : "")
                                + ">"
                                + group.getDescription()
                                + "</option>\n");
                    }
                }
                b.append(sp(in) + "</select>\n"
                        + sp(--in)
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                // Toolbar
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td colspan=\"2\" class=\"borderless\">"
                        + "<input type=\"button\""
                        + " style=\"width: 100px;\""
                        + " value=\"Remove\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValConRemGroup + "';"
                        + "document.getElementById('"
                        + reqParGroupId + "').value="
                        + "document.getElementById('removeGroup').value;"
                        + "document.users.submit();\">"
                        + "&nbsp;"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Cancel\""
                        + " onclick=\"javascript:"
                        + "document.users.submit();\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                b.append(sp(--in) + "</table>");
                b.append(sp(--in) + "</fieldset>\n");
            }
            // ===============================================================
            // "New user" editor
            // ===============================================================
            else if ((reqValAction != null)
                    && (reqValAction.equalsIgnoreCase(actValReqNewUser))) {
                b.append(sp(in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>New user" + "</legend>\n");
                b.append(sp(in) + "<table class=\"borderless\">");
                // User name
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Name</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">"
                        + "<input type=\"text\""
                        + " class=\"form\""
                        + " id=\"" + reqParUserName + "\""
                        + " name=\"" + reqParUserName + "\""
                        + " value=\""
                        + ((reqValUserName != null) ? reqValUserName : "" )
                        + "\">"
                        + "</td>\n"
                        + sp(--in) + "</tr>\n");
                // Email address
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Email</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">"
                        + "<input type=\"text\""
                        + " class=\"form\""
                        + " id=\"" + reqParUserEmail + "\""
                        + " name=\"" + reqParUserEmail + "\""
                        + " value=\""
                        + ((reqValUserEmail != null) ? reqValUserEmail : "" )
                        + "\">"
                        + "</td>\n"
                        + sp(--in) + "</tr>\n");
                // Account password
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Password</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">"
                        + "<input type=\"password\""
                        + " class=\"form\""
                        + " id=\"" + reqParUserPass + "\""
                        + " name=\"" + reqParUserPass + "\""
                        + " value=\""
                        + ((reqValUserPass != null) ? reqValUserPass : "" )
                        + "\">"
                        + "</td>\n"
                        + sp(--in) + "</tr>\n");
                // Confirmation password
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Confirm</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">"
                        + "<input type=\"password\""
                        + " class=\"form\""
                        + " id=\"" + reqParPassConf + "\""
                        + " name=\"" + reqParPassConf + "\""
                        + " value=\""
                        + ((reqValPassConf != null) ? reqValPassConf : "" )
                        + "\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                // Toolbar
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td colspan=\"2\" class=\"borderless\">"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Apply\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValAddNewUser + "';"
                        + "document.users.submit();\">"
                        + "&nbsp;"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Cancel\""
                        + " onclick=\"javascript:"
                        + "document.users.submit();\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                b.append(sp(--in) + "</table>");
                b.append(sp(--in) + "</fieldset>\n");
            }
            // ===============================================================
            // "Remove user" editor
            // ===============================================================
            else if ((reqValAction != null)
                    && (reqValAction.equalsIgnoreCase(actValReqRemUser))) {
                b.append(sp(in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>Remove user" + "</legend>\n");
                b.append(sp(in) + "<table class=\"borderless\">");
                // User name
                b.append(sp(++in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>User name</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">\n"
                        + sp(++in)
                        + "<select class=\"form\""
                        + " id=\"removeUser\""
                        + " name=\"removeUser\">\n");
                for (User user : secUM.getUsers()) {
                    // Do not display the SQO-OSS system group
                    if (user.getName().equalsIgnoreCase(
                            sobjSecurity.getSystemUser()) == false) {
                        b.append(sp(in) + "<option"
                                + " value=\"" + user.getId() + "\""
                                + (((selUser != null)
                                        && (selUser.getId() == user.getId()))
                                        ? " selected"
                                        : "")
                                + ">"
                                + user.getName()
                                + "</option>\n");
                    }
                }
                b.append(sp(in) + "</select>\n"
                        + sp(--in)
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                // Toolbar
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td colspan=\"2\" class=\"borderless\">"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Remove\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValConRemUser + "';"
                        + "document.getElementById('"
                        + reqParUserId + "').value="
                        + "document.getElementById('removeUser').value;"
                        + "document.users.submit();\">"
                        + "&nbsp;"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Cancel\""
                        + " onclick=\"javascript:"
                        + "document.users.submit();\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                b.append(sp(--in) + "</table>");
                b.append(sp(--in) + "</fieldset>\n");
            }
            // ===============================================================
            // "Add service" editor
            // ===============================================================
            else if ((reqValAction != null)
                    && (reqValAction.equalsIgnoreCase(actValReqService))) {
                b.append(sp(in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>Add service"
                        + ((selGroup != null) 
                                ? " to group" + selGroup.getDescription() 
                                        : "")
                        + "</legend>\n");
                b.append(sp(in) + "<table class=\"borderless\">");
                // Service name
                b.append(sp(++in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>User name</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">\n"
                        + sp(++in)
                        + "<select class=\"form\""
                        + " id=\"addService\""
                        + " name=\"addService\">\n");
                for (ServiceUrl service : secSU.getServiceUrls()) {
                        b.append(sp(in) + "<option"
                                + " value=\"" + service.getId() + "\""
                                + ">"
                                + service.getUrl()
                                + "</option>\n");
                }
                b.append(sp(in) + "</select>\n"
                        + sp(--in)
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                // Toolbar
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td colspan=\"2\" class=\"borderless\">"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Remove\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValConRemUser + "';"
                        + "document.getElementById('"
                        + reqParUserId + "').value="
                        + "document.getElementById('removeUser').value;"
                        + "document.users.submit();\">"
                        + "&nbsp;"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Cancel\""
                        + " onclick=\"javascript:"
                        + "document.users.submit();\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                b.append(sp(--in) + "</table>");
                b.append(sp(--in) + "</fieldset>\n");
            }
            // ===============================================================
            // Main viewers and editors
            // ===============================================================
            else {
                // Create the fieldset for the "user" views
                if ((reqValViewList.equals("users")) || (selUser != null)) {
                    b.append(sp(++in) + "<fieldset>\n");
                    b.append(sp(++in) + "<legend>"
                            + ((selUser != null)
                                    ? "User " + selUser.getName()
                                    : "All users")
                            + "</legend>\n");
                }
                // Create the fieldset for the "group" views
                else if ((reqValViewList.equals("groups")) || (selGroup != null)) {
                    b.append(sp(++in) + "<fieldset>\n");
                    b.append(sp(++in) + "<legend>"
                            + ((selGroup != null)
                                    ? "Group " + selGroup.getDescription()
                                    : "All groups")
                            + "</legend>\n");
                }

                b.append(sp(in) + "<table>\n");
                b.append(sp(++in) + "<thead>\n");
                b.append(sp(++in) + "<tr class=\"head\">\n");

                // ===========================================================
                // User editor - header row
                // ===========================================================
                if (selUser != null) {
                    b.append(sp(++in) + "<td class=\"head\""
                            + " style=\"width: 40%;\">"
                            + "Account Details</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "Member Of</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "Available Groups</td>\n");
                    maxColspan = 3;
                }
                // ===========================================================
                // Users list - header row
                // ===========================================================
                else if (reqValViewList.equals("users")) {
                    b.append(sp(++in) + "<td class=\"head\""
                            + " style=\"width: 10%;\">"
                            + "User Id</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "User Name</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "User Email</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "Created</td>\n");
                    maxColspan = 4;
                }
                // ===========================================================
                // Group editor - header row
                // ===========================================================
                else if (selGroup != null) {
                    b.append(sp(++in) + "<td class=\"head\""
                            + " style=\"width: 40%;\">"
                            + "Resource Name</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "Privilege Type</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "Privilege Value</td>\n");
                    maxColspan = 3;
                }
                // ===========================================================
                // Groups list - header row
                // ===========================================================
                else if (reqValViewList.equals("groups")) {
                    b.append(sp(++in) + "<td class=\"head\""
                            + " style=\"width: 10%;\">"
                            + "Group Id</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 90%;\">"
                            + "Group Name</td>\n");
                    maxColspan = 2;
                }

                b.append(sp(--in) + "</tr>\n");
                b.append(sp(--in) + "</thead>\n");
                b.append(sp(in) + "<tbody>\n");

                // ===========================================================
                // User editor - content rows
                // ===========================================================
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
                            + " id=\"attachedGroups\" name=\"attachedGroups\""
                            + " size=\"4\""
                            + " style=\"width: 100%; border: 0;\""
                            + "onchange=\""
                            + "document.getElementById('"
                            + reqParGroupId + "').value="
                            + "document.getElementById('attachedGroups').value;"
                            + "document.users.submit();\""
                            + "\""
                            + ">\n");
                    sp(++in);
                    for (Object memberOf : selUser.getGroups()) {
                        Group group = (Group) memberOf;
                        boolean selected = ((selGroup != null)
                                && (selGroup.getId() == group.getId()));
                        b.append(sp(in) + "<option"
                                + " value=\"" + group.getId() + "\""
                                + ((selected) ? " selected" : "")
                                + ">"
                                + group.getDescription()
                                + "</option>\n");
                    }
                    b.append(sp(--in) + "</select>\n");
                    b.append(sp(--in) + "</td>\n");
                    // Display all group where the selected user is not a member
                    b.append(sp(in) + "<td>\n");
                    b.append(sp(++in) + "<select"
                            + " id=\"availableGroups\" name=\"availableGroups\""
                            + " size=\"4\""
                            + " style=\"width: 100%; border: 0;\""
                            + "onchange=\""
                            + "document.getElementById('"
                            + reqParGroupId + "').value="
                            + "document.getElementById('availableGroups').value;"
                            + "document.users.submit();\""
                            + "\""
                            + ">\n");
                    sp(++in);
                    for (Group group : secGM.getGroups()) {
                        // Skip groups where this user is already a member 
                        if (selUser.getGroups().contains(group) == false) {
                            boolean selected = ((selGroup != null)
                                    && (selGroup.getId() == group.getId()));
                            b.append(sp(in) + "<option"
                                    + " value=\"" + group.getId() + "\""
                                    + ((selected) ? " selected" : "")
                                    + ">"
                                    + group.getDescription()
                                    + "</option>\n");
                        }
                    }
                    b.append(sp(--in) + "</select>\n");
                    b.append(sp(--in) + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                }
                // ===========================================================
                // Users list -content rows
                // ===========================================================
                else if (reqValViewList.equals("users")) {
                    for (User nextUser : secUM.getUsers()) {
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
                // ===========================================================
                // Group editor - content rows
                // ===========================================================
                else if (selGroup != null) {
                    if (selGroup.getGroupPrivileges().isEmpty()) {
                        b.append(sp(++in) + "<tr>\n");
                        b.append(sp(++in) + "<td"
                                + " colspan=\"" + maxColspan + "\""
                                + " class=\"noattr\""
                                + ">"
                                + "This group has no attached resources."
                                + "</td>\n");
                        b.append(sp(--in) + "</tr>\n");
                    }
                    else {
                        for (Object priv : selGroup.getGroupPrivileges()) {
                            b.append(sp(++in) + "<tr>\n");
                            // Cast to a GroupPrivilege and display it
                            GroupPrivilege grPriv = (GroupPrivilege) priv;
                            // Service name
                            b.append(sp(++in) + "<td>"
                                    + grPriv.getUrl().getUrl()
                                    + "</td>\n");
                            // Privilege type
                            b.append(sp(in) + "<td>"
                                    + grPriv.getPv().getPrivilege().getDescription()
                                    + "</td>\n");
                            // Privilege value
                            b.append(sp(in) + "<td>"
                                    + grPriv.getPv().getValue()
                                    + "</td>\n");
                            b.append(sp(--in) + "</tr>\n");
                        }
                    }
                }
                // ===========================================================
                // Groups list -content rows
                // ===========================================================
                else if (reqValViewList.equals("groups")) {
                    for (Group nextGroup : secGM.getGroups()) {
                        String htmlEditGroup = "<td class=\"edit\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParGroupId + "').value='"
                            + nextGroup.getId() + "';"
                            + "document.users.submit();\">"
                            + "<img src=\"/edit.png\" alt=\"[Edit]\"/>"
                            + nextGroup.getDescription()
                            + "</td>\n";
                        b.append(sp(++in) + "<tr>\n");
                        b.append(sp(++in) + "<td>"
                                + nextGroup.getId()
                                + "</td>\n");
                        b.append(sp(in) + htmlEditGroup);
                        b.append(sp(--in) + "</tr>\n");
                    }
                }

                // ===============================================================
                // User editor - toolbar
                // ===============================================================
                if ((selUser != null)
                    && (selUser.getName().equals(
                            sobjSecurity.getSystemUser()) == false)) {
                    // Create the toolbar
                    b.append(sp(in) + "<tr>\n");
                    // User modifications
                    b.append(sp(++in) + "<td>\n");
                    b.append(sp(++in) + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"Edit\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValConEditUser + "';"
                            + "document.users.submit();\""
                            + ">\n");
                    b.append(sp(in) + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"Remove\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValConRemUser + "';"
                            + "document.users.submit();\""
                            + ">\n");
                    b.append(sp(--in) + "</td>\n");
                    // Detach group
                    b.append(sp(in) + "<td>\n");
                    if ((selGroup != null)
                            && (selUser.getGroups().contains(selGroup) == true)) {
                        b.append(sp(++in) + "<input type=\"button\""
                                + " class=\"install\""
                                + " style=\"width: 100px;\""
                                + " value=\"Detach\""
                                + " onclick=\"javascript:"
                                + "document.getElementById('"
                                + reqParAction + "').value='"
                                + actValRemFromGroup + "';"
                                + "document.users.submit();\""
                                + ">\n");
                        in--;
                    }
                    b.append(sp(in) + "</td>\n");
                    // Assign group 
                    b.append(sp(in) + "<td>\n");
                    if ((selGroup != null)
                            && (selUser.getGroups().contains(selGroup) == false)) {
                        b.append(sp(++in) + "<input type=\"button\""
                                + " class=\"install\""
                                + " style=\"width: 100px;\""
                                + " value=\"Assign\""
                                + " onclick=\"javascript:"
                                + "document.getElementById('"
                                + reqParAction + "').value='"
                                + actValAddToGroup + "';"
                                + "document.users.submit();\""
                                + ">\n");
                        in--;
                    }
                    b.append(sp(in) + "</td>\n");
                    // Close the toolbar
                    b.append(sp(--in) + "</tr>\n");
                }
                // ===============================================================
                // Group editor - toolbar
                // ===============================================================
                else if ((selGroup != null)
                    && (selGroup.getDescription().equals(
                            sobjSecurity.getSystemGroup()) == false)) {
                    // Create the toolbar
                    b.append(sp(in) + "<tr>\n");
                    // Group modifications
                    b.append(sp(in++) + "<td colspan=\""
                            + maxColspan
                            + "\">\n");
                    b.append(sp(++in) + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"Add Resource\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValReqService + "';"
                            + "document.users.submit();\""
                            + ">\n");
                    b.append(sp(in) + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"Remove\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValConRemGroup + "';"
                            + "document.users.submit();\""
                            + ">\n");
                    // Close the toolbar
                    b.append(sp(--in) + "</tr>\n");
                }

                // ===========================================================
                // Common toolbar
                // ===========================================================
                b.append(sp(in++) + "<tr class=\"subhead\">\n");
                b.append(sp(in++) + "<td colspan=\"" + maxColspan + "\">\n"
                        // List users
                        + sp(in)
                        + ((reqValViewList.equals("users") == false)
                                ? "<input type=\"button\""
                                        + " class=\"install\""
                                        + " style=\"width: 100px;\""
                                        + " value=\"Users list\""
                                        + " onclick=\"javascript:"
                                        + " document.getElementById('"
                                        + reqParViewList + "').value='users';"
                                        + " document.getElementById('"
                                        + reqParUserId + "').value='';"
                                        + " document.getElementById('"
                                        + reqParGroupId + "').value='';"
                                        + "document.users.submit();\">\n"
                                        : ""
                        )
                        // List groups
                        + sp(in)
                        + ((reqValViewList.equals("groups") == false)
                                ? "<input type=\"button\""
                                        + " class=\"install\""
                                        + " style=\"width: 100px;\""
                                        + " value=\"Groups list\""
                                        + " onclick=\"javascript:"
                                        + " document.getElementById('"
                                        + reqParViewList + "').value='groups';"
                                        + " document.getElementById('"
                                        + reqParUserId + "').value='';"
                                        + " document.getElementById('"
                                        + reqParGroupId + "').value='';"
                                        + "document.users.submit();\">\n"
                                        : ""
                        )
                        // Add User
                        + sp(in)
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Add user\""
                        + " onclick=\"javascript:"
                        + " document.getElementById('"
                        + reqParGroupId + "').value='';"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValReqNewUser + "';"
                        + "document.users.submit();\">\n"
                        // Remove User
                        + sp(in)
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Remove user\""
                        + " onclick=\"javascript:"
                        + " document.getElementById('"
                        + reqParGroupId + "').value='';"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValReqRemUser + "';"
                        + "document.users.submit();\">\n"
                        // Add group
                        + sp(in)
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Add group\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValReqNewGroup + "';"
                        + "document.users.submit();\">\n"
                        // Remove Group
                        + sp(in)
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Remove group\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValReqRemGroup + "';"
                        + "document.users.submit();\">\n"
                        + sp(--in)
                        + "</td>\n");
                b.append(sp(--in) + "</tr>\n");

                // Close the table
                b.append(sp(--in) + "</tbody>\n");
                b.append(sp(--in) + "</table>\n");
                b.append(sp(--in) + "</fieldset>\n");

                // ===============================================================
                // "Selected group" viewer
                // ===============================================================
                if ((selUser != null) && (selGroup != null)) {
                    b.append(sp(in) + "<fieldset>\n");
                    b.append(sp(++in) + "<legend>Group "
                            + selGroup.getDescription() + "</legend\n>");
                    b.append(sp(in) + "<table>\n");

                    b.append(sp(++in) + "<thead>\n");
                    b.append(sp(++in) + "<td class=\"head\""
                            + " style=\"width: 40%;\">"
                            + "Resource Name</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "Privilege Type</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "Privilege Value</td>\n");
                    b.append(sp(--in) + "</thead>\n");
                    maxColspan = 3;

                    b.append(sp(in) + "<tbody>\n");
                    if (selGroup.getGroupPrivileges().isEmpty()) {
                        b.append(sp(++in) + "<tr>\n");
                        b.append(sp(++in) + "<td"
                                + " colspan=\"" + maxColspan + "\""
                                + " class=\"noattr\""
                                + ">"
                                + "This group has no attached resources."
                                + "</td>\n");
                        b.append(sp(--in) + "</tr>\n");
                    }
                    else {
                        for (Object priv : selGroup.getGroupPrivileges()) {
                            b.append(sp(++in) + "<tr>\n");
                            // Cast to a GroupPrivilege and display it
                            GroupPrivilege grPriv = (GroupPrivilege) priv;
                            // Service name
                            b.append(sp(++in) + "<td>"
                                    + grPriv.getUrl().getUrl()
                                    + "</td>\n");
                            // Privilege type
                            b.append(sp(in) + "<td>"
                                    + grPriv.getPv().getPrivilege().getDescription()
                                    + "</td>\n");
                            // Privilege value
                            b.append(sp(in) + "<td>"
                                    + grPriv.getPv().getValue()
                                    + "</td>\n");
                            b.append(sp(--in) + "</tr>\n");
                        }
                    }

//                    // "Available rights" header
//                    if (secPM.getPrivileges().length > 0) {
//                        b.append(sp(in) + "<tr class=\"subhead\">\n");
//                        b.append(sp(++in) + "<td class=\"subhead\" colspan=\"4\">"
//                                + "Available</td>");
//                        b.append(sp(--in) + "</tr>\n");
//                        // "Available rights" list
//                        for (Privilege privilege : secPM.getPrivileges()) {
//                            b.append(sp(in) + "<tr>\n");
//                            // Action bar
//                            b.append(sp(++in) + "<td>"
//                                    + "&nbsp;"
//                                    + "</td>\n");
//                            // Available services
//                            b.append(sp(in) + "<td>"
//                                    + "&nbsp;"
//                                    + "</td>\n");
//                            // Available privileges
//                            b.append(sp(in) + "<td>"
//                                    + privilege.getDescription()
//                                    + "</td>\n");
//                            // Available rights
//                            b.append(sp(in) + "<td>\n");
//                            PrivilegeValue[] values =
//                                secPM.getPrivilegeValues(privilege.getId());
//                            if ((values != null) && (values.length > 0)) {
//                                b.append(sp(++in) + "<select"
//                                        + " style=\"width: 100%; border: 0;\""
//                                        + ">\n");
//                                in++;
//                                for (PrivilegeValue value : values) {
//                                    b.append(sp(in) + "<option"
//                                            + " value=\"" + value.getId() + "\""
//                                            + " onclick=\"javascript:"
//                                            + " document.getElementById('"
//                                            + reqParRightId + "').value='"
//                                            + value.getId() + "';"
//                                            + "document.users.submit();\""
//                                            + ">"
//                                            + value.getValue()
//                                            + "</option>\n");
//                                }
//                                b.append(sp(--in) + "</select>\n");
//                            }
//                            else {
//                                b.append(sp(++in) + "<b>NA</b>");
//                            }
//                            b.append(sp(--in) + "</td>\n");
//                            b.append(sp(--in) + "</tr>\n");
//                        }
//                    }

                    b.append(sp(--in) + "</tbody>\n");

                    b.append(sp(--in) + "</table>\n");
                    b.append(sp(--in) + "</fieldset>\n");
                }
            }

            // ===============================================================
            // INPUT FIELDS
            // ===============================================================
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
            // "View list" input field
            b.append(sp(in) + "<input type=\"hidden\""
                    + " id=\"" + reqParViewList + "\"" 
                    + " name=\"" + reqParViewList + "\""
                    + " value=\"\">\n");

            // ===============================================================
            // Close the form
            // ===============================================================
            b.append(sp(--in) + "</form>\n");
        }
        else {
            b.append(normalFieldset(
                    "Users list",
                    null,
                    new StringBuilder("No users found!"),
                    in));
        }
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

    public static void logRequest(String request) {
        sobjLogger.info(request);
    }  
}
