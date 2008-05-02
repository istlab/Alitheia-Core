package eu.sqooss.impl.service.corba.alitheia.db;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.impl.service.corba.alitheia.DatabasePOA;

/**
 * Wrapper class to enable the Database to be exported into the
 * Corba ORB.
 * @author Christoph Schleifenbaum, KDAB
 */
public class DbImpl extends DatabasePOA {
   
    protected DBService db = null;

	public DbImpl(BundleContext bc) {
		ServiceReference serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        AlitheiaCore core = (AlitheiaCore) bc.getService(serviceRef);
        if (core == null) {
            System.out.println("CORBA database could not get the Alitheia core");
            return;
        }
        db = core.getDBService();
    }

	/**
	 * Add a new record to the system database, using the default database
	 * session. This should initialize any tables that are needed for storage of 
	 * project information.
	 * @param dbObject the record to persist into the database
	 * @return true if the record insertion succeeded, false otherwise
	 */
    public boolean addRecord(org.omg.CORBA.Any dbObject) {
        return db.addRecord(DAObject.fromCorbaObject(dbObject));
    }
}
