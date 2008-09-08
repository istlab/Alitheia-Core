/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Paul J. Adams <paul.adams@siriusit.co.uk>
 * Copyright 2008 by Adriaan de Groot <groot@kde.org>
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

package eu.sqooss.metrics.mde;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ProjectVersionMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class MDEImplementation extends AbstractMetric implements ProjectVersionMetric {
    private FDSService fds;

    private static final String MNEMONIC_MDE_DEVTOTAL = "MDE.dt";
    
    public MDEImplementation(BundleContext bc) {
        super(bc);
        super.addActivationType(ProjectVersion.class);
        
        super.addMetricActivationType(MNEMONIC_MDE_DEVTOTAL, ProjectVersion.class);
        
        ServiceReference serviceRef = null;
        serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
       
        fds = ((AlitheiaCore)bc.getService(serviceRef)).getFDSService();    
    }

    public boolean install() {
        boolean result = super.install();
        if (result) {
            result &= super.addSupportedMetrics(
                    "Mean Developer Engagement: Ancilliary dev(total)",
                    MNEMONIC_MDE_DEVTOTAL,
                    MetricType.Type.SOURCE_CODE);
        }
        return result;
    }

    public List<ResultEntry> getResult(ProjectVersion a, Metric m) {
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();
        // Search for a matching project file measurement
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectVersion", a);
        filter.put("metric", m);
        List<ProjectVersionMeasurement> measurement =
            db.findObjectsByProperties(ProjectVersionMeasurement.class, filter);
	return convertVersionMeasurements(measurement,m.getMnemonic());
    }

    public void run(ProjectVersion pv) {
	// Find the latest ProjectVersion for which we have data
	Metric m = Metric.getMetricByMnemonic(MNEMONIC_MDE_DEVTOTAL);
	try {
	    List<?> id = 
		db.doSQL("select max(project_version_id) from project_version_measurement where metric_id=" +
			 m.getId());
	    if(!id.isEmpty()) {
		ProjectVersionMeasurement latest =
		    db.findObjectById(ProjectVersionMeasurement.class, (Long) id.get(0));
	    }
	}
	catch(java.sql.SQLException err) {
	    // Worry about this later
	}
    }

    /*
     * Find the total number of devleopers in the project for every week
     * for which the data is unknown up until pv
     */
    public void runDevTotal(ProjectVersion pv) {
    }
}
