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

import java.net.URI;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;

public abstract class AbstractView {
    // Core components
    protected static AlitheiaCore sobjCore = null;
    protected static ServiceReference srefCore = null;

    // Critical logging components
    protected static LogManager sobjLogManager = null;
    protected static Logger sobjLogger = null;

    // Service components
    protected static DBService sobjDB = null;
    protected static MetricActivator compMA = null;
    protected static PluginAdmin sobjPA = null;
    protected static Scheduler sobjSched = null;
    protected static TDSService sobjTDS = null;
    protected static UpdaterService sobjUpdater = null;
    protected static ClusterNodeService sobjClusterNode = null;
    protected static SecurityManager sobjSecurity = null;

    // Velocity stuff
    protected VelocityContext vc = null;
    protected BundleContext bc = null;

    // Names of the various resource files
    private static String RES_LABELS_FILE   = "ResourceLabels";
    private static String RES_ERRORS_FILE   = "ResourceErrors";
    private static String RES_MESSAGES_FILE = "ResourceMessages";

    // Resource bundles
    private static ResourceBundle resLbl = null;
    private static ResourceBundle resMsg = null;
    private static ResourceBundle resErr = null;

    // Debug flag - global for all views
    protected static boolean DEBUG = false;
    
    // Some constants that are used internally
    private static String NULL_PARAM_NAME = "Undefined parameter name!";

    /**
     * Instantiates a new <code>AbstractView</code> object.
     * 
     * @param bundlecontext the parent bundle's context
     * @param vc the Velocity instance's context
     */
    public AbstractView(BundleContext bundlecontext, VelocityContext vc) {
        // Keep the Velocity context instance
        this.vc = vc;
        this.bc = bundlecontext;

        // Retrieve the SQO-OSS core service's object
        srefCore = bundlecontext.getServiceReference(
                AlitheiaCore.class.getName());
        if (srefCore != null)
            sobjCore = (AlitheiaCore) bundlecontext.getService(srefCore);
        else
            System.out.println("ERROR"
                    + " " + Logger.NAME_SQOOSS_WEBADMIN
                    + " - " + "Can not find the SQO-OSS core service!");

        // Retrieve the instances of the core components
        if (sobjCore != null) {
            //Get the log manager's instance
            sobjLogManager = sobjCore.getLogManager();
            if (sobjLogManager != null) {
                // Instantiate a dedicated logger 
                sobjLogger = sobjLogManager.createLogger(
                        Logger.NAME_SQOOSS_WEBADMIN);
            }

            // Get the database component's instance
            sobjDB = sobjCore.getDBService();
            if ((sobjDB == null) && (sobjLogger != null))
                sobjLogger.debug("Could not get the database component's instance.");

            // Get the plug-in admin's instance
            sobjPA = sobjCore.getPluginAdmin();
            if ((sobjPA == null) && (sobjLogger != null))
                sobjLogger.debug("Could not get the plug-in admin's instance.");

            // Get the scheduler's instance
            sobjSched = sobjCore.getScheduler();
            if ((sobjSched == null) && (sobjLogger != null))
                sobjLogger.debug("Could not get the scheduler's instance.");

            // Get the metric activator's instance
            compMA = sobjCore.getMetricActivator();
            if ((compMA == null) && (sobjLogger != null))
                sobjLogger.debug("Could not get the metric activator's instance.");

            // Get the TDS component's instance
            sobjTDS = sobjCore.getTDSService();
            if ((sobjTDS == null) && (sobjLogger != null))
                sobjLogger.debug("Could not get the TDS component's instance.");

            // Get the updater component's instance
            sobjUpdater = sobjCore.getUpdater();
            if ((sobjUpdater == null) && (sobjLogger != null))
                sobjLogger.debug("Could not get the updater component's instance.");

            // Get the ClusterNodeService component's instance
            sobjClusterNode = sobjCore.getClusterNodeService();
            if ((sobjClusterNode != null) && (sobjLogger != null))
                sobjLogger.debug("Got the ClusterNodeService component's instance.");

            
            // Get the security manager's instance
            sobjSecurity = sobjCore.getSecurityManager();
            if ((sobjSecurity == null) && (sobjLogger != null))
                sobjLogger.debug("Could not get the security manager's instance.");
        }
    }

