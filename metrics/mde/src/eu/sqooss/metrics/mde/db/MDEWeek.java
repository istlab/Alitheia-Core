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

import java.util.HashMap;
import java.util.List;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.StoredProject;


/**
 * This is an auxialiary class for the MDE metric that stores
 * a whether a developer is active in a given week.
 *
 * @author adridg
 */
public class MDEWeek extends DAObject {
    /**
     * The developer we are adding information to. This is the
     * primary key of the table as well.
     */
    private Developer developer;

    /**
     * Week that this record applies to.
     */
    private int week;

    /**
     * Was the developer active that week.
     */
    private boolean active;

    public MDEWeek() {
        super();
    }

    /**
     * Convenience constructor that sets the developer.
     * @param d Developer this MDE Developer refers to
     */
    public MDEWeek(Developer d, int week, boolean a) {
        this();
        this.developer = d;
        this.week = week;
        this.active = a;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int i) {
        this.week = i;
    }

    public boolean getActive() {
        return active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean i) {
        this.active = i;
    }

    /**
     * Convenience method for setting a week's data in one go,
     * separate from the constructor.
     */
    public void setStatus(int week, boolean a) {
        this.week = week;
        this.active = a;
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

    /**
     * Convenience method to look up a (developer,week ) pair
     * in the database and return the MDEWeek object for it,
     * or null if it is not in the database.
     * @param d Developer to look for
     * @param w Week number relative to the beginning of the project
     * @return MDEWeek object from the database or null if none
     */
    public static MDEWeek find(Developer d, int w) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        HashMap<String,Object> parameters = new HashMap<String,Object>(2);
        parameters.put("developer", d);
        parameters.put("week", w);

        List<MDEWeek> l = dbs.findObjectsByProperties(MDEWeek.class,
                parameters);
        if ((null != l) && !l.isEmpty()) {
            return l.get(0);
        } else {
            return null;
        }
    }
}
