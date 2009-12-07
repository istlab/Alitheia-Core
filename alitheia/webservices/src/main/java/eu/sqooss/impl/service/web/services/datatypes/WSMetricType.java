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

import eu.sqooss.service.db.MetricType;

/**
 * This class wraps the <code>eu.sqooss.service.db.MetricType</code> 
 */
public class WSMetricType {
    
    private long id;
    private String type;
    
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
     * @return the type
     */
    public String getType() {
        return type;
    }
    
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * The method creates a new <code>WSMetricType</code> object
     * from the existent DAO object.
     * The method doesn't care of the db session. 
     * 
     * @param metricType - DAO metric type object
     * 
     * @return The new <code>WSMetricType</code> object
     */
    public static WSMetricType getInstance(MetricType metricType) {
        if (metricType == null) return null;
        try {
            WSMetricType wsMetricType = new WSMetricType();
            wsMetricType.setId(metricType.getId());
            wsMetricType.setType(metricType.getType());
            return wsMetricType;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * The method returns an array containing
     * all of the elements in the metric types list.
     * The list argument should contain DAO
     * <code>MetricType</code> objects.
     * The method doesn't care of the db session.
     *  
     * @param metricTypes - the metric types list;
     * the elements should be <code>MetricType</code> objects  
     * 
     * @return - an array with <code>WSMetricType</code> objects;
     * if the list is null, contains different object type
     * or the DAO can't be wrapped then the array is null
     */
    public static WSMetricType[] asArray(List<?> metricTypes) {
        WSMetricType[] result = null;
        if (metricTypes != null) {
            result = new WSMetricType[metricTypes.size()];
            MetricType currentMetricType;
            WSMetricType currentWSMetricType;
            for (int i = 0; i < result.length; i++) {
                try {
                    currentMetricType = (MetricType) metricTypes.get(i);
                } catch (ClassCastException e) {
                    return null;
                }
                currentWSMetricType = WSMetricType.getInstance(currentMetricType);
                if (currentWSMetricType == null) return null;
                result[i] = currentWSMetricType;
            }
        }
        return result;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
