/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 by Adriaan de Groot <groot@kde.org>
 *
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

package eu.sqooss.impl.service.webui;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.impl.service.logging.LogManagerConstants;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.util.SQOUtils;

import java.util.Hashtable;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;

// Java Extensions
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

public class WebUIServer extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public static final String pageHead = "<html><head><title>SQO-OSS Web UI</title><link rel='stylesheet' type='text/css' href='/css' /></head><body>";
    public static final String pageIntro = "<p class='box'>This is the administrative interface page for the Alitheia system. System stats are shown below.</p>";
    public static final String pageBundleStats = "<h1>Available Bundles</h1>";
    public static final String pageServiceStats = "<h1>Services</h1>";
    public static final String pageFooter = "</body></html>";

    private BundleContext bundlecontext = null;

    private LogManager logService = null;
    private Logger logger = null;
    private Hashtable<String,String[]> staticContentMap;
    private Hashtable<String,String> dynamicContentMap;
    private Hashtable<String,String> dynamicSubstitutions;

    public WebUIServer(BundleContext bc) {
        ServiceReference serviceRef = null;
        bundlecontext = bc;

        serviceRef = bc.getServiceReference("eu.sqooss.service.logging.LogManager");
        logService = (LogManager) bc.getService(serviceRef);

        if (logService != null) {
            logger = logService.createLogger(Logger.NAME_SQOOSS_WEBUI);

            if (logger != null) {
                logger.info("WebUIServer started");
            }
        } else {
            System.out.println("! Got neither a service nor a logger");
        }

        staticContentMap = new Hashtable<String,String[]>();
        String[] flossie = { "image/x-png", "/flossie.png" } ;
        String[] css = { "text/css", "/alitheia.css" } ;
        String[] logo = { "image/x-png", "/alitheia.png" } ;
        staticContentMap.put("logo", flossie);
        staticContentMap.put("css",css);
        staticContentMap.put("alitheia.png", logo);
        staticContentMap.put("alitheia.css", css);

        dynamicContentMap = new Hashtable<String,String>();
        dynamicContentMap.put("about","/about.html");
        dynamicContentMap.put("status","/index.html");
        dynamicContentMap.put("index","/index.html");

        dynamicSubstitutions = new Hashtable<String,String>();
    }

    protected String[] getServiceNames() {
        if ( bundlecontext != null ) {
            try {
                ServiceReference servicerefs[] = bundlecontext.getServiceReferences(
                    null,null);

                String names[] = new String[servicerefs.length];
                int i = 0;
                for (ServiceReference r : servicerefs) {
                    String s;
                    Object clazz = r.getProperty( org.osgi.framework.Constants.OBJECTCLASS );
                    if (clazz != null) {
                        s = SQOUtils.join( (String[])clazz, ", ");
                    } else {
                        s = "No class defined";
                    }
                    names[i++]=s;
                }

                return names;
            } catch (org.osgi.framework.InvalidSyntaxException e) {
                logger.severe("Invalid request syntax");
                return null;
            }
        } else {
            return null;
        }
    }

    protected String[] getBundleNames() {
        if ( bundlecontext != null ) {
            Bundle[] bundles = bundlecontext.getBundles();
            String names[] = new String[bundles.length];
            int i = 0;
            String[] statenames = { "uninstalled",
                "installed",
                "resolved",
                "starting",
                "stopping",
                "active" } ;
            String s;
            for (Bundle b : bundles) {
                int state = b.getState();
                s = b.getSymbolicName() + " = " + state + " (" + SQOUtils.bitfieldToString(statenames,state) + ")";;
                names[i++] = s;
            }
            return names;
        } else {
            return null;
        }
    }

    public String renderList(String[] names) {
        if (names.length > 0) {
            StringBuilder b = new StringBuilder();
            for (String s : names) {
                b.append("<li>" + s + "</li>");
            }
            return b.toString();
        } else {
            return "<li>&lt;none&gt;</li>";
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
    protected void sendResource(HttpServletResponse response, String mimeType, String path)
        throws ServletException, IOException {
        InputStream istream = getClass().getResourceAsStream(path);
        if ( istream == null ) {
            // TODO: Is there a more specific exception?
            throw new IOException( "Path not found: " + path );
        }

        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        int totalBytes = 0;

        if ( logger != null ) {
            logger.info("Serving " + path + " (" + mimeType + ")");
        }

        response.setContentType(mimeType);
        ServletOutputStream ostream = response.getOutputStream();
        while ( (bytesRead = istream.read(buffer)) > 0 ) {
            ostream.write(buffer,0,bytesRead);
            totalBytes += bytesRead;
        }

        // TODO: Check that the bytes written were as many as the
        //  file size in the JAR (how? it's an InputStream).
        if ( logger != null ) {
            logger.info("Wrote " + totalBytes + " from " + path);
        }
    }

    protected void sendTemplate(HttpServletResponse response, String path, Hashtable<String,String> subs)
        throws ServletException, IOException {
        BufferedReader istream = new BufferedReader(
            new InputStreamReader(getClass().getResourceAsStream(path)));
        if ( istream == null ) {
            // TODO: Is there a more specific exception?
            throw new IOException( "Path not found: " + path );
        }

        if ( logger != null ) {
            logger.info("Serving template " + path);
        }

        response.setContentType("text/html");
        PrintWriter print = response.getWriter();
        while ( istream.ready() ) {
            String line = istream.readLine();
            if ( line.startsWith("@@") && subs.containsKey(line) ) {
                print.println(subs.get(line));
            } else {
                print.println(line);
            }
        }
    }


    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException,
                                                              IOException {
        if ( logger != null ) {
            logger.info("GET path=" + request.getPathInfo());
        }

        String query = request.getPathInfo();
        if ( (query != null) && (staticContentMap.containsKey(query)) ) {
            String[] mapvalues = staticContentMap.get(query);
            sendResource(response, mapvalues[0], mapvalues[1]);
        } else {
            dynamicSubstitutions.clear();
            dynamicSubstitutions.put("@@STATUS","The cruncher is offline.");
            dynamicSubstitutions.put("@@LOGO","<img src='/logo' id='logo' alt='Logo' />");
            dynamicSubstitutions.put("@@COPYRIGHT","Copyright 2007 SQO-OSS Consortium members");
            dynamicSubstitutions.put("@@ABOUT","<p class='box'>This is the administrative interface.</p>");
            dynamicSubstitutions.put("@@BUNDLE",renderList(getBundleNames()));
            dynamicSubstitutions.put("@@SERVICE",renderList(getServiceNames()));
            if ( (query != null) && dynamicContentMap.containsKey(query) ) {
                sendTemplate(response,dynamicContentMap.get(query),dynamicSubstitutions);
            } else {
                sendTemplate(response,"/index.html",dynamicSubstitutions);
            }
        }
    }
}


