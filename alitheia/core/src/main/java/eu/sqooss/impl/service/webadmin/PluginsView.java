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

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMessages;

import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.pa.PluginInfo.ConfigurationType;
import eu.sqooss.service.util.StringUtils;

public class PluginsView extends AbstractView{
    public PluginsView(BundleContext bundlecontext, VelocityContext vc) {
        super(bundlecontext, vc);
    }

    /**
     * Renders the various plug-in's views.
     *
     * @param req the servlet's request object
     *
     * @return The HTML presentation of the generated view.
     */
    public String render(HttpServletRequest req) {
        // Clear error message buffer first
    	this.errorMessages.clear();
    	
    	// Clear debug messages
    	this.debugMessages.clear();

        // Request parameters
        String reqParAction        = "action";
        String reqParHashcode      = "pluginHashcode";
        String reqParPropName      = "propertyName";
        String reqParPropDescr     = "propertyDescription";
        String reqParPropType      = "propertyType";
        String reqParPropValue     = "propertyValue";
        String reqParShowProp      = "showProperties";
        String reqParShowActv      = "showActivators";
        // Recognized "action" parameter's values
        String actValInstall       = "installPlugin";
        String actValUninstall     = "uninstallPlugin";
        String actValSync          = "syncPlugin";
        String actValReqAddProp    = "createProperty";
        String actValReqUpdProp    = "updateProperty";
        String actValConAddProp    = "confirmProperty";
        String actValConRemProp    = "removeProperty";
        // Request values
        String reqValAction        = "";
        String reqValHashcode      = null;
        String reqValPropName      = null;
        String reqValPropDescr     = null;
        String reqValPropType      = null;
        String reqValPropValue     = null;
        boolean reqValShowProp     = false;         // Show plug-in properties
        boolean reqValShowActv     = false;         // Show plug-in activators
        // Info object of the selected plug-in
        PluginInfo selPI           = null;

        // Proceed only when at least one plug-in is registered
        if (sobjPA.listPlugins().isEmpty()) {
        	vc.put("noPluginsAvailable",true);
        }
        else {
            // ===============================================================
            // Parse the servlet's request object
            // ===============================================================
            if (req != null) {
                // Retrieve the selected editor's action (if any)
                reqValAction = req.getParameter(reqParAction);
                if (reqValAction == null) {
                    reqValAction = "";
                };
                // Retrieve the various display flags
                if ((req.getParameter(reqParShowProp) != null)
                        && (req.getParameter(reqParShowProp).equals("true"))) {
                    reqValShowProp = true;
                }
                if ((req.getParameter(reqParShowActv) != null)
                        && (req.getParameter(reqParShowActv).equals("true"))) {
                    reqValShowActv = true;
                }
                // Retrieve the selected configuration property's values
                if ((reqValAction.equals(actValConAddProp))
                        || (reqValAction.equals(actValReqUpdProp))
                        || (reqValAction.equals(actValConRemProp))) {
                    // Name, description, type and value
                    reqValPropName  = req.getParameter(reqParPropName);
                    reqValPropDescr = req.getParameter(reqParPropDescr);
                    reqValPropType  = req.getParameter(reqParPropType);
                    reqValPropValue = req.getParameter(reqParPropValue);
                }
                // Retrieve the selected plug-in's hash code
                reqValHashcode = req.getParameter(reqParHashcode);
                // Plug-in based actions
                if (reqValHashcode != null) {
                    // =======================================================
                    // Plug-in install request
                    // =======================================================
                    if (reqValAction.equals(actValInstall)) {
                        pluginInstallRequest(reqValHashcode);
                    }
                    // =======================================================
                    // Plug-in un-install request
                    // =======================================================
                    else if (reqValAction.equals(actValUninstall)) {
                        pluginUninstallRequest(reqValHashcode);
                    }
                }
                // Retrieve the selected plug-in's info object
                if (reqValHashcode != null) {
                    selPI = sobjPA.getPluginInfo(reqValHashcode);
                }
                // Plug-in info based actions
                if ((selPI != null) && (selPI.installed)) {
                    // =======================================================
                    // Plug-in synchronize (on all projects) request
                    // =======================================================
                    if (reqValAction.equals(actValSync)) {
                        compMA.syncMetrics(sobjPA.getPlugin(selPI));
                    }
                    // =======================================================
                    // Plug-in's configuration property removal
                    // =======================================================
                    else if (reqValAction.equals(actValConRemProp)) {
                        selPI = pluginConfigPropertyRemoval(reqValHashcode,
								reqValPropName, reqValPropType, selPI);
                        // Return to the update view upon error
                        if (errorMessages.size() > 0) {
                            reqValAction = actValReqUpdProp;
                        }
                    }
                    // =======================================================
                    // Plug-in's configuration property creation/update
                    // =======================================================
                    else if (reqValAction.equals(actValConAddProp)) {
                        // Check for a property update
                        boolean update = selPI.hasConfProp(
                                reqValPropName, reqValPropType);
                        // Update configuration property
                        selPI = pluginConfigurationUpdateCreateProperty(
								reqValHashcode, reqValPropName,
								reqValPropDescr, reqValPropType,
								reqValPropValue, selPI, update);
                        // Return to the create/update view upon error
                        if (errorMessages.size() > 0) { 
                            if (update) reqValAction = actValReqUpdProp;
                            else reqValAction = actValReqAddProp;
                        }
                    }
                }
            }

            // ===============================================================
            // "Create/update configuration property" editor
            // ===============================================================
            if ((selPI != null) && (selPI.installed)
                    && ((reqValAction.equals(actValReqAddProp))
                            || (reqValAction.equals(actValReqUpdProp)))) {
                createUpdateConfigurationProperty(reqParAction,
						reqParPropName, reqParPropDescr, reqParPropType,
						reqParPropValue, actValConAddProp, actValConRemProp,
						reqValPropName, reqValPropDescr, reqValPropType,
						reqValPropValue, selPI);
            }
            // ===============================================================
            // Plug-in editor
            // ===============================================================
            else if (selPI != null) {
                pluginEditForm(reqParAction, reqParHashcode,
						reqParPropName, reqParPropDescr, reqParPropType,
						reqParPropValue, actValInstall, actValUninstall,
						actValSync, actValReqAddProp, actValReqUpdProp, selPI);
            }
            // ===============================================================
            // Plug-ins list
            // ===============================================================
            else {
                pluginList(reqParHashcode, reqParShowProp,
						reqParShowActv, reqValShowProp, reqValShowActv);
            }

            vc.put("reqParAction",reqParAction);
            vc.put("reqParHashcode",reqParHashcode);
            vc.put("reqParPropName",reqParPropName);
            vc.put("reqParPropDescr",reqParPropDescr);
            vc.put("reqParPropType",reqParPropType);
            vc.put("reqParPropValue",reqParPropValue);
            vc.put("reqParShowProp",reqParShowProp);
            vc.put("reqParShowActv",reqParShowActv);
            vc.put("actValInstall",actValInstall);
            vc.put("actValUninstall",actValUninstall);
            vc.put("actValSync",actValSync);
            vc.put("actValReqAddProp",actValReqAddProp);
            vc.put("actValReqUpdProp",actValReqUpdProp);
            vc.put("actValConAddProp",actValConAddProp);
            vc.put("actValConRemProp",actValConRemProp);
            vc.put("reqValAction",reqValAction);
            vc.put("reqValHashcode",reqValHashcode);
            vc.put("reqValPropName",reqValPropName);
            vc.put("reqValPropDescr",reqValPropDescr);
            vc.put("reqValPropType",reqValPropType);
            vc.put("reqValPropValue",reqValPropValue);
            vc.put("reqValShowProp",reqValShowProp);
            vc.put("reqValShowActv",reqValShowActv);
        }


        return velocityContextToString("pluginsView.html");
    }

