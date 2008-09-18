/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Adriaan de Groot <groot@kde.org>
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

/**
 * The bug interface represents a single bug from a bug tracker (whichever
 * that may be) in the most abstract form possible. The lowest common
 * denominator resolves to an ID, a reporter name and a severity. The
 * severity is an uninterpreted integer, so do not assume that larger
 * numbers are "worse" or "better".
 */
public class BTSEntry {
    
    public String bugID;
    public Date creationTimestamp;
    public String shortDescr;
    public BugSeverity severity;
    public BugPriority priority;
    public BugState state;
    public BugResolution resolution;
    
    public String product;
    public String component;
    
    public String reporter;
    public String assignee;
    
    public List<BTSEntryComments> commentslist;
    
    public enum BugSeverity {
        BLOCKER, CRITICAL, MAJOR, NORMAL, MINOR, TRIVIAL, ENHANCEMENT
    }
    
    public enum BugPriority {
        LOW, MEDIUM, HIGH
    }
    
    public enum BugState {
        UNCONFIRMED, NEW, ASSIGNED, VERIFIED, CLOSED, REOPENED
    }
    
    public enum BugResolution {
        FIXED, INVALID, WONTFIX, DUPLICATE, WORKSFORME, MOVED
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

