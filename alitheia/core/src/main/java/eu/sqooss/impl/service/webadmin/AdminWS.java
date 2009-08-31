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

package eu.sqooss.impl.service.webadmin;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.scheduler.Scheduler;

public class AdminWS extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BundleContext bundleContext = null;
    private Scheduler scheduler = null;
    private DBService dbservice = null;

    public AdminWS( BundleContext bc ) {
        bundleContext = bc;
        ServiceReference srefCore = 
            bc.getServiceReference(AlitheiaCore.class.getName());
        if (srefCore != null) {
            AlitheiaCore core = (AlitheiaCore) bc.getService(srefCore);
            scheduler = core.getScheduler();
            dbservice = core.getDBService();
        } else {
            System.out.println("No CORE");
        }
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException,
                                                              IOException {
        long upTime = ManagementFactory.getRuntimeMXBean().getUptime();
        System.err.println(request.getPathInfo());
        response.setContentType("text/plain");
        PrintWriter print = response.getWriter();
        print.println("online=true");
        print.println("uptime=" + upTime);
        print.println("load=" + scheduler.getSchedulerStats().getWaitingJobs());
        dbservice.startDBSession();
        print.println("projects=" + StoredProject.getProjectCount());
        dbservice.rollbackDBSession();

        int count = 0;
        if (bundleContext != null) {
            Bundle[] bundles = bundleContext.getBundles();
            for ( Bundle b : bundles ) {
                String s = b.getSymbolicName();
                if (s.startsWith("eu.sqooss.metric")) {
                    count++;
                }
            }
        }
        print.println("metrics=" + count);
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

