/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,  
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

package eu.sqooss.impl.service.admin;

import java.util.HashMap;
import java.util.Set;

import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.logging.Logger;

/**
 * The implementation of the system administration service.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public final class AdminServiceImpl implements AdminService {
    /** The actionid/class registry */
    private HashMap<String, Class<? extends AdminAction>> actionList;
    
    /** An instance to a logger*/
    public Logger log;
    
    public AdminServiceImpl(Logger l) {
        log = l;
        actionList = new HashMap<String, Class<? extends AdminAction>>();
    }
    
    /** {@inheritDoc} */
    public boolean registerAdminAction(String name, 
            Class<? extends AdminAction> clazz) {
        
        if (actionList.containsKey(name))
            return false;
        
        actionList.put(name, clazz);
        log.info("Registered admin action: " + clazz);
        return true;
    }
    
    /** {@inheritDoc} */
    public AdminAction getAction(String s) {
        Class<? extends AdminAction> a = actionList.get(s);
        if (a == null)
            return null;
        AdminAction aa = null;
        try {
            aa = a.newInstance();
        } catch (InstantiationException e) {
            log.warn("Cannot instantiate action class " + aa + 
                    ". Error: " + e.getMessage());
        } catch (IllegalAccessException e) {
            log.warn("Cannot instantiate action class " + aa + 
                    " Error: " + e.getMessage());
        }
        return aa;
    }
 
    /** {@inheritDoc} */
    public Set<String> getAllActionNames() {
        return actionList.keySet();
    }
}
