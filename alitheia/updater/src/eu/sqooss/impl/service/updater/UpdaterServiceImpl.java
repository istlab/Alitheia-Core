/*
This file is part of the Alitheia system, developed by the SQO-OSS
consortium as part of the IST FP6 SQO-OSS project, number 033331.

Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package eu.sqooss.impl.service.updater;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.sqooss.services.updater.UpdaterService;
import eu.sqooss.service.logging.Logger;

public class UpdaterServiceImpl extends HttpServlet implements UpdaterService {

    private ServiceReference serviceRef = null;    
    private HttpService httpService = null;    
    private Logger logService = null;
    
    public UpdaterServiceImpl(BundleContext bc) throws ServletException, NamespaceException {
        
        logService = (Logger) bc.getServiceReference("eu.sqooss.service.logging.Logger");
        
        if (logService != null) {
            logService.setConfigurationProperty("file.name", "update-service.log");
            logService.setConfigurationProperty("message.format", "text/plain");
        }
        System.out.println("Got logging!");
        serviceRef = bc.getServiceReference("org.osgi.service.http.HttpService");
        if (serviceRef != null) {
            httpService = (HttpService) bc.getService(serviceRef);
            httpService.registerServlet("/updater", (Servlet) this, new Hashtable(), null);
        } else {
            logService.severe("Could not load the HTTP service."); 
        }
    }
    
    public void update(String path) {
        logService.info("Request to update path:" + path);
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException,IOException {
        String s = request.getParameter("path");
        if ( s != null )
            update(s);
        else {
            response.setContentType("text/html");
            response.getWriter().write("<html><head><title>Error</title></head><body><h1>Error</h1></body></html>");
        }
    }

}
