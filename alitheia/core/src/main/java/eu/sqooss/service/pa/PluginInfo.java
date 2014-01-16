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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.pa.ConfigurationType.InvalidValueForTypeException;
import eu.sqooss.service.util.StringUtils;

/**
 * This class holds runtime and configuration information about single metric
 * plug-in.
 * <br/>
 * Usually an instance of a <code>PluginInfo</code> is created from the
 * <code>PluginAdmin</code> implementation, just after a new metric plug-in
 * bundle is installed in the OSGi framework, who registers a metric
 * plug-in service. Some of the information provided from the metric
 * plug-in object registered with that OSGi service, as well as part of
 * the service's information are copied into this new <code>PluginInfo</code>
 * instance.
 */
public class PluginInfo implements Comparable<PluginInfo> {

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
    private Set<Class<? extends DAObject>> activationTypes =
        new HashSet<Class<? extends DAObject>>();

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
    private Set<PluginConfiguration> config =
        new HashSet<PluginConfiguration>();

    /**
     * This flag is set to <code>false<code> on a newly registered metric
     * plug-ins, and changed to <code>true</code> after the metric plug-in's
     * <code>install()</code> method is called (and successfully performed).
     */
    private boolean installed = false;

    /**
     * Empty constructor.
     */
    public PluginInfo() {
    	this(null,null);
    }

    /**
     * Simple constructor, that creates a new <code>PluginInfo</code> instance
     * and initializes it with the given metric plug-in's configuration
     * parameters.
     *
     * @param c - the list of configuration parameters
     */
    public PluginInfo(Set<PluginConfiguration> c) {
        this(c,null);
    }

    /**
     * Creates a new <code>PluginInfo</code> instance, and initializes it with
     * the given metric plug-in's configuration parameters and the description
     * fields found in the given plug-in instance.
     *
     * @param c - the list of configuration parameters
     * @param p the <code>AlitheiaPlugin</code> instance
     */
    public PluginInfo(Set<PluginConfiguration> c, AlitheiaPlugin p) {
        setPluginConfiguration(c);
        if (p != null) {
            setAlitheiaPlugin(p);
        }
    }


    /**
     * Initializes the configuration set that is available for this plug-in.
     * 
     * @param c the plug-in configuration set
     */
    public void setPluginConfiguration (Set<PluginConfiguration> c) {
        this.config = c;
    }
    
    /**
     * Gets the name, version and activationtypes of the given plug-in
     * and sets those values to the local fields. 
     * 
     * @param p the plug-in 
     */
    public void setAlitheiaPlugin(AlitheiaPlugin p){
    	this.pluginName = p.getName();
        this.pluginVersion = p.getVersion();
        this.activationTypes = p.getActivationTypes();
    }
    
    public boolean isInstalled() {
    	return this.installed;
    }
    
    public void install(){
    	this.installed = true;
    }
    
    public void uninstall(){
    	this.installed = false;
    }

    /**
     * Returns the list of existing metric configuration parameters.
     *
     * @return The list of configuration parameters.
     */
    public Set<PluginConfiguration> getConfiguration() {
        return this.config;
    }

    /**
     * Returns the Id of the given configuration property.
     *
     * @param name the property's name
     * @param type the property's type
     *
     * @return The property's Id, or <code>null</code> if the property does
     *   not exist.
     */
    public Long getConfPropId (String name, ConfigurationType type) {
        // Check if all values are valid before going into a loop
        if (isValidName(name) && type != null) {
	        // Search for a matching property
	        for (PluginConfiguration property : config) {
	            if (property.getName().equals(name) && property.getType().equals(type)) {
	                return property.getId();
	            }
	        }
        }
        return null;
    }

    /**
     * Verifies, if the specified configuration property exist in this
     * plug-in's information object.
     *
     * @param name the property's name
     * @param type the property's type
     *
     * @return <code>true</code>, if such property is found,
     *   or <code>false</code> otherwise.
     */
    public boolean hasConfProp (String name, ConfigurationType type) {
        return (getConfPropId(name, type) == null) ? false : true;
    }

