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
 * States a bug resolution process can be into.
 * 
 *  @assoc 1 - n Bug
 */
@Entity
@Table(name="BUG_RESOLUTION")
public class BugResolution extends DAObject {
    
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="BUG_RESOLUTION_ID")
	@XmlElement
	private long id; 
	
    /** The bug resolution */
	@Column(name="RESOLUTION")
    private String resolution;

	/** Bugs in this resolution state*/
	@OneToMany(mappedBy="resolution", orphanRemoval=true)
    private Set<Bug> bugs;
    
    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
    
    public Resolution getBugResolution() {
        return Resolution.fromString(getResolution());
    }
    
    public void setBugResolution(Resolution s) {
        this.resolution = s.toString();
    }
    
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
    
    /**
     * Encapsulates all available resolution states a bug can be in with a 
     * typesafe enum. Resolution states have an 1:1 relationship with the
     * equivalent TDS states.
     */
    public enum Resolution {
        /** The bug is fixed */
        FIXED, 
        /** The bug description is not correct or the bug */
        INVALID,
        /** The bug will not be fixed due to administrative decision*/
        WONTFIX,
        /** The bug is duplicate of another bug */
        DUPLICATE,
        /** The bug might be a bug but did not appear on the tester's workstation */
        WORKSFORME,
        /** The bug has been moved to another bug description */
        MOVED,
        /** The bug has not been resolved yet*/
        UNKNOWN;
        
        public static Resolution fromString(String s) {
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
            if (s.equalsIgnoreCase("UNKNOWN"))
                return UNKNOWN;
            /*Default value*/
            return UNKNOWN;
        }
    }    
    
    /**
     * Return or create and return the resolution code DB representation 
     * corresponding to the provided resolution code
     * @param s The resolution code to check for
     * @return A BugResolution DAO or null if an error occurred while creating
     * the resolution code line to the database
     */
    public static BugResolution getBugResolution(DBService dbs, BugResolution.Resolution s) {
        return getBugResolution(dbs, s.toString(), true);
    }
    
    /**
     * Return or create and return the resolution code DB representation 
     * corresponding to the provided String
     * 
     * @param resolution The bug resolution code representation 
     * @param create If true, create a DB entry for the provided resolution 
     * code
     * @return A BugResolution DAO or null when the DAO was not found 
     * and the create field was set to null or when an error occurred
     * while modifying the DB.
     */
    public static BugResolution getBugResolution(DBService dbs, String resolution, boolean create) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("resolution", resolution);
        
        List<BugResolution> st = dbs.findObjectsByProperties(BugResolution.class,
                params);
        
        if (!st.isEmpty()) {
            return st.get(0);
        }
        
        if (!create) {
            return null;
        }
        
        if (Resolution.fromString(resolution) == null) {
            return null;
        }
        
        BugResolution bs = new BugResolution();
        bs.setResolution(resolution);
        
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
