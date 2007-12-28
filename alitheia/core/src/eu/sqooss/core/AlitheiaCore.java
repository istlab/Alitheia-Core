/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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


import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.service.http.NamespaceException;

import eu.sqooss.impl.service.db.DBServiceImpl;
import eu.sqooss.impl.service.fds.FDSServiceImpl;
import eu.sqooss.impl.service.logging.LogManagerConstants;
import eu.sqooss.impl.service.logging.LogManagerImpl;
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
import eu.sqooss.impl.service.messaging.MessagingServiceImpl;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.webadmin.WebadminService;

public class AlitheiaCore {

    private LogManagerImpl logger;
    private DBService db;
    private FDSService fds;
    private MessagingService msg;
    private Scheduler sched;
    private SecurityManager sec;
    private TDSService tds;
    private UpdaterService updater;
    private WebadminService webadmin;

    private org.osgi.framework.BundleContext bc;

    public AlitheiaCore(BundleContext bc) {
        this.bc = bc;
        getLogManager();
    }

    public void initWebAdmin() {
        if (webadmin == null) {
            webadmin = new WebadminServiceImpl(bc);
        }
    }
    
    public LogManager getLogManager() {
        if (logger == null) {
            logger = new LogManagerImpl(bc);
        }
        return logger;
    }

    public DBService getDBService() {
        if (db == null) {
            db = new DBServiceImpl(bc, getLogManager().createLogger(LogManagerConstants.loggerNames[1]));
        }

        return db;
    }

    public FDSService getFDSService() {
        if (fds == null) {
            fds = new FDSServiceImpl(bc, getLogManager().createLogger(LogManagerConstants.loggerNames[2]));
        }

        return fds;
    }

    public MessagingService getMessagingService() {
        if (msg == null) {
            msg = new MessagingServiceImpl(bc);
            // msg = new eu.sqooss.impl.service.messaging.MessagingServiceImpl()
        }

        return msg;
    }

    public Scheduler getScheduler() {
        if (sched == null) {
            sched = new SchedulerServiceImpl(bc, getLogManager().createLogger(LogManagerConstants.loggerNames[5]));
        }

        return sched;
    }

    public SecurityManager getSecurityManager() {
        if (sec == null) {
            sec = new SecurityManagerImpl(this.getDBService(),
                    this.getLogManager().createLogger(Logger.NAME_SQOOSS_SECURITY));
        }

        return sec;
    }

    public TDSService getTDSService() {
        if (tds == null) {
            tds = new TDSServiceImpl(getLogManager().createLogger(LogManagerConstants.loggerNames[8]));
        }
        return tds;
    }

    public UpdaterService getUpdater() {
        if (updater == null) {
            try {
                updater = new UpdaterServiceImpl(bc, getLogManager().createLogger(LogManagerConstants.loggerNames[9]));
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (NamespaceException e) {
                e.printStackTrace();
            }
        }
        return updater;
    }

    /**
     * This is the selfTest() method which is called by the system
     * tester at startup. The method itself serves only as a dispatcher
     * to call the selfTest() methods of all of the sub-services of
     * the Alitheia core. It does this much like the tester does:
     * use reflection to get the selfTest() method. There is probably
     * some duplicated logging if failures occur (by this method
     * and the calling TesterService.selfTest()).
     *
     * @return object representing all of the failures of the test
     *          (we use a List of Object to collect the failures
     *          across sub-services).
     */
    public final Object selfTest() {
        // We are going to push all of the test failures onto this
        // list to dump in one go later.
        List <Object > result = new LinkedList < Object >();

        List < Object > testObjects = new LinkedList < Object >();
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
                            l.info("Test failed: " + r.toString());
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
                        l.warn("selfTest() method failed: "
                                + e.getMessage());
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

