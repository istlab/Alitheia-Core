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

package eu.sqooss.impl.service.fds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.fds.CheckoutException;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.InMemoryCheckout;
import eu.sqooss.service.fds.OnDiskCheckout;
import eu.sqooss.service.fds.Timeline;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.InvalidAccessorException;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.PathChangeType;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.tds.Revision;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.TDSService;

/** {@inheritDoc} */
public class FDSServiceImpl implements FDSService, Runnable {
    /** The logger for the FDS. */
    private Logger logger = null;
    /** We use the TDS for raw data access. */
    private TDSService tds = null;

    /**
     * The FDS is configured to place checkouts -- which are the main things
     * that the FDS is supposed to manage -- somewhere in the filesystem. This
     * is the root of those checkouts; underneath here each project has a
     * directory, and then checkouts of that project live under there.
     */
    private File fdsCheckoutRoot = null;
    /**
     * Checkouts are done in directories with a random prefix; this is done to
     * avoid the suggestion that the checkouts are tied to specific revisions.
     * We generate the random prefixes with this random generator.
     */
    private Random randomCheckout = null;

    /**
     * Cache checkouts in a live system. The cache will not be re-populated from
     * on disk data if the system is shutdown.
     */
    private ConcurrentHashMap<String, OnDiskCheckout> checkoutCache;

    /**
     * Number of handles acquired on each cached checkout.
     */
    private ConcurrentHashMap<OnDiskCheckout, Integer> checkoutHandles;

    private BundleContext bc;
    
    /*
     * The following constants influence the formatting of checkout and project
     * directory names.
     */
    /**
     * Project IDs are formatted as decimals (with leading zeroes) of this
     * length; 8 covers the expected range of IDs.
     */
    private static final int INT_AS_DECIMAL_LENGTH = 8;
    /**
     * Each checkout gets a random hex string prefixed to a guaranteed unique
     * identifier. The length of the prefix is defined here.
     */
    private static final int RANDOM_PREFIX_LENGTH = 8;
    /**
     * States how many hex digits are needed to express an int.
     */
    private static final int INT_AS_HEX_LENGTH = 8;

    public FDSServiceImpl() { }

    /**
     * The FDS considers its checkout root to be 'private' and will write all
     * kinds of stuff in there. The checkouts need to be cleaned up on shutdown
     * at the very least, in order to avoid polluting the filesystem with
     * orphaned checkout directories.
     * 
     */
    private class CleanupThread extends Thread {

        public CleanupThread(String name) {
            super(name);
        }

        public void run() {
            System.err.println("Cleaning up " + fdsCheckoutRoot);
            logger.info("Cleaning up " + fdsCheckoutRoot);
            DiskUtil.rmRf(fdsCheckoutRoot);
        }
    }

    /**
     * Create, for a given SCM accessor, an on disk checkout at a given
     * revision. This assumes that there is not already a checkout for this
     * project in this revision, and uses the SCM itself to do the checkout
     * somewhere underneath the FDS root.
     * 
     * @return
     */
    private OnDiskCheckout createCheckout(SCMAccessor scm, ProjectVersion pv) {
        logger.info("Creating new checkout for " + pv);

        File projectRoot = new File(fdsCheckoutRoot, String.format("%0"
                + INT_AS_DECIMAL_LENGTH + "d", pv.getProject().getId())
                + "-" + pv.getProject().getName());
        // It shouldn't exist yet
        projectRoot.mkdir();

        // Side effect: throws if the revision is invalid
        Revision r = scm.newRevision(pv.getRevisionId());

        // In order to discourage assumptions about what checkouts belong
        // where, assign each an 8-character random prefix and then
        // encode the revision number as well; this means that we can
        // update and futz with the revisions within each checkout directory.
        byte[] randomBytes = new byte[(RANDOM_PREFIX_LENGTH + 1) / 2];
        randomCheckout.nextBytes(randomBytes);
        char[] randomPrefixChars = Hex.encodeHex(randomBytes);
        String format = "%0" + INT_AS_HEX_LENGTH + "x";
        File checkoutRoot = new File(projectRoot, new String(randomPrefixChars)
                + "." + String.format(format, r.getUniqueId()));
        // It shouldn't exist yet either
        if (checkoutRoot.exists()) {
            logger.warn("Checkout root <" + checkoutRoot + "> already exists.");
            if (checkoutRoot.isDirectory()) {
                logger.info("Recycling the checkout root.");
            } else {
                logger.warn("Already existing root <" + checkoutRoot
                        + "> is not a directory. Can't use that one.");
                return null;
            }
        } else {
            if (!checkoutRoot.mkdirs()) {
                logger.warn("Could not create checkout root <" + checkoutRoot
                        + ">");
                return null;
            }
        }
        // Now checkoutRoot exists and is a directory.

        logger.info("Created checkout root <" + checkoutRoot + ">");
        OnDiskCheckout c = new OnDiskCheckoutImpl("", pv, checkoutRoot);
        return c;
    }

