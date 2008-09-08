/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.service.db;

/**
 * Instances of this class represent a measurement made against a
 * specific file, as stored in the database
 */
public class ProjectFileMeasurement extends MetricMeasurement {
    /**
     * The file against which the measurement was made
     */ 
    private ProjectFile projectFile;

    public ProjectFileMeasurement() {
        // Nothing to do here
        super();
    }

    /**
     * Convenience constructor to avoid having to call three methods
     * to set up sensible values in a measurement.
     * 
     * @param m Metric this measurement is from
     * @param f File this measurement is for
     * @param value (String) value representation of the measurement
     */
    public ProjectFileMeasurement(Metric m, ProjectFile f, String value) {
        super();
        setMetric(m);
        setProjectFile(f);
        setResult(value);
    }
    
    public ProjectFile getProjectFile() {
        return projectFile;
    }

    public void setProjectFile(ProjectFile pf) {
        this.projectFile = pf;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
