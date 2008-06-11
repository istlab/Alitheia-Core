package eu.sqooss.impl.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.impl.service.corba.alitheia.CoreHelper;
import eu.sqooss.impl.service.corba.alitheia.DatabaseHelper;
import eu.sqooss.impl.service.corba.alitheia.FDSHelper;
import eu.sqooss.impl.service.corba.alitheia.LoggerHelper;
import eu.sqooss.impl.service.corba.alitheia.SchedulerHelper;
import eu.sqooss.impl.service.corba.alitheia.core.CoreImpl;
import eu.sqooss.impl.service.corba.alitheia.logger.LoggerImpl;
import eu.sqooss.impl.service.corba.alitheia.scheduler.SchedulerImpl;
import eu.sqooss.impl.service.corba.alitheia.db.DbImpl;
import eu.sqooss.impl.service.corba.alitheia.fds.FDSImpl;

public class CorbaActivator implements BundleActivator {

	/**
	 * ORBThread is a tread running an ORB in the background.
	 * @author Christoph Schleifenbaum, KDAB
	 */
    private class ORBThread extends Thread
    {
        private ORB orb;
        
        /**
         * Creates a new ORBThread instance
         * @param orb The 
         */
        public ORBThread(ORB orb) {
            this.orb = orb;
        }
    
        /**
         * Runs the ORB in his thread, until it's stopped
         * using shutdown().
         * {@inheritDoc}
         */
        public void run() {
            orb.run();
        }
        
        /**
         * Stops the ORB thread.
         */
        public void shutdown() {
            orb.shutdown(true);
        }
    }
    
    private ORBThread orbthread;
    
    private NamingContextExt ncRef;
    private POA rootpoa;
    private static CorbaActivator instance;
    private ORB orb;
    
    private BundleContext bc;
    private LoggerImpl loggerImpl;
    
    private Map< Integer, ServiceRegistration > registrations;
    
    /**
     * {@inheritDoc}
     */
    public void start(BundleContext bc) throws Exception {
        
        instance = this;

        this.bc = bc;
        
        loggerImpl = new LoggerImpl(bc);
        if (!Boolean.valueOf(bc.getProperty("eu.sqooss.corba.enable"))) {
            loggerImpl.info(eu.sqooss.service.logging.Logger.NAME_SQOOSS, 
                "Corba service disabled by config value.");
            return;
        } else {
            loggerImpl.info(eu.sqooss.service.logging.Logger.NAME_SQOOSS, 
                "Corba service starting.");
        }

        // create servant and register it with the ORB
        try {
            // standard ORB settings
            String[] orb_args = new String[2];
            orb_args[0] = "-ORBInitRef";
            orb_args[1] = "NameService=" + bc.getProperty("eu.sqooss.corba.orb.nameservice");
            
            // create and initialize the ORB
            Properties p = new Properties();
            p.setProperty("com.sun.CORBA.codeset.charsets", "0x05010001, 0x00010109");    // UTF-8, UTF-16
            orb = ORB.init( orb_args, p );
            
            // get reference to rootpoa & activate the POAManager
            rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();
            
            // get the root naming context
            //The string "NameService" is defined for all CORBA ORBs.
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            
            //objRef is a generic object reference. We must narrow it down
            // to the interface we need.
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            ncRef = NamingContextExtHelper.narrow(objRef);
            
            // this list contains all service registrations made via the core 
            registrations = new HashMap< Integer, ServiceRegistration >();
            
            // register the logger in CORBA
            registerCorbaObject("AlitheiaLogger", LoggerHelper.narrow(rootpoa.servant_to_reference(loggerImpl)));
            
            // register the core in CORBA
            registerCorbaObject("AlitheiaCore", CoreHelper.narrow(rootpoa.servant_to_reference(new CoreImpl(bc))));
 
            // register the database in CORBA
            registerCorbaObject("AlitheiaDatabase", DatabaseHelper.narrow(rootpoa.servant_to_reference(new DbImpl(bc))));
            
            // register the FDS in CORBA
            registerCorbaObject("AlitheiaFDS", FDSHelper.narrow(rootpoa.servant_to_reference(new FDSImpl(bc))));

            // register the Scheduler in CORBA
            registerCorbaObject("AlitheiaScheduler", SchedulerHelper.narrow(rootpoa.servant_to_reference(new SchedulerImpl(bc))));

            // start the ORB thread in the background
            orbthread = new ORBThread(orb);
            orbthread.start();
            
            loggerImpl.info(eu.sqooss.service.logging.Logger.NAME_SQOOSS, "CORBA Service ready and waiting...");
        } catch (Throwable t) {
            loggerImpl.error(eu.sqooss.service.logging.Logger.NAME_SQOOSS, "CORBA Service failed to connect to ORB.");
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void stop(BundleContext context) throws Exception {
        orbthread.shutdown();
        
        for (ServiceRegistration sr : registrations.values()) {
            sr.unregister();
        }
        
        instance = null;
    }
    
    /**
     * Registers an object from the ORB within Alitheia.
     * @param clazz The class name used used to register the service in the OSGi framework. 
     * @param service The service object.
     * @return The service ID of the registered service.
     */
    public synchronized int registerExternalCorbaObject(String clazz, Object service) {
        ServiceRegistration sr = bc.registerService(clazz, service, null);
        ServiceReference ref = sr.getReference();
        Long serviceId = (Long) ref.getProperty(Constants.SERVICE_ID);
        int id = serviceId.intValue();
        registrations.put(id, sr);
        loggerImpl.debug(eu.sqooss.service.logging.Logger.NAME_SQOOSS, "Registered a CORBA object to ID " + id);
        return id;
    }
    
    /**
     * Unregisters a service previously registered using registerExternalCorbaObject
     * @param id The id returned by registerExternalCorbaObject upon registration.
     */
    public synchronized void unregisterExternalCorbaObject(int id) {
        try {
            registrations.get(id).unregister();
            registrations.remove(id);
        } catch( Exception e ) {
            loggerImpl.error(eu.sqooss.service.logging.Logger.NAME_SQOOSS,
                             "Exception when trying ro unregister a CORBA object with ID " + id);
        }
    }
    
    /**
     * Registers an object in the ORB.
     * @param name The name used to identify the object in the ORB.
     * @param obj The object to be registered.
     */
    protected void registerCorbaObject(String name, org.omg.CORBA.Object obj) throws InvalidName, NotFound, CannotProceed {
        NameComponent path[] = ncRef.to_name(name);
        ncRef.rebind(path, obj);
    }

    /**
     * Gets an external object out of the ORB.
     * @param name The name of the object in the ORB.
     * @return An corba object reference
     */
    public org.omg.CORBA.Object getExternalCorbaObject(String name) throws NotFound, CannotProceed, InvalidName
    {
        org.omg.CORBA.Object nameservice;
        try {
            nameservice = orb.resolve_initial_references("NameService");
        } catch (org.omg.CORBA.ORBPackage.InvalidName e) {
            loggerImpl.error(eu.sqooss.service.logging.Logger.NAME_SQOOSS, "CORBA Service couldn't get the NameService trying to get object \"" + name +"\".");
            return null;
        }
        NamingContext ncRef = NamingContextHelper.narrow(nameservice); 
        NameComponent path[] = { new NameComponent(name, "") };
        return ncRef.resolve(path);
    }

    /**
     * Returns the singleton instance of the CorbaActivator class.
     */
    public static CorbaActivator instance() {
        return instance;
    }
}
