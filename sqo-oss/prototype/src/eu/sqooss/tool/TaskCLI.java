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

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.hibernate.*;

import eu.sqooss.db.*;
import eu.sqooss.plugin.*;
import eu.sqooss.util.*;

/**
 * Command line handling for the task options (plugin execution)
 */
public class TaskCLI extends CLI {

    private Session session;
    private HashMap<String, Metric> metrics;

    TaskCLI(String args[]) {
        super(args);

        /* Arguments to task */
        options.addOption("pl", "plugin", true, "Plugin name");
        options.addOption("p", "project", true, "Project name");
        options.addOption("v", "version", true, "Project version");

        /* Help */
        options.addOption("h", "help", false, "Print online help");
    }

    public void parse() {
        TaskCLI tcli = new TaskCLI(args);
        CommandLine cmdLine = tcli.parseArgs();

        if (cmdLine == null || cmdLine.getOptions().length == 0
                || cmdLine.hasOption('h')) {
            System.out.println(HEADER);
            tcli.formatter.printHelp(" ", tcli.options);
            return;
        }
        if (!ensureOptions(cmdLine, "pl p v"))
            error("One of the required options (pl, p, v) is missing "
                    + "or has no argument", cmdLine);

        String plugin = getOptionValue(cmdLine, "pl");
        String project = getOptionValue(cmdLine, "p");
        String version = getOptionValue(cmdLine, "v");

        assert plugin != "";
        assert project != "";
        assert version != "";

        /* check if the plugin exists and is registered */
        Plugin p = checkPlugin(plugin);
        if (p == null)
            error("The requested plugin is not registered in the system");

        session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        /* check if the project exists and is registered */
        StoredProject pr = checkProject(project);
        if (pr == null)
            error("The requested project is not registered in the system");

        /* Check if the requested revision is available in the system */
        ProjectVersion pv = checkProjectRevision(version, pr);
        if (pv == null)
            error("The requested revision is not registered in the system");

        /* Retrieve the project files */
        List projectFiles = pv.getProjectVersionFiles();
        if (projectFiles.size() == 0)
            error("The specified revision does not contain any items");

        /* If we got this far, at last it's time to execute the Plugin */
        System.out.println(String.format("Executing Plugin %s for "
                + "Revision %s of project %s", plugin, version, project));

        performProcessing(p, pv, projectFiles);

        session.getTransaction().commit();
        System.out.println("Processing complete");
    }

    /**
     * Performs the processing by invoking the plugin for each project file
     * @param p
     *            The plugin to be used for processing
     * @param pv
     *            The version of the project to be processed
     * @param projectFiles
     *            The list of files belonging in the version to be processed
     */
    private void performProcessing(Plugin p, ProjectVersion pv, List projectFiles) {
        Iterator pfit = projectFiles.iterator();
        while (pfit.hasNext()) {
            ProjectFile pf = (ProjectFile) pfit.next();
            System.out.println(
                    String.format("Processing file %s", pf.getName()));
            HashMap<String, String> results;
            try {
                results = p.run(pf);
            } catch (PluginException pe) {
                System.out.println(String.format("An error occured while "
                        + "processing file %s:\n %s", pf.getName(),
                        pe.getMessage()));
                log(pe.toString());
                continue;
            }
            storeResults(results, pf, pv);
        }
    }

    /**
     * Stores the results (metrics) calculated for a file by the plugin
     * 
     * @param results
     *            A HashMap containing metric name - metric value pairs
     * @param pf
     *            The ProjectFile from which the results occured
     * @param pv
     *            The ProjectVersion that was processed
     */
    private void storeResults(HashMap<String, String> results, ProjectFile pf,
            ProjectVersion pv) {

        Date date = new Date();
        for (Entry<String, String> entry : results.entrySet()) {

            Measurement m = new Measurement();
            m.setProjectFile(pf);
            m.setProjectVersion(pv);
            m.setWhenRun(date);
            m.setMetric(metrics.get(entry.getKey()));
            m.setResult(entry.getValue());
            session.save(m);
        }
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

    /**
     * Checks whether a plugin is registered in the system and available
     * 
     * @param plugin
     *            The name of the plugin
     * @return An instance of the plugin
     */
    private Plugin checkPlugin(String plugin) {
        Plugin p = null;
        PluginList pl = PluginList.getInstance();
        ReadOnlyIterator roi = pl.getPlugins();
        while (roi.hasNext()) {
            Plugin current = (Plugin) roi.next();
            if (current.getName().equalsIgnoreCase(plugin)) {
                p = current;
                break;
            }
        }
        if (p != null) {
            // initialize the hashmap of the metrics supported by the plugin
            metrics = new HashMap<String, Metric>();
            for (Metric m : p.getMetrics()) {
                metrics.put(m.getName(), m);
            }
        }
        return p;
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
}
