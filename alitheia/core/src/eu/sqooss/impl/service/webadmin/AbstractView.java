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

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;

public abstract class AbstractView {
    // Core components
    private static AlitheiaCore sobjAlitheiaCore = null;
    protected static ServiceReference srefCore = null;

    // Critical logging components
    private static LogManager sobjLogManager = null;
    protected static Logger sobjLogger = null;

    // Service components
    protected static DBService sobjDB = null;
    protected static MetricActivator compMA = null;
    protected static PluginAdmin sobjPA = null;
    protected static Scheduler sobjSched = null;
    protected static TDSService sobjTDS = null;
    protected static UpdaterService sobjUpdater = null;
    protected static SecurityManager sobjSecurity = null;

    // Velocity stuff
    protected VelocityContext vc = null;

    // Names of the various resource files
    private static String RES_LABELS_FILE = "ResourceLabels";
    private static String RES_ERRORS_FILE = "ResourceErrors";

    // Debug flag
    protected static boolean DEBUG = false;

    public AbstractView(BundleContext bundlecontext, VelocityContext vc) {
        
        this.vc = vc;

        srefCore = bundlecontext.getServiceReference(AlitheiaCore.class.getName());
        if (srefCore != null) {
            sobjAlitheiaCore = (AlitheiaCore) bundlecontext.getService(srefCore);
        }
        else {
            System.out.println("No Alitheia Core found.");
        }

        if (sobjAlitheiaCore != null) {
            //Get the LogManager and Logger objects
            sobjLogManager = sobjAlitheiaCore.getLogManager();
            if (sobjLogManager != null) {
                sobjLogger = sobjLogManager.createLogger(
                        Logger.NAME_SQOOSS_WEBADMIN);
            }

            // Get the DB Service object
            sobjDB = sobjAlitheiaCore.getDBService();
            if (sobjDB != null) {
                sobjLogger.debug("WebAdmin got DB Service object.");
            }

            // Get the Plug-in Administration object
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
            compMA = sobjAlitheiaCore.getMetricActivator();
            if (compMA != null) {
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
    }

    public static ResourceBundle getLabelsBundle (Locale locale) {
        if (locale != null)
            return ResourceBundle.getBundle(RES_LABELS_FILE, locale);
        else
            return ResourceBundle.getBundle(RES_LABELS_FILE);
    }

    public static ResourceBundle getErrorsBundle (Locale locale) {
        if (locale != null)
            return ResourceBundle.getBundle(RES_ERRORS_FILE, locale);
        else
            return ResourceBundle.getBundle(RES_ERRORS_FILE);
    }

    protected static String debugRequest (HttpServletRequest request) {
        StringBuilder b = new StringBuilder();
        Enumeration<?> e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            b.append(key + "=" + request.getParameter(key) + "<br/>\n");
        }
        return b.toString();
    }

    protected static String sp (long num) {
        StringBuilder b = new StringBuilder();
        String space = "  ";
        for (long i = 0; i < num; i++) {
            b.append(space);
        }
        return b.toString();
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
    protected static String normalFieldset (
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
    protected static String errorFieldset (StringBuilder errors, long in) {
        return normalFieldset("Errors", null, errors, in);
    }

    /**
     * Converts a <code>String</code> into a <code>Long</code>,
     * while handling internally any thrown exception.
     * 
     * @param value the <code>String</code> value
     * 
     * @return The <code>Long</code> value.
     */
    protected static Long fromString (String value) {
        try {
            return (new Long(value));
        }
        catch (NumberFormatException ex){
            return null;
        }
    }

    protected static boolean checkName (String text) {
        Pattern p = Pattern.compile("[a-zA-Z0-9]*");
        return p.matcher(text).matches();
    }

    protected static boolean checkEmail (String text) {
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
}

//vi: ai nosi sw=4 ts=4 expandtab
