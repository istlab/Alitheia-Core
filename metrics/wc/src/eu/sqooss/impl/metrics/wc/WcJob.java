/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
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

package eu.sqooss.impl.metrics.wc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Time;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AbstractMetricJob;
import eu.sqooss.service.db.Measurement;
import eu.sqooss.service.db.ProjectFile;

public class WcJob extends AbstractMetricJob {

    private ProjectFile pf;
    AbstractMetric parent = null;

    public WcJob(AbstractMetric owner, ProjectFile a) {
        super(owner);
        parent = owner;
        pf = a;
    }

    public int priority() {
        return 0xbeef;
    }
    
    public void run() {
        Measurement m = new Measurement();
        int lines = 0;

        File f = fds.getFile(pf);
        if (f != null) {
            try {
                log.info(
                        this.getClass().getName()
                        + " Measuring: "
                        + f.getAbsolutePath());
                LineNumberReader lnr = new LineNumberReader(new FileReader(f));
                while(lnr.readLine() != null) {
                    lines++;
                }
                lnr.close();
            } catch (FileNotFoundException e) {
                log.error(
                        this.getClass().getName()
                        + " Cannot open file: "
                        + f.getAbsolutePath());
            } catch (IOException e) {
                log.error(
                        this.getClass().getName()
                        + " IO Error ("
                        + e
                        + "): "
                        + f.getAbsolutePath());
            }
            
            // Create the measurement DAO and add it to the DB
            m.setMetric(parent.getSupportedMetrics().get(0));
            m.setProjectVersion(pf.getProjectVersion());
            /* NOTE: nanoTime() can not be directly used for creating a 
             *       Time object, use currentTimeMillis() instead
             */
            //m.setWhenRun(new Time(System.nanoTime()));
            m.setWhenRun(new Time(System.currentTimeMillis()));
            m.setResult(String.valueOf(lines));
            
            // Try to store the Measurement DAO into the DB
            db.addRecord(m);
        }
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
