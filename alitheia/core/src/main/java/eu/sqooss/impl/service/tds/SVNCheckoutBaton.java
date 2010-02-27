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

package eu.sqooss.impl.service.tds;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.ISVNReporter;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;

import eu.sqooss.service.logging.Logger;

/**
 * This class implements the reporter baton interface required
 * by SVNKit for checkout and export and update operations.
 * The implementation always reports an empty tree, regardless
 * of the files present in the filesystem under the local path
 * for this reporter. This may cause additional load on the SVN
 * server.
 *
 * Based on the SVNKit examples.
 */
public class SVNCheckoutBaton implements ISVNReporterBaton {
    private long sourceRevision;
    private long targetRevision;
    public static Logger logger;

    public SVNCheckoutBaton(long revision) {
        sourceRevision = 0;
        targetRevision = revision;
    }

    public SVNCheckoutBaton(long src, long dst) {
        sourceRevision = src;
        targetRevision = dst;
    }

    public void report(ISVNReporter reporter)
        throws SVNException {
        try {
            if (sourceRevision == 0) {
                // If we are coming from r.0, then the checkout
                // directory is empty -- that's what that true
                // means in setPath().
                reporter.setPath("", null, targetRevision, true);
            } else {
                // All of the files are already there in r.source
                // since we are updating an old checkout.
                reporter.setPath("", null, sourceRevision, false);
            }
            reporter.finishReport();
        } catch (SVNException e) {
            reporter.abortReport();
            logger.info("Checkout aborted by baton.");
        }
        logger.info("Empty checkout baton ready.");
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

