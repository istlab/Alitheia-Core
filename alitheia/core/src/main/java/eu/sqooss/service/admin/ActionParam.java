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

package eu.sqooss.service.admin;

/**
 * All potential action parameters and associated help messages.
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public enum ActionParam {
    PROJECT_NAME("projectname", "The name used to register a project " +
    		"to the database"),
    PROJECT_ID("projectid", "The database key for the project"),
    CLUSTERNODE_NAME("clusternodename", " The name of a node in a " +
    		"SQO-OSS cluster."),
    PLUGIN_ID("pluginid", "The SQO-OSS id for specific plugin");
    
    private String name;
    private String help;
    
    private ActionParam(String name, String help) {
        this.name = name;
        this.help = help;
    }
    
    public String getName() {
        return name;
    }
    
    public String getHelp() {
        return help;
    }
    
    public static ActionParam fromString(String p) {
        if (p.equalsIgnoreCase(PROJECT_NAME.toString()))
            return PROJECT_NAME;
        if (p.equalsIgnoreCase(PROJECT_ID.toString()))
            return PROJECT_ID;
        if (p.equalsIgnoreCase(CLUSTERNODE_NAME.toString()))
            return CLUSTERNODE_NAME;
        return null;
    }
}