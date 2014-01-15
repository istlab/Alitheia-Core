/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.plugins.tds.svn;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import eu.sqooss.plugins.tds.scm.SCMCommitLog;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.Revision;

/**
 * A simplistic implementation of the CommitLog interface.
 */
public class SVNCommitLogImpl extends SCMCommitLog {
    private LinkedList<Revision> entries;

    public SVNCommitLogImpl() {
        entries = new LinkedList<Revision>();
    }

    public List<Revision> getEntries() {
        return entries;
    }

    // Interface methods
    /**{@inheritDoc}}*/
    public Revision first() {
        if (entries.size() < 1) {
            return null;
        }
        
        return entries.getFirst();
    }
    
    /**{@inheritDoc}}*/
    public Revision last() {
        if (entries.size() < 1) {
            return null;
        }
        
        return entries.getLast();
    }
    
    /**{@inheritDoc}}*/
    public int size() {
        return entries.size();
    }

    /**{@inheritDoc}}*/
    public Iterator<Revision> iterator() {
        return entries.iterator();
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

