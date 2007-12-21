/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 Georgios Gousios <gousiosg@gmail.com>
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
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.fds.FDSService;

public abstract class ProductivityMetricJob extends Job {

	protected AlitheiaCore core = null;
    protected TDSService tds = null;
    protected FDSService fds = null;
    protected DBService db = null;
    protected Logger log;

    private ServiceReference serviceRef;

    public ProductivityMetricJob(BundleContext bc, Logger log) {
        this.log = log;

        serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) bc.getService(serviceRef);

        if (core != null) {
            log.info("Got Core Service!");
        } else {
            log.error("Didn't get Core Service");
        }
        
        tds = core.getTDSService();

        if (tds != null) {
            log.info("Got TDS Service!");
        } else {
            log.error("Didn't get TDS Service");
        }
        
        fds = core.getFDSService();

        if (fds != null) {
            log.info("Got FDS Service!");
        } else {
            log.error("Didn't get FDS Service");
        }
        
        db = core.getDBService();
        
        if (db != null) {
            log.info("Got DB Service!");
        } else {
            log.error("Didn't get DB Service");
        }
    }

    protected void run() throws Exception {
        log.info(this.getClass().getName() + ": Nothing to do");
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
