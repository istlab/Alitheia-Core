/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.impl.service.web.services.datatypes;

import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectVersionMeasurement;

public class WSMetricMeasurement {

    private long id;
    private long targetId;
    private WSMetric metric;
    private long whenRun;
    private String result;

    private WSMetricMeasurement(ProjectFileMeasurement measurement) {
        this.id = measurement.getId();
        this.targetId = measurement.getProjectFile().getId();
        this.metric = new WSMetric(measurement.getMetric());
        this.whenRun = measurement.getWhenRun().getTime();
        this.result = measurement.getResult();
    }

    private WSMetricMeasurement(ProjectVersionMeasurement measurement) {
        this.id = measurement.getId();
        this.targetId = measurement.getProjectVersion().getId();
        this.metric = new WSMetric(measurement.getMetric());
        this.whenRun = measurement.getWhenRun().getTime();
        this.result = measurement.getResult();
    }

    public static WSMetricMeasurement createInstance(Object metricMeasurement) {
        if (metricMeasurement instanceof ProjectFileMeasurement) {
            return new WSMetricMeasurement((ProjectFileMeasurement)metricMeasurement);
        } else if (metricMeasurement instanceof ProjectVersionMeasurement) {
            return new WSMetricMeasurement((ProjectVersionMeasurement)metricMeasurement);
        } else {
            return null;
        }
    }

    public long getId() {
        return id;
    }

    public String getResult() {
        return result;
    }

    public WSMetric getMetric() {
        return metric;
    }

    public long getWhenRun() {
        return whenRun;
    }

    public long getTargetId() {
        return targetId;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
