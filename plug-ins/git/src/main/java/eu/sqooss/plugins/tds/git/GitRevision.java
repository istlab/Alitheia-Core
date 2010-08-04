/*
 * Copyright 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

package eu.sqooss.plugins.tds.git;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.sqooss.service.tds.CommitCopyEntry;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.PathChangeType;
import eu.sqooss.service.tds.Revision;

/**
 * An implementation of the Revision interface for Git
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 *
 */
public class GitRevision implements Revision {

    private String id;
    private Date date;
    
    public GitRevision(String id, Date date) {
        this.id = id;
        this.date = date;
    }
    
    @Override
    public int compareTo(Revision other) throws InvalidProjectRevisionException {
        if (!(other instanceof GitRevision))
            throw new InvalidProjectRevisionException(other.getUniqueId(), GitRevision.class);
        GitRevision othergit = (GitRevision) other;
        if (this.date.getTime() == othergit.date.getTime())
            return 0;
        
        if (this.date.getTime() > othergit.date.getTime())
            return 1;
        else 
            return -1;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String getUniqueId() {
        return id;
    }

    @Override
    public String getAuthor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getChangedPaths() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, PathChangeType> getChangedPathsStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CommitCopyEntry> getCopyOperations() {
        // TODO Auto-generated method stub
        return null;
    }
}
