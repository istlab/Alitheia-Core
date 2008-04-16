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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.lib.result.Result;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.EvaluationMark;
import eu.sqooss.service.db.FileGroup;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.util.Pair;


/**
 * A base class for all metrics. Implements basic functionality such as
 * logging setup and plug-in information retrieval from the OSGi bundle
 * manifest file. Metrics can choose to directly implement
 * the {@link eu.sqooss.abstractmetric.AlitheiaPlugin} interface instead of extending
 * this class.
 */
public abstract class AbstractMetric
implements eu.sqooss.service.abstractmetric.AlitheiaPlugin {

    /** Reference to the metric bundle context */
    protected BundleContext bc;

    /** Log manager for administrative operations */
    protected LogManager logService = null;

    /** Logger for administrative operations */
    protected Logger log = null;

    /** Reference to the DB service, not to be passed to metric jobs */
    protected DBService db;

    /** Cache the metrics list on first access*/
    protected List<Metric> metrics = null;

    /** Cache the result of the mark evaluation function*/
    protected HashMap<Long, Boolean> evaluationMarked = new HashMap<Long, Boolean>();

    /**
     * Init basic services common to all implementing classes
     * @param bc - The bundle context of the implementing metric - to be passed
     * by the activator.
     */
    protected AbstractMetric(BundleContext bc) {

        this.bc = bc;
        ServiceReference serviceRef = null;
        serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());

        logService = ((AlitheiaCore) bc.getService(serviceRef)).getLogManager();

        if (logService != null) {
            log = logService.createLogger(Logger.NAME_SQOOSS_METRIC);

            if (log != null)
                log.info("Got a valid reference to the logger");
        }

        if (log == null) {
            System.out.println("ERROR: Got no logger");
        }

        db = ((AlitheiaCore) bc.getService(serviceRef)).getDBService();

        if(db == null)
            log.error("Could not get a reference to the DB service");
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
        return Plugin.getPlugin(getName()).getInstalldate();
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
    public Result getResult(DAObject o)
        throws MetricMismatchException {
        if ((this instanceof ProjectVersionMetric) &&
            (o instanceof ProjectVersion)) {
            return ((ProjectVersionMetric)this).getResult((ProjectVersion) o);
        }
        if ((this instanceof StoredProjectMetric) &&
            (o instanceof StoredProject)) {
            return ((StoredProjectMetric)this).getResult((StoredProject) o);
        }
        if ((this instanceof ProjectFileMetric) &&
            (o instanceof ProjectFile)) {
            return ((ProjectFileMetric)this).getResult((ProjectFile) o);
        }
        if ((this instanceof FileGroupMetric) &&
            (o instanceof FileGroup)) {
            return ((FileGroupMetric)this).getResult((FileGroup) o);
        }

        throw new MetricMismatchException(o);
    }

    /**
     * Call the appropriate run() method according to
     * the type of the entity that is measured.
     *
     * @param o DAO which determines which sub-interface run
     *          method is called and also determines what
     *          is to be measured by that sub-interface.
     * @throws MetricMismatchException if the DAO is of a type
     *          not supported by this metric.
     *
     * FIXME:
     */
    public void run(DAObject o) throws MetricMismatchException {
        if ((this instanceof ProjectVersionMetric) &&
            (o instanceof ProjectVersion)) {
            ((ProjectVersionMetric)this).run((ProjectVersion) o);
            return;
        }
        if ((this instanceof StoredProjectMetric) &&
            (o instanceof StoredProject)) {
            ((StoredProjectMetric)this).run((StoredProject) o);
            return;
        }
        if ((this instanceof ProjectFileMetric) &&
            (o instanceof ProjectFile)) {
            ((ProjectFileMetric)this).run((ProjectFile) o);
            return;
        }
        if ((this instanceof FileGroupMetric) &&
            (o instanceof FileGroup)) {
            ((FileGroupMetric)this).run((FileGroup) o);
            return;
        }

        throw new MetricMismatchException(o);
    }

    /**
     * Add a supported metric description to the database.
     *
     * @param desc String description of the metric
     * @param type The metric type of the supported metric
     * @return True if the operation succeeds, false otherwise (i.e. duplicates etc)
     */
    protected final boolean addSupportedMetrics(String desc, MetricType.Type type) {
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
        m.setMetricType(MetricType.getMetricType(type));
        m.setPlugin(Plugin.getPlugin(getName()));
        return db.addRecord(m);
    }

    /**
     * Get the description objects for all metrics supported by this plug-in
     * as found in the database.
     *
     * @return the list of metric descriptors, or null if none
     */
    public List<Metric> getSupportedMetrics() {
        if (metrics == null) {
            metrics = Plugin.getSupportedMetrics(Plugin.getPlugin(getName()));
        }
        
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
    public void markEvaluation (Metric me, StoredProject sp) {
    	if(evaluationMarked.containsKey(sp.getId()) &&
    			!evaluationMarked.get(sp.getId())) {
    		// Get a DB session
            Session s = db.getSession(this);

            // Search for a previous evaluation of this metric on this project
            HashMap<String, Object> filter = new HashMap<String, Object>();
            filter.put("metric", me);
            filter.put("storedProject", sp);
            List<EvaluationMark> wasEvaluated = db.findObjectsByProperties(s,
                    EvaluationMark.class, filter);

            // If this is a first time evaluation, then remember this in the DB
            if (wasEvaluated.isEmpty()) {
                EvaluationMark evaluationMark = new EvaluationMark();
                evaluationMark.setMetric(me);
                evaluationMark.setStoredProject(sp);
                db.addRecord(evaluationMark);
            }
            evaluationMarked.put(sp.getId(), true);
            // Free the DB session
            db.returnSession(s);
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

        Plugin p = new Plugin();
        p.setName(getName());
        p.setInstalldate(new Date(System.currentTimeMillis()));
        p.setVersion(getVersion());
        p.setActive(true);
        db.addRecord(p);
        
        return true;
    }

    /**
     * Remove a plug-in's record from the DB. The DB's referential integrity
     * mechanisms are expected to automatically remove associated records.
     * Subclasses should also clean up any custom tables created.
     *
     * TODO: Remove metric registrations from the plugin registry
     */
    public boolean remove() {

        Plugin p = Plugin.getPlugin(getName());
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

    /** {@inheritDoc} */
    public Collection<Pair<String, ConfigurationTypes>> getConfigurationSchema() {
        return null;
        // Pretend that there are no configuration values.
        
//        return null;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

