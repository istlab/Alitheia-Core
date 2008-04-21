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
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Runtime and configuration information for plugins. This class is 
 * automatically generated for all the plug-ins the system knows of.
 */
public class PluginInfo {

    /**
     * These are the types of configuration values that metrics can
     * support. This is used mostly for rendering and validation purposes.
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
    
    /** The bundle id as returned from OSGi */
    private ServiceReference serviceRef = null;
    
    /** The bundle id as returned from the plugin */
    private String pluginName = null;
    
    /** The bundle id as returned from the plugin */
    private String pluginVersion = null;
    
    /** Sub-interfaces of the {@link AlitheiaPlugin} interface*/
    List<Class<? extends DAObject>> activationTypes; 
    
    /** Is the plug-in registered to the system? */
    public boolean installed = false;
    
    /** The metric implementation class hashcode */
    private String hashcode;
    
    /** A list containing plugin configuration entries*/
    private List<PluginConfiguration> config = null;

    /**
     * Constructor
     * @param c
     */
    public PluginInfo(List<PluginConfiguration> c) {
        this.config = c;
    }
        
    /**
     * Update a configuration entry for a plugin.
     * 
     * @param name The configuration property name
     * @param newValue The new value to assign to the config property
     * 
     * @return True if the value change operation succeeded. False might 
     * indicate incorrect new value type or error updating the database
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
