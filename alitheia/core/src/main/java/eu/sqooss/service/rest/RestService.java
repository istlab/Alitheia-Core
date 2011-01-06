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

/**
 * Alitheia Core REST API service. Allows custom paths to be registered under 
 * the /api namespace.  
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 *
 */
public interface RestService {
	
	/**
	 * Service name inside OSGi namespace service registration.
	 */
	public static final String SERVICE_NAME = RestService.class.getName();

	/**
	 * Add a resource to the registry. A resource is a JAX-RS annotated POJO.
	 * The class-level path annotation must always be equal to <code>/api</code>
	 * (i.e. <code>@Path("/api")</code>), otherwise the resource will not be
	 * accessible.
	 * 
	 * @param resource The resource to add.
	 */
	public void addResource(Class<?> resource);
	
	/**
	 * Remove a resource from the resource registry.
	 * @param resource  The resource to remove.
	 */
	public void removeResource(Class<?> resource);	
}