    /**
     * Sets a new value of existing metric plugin's configuration property
     * by creating a new database record.
     *
     * @param db the DB components object
     * @param name the configuration property's name
     * @param type the configuration property's type
     * @param newVal the new value, that should be assigned to the
     *   selected configuration property
     *
     * @return <code>true</code> upon successful update, of <code>false</code>
     *   when a corresponding database record does not exist.
     *
     * @throws <code>Exception</code> upon incorrect value's syntax, or
     *   invalid property's type.
     */
    public boolean updateConfigEntry(DBService db, String name, ConfigurationType type, String newVal)
        throws InvalidValueForTypeException {
    	// Check if all values are valid before going into a loop
        if (isValidName(name) && type != null) {
	        // Check if such configuration property exists
	        for (PluginConfiguration pc : config) {
	            if (pc.getName().equals(name) && pc.getType().equals(type)) {	                
	                // Validate the new value for the type
	                type.checkValue(newVal);
	
	                // Update the given configuration property
	                pc = db.attachObjectToDBSession(pc);
	                pc.setValue(newVal);
	                return true;
	            }
	        }
        }
        return false;
    }

    /**
     * Adds a new configuration property for this metric plug-in by creating
     * a new database record for it.
     *
     * @param p the relevant plug-in
     * @param name the configuration property's name
     * @param type the configuration property's type
     * @param value the configuration property's value
     * @param description the configuration property's description
     *
     * @return <code>true</code> upon successful append, of <code>false</code>
     *   when a corresponding database record can not be created.
     *
     * @throws <code>Exception</code> upon incorrect value's syntax,
     *   invalid property's type, or invalid property's name.
     */
    public boolean addConfigEntry(
            Plugin p,
            String name,
            ConfigurationType type,
            String value,
            String description)
    throws IllegalArgumentException, InvalidValueForTypeException {
        // Check for invalid (null) properties
    	if (!isValidName(name)) {
        	throw new IllegalArgumentException("Name cannot be null or empty!");
        } else if( type == null ) {
        	throw new IllegalArgumentException("Type cannot be null!");
        }

        // Validate the value for the type
        type.checkValue(value);

        // Add the new configuration property
        PluginConfiguration newParam = new PluginConfiguration();
        newParam.setName(name);
        newParam.setMsg((description != null) ? description : "");
        newParam.setType(type);
        newParam.setValue(value);
        newParam.setPlugin(p);
        return p.getConfigurations().add(newParam);
    }
    
    public boolean addConfigEntry(
            Plugin p,
            String name,
            ConfigurationType type,
            String value)
    throws IllegalArgumentException, InvalidValueForTypeException {
    	return this.addConfigEntry(p,name,type,value,null);
    }

    /**
     * Removes an existing configuration property of this metric plug-in by
     * deleting its database record.
     *
     * @param db the DB components object
     * @param name the configuration property's name
     * @param type the configuration property's type
     *
     * @return <code>true</code> upon successful remove, or <code>false</code>
     *   when a corresponding database record can not be found.
     *
     * @throws <code>Exception</code> upon invalid property's type or name.
     */
    public boolean removeConfigEntry(
            DBService db,
            String name,
            ConfigurationType type) {
        // Get the property's Id
        Long propId = getConfPropId(name, type);
        if (propId != null) {
            // Remove the specified configuration property
            PluginConfiguration prop = db.findObjectById(
                    PluginConfiguration.class, propId);
            if (prop != null )
                return db.deleteRecord(prop);
        }

        return false;
    }

    /**
     * Returns the plugin's name stored in this object.
     *
     * @return Corresponding plug-in's name.
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * Returns the version stored in this <object.
     *
     * @return Corresponding plug-in's version.
     */
    public String getPluginVersion() {
        return pluginVersion;
    }

    /**
     * Returns the list off all activation interfaces (types) 
     * supported by the associated plug-in.
     *
     * @return - the list of supported activation interfaces
     */
    public Set<Class<? extends DAObject>> getActivationTypes() {
        return this.activationTypes;
    }

