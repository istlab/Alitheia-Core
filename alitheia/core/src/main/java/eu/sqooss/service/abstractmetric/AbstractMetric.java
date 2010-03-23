/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
 *                 Athens, Greece.
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
package eu.sqooss.service.abstractmetric;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailMessageMeasurement;
import eu.sqooss.service.db.MailingListThread;
import eu.sqooss.service.db.MailingListThreadMeasurement;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.util.Pair;

/**
 * A base class for all metrics. Implements basic functionality such as
 * logging setup and plug-in information retrieval from the OSGi bundle
 * manifest file. Metrics can choose to directly implement
 * the {@link eu.sqooss.abstractmetric.AlitheiaPlugin} interface instead of 
 * extending this class.
 */
public abstract class AbstractMetric implements AlitheiaPlugin {

    /** Reference to the metric bundle context */
    protected BundleContext bc;

    /** Logger for administrative operations */
    protected Logger log = null;

    /** Reference to the DB service, not to be passed to metric jobs */
    protected DBService db;

    /** 
     * Reference to the plugin administrator service, not to be passed to 
     * metric jobs 
     */
    protected PluginAdmin pa;

    /** 
     * Metric mnemonics for the metrics required to be present for this 
     * metric to operate.
     */
    private Set<String> dependencies = new HashSet<String>();
    
    /** Set of declared metrics indexed by their mnemonic*/
    private Map<String, Metric> metrics = new HashMap<String, Metric>();
    
    /** The list of this plug-in's activators*/
    private Set<Class<? extends DAObject>> activators = 
        new HashSet<Class<? extends DAObject>>();
    
    /**
     * Init basic services common to all implementing classes
     * @param bc - The bundle context of the implementing metric - to be passed
     * by the activator.
     */
    protected AbstractMetric(BundleContext bc) {

        this.bc = bc;
       
        log = AlitheiaCore.getInstance().getLogManager().createLogger(Logger.NAME_SQOOSS_METRIC);

        if (log == null) {
            System.out.println("ERROR: Got no logger");
        }

        db = AlitheiaCore.getInstance().getDBService();

        if(db == null)
            log.error("Could not get a reference to the DB service");

        pa = AlitheiaCore.getInstance().getPluginAdmin();

        if(pa == null)
            log.error("Could not get a reference to the Plugin Administation "
                    + "service");
        
        /*Discover the declared metrics*/
        MetricDeclarations md = this.getClass().getAnnotation(MetricDeclarations.class);

		if (md != null && md.metrics().length > 0) {
			for (MetricDecl metric : md.metrics()) {
				log.debug("Found metric: " + metric.mnemonic() + " with "
						+ metric.activators().length + " activators");

				if (metrics.containsKey(metric.mnemonic())) {
				    log.error("Duplicate metric mnemonic " + metric.mnemonic());
				    continue;
				}
				
				Metric m = new Metric();
				m.setDescription(metric.descr());
				m.setMnemonic(metric.mnemonic());
				m.setMetricType(new MetricType(MetricType.fromActivator(metric.activators()[0])));
			
				activators.addAll(Arrays.asList(metric.activators()));
				
				metrics.put(m.getMnemonic(), m);
				if (metric.dependencies().length > 0)
					dependencies.addAll(Arrays.asList(metric.dependencies()));
			}
		} else {
			log.warn("Plug-in " + getName() + " declares no metrics");
		}
     }

    /**
     * Retrieve author information from the plug-in bundle
     */
    public String getAuthor() {

        return (String) bc.getBundle().getHeaders().get(
                org.osgi.framework.Constants.BUNDLE_CONTACTADDRESS);
    }

    /**
     * Retrieve the plug-in description from the plug-in bundle
     */
    public String getDescription() {

        return (String) bc.getBundle().getHeaders().get(
                org.osgi.framework.Constants.BUNDLE_DESCRIPTION);
    }

    /**
     * Retrieve the plug-in name as specified in the metric bundle
     */
    public String getName() {

        return (String) bc.getBundle().getHeaders().get(
                org.osgi.framework.Constants.BUNDLE_NAME);
    }

