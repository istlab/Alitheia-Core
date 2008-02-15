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
package eu.sqooss.service.pa;

import java.util.Collection;
import org.osgi.framework.ServiceReference;

import eu.sqooss.service.abstractmetric.Metric;
import eu.sqooss.service.db.DAObject;

// TODO: Auto-generated Javadoc
/**
 * PluginAdmin defines an interface for classes that provide utilities for
 *  managing SQO-OSS plug-ins, and more specifically metric plug-ins.
 */

public interface PluginAdmin {

    /** The Constant METRICS_CLASS shall be used as a filter when searching
     * for registered metric services. */
    public final static String METRICS_CLASS = "eu.sqooss.impl.metrics.*";

    /**
     * Returns a collection containing information about all metrics services
     * currently registered in the framework.
     *
     * @return the list of all metrics currently registered in the framework
     */
    public Collection<MetricInfo> listMetrics();

    /**
     * Get the list of metrics that have (sub-)interfaces for the given
     * DAO object's type. The service references are returned, not the
     * services themselves, since the metrics may disappear or be un-registered
     * during the lifetime of this collection.
     *
     * @param o Object that implies the type of interface that is wanted.
     * @return Collection of services references. May be null
     *          if no such interfaces exist.
     */
    public ServiceReference[] listMetricProviders(DAObject o);
    
    /**
     * Get the list of metrics that are interested in ProjectVersions;
     * equivalent to passing a DAObject of class ProjectVersion to
     * listMetricProviders, above.
     * 
     * @return Collection of service references. May be null if no
     *          such interfaces exist.
     */
    public ServiceReference[] listProjectVersionMetrics();
    
    /**
     * Calls the install() method of the metric object provided from a metric
     * service registered with the specified service ID.
     *
     * @param service_ID the service ID of the selected metric service
     *
     * @return true, if successful; false otherwise
     */
    public boolean installMetric(Long service_ID);
}

//vi: ai nosi sw=4 ts=4 expandtab
