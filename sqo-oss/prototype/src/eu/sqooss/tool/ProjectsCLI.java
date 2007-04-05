/*
 * Copyright (c) Members of the SQO-OSS Collaboration, 2007
 * All rights reserved by respective owners.
 * See http://www.sqo-oss.eu/ for details on the copyright holders.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the SQO-OSS project nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.sqooss.tool;

import org.apache.commons.cli.CommandLine;

/**
 * Handling of command line for and dispatching of project-related
 * functions
 */
public class ProjectsCLI extends CLI {

    protected ProjectsCLI(String[] args) {
        super(args);
        /*Functions*/
        options.addOption("ap", "add-project", false, "Add a project");
        options.addOption("av", "add-version", false,
                "Add a new version to an " + "existing project");
        options.addOption("lp", "list-projects", false, "List all projects");
        options.addOption("lv", "list-versions", false,
                "List versions of project");

        options.addOption("dp", "delete-project", false,
                "Delete a project and versions");
        options.addOption("dv", "delete-versions", false,
                "Delete a project version");

        /*Arguments to functions*/
        options.addOption("i", "project-id", false, "Project ID");
        options.addOption("n", "project-name", false, "Project Name");
        options.addOption("l", "project-local-path", false,
                "Project local path");
        options.addOption("c", "project-remote-part", false,
                "Project remote path");
        options.addOption("s", "project-svn-url", false, "Project SVN URL");

        /*Help*/
        options.addOption("h", "help", false, "Project functions and options");
    }

    public void parse(String[] args) {
        ProjectsCLI pcli = new ProjectsCLI(args);
        CommandLine cmdLine = pcli.parseArgs();

        if (!(cmdLine.hasOption("ap") || cmdLine.hasOption("av")
                || cmdLine.hasOption("lp") || cmdLine.hasOption("lv")
                || cmdLine.hasOption("dp") || cmdLine.hasOption("dv"))) {
            error("One of the ap, av, lp, lv, dp, dv options must be set", cmdLine);
            
            return;
        }
        
        if(cmdLine.hasOption("ap")) {
            if (!ensureOptions(cmdLine, "n l c s")) 
                error("Missing options", cmdLine);
        }
        
        if(cmdLine.hasOption("lp")) {
            
        }
    }
    

}
