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

package eu.sqooss.service.fds;

import java.net.URL;

import eu.sqooss.service.db.DAObject;

/**
 * A generic data transfer object holding references to 
 * resources describing a project event.  
 * 
 */
public abstract class ProjectEvent implements Comparable<ProjectEvent> {
    
    /**
     * Second-accurate timestamp
     */
    protected long timestamp;
    
    /**
     * REST-like resource access. A rough example format for
     * each one of the supported resources is the following.
     * <pre>
     *   repo://project/<revision-number>/path/to/file-or-dir
     *   mail://project/mailinglist/year/month/date/messageid
     *   bts://project/report-id
     * </pre>
     */
    protected URL resourceURL;

    /**
     * Database record about the event.
     */
    protected DAObject associatedDAO;
    
    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return the resourceURL
     */
    public URL getResourceURL() {
        return resourceURL;
    }
    
    /**
     * @return an integer representing the "priority" of the event.
     * This value is used to further sort events with identical timestamps.
     */
    public abstract int eventPriority();
    
    /**
     * Compare events using the following discriminants in order :
     * timestamp -> priority -> URL
     * @return -1/0/1 if the event is more prioritary/equal/less prioritary than other
     */
    public int compareTo(ProjectEvent other)
    {
        if ( getTimestamp() == other.getTimestamp() ) {
            if ( eventPriority() == other.eventPriority() ) {
                // The URL embeds the identifier, and it should be the only
                // difference between the two project events at this point
                return getResourceURL().toString().compareTo( 
                        other.getResourceURL().toString() );
            }
            return eventPriority() > other.eventPriority() ? 1 : -1;
        }
        return getTimestamp() > other.getTimestamp() ? 1 : -1;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
