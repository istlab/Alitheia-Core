/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 Georgios Gousios <gousiosg@aueb.gr>
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

package eu.sqooss.metrics.abstractmetric;

import java.util.Date;
import eu.sqooss.service.db.DAObject;

/**
 * Common metric plug-in related functionality. Must be implemented
 * by all metrics plug-ins.
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
 *      the entity this metric is bound to</li> 
 *      <ul>
 *          <li>{@link StroredProjectMetric}</li>
 *          <li>{@link ProjectVersionMetric}</li>
 *          <li>{@link ProjectFileMetric}</li>
 *          <li>{@link FileGroupMetric}</li>
 *      </ul>
 *  </ul>
 *  
 */
public interface Metric {

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
     * Generic "get results" function, it is specialised by sub-interfaces 
     * @param o 
     * @return
     */
    MetricResult getResult(DAObject o);
    
    /**
     * Generic run plug-in method
     * @param o The DAO that gets passed to the plug-in in order to run it
     */
    
    public void run(DAObject o);
       
    /**
     * After installing a new version of the metric, try to 
     * update the results. The metric may opt to partially
     * or fully update its results tables or files.
     *  
     * @return True, if the update succeeded, false otherwise
     */
    boolean update();

    /**
     * Perform maintenance operations when installing a new or updated
     * version of the metric
     * 
     * @return True, if the installation succeeded, false otherwise
     */
    boolean install();

    /**
     * Free the used resources and clean up on metric removal
     * 
     * @return True, if the installation succeeded, false otherwise
     */
    boolean remove();

}
