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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.hibernate.Query;
import org.hibernate.Session;

import eu.sqooss.db.*;
import eu.sqooss.util.HibernateUtil;
import eu.sqooss.vcs.*;

/**
 * Handling of command line for and dispatching of project-related functions
 */
public class ProjectsCLI extends CLI {

    private static String basepath;
    private Session session;

    static {
        basepath = Configurator.getInstance().getValue(
                ConfigurationOptions.SQOOSS_HOME)
                + File.separatorChar + "projects";
    }

    ProjectsCLI(String[] args) {
        super(args);
        /* Functions */
        options.addOption("ap", "add-project", false, "Add a project");
        options.addOption("av", "add-version", false,
                "Add a new version to an " + "existing project");
        options.addOption("lp", "list-projects", false, "List all projects");
        options.addOption("lv", "list-versions", false,
                "List versions of project");

        options.addOption("dp", "delete-project", false,
                "Delete a project and versions");
        options.addOption("dv", "delete-version", false,
                "Delete a project version");
        options.addOption("f", "file-list", false,
                "Displays the file list for a project");

        /* Arguments to functions */
        options.addOption("i", "project-id", true, "Project ID");
        options.addOption("n", "project-name", true, "Project Name");
        options
                .addOption("l", "project-local-path", true,
                        "Project local path");
        options.addOption("r", "project-remote-path", true,
                "Project remote path");
        options.addOption("v", "version", true, "Project version");
        options.addOption("s", "project-svn-url", true, "Project SVN URL");

        /* Help */
        options.addOption("h", "help", true, "Project functions and options");
    }

    public void parse() {
        ProjectsCLI pcli = new ProjectsCLI(args);
        CommandLine cmdLine = pcli.parseArgs();

        if (!(cmdLine.hasOption("ap") || cmdLine.hasOption("av")
                || cmdLine.hasOption("lp") || cmdLine.hasOption("lv")
                || cmdLine.hasOption("dp") || cmdLine.hasOption("dv")
                || cmdLine.hasOption("f"))) {
            error("One of the ap, av, lp, lv, dp, dv, f options must be set",
                    cmdLine);
            return;
        }
        
        String projectid = getOptionValue(cmdLine, "i");
        String projectname = getOptionValue(cmdLine, "n");
        String localPath = getOptionValue(cmdLine, "l");
        String remotePath = getOptionValue(cmdLine, "r");
        String svnurl = getOptionValue(cmdLine, "s");;
        String version = getOptionValue(cmdLine, "v");;

        session = HibernateUtil.getSessionFactory().getCurrentSession();
        
        /* Project file listing handling */
        if (cmdLine.hasOption("f")) {
            if (!ensureOptions(cmdLine, "n v")) {
                error("One of the required options (n, v) is missing "
                        + "or has no argument", cmdLine);
            }
            assert projectname != "";
            assert version != "";

            /* check if the project exists and is registered */
            StoredProject pr = checkProject(projectname);
            if (pr == null)
                error("The requested project is not registered in the system");

            /* Check if the requested revision is available in the system */
            ProjectVersion pv = checkProjectRevision(version, pr);
            if (pv == null)
                error("The requested revision is not registered in the system");

            /* list the project files */
            listProjectVersionFiles(pv);
            return;
        }

        if (cmdLine.hasOption("ap")) {
            if (!ensureOptions(cmdLine, "n r s"))
                error("One of the required options (n,r,s) is missing "
                        + "or has no argument", cmdLine);

            assert projectname != "";
            assert remotePath != "";
            assert svnurl != "";

            // if(!checkProjectExists(name, localPath, remotePath, url))
            addProject(projectname, remotePath, localPath, svnurl);
            session.getTransaction().commit();
            return;
        }

        if (cmdLine.hasOption("av")) {
            if (!ensureOptions(cmdLine, "i v"))
                error("One of the required options (i ,v) is missing", cmdLine);

            assert projectid != "";
            assert version != "";
            session.getTransaction().commit();
            return;
        }

        if(cmdLine.hasOption("dp")) {
            if(!ensureOptions(cmdLine, "i"))
                error("One of the required options (i) is missing", cmdLine);
            
            assert projectid != "";
            deleteProject(projectid);
            session.getTransaction().commit();
            return;
        }
        
        if(cmdLine.hasOption("dv")) {
            if(!ensureOptions(cmdLine, "i v"))
                error("One of the required options (i, v) is missing", cmdLine);
            
            assert projectid != "";
            assert version != "";
            //TODO deleteProjectVersion(pv);
            session.getTransaction().commit();
            return;
        }
        
        if (cmdLine.hasOption("lp")) {
            listProjects();
            return;
        }

        if (cmdLine.hasOption("lv")) {
            if (!ensureOptions(cmdLine, "i"))
                error("One of the required options (i) is missing", cmdLine);
            
            assert projectid != "";
            listProjectVersions(projectid);
            return;
        }
    }

