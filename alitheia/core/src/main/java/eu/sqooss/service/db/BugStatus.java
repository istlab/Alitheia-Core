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

import eu.sqooss.core.AlitheiaCore;

/**
 * States a bug resolution process can be into.
 * 
 * @assoc 1 - n Bug
 */
@Entity
@Table(name="BUG_STATUS")
public class BugStatus extends DAObject {
    
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="BUG_STATUS_ID")
	@XmlElement
	private long id; 
	
    /** The bug status */
	@Column(name="status")
    private String status;

    /** Bugs with this status*/
    @OneToMany(mappedBy="status", orphanRemoval=true)
    private Set<Bug> bugs;
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public BugStatus.Status getBugStatus() {
        return Status.fromString(getStatus());
    }
    
    public void setBugStatus(Status s) {
        this.status = s.toString();
    }
    
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
    
    /**
     * Encapsulates all states a bug can be in with a typesafe enum.
     * Resolution states have an 1:1 relationship with the
     * equivalent TDS classes.
     */
    public enum Status {
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
        CLOSED,
        /** The status is not known*/
        UNKNOWN;
        
        /**
         * 
         * @return The {@link Status} enum field or null
         */
        public static Status fromString(String status) {
            if (status == null)
                return null;
            if (status.equalsIgnoreCase("UNCONFIRMED"))
                return UNCONFIRMED;
            if (status.equalsIgnoreCase("NEW"))
                return NEW;
            if (status.equalsIgnoreCase("ASSIGNED"))
                return ASSIGNED;
            if (status.equalsIgnoreCase("REOPENED"))
                return REOPENED;
            if (status.equalsIgnoreCase("RESOLVED"))
                return RESOLVED;
            if (status.equalsIgnoreCase("VERIFIED"))
                return VERIFIED;
            if (status.equalsIgnoreCase("CLOSED"))
                return CLOSED;
            if (status.equalsIgnoreCase("UNKNOWN"))
                return UNKNOWN;
            return UNKNOWN;
        }
    }
    
    
    /**
     * Return or create and return the status code DB representation 
     * corresponding to the provided status code
     * @param s The status code to check for
     * @return A BugStatus DAO or null if an error occurred while creating
     * the status code line to the database
     */
    public static BugStatus getBugStatus(BugStatus.Status s) {
        return getBugStatus(s.toString(), true);
    }
    
    /**
     * Return or create and return the status code DB representation 
     * corresponding to the provided String
     * @param status The bug status code representation 
     * @param create If true, create a DB entry for the  
     * @return A BugStatus DAO or null when the DAO was not found 
     * and the create field was set to null or when an error occurred
     * while modifying the DB.
     */
    public static BugStatus getBugStatus(String status, boolean create) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("status", status);
        
        List<BugStatus> st = dbs.findObjectsByProperties(BugStatus.class,
                params);
        
        if (!st.isEmpty()) {
            return st.get(0);
        }
        
        if (!create) {
            return null;
        }
        
        if (Status.fromString(status) == null) {
            return null;
        }
        
        BugStatus bs = new BugStatus();
        bs.setStatus(status);
        
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
