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

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.util.Pair;
import eu.sqooss.service.webadmin.WebadminService;

public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /// Logger given by our owner to write log messages to.
    private Logger logger = null;
    
    private DBService db = null;

    // Content tables
    private Hashtable<String, Pair<String, AbstractView>> dynamicContentMap = null;
    private Hashtable<String, Pair<String, String>> staticContentMap = null;

    // Dynamic substitutions
    VelocityContext vc = null;
    VelocityEngine ve = null;
    
    public AdminServlet(BundleContext bc,
            WebadminService webadmin,
            Logger logger,
            VelocityEngine ve) {
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
        dynamicContentMap = new Hashtable<String, Pair<String, AbstractView>>();
        Pair<String, AbstractView> pluginsPair =  new Pair<String, AbstractView>("index.html", new PluginsView(bc, vc));
        ProjectsView projectsView = new ProjectsView(bc, vc);
        Pair<String, AbstractView> projectsPair =  new Pair<String, AbstractView>("projects.html", projectsView);
        Pair<String, AbstractView> projectsListPair =  new Pair<String, AbstractView>("projectslist.html", projectsView);
        Pair<String, AbstractView> logsPair =  new Pair<String, AbstractView>("logs.html", new LogsView(bc, vc));
        JobsView jobsView = new JobsView(bc, vc);
        Pair<String, AbstractView> jobsPair =  new Pair<String, AbstractView>("jobs.html", jobsView);
        Pair<String, AbstractView> allJobsPair =  new Pair<String, AbstractView>("jobs_all.html", jobsView);
        Pair<String, AbstractView> jobStatPair =  new Pair<String, AbstractView>("job_stat.html", jobsView);
        Pair<String, AbstractView> rulesPair =  new Pair<String, AbstractView>("rules.html", new RulesView(bc, vc));
        Pair<String, AbstractView> resultsPair =  new Pair<String, AbstractView>("results.html", new ResultsView(bc, vc));
        
        dynamicContentMap.put("/", pluginsPair);
        dynamicContentMap.put("/index", pluginsPair);
        dynamicContentMap.put("/projects", projectsPair);
        dynamicContentMap.put("/projectlist", projectsListPair);
        dynamicContentMap.put("/logs", logsPair);
        dynamicContentMap.put("/jobs", jobsPair);
        dynamicContentMap.put("/alljobs", allJobsPair);
        dynamicContentMap.put("/jobstat", jobStatPair);
        dynamicContentMap.put("/rules", rulesPair);
        dynamicContentMap.put("/start", resultsPair);
        dynamicContentMap.put("/stop", resultsPair);
        dynamicContentMap.put("/restart", resultsPair);
        dynamicContentMap.put("/addproject", resultsPair);
        dynamicContentMap.put("/diraddproject", resultsPair);
        
        // Now the dynamic substitutions and renderer
        vc = new VelocityContext();
        
        // Object-based substitutions
        vc.put("tr", new TranslationProxy());
        vc.put("metrics", pluginsPair.second);
        vc.put("projects", projectsPair.second);
        vc.put("logs", logsPair.second);
        vc.put("jobs", jobsPair.second);
        vc.put("rules", rulesPair.second);
        vc.put("results", resultsPair.second);
    }

    /**
     * Add content to the static map
     */
    private void addStaticContent(String path, String type) {
        Pair<String, String> p = new Pair<String, String> (path,type);
        staticContentMap.put(path, p);
    }

    /**
     * Handles http GET requests, delegates most of its functionality to handleRequest
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException,
                                                              IOException {
    	if(request != null && request.getPathInfo() != null) {
    		logger.debug("POST:" +  request.getPathInfo());
    		handleRequest(request, response);
    	}
    }

    /**
     * Handles http POST requests, delegates most of its functionality to handleRequest
     */
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                               IOException {
    	if(request != null && request.getPathInfo() != null) {
    		logger.debug("POST:" +  request.getPathInfo());
    		handleRequest(request, response);
    	}
    }
    
    /**
     * Method that receives incoming requests and delegates further handling to the View classes  
     * 
     * @param request the received request
     * @param response the response that will be sent back
     * @throws ServletException
     * @throws IOException
     */
    private void handleRequest(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                               IOException {
       	if (!db.isDBSessionActive()) {
            db.startDBSession();
        } 
        
        try {
            String query = request.getPathInfo();
            if (staticContentMap.containsKey(query)) {
                sendResource(response, staticContentMap.get(query));
            }
            else if (dynamicContentMap.containsKey(query)) {
            	//Handle appropriate request
            	dynamicContentMap.get(query).second.exec(request);
            	//Serve content
                 sendPage(response, request, dynamicContentMap.get(query).first);
            }
        } catch (NullPointerException e) {
            logger.warn("Got a NPE while handling request data.");
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
            ostream.write(buffer, 0, bytesRead);
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
        AbstractView.initResources(Locale.ENGLISH);

        // Simple string substitutions
        vc.put("APP_NAME", AbstractView.getLbl("app_name"));
        vc.put("COPYRIGHT",
                "Copyright 2007-2008"
                + "<a href=\"http://www.sqo-oss.eu/about/\">"
                + "&nbsp;SQO-OSS Consortium Members"
                + "</a>");
        vc.put("LOGO", "<img src='/logo' id='logo' alt='Logo' />");
        
        vc.put("request", request);
    }  
    
    /**
     * This is a class whose sole purpose is to provide a useful API from
     * within Velocity templates for the translation functions offered by
     * the AbstractView. Only one object needs to be created, and it
     * forwards all the label(), message() and error() calls to the translation
     * methods of the view.
     */
    public class TranslationProxy {
        public TranslationProxy() { 
        }
        
        /** Translate a label */
        public String label(String s) {
            return AbstractView.getLbl(s);
        }
        
        /** Translate a (multi-line, html formatted) message */
        public String message(String s) {
            return AbstractView.getMsg(s);
        }
        
        /** Translate an error message */
        public String error(String s) {
            return AbstractView.getErr(s);
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
