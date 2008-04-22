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
package eu.sqooss.service.pa;

import java.util.Collection;
import java.util.List;

import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DAObject;

/**
 * PluginAdmin defines an interface for classes that provide utilities for
 *  managing SQO-OSS plug-ins.
 */
public interface PluginAdmin {

    /** The Constant PLUGINS_CLASS is used as a filter when searching
     * for registered metric services. */
    public final static String PLUGIN_CLASS = "eu.sqooss.impl.metrics.*";

    /**
     * Returns a collection containing information about all metrics services
     * currently registered in the framework.
     *
     * @return the list of all metrics services registered in the framework
     */
    public Collection<PluginInfo> listPlugins();
    
    /**
     * Returns a plug-in info object for a specific plug-in
     * 
     * @param m The metric to return info about
     * @return Information that the system has about a specific metric
     */
    public PluginInfo getPluginInfo(AlitheiaPlugin m);
    
    /**
     * Get the plugin's interface from a plug-in information object 
     * @param m
     * @return The metric interface
     */
    public AlitheiaPlugin getPlugin(PluginInfo m);

    /**
     * Get the list of metrics whose activation types match the provided
     * class
     *
     * @param o Object that implies the type of interface that is wanted.
     * @return Collection of services references. May be null
     *          if no such interfaces exist.
     */
    public <T extends DAObject> List<PluginInfo> listPluginProviders(Class<T> o);
    
    /**
     * Get a reference to the plug-in interface that implements the metric 
     * whose name matches the provided mnemonic name. 
     *  
     * @param mnemonic 
     * @return A reference to the implementing plugin interface
     */
    public AlitheiaPlugin getImplementingPlugin(String mnemonic);
    
    /**
     * Calls the install() method of the metric object provided from a metric
     * service registered with the specified service ID.
     *
     * @param service_ID the service ID of the selected metric service
     *
     * @return true, if successful; false otherwise
     */
    public boolean installPlugin(Long service_ID);

    /**
     * TODO: Better explanation
     * Calls the install() method of the metric object provided from a metric
     * service located by the given hashcode value.
     *
     * @param hashcode the hashcode used to index the <code>PluginInfo</code>
     * object that points to the affected metric service
     *
     * @return true, if successful; false otherwise
     */
    public boolean installPlugin(String hashcode);

    /**
     * Remove the plug-in and the associated entries from the database
     * @param serviceID The plug-in's service ID
     * @return True if removal succeeded, false otherwise
     */
    public boolean uninstallPlugin(Long serviceID);
    
    /**
     * Update the plugin registration info when the plug-in has updated
     * its database records
     * @param p The updated plugin
     * @return
     */
    public void pluginUpdated(AlitheiaPlugin p);

}

//vi: ai nosi sw=4 ts=4 expandtab