	/**
	 * @param b
	 * @param in
	 * @param reqParHashcode
	 * @param reqParShowProp
	 * @param reqParShowActv
	 * @param reqValShowProp
	 * @param reqValShowActv
	 * @return
	 */
	private void pluginList(String reqParHashcode,
			String reqParShowProp, String reqParShowActv,
			boolean reqValShowProp, boolean reqValShowActv) {

		// Retrieve information for all registered metric plug-ins
		Collection<PluginInfo> l = sobjPA.listPlugins();
		List<Map<String,Object>> installedPlugins = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> notInstalledPlugins = new ArrayList<Map<String,Object>>();
		vc.put("reqParHashcode", reqParHashcode);
		for(PluginInfo i : l) {
			Map<String,Object> plugin= new HashMap<String,Object>();
	    	plugin.put("name", i.getPluginName()+"");
	    	plugin.put("pluginClass", StringUtils.join((String[]) (i.getServiceRef().getProperty(Constants.OBJECTCLASS)),",")+"");
	    	plugin.put("hashcode",i.getHashcode()+"");
	    	plugin.put("version",i.getPluginVersion()+"");
	        plugin.put("pluginConfiguration",i.getConfiguration());
	        plugin.put("activators",i.getActivationTypes());
	        if(!i.installed){
	        	notInstalledPlugins.add(plugin);
	        }else{
	        	installedPlugins.add(plugin);
	        }

		}
		vc.put("reqParShowProp", reqParShowProp);
		vc.put("reqParShowActv", reqParShowActv);
		vc.put("installedPlugins",installedPlugins);
		vc.put("notInstalledPlugins", notInstalledPlugins);
		vc.put("showActivitiesChecked",(reqValShowActv) ? "checked" : "");
		vc.put("showPropertiesChecked",(reqValShowProp) ? "checked" : "");
		vc.put("showActivators",reqValShowActv);
		vc.put("showProperties",reqValShowProp);
		vc.put("currentPluginTemplate", "pluginList.html");
	}

