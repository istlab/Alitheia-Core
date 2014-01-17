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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.EncapsulationUnitMeasurement;
import eu.sqooss.service.db.ExecutionUnitMeasurement;
import eu.sqooss.service.db.MailMessageMeasurement;
import eu.sqooss.service.db.MailingListThreadMeasurement;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricMeasurement;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.NameSpaceMeasurement;
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
import eu.sqooss.service.scheduler.Job;

/**
 * A base class for all metrics. Implements basic functionality such as
 * logging setup and plug-in information retrieval from the OSGi bundle
 * manifest file. Metrics can choose to directly implement
 * the {@link eu.sqooss.abstractmetric.AlitheiaPlugin} interface instead of 
 * extending this class.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMetric implements AlitheiaPlugin {

    /** Reference to the metric bundle context */
    protected BundleContext bc;

    /** Logger for administrative operations */
    protected Logger log;

    /** Reference to the DB service, not to be passed to metric jobs */
    protected DBService db;

    /** Reference to the plugin administrator service, not to be passed to metric jobs */
    protected PluginAdmin pa;

    /** Reference to the collection of actual Metric objects */
    protected MetricCollection metrics;
    
    /** Reference to the collection of PluginConfigurations */
    protected MetricConfiguration config;
    
    /** A manager for locks of actions */
    protected LockManager locks;
    
    /** A referenced to the actual Metric's class */
    protected Class<? extends AbstractMetric> implementor;
    
    /** The scheduler job that executes this metric */
    protected ThreadLocal<Job> job = new ThreadLocal<Job>();
    
    /**
     * Init basic services common to all implementing classes
     * @param bc - The bundle context of the implementing metric - to be passed
     * by the activator.
     */
    protected AbstractMetric(BundleContext bc) {
    	this.bc = bc;
    	
    	implementor = getClass();
        
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
        
        metrics = new MetricCollection(this,log);
    	metrics.discoverMetrics(implementor.getAnnotation(MetricDeclarations.class));
    	
    	config = new MetricConfiguration(this,log,pa);
    	
    	locks = new LockManager(this,log);
    }
   

    /**
     * Retrieve author information from the plug-in bundle
     */
    public String getAuthor() {
        return (String) bc.getBundle().getHeaders().get(
                Constants.BUNDLE_CONTACTADDRESS);
    }

    /**
     * Retrieve the plug-in description from the plug-in bundle
     */
    public String getDescription() {
        return (String) bc.getBundle().getHeaders().get(
                Constants.BUNDLE_DESCRIPTION);
    }

    /**
     * Retrieve the plug-in name as specified in the metric bundle
     */
    public String getName() {
        return (String) bc.getBundle().getHeaders().get(
                Constants.BUNDLE_NAME);
    }

    /**
     * Retrieve the plug-in version as specified in the metric bundle
     */
    public String getVersion() {
        return (String) bc.getBundle().getHeaders().get(
                Constants.BUNDLE_VERSION);
    }
    
    /**
     * Retrieve the plug-in's current tate as specified in the metric bundle
     */
    public int getState(){
    	return bc.getBundle().getState();
    }

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
     public List<Result> getResultIfAlreadyCalculated(DAObject o, List<Metric> l) throws MetricMismatchException {      
        List<Result> result = new ArrayList<Result>();
        for (Metric m : l) {
            if (!metrics.contains(m)) {
                throw new MetricMismatchException("Metric " + m.getMnemonic()
                        + " not defined by " + getName());
            }
            List<Result> re = null;
            try {
                Method method = findGetResultMethod(o.getClass());
                re = (List<Result>) method.invoke(this, o, m);
            } catch (NoSuchMethodException e) {
                log.error("No method getResult(" + m.getMetricType().toActivator() + ") for type "
                        + implementor.getName());
            } catch (Exception e) {
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
	         m = implementor.getMethod("getResult", clazz, Metric.class);                
	     } catch (NoSuchMethodException nsme) {
	         try {
	             m = implementor.getMethod("getResult", clazz.getSuperclass(), Metric.class);
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
        if (r == null || r.isEmpty() ) {
        	// the result hasn't been calculated yet. Do so.
            r = getResultIfNotCalculated(o, l);
        }
        return r;
    }
    
    private List<Result> getResultIfNotCalculated(DAObject o, List<Metric> l)
    throws MetricMismatchException, AlreadyProcessingException, Exception {
    	List<Result> r = null;
        /*
         * To ensure that no two instances of the metric operate on the same
         * DAO lock on the DAO. Working on the same DAO can happen often
         * when a plugin starts the calculation of another metric as a
         * result of a plugin dependency association. This lock has the side
         * effect that no two Plugins can be invoked with the same DAO as an
         * argument even if the plug-ins do not depend on each other.
         */
    	synchronized (locks.lockObject(o)) {
            try {
                run(o);
                r = getResultIfAlreadyCalculated(o, l);
                if ( (r == null || r.isEmpty()) &&
                     (job.get() == null || job.get().state() != Job.State.Yielded) ) {
                        log.debug("Metric " + o.getClass() + " didn't return"
                            + "a result even after running it. DAO: "
                            + o.getId());
                }
            } finally {
                locks.unlockObject(o);
            }
        }
    	return r;
    }
    
    public boolean checkDependencies() {
    	return metrics.checkDependencies(pa);
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
        } catch (InvocationTargetException e) {
        	e.printStackTrace();
            // Forward exception to metric job exception handler
            if (e.getCause() instanceof AlreadyProcessingException) {
                throw (AlreadyProcessingException) e.getCause();
            } else {
                if (e != null) {
                    logErr("run", o, e);
                    if (e.getCause() != null)
                        throw new Exception(e.getCause());
                    else
                        throw new Exception(e);
                }
            }
        } catch( Exception e ) {
        	logErr("run", o, e);
        }
    }
    
    private Method findRunMethod(String name, Class<?> clazz) 
        throws NoSuchMethodException {
        Method m = null;
        
        try {
            m = implementor.getMethod(name, clazz);                
        } catch (NoSuchMethodException nsme) {
            try {
                m = implementor.getMethod(name, clazz.getSuperclass());
            } catch (NoSuchMethodException nsme1) {
                throw nsme;
            }
        }
       
        return m;
    }
    
    private void logErr(String method, DAObject o, Exception e) {
        log.error("Plugin:" + implementor.toString() + 
                "\nDAO id: " + o.getId() + 
                "\nDAO class: " + o.getClass() +
                "\nDAO toString(): " + o.toString() +
                "\nError when invoking the " + method + " method." +
                "\nException:" + e.getClass().getName() +
                "\nError:" + e.getMessage() + 
                "\nReason:" + e.getCause(), e);
    }

    /** {@inheritDoc} */
    public final List<Metric> getAllSupportedMetrics() {
        String qry = "from Metric m where m.plugin=:plugin";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("plugin", Plugin.getPluginByHashcode(getUniqueKey()));
        return (List<Metric>)db.doHQL(qry, params);
    }
    
    /** {@inheritDoc} */
    public final List<Metric> getSupportedMetrics(Class<? extends DAObject> activator) {
        return metrics.getSupportedMetrics(activator);
    }
    
    /**
     * Default (empty) implementation of the clean up method. What to 
     * do with the provided DAO is left to sub-classes to decide.
     * {@inheritDoc}
     */
    public boolean cleanup(DAObject sp) {
        log.warn("Empty cleanup method for plug-in " + implementor.getName());
        return true; 
    }

    /**{@inheritDoc}}*/
    public boolean update() {
        ServiceReference serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        MetricActivator ma = ((AlitheiaCore)bc.getService(serviceRef)).getMetricActivator();
        if (ma == null) {
            return false;
        } else {
        	ma.syncMetrics(this);
        	return true;
        }
    }

    /**{@inheritDoc}*/
    public final Set<Class<? extends DAObject>> getActivationTypes() {    
        return metrics.getActivationTypes();
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
    	String name = implementor.getCanonicalName();
		byte[] data = name.getBytes(); 
		m.update(data,0,data.length);
		BigInteger i = new BigInteger(1,m.digest());
		return String.format("%1$032X", i);
    }

    /** {@inheritDoc} */
    public final Set<PluginConfiguration> getConfigurationSchema() {
        return config.getConfigurationSchema();
    }
    
    /**
     * Get a configuration option for this metric from the plugin configuration store
     * 
     * @param option The configuration option to retrieve
     * @return The configuration entry corresponding the provided description or
     * null if not found in the plug-in's configuration schema
     */
    public PluginConfiguration getConfigurationOption(String option) {
    	return config.getConfigurationOption(option);
    }
    
    private static final Map<Class<? extends MetricMeasurement>, String> resultFieldNames = 
        new HashMap<Class<? extends MetricMeasurement>, String>();
    
    static {
        resultFieldNames.put(StoredProjectMeasurement.class, "storedProject");
        resultFieldNames.put(ProjectVersionMeasurement.class, "projectVersion");
        resultFieldNames.put(ProjectFileMeasurement.class, "projectFile");
        resultFieldNames.put(MailMessageMeasurement.class, "mail");
        resultFieldNames.put(MailingListThreadMeasurement.class, "thread");
        resultFieldNames.put(ExecutionUnitMeasurement.class, "executionUnit");
        resultFieldNames.put(EncapsulationUnitMeasurement.class, "encapsulationUnit");
        resultFieldNames.put(NameSpaceMeasurement.class, "namespace");
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
        return metrics.getActivationTypes(m);
    }
    
    /** {@inheritDoc} */
    public final Set<String> getDependencies() {
        return metrics.getDependencies();
    }
    
    public final Collection<Metric> getMetrics() {
    	return metrics.getMetrics();
    }
    

    @Override
    public Map<MetricType.Type, SortedSet<Long>> getObjectIdsToSync(StoredProject sp, Metric m) 
    throws MetricActivationException {
    	Map<MetricType.Type, SortedSet<Long>> IDs = new HashMap<Type, SortedSet<Long>>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("project", sp);
        params.put("metric", m.getId());

    	for (Class<? extends DAObject> at : getMetricActivationTypes(m)) {
	    	String q = MetricQueries.getQuery(at);
			List<Long> objectIds = (List<Long>) db.doHQL(q, params);
	    	TreeSet<Long> ids = new TreeSet<Long>();
	    	ids.addAll(objectIds);
	    	IDs.put(MetricType.fromActivator(at), ids);
    	}
    	
    	return IDs;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setJob(Job j) {
        this.job.set(j);
    }
 }
