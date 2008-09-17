/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 by Paul J. Adams <paul.adams@siriusit.co.uk>
 * Copyright 2008 by Athens University of Economics and Business
 *     Author Adriaan de Groot <groot@kde.org>
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
 * This is an auxiliary class for the MDE metric that stores
 * a starting timestamp for each developer.
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
     *
     * The start time is set only once.
     */
    private long start;

    /**
     * Starting week relative to the start of the project for the developer.
     * This is measured in weeks from the beginning of the project,
     * as calculated by MDEImplementation.convertToWeekOffset.
     */
    private int start_week;


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
