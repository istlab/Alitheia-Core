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

package eu.sqooss.service.tds;

import eu.sqooss.core.AlitheiaCoreService;

/**
 * The TDS service interface provides a way to retrieve and configure project
 * data access objects. The TDS is organised around project accessors: a project
 * data accessor is an utility that knows where the project's resources are and
 * how to instantiate the appropriate repository interfaces depending on the
 * repository type.
 * 
 * A typical lifecycle is as follows:
 * <ol>
 * <li>Check if there already is an accessor (optional); if there isn't this
 * indicated that the project has not been requested recently.</li>
 * <li>Request an accessor for the project. This may return null to inform that
 * the project does not exist. </li>
 * <li>If the accessor is returned, use its interface to get information from
 * the project.</li>
 * <li>When done, release the accessor.</li>
 * </ol>
 * 
 * <h2>Data accessor plug-ins</h2>
 * 
 * The TDS service currently supports 3 abstract types of project data
 * repositories:
 * <ul>
 * <li>Source Code Management (SCM) repositories</li>
 * <li>Mailing lists </li>
 * <li>Bug Tracking System (BTS) repositories</li>
 * </ul>
 * 
 * For each one of the supported project data repositories, there exists an
 * interface that abstracts the underlying data format. On project addition, the
 * TDS instantiates a concrete implementation of each one of the
 * <tt>ProjectAccessor</tt> sub interfaces, based on the URI of each project
 * resource.
 * 
 * To support this scheme, each new data accessor implementation must implement
 * all interfaces for the specific data source it provides access to and
 * register its implementation base with the
 * {@link eu.sqooss.impl.service.tds.TDSServiceImpl} class.
 * 
 * @see ProjectAccessor
 */

public interface TDSService extends AlitheiaCoreService {
    /**
     * Check that the given project exists in the TDS.
     * @param id project to check for
     * @return if the project is known to the TDS
     */
    public boolean projectExists(long id);

    /**
     * Check if the given project ID has an accessor object ready.
     * 
     * @param id project to check for
     * @return true if an accessor has already been created for this project. A
     *         project may exist, yet have no accessor yet.
     */
    public boolean accessorExists(long id);

    /**
     * Retrieve the accessor object for the given project id . May return null
     * if the accessor cannot be obtained.
     * 
     * @param id project to get the accessor for
     * @return accessor object or null
     */
    public ProjectAccessor getAccessor(long id);

    /**
     * Release a claim on the accessor. The accessor must have
     * been previously obtained by a call to getAccessor.
     * You may not use the accessor object after releasing it.
     * @param accessor object to release
     */
    public void releaseAccessor(ProjectAccessor tda);

    /**
     * Add an accessor for a project (usually a new project just
     * added to the system, in preparation for syncing it).
     *
     * @param id   Project ID
     * @param name Project name (informational only)
     * @param bts  Bug tracker URL
     * @param mail URL for email access
     * @param scm  URL for repository
     *
     */
    public void addAccessor(long id, String name, String bts, String mail, String scm);
    
    /**
     * Ask the Thin Data Access service if the provided URL is supported,
     * i.e. if there are parsers/accessors that can provide access to the data
     * pointed to by the URL
     * @param URL The URL to check for accessor
     * @return True if there is an accessor for the provided data source,
     * false otherwise.
     */
    public boolean isURLSupported(String URL);
    
    /**
     * Register a DataAccessor plug-in class. After registration the class 
     * becomes immediately available to the running system. Plug-ins can
     * thus register dynamically.
     * 
     * @param protocols The protocol that the plug-in supports
     * @param clazz The class that implements the DataAccessor plug-in
     */
    public void registerPlugin(String[] protocols, Class<? extends DataAccessor> clazz);
    
    /**
     * Unregister a previously registered DataAccessor plug-in.
     * @param clazz
     */
    public void unregisterPlugin(Class<? extends DataAccessor> clazz);
}

// vi: ai nosi sw=4 ts=4 expandtab