    /**
     * Retrieve the plug-in version as specified in the metric bundle
     */
    public String getVersion() {

        return (String) bc.getBundle().getHeaders().get(
                org.osgi.framework.Constants.BUNDLE_VERSION);
    }

    /**
     * Retrieve the installation date for this plug-in version
     */
    public final Date getDateInstalled() {
        return Plugin.getPluginByHashcode(getUniqueKey()).getInstalldate();
    }

    Map<Long,Pair<Object,Long>> blockerObjects = new ConcurrentHashMap<Long,Pair<Object,Long>>();

    /**
     * Call the appropriate getResult() method according to
     * the type of the entity that is measured.
     *
     * Use this method if you don't want the metric results
     * to be calculated on-demand. Otherwise, use getResult().
     *
     * @param o DAO that specifies the desired result type.
     *      The type of o is used to dispatch to the correct
     *      specialized getResult() method of the sub-interfaces.
     * @return result (measurement) performed by this metric
     *      on the project data specified by o.
     * @throws MetricMismatchException if the DAO is of a type
     *      not supported by this metric.
     */
     @SuppressWarnings("unchecked")
     public Result getResultIfAlreadyCalculated(DAObject o, List<Metric> l) throws MetricMismatchException {
        boolean found = false;
        Result r = new Result();
        
        for (Metric m : l) {
            if (!metrics.containsKey(m.getMnemonic())) {
                throw new MetricMismatchException("Metric " + m.getMnemonic()
                        + " not defined by plugin "
                        + Plugin.getPluginByHashcode(getUniqueKey()).getName());
            }
            List<ResultEntry> re = null;
            for (Class<? extends DAObject> c : getActivationTypes()) {
                if (c.isInstance(o)) {
                    found = true;
                    try {
                        Method method = this.getClass().getMethod("getResult", c, Metric.class);
                        re =  (List<ResultEntry>) method.invoke(this, o, m);
                    } catch (SecurityException e) {
                        logErr("getResult", o, e);
                    } catch (NoSuchMethodException e) {
                        log.error("No method getResult(" + c.getName()
                                + ") for type " + this.getClass().getName());
                    } catch (IllegalArgumentException e) {
                        logErr("getResult", o, e);
                    } catch (IllegalAccessException e) {
                        logErr("getResult", o, e);
                    } catch (InvocationTargetException e) {
                        logErr("getResult", o, e);
                    }
                }
            }
            if (!found) {
                throw new MetricMismatchException(o);
            }

            if (re != null) {
                r.addResultRow(new ArrayList<ResultEntry> (re));
            }
        }

        return r;
    }

     /**
      * Convert a list of ProjectFileMeasurements to the (less-well-typed) list
      * of ResultEntries; this just extracts the single integer value stored as the
      * result in each ProjectFileMeasurement. The metric mnemonic may be provided
      * as a label for the measurement.
      * 
      * @param l List of measurements to convert
      * @param label Metric mnemonic label
      * @return null if there are no measurements; otherwise a list of ResultEntries
      */
     static public List<ResultEntry> convertFileMeasurements(List<ProjectFileMeasurement> l,String label) {
    	 // "No result" is null, not an empty list of results
    	 if (null == l) {
    		 return null;
    	 }
    	 if (l.isEmpty()) {
    		 return null;
    	 }

    	 List<ResultEntry> results = new ArrayList<ResultEntry>();
    	 for(ProjectFileMeasurement r : l) {
    	     // There is only one measurement per metric and project file
            Integer value = Integer.parseInt(r.getResult());
            // ... and therefore only one result entry
            ResultEntry entry = new ResultEntry(value,
                    ResultEntry.MIME_TYPE_TYPE_INTEGER, label);
            results.add(entry);
    	 }
    	 return results;
     }

