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

package eu.sqooss.service.pa;

import java.util.Collection;
import java.util.List;

import eu.sqooss.core.AlitheiaCoreService;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DAObject;

/**
 * <code>PluginAdmin</code> defines an interface for classes, that provide
 * utilities for managing SQO-OSS metric plug-ins.<br/>
 * <br/>
 * A <code>PluginAdmin</code> implementation should keep a list of metric
 * plug-in's information objects of type {@link PluginInfo}, describing the
 * available metric plug-ins, and indexed by a unique hash value.<br/>
 */
public interface PluginAdmin extends AlitheiaCoreService {

    /**
     * The constant <code>PLUGINS_CLASS</code> represents a filter, that can
     * be used during OSGi-based service search, and matches all
     * registered metric plug-in services.
     */
    public final static String PLUGIN_CLASS = "*.metrics.*";

    /**
     * Returns a collection containing information about all metrics plug-ins
     * currently available in the SQO-OSS framework.
     *
     * @return The list of all metrics plug-in services.
     */
    public Collection<PluginInfo> listPlugins();

    /**
     * Returns the metric plug-in's information object, that belongs to the
     * given metric plug-in object.
     * 
     * @param m - the metric plug-in object
     * 
     * @return All information that <code>PluginAdmin</code> has collected
     * about this metric plug-in.
     */
    public PluginInfo getPluginInfo(AlitheiaPlugin m);

    /**
     * Returns the metric plug-in's information object, that belongs to the
     * metric plug-in with the given hash code.
     *
     * @param hash - the hash code value
     * 
     * @return The <code>PluginInfo</code> object associated with that
     *   plug-in, or <code>null</null> when the specified hash code doesn't
     *   reference an existing plug-in.
     */
    public PluginInfo getPluginInfo(String hash);

    /**
     * Get the metric plug-in object, that corresponds to the given plug-in's
     * information object.
     * 
     * @param m - the metric plug-in information object
     * 
     * @return The metric plug-in object.
     */
    public AlitheiaPlugin getPlugin(PluginInfo m);

    /**
     * Get the list of metric plug-ins, whose list of activation types include
     * a type, that matches the interface of the provided object.
     *
     * @param o - object that implies the type of interface, that is wanted
     * 
     * @return Collection of services references, or <code>null</code>
     *   if no such interface type exist.
     */
    public <T extends DAObject> List<PluginInfo> listPluginProviders(Class<T> o);
    
    /**
     * Get the metric plug-in object, that implements the metric, 
     * whose name matches the provided mnemonic name.
     *  
     * @param mnemonic - the metric's mnemonic name
     * 
     * @return The metric plug-in object that implements this metric, or null
     * if no such plug-in found.
     */
    public AlitheiaPlugin getImplementingPlugin(String mnemonic);

    /**
     * This method calls the <code>install()</code> method of the metric
     * plug-in object, that is provided from the metric plug-in service
     * registered with the given service ID.<br/>
     * The installation process involves updating the metric plug-in's
     * information object and creating the corresponding database records.
     *
     * @param service_ID - the OSGi service ID of the selected metric
     *   plug-in service
     *
     * @return <code>true</code>, if successfully installed,
     *   or <code>false</code> otherwise
     */
    public boolean installPlugin(Long service_ID);

    /**
     * This method calls the <code>install()</code> method of the metric
     * plug-in object, that is provided from the metric plug-in service
     * located by the specified hash value.<br/>
     * The installation process involves updating the metric plug-in's
     * information object and creating the corresponding database records.
     *
     * @param hash - the hash value
     * 
     * @return <code>true</code>, if successfully installed,
     *   or <code>false</code> otherwise
     */
    public boolean installPlugin(String hash);

    /**
     * This method calls the <code>remove()</code> method of the metric
     * plug-in object, that is provided from the metric plug-in service
     * registered with the specified service ID.<br/>
     * The de-installation process involves updating the metric plug-in's
     * information object and removing the associated database records.
     * 
     * @param serviceID - the OSGi service ID of the selected metric
     *   plug-in service
     * 
     * @return <code>true</code>, if successfully removed,
     *   or <code>false</code> otherwise
     */
    public boolean uninstallPlugin(Long serviceID);

    /**
     * This method calls the <code>remove()</code> method of the metric
     * plug-in object, that is provided from the metric plug-in service
     * located by the specified hash value.<br/>
     * The de-installation process involves updating the metric plug-in's
     * information object and removing the associated database records.
     * 
     * @param hash - the hash value
     * 
     * @return <code>true</code>, if successfully removed,
     *   or <code>false</code> otherwise
     */
    public boolean uninstallPlugin(String hash);

    /**
     * Refreshes the metric plug-in's information object, when the database
     * records associated with the corresponding metric plug-in has been
     * updated.
     * 
     * @param p The updated metric plug-in
     */
    public void pluginUpdated(AlitheiaPlugin p);
}

//vi: ai nosi sw=4 ts=4 expandtab
