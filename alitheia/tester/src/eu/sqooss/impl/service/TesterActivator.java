/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 by Adriaan de Groot <groot@kde.org>
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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

// Now the SQO-OSS imports, alphabetically
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.logging.LogManager;


public class TesterActivator implements BundleActivator {
    private ServiceReference logmanager = null;
    private Logger logger = null;

    private void getLogger( BundleContext bc ) {
        logmanager = bc.getServiceReference(LogManager.class.getName());
        if (logmanager != null) {
            LogManager m = (LogManager) bc.getService(logmanager);
            logger = m.createLogger(Logger.NAME_SQOOSS_UPDATER);
        } 
    }

    public void start( BundleContext bc ) {
        // Really really enabled?
        if (!Boolean.valueOf(bc.getProperty("eu.sqooss.tester.enable"))) {
            System.out.println("Self-test is disabled.");
            return;
        }

        System.out.println("Self-test is enabled. Starting self-test.");
        getLogger(bc);
        if (logger == null) {
            return;
        }

        logger.info("Running self-test.");

        Bundle[] bundles = bc.getBundles();
        for (Bundle b : bundles) {
            if (!b.getSymbolicName().startsWith("eu.sqooss")) {
                continue;
            }
            ServiceReference[] services = b.getRegisteredServices();
            if (services == null) {
                continue;
            }
            for (ServiceReference s : services) {
                Object o = bc.getService(s);
                Method m = null;
                try { 
                    m = o.getClass().getMethod("selfTest");
                } catch (NoSuchMethodException e) {
                    // logger.info("No test method for service.");
                }
                if (m != null) {
                    logger.info("BEGIN Test " + o.getClass().getName());

                    try {
                        Object r = m.invoke(o);
                        if (r != null) {
                            logger.info("Test failed: " + r.toString());
                        }
                    } catch (SecurityException e) {
                        logger.info("Can't access selfTest() method.");
                    } catch (IllegalAccessException e) {
                        logger.info("Failed to invoke selfTest() method: " + e.getMessage());
                    } catch (InvocationTargetException e) {
                        logger.info("Failed to invoke selfTest() on service: " + e.getMessage());
                    } catch (Exception e) {
                        logger.warning("selfTest() method failed: " + e.getMessage());
                        e.printStackTrace();
                    }

                    logger.info("END   Test " + o.getClass().getName());
                    m = null;
                }
                bc.ungetService(s);
            }
        }

        logger.info("Done with self-test.");
    }

    public void stop( BundleContext bc ) 
        throws Exception {
        if (logmanager != null) {
            bc.ungetService(logmanager);
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

