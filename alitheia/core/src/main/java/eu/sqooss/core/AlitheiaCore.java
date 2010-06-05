/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.http.HttpService;

import eu.sqooss.impl.service.cluster.ClusterNodeServiceImpl;
import eu.sqooss.impl.service.db.DBServiceImpl;
import eu.sqooss.impl.service.fds.FDSServiceImpl;
import eu.sqooss.impl.service.logging.LogManagerImpl;
import eu.sqooss.impl.service.metricactivator.MetricActivatorImpl;
import eu.sqooss.impl.service.pa.PAServiceImpl;
import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.impl.service.security.SecurityManagerImpl;
import eu.sqooss.impl.service.tds.TDSServiceImpl;
import eu.sqooss.impl.service.updater.UpdaterServiceImpl;
import eu.sqooss.impl.service.webadmin.WebadminServiceImpl;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.webadmin.WebadminService;

/**
 * Startup class of the SQO-OSS framework's core. Its main goal is to
 * initialize all core components and be able to provide them upon request.
 * There is one AlitheiaCore instance which may be retrieved statically
 * through getInstance(); after that you can use the get*Service() methods
 * to get each of the other core components as needed.
 */
public class AlitheiaCore implements ServiceListener {

    /** The Logger component's instance. */
    private LogManagerImpl logger;
    
    /** The parent bundle's context object. */
    private BundleContext bc;
    
    /** The Core is singleton-line because it has a special instance */
    private static AlitheiaCore instance = null;
    
    /** Classes to services to wait for prior to self initialisation*/
    private Vector<Class<?>> srvWait;
    
    /** Flag set when the init routines have run*/
    private AtomicBoolean initFlag;
    
    /** Holds initialised service instances */
    private HashMap<Class<? extends AlitheiaCoreService>, Object> instances;
    
    /* Service Configuration */
    private static List<Class<? extends AlitheiaCoreService>> services;
    private static Map<Class<? extends AlitheiaCoreService>, Class<?>> implementations;

    static {
    	services = new ArrayList<Class<? extends AlitheiaCoreService>>();
    	implementations = new HashMap<Class<? extends AlitheiaCoreService>, Class<?>>();
    	
    	/* 
    	 * Order matters here as services are initialised 
    	 * in the order they appear in this list
    	 */
    	//services.add(LogManager.class); //This service is started manually
    	services.add(DBService.class);	
    	//All services after this point are guaranteed to have access to the DB 
    	services.add(PluginAdmin.class);
    	services.add(Scheduler.class);
    	services.add(TDSService.class);
    	services.add(ClusterNodeService.class);
    	services.add(FDSService.class);
    	services.add(MetricActivator.class);
    	//services.add(Parser.class);
    	services.add(SecurityManager.class);
    	services.add(UpdaterService.class);
    	services.add(WebadminService.class);
    	
    	implementations.put(LogManager.class, LogManagerImpl.class);
    	implementations.put(DBService.class, DBServiceImpl.class);	 
    	implementations.put(PluginAdmin.class, PAServiceImpl.class);
    	implementations.put(Scheduler.class, SchedulerServiceImpl.class);
    	implementations.put(TDSService.class, TDSServiceImpl.class);
    	implementations.put(ClusterNodeService.class, ClusterNodeServiceImpl.class);
    	implementations.put(FDSService.class, FDSServiceImpl.class);
    	implementations.put(MetricActivator.class, MetricActivatorImpl.class);
    	//implementations.put(Parser.class, Parse);
    	implementations.put(SecurityManager.class, SecurityManagerImpl.class);
    	implementations.put(UpdaterService.class, UpdaterServiceImpl.class);
    	implementations.put(WebadminService.class, WebadminServiceImpl.class);
    }
   
    /**
     * Simple constructor.
     * 
     * @param bc The parent bundle's context object.
     */
    public AlitheiaCore(BundleContext bc) {
        this.bc = bc;
        instance = this;
        err("Instance Created");
        
        initFlag = new AtomicBoolean();
        initFlag.set(false);
        
        instances = new HashMap<Class<? extends AlitheiaCoreService>, Object>();
        
        srvWait = new Vector<Class<?>>();
        srvWait.add(HttpService.class);
        srvWait.add(EventAdmin.class);
        //checkForServicesAndInit();
    }

