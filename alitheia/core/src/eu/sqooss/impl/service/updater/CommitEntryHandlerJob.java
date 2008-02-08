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

package eu.sqooss.impl.service.updater;

import java.io.File;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.CommitEntry;
import eu.sqooss.service.tds.PathChangeType;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterException;

public class CommitEntryHandlerJob extends Job {

    private AlitheiaCore core;
    private DBService dbs;
    private Logger logger;
    private ProjectVersion pv;
    private String path;
    private PathChangeType changeType;

    CommitEntryHandlerJob(AlitheiaCore core, Logger logger)
            throws UpdaterException {
        if ((core == null) || (logger == null)) {
            throw new UpdaterException(
                    "The components required by the job are unavailable.");
        }

        this.dbs = core.getDBService();
        this.logger = logger;
    }

    public int priority() {
        return 1;
    }

    protected void run() {
        if (pv == null || path == null || (path.length() < 1)) {
            logger.error("The Job has not been initialised");
            this.setState(State.Error);
        }
        
        ProjectFile pf = new ProjectFile();
        File f = new File(path);
        pf.setName(f.getName());
        pf.setProjectVersion(pv);
        pf.setStatus(changeType.toString());
        dbs.addRecord(pf);
    }

    void init(ProjectVersion pv, String path, PathChangeType changeType) {
        this.pv = pv;
        this.path = path;
        this.changeType = changeType;
    }

}
