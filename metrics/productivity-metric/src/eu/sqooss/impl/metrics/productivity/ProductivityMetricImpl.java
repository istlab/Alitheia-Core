/* This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 Georgios Gousios <gousiosg@gmail.com>
 * 
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

package eu.sqooss.impl.metrics.productivity;

import org.osgi.framework.BundleContext;

import eu.sqooss.lib.result.Result;
import eu.sqooss.metrics.productivity.ProductivityMetric;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

public class ProductivityMetricImpl extends AbstractMetric implements
        ProductivityMetric {

    private static final long serialVersionUID = 1L;

    public ProductivityMetricImpl(BundleContext bc) {
        super(bc);
    }

    public boolean install() {
        return super.install();
    }

    public boolean remove() {
        return false;
    }

    public boolean update() {
        return false;
    }

    public boolean delete(StoredProject a) {
        return false;
    }

    public Result getResult(StoredProject a) {
        return null;
    }

    public void run(StoredProject a) {
    }

    public boolean delete(ProjectVersion a) {
        return false;
    }

    public Result getResult(ProjectVersion a) {
        return null;
    }

    public boolean run(ProjectVersion a, ProjectVersion b) {
        return false;
    }

    public void run(ProjectVersion a) {
        run(a,a);
    }
    public Object selfTest() {

        return null;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
