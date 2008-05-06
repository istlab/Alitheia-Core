package eu.sqooss.impl.service.corba.alitheia.db;

import org.omg.CORBA.Any;
import org.omg.CORBA.AnyHolder;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.corba.alitheia.DatabasePOA;
import eu.sqooss.service.db.DBService;

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
        DAObject.db = db;
    }

	/**
	 * Add a new record to the system database, using the default database
	 * session. This should initialize any tables that are needed for storage of 
	 * project information.
	 * @param dbObject the record to persist into the database
	 * @return true if the record insertion succeeded, false otherwise
	 */
    public boolean addRecord(AnyHolder dbObject) {
        eu.sqooss.service.db.DAObject obj = DAObject.fromCorbaObject(dbObject.value);
        boolean result = db.addRecord(obj);
        dbObject.value = DAObject.toCorbaObject(obj);
        return result;
    }

    /**
     * Update an existing record in the system database, using the default database session.
     *
     * @param record the record to update in the database
     * @return true if the record update succeeded, false otherwise
     */
    public boolean updateRecord(AnyHolder record) {
        eu.sqooss.service.db.DAObject obj = DAObject.fromCorbaObject(record.value);
        boolean result = db.updateRecord(obj);
        record.value = DAObject.toCorbaObject(obj);
        return result;
    }

    /**
     * Delete an existing record from the system database, using the default 
     * database session.
     * @param record the record to remove from the database
     * @return true if the record deletion succeeded, false otherwise
     */
    public boolean deleteRecord(Any record) {
        return db.deleteRecord(DAObject.fromCorbaObject(record));
    }

    /**
     * A generic query method to retrieve a single DAObject subclass using its 
     * identifier. The return value is parameterized to the actual type of 
     * DAObject queried so no downcast is needed.
     * @param id the DAObject's identifier
     * @return the DAOObject if a match for the class and the identifier was 
     * found in the database, or null otherwise or if a database access error occured
     */
    @SuppressWarnings("unchecked")
    public Any findObjectById(Any type, int id) {
        Class<eu.sqooss.service.db.DAObject> classType = 
            (Class<eu.sqooss.service.db.DAObject>) DAObject.fromCorbaType(type);
        eu.sqooss.service.db.DAObject obj = db.findObjectById(classType, id);
        return DAObject.toCorbaObject(obj);
    }
}
