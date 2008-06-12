package eu.sqooss.impl.service.corba.alitheia.fds;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.corba.alitheia.Checkout;
import eu.sqooss.impl.service.corba.alitheia.FDSPOA;
import eu.sqooss.impl.service.corba.alitheia.ProjectFile;
import eu.sqooss.impl.service.corba.alitheia.ProjectVersion;
import eu.sqooss.impl.service.corba.alitheia.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.InMemoryCheckout;
import eu.sqooss.service.fds.InMemoryDirectory;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.ProjectRevision;

/**
 * Wrapper class provided to export the FDS into the Corba ORB.
 * @author Christoph Schleifenbaum, KDAB
 */
public class FDSImpl extends FDSPOA {

	protected FDSService fds = null;
	protected DBService db = null;
	
	protected Map<ProjectVersion, Checkout> checkouts = new HashMap< ProjectVersion, Checkout>(); 
	
	public FDSImpl(BundleContext bc) {
        ServiceReference serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        AlitheiaCore core = (AlitheiaCore) bc.getService(serviceRef);
        if (core == null) {
            System.out.println("CORBA database could not get the Alitheia core");
            return;
        }
        fds = core.getFDSService();
        db = core.getDBService();
	}

    /**
     * Gets the contents of \a file.
     * @param contents Corba string holder to put the content int.
     * @return The size of the file. 
     */
    public byte[] getFileContents(ProjectFile file) {
        db.startDBSession();
        byte[] result = new byte[0];
        InputStream stream = null;
        try {
            stream = fds.getFileContents(DAObject.fromCorbaObject(file));
            final int length = 1024;
            int read = 0;
            do {
                byte[] part = new byte[length];
                read = stream.read(part);
                if (read>0) {
                	byte[] readSoFar = new byte[result.length + read];
                	System.arraycopy(result, 0, readSoFar, 0, result.length);
                	System.arraycopy(part, 0, readSoFar, result.length, read);
                    result = readSoFar;
                }
            } while (read==length);
        } catch ( Exception e ) {
            return result;
        } finally {
            db.commitDBSession();
        }
        return result;
    }

    /**
     * Gets the contents of \a file.
     * @param contents Corba string holder to put the content int.
     * @return The size of the file. 
     */
    public byte[] getFileContentParts(ProjectFile file, int begin, int length) {
        db.startDBSession();
        byte[] result = new byte[0];
        byte[] content = new byte[length];
        int bytesRead = 0;
        InputStream stream = null;
        try {
            stream = fds.getFileContents(DAObject.fromCorbaObject(file));
            stream.skip(begin);
            bytesRead = stream.read(content);
            if (bytesRead>0) {
            	result = new byte[bytesRead];
            	System.arraycopy(content, 0, result, 0, bytesRead);
            }
        } catch ( Exception e ) {
        	return result;
        } finally {
            db.commitDBSession();
        }
        return result;
    }

	/**
	 * Get all files within \a dir.
	 * Works recursively.
	 */
	protected List<eu.sqooss.service.db.ProjectFile> getFiles(InMemoryDirectory dir) {
		List<eu.sqooss.service.db.ProjectFile> files = dir.getFiles();
		for (InMemoryDirectory subdir : dir.getSubDirectories()) {
			files.addAll(getFiles(subdir));
		}
		return files;
	}
	
	/**
	 * Creates a new checkout.
	 * @param version The ProjectVersion to create the checkout for.
	 * @return A reference to a Corba style checkout.
	 */
	protected Checkout createCheckout(eu.sqooss.service.db.ProjectVersion version, String pattern) throws InvalidRepositoryException, InvalidProjectRevisionException {
		ProjectRevision rev = new ProjectRevision(version.getVersion());
		InMemoryCheckout co = fds.getInMemoryCheckout(version.getProject().getId(), rev, Pattern.compile(pattern));
		
		List<eu.sqooss.service.db.ProjectFile> files = getFiles(co.getRoot());
		
		Checkout result = new Checkout();
		result.version = DAObject.toCorbaObject(version);
		result.files = new ProjectFile[files.size()];
		for (int i = 0; i < files.size(); ++i ) {
			result.files[ i ] = DAObject.toCorbaObject(files.get(i));
		}

		return result;
	}

	/**
	 * Gets a checkout.
	 * @param version The ProjectVersion to create the checkout for.
	 */
	public Checkout getCheckout(ProjectVersion version, String pattern) {
        db.startDBSession();
		Checkout result = null;
        try {
            result = createCheckout(DAObject.fromCorbaObject(version), pattern);
        } catch (InvalidRepositoryException e) {
            // just returns null, then;
        } catch (InvalidProjectRevisionException e) {
        } finally {
            db.commitDBSession();
        }
		return result;
	}

	/**
	 * Releases a checkout. I.e. delete it from the cache.
	 * @param version The ProjectVersion to release the checkout for.
	 */
	public void releaseCheckout(ProjectVersion version) {
		checkouts.remove(version);
	}
}
