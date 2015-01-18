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
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.VelocityContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;

public class PluginsController extends ActionController {
    private static final String PLUGIN_HASHCODE = "pluginHashcode";
    private static final String PLUGIN_NAME = "pluginName";
    private static final String PLUGIN_DESCRIPTION = "pluginDescription";
    private static final String PLUGIN_TYPE = "pluginType";
    private static final String PLUGIN_VALUE = "pluginValue";

    /** The plugin admin from the core. */
    private PluginAdmin pluginAdmin = null;

    /** The database service. */
    private DBService databaseService = null;

    /** The metric activator. */
    private MetricActivator metricActivator = null;

    /** The selected plugin (based on hashcode) */
    private PluginInfo selPlugin = null;

    public PluginsController() {
        super("plugins.html");
    }

    @Override
    protected void beforeAction(Map<String, String> requestParameters, VelocityContext velocityContext) {
        AlitheiaCore core = AlitheiaCore.getInstance();

        pluginAdmin = core.getPluginAdmin();
        databaseService = core.getDBService();
        metricActivator = core.getMetricActivator();

        Collection<PluginInfo> plugins;

        selPlugin = null;

        String hashCode = requestParameters.get(PLUGIN_HASHCODE);

        if (null != hashCode) {
            selPlugin = pluginAdmin.getPluginInfo(hashCode);
            velocityContext.put("plugin", selPlugin);
        }
        plugins = pluginAdmin.listPlugins();
        velocityContext.put("plugins", plugins);
    }

    @Action
    public void home(Map<String, String> requestParameters, VelocityContext velocityContext) {
    }

    @Action("showPlugin")
    public String showPlugin(Map<String, String> requestParameters, VelocityContext velocityContext) {
        if (null == selPlugin) {
            return null;
        }

        // Fill plugins with only the 1 selected plugin
        ArrayList<PluginInfo> plugins = new ArrayList<>();
        plugins.add(selPlugin);
        velocityContext.put("plugins", plugins);

        // Get metrics for plugin
        AlitheiaCore core = AlitheiaCore.getInstance();
        pluginAdmin = core.getPluginAdmin();
        Collection<Metric> metrics = pluginAdmin.getPlugin(selPlugin).getAllSupportedMetrics();
        velocityContext.put("metrics", metrics);
        
        if (selPlugin.installed) {
            // Get configuration properties for plugin
            Plugin tempPlugin = Plugin.getPluginByHashcode(selPlugin.getHashcode());
    
            Set<PluginConfiguration> configurations = null;
            try {
                configurations = tempPlugin.getConfigurations();
            } catch (NullPointerException e) {
                addWarning(Localization.getErr("plugin_configurations_not_loaded"));
                configurations = Collections.emptySet();
            }
            velocityContext.put("configs", configurations);
        }
        
        return "plugin.html";
    }

    @Action("installPlugin")
    public String installPlugin(Map<String, String> requestParameters, VelocityContext velocityContext) {
        if (null == selPlugin) {
            return null;
        }

        String hashcode = selPlugin.getHashcode();
        if (pluginAdmin.installPlugin(hashcode) == false) {
            addWarning(Localization.getErr("plugin_install_failed"));
        } else {
            // Persist the DB changes
            selPlugin = pluginAdmin.getPluginInfo(hashcode);
            pluginAdmin.pluginUpdated(pluginAdmin.getPlugin(selPlugin));
            addSuccess(Localization.getMsg("plugin_installed"));
        }

        return showPlugin(requestParameters, velocityContext);
    }

    @Action("uninstallPlugin")
    public String uninstallPlugin(Map<String, String> requestParameters, VelocityContext velocityContext) {
        if (null == selPlugin) {
            return null;
        }

        String hashcode = selPlugin.getHashcode();
        if (pluginAdmin.uninstallPlugin(hashcode) == false) {
            addWarning(Localization.getErr("plugin_uninstall_failed"));
            return null;
        } else {
            addInfo(Localization.getMsg("plugin_uninstall_queued"));
        }

        return showPlugin(requestParameters, velocityContext);
    }

