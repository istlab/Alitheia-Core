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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.service.http.NamespaceException;

import eu.sqooss.impl.service.db.DBServiceImpl;
import eu.sqooss.impl.service.fds.FDSServiceImpl;
import eu.sqooss.impl.service.logging.LogManagerImpl;
import eu.sqooss.impl.service.messaging.MessagingServiceImpl;
import eu.sqooss.impl.service.metricactivator.MetricActivatorImpl;
import eu.sqooss.impl.service.pa.PAServiceImpl;
import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.impl.service.security.SecurityManagerImpl;
import eu.sqooss.impl.service.tds.TDSServiceImpl;
import eu.sqooss.impl.service.updater.UpdaterServiceImpl;
import eu.sqooss.impl.service.webadmin.WebadminServiceImpl;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.messaging.MessagingService;
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
 */
public class AlitheiaCore {

    /** The Logger component's instance. */
    private LogManagerImpl logger;
    
    /** The DB component's instance. */
    private DBService db;
    
    /** The FDS component's instance. */
    private FDSService fds;
    
    /** The Messaging component's instance. */
    private MessagingService msg;
    
    /** The Scheduler component's instance. */
    private Scheduler sched;
    
    /** The Security component's instance. */
    private SecurityManager sec;
    
    /** The TDS component's instance. */
    private TDSService tds;
    
    /** The Udpater component's instance. */
    private UpdaterService updater;
    
    /** The WebAdmin component's instance. */
    private WebadminService webadmin;
    
    /** The Plug-in Admin component's instance. */
    private PAServiceImpl padmin;
    
    /** The Metric Activator component's instance. */
    private MetricActivator ma;

    /** The parent bundle's context object. */
    private BundleContext bc;
    
    /** Is the database inited yet? */
    private AtomicBoolean dbInited;

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
    }

    /**
     * Initializes an instance of the WebAdmin component.
     */
    private void initWebAdmin() {
        webadmin = new WebadminServiceImpl(bc, getMessagingService(),
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
     * Simple constructor.
     * 
     * @param bc The parent bunde's context object.
     */
    public AlitheiaCore(BundleContext bc) {
        this.bc = bc;
        dbInited = new AtomicBoolean(false);
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
        // *** NOTE: Do not change the initialization order! ***
        // Create an instance of the Logger component.
        initLogger();
        // Create an instance of the DB component.
        initDB();
        // Create an instance of the PluginAdmin component
        initPluginAdmin();
        // Create an instance of the WebAdmin component
        initWebAdmin();
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
        //TODO: Naive busy wait
     //   while(dbInited.get() != true) {
       // }
        return db;
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
     * Returns the locally stored Messaging component's instance.
     * <br/>
     * <i>The instance is created when this method is called for a first
     * time.</i>
     * 
     * @return The Messaging component's instance.
     */
    public MessagingService getMessagingService() {
        if (msg == null) {
            msg = new MessagingServiceImpl(bc);
        }
        return msg;
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
     * Returns the locally stored Metric Activator component's instance.
     * <br/>
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
     * This is the <code>selfTest()</code> method, which is called by the
     * system tester at startup. The method itself serves only as a dispatcher
     * to the <code>selfTest()</code> methods of all the components, that
     * build the SQO-OSS core. It does this much like the Tester service does
     * i.e. uses reflection to get the <code>selfTest()</code> method.
     * <br/>
     * NOTE: There could be a duplicated log entries, when a failure occurs,
     * that's logged by both this method and the calling 
     * <code>TesterService.selfTest())</code>.
     * 
     * @return The object representing all of the failures of the test (we use a
     * List of Object to collect the failures across sub-services).
     */
    public final Object selfTest() {
        // We are going to push all of the test failures onto this
        // list to dump in one go later.
        List<Object> result = new LinkedList<Object>();

        List<Object> testObjects = new LinkedList<Object>();
        try {
            testObjects.add(getScheduler());
            testObjects.add(getDBService());
            testObjects.add(getFDSService());
            testObjects.add(getLogManager());
            testObjects.add(getMessagingService());
            testObjects.add(getSecurityManager());
            testObjects.add(getTDSService());
            testObjects.add(getUpdater());
        } catch (Throwable t) {
            t.printStackTrace();
            return t.toString();
        }

        Logger l = getLogManager().createLogger(Logger.NAME_SQOOSS_TESTER);
        
        for (Object o : testObjects) {
            String className = o.getClass().getName();
            try {
                Method m = o.getClass().getMethod("selfTest");
                if (m != null) {
                    l.info("BEGIN SubTest " + className);

                    // Now trim down to only the class name
                    int lastDot = className.lastIndexOf('.');
                    if (lastDot > 0) {
                        className = className.substring(lastDot + 1);
                    }

                    String enabled = bc.getProperty("eu.sqooss.tester.enable."
                            + className);
                    if ((enabled != null) && !Boolean.valueOf(enabled)) {
                        l.info("SKIP  Test (disabled in configuration)");
                        continue;
                    }

                    try {
                        Object r = m.invoke(o);
                        if (r != null) {
                            l.info(className + "'s test failed: "
                                    + r.toString());
                            result.add(r);
                        }
                    } catch (SecurityException e) {
                        l.info("Can't access selfTest() method.");
                    } catch (IllegalAccessException e) {
                        l.info("Failed to invoke selfTest() method: "
                                + e.getMessage());
                    } catch (InvocationTargetException e) {
                        l.info("Failed to invoke selfTest() on service: "
                                + e.getMessage());
                    } catch (Exception e) {
                        l.warn("selfTest() method failed: " + e.getMessage());
                        e.printStackTrace();
                    }

                    l.info("END   SubTest " + o.getClass().getName());
                    m = null;
                }
            } catch (NoSuchMethodException e) {
                l.warn("Core component " + className + " has no selfTest()");
            }
        }
        return result;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
