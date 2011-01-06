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
package eu.sqooss.admin;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.rest.RestService;
import eu.sqooss.service.logging.Logger;

public class Activator implements BundleActivator {
    
    private Logger log;
    private ServiceRegistration sr;
    
    @SuppressWarnings("unchecked")
    public void start(BundleContext bc) throws Exception {
       
        Logger log = AlitheiaCore.getInstance().getLogManager().createLogger("sqooss.admin");
        log.info("Starting bundle " + bc.getBundle().getSymbolicName() 
                + " [" + bc.getBundle() + "]");
        
        AdminServiceImpl service = new AdminServiceImpl();
        sr = bc.registerService(AdminService.class.getName(), service, new Hashtable());
        
        log.info("Admin Service started: " + AdminServiceImpl.class.getName());
        
        ServiceReference restRef = bc.getServiceReference(RestService.class.getName());
        RestService rest = null;
        
        if (restRef != null) {
            rest = (RestService) bc.getService(restRef);
        } else {
            log.error("Could not find the Alitheia Core Rest service!");
        }

        rest.addResource(AdminServiceImpl.class);
    }

    public void stop(BundleContext bc) throws Exception {
        sr.unregister();
    }
}