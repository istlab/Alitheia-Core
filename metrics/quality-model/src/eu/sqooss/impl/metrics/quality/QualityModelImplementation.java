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
	
    private HashMap<String,String> mnemonicToName;

    public static final String OVERALL_QUALITY_MNEMONIC = "Qual";
    public static final String CODE_QUALITY_MNEMONIC = "Qual.cd";
    public static final String MAINTAINABLITY_MNEMONIC = "Qual.cd.mnt";
    public static final String ANALAZYBILITY_MNEMONIC = "Qual.cd.mnt.anlz";
    public static final String CHANGEABILITY_MNEMONIC = "Qual.cd.mnt.chng";
    public static final String STABILITY_MNEMONIC = "Qual.cd.mnt.stb";
    public static final String TESTABILITY_MNEMONIC = "Qual.cd.mnt.tstb";
    public static final String RELIABILITY_MNEMONIC = "Qual.cd.rlb";
    public static final String MATURITY_MNEMONIC = "Qual.cd.rlb.mtr";
    public static final String EFFECTIVENESS_MNEMONIC = "Qual.cd.rlb.effct";
    public static final String SECURITY_MNEMONIC = "Qual.cd.sec";
    public static final String COMMUNITY_QUALITY_MNEMONIC = "Qual.cm";
    public static final String MAILLING_LIST_QUALITY_MNEMONIC = "Qual.cm.mail";
    public static final String DOCUMENTATION_QUALITY_MNEMONIC = "Qual.cm.doc";


	public QualityModelImplementation(BundleContext bc) {
        super(bc);     
        super.addActivationType(ProjectVersion.class);
        super.addMetricActivationType(OVERALL_QUALITY_MNEMONIC, ProjectVersion.class);

     // Quality Sub-Metrics
        super.addMetricActivationType(CODE_QUALITY_MNEMONIC, ProjectVersion.class);
        super.addMetricActivationType(COMMUNITY_QUALITY_MNEMONIC, ProjectVersion.class);

     // Code Quality Sub-Metrics
        super.addMetricActivationType(MAINTAINABLITY_MNEMONIC, ProjectVersion.class);        
        super.addMetricActivationType(RELIABILITY_MNEMONIC, ProjectVersion.class);
        super.addMetricActivationType(SECURITY_MNEMONIC, ProjectVersion.class);

     // Maintainabilty Quality Sub-Metrics
        super.addMetricActivationType(ANALAZYBILITY_MNEMONIC, ProjectVersion.class);        
        super.addMetricActivationType(CHANGEABILITY_MNEMONIC, ProjectVersion.class);
        super.addMetricActivationType(STABILITY_MNEMONIC, ProjectVersion.class);
        super.addMetricActivationType(TESTABILITY_MNEMONIC, ProjectVersion.class);

     // Reliability Quality Sub-Metrics
        super.addMetricActivationType(MATURITY_MNEMONIC, ProjectVersion.class);        
        super.addMetricActivationType(EFFECTIVENESS_MNEMONIC, ProjectVersion.class);

     // Community Quality Sub-Metrics
        super.addMetricActivationType(MAILLING_LIST_QUALITY_MNEMONIC, ProjectVersion.class);
        super.addMetricActivationType(DOCUMENTATION_QUALITY_MNEMONIC, ProjectVersion.class);
        
        initializeMnemonicToName();        

     // Retrieve the instance of the Alitheia core service
        ServiceReference serviceRef = bc.getServiceReference(
                AlitheiaCore.class.getName());
        if (serviceRef != null)
            core = (AlitheiaCore) bc.getService(serviceRef);
    }
    
    private void initializeMnemonicToName(){
        mnemonicToName = new HashMap<String,String>();
        mnemonicToName.put(OVERALL_QUALITY_MNEMONIC, QualityModelBean.OVERALL_QUALITY);
        mnemonicToName.put(CODE_QUALITY_MNEMONIC, QualityModelBean.CODE_QUALITY);
        mnemonicToName.put(MAINTAINABLITY_MNEMONIC, QualityModelBean.MAINTAINABLITY);
        mnemonicToName.put(ANALAZYBILITY_MNEMONIC, QualityModelBean.ANALAZYBILITY);
        mnemonicToName.put(CHANGEABILITY_MNEMONIC, QualityModelBean.CHANGEABILITY);
        mnemonicToName.put(STABILITY_MNEMONIC, QualityModelBean.STABILITY);
        mnemonicToName.put(TESTABILITY_MNEMONIC, QualityModelBean.TESTABILITY);
        mnemonicToName.put(RELIABILITY_MNEMONIC, QualityModelBean.RELIABILITY);
        mnemonicToName.put(MATURITY_MNEMONIC, QualityModelBean.MATURITY);
        mnemonicToName.put(EFFECTIVENESS_MNEMONIC, QualityModelBean.EFFECTIVENESS);
        mnemonicToName.put(SECURITY_MNEMONIC, QualityModelBean.SECURITY);
        mnemonicToName.put(COMMUNITY_QUALITY_MNEMONIC, QualityModelBean.COMMUNITY_QUALITY);
        mnemonicToName.put(MAILLING_LIST_QUALITY_MNEMONIC, QualityModelBean.MAILLING_LIST_QUALITY);
        mnemonicToName.put(DOCUMENTATION_QUALITY_MNEMONIC, QualityModelBean.DOCUMENTATION_QUALITY);

    }
      
    public boolean install() { 	
    	boolean result = true;
    	
    	
    	//TODO add ALL the metrics dependecies when they are ready!
    	addDependency("Wc.loc");
    	
    	// Installing...
    	result &= super.install();
    	if (result) {
    		result &= super.addSupportedMetrics("A Profile-based Overall Quality", OVERALL_QUALITY_MNEMONIC, MetricType.Type.PROJECT_WIDE);
    		result &= super.addSupportedMetrics("Code Quality", CODE_QUALITY_MNEMONIC, MetricType.Type.PROJECT_WIDE);
    		result &= super.addSupportedMetrics("Community Quality", COMMUNITY_QUALITY_MNEMONIC, MetricType.Type.PROJECT_WIDE);

         // Code Quality Sub-Metrics
            result &= super.addSupportedMetrics("Maintainability",MAINTAINABLITY_MNEMONIC, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics("Reliabity",RELIABILITY_MNEMONIC, MetricType.Type.BUG_DATABASE);
            result &= super.addSupportedMetrics("Security",SECURITY_MNEMONIC, MetricType.Type.SOURCE_CODE);

         // Maintainabilty Quality Sub-Metrics
            result &= super.addSupportedMetrics("Analyzability",ANALAZYBILITY_MNEMONIC, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics("Changeability",CHANGEABILITY_MNEMONIC, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics("Stability",STABILITY_MNEMONIC, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics("Testability",TESTABILITY_MNEMONIC, MetricType.Type.SOURCE_CODE);

         // Reliability Quality Sub-Metrics
            result &= super.addSupportedMetrics("Maturity",MATURITY_MNEMONIC, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics("Effectiveness",EFFECTIVENESS_MNEMONIC, MetricType.Type.SOURCE_CODE);

         // Community Quality Sub-Metrics
            result &= super.addSupportedMetrics("Mailling list quality",MAILLING_LIST_QUALITY_MNEMONIC, MetricType.Type.MAILING_LIST);
            result &= super.addSupportedMetrics("Documentation quality",DOCUMENTATION_QUALITY_MNEMONIC, MetricType.Type.PROJECT_WIDE);
            

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
