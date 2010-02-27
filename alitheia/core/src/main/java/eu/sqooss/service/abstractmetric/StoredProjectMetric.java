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

import java.util.List;

import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.StoredProject;

/**
 * A metric plug-in implements the <tt>StoredProjectMetric</tt> interface to
 * indicate that its results are linked to the StoredProject table, and
 * consequently needs to be recalculated when a change that affects the
 * whole project occurs (e.g. addition or deletion of a project). 
 * The metric might also just
 * run on StoredProject changes (but store results against another entity), 
 * in which case it should implement the {@link #run(StoredProject)} method 
 * normally and provide an implementation of 
 * {@link #getResult(StoredProject, Metric)} that just returns an empty list.
 */
public interface StoredProjectMetric extends AlitheiaPlugin {

    /**
     * Run the metric to update the metric results on the project
     * indicated by the argument DAO.
     * 
     * @param The DAO to calculate results against
     * @throws AlreadyProcessingException
     *                 When another metric is calculating results on the same
     *                 DAO when this metric was activated. This exception should
     *                 not be caught (or if caught it should be re-thrown) as
     *                 correct excecution of metrics depends on it.
     *                 
     * @see eu.sqooss.service.db.StoredProject
     */
    void run(StoredProject a) throws AlreadyProcessingException ;

    /**
     * Return metric results for StoredProject <tt>s</tt>. Will not trigger a
     * calculation if the result is not in the DB.
     * 
     * @param s DAO to retrieve a stored measurement for.
     * @param m The metric whose result should be retrieved.
     * @return The metric result or an empty result list if the result was not
     *         calculated or the requested metric is not implemented by this
     *         plugin.
     */
    List<ResultEntry> getResult(StoredProject s, Metric m);
}
