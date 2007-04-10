/*
 * Copyright (c) Members of the SQO-OSS Collaboration, 2007
 * All rights reserved by respective owners.
 * See http://www.sqo-oss.eu/ for details on the copyright holders.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the SQO-OSS project nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package eu.sqooss.vcs;

import java.util.Iterator;
import java.util.Vector;

/**
 * Represents a commit log, implemented as an iterable collection of
 * {@link CommitLogEntry} objects.
 * 
 */
public class CommitLog implements Iterable<CommitLogEntry> {

    /* TODO:perhaps arrange entries by date */
    private Vector<CommitLogEntry> entries;

    private Revision start, end;

    /**
     * Constructs a new instance of the class that contains the entries
     * created by the commits between the two revisions
     * 
     * @param start The first revision
     * @param end The last revision
     */
    public CommitLog(Revision start, Revision end) {
        if ((start == null) || (end == null)) {
            throw new IllegalArgumentException();
        }

        this.start = start;
        this.end = end;
        entries = new Vector<CommitLogEntry>();
    }

    /**
     * Gets the first revision
     * @return The first {@link Revision} contained in the log
     */
    public Revision getStart() {
        return start;
    }

    /**
     * Gets the last revision
     * @return The last {@link Revision} contained in the log
     */
    public Revision getEnd() {
        return end;
    }

    /**
     * Provides access to the entries contained in the log
     * @return A Vector of {@link CommitLogEntry} objects contained in the log
     */
    Vector<CommitLogEntry> getEntries() {
        return entries;
    }

    /**
     * Adds a new entry to the log
     * @param entry The {@link CommitLogEntry} to add to the log
     */
    public void add(CommitLogEntry entry) {
        entries.add(entry);
    }

    /**
     * Removes an entry from the log
     * @param entry The {@link CommitLogEntry} to remove from the log
     * @return A boolean value indicating whether the entry was removed
     */
    public boolean remove(CommitLogEntry entry) {
        return entries.remove(entry);
    }

    /**
     * Removes all entries from the log
     */
    public void clear() {
        entries.clear();
    }

    /**
     * Returns the number of entries contained in the log
     * @return an integer indicating the number of entries contained in the log
     */
    public int size() {
        return entries.size();
    }

    /**
     * Gets an iterator for enumerating through the entries contained in the
     * log
     */
    public Iterator<CommitLogEntry> iterator() {
        return entries.iterator();
    }

    /**
     * Iterates through the entries contained in the log and prints them on the
     * standard output
     */
    public void printCommitLog() {
        for (CommitLogEntry entry: entries) {
            System.out.println(entry);
        }
    }

}
