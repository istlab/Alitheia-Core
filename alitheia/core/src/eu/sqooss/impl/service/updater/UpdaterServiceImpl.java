/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 Georgios Gousios <gousiosg@gmail.com>
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.updater.UpdaterException;
import eu.sqooss.service.updater.UpdaterService;

public class UpdaterServiceImpl extends HttpServlet implements UpdaterService {

    private static final long serialVersionUID = 1L;
    private Logger logger = null;
    private AlitheiaCore core = null;
    private HttpService httpService = null;
    private BundleContext context;
    private Map<String,Set<UpdateTarget>> currentJobs = null; 

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
        
        currentJobs = new HashMap<String,Set<UpdateTarget>>();
        logger.info("Succesfully started updater service");
    }

    /**
     * Check if an update of the given type t is running for the given project
     * name; if t is ALL, check if any update is running.
     * 
     * @param projectName project to check for
     * @param t update type
     * @return true if such an update is running
     */
    private boolean isUpdateRunning(String projectName, UpdateTarget t) {
        synchronized (currentJobs) {
            Set<UpdateTarget> s = currentJobs.get(projectName);
            if (s==null) {
                // Nothing in progress
                return false;
            }
            if (t==UpdateTarget.ALL) {
                return !s.isEmpty();
            }
            if (s.contains(t)) {
                return true;
            }
            return false;
        }
    }
    
    /**
     * Claim an update job of the given type for the project. You may
     * not claim ALL as a type of update -- use the individual types.
     * 
     * @param projectName the project to claim
     * @param t the type of update that is being claimed
     * @return true if the claim succeeds
     */
    private boolean addUpdate(String projectName, UpdateTarget t) {
        if (t==UpdateTarget.ALL) {
            logger.warn("Adding update target ALL is bogus.");
            return false;
        }
        synchronized (currentJobs) {
            //Duplicate some code from isUpdateRunning
            Set<UpdateTarget> s = currentJobs.get(projectName);
            if (s==null) {
                s = new HashSet<UpdateTarget>(4);
                currentJobs.put(projectName, s);
            }
            if (isUpdateRunning(projectName,t)) {
                return false;
            }
            s.add(t);
        }
        return true;
    }
    
    public void removeUpdater(String projectName, UpdateTarget t) {
        if (t==UpdateTarget.ALL) {
            logger.warn("Removing update target ALL is bogus.");
            return;
        }
        synchronized (currentJobs) {
            Set<UpdateTarget> s = currentJobs.get(projectName);
            if (s!=null) {
                s.remove(t);
            }
        }
    }
    public boolean update(StoredProject project, UpdateTarget target) {
        if (project == null) {
            logger.info("Bad project name for update.");
            return false;
        }
        logger.info("Request to update project:" + project.getName() + " for target: "
                + target);

        if (!core.getScheduler().isExecuting()) {
            // Make sure there are enough threads for the updater.
            core.getScheduler().startExecute(Runtime.getRuntime().availableProcessors());
        }

        Set<UpdateTarget> s = currentJobs.get(project.getName());
        if ((s==null) || (s.isEmpty())) {
            logger.info("Update set is empty");
        } else {
            String msg = "Update set is: ";
            for (UpdateTarget u : s) {
                msg = msg + u.toString();
            }
            logger.info(msg);
        }
        // Check all the types we need and make claims
        synchronized(currentJobs) {
            if (isUpdateRunning(project.getName(),target)) {
                return false;
            }
            if (target==UpdateTarget.ALL) {
                addUpdate(project.getName(),UpdateTarget.MAIL);
                addUpdate(project.getName(),UpdateTarget.CODE);
                // Bugs are suppressed - no job to handle it
                // addUpdate(project.getName(),UpdateTarget.BUGS);
            } else {
                addUpdate(project.getName(),target);
            }
        }
        
        if (target == UpdateTarget.MAIL || target == UpdateTarget.ALL) {
            // mailing list update
            try {
                MailUpdater mu = new MailUpdater(project, this, core, logger);
                core.getScheduler().enqueue(mu);
            } catch(SchedulerException e) {
                logger.error("The Updater failed to update the mailing list " +
                    " metadata data for project " + project.getName() + 
                    " Scheduler error: " + e.getMessage());
                return false;
           } catch (UpdaterException e) {
               logger.error("The Updater failed to update the mailing list " +
                       "metadata for project " + project.getName() + 
                       " Updater error: " +  e.getMessage());
                   return false;
            }
        } 
        
        if (target == UpdateTarget.CODE || target == UpdateTarget.ALL) {
            // source code update
            try {
                SourceUpdater su = new SourceUpdater(project, this, core, logger);
                core.getScheduler().enqueue(su);
            } catch (SchedulerException e) {
                logger.error("The Updater failed to update the repository" +
                		" metadata for project " + project.getName() + 
                		" Scheduler error: " + e.getMessage());
                return false;
            } catch (UpdaterException e) {
                logger.error("The Updater failed to update the repository " +
                        "metadata for project " + project.getName() + 
                        " Updater error: " +  e.getMessage());
                    return false;
            }
        } 
        
        if (target == UpdateTarget.BUGS || target == UpdateTarget.ALL) {
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
