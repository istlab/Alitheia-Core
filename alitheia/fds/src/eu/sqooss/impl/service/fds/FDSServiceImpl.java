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
import java.util.Random;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import org.apache.commons.codec.binary.Hex;

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

/** {@inheritDoc} */
public class FDSServiceImpl implements FDSService {
    /** We will keep around the bundle context for the properties. */
    private BundleContext bundleContext = null;
    /** The logger for the FDS. */
    private Logger logger = null;
    /** We use the TDS for raw data access. */
    private TDSService tds = null;

    /**
     * The FDS is configured to place checkouts -- which are the main
     * things that the FDS is supposed to manage -- somewhere in the
     * filesystem. This is the root of those checkouts; underneath
     * here each project has a directory, and then checkouts of that
     * project live under there.
     */
    private File fdsCheckoutRoot = null;
    /**
     * Checkouts are done in directories with a random prefix; this
     * is done to avoid the suggestion that the checkouts are tied to
     * specific revisions. We generate the random prefixes with this
     * random generator.
     */
    private Random randomCheckout = null;

    /**
     * This map maps project names to lists of checkouts; the
     * checkouts all have different revisions.
     */
    private HashMap < Long, List < CheckoutImpl > > checkoutCollection;

    /*
     * The following constants influence the formatting of checkout
     * and project directory names.
     */

    /**
     * Project IDs are formatted as decimals (with leading zeroes)
     * of this length; 8 covers the expected range of IDs.
     */
    private static final int INT_AS_DECIMAL_LENGTH = 8;
    /**
     * Each checkout gets a random hex string prefixed to a
     * guaranteed unique identifier. The length of the prefix
     * is defined here.
     */
    private static final int RANDOM_PREFIX_LENGTH = 8;
    /**
     * States how many hex digits are needed to express an int.
     */
    private static final int INT_AS_HEX_LENGTH = 8;

    /**
     * The FDS considers its checkout root to be 'private' and will
     * write all kinds of stuff in there. The checkouts need to be
     * cleaned up on shutdown at the very least, in order to avoid
     * polluting the filesystem with orphaned checkout directories.
     * Also cleanup at startup, to prevent accidentally dropping
     * checkouts in older directories.
     *
     * Only directories that match the patterns created by the FDS
     * are actually cleaned up.
     *
     * Keep this synchronised with the format of files created elsewhere.
     */
    private void cleanupCheckoutRoot() {
        logger.info("Cleaning up " + fdsCheckoutRoot);
        File[] projects = fdsCheckoutRoot.listFiles();
        String projectRE = "^[0-9]{" + INT_AS_DECIMAL_LENGTH + "}-.*";
        String checkoutRE = "^[0-9a-f]{" + RANDOM_PREFIX_LENGTH
            + "}.[0-9a-f]{" + INT_AS_HEX_LENGTH + "}$";
        for (File f : projects) {
            String s = f.getName();
            if (s.matches(projectRE)) {
                File[] checkouts = f.listFiles();
                for (File c : checkouts) {
                    if (c.getName().matches(checkoutRE)) {
                        logger.info("Delete " + c);
                        DiskUtil.rmRf(c);
                    }
                }
            }
        }
    }

    /**
     * Constructor. Get the services the FDS needs, retrieve settings,
     * initialize data structures and clean up the checkout directory
     * in preparation for writing new stuff under the FDS root.
     *
     * @param bc bundlecontext for configuration parameters.
     */
    public FDSServiceImpl(final BundleContext bc) {
        bundleContext = bc;
        ServiceReference serviceRef =
            bc.getServiceReference(LogManager.class.getName());
        LogManager logService = (LogManager) bc.getService(serviceRef);
        logger = logService.createLogger(Logger.NAME_SQOOSS_FDS);
        if (logger != null) {
            logger.info("FDS service created.");
        } else {
            System.out.println("# FDS failed to get logger.");
            // This means we'll throw a null pointer exception in a minute,
            // which is fine -- can't run without a logger.
        }

        serviceRef = bc.getServiceReference(TDSService.class.getName());
        tds = (TDSService) bc.getService(serviceRef);
        logger.info("Got TDS service for FDS.");

        checkoutCollection = new HashMap < Long, List < CheckoutImpl > >();

        // Get the checkout root from the properties file.
        String s = bc.getProperty("eu.sqooss.fds.root");
        if (s == null) {
            logger.info("No eu.sqooss.fds.root set, using default /var/tmp");
            s = "/var/tmp";
        } else {
            logger.info("FDS root directory " + s);
        }
        fdsCheckoutRoot = new File(s);
        randomCheckout = new Random();

        cleanupCheckoutRoot();
    }

