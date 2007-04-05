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

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.hibernate.Session;

import eu.sqooss.db.MetricType;
import eu.sqooss.db.StoredProject;
import eu.sqooss.util.HibernateUtil;
import eu.sqooss.vcs.InvalidRepositoryException;
import eu.sqooss.vcs.Repository;
import eu.sqooss.vcs.RepositoryFactory;

/**
 * Handling of command line for and dispatching of project-related
 * functions
 */
public class ProjectsCLI extends CLI {

    private static String basepath;
    
    static{
        basepath = Configurator.getInstance().getValue(ConfigurationOptions.SQOOSS_HOME) + 
            File.separatorChar   + "projects";
    }
    
    ProjectsCLI(String[] args) {
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
        options.addOption("i", "project-id", true, "Project ID");
        options.addOption("n", "project-name", true, "Project Name");
        options.addOption("l", "project-local-path", true,
                "Project local path");
        options.addOption("r", "project-remote-path", true,
                "Project remote path");
        options.addOption("v", "version", true, "Project version");
        options.addOption("s", "project-svn-url", true, "Project SVN URL");

        /*Help*/
        options.addOption("h", "help", true, "Project functions and options");
    }

    public void parse() {
        ProjectsCLI pcli = new ProjectsCLI(args);
        CommandLine cmdLine = pcli.parseArgs();

        if (!(cmdLine.hasOption("ap") || cmdLine.hasOption("av")
                || cmdLine.hasOption("lp") || cmdLine.hasOption("lv")
                || cmdLine.hasOption("dp") || cmdLine.hasOption("dv"))) {
            error("One of the ap, av, lp, lv, dp, dv options must be set", cmdLine);
            
            return;
        }
        
        if(cmdLine.hasOption("ap")) {
            if (!ensureOptions(cmdLine, "n r s")) 
                error("One of the required options (n,r,s) is missing " +
                        "or has no argument", cmdLine);
            
            String name = cmdLine.getOptionValue("n").trim();
            String remotePath = cmdLine.getOptionValue("r").trim();
            String url = cmdLine.getOptionValue("s").trim();
            String localPath = cmdLine.getOptionValue("l");
            
            assert name != null && name != "";
            assert remotePath != null && remotePath != "";
            assert url != null && url != "";
            
            //if(!checkProjectExists(name, localPath, remotePath, url))
            addProject(name, remotePath, localPath, url);
        }
        
        if(cmdLine.hasOption("av")) {
            if (!ensureOptions(cmdLine, "i v")) 
                error("One of the required options (i) is missing", cmdLine);
        }
        
        if(cmdLine.hasOption("lp")) {
            
        }
        
        if(cmdLine.hasOption("lv")) {
            if (!ensureOptions(cmdLine, "i")) 
                error("One of the required options (i) is missing", cmdLine);
        }
    }
    
    /**
     * Adds a project to the StoredProject table and checks out 
     * its current version to the base path 
     */
    
    private void addProject(String name, String remotePath, String localPath, String url) {
        
        String projectPath = null;
        
        if(localPath != null)
            projectPath = (localPath == basepath + File.separatorChar + name)?localPath:localPath;
        else
            projectPath = basepath + File.separatorChar + name;
        
        System.out.println(basepath);
        System.out.println(projectPath);
        
        File f = new File(projectPath);
        
        if(f.exists()) {
            error("Project directory already exists - not adding");
        }
        else {
          log("Creating project dir: " + f.getAbsolutePath());
          f.mkdirs();
        }
        
        Repository r = null;
        
        try {
            r = RepositoryFactory.getRepository(projectPath, url);
        } catch (InvalidRepositoryException e) {
            error(e.getMessage());
        }
        
        long curver = r.getCurrentVersion(false);
        log("Current project version:" + curver);
        
        f = new File(projectPath); 
       
        
        log("Adding project entry to the database");
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        
        session.beginTransaction();
        
        StoredProject p = new StoredProject();
        p.setName(name);
        p.setLocalPath(projectPath);
        p.setRemotePath(remotePath.trim());
        p.setSvnUrl(url);
        p.setContactPoint("admin@");
        p.setWebsite(url);
        p.setMailPath(projectPath + File.separatorChar + "mail");
        
        session.save(p);

        session.getTransaction().commit();
    }
}
