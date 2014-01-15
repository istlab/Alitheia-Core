/*
 * Copyright 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.plugins.git;

import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.plugins.scm.SCMActivator;
import eu.sqooss.plugins.tds.git.GitAccessor;
import eu.sqooss.plugins.updater.git.GitUpdater;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;

public class Activator extends SCMActivator {

    public void start(BundleContext bc) throws Exception {
        /* 
         * List of data protocols implemented by this plug-in
         * Only one accessor implementation per protocol is permitted.
         */
        String[] protocols = {"git-file"};

        /*
         * Register the plug-in accessor to the TDS service 
         */
        TDSService tds = getAlitheiaCoreInstance().getTDSService();
        tds.registerPlugin(protocols, GitAccessor.class);

        /*
         * Register the plug-in to the updater service
         */
        UpdaterService us = getAlitheiaCoreInstance().getUpdater();
        
        us.registerUpdaterService(GitUpdater.class);
    }
    
    public void stop(BundleContext context) throws Exception {
        UpdaterService us = getAlitheiaCoreInstance().getUpdater();
        us.unregisterUpdaterService(GitUpdater.class);
        
        TDSService tds = getAlitheiaCoreInstance().getTDSService();
        tds.unregisterPlugin(GitAccessor.class);
    }

	protected AlitheiaCore getAlitheiaCoreInstance() {
		return AlitheiaCore.getInstance();
	}

}

// vi: ai nosi sw=4 ts=4 expandtab