    /**
     * Stop method. Called by the activator on exit, just does cleanup.
     */
    public final void stop() {
        cleanupCheckoutRoot();
    }

    /**
     * Scan a list of checkouts for one with the right project revision.
     *
     * @param l list of checkouts to search for. These checkouts must belong
     *          to the project we're interested in.
     * @param r revision to search for.
     *
     * @return null if no checkout exists with exactly that revision, or
     *          the checkout object itself. Also returns null if the revision
     *          has not been normalized to have a SVN revision (instead of
     *          just a date or some other revision kind).
     */
    private CheckoutImpl findCheckout(final List < CheckoutImpl > l,
        final ProjectRevision r) {
        if (!r.hasSVNRevision()) {
            return null;
        }
        for (Iterator < CheckoutImpl > i = l.iterator(); i.hasNext(); ) {
            CheckoutImpl c = i.next();
            if (c.getRevision().getSVNRevision() == r.getSVNRevision()) {
                return c;
            }
        }
        return null;
    }

    /**
     * Scan a list of checkouts heuristically for one checkout that is
     * 'close enough' and which may be updated.
     *
     * @param l list of checkouts to search in.
     * @param r desired revision.
     * @return null if no checkout exists that is 'close enough'.
     */
    private CheckoutImpl findUpdatableCheckout(final List < CheckoutImpl > l,
        final ProjectRevision r) {
        if (!r.hasSVNRevision()) {
            return null;
        }

        // This is the acceptable distance between the desired revision
        // and the revision of the checkout which will be returned.
        // As a crude first heuristic for what's acceptable, we use a
        // distance that grows with the number of checkouts.
        long acceptableDistance = l.size() * 37;
        long lowerBound = r.getSVNRevision() - acceptableDistance;
        long upperBound = r.getSVNRevision() + acceptableDistance;

        for (Iterator < CheckoutImpl > i = l.iterator(); i.hasNext(); ) {
            CheckoutImpl c = i.next();
            if (c.getReferenceCount() == 0) {
                // This is a candidate
                long rev = c.getRevision().getSVNRevision();
                if ( (lowerBound <= rev) && (rev <= upperBound) ) {
                    return c;
                }
            }
        }

        return null;
    }

