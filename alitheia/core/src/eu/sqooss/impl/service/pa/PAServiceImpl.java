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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;

public class PAServiceImpl implements PluginAdmin, ServiceListener {

    /* ===[ Constants: Service search filters ]=========================== */

    private static final String SREF_FILTER_PLUGIN =
        "(" + Constants.OBJECTCLASS + "=" + PluginAdmin.PLUGIN_CLASS + ")";

    /* ===[ Constants: Common log messages ]============================== */

    private static final String NO_MATCHING_SERVICES =
        "No matching services were found!";
    private static final String NOT_A_PLUGIN =
        "Not a metric plug-in service!";
    private static final String INVALID_FILTER_SYNTAX =
        "Invalid filter syntax!";
    private static final String INVALID_SREF =
        "Invalid service reference!";
    private static final String CANT_GET_SOBJ =
        "The service object can not be retrieved!";

    /* ===[ Global variable ]============================================= */

    // The parent bundle's context object
    private BundleContext bc;

    // Required SQO-OSS components
    private Logger logger;
    private DBService sobjDB = null;

    /** 
     * Keeps a list of registered metric plug-in's services, indexed by the 
     * plugin's hash code (stored in the database).
     */
    private HashMap<String, PluginInfo> registeredPlugins =
        new HashMap<String, PluginInfo>();

    /**
     * Instantiates a new <code>PluginAdmin</code>.
     * 
     * @param bc - the parent bundle's context object
     * @param logger - the Logger component's instance
     */
    public PAServiceImpl (BundleContext bc, Logger logger) {
        this.bc = bc;

        // Store the Logger instance
        this.logger = logger;
        logger.info("Starting the PluginAdmin component.");

        // Get the AlitheaCore's object
        ServiceReference srefCore = null;
        srefCore = bc.getServiceReference(AlitheiaCore.class.getName());
        AlitheiaCore sobjCore = (AlitheiaCore) bc.getService(srefCore);

        if (sobjCore != null) {
            // Obtain the required core components
            sobjDB = sobjCore.getDBService();
            if (sobjDB == null) {
                logger.error("Can not obtain the DB object!");
            }

            // Attach this object as a listener for metric services
            try {
                bc.addServiceListener(this, SREF_FILTER_PLUGIN);
            } catch (InvalidSyntaxException e) {
                logger.error(INVALID_FILTER_SYNTAX);
            }

            // Register an extension to the Equinox console, in order to
            // provide commands for managing plug-in's services
            bc.registerService(
                    CommandProvider.class.getName(),
                    new PACommandProvider(this) ,
                    null);

            logger.debug("The PluginAdmin component was successfully started.");
        }
        else {
            logger.error("Can not obtain the Core object!");
        }
    }

