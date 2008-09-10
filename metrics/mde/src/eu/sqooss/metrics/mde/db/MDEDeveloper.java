/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Paul J. Adams <paul.adams@siriusit.co.uk>
 * Copyright 2008 by Adriaan de Groot <groot@kde.org>
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

package eu.sqooss.metrics.mde.db;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.StoredProject;


/**
 * This is an auxialiary class for the MDE metric that stores
 * a starting timestamp for each developer as well as information
 * about the weeks (relative to a project) in which the developer
 * worked. 
 * 
 * @author adridg
 */
public class MDEDeveloper extends DAObject {
    /**
     * The developer we are adding information to. This is the
     * primary key of the table as well.
     */
    private Developer developer;
    /**
     * Timestamp of the first commit by this developer; after
     * that timestamp (date) the developer is considered part
     * of the total developer count for the project.
     */
    private long start;
    /**
     * Starting week relative to the start of a project for the developer.
     */
    private int start_week;
    /**
     * Which week was the developer most recently active? This is relative
     * to the starting week of the project.
     */
    private int active_week;
    /**
     * What is the length of service leading up to active_week? This
     * is the number of consecutive weeks (ending in active_week) that
     * the developer was active in the project.
     */
    private int service;

    public MDEDeveloper() {
        super();
    }

    /**
     * Convenience constructor that sets the developer.
     * @param d Developer this MDE Developer refers to
     */
    public MDEDeveloper(Developer d) {
        this();
        setDeveloper(d);
    }
    
    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }
    
    public long getStart() {
        return start;
    }
    
    public void setStart(long t){
        this.start = t;
    }

    public int getStartWeek() {
        return start_week;
    }
    
    public void setStartWeek(int i) {
        start_week = i;
    }
    
    public int getActiveWeek() {
        return active_week;
    }
    
    public void setActiveWeek(int i) {
        active_week = i;
    }
    
    public int getServiceTime() {
        return service;
    }
    
    public void setServiceTime(int i) {
        service = i;
    }
    
    /**
     * Convenience method to get the project that the developer
     * is working on.
     * 
     * @return StoredProject for this MDEDeveloper
     */
    public StoredProject getProject() {
        return developer.getStoredProject();
    }
    
    public static MDEDeveloper find(Developer d) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        HashMap<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("developer", d);
        
        List<MDEDeveloper> l = dbs.findObjectsByProperties(MDEDeveloper.class, 
                parameters);
        if ((null != l) && !l.isEmpty()) {
            return l.get(0);
        } else {
            return null;
        }
    }
}