    /**
     * Initializes the various resource bundle with the specified locale.
     * 
     * @param locale the user's locale
     */
    public static void initResources (Locale locale) {
        resLbl = getLabelsBundle(locale);
        resErr = getErrorsBundle(locale);
        resMsg = getMessagesBundle(locale);
    }

    /**
     * Retrieves the value of the given resource property from the
     * resource bundle that stores all label strings.
     * 
     * @param name the name of the resource property
     * 
     * @return The property's value, when that property can be found in the
     *   corresponding resource bundle, OR the provided property name's
     *   parameter, when such property is missing.
     */
    public static String getLbl (String name) {
        if (resLbl != null) {
            try {
                return resLbl.getString(name);
            }
            catch (NullPointerException ex) {
                return NULL_PARAM_NAME;
            }
            catch (MissingResourceException ex) {
                return name;
            }
        }
        return name;
    }

    /**
     * Retrieves the value of the given resource property from the
     * resource bundle that stores all error strings.
     * 
     * @param name the name of the resource property
     * 
     * @return The property's value, when that property can be found in the
     *   corresponding resource bundle, OR the provided property name's
     *   parameter, when such property is missing.
     */
    public static String getErr (String name) {
        if (resErr != null) {
            try {
                return resErr.getString(name);
            }
            catch (NullPointerException ex) {
                return NULL_PARAM_NAME;
            }
            catch (MissingResourceException ex) {
                return name;
            }
        }
        return name;
    }

    /**
     * Retrieves the value of the given resource property from the
     * resource bundle that stores all message strings.
     * 
     * @param name the name of the resource property
     * 
     * @return The property's value, when that property can be found in the
     *   corresponding resource bundle, OR the provided property name's
     *   parameter, when such property is missing.
     */
    public static String getMsg (String name) {
        if (resMsg != null) {
            try {
                return resMsg.getString(name);
            }
            catch (NullPointerException ex) {
                return NULL_PARAM_NAME;
            }
            catch (MissingResourceException ex) {
                return name;
            }
        }
        return name;
    }

    // TODO: Move this method's logic in the initResources() once all views
    // are using the new methods.
	public static ResourceBundle getLabelsBundle(Locale locale) {
		locale = Locale.ENGLISH;
		return ResourceBundle.getBundle(RES_LABELS_FILE, locale);
	}

    // TODO: Move this method's logic in the initResources() once all views
    // are using the new methods.
	public static ResourceBundle getErrorsBundle(Locale locale) {
		locale = Locale.ENGLISH;
		return ResourceBundle.getBundle(RES_ERRORS_FILE, locale);
	}

    // TODO: Move this method's logic in the initResources() once all views
    // are using the new methods.
    public static ResourceBundle getMessagesBundle (Locale locale) {
    	locale = Locale.ENGLISH;
    	return ResourceBundle.getBundle(RES_MESSAGES_FILE, locale);
    }

