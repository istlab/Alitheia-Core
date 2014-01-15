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
 * The bug resolution priority.
 * 
 * @assoc 1 - n Bug
 */
@Entity
@Table(name="BUG_PRIORITY")
public class BugPriority extends DAObject {
    
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="BUG_PRIORITY_ID")
	@XmlElement
	private long id; 
	
	/** The bug priority */
	@Column(name="PRIORITY")
    private String priority;

	/**Bugs with this priority*/
	@OneToMany(mappedBy="priority", orphanRemoval=true)
	private Set<Bug> bugs;
	
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
    
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setPriority(String priority) {
		this.priority = priority;
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
         * @return The status state or null if null was given
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
    
    public void setBugs(Set<Bug> bugs) {
		this.bugs = bugs;
	}

	public Set<Bug> getBugs() {
		return bugs;
	}
}
