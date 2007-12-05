/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 by Adriaan de Groot <groot@kde.org>
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

package eu.sqooss.impl.service.webadmin;

import java.util.List;
import java.util.LinkedList;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.TDSService;

/**
 * The stuffer class is a runnable that takes all of the access data
 * stored in the DB and feeds it to the TDS in order to get the TDS
 * up and running. This supercedes the tds.conf.
 */

class Stuffer implements Runnable {
    private DBService db;
    private Logger logger;
    private TDSService tds;

    public Stuffer( DBService db, Logger l, TDSService t ) {
        this.db = db;
        this.logger = l;
        this.tds = t;

        logger.info("Stuffer ready.");
    }

    private void bogusStuffer() {
        logger.info("Stuffing bogus project into TDS");
        // Some dorky default project so the TDS is not empty
        // for the test later.
        tds.addAccessor(1, "KPilot", "", "",
            "http://cvs.codeyard.net/svn/kpilot/" );
    }

    public void run() {
        logger.info("Now running stuffer.");

        if (db != null) {
            List l = db.doHQL("from StoredProject");

            if (l.isEmpty()) {
                bogusStuffer();
                // Next for loop is empty as well
            }
            for(Object o : l) {
                StoredProject p = (StoredProject) o;
                tds.addAccessor(p.getId(), p.getName(), p.getBugs(), p.getMail(), p.getRepository());
            }
        } else {
            bogusStuffer();
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

