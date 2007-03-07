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

public class CommitLog implements Iterable<CommitLogEntry> {

    /*TODO:perhaps arrange entries by date*/
    private Vector<CommitLogEntry> entries;

    private Revision start, end;

    public CommitLog(Revision start, Revision end) {
	if ((start == null) || (end == null)) {
	    throw new IllegalArgumentException();
	}

	this.start = start;
	this.end = end;
	entries = new Vector<CommitLogEntry>();
    }

    public Revision getStart() {
	return start;
    }

    public Revision getEnd() {
	return end;
    }

    Vector<CommitLogEntry> getEntries() {
	return entries;
    }

    public void add(CommitLogEntry entry) {
	entries.add(entry);
    }

    public boolean remove(CommitLogEntry entry) {
	return entries.remove(entry);
    }

    public void clear() {
	entries.clear();
    }

    public int size() {
	return entries.size();
    }

    public Iterator<CommitLogEntry> iterator() {
	return entries.iterator();
    }
}
