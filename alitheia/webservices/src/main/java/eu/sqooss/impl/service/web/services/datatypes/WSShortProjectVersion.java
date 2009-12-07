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

import eu.sqooss.service.db.ProjectVersion;

/**
 * This class partially wraps a single project version <tt>DAO</tt> i.e. just
 * the <tt>Id</tt> of the version <tt>DAO</tt> and the version's timestamp.
 * <br/>
 * In you need a full version <tt>DAO</tt> wrapper, then please have a look at
 * the <code>WSProjectVersion</code> class.
 * @see eu.sqooss.impl.service.web.services.datatypes.WSProjectVersion
 * 
 * @author Evgeni Grigorov, <tt>(ProSyst Software GmbH)</tt>
 * @author Boryan Yotov, <tt>(ProSyst Software GmbH)</tt>
 */
public class WSShortProjectVersion {

    private long id;
    private long timestamp;

    /**
     * Returns the <tt>Id</tt> of the wrapped version <tt>DAO</tt>.
     * @return The <tt>Id</tt> of the wrapped project version <tt>DAO</tt>.
     */
    public long getId() {
        return id;
    }

    /**
     * Initializes the <tt>DAO Id</tt> of this project version wrapper.
     * @param id the <tt>Id</tt> of the wrapped project version <tt>DAO</tt>
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return The timestamp of the wrapped project version.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Initializes the timestamp of this project version wrapper.
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * This method instantiates and initializes a new
     * <code>WSShortProjectVersion</code> object by wrapping the given project
     * version <tt>DAO</tt>.
     * 
     * @param dao a project version <tt>DAO</tt>
     * 
     * @return The new <code>WSShortProjectVersion</code> object
     */
    public static WSShortProjectVersion getInstance(ProjectVersion dao) {
        if (dao == null) return null;
        try {
            WSShortProjectVersion wrapper = new WSShortProjectVersion();
            wrapper.setId(dao.getId());
            wrapper.setTimestamp(dao.getTimestamp());
            return wrapper;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * This method returns an array containing all of the elements in the
     * given list of <code>ProjectVersion</code> <tt>DAO</tt>s.
     *  
     * @param daoList a list of <code>ProjectVersion</code> <tt>DAO</tt>s
     * 
     * @return An array of <code>WSShortProjectVersion</code> objects,
     *   or <code>null</code> upon an empty list or incompatible
     *   <tt>DAO</tt>s.
     */
    public static WSShortProjectVersion[] asArray(List<?> daoList) {
        WSShortProjectVersion[] result = null;
        if (daoList != null) {
            result = new WSShortProjectVersion[daoList.size()];
            ProjectVersion dao;
            WSShortProjectVersion wrapper;
            for (int i = 0; i < result.length; i++) {
                try {
                    dao = (ProjectVersion) daoList.get(i);
                } catch (ClassCastException e) {
                    return null;
                }
                wrapper = WSShortProjectVersion.getInstance(dao);
                if (wrapper == null)
                    return null;
                result[i] = wrapper;
            }
        }
        return result;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
