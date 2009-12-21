/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.sqooss.impl.service.admin.AdminServiceImpl;
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
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.parser.Parser;
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
    
    /** The DB component's instance. */
    private DBService db;
    
    /** The FDS component's instance. */
    private FDSService fds;
    
    /** The Scheduler component's instance. */
    private Scheduler sched;
    
    /** The Security component's instance. */
    private SecurityManager sec;
    
    /** The TDS component's instance. */
    private TDSService tds;
    
    /** The Updater component's instance. */
    private UpdaterService updater;
    
    /** The ClusterNode componen't instance. */
    private ClusterNodeService clusternode;
    
    /** The WebAdmin component's instance. */
    private WebadminService webadmin;
    
    /** The Plug-in Admin component's instance. */
    private PAServiceImpl padmin;
    
    /** The Metric Activator component's instance. */
    private MetricActivator ma;

    /** The parent bundle's context object. */
    private BundleContext bc;
    
    /** The parser service instance */
    private Parser parser;
    
    /** Instance of the admin service*/
    private AdminService admin;
    
    /** The Core is singleton-line because it has a special instance */
    private static AlitheiaCore instance = null;
    
    /** Classes to services to wait for prior to self initialisation*/
    private Vector<Class<?>> srvWait;
    
    /** Flag set when the init routines have run*/
    private AtomicBoolean initFlag;
    
    /**
     * Initializes an instance of the Logger component.
     */
    private void initLogger() {
        logger = new LogManagerImpl(bc);
    }

    /**
     * Initializes an instance of the DB component.
     */
    private void initDB() {
        db = new DBServiceImpl(bc,
                getLogManager().createLogger(Logger.NAME_SQOOSS_DATABASE));
        if (!((AlitheiaCoreService)db).init()) {
        	db = null;
        }
    }

    /**
     * Initializes an instance of the WebAdmin component.
     */
    private void initWebAdmin() {
        webadmin = new WebadminServiceImpl(bc,
                getLogManager().createLogger(Logger.NAME_SQOOSS_WEBADMIN));
    }

    /**
     * Initializes an instance of the Plug-in Admin component.
     */
    private void initPluginAdmin() {
    	padmin = new PAServiceImpl(bc,
                getLogManager().createLogger(Logger.NAME_SQOOSS_PA));
    }
    
    /**
     * Initializes an instance of the Plug-in Admin component.
     */
    private void initAdminService() {
    	admin = new AdminServiceImpl(bc,
    			getLogManager().createLogger(Logger.NAME_SQOOSS_ADMINACTION));
	}

    /**
     * Simple constructor.
     * 
     * @param bc The parent bundle's context object.
     */
    public AlitheiaCore(BundleContext bc) {
        this.bc = bc;
        if (null == instance) {
            instance = this;
            System.out.println("Alitheia Core: Instance Created");
        }
        
        initFlag = new AtomicBoolean();
        initFlag.set(false);
        
        srvWait = new Vector<Class<?>>();
        srvWait.add(HttpService.class);
        srvWait.add(EventAdmin.class);
        checkForServicesAndInit();
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
     * object by instantiating some of the core components, that are
     * either used by this object, or have to be created before any other
     * component is initialized or any metric plug-in service started.
     * <br/>
     * The list of created <i>(in this order)</i> instances include:
     * <ul>
     *   <li> Logger component
     *   <li> DB component
     *   <li> Plug-in Admin component
     *   <li> WebAdmin component
     * </ul>
     */
    public void init() {
    	
    	if (initFlag.get() == true) {
    		return;
    	}
		System.err.println("AlitheiaCore: Required services online, initialising");
    	initFlag.compareAndSet(false, true);
    		
    	// *** NOTE: Do not change the initialization order! ***
        // Create an instance of the Logger component.
        initLogger();
        // Create an instance of the DB component.
        initDB();
        // Create an instance of the PluginAdmin component
        initPluginAdmin();
        // Create an instance of the WebAdmin component
        initWebAdmin();
        // Create an instance of the Administration service component
        initAdminService(); 
    }
    
    public void shutDown() {
    	((AlitheiaCoreService)db).shutDown();
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
        return webadmin;
    }

    /**
     * Returns the locally stored Plug-in Admin component's instance.
     * 
     * @return The Plug-in Admin component's instance.
     */
    public PluginAdmin getPluginAdmin() {
        return padmin;
    }

    /**
     * Returns the locally stored DB component's instance.
     * 
     * @return The DB component's instance.
     */
    public DBService getDBService() {
        return db;
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
        if (fds == null) {
        	fds = new FDSServiceImpl(bc,
                    getLogManager().createLogger(Logger.NAME_SQOOSS_FDS));
        }
        return fds;
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
        if (sched == null) {
        	sched = new SchedulerServiceImpl(bc,
                    getLogManager().createLogger(
                            Logger.NAME_SQOOSS_SCHEDULING));
        }
        return sched;
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
        if (sec == null) {
        	sec = new SecurityManagerImpl(bc,
                    getLogManager().createLogger(
                            Logger.NAME_SQOOSS_SECURITY));
        }
        return sec;
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
        if (tds == null) {
        	tds = new TDSServiceImpl(bc, 
                    getLogManager().createLogger(Logger.NAME_SQOOSS_TDS));
        }
        return tds;
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
        if (updater == null) {
            try {
            	updater = new UpdaterServiceImpl(bc,
                        getLogManager().createLogger(
                                Logger.NAME_SQOOSS_UPDATER));
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (NamespaceException e) {
                e.printStackTrace();
            }
        }
        return updater;
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
        if (clusternode == null) {
            try {
            	clusternode = new ClusterNodeServiceImpl(bc,
                        getLogManager().createLogger(
                                Logger.NAME_SQOOSS_CLUSTERNODE));
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (NamespaceException e) {
                e.printStackTrace();
            }
        }
        return clusternode;
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
        if (ma == null) {
        	ma = new MetricActivatorImpl(bc,
                    getLogManager().createLogger(
                            Logger.NAME_SQOOSS_METRICACTIVATOR));
        }
        return ma;
    }
    
    /**
     * Returns an instance to the administration service.
     * 
     * @return A reference to the administration service or null if the
     *  service cannot be instantiated.
     */
    public AdminService getAdminService() {
    	admin = new AdminServiceImpl(bc,
    			getLogManager().createLogger(Logger.NAME_SQOOSS_ADMINACTION));
			

        return admin;
    }
    
	@Override
	public synchronized void serviceChanged(ServiceEvent event) {
		if (event.getType() == ServiceEvent.REGISTERED) {
			if (event.getServiceReference().toString().matches(".*" 
					+ HttpService.class.getName() + ".*")) {
				System.err.println("AlitheiaCore: HTTP service registered");
			}
			
			if (event.getServiceReference().toString().matches(".*" 
					+ EventAdmin.class.getName() + ".*")) {
				System.err.println("AlitheiaCore: Event service registered");
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
}

// vi: ai nosi sw=4 ts=4 expandtab