	/**
	 * @param b
	 * @param in
	 * @param reqParAction
	 * @param reqParHashcode
	 * @param reqParPropName
	 * @param reqParPropDescr
	 * @param reqParPropType
	 * @param reqParPropValue
	 * @param actValInstall
	 * @param actValUninstall
	 * @param actValSync
	 * @param actValReqAddProp
	 * @param actValReqUpdProp
	 * @param selPI
	 * @return
	 */
	private void pluginEditForm(String reqParAction,
			String reqParHashcode, String reqParPropName,
			String reqParPropDescr, String reqParPropType,
			String reqParPropValue, String actValInstall,
			String actValUninstall, String actValSync, String actValReqAddProp,
			String actValReqUpdProp, PluginInfo selPI) {
		// Create the plug-in field-set
		vc.put("pluginStatus",(selPI.installed) ? "Installed" : "Registered");
		vc.put("pluginName", selPI.getPluginName()+"");
		vc.put("pluginClass", StringUtils.join((String[]) (selPI.getServiceRef().getProperty(Constants.OBJECTCLASS)),",")+"");
		vc.put("pluginVersion", selPI.getPluginVersion()+"");
		vc.put("installed", selPI.installed);
		vc.put("pluginHashcode", selPI.getHashcode()+"");
		vc.put("reqParAction",reqParAction);
		vc.put("reqParHashcode",reqParHashcode);
		vc.put("reqParPropName",reqParPropName);
		vc.put("reqParPropDescr",reqParPropDescr);
		vc.put("reqParPropType",reqParPropType);
		vc.put("reqParPropValue",reqParPropValue);
		vc.put("actValInstall",actValInstall);
		vc.put("actValUninstall",actValUninstall);
		vc.put("actValSync",actValSync);
		vc.put("actValReqAddProp",actValReqAddProp);
		vc.put("actValReqUpdProp",actValReqUpdProp);
		if(selPI.installed){
			List<Map<String,String>> pluginConfigurations = new ArrayList<Map<String,String>>();
			for(PluginConfiguration config  : Plugin.getPluginByHashcode(selPI.getHashcode()).getConfigurations())
				pluginConfigurations.add(addPluginConfiguration(config));
			vc.put("pluginConfigurations", pluginConfigurations);

			List<Map<String,String>> metricList = new ArrayList<Map<String,String>>();
			List<Metric> metrics = sobjPA.getPlugin(selPI).getAllSupportedMetrics();
			if(metrics != null){
				for(Metric metric : metrics)
					metricList.add(addMetricProperties(metric));
				vc.put("metricList", metricList);
			}
		}
		vc.put("currentPluginTemplate","pluginEditor.html");
	}