    /**
     * Retrieves the service Id of the specified service reference.
     * 
     * @param sref - the service reference
     * 
     * @return The service Id.
     */
    private Long getServiceId (ServiceReference sref) {
        // Check for a valid service reference
        if (sref == null) {
            logger.error(INVALID_SREF);
            return null;
        }
        try {
            return (Long) sref.getProperty(Constants.SERVICE_ID);
        }
        catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Extracts the service Id of the metric plug-in service described in the
     * metric plug-in's information object located by the specified hash 
     * code's value.
     * 
     * @param hash - the hash code's value
     * 
     * @return The service Id.
     */
    private Long getServiceID (String hash) {
        if ((hash != null) && (registeredPlugins.containsKey(hash))) {
            // Get the plug-in info object pointed by the given hash
            PluginInfo infoPlugin = registeredPlugins.get(hash);

            // Return the service's Id
            return getServiceId(infoPlugin.getServiceRef());
        }

        return null;
    }

    /**
     * Gets the metric plug-in's service reference, that is registered with
     * the given service Id.
     * 
     * @param serviceId - the service Id
     * 
     * @return The metric plug-in's service reference.
     */
    private ServiceReference getPluginService (Long serviceId) {
        // Format a search filter for a service with the given service Id
        String serviceFilter =
            "(" + Constants.SERVICE_ID +"=" + serviceId + ")";

        // Retrieve all services that match the search filter
        ServiceReference[] matchingServices = null;
        try {
            /* Since the service search is performed using a service Id,
             * it MUST return only one service reference.
             */
            matchingServices = bc.getServiceReferences(null, serviceFilter);
            if ((matchingServices == null) && (matchingServices.length != 1)) {
                logger.error(NO_MATCHING_SERVICES);
            }
            else {
                return matchingServices[0];
            }
        }
        catch (InvalidSyntaxException e) {
            logger.error(INVALID_FILTER_SYNTAX);
        }

        return null;
    }

    /**
     * Gets the metric plug-in's object registered with the given service.
     * 
     * @param srefPlugin - the metric plug-in's service reference
     * 
     * @return The metric plug-in's object.
     */
    private AlitheiaPlugin getPluginObject (ServiceReference srefPlugin) {
        // Check for a valid service reference
        if (srefPlugin == null) {
            logger.error(INVALID_SREF);
            return null;
        }
        try {
            // Retrieve the metric plug-in's object from the service reference
            AlitheiaPlugin sobjPlugin = (AlitheiaPlugin) bc.getService(srefPlugin);
            // Check for a valid plug-in object
            if (sobjPlugin == null) {
                logger.error(CANT_GET_SOBJ);
            }
            return sobjPlugin;
        }
        catch (ClassCastException e) {
            logger.warn(NOT_A_PLUGIN);
        }

        return null;
    }

    /**
     * Creates a new <code>PluginInfo</code> object for registered plug-in
     * from the given metric plug-in's service.
     * 
     * @param srefPlugin - the metric plug-in's service reference
     * 
     * @return The new <code>PluginInfo</code> object, or <code>null</code>
     *   upon failure.
     */
    private PluginInfo createRegisteredPI (ServiceReference srefPlugin) {
        // Get the metric plug-in's object
        AlitheiaPlugin sobjPlugin = getPluginObject(srefPlugin);

        // Create a plug-in info object
        if (sobjPlugin != null) {
            logger.debug(
                    "Creating info object for registered plug-in "
                    + sobjPlugin.getName());
            PluginInfo pluginInfo = new PluginInfo();
            pluginInfo.setPluginName(sobjPlugin.getName());
            pluginInfo.setPluginVersion(sobjPlugin.getVersion());
            pluginInfo.setServiceRef(srefPlugin);
            pluginInfo.setHashcode(getServiceId(srefPlugin).toString());
            // Mark as not installed
            pluginInfo.installed = false;
            return pluginInfo;
        }

        return null;
    }

    /**
     * Creates a new <code>PluginInfo</code> object for installed plug-in
     * from the given metric plug-in's service and database record.
     *
     * @param srefPlugin - the metric plug-in's service reference
     * @param p - the DAO object associated with this metric plug-in
     *
     * @return The new <code>PluginInfo</code> object, or <code>null</code>
     *   upon failure.
     */
    private PluginInfo createInstalledPI (ServiceReference srefPlugin, Plugin p) {
        // Get the metric plug-in's object
        AlitheiaPlugin sobjPlugin = getPluginObject(srefPlugin);
        // Create a plug-in info object
        if (sobjPlugin != null) {
            logger.debug(
                    "Creating info object for installed plug-in "
                    + sobjPlugin.getName());
            PluginInfo pluginInfo =
                new PluginInfo(Plugin.getConfigEntries(p), sobjPlugin);
            pluginInfo.setServiceRef(srefPlugin);
            pluginInfo.setHashcode(p.getHashcode());
            // Mark as installed
            pluginInfo.installed = true;
            return pluginInfo;
        }

        return null;
    }

    /**
     * Returns the database record associated with the metric plug-in that is
     * referenced by the given service reference.
     * 
     * @param srefPlugin - the plug-in's service reference
     * 
     * @return The <code>Plugin</code> DAO object if found in the database,
     *   or <code>null</code> when a matching record does not exist.
     */
    Plugin pluginRefToPluginDAO(ServiceReference srefPlugin) {
        // Get the metric plug-in's object
        AlitheiaPlugin sobjPlugin = getPluginObject(srefPlugin);

        // Return the DAO object associated with this plug-in
        if (sobjPlugin != null) {
            return Plugin.getPluginByHashcode(sobjPlugin.getUniqueKey());
        }

        return null;
    }

    /**
     * Gets the <code>PluginInfo</code> object assigned to the given metric
     * plug-in's service.
     * 
     * @param srefPlugin - the plug-in's service reference
     * 
     * @return The <code>PluginInfo</code> object, or <code>null</code> when
     *  this plug-in has no <code>PluginInfo</code> object assigned to it.
     */
    private PluginInfo getPluginInfo(ServiceReference srefPlugin) {
        // Search for a match through all PluginInfo objects
        if (srefPlugin != null) {
            for (PluginInfo p : registeredPlugins.values()) {
                if (p.getServiceRef().equals(srefPlugin))
                    return p;
            }
        }

        return null;
    }

    /**
     * Performs various maintenance operations upon registration of a new 
     * metric plug-in's service.
     * 
     * @param srefPlugin - the metric plug-in's service reference
     */
    private void pluginRegistered (ServiceReference srefPlugin) {
        // Keeps the PluginInfo object
        PluginInfo pluginInfo;

        // Try to get the DAO that belongs to this metric plug-in
        Plugin daoPlugin = pluginRefToPluginDAO(srefPlugin);

        // Plug-in that is already installed, has a valid DAO
        if (daoPlugin != null) {
            // Create an info object for installed plug-in
            pluginInfo = createInstalledPI(srefPlugin, daoPlugin);
        }
        // This plug-in is just registered
        else {
            // Create an info object for registered plug-in
            pluginInfo = createRegisteredPI(srefPlugin);
        }

        if (pluginInfo == null) {
            logger.error(
                    "Upon plug-in service registration - "
                    + " can not create a PluginInfo object!");
        }
        else {
            // Store the info object into the info object's list
            registeredPlugins.put(pluginInfo.getHashcode(), pluginInfo);
            logger.info(
                    "Plug-in service (" + pluginInfo.getPluginName() + ")"
                    + " was registered.");
        }
    }

    /**
     * Performs various maintenance operations during unregistration of an
     * existing metric plug-in's service.
     * 
     * @param srefPlugin - the metric plug-in's service reference
     */
    private void pluginUnregistering (ServiceReference srefPlugin) {
        // Get the PluginInfo object assigned to this plug-in
        PluginInfo pluginInfo = getPluginInfo(srefPlugin);
        if (pluginInfo == null) {
            logger.error(
                    "During plug-in service unregistration - "
                    + " a matching PluginInfo object was not found!");
            return;
        }
        else {
            // Remove the info object from the info object's list
            registeredPlugins.remove(pluginInfo.getHashcode());
            logger.info(
                    "Plug-in service (" + pluginInfo.getPluginName() + ")"
                    + " is unregistering.");
        }
    }

    /**
     * Performs various maintenance operations upon a change in an existing
     * metric plug-in's service
     *
     * @param srefPlugin - the metric plug-in's service reference
     */
    private void pluginModified (ServiceReference srefPlugin) {
        // Get the PluginInfo object assigned to this plug-in
        PluginInfo pluginInfo = getPluginInfo(srefPlugin);
        if (pluginInfo != null) {
            logger.info(
                    "Plug-in service (" + pluginInfo.getPluginName() + ")"
                    + " was modified.");
        }
    }

/* ===[ Implementation of the ServiceListener interface ]================= */

    public void serviceChanged(ServiceEvent event) {
        // Get a reference to the affected service
        ServiceReference affectedService = event.getServiceReference();

        // Create a DB session
        sobjDB.startDBSession();

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
            pluginModified(affectedService);
        }

        // Close the DB session
        sobjDB.commitDBSession();
    }

/* ===[ Implementation of the PluginAdmin interface ]===================== */

