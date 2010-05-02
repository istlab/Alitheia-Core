/*
 * Copyright 2010 - Organization for Free and Open Source Software,  
 *                 Athens, Greece.
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
package eu.sqooss.rest;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.rest.impl.ResteasyServiceImpl;
import eu.sqooss.rest.impl.ResteasyServlet;
import eu.sqooss.service.logging.Logger;

public class Activator implements BundleActivator {
	
    Logger log;
    
	@SuppressWarnings("unchecked")
	public void start(BundleContext bc) throws Exception {
	   
	    Logger log = AlitheiaCore.getInstance().getLogManager().createLogger("sqooss.rest");
		log.info("Starting bundle " + bc.getBundle().getSymbolicName() 
				+ " [" + bc.getBundle() + "]");
		
		HttpService http = null;
		ServiceReference httpRef = bc.getServiceReference(
				HttpService.class.getName());

		if (httpRef != null) {
			http = (HttpService) bc.getService(httpRef);
		} else {
			log.error("Could not find a HTTP service!");
			return;
		}
		
		Dictionary<String, String> params = new Hashtable<String, String>();
		params.put("resteasy.scan", "false");
		params.put("javax.ws.rs.Application", "eu.sqooss.rest.api.RestServiceApp");
		params.put("resteasy.interceptor.before.precedence", "SECURITY : BEGIN");
		params.put("resteasy.append.interceptor.precedence", "END");
		
		ResteasyServlet bridge = new ResteasyServlet();
		try {
			http.registerServlet("/api", bridge, params, null);
		} catch (Exception e) {
			log.error("Error registering ResteasyServlet", e);
		}
		
		ResteasyServiceImpl service = new ResteasyServiceImpl(bridge.getServletContext());
		bc.registerService(ResteasyServiceImpl.SERVICE_NAME,service,new Hashtable());
		
		log.info("RESTEasy OSGi service started: " + ResteasyServiceImpl.class.getName());
	}

	public void stop(BundleContext bc) throws Exception {
		log.info("Bundle rest stopped sucessfully");
	}
}