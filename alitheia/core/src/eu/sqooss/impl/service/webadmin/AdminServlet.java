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

import eu.sqooss.impl.service.webadmin.WebAdminRenderer;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.util.Pair;
import eu.sqooss.service.webadmin.WebadminService;

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static BundleContext bc = null;
    private static WebadminService webadmin = null;

    /// Logger given by our owner to write log messages to.
    private Logger logger = null;

    // Content tables
    private Hashtable<String, String> dynamicContentMap = null;
    private Hashtable<String, Pair<String, String>> staticContentMap = null;

    // Dynamic substitutions
    VelocityContext vc = null;
    VelocityEngine ve = null;

    // Renderer of content
    WebAdminRenderer render = null;

    // Plug-ins view
    PluginsView pluginsView = null;

    // Users view
    UsersView usersView = null;

    // Invocation rules view
    RulesView rulesView = null;

    // Projects view
    ProjectsView projectsView = null;

    public AdminServlet(BundleContext bc,
            WebadminService webadmin,
            Logger logger,
            VelocityEngine ve) {
        this.webadmin = webadmin;
        this.bc = bc;
        this.ve = ve;
        this.logger = logger;

        // Create the static content map
        staticContentMap = new Hashtable<String, Pair<String, String>>();
        addStaticContent("/screen.css", "text/css");
        addStaticContent("/webadmin.css", "text/css");
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
        addStaticContent("/edit.png", "image/x-png");
        addStaticContent("/jobs.png", "image/x-png");
        addStaticContent("/rules.png", "image/x-png");

        // Create the dynamic content map
        dynamicContentMap = new Hashtable<String, String>();
        dynamicContentMap.put("/", "index.html");
        dynamicContentMap.put("/index", "index.html");
        dynamicContentMap.put("/projects", "projects.html");
        dynamicContentMap.put("/logs", "logs.html");
        dynamicContentMap.put("/jobs", "jobs.html");
        dynamicContentMap.put("/alljobs", "alljobs.html");
        dynamicContentMap.put("/users", "users.html");
        dynamicContentMap.put("/rules", "rules.html");

        // Now the dynamic substitutions and renderer
        vc = new VelocityContext();
        render = new WebAdminRenderer(bc, vc);

        // Create the various view objects
        rulesView = new RulesView(bc, vc);
        usersView = new UsersView(bc, vc);
        rulesView = new RulesView(bc, vc);
        projectsView = new ProjectsView(bc, vc);
    }

    /**
     * Add content to the static map
     */
    private void addStaticContent(String path, String type) {
        Pair<String, String> p = new Pair<String, String> (path,type);
        staticContentMap.put(path, p);
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException,
                                                              IOException {
        try {
            String query = request.getPathInfo();

            // Add the request to the log
            logger.debug("GET:" + query);

            // This is static content
            if (query.startsWith("/stop")) {
                vc.put("RESULTS", "<p>Alitheia Core is now shutdown.</p>");
                sendPage(response, request, "/results.html");

                // Now stop the system
                logger.info("System stopped by user request to webadmin.");
                try {
                    bc.getBundle(0).stop();
                } catch (BundleException be) {
                    logger.warn("Could not stop bundle 0.");
                    // And ignore
                }
                return;
            }
            if (query.startsWith("/restart")) {
                vc.put("RESULTS", "<p>Alitheia Core is now restarting.</p>");
                sendPage(response, request, "/results.html");

                //FIXME: How do we do a restart?
                return;
            }
            else if ((query != null) && (staticContentMap.containsKey(query))) {
                sendResource(response, staticContentMap.get(query));
            }
            else if ((query != null) && (dynamicContentMap.containsKey(query))) {
                sendPage(response, request, dynamicContentMap.get(query));
            }
        }
        catch (NullPointerException e) {
            logger.warn("Got a NPE while rendering a page.",e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                               IOException {
        try {
            String query = request.getPathInfo();

            // Add the request to the log
            logger.debug("POST:" + query);

            if (query.startsWith("/addproject")) {
                render.addProject(request);
                sendPage(response, request, "/results.html");
            } else if (query.startsWith("/diraddproject")) {
                render.addProjectDir(request);
                sendPage(response, request, "/results.html");
            }
            else if (query.startsWith("/motd")) {
                render.setMOTD(webadmin, request);
                sendPage(response, request, "/results.html");
            }
            else {
                doGet(request,response);
            }
        } catch (NullPointerException e) {
            logger.warn("Got a NPE while handling POST data.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
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
    }

    protected void sendPage(
            HttpServletResponse response,
            HttpServletRequest request,
            String path)
        throws ServletException, IOException {
        Template t = null;
        try {
            t = ve.getTemplate( path );
        } catch (Exception e) {
            logger.warn("Failed to get template <" + path + ">");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        StringWriter writer = new StringWriter();
        PrintWriter print = response.getWriter();

        // Do any substitutions that may be required
        createSubstitutions(request);
        response.setContentType("text/html");
        t.merge(vc, writer);

        print.print(writer.toString());
    }

    private void createSubstitutions(HttpServletRequest request) {
        // Initialize the resource bundles with the provided locale
        AbstractView.initResources(request.getLocale());

        // Simple string substitutions
        vc.put("APP_NAME", AbstractView.getLbl("app_name"));
        vc.put("PLUGINS_HEADER", AbstractView.getLbl("plugins_mngm"));
        vc.put("PROJECTS_HEADER", AbstractView.getLbl("projects_mngm"));
        vc.put("USERS_HEADER", AbstractView.getLbl("users_mngm"));
        vc.put("RULES_HEADER", AbstractView.getLbl("rules_mngm"));
        vc.put("COPYRIGHT",
                "Copyright 2007-2008"
                + "<a href=\"http://www.sqo-oss.eu/about/\">"
                + "&nbsp;SQO-OSS Consortium Members"
                + "</a>");
        vc.put("LOGO", "<img src='/logo' id='logo' alt='Logo' />");
        vc.put("MENU",
                "<ul id=\"menu\">"
                + "<li id=\"nav-1\"><a href=\"/index\">"
                + AbstractView.getLbl("plugins") + "</a></li>"
                + "<li id=\"nav-3\"><a href=\"/projects\">"
                + AbstractView.getLbl("projects") + "</a></li>"
                + "<li id=\"nav-6\"><a href=\"/users\">"
                + AbstractView.getLbl("users") + "</a></li>"
                + "<li id=\"nav-2\"><a href=\"/logs\">"
                + AbstractView.getLbl("logs") + "</a></li>"
                + "<li id=\"nav-4\"><a href=\"/jobs\">"
                + AbstractView.getLbl("jobs") + "</a></li>"
                + "<li id=\"nav-7\"><a href=\"/rules\">"
                + AbstractView.getLbl("rules") + "</a></li>"
                + "</ul>");
        vc.put("OPTIONS",
                "<fieldset id=\"options\">"
                + "<legend>" + AbstractView.getLbl("options") + "</legend>"
                + "<form id=\"motd\" method=\"post\" action=\"motd\">"
                + "<p>" + AbstractView.getLbl("motd") + ":</p><br/>"
                + "<input id=\"motdinput\" type=\"text\" name=\"motdtext\""
                + " class=\"form\"/>"
                + "<br/>"
                + "<input type=\"submit\" value=\""
                + AbstractView.getLbl("l0005") + "\" id=\"motdbutton\" />"
                + "</form>"
                + "<form id=\"start\" method=\"post\" action=\"restart\">"
                + "<input type=\"submit\" value=\""
                + AbstractView.getLbl("restart") + "\" />\n"
                + "</form>"
                + "<form id=\"stop\" method=\"post\" action=\"stop\">"
                + "<input type=\"submit\" value=\""
                + AbstractView.getLbl("stop") + "\" />"
                + "</form></fieldset>");

        // Function-based substitutions
        //vc.put("STATUS", someFunction); FIXME
        vc.put("GETLOGS", render.renderLogs());
        vc.put("UPTIME", render.getUptime());
        vc.put("QUEUE_LENGTH", render.getSchedulerDetails("WAITING"));
        vc.put("JOB_EXEC", render.getSchedulerDetails("RUNNING"));
        vc.put("JOB_WAIT", render.getSchedulerDetails("WAITING"));
        vc.put("JOB_WORKTHR", render.getSchedulerDetails("WORKER"));
        vc.put("JOB_FAILED", render.getSchedulerDetails("FAILED"));
        vc.put("JOB_TOTAL", render.getSchedulerDetails("TOTAL"));
        vc.put("WAITJOBS", render.renderWaitJobs());
        vc.put("FAILJOBS", render.renderFailedJobs());
        vc.put("JOBFAILSTATS", render.renderJobFailStats());
        // Plug-ins content
        vc.put("METRICS", pluginsView.render(request));
        // Users content
        vc.put("USERS", usersView.render(request));
        // Rules content
        vc.put("RULES", rulesView.render(request));
        // Projects content
        vc.put("PROJECTS", projectsView.render(request));

        // Composite substitutions
        vc.put("STATUS_CORE",
                "<fieldset id=\"status\">"
                + "<legend>" + AbstractView.getLbl("status") + "</legend>"
                + "<ul>"
                + "<li class=\"uptime\">" + AbstractView.getLbl("uptime")
                + ": " + vc.get("UPTIME") + "</li>"
                + "<li class=\"queue\">" + AbstractView.getLbl("queue_length")
                + ": " + vc.get("QUEUE_LENGTH") + "</li>"
                + "</ul>"
                + "</fieldset>");
        vc.put("STATUS_JOBS",
                "<fieldset id=\"jobs\">"
                + "<legend>" + AbstractView.getLbl("job_info") + "</legend>"
                + "<table width='100%' cellspacing=0 cellpadding=3>"
                + "<tr>"
                + "<td>" + AbstractView.getLbl("executing") + ":</td>"
                + "<td class=\"number\">" + vc.get("JOB_EXEC") + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>" + AbstractView.getLbl("waiting") + ":</td>"
                + "<td class=\"number\">" + vc.get("JOB_WAIT") + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>" + AbstractView.getLbl("failed") + ":</td>"
                + "<td class=\"number\">" + vc.get("JOB_FAILED") + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>" + AbstractView.getLbl("total") + ":</td>"
                + "<td class=\"number\">" + vc.get("JOB_TOTAL") + "</td>"
                + "</tr>"
                + "<tr class=\"newgroup\">"
                + "<td>" + AbstractView.getLbl("workers") + ":</td>"
                + "<td class=\"number\">" + vc.get("JOB_WORKTHR") + "</td>"
                + "</tr>"
                + "</table>"
                + "</fieldset>");
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
