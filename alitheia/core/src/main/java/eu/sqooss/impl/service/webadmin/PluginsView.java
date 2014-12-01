/*RVALA
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
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.hibernate.Hibernate;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.pa.PluginInfo.ConfigurationType;
import eu.sqooss.service.util.StringUtils;

public class PluginsView extends AbstractView {

	// error messages
	private static StringBuilder e;
	// Info object of the selected plug-in
	private static PluginInfo selPI = null;
	// request variables for easier access
	private static String reqValPropName = null;
	private static String reqValPropDescr = null;
	private static String reqValPropType = null;
	private static String reqValPropValue = null;
	private static String reqValAction = "";
	private static String reqValHashcode = null;
	private static boolean reqValShowProp = false; // Show plug-in properties
	private static boolean reqValShowActv = false; // Show plug-in activators

	public PluginsView(BundleContext bundlecontext, VelocityContext vc) {
		super(bundlecontext, vc);
		e = new StringBuilder();
	}

	/**
	 * Determines whether the list of available plugins is empty.
	 * 
	 * @return boolean is list of available plugins empty?
	 */
	public static boolean isPluginsListEmpty() {
		return sobjPA.listPlugins().isEmpty();
	}

	/**
	 * Returns the collection of available plugins.
	 * 
	 * @return Collection available plugins
	 */
	public static Collection<PluginInfo> getPluginsList() {
		return sobjPA.listPlugins();
	}

	/**
	 * Returns the String of error messages collected during request execution.
	 * 
	 * @return String Error messages.
	 */
	public static String getErrorMessages() {
		if (e.length() > 0) {
			return e.toString();
		} else {
			return null;
		}
	}

	/**
	 * Returns the currently selected plugin.
	 * 
	 * @return PluginInfo Selected plugin.
	 */
	public static PluginInfo getSelectedPlugin() {
		return selPI;
	}

	/**
	 * Returns the request property name.
	 * 
	 * @return String Request property name
	 */
	public static String getValPropName() {
		return (reqValPropName != null) ? reqValPropName : "";
	}

	/**
	 * Returns the request property type.
	 * 
	 * @return String Request property type
	 */
	public static String getValPropType() {
		return (reqValPropType != null) ? reqValPropType : "";
	}

	/**
	 * Returns the request property description.
	 * 
	 * @return String Request property description.
	 */
	public static String getValPropDescr() {
		return (reqValPropDescr != null) ? reqValPropDescr : "";
	}

	/**
	 * Returns the request property value.
	 * 
	 * @return String Request property value
	 */
	public static String getValPropValue() {
		return (reqValPropValue != null) ? reqValPropValue : "";
	}

	/**
	 * Returns request action.
	 * 
	 * @return String Request action
	 */
	public static String getValAction() {
		return (reqValAction != null) ? reqValAction : "";
	}

	/**
	 * Returns request hashcode.
	 * 
	 * @return String Request hashcode
	 */
	public static String getValHashcode() {
		return (reqValHashcode != null) ? reqValHashcode : "";
	}

	/**
	 * Determines if properties of plugins should be shown.
	 * 
	 * @return boolean Should properties of plugins be shown?
	 */
	public static boolean getValShowProp() {
		return reqValShowProp;
	}

	/**
	 * Determines if activities of plugins should be shown.
	 * 
	 * @return boolean Should activities of plugins be shown?
	 */
	public static boolean getValShowActv() {
		return reqValShowActv;
	}

	/**
	 * Returns array of configuration types.
	 * 
	 * @return ConfigurationType[] Configuration types
	 */
	public static ConfigurationType[] getConfigurationTypes() {
		return ConfigurationType.values();
	}

	/**
	 * Returns list of metrics of given plugin.
	 * 
	 * @param pi
	 *            plugin to show metrics of
	 * @return List<Metric> list of metrics for given plugin
	 */
	public static List<Metric> getPluginMetrics(PluginInfo pi) {
		if (pi == null)
			return null;
		return sobjPA.getPlugin(pi).getAllSupportedMetrics();
	}

	/**
	 * Determines if the list of plugin metrics is empty.
	 * 
	 * @param pi
	 *            given plugin
	 * @return boolean Is list of metrics of given plugin empty?
	 */
	public static boolean isPluginMetricsEmpty(PluginInfo pi) {
		if (pi == null)
			return true;
		List<Metric> metrics = sobjPA.getPlugin(pi).getAllSupportedMetrics();
		if ((metrics == null) || (metrics.isEmpty())) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the configuration of given plugin.
	 * 
	 * @param pi
	 *            given plugin
	 * @return Set<PluginConfiguration> configuration of given plugin
	 */
	public static Set<PluginConfiguration> getPluginConfiguration(PluginInfo pi) {
		if (pi == null)
			return null;
		return Plugin.getPluginByHashcode(pi.getHashcode()).getConfigurations();
	}

	/**
	 * Determines if the list of configuration of plugin is empty.
	 * 
	 * @param pi
	 *            given plugin
	 * @return Is list of configuration of plugin empty?
	 */
	public static boolean isPluginConfigurationEmpty(PluginInfo pi) {
		if (pi == null)
			return true;
		Set<PluginConfiguration> config = Plugin.getPluginByHashcode(
				pi.getHashcode()).getConfigurations();
		if ((config == null) || (config.isEmpty())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Determines if the DEBUG variable is on.
	 * @return Is DEBUG on?
	 */
	public static boolean isDebugOn() {
		return DEBUG;
	}

	/**
	 * Retrieves data from the request required for rendering and executes the request.
	 * @param req
	 */
	public void exec(HttpServletRequest req) {
		// reinitialize all request variables
		e = new StringBuilder();
		selPI = null;
		reqValPropName = null;
		reqValPropDescr = null;
		reqValPropType = null;
		reqValPropValue = null;
		reqValAction = "";
		reqValHashcode = null;
		reqValShowProp = false; // Show plug-in properties
		reqValShowActv = false; // Show plug-in activators

		// Proceed only when at least one plug-in is registered
		if (!isPluginsListEmpty()) {

			retrieveEditorsAction(req);
			retrieveDisplayFlags(req);
			retrieveSelectedConfigurationPropertyValues(req);
			// Retrieve the selected plug-in's hash code
			reqValHashcode = req.getParameter(WebAdminConstants.REQ_PAR_HASHCODE);

			// Plug-in based actions
			if (reqValHashcode != null) {
				processPluginInstallRequests();
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
				if (reqValAction.equals(WebAdminConstants.ACT_VAL_SYNC)) {
					compMA.syncMetrics(sobjPA.getPlugin(selPI));
				}
				// =======================================================
				// Plug-in's configuration property removal
				// =======================================================
				else if (reqValAction.equals(WebAdminConstants.ACT_VAL_CON_REM_PROP)) {
					removePluginConfigurationProperty();
				}
				// =======================================================
				// Plug-in's configuration property creation/update
				// =======================================================
				else if (reqValAction.equals(WebAdminConstants.ACT_VAL_CON_ADD_PROP)) {
					// Check for a property update
					boolean update = selPI.hasConfProp(reqValPropName,
							reqValPropType);
					// Update configuration property
					if (update) {
						updatePluginConfigurationProperty();
					}
					// Create configuration property
					else {
						createPluginConfigurationProperty();
					}
					// Return to the create/update view upon error
					if (e.toString().length() > 0) {
						if (update)
							reqValAction = WebAdminConstants.ACT_VAL_REQ_UPD_PROP;
						else
							reqValAction = WebAdminConstants.ACT_VAL_REQ_ADD_PROP;
					}
				}
			}
		}
	}

	/**
	 * Retrieve the selected editor's action (if any)
	 * 
	 * @param req
	 */
	private static void retrieveEditorsAction(HttpServletRequest req) {
		reqValAction = req.getParameter(WebAdminConstants.REQ_PAR_ACTION);
		if (reqValAction == null) {
			reqValAction = "";
		}
	}

	/**
	 * Retrieve the various display flags
	 * 
	 * @param req
	 */
	private static void retrieveDisplayFlags(HttpServletRequest req) {
		if ((req.getParameter(WebAdminConstants.REQ_PAR_SHOW_PROP) != null)
				&& (req.getParameter(WebAdminConstants.REQ_PAR_SHOW_PROP)
						.equals("true"))) {
			reqValShowProp = true;
		}
		if ((req.getParameter(WebAdminConstants.REQ_PAR_SHOW_ACTV) != null)
				&& (req.getParameter(WebAdminConstants.REQ_PAR_SHOW_ACTV)
						.equals("true"))) {
			reqValShowActv = true;
		}
	}

	/**
	 * Retrieve the selected configuration property's values
	 * 
	 * @param req
	 */
	private static void retrieveSelectedConfigurationPropertyValues(
			HttpServletRequest req) {
		if ((reqValAction.equals(WebAdminConstants.ACT_VAL_CON_ADD_PROP))
				|| (reqValAction.equals(WebAdminConstants.ACT_VAL_REQ_UPD_PROP))
				|| (reqValAction.equals(WebAdminConstants.ACT_VAL_CON_REM_PROP))) {
			// Name, description, type and value
			reqValPropName = req.getParameter(WebAdminConstants.REQ_PAR_PROP_NAME);
			reqValPropDescr = req.getParameter(WebAdminConstants.REQ_PAR_PROP_DESC);
			reqValPropType = req.getParameter(WebAdminConstants.REQ_PAR_PROP_TYPE);
			reqValPropValue = req
					.getParameter(WebAdminConstants.REQ_PAR_PROP_VALUE);
		}
	}

	/**
	 * Plug-in install and uninstall request
	 */
	private static void processPluginInstallRequests() {
		if (reqValAction.equals(WebAdminConstants.ACT_VAL_INSTALL)) {
			if (sobjPA.installPlugin(reqValHashcode) == false) {
				e.append("Plug-in can not be installed!"
						+ " Check log for details.");
			}
			// Persist the DB changes
			else {
				PluginInfo pInfo = sobjPA.getPluginInfo(reqValHashcode);
				sobjPA.pluginUpdated(sobjPA.getPlugin(pInfo));
			}
		} else if (reqValAction.equals(WebAdminConstants.ACT_VAL_UNINSTALL)) {
			if (sobjPA.uninstallPlugin(reqValHashcode) == false) {
				e.append("Plug-in can not be uninstalled."
						+ " Check log for details.");
			} else {
				e.append("A job was scheduled to remove the plug-in");
			}
		}
	}

	/**
	 * Plug-in's configuration property removal
	 */
	private static void removePluginConfigurationProperty() {
		if (selPI.hasConfProp(reqValPropName, reqValPropType)) {
			try {
				if (selPI.removeConfigEntry(sobjDB, reqValPropName,
						reqValPropType)) {
					// Update the Plug-in Admin's information
					sobjPA.pluginUpdated(sobjPA.getPlugin(selPI));
					// Reload the PluginInfo object
					selPI = sobjPA.getPluginInfo(reqValHashcode);
				} else {
					e.append("Property removal" + " has failed!"
							+ " Check log for details.");
				}
			} catch (Exception ex) {
				e.append(ex.getMessage());
			}
		} else {
			e.append("Unknown configuration property!");
		}
		// Return to the update view upon error
		if (e.toString().length() > 0) {
			reqValAction = WebAdminConstants.ACT_VAL_REQ_UPD_PROP;
		}
	}

	/**
	 * Plug-in's configuration property update
	 */
	private static void updatePluginConfigurationProperty() {
		try {
			if (selPI
					.updateConfigEntry(sobjDB, reqValPropName, reqValPropValue)) {
				// Update the Plug-in Admin's information
				sobjPA.pluginUpdated(sobjPA.getPlugin(selPI));
				// Reload the PluginInfo object
				selPI = sobjPA.getPluginInfo(reqValHashcode);
			} else {
				e.append("Property update" + " has failed!"
						+ " Check log for details.");
			}
		} catch (Exception ex) {
			e.append(ex.getMessage());
		}
	}

	private static void createPluginConfigurationProperty() {
		try {
			if (selPI.addConfigEntry(sobjDB, reqValPropName, reqValPropDescr,
					reqValPropType, reqValPropValue)) {
				// Update the Plug-in Admin's information
				sobjPA.pluginUpdated(sobjPA.getPlugin(selPI));
				// Reload the PluginInfo object
				selPI = sobjPA.getPluginInfo(reqValHashcode);
			} else {
				e.append("Property creation" + " has failed!"
						+ " Check log for details.");
			}
		} catch (Exception ex) {
			e.append(ex.getMessage());
		}
	}
}

// vi: ai nosi sw=4 ts=4 expandtab