    /**
     * Checks out a project version and adds the relevant entries to the
     * database
     * 
     * @param projectId
     *            The project id to work with
     * @param version
     *            Version number to add
     */
    private boolean addNewVersion(String projectId, String version) {

        log("Checking out current version");
        HashMap<String, String> project = StoredProject
                .getProjectInfo(projectId);
        // File f = new File(projectPath + File.separatorChar + "");

        return false;
    }

    /**
     * Adds a project to the StoredProject table and checks out its current
     * version to the base path
     */
    private void addProject(String name, String remotePath, String localPath,
            String url) {

        String projectPath = null;

        if (localPath != null)
            projectPath = (localPath == basepath + File.separatorChar + name) ? localPath
                    : localPath;
        else
            projectPath = basepath + File.separatorChar + name;

        System.out.println(basepath);
        System.out.println(projectPath);

        File f = new File(projectPath);

        if (f.exists()) {
            error("Project directory already exists - not adding");
        } else {
            log("Creating project dir: " + f.getAbsolutePath());
            f.mkdirs();
        }

        Repository r = null;

        try {
            r = RepositoryFactory.getRepository(projectPath, url);
        } catch (InvalidRepositoryException e) {
            error(e.getMessage());
        }

        long curver = r.getCurrentVersion(true);
        log("Current project version:" + curver);

        log("Adding project entry to the database");

        session.beginTransaction();

        StoredProject p = new StoredProject();
        p.setName(name);
        p.setLocalPath(projectPath);
        p.setRemotePath(remotePath);
        p.setSvnUrl(url);
        p.setContactPoint("admin@");
        p.setWebsite(url);
        p.setMailPath(projectPath + File.separatorChar + "mail");

        session.save(p);

        session.getTransaction().commit();
    }

    /**
     * Deletes a project and all associated objects from the database
     * @param projectid
     *                   The ID of the project to delete
     */
    private void deleteProject(String projectid) {
        //TODO
    }
    
    /**
     * Deletes a project version and all associated objects from the database
     * @param pv
     *           The ProjectVersion to delete
     */
    private void deleteProjectVersion(ProjectVersion pv) {
        //TODO
    }
    
    /**
     * Lists the projects registered and stored in the database
     */
    private void listProjects() {
        
        Query q = session.createQuery("from STORED_PROJECT pr");
        List results = q.list();
        if(results.size() == 0) {
            System.out.println("No projects registered");
            return;
        }
        System.out.println(String.format("%4s%16s%30s%30s",
                "ID", "Project name", "Local path", "Repository url"));
        Iterator it = results.iterator();
        while(it.hasNext()) {
            StoredProject sp = (StoredProject)it.next();   
            System.out.println(String.format("%4s%16s%%30s%30s",
                                             sp.getId(),
                                             sp.getName(),
                                             sp.getLocalPath(),
                                             sp.getSvnUrl()));
        }
    }
    
    /**
     * Lists the versions of a project registered to the system
     * @param projectid 
     *                   The ID of the project whose versions are requested
     */
    private void listProjectVersions(String projectid) {
        Query q = session.createQuery("from PROJECT_VERSION pv WHERE "
                + "pv.PROJECT_ID = :pid");
        q.setString("pid", projectid);
        List results = q.list();
        if(results.size() == 0) {
            System.out.println("No versions found");
            return;
        }
        System.out.println("ID   Version");
        Iterator it = results.iterator();
        while(it.hasNext()) {
            ProjectVersion pv = (ProjectVersion)it.next();   
            System.out.println(String.format("%5s%s%",
                                             pv.getId(),
                                             pv.getVersion()));
        }
    }
    
    /**
     * Lists the files associated with a project version
     * 
     * @param pv
     *            The project version
     */
    private void listProjectVersionFiles(ProjectVersion pv) {
        ArrayList<ProjectFile> projectFiles = pv.getProjectVersionFiles();
        System.out.println("File listing of version " + pv.getVersion());
        System.out.println("Status Filename");
        for (ProjectFile pf : projectFiles) {
            System.out.println(String.format("%6s %s", pf.getStatus(), pf
                    .getName()));
        }
    }

    /**
     * Checks whether a project is registered in the database
     * 
     * @param project
     *            The project name
     * @return An instance of the project
     */
    private StoredProject checkProject(String project) {
        StoredProject pr = (StoredProject) session.createQuery(
                "from STORED_PROJECT as sp where sp.NAME = :prname").setString(
                "prname", project).uniqueResult();
        return pr;
    }

    /**
     * Checks whether a specific revision of a project is available and stored
     * in the database
     * 
     * @param revision
     *            The revision of the project (ProjectVersion)
     * @param pr
     *            The project to be checked
     * @return The ProjectVersion corresponding to the given revision
     */
    private ProjectVersion checkProjectRevision(String revision,
            StoredProject pr) {
        ProjectVersion pv;
        Query q = session.createQuery("from PROJECT_VERSION pv where "
                + "pv.PROJECT_ID = :projid and pv.VERSION like :version");
        q.setLong("projid", pr.getId());
        q.setString("version", revision);
        pv = (ProjectVersion) q.uniqueResult();
        return pv;
    }
}