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
     * Overload for convenience. See isUpdateRunning(String,UpdateTarget).
     */
    private boolean isUpdateRunning(StoredProject p, UpdateTarget t) {
        return isUpdateRunning(p.getName(),t);
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
    
    /*
     * Overload for convenience. See addUpdate(String,UpdateTarget).
     */
    private boolean addUpdate(StoredProject p, UpdateTarget t) {
        return addUpdate(p.getName(),t);
    }
    
    /**
     * Removes an earlier claim made through addUpdate().
     * 
     * @param projectName name of the project whose claim is released
     * @param t type of claim to release
     */
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
    
    /*
     * Overload for convenience. See removeUpdater(String, UpdateTarget)
     */
    public void removeUpdater(StoredProject p, UpdateTarget t) {
        removeUpdater(p.getName(),t);
    }
    
    /**
     * Overload for convenience. Multiple removeUpdater(String, UpdateTarget)
     * calls are made to release all the claims in the set.
     * 
     * @param p project to release claims for
     * @param t set of targets to release
     */
    public void removeUpdater(StoredProject p, Set<UpdateTarget> t) {
        if ((t==null) || t.isEmpty()) {
            return;
        }
        for (UpdateTarget u : t) {
            removeUpdater(p,u);
        }
    }
    
    /**
     * Produce a string representation of the set of update targets.
     * @param s set to convert to string
     * @return human-readable string representation
     */
    private String explain(Set<UpdateTarget> s) {
        if ((s==null) || s.isEmpty()) {
            return "empty";
        }
        
        String msg = "";
        for (UpdateTarget u : s) {
            msg = msg + u.toString() + " ";
        }
        return msg.trim();
    }
    
    public boolean update(StoredProject project, UpdateTarget target, Set<Integer> result) {
        if (project == null) {
            logger.info("Bad project name for update.");
            return false;
        }
        logger.info("Request to update project:" + project.getName() + " for target: "
                + target);
        if (result==null) {
            logger.info("Ignoring results return variable.");
        } else {
            if (result.size() != 0) {
                logger.info("Using a non-empty results return variable.");
            }
        }
        if (!core.getScheduler().isExecuting()) {
            // Make sure there are enough threads for the updater.
            core.getScheduler().startExecute(Runtime.getRuntime().availableProcessors());
        }

        Set<UpdateTarget> s = currentJobs.get(project.getName());
        logger.info("Update set is:" + explain(s));
       
        // Check all the types we need and make claims
        synchronized(currentJobs) {
            if (isUpdateRunning(project.getName(),target)) {
                return false;
            }
            if (target==UpdateTarget.ALL) {
                addUpdate(project,UpdateTarget.MAIL);
                addUpdate(project,UpdateTarget.CODE);
                // Bugs have no jobs to run; this claim will be fixed
                // later and released.
                addUpdate(project,UpdateTarget.BUGS);
            } else {
                addUpdate(project,target);
            }
        }
        
        // When we get to here, we have staked our claims already -- thus preventing
        // others from getting through the synchronized block above -- and can start
        // queueing jobs; we maintain a list of successfully queued update jobs
        // to report to the user.
        if (target == UpdateTarget.MAIL || target == UpdateTarget.ALL) {
            // mailing list update
            boolean queued_successfully = false;
            try {
                MailUpdater mu = new MailUpdater(project, this, core, logger);
                core.getScheduler().enqueue(mu);
                if (result != null) {
                    result.add(mu.hashCode());
                }
                queued_successfully = true;
            } catch(SchedulerException e) {
                logger.error("The Updater failed to update the mailing list " +
                    " metadata data for project " + project.getName() + 
                    " Scheduler error: " + e.getMessage());
            } catch (UpdaterException e) {
                logger.error("The Updater failed to update the mailing list " +
                        "metadata for project " + project.getName() + 
                        " Updater error: " +  e.getMessage());
            } finally {
                if (!queued_successfully) {
                    removeUpdater(project,UpdateTarget.MAIL);
                }
            }
        } 
        
        if (target == UpdateTarget.CODE || target == UpdateTarget.ALL) {
            // source code update
            boolean queued_successfully = false;
            try {
                SourceUpdater su = new SourceUpdater(project, this, core, logger);
                core.getScheduler().enqueue(su);
                if (result != null) {
                    result.add(su.hashCode());
                }
                queued_successfully = true;
            } catch (SchedulerException e) {
                logger.error("The Updater failed to update the repository" +
                		" metadata for project " + project.getName() + 
                		" Scheduler error: " + e.getMessage());
            } catch (UpdaterException e) {
                logger.error("The Updater failed to update the repository " +
                        "metadata for project " + project.getName() + 
                        " Updater error: " +  e.getMessage());
            } finally {
                if (!queued_successfully) {
                    removeUpdater(project,UpdateTarget.CODE);
                }
            }
        } 
        
        if (target == UpdateTarget.BUGS || target == UpdateTarget.ALL) {
            // bug database update
            boolean queued_successfully = false;
            try {
                // No updater for bugs yet
            } finally {
                if (!queued_successfully) {
                    removeUpdater(project,UpdateTarget.BUGS);
                }
            }
        }

        return true;
    }

    /**
     * Overload for convenience. Use string instead of stored project.
     * Doesn't usefully distinguish between project not found and other
     * kinds of errors returned by update().
     */
    public boolean update(String p, UpdateTarget t, Set<Integer> results) {
        StoredProject project = StoredProject.getProjectByName(p, logger);
        if (project == null) {
            //the project was not found, so the job can not continue
            logger.warn("The project <" + p + "> was not found");
            return false;
        }

        return update(project, t, results);
    }
    
    /**
     * This is the standard HTTP request handler. It maps GET parameters onto
     * the method arguments for update(project,target). The response always
     * gets a response code -- SC_OK (200) only if the update was able to
     * start at all.
     * 
     * The response codes in HTTP are used as follows:
     * - SC_OK  if the update starts successfully; a fake XML response is
     *          returned describing the success.
     * - SC_BAD_REQUEST (400) if the request is syntactically incorrect, which in
     *          this case means that one of the required parameters "project"
     *          or "target" is missing.
     * - SC_NOT_FOUND (404) if the project does not exist in the database or
     *          is otherwise not found. (This may be confusing with the 404
     *          returned when the updater servlet is not running, so may need
     *          to change this at some point).
     * - SC_NOT_IMPLEMENTED if the update target type is not supported, for
     *          instance because it names a datatype that we do not know about
     *          (valid values are "mail", "code", "bugs" and "all" right now).
     * - SC_CONFLICT if there is already an update running for the given project
     *          and data source; only one can be active at any time.                           
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
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }

        logger.info("Updating project " + p + " target " + t);
        Set<Integer> jobs = new HashSet<Integer>(4);
        if (!update(project, target,jobs)) {
            // Something's wrong
            response.sendError(HttpServletResponse.SC_CONFLICT);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/xml;charset=UTF-8");
            response.getWriter().println("<updater>");
            response.getWriter().println("<project><id>" + project.getId() + "</id>");
            response.getWriter().println("<name>" + project.getName() + "</name></project>");
            if (jobs.isEmpty()) {
                response.getWriter().println("<status>No jobs started.</status>");
            } else {
                response.getWriter().println("<status>Jobs started (" + jobs.size() + ")</status>");
                response.getWriter().println("<jobs>");
                for (Integer i : jobs) {
                    response.getWriter().println("<job>" + i + "</job>");
                }
                response.getWriter().println("</jobs>");
                response.getWriter().println("<gratuitous>8008135</gratuitous>");
            }
            response.getWriter().println("</updater>");
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
