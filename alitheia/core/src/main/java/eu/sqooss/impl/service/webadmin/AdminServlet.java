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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;

import javax.activation.MimetypesFileTypeMap;
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
    //private BundleContext bc = null;

    // Logger given by our owner to write log messages to.
    private Logger logger = null;

    private DBService db = null;

    // Content tables
    private Hashtable<String, ActionController> dynamicContentMap = new Hashtable<>();
    private Hashtable<String, Pair<String, String>> staticContentMap = new Hashtable<>();
    private static MimetypesFileTypeMap mimeMap = new MimetypesFileTypeMap();
    static {
        // By default the JRE thinks CSS is an application/octet-stream
        mimeMap.addMimeTypes("text/css\tcss\tCSS");
        mimeMap.addMimeTypes("text/javascript\tjs\tJS");
    }

    // Dynamic substitutions
    VelocityContext vc = null;
    VelocityEngine ve = null;

    TranslationProxy tr = new TranslationProxy();

    public AdminServlet(BundleContext bc, WebadminService webadmin,
            Logger logger, VelocityEngine ve) {
        //this.webadmin = webadmin;
        //this.bc = bc;
        this.ve = ve;
        this.logger = logger;

        AlitheiaCore core = AlitheiaCore.getInstance();
        db = core.getDBService();

        // Create the static content map
        addStaticContent("/js/jquery.js", "/js/bootstrap.js",
                "/js/job_stats.js", "/css/bootstrap.css", "/css/screen.css",
                "/css/webadmin.css", "/img/sqo-oss.png", "/img/queue.png",
                "/img/uptime.png", "/img/greyBack.jpg", "/img/projects.png",
                "/img/logs.png", "/img/metrics.png", "/img/gear.png",
                "/img/header-repeat.png", "/img/add_user.png", "/img/edit.png",
                "/img/jobs.png", "/img/rules.png");

        ActionController pluginsController = new PluginsController();
        ActionController adminActionController = new AdminActionController(bc);
        ActionController logController = new LogController();
        ActionController projectsController = new ProjectsController();
        ActionController jobController = new JobController();
        
        // Create the dynamic content map
        addDynamicContent("/", pluginsController);
        addDynamicContent("/index", pluginsController);
        addDynamicContent("/projects", projectsController);
        // TODO: What did this do?
        //addDynamicContent("/projectlist", "projectslist.html");
        addDynamicContent("/logs", logController);
        addDynamicContent("/jobs", jobController);
        // TODO: What did this do?
        addDynamicContent("/users", "users.html");
        addDynamicContent("/rules", "rules.html");
        addDynamicContent("/jobstat", "jobstat.html");
        addDynamicContent("/admin", adminActionController);

        // Now the dynamic substitutions and renderer
        vc = new VelocityContext();
    }

    /**
     * Add content to the static map
     *
     * @param paths the paths to static files
     */
    private void addStaticContent(String... paths) {
        for (String path : paths) {
            Pair<String, String> p = new Pair<String, String>(path,
                    mimeMap.getContentType(path));
            staticContentMap.put(path, p);
        }
    }
    
    /**
     * Adds a link between a URL path and a template with a specific controller.
     *
     * @param path the path
     * @param template the template
     * @param actionController the action controller
     */
    private void addDynamicContent(String path, ActionController actionController) {
        dynamicContentMap.put(path, actionController);
    }
    
    /**
     * Adds a link between a URL path and a template without a specific controller.
     *
     * @param path the path
     * @param template the template
     */
    @Deprecated
    private void addDynamicContent(String path, String template) {
        ActionController actionController = new ActionController(template) {};
        addDynamicContent(path, actionController);
    }

    /**
     * Handle GET requests.
     *
     * @param request the request
     * @param response the response
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // ensure the database connection is active
        if (!db.isDBSessionActive()) {
            db.startDBSession();
        }

        try {
            String query = request.getPathInfo();

            // Add the request to the log
            logger.debug("GET:" + query);

            // This is static content
            // TODO REIMPLEMENT
            if ((query != null) && (staticContentMap.containsKey(query))) {
                sendResource(response, staticContentMap.get(query));
            } else if ((query != null)
                    && (dynamicContentMap.containsKey(query))) {
                final String ACTION_PARAMETER = "action";
                ActionController actionController = dynamicContentMap.get(query);
                String action = request.getParameter(ACTION_PARAMETER);
                if (null == action) {
                    action = "";
                } else {
                    action = action.trim();
                }
                HashMap<String, String> requestParameters = new HashMap<>();
                Enumeration<?> paramNames = request.getParameterNames();
                while (paramNames.hasMoreElements()) {
                    String key = (String) paramNames.nextElement();
                    if (ACTION_PARAMETER.equals(key)) {
                        continue;
                    }
                    requestParameters.put(key, request.getParameter(key));
                }
                actionController.render(action, vc, requestParameters);
                sendPage(response);
            }
        } catch (NullPointerException e) {
            logger.warn("Got a NPE while rendering a page.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
        }
    }

    /**
     * Handle POST requests.
     *
     * @param request the request
     * @param response the response
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Sends a resource (stored in the jar file) as a response. The mime-type is
     * set to @p mimeType . The @p path to the resource should start with a / .
     *
     * Test cases: - null mimetype, null path, bad path, relative path, path not
     * found, - null response
     *
     * TODO: How to simulate conditions that will cause IOException
     */
    protected void sendResource(HttpServletResponse response,
            Pair<String, String> source) throws ServletException, IOException {

        InputStream istream = getClass().getResourceAsStream(source.first);
        if (istream == null) {
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

    protected void sendPage(HttpServletResponse response) throws ServletException,
            IOException {

        Localization.initResources(Locale.ENGLISH);
        Template page;

        // Object-based substitutions
        vc.put("tr", tr); // translations proxy

        PrintWriter printer = response.getWriter();
        response.setContentType("text/html");

        try {
            page = ve.getTemplate("main.html");
            page.merge(vc, printer);
        } catch (Exception e) {
            logger.warn("Something went wrong!");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This is a class whose sole purpose is to provide a useful API from within
     * Velocity templates for the translation functions offered by the
     * AbstractView. Only one object needs to be created, and it forwards all
     * the label(), message() and error() calls to the translation methods of
     * the view.
     */
    public class TranslationProxy {
        public TranslationProxy() {
        }

        /** Translate a label */
        public String label(String s) {
            return Localization.getLbl(s);
        }

        /** Translate a (multi-line, html formatted) message */
        public String message(String s) {
            return Localization.getMsg(s);
        }

        /** Translate an error message */
        public String error(String s) {
            return Localization.getErr(s);
        }
    }

    public static String getMimeType(String path) {
        return mimeMap.getContentType(path);
    }
}
