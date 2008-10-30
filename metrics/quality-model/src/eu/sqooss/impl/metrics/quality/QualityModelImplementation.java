/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Stefanos Skalistis <sskalistis@gmail.com>
 * 											 <sskalist@gmail.com>
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

package eu.sqooss.impl.metrics.quality;

import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.metrics.quality.QualityModel;
import eu.sqooss.metrics.quality.bean.QualityModelBean;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;


public class QualityModelImplementation extends AbstractMetric implements QualityModel {

	/**
	 * Holds the instance of the Alitheia core service
	 */
	private AlitheiaCore core;
	
	public QualityModelImplementation(BundleContext bc) {
        super(bc);     
        super.addActivationType(ProjectVersion.class);
        super.addMetricActivationType("Qual", ProjectVersion.class);

     // Quality Sub-Metrics
        super.addMetricActivationType("Qual.cd", ProjectVersion.class);
        super.addMetricActivationType("Qual.cm", ProjectVersion.class);

     // Code Quality Sub-Metrics
        super.addMetricActivationType("Qual.cd.mnt", ProjectVersion.class);        
        super.addMetricActivationType("Qual.cd.rlb", ProjectVersion.class);
        super.addMetricActivationType("Qual.cd.sec", ProjectVersion.class);

     // Maintainabilty Quality Sub-Metrics
        super.addMetricActivationType("Qual.cd.mnt.anlz", ProjectVersion.class);        
        super.addMetricActivationType("Qual.cd.mnt.chng", ProjectVersion.class);
        super.addMetricActivationType("Qual.cd.mnt.stb", ProjectVersion.class);
        super.addMetricActivationType("Qual.cd.mnt.tstb", ProjectVersion.class);

     // Community Quality Sub-Metrics
        super.addMetricActivationType("Qual.cm.mail", ProjectVersion.class);
        super.addMetricActivationType("Qual.cd.doc", ProjectVersion.class);
        
        
     // Retrieve the instance of the Alitheia core service
        ServiceReference serviceRef = bc.getServiceReference(
                AlitheiaCore.class.getName());
        if (serviceRef != null)
            core = (AlitheiaCore) bc.getService(serviceRef);
    }
      
    public boolean install() { 	
    	boolean result = true;
    	
    	
    	//TODO add ALL the metrics dependecies when they are ready!
    	addDependency("Wc.loc");
    	
    	// Installing...
    	result &= super.install();
    	if (result) {
    		result &= super.addSupportedMetrics("A Profile-based Overall Quality", "Qual", MetricType.Type.PROJECT_WIDE);
    		result &= super.addSupportedMetrics("Code Quality", "Qual.cd", MetricType.Type.PROJECT_WIDE);
    		result &= super.addSupportedMetrics("Community Quality", "Qual.cm", MetricType.Type.PROJECT_WIDE);

         // Code Quality Sub-Metrics
            result &= super.addSupportedMetrics("Maintainability","Qual.cd.mnt", MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics("Reliabity","Qual.cd.rlb", MetricType.Type.BUG_DATABASE);
            result &= super.addSupportedMetrics("Security","Qual.cd.sec", MetricType.Type.SOURCE_CODE);

         // Maintainabilty Quality Sub-Metrics
            result &= super.addSupportedMetrics("Analyzability","Qual.cd.mnt.anlz", MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics("Changeability","Qual.cd.mnt.chng", MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics("Stability","Qual.cd.mnt.stb", MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics("Testability","Qual.cd.mnt.tstb", MetricType.Type.SOURCE_CODE);

         // Community Quality Sub-Metrics
            result &= super.addSupportedMetrics("Mailling list quality","Qual.cm.mail", MetricType.Type.MAILING_LIST);
            result &= super.addSupportedMetrics("Documentation quality","Qual.cm.doc", MetricType.Type.PROJECT_WIDE);
            

    	}
    	return result;
    }

    public boolean remove() {
    	boolean result = super.remove();
    	return result;
    }

    public List<ResultEntry> getResult(ProjectVersion v, Metric m) {
        
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();
        // Search for a matching project file measurement
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectVersion", v);
        filter.put("metric", m);
        List<ProjectVersionMeasurement> measurement =
            db.findObjectsByProperties(ProjectVersionMeasurement.class, filter);
    	return convertVersionMeasurement(measurement.get(0),m.getMnemonic());
    }

	public void run(ProjectVersion v) {
        String result = null;
        
        QualityModelBean model = new QualityModelBean();
        // Store the results
        // NOTE: Might not work...most use getMetricByMnemonic()
        for(Metric metric : this.getSupportedMetrics() ){
            result = model.getPessimisticAssignement().toString();
            addProjectVersionMeasurement(metric,v,result);
        }

        
    }  

    private void addProjectVersionMeasurement(Metric metric, ProjectVersion pv, String value) {
        ProjectVersionMeasurement pvm = new ProjectVersionMeasurement(metric, pv, value);
        db.addRecord(pvm);
        markEvaluation(metric, pv);
    }
     /**
      * Convenience method to convert a single measurement to a list of
      * result entries
      * @param v Single measurement to convert
      * @param label Metric mnemonic label
      * @return Singleton list of results for the measurement
      */
      public static List<ResultEntry> convertVersionMeasurement(ProjectVersionMeasurement v, String mnemonic) {
         List<ResultEntry> results = new ArrayList<ResultEntry>(1);
         results.add(new ResultEntry(v.getResult(), ResultEntry.MIME_TYPE_TEXT_PLAIN, mnemonic));
         return results;
     }

	
}

// vi: ai nosi sw=4 ts=4 expandtab
