/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Adriaan de Groot <groot@kde.org>
 * Copyright 2008 by Paul J. Adams <paul.adams@siriusit.co.uk>
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

package eu.sqooss.impl.service.webadmin;

import eu.sqooss.core.AlitheiaCore;

import eu.sqooss.impl.service.webadmin.WebAdminRenderer;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.util.Pair;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.scheduler.Scheduler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.Date;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private long startTime = new Date().getTime();

    private BundleContext bundlecontext = null;

    // Content tables
    private Hashtable<String, String> dynamicContentMap = null;
    private Hashtable<String, Pair<String, String>> staticContentMap = null;

    // Dynamic substitutions
    VelocityContext vc = null;
    VelocityEngine ve = null;

    // Critical logging components
    private LogManager sobjLogManager = null;
    private Logger sobjLogger = null;

    private AlitheiaCore sobjAlitheiaCore = null;
    private ServiceReference srefCore = null;

    // Service objects
    private DBService sobjDB = null;
    private PluginAdmin sobjPluginAdmin = null;
    private Scheduler sobjSched = null;

    public AdminServlet(BundleContext bc) {
        // Setup the crucial components
        bundlecontext = bc;
        getComponents();

        // Create the static content map
        staticContentMap = new Hashtable<String, Pair<String, String>>();
        addStaticContent("/screen.css", "text/css");
        addStaticContent("/sqo-oss.png", "image/x-png");
        addStaticContent("/queue.png", "image/x-png");
        addStaticContent("/uptime.png", "image/x-png");
        addStaticContent("/greyBack.jpg", "image/x-jpg");
        addStaticContent("/projects.png", "image/x-png");
        addStaticContent("/logs.png", "image/x-png");
        addStaticContent("/metrics.png", "image/x-png");
        addStaticContent("/gear.png", "image/x-png");
        addStaticContent("/header-repeat.png", "image/x-png");
        addStaticContent("/add_user.png", "image/x-png");

        // Create the dynamic content map
        dynamicContentMap = new Hashtable<String, String>();
        dynamicContentMap.put("/", "index.html");
        dynamicContentMap.put("/index", "index.html");
        dynamicContentMap.put("/projects", "projects.html");
        dynamicContentMap.put("/logs", "logs.html");
        dynamicContentMap.put("/jobs", "jobs.html");
        dynamicContentMap.put("/alljobs", "alljobs.html");
        dynamicContentMap.put("/users", "users.html");

        // Now the dynamic substitutions
        vc = new VelocityContext();
        createSubstitutions(true);

        try {
            ve = new VelocityEngine();
            ve.setProperty("runtime.log.logsystem.class",
                           "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
            ve.setProperty("runtime.log.logsystem.log4j.category", 
                           Logger.NAME_SQOOSS_WEBADMIN);
            ve.setProperty("resource.loader","bundle");
            ve.setProperty("bundle.resource.loader.description",
                           "Loader from the bundle.");
            ve.setProperty("bundle.resource.loader.class",
                           "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
            ve.setProperty("bundle.resource.loader.path",
                           "jar:file:eu.sqooss.alitheia.core-0.0.1.jar");
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Add content to the static map
     */
    private void addStaticContent(String path, String type) {
        Pair<String, String> p = new Pair<String, String> (path,type);
        staticContentMap.put(path, p);
    }

    /**
     * This function retrieves all of the service components required by the AdminServlet
     */
    private void getComponents() {
        srefCore = bundlecontext.getServiceReference(AlitheiaCore.class.getName());

        if (srefCore != null) {
            sobjAlitheiaCore = (AlitheiaCore) bundlecontext.getService(srefCore);
        }
        else {
            System.out.println("AdminServlet: No Alitheia Core found.");
        }

        if (sobjAlitheiaCore != null) {
            //Get the LogManager and Logger objects
            sobjLogManager = sobjAlitheiaCore.getLogManager();
            if (sobjLogManager != null) {
                sobjLogger = sobjLogManager.createLogger(Logger.NAME_SQOOSS_WEBADMIN);
            }

            // Get the DB Service object
            sobjDB = sobjAlitheiaCore.getDBService();
            if (sobjDB != null) {
                sobjLogger.debug("WebAdmin got DB Service object.");
            }

            // Get the Plugin Administration object
            sobjPluginAdmin = sobjAlitheiaCore.getPluginManager();
            if (sobjPluginAdmin != null) {
                sobjLogger.debug("WebAdmin got Plugin Admin object.");
            }

            // Get the scheduler
            sobjSched = sobjAlitheiaCore.getScheduler();
            if (sobjSched != null) {
                sobjLogger.debug("WebAdmin got Scheduler Service object.");
            }
        }
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException,
                                                              IOException {
        try {
            sobjLogger.debug("GET path=" + request.getPathInfo());
            String query = request.getPathInfo();
            
            // This is static content
            if ((query != null) && (staticContentMap.containsKey(query))) {
                sendResource(response, staticContentMap.get(query));
            }
            else if ((query != null) && (dynamicContentMap.containsKey(query))) {
                sendPage(response, dynamicContentMap.get(query));
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                               IOException {
    }

    /**
     * Sends a resource (stored in the jar file) as a response. The mime-type
     * is set to @p mimeType . The @p path to the resource should start
     * with a / .
     *
     * Test cases:
     *   - null mimetype, null path, bad path, relative path, path not found,
     *   - null response
     *
     * TODO: How to simulate conditions that will cause IOException
     */
    protected void sendResource(HttpServletResponse response, Pair<String,String> source)
        throws ServletException, IOException {
        InputStream istream = getClass().getResourceAsStream(source.first);
        if ( istream == null ) {
            throw new IOException("Path not found: " + source.first);
        }

        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        int totalBytes = 0;

        response.setContentType(source.second);
        ServletOutputStream ostream = response.getOutputStream();
        while ((bytesRead = istream.read(buffer)) > 0) {
            ostream.write(buffer,0,bytesRead);
            totalBytes += bytesRead;
        }

        if (sobjLogger != null) {
            sobjLogger.debug("Wrote " + totalBytes + " from " + source.first);
        }
    }

    protected void sendPage(HttpServletResponse response, String path)
        throws ServletException, IOException, Exception {
        Template t = ve.getTemplate( path );
        StringWriter writer = new StringWriter();
        PrintWriter print = response.getWriter();

        // Do any substitutions that may be required
        createSubstitutions(false);
        response.setContentType("text/html");
        t.merge(vc, writer);

        print.print(writer.toString());
    }

    private void createSubstitutions(boolean initialRun) {
        if (initialRun) {
            // Simple string substitutions
            vc.put("COPYRIGHT", "Copyright 2007-2008 <a href=\"http://www.sqo-oss.eu/about/\">SQO-OSS Consortium Members</a>");
            vc.put("LOGO", "<img src='/logo' id='logo' alt='Logo' />");
            vc.put("MENU",
                   "<ul id=\"menu\">" +
                   "<li id=\"nav-1\"><a href=\"/index\">Metrics</a></li>" +
                   "<li id=\"nav-3\"><a href=\"/projects\">Projects</a></li>" +
                   "<li id=\"nav-6\"><a href=\"/users\">Users</a></li>" +
                   "<li id=\"nav-2\"><a href=\"/logs\">Logs</a></li>" +
                   "<li id=\"nav-4\"><a href=\"/jobs\">Jobs</a></li>" +
                   "</ul>");
            vc.put("OPTIONS","<fieldset id=\"options\">" +
                   "<legend>Options</legend>" +
                   "<form id=\"start\" method=\"post\" action=\"restart\">" +
                   "<p><input type=\"submit\" value=\"Restart\" /></p>" +
                   "</form>" +
                   "<form id=\"stop\" method=\"post\" action=\"stop\">" +
                   "<p><input type=\"submit\" value=\"Stop\" /></p>" +
                   "</form></fieldset>");
        }

        // Function-based substitutions
        //vc.put("STATUS", someFunction); FIXME
        vc.put("GETLOGS", WebAdminRenderer.renderList(sobjLogManager.getRecentEntries()));
        //vc.put("PROJECTS", renderProjects(sobjDB.doHQL("from StoredProject"), sobjPluginAdmin.listPlugins()));
        vc.put("UPTIME", WebAdminRenderer.getUptime(startTime, new Date().getTime()));
        vc.put("QUEUE_LENGTH", String.valueOf(sobjSched.getSchedulerStats().getWaitingJobs()));
        //vc.put("JOB_EXEC", String.valueOf(sobjSched.getSchedulerStats().getRunningJobs()));
        //vc.put("JOB_WAIT", String.valueOf(sobjSched.getSchedulerStats().getWaitingJobs()));
        //vc.put("JOB_WORKTHR", String.valueOf(sobjSched.getSchedulerStats().getWorkerThreads()));
        //vc.put("JOB_FAILED", String.valueOf(sobjSched.getSchedulerStats().getFailedJobs()));
        //vc.put("JOB_TOTAL", String.valueOf(sobjSched.getSchedulerStats().getTotalJobs()));
        //vc.put("WAITJOBS", renderWaitJobs());
        //vc.put("FAILJOBS", renderFailedJobs());
        //vc.put("JOBFAILSTATS", renderJobFailStats());
        //vc.put("METRICS", renderMetrics());

        // These are composite substitutions
        vc.put("STATUS_CORE","<fieldset id=\"status\">" +
                     "<legend>Status</legend>" +
                     "<ul>" +
                     "<li class=\"uptime\">Uptime: " +
                                          vc.get("UPTIME") +
                     "</li>" +
                     "<li class=\"queue\">Job Queue Length: " +
                                          vc.get("QUEUE_LENGTH") +
                                          "</li></ul></fieldset>");
        //vc.put("STATUS_JOBS","<fieldset id=\"jobs\">" +
        //             "<legend>Job Info</legend>" +
        //             "<table width='100%' cellspacing=0 cellpadding=3>" +
        //             "<tr><td>Executing:</td><td class=\"number\">" +
        //                                  dynamicSubstitutions.get("JOB_EXEC") +
        //             "</td></tr>" +
        //             "<tr><td>Waiting:</td><td class=\"number\">" +
        //                                  dynamicSubstitutions.get("JOB_WAIT") +
        //             "</td></tr>" +
        //             "<tr><td>Failed:</td><td class=\"number\">" +
        //                                  dynamicSubstitutions.get("JOB_FAILED") +
        //             "</td></tr>" +
        //             "<tr><td>Total:</td><td class=\"number\">" +
        //                                  dynamicSubstitutions.get("JOB_TOTAL") +
        //             "</td></tr>" +
        //             "<tr class=\"newgroup\"><td>Workers:</td><td class=\"number\">" +
        //                                  dynamicSubstitutions.get("JOB_WORKTHR") +
        //                                  "</td></tr></table></fieldset>");
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
