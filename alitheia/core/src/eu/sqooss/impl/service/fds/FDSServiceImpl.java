/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.fds.Checkout;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.Timeline;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.PathChangeType;
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
        if (projects == null) {
            return;
        }
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
    public FDSServiceImpl(BundleContext bc, Logger l) {
        bundleContext = bc;
        logger = l;

        ServiceReference serviceRef = bc.getServiceReference(
            AlitheiaCore.class.getName());
        // This will NPE if there wasn't a TDS available, no problem.
        tds = ((AlitheiaCore) bc.getService(serviceRef)).getTDSService();
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
                if ((lowerBound <= rev) && (rev <= upperBound)) {
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
    private CheckoutImpl findCheckout(Checkout c)
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
            throw new RuntimeException("Duplicate checkouts for "
                + c.getName() + " " + c.getRevision());
        }
    }

    /**
     * Create, for a given SCM accessor, a checkout at a given revision.
     * This assumes that there is not already a checkout for this
     * project in this revision, and uses the SCM itself to do the
     * checkout somewhere underneath the FDS root.
     */
    private CheckoutImpl createCheckout(SCMAccessor svn, ProjectRevision r)
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
            logger.warn("Checkout root <" + checkoutRoot
                + "> already exists.");
            if (checkoutRoot.isDirectory()) {
                logger.info("Recycling the checkout root.");
            } else {
                logger.warn("Already existing root <" + checkoutRoot + "> is not a directory. Can't use that one.");
                return null;
            }
        } else {
            if (!checkoutRoot.mkdirs()) {
                logger.warn("Could not create checkout root <" +
                    checkoutRoot + ">");
                return null;
            }
        }
        // Now checkoutRoot exists and is a directory.

        logger.info("Created checkout root <" + checkoutRoot + ">");
        try {
            CheckoutImpl c = new CheckoutImpl(svn, "", r, checkoutRoot);
            c.claim();
            return c;
        } catch (FileNotFoundException e) {
            logger.warn("Root of project " + svn.getName()
                + " does not exist: " + e.getMessage());
            return null;
        }
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
            c.updateCheckout(svn, r);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * For a project file, return the SVN revision that it refers to.
     *
     * @param pf The ProjectFile to look up.
     * @return The SVN revision (as a number!) for the project or null
     *      if the project file is deleted or otherwise unavailable.
     */
    private ProjectRevision projectFileRevision(ProjectFile pf) {
        // Make sure that the file exists in the specified project version
        String fileStatus = pf.getStatus();
        if (PathChangeType.valueOf(fileStatus) == PathChangeType.DELETED) {
            return null;
        }

        /* NOTE: The following code assumes that a project version equals to
         *       a project revision
         */
        long projectVersion = pf.getProjectVersion().getVersion();
        return new ProjectRevision(projectVersion);
    }

    /**
     * Get the File where the given project file will be cached locally
     * by the FDS.
     *
     * @param pf ProjectFile to look up.
     * @param r  Revision of the project file; this is a minor optimization,
     *      if r is null the revision is retrieved from @p pf anyway.
     * @return File for this project file, or null if there is no such
     *      file in the given revision.
     */
    private File projectFileLocal(ProjectFile pf, ProjectRevision r) {
        ProjectRevision pr = null;
        if (r == null) {
            pr = projectFileRevision(pf);
        } else {
            pr = r;
        }

        // Path generation for a "single file checkout"
        File checkoutFile = new File(
                fdsCheckoutRoot
                + System.getProperty("file.separator")
                + pf.getProjectVersion().getProject().getId()
                + System.getProperty("file.separator")
                + pr.getSVNRevision()
                + System.getProperty("file.separator")
                + pf.getName());

        // TODO: possibly also look in existing checkouts?
        return checkoutFile;
    }

    /**
     * For a given project file, return the SCM accessor that can
     * be used to get at the file contents.
     *
     * @param pf The project file to look up.
     * @return The accessor or null on failure.
     */
    private SCMAccessor projectFileAccessor(ProjectFile pf) {
        // Retrieve the project ID
        long projectId = pf.getProjectVersion().getProject().getId();

        // Get a TDS handle for the selected ProjectFile
        return tds.getAccessor(projectId).getSCMAccessor();
    }

    // ===[ INTERFACE METHODS ]===============================================

    /** {@inheritDoc} */
    public File getFile (ProjectFile pf) {
        ProjectRevision projectRevision = projectFileRevision(pf);
        if (projectRevision == null) {
            return null;
        }

        File checkoutFile = projectFileLocal(pf, projectRevision);
        if (checkoutFile == null) {
            return null;
        }

        SCMAccessor scm = projectFileAccessor(pf);
        if (scm == null) {
            return null;
        }

        try {
            // Skip the checkout, in case this ProjectFile is already
            // available (i.e. retrieved in a previous checkout)
            if (!checkoutFile.exists()) {
                // Create the path to the target file if it doesn't exist
                if ((checkoutFile.getParentFile() != null)
                        && (!checkoutFile.getParentFile().exists())) {
                    checkoutFile.getParentFile().mkdirs();
                }
                // Try to checkout the target file
                scm.getFile(
                        pf.getName(),
                        projectRevision,
                        checkoutFile);
            }

            // Make sure that the target file is accessible
            if ((checkoutFile.exists())
                    && (checkoutFile.isFile())
                    && (checkoutFile.canRead())) {
                return checkoutFile;
            }
        // returning null here is fine
        } catch (InvalidRepositoryException e) {
            logger.error("The repository for " + pf.toString() + " is invalid.");
        } catch (InvalidProjectRevisionException e) {
            logger.error("The repository for " + pf.toString() + " has no revision " + projectRevision + ".");
        } catch (FileNotFoundException e) {
            logger.error("File " + pf.toString() + " not found in the given repository.");
        }
        return null;
    }

    /** {@inheritDoc} */
    public byte[] getFileContents(ProjectFile pf) {
        // Let's see if the file is cached already.
        ProjectRevision pr = projectFileRevision(pf);
        if (pr == null) {
            return null;
        }

        File checkoutFile = projectFileLocal(pf, pr);

        if ((checkoutFile != null) &&
            checkoutFile.exists() &&
            checkoutFile.isFile() &&
            checkoutFile.canRead()) {
            return eu.sqooss.service.util.FileUtils.fileContents(checkoutFile);
        }

        // We get here if the file isn't locally cached,
        // so we need to get it from the SCM.
        SCMAccessor scm = projectFileAccessor(pf);
        if (scm == null) {
            return null;
        }

        // Need to get file from SCM
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
	// TODO: read bytes from SCM
        return output.toByteArray();
    }

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
            logger.warn("Accessor not available even though it exists.");
            throw new InvalidRepositoryException(String.valueOf(projectId),
                "", "No accessor available.");
        }

        SCMAccessor svn = a.getSCMAccessor();
        if (svn == null) {
            logger.warn("No SCM available for " + svn.getName());
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
            //DiskUtil.selfTest(logger);
        } else {
            logger.info("Skipping DiskUtil self-test.");
        }

        final int TEST_PROJECT_ID = 1337;
       	tds.addAccessor(TEST_PROJECT_ID, "KPilot", "", null, "http://cvs.codeyard.net/svn/kpilot/" );

        // This is supposed to throw an exception
        boolean thrown = false;
        try {
            logger.info("Intentionally throwing InvalidRepository.");
            Checkout c = getCheckout(-1, new ProjectRevision(1));
        } catch (InvalidRepositoryException e) {
            logger.info("Exception triggered as expected.");
            thrown = true;
        } catch (InvalidProjectRevisionException e) {
            logger.warn("Invalid revision triggered first (wrongly).");
            thrown = true;
        } catch (NullPointerException e) {
            logger.warn("Null pointer in checkout.");
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
            Checkout c = getCheckout(TEST_PROJECT_ID, new ProjectRevision(1000000000));
        } catch (InvalidRepositoryException e) {
            logger.warn("No project with ID " + TEST_PROJECT_ID);
            thrown = true;
        } catch (InvalidProjectRevisionException e) {
            logger.info("Exception triggered as expected.");
            thrown = true;
        } catch (NullPointerException e) {
            logger.warn("Null pointer in checkout.");
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
            projectCheckout = getCheckout(TEST_PROJECT_ID, new ProjectRevision(1));
        } catch (InvalidRepositoryException e) {
            logger.warn("(Still) no project with ID " + TEST_PROJECT_ID);
            e.printStackTrace();
            thrown = true;
        } catch (InvalidProjectRevisionException e) {
            logger.warn("Project ID " + TEST_PROJECT_ID + " has no revision 1");
            thrown = true;
        } catch (NullPointerException e) {
            logger.warn("Null pointer in checkout.");
            e.printStackTrace();
        }
        if (thrown) {
            return new String("Unexpected exception thrown for p." + TEST_PROJECT_ID + " r.1");
        }

        if (projectCheckout != null) {
            try {
                // Normally you don't do an update on a checkout that
                // is claimed by someone.
                updateCheckout(projectCheckout, new ProjectRevision(4));
                logger.info(projectCheckout.getCommitLog().toString());
            } catch (InvalidRepositoryException e) {
                logger.warn("Project ID " + TEST_PROJECT_ID + " has vanished again.");
            } catch (InvalidProjectRevisionException e) {
                logger.warn("Project ID " + TEST_PROJECT_ID + " has no revision 4");
            }
        }

        if (projectCheckout != null) {
            try {
                releaseCheckout(projectCheckout);
            } catch (InvalidRepositoryException e) {
                logger.warn("Project ID " + TEST_PROJECT_ID + " is no longer managed.");
            }
        }

        // This test goes through and updates the KPilot checkout from r.4 to
        // r.60, which will take some time. It should demonstrate that the
        // checkout is released and updated each time. Note that we still hold
        // the reference to projectCheckout, which is something we should no
        // longer access -- but it's the object reference to the checkout which
        // we *should* get.
        enabled = bundleContext.getProperty(
            "eu.sqooss.tester.enable.FDSServiceImpl.Updates");
        if ((enabled == null) || Boolean.valueOf(enabled)) {
            CheckoutImpl otherCheckout = null;
            long currentRevision = 4;
            try {
                logger.info("Advancing single checkout object.");
                otherCheckout = getCheckout(TEST_PROJECT_ID,
                    new ProjectRevision(currentRevision));
                if (otherCheckout != projectCheckout) {
                    logger.warn("Second request for " + TEST_PROJECT_ID + " r.4 returned "
                        + "different object.");
                }
                while (currentRevision < 60) {
                    releaseCheckout(otherCheckout);
                    currentRevision++;
                    otherCheckout = getCheckout(TEST_PROJECT_ID,
                        new ProjectRevision(currentRevision));
                }
                if (otherCheckout != projectCheckout) {
                    logger.warn("Sixtieth request for " + TEST_PROJECT_ID + " r.60 returned "
                        + "different object.");
                }
            } catch (InvalidRepositoryException e) {
                logger.warn("Project ID " + TEST_PROJECT_ID + " has vanished again.");
            } catch (InvalidProjectRevisionException e) {
                logger.warn("Project ID " + TEST_PROJECT_ID + " has no revision "
                    + currentRevision);
            }
        } else {
            logger.info("Skipping update self-test.");
        }
        return null;
    }

    public Timeline getTimeline(StoredProject c) {
        return new TimelineImpl(c);
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

