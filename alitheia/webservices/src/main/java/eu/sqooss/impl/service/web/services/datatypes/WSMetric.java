/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.impl.service.web.services.datatypes;

import java.util.List;

import eu.sqooss.service.db.Metric;

/**
 * This class wraps the <code>eu.sqooss.service.db.Metric</code>
 */
public class WSMetric {

    /** Metric metadata's fields */
    private long    id;
    private long    pluginId;
    private long    metricTypeId;
    private String  mnemonic;
    private String  description;
    private String  activator;

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the pluginId
     */
    public long getPluginId() {
        return pluginId;
    }
    
    /**
     * @param pluginId the pluginId to set
     */
    public void setPluginId(long pluginId) {
        this.pluginId = pluginId;
    }
    
    /**
     * @return the metricTypeId
     */
    public long getMetricTypeId() {
        return metricTypeId;
    }
    
    /**
     * @param metricTypeId the metricTypeId to set
     */
    public void setMetricTypeId(long metricTypeId) {
        this.metricTypeId = metricTypeId;
    }
    
    /**
     * @return the mnemonic
     */
    public String getMnemonic() {
        return mnemonic;
    }
    
    /**
     * @param mnemonic the mnemonic to set
     */
    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }
    
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the activation type that the parent plug-in has associated with
     * this metric.
     * 
     * @return The metric's activation type.
     */
    public String getActivator() {
        return activator;
    }

    /**
     * Sets the activation type of this metric. Usually this should be the
     * activation type that the plug-in associated with this metric during its
     * registration.
     * 
     * @param activator the activation type
     */
    public void setActivator(String activator) {
        this.activator = activator;
    }

    /**
     * The method creates a new <code>WSMetric</code> object
     * from the existent DAO object.
     * The method doesn't care of the db session. 
     * 
     * @param metric - DAO metric object
     * 
     * @return The new <code>WSMetric</code> object
     */
    public static WSMetric getInstance(Metric metric) {
        if (metric == null) return null;
        try {
            WSMetric wsMetric = new WSMetric();
            wsMetric.setId(metric.getId());
            wsMetric.setDescription(metric.getDescription());
            wsMetric.setMetricTypeId(metric.getMetricType().getId());
            wsMetric.setMnemonic(metric.getMnemonic());
            wsMetric.setPluginId(metric.getPlugin().getId());
            return wsMetric;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * The method returns an array containing
     * all of the elements in the metrics list.
     * The list argument should contain DAO
     * <code>Metric</code> objects.
     * The method doesn't care of the db session. 
     *  
     * @param metrics - the metrics list;
     * the elements should be <code>Metric</code> objects  
     * 
     * @return - an array with <code>WSMetric</code> objects;
     * if the list is null, contains different object type
     * or the DAO can't be wrapped then the array is null
     */
    public static WSMetric[] asArray(List<?> metrics) {
        WSMetric[] result = null;
        if (metrics != null) {
            result = new WSMetric[metrics.size()];
            Metric currentMetric;
            WSMetric currentWSMetric;
            for (int i = 0; i < result.length; i++) {
                try {
                    currentMetric = (Metric) metrics.get(i);
                } catch (ClassCastException e) {
                    return null;
                }
                currentWSMetric = WSMetric.getInstance(currentMetric);
                if (currentWSMetric == null) return null;
                result[i] = currentWSMetric;
            }
        }
        return result;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
