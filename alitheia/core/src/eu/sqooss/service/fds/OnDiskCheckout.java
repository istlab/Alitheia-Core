package eu.sqooss.service.fds;

import java.io.File;
import java.io.FileNotFoundException;

import eu.sqooss.service.db.ProjectVersion;

/**
 * An <tt>OnDiskCheckout</tt> represents a working copy (checkout) of a project
 * somewhere within the filesystem of the Alitheia system. A checkout
 * has a specific version attached to it. The checkout is supposed to 
 * be a read-only working copy; if the data of a checkout are modified 
 * in some way, the results are unspecified. Other parts
 * of the Alitheia system may access the same checkout concurrently.
 * Use the FDSService to obtain a checkout and remember to release it
 * when done. 
 * <p>
 *      <em>If the checkout is not released after use, then the checkout
 *      cannot be updated, and this means that a new copy of the checkout
 *      data will be created on the disk.</em> 
 * </p>
 * 
 * <p>
 * Typical use of a checkout looks like this:
 *
 * <pre>
 * Checkout c = fds.getCheckout(projectVersion);
 * File r = c.getRoot();
 * // Do stuff in the file system tree under r, but don't change anything!
 * fds.releaseCheckout(c);
 * </pre>
 * </p>
 */
public interface OnDiskCheckout {
    
    /**
     * Get the root within the Alitheia filesystem where the checkout lives,
     * for further manipulation with regular java.io methods.
     *
     * @return File representing the abstract path to the root of the
     *          checkout; all files live beneath this.
     *          
     * @throws FileNotFoundException When the root checkout directory cannot be
     * accessed. 
     * @throws CheckoutException When there is an error accessing the
     * SCM repository or when the checkout's revision does not resolve to 
     * an SCM revision
     */
    public File getRoot() throws FileNotFoundException, 
        CheckoutException;
    
    /**
     * Get the revision at which this checkout was made.
     *
     * @return Revision (resolved to both timestamp and SVN revision
     *          number) of this checkout. Will not change.
     */
    ProjectVersion getProjectVersion();

    /**
     * Get the path in the source code repository that this checkout represents
     * 
     * @return The repository path. 
     */
    String getRepositoryPath();
}
