/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 Panos Louridas <louridas@aueb.gr>
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

import eu.sqooss.service.db.DBService;

import eu.sqooss.service.logging.Logger;

import eu.sqooss.service.scheduler.Scheduler;

import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterException;
import eu.sqooss.service.scheduler.Job;


/** 
 * Provides the entrypoint for updating bug-related data.
 * 
 * Construction-wise, it follows the Builder pattern as described by Joshua Block.
 * As we are not sure about the full set of parameters we will be able to use, we employ
 * this design pattern to enable us to work with "optional named" parameters.
 * 
 * @author Panos Louridas (louridas@aueb.gr)
 */
public class BugUpdater {
    
    private TDSService tds;

    private DBService dbs;

    private Scheduler scheduler;

    private Logger logger;

    public class Builder {        
        private TDSService tds;
        private DBService dbs;
        private Scheduler scheduler;
        private Logger logger;

        public Builder(String path, TDSService tds, DBService dbs, Scheduler scheduler, Logger logger) throws UpdaterException {
            if ((path == null) || (tds == null) || (dbs == null) || (scheduler == null) || (logger == null)) {
                throw new UpdaterException("The components required by the updater are unavailable");
            }
            this.tds = tds;
            this.dbs = dbs;
            this.scheduler = scheduler;
            this.logger = logger;
        }
        
        /*
         * Example of optional parameter:
         * public Builder foo(int val) {
         *     this.foo = val;
         *     return this;
         * }
         */
        
        public BugUpdater build() throws UpdaterException {
            return new BugUpdater(this);
        }
    }
    
    private BugUpdater(Builder builder) throws UpdaterException {
    }
    
    public void doUpdate() throws UpdaterException {	
    }
}
