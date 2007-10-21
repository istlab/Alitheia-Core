/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 Georgios Gousios <gousiosg@aueb.gr>
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

package eu.sqooss.impl.service.updater;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.scheduler.Scheduler;

public class UpdaterServiceImpl extends HttpServlet implements UpdaterService {

    private static final long serialVersionUID = 1L;

    private ServiceReference serviceRef = null;

    private HttpService httpService = null;
    
    private TDSService tdsService = null;
    private DBService dbService = null;
    private Scheduler scheduler = null;
    
    private LogManager logService = null;

    private Logger logger = null;

    public UpdaterServiceImpl(BundleContext bc) throws ServletException,
            NamespaceException {

        /*Get a reference to the logging service*/
        serviceRef = bc.getServiceReference(LogManager.class.getName());
        logService = (LogManager) bc.getService(serviceRef);

        if (logService != null) {
            logger = logService.createLogger(Logger.NAME_SQOOSS_UPDATER);

            if (logger != null)
                logger.info("Got a valid reference to the logger");
        }

        if (logger == null) {
            System.out.println("ERROR: Got no logger");
        }
        
        /*Get a reference to the TDS service*/
        serviceRef = bc.getServiceReference(TDSService.class.getName());
        tdsService = (TDSService) bc.getService(serviceRef);
        if (tdsService == null)
            logger.severe("Could not load the TDS service");
        else
            logger.info("Got a reference to the TDS service");
        
        /*Get a reference to the DB service*/
        serviceRef = bc.getServiceReference(DBService.class.getName());
        dbService = (DBService) bc.getService(serviceRef);
        if (dbService == null)
            logger.severe("Could not load the DB service");
        else
            logger.info("Got a valid reference to the DB service");
        
        /*Get a reference to the scheduler service*/
        serviceRef = bc.getServiceReference(Scheduler.class.getName());
        scheduler = (Scheduler) bc.getService(serviceRef);
        if (scheduler == null)
            logger.severe("Could not load the scheduler");
        else
            logger.info("Got a valid reference to the Scheduler");


        /*Get a reference to the HTTP service*/
        serviceRef = bc.getServiceReference("org.osgi.service.http.HttpService");

        if (serviceRef != null) {
            httpService = (HttpService) bc.getService(serviceRef);
            httpService.registerServlet("/updater", (Servlet) this, null, null);
        } else {
            logger.severe("Could not load the HTTP service.");
        }
        
        logger.info("Succesfully started updater service");
    }

    public void update(String path, UpdateTarget target) {
        logger.info("Request to update project:" + path + " for target: " 
                + target);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String p = request.getParameter("project");
        String t = request.getParameter("target");

        if (p != null && UpdateTarget.fromString(t) != null) {
            update(p, UpdateTarget.fromString(t));
        } else {
            logger.severe("Failing request to update project:" + p 
                    + " for target: "+ t);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
