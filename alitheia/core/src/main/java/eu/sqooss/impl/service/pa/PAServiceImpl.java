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

package eu.sqooss.impl.service.pa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;

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
    private AlitheiaCore sobjCore = null;
    
    /**
     * Keeps a list of registered metric plug-in's services, indexed by the
     * plugin's hash code (stored in the database).
     */
    private ConcurrentHashMap<String, PluginInfo> registeredPlugins =
        new ConcurrentHashMap<String, PluginInfo>();

    public PAServiceImpl () { }

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
    private Long getServiceId (String hash) {
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
            if ((matchingServices == null) || (matchingServices.length != 1)) {
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
//            PluginInfo pluginInfo = new PluginInfo();
//            pluginInfo.setPluginName(sobjPlugin.getName());
//            pluginInfo.setPluginVersion(sobjPlugin.getVersion());
//            pluginInfo.setServiceRef(srefPlugin);
//            pluginInfo.setHashcode(getServiceId(srefPlugin).toString());
            PluginInfo pluginInfo =
                new PluginInfo(sobjPlugin.getConfigurationSchema(), sobjPlugin);
            pluginInfo.setServiceRef(srefPlugin);
            pluginInfo.setHashcode(sobjPlugin.getUniqueKey());
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
                new PluginInfo(p.getConfigurations(), sobjPlugin);
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
        Long sid = getServiceId(hash);
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
                        PluginInfo pluginInfo = createInstalledPI(srefPlugin,
                                daoPlugin);
                        if (pluginInfo != null) {
                            // Remove the old "registered" info object
                            registeredPlugins.remove(
                                    sobjPlugin.getUniqueKey());
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
        Long sid = getServiceId(hash);
        if (sid != null) {
            return uninstallPlugin (sid);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.pa.PluginAdmin#uninstallPlugin(java.lang.Long)
     */
    public boolean uninstallPlugin(Long serviceID) {
        Scheduler s = AlitheiaCore.getInstance().getScheduler();
        try {
            PluginUninstallJob puj = new PluginUninstallJob(serviceID);
            s.enqueue(puj);
        } catch (SchedulerException e) {
            ServiceReference srefPlugin = getPluginService(serviceID);
            // Get the metric plug-in's object
            AlitheiaPlugin sobjPlugin = getPluginObject(srefPlugin);
            logger.warn("Failed to enqueue plugin uninstall job for plug-in <" 
                    +  sobjPlugin.getName() + ">. Error was: " + e.getMessage());
            return false;
        }
        return true;
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

            if (mi.getPluginName().equals(m.getName())
                    && mi.getPluginVersion().equals(m.getVersion())) {
                return mi;
            }
        }
        return null;
    }

    public PluginInfo getPluginInfo(String hash) {
        return registeredPlugins.get(hash);
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
        // Get the plug-in's info object
        PluginInfo pi = getPluginInfo(p);
        // Will happen if called during bundle's startup
        if (pi == null) {
            logger.warn("Ignoring configuration update for not active" +
                    " plugin <" + p.getName() + "> bundle.");
            return;
        }
        // Check for installed metric plug-in
        if (pi.installed) {
            ServiceReference srefPlugin = pi.getServiceRef();
            Plugin pDao = pluginRefToPluginDAO(srefPlugin);
            pi = createInstalledPI(srefPlugin, pDao);
            if (pi != null) {
                registeredPlugins.put(pi.getHashcode(), pi);
                logger.info("Plug-in (" + pi.getPluginName()
                        + ") successfuly updated");
                // TODO: Not sure, if this is the correct plug-in method
                //       to call upon configuration update, but it is the
                //       only one which performs something in that scope.
                //getPlugin(pi).update();
            }
        }
        // The given metric plug-in is not installed
        else {
            logger.warn("Ignoring configuration update for registered"
                    + " plug-in (" + p.getName() + ")");
        }
    }

    public AlitheiaPlugin getImplementingPlugin(String mnemonic) {
        Iterator<String> i = registeredPlugins.keySet().iterator();

        while (i.hasNext()) {
            PluginInfo pi = registeredPlugins.get(i.next());
            // Skip metric plug-ins that are registered but not installed
            if (pi.installed) {
                ServiceReference sr = pi.getServiceRef();
                Plugin p = pluginRefToPluginDAO(sr);
                Set<Metric> lm = p.getSupportedMetrics();
                for (Metric m : lm){
                    if (m.getMnemonic().equals(mnemonic)) {
                        return getPlugin(pi);
                    }
                }
            }
        }
        // No plug-ins found
        return null;
    }
    
    private class PluginUninstallJob extends Job {

        private Long serviceID;
        
        public PluginUninstallJob(Long serviceID) {
            this.serviceID = serviceID;
        }
        
        @Override
        public long priority() {
            // TODO Auto-generated method stub
            return 0x3;
        }

        @Override
        protected void run() throws Exception {
            // TODO Auto-generated method stub
            logger.info (
                    "Uninstalling plugin with service ID " + serviceID);

            // Pre-formated error messages
            final String UNINSTALL_FAILED =
                "The uninstallation of plugin with"
                + " service ID "+ serviceID
                + " failed : ";

            try {
                DBService dbs = AlitheiaCore.getInstance().getDBService();
                dbs.startDBSession();
                // Get the metric plug-in's service
                ServiceReference srefPlugin = getPluginService(serviceID);

                // Get the metric plug-in's object
                AlitheiaPlugin sobjPlugin = getPluginObject(srefPlugin);
                if (sobjPlugin == null) {
                    logger.warn(UNINSTALL_FAILED + CANT_GET_SOBJ);
                }
                // Execute the remove() method of this metric plug-in,
                // and update the plug-in's information object upon success.
                if (sobjPlugin.remove()) {
                    // Release the stored configuration DAOs
                    getPluginInfo(sobjPlugin).setPluginConfiguration(
                            new HashSet<PluginConfiguration>());
                    
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
                    }
                }
                dbs.commitDBSession();
            } catch (Exception e) {
                logger.warn(UNINSTALL_FAILED, e);
            }
        }
        
        @Override
        public String toString() {
            return "PluginUninstallJob - ServiceID:{" + serviceID + "}" ;
        }
    }

	@Override
	public boolean startUp() {
	    logger.info("Starting the PluginAdmin component.");

        // Get the AlitheiaCore's object
        sobjCore = AlitheiaCore.getInstance();

        if (sobjCore != null) {
            // Obtain the required core components
            sobjDB = sobjCore.getDBService();
            if (sobjDB == null) {
                logger.error("Can not obtain the DB object!");
            }

            try {
                bc.addServiceListener(this, SREF_FILTER_PLUGIN);
            } catch (InvalidSyntaxException e) {
                logger.error("Invalid filter syntax ", e);
            }
            
            logger.debug("The PluginAdmin component was successfully started.");
        }
        else {
            logger.error("Can not obtain the Core object!");
        }
		return true;
	}

	@Override
	public void shutDown() {
		return ;
	}

	@Override
	public void setInitParams(BundleContext bc, Logger l) {
	    this.bc = bc;
        this.logger = l;
	}
}

//vi: ai nosi sw=4 ts=4 expandtab
