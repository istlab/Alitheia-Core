package eu.sqooss.impl.service.corba.alitheia.db;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.impl.service.corba.alitheia.DatabasePOA;

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

    public boolean addRecord(org.omg.CORBA.Any dbObject) {
        return db.addRecord(DAObject.fromCorbaObject(dbObject));
    }
}