     /**
      * Convert a list of ProjectVersionMeasurements to the (less-well-typed) list
      * of ResultEntries; this just extracts the single integer value stored as the
      * result in each ProjectVersionMeasurement. The metric mnemonic may be provided
      * as a label for the measurement.
      * 
      * @param l List of measurements to convert
      * @param label Metric mnemonic label
      * @return null if there are no measurements; otherwise a list of ResultEntries
      */
     static public List<ResultEntry> convertVersionMeasurements(List<ProjectVersionMeasurement> l,String label) {
    	 // "No result" is null, not an empty list of results
    	 if (null == l) {
    		 return null;
    	 }
    	 if (l.isEmpty()) {
    		 return null;
    	 }

    	 List<ResultEntry> results = new ArrayList<ResultEntry>();
    	 for(ProjectVersionMeasurement r : l) {
    		 // There is only one measurement per metric and project file
    		 Integer value = Integer.parseInt(r.getResult());
    		 // ... and therefore only one result entry
    		 ResultEntry entry = 
    			 new ResultEntry(value, ResultEntry.MIME_TYPE_TYPE_INTEGER, label);
    		 results.add(entry);
    	 }
    	 return results;
     }

     /**
      * Convenience method to convert a single measurement to a list of
      * result entries; like calling convertVersionMeasurements() with
      * a singleton list.
      * @param v Single measurement to convert
      * @param label Metric mnemonic label
      * @return Singleton list of results for the measurement
      */
     static public List<ResultEntry> convertVersionMeasurement(ProjectVersionMeasurement v, String label) {
         List<ResultEntry> results = new ArrayList<ResultEntry>(1);
         results.add(new ResultEntry(Integer.parseInt(v.getResult()), ResultEntry.MIME_TYPE_TYPE_INTEGER, label));
         return results;
     }

    /**
     * Call the appropriate getResult() method according to
     * the type of the entity that is measured.
     *
     * If the appropriate getResult() doesn't return any value,
     * the metric is forced to calculate the result. Then the
     * appropriate getResult() method is called again.
     *
     * @param o DAO that specifies the desired result type.
     *      The type of o is used to dispatch to the correct
     *      specialized getResult() method of the sub-interfaces.
     * @return result (measurement) performed by this metric
     *      on the project data specified by o.
     * @throws MetricMismatchException if the DAO is of a type
     *      not supported by this metric.
     * @throws AlreadyProcessingException 
     */
    public Result getResult(DAObject o, List<Metric> l) 
    throws MetricMismatchException, AlreadyProcessingException, Exception {
        Result r = getResultIfAlreadyCalculated(o, l);

        // the result hasn't been calculated yet. Do so.
        if (r == null || r.getRowCount() == 0) {
           /*
             * To ensure that no two instances of the metric operate on the same
             * DAO lock on the DAO. Working on the same DAO can happen often
             * when a plugin starts the calculation of another metric as a
             * result of a plugin dependency association. This lock has the side
             * effect that no two Plugins can be invoked with the same DAO as an
             * argument even if the plug-ins do not depend on each other.
             */
            synchronized (lockObject(o)) {
                try {
                    run(o);
                    
                    r = getResultIfAlreadyCalculated(o, l);
                    if (r == null || r.getRowCount() == 0) {
                        log.debug("Metric " + getClass() + " didn't return"
                                + "a result even after running it. DAO: "
                                + o.getId());
                    }
                } finally {
                    unlockObject(o);
                }
            }
        }

        return r;
    }

    private Map<Long,Pair<Object,Integer>> locks = new HashMap<Long,Pair<Object,Integer>>();
    
