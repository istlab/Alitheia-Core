/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Paul J. Adams <paul.adams@siriusit.co.uk>
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

package eu.sqooss.service.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.service.CoreActivator;
import eu.sqooss.service.db.DAObject;

public class Metric extends DAObject{
    private Plugin plugin;
    private MetricType metricType;
    private String mnemonic;
    private String description;

    public Metric() {
        //Nothing to do here
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (!(obj instanceof Metric))) {
            return false;
        }
        Metric anotherMetric = (Metric) obj;
        if (mnemonic == null) {
            return this.getId() == anotherMetric.getId(); 
        } else {
            return (this.mnemonic.equals(anotherMetric.getMnemonic()));
        }
    }

    @Override
    public int hashCode() {
        if (mnemonic != null) {
            return mnemonic.hashCode(); 
        } else {
            return Long.valueOf(this.getId()).hashCode();
        }
    }   
    
    /**
     * Get a metric from its mnemonic name
     * @param mnem - The metric mnemonic name to search for
     * @return A Metric object or null when no metric can be found for the 
     * provided mnemonic
     */
    public static Metric getMetricByMnemonic(String mnem) {
        DBService dbs = CoreActivator.getDBService();
        
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("mnemonic", mnem);
        
        List<Metric> result = dbs.findObjectsByProperties(Metric.class, properties);
        
        if (result.size() <= 0)
            return null;
            
        return result.get(0);
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

