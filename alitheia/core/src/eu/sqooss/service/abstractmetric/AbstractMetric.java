/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Georgios Gousios <gousiosg@gmail.com>
 *
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.EvaluationMark;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;

/**
 * A base class for all metrics. Implements basic functionality such as
 * logging setup and plug-in information retrieval from the OSGi bundle
 * manifest file. Metrics can choose to directly implement
 * the {@link eu.sqooss.abstractmetric.AlitheiaPlugin} interface instead of extending
 * this class.
 */
public abstract class AbstractMetric implements AlitheiaPlugin {

    /** Reference to the metric bundle context */
    protected BundleContext bc;

    /** Logger for administrative operations */
    protected Logger log = null;

    /** Reference to the DB service, not to be passed to metric jobs */
    protected DBService db;

    /** Reference to the DB service, not to be passed to metric jobs */
    protected PluginAdmin pa;

    /** Metric dependencies */
    protected List<String> metricDependencies = new ArrayList<String>();

    /**Types used to activate this metric*/
    protected List<Class<? extends DAObject>> activationTypes = new ArrayList<Class<? extends DAObject>>();

    /** Cache the result of the mark evaluation function*/
    protected HashMap<Long, Long> evaluationMarked = new HashMap<Long, Long>();

    /**
     * Init basic services common to all implementing classes
     * @param bc - The bundle context of the implementing metric - to be passed
     * by the activator.
     */
    protected AbstractMetric(BundleContext bc) {

        this.bc = bc;
        ServiceReference serviceRef = null;
        serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());

        log = ((AlitheiaCore) bc.getService(serviceRef)).
                getLogManager().createLogger(Logger.NAME_SQOOSS_METRIC);

        if (log == null) {
            System.out.println("ERROR: Got no logger");
        }

        db = ((AlitheiaCore) bc.getService(serviceRef)).getDBService();

        if(db == null)
            log.error("Could not get a reference to the DB service");

        pa = ((AlitheiaCore) bc.getService(serviceRef)).getPluginAdmin();

        if(pa == null)
            log.error("Could not get a reference to the Plugin Administation "
                    + "service");
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

