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
package eu.sqooss.metrics.abstractmetric;

import java.util.Date;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.FileGroup;
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
public abstract class AbstractMetric implements Metric {

    protected BundleContext bc;
    protected ServiceReference serviceRef = null;

    protected LogManager logService = null;

    protected Logger logger = null;

    
    protected AbstractMetric(BundleContext bc){ 
	/* Get a reference to the logging service */
	serviceRef = bc.getServiceReference(LogManager.class.getName());
	logService = (LogManager) bc.getService(serviceRef);

	if (logService != null) {
	    logger = logService.createLogger(Logger.NAME_SQOOSS_METRIC);

	    if (logger != null)
		logger.info("Got a valid reference to the logger");
	}

	if (logger == null) {
	    System.out.println("ERROR: Got no logger");
	}
	this.bc = bc;
    }
    
    public String getAuthor() {
	if (bc != null)
	    return (String) bc.getBundle().getHeaders().get(
		    org.osgi.framework.Constants.BUNDLE_CONTACTADDRESS);
	return null;
    }

    public String getDescription() {
	if (bc != null)
	    return (String) bc.getBundle().getHeaders().get(
		    org.osgi.framework.Constants.BUNDLE_DESCRIPTION);
	return null;
    }

    public String getName() {
	if (bc != null)
	    return (String) bc.getBundle().getHeaders().get(
		    org.osgi.framework.Constants.BUNDLE_NAME);
	return null;
    }

    public String getVersion() {
	if (bc != null)
	    return (String) bc.getBundle().getHeaders().get(
		    org.osgi.framework.Constants.BUNDLE_VERSION);
	return null;
    }

    public void delete(DAObject o) {
	if(this instanceof ProjectVersionMetric)
	    delete((ProjectVersion) o);
	if(this instanceof StoredProjectMetric)
	    delete((StoredProject) o);
	if(this instanceof ProjectFileMetric)
	    delete((ProjectFile) o);
	if(this instanceof FileGroupMetric)
	    delete((FileGroup) o);
    }

    public MetricResult getResult(DAObject o) {
	
	if(this instanceof ProjectVersionMetric)
	    return getResult((ProjectVersion) o);
	if(this instanceof StoredProjectMetric)
	    return getResult((StoredProject) o);
	if(this instanceof ProjectFileMetric)
	    return getResult((ProjectFile) o);
	if(this instanceof FileGroupMetric)
	    return getResult((FileGroup) o);
	return null;
    }

    public void run(DAObject o) {
	if(this instanceof ProjectVersionMetric)
	    run((ProjectVersion) o);
	if(this instanceof StoredProjectMetric)
	    run((StoredProject) o);
	if(this instanceof ProjectFileMetric)
	    run((ProjectFile) o);
	if(this instanceof FileGroupMetric)
	    run((FileGroup) o);
    }

    public abstract boolean install();

    public abstract boolean remove();

    public abstract boolean update();

    public abstract Date getDateInstalled();
    
    
    protected boolean initStorage() {
	
	return false;
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

