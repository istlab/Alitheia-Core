/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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
package eu.sqooss.impl.service.pa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import eu.sqooss.service.abstractmetric.FileGroupMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.ProjectFileMetric;
import eu.sqooss.service.abstractmetric.ProjectVersionMetric;
import eu.sqooss.service.abstractmetric.StoredProjectMetric;
import eu.sqooss.service.db.FileGroup;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.pa.ConfigUtils;
import eu.sqooss.service.pa.PluginConfig;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.pa.PluginAdmin;

public class PAServiceImpl implements PluginAdmin, ServiceListener {

    /* ===[ Constants: System and configuration related ]================= */

    // The folder (relative to the Equinox root) where the default
    // configuration files are located
    // TODO: Better store this parameter in the Equinox's config.ini
    private static final String CONF_DIR =
        "configuration";

    // File separator (as retrieved from the host system)
    private static final String FILE_SEP =
        System.getProperty("file.separator");

    // Current working directory (expected to point to the Equinox's root)
    private static final String CWD_PATH =
        System.getProperty("user.dir");

    /* ===[ Constants: Service search filters ]=========================== */

    private static final String SREF_FILTER_PLUGIN =
        "(" + Constants.OBJECTCLASS + "=" + PluginAdmin.PLUGIN_CLASS + ")";

    /* ===[ Constants: Common log messages ]============================== */

    private static final String NO_MATCHING_SERVICES =
        "No matching services were found!";
    private static final String NOT_A_PLUGIN =
        "Not a plugin service!";
    private static final String INVALID_FILTER_SYNTAX =
        "Invalid filter syntax!";
    private static final String CANT_GET_SOBJ =
        "The service object can not be retrieved!";


    /* ===[ Global variables ]============================================ */

    private Logger logger;

    // Store our parent's bundle context here
    private BundleContext bc;

    // Keeps a list of registered plugin services, indexed by service ID
    private HashMap<Long, PluginInfo> registeredPlugins =
        new HashMap<Long, PluginInfo>();

    // Holds the current set of plugins configurations, indexed by class name
    private HashMap<String, PluginConfig> pluginConfigurations =
        new HashMap<String, PluginConfig>();

    // Provides routines for accessing a specified configuration file
    ConfigUtils configReader = null;

    /* ===[ Constructors ]================================================ */

    public PAServiceImpl (BundleContext bc, Logger logger) {
        this.logger = logger;
        this.bc = bc;

        logger.info("PluginAdmin service starting.");

        // Read the default configuration file
        String configDir = CWD_PATH + FILE_SEP + CONF_DIR + FILE_SEP;
        configReader = new XMLConfigParser(
                configDir + "plugins.xml",
                configDir + "plugins.xsd");
        // ... and retrieve all available plugins configurations
        if (configReader != null) {
            pluginConfigurations = configReader.getPluginConfiguration();
        }
        logger.debug("Done reading from file " + configDir + "plugins.xml");

        // Collect information about pre-existing plugin services
        this.collectPluginInfo();

        // Attach this object as a listener for metric services
        try {
            bc.addServiceListener(this, SREF_FILTER_PLUGIN);
        } catch (InvalidSyntaxException e) {
            logger.error(INVALID_FILTER_SYNTAX);
        }

        // Register an extension to the Equinox console, in order to
        // provide commands for managing plugin services
        bc.registerService(
                CommandProvider.class.getName(),
                new PACommandProvider(this) ,
                null);
        logger.debug("PluginAdmin registered successfully.");
    }

    /**
     * Constructs a PluginInfo object, from the available information
     * regarding the selected plugin service reference
     *
     * @param srefPlugin the service reference object
     *
     * @return a MetricInfo object containing the extracted metric
     * information
     */
    private PluginInfo getPluginInfo (ServiceReference srefPlugin) {
        if (srefPlugin == null) {
            logger.debug("Got a null service reference, ignoring.");
            return null;
        }

        PluginInfo pluginInfo = new PluginInfo();

        // Set the plugin's service ID and service reference
        pluginInfo.setServiceID(
                (Long) srefPlugin.getProperty(Constants.SERVICE_ID));
        pluginInfo.setServiceRef(srefPlugin);

        // Set the class name(s) of the object(s) used in the
        // service registration
        String[] plugin_classes =
            (String[]) srefPlugin.getProperty(Constants.OBJECTCLASS);
        pluginInfo.setObjectClass(plugin_classes);

        // Set the ID and name of the bundle which has registered
        // this service
        pluginInfo.setBundleID(
                srefPlugin.getBundle().getBundleId());
        pluginInfo.setBundleName(
                srefPlugin.getBundle().getSymbolicName());
        logger.debug("Getting info for plugin " + pluginInfo.getBundleName());

        // SQO-OSS related info fields
        AlitheiaPlugin pluginObject = (AlitheiaPlugin) bc.getService(srefPlugin);
        if (pluginObject != null) {
            pluginInfo.setPluginName(pluginObject.getName());
            pluginInfo.setPluginVersion(pluginObject.getVersion());
            pluginInfo.setAttributes(pluginObject.getConfigurationSchema());

            // Retrieve all object types that this plugin can calculate
            Vector<String> metricType = new Vector<String>();
            if (pluginObject instanceof ProjectFileMetric) {
                metricType.add(ProjectFile.class.getName());
            }
            if (pluginObject instanceof ProjectVersionMetric) {
                metricType.add(ProjectVersion.class.getName());
            }
            if (pluginObject instanceof StoredProjectMetric) {
                metricType.add(StoredProject.class.getName());
            }
            if (pluginObject instanceof FileGroupMetric) {
                metricType.add(FileGroup.class.getName());
            }
            if (!metricType.isEmpty()) {
                String[] types = new String[metricType.size()];
                types = metricType.toArray(types);
                pluginInfo.setPluginType(types);
            }
        }

        return pluginInfo;
    }

