/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 by Adriaan de Groot <groot@kde.org>
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

package eu.sqooss.service.tds;

import eu.sqooss.service.tds.TDAccessor;

/**
 * The TDS service interface provides a way to retrieve and release
 * and configure the thin data access objects. A typical lifecycle
 * is as follows:
 *
 * - Check if there already is an accessor (optional); if there isn't
 *   this indicated that the project has not been requested recently.
 * - Request an accessor for the project. This may throw a variety of
 *   exceptions indicating resource or permissions problems (or that
 *   the project does not exist).
 * - If the accessor is returned, use its interface to get information
 *   from the project.
 * - When done, release the accessor.
 *
 * The accessor pool is limited by available connections and threads for
 * pulling information out of the file store, so do remember to free
 * accessors once you are done.
 *
 * @see TDAccessor
 */
public interface TDSService {
    /**
     * Check that the given project exists in the TDS.
     */
    public boolean projectExists( long id );

    /**
     * Check if the given project ID has an accessor object ready.
     * This may be used to suppress requests for the accessor if
     * it is not in use yet.
     */
    public boolean accessorExists( long id );

    /**
     * Retrieve the accessor object for the given project @p id .
     */
    public TDAccessor getAccessor( long id );

    /**
     * Release your claim on the accessor.
     */
    public void releaseAccessor( TDAccessor tda );

    /**
     * Add an accessor for a project (usually a new project just
     * added to the system, in preparation for syncing it).
     *
     * @param id   Project ID
     * @param name Project name (informational only)
     * @param bts  Bug tracker URL
     * @param mail URL for email access
     * @param scm  URL for repository
     *
     * @see eu.sqooss.service.db.StoredProject
     */
    public void addAccessor( long id, String name, String bts, String mail, String scm );
}

// vi: ai nosi sw=4 ts=4 expandtab