    @Action("syncPlugin")
    public String syncPlugin(Map<String, String> requestParameters, VelocityContext velocityContext) {
        if (null == selPlugin) {
            return null;
        }

        if (selPlugin.installed) {
            metricActivator.syncMetrics(pluginAdmin.getPlugin(selPlugin));
        }

        return showPlugin(requestParameters, velocityContext);
    }

    @Action("createProperty")
    public String createProperty(Map<String, String> requestParameters, VelocityContext velocityContext) {
        if (null == selPlugin) {
            return null;
        }

        String hashcode = selPlugin.getHashcode();
        if (selPlugin.installed) {
            String propertyName = requestParameters.get(PLUGIN_NAME);
            String propertyType = requestParameters.get(PLUGIN_TYPE);
            String propertyDescription = requestParameters.get(PLUGIN_DESCRIPTION);
            String propertyValue = requestParameters.get(PLUGIN_VALUE);
            try {
                if (selPlugin.addConfigEntry(databaseService, propertyName, propertyDescription, propertyType, propertyValue)) {
                    // Update the Plug-in Admin's information
                    pluginAdmin.pluginUpdated(pluginAdmin.getPlugin(selPlugin));
                    // Reload the PluginInfo object
                    selPlugin = pluginAdmin.getPluginInfo(hashcode);
                } else {
                    addWarning(Localization.getErr("property_create_failed"));
                }
            } catch (Exception ex) {
                // TODO: Print error to user
            }
        } else {
            addWarning(Localization.getErr("plugin_not_found"));
        }

        return showPlugin(requestParameters, velocityContext);
    }

    @Action("updateProperty")
    public String updateProperty(Map<String, String> requestParameters, VelocityContext velocityContext) {
        if (null == selPlugin) {
            return null;
        }
        String hashcode = requestParameters.get(PLUGIN_HASHCODE);

        if (null != selPlugin && selPlugin.installed) {
            String propertyName = requestParameters.get(PLUGIN_NAME);
            String propertyValue = requestParameters.get(PLUGIN_VALUE);
            try {
                if (selPlugin.updateConfigEntry(databaseService, propertyName, propertyValue)) {
                    // Update the Plug-in Admin's information
                    pluginAdmin.pluginUpdated(pluginAdmin.getPlugin(selPlugin));
                    // Reload the PluginInfo object
                    selPlugin = pluginAdmin.getPluginInfo(hashcode);
                } else {
                    addWarning(Localization.getErr("property_update_failed"));
                }
            } catch (Exception ex) {
                // TODO: Print error to user
            }
        } else {
            addWarning(Localization.getErr("plugin_not_found"));
        }

        return showPlugin(requestParameters, velocityContext);
    }

    @Action("confirmProperty")
    public String confirmProperty(Map<String, String> requestParameters, VelocityContext velocityContext) {
        System.out.println("CALLED confirmProperty");
        return showPlugin(requestParameters, velocityContext);
    }

    @Action("removeProperty")
    public String removeProperty(Map<String, String> requestParameters, VelocityContext velocityContext) {
        if (null == selPlugin) {
            return null;
        }
        String hashcode = requestParameters.get(PLUGIN_HASHCODE);
        if (null != selPlugin && selPlugin.installed) {
            String propertyName = requestParameters.get(PLUGIN_NAME);
            String propertyType = requestParameters.get(PLUGIN_TYPE);
            if (selPlugin.hasConfProp(propertyName, propertyType)) {
                try {
                    if (selPlugin.removeConfigEntry(databaseService, propertyName, propertyType)) {
                        // Update the Plug-in Admin's information
                        pluginAdmin.pluginUpdated(pluginAdmin.getPlugin(selPlugin));
                        // Reload the PluginInfo object
                        selPlugin = pluginAdmin.getPluginInfo(hashcode);
                    } else {
                        addWarning(Localization.getErr("property_remove_failed"));
                    }
                } catch (Exception ex) {
                    // TODO: Set the ex.getMessage() somewhere
                }
            }
        }
        return showPlugin(requestParameters, velocityContext);
    }
}
