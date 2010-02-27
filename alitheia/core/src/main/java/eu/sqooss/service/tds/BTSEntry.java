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

package eu.sqooss.service.tds;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The bug interface represents a single bug from a bug tracker (whichever
 * that may be) in the most abstract form possible. This class has all 
 * fields public to avoid excessive use of getters/setters. The class is
 * only intented to act as a Data Transfer Object.
 */
public class BTSEntry {
    
    public String bugID;
    public Date creationTimestamp;
    public Date latestUpdateTimestamp;
    public String shortDescr;
    public BugSeverity severity;
    public BugPriority priority;
    public BugStatus state;
    public BugResolution resolution;
    
    public String product;
    public String component;
    
    public String reporter;
    public String assignee;
    
    public List<BTSEntryComment> commentslist;
    public List<BTSEntryAttachement> attachementlist;
    
    public BTSEntry() {
        commentslist = new ArrayList<BTSEntryComment>();
        attachementlist = new ArrayList<BTSEntryAttachement>();
    }
    
    public enum BugSeverity {
        /** The bug must be fixed or else...*/
        BLOCKER, 
        /** The bug is critical, affecting the quality of the system/data*/
        CRITICAL, 
        /** The bug is significant and should be fixed asap.*/
        MAJOR, 
        /** The bug is just another bug.*/
        NORMAL, 
        /** The bug does not affect the typical operation of the software*/
        MINOR, 
        /** A trivial to fix bug, a typo in some doc or similar.*/
        TRIVIAL, 
        /** A user request to enhance the software rather than am actual bug.*/
        ENHANCEMENT;
        /**
         * Get a status state from a string.
         * @return The status state or null if could not be found
         */
        public static BugSeverity fromString(String s) {
            if (s == null)
                return null;
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
        /** Low resolution priority.*/
        LOW, 
        /** Medium resolution priority. */
        MEDIUM, NORMAL,
        /** High resolution priority.*/
        HIGH;
        
        /**
         * Get a status state from a string.
         * @return The status state or null if could not be found
         */
        public static BugPriority fromString(String s) {
            if (s == null)
                return null;
            if (s.equalsIgnoreCase("LOW"))
                return LOW;
            if (s.equalsIgnoreCase("MEDIUM") || s.equals("NORMAL"))
                return MEDIUM;
            if (s.equalsIgnoreCase("HIGH"))
                return HIGH;
            return null;
        }
    }
    
    /**
     * Encapsulates all possible bug status states.
     */
    public enum BugStatus { 
        /** Not sure if is a bug, or in voting process */
        UNCONFIRMED,
        /** A new bug */
        NEW,
        /** The bug has been assigned to a developer */
        ASSIGNED,
        /** Bug was closed, but fix was not acceptable */
        REOPENED,
        /** The problem was fixed, pending verification */
        RESOLVED,
        /** The solution is accepted */
        VERIFIED,
        /** The bug is resolved */
        CLOSED;
        
        /**
         * Get a status state from a string.
         * @return The status state or null if could not be found
         */
        public static BugStatus fromString(String s) {
            if (s == null)
                return null;
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
            if (s.equalsIgnoreCase("RESOLVED"))
                return RESOLVED;
            return null;
        }
    }
    
    /**
     * Encapsulates all possible bug resolution states.
     */
    public enum BugResolution {
        /** The bug is fixed */
        FIXED, 
        /** The bug description is not correct or the bug */
        INVALID,
        /** The bug will not be fixed due to administrative decision*/
        WONTFIX,
        /** The bug is duplicate of another bug */
        DUPLICATE,
        /** The bug might be a bug but did not appear on the tester's 
         * workstation */
        WORKSFORME,
        /** The bug has been moved to another bug description */
        MOVED;
        
        /**
         * Get a resolution state from a string.
         * @return The resolution state or null if could not be found
         */
        public static BugResolution fromString(String s) {
            if (s == null)
                return null;
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
    
    /**
     * Bug revision comment.
     */
    public class BTSEntryComment {
        public String commentAuthor;
        public Date commentTS;
        public String comment;
        
        public BTSEntryComment() {   
        }
    }
    
    /**
     * Bug attachement
     */
    public class BTSEntryAttachement {
        public Date date;
        public String description;
        public String type;
        
        public BTSEntryAttachement() {
        }
    }

}

// vi: ai nosi sw=4 ts=4 expandtab

