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

package eu.sqooss.impl.service.tds;

import java.net.URI;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.InvalidAccessorException;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.tds.BTSAccessor;
import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.tds.SCMAccessor;

/**
 * A collection of accessors to project data sources. 
 */
public class ProjectDataAccessorImpl implements ProjectAccessor {
    private String bts;
    private String mail;
    private String scm;
    private String name;
    private long id;
    private BTSAccessor btsAccessor = null;
    private SCMAccessor scmAccessor = null;
    private MailAccessor mailAccessor = null;

    public static Logger logger = null;

    public ProjectDataAccessorImpl(long id, String name, String bts,
            String mail, String scm) {
        this.bts = bts;
        this.mail = mail;
        this.scm = scm;
        this.id = id;
        this.name = name;
    }

    // Interface functions
    /** {@inheritDoc} */
    public BTSAccessor getBTSAccessor() throws InvalidAccessorException {
    	try {
    		if (btsAccessor == null) {
				btsAccessor = (BTSAccessor) DataAccessorFactory.getInstance(
						URI.create(this.bts), name);
				if (btsAccessor == null) {
					logger.warn("Bug data accessor for project <" + name
							+ "> could not be initialized");
					throw new InvalidAccessorException(btsAccessor, 
							URI.create(this.bts));
				}
			}
    	} catch (Exception e) {
    		throw new InvalidAccessorException(btsAccessor, URI.create(this.bts));
    	}
        return btsAccessor;
    }

    /** {@inheritDoc} */
    public MailAccessor getMailAccessor() throws InvalidAccessorException {
    	try {
    		if (mailAccessor == null) {
    			mailAccessor = (MailAccessor) DataAccessorFactory.getInstance(
    					URI.create(this.mail), name);
    			if (mailAccessor == null) {
    				logger.warn("Mailing list accessor for project <" + name
                        + "> could not be initialized");
    				throw new InvalidAccessorException(mailAccessor, 
							URI.create(this.mail));
    			}
    		}
    	} catch (Exception e) {
    		throw new InvalidAccessorException(mailAccessor, URI.create(this.mail));
    	}
        return mailAccessor;
    }

    /** {@inheritDoc} */
    public SCMAccessor getSCMAccessor() throws InvalidAccessorException {
    	scm = scm.replace(" ", "%20").replace("\\", "/");
    	
    	try {
    		URI uri = URI.create(scm);
			if (scmAccessor == null) {
				scmAccessor = (SCMAccessor) DataAccessorFactory.getInstance(
						uri, name);
				if (scmAccessor == null) {
					logger.warn("SCM accessor for project <" + name
							+ "> could not be initialized");
					throw new InvalidAccessorException(scmAccessor, uri);
				}
			}
		} catch (IllegalArgumentException iae) {
			logger.error("Error " + iae.toString() + " creating URI from string: " + scm);
			return null;
		} catch (Exception e) {
			throw new InvalidAccessorException(scmAccessor, 
					URI.create(scm));
		}

        return scmAccessor;
    }

    /**
     * Get the project's system id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Get the project's name
     */
    public String getName() {
        return this.name;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

