/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.impl.service.fds;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.service.fds.Checkout;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.ProjectRevision;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.TDAccessor;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.impl.service.fds.CheckoutImpl;

public class FDSServiceImpl implements FDSService {
    private LogManager logService = null;
    private Logger logger = null;
    private TDSService tds = null;
    private File fdsCheckoutRoot = null;

    /**
     * This map maps project names to lists of checkouts; the
     * checkouts all have different revisions.
     */
    private HashMap<String,List<CheckoutImpl>> checkoutCollection;

    public FDSServiceImpl(BundleContext bc) {
        ServiceReference serviceRef = bc.getServiceReference(LogManager.class.getName());
        logService = (LogManager) bc.getService(serviceRef);
        logger = logService.createLogger(Logger.NAME_SQOOSS_FDS);
        if (logger != null) {
            logger.info("FDS service created.");
        } else {
            System.out.println("# FDS failed to get logger.");
            return;
        }

        serviceRef = bc.getServiceReference(TDSService.class.getName());
        tds = (TDSService) bc.getService(serviceRef);
        logger.info("Got TDS service for FDS.");

        checkoutCollection = new HashMap<String,List<CheckoutImpl>>();
        logger.info("FDS root directory " + bc.getProperty("eu.sqooss.fds.root"));
        fdsCheckoutRoot = new File(bc.getProperty("eu.sqooss.fds.root"));
    }

    /**
     * Scan a list of checkouts for one with the right project revision.
     */
    private CheckoutImpl findCheckout(List<CheckoutImpl> l, ProjectRevision r) {
        for (Iterator<CheckoutImpl> i = l.iterator(); i.hasNext(); ) {
            CheckoutImpl c = i.next();
            if (c.getRevision().getSVNRevision() == r.getSVNRevision()) {
                return c;
            }
        }
        return null;
    }

    private CheckoutImpl findCheckout( Checkout c )
        throws InvalidRepositoryException {
        List<CheckoutImpl> l = checkoutCollection.get(c.getProjectName());
        if (l == null) {
            throw new InvalidRepositoryException(c.getProjectName(),"",
                "No managed checkout for this project.");
        }
        if (!c.getRevision().isValid() || !c.getRevision().hasSVNRevision()) {
            throw new InvalidRepositoryException(c.getProjectName(),"",
                "Bogus checkout has bad revision attached.");
        }

        CheckoutImpl candidate = findCheckout(l, c.getRevision());
        if (candidate == c) {
            return candidate;
        } else {
            // This means that we have two objects in the checkoutCollection
            // with the same project name and the same SVN revision, but
            // they are different objects. This must not happen.
            throw new RuntimeException("Duplicate checkouts for " +
                c.getProjectName() + " " + c.getRevision());
        }
    }

    // Interface methods
    public Checkout getCheckout(String projectName, ProjectRevision r)
        throws InvalidRepositoryException,
               InvalidProjectRevisionException {
        if (!tds.projectExists(projectName)) {
            throw new InvalidRepositoryException(projectName,"",
                "No such project to check out.");
        }
        if (!tds.accessorExists(projectName)) {
            throw new InvalidRepositoryException(projectName,"",
                "No accessor available.");
        }

        TDAccessor a = tds.getAccessor(projectName);
        if (a == null) {
            logger.warning("Accessor not available even though accessorExists()");
            throw new InvalidRepositoryException(projectName,"",
                "No accessor available.");
        }

        SCMAccessor svn = a.getSCMAccessor();
        if (svn == null) {
            logger.warning("No SCM available for " + projectName);
            throw new InvalidRepositoryException(projectName,"",
                "No SCM accessor available.");
        }

        svn.resolveProjectRevision(r);

        List<CheckoutImpl> l = checkoutCollection.get(projectName);
        if (l!=null) {
            CheckoutImpl c = findCheckout(l,r);
            if (c != null) {
                c.claim();
            }
            // TODO: else create this checkout
            return c;
        } else {
            // This is the very first checkout of the project,
            // isn't that exciting.
            l = new LinkedList<CheckoutImpl>();
            checkoutCollection.put(projectName,l);
        }
        return null;
    }

    public void releaseCheckout(Checkout c)
        throws InvalidRepositoryException {
        CheckoutImpl i = findCheckout(c);
        if (i.release() < 1) {
            logger.info("Checkout of " + c.getProjectName() + " (" +
                c.getRevision() + ") is free.");
        }
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