    /**
     * This is for consistency checks: any project p should have exactly
     * *one* checkout for a revision r, no more than that
     *
     * @param c Checkout to search for.
     * @return Checkout; ought to be c again.
     * @throws InvalidRepositoryException if the checkout refers to a
     *          non-existent project or has a bogus revision attached.
     * @throws RuntimeException if we find a second checkout of the
     *          same project and revision which is not equal to c.
     */
    private CheckoutImpl findCheckout( Checkout c )
        throws InvalidRepositoryException {
        List<CheckoutImpl> l = checkoutCollection.get(c.getId());
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

    /**
     * Create, for a given SCM accessor, a checkout at a given revision.
     * This assumes that there is not already a checkout for this
     * project in this revision, and uses the SCM itself to do the
     * checkout somewhere underneath the FDS root.
     */
    private CheckoutImpl createCheckout( SCMAccessor svn, ProjectRevision r )
        throws InvalidRepositoryException,
               InvalidProjectRevisionException {
        logger.info("Creating new checkout for " + svn.getName() + " " + r);

        File projectRoot = new File(fdsCheckoutRoot,
            String.format("%0" + INT_AS_DECIMAL_LENGTH + "d", svn.getId())
                + "-" + svn.getName());
        // It shouldn't exist yet
        projectRoot.mkdir();

        // Side effect: throws if the revision is invalid
        svn.resolveProjectRevision(r);

        // In order to discourage assumptions about what checkouts belong
        // where, assign each an 8-character random prefix and then
        // encode the revision number as well; this means that we can
        // update and futz with the revisions within each checkout directory.
        byte[] randomBytes = new byte[(RANDOM_PREFIX_LENGTH + 1)/2];
        randomCheckout.nextBytes(randomBytes);
        char[] randomPrefixChars = Hex.encodeHex(randomBytes);
        String format = "%0" + INT_AS_HEX_LENGTH + "x";
        File checkoutRoot = new File(projectRoot,
            new String(randomPrefixChars)
            + "." + String.format(format, r.getSVNRevision()));
        // It shouldn't exist yet either
        if (checkoutRoot.exists()) {
            logger.warning("Checkout root <" + checkoutRoot
                + "> already exists.");
            if (checkoutRoot.isDirectory()) {
                logger.info("Recycling the checkout root.");
            } else {
                // TODO: throw instead?
                return null;
            }
        } else {
            if (!checkoutRoot.mkdirs()) {
                logger.warning("Could not create checkout root <" +
                    checkoutRoot + ">");
                // TODO: throw instead?
                return null;
            }
        }
        // Now checkoutRoot exists and is a directory.

        logger.info("Created checkout root <" + checkoutRoot + ">");
        try {
            svn.getCheckout("", r, checkoutRoot);
        } catch (FileNotFoundException e) {
            logger.warning("Root of project " + svn.getName()
                + " does not exist: " + e.getMessage());
            return null;
        }

        CheckoutImpl c = new CheckoutImpl( svn.getId(), svn.getName());
        c.setCheckout( checkoutRoot, r );
        c.claim();

        return c;
    }

    /**
     * Update a checkout from its current revision to another one.
     * This should only be done when the checkout has no references
     * anymore. Updating a checkout may be more efficient than doing
     * a whole new checkout, but this should be balanced against the
     * number of users of checkouts of a given SCM and the amount of
     * available disk space.
     */
    private void updateCheckout(CheckoutImpl c, ProjectRevision r)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {
        logger.info("Updating project " + c.getId() + " from "
            + c.getRevision() + " to " + r);
        TDAccessor a = tds.getAccessor(c.getId());
        SCMAccessor svn = a.getSCMAccessor();
        svn.resolveProjectRevision(r);
        try {
            svn.updateCheckout("", c.getRevision(), r, c.getRoot());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        c.setRevision(r);
    }

    // Interface methods
    /** {@inheritDoc} */
    public CheckoutImpl getCheckout(long projectId, ProjectRevision r)
        throws InvalidRepositoryException,
               InvalidProjectRevisionException {
        if (!tds.projectExists(projectId)) {
            throw new InvalidRepositoryException(String.valueOf(projectId),
                "", "No such project to check out.");
        }
        if (!tds.accessorExists(projectId)) {
            throw new InvalidRepositoryException(String.valueOf(projectId),
                "", "No accessor available.");
        }

        TDAccessor a = tds.getAccessor(projectId);
        if (a == null) {
            logger.warning("Accessor not available even though it exists.");
            throw new InvalidRepositoryException(String.valueOf(projectId),
                "", "No accessor available.");
        }

        SCMAccessor svn = a.getSCMAccessor();
        if (svn == null) {
            logger.warning("No SCM available for " + svn.getName());
            throw new InvalidRepositoryException(svn.getName(),"",
                "No SCM accessor available.");
        }

        svn.resolveProjectRevision(r);

        logger.info("Finding available checkout for "
            + svn.getName() + " " + r);
        List<CheckoutImpl> l = checkoutCollection.get(projectId);
        if (l!=null) {
            synchronized(l) {
                CheckoutImpl c = findCheckout(l,r);
                if (c != null) {
                    c.claim();
                } else {
                    c = findUpdatableCheckout(l,r);
                    if (c != null) {
                        updateCheckout(c, r);
                        c.claim();
                    } else {
                        c = createCheckout(svn,r);
                        l.add(c);
                    }
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
                    c = createCheckout(svn,r);
                    l.add(c);
                }
                return c;
            }
        }
    }

    /** {@inheritDoc} */
    public void releaseCheckout(Checkout c)
        throws InvalidRepositoryException {
        CheckoutImpl i = findCheckout(c);
        if (i.release() < 1) {
            logger.info("Checkout of " + c.getName() + " (" +
                c.getRevision() + ") is free.");
        }
    }

    /**
     * Perform a self-test on the FDS by trying various operations
     * on existing checkouts.
     */
    public Object selfTest() {
        if (logger == null) {
            return new String("No logger available.");
        }
        if (checkoutCollection == null) {
            return new String("No checkout collection available.");
        }

        String enabled = bundleContext.getProperty(
            "eu.sqooss.tester.enable.FDSServiceImpl.DiskUtil");
        if ((enabled == null) || Boolean.valueOf(enabled)) {
            DiskUtil.selfTest(logger);
        } else {
            logger.info("Skipping DiskUtil self-test.");
        }

        // This is supposed to throw an exception
        boolean thrown = false;
        try {
            logger.info("Intentionally throwing InvalidRepository.");
            Checkout c = getCheckout(-1, new ProjectRevision(1));
        } catch (InvalidRepositoryException e) {
            logger.info("Exception triggered as expected.");
            thrown = true;
        } catch (InvalidProjectRevisionException e) {
            logger.warning("Invalid revision triggered first (wrongly).");
            thrown = true;
        } catch (NullPointerException e) {
            logger.warning("Null pointer in checkout.");
            e.printStackTrace();
        }
        if (!thrown) {
            return new String("No exception thrown for bogus project.");
        }

        // This is supposed to throw as well
        thrown = false;
        try {
            logger.info("Intentionally triggering InvalidRevision exception.");
            // Assuming KDE doesn't reach 1 billion commits before 2038
            Checkout c = getCheckout(1, new ProjectRevision(1000000000));
        } catch (InvalidRepositoryException e) {
            logger.warning("No project with ID 1.");
            thrown = true;
        } catch (InvalidProjectRevisionException e) {
            logger.info("Exception triggered as expected.");
            thrown = true;
        } catch (NullPointerException e) {
            logger.warning("Null pointer in checkout.");
            e.printStackTrace();
        }
        if (!thrown) {
            return new String("No exception thrown for bogus revision.");
        }

        // This time it should not fail
        thrown = false;
        CheckoutImpl projectCheckout = null;
        try {
            logger.info("Getting something sensible out of FDS");
            projectCheckout = getCheckout(1, new ProjectRevision(1));
        } catch (InvalidRepositoryException e) {
            logger.warning("(Still) no project with ID 1.");
            e.printStackTrace();
            thrown = true;
        } catch (InvalidProjectRevisionException e) {
            logger.warning("Project ID 1 has no revision 1");
            thrown = true;
        } catch (NullPointerException e) {
            logger.warning("Null pointer in checkout.");
            e.printStackTrace();
        }
        if (thrown) {
            return new String("Unexpected exception thrown for p.1 r.1");
        }

        if (projectCheckout != null) {
            try {
                updateCheckout(projectCheckout, new ProjectRevision(4));
            } catch (InvalidRepositoryException e) {
                logger.warning("Project ID 1 has vanished again.");
            } catch (InvalidProjectRevisionException e) {
                logger.warning("Project ID 1 has no revision 4");
            }
        }

        return null;
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

