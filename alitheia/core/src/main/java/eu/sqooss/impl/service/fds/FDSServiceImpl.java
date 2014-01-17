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
import java.io.FileNotFoundException;
import java.io.InputStream;

import eu.sqooss.service.util.FileUtils;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.fds.CheckoutException;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.OnDiskCheckout;
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
public class FDSServiceImpl implements FDSService {
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

    private BundleContext bc;
    
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
    private OnDiskCheckout createCheckout(SCMAccessor scm, ProjectVersion pv, String path) {
        logger.info("Creating new checkout for " + pv);

        File projectRoot = new File(fdsCheckoutRoot, pv.getProject().getName());
        // It might not exist yet
        projectRoot.mkdirs();

        // Side effect: throws if the revision is invalid
        Revision r = scm.newRevision(pv.getRevisionId());
        File checkoutRoot = new File(projectRoot, pv.getRevisionId());

        if (checkoutRoot.exists()) {
            logger.warn("Checkout root <" + checkoutRoot + "> exists. " +
                    "Cleaning up");
            FileUtils.deleteRecursive(checkoutRoot);
        }
        if (!checkoutRoot.mkdirs()) {
            logger.warn("Could not create checkout root <" + checkoutRoot
                    + ">");
            return null;
        }

        // Now checkoutRoot exists and is a directory.
        logger.info("Created checkout root <" + checkoutRoot + ">");
        OnDiskCheckoutImpl c = new OnDiskCheckoutImpl(scm, path, pv, checkoutRoot);
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
    public OnDiskCheckout getCheckout(ProjectVersion pv, String path)
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
        return createCheckout(svn, pv, path);
    }

    /** {@inheritDoc} */
    public void releaseCheckout(OnDiskCheckout c) {
        File root = null;
        try {
            root = c.getRoot();
            FileUtils.deleteRecursive(root);
        } catch (Exception e) {
            logger.error("Cannot clean up checkout root: " +
                    root.getAbsolutePath());
        }
        c = null;
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

        // Get the checkout root from the properties file.
        String s = bc.getProperty("eu.sqooss.fds.root");
        if (s == null) {
            logger.info("No eu.sqooss.fds.root set, using default /var/tmp/alitheia");
            s = "/var/tmp/alitheia";
        } else {
            logger.info("FDS root directory " + s);
        }
        fdsCheckoutRoot = new File(s);

        return true;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

