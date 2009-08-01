/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Adriaan de Groot <groot@kde.org>
 *
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
 * A representation of an entry in the commit log of an SCM system. 
 *
 */
public interface CommitEntry {
    
    /**
     * Get the project revision / commit hash for the commit entry 
     */
    Revision getRevision();
    
    /**
     * Get the username of the person that performed the commit 
     * 
     */
    String getAuthor();
    
    /**
     * Get the message attached to the commit
     */
    String getMessage();
    
    /**
     * Get the date of the commit
     */
    Date getDate();
    
    /**
     * Get a set of paths that changed by the commit
     */
    Set<String> getChangedPaths();
    
    /**
     * Get the modification types that were performed on each changed path
     * by the commit
     */
    Map<String, PathChangeType> getChangedPathsStatus();
    
    /**
     * Get a list of copy operations that took place in this revision
     */
    List<CommitCopyEntry> getCopyOperations();

    /**
     * Get a string representation of the commit
     */
    String toString();
}

// vi: ai nosi sw=4 ts=4 expandtab