    /**
     * Construct an HTML-based list of all parameters and their values, that
     * are contained in the given servlet's request object. Useful for debug
     * of the views functionality.
     * 
     * @param request the servlet's request object
     * 
     * @return The list of request parameters.
     */
    protected static String debugRequest (HttpServletRequest request) {
        StringBuilder b = new StringBuilder();
        Enumeration<?> e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            b.append(key + "=" + request.getParameter(key) + "<br/>\n");
        }
        return b.toString();
    }

    /**
     * Generates a string that contains a <b>2*num</b> spaces.
     * <br/>
     * <i>Used for indentation of the HTML content that is generated by the
     * various views.</i>
     * 
     * @param num the indentation depth
     * 
     * @return The indentation string.
     */
    protected static String sp (long num) {
        StringBuilder b = new StringBuilder();
        for (long i = 0; i < num; i++)
            b.append("  ");
        return b.toString();
    }

    /**
     * Generates a simple table row (<i>with two columns</i>) that represents
     * a single text input element with a title line. The title line will be
     * stored in the first cell, while the text input will be placed in the
     * second cell.
     * <br/>
     * <i>This method is used by the various views for generating simple input
     * screens.</i>
     * 
     * @param title the title that will preceed the input element
     * @param parName the input element's name
     * @param parValue the input element's initial value
     * @param in the indentation depth
     * 
     * @return The string that contains the table's row, or an empty string
     *   upon invalid (<code>null</code>) name of the input element.
     */
    protected static String normalInputRow (
            String title, String parName, String parValue, long in) {
        // Stores the assembled HTML content
        StringBuilder b = new StringBuilder("\n");

        // Create the input field's row
        if (parName != null) {
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in) + "<td class=\"borderless\""
                    + " style=\"width:100px;\">"
                    + "<b>" + ((title != null) ? title : "") + "</b>"
                    + "</td>\n");
            b.append(sp(in++) + "<td class=\"borderless\">\n");
            b.append(sp(in) + "<input type=\"text\""
                    + " class=\"form\""
                    + " id=\"" + parName + "\""
                    + " name=\"" + parName + "\""
                    + " value=\""
                    + ((parValue != null) ? parValue : "" )
                    + "\" size=\"60\">\n");
            b.append(sp(--in) + "</td>\n");
            b.append(sp(--in) + "</tr>\n");
        }

        // Return the generated content
        return b.toString();
    }

    /**
     * Generates a simple table row (<i>with two columns</i>) that represents
     * a single text message with a title line. The title line will be
     * stored in the first cell, while the message will be placed in the
     * second cell.
     * <br/>
     * <i>This method is used by the various views for generating simple info
     * screens.</i>
     * 
     * @param title the title that will preceed the text message
     * @param value the text message
     * @param in the indentation depth
     * 
     * @return The string that contains the table's row.
     */
    protected static String normalInfoRow (
            String title, String value, long in) {
        // Stores the assembled HTML content
        StringBuilder b = new StringBuilder("\n");

        // Create the info row
        b.append(sp(in++) + "<tr>\n");
        b.append(sp(in) + "<td class=\"borderless\""
                + " style=\"width:100px;\">"
                + "<b>" + ((title != null) ? title : "") + "</b>"
                + "</td>\n");
        b.append(sp(in++) + "<td class=\"borderless\">\n");
        b.append(sp(in) + ((value != null) ? value : "") + "\n");
        b.append(sp(--in) + "</td>\n");
        b.append(sp(--in) + "</tr>\n");
        
        // Return the generated content
        return b.toString();
    }

    /**
     * Produces an HTML fieldset tag which encapsulates the HTML
     * content that is stored in the given <code>StringBuilder</code> object.
     * 
     * @param name the fieldset legend's name
     * @param css the CSS class name to use
     * @param content the HTML content
     * @param in the indentation depth
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

    // TODO: Remove this method, since it is not I18n compatible.
    protected static String errorFieldset (StringBuilder errors, long in) {
        return normalFieldset("Errors", null, errors, in);
    }

    /**
     * Creates a <code>Long</code> object from the content of the given
     * <code>String</code> object, while handling internally any thrown
     * exception.
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

    /**
     * Method for validation of a simple name-based properties.
     * <br/>
     * The validation will be successful on values that contain alphanumeric
     * characters, plus the space character (<i>as long as it does not appear
     * as first or last character in the sequence</i>).
     * 
     * @param text the property value
     * 
     * @return <code>true</code> upon successful validation,
     *   or <code>false</code> otherwise.
     */
    protected static boolean checkName (String text) {
        if (text == null) return false;

        // Check for head or foot occurrence of deprecated signs
        Pattern p = Pattern.compile("^[ ]+.*");
        if (p.matcher(text).matches()) return false;
        p = Pattern.compile(".*[ ]+$");
        if (p.matcher(text).matches()) return false;
        // Check the name
        p = Pattern.compile("[\\p{Alnum} ]+");
        return p.matcher(text).matches();
    }

    /**
     * Method for validation of a project name-based properties.
     * <br/>
     * The validation will be successful on values that contain alphanumeric
     * characters, plus the space and the underscore characters
     * (<i>as long as they do not appear as first or last character in the
     * sequence</i>).
     * 
     * @param text the property value
     * 
     * @return <code>true</code> upon successful validation,
     *   or <code>false</code> otherwise.
     */
    protected static boolean checkProjectName (String text) {
        if (text == null) return false;

        // Check for head or foot occurrence of deprecated signs
        Pattern p = Pattern.compile("^[ _\\-]+.*");
        if (p.matcher(text).matches()) return false;
        p = Pattern.compile(".*[ _\\-]+$");
        if (p.matcher(text).matches()) return false;
        // Check the name
        p = Pattern.compile("[\\p{Alnum}_\\- ]+");
        return p.matcher(text).matches();
    }

    /**
     * Method for validation of properties that hold an email address.
     * <br/>
     * The validation will be successful on values that satisfy the email
     * address specification from RFC 2822.
     * <br/>
     * <i>Note: this methods tries to follow RFC 2822 as much as possible,
     * but is not yet fully compatible with it.</i>
     * 
     * @param text the property value
     * 
     * @return <code>true</code> upon successful validation,
     *   or <code>false</code> otherwise.
     */
    protected static boolean checkEmail (String text) {
        if (text == null) return false;

        // Check for adjacent dot signs
        Pattern p = Pattern.compile("\\.\\.");
        if (p.matcher(text).matches()) return false;
        // Split the email into local and domain part
        String parts[] = text.split("@");
        if (parts.length != 2) return false;
        // Check for head or foot occurrence of dot signs
        p = Pattern.compile("^[.].*");
        if (p.matcher(parts[0]).matches()) return false;
        if (p.matcher(parts[1]).matches()) return false;
        p = Pattern.compile(".*[.]$");
        if (p.matcher(parts[0]).matches()) return false;
        if (p.matcher(parts[1]).matches()) return false;
        // Local part's regular expression
        Pattern l = Pattern.compile("^[\\p{Alnum}!#$%*/?|^{}`~&'+-=_.]+$");
        // Domain part's regular expression
        Pattern d = Pattern.compile("^[\\p{Alnum}.-]+[.][\\p{Alpha}]{2,4}$");
        // Match both parts
        return ((l.matcher(parts[0]).matches())
                && (d.matcher(parts[1]).matches()));
    }

    /**
     * Method for validation of properties that hold an URL string against
     * the given URL scheme(s). The validation is based on the specifications
     * for that scheme(s).
     * <br/>
     * The scheme sequence can contain a single scheme
     * (e.g. <code>"http"</code>), or two or more schemes separated by commas
     * (e.g. <code>"http,https,file"</code>).
     * <br/>
     * <br/>
     * <i><b>Note:</b> Not yet fully implemented. Right now this method checks
     * only if a scheme name does match.</i>
     * 
     * @param text the property value
     * @param schemes the list of comma separated scheme definitions
     * 
     * @return <code>true</code> upon successful validation,
     *   or <code>false</code> otherwise.
     */
    protected static boolean checkUrl (String text, String schemes) {
        if (text == null || schemes == null) 
            return false;

        URI toTest;
        try {
            toTest = URI.create(text);
            if (toTest.getScheme() == null)
                return false;
        } catch (IllegalArgumentException iae) {
            return false;
        }

        String[] urlschemes = schemes.split(",");
        if (urlschemes.length == 0)
            return false;

       for (String scheme : urlschemes) {
           if (toTest.getScheme().equals(scheme))
               return true;
       }
       return false;
    }

    /**
     * Check if the provided URL is supported by the TDS data accessor 
     * plug-ins.
     * @param url The URL to check
     * @return True if the URL is supported, false otherwise or if the 
     * provided string is not a URL.
     */
    protected static boolean checkTDSUrl (String url) {
        return sobjTDS.isURLSupported(url);
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
