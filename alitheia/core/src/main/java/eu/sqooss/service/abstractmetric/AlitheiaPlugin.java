/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
 *                 Athens, Greece.
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
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.metricactivator.MetricActivationException;

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
 *   <li>Mail Message</li>
 *   <li>Mailing List</li>
 *   <li>Bug</li>
 *   <li>Developer</li>
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
     * Get the metric result, without triggering a metric recalculation if
     * the result is not present.
     * If the result was not calculated yet, the result set is empty. If you
     * want to trigger the calculation to get a result, use getResult() instead.
     *
     * @param o DAO whose type specifies the specialized sub-interface to use
     *          and whose value determines which result to get.
     * @return l A list of metrics
     * @return value of the measurement or null if there is no such measurement.
     * @throws MetricMismatchException if the DAO type is one not supported by
     *          this metric.
     */
    List<Result> getResultIfAlreadyCalculated(DAObject o, List<Metric> l)
    	throws MetricMismatchException;

    /**
     * Get a metric result. 
     * If the result was not calculated yet, the plugin's run method is called,
     * and the request waits until the run method returns.
     * If you don't want this behavior, use getResultIfAlreadyCalculated()
     * instead.
     *
     * @param o DAO whose type specifies the specialized sub-interface to use
     *          and whose value determines which result to get.
     * @return l A list of metrics
     * @return value of the measurement or null if there is no such measurement.
     * @throws MetricMismatchException if the DAO type is one not supported by
     *          this metric.
     * @throws AlreadyProcessingException to signify that the provided DAO is 
     * currently locked for processing by another thread.  
     * @throws Exception All exceptions initiated by the errors in code 
     * included in implemenations of those classes.           
     */
    List<Result> getResult(DAObject o, List<Metric> l)
        throws MetricMismatchException, AlreadyProcessingException, Exception;

    /**
     * Get the description objects for all metrics supported by this plug-in
     * as found in the database.
     *
     * @return the list of metric descriptors, or null if none
     */
    List<Metric> getAllSupportedMetrics();

    /**
     * Get all metrics that are bound to the provided activation type. 
     *
     * @return the list of metric DAOs for the provided activation type, empty
     * if the plug-in does not support the provided activation type
     */
    List<Metric> getSupportedMetrics(Class<? extends DAObject> activationType);
    
    /**
     * This method performs a measurement for
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
     * Note that even if you use (parallel running) jobs in your jobs, the
     * metric's run method needs to block until the result is calculated.
     *
     * @param o The DAO that gets passed to the plug-in in order to run it
     * @throws MetricMismatchException if the DAO is of an unsupported type.
     * @throws AlreadyProcessingException to signify that the provided DAO is 
     * currently locked for processing by another thread. 
     * @throws Exception Any other exception initiated from the plugin code
     */
    void run(DAObject o) throws MetricMismatchException, 
        AlreadyProcessingException, Exception;

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
     * Clean results on project removal
     * 
     * @param sp The DAO to be used as reference when cleaning up results.
     * @return True, if the cleanup succeeded, false otherwise
     */
    boolean cleanup(DAObject sp);
    
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
     * @return A set of DAObject subclasses
     */
    Set<Class<? extends DAObject>> getActivationTypes();

    /**
     * Get the activation type that corresponds to the activation type which 
     * the metric result is stored. 
     * 
     * @param m - The metric for which to search for an activation type
     * @return A list of subclasses of DAObject (a.k.a activation types).
     */
    List<Class<? extends DAObject>> getMetricActivationTypes (Metric m);
    
    /**
     * Retrieves the list of configuration properties for this plug-in.
     * <br/>
     * Metric plug-ins can use the <code>AbstractMetric</code>'s
     * <code>addConfigEntry</code> and <code>removeConfigEntry</code> methods
     * to manage their own configuration schema.
     *
     * @return The set of the existing configuration properties for
     *   this plug-in. This may be an empty list if no configuration is
     *   needed or if the plug-in is not active.
     */
    Set<PluginConfiguration> getConfigurationSchema();
    
    /**
     * Metric mnemonics for the metrics required to be present for this 
     * plugin to operate. 
     * 
     * @return A, possibly empty, set of metric mnemonics. 
     */
    Set<String> getDependencies();
    
    /**
     * Get a list of object ids for the database entities to run the metric
     * on, ordered by activation type. This method essentially allows the plugin
     * to specify a custom processing order for metadata entities to be processed
     * by metrics. The default execution order is specified 
     * 
     */
    Map<MetricType.Type, SortedSet<Long>> getObjectIdsToSync(StoredProject sp, Metric m) 
    	throws MetricActivationException;
}
