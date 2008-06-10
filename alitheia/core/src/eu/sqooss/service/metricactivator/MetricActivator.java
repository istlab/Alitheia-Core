/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by the Georgios Gousios <gousiosg@gmail.com>
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

import java.util.SortedSet;

import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.InvocationRule.ActionType;

/**
 * The MetricActivator service is responsible for kickstarting metric jobs 
 * either after project metadata updates or  
 */
public interface MetricActivator {

    /**
     * Runs all metrics that support the given project's resource type,
     * on all project resources (of the same resource type) pointed
     * by the specified resource IDs list. 
     * 
     * @param clazz resource type 
     * @param objectIDs resource IDs list
     */
    public <T extends DAObject> void runMetrics(Class<T> clazz, SortedSet<Long> objectIDs);
    
    /**
     * Synchronize metric results for all metrics for a specific project
     * 
     * @param clazz
     * @param sp
     */
    public <T extends DAObject> void syncMetrics(StoredProject sp);
    
    /**
     * 
     * 
     * @param m
     * @param sp
     */
    public void syncMetric(AlitheiaPlugin m, StoredProject sp);
    
    /**
     * 
     * @param m
     * @param sp
     * @return
     */
    public ProjectVersion getLastAppliedVersion(AlitheiaPlugin m, StoredProject sp);

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
