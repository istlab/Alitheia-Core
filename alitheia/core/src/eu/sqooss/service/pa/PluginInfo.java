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

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.FileGroupMetric;
import eu.sqooss.service.abstractmetric.ProjectFileMetric;
import eu.sqooss.service.abstractmetric.ProjectVersionMetric;
import eu.sqooss.service.abstractmetric.StoredProjectMetric;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class holds runtime and configuration information about single metric
 * plug-in.
 * <br/>
 * Usually an instance of a <code>PluginInfo</code> is created from the
 * <code>PluginAdmin<code> implementation, just after a new metric plug-in
 * bundle is installed in the OSGi framework, who registers a metric
 * plug-in service. Some of the information provided from the metric
 * plug-in object registered with that OSGi service, as well as part of
 * the service's information are copied into this new <code>PluginInfo</code>
 * instance.
 */
public class PluginInfo {

    /**
     * This enumeration includes all permitted types of configuration values,
     * that a metrics can support. The various configuration parameters and
     * their values are used mostly from internal metric processes, like
     * results rendering and validation.
     */
    public enum ConfigurationType {
        INTEGER,
        STRING,
        BOOLEAN;
        
        public static ConfigurationType fromString(String config) {
            if (config == BOOLEAN.toString())
                return BOOLEAN;

            if (config == STRING.toString())
                return STRING;

            if (config == INTEGER.toString())
                return INTEGER;

            return null;
        }
    };

    /**
     * The service reference of the service that registered this metric
     * plug-in
     */
    private ServiceReference serviceRef = null;

    /**
     * The name of the associated  metric plug-in
     */
    private String pluginName = null;

    /**
     * The version of the associated metric plug-in
     */
    private String pluginVersion = null;

    /**
     * This list include all activation interfaces supported by the associated
     * metric plug-in.
     * <br/>
     * The list of permitted activation interfaces is described in the
     * {@link AlitheiaPlugin} interface and currently includes:
     * <ul>
     *   <li>{@link StoredProjectMetric}</li>
     *   <li>{@link ProjectVersionMetric}</li>
     *   <li>{@link ProjectFileMetric}</li>
     *   <li>{@link FileGroupMetric}</li>
     * </ul>
     */
    List<Class<? extends DAObject>> activationTypes; 

    /**
     * The hash code's value of the associated metric metric plug-in.
     * <br/>
     * After a new metric plug-in is registered as service in the OSGi
     * framework, the <code>PluginAdmin</code> initializes this field with
     * the service's ID value, by calling the <code>setHashcode(String)</code>
     * method.
     * <br/>
     * Once the metric plug-in's <code>install()</code> method is called,
     * the <code>PluginAdmin</code> replaces the old <code>PluginInfo</code>
     * with a new one, whose <code>hashcode</code> field is initialized with
     * the hash code's value, that this metric plug-in stored in its database
     * record.
     */
    private String hashcode;

    /**
     * A list containing the current set of configuration parameters of the
     * associated metric plug-in
     */
    private List<PluginConfiguration> config = null;

    /**
     * This flag is set to <code>false<code> on a newly registered metric
     * plug-ins, and changed to <code>true</code> after the metric plug-in's
     * <code>install()</code> method is called (and successfully performed).
     */
    public boolean installed = false;

    /**
     * Simple constructor, that creates a new <code>PluginInfo</code> instance
     * and initializes it with the given metric plug-in's configuration
     * parameters.
     * 
     * @param c - the list of configuration parameters
     */
    public PluginInfo(List<PluginConfiguration> c) {
        this.config = c;
    }
        
    /**
     * Updates the given metric plugin's configuration parameter with a new
     * value.
     * 
     * @param name - the configuration property name
     * @param newValue - the new value, that should be assigned to the
     *   given configuration property
     * 
     * @return <code>true</code> if the value has been successfully modified.
     *   Return value of <code>false</code> might indicate:
     *   <ul>
     *     <li>incorrect value type
     *     <li>failed update on the corresponding database record
     *   </ul>
     */
    public boolean updateConfigEntry(String name, String newValue) {

        for (PluginConfiguration pc : config) {
            if (pc.getName() == name) {
                ConfigurationType c = ConfigurationType
                        .fromString(pc.getType());

                if (c == null) {
                    return false;
                }

                if (c == ConfigurationType.BOOLEAN) {
                    if (newValue != "true" && newValue != "false") {
                        return false;
                    }
                } else if (c == ConfigurationType.INTEGER) {
                    try {
                        Integer.valueOf(newValue);
                    } catch (NumberFormatException nfe) {
                        return false;
                    }
                }
                HashMap<String, Object> names = new HashMap<String, Object>();
                
                return PluginConfiguration.updConfigurationEntry(
                        Plugin.getPluginByHashcode(hashcode), names);
            }
        }
        return false;
    }

    
    public void setPluginName(String metricName) {
        this.pluginName = metricName;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginVersion(String metricVersion) {
        this.pluginVersion = metricVersion;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public List<Class<? extends DAObject>> getActivationTypes() {
        return this.activationTypes;
    }
    
    public void setActivationTypes(List<Class<? extends DAObject>> l) {
        this.activationTypes = l;
    }

    public void addActivationType(Class<? extends DAObject> activator) {
        this.activationTypes.add(activator);
    }

   /**
    * Return true if the plug-in supports the provided activation type
    */
   public boolean isActivationType(Class<? extends DAObject> o) {
       
       Iterator<Class<? extends DAObject>> i = this.activationTypes.iterator();
       
       while (i.hasNext()) {
           if (i.next().equals(o)) 
               return true;
       }
       return false;
   }

    public ServiceReference getServiceRef() {
        return serviceRef;
    }

    public void setServiceRef(ServiceReference serviceRef) {
        this.serviceRef = serviceRef;
    }
    
    public List<PluginConfiguration> getConfiguration() {
        return this.config;
    }
   
    public String getHashcode() {
        return hashcode;
    }

    public void setHashcode(String hashcode) {
        this.hashcode = hashcode;
    }
    
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append((
                (getPluginName().length() > 0)
                ? getPluginName()
                        : "[UNKNOWN]"));
        b.append(" - ");
        b.append(getPluginVersion());
        b.append(" [");
        b.append(StringUtils.join((String[]) (serviceRef.getProperty(Constants.OBJECTCLASS)),","));
        b.append("]");
        return b.toString();
    }
}
