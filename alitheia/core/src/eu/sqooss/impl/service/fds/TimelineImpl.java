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

import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.fds.BugDBEvent;
import eu.sqooss.service.fds.MailingListEvent;
import eu.sqooss.service.fds.ProjectEvent;
import eu.sqooss.service.fds.RepositoryEvent;
import eu.sqooss.service.fds.Timeline;

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
        
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        StringBuilder query = new StringBuilder("select pv ");
        query.append("from ProjectVersion pv ");
        query.append("where pv.timestamp < :paramTo ");
        query.append("and pv.timestamp > :paramFrom ");
        query.append("and pv.project = :paramProject ");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("paramTo", end);
        params.put("paramFrom", begin);
        params.put("paramProject", project);
        
        List<ProjectVersion> versions = (List<ProjectVersion>) dbs.doHQL(query.toString(), params);
        
        for(ProjectVersion version : versions) {
        	result.add( new RepositoryEvent(version.getTimestamp(), version) );
        }
        return result;
    }
    
    private SortedSet<MailingListEvent> getMailTimeLine(Calendar from, Calendar to) {
        SortedSet<MailingListEvent> result = new TreeSet<MailingListEvent>();
        
        final Date begin = from.getTime();
        final Date end = to.getTime();
        
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        StringBuilder query = new StringBuilder("select mm ");
        query.append("from MailMessage mm ");
        query.append("where mm.arrivalDate < :paramTo ");
        query.append("and mm.arrivalDate > :paramFrom ");
        query.append("and mm.list = :paramList ");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("paramTo", end);
        params.put("paramFrom", begin);        
        
        Set<MailingList> lists = project.getMailingLists();
        if (lists != null) {
			for (MailingList list : lists) {
				params.put("paramlist", list);
				List<MailMessage> messages = (List<MailMessage>) dbs.doHQL(query.toString(), params);

				for (MailMessage message : messages) {
					result.add(new MailingListEvent(
							message.getSendDate().getTime(), message));
				}
			}
		}

        return result;
    }

    private SortedSet<BugDBEvent> getBugTimeLine(Calendar from, Calendar to) {
        SortedSet<BugDBEvent> result = new TreeSet<BugDBEvent>();

        final Date begin = from.getTime();
        final Date end = to.getTime();

        DBService dbs = AlitheiaCore.getInstance().getDBService();
        StringBuilder query = new StringBuilder("select b ");
        query.append("from Bug b ");
        query.append("where b.creationTS < :paramTo ");
        query.append("and b.creationTS > :paramFrom ");
        query.append("and b.project = :paramProject ");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("paramTo", end);
        params.put("paramFrom", begin);
        params.put("paramProject", project);
        List<Bug> bugs = (List<Bug>) dbs.doHQL(query.toString(), params);
        
        for (Bug bug : bugs) {
			result.add(new BugDBEvent(bug.getCreationTS().getTime(), bug));
		}
        return result;
    }
 
    // Interface Timeline
    /** {@inheritDoc} */
    public SortedSet<ProjectEvent> getTimeLine(Calendar from, Calendar to, ResourceType rt) {
        return getTimeLine( from, to, EnumSet.of(rt) );
    }

    public SortedSet<ProjectEvent> getTimeLine(Calendar from, Calendar to, EnumSet<ResourceType> rts) {
        SortedSet<ProjectEvent> result = new TreeSet<ProjectEvent>();

        if (rts.contains(ResourceType.SCM)) {
            result.addAll(getScmTimeLine(from, to));
        }
        if(rts.contains(ResourceType.MAIL)) {
            result.addAll(getMailTimeLine(from, to));
        }
        if(rts.contains(ResourceType.BTS)) {
            result.addAll(getBugTimeLine(from, to ));
        }

        return result;
    }
}



// vi: ai nosi sw=4 ts=4 expandtab
