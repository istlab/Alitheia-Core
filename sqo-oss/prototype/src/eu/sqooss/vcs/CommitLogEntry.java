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

import java.util.*;

/**
 * Represents a log entry containing comments, author and date/time
 * information
 *
 * If one of the fields of a commit log entry could be used as a
 * unique ID (the date is not a safe option, perhaps a
 * hash/combination of author and date could do) then the CommitLog
 * could hold a list of entries sorted by this ID to get a
 * chronologically ordered list.
 */
public class CommitLogEntry {

    /**
     * Information about the author who performed a commit
     */
    public String Author;

    /**
     * The comment logged by a commiter
     */
    public String Comment;

    /**
     * The date and time when the commit was performed
     */
    public Date Date;

    /**
     * The identifier of the revision / commit
     */
    public String Revision;

    /**
     * Constructs a new instance of the class
     * 
     * @param author The author who performed a commit
     * @param comment The comment logged by a commiter
     * @param date The date and time when the commit was performed
     * @param revision The identifier of the revision
     */
    public CommitLogEntry(String author, String comment, Date date,
	    String revision) {
	Author = author;
	Comment = comment;
	Date = date;
	Revision = (revision == null) ? "" : revision;
    }
}
