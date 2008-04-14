/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.service.abstractmetric;

import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.tds.TDSService;

public class DefaultUpdateJob extends Job {

    StoredProject sp;
    protected LogManager logService = null;
    protected Logger log = null;
    protected AlitheiaCore core = null;
    protected TDSService tds = null;
    protected FDSService fds = null;
    protected DBService db = null;
    private AbstractMetric owner;
    
    public DefaultUpdateJob (AbstractMetric owner, StoredProject sp) {

        ServiceReference serviceRef = null;
        serviceRef = owner.bc.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) owner.bc.getService(serviceRef);
        logService = core.getLogManager();

        if (logService != null) 
            log = logService.createLogger(Logger.NAME_SQOOSS_METRIC);

        if (log == null)
            System.out.println("ERROR: Got no logger");

        tds = core.getTDSService();

        if (tds == null) 
            log.error("Didn't get TDS Service");

        fds = core.getFDSService();

        if (fds == null) 
            log.error("Didn't get FDS Service");

        db = core.getDBService();

        if (db == null) 
            log.error("Didn't get DB Service");
        
        this.sp = sp;
    }
    
    @Override
    public int priority() {
        return 0xada;
    }

    @Override
    protected void run() throws Exception {
        log.info("Updating metric " + owner.getName() + " for project " + sp.getName());
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

