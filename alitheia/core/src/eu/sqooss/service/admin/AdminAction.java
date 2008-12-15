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

package eu.sqooss.service.admin;

import java.util.Map;

/**
 * Encapsulates an administrative action on a Alitheia Core.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public interface AdminAction {

    /**
     * Returns a unique identifier about the action. This name is used to
     * identify the action to the user, so it should be a short, user-readable
     * String.
     * 
     * @return The name of the action.
     */
    public String getActionName();

    /**
     * Returns a short description about the encapsulated action.
     * 
     * @return The name of the action.
     */
    public String getActionDescr();
    
    /**
     * Executes the action. If the result is false, call the {@link #getError()}
     * method to get the action result.
     * 
     * @param params
     *            The parameters to pass to the action.
     * @return True if the action succeeds. False otherwise.
     */
    public boolean execute(Map<String, Object> params);

    /**
     * Get the result of the action.
     * 
     * @return A String representing the result of the action.
     */
    public String getResult();

    /**
     * If the execution of the action produces an error, this method will return
     * it.
     * 
     * @return The error as encoded from the set of predefined errors.
     */
    public AdminActionError getError();

    /**
     * Help about the action and its parameters.
     * 
     * @return A key-value pair Map with help abou the action and its
     *         parameters. The returned Map contains at least an entry whose key
     *         is the name of the action (as returned by
     *         {@link #getActionName()}) and the value is the description of the
     *         action.
     */
    public Map<String, String> getHelp();
}