    /**
     * For a project file, return the SCM revision that it refers to.
     * 
     * @param pf
     *            The ProjectFile to look up.
     * @return The SCM revision for the project or null if the project file is
     *         deleted or otherwise unavailable.
     */
    private Revision projectFileRevision(ProjectFile pf) {
        // Make sure that the file exists in the specified project version
        String fileStatus = pf.getState().toString();
        if (PathChangeType.valueOf(fileStatus) == PathChangeType.DELETED) {
            return null;
        }

        String projectVersion = pf.getProjectVersion().getRevisionId();
        long projectId = pf.getProjectVersion().getProject().getId();
        try {
            return tds.getAccessor(projectId).getSCMAccessor().newRevision(
                    projectVersion);
        } catch (InvalidAccessorException e) {
            logger.error("Invalid SCM accessor for project "
                    + pf.getProjectVersion().getProject().getName() + " "
                    + e.getMessage());
            return null;
        }
    }

    /**
     * Get the File where the given project file will be cached locally by the
     * FDS.
     * 
     * @param pf
     *            ProjectFile to look up.
     * @param r
     *            Revision of the project file; this is a minor optimization, if
     *            r is null the revision is retrieved from @p pf anyway.
     * @return File for this project file, or null if there is no such file in
     *         the given revision.
     */
    private File projectFileLocal(ProjectFile pf, Revision r) {
        Revision pr = null;
        if (r == null) {
            pr = projectFileRevision(pf);
        } else {
            pr = r;
        }

        // Path generation for a "single file checkout"
        File checkoutFile = new File(fdsCheckoutRoot
                + System.getProperty("file.separator")
                + pf.getProjectVersion().getProject().getId()
                + System.getProperty("file.separator") + pr.getUniqueId()
                + System.getProperty("file.separator") + pf.getFileName());

        // TODO: possibly also look in existing checkouts?
        return checkoutFile;
    }

    /**
     * For a given project file, return the SCM accessor that can be used to get
     * at the file contents.
     * 
     * @param pf
     *            The project file to look up.
     * @return The accessor or null on failure.
     */
    private SCMAccessor projectFileAccessor(ProjectFile pf) {
        // Retrieve the project ID
        long projectId = pf.getProjectVersion().getProject().getId();

        // Get a TDS handle for the selected ProjectFile
        try {
            return tds.getAccessor(projectId).getSCMAccessor();
        } catch (InvalidAccessorException e) {
            logger.error("Invalid SCM accessor for project "
                    + pf.getProjectVersion().getProject().getName() + " "
                    + e.getMessage());
            return null;
        }
    }