	private Map<String,String> addMetricProperties(Metric metric){
		Map<String,String> result = new HashMap<String,String>();
		//append empty string to convert null to string
		result.put("id",metric.getId()+"");
		result.put("mnemonic",metric.getMnemonic()+"");
		result.put("type",metric.getMetricType().getType()+"");
		result.put("description",metric.getDescription()+"");
		return result;
	}

	private Map<String,String> addPluginConfiguration(PluginConfiguration config){
		Map<String,String> result = new HashMap<String,String>();
		//append empty string to convert null to string
		result.put("name", config.getName()+"");
		result.put("message", config.getMsg()+"");
		result.put("type", config.getType()+"");
		result.put("value",config.getValue()+"");
		result.put("description",config.getMsg() == null ? "No description available." : config.getMsg());
		return result;
	}

	/**
	 * @param b
	 * @param in
	 * @param reqParAction
	 * @param reqParPropName
	 * @param reqParPropDescr
	 * @param reqParPropType
	 * @param reqParPropValue
	 * @param actValConAddProp
	 * @param actValConRemProp
	 * @param reqValPropName
	 * @param reqValPropDescr
	 * @param reqValPropType
	 * @param reqValPropValue
	 * @param selPI
	 * @return
	 */
	private void createUpdateConfigurationProperty(
			String reqParAction, String reqParPropName, String reqParPropDescr,
			String reqParPropType, String reqParPropValue,
			String actValConAddProp, String actValConRemProp,
			String reqValPropName, String reqValPropDescr,
			String reqValPropType, String reqValPropValue, PluginInfo selPI) {

		// Check for a property update request
		boolean update = selPI.hasConfProp(
		        reqValPropName, reqValPropType);
		if(update)
			vc.put("update",update);
		vc.put("updateText", (update) ? "Update property of ": "Create property for ");
		vc.put("pluginName", selPI.getPluginName()+"");
		vc.put("propertyName", reqParPropName);
		vc.put("propertyNameValue", (reqValPropName != null) ? reqValPropName : "");
		vc.put("propertyDescriptionValue",  (reqValPropName != null) ? reqValPropName : "");
		vc.put("propertyDescription", reqParPropDescr);
		vc.put("propertyTypeValue", (reqValPropType != null) ? reqValPropType : "");
		vc.put("propertyType",reqParPropType);
		vc.put("configurationTypes", ConfigurationType.values());
		vc.put("propertyValueValue", (reqValPropValue != null) ? reqValPropValue : "");
		vc.put("propertyValue", reqParPropValue);
		vc.put("createButtonText", ((update) ? "Update" : "Create"));
		vc.put("actionId",reqParAction);
		vc.put("actionRemove",actValConRemProp);
		vc.put("actionAdd", actValConAddProp);
		vc.put("currentPluginTemplate", "createUpdateConfigurationProperty.html");
	}

