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
        "Not a plugin service!";
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
        
      //  collectPluginInfo();
        
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
     * Constructs a PluginInfo object, by combining information
     * from OSGi and the plug-in's record in the database
     *
     * @param srefPlugin the service reference object
     *
     * @return a {@link PluginInfo} object containing the extracted metric
     * information
     */
    private PluginInfo getPluginInfo (ServiceReference srefPlugin, Plugin p) {
        if (srefPlugin == null) {
            logger.error("Got a null service reference while getting plugin info");
            return null;
        }
        
        if (p == null) {
            logger.error("Plugin record not in DB, plugin with service id <"
                    + srefPlugin.getProperty(Constants.SERVICE_ID)
                    + "> is not installed");
            return null;
        }
        logger.debug("Getting info for plugin " + p.getName());
        AlitheiaPlugin pluginObject = (AlitheiaPlugin) bc.getService(srefPlugin);
        
        PluginInfo pluginInfo = new PluginInfo(Plugin.getConfigEntries(p));

        pluginInfo.setServiceRef(srefPlugin);
        pluginInfo.setHashcode(p.getHashcode());
        pluginInfo.installed = true;
        
        if (pluginObject != null) {
            pluginInfo.setPluginName(pluginObject.getName());
            pluginInfo.setPluginVersion(pluginObject.getVersion());
            pluginInfo.setActivationTypes(pluginObject.getActivationTypes());
        }
        
        return pluginInfo;
    }

    /**
     * Try to associate a plugin service reference to a database plugin record
     * @param srefPlugin
     * @return A Plugin DAO object if entry in the database, or null
     */
    private Plugin pluginRefToPluginDAO(ServiceReference srefPlugin) {
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
     * plugin service
     * 
     * @param srefPlugin the reference to the registered metric service
     */
    private void pluginRegistered (ServiceReference srefPlugin) {
        
        Plugin pDao = pluginRefToPluginDAO(srefPlugin); 
      
        /* Retrieve information about this plugin and add this plugin to the
         * list of registered/available plugins 
         */
        PluginInfo plugInfo = getPluginInfo(srefPlugin, pDao);
        registeredPlugins.put(plugInfo.getHashcode(), plugInfo);

        logger.info("Plugin (" + plugInfo.getPluginName() + ") registered");
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

        if (pi == null) {
            logger.warn("Plugin info not found, plug-in already " +
            		"unregistered?");
            return;
        }
        
        logger.info("Plugin service \""+ pi.getPluginName() 
                + "\" is unregistering.");

        registeredPlugins.remove(pi.getHashcode());
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

    public boolean installPlugin(Long sid) {
        // Format a search filter for the plugin service with <sid> serviceId
        boolean installed = false;
        String serviceFilter =
            "(" + Constants.SERVICE_ID +"=" + sid + ")";
        logger.info (
                "Installing plugin with service ID " + sid);

        final String INSTALL_FAILED =
            "The installation of plugin with"
            + " service ID "+ sid
            + " failed : ";
        
        ServiceReference[] matchingServices = null;

        try {
            matchingServices = bc.getServiceReferences(null, serviceFilter);

            // Since the search was performed using a serviceId, it must
            // be only one service reference that is found
            if ((matchingServices == null) && (matchingServices.length != 1)) {
                logger.warn(NO_MATCHING_SERVICES);
                return installed;
            }

            ServiceReference sref = matchingServices[0];

            if (sref == null) {
                logger.warn(NO_MATCHING_SERVICES);
            }

            /*Retrieve the plugin object registered with this service */
            AlitheiaPlugin sobj = (AlitheiaPlugin) bc.getService(sref);
            if (sobj == null) {
                logger.warn(INSTALL_FAILED + CANT_GET_SOBJ);
                return installed;
            }

            /* Execute the install() method for the plugin */
            installed = sobj.install();

            /* If installation is successful, then report this 
             * into the plugin's information object
             */
            if (installed) {
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

    public <T extends DAObject> List<PluginInfo> listPluginProviders(Class<T> o) {

        Iterator<PluginInfo> plugins = registeredPlugins.values().iterator();
        ArrayList<PluginInfo> matching = new ArrayList<PluginInfo>();

        while (plugins.hasNext()) {
            PluginInfo pi = plugins.next();
            if ((pi.isActivationType(o)) && (pi.getServiceRef() != null)) {
                matching.add(pi);
            }
        }
        return matching;
    }
    
    public boolean uninstallPlugin(Long serviceID) {
        
        return false;
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
            logger.warn("Ignoring configuration update for not registered " +
            		"plugin <" + p.getName() + ">");
            return;
        }
        ServiceReference srefPlugin = pi.getServiceRef(); 
        Plugin pDao = pluginRefToPluginDAO(srefPlugin);
        
        PluginInfo plugInfo = getPluginInfo(srefPlugin, pDao);
        registeredPlugins.put(plugInfo.getHashcode(), plugInfo);

        logger.info("Plugin (" + plugInfo.getPluginName() + ") updated");
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
