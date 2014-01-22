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
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

/**
 * The bug resolution severity.
 * 
 *  @assoc 1 - n Bug
 */
@Entity
@Table(name="BUG_SEVERITY")
public class BugSeverity extends DAObject {
    
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="BUG_SEVERITY_ID")
	@XmlElement
	private long id; 
	
   
	/** The bug severity */
	@Column(name="severity")
    private String severity;
	
	/** Bugs with this severity*/
	@OneToMany(mappedBy="severity", orphanRemoval=true)
	private Set<Bug> bugs;

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public Severity getBugSeverity() {
        return Severity.fromString(getSeverity());
    }
    
    public void setBugseverity(Severity s) {
        this.severity = s.toString();
    }
    
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
    
    /**
     * Encapsulates all available severity states a bug can be in with a 
     * typesafe enum. severity states have an 1:1 relationship with the
     * equivalent TDS states.
     */
    public enum Severity {
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
        ENHANCEMENT,
        /** An known severity level, used to match */
        UNKNOWN;
        
        /**
         * Get a status state from a string.
         * @return The status state or null if could not be found
         */
        public static Severity fromString(String s) {
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
            if (s.equalsIgnoreCase("UNKNOWN"))
                return UNKNOWN;
            return UNKNOWN;
        }
    }    
    
    /**
     * Return or create and return the severity code DB representation 
     * corresponding to the provided severity code
     * @param s The severity code to check for
     * @return A Bugseverity DAO or null if an error occurred while creating
     * the severity code line to the database
     */
    public static BugSeverity getBugseverity(DBService dbs, Severity s) {
        return getBugSeverity(dbs, s.toString(), true);
    }
    
    /**
     * Return or create and return the severity code DB representation 
     * corresponding to the provided String
     * 
     * @param severity The bug severity code representation 
     * @param create If true, create a DB entry for the provided severity 
     * code
     * @return A BugSeverity DAO or null when the DAO was not found 
     * and the create field was set to null or when an error occurred
     * while modifying the DB.
     */
    public static BugSeverity getBugSeverity(DBService dbs, String severity, boolean create) {
        //DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("severity", severity);
        
        List<BugSeverity> st = dbs.findObjectsByProperties(BugSeverity.class,
                params);
        
        if (!st.isEmpty()) {
            return st.get(0);
        }
        
        if (!create) {
            return null;
        }
        
        if (Severity.fromString(severity) == null) {
            return null;
        }
        
        BugSeverity bs = new BugSeverity();
        bs.setSeverity(severity);
        
        if (!dbs.addRecord(bs))
            return null;
        
        return bs;
    }

	public void setBugs(Set<Bug> bugs) {
		this.bugs = bugs;
	}

	public Set<Bug> getBugs() {
		return bugs;
	}
}
