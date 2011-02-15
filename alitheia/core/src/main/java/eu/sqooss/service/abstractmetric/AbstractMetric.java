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
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MailMessageMeasurement;
import eu.sqooss.service.db.MailingListThreadMeasurement;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricMeasurement;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectMeasurement;
import eu.sqooss.service.db.MetricType.Type;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivationException;
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
    
    private Map<Metric, List<Class<? extends DAObject>>> metricActType =
    	new HashMap<Metric, List<Class<? extends DAObject>>>();
    
    protected static final String QRY_SYNC_PV = "select pv.id from ProjectVersion pv " +
    		"where pv.project = :project and not exists(" +
    		"	select pvm.projectVersion from ProjectVersionMeasurement pvm " +
    		"	where pvm.projectVersion.id = pv.id and pvm.metric.id = :metric) " +
    		"order by pv.sequence asc";
    
    protected static final String QRY_SYNC_PF = "select pf.id " +
    		"from ProjectVersion pv, ProjectFile pf " +
    		"where pf.projectVersion=pv and pv.project = :project " +
    		"and not exists (" +
    		"	select pfm.projectFile " +
    		"	from ProjectFileMeasurement pfm " +
    		"	where pfm.projectFile.id = pf.id " +
    		"	and pfm.metric.id = :metric) " +
    		"	and pf.isDirectory = false)  " +
    		"order by pv.sequence asc";
    
    protected static final String QRY_SYNC_PD = "select pf.id " +
		"from ProjectVersion pv, ProjectFile pf " +
		"where pf.projectVersion=pv and pv.project = :project " +
		"and not exists (" +
		"	select pfm.projectFile " +
		"	from ProjectFileMeasurement pfm " +
		"	where pfm.projectFile.id = pf.id " +
		"	and pfm.metric.id = :metric) " +
		"	and pf.isDirectory = true)  " +
		"order by pv.sequence asc";
    
    protected static final String QRY_SYNC_MM = "select mm.id " +
    		"from MailMessage mm " +
    		"where mm.list.storedProject = :project " +
    		"and mm.id not in (" +
    		"	select mmm.mail.id " +
    		"	from MailMessageMeasurement mmm " +
    		"	where mmm.metric.id =:metric and mmm.mail.id = mm.id))";
    
    protected static final String QRY_SYNC_MT = "select mlt.id " +
    		"from MailingListThread mlt " +
    		"where mlt.list.storedProject = :project " +
    		"and mlt.id not in (" +
    		"	select mltm.thread.id " +
    		"	from MailingListThreadMeasurement mltm " +
    		"	where mltm.metric.id =:metric and mltm.thread.id = mlt.id)";
    
    protected static final String QRY_SYNC_DEV = "select d.id " +
    		"from Developer d " +
    		"where d.storedProject = :project";
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
			
				List<Class<? extends DAObject>> activs = new ArrayList<Class<? extends DAObject>>();				
				for (Class<? extends DAObject> o : metric.activators()) {
					activs.add(o);
				}
				
				metricActType.put(m, activs);
				
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
     public List<Result> getResultIfAlreadyCalculated(DAObject o, List<Metric> l) throws MetricMismatchException {
        boolean found = false;        
        List<Result> result = new ArrayList<Result>();
        
        for (Metric m : l) {
            if (!metrics.containsKey(m.getMnemonic())) {
                throw new MetricMismatchException("Metric " + m.getMnemonic()
                        + " not defined by plugin "
                        + Plugin.getPluginByHashcode(getUniqueKey()).getName());
            }
            List<Result> re = null;
            try {
                Method method = findGetResultMethod(o.getClass());
                re = (List<Result>) method.invoke(this, o, m);
            } catch (SecurityException e) {
                logErr("getResult", o, e);
            } catch (NoSuchMethodException e) {
                log.error("No method getResult(" + m.getMetricType().toActivator() + ") for type "
                        + this.getClass().getName());
            } catch (IllegalArgumentException e) {
                logErr("getResult", o, e);
            } catch (IllegalAccessException e) {
                logErr("getResult", o, e);
            } catch (InvocationTargetException e) {
                logErr("getResult", o, e);
            }
            if (re != null && !re.isEmpty()) {
                result.addAll(re);
            }
        }

        return result;
    }

     private Method findGetResultMethod(Class<?> clazz) 
     throws NoSuchMethodException {
     Method m = null;
     
     try {
         m = this.getClass().getMethod("getResult", clazz, Metric.class);                
     } catch (NoSuchMethodException nsme) {
         try {
             m = this.getClass().getMethod("getResult", clazz.getSuperclass(), Metric.class);
         } catch (NoSuchMethodException nsme1) {
             throw nsme;
         }
     }

     return m;
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
    public List<Result> getResult(DAObject o, List<Metric> l) 
    throws MetricMismatchException, AlreadyProcessingException, Exception {
        List<Result> r = getResultIfAlreadyCalculated(o, l);

        // the result hasn't been calculated yet. Do so.
        if (r == null || r.size() == 0) {
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
                    if (r == null || r.size() == 0) {
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
                    ma.runMetric(o, this);
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

        try {
            Method m = findRunMethod("run", o.getClass());
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
            // Forward exception to metric job exception handler
            if (e.getCause() instanceof AlreadyProcessingException) {
                throw (AlreadyProcessingException) e.getCause();
            } else {
                if (e != null && e.getCause() != null) {
                    logErr("run", o, e);
                    if (e.getCause() != null)
                        throw new Exception(e.getCause());
                    else
                        throw new Exception(e);
                }
            }
        }
    }
    
    private Method findRunMethod(String name, Class<?> clazz) 
        throws NoSuchMethodException {
        Method m = null;
        
        try {
            m = this.getClass().getMethod(name, clazz);                
        } catch (NoSuchMethodException nsme) {
            try {
                m = this.getClass().getMethod(name, clazz.getSuperclass());
            } catch (NoSuchMethodException nsme1) {
                throw nsme;
            }
        }
       
        return m;
    }
    
    private void logErr(String method, DAObject o, Exception e) {
        log.error("Plugin:" + this.getClass().toString() + 
                "\nDAO id: " + o.getId() + 
                "\nDAO class: " + o.getClass() +
                "\nDAO toString(): " + o.toString() +
                "\nError when invoking the " + method + " method." +
                "\nException:" + e.getClass().getName() +
                "\nError:" + e.getMessage() + 
                "\nReason:" + e.getCause(), e);
    }


    /** {@inheritDoc} */
    public List<Metric> getAllSupportedMetrics() {
        List<Metric> supportedMetrics = new ArrayList<Metric>();
        supportedMetrics.addAll(
        		Plugin.getPluginByHashcode(getUniqueKey()).getSupportedMetrics());

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
            if (getMetricActivationTypes(metric).contains(activator)) {
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
        	Metric m = metrics.get(mnem);
        	Type type = Type.fromString(m.getMetricType().getType());
        	MetricType newType = MetricType.getMetricType(type);
        	if (newType == null) {
                newType = new MetricType(type);
                db.addRecord(newType);
                m.setMetricType(newType);
            }
        	
        	m.setMetricType(newType);
        	m.setPlugin(p);
        	db.addRecord(m);
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
        return activators;
    }

    /**
     * Return an MD5 hex key uniquely identifying the plug-in
     */
    public final String getUniqueKey() {
    	MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			log.error("Cannot find a valid implementation of the MD5 " +
					"hash algorithm");
		}
    	String name = this.getClass().getCanonicalName();
		byte[] data = name.getBytes(); 
		m.update(data,0,data.length);
		BigInteger i = new BigInteger(1,m.digest());
		return String.format("%1$032X", i);
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
    
    private static Map<Class<? extends MetricMeasurement>, String> resultFieldNames = 
        new HashMap<Class<? extends MetricMeasurement>, String>();
    
    static {
        resultFieldNames.put(StoredProjectMeasurement.class, "storedProject");
        resultFieldNames.put(ProjectVersionMeasurement.class, "projectVersion");
        resultFieldNames.put(ProjectFileMeasurement.class, "projectFile");
        resultFieldNames.put(MailMessageMeasurement.class, "mail");
        resultFieldNames.put(MailingListThreadMeasurement.class, "thread");
    }
    
    /**
     * Convenience method to get the measurement for a single metric.
     */
    protected List<Result> getResult(DAObject o, Class<? extends MetricMeasurement> clazz, 
            Metric m, Result.ResultType type) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        Map<String, Object> props = new HashMap<String, Object>();
        
        props.put(resultFieldNames.get(clazz), o);
        props.put("metric", m);
        List resultat = dbs.findObjectsByProperties(clazz, props);
        
        if (resultat.isEmpty())
            return Collections.EMPTY_LIST;
        
        ArrayList<Result> result = new ArrayList<Result>();
        result.add(new Result(o, m, ((MetricMeasurement)resultat.get(0)).getResult(), type));
        return result;
        
    }
    
    /**{@inheritDoc}*/
    @Override
    public final List<Class<? extends DAObject>> getMetricActivationTypes (Metric m) {
        return metricActType.get(m);
    }
    
    /**
     * Check if the plug-in dependencies are satisfied
     */
    private boolean checkDependencies() {
        for (String mnemonic : dependencies) {
        	//Check thyself first
        	if (metrics.containsKey(mnemonic))
        		continue;
        	
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

    @Override
    public Map<MetricType.Type, SortedSet<Long>> getObjectIdsToSync(StoredProject sp, Metric m) 
    throws MetricActivationException {

    	Map<MetricType.Type, SortedSet<Long>> IDs = new HashMap<Type, SortedSet<Long>>();
    	
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("project", sp);
        params.put("metric", m.getId());

    	String q = null;
    	
    	for (Class<? extends DAObject> at : getMetricActivationTypes(m)) {
    	
	    	if (MetricType.fromActivator(at) == Type.PROJECT_VERSION) {
	    		q = QRY_SYNC_PV;
	    	} else if (MetricType.fromActivator(at) == Type.SOURCE_FILE) {
	    		q = QRY_SYNC_PF;
	    	} else if (MetricType.fromActivator(at) == Type.SOURCE_DIRECTORY) {
	    		q = QRY_SYNC_PD;
	     	} else if (MetricType.fromActivator(at) == Type.MAILING_LIST) {
	    		throw new MetricActivationException("Metric synchronisation with MAILING_LIST objects not implemented");
	    	} else if (MetricType.fromActivator(at) == Type.MAILMESSAGE) {
	    		q = QRY_SYNC_MM;
	    	} else if (MetricType.fromActivator(at) == Type.MAILTHREAD) {
	    		q = QRY_SYNC_MT;
	    	} else if (MetricType.fromActivator(at) == Type.BUG) {
	    		throw new MetricActivationException("Metric synchronisation with BUG objects not implemented");
	    	} else if (MetricType.fromActivator(at) == Type.DEVELOPER) {
	    		q = QRY_SYNC_DEV;
	    	} else {
	    		throw new MetricActivationException("Metric synchronisation with GENERIC objects not implemented");
	    	}
	    	
	    	List<Long> objectIds = (List<Long>) db.doHQL(q, params);
	    	TreeSet<Long> ids = new TreeSet<Long>();
	    	ids.addAll(objectIds);
	    	IDs.put(MetricType.fromActivator(at), ids);
    	}
    	return IDs;
    }
}
