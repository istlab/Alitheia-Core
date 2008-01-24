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
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Constants;

import eu.sqooss.impl.service.alitheia.CoreHelper;
import eu.sqooss.impl.service.alitheia.LoggerHelper;
import eu.sqooss.impl.service.alitheia.core.CoreImpl;
import eu.sqooss.impl.service.alitheia.logger.LoggerImpl;

public class CorbaActivator implements BundleActivator {

    private class ORBThread extends Thread
    {
        private ORB orb;
        
        public ORBThread(ORB orb) {
            this.orb = orb;
        }
        
        public void run() {
            orb.run();
        }
        
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
    
    public void start(BundleContext bc) throws Exception {
        
        instance = this;

        this.bc = bc;
        
        // create servant and register it with the ORB
        loggerImpl = new LoggerImpl(bc);
        try{
            Properties props = new Properties();
            //props.put("org.omg.CORBA.ORBInitialPort", "1050");

            String nsHost = "localhost";
            String nsPort = "2809";
            
            String[] orb_args = new String[2];
            orb_args[0] = "-ORBInitRef";
            orb_args[1] = "NameService=corbaloc:iiop:1.2@" + nsHost + ":" + nsPort + "/NameService";
            
            // create and initialize the ORB
            orb = ORB.init( orb_args, props );
            
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
            registerCorbaObject("Logger", LoggerHelper.narrow(rootpoa.servant_to_reference(loggerImpl)));
            
            // register the core in CORBA
            registerCorbaObject("Core", CoreHelper.narrow(rootpoa.servant_to_reference(new CoreImpl(bc))));
            
            // start the ORB thread in the background
            orbthread = new ORBThread(orb);
            orbthread.start();
            
            loggerImpl.info(eu.sqooss.service.logging.Logger.NAME_SQOOSS, "CORBA Service ready and waiting...");
        } catch (Throwable t) {
            loggerImpl.error(eu.sqooss.service.logging.Logger.NAME_SQOOSS, "CORBA Service failed to connect to ORB.");
        }
    }

    public synchronized void stop(BundleContext context) throws Exception {
        orbthread.shutdown();
        
        for (ServiceRegistration sr : registrations.values()) {
            sr.unregister();
        }
        
        instance = null;
    }
    
    public synchronized int registerExternalCorbaObject(String clazz, Object service) {
        ServiceRegistration sr = bc.registerService(clazz, service, null);
        ServiceReference ref = sr.getReference();
        Long serviceId = (Long) ref.getProperty(Constants.SERVICE_ID);
        int id = serviceId.intValue();
        registrations.put(id, sr);
        return id;
    }
    
    public synchronized void unregisterExternalCorbaObject(int id) {
        registrations.get(id).unregister();
        registrations.remove(id);
    }
    
    protected void registerCorbaObject(String name, org.omg.CORBA.Object obj) throws InvalidName, NotFound, CannotProceed {
        NameComponent path[] = ncRef.to_name(name);
        ncRef.rebind(path, obj);
    }
    
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
    
    public static CorbaActivator instance() {
        return instance;
    }
}