    private Object lockObject(DAObject o) throws AlreadyProcessingException {
    	synchronized (locks) {
            if (!locks.containsKey(o.getId())) {
                locks.put(o.getId(), 
                        new Pair<Object, Integer>(new Object(),0));
            }
            Pair<Object, Integer> p = locks.get(o.getId());
            if (p.second + 1 > 1) {
                /*
                 * Break and reschedule the calculation of each call to the
                 * getResult method if it originates from another thread than
                 * the thread that has currently locked the DAO object. 
                 * This is required for the DB transaction in the stopped
                 * job to see the results of the calculation of the original
                 * job.
                 */ 
                log.debug("DAO Id:" + o.getId() + 
                        " Already locked - failing job");
                try {
                    throw new AlreadyProcessingException();
                } finally {
                    MetricActivator ma = AlitheiaCore.getInstance().getMetricActivator();
                    HashSet<Long> s = new HashSet<Long>();
                    s.add(o.getId());
                    ma.runMetrics(s, o.getClass());
                }
            }
            p.second = p.second + 1;
            return p.first;
        }
    }
    
    private void unlockObject(DAObject o) {
    	synchronized(locks) {
    		Pair<Object,Integer> p = locks.get(o.getId());
    		p.second = p.second - 1;
    		if (p.second == 0) {
    			locks.remove(o.getId());
    		} else {
    		log.debug("Unlocking DAO Id:" + o.getId());
    		}
    	}
    }
    
    /**
     * Call the appropriate run() method according to the type of the entity
     * that is measured.
     *
     * @param o
     *                DAO which determines which sub-interface run method is
     *                called and also determines what is to be measured by that
     *                sub-interface.
     * @throws MetricMismatchException
     *                 if the DAO is of a type not supported by this metric.
     */
    public void run(DAObject o) throws MetricMismatchException, 
        AlreadyProcessingException, Exception {

        if (!checkDependencies()) {
            log.error("Plug-in dependency check failed");
            return;
        }
        
        boolean found = false;
        Iterator<Class<? extends DAObject>> i = getActivationTypes().iterator();

        while(i.hasNext()) {
            Class<? extends DAObject> c = i.next();
            if (c.isInstance(o)) {
                found = true;
                try {
                    Method m = this.getClass().getMethod("run", c);
                    m.invoke(this, o);
                } catch (SecurityException e) {
                    logErr("run", o, e);
                } catch (NoSuchMethodException e) {
                    logErr("run", o, e);
                } catch (IllegalArgumentException e) {
                    logErr("run", o, e);
                } catch (IllegalAccessException e) {
                    logErr("run", o, e);
                } catch (InvocationTargetException e) {
                    //Forward exception to metric job exception handler
                    if (e.getCause() instanceof AlreadyProcessingException) { 
                        throw (AlreadyProcessingException) e.getCause();
                    }
                    else {
                        if (e != null && e.getCause() != null) {
                            logErr("run", o, e);
                            throw new Exception(e.getCause());
                        }
                    }
                } 
            }
        }
        if(!found) {
            throw new MetricMismatchException(o);
        }
    }
    
    private void logErr(String method, DAObject o, Exception e) {
        log.error("Plugin:" + this.getClass().toString() + 
                "\nDAO id:" + o.getId() + 
                "\nDAO class:" + o.getClass() +
                "\nUnable to invoke " + method + " method." +
                "\nException:" + e.getClass().getName() +
                "\nError:" + e.getMessage() + 
                "\nReason:" + e.getCause().getMessage(), e);
    }

    /**
     * Add a supported metric description to the database. The mnemonic of
     * the metric must be unique -- this is enforced by the code. It is 
     * therefore a good idea to namespace your metric mnemonics in some way.
     *
     * @param desc String description of the metric
     * @param mnemonic Mnemonic string name of the metric
     * @param type The metric type of the supported metric
     * @return True if the operation succeeds, false otherwise (e.g. duplicate
     *         mnemonic or bad metric type)
     */
    protected final boolean addSupportedMetrics(String desc, String mnemonic,
            MetricType.Type type) {
        /* NOTE: In its current status the DB doesn't provide predefined
         *       metric type records. Therefore the following block is
         *       used to create them explicitly when required.
         */
        if (MetricType.getMetricType(type) == null) {
            MetricType newType = new MetricType(type);
            db.addRecord(newType);
        }
        Plugin p = Plugin.getPluginByHashcode(getUniqueKey());
        Metric m = new Metric();
        m.setDescription(desc);
        m.setMnemonic(mnemonic);
        m.setMetricType(MetricType.getMetricType(type));
        m.setPlugin(p);
                
        return p.getSupportedMetrics().add(m);
    }

