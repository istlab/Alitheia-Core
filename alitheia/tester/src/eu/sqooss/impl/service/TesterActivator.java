/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Adriaan de Groot <groot@kde.org>
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

package eu.sqooss.impl.service;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

// Now the SQO-OSS imports, alphabetically
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;

public class TesterActivator implements BundleActivator, EventHandler, Runnable {
    
    private static final int TEST_WAITING_TIME = 3000;
    
    private ServiceReference serviceRef = null;
    private AlitheiaCore core = null;
    private Logger logger = null;
    private BundleContext bc;
    private ServiceRegistration sReg;

    private void getLogger() {
        assert (core != null);
        LogManager man = core.getLogManager();
        logger = man.createLogger(Logger.NAME_SQOOSS_TESTER);
    }

    public void start(BundleContext bc) {
        // Really really enabled?
        if (!Boolean.valueOf(bc.getProperty("eu.sqooss.tester.enable"))) {
            System.out.println("Self-test is disabled.");
            return;
        }

        this.bc = bc;
        
        Dictionary<String, String> props = new Hashtable<String, String>(1);
        props.put(EventConstants.EVENT_TOPIC, DBService.EVENT_STARTED);
        sReg = bc.registerService(EventHandler.class.getName(), this, props);
        
        System.out.println("Self-test is enabled.");
    }

    public void stop(BundleContext bc) throws Exception {
        if (serviceRef != null) {
            bc.ungetService(serviceRef);
        }
        if (sReg != null) {
            sReg.unregister();
        }
    }

    /**
     * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
     */
    public void handleEvent(Event event) {
        if (DBService.EVENT_STARTED.equals(event.getTopic())) {
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        
        try {
            Thread.sleep(TEST_WAITING_TIME);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        
        serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) bc.getService(serviceRef);

        getLogger();
        if (logger == null) {
            return;
        }

        logger.info("Running self-test.");
        System.out.println("Self-test is enabled. Starting self-test");

        Bundle[] bundles = bc.getBundles();
        for (Bundle b : bundles) {
            String bundleName = b.getSymbolicName();
            if ((bundleName == null) || (!bundleName.startsWith("eu.sqooss"))) {
                continue;
            }
            ServiceReference[] services = b.getRegisteredServices();
            if (services == null) {
                continue;
            }
            for (ServiceReference s : services) {
                logger.info("TRY   Test " + s.toString());
                Object o = bc.getService(s);
                Method m = null;
                try {
                    m = o.getClass().getMethod("selfTest");
                } catch (NoSuchMethodException e) {
                    // logger.info("No test method for service.");
                }
                if (m != null) {
                    String className = o.getClass().getName();
                    logger.info("BEGIN Test " + className);

                    // Now trim down to only the class name
                    int lastDot = className.lastIndexOf('.');
                    if (lastDot > 0) {
                        className = className.substring(lastDot + 1);
                    }

                    String enabled = bc.getProperty("eu.sqooss.tester.enable."
                            + className);
                    if ((enabled != null) && !Boolean.valueOf(enabled)) {
                        logger.info("SKIP  Test (disabled in configuration)");
                        continue;
                    }

                    try {
                        Object r = m.invoke(o);
                        if (r != null) {
                            logger.info("Test failed: " + r.toString());
                        }
                    } catch (SecurityException e) {
                        logger.info("Can't access selfTest() method.");
                    } catch (IllegalAccessException e) {
                        logger.info("Failed to invoke selfTest() method: "
                                + e.getMessage());
                    } catch (InvocationTargetException e) {
                        logger.info("Failed to invoke selfTest() on service: "
                                + e.getMessage());
                    } catch (Exception e) {
                        logger.warn("selfTest() method failed: "
                                + e.getMessage());
                        e.printStackTrace();
                    }

                    logger.info("END   Test " + o.getClass().getName());
                    m = null;
                }
                bc.ungetService(s);
            }
        }

        logger.info("Finished self-test.");
        System.out.println("Self-test is enabled. Finished self-test.");
    }
    
}

// vi: ai nosi sw=4 ts=4 expandtab

