package eu.sqooss.impl.service;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.alitheia.Logger;
import eu.sqooss.impl.service.alitheia.LoggerHelper;
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
	
	public void start(BundleContext bc) throws Exception {
		try{
			Properties props = new Properties();
			props.put("org.omg.CORBA.ORBInitialPort", "1050");
			
			// create and initialize the ORB
			ORB orb = ORB.init(new String[0], props);
			
			// get reference to rootpoa & activate the POAManager
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();
			
			// create servant and register it with the ORB
			LoggerImpl loggerImpl = new LoggerImpl(bc);
			
			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(loggerImpl);
			Logger logRef = LoggerHelper.narrow(ref);
			
			// get the root naming context
			//The string "NameService" is defined for all CORBA ORBs.
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			
			//objRef is a generic object reference. We must narrow it down
			// to the interface we need.
			// Use NamingContextExt which is part of the Interoperable
			// Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			
			// bind the Object Reference with the Naming Service.
			String name = "Logger";
			NameComponent path[] = ncRef.to_name(name);
			//pass the name to the Naming Service, binding the logRef to the string
			// "Logger"
			ncRef.rebind(path, logRef);
			
			orbthread = new ORBThread(orb);
			orbthread.start();
			
			//loggerImpl.info(,"CORBA Service ready and waiting...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop(BundleContext context) throws Exception {
		orbthread.shutdown();
	}

}
