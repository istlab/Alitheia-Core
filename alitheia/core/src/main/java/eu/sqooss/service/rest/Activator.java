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
package eu.sqooss.service.rest;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.rest.impl.ResteasyServiceImpl;
import eu.sqooss.service.logging.Logger;

public class Activator implements BundleActivator {
	
    private Logger log;
    private ResteasyServiceImpl service;
    private ServiceRegistration sr;
    
	@SuppressWarnings("unchecked")
	public void start(BundleContext bc) throws Exception {
	   
	    Logger log = AlitheiaCore.getInstance().getLogManager().createLogger("sqooss.rest");
		log.info("Starting bundle " + bc.getBundle().getSymbolicName() 
				+ " [" + bc.getBundle() + "]");
		
		service = new ResteasyServiceImpl(bc);
		sr = bc.registerService(RestService.class.getName(), (RestService)service, new Hashtable());
		
		log.info("RESTEasy OSGi service started: " + RestService.class.getName());
		
		service.addResource(eu.sqooss.rest.api.StoredProjectResource.class);
		service.addResource(eu.sqooss.rest.api.MetricsResource.class);
	}

	public void stop(BundleContext bc) throws Exception {
		service.stop();
		sr.unregister();
	}
}