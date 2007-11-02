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

package eu.sqooss.service.fds;

import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.ProjectRevision;

import eu.sqooss.service.fds.Checkout;

/**
 * The FDS (Fat Data Service) is the part of the raw data access layer that handles
 * caching and coordination of raw project data. It is primarily concerned with handling
 * (and updating) checkouts of project sources, not with access to mail or the BTS.
 */
public interface FDSService {
    /**
     * This maintains (and caches) a checkout of a given project in a
     * given revision; remember to release the checkout when you're done
     * with it. As long as a checkout is held by someone, the revisions of
     * files in the checkout will not change, but once the checkout is
     * released by all, it may be updated to some new revision.
     *
     * @param id   Project ID
     * @param name Project name (informative only)
     * @param r    Revision (project state) to get
     *
     * @return Checkout object. Remember to release it later.
     */
    public Checkout getCheckout( long id, String projectName, ProjectRevision r )
        throws InvalidRepositoryException,
               InvalidProjectRevisionException;

    /**
     * Release a previously obtained checkout.
     *
     * @param c Checkout obtained from previous call to getCheckout()
     */
    public void releaseCheckout( Checkout c )
        throws InvalidRepositoryException;
}

// vi: ai nosi sw=4 ts=4 expandtab