	/**
	 * @param e
	 * @param reqValHashcode
	 * @param reqValPropName
	 * @param reqValPropDescr
	 * @param reqValPropType
	 * @param reqValPropValue
	 * @param selPI
	 * @param update
	 * @return
	 */
	private PluginInfo pluginConfigurationUpdateCreateProperty(
			String reqValHashcode, String reqValPropName,
			String reqValPropDescr, String reqValPropType,
			String reqValPropValue, PluginInfo selPI, boolean update) {
		if (update) {
		    try {
		        if (selPI.updateConfigEntry(
		                sobjDB,
		                reqValPropName,
		                reqValPropValue)) {
		            // Update the Plug-in Admin's information
		            sobjPA.pluginUpdated(
		                    sobjPA.getPlugin(selPI));
		            // Reload the PluginInfo object
		            selPI =
		                sobjPA.getPluginInfo(reqValHashcode);
		        }
		        else {
		        	errorMessages.add("Property update" + " has failed!" + " Check log for details.");
		        }
		    }
		    catch (Exception ex) {
		    	errorMessages.add(ex.getMessage());
		    }
		}
		// Create configuration property
		else {
		    try {
		        if (selPI.addConfigEntry(
		                sobjDB,
		                reqValPropName,
		                reqValPropDescr,
		                reqValPropType,
		                reqValPropValue)) {
		            // Update the Plug-in Admin's information
		            sobjPA.pluginUpdated(
		                    sobjPA.getPlugin(selPI));
		            // Reload the PluginInfo object
		            selPI =
		                sobjPA.getPluginInfo(reqValHashcode);
		        }
		        else {
		        	errorMessages.add("Property creation"
		                    + " has failed!"
		                    + " Check log for details.");
		        }
		    }
		    catch (Exception ex) {
		    	errorMessages.add(ex.getMessage());
		    }
		}
		return selPI;
	}

	/**
	 * @param e
	 * @param reqValHashcode
	 * @param reqValPropName
	 * @param reqValPropType
	 * @param selPI
	 * @return
	 */
	private PluginInfo pluginConfigPropertyRemoval(
			String reqValHashcode, String reqValPropName,
			String reqValPropType, PluginInfo selPI) {
		if (selPI.hasConfProp(
		        reqValPropName, reqValPropType)) {
		    try {
		        if (selPI.removeConfigEntry(
		                sobjDB,
		                reqValPropName,
		                reqValPropType)) {
		            // Update the Plug-in Admin's information
		            sobjPA.pluginUpdated(
		                    sobjPA.getPlugin(selPI));
		            // Reload the PluginInfo object
		            selPI = sobjPA.getPluginInfo(
		                    reqValHashcode);
		        }
		        else {
		        	errorMessages.add("Property removal"
		                    + " has failed!"
		                    + " Check log for details.");
		        }
		    }
		    catch (Exception ex) {
		    	errorMessages.add(ex.getMessage());
		    }
		}
		else {
			errorMessages.add ("Unknown configuration property!");
		}
		return selPI;
	}

	/**
	 * @param e
	 * @param reqValHashcode
	 */
	private void pluginUninstallRequest(String reqValHashcode) {
		if (sobjPA.uninstallPlugin(reqValHashcode) == false) {
			errorMessages.add("Plug-in can not be uninstalled."
		            + " Check log for details.");
		} else {
			errorMessages.add("A job was scheduled to remove the plug-in");
		}
	}

	/**
	 * @param e
	 * @param reqValHashcode
	 */
	private void pluginInstallRequest(String reqValHashcode) {
		if (sobjPA.installPlugin(reqValHashcode) == false) {
			errorMessages.add("Plug-in can not be installed!"
		            + " Check log for details.");
		}
		// Persist the DB changes
		else {
		    PluginInfo pInfo =
		        sobjPA.getPluginInfo(reqValHashcode);
		    sobjPA.pluginUpdated(sobjPA.getPlugin(pInfo));
		}
	}
}

//vi: ai nosi sw=4 ts=4 expandtab
