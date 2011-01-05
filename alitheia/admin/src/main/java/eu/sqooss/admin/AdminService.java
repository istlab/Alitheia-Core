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

import java.util.Set;

/**
 * A service that contains and executes administrative actions on behalf of
 * clients. 
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 */
public interface AdminService {
    
    /**
     * Get a list of instances of all administrative actions
     * 
     * @return List of administrative actions.
     */
    Set<AdminAction> getAdminActions();

    /**
     * Add an admininstrative action implementation to the list of
     * administrative actions.
     * 
     * @param uniq The unique name to register the action with
     * @param clazz The class that implements the action
     */
    void registerAdminAction(String uniq, Class<? extends AdminAction> clazz);
    
    /**
     * Create an instance of an administrative action given the unique 
     * action identifier.
     * 
     * @param uniq - The identified to create the action for
     * @return Instance of action or null if the action does not exist
     */
    AdminAction create(String uniq);
    
    /**
     * Execute an administrative action. 
     * 
     * @param a - The instance of the action to execute.
     */
    void execute(AdminAction a);
}
