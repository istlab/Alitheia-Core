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
    private static final String CANT_GET_SOBJ =
        "The service object can not be retrieved!";

    private Logger logger;
    private BundleContext bc;

    /** 
     * Keeps a list of registered plugin services, indexed by each 
     * plugin's database hashcode
     */
    private HashMap<String, PluginInfo> registeredPlugins =
        new HashMap<String, PluginInfo>();

    public PAServiceImpl (BundleContext bc, Logger logger) {
        this.logger = logger;
        this.bc = bc;

        ServiceReference serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        AlitheiaCore core = (AlitheiaCore) bc.getService(serviceRef);
        
        core.getDBService();
        
        logger.info("PluginAdmin service starting.");
        
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
     * Creates a new <code>PluginInfo</code> object, by combining information
     * provided from the OSGi framework and the metric plug-in's record in
     * the database
     *
     * @param srefPlugin - the service reference object
     * @param p - the DAO object associated with this metric plug-in
     *
     * @return A {@link PluginInfo} object containing the extracted metric
     * information
     */
    private PluginInfo getPluginInfo (ServiceReference srefPlugin, Plugin p) {
        // Check for a valid service reference
        if (srefPlugin == null) {
            logger.error("Got a null service reference while getting plugin info");
            return null;
        }
        // Check for a valid DAO object
        if (p == null) {
            logger.error("Plugin record not in DB, plugin with service id <"
                    + srefPlugin.getProperty(Constants.SERVICE_ID)
                    + "> is not installed");
            return null;
        }
        // Begin with the info object's creation
        logger.debug("Creating info onject for plug-in " + p.getName());
        // Retrieve the metric plug-in's object from the service reference
        AlitheiaPlugin pluginObject = (AlitheiaPlugin) bc.getService(srefPlugin);
        if (pluginObject != null) {
            PluginInfo pluginInfo =
                new PluginInfo(Plugin.getConfigEntries(p), pluginObject);

            pluginInfo.setServiceRef(srefPlugin);
            pluginInfo.setHashcode(p.getHashcode());
            pluginInfo.installed = true;

            return pluginInfo;
        }

        return null;
    }

    /**
     * Try to associate a plugin service reference to a database plugin record
     * @param srefPlugin
     * @return A Plugin DAO object if entry in the database, or null
     */
    Plugin pluginRefToPluginDAO(ServiceReference srefPlugin) {
        AlitheiaPlugin plugin = (AlitheiaPlugin) bc.getService(srefPlugin);

        if (plugin == null) {
            logger.error("Error retrieving plugin interface from service"
                    + " reference. Uncaught error in plugin installation?");
            return null;
        }

        /* The DB object representing the Plugin */
        Plugin pluginDAO = Plugin.getPluginByHashcode(plugin.getUniqueKey());

        return pluginDAO;
    }
    
    /**
     * Performs various maintenance operations upon registration of a new 
     * metric plug-in service
     * 
     * @param srefPlugin the reference to the registered metric service
     */
    private void pluginRegistered (ServiceReference srefPlugin) {
        // Try to get the DAO that belongs to this metric plug-in
        Plugin pDao = pluginRefToPluginDAO(srefPlugin);
        // Store this metric service's ID as a string
        String srefId = srefPlugin.getProperty(
                Constants.SERVICE_ID).toString();

        // Retrieve information about this metric plug-in
        PluginInfo pluginInfo = getPluginInfo(srefPlugin, pDao);

        // Check for a plug-in that were installed
        if (pluginInfo != null) {
            // Remove the old "not yet installed" info entry (if any)
            if (registeredPlugins.containsKey(srefId))
                registeredPlugins.remove(srefId);
            // Store in the registered plug-ins list
            registeredPlugins.put(pluginInfo.getHashcode(), pluginInfo);
            logger.info("Plugin (" + pluginInfo.getPluginName() + ") registered");
        }
        // This plug-in is not yet installed
        else {
            AlitheiaPlugin sobjPlugin =
                (AlitheiaPlugin) bc.getService(srefPlugin);
            // Create a plug-in info object
            pluginInfo = new PluginInfo();
            pluginInfo.setPluginName(sobjPlugin.getName());
            pluginInfo.setPluginVersion(sobjPlugin.getVersion());
            pluginInfo.setServiceRef(srefPlugin);
            pluginInfo.setHashcode(srefId);
            // Mark as not installed
            pluginInfo.installed = false;
            // Store in the registered plug-ins list
            registeredPlugins.put(srefId, pluginInfo);
        }
    }

    /**
     * Performs various maintenance operations during unregistering of a plugin
     * service
     * 
     * @param srefPlugin
     *                the reference to the registered metric service
     */
    private void pluginUnregistering (ServiceReference srefPlugin) {
        PluginInfo pi = getPluginInfo(srefPlugin);
        // Service ID as String
        String srefId = srefPlugin.getProperty(
                Constants.SERVICE_ID).toString();

        if (pi == null) {
            logger.warn("Plugin info not found, plug-in already " +
                    "unregistered?");
            return;
        }

        logger.info("Plugin service \"" + pi.getPluginName() 
                + "\" is unregistering.");

        // Check for metric plug-ins that were registered but not installed
        if (registeredPlugins.containsKey(srefId)) {
            registeredPlugins.remove(srefId);
        }
        // Check for metric plug-ins that were registered and installed
        else {
            registeredPlugins.remove(pi.getHashcode());
        }
    }

    /**
     * Performs various maintenance operations upon a change in an existing
     * plugin service
     *
     * @param srefPlugin the reference to the registered metric service
     */
    private void pluginModified (ServiceReference srefPlugin) {
        PluginInfo pi = getPluginInfo(srefPlugin);
        logger.info("Plugin service \" "
                + pi.getPluginName() + " \" was modified.");
    }
    
    private PluginInfo getPluginInfo(ServiceReference sref) {
        
        for (PluginInfo p : this.registeredPlugins.values()) {
            if(p.getServiceRef().equals(sref))
                return p;
        }
        return null;
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
        return registeredPlugins.values();
    }

    /**
     * Extracts the service ID of the metric plug-in service described in the
     * metric plug-in's information object located by the specified hash 
     * code's value.
     * 
     * @param hash the hash code's value
     * 
     * @return The service ID.
     */
    private Long getServiceID (String hash) {
        if ((hash != null) && (registeredPlugins.containsKey(hash))) {
            // Get the plug-in info object pointed by the given hash
            PluginInfo infoPlugin = registeredPlugins.get(hash);

            // Retrieve the plug-in service from the info object
            ServiceReference srefPlugin = infoPlugin.getServiceRef();
            if (srefPlugin != null) {
                // Call the install() method on the plug-in service's object
                try {
                    Long sid =
                        (Long) srefPlugin.getProperty(Constants.SERVICE_ID);
                    return sid;
                }
                catch (ClassCastException e) {
                    return null;
                }
            }
        }

        return null;
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
    public boolean installPlugin(Long sid) {
        // Flag for a successful installation
        boolean installed = false;

        // Format a search filter for a service with the given service ID
        String serviceFilter =
            "(" + Constants.SERVICE_ID +"=" + sid + ")";
        logger.info (
                "Installing plugin with service ID " + sid);

        // Pre-formated error messages
        final String INSTALL_FAILED =
            "The installation of plugin with"
            + " service ID "+ sid
            + " failed : ";

        // Stores all services that match the search filter
        ServiceReference[] matchingServices = null;
        try {
            /* Since the search is performed using a service ID, it MUST
             * return only one service reference
             */
            matchingServices = bc.getServiceReferences(null, serviceFilter);
            if ((matchingServices == null) && (matchingServices.length != 1)) {
                logger.warn(NO_MATCHING_SERVICES);
                return installed;
            }

            // Get an instance of the matching service
            ServiceReference sref = matchingServices[0];
            if (sref == null) {
                logger.warn(NO_MATCHING_SERVICES);
            }

            // Retrieve the plug-in object registered with this service
            AlitheiaPlugin sobj = (AlitheiaPlugin) bc.getService(sref);
            if (sobj == null) {
                logger.warn(INSTALL_FAILED + CANT_GET_SOBJ);
                return installed;
            }

            // Execute the install() method of this metric plug-in
            installed = sobj.install();

            /* Create/update the plug-in's information object, upon successful
             * installation. 
             */
            if (installed) {
                // Remove the old "not yet installed" info entry (if any)
                if (registeredPlugins.containsKey(sid.toString()))
                    registeredPlugins.remove(sid.toString());
                // Create/update the plug-in info entry
                Plugin pdao = this.pluginRefToPluginDAO(sref);
                PluginInfo pi = getPluginInfo(sref, pdao);
                pi.installed = true;
                this.registeredPlugins.put(pi.getHashcode(), pi);
            }
        } catch (InvalidSyntaxException e) {
            logger.warn(INVALID_FILTER_SYNTAX);
        } catch (ClassCastException e) {
            logger.warn(INSTALL_FAILED + NOT_A_PLUGIN);
        } catch (Error e) {
            logger.warn(INSTALL_FAILED + e);
        } 

        return installed;
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
        // TODO: Implementation

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

    public AlitheiaPlugin getPlugin(PluginInfo m) {
        ServiceReference s = m.getServiceRef();
        return (AlitheiaPlugin) bc.getService(s);
    }

    public void pluginUpdated(AlitheiaPlugin p) {
        PluginInfo pi = getPluginInfo(p);
        if (pi == null) {
            logger.warn("Ignoring configuration update for not registered" +
                    " plugin <" + p.getName() + ">");
            return;
        }
        if (pi.installed == false) {
            logger.warn("Ignoring configuration update for not installed" +
                    " plugin <" + p.getName() + ">");
            return;
        }
        ServiceReference srefPlugin = pi.getServiceRef(); 
        Plugin pDao = pluginRefToPluginDAO(srefPlugin);
        
        PluginInfo plugInfo = getPluginInfo(srefPlugin, pDao);
        registeredPlugins.put(plugInfo.getHashcode(), plugInfo);

        logger.info("Plugin (" + plugInfo.getPluginName() + ") updated");
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
