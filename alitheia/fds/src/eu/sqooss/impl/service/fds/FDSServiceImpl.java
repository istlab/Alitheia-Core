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
import java.io.FileNotFoundException;
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

public class FDSServiceImpl implements FDSService {
    private LogManager logService = null;
    private Logger logger = null;
    private TDSService tds = null;
    private File fdsCheckoutRoot = null;

    /**
     * This map maps project names to lists of checkouts; the
     * checkouts all have different revisions.
     */
    private HashMap<Long,List<CheckoutImpl>> checkoutCollection;

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

        checkoutCollection = new HashMap<Long,List<CheckoutImpl>>();
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
        List<CheckoutImpl> l = checkoutCollection.get(c.getName());
        if (l == null) {
            throw new InvalidRepositoryException(c.getName(),"",
                "No managed checkout for this project.");
        }
        if (!c.getRevision().isValid() || !c.getRevision().hasSVNRevision()) {
            throw new InvalidRepositoryException(c.getName(),"",
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
                c.getName() + " " + c.getRevision());
        }
    }

    private CheckoutImpl createCheckout( SCMAccessor svn,
        long projectId, String projectName, ProjectRevision r )
        throws InvalidRepositoryException,
               InvalidProjectRevisionException {
        File projectRoot = new File(fdsCheckoutRoot,new Long(projectId).toString());
        // It shouldn't exist yet
        projectRoot.mkdir();

        File checkoutRoot = new File(projectRoot,new Long(r.getSVNRevision()).toString());
        // It shouldn't exist yet either
        checkoutRoot.mkdir();

        try {
            svn.checkOut( "", r, checkoutRoot.toString() );
        } catch (FileNotFoundException e) {
            logger.warning("Root of project " + svn.getName() + " does not exist.");
            return null;
        }

        CheckoutImpl c = new CheckoutImpl( projectId, projectName );
        c.setCheckout( checkoutRoot, r );
        c.claim();

        return c;
    }

    // Interface methods
    public Checkout getCheckout(long projectId, String projectName, ProjectRevision r)
        throws InvalidRepositoryException,
               InvalidProjectRevisionException {
        if (!tds.projectExists(projectId)) {
            throw new InvalidRepositoryException(projectName,"",
                "No such project to check out.");
        }
        if (!tds.accessorExists(projectId)) {
            throw new InvalidRepositoryException(projectName,"",
                "No accessor available.");
        }

        TDAccessor a = tds.getAccessor(projectId);
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

        List<CheckoutImpl> l = checkoutCollection.get(projectId);
        if (l!=null) {
            synchronized(l) {
                CheckoutImpl c = findCheckout(l,r);
                if (c != null) {
                    c.claim();
                } else {
                    c = createCheckout(svn,projectId,projectName,r);
                    l.add(c);
                }
                return c;
            }
        } else {
            // This is the very first checkout of the project,
            // isn't that exciting.
            synchronized(checkoutCollection) {
                CheckoutImpl c = null;
                l = new LinkedList<CheckoutImpl>();
                synchronized(l) {
                    checkoutCollection.put(projectId,l);
                    c = createCheckout(svn,projectId,projectName,r);
                    l.add(c);
                }
                return c;
            }
        }
    }

    public void releaseCheckout(Checkout c)
        throws InvalidRepositoryException {
        CheckoutImpl i = findCheckout(c);
        if (i.release() < 1) {
            logger.info("Checkout of " + c.getName() + " (" +
                c.getRevision() + ") is free.");
        }
    }

    public Object selfTest() {
        if (logger == null) {
            return new String("No logger available.");
        }
        if (checkoutCollection == null) {
            return new String("No checkout collection available.");
        }

        // This is supposed to throw an exception
        boolean thrown = false;
        try {
            logger.info("Intentionally triggering InvalidRepository exception.");
            Checkout c = getCheckout(-1, null, new ProjectRevision(1));
        } catch (InvalidRepositoryException e) {
            thrown = true;
        } catch (InvalidProjectRevisionException e) {
            logger.warn("Invalid revision triggered first (wrongly).");
            thrown = true;
        }
        if (!thrown) {
            return  new String("No exception thrown for bogus project.");
        }

        // This is supposed to throw as well
        thrown = false;
        try {
            logger.info("Intentionally triggering InvalidRevision exception.");
            // Assuming KDE doesn't reach 1 billion commits before 2038
            Checkout c = getCheckout(1, "kde", new ProjectRevision(1000000000));
        } catch (InvalidRepositoryException e) {
            logger.warn("No project with ID 1.");
            thrown = true;
        } catch (InvalidProjectRevisionException e) {
            thrown = true;
        }
        if (!thrown) {
            return new String("No exception thrown for bogus revision.");
        }

        return null;
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

