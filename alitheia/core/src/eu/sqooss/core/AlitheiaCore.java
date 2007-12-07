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

public class AlitheiaCore {

    private LogManagerImpl logger;
    private DBService db;
    private FDSService fds;
    private MessagingService msg;
    private Scheduler sched;
    private SecurityManager sec;
    private TDSService tds;
    private UpdaterService updater;

    private org.osgi.framework.BundleContext bc;

    public AlitheiaCore(BundleContext bc) {
        this.bc = bc;
        getLogManager();
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
            sec = new SecurityManagerImpl(this.getLogManager().createLogger(Logger.NAME_SQOOSS_SECURITY));
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
    
    public Object selfTest()
    {
    	List<Object> testObjects = new LinkedList<Object>();
    	try{
    		testObjects.add(getScheduler());
    		testObjects.add(getDBService());
    		testObjects.add(getFDSService());
    		testObjects.add(getLogManager());
    		testObjects.add(getMessagingService());
    		testObjects.add(getSecurityManager());
    		testObjects.add(getTDSService());
    		testObjects.add(getUpdater());
    	}
    	catch( Throwable t )
    	{
    		t.printStackTrace();
    		return t.toString();
    	}
    	
    	Object result = null;
    	
    	for (Object o: testObjects)
    	{
        	try {
        		System.out.println("Running " + o.getClass().getName() );
            	Method m = o.getClass().getMethod("selfTest");
            	try {
					result = m.invoke(o);
				} catch ( Exception e ) {
					// e.printStackTrace();
					System.out.println( "FAILED Test method of class " + o.getClass().getName() + " failed." );
				}
				
				if (result != null)
				{
					return result;
				}
        	} catch (NoSuchMethodException e) {
        		// logger.info("No test method for service.");
        	}
    	}
    	
    	return result;
	}
}