    /**
     * The core has a blessed instance which you can get from here;
     * that instance in turn will give you the DB service and others
     * that it holds on to. So code that needs a particular service
     * can use AlitheiaCore.getInstance().get*Service() to get
     * a reference to specific services.
     * 
     * @return Instance, or null if it's not initialized yet
     */
    public static AlitheiaCore getInstance() {
        return instance;
    }
    
    /**
     * This method performs initialization of the <code>AlitheiaCore</code>
     * object by instantiating the core components, by calling the 
     * method on their service interface. After all services have been 
     * instantiated (failures are reported but do not block the instatiation
     * process), the system initialises the metric bundles. To do so, it 
     * queries all bundles for those that contain a <code>*.metrics.*</code>
     * portion in their symbolic path and starts those that do. 
     */
    public void init() {
    	
        if (initFlag.get() == true) {
            return;
        }
        err("Required services online, initialising");
        initFlag.compareAndSet(false, true);

        logger = new LogManagerImpl();
        logger.setInitParams(bc, null);
        if (!logger.startUp()) {
            err("Cannot start the log service, aborting");
        }
        instances.put(LogManager.class, logger);

        for (Class<? extends AlitheiaCoreService> s : services) {
            Class<?> impl = implementations.get(s);
            try {
                Object o = impl.newInstance();

                if (o == null) {
                    err("Service object for service " + s
                            + " could not be created");
                    continue;
                }
                
                //Extract the unique service portion of the class FQN.
                //e.g. from eu.sqooss.service.db.DBService -> db
                String[] paths = s.getCanonicalName().split("\\.");
                
                /* Logger names are constructed as per */
                s.cast(o).setInitParams(bc,
                        logger.createLogger("sqooss." + paths[3]));

                if (!s.cast(o).startUp()) {
                    err("Service " + s + " could not be started");
                    return;
                }
                
                instances.put(s, s.cast(o));
                err("Service " + impl.getName() + " started");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        err("Initialising metric bundles");
        for (Bundle b : bc.getBundles()) {
            if (!b.getSymbolicName().contains(".metrics."))
                continue; //Not a metrics bundle
            
            try {
                b.start();
                err("Starting " + b.getSymbolicName());
            } catch (BundleException e) {
                err("Failed to start " + b.getSymbolicName());
                e.printStackTrace();
            }
        }
    }
    
    public void shutDown() {
    	List<Class<? extends AlitheiaCoreService>> revServices = 
    		new ArrayList<Class<? extends AlitheiaCoreService>>(services);
    	Collections.reverse(revServices);
    	
    	for (Class<? extends AlitheiaCoreService> s : revServices) {
    		Object o = instances.get(s);
    		try	{
    			s.cast(o).shutDown();
    			instances.remove(s);
    		} catch (Throwable t) {
    			t.printStackTrace();
			}    		
    	}
	}

    /**
     * Returns the locally stored Logger component's instance.
     * 
     * @return The Logger component's instance.
     */
    public LogManager getLogManager() {
        return (LogManager)instances.get(LogManager.class);
    }

    /**
     * Returns the locally stored WebAdmin component's instance.
     * 
     * @return The WebAdmin component's instance.
     */
    public WebadminService getWebadminService() {
        return (WebadminService)instances.get(WebadminService.class);
    }

    /**
     * Returns the locally stored Plug-in Admin component's instance.
     * 
     * @return The Plug-in Admin component's instance.
     */
    public PluginAdmin getPluginAdmin() {
        return (PluginAdmin)instances.get(PluginAdmin.class);
    }

    /**
     * Returns the locally stored DB component's instance.
     * 
     * @return The DB component's instance.
     */
    public DBService getDBService() {
        return (DBServiceImpl)instances.get(DBService.class);
    }
    
    /**
     * Returns the locally stored Parser componenet's instance.
     * 
     * @return The Parser component's instance.
     */
    /*public Parser getParser() {
    	if(parser == null) {
    		parser = new ParserImpl(getLogManager().createLogger(Logger.NAME_SQOOSS_PARSER));
    	}
    	
    	return parser;
    }*/

    /**
     * Unused check of the core instance for liveness. Because the instance
     * might not lee without the rest of the bikini services, we need to
     * check that they are present.
     * Added after evening discussion (<i>some 5 pints and a bunch of naked
     * bikini models later<i>) at Amarilia on liveness.
     */
    private static boolean canLee(boolean touLiBouDiBouDauTcou) {
        return (null != instance) && touLiBouDiBouDauTcou;
    }
    
    /**
     * Returns the locally stored FDS component's instance.
     * <br/>
     * <i>The instance is created when this method is called for a first
     * time.</i>
     * 
     * @return The FDS component's instance.
     */
    public FDSService getFDSService() {
        return (FDSService)instances.get(FDSService.class);
    }

    /**
     * Returns the locally stored Scheduler component's instance.
     * <br/>
     * <i>The instance is created when this method is called for a first
     * time.</i>
     * 
     * @return The Scheduler component's instance.
     */
    public Scheduler getScheduler() {
        return (Scheduler)instances.get(Scheduler.class);
    }

    /**
     * Returns the locally stored Security component's instance.
     * <br/>
     * <i>The instance is created when this method is called for a first
     * time.</i>
     * 
     * @return The Security component's instance.
     */
    public SecurityManager getSecurityManager() {
        return (SecurityManager)instances.get(SecurityManager.class);
    }

    /**
     * Returns the locally stored TDS component's instance.
     * <br/>
     * <i>The instance is created when this method is called for a first
     * time.</i>
     * 
     * @return The TDS component's instance.
     */
    public TDSService getTDSService() {
        return (TDSService)instances.get(TDSService.class);
    }

    /**
     * Returns the locally stored Updater component's instance.
     * <br/>
     * <i>The instance is created when this method is called for a first
     * time.</i>
     * 
     * @return The Updater component's instance.
     */
    public UpdaterService getUpdater() {
        return (UpdaterService)instances.get(UpdaterService.class);
    }

    /**
     * Returns the locally stored ClusterNodeService component's instance.
     * <br/>
     * <i>The instance is created when this method is called for a first
     * time.</i>
     * 
     * @return The ClusterNodeSerive component's instance.
     */
    public ClusterNodeService getClusterNodeService() {
        return (ClusterNodeService)instances.get(ClusterNodeService.class);
    }

    
    
    /**
     * Returns the locally stored Metric Activator component's instance.
     * 
     * <i>The instance is created when this method is called for a first
     * time.</i>
     * 
     * @return The Metric Activator component's instance.
     */
    public MetricActivator getMetricActivator() {
    	return (MetricActivator)instances.get(MetricActivator.class);
    }
    
	@Override
	public synchronized void serviceChanged(ServiceEvent event) {
		if (event.getType() == ServiceEvent.REGISTERED) {
			if (event.getServiceReference().toString().matches(".*" 
					+ HttpService.class.getName() + ".*")) {
				err("HTTP service registered");
			}
			
			if (event.getServiceReference().toString().matches(".*" 
					+ EventAdmin.class.getName() + ".*")) {
				err("Event service registered");
			}
			checkForServicesAndInit();
		}
	}

	private synchronized void checkForServicesAndInit() {
		boolean init = true;
		
		for (Class<?> srvClass : srvWait) {
			ServiceReference sr  =  bc.getServiceReference(srvClass.getName());
			if (sr == null) {
				init = false;
				continue;
			}
			
			Object o = bc.getService(sr);
			
			if (o == null) {
				init = false;
				continue;
			}
		}
		
		if (init == true) {
			init();
		}
	}
	
	private void err(String msg) {
		System.err.println("AlitheiaCore: " + msg);
	}
}

// vi: ai nosi sw=4 ts=4 expandtab
