/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

package eu.sqooss.service.metricactivator;

import eu.sqooss.core.AlitheiaCoreService;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.InvocationRule.ActionType;

/**
 * The MetricActivator service is responsible for kickstarting metric jobs
 * either after project metadata updates or
 */
public interface MetricActivator extends AlitheiaCoreService {

	/**
	 * Run a metric plug-in on a single resource object. Will not schedule
	 * a job if the metric is not of the same activation type as
	 * the DAO object
	 * 
	 * @param <T> The resource to run the metric on
	 * @param ap The plug-in to execute
	 */
	public <T extends DAObject> void runMetric(T resource, AlitheiaPlugin ap);
	
    /**
     * Runs all metrics that support the given activation type, on the
     * given project.
     *
     * @param sp The project on whose data the metric sync will be schedule
     * @param clazz resource type
     */
    public void syncMetrics(StoredProject sp, Class<? extends DAObject> clazz);

    /**
     * Run all plug-ins on the provided stored project
     *
     * @param sp The stored project to run the metrics on
     */
    public void syncMetrics(StoredProject sp);

    /**
     * Run the provided plug-in over all projects 
     *
     * @param sp The plug-in to run
     */
    public void syncMetrics(AlitheiaPlugin ap);
    
    /**
     * Synchronize a plug-in with the current state of the project
     *
     * @param m The plug to run on the project
     * @param sp The project DAO for the project whose state is to be 
     * synced with the results of the provided plugin
     */
    public void syncMetric(AlitheiaPlugin m, StoredProject sp);

    /**
     * Instructs the <code>MetricActivator<code> component, to load the list
     * of invocation rules available in the SQO-OSS database.
     * <br/>
     * In the special case when no rules can be found in the database,
     * this method will initialize the rule chain, by creating a database
     * record for its default rule i.e. the default chain policy.
     */
    public void initRules();

    /**
     * Reloads the invocation rule with the specified Id from the database.
     * When a rule with the given Id doesn't exist in the database, then it
     * is removed from the <code>MetricActivator</code>'s cache as well
     * (<i>if found in the cache</i>).
     * When such rule exists in the database, then its record in the cache is
     * replaced with the current database record.
     *
     * @param ruleId the invocation rule's Id
     */
    public void reloadRule(Long ruleId);

    public ActionType matchRules (AlitheiaPlugin ap, DAObject resource);
}
