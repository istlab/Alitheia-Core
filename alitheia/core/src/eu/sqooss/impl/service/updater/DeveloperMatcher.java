/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2009 - Organization for Free and Open Source Software,  
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
package eu.sqooss.impl.service.updater;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;

/**
 * Bug updater. Reads data from the TDS and updates the bug metadata
 * database. 
 */
public class DeveloperMatcher extends Job {

    private StoredProject sp;
    private UpdaterServiceImpl upd;
    private Logger log;
    private DBService dbs;
    
    
    public DeveloperMatcher(StoredProject sp, UpdaterServiceImpl upd, Logger l) {
        this.sp = sp;
        this.upd = upd;
        this.log = l;
        dbs = AlitheiaCore.getInstance().getDBService();
    }
    
    @Override
    public int priority() {
        return 3;
    }

    @Override
    protected void run() throws Exception {
        dbs.startDBSession();
        
        String query = "from Developer d where d.storedProject = :sp" ;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sp", sp);
        List<Developer> devs = (List<Developer>) dbs.doHQL(query, params, true);
        
        for (Developer d : devs) {
            
        }
    }
}
