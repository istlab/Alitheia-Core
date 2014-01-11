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
package eu.sqooss.impl.service.rest;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.rest.RestService;

public class ResteasyServiceImpl implements RestService {

	private BundleContext bc;
    private Logger log ;
   
	@Override
	public void addResource(Class<?> resource) {
		unregisterApp();
		RestServiceRegistry.getInstance().add(resource);
		registerApp();
	}

	@Override
	public void removeResource(Class<?> resource) {
		unregisterApp();
		RestServiceRegistry.getInstance().remove(resource);
		registerApp();
	}
	
	private void registerApp() {
		HttpService http = getHttpService();

		Dictionary<String, String> params = new Hashtable<String, String>();
		params.put("resteasy.scan", "false");
		params.put("javax.ws.rs.Application", "eu.sqooss.service.rest.RestServiceApp");

		ResteasyServlet bridge = new ResteasyServlet();
		try {
			http.registerServlet("/api", bridge, params, null);
		} catch (Exception e) {
			log.error("Error registering ResteasyServlet", e);
		}
	}

	private void unregisterApp() {
		HttpService http = getHttpService();
		http.unregister("/api");
	}
	
	private HttpService getHttpService() {
		HttpService http = null;
		ServiceReference httpRef = bc.getServiceReference(
				HttpService.class.getName());

		if (httpRef != null) {
			http = (HttpService) bc.getService(httpRef);
		} else {
			log.error("Could not find a HTTP service!");
		}
		
		return http;
	}

    @Override
    public boolean startUp() {
        addResource(eu.sqooss.rest.api.StoredProjectResource.class);
        addResource(eu.sqooss.rest.api.MetricsResource.class);
        addResource(eu.sqooss.rest.api.SchedulerResource.class);
        return true;
    }

    @Override
    public void shutDown() {
        unregisterApp();
    }

    @Override
    public void setInitParams(BundleContext bc, Logger l) {
        this.bc = bc;
        this.log = l;
    }
}