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

package eu.sqooss.service.db;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import eu.sqooss.core.AlitheiaCore;

/**
 * Base class for describing all DAO, providing a simple id interface
 */
@XmlAccessorType(XmlAccessType.NONE)
public class DAObject {
    /**
     * The DAO instance ID, required by Hibernate
     */
    private long id;

    /**
     * Returns the unique ID assigned to this instance
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the unique instance ID to this instance
     *
     * @param id   The unique id to set for this instance
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * Create a DAO instance of the correct type for a given DAO id.
     * @param <T> A subclass of DAObject
     * @param id The id of the object to look for
     * @param type The type of the object to look for
     * @return 
     */
    public static <T extends DAObject> T loadDAObyId(long id, Class<T> type) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        return dbs.findObjectById(type, id);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
