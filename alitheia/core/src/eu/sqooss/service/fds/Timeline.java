/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.service.fds;

import java.util.Calendar;
import java.util.SortedSet;

/**
 * A timeline  is a chronological view of the events that change a 
 * project's state. 
 * 
 * A project's state at any given moment <tt>t</tt> in
 * time is defined by the set <tt>S(t)={s,e,b}(t)</tt> where:
 * 
 * <ul>
 *      <li><tt>s(t)</tt> = Latest commit to the repository prior to <tt>t</tt></li>
 *      <li><tt>e(t)</tt> = Latest commit to the mail archives prior to <tt>t</tt></li>
 *      <li><tt>b(t)</tt> = Latest entry (bug report, action on a bug, etc) to
 *              the bug management database prior to <tt>t</tt></li>
 * </ul>
 *
 * Events are sorted by their timestamp, with second accuracy. In the case 
 * where 2 events occur at the same second, the class tries its best to 
 * reconcile microsecond-accurate repository timestamps with 
 * second-accurate email and bug database timestamps. 
 */
public interface Timeline {
    
    /* Yes, we have this all over the place */
    public enum EventType {
        SVN,
        MAIL,
        BTS, 
        ALL
    }
    
    /**
     * Return a timeline of events for project p, starting from and including 
     * events occurred at timestamp <tt>from</tt> ending up and including
     * events occurred at timestamp <tt>to</tt> for the specified EventType.
     * If the resource type parameter is ALL, then a timeline of all events
     * is returned
     *  
     * @param from Timeline start
     * @param to Timeline end
     * @param rt The resource type to include in the event timeline. If 
     *  equals to EventType.ALL then event types for all resources are returned.
     * @return A sorted list of TimeLineEntries
     */
    SortedSet<ProjectEvent> getTimeLine(Calendar from, Calendar to, EventType rt);
}


//vi: ai nosi sw=4 ts=4 expandtab
