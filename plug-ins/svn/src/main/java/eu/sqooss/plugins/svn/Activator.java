/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.plugins.svn;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.plugins.scm.SCMActivator;
import eu.sqooss.plugins.tds.scm.SCMAccessor;
import eu.sqooss.plugins.tds.svn.SVNAccessorImpl;
import eu.sqooss.plugins.updater.scm.SCMUpdater;
import eu.sqooss.plugins.updater.svn.SVNUpdaterImpl;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;

public class Activator extends SCMActivator {
  
	@Override
	protected String[] getProtocols() {
		return new String[] {"svn", "svn-http", "svn-file"};
	}

	@Override
	protected Class<? extends SCMUpdater> getUpdaterClass() {
		return SVNUpdaterImpl.class;
	}

	@Override
	protected Class<? extends SCMAccessor> getAccessorClass() {
		return SVNAccessorImpl.class;
	}
}