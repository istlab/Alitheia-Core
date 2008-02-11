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

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.updater.UpdaterService;

public class UpdaterServiceImpl extends HttpServlet implements UpdaterService {

    private static final long serialVersionUID = 1L;
    private Logger logger = null;
    private AlitheiaCore core = null;
    private HttpService httpService = null;
    private BundleContext context;

    public UpdaterServiceImpl(BundleContext bc, Logger logger) throws ServletException,
            NamespaceException {
        this.context = bc;
        this.logger = logger;
        /* Get a reference to the core service*/
        ServiceReference serviceRef = null;
        serviceRef = context.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) context.getService(serviceRef);
        if (logger != null) {
            logger.info("Got a valid reference to the logger");
        } else {
            System.out.println("ERROR: Updater got no logger");
        }

        /* Get a reference to the HTTP service */
        serviceRef = context.getServiceReference("org.osgi.service.http.HttpService");
        if (serviceRef != null) {
            httpService = (HttpService) context.getService(serviceRef);
            httpService.registerServlet("/updater", (Servlet) this, null, null);
        } else {
            logger.error("Could not load the HTTP service.");
        }
        logger.info("Succesfully started updater service");
    }

    public boolean update(StoredProject project, UpdateTarget target) {
        if (project == null) {
            logger.info("Bad project name for update.");
            return false;
        }
        logger.info("Request to update project:" + project.getName() + " for target: "
                + target);

        if (!core.getScheduler().isExecuting()) {
            // Make sure there is a thread for this update.
            core.getScheduler().startExecute(1);
        }

        if (target == UpdateTarget.MAIL || target == UpdateTarget.ALL) {
            // mailing list update
            try {
                MailUpdater mu = new MailUpdater(project, core, logger);
                core.getScheduler().enqueue(mu);
            } catch (Exception e) {
                logger.error("The Updater failed to update the mailing list " +
                    "data for project " + project.getName());
                return false;
            }
        } else if (target == UpdateTarget.CODE || target == UpdateTarget.ALL) {
            // source code update
            try {
                SourceUpdater su = new SourceUpdater(project, core, logger);
                core.getScheduler().enqueue(su);
            } catch (Exception e) {
                logger.error("The Updater failed to update the code for project "
                                + project.getName());
                return false;
            }
        } else if (target == UpdateTarget.BUGS || target == UpdateTarget.ALL) {
            // bug database update
        }

        return true;
    }

    /**
     * This is the standard HTTP request handler. It maps GET parameters onto
     * the method arguments for update(project,target). The response always
     * gets a response code -- SC_OK (200) only if the update was able to
     * start at all.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String p = request.getParameter("project");
        String t = request.getParameter("target");

        if (p == null) {
            logger.warn("Bad updater request is missing project name.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (t == null) {
            logger.warn("Bad updater request is missing update target.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        StoredProject project = StoredProject.getProjectByName(p, logger);
        if (project == null) {
            //the project was not found, so the job can not continue
            logger.warn("The project <" + p + "> was not found");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        UpdateTarget target = null;
        try {
            target = UpdateTarget.valueOf(t.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Bad updater request for target <" + t + ">");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        logger.info("Updating project " + p + " target " + t);
        if (!update(project, target)) {
            // Something's wrong
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/xml;charset=UTF-8");
            response.getWriter().println("<updater><jobid>8008135</jobid></updater>");
            response.getWriter().flush();
        }

    }

    public Object selfTest() {
        if (logger == null) {
            return new String("No logger available.");
        }

        // There isn't really much to test here that doesn't affect the
        // state of the system in an undesirable way.
        return null;
    }
}