    /**
     * Check whether a checkout can be done
     */
    private boolean canCheckout(ProjectVersion pv) throws CheckoutException {

        long projectId = pv.getProject().getId();

        if (!tds.projectExists(projectId)) {
            throw new CheckoutException("No such project " + pv.getProject()
                    + " to check out.");
        }
        if (!tds.accessorExists(projectId)) {
            throw new CheckoutException("No accessor available for project: "
                    + pv.getProject().getName());
        }

        ProjectAccessor a = tds.getAccessor(projectId);

        if (a == null) {
            logger.warn("Accessor not available even though it exists.");
            throw new CheckoutException("Accessor " + "for project "
                    + pv.getProject().getName()
                    + " not available even though it exists.");
        }

        try {
            SCMAccessor svn = a.getSCMAccessor();
            if (svn == null) {
                logger
                        .warn("No SCM available for "
                                + pv.getProject().getName());
                throw new CheckoutException(
                        "No SCM accessor available for project "
                                + pv.getProject().getName());
            }
        } catch (InvalidAccessorException e) {
            throw new CheckoutException("Invalid SCM accessor for project "
                    + pv.getProject().getName() + " " + e.getMessage());
        }

        return true;
    }

    // Checkout cache ops
    /**
     * Atomic get from cache and increment handle count.
     */
    private synchronized OnDiskCheckout getCheckoutFromCache(ProjectVersion pv) {

        if (pv == null || pv.getId() == 0) {
            return null;
        }

        OnDiskCheckout co = checkoutCache.get(cacheKey(pv));

        if (co == null)
            return null;

        checkoutHandles.put(co, checkoutHandles.get(co) + 1);

        return co;
    }

    /**
     * Atomic decrement of checkout handle counts.
     */
    private synchronized void returnCheckout(OnDiskCheckout c) {
        if (c == null)
            return;

        if (checkoutHandles.contains(c))
            checkoutHandles.put(c, checkoutHandles.get(c) - 1);
    }

    /**
     * Atomic add checkout to both cache tables
     */
    private synchronized void addCheckoutToCache(ProjectVersion pv,
            OnDiskCheckout c) {
        checkoutCache.putIfAbsent(cacheKey(pv), c);
        checkoutHandles.putIfAbsent(c, 0);
    }

    /**
     * Atomically check whether the checkout can be updated
     */
    private synchronized boolean isUpdatable(OnDiskCheckout c) {
        if (checkoutHandles.get(c) > 0)
            return false;
        return true;
    }

    /**
     * Check if there is a checkout for a specific project version.
     */
    private synchronized boolean cacheContains(ProjectVersion pv) {
        if (checkoutCache.keySet().contains(cacheKey(pv)))
            return true;
        return false;
    }

    // Cache key ops
    /**
     * Munge together info from the provided project version to create a unique
     * key for indexing cache checkouts.
     */
    private String cacheKey(ProjectVersion pv) {
        return pv.getProject().getName() + "|" + pv.getId() + "|"
                + pv.getRevisionId();
    }

    /**
     * Retrieve the project name part of the provided cache key.
     */
    private String cacheKeyProject(String key) {
        if (key == null || key.length() == 0)
            return null;

        return key.split("|")[0];
    }

    /**
     * Retrieve from the provided cache key and resolve from the DB the
     * ProjectVersion object attached to a checkout.
     */
    private ProjectVersion cacheKeyProjectVersion(String key) {
        if (key == null || key.length() == 0)
            return null;

        DBService dbs = AlitheiaCore.getInstance().getDBService();

        Long id = Long.parseLong(key.split("|")[1]);
        return dbs.findObjectById(ProjectVersion.class, id);
    }

    /**
     * Convert between database and SCM revision representations
     */
    private static Revision projectVersionToRevision(ProjectVersion pv) {
        TDSService tds = AlitheiaCore.getInstance().getTDSService();
        SCMAccessor scm = null;

        if (tds.accessorExists(pv.getProject().getId())) {
            scm = (SCMAccessor) tds.getAccessor(pv.getProject().getId());
        } else {
            return null;
        }

        return scm.newRevision(pv.getRevisionId());
    }

    // ===[ INTERFACE METHODS ]===============================================

