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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The bug interface represents a single bug from a bug tracker (whichever
 * that may be) in the most abstract form possible.
 */
public class BTSEntry {
    
    public String bugID;
    public Date creationTimestamp;
    public String shortDescr;
    public BugSeverity severity;
    public BugPriority priority;
    public BugStatus state;
    public BugResolution resolution;
    
    public String product;
    public String component;
    
    public String reporter;
    public String assignee;
    
    public List<BTSEntryComments> commentslist;
    
    public BTSEntry() {
        commentslist = new ArrayList<BTSEntryComments>();
    }
    
    public enum BugSeverity {
        BLOCKER, CRITICAL, MAJOR, NORMAL, MINOR, TRIVIAL, ENHANCEMENT;
        
        public static BugSeverity fromString(String s) {
            if (s.equalsIgnoreCase("BLOCKER"))
                return BLOCKER;
            if (s.equalsIgnoreCase("CRITICAL"))
                return CRITICAL;
            if (s.equalsIgnoreCase("MAJOR"))
                return MAJOR;
            if (s.equalsIgnoreCase("NORMAL"))
                return NORMAL;
            if (s.equalsIgnoreCase("MINOR"))
                return MINOR;
            if (s.equalsIgnoreCase("TRIVIAL"))
                return TRIVIAL;
            if (s.equalsIgnoreCase("ENHANCEMENT"))
                return ENHANCEMENT;
            return null;
        }
    }
    
    public enum BugPriority {
        LOW, MEDIUM, HIGH;
        
        public static BugPriority fromString(String s) {
            if (s.equalsIgnoreCase("LOW"))
                return LOW;
            if (s.equalsIgnoreCase("MEDIUM"))
                return MEDIUM;
            if (s.equalsIgnoreCase("HIGH"))
                return HIGH;
            return null;
        }
    }
    
    public enum BugStatus {
        UNCONFIRMED, NEW, ASSIGNED, VERIFIED, CLOSED, REOPENED;
        public static BugStatus fromString(String s) {
            if (s.equalsIgnoreCase("UNCONFIRMED"))
                return UNCONFIRMED;
            if (s.equalsIgnoreCase("NEW"))
                return NEW;
            if (s.equalsIgnoreCase("ASSIGNED"))
                return ASSIGNED;
            if (s.equalsIgnoreCase("VERIFIED"))
                return VERIFIED;
            if (s.equalsIgnoreCase("CLOSED"))
                return CLOSED;
            if (s.equalsIgnoreCase("REOPENED"))
                return REOPENED;
            return null;
        }
    }
    
    public enum BugResolution {
        FIXED, INVALID, WONTFIX, DUPLICATE, WORKSFORME, MOVED;
        
        public static BugResolution fromString(String s) {
            if (s.equalsIgnoreCase("FIXED"))
                return FIXED;
            if (s.equalsIgnoreCase("INVALID"))
                return INVALID;
            if (s.equalsIgnoreCase("WONTFIX"))
                return WONTFIX;
            if (s.equalsIgnoreCase("DUPLICATE"))
                return DUPLICATE;
            if (s.equalsIgnoreCase("WORKSFORME"))
                return WORKSFORME;
            if (s.equalsIgnoreCase("MOVED"))
                return MOVED;
            return null;
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

