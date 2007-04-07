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

import org.apache.commons.cli.CommandLine;
import org.hibernate.*;

import eu.sqooss.db.*;
import eu.sqooss.plugin.*;
import eu.sqooss.util.*;

/**
 * Command line handling for the results options (metric retrieval)
 */
public class ResultsCLI extends CLI {

    private Session session;

    private HashMap<String, Metric> metrics;

    ResultsCLI(String args[]) {
        super(args);
        metrics = new HashMap<String, Metric>();

        /* Arguments to task */
        options.addOption("p", "project", true, "Project name");
        options.addOption("pl", "plugin", true, "Plugin Name");
        options.addOption("r", "revision", true, "Project revision");
        options.addOption("m", "metric", true, "Metric name(s)");

        /* Help */
        options.addOption("h", "help", false, "Print online help");
    }

    public void parse() {
        ResultsCLI rcli = new ResultsCLI(args);
        CommandLine cmdLine = rcli.parseArgs();

        if (cmdLine == null || cmdLine.getOptions().length == 0
                || cmdLine.hasOption('h')) {
            System.out.println(HEADER);
            rcli.formatter.printHelp(" ", rcli.options);
            return;
        }
        if (!ensureOptions(cmdLine, "p r"))
            error("One of the required options (p, r) is missing "
                    + "or has no argument", cmdLine);

        if (!(cmdLine.hasOption("pl") || cmdLine.hasOption("m")))
            error("One of the optional options (pl, m) must be set", cmdLine);

        String project = cmdLine.getOptionValue("p");
        String revision = cmdLine.getOptionValue("r");
        assert project != null && project != "";
        assert revision != null && revision != "";

        String plugin = "", metric = "";

        if (cmdLine.hasOption("pl")) {
            plugin = cmdLine.getOptionValue("pl");
            assert plugin != null && plugin != "";
        }
        if (cmdLine.hasOption("m")) {
            metric = cmdLine.getOptionValue("m");
            assert metric != null && metric != "";
        }

        session = HibernateUtil.getSessionFactory().getCurrentSession();

        if (cmdLine.hasOption("pl")) {
            /*
             * check if the plugin exists and is registered, and if so
             * initialize the list of metrics it supports
             */
            checkPlugin(plugin);
        } else {
            /* load the metric from the database and add it to the hashmap */
            checkMetric(metric);
        }

        /* check if the project exists and is registered */
        StoredProject pr = checkProject(project);
        if (pr == null)
            error("The requested project is not registered in the system");

        /* Check if the requested revision is available in the system */
        ProjectVersion pv = checkProjectRevision(revision, pr);
        if (pv == null)
            error("The requested revision is not registered in the system");

        /* Retrieve the project files */
        List projectFiles = pv.getProjectVersionFiles();
        if (projectFiles.size() == 0)
            error("The specified revision does not contain any items");

        /* If we got this far, it's time to retrieve the metric values */
        retrieveResults(pv, projectFiles);

        System.out.println("Processing complete");
    }

    /**
     * Performs the processing by retrieving the measurements for each project
     * file and for each metric
     * 
     * @param pv
     *            The version of the project to be processed
     * @param projectFiles
     *            The list of files belonging in the version to be processed
     */
    private void retrieveResults(ProjectVersion pv, List projectFiles) {

        printHeader();

        Iterator pfit = projectFiles.iterator();
        while (pfit.hasNext()) {
            ProjectFile pf = (ProjectFile) pfit.next();
            System.out.print(String.format("%40s", pf.getName()));

            for (Metric m : metrics.values()) {
                try {
                    Measurement measurement = retrieveMeasurement(pv, pf, m);
                    if (measurement != null)
                        System.out.print(String.format("%6s", 
                                measurement.getResult()));
                    else
                        System.out.print(String.format("%6s", "N/A"));
                } catch (Exception e) {
                    System.out.print(String.format("%6s", "ERR"));
                }
            }
            System.out.println();
        }
    }

    /**
     * Retrieves a single measurement from the database
     * 
     * @param pv
     *            The project version of the measurement
     * @param pf
     *            The project file of the measurement
     * @param m
     *            The metric of the measurement
     * @return The corresponding measurement instance
     */
    private Measurement retrieveMeasurement(ProjectVersion pv, ProjectFile pf, Metric m) {
        Query q = session.createQuery("from MEASUREMENT me where "
                + "me.METRIC_ID=:mid AND me.PROJECT_FILE_ID=:pfid "
                + "AND me.PROJECT_VERSION_ID=:pvid");
        q.setLong("mid", m.getId());
        q.setLong("pfid", pf.getId());
        q.setLong("pvid", pv.getId());

        Measurement measurement = (Measurement) q.uniqueResult();
        return measurement;
    }

    /**
     * Prints the header before the results
     */
    private void printHeader() {
        StringBuffer header = new StringBuffer();
        header.append(String.format("%40s", "File"));
        for (Metric m : metrics.values()) {
            header.append(String.format("%6s", m.getName()));
        }
        System.out.println(header.toString());
    }

    /**
     * Retrieves the list of files associated with a specific version of a
     * project
     * 
     * @param pv
     *            The ProjectVersion whose list of files is to be retrieved
     * @return A list containing ProjectFile objects
     */
    private List retrieveProjectFiles(ProjectVersion pv) {
        Query q = session.createQuery("from PROJECT_FILE pf where "
                + "pf.PROJECT_VERSION_ID = :projverid");
        q.setLong("projverid", pv.getId());
        List projectFiles = q.list();
        return projectFiles;
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
     * Checks whether a plugin is registered in the system and available and
     * loads the list of metrics it supports
     * 
     * @param plugin
     *            The name of the plugin
     */
    private void checkPlugin(String plugin) {
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
            // initialize the hashmap with the metrics supported by the plugin
            for (Metric m : p.getMetrics()) {
                metrics.put(m.getName(), m);
            }
        } else
            error("The requested plugin is not registered in the system");
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
     * Checks whether a metric is registered in the database and loads it
     * 
     * @param metric
     *            The metric name
     */
    private void checkMetric(String metric) {
        Metric m = (Metric) session.createQuery(
                "from METRIC as m where m.NAME = :mname").setString("mname",
                metric).uniqueResult();
        if (m != null) {
            metrics.put(m.getName(), m);
        }
    }
}
