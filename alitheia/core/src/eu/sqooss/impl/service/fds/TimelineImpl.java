/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Christoph Schleifenbaum <christoph@kdab.net>
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

package eu.sqooss.impl.service.fds;

import eu.sqooss.service.fds.Timeline;
import eu.sqooss.service.fds.ProjectEvent;
import eu.sqooss.service.fds.RepositoryEvent;

import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.ProjectVersion;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.Calendar;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.List;

/**
 * The TimelineImpl implements the Timeline interface. It represents a
 * chronological view of the events that change a project's state.
 */
class TimelineImpl implements Timeline {
   
    private StoredProject project;

    public TimelineImpl(StoredProject project) {
        this.project = project;
    }

    // Interface Timeline
    /** {@inheritDoc} */
    public SortedSet<ProjectEvent> getTimeLine(Calendar from, Calendar to, EventType rt) {
        SortedSet<ProjectEvent> result = new TreeSet<ProjectEvent>();

        final long begin = (long)(from.getTime().getTime());
        final long end = (long)(to.getTime().getTime());

        if (rt==EventType.SVN || rt==EventType.ALL) {
            // get all versions
            List<ProjectVersion> versions = project.getProjectVersions();
            for(ProjectVersion version : versions) {
                // compare the version's timestop to \a from ond \a to
                if (version.getTimestamp() < begin || version.getTimestamp() > end)
                    continue;
                // TODO: How to I create the URL?
                URL url = null;
                try {
                    url = new URL("repo://...");
                } catch(MalformedURLException e) {
                }

                result.add(new RepositoryEvent(version.getTimestamp(), url, version));
            }
        }
        if( rt==EventType.MAIL || rt==EventType.ALL) {
            // TODO
        }
        if( rt==EventType.BTS || rt==EventType.ALL) {
            // TODO
        }

        return result;
    }
}



// vi: ai nosi sw=4 ts=4 expandtab

