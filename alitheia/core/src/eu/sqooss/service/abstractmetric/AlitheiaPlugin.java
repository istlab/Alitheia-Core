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

import java.util.Collection;
import java.util.Date;

import eu.sqooss.lib.result.Result;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.util.Pair;


/**
 * Common metric plug-in related functionality. Must be implemented
 * by all metrics plug-ins. There are four areas of functionality covered
 * by this interface: metric metadata (about the metric itself),
 * measurement (applying the metric to something), lifecycle (installation
 * and removal) and configuration (of the metric, for future measurements).
 *
 * The metric metadata comprises name, description, author information
 * and dates installed; this is static in the metric.
 *
 * Measurement comprises two methods: run which actually performs a measure-
 * ment on some project artifact (which one depends on the type of DAObject
 * which is passed in) and getResult which returns the value obtained
 * by a previous measurement.
 *
 * Lifecycle management is done through three verbs: install, remove and
 * update. These do what is on the box and should modify the database
 * schemas as appropriate.
 *
 * Finally, configuration management is for settings that each plugin
 * may have. These are (name,type) and (name,value) pairs; the former
 * is the configuration schema, the latter the values in that schema.
 *
 * All metrics are bound to one or more of the following
 * project entities:
 *
 * <ul>
 *  <li>Project</li>
 *  <li>Project Version</li>
 *  <li>File Group</li>
 *  <li>File</li>
 * </ul>
 *
 * As a result, all metric implementations need to implement at least 2 interfaces:
 *
 *  <ul>
 *      <li>This interface</li>
 *      <li>One or more of the following interfaces, depending on the type of
 *      the entity the metric is bound to</li>
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
     */
    String getVersion();

    /**
     * Get information about the metric author
     */
    String getAuthor();

    /**
     * Get the date this version of the metric has been installed
     */
    Date getDateInstalled();

    /**
     * Get the metric name
     */
    String getName();

    /**
     * Get a free text description of what this metric calculates
     */
    String getDescription();

    /**
     * Generic "get results" function, it is specialised by sub-interfaces.
     *
     * @param o DAO whose type specifies the specialised sub-interface to use
     *          and whose value determines which result to get.
     * @return value of the measurement or null if there is no such measurement.
     * @throws MetricMismatchException if the DAO type is one not supported by
     *          this metric.
     */
    Result getResult(DAObject o)
        throws MetricMismatchException;

    /**
     * Generic run plug-in method. This method performs a measurement for
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
     * These are the types of configuration values that metrics can
     * support. This is used mostly for rendering and validation purposes.
     */
    public enum ConfigurationTypes {
        INTEGER,
        STRING,
        BOOLEAN
    } ;
}
