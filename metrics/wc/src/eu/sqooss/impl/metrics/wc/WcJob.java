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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Time;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AbstractMetricJob;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectFile;

public class WcJob extends AbstractMetricJob {

    // DAO of the project file that has to be measured
    private ProjectFile pf;

    // Reference to the metric that created this job
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
        
        //We do not support directories
        if(pf.getIsDirectory()) {
            return;
        }
        
        // Retrieve the content of the selected project file
        byte[] content = fds.getFileContents(pf);
        if (content != null) {
            // Create an input stream from the project file's content
            ByteArrayInputStream in = new ByteArrayInputStream(content);
            try {
                log.info(
                        this.getClass().getName()
                        + " Measuring: "
                        + pf.getName());

                // Measure the number of lines in the project file
                LineNumberReader lnr =
                    new LineNumberReader(new InputStreamReader(in));
                int lines = 0;
                while(lnr.readLine() != null) {
                    lines++;
                }
                lnr.close();

                // Create the measurement DAO
                // TODO: What to do if this plug-in has registered more that
                //       one metric. Create a separate Measurement for all
                //       of them ?
                if (!parent.getSupportedMetrics().isEmpty()) {
                    Metric metric = parent.getSupportedMetrics().get(0);
                    ProjectFileMeasurement m = new ProjectFileMeasurement();
                    m.setMetric(metric);
                    m.setProjectFile(pf);
                    m.setWhenRun(new Time(System.currentTimeMillis()));
                    m.setResult(String.valueOf(lines));
                    
                    // Try to store the Measurement DAO into the DB
                    db.addRecord(m);
                    
                    // Check for a first time evaluation of this metric
                    // on this project
                    parent.markEvaluation (
                            metric,
                            pf.getProjectVersion().getProject());
                }
            } catch (IOException e) {
                log.error(
                        this.getClass().getName()
                        + " IO Error <"
                        + e
                        + "> while measuring: "
                        + pf.getName());
            }
        }
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
