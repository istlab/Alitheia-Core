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

package eu.sqooss.service.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.core.AlitheiaCore;

/**
 * The bug resolution priority.
 * 
 * @assoc 1 - n Bug
 */
public class BugPriority extends DAObject {
    
    /** The bug priority */
    private String priority;

    public String getPriority() {
        return priority;
    }

    public void setpriority(String priority) {
        this.priority = priority;
    }
    
    public Priority getBugPriority() {
        return Priority.fromString(getPriority());
    }
    
    public void setBugPriority(Priority s) {
        this.priority = s.toString();
    }
    
    /**
     * Encapsulates all available priority states a bug can be in with a 
     * typesafe enum. priority states have an 1:1 relationship with the
     * equivalent TDS states.
     */
    public enum Priority {
        /** Low resolution priority.*/
        LOW, 
        /** Medium resolution priority. */
        MEDIUM,
        /** High resolution priority.*/
        HIGH, 
        /**All other priorities*/
        UNKNOWN;
        
        /**
         * Get a status state from a string.
         * @return The status state or null if could not be found
         */
        public static Priority fromString(String s) {
            if (s == null)
                return null;                
            
            if (s.equalsIgnoreCase("LOW"))
                return LOW;
            if (s.equalsIgnoreCase("MEDIUM"))
                return MEDIUM;
            if (s.equalsIgnoreCase("HIGH"))
                return HIGH;
            if (s.equalsIgnoreCase("UNSPECIFIED"))
                return UNKNOWN;
            return UNKNOWN;
        }
    }    
    
    /**
     * Return or create and return the priority code DB representation 
     * corresponding to the provided priority code
     * @param s The priority code to check for
     * @return A Bugpriority DAO or null if an error occurred while creating
     * the priority code line to the database
     */
    public static BugPriority getBugPriority(Priority s) {
        if (s == null)
            return null;
        return getBugPriority(s.toString(), true);
    }
    
    /**
     * Return or create and return the priority code DB representation 
     * corresponding to the provided String
     * 
     * @param priority The bug priority code representation 
     * @param create If true, create a DB entry for the provided priority 
     * code
     * @return A BugPriority DAO or null when the DAO was not found 
     * and the create field was set to null or when an error occurred
     * while modifying the DB.
     */
    public static BugPriority getBugPriority(String priority, boolean create) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("priority", priority);
        
        List<BugPriority> st = dbs.findObjectsByProperties(BugPriority.class,
                params);
        
        if (!st.isEmpty()) {
            return st.get(0);
        }
        
        if (!create) {
            return null;
        }
        
        if (Priority.fromString(priority) == null) {
            return null;
        }
        
        BugPriority bs = new BugPriority();
        bs.setpriority(priority);
        
        if (!dbs.addRecord(bs))
            return null;
        
        return bs;
    }
}
