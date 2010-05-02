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
package eu.sqooss.rest.impl;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import eu.sqooss.rest.ResteasyService;

public class ResteasyServiceImpl implements ResteasyService {
	
	private ServletContext context;
	
	public ResteasyServiceImpl(ServletContext context) {
		this.context = context;
	}
	
	public ResteasyProviderFactory getResteasyProviderFactory() {
		if (context != null) {
			return (ResteasyProviderFactory) context.getAttribute(ResteasyProviderFactory.class.getName());
		}else{
			return null;
		}
	}
	
	public Dispatcher getDispatcher() {
		if (context != null) {
			return (Dispatcher) context.getAttribute(Dispatcher.class.getName());
		}else{
			return null;
		}

	}
	
	public Registry getRegistry() {
		if (context != null) {
			return (Registry) context.getAttribute(Registry.class.getName());
		}else{
			return null;
		}
	}
	
	public void addSingletonResource(Object resource) {
		getRegistry().addSingletonResource(resource);
	}
	
	
	public void removeSingletonResource(Class<?> clazz) {
		getRegistry().removeRegistrations(clazz);
	}
	
	public void addApplication(Application a) {
	}
}