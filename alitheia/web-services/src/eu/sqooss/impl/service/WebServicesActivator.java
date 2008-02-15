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

package eu.sqooss.impl.service;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.web.services.Constants;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.web.services.WebServices;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.tds.TDSService;

/**
 * This class is used to start and stop the web services bundle. 
 */
public class WebServicesActivator implements BundleActivator {
    
    private ServiceReference coreServiceRef;
    private ServiceRegistration webServicesReg;
    private LogManager logManager;
    private Logger logger;
    
    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext bc) throws Exception {
    
        coreServiceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        if (coreServiceRef == null) {
            throw new BundleException("Can't get the alitheia core service!");
        }
        
        AlitheiaCore core = (AlitheiaCore)bc.getService(coreServiceRef);
        
        SecurityManager securityManager = core.getSecurityManager();
        DBService db = core.getDBService();
        TDSService tds = core.getTDSService();
        logManager  = core.getLogManager();
        logger = logManager.createLogger(Logger.NAME_SQOOSS_WEB_SERVICES);
        
        //registers the web service
        Object serviceObject = new WebServices(bc, securityManager, db, tds, logger);
        Properties props = initProperties(bc);
        String serviceClass = props.getProperty(Constants.PROPERTY_KEY_WEB_SERVICES_INTERFACE); 
        webServicesReg = bc.registerService(serviceClass, serviceObject, props);
        
        logger.info("The web services bundle is started!");
    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext bc) throws Exception {
        if (logger != null) {
            logger.info("The web services bundle is stopped!");
        }
        
        if (webServicesReg != null) {
            webServicesReg.unregister();
        }
        
        if (coreServiceRef != null) {
            bc.ungetService(coreServiceRef);
            logManager.releaseLogger(logger.getName());
        }
        
    }
    
    /**
     * Loads the properties of the web services from the configuration file.
     * If some of the mandatory properties are missing then the method sets default properties.
     * 
     * @param bc The bundle context is used to access the configuration file.
     * 
     * @return the properties of the web services (i.e. web.service.name,
     * web.service.context and interface.class)
     * 
     */
    private Properties initProperties(BundleContext bc) {
        Bundle bundle = bc.getBundle();
        URL propsUrl = bundle.getEntry(Constants.FILE_NAME_PROPERTIES); 
        Properties props = new Properties();
        if (propsUrl != null) {
            try {
                props.load(propsUrl.openStream());
            } catch (IOException e) {
                //uses default properties
                logger.info(e.getMessage());
            }
        }
        setDefaultPropertiesIfNeed(props);
        return props;
    }
    
    /**
     * Checks the mandatory properties.
     * 
     * @param props
     */
    private void setDefaultPropertiesIfNeed(Properties props) {
        if (props.getProperty(Constants.PROPERTY_KEY_WEB_SERVICES_CONTEXT) == null) {
        props.setProperty(Constants.PROPERTY_KEY_WEB_SERVICES_CONTEXT,
                Constants.PROPERTY_VALUE_WEB_SERVICES_CONTEXT);
        }
        if (props.getProperty(Constants.PROPERTY_KEY_WEB_SERVICES_INTERFACE) == null) {
        props.setProperty(Constants.PROPERTY_KEY_WEB_SERVICES_INTERFACE,
                Constants.PROPERTY_VALUE_WEB_SERVICES_INTERFACE);
        }
        if (props.getProperty(Constants.PROPERTY_KEY_WEB_SERVICES_NAME) == null) {
        props.setProperty(Constants.PROPERTY_KEY_WEB_SERVICES_NAME,
                Constants.PROPERTY_VALUE_WEB_SERVICES_NAME);
        }
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
