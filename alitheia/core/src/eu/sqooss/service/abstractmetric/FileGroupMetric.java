/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 Georgios Gousios <gousiosg@gmail.com>
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

import java.util.List;

import eu.sqooss.lib.result.ResultEntry;
import eu.sqooss.service.db.FileGroup;
import eu.sqooss.service.db.Metric;
/**
 * A metric plug-in implements the <tt>FileGroupMetric</tt> interface to
 * indicate that its results are linked to the FileGroup table, and
 * consequently needs to be recalculated when the FileGroup has been updated
 */

public interface FileGroupMetric {

    /**
     * Run the metric to update the metric results when new versions
     * of the evaluated project are available.
     *
     * By default, the run method will start updating metric results from
     * <tt>a</tt> to the newest project version.
     *
     * @param The first new version DAO
     * @return True, if the metric run succeeded, false otherwise
     * @see eu.sqooss.service.db.FileGroup
     */
    void run(FileGroup a);

    /**
     * Return metric results for project version <tt>a</tt>
     *
     * @param metricTypeDAO
     * @return
     */
    List<ResultEntry> getResult(FileGroup a, Metric m);
}
