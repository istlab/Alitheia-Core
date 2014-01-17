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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.logging.LogManagerImpl;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.rest.RestService;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.webadmin.WebadminService;

/**
 * Startup class of the Alitheia framework's core. Its main goal is to
 * initialize all core components and be able to provide them upon request.
 * There is one AlitheiaCore instance which may be retrieved statically
 * through getInstance(); after that you can use the get*Service() methods
 * to get each of the other core components as needed.
 */
@Singleton
public class AlitheiaCore {

    @Inject
    private LogManager logger;
    @Inject
    private DBService dbService;
    @Inject
    private PluginAdmin pluginAdmin;
    @Inject
    private Scheduler scheduler;
    @Inject
    private TDSService tdsService;
    @Inject
    private ClusterNodeService clusterNodeService;
    @Inject
    private FDSService fdsService;
    @Inject
    private MetricActivator metricActivator;
    @Inject
    private UpdaterService updaterService;
    @Inject
    private WebadminService webadminService;
    @Inject
    private RestService restService;
    @Inject
    private AdminService adminService;
    
    /** The parent bundle's context object. */
    @Inject
    private BundleContext bc;
    
    /** The Core is singleton-line because it has a special instance */
    private static AlitheiaCore instance;
    
    /** Holds initialised service instances */
    private HashMap<Class<? extends AlitheiaCoreService>, Object> instances;
    
    /* Service Configuration */
    private static Vector<Class<? extends AlitheiaCoreService>> services =
            new Vector<Class<? extends AlitheiaCoreService>>();
    private static Map<Class<? extends AlitheiaCoreService>, Class<?>> implementations =
            new HashMap<Class<? extends AlitheiaCoreService>, Class<?>>();
   
    /**
     * Simple constructor.
     * 
     * @param bc The parent bundle's context object.
     */
    public AlitheiaCore() {
        instance = this;
        err("Instance Created");
        
        instances = new HashMap<Class<? extends AlitheiaCoreService>, Object>();
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
    
    /*Create a temp instance to use for testing.*/
    public static AlitheiaCore testInstance() {
        instance = new AlitheiaCore();
        return instance;
    }
    
    /**
     * Register an external implementation of an AlitheiaCore service. It
     * will override any internally defined implementation.
     *
     * @param service The service interface to register an implementation for
     * @param clazz The class that implements the registered service
     */
    public synchronized void registerService(
            Class<? extends AlitheiaCoreService> service,
            Class<?> clazz) {

        if (!services.contains(service))
            services.add(service);
        implementations.put(service, clazz);
        initService(service);
    }

    /**
     * Unregisters an external implementation of an Alitheia Core service. 
     * This method does not check if external entities hold references to the
     * service to be unregistered.
     */
    public synchronized void unregisterService(
            Class<? extends AlitheiaCoreService> service) {
        implementations.remove(service);
    }

    /**
     * This method performs initialization of the <code>AlitheiaCore</code>
     * object by instantiating the core components, by calling the 
     * method on their service interface. Failures are reported but do not 
     * block the instatiation process).
     */
    public void init() {
        err("Required services online, initialising");

        logger.setInitParams(bc, null);
        if (!logger.startUp()) {
            err("Cannot start the log service, aborting");
        }
        instances.put(LogManager.class, logger);
        err("Service " + LogManagerImpl.class.getName() + " started");

        initInjectedService(dbService);
        initInjectedService(pluginAdmin);
        initInjectedService(scheduler);
        initInjectedService(tdsService);
        initInjectedService(clusterNodeService);
        initInjectedService(fdsService);
        initInjectedService(metricActivator);
        initInjectedService(updaterService);
        initInjectedService(webadminService);
        initInjectedService(restService);
        initInjectedService(adminService);

        for (Class<? extends AlitheiaCoreService> s : services) {
            initService(s);
        }

    }

    private synchronized void initInjectedService(AlitheiaCoreService service) {
        service.setInitParams(bc, createLogger(service.getClass()));
        if (service.startUp()) {
            err("Started service: " + service.getClass().getName());
        } else {
            err("Cannot start injected service: "
                    + service.getClass().getName());
        }
    }

    private synchronized void initService(Class<? extends AlitheiaCoreService> s) {
        Class<?> impl = implementations.get(s);

        if (impl == null) {
            err("No implementation found for service " + s);
            return;
        }

        try {
            Object o = impl.newInstance();

            if (o == null) {
                err("Service object for service " + s
                        + " could not be created");
                return;
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
    
    public Logger createLogger(Class<? extends AlitheiaCoreService> clazz) {
        // Extract the unique service portion of the class FQN.
        // e.g. from eu.sqooss.service.db.DBService -> db
        String[] paths = clazz.getCanonicalName().split("\\.");

        /* Logger names are constructed as per */
        return logger.createLogger("sqooss." + paths[3]);
    }

    /**
     * Returns the locally stored Logger component's instance.
     * 
     * @return The Logger component's instance.
     */
    public LogManager getLogManager() {
        return logger;
    }

    /**
     * Returns the locally stored WebAdmin component's instance.
     * 
     * @return The WebAdmin component's instance.
     */
    public WebadminService getWebadminService() {
        return webadminService;
    }

    /**
     * Returns the locally stored Plug-in Admin component's instance.
     * 
     * @return The Plug-in Admin component's instance.
     */
    public PluginAdmin getPluginAdmin() {
        return pluginAdmin;
    }

    /**
     * Returns the locally stored DB component's instance.
     * 
     * @return The DB component's instance.
     */
    public DBService getDBService() {
        return dbService;
        //return DBServiceImpl.getInstance(); // <-- Ugly but required for testing.
    }
    
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
        return fdsService;
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
        return scheduler;
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
        return tdsService;
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
        return updaterService;
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
        return clusterNodeService;
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
    	return metricActivator;
    }
    
    /**
     * Returns the locally stored Administration Service component's instance.
     * 
     * @return The Administration Service component's instance.
     */
    public AdminService getAdminService() {
    	return adminService;
    }
	
	private void err(String msg) {
		System.err.println("AlitheiaCore: " + msg);
	}
}

// vi: ai nosi sw=4 ts=4 expandtab
