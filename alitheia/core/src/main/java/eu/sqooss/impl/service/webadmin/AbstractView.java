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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.Template;
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
    protected AlitheiaCore sobjCore = null;
    protected ServiceReference srefCore = null;

    // Critical logging components
    protected LogManager sobjLogManager = null;
    protected Logger sobjLogger = null;

    // Service components
    protected DBService sobjDB = null;
    protected MetricActivator compMA = null;
    protected PluginAdmin sobjPA = null;
    protected Scheduler sobjSched = null;
    protected TDSService sobjTDS = null;
    protected UpdaterService sobjUpdater = null;
    protected ClusterNodeService sobjClusterNode = null;
    protected SecurityManager sobjSecurity = null;

    // Velocity stuff
    protected VelocityContext vc = null;
    protected BundleContext bc = null;

    // Names of the various resource files
    private String RES_ERRORS_FILE   = "ResourceErrors";
    
    private String NULL_PARAM_NAME = "Undefined parameter name!";

    // Resource bundle
    private ResourceBundle resErr = null;

    // Error messages container
    List<String> errorMessages;
    List<String> debugMessages;
    
    // Debug flag - global for all views
    protected static final boolean DEBUG = true;
    
    // Some constants that are used internally


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
       
        sobjCore = AlitheiaCore.getInstance();
        errorMessages = new ArrayList<String>();
        debugMessages = new ArrayList<String>();
        vc.put("errorMessages", errorMessages);
        vc.put("debugMessages", debugMessages);
        
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
    public void initResources (Locale locale) {
        resErr = ResourceBundle.getBundle(RES_ERRORS_FILE, locale);
        vc.put("tr",new TranslationProxy(locale));
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
    public String getErr (String name) {
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
     * Construct an HTML-based list of all parameters and their values, that
     * are contained in the given servlet's request object. Useful for debug
     * of the views functionality.
     * 
     * @param request the servlet's request object
     * 
     * @return The list of request parameters.
     */
    protected String debugRequest (HttpServletRequest request) {
        StringBuilder b = new StringBuilder();
        Enumeration<?> e = request.getParameterNames();
        while (e != null && e.hasMoreElements()) {
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
    protected String sp (long num) {
        StringBuilder b = new StringBuilder();
        for (long i = 0; i < num; i++)
            b.append("  ");
        return b.toString();
    }
    
  
    protected Map<String,String> normalInputRowMap(String title, String parName, String parValue){
    	Map<String,String> field = new HashMap<String,String>(3);
    	field.put("title",(title != null) ? title : "");
    	field.put("name",(parName != null) ? parName : "");
    	field.put("value",(parValue != null) ? parValue : "");
    	return field;
    }

    protected Map<String,String> normalInfoRowMap(String title, String value){
    	Map<String,String> field = new HashMap<String,String>(2);
    	field.put("title",(title != null) ? title : "");
    	field.put("value",(value != null) ? value : "");
    	return field;
    }
    
    protected String velocityContextToString(String templateName){
        Template template = AdminServlet.getVelocityEngine().getTemplate(templateName);
        StringWriter writer = new StringWriter();
        template.merge(vc, writer);
        return writer.toString();
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
    protected Long fromString (String value) {
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
    protected boolean checkName (String text) {
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
    protected boolean checkProjectName (String text) {
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
    protected boolean checkEmail (String text) {
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
     * Check if the provided URL is supported by the TDS data accessor 
     * plug-ins.
     * @param url The URL to check
     * @return True if the URL is supported, false otherwise or if the 
     * provided string is not a URL.
     */
    protected boolean checkTDSUrl (String url) {
        return sobjTDS.isURLSupported(url);
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
