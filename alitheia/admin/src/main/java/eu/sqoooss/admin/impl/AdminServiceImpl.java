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

package eu.sqoooss.admin.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import eu.sqooss.admin.AdminAction;
import eu.sqooss.admin.AdminService;
import eu.sqooss.admin.actions.ExecutableAdminAction;

/**
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 *
 */
public class AdminServiceImpl implements AdminService {

    HashMap<String, Class<? extends AdminAction>> services;
    
    @Override
    public Set<AdminAction> getAdminActions() {
        return new HashSet(services.values());
    }
    
    @Override
    public void registerAdminAction(String uniq,
            Class<? extends AdminAction> clazz) {
        services.put(uniq, clazz);
    }

    @Override
    public AdminAction create(String uniq) {
        Class<? extends AdminAction> clazz = services.get(uniq);
        
        if (clazz == null)
            return null;
        
        try {
            AdminAction aa = clazz.newInstance();
            return aa;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void execute(AdminAction a) {
        ((ExecutableAdminAction)a).execute();
    }
}