    /** {@inheritDoc} */
    public synchronized File getFile(ProjectFile pf) {
        Revision projectRevision = projectFileRevision(pf);
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
            if (!(checkoutFile.exists() && checkoutFile.length() <= 0)) {
                // Create the path to the target file if it doesn't exist
                if ((checkoutFile.getParentFile() != null)
                        && (!checkoutFile.getParentFile().exists())) {
                    checkoutFile.getParentFile().mkdirs();
                }
                // Try to checkout the target file
                scm.getFile(pf.getFileName(), projectRevision, checkoutFile);
            }

            // Make sure that the target file is accessible
            if ((checkoutFile.exists()) && (checkoutFile.isFile())
                    && (checkoutFile.canRead())) {
                return checkoutFile;
            }
            // returning null here is fine
        } catch (InvalidRepositoryException e) {
            logger.error("The repository for " + pf.toString()
                    + " is invalid: " + e.getMessage());
        } catch (InvalidProjectRevisionException e) {
            logger.error("The repository for "
                    + pf.getProjectVersion().getProject() + " has no revision "
                    + projectRevision + ":" + e.getMessage());
        } catch (FileNotFoundException e) {
            logger.error("File " + pf.toString() + " not found in the given "
                    + "repository: " + e.getMessage());
        }
        return null;
    }

    /** {@inheritDoc} */
    public InputStream getFileContents(ProjectFile pf) {

        Revision projectRevision = projectFileRevision(pf);
        if (projectRevision == null) {
            return null;
        }

        SCMAccessor scm = projectFileAccessor(pf);
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        try {
            scm.getFile(pf.getFileName(), projectRevision, buff);
        } catch (InvalidProjectRevisionException e) {
            logger.error("The repository for " + pf.toString()
                    + " is invalid: " + e.getMessage());
        } catch (InvalidRepositoryException e) {
            logger.error("The repository for "
                    + pf.getProjectVersion().getProject() + " has no revision "
                    + projectRevision + ":" + e.getMessage());
        } catch (FileNotFoundException e) {
            logger.error("File " + pf.toString() + " not found in the given "
                    + "repository: " + e.getMessage());
        }

        ByteArrayInputStream contents = new ByteArrayInputStream(buff
                .toByteArray());
        return contents;
    }

    /** {@inheritDoc} */
    public InMemoryCheckout getInMemoryCheckout(ProjectVersion pv)
            throws CheckoutException {
        return getInMemoryCheckout(pv, Pattern.compile(".*"));
    }

    /** {@inheritDoc} */
    public InMemoryCheckout getInMemoryCheckout(ProjectVersion pv,
            Pattern pattern) throws CheckoutException {

        if (!canCheckout(pv)) {
            return null;
        }

        long projectId = pv.getProject().getId();
        SCMAccessor svn = null;
        try {
            svn = tds.getAccessor(projectId).getSCMAccessor();
        } catch (InvalidAccessorException e) {
            throw new CheckoutException("Invalid SCM accessor for project "
                    + pv.getProject().getName() + ": " + e.getMessage());
        }
        svn.newRevision(pv.getRevisionId());
        logger
                .info("Finding available checkout for "
                        + pv.getProject().getName() + " revision "
                        + pv.getRevisionId());

        return new InMemoryCheckoutImpl(pv, pattern);
    }

    /** {@inheritDoc} */
    public OnDiskCheckout getCheckout(ProjectVersion pv)
            throws CheckoutException {

        if (!canCheckout(pv)) {
            return null;
        }

        long projectId = pv.getProject().getId();
        SCMAccessor svn = null;
        try {
            svn = tds.getAccessor(projectId).getSCMAccessor();
        } catch (InvalidAccessorException e) {
            throw new CheckoutException("Invalid SCM accessor for project "
                    + pv.getProject().getName() + ": " + e.getMessage());
        }

        svn.newRevision(pv.getRevisionId());

        logger.info("Finding available checkout for " + pv);
        OnDiskCheckout co = getCheckoutFromCache(pv);

        if (co != null) {
            // Checkout acquired from cache, return it.
            return co;
        }

        // Search for a cached checkout that could be updated
        Set<String> c = checkoutCache.keySet();
        OnDiskCheckoutImpl updatable = null;

        for (String s : c) {
            if (cacheKeyProject(s).equals(pv.getProject())) {
                ProjectVersion cached = cacheKeyProjectVersion(s);
                if (cached.lt(pv)) {
                    updatable = (OnDiskCheckoutImpl) getCheckoutFromCache(cached);

                    if (checkoutHandles.get(updatable) == 1) {
                        try {
                            updateCheckout(updatable, pv);
                        } finally {
                            releaseCheckout(updatable);
                        }
                        return getCheckoutFromCache(pv);
                    }
                    releaseCheckout(updatable);
                    updatable = null;
                }
            }
        }

        // No updatable checkout found, create
        synchronized (pv) {
            if (!cacheContains(pv))
                addCheckoutToCache(pv, createCheckout(svn, pv));
        }
        return getCheckoutFromCache(pv);
    }

    /** {@inheritDoc} */
    public boolean updateCheckout(OnDiskCheckout c, ProjectVersion pv)
            throws CheckoutException {

        if (c != null) {
            return false;
        }

        // Check if the checkout is held by another client before updating
        if (!isUpdatable(c)) {
            return false;
        }

        OnDiskCheckoutImpl cimpl = (OnDiskCheckoutImpl) c;
        cimpl.lock();

        // Check if an update took place while waiting for the lock to become
        // available
        if (cimpl.getProjectVersion().gt(pv)) {
            logger.error("Error updating checkout. Checkout has been"
                    + " already updated to a newer version");
            throw new CheckoutException("Checkout already updated");
        } else if (cimpl.getProjectVersion().eq(pv)) {
            return true;
        }

        SCMAccessor scm = (SCMAccessor) AlitheiaCore.getInstance()
                .getTDSService().getAccessor(pv.getProject().getId());
        try {
            scm.updateCheckout(cimpl.getRepositoryPath(),
                    projectVersionToRevision(cimpl.getProjectVersion()),
                    projectVersionToRevision(pv), cimpl.getRoot());
            cimpl.setRevision(pv);

        } catch (InvalidProjectRevisionException e) {
            throw new CheckoutException("Project version " + pv
                    + " does not map to an SCM revision. Error was:"
                    + e.getMessage());
        } catch (InvalidRepositoryException e) {
            throw new CheckoutException("Error accessing repository "
                    + scm.toString() + ". Error was:" + e.getMessage());
        } catch (FileNotFoundException e) {
            throw new CheckoutException("Error accessing checkout root. "
                    + e.getMessage());
        } finally {
            cimpl.unlock();
        }
        return true;
    }

    /** {@inheritDoc} */
    public void releaseCheckout(OnDiskCheckout c) {

        if (c == null) {
            logger.warn("Attempting to release null checkout");
            return;
        }

        if (!checkoutCache.contains(c)) {
            logger.warn("Attempting to release not cached checkout");
            return;
        }

        returnCheckout(c);
    }

    public Timeline getTimeline(StoredProject c) {
        return new TimelineImpl(c);
    }

    public void run() {

    }

    @Override
    public void setInitParams(BundleContext bc, Logger l) {
        logger = l;
        this.bc = bc;
    }

    @Override
    public void shutDown() {
        String s = bc.getProperty("eu.sqooss.fds.cleanupOnExit");

        if (s != null && s.equals("true")) {
            CleanupThread t = new CleanupThread("FDS cleanup thread");
            Runtime.getRuntime().addShutdownHook(t);
            logger.info("Registered shutdown cleanup thread");
        }
    }

    @Override
    public boolean startUp() {
        tds = AlitheiaCore.getInstance().getTDSService();
        logger.info("Got TDS service for FDS.");

        checkoutCache = new ConcurrentHashMap<String, OnDiskCheckout>();
        checkoutHandles = new ConcurrentHashMap<OnDiskCheckout, Integer>();
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

        return true;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