    /**
     * Get the description objects for all metrics supported by this plug-in
     * as found in the database.
     *
     * @return the list of metric descriptors, or null if none
     */
    public List<Metric> getAllSupportedMetrics() {
        List<Metric> supportedMetrics = new ArrayList<Metric>();
        supportedMetrics.addAll( Plugin.getPluginByHashcode(getUniqueKey()).getSupportedMetrics() );

        if (supportedMetrics.isEmpty()) {
            return null;
        } else {
            return supportedMetrics;
        }
    }
    
    /** {@inheritDoc} */
    public List<Metric> getSupportedMetrics(Class<? extends DAObject> activator) {
        List<Metric> m = new ArrayList<Metric>();

        //Query the database just once
        List<Metric> all = getAllSupportedMetrics();
        
        if (all == null || all.isEmpty())
            return m;
        
        for (Metric metric : all) {
            if (getMetricActivationType(metric).equals(activator)) {
                m.add(metric);
            }
        }
        
        return m;
    }
   
    /**
     * Register the metric to the DB. Subclasses can run their custom
     * initialization routines (i.e. registering DAOs or tables) after calling
     * super().install()
     */
    public boolean install() {
        //1. check if dependencies are satisfied
        if (!checkDependencies()) {
            log.error("Plug-in installation failed");
            return false;
        }
        
        HashMap<String, Object> h = new HashMap<String, Object>();
        h.put("name", this.getName());

        List<Plugin> plugins = db.findObjectsByProperties(Plugin.class, h);

        if (!plugins.isEmpty()) {
            log.warn("A plugin with name <" + getName()
                    + "> is already installed, won't re-install.");
            return false;
        }

        /*Check if the metric has unsatisfied dependencies*/
        for(String dep : dependencies) {
            if (pa.getImplementingPlugin(dep) == null) {
                log.error("No plug-in installed that implements the " + dep
                        + " metric");
                return false;
            }
        }

        //2. Add the plug-in
        Plugin p = new Plugin();
        p.setName(getName());
        p.setInstalldate(new Date(System.currentTimeMillis()));
        p.setVersion(getVersion());
        p.setActive(true);
        p.setHashcode(getUniqueKey());
        boolean result =  db.addRecord(p);
        
        //3. Add the metrics
        for (String mnem :metrics.keySet()) {
        	//addSupportedMetrics(desc, mnemonic, type)
        }
        
        return result;
    }

    /**
     * Remove a plug-in's record from the DB. The DB's referential integrity
     * mechanisms are expected to automatically remove associated records.
     * Subclasses should also clean up any custom tables created.
     */
    public boolean remove() {
        Plugin p = Plugin.getPluginByHashcode(getUniqueKey());
        return db.deleteRecord(p);
    }
    
    /**
     * Default (empty) implementation of the clean up method. What to 
     * do with the provided DAO is left to sub-classes to decide.
     * {@inheritDoc}
     */
    public boolean cleanup(DAObject sp) {
        log.warn("Empty cleanup method for plug-in " 
                + this.getClass().getName());
        return true; 
    }

    /**{@inheritDoc}}*/
    public boolean update() {
        ServiceReference serviceRef = null;
        serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());

        MetricActivator ma =
            ((AlitheiaCore)bc.getService(serviceRef)).getMetricActivator();

        if (ma == null) {
            return false;
        }

        ma.syncMetrics(this);