    /**
     * Collects information about all registered metrics
     */
    private void collectPluginInfo() {
        logger.debug("Collecting plugin info.");
        // Retrieve a list of all references to registered metric services
        ServiceReference[] pluginList = null;
        try {
            pluginList = bc.getServiceReferences(null, SREF_FILTER_PLUGIN);
        } catch (InvalidSyntaxException e) {
            logger.warn(INVALID_FILTER_SYNTAX);
        }

        // Retrieve information about all registered metrics found
        if ((pluginList != null) && (pluginList.length > 0)) {
            for (ServiceReference s : pluginList) {
                PluginInfo pluginInfo = getPluginInfo(s);
   
                // Add this metric's info to the list
                if (pluginInfo != null) {
                    registeredPlugins.put(
                            pluginInfo.getServiceID(),
                            pluginInfo);
                }
            }
        }
        else {
            logger.info("No pre-existing plugins were found!");
        }
    }

    /**
     * Apply a configuration to a given plugin. The plugin is identified
     * in three ways, by service reference, id and plugin info -- the caller
     * must ensure that these are all in-sync or undefined behavior may
     * occur.
     *
     * Depending on the values in the configuration set, the plugin
     * may be installed automatically.
     */
    private void configurePlugin(ServiceReference s, Long serviceId, PluginInfo info, PluginConfig config) {
        // Checks if this plugin has to be automatically
        // installed upon registration
        if (Boolean.valueOf(config.getString(PluginConfig.KEY_AUTOINSTALL))) {
            if (installPlugin(serviceId)) {
                logger.debug("Plugin " + serviceId + " installed OK.");
            }
            else {
                logger.warn (
                        "The install method of plugin with"
                        + " service ID " + serviceId+ " failed.");
            }
        }
    }

    /**
     * Performs various maintenance operations upon registration of a new
     * plugin service
     *
     * @param srefPlugin the reference to the registered metric service
     */
    private void pluginRegistered (ServiceReference srefPlugin) {
        // Retrieve the service ID
        Long serviceId =
            (Long) srefPlugin.getProperty(Constants.SERVICE_ID);
        logger.info("A plugin service was registered with ID " + serviceId);

        // Dispose from the list of available plugins any old plugin, that
        // uses the same ID. Should not be required, as long as plugin
        // services got properly unregistered.
        if (registeredPlugins.containsKey(serviceId)) {
            registeredPlugins.remove(serviceId);
        }

        // Retrieve information about this plugin and add this plugin to the
        // list of registered/available plugins
        PluginInfo plugInfo = getPluginInfo(srefPlugin);
        registeredPlugins.put(serviceId, plugInfo);

        // Search for an applicable configuration set and apply it
        Iterator<String> configSets =
            pluginConfigurations.keySet().iterator();
        while (configSets.hasNext()) {
            // Match is performed against the plugin's class name(s)
            String className = configSets.next();
            // TODO: It could happen that a service get registered with more
            // than one class. In this case a situation can arise where
            // two or more matching configuration sets exists.
            if (plugInfo.usesClassName(className)) {
                // Apply the current configuration set to this plugin
                logger.debug(
                        "A configuration set was found for plugin with"
                        + " object class name " + className
                        + " and service ID "    + serviceId);
                PluginConfig configSet =
                    pluginConfigurations.get(className);

                // Execute the necessary post-registration actions
                if (configSet != null) {
                    configurePlugin(srefPlugin, serviceId, plugInfo, configSet);
                }
            }
        }
    }

    /**
     * Performs various maintenance operations during unregistering of a
     * plugin service
     *
     * @param srefPlugin the reference to the registered metric service
     */
    private void pluginUnregistering (ServiceReference srefPlugin) {
        // Retrieve the service ID
        Long serviceId =
            (Long) srefPlugin.getProperty(Constants.SERVICE_ID);
        logger.info(
                "A plugin service with ID "
                + serviceId + " is unregistering.");

        // Remove this service from the list of available plugin services
        if (registeredPlugins.containsKey(serviceId)) {
            registeredPlugins.remove(serviceId);
        }
    }

