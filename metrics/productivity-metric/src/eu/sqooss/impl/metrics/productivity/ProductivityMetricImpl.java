/* This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.impl.metrics.productivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions.ActionCategory;
import eu.sqooss.impl.metrics.productivity.ProductivityMetricActions.ActionType;
import eu.sqooss.metrics.productivity.ProductivityMetric;
import eu.sqooss.metrics.productivity.db.ProductivityActionType;
import eu.sqooss.metrics.productivity.db.ProductivityActions;
import eu.sqooss.metrics.productivity.db.ProductivityWeights;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.pa.PluginInfo;

public class ProductivityMetricImpl extends AbstractMetric implements
        ProductivityMetric {

    public static final String CONFIG_CMF_THRES = "CMF_threshold";
    public static final String CONFIG_WEIGHT_UPDATE_VERSIONS = "Weights_Update_Interval";
    
    public ProductivityMetricImpl(BundleContext bc) {
        super(bc);
        super.addActivationType(ProjectVersion.class);
        super.addActivationType(Developer.class);
        
        super.addMetricActivationType("PROD", Developer.class);
    }
    
    public boolean install() {
    	 boolean result = super.install();
         if (result) {
             result &= super.addSupportedMetrics(
                     "Developer Productivity Metric",
                     "PROD",
                     MetricType.Type.PROJECT_WIDE);
         }
         addConfigEntry(CONFIG_CMF_THRES, 
                 "5" , 
                 "Number of committed files above which the developer is penalized", 
                 PluginInfo.ConfigurationType.INTEGER);
         addConfigEntry(CONFIG_WEIGHT_UPDATE_VERSIONS, 
                 "150" , 
                 "Number of new versions before recalculating the productivity weights", 
                 PluginInfo.ConfigurationType.INTEGER);
         return result;
    }

    /**
     * Returns an arbitrary result to indicate that the provided project
     * version has been already processed. If the provided version 
     * was not processed, it returns null.
     * 
     * {@inheritDoc}
     */
    public List<ResultEntry> getResult(ProjectVersion a, Metric m) {
        
        ArrayList<ResultEntry> res = new ArrayList<ResultEntry>();
        String paramVersion = "paramVersion";
        
        String query = "select a from ProductivityActions a " +
                " where a.projectVersion = :" + paramVersion ;
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramVersion, a);

        List<?> p = db.doHQL(query, parameters);
    
        if ( p == null || p.isEmpty() ){
            return null;
        } 
            
        res.add(new ResultEntry(1, ResultEntry.MIME_TYPE_TYPE_INTEGER, 
                m.getMnemonic()));
        return res;
    }

    /**
     * This plug-in's result is returned per developer. 
     */
    public List<ResultEntry> getResult(Developer a, Metric m) {
        
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();
        ProductivityWeights weight;
        double value = 0;

        ActionCategory[] actionCategories = ActionCategory.values();

        for (int i = 0; i < actionCategories.length; i++) {
            weight = ProductivityWeights.getWeight(actionCategories[i]);

            if (weight != null) {
                value = value + weight.getWeight() * 
                    getResultPerActionCategory(a, actionCategories[i]);
            }
        }

        ResultEntry entry = new ResultEntry(value,
                ResultEntry.MIME_TYPE_TYPE_DOUBLE, m.getMnemonic());
        results.add(entry);
        return results;
    }

    public void run(ProjectVersion v) {
        ProductivityMetricJob j = new ProductivityMetricJob(bc, this, v);
        j.run();
    }

    public void run(Developer v) {
        
    }

    /**
     * Get result per developer and per category
     * 
     */
    private double getResultPerActionCategory(Developer d, ActionCategory ac) {
        
        ArrayList<ActionType> actionTypes = ActionType.getActionTypes(ac);
        
        ProductivityWeights weight;
        long totalActions;
        double value = 0;

        for (int i=0; i<actionTypes.size(); i++) {
            weight = ProductivityWeights.getWeight(actionTypes.get(i));
            
            if (weight == null) {
                continue;
            }
            
            ProductivityActionType at = 
                ProductivityActionType.getProductivityActionType(actionTypes.get(i), null);
                
            totalActions = 
                ProductivityActions.getTotalActionsPerTypePerDeveloper(actionTypes.get(i), d);

            if(totalActions != 0){
                if (at.getIsPositive())
                    value += weight.getWeight() * totalActions;
                else
                    value -= weight.getWeight() * totalActions;
            }
        }
        return value;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
