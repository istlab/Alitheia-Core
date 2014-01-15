/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.util.Pair;
import eu.sqooss.service.webadmin.WebadminService;

public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final String globalTemplateLocation = "global.html";
    private static BundleContext bc = null;
    /// Logger given by our owner to write log messages to.
    private Logger logger = null;
    
    private DBService db = null;

    // Content tables
    private Hashtable<String, String> dynamicContentMap = null;
    private Hashtable<String, Pair<String, String>> staticContentMap = null;

    // Dynamic substitutions
    VelocityContext vc = null;
    VelocityEngine ve = null;

    // Renderer of content
    WebAdminRenderer adminView = null;

    // Plug-ins view
    PluginsView pluginsView = null;

    // Projects view
    ProjectsView projectsView = null;

    TranslationProxy tr;
    
    public AdminServlet(BundleContext bc,
            WebadminService webadmin,
            Logger logger,
            VelocityEngine ve) {
        AdminServlet.bc = bc;
        this.ve = ve;
        this.logger = logger;
        
        AlitheiaCore core = AlitheiaCore.getInstance();
        db = core.getDBService();
        
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
        dynamicContentMap.put("/projectlist", "projectslist.html");
        dynamicContentMap.put("/logs", "logs.html");
        dynamicContentMap.put("/jobs", "jobs.html");
        dynamicContentMap.put("/alljobs", "alljobs.html");
        dynamicContentMap.put("/users", "users.html");
        dynamicContentMap.put("/rules", "rules.html");
        dynamicContentMap.put("/jobstat", "jobstat.html");

        // Now the dynamic substitutions and renderer
        vc = new VelocityContext();
        adminView = new WebAdminRenderer(bc, vc);

        // Create the various view objects
        pluginsView = new PluginsView(bc, vc);
        projectsView = new ProjectsView(bc, vc);
        tr = new TranslationProxy(adminView);
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
        if (!db.isDBSessionActive()) {
            db.startDBSession();
        } 
        
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
        } catch (NullPointerException e) {
            logger.warn("Got a NPE while rendering a page.",e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
        }
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                               IOException {
        if (!db.isDBSessionActive()) {
            db.startDBSession();
        } 
        
        try {
            String query = request.getPathInfo();
            logger.debug("POST:" + query);

            if (query.startsWith("/addproject")) {
                //addProject(request);
                sendPage(response, request, "/results.html");
            } else if (query.startsWith("/diraddproject")) {
                AdminService as = AlitheiaCore.getInstance().getAdminService();
                AdminAction aa = as.create(AddProject.MNEMONIC);
                aa.addArg("dir", request.getParameter("properties"));
                as.execute(aa);
                if (aa.hasErrors())
                	vc.put("RESULTS", aa.errors());
                else
                	vc.put("RESULTS", aa.results());
                sendPage(response, request, "/results.html");
            } else {
                doGet(request,response);
            }
        } catch (NullPointerException e) {
            logger.warn("Got a NPE while handling POST data.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
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
        response.setContentType(source.second);
        ServletOutputStream ostream = response.getOutputStream();
        while ((bytesRead = istream.read(buffer)) > 0) {
            ostream.write(buffer,0,bytesRead);
        }
    }

    protected void sendPage(
            HttpServletResponse response,
            HttpServletRequest request,
            String path)
        throws ServletException, IOException {
    	
        Template t = null;
        String loc = null;
        try {
        	// get global template for all specific pages except 
        	// jobstat (a seperate html template showed in iframe in sidebar
        	if ( path.toLowerCase().contains("jobstat") ) {
        		loc = path;
        	} else {
        		loc = globalTemplateLocation;
        	}
            t = ve.getTemplate( loc );
        } catch (Exception e) {
        	logger.warn("Failed to get template <" + loc + ">");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        StringWriter writer = new StringWriter();
        PrintWriter print = response.getWriter();
        
        // put requested page into velocity context
        vc.put("CONTENTS", path);
        
        // 
        projectsView.render(request);

        // Do any substitutions that may be required
        createSubstitutions(request);
        response.setContentType("text/html");
        t.merge(vc, writer);

        print.print(writer.toString());
    }

    private void createSubstitutions(HttpServletRequest request) {
        // Initialize the resource bundles with the provided locale
        pluginsView.initResources(Locale.ENGLISH);

        // Simple string substitutions
        vc.put("APP_NAME", pluginsView.getLbl("app_name"));
        vc.put("COPYRIGHT",
                "Copyright 2007-2008"
                + "<a href=\"http://www.sqo-oss.eu/about/\">"
                + "&nbsp;SQO-OSS Consortium Members"
                + "</a>");
        vc.put("LOGO", "<img src='/logo' id='logo' alt='Logo' />");
        vc.put("UPTIME", adminView.getUptime());

        // Object-based substitutions
        vc.put("scheduler", adminView.sobjSched.getSchedulerStats());
        vc.put("tr",tr); // translations proxy
        vc.put("admin",adminView);
        vc.put("projects",projectsView);
        vc.put("metrics",pluginsView);
        vc.put("request", request); // The request can be used by the render() methods
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
