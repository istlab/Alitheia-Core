/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 Athens University of Economics and Business
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

package eu.sqooss.impl.metrics.modulemetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.metrics.modulemetrics.ModuleMetrics;
import eu.sqooss.metrics.modulemetrics.db.ModuleNOL;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.AlreadyProcessingException;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.fds.FileTypeMatcher;

public class ModuleMetricsImplementation extends AbstractMetric implements
        ModuleMetrics {

    // Mnemonic names of all metric dependencies
    private static String DEP_WC_LOC = "Wc.loc";

    // Mnemonic names of all supported metrics
    private static String MET_MNOF = "MNOF";
    private static String MET_MNOL = "MNOL";
    private static String MET_AMS = "AMS";

    // Holds the instance of the Alitheia core service
    private AlitheiaCore core;

    /**
     * Instantiates a new <code>ModuleMetricsImplementation</code> object.
     * 
     * @param bc the parent bundle's context object
     */
    public ModuleMetricsImplementation(BundleContext bc) {
        super(bc);

        super.addActivationType(ProjectFile.class);
        super.addActivationType(ProjectVersion.class);

        super.addMetricActivationType(MET_MNOF, ProjectFile.class);
        super.addMetricActivationType(MET_MNOL, ProjectFile.class);
        super.addMetricActivationType(MET_AMS, ProjectVersion.class);

        // Define the plug-in dependencies
        super.addDependency(DEP_WC_LOC);

        // Retrieve the instance of the Alitheia core service
        ServiceReference serviceRef = bc.getServiceReference(
                AlitheiaCore.class.getName());
        if (serviceRef != null)
            core = (AlitheiaCore) bc.getService(serviceRef);
    }

    public boolean install() {
        boolean result = super.install();
        if (result) {
            result &= super.addSupportedMetrics(
                    "Number of Files in Module",
                    MET_MNOF,
                    MetricType.Type.SOURCE_FOLDER);
            result &= super.addSupportedMetrics(
                    "Number of Lines in Module",
                    MET_MNOL,
                    MetricType.Type.SOURCE_FOLDER);
            result &= super.addSupportedMetrics(
                    "Average Module Size",
                    MET_AMS,
                    MetricType.Type.PROJECT_WIDE);
        }
        return result;
    }

    public List<ResultEntry> getResult(ProjectFile pf, Metric m) {
        // Prepare an array for storing the retrieved measurement results
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();

        /*
         * NOTE: Return a fake result on ProjectFile which is not a folder,
         * since we can not instruct the MetricActivator to not call us on
         * regular files.
         */
        if (pf.getIsDirectory() == false) {
            results.add(new ResultEntry(
                    0, ResultEntry.MIME_TYPE_TYPE_INTEGER, m.getMnemonic()));
            return results;
        }

        /*
         * TODO: This metric requires two parameters:
         *      - a major parameter - ProjectFile DAO
         *      - a minor parameter - ProjectVersion DAO
         * in order to provide correct results. Because of a known limitation
         * in the AbstractMetric.getResult() methods, right now the minor DAO
         * can not be passed as a parameter to this metric and therefore has
         * to be simulated. In this case the latest known ProjectVersion is
         * used instead of it.
         */
        ProjectVersion pv = ProjectVersion.getLastProjectVersion(
                pf.getProjectVersion().getProject());

        // Search for a matching measurement results
        String measurement = null;
        if (m.getMnemonic().equals(MET_MNOL)) {
            measurement = ModuleNOL.getResult(pf, pv);
        } else if (m.getMnemonic().equals(MET_MNOF)) {
            measurement = String.valueOf(ProjectFile.getFilesForVersion(
                    pv, pf.toDirectory(), ProjectFile.MASK_FILES).size());
        }

        // Convert the measurement into a result object
        if (measurement != null) {
            results.add(new ResultEntry(
                    Integer.parseInt(measurement),
                    ResultEntry.MIME_TYPE_TYPE_INTEGER,
                    m.getMnemonic()));
        }

        return results.isEmpty() ? null : results;
    }

    public List<ResultEntry> getResult(ProjectVersion pv, Metric m) {
        // Prepare an array for storing the retrieved measurement results
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();

        // Search for a matching measurement results
        List<ProjectVersionMeasurement> measurement = null;
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectVersion", pv);
        filter.put("metric", m);
        measurement = db.findObjectsByProperties(
                ProjectVersionMeasurement.class, filter);

        // Convert the measurement into a result object
        if (!measurement.isEmpty()) {
            results.add(new ResultEntry(
                    Float.parseFloat(measurement.get(0).getResult()),
                    ResultEntry.MIME_TYPE_TYPE_FLOAT,
                    m.getMnemonic()));
        }

        return results.isEmpty() ? null : results;
    }

    public void run(ProjectFile pf) {
        /*
         * Evaluate the "NOF" metric result.
         * NOTE: Evaluated on the fly, therefore just add an evaluation mark
         * for it.
         */
        markEvaluation(
                Metric.getMetricByMnemonic(MET_MNOF),
                pf.getProjectVersion().getProject());

        /*
         * Evaluate the "NOL" metric result.
         * NOTE: Evaluated during the "AMS" metric evaluation. Otherwise and
         * because of the current activation scheme, the consistency of the
         * "NOL" metric results (used during the "AMS" calculation) can not
         * be guaranteed.
         */
    }

    public void run(ProjectVersion pv) throws AlreadyProcessingException {
        // First, make sure that "AMS" is evaluated on the previous version.
        ProjectVersion prevVersion = pv.getPreviousVersion();
        if (prevVersion != null) {
            AlitheiaPlugin amsPlugin =
                core.getPluginAdmin().getImplementingPlugin(MET_AMS);
            List<Metric> AMS = new ArrayList<Metric>();
            AMS.add(Metric.getMetricByMnemonic(MET_AMS));
            try {
                amsPlugin.getResult(prevVersion, AMS);
            } catch (Exception e) {
                // Do nothing
            }
        }

        /*
         * Evaluate the "NOL" metric on all folders affected by changes in
         * this version.
         */
        Set<ProjectFile> changedFiles = pv.getVersionFiles();
        if (changedFiles != null)
            evalNOL(changedFiles);

        // Get the list of folders which exist in this project version.
        List<ProjectFile> folders = ProjectFile.getAllDirectoriesForVersion(pv);

        // Calculate the metric results
        int locs = 0;
        int sourceModules = 0;
        for (ProjectFile pf : folders) {
            // Try to retrieve the MNOL measurement for this folder
            try {
                int mnolValue = Integer.parseInt(ModuleNOL.getResult(pf, pv));
                /*
                 * TODO: Unless a getResult(majorDAO, minorDAO, metricMnemonic)
                 * is implemented, the line bellow should be left commented
                 * and the above call used instead.
                 */
//                int mnolValue += amsPlugin.getResult(pf, pv, MNOL)
//                        .getRow(0).get(0).getInteger();
                if (mnolValue > 0)
                    sourceModules++;
                locs += mnolValue;
            } catch (NumberFormatException ex) {
                continue;
            }
        } 

        // Store the "AMS" metric result
        Metric metric = Metric.getMetricByMnemonic(MET_AMS);
        ProjectVersionMeasurement ams = new ProjectVersionMeasurement(
                metric, pv, String.valueOf(0));
        if (sourceModules > 0)
            ams.setResult(String.valueOf(((float) (locs / sourceModules))));
        db.addRecord(ams);
        markEvaluation(metric, pv.getProject());
    }

    private void evalNOL(Set<ProjectFile> files) throws AlreadyProcessingException {
        // Get a reference to the WC.LOC metric dependency.
        List<Metric> locMetric = new ArrayList<Metric>();
        AlitheiaPlugin locPlugin = core.getPluginAdmin().getImplementingPlugin(
                DEP_WC_LOC);
        if (locPlugin == null) {
            log.error("Could not find the " + DEP_WC_LOC + " metric's plug-in");
            return;
        }
        locMetric.add(Metric.getMetricByMnemonic(DEP_WC_LOC));

        List<Long> alreadyEvaluated = new ArrayList<Long>();
        for (ProjectFile pf : files) {
            // Skip folders
            if (pf.getIsDirectory())
                continue;

            // Retrieve the DAO of the folder affected by this file change.
            ProjectFile affectedFolder = pf.getParentFolder();
            ProjectVersion affectedVer = pf.getProjectVersion();

            /*
             * Skip upon invalid parent folder DAO.
             * NOTE: For a file that resides in the project's root folder the
             * error message will not be generated.
             */
            if (affectedFolder == null) {
                Directory parentDir = pf.getDir();
                Directory rootDir = Directory.getDirectory(Directory.SCM_ROOT,
                        false);
                if ((parentDir == null) || (rootDir == null)
                        || (parentDir.getId() != rootDir.getId()))
                    log.error("Could not get enclosing directory for pf.id="
                            + pf.getId());
                continue;
            }

            // Do not evaluate the same folder twice
            if (alreadyEvaluated.contains(affectedFolder.getId()))
                continue;
            alreadyEvaluated.add(affectedFolder.getId());

            /*
             * Get the list of files residing in the affected directory in the
             * changed file's version
             */
            List<ProjectFile> pfs = ProjectFile.getFilesForVersion(affectedVer,
                    affectedFolder.toDirectory(), ProjectFile.MASK_FILES);

            // Calculate the "NOL" metric results
            int loc = 0;
            boolean foundSource = false;
            for (ProjectFile f : pfs) {
                if (FileTypeMatcher.getFileType(f.getName()) == FileTypeMatcher.FileType.SRC) {
                    // Found one source file, treat the folder as a source module
                    foundSource = true;
                }
                // Get the necessary measurement from the Wc.loc metric
                try {
                    if (FileTypeMatcher.isTextType(f.getName())) {
                        loc += locPlugin.getResult(
                                f, locMetric).getRow(0).get(0).getInteger();
                    }
                } catch (Exception e) {
                    log.error("Results of " + DEP_WC_LOC
                            + " metric for project: "
                            + f.getProjectVersion().getProject().getName()
                            + ", file: " + f.getFileName() + ", version: "
                            + f.getProjectVersion().getRevisionId()
                            + " could not be retrieved: " + e.getMessage());
                    // Do not store partial results
                    return;
                }
            }

            // Store the "NOL" metric result.
            if (foundSource) {
                Metric metric = Metric.getMetricByMnemonic(MET_MNOL);
                ModuleNOL mnol = new ModuleNOL(
                        affectedFolder, affectedVer, String.valueOf(loc));
                db.addRecord(mnol);
                markEvaluation(metric, affectedVer.getProject());
            }
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
