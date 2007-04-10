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
import java.util.*;

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

    private final static String basepath;

    private Session session;

    static {
        basepath = Configurator.getInstance()
                   .getValue(ConfigurationOptions.SQOOSS_HOME)
                   + File.separatorChar + "projects";
    }

    ProjectsCLI(String[] args) {
        super(args);
        /* Functions */
        options.addOption("ap", "add-project", false, "Add a project");
        options.addOption("av", "add-version", false,
                "Add a new version to an existing project");
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
        options.addOption("l", "project-local-path", true,
                        "Project local path");
        options.addOption("r", "project-remote-path", true,
                "Project remote path");
        options.addOption("v", "project-version", true, "Project version");
        options.addOption("s", "project-svn-url", true, "Project SVN URL");

        /* Help */
        options.addOption("h", "help", false, "Project functions and options");
    }

    public void parse() {
        ProjectsCLI pcli = new ProjectsCLI(args);
        CommandLine cmdLine = pcli.parseArgs();        

        if(cmdLine == null) {
            error("Bad arguments");
        }
        
        if(cmdLine.getOptions().length == 0) {
            error( "No options set", cmdLine);
            return;
        }
        
        if(cmdLine.hasOption('h')) {
            System.out.println(HEADER);
            pcli.formatter.printHelp(" ", pcli.options);            
            return;
        }

        if (!(cmdLine.hasOption("ap") || cmdLine.hasOption("av")
                || cmdLine.hasOption("lp") || cmdLine.hasOption("lv")
                || cmdLine.hasOption("dp") || cmdLine.hasOption("dv") || cmdLine
                .hasOption("f"))) {
            error("One of the ap, av, lp, lv, dp, dv, f options must be set",
                    cmdLine);
            return;
        }

        String projectid = getOptionValue(cmdLine, "i");
        String projectName = getOptionValue(cmdLine, "n");
        String localPath = getOptionValue(cmdLine, "l");
        String remotePath = getOptionValue(cmdLine, "r");
        String svnurl = getOptionValue(cmdLine, "s");
        String version = getOptionValue(cmdLine, "v");

        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        /* Project file listing handling */
        if (cmdLine.hasOption("f")) {
            if (!ensureOptions(cmdLine, "n v")) {
                error("One of the required options (n, v) is missing "
                        + "or has no argument", cmdLine);
            }
            assert projectName != "";
            assert version != "";

            /* check if the project exists and is registered */
            StoredProject pr = checkProject(projectName);
            if (pr == null) {
                error("The requested project is not registered in the system");
            }
            /* Check if the requested revision is available in the system */
            ProjectVersion pv = checkProjectVersion(version, pr);
            if (pv == null) {
                error("The requested revision is not registered in the system");
            }
            /* list the project files */
            listProjectVersionFiles(pv);
            return;
        }

        if (cmdLine.hasOption("ap")) {
            if (!ensureOptions(cmdLine, "n r s")) {
                error("One of the required options (n,r,s) is missing "
                        + "or has no argument", cmdLine);
            }
            assert projectName != "";
            assert remotePath != "";
            assert svnurl != "";
            if (localPath == "") {
                localPath = basepath + File.separator + projectName;
            }

            long projectId = addProject(projectName, remotePath, localPath,
                    svnurl);
            session.getTransaction().commit();
            log("Added " + projectName + " with ID " + projectId);
            System.out.println("Project ID: " + projectId);
            return;
        }

        if (cmdLine.hasOption("av")) {
            if (!ensureOptions(cmdLine, "i v")) {
                error("One of the required options (i,v) is missing", cmdLine);
            }
            assert projectid != "";
            assert version != "";
            StoredProject pr = loadProject(projectid);
            if (pr == null) {
                error("The requested project is not registered");
            }
            addNewVersion(pr, version);
            session.getTransaction().commit();
            return;
        }

        if (cmdLine.hasOption("dp")) {
            if (!ensureOptions(cmdLine, "i")) {
                error("One of the required options (i) is missing", cmdLine);
            }
            assert projectid != "";
            StoredProject pr = loadProject(projectid);
            if (pr == null) {
                error("The requested project is not registered");
            }
            deleteProject(pr);
            session.getTransaction().commit();
            return;
        }

        if (cmdLine.hasOption("dv")) {
            if (!ensureOptions(cmdLine, "i v")) {
                error("One of the required options (i, v) is missing", cmdLine);
            }
            assert projectid != "";
            assert version != "";
            StoredProject pr = loadProject(projectid);
            if (pr == null) {
                error("The requested project is not registered");
            }
            ProjectVersion pv = checkProjectVersion(version, pr);
            if (pv == null) {
                error("The requested revision is not registered");
            }
            deleteProjectVersion(pv);
            session.getTransaction().commit();
            return;
        }

        if (cmdLine.hasOption("lp")) {
            listProjects();
            return;
        }

        if (cmdLine.hasOption("lv")) {
            if (!ensureOptions(cmdLine, "i")) {
                error("One of the required options (i) is missing", cmdLine);
            }
            assert projectid != "";
            listProjectVersions(projectid);
            return;
        }
    }

    /**
     * Checks out a project version and adds the relevant entries to the
     * database
     * 
     * @param project
     *            The project to work with
     * @param version
     *            Version number to add
     */
    private void addNewVersion(StoredProject project, String version) {
        log(String.format("Checking out version %s of project %s", version,
                project.getName()));
        checkoutProject(project, version);
    }

    /**
     * Adds a project to the StoredProject table and checks out its current
     * version to the base path.
     * 
     * @return the project id
     */
    private long addProject(String name, String remotePath, String localPath,
            String url) {
        String projectPath = null;
        if (localPath != null && localPath != "") {
            projectPath = localPath;
        } else {
            projectPath = basepath + File.separatorChar + name;
        }

        File f = new File(projectPath);
        if (f.exists()) {
            error("Project directory " + projectPath
                    + " already exists - not adding");
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

        StoredProject p = new StoredProject();
        p.setName(name);
        p.setLocalPath(projectPath);
        p.setRemotePath(remotePath);
        p.setSvnUrl(url);
        p.setContactPoint("admin@");
        p.setWebsite(url);
        p.setMailPath(projectPath + File.separatorChar + "mail");

        session.save(p);
        addNewVersion(p, String.valueOf(curver));
        return p.getId();
    }

    /**
     * Checks out a project revision at the location selected or the base
     * location for all projects
     * 
     * @param project
     * @param version
     */
    private void checkoutProject(StoredProject project, String version) {
        Repository r = null;
        Revision rev;
        try {
            r = RepositoryFactory.getRepository(project.getLocalPath(), project
                    .getSvnUrl());
        } catch (InvalidRepositoryException ire) {
            error("Failed to access the project's repository");
        }
        rev = new Revision(version); /*
                                      * if the version is in invalid format,
                                      * the HEAD revision will be used
                                      */
        r.checkout(rev);
        Vector<String> files = new Vector<String>();
        r.listEntries(files, "", rev);

        ProjectVersion pv = new ProjectVersion();
        pv.setStoredProject(project);
        pv.setVersion(String.valueOf(r.getCurrentVersion(false)));
        session.save(pv);

        for (String file : files) {
            try {
                ProjectFile pf = new ProjectFile();
		String localisedPath = project.getLocalPath()
		    + System.getProperty("file.separator") + file;
		File testFile = new File(localisedPath);
		if (!testFile.exists()) {
		    continue;
		}
		pf.setName(localisedPath);
                pf.setProjectVersion(pv);
                session.save(pf);
            } catch (Exception ex) {
                log("Failed to store information for file: " + file);
                continue;
            }
        }
    }

    /**
     * Deletes a project and all associated objects from the database
     * 
     * @param project
     *            The project to delete
     */
    private void deleteProject(StoredProject project) {
        Set projectVersions = project.getProjectVersions();
        Iterator it = projectVersions.iterator();
        while (it.hasNext()) {
            ProjectVersion pv = (ProjectVersion) it.next();
            deleteProjectVersion(pv);
        }
        session.delete(project);
    }

    /**
     * Deletes a project version and all associated objects from the database
     * 
     * @param pv
     *            The ProjectVersion to delete
     */
    private void deleteProjectVersion(ProjectVersion pv) {
        // delete all project files and the related measurements
        ArrayList<ProjectFile> files = pv.getProjectVersionFiles();
        for (ProjectFile pf : files) {
            Query q = session.createQuery("from Measurement m where "
                    + "m.projectVersion.id = :pvid AND "
                    + "m.projectFile.id = :pfid");
            q.setLong("pvid", pv.getId());
            q.setLong("pfid", pf.getId());
            List measurements = q.list();
            Iterator it = measurements.iterator();
            while (it.hasNext()) {
                Measurement m = (Measurement) it.next();
                session.delete(m);
            }
            session.delete(pf);
        }
        session.delete(pv);
    }

    /**
     * Lists the projects registered and stored in the database
     */
    private void listProjects() {

        Query q = session.createQuery("from eu.sqooss.db.StoredProject pr");
        List results = q.list();
        if (results.size() == 0) {
            System.out.println("No projects registered");
            return;
        }
        System.out.println(String.format("%4s%16s%30s%30s", "ID",
                "Project name", "Local path", "Repository url"));
        Iterator it = results.iterator();
        while (it.hasNext()) {
            StoredProject sp = (StoredProject) it.next();
            System.out.println(String.format("%4s%16s%%30s%30s", sp.getId(), sp
                    .getName(), sp.getLocalPath(), sp.getSvnUrl()));
        }
    }

    /**
     * Lists the versions of a project registered to the system
     * 
     * @param projectid
     *            The ID of the project whose versions are requested
     */
    private void listProjectVersions(String projectid) {
        Query q = session.createQuery("from ProjectVersion pv where "
                + "pv.project.id = :pid");
        q.setString("pid", projectid);
        List results = q.list();
        if (results.size() == 0) {
            System.out.println("No versions found");
            return;
        }
        System.out.println("ID   Version");
        Iterator it = results.iterator();
        while (it.hasNext()) {
            ProjectVersion pv = (ProjectVersion) it.next();
            System.out.println(String.format("%5s%s%", pv.getId(), pv
                    .getVersion()));
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
        System.out.println("Filename");
        for (ProjectFile pf : projectFiles) {
            System.out.println(pf.getName());
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
                "from StoredProject as sp where sp.name = :prname").setString(
                "prname", project).uniqueResult();
        return pr;
    }

    /**
     * Loads a project from the database
     * 
     * @param projectid
     *            The project id
     * @return An instance of the project
     */
    private StoredProject loadProject(String projectid) {

        StoredProject pr = (StoredProject) session.load(StoredProject.class,
                Long.parseLong(projectid));
        return pr;
    }

    /**
     * Checks whether a specific revision of a project is available and stored
     * in the database
     * 
     * @param version
     *            The version of the project
     * @param pr
     *            The project to be checked
     * @return The ProjectVersion corresponding to the given version
     */
    private ProjectVersion checkProjectVersion(String version, StoredProject pr) {
        ProjectVersion pv;
        Query q = session.createQuery("from ProjectVersion as pv where "
                + "pv.storedProject.id = :projid "
                + "and pv.version like :version");
        q.setLong("projid", pr.getId());
        q.setString("version", version);
        pv = (ProjectVersion) q.uniqueResult();
        return pv;
    }
}
