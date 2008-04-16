/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Adriaan de Groot <groot@kde.org>
 *
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

import java.util.Hashtable;

import javax.servlet.Servlet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

import eu.sqooss.service.webadmin.WebadminService;

public class WebadminServiceImpl implements WebadminService {
    private ServiceReference srefHTTPService    = null;
    private HttpService sobjHTTPService         = null;
    private AdminServlet servlet                = null;

    public WebadminServiceImpl(BundleContext bc) {
        // Get a reference to the HTTPService, and then its object
        srefHTTPService = bc.getServiceReference(
            HttpService.class.getName());
        if (srefHTTPService != null) {
            sobjHTTPService = (HttpService) bc.getService(srefHTTPService);
        }
        else {
            System.out.println(
                "[ERROR] Could not find a HTTP service!");
        }

        // Register the front-end servlets
        if (sobjHTTPService != null) {
            servlet = new AdminServlet(bc);
            try {
                sobjHTTPService.registerServlet(
                    "/",
                    (Servlet) servlet,
                    new Hashtable(),
                    null);
            }
            catch (Exception e) {
                System.out.println("[ERROR] AdminServlet: " + e);
            }

            try {
                sobjHTTPService.registerServlet(
                        "/ws",
                        (Servlet) new AdminWS(bc),
                        new Hashtable(),
                        null);
            }
            catch (Exception e) {
                System.out.println("[ERROR] AdminWS: " + e);
            }
        }
    }

    private static final String keyMOTD = "eu.sqoooss.alitheia.core.motd";
    private String valueMOTD = null;

    public String[] getConfigurationKeys() {
        String[] s = new String[1];
        s[0] = keyMOTD;
        return s;
    }

    public String getConfigurationProperty(String key) {
        if (keyMOTD.equals(key)) {
            return valueMOTD;
        }
        return null;
    }

    public void setConfigurationProperty(String key, String val) {
        if (keyMOTD.equals(key)) {
            valueMOTD = val;
        }
    }

    // Perform a self-test
    public Object selfTest() {
        return null;
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

