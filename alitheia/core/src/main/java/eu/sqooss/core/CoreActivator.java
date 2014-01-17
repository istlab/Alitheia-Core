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

package eu.sqooss.core;

import javax.inject.Inject;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.inject.Guice;

import static org.ops4j.peaberry.Peaberry.osgiModule;
import eu.sqooss.impl.service.admin.AdminServiceModule;
import eu.sqooss.impl.service.cluster.ClusterNodeModule;
import eu.sqooss.impl.service.db.DBServiceModule;
import eu.sqooss.impl.service.fds.FDSServiceModule;
import eu.sqooss.impl.service.logging.LogManagerModule;
import eu.sqooss.impl.service.metricactivator.MetricActivatorModule;
import eu.sqooss.impl.service.pa.PluginAdminModule;
import eu.sqooss.impl.service.rest.RestServiceModule;
import eu.sqooss.impl.service.scheduler.SchedulerServiceModule;
import eu.sqooss.impl.service.tds.TDSServiceModule;
import eu.sqooss.impl.service.updater.UpdaterServiceModule;
import eu.sqooss.impl.service.webadmin.WebAdminModule;


public class CoreActivator implements BundleActivator {

    /** Keeps the <code>AlitheaCore</code> instance. */
    @Inject
    private AlitheiaCore core;
    
    /** Keeps the <code>AlitheaCore</code>'s service registration instance. */
    private ServiceRegistration sregCore;

    public void start(BundleContext bc) throws Exception {
        try {
            Guice.createInjector(new AlitheiaCoreModule(), new DBServiceModule(),
                                 new MetricActivatorModule(), new RestServiceModule(),
                                 new SchedulerServiceModule(), new WebAdminModule(),
                                 new UpdaterServiceModule(), new AdminServiceModule(),
                                 new ClusterNodeModule(), new LogManagerModule(),
                                 new PluginAdminModule(), new TDSServiceModule(),
                                 new FDSServiceModule(), osgiModule(bc)
            ).injectMembers(this);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        core.init();
        sregCore = bc.registerService(AlitheiaCore.class.getName(), core, null);
    }
  
    public void stop(BundleContext bc) throws Exception {
    	core.shutDown();
    	if (sregCore != null) {
    		sregCore.unregister();
    	}
    	core = null;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
