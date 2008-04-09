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
import eu.sqooss.service.fds.MailingListEvent;
import eu.sqooss.service.fds.RepositoryEvent;
import eu.sqooss.service.fds.BugDBEvent;

import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.DAOException;

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

    private SortedSet<RepositoryEvent> getScmTimeLine(Calendar from, Calendar to) {
        SortedSet<RepositoryEvent> result = new TreeSet<RepositoryEvent>();
        
        final long begin = from.getTimeInMillis();
        final long end = to.getTimeInMillis();

        List<ProjectVersion> versions = project.getProjectVersions();
        for(ProjectVersion version : versions) {
            if (version.getTimestamp() < begin || version.getTimestamp() > end)
                continue;
            result.add( new RepositoryEvent(version.getTimestamp(), version) );
        }

        return result;
    }
    
    private SortedSet<MailingListEvent> getMailTimeLine(Calendar from, Calendar to) {
        SortedSet<MailingListEvent> result = new TreeSet<MailingListEvent>();
        
        final Date begin = from.getTime();
        final Date end = to.getTime();
        
        try {
            List<MailingList> lists = MailingList.getListsPerProject(project);
            for (MailingList list : lists) {
                // get all messages
                List<MailMessage> messages = list.getMessages();
                for (MailMessage message : messages)
                {
                    if (message.getSendDate().before(begin) ||
                        message.getSendDate().after(end))
                        continue;
                    result.add( new MailingListEvent( message.getSendDate().getTime(), message ) );
                }
            }
        } catch(DAOException ex) {
            // Will be removed when the DB code is refactored
        }

        return result;
    }

    private SortedSet<BugDBEvent> getBugTimeLine(Calendar from, Calendar to) {
        SortedSet<BugDBEvent> result = new TreeSet<BugDBEvent>();

        final Date begin = from.getTime();
        final Date end = to.getTime();

        List<Bug> bugs = Bug.getProjectBugs(project);
        for (Bug bug : bugs) {
            // PENDING: Use creation date or last update date ?
            if ( bug.getCreationTS().before(begin) ||
                 bug.getCreationTS().after(end) )
                continue;
            result.add( new BugDBEvent( bug.getCreationTS().getTime(), bug ) );
        }

        return result;
    }
 
    // Interface Timeline
    /** {@inheritDoc} */
    public SortedSet<ProjectEvent> getTimeLine(Calendar from, Calendar to, EventType rt) {
        SortedSet<ProjectEvent> result = new TreeSet<ProjectEvent>();

        if (rt==EventType.SCM || rt==EventType.ALL) {
            result.addAll(getScmTimeLine(from, to));
        }
        if( rt==EventType.MAIL || rt==EventType.ALL) {
            result.addAll(getMailTimeLine(from, to));
        }
        if( rt==EventType.BTS || rt==EventType.ALL) {
            result.addAll(getBugTimeLine(from, to ));
        }

        return result;
    }
}



// vi: ai nosi sw=4 ts=4 expandtab
