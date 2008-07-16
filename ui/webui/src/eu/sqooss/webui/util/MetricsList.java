/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Sebastian Kuegler <sebas@kde.org>
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

package eu.sqooss.webui.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import eu.sqooss.webui.Metric;
import eu.sqooss.webui.Metric.MetricActivator;
import eu.sqooss.webui.Metric.MetricType;

public class MetricsList extends ArrayList<Metric> {

    /**
     * Class serial
     */
    private static final long serialVersionUID = 2751571082259271311L;

    /**
     * Returns the list of all metrics sorted by their mnemonic name.
     * 
     * @return The list of metrics.
     */
    public SortedMap<String, Metric> sortByMnemonic() {
        SortedMap<String, Metric> result = new TreeMap<String, Metric>();
        for (Metric nextMetric : this)
            result.put(nextMetric.getMnemonic(), nextMetric);
        return result;
    }

    /**
     * Returns the list of all metrics sorted by their Id.
     * 
     * @return The list of metrics.
     */
    public SortedMap<Long, Metric> sortById() {
        SortedMap<Long, Metric> result = new TreeMap<Long, Metric>();
        for (Metric nextMetric : this)
            result.put(nextMetric.getId(), nextMetric);
        return result;
    }

    /**
     * Gets the metric with the given mnemonic name.
     * 
     * @param mnemonic the metric's mnemonic name
     * 
     * @return The metric object, or <code>null</code> if a metric with the
     *   given mnemonic name can not be found in this list.
     */
    public Metric getMetricByMnemonic(String mnemonic) {
        if (mnemonic == null) return null;
        return sortByMnemonic().get(mnemonic);
    }

    /**
     * Gets the metric with the given Id.
     * 
     * @param id the metric's Id
     * 
     * @return The metric object, or <code>null</code> if a metric with the
     *   given Id can not be found in this list.
     */
    public Metric getMetricById(Long id) {
        return sortById().get(id);
    }

    /**
     * Gets the list of mnemonic names of all metrics in this list, indexed by
     * metric Id.
     * 
     * @return The list of mnemonic names, or an empty list when none are
     *   selected.
     */
    public Map<Long, String> getMetricMnemonics() {
        Map<Long, String> result = new HashMap<Long, String>();
        for (Metric nextMetric : this)
            result.put(nextMetric.getId(), nextMetric.getMnemonic());
        return result;
    }

    /**
     * Gets a filtered list of the mnemonic names of all metrics in this list,
     * that match the given <code>MetricActivator</code> and 
     * <code>MetricType</code> filters, indexed by metric Id.
     * 
     * @param activator the metric activator filter
     * @param type the metric type filter
     * 
     * @return The list of mnemonic names, or an empty list when none are
     * selected.
     */
    public Map<Long, String> getMetricMnemonics(
            MetricActivator activator, MetricType type) {
        Map<Long, String> result = new HashMap<Long, String>();
        for (Metric nextMetric : this) {
            if ((nextMetric.getActivator().equals(activator))
                    && (nextMetric.getType().equals(type)))
                result.put(nextMetric.getId(), nextMetric.getMnemonic());
        }
        return result;
    }
}
