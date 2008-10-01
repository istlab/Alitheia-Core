/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Adriaan de Groot <groot@kde.org>
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

import java.net.URI;
import java.util.List;

/**
 * The root of the accessor hierarchy. A data accessor is a class that 
 * provides access to a specific software repository data format. Each
 * accessor declares a list of URIs (actually, only the scheme part is 
 * of interest) for the file formats it supports. Implementing classes
 * must register themselves with the 
 * @link eu.sqooss.impl.service.tds.TDSServiceImpl
 * class. 
 * 
 * To comply with dynamic initialization requirements, all implementations
 * of this interface must have a default constructor with no arguments
 * declared. 
 * 
 * @see DataAccessorFactory, TDSServiceImpl
 */
public interface DataAccessor {
    
    /**
     * A list of schemes (for example <tt>http://</tt>, <tt>svn://</tt>,
     * <tt>maildir://</tt>) encoded as URIs that are supported by this data
     * accessor.
     * 
     * @return A non-empty list of URIs
     */
    public List<URI> getSupportedURLSchemes();
    
    /**
     * Initialise underlying structures/libraries and try to access the
     * provided URL. If no exception is thrown the implementation assumes
     * correct initialisation AND access to the underlying data store. This
     * method is used on project addition to validate user provided URLs.
     * 
     * @param dataURL URI pointing to the on disk/network location of the 
     * data store's location
     * @param projectName The name of the project this accessor is bound to
     * @throws AccessorException When an error occurs during initialisation
     * of project repository data
     */
    public void init(URI dataURL, String projectName) throws AccessorException;
}

// vi: ai nosi sw=4 ts=4 expandtab