    /* (non-Javadoc)
     * @see eu.sqooss.service.pa.PluginAdmin#listPlugins()
     */
    public Collection<PluginInfo> listPlugins() {
        return registeredPlugins.values();
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.pa.PluginAdmin#installPlugin(java.lang.String)
     */
    public boolean installPlugin(String hash) {
        Long sid = getServiceID(hash);
        if (sid != null) {
            return installPlugin (sid);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.pa.PluginAdmin#installPlugin(java.lang.Long)
     */
    public boolean installPlugin(Long serviceID) {
        logger.info (
                "Installing plugin with service ID " + serviceID);

        // Pre-formated error messages
        final String INSTALL_FAILED =
            "The installation of plugin with"
            + " service ID "+ serviceID
            + " failed : ";

        try {
            // Get the metric plug-in's service
            ServiceReference srefPlugin = getPluginService(serviceID);

            // Get the metric plug-in's object
            AlitheiaPlugin sobjPlugin = getPluginObject(srefPlugin);
            if (sobjPlugin != null) {
                // Execute the install() method of this metric plug-in,
                // and update the plug-in's information object upon success.
                if (sobjPlugin.install()) {
                    // Get the DAO that belongs to this metric plug-in
                    Plugin daoPlugin = pluginRefToPluginDAO(srefPlugin);
                    if (daoPlugin != null) {
                        // Create an info object for installed plug-in
                        PluginInfo pluginInfo =
                            createInstalledPI(srefPlugin, daoPlugin);
                        if (pluginInfo != null) {
                            // Remove the old "registered" info object
                            registeredPlugins.remove(serviceID.toString());
                            // Store the info object
                            registeredPlugins.put(
                                    pluginInfo.getHashcode(), pluginInfo);
                            return true;
                        }
                    }
                }
            }
            else {
                logger.warn(INSTALL_FAILED + CANT_GET_SOBJ);
            }
        } catch (Error e) {
            logger.warn(INSTALL_FAILED + e);
        } 

        return false;
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.pa.PluginAdmin#uninstallPlugin(java.lang.String)
     */
    public boolean uninstallPlugin(String hash) {
        Long sid = getServiceID(hash);
        if (sid != null) {
            return uninstallPlugin (sid);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.pa.PluginAdmin#uninstallPlugin(java.lang.Long)
     */
    public boolean uninstallPlugin(Long serviceID) {
        logger.info (
                "Uninstalling plugin with service ID " + serviceID);

        // Pre-formated error messages
        final String INSTALL_FAILED =
            "The uninstallation of plugin with"
            + " service ID "+ serviceID
            + " failed : ";

        try {
            // Get the metric plug-in's service
            ServiceReference srefPlugin = getPluginService(serviceID);

            // Get the metric plug-in's object
            AlitheiaPlugin sobjPlugin = getPluginObject(srefPlugin);
            if (sobjPlugin != null) {
                // Execute the remove() method of this metric plug-in,
                // and update the plug-in's information object upon success.
                if (sobjPlugin.remove()) {
                        // Create an info object for registered plug-in
                        PluginInfo pluginInfo =
                            createRegisteredPI(srefPlugin);
                        if (pluginInfo != null) {
                            // Remove the old "installed" info object
                            registeredPlugins.remove(
                                    sobjPlugin.getUniqueKey());
                            // Store the info object
                            registeredPlugins.put(
                                    pluginInfo.getHashcode(), pluginInfo);
                            return true;
                        }
                }
            }
            else {
                logger.warn(INSTALL_FAILED + CANT_GET_SOBJ);
            }
        } catch (Error e) {
            logger.warn(INSTALL_FAILED + e);
        } 

        return false;
    }

    public <T extends DAObject> List<PluginInfo> listPluginProviders(Class<T> o) {

        Iterator<PluginInfo> plugins = registeredPlugins.values().iterator();
        ArrayList<PluginInfo> matching = new ArrayList<PluginInfo>();

        while (plugins.hasNext()) {
            PluginInfo pi = plugins.next();
            if ((pi.installed)
                    && (pi.isActivationType(o))
                    && (pi.getServiceRef() != null)) {
                matching.add(pi);
            }
        }
        return matching;
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

    /* (non-Javadoc)
     * @see eu.sqooss.service.pa.PluginAdmin#getPlugin(eu.sqooss.service.pa.PluginInfo)
     */
    public AlitheiaPlugin getPlugin(PluginInfo pluginInfo) {
        if (pluginInfo != null) {
            return getPluginObject(pluginInfo.getServiceRef());
        }

        return null;
    }

    public void pluginUpdated(AlitheiaPlugin p) {
        // Make sure that this method is called with an existing DB session
        boolean ownDBSession = false;
        if (sobjDB.isDBSessionActive() == false) {
            // Create a DB session
            sobjDB.startDBSession();
            ownDBSession = true;
        }

        PluginInfo pi = getPluginInfo(p);
        if (pi == null) {
            logger.warn("Ignoring configuration update for not registered" +
                    " plugin <" + p.getName() + ">");
            if (ownDBSession) sobjDB.rollbackDBSession();
            return;
        }
        if (pi.installed == false) {
            logger.warn("Ignoring configuration update for not installed" +
                    " plugin <" + p.getName() + ">");
            if (ownDBSession) sobjDB.rollbackDBSession();
            return;
        }

        ServiceReference srefPlugin = pi.getServiceRef(); 
        Plugin pDao = pluginRefToPluginDAO(srefPlugin);

        PluginInfo plugInfo = createInstalledPI(srefPlugin, pDao);
        registeredPlugins.put(plugInfo.getHashcode(), plugInfo);

        logger.info("Plugin (" + plugInfo.getPluginName() + ") updated");

        // Close the DB session
        if (ownDBSession) sobjDB.commitDBSession();
    }

    public AlitheiaPlugin getImplementingPlugin(String mnemonic) {
        Iterator<String> i = registeredPlugins.keySet().iterator();

        while (i.hasNext()) {
            PluginInfo pi = registeredPlugins.get(i.next());
            // Skip metric plug-ins that are registered but not installed
            if (pi.installed) {
                ServiceReference sr = pi.getServiceRef();
                Plugin p = pluginRefToPluginDAO(sr);
                List<Metric> lm = Plugin.getSupportedMetrics(p);
                for (Metric m : lm){
                    if (m.getMnemonic() == mnemonic) {
                        return getPlugin(pi);
                    }
                }
            }
        }
        // No plug-ins found
        return null;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
