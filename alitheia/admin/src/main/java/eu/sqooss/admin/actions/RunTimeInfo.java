/*
 * Copyright 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.admin.actions;

import eu.sqooss.admin.AdminActionBase;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.scheduler.SchedulerStats;

public class RunTimeInfo extends AdminActionBase {

    private static final String descr = "Returns misc runtime information";

    public RunTimeInfo() {
        super();
    }

    @Override
    public String mnemonic() {
        return "rti";
    }

    @Override
    public String descr() {
        return descr;
    }

    @Override
    public void execute() throws Exception {
        super.execute();
        try {
            SchedulerStats s = AlitheiaCore.getInstance().getScheduler()
                    .getSchedulerStats();
            result.put("sched.jobs.failed", s.getFailedJobs());
            result.put("sched.jobs.wait", s.getWaitingJobs());
            result.put("sched.jobs.finished", s.getFinishedJobs());
            result.put("sched.threads.idle", s.getIdleWorkerThreads());
            result.put("sched.threads.total", s.getWorkerThreads());
        } catch (Exception e) {
            error(e);
        }
        finished();
    }
}
