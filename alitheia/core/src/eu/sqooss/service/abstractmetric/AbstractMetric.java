/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 by Georgios Gousios <gousiosg@gmail.com>
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

import java.util.Date;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import org.hibernate.Session;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.FileGroup;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;


/**
 * A base class for all metrics. Implements basic functionality such as
 * logging setup and plug-in information retrieval from the OSGi bundle
 * manifest file. Metrics can choose to directly implement
 * the {@link eu.sqooss.metrics.Metric} interface instead of extending 
 * this class.
 */
public abstract class AbstractMetric implements eu.sqooss.service.abstractmetric.Metric {

    protected BundleContext bc;
    protected LogManager logService = null;
    protected Logger log = null;
    protected DBService db;
    
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

    public String getAuthor() {

        return (String) bc.getBundle().getHeaders().get(
                org.osgi.framework.Constants.BUNDLE_CONTACTADDRESS);
    }

    public String getDescription() {

        return (String) bc.getBundle().getHeaders().get(
                org.osgi.framework.Constants.BUNDLE_DESCRIPTION);
    }

    public String getName() {

        return (String) bc.getBundle().getHeaders().get(
                org.osgi.framework.Constants.BUNDLE_NAME);
    }

    public String getVersion() {

        return (String) bc.getBundle().getHeaders().get(
                org.osgi.framework.Constants.BUNDLE_VERSION);
    }

    public final Date getDateInstalled() {
        return Plugin.getPlugin(db, getName()).getInstalldate();
    }

    public MetricResult getResult(DAObject o) {

        if (this instanceof ProjectVersionMetric)
            return getResult((ProjectVersion) o);
        if (this instanceof StoredProjectMetric)
            return getResult((StoredProject) o);
        if (this instanceof ProjectFileMetric)
            return getResult((ProjectFile) o);
        if (this instanceof FileGroupMetric)
            return getResult((FileGroup) o);
        return null;
    }

    public void run(DAObject o) {
        if (this instanceof ProjectVersionMetric)
            run((ProjectVersion) o);
        if (this instanceof StoredProjectMetric)
            run((StoredProject) o);
        if (this instanceof ProjectFileMetric)
            run((ProjectFile) o);
        if (this instanceof FileGroupMetric)
            run((FileGroup) o);
    }

    /**
     * Add a supported metric description to the database.
     * 
     * @param desc String description of the metric
     * @param type The metric type of the supported metric
     * @return 
     */
    protected final boolean addSupportedMetrics(String desc, MetricType.Type type) {
        Metric m = new Metric();
        m.setDescription(desc);
        m.setMetricType(MetricType.getMetricType(db, type));
        m.setPlugin(Plugin.getPlugin(db, getName()));
        return db.addRecord(m);
    }
    
    /**
     * Register the metric to the DB. Subclasses can perform
     * their custom initialization routines after calling 
     * super()
     */
    public boolean install() {
        
        Session s = db.getSession(this);

        List plugins = s.createQuery("from Plugin as m where m.name = ? ")
                .setString(0, getName())
                .list();

        if (!plugins.isEmpty()) {
            log.warn("Plugin <" + getName()
                    + "> is already installed, won't re-install.");
            return false;
        }

        Plugin p = new Plugin();
        p.setName(getName());
        p.setInstalldate(new Date(System.currentTimeMillis()));
        db.returnSession(s);
        
        return db.addRecord(p);
    }

    public abstract boolean remove();

    public abstract boolean update();

}

// vi: ai nosi sw=4 ts=4 expandtab