    /**
     * Performs various maintenance operations upon a change in an existing
     * plugin service
     *
     * @param srefPlugin the reference to the registered metric service
     */
    private void pluginModified (ServiceReference srefPlugin) {
        // Retrieve the service ID
        Long serviceId =
            (Long) srefPlugin.getProperty(Constants.SERVICE_ID);
        logger.info(
                "A plugin service with ID "
                + serviceId + " was modified.");
    }

/* ===[ Implementation of the ServiceListener interface ]================= */

    public void serviceChanged(ServiceEvent event) {
        // Get a reference to the affected service
        ServiceReference affectedService = event.getServiceReference();

        // Find out what happened to the service
        switch (event.getType()) {
        // New service was registered
        case ServiceEvent.REGISTERED:
            pluginRegistered(affectedService);
            break;
        // An existing service is unregistering
        case ServiceEvent.UNREGISTERING:
            pluginUnregistering(affectedService);
            break;
        // The configuration of an existing service was modified
        case ServiceEvent.MODIFIED:
            pluginModified (affectedService);
        }
    }

/* ===[ Implementation of the PluginAdmin interface ]===================== */

    public Collection<PluginInfo> listPlugins() {
        if (!registeredPlugins.isEmpty()) {
            return registeredPlugins.values();
        }
        return null;
    }

    public boolean installPlugin(Long sid) {
        // Format a search filter for the plugin service with <sid> serviceId
        String serviceFilter =
            "(" + Constants.SERVICE_ID +"=" + sid + ")";
        logger.info (
                "Installing plugin with service ID " + sid);

        final String INSTALL_FAILED =
            "The installation of plugin with"
            + " service ID "+ sid
            + " failed : ";

        try {
            ServiceReference[] matchingServices =
                bc.getServiceReferences(null, serviceFilter);
            if ((matchingServices != null)
                    && (matchingServices.length == 1)) {
                // Since the search was performed using a serviceId, it must
                // be only one service reference that is found
                ServiceReference sref = matchingServices[0];

                if (sref != null) {
                    try {
                        // Retrieve the plugin object registered with this
                        // service
                        AlitheiaPlugin sobj = (AlitheiaPlugin) bc.getService(sref);
                        if (sobj != null) {
                            // Try to execute the install() method of this
                            // plugin
                            boolean installed = sobj.install();

                            // If the install() is successful, then note this
                            // into the plugin's information object
                            if ((installed) &&
                                    (registeredPlugins.containsKey(sid))) {
                                // Retrieve the corresponding information
                                // object
                                PluginInfo pluginInfo =
                                    registeredPlugins.get(sid);
                                if (pluginInfo != null) {
                                    pluginInfo.installed = true;
                                }
                            }
                            return installed;
                        }
                        else {
                            logger.warn(INSTALL_FAILED + CANT_GET_SOBJ);
                        }
                    } catch (ClassCastException e) {
                        logger.warn(INSTALL_FAILED + NOT_A_PLUGIN);
                    } catch (Error e) {
                        logger.warn(INSTALL_FAILED + e);
                    }
                }
                else {
                    logger.warn(NO_MATCHING_SERVICES);
                }
            }
            else {
                logger.warn(NO_MATCHING_SERVICES);
            }
        } catch (InvalidSyntaxException e) {
            logger.warn(INVALID_FILTER_SYNTAX);
        }

        return false;
    }

    public ServiceReference[] listPluginProviders(Class<?> o) {
        if (registeredPlugins.isEmpty()) {
            // No metrics. Don't bother looking.
            return null;
        }
        // There should be at least one registered metric
        // All registered metrics
        Iterator<PluginInfo> plugins =
            registeredPlugins.values().iterator();
        // Plugins matching this search
        Vector<ServiceReference> matching =
            new Vector<ServiceReference>();
        // Search for plugin of compatible type
        while (plugins.hasNext()) {
            PluginInfo nextPlugin = plugins.next();
            if ((nextPlugin.isType(o.getName()))
                && (nextPlugin.getServiceRef() != null)) {
                    matching.add(nextPlugin.getServiceRef());
                }
        }
        // Return the matching ones
        if (matching.size() > 0) {
            ServiceReference[] pluginsList =
                new ServiceReference[matching.size()];
            pluginsList = matching.toArray(pluginsList);
            return pluginsList;
        }
        // None found
        return null;
    }

    public PluginInfo getPluginInfo(AlitheiaPlugin m) {
        PluginInfo mi = null;
        Collection<PluginInfo> c = listPlugins();
        Iterator<PluginInfo> i = c.iterator();
        
        while (i.hasNext()) {
            mi = i.next();

            if (mi.getPluginName() == m.getName()
                    && mi.getPluginVersion() == m.getVersion()) {
                return mi;
            }
        }
        return null;
    }

    public AlitheiaPlugin getPlugin(PluginInfo m) {
        ServiceReference s = m.getServiceRef();
        return (AlitheiaPlugin) bc.getService(s);
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
