/*
 * Copyright 2008 - Organization for Free and Open Source Software,
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

package eu.sqooss.impl.service.web.services.datatypes;

import java.util.List;

import eu.sqooss.service.db.Bug;

/**
 * This class partially wraps a single bug entry <tt>DAO</tt> i.e. just
 * the <tt>Id</tt> of the bug <tt>DAO</tt> and the creation timestamp.
 * <br/>
 * In you need a full bug <tt>DAO</tt> wrapper, then please have a look at
 * the <code>WSBug</code> class.
 * @see eu.sqooss.impl.service.web.services.datatypes.WSBug
 * 
 * @author Evgeni Grigorov, <tt>(ProSyst Software GmbH)</tt>
 * @author Boryan Yotov, <tt>(ProSyst Software GmbH)</tt>
 */
public class WSShortBug {

    private long id;
    private long timestamp;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * This method instantiates and initializes a new <code>WSShortBug</code>
     * object by wrapping the given bug <tt>DAO</tt>.
     * 
     * @param dao a bug <tt>DAO</tt>
     * 
     * @return The new <code>WSShortBug</code> object
     */
    public static WSShortBug getInstance(Bug dao) {
        if (dao == null) return null;
        try {
            WSShortBug wrapper = new WSShortBug();
            wrapper.setId(dao.getId());
            wrapper.setTimestamp(dao.getCreationTS().getTime());
            return wrapper;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * This method returns an array containing all of the elements in the
     * given list of <code>Bug</code> <tt>DAO</tt>s.
     *  
     * @param daoList a list of <code>Bug</code> <tt>DAO</tt>s
     * 
     * @return An array of <code>WSShortBug</code> objects,
     *   or <code>null</code> upon an empty list or incompatible
     *   <tt>DAO</tt>s.
     */
    public static WSShortBug[] asArray(List<?> daoList) {
        WSShortBug[] result = null;
        if (daoList != null) {
            result = new WSShortBug[daoList.size()];
            Bug dao;
            WSShortBug wrapper;
            for (int i = 0; i < result.length; i++) {
                try {
                    dao = (Bug) daoList.get(i);
                } catch (ClassCastException e) {
                    return null;
                }
                wrapper = WSShortBug.getInstance(dao);
                if (wrapper == null)
                    return null;
                result[i] = wrapper;
            }
        }
        return result;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
