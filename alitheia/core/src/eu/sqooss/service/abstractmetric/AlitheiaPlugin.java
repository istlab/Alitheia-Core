/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.service.abstractmetric;

import java.util.Date;
import java.util.List;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.PluginConfiguration;


/**
 * This interface defines the common metric plug-in related functionality.
 * It must be implemented by all metric plug-ins.
 * <br/>
 * There are four areas of functionality covered by this interface:
 * <ul>
 *   <li> metric meta-data (describing the metric plug-in)
 *   <li> evaluation measurements
 *   <li> plug-in life cycle (installation and removal routines)
 *   <li> configuration management
 * </ul>
 * <br/><br/>
 * The metric meta-data comprises plug-in name, description, author
 * information and installation date; which are static for each metric
 * plug-in.
 *<br/><br/>
 * Measurement comprises two methods: <code>run()</code> which performs a
 * measurement on some project artifact (<i>which one depends on the type of
 * <code>DAObject</code> which is passed in</i>) and <code>getResult()</code>,
 * which returns the value(s) obtained by a previous measurement.
 * <br/><br/>
 * Life-cycle management is implemented in three methods:
 * <ul>
 *   <li> install
 *   <li> remove
 *   <li> update
 * </ul>
 * These takes care of the proper initialization and setup of a metric plug-in
 * during installation, as well as its proper removal when it is no more
 * needed.
 * <br/><br/>
 * Finally, configuration management deals with configuration settings, that
 * each plug-in can be accompanied with. A configuration property comprises of
 * a name, value, type, and a description tuple, and is stored directly into a
 * SQO-OSS database object that represents that configuration entry.
 *<br/><br/>
 * All metrics are bound to one or more of the following project resources:
 * <ul>
 *   <li>Project</li>
 *   <li>Project Version</li>
 *   <li>File Group</li>
 *   <li>File</li>
 *  </ul>
 * 
 * As a result, all metric plug-in implementations must implement at least two
 * interfaces:
 *  <ul>
 *      <li>This interface</li>
 *      <li>One or more of the following interfaces, depending on the type of
 *      the resource the metric plug-in is bound to</li>
 *      <ul>
 *          <li>{@link StoredProjectMetric}</li>
 *          <li>{@link ProjectVersionMetric}</li>
 *          <li>{@link ProjectFileMetric}</li>
 *          <li>{@link FileGroupMetric}</li>
 *      </ul>
 *  </ul>
 *
 */
public interface AlitheiaPlugin {

    /**
     * Get the metric version. Free form text.
     * 
     * @return The metric's version.
     */
    String getVersion();

    /**
     * Get information about the metric author
     * 
     * @return The metric's author.
     */
    String getAuthor();

    /**
     * Get the date this version of the metric has been installed
     * 
     * @return The metric's installation date.
     */
    Date getDateInstalled();

    /**
     * Get the metric name
     * 
     * @return The metric's name.
     */
    String getName();

    /**
     * Get a free text description of what this metric calculates
     * 
     * @return The metric's description.
     */
    String getDescription();

    /**
     * Generic "get results" method, it is specialised by sub-interfaces.
     *
     * @param o DAO whose type specifies the specialised sub-interface to use
     *          and whose value determines which result to get.
     * @return l A list of metrics 
     * @return value of the measurement or null if there is no such measurement.
     * @throws MetricMismatchException if the DAO type is one not supported by
     *          this metric.
     */
    Result getResult(DAObject o, List<Metric> l)
        throws MetricMismatchException;

    /**
     * Get the description objects for all metrics supported by this plug-in
     * as found in the database.
     *
     * @return the list of metric descriptors, or null if none
     */
    List<Metric> getSupportedMetrics();
    
    /**
     * Generic "run plug-in" method. This method performs a measurement for
     * the given DAO, if possible. The DAO might be any one of the types
     * that make sense for measurements -- ProjectVersion, projectFile,
     * some others. If a DAO of a type that the metric doesn't support
     * is passed in, throws a MetricMismatchException.
     *
     * The calculation of measurements may be a computationally expensive
     * task, so metrics should start jobs (by themselves) to handle that.
     * The subclass AbstractMetric handles job creation automatically for
     * metrics that have simple requirements (a single job for doing the
     * calculation).
     *
     * @param o The DAO that gets passed to the plug-in in order to run it
     * @throws MetricMismatchException if the DAO is of an unsupported type.
     */
    void run(DAObject o)
        throws MetricMismatchException;

    /**
     * After installing a new version of the metric, try to
     * update the results. The metric may opt to partially
     * or fully update its results tables or files.
     *
     * @return True, if the update succeeded, false otherwise
     */
    boolean update();

    /**
     * Perform maintenance operations when installing a new
     * version of the metric
     *
     * @return True if installation succeeded, false otherwise 
     */
    boolean install();

    /**
     * Free the used resources and clean up on metric removal
     *
     * @return True, if the removal succeeded, false otherwise
     */
    boolean remove();
    
    /**
     * Return a string that is unique for this plugin, used for indexing this
     * plugin to the system database
     * 
     * @return A unique string, max length 255 characters
     */
    String getUniqueKey();
    
    /**
     * Get the types supported by this plug-in for data processing and result
     * retrieval. An activation type is DAO subclass which is passed as argument
     * to the {@link AlitheiaPlugin.run()} and
     * {@link AlitheiaPlugin.getResult()}} methods to trigger metric
     * calculation and result retrieval.
     * 
     * @return A list of DAObject subclasses
     */     
    List<Class<? extends DAObject>> getActivationTypes();
    
    /**
     * Get the plugin's configuration schema. 
     * @return A list of PluginConfiguration objects
     */
    List<PluginConfiguration> getConfigurationSchema();
    
    /**
     * Return a list of metric mnemonics that the metrics in this plugin
     * use. 
     * @return A list of metric dependencies for this plug-in
     */
    List<String> getMetricDependencies();
}
