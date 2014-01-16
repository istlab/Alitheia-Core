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

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
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
     * This list include all activation interfaces supported by the associated plug-in.
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
     * The hash code's value of the associated plug-in.
     * <br/>
     * After a new plug-in is registered as service in the OSGi framework, 
     * the <code>PluginAdmin</code> initializes this field with the service's ID value, 
     * by calling the <code>setHashcode(String)</code> method.
     * <br/>
     * Once the metric plug-in's <code>install()</code> method is called,
     * the <code>PluginAdmin</code> replaces the old <code>PluginInfo</code> with a new one, 
     * whose <code>hashcode</code> field is initialized with the hash code's value, 
     * that this metric plug-in stored in its database record.
     */
    private String hashcode;

    /**
     * A list containing the current set of configuration parameters of the metric plug-in
     */
    private Set<PluginConfiguration> config = new HashSet<PluginConfiguration>();

    /**
     * This flag is set to <code>false<code> on a newly registered metric plug-ins, 
     * and changed to <code>true</code> after the metric plug-in's 
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
     * and initializes it with the given metric plug-in's configuration parameters.
     *
     * @param c - the list of configuration parameters
     */
    public PluginInfo(Set<PluginConfiguration> c) {
        this(c,null);
    }

    /**
     * Creates a new <code>PluginInfo</code> instance, and initializes it with
     * the given metric plug-in's configuration parameters and the description
     * fields found in the given corresponding plug-in instance.
     *
     * @param c the list of configuration parameters
     * @param p the corresponding <code>AlitheiaPlugin</code> instance
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
     * Gets the name, version and activation types of the given plug-in
     * and sets those values to the local fields. 
     * 
     * @param p the plug-in 
     */
    public void setAlitheiaPlugin(AlitheiaPlugin p){
    	this.pluginName = p.getName();
        this.pluginVersion = p.getVersion();
        this.activationTypes = p.getActivationTypes();
    }
    
    /**
     * Returns if the corresponding plug-in is installed or not.
     *
     * @return True or false
     */
    public boolean isInstalled() {
    	return this.installed;
    }
    
    /** 
     * Flags the corresponding plug-in as installed 
     */
    public void install(){
    	this.installed = true;
    }
    
    /** 
     * Flags the corresponding plug-in as uninstalled 
     */
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
     * Returns the Id of the given name/type combination,
     * identifying a unique configuration property {@link PluginConfiguration}
     *
     * @param name the property's name
     * @param type the property's type
     *
     * @return The property's Id, or <code>null</code> if the property does not exist.
     */
    public Long getConfPropId(String name, ConfigurationType type) {
        // Check if all values are valid before going into a loop
        if (name != null && type != null) {
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
     * Checks if the specified configuration property exist.
     *
     * @param name the property's name
     * @param type the property's type
     *
     * @return <code>true</code>, if such property is found,
     *   or <code>false</code> otherwise.
     */
    public boolean hasConfProp(String name, ConfigurationType type) {
        return (getConfPropId(name, type) == null) ? false : true;
    }

    /**
     * Sets a new value of an existing configuration property by updating the database record.
     *
     * @param db the DB components object
     * @param entry the configuration property to update
     *
     * @return <code>true</code> upon successful update, of <code>false</code>
     *   when a corresponding database record does not exist.
     *
     * @throws <code>IllegalArgumentException</code> upon invalid db or entry
     */
    public boolean updateConfigEntry(DBService db, PluginConfiguration entry)
    throws IllegalArgumentException {
    	// Check for invalid (null) properties
    	validate(db,entry);
    	
	    // Check if the configuration property exists here
    	PluginConfiguration foundPC = null, newPC = null;
	    for (PluginConfiguration pc : config) {
	       if (pc.equals(entry)) {	                
	           // Update the given configuration property (db)
	    	   foundPC = pc;
	    	   newPC = db.attachObjectToDBSession(entry);
	    	   break;
	       }
	    }
    	if( newPC != null ){
	        // Update the given configuration property (local)
    		config.remove(foundPC);
    		config.add(newPC);
    		return true;
    	} else {
    		return false;
    	}
    }

    /**
     * Adds a new configuration property for by creating a new database record for it.
     *
     * @param db the DB components object
     * @param entry the configuration property to create
     *
     * @return <code>true</code> upon successful append, or <code>false</code>
     *   when a corresponding database record can not be created.
     *
     * @throws <code>IllegalArgumentException</code> upon invalid db or entry
     */
    public boolean addConfigEntry( DBService db, PluginConfiguration entry)
    throws IllegalArgumentException {
        // Check for invalid (null) properties
    	validate(db,entry);

        // Set the property's plug-in by using our hashcode
        try{ entry.setPlugin(Plugin.getPluginByHashcode(this.hashcode)); } 
        catch( Exception ignore ) { }
        
        // Add the new configuration property (db+local)
        if( db.addRecord(entry))
        	return config.add(entry);
        else
        	return false;
    }

    /**
     * Removes an existing configuration property of by deleting its database record.
     *
     * @param db the DB components object
     * @param entry the configuration property
     *
     * @return <code>true</code> upon successful remove, or <code>false</code>
     *   when a corresponding database record can not be found.
     *
     * @throws <code>IllegalArgumentException</code> upon invalid db or entry
     */
    public boolean removeConfigEntry(DBService db, PluginConfiguration entry)
    throws IllegalArgumentException {
    	// Check for invalid (null) properties
    	validate(db,entry);
    	
    	// Check if the configuration property exists here
    	boolean removed = false;
	    for (PluginConfiguration pc : config) {
	       if (pc.equals(entry)) {
	           // Remove the given configuration property (db)
	    	   removed = db.deleteRecord(entry);
	    	   break;
	       }
	    }
	    
	    if( removed )
	        // Remove the given configuration property (local)
	    	return config.remove(entry);
	    else 
	    	return false;
    }
    
    /**
     * Helper function to throw IllegalArgumentExceptions for nullpointer-arguments
     *
     * @param db the DB components object
     * @param entry the configuration property
     *
     * @throws <code>IllegalArgumentException</code> upon invalid db or entry
     */
    private static void validate(DBService db, PluginConfiguration entry)
    	    throws IllegalArgumentException {
    	if ( db == null ) {
        	throw new IllegalArgumentException("Database cannot be null!");
        } else if( entry == null ) {
        	throw new IllegalArgumentException("Entry cannot be null!");
        }
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
     * The value must be unique, which means that no other <code>PluginInfo</code> 
     * with the same hash code shoud be kept by the <code>PluginAdmin</code> 
     * instance that created this object.
     *
     * @param hashcode - the hash code's value of this object
     */
    public void setHashcode(String hashcode) {
        this.hashcode = hashcode;
    }

    /**
     * Returns the (raw) hash code's value of this instance.
     *
     * @return The hash code's value of this object.
     */
    public String getHashcode() {
        return hashcode;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        // Add the metric plug-in's name
        b.append((
                ((getPluginName() != null) && (getPluginName().length() > 0))
                ? getPluginName() : "[UNKNOWN]"));
        // Add the metric plug-in's version
        b.append((
                ((getPluginVersion() != null) && (getPluginVersion().length() > 0))
                ? getPluginVersion() : "[UNKNOWN]"));
        // Add the metric plug-in's class name
        b.append(" [");
        if (serviceRef != null) {
            String[] classNames = (String[])serviceRef.getProperty(Constants.OBJECTCLASS);
            b.append((
                    ((classNames != null) && (classNames.length > 0))
                    ? (StringUtils.join(classNames, ",")) : "UNKNOWN"));
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
}

//vi: ai nosi sw=4 ts=4 expandtab
