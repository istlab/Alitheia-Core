/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.service.admin;

import java.util.Set;

import eu.sqooss.core.AlitheiaCoreService;
import eu.sqooss.impl.service.admin.ActionBase;

/**
 * The core administration service. The purpose of the service is to 
 * encapsulate common administrative actions (e.g. add a project, 
 * trigger an update) that result from user input to the system as 
 * a set of reusable components for use by other administrative
 * services (e.g. the updater, webadmin and cluster services). 
 * <br>
 * The administration service is not available to metric plug-ins.
 * 
 * <h3>Action implementation notes</h3>
 * By convention, all administrative action implementations:
 * <ul>
 *   <li>expect an open database session </li>
 *   <li>inherit from {@link ActionBase}</li>
 *   <li>Use the {@link AdminActionError} enumeration to register 
 *   all error messages that they return.</li>  
 * </ul>
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 * @see AdminAction
 * @see ActionBase
 */
public interface AdminService extends AlitheiaCoreService {
    /**
     * Used by subsystem implementations to register shared administrative 
     * actions. 
     * 
     * @param name The action to register.
     * @param clazz The class that implements the action.
     * 
     * @return True if the registration succeeded. False if the provided 
     * action name is used by another action already.
     */
    public boolean registerAdminAction(String name, Class<? extends AdminAction> clazz);
    
    /**
     * Get the action corresponding to the provided action identifier.
     * 
     * @param s The action identifier for the required action. The caller 
     * must know the action identifier in advance.
     * @return An action instance corresponding to the provided identifier
     * or null if the action cannot be resolved.
     */
    public AdminAction getAction(String s);
    
    /**
     * Get a list of all actions as instantiated objects.
     * @return A potentially empty set of action identifiers.
     */
    public Set<String> getAllActionNames();
}
