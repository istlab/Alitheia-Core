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

package eu.sqooss.webui.datatype;

import eu.sqooss.ws.client.datatypes.WSShortBug;

/**
 * This class represents a tiny wrapper for a single bug entry associated
 * with a project that has been evaluated by the SQO-OSS framework. It
 * encapsulates and provides access to just two of the bug meta-data fields
 * i.e. assigned <tt>DAO Id</tt> and creation timestamp.
 * 
 * @author Boryan Yotov, <tt>(ProSyst Software GmbH)</tt>
 */
public class ShortBug {

    /*
     * Bug entry's specific meta-data
     */
    protected long id;
    protected long timestamp;

    /**
     * Creates a new a <code>ShortBug</code> instance, and initializes it
     * with the information provided from the given
     * <code>WSShortBug</code> wrapper object.
     * 
     * @param wrapper a <code>WSShortBug</code> bug wrapper
     */
    public ShortBug(WSShortBug wrapper) {
        if (wrapper != null) {
            id = wrapper.getId();
            timestamp = wrapper.getTimestamp();
        }
    }

    /**
     * Gets the <tt>DAO Id</tt> of the stored bug entry.
     * 
     * @return The <tt>DAO Id</tt> of the stored bug entry.
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the timestamp (creation time) of the stored bug entry.
     * 
     * @return The timestamp of the stored bug entry.
     */
    public long getTimestamp() {
        return timestamp;
    }
}