        return true;
    }

    /**{@inheritDoc}*/
    public final Set<Class<? extends DAObject>> getActivationTypes() {
        Set<Class<? extends DAObject>> activators = new HashSet<Class<? extends DAObject>>();
        
        for(Metric m : metrics.values()) {
            activators.add(m.getMetricType().toActivator());
        }
        
        return activators;
    }

    /**
     * Return an MD5 hex key uniquely identifying the plug-in
     */
    public final String getUniqueKey() {
        return DigestUtils.md5Hex(this.getClass().getCanonicalName());
    }

    /** {@inheritDoc} */
    public final Set<PluginConfiguration> getConfigurationSchema() {
        // Retrieve the plug-in's info object
        PluginInfo pi = pa.getPluginInfo(getUniqueKey());
        if (pi == null) {
            // The plug-in's info object is always null during bundle startup,
            // but if it is not available when the bundle is active, something
            // is possibly wrong.
            if (bc.getBundle().getState() == Bundle.ACTIVE) {
                log.warn("Plugin <" + getName() + "> is loaded but not installed.");
            }
            return Collections.emptySet();
        }
        return pi.getConfiguration();
    }

    /**
     * Add an entry to this plug-in's configuration schema.
     *
     * @param name The name of the configuration property
     * @param defValue The default value for the configuration property
     * @param msg The description of the configuration property
     * @param type The type of the configuration property
     */
    protected final void addConfigEntry(String name, String defValue,
            String msg, PluginInfo.ConfigurationType type) {
        // Retrieve the plug-in's info object
        PluginInfo pi = pa.getPluginInfo(getUniqueKey());
        // Will happen if called during bundle's startup
        if (pi == null) {
            log.warn("Adding configuration key <" + name +
                "> to plugin <" + getName() + "> failed: " +
                "no PluginInfo.");
            return;
        }
        // Modify the plug-in's configuration
        try {
            // Update property
            if (pi.hasConfProp(name, type.toString())) {
                if (pi.updateConfigEntry(db, name, defValue)) {
                    // Update the Plug-in Admin's information
                    pa.pluginUpdated(pa.getPlugin(pi));
                }
                else {
                    log.error("Property (" + name +") update has failed!");
                }
            }
            // Create property
            else {
                if (pi.addConfigEntry(
                        db, name, msg, type.toString(), defValue)) {
                    // Update the Plug-in Admin's information
                    pa.pluginUpdated(pa.getPlugin(pi));
                }
                else {
                    log.error("Property (" + name +") append has failed!");
                }
            }
        }
        catch (Exception ex){
            log.error("Can not modify property (" + name +") for plugin ("
                    + getName(), ex);
        }
    }

    /**
     * Remove an entry from the plug-in's configuration schema
     *
     * @param name The name of the configuration property to remove
     * @param name The type of the configuration property to remove
     */
    protected final void removeConfigEntry(
            String name,
            PluginInfo.ConfigurationType type) {
        // Retrieve the plug-in's info object
        PluginInfo pi = pa.getPluginInfo(getUniqueKey());
        // Will happen if called during bundle's startup
        if (pi == null) {
            log.warn("Removing configuration key <" + name +
                "> from plugin <" + getName() + "> failed: " +
                "no PluginInfo.");
            return;
        }
        // Modify the plug-in's configuration
        try {
            if (pi.hasConfProp(name, type.toString())) {
                if (pi.removeConfigEntry(db, name, type.toString())) {
                    // Update the Plug-in Admin's information
                    pa.pluginUpdated(pa.getPlugin(pi));
                }
                else {
                    log.error("Property (" + name +") remove has failed!");
                }
            }
            else {
                log.error("Property (" + name +") does not exist!");
            }
        }
        catch (Exception ex){
            log.error("Can not remove property (" + name +") from plugin ("
                    + getName() + ")", ex);
        }
    }
    
    /**
     * Get a configuration option for this metric from the plugin configuration
     * store
     * 
     * @param config The configuration option to retrieve
     * @return The configuration entry corresponding the provided description or
     * null if not found in the plug-in's configuration schema
     */
    public PluginConfiguration getConfigurationOption(String config) {
        Set<PluginConfiguration> conf = 
            pa.getPluginInfo(getUniqueKey()).getConfiguration();
        
        Iterator<PluginConfiguration> i = conf.iterator();
        
        while (i.hasNext()) {
            PluginConfiguration pc = i.next();
            if (pc.getName().equals(config)) {
                return pc;
            }
        }
        
        /* Config option not found */
        return null;
    }
    
    /**
     * Convenience method to get the measurement for a single metric for a 
     * StoredProject.
     */
    protected List<ResultEntry> getResult(StoredProject s, Metric m, String mime) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("storedProject", s);
        props.put("metric", m);
        List<StoredProject> mmsgs = dbs.findObjectsByProperties(StoredProject.class, props);
        
        if (mmsgs.isEmpty())
            return null;
        
        ArrayList<ResultEntry> result = new ArrayList<ResultEntry>();
        result.add(new ResultEntry(mmsgs.get(0), mime, m.getMnemonic()));
        return result;
    }
    
    /**
     * Convenience method to get the measurement for a single metric for a 
     * MailMessage.
     */
    protected List<ResultEntry> getResult(MailMessage mm, Metric m, String mime) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("mail", mm);
        props.put("metric", m);
        List<MailMessageMeasurement> mmsgs = dbs.findObjectsByProperties(MailMessageMeasurement.class, props);
        
        if (mmsgs.isEmpty())
            return null;
        
        ArrayList<ResultEntry> result = new ArrayList<ResultEntry>();
        result.add(new ResultEntry(mmsgs.get(0), mime, m.getMnemonic()));
        return result;
    }
    
    /**
     * Convenience method to get the measurement for a single metric for a 
     * MailingListThread.
     */
    protected List<ResultEntry> getResult(MailingListThread mt, Metric m, String mime) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("thread", mt);
        props.put("metric", m);
        List<MailingListThreadMeasurement> mmsgs = dbs.findObjectsByProperties(MailingListThreadMeasurement.class, props);
        
        if (mmsgs.isEmpty())
            return null;
        
        ArrayList<ResultEntry> result = new ArrayList<ResultEntry>();
        result.add(new ResultEntry(Integer.parseInt(mmsgs.get(0).getResult()), mime, m.getMnemonic()));
        return result;
    }
    
    /**
     * Convenience method to get the measurement for a single metric for a 
     * ProjectVersion.
     */
    protected List<ResultEntry> getResult(ProjectVersion pv, Metric m, String mime) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("projectVersion", pv);
        props.put("metric", m);
        List<ProjectVersionMeasurement> pvms = dbs.findObjectsByProperties(ProjectVersionMeasurement.class, props);
        
        if (pvms.isEmpty())
            return null;
        
        ArrayList<ResultEntry> result = new ArrayList<ResultEntry>();
        result.add(new ResultEntry(Integer.parseInt(pvms.get(0).getResult()), mime, m.getMnemonic()));
        return result;
    }
    
    /**
     * Convenience method to get the measurement for a single metric for a 
     * ProjectFile.
     */
    protected List<ResultEntry> getResult(ProjectFile pf, Metric m, String mime) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("projectFile", pf);
        props.put("metric", m);
        List<ProjectFileMeasurement> pfms = dbs.findObjectsByProperties(ProjectFileMeasurement.class, props);
        
        if (pfms.isEmpty())
            return null;
        
        ArrayList<ResultEntry> result = new ArrayList<ResultEntry>();
        result.add(new ResultEntry(Integer.parseInt(pfms.get(0).getResult()), mime, m.getMnemonic()));
        return result;
    }
    
    /**{@inheritDoc}*/
    public final Class<? extends DAObject> getMetricActivationType(Metric m) {
        return metrics.get(m.getMnemonic()).getMetricType().toActivator();
    }
    
    /**
     * Check if the plug-in dependencies are satisfied
     */
    private boolean checkDependencies() {
        for (String mnemonic : dependencies) {
            if (pa.getImplementingPlugin(mnemonic) == null) {
                log.error("No plug-in implements metric "  + mnemonic + 
                        " which is required by " + getName());
                return false;
            }
        }
        return true;
    }
    
    /** {@inheritDoc} */
    public Set<String> getDependencies() {
        return dependencies;
    }
}