    /**
     * Call the appropriate getResult() method according to
     * the type of the entity that is measured.
     *
     * @param o DAO that specifies the desired result type.
     *      The type of o is used to dispatch to the correct
     *      specialised getResult() method of the sub-interfaces.
     * @return result (measurement) performed by this metric
     *      on the project data specified by o.
     * @throws MetricMismatchException if the DAO is of a type
     *      not supported by this metric.
     */
    @SuppressWarnings("unchecked")
    public Result getResult(DAObject o, List<Metric> l) throws MetricMismatchException {

        boolean found = false;
        Result r = new Result();

        Iterator<Class<? extends DAObject>> i = getActivationTypes().iterator();

        List<Metric> metrics = getSupportedMetrics();

        for (Metric m : l) {
            if (!metrics.contains(m)) {
                throw new MetricMismatchException("Metric " + m.getMnemonic()
                        + " not defined by plugin "
                        + Plugin.getPluginByHashcode(getUniqueKey()).getName());
            }

            ArrayList<ResultEntry> re = null;
            while (i.hasNext()) {
                Class<? extends DAObject> c = i.next();
                if (c.isInstance(o)) {
                    found = true;
                    try {
                        Method method = this.getClass().getMethod("getResult", c, Metric.class);
                        re =  (ArrayList<ResultEntry>) method.invoke(this, o, m);
                    } catch (SecurityException e) {
                        log.error("Unable to invoke getResult method:"
                                + e.getMessage());
                    } catch (NoSuchMethodException e) {
                        log.error("No method getResult(" + c.getName()
                                + ") for type " + this.getClass().getName());
                    } catch (IllegalArgumentException e) {
                        log.error("Unable to invoke getResult method:"
                                + e.getMessage());
                    } catch (IllegalAccessException e) {
                        log.error("Unable to invoke getResult method:"
                                + e.getMessage());
                    } catch (InvocationTargetException e) {
                        log.error("Unable to invoke getResult method:"
                                + e.getMessage());
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
     * Call the appropriate run() method according to the type of the entity
     * that is measured.
     *
     * @param o
     *                DAO which determines which sub-interface run method is
     *                called and also determines what is to be measured by that
     *                sub-interface.
     * @throws MetricMismatchException
     *                 if the DAO is of a type not supported by this metric.
     *
     * FIXME:
     */
    public void run(DAObject o) throws MetricMismatchException {

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
                    log.error("Unable to invoke run method:" + e.getMessage());
                } catch (NoSuchMethodException e) {
                    log.error("No method run(" + c.getName() +") for type "
                            + this.getClass().getName());
                } catch (IllegalArgumentException e) {
                    log.error("Unable to invoke run method:" + e.getMessage());
                } catch (IllegalAccessException e) {
                    log.error("Unable to invoke run method:" + e.getMessage());
                } catch (InvocationTargetException e) {
                    log.error("Unable to invoke run method:" + e.getMessage());
                }
            }
        }
        if(!found)
            throw new MetricMismatchException(o);
    }

    /**
     * Add a supported metric description to the database.
     *
     * @param desc String description of the metric
     * @param type The metric type of the supported metric
     * @return True if the operation succeeds, false otherwise (i.e. duplicates etc)
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
        Metric m = new Metric();
        m.setDescription(desc);
        m.setMnemonic(mnemonic);
        m.setMetricType(MetricType.getMetricType(type));
        m.setPlugin(Plugin.getPluginByHashcode(getUniqueKey()));
        return db.addRecord(m);

    }

    /**
     * Get the description objects for all metrics supported by this plug-in
     * as found in the database.
     *
     * @return the list of metric descriptors, or null if none
     */
    public List<Metric> getSupportedMetrics() {
        List<Metric> metrics = Plugin.getSupportedMetrics(Plugin.getPluginByHashcode(getUniqueKey()));
        if (metrics.isEmpty()) {
            return null;
        } else {
            return metrics;
        }
    }

    /**
     * Creates a record in the database, when the specified metric has been
     * evaluated for a first time in the scope of the selected project.
     *
     * @param me Evaluated metric
     * @param sp Evaluated project
     */
    public synchronized void markEvaluation (Metric me, StoredProject sp) {
        if((evaluationMarked.containsKey(sp.getId()) == false) ||
                (evaluationMarked.get(sp.getId()) != me.getId())) {
            // Store the evaluation mark locally
            evaluationMarked.put(sp.getId(), me.getId());

            // Search for a previous evaluation of this metric on this project
            HashMap<String, Object> filter = new HashMap<String, Object>();
            filter.put("metric", me);
            filter.put("storedProject", sp);
            List<EvaluationMark> wasEvaluated = db.findObjectsByProperties(
                    EvaluationMark.class, filter);

            // If this is a first time evaluation, then remember this in the DB
            if (wasEvaluated.isEmpty()) {
                EvaluationMark evaluationMark = new EvaluationMark();
                evaluationMark.setMetric(me);
                evaluationMark.setStoredProject(sp);

                db.addRecord(evaluationMark);
            }
        }
    }

    /**
     * Register the metric to the DB. Subclasses can run their custom
     * initialization routines (i.e. registering DAOs or tables) after calling
     * super()
     */
    public boolean install() {
        HashMap<String, Object> h = new HashMap<String, Object>();
        h.put("name", this.getName());

        List<Plugin> plugins = db.findObjectsByProperties(Plugin.class, h);

        if (!plugins.isEmpty()) {
            log.warn("A plugin with name <" + getName()
                    + "> is already installed, won't re-install.");
            return false;
        }

        /*Check if the metric has unsatisfied dependencies*/
        for(String dep : metricDependencies) {
            if (pa.getImplementingPlugin(dep) == null) {
                log.error("No plug-in installed that implements the " + dep
                        + " metric");
                return false;
            }
        }

        Plugin p = new Plugin();
        p.setName(getName());
        p.setInstalldate(new Date(System.currentTimeMillis()));
        p.setVersion(getVersion());
        p.setActive(true);
        p.setHashcode(getUniqueKey());

        return db.addRecord(p);

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

    public boolean update() {
        HashMap<String, Object> h = new HashMap<String, Object>();
        List<StoredProject> l = db.findObjectsByProperties(StoredProject.class, h);

        for(StoredProject sp : l) {
            Scheduler s = ((AlitheiaCore) bc.getService(this.bc
                    .getServiceReference(AlitheiaCore.class.getName())))
                    .getScheduler();
            try {
                s.enqueue(new DefaultUpdateJob(this, sp));
            } catch (SchedulerException e) {
                log.error("Cannot schedule update job");
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public final List<Class<? extends DAObject>> getActivationTypes() {
        return activationTypes;
    }

    protected final void addActivationType(Class<? extends DAObject> c) {
        activationTypes.add(c);
        // Call the Plug-in Admin only on started metric bundles
        if (bc.getBundle().getState() == Bundle.ACTIVE) {
            pa.pluginUpdated(this);
        }
    }

    /**
     * Return an MD5 hex key.
     */
    public final String getUniqueKey() {
        return DigestUtils.md5Hex(this.getClass().getCanonicalName());
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.abstractmetric.AlitheiaPlugin#getConfigurationSchema()
     */
    public final List<PluginConfiguration> getConfigurationSchema() {
        // Retrieve the plug-in's info object
        PluginInfo pi = pa.getPluginInfo(getUniqueKey());
        if (pi == null) {
            // The plug-in's info object is always null during bundle startup,
            // but if it is not available when the bundle is active, something
            // is possibly wrong.
            if (bc.getBundle().getState() == Bundle.ACTIVE) {
                log.warn("No info object for plugin <" + getName() + ">");
            }
            return new ArrayList<PluginConfiguration>();
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

    public final List<String> getMetricDependencies() {
        return this.metricDependencies;
    }

    /**
     * Add a metric mnemonic to the list of metric dependencies.
     * @param mnemonic The mnemonic to be added
     */
    protected final void addMetricDepedency(String mnemonic) {
        if(!metricDependencies.contains(mnemonic)) {
            metricDependencies.add(mnemonic);
        }
    }
}