    /**
     * Adds one or more additional activation interfaces (types) to the
     * locally stored list of supported activation interfaces.
     *
     * @param activator - the list of additional activation interfaces
     */
    public void addActivationType(Class<? extends DAObject> activator) {
        this.activationTypes.add(activator);
    }

    /**
     * Compares the provided activation interface to the locally stored list
     * of supported activation interfaces.
     *
     * @return <code>true</code> when the given activation interface is found
     * in the list, or <code>false</code> otherwise.
     */
    public boolean isActivationType(Class<? extends DAObject> o) {
        // Compare the activation list's entries to the given activation interface, 
    	// until a match is found
        Iterator<Class<? extends DAObject>> i =
            this.activationTypes.iterator();
        while (i.hasNext()) {
            if (i.next().equals(o))
                return true;
        }
        return false;
    }

    /**
     * Initializes the corresponding local field with the reference to the
     * service, that registered the associated metric plug-in.
     *
     * @param serviceRef - the service reference
     */
    public void setServiceRef(ServiceReference serviceRef) {
        this.serviceRef = serviceRef;
    }

    /**
     * Returns the service reference that points to the associated metric
     * plug-in.
     *
     * @return The service reference.
     */
    public ServiceReference getServiceRef() {
        return serviceRef;
    }

    /**
     * Sets the hash code's value of this <code>PluginInfo</code> instance.
     * <br/>
     * The value must be unique, which means that no other
     * <code>PluginInfo</code> with the same hash code should be kept by
     * the <code>PluginAdmin</code> instance that created this object.
     *
     * @param hashcode - the hash code's value of this object
     */
    public void setHashcode(String hashcode) {
        this.hashcode = hashcode;
    }

    /**
     * Returns the hash code's value of this <code>MetricInfo</code> instance.
     *
     * @return The hash code's value of this object.
     */
    public String getHashcode() {
        return hashcode;
    }

    /**
     * Creates a text representation of this <code>MetricInfo</code>
     * instance.
     *
     * @return The text representation of this object.
     */
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        // Add the metric plug-in's name
        b.append((
                ((getPluginName() != null)
                        && (getPluginName().length() > 0))
                ? getPluginName()
                        : "[UNKNOWN]"));
        // Add the metric plug-in's version
        b.append((
                ((getPluginVersion() != null)
                        && (getPluginVersion().length() > 0))
                ? getPluginVersion()
                        : "[UNKNOWN]"));
        // Add the metric plug-in's class name
        b.append(" [");
        if (getServiceRef() != null) {
            String[] classNames =
                (String[]) serviceRef.getProperty(Constants.OBJECTCLASS);
            b.append ((
                    ((classNames != null)
                            && (classNames.length > 0))
                    ? (StringUtils.join(classNames, ","))
                            : "UNKNOWN"));
        }
        else {
            b.append("UNKNOWN");
        }
        b.append("]");
        return b.toString();
    }

    @Override
	public int compareTo(PluginInfo pi) {
		String other = pi.getHashcode();
		if( hashcode == null && other == null ){
			return 0;
		} else if (hashcode == null ^ other == null) {
	        return (hashcode == null) ? -1 : 1;
	    } else {
	    	return hashcode.compareTo(other);
	    }
	}
	
	@Override
	public int hashCode() {
		return (hashcode == null) ? 0 : hashcode.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj == null) {
			return false;
		}
		else if (!(obj instanceof PluginInfo)) {
			return false;
		}
		else {
			PluginInfo other = (PluginInfo) obj;
			if (hashcode == null) {
				if (other.hashcode != null) {
					return false;
				}
			} else if (!hashcode.equals(other.hashcode)) {
				return false;
			}
			return true;
		}
	}

	private static boolean isValidName(String name){
		return name != null && !name.trim().isEmpty();
	}
}

//vi: ai nosi sw=4 ts=4 expandtab
