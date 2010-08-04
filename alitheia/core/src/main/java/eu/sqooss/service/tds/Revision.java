/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.service.tds;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Revision denotes a revision of a project; revisions may be created from
 * dates or from SCM unique identifiers. In most SCM implementations, a Revision
 * represents a commit. Depending on the underlying SCM, a revision may require
 * resolution (i.e. matching the revision sting to a revision date or
 * vice-versa) prior to being return to client code. All revisions returned to
 * client code are guaranteed to be validated.
 */
public interface Revision {
    
    /**
     * Get that date associated to this revision.
     * 
     * @return The revision date, or null if the revision has not been 
     * validated.
     */
    public Date getDate();

    /**
     * Get the ID that uniquely identifies a revision in the repository.
     */
    public String getUniqueId();
    
    /**
     * Get the username of the person that performed the commit 
     * 
     */
    public String getAuthor();
    
    /**
     * Get the message attached to the commit
     */
    public String getMessage();
    
    /**
     * Get a set of paths that changed by the commit
     */
    public Set<String> getChangedPaths();
    
    /**
     * Get the modification types that were performed on each changed path
     * by the commit
     */
    public Map<String, PathChangeType> getChangedPathsStatus();
    
    /**
     * Get a list of copy operations that took place in this revision
     */
    public List<CommitCopyEntry> getCopyOperations();
    
    /**
     * Return human-readable representation of this ProjectRevision.
     */
    public String toString();
    
    /**
     * Compare a revision to another revision. This method follows the
     * Comparable interface semantics.
     * 
     * @param o The revision to compare against
     * @return 0 if the two revisions are the same. >0 if the provided
     *  revision is newer (for the underlying repository's definition of
     *  newer) that this revision. <0 otherwise.
     * @exception InvalidProjectRevisionException If the underlying type of
     * the provided revision is not the same as this type
     */
    public int compareTo(Revision o) throws InvalidProjectRevisionException;
}