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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.metrics.modulemetrics.ModuleMetrics;
import eu.sqooss.metrics.modulemetrics.db.ModuleNOL;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
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

        // Retrieve an instance of the Alitheia core service
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

        // Skip, if the given ProjectFile is not a folder
        if (pf.getIsDirectory() == false) {
            Integer value = 0;
            ResultEntry entry = new ResultEntry(
                    value,
                    ResultEntry.MIME_TYPE_TYPE_INTEGER,
                    m.getMnemonic());
            results.add(entry);
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
        ProjectVersion pv = ProjectVersion.getLastProjectVersion(pf
                .getProjectVersion().getProject());

        // Search for a matching measurement results
        String measurement = null;
        if (m.getMnemonic().equals(MET_MNOL)) {
            measurement = ModuleNOL.getResult(pf, pv);
        } else if (m.getMnemonic().equals(MET_MNOF)) {
            List<ProjectFile> pfs = ProjectFile.getFilesForVersion(
                    pv, pf.toDirectory(), ProjectFile.MASK_FILES);
            if (pfs != null)
                measurement = String.valueOf(pfs.size());
        }

        // Convert the measurement into a result object
        if (measurement != null) {
            Integer value = Integer.parseInt(measurement);
            ResultEntry entry = new ResultEntry(
                    value,
                    ResultEntry.MIME_TYPE_TYPE_INTEGER,
                    m.getMnemonic());
            results.add(entry);
        }

        return (results.isEmpty() ? null : results);
    }

    public List<ResultEntry> getResult(ProjectVersion v, Metric m) {
        // Prepare an array for storing the retrieved measurement results
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();

        // Search for a matching measurement results
        List<ProjectVersionMeasurement> measurement = null;
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectVersion", v);
        filter.put("metric", m);
        measurement = db.findObjectsByProperties(
                ProjectVersionMeasurement.class,
                filter);

        // Convert the measurement into a result object
        if (!measurement.isEmpty()) {
            float value = Float.parseFloat(measurement.get(0).getResult());
            ResultEntry entry = new ResultEntry(
                    value,
                    ResultEntry.MIME_TYPE_TYPE_FLOAT,
                    m.getMnemonic());
            results.add(entry);
        }

        return (results.isEmpty() ? null : results);
    }

    public void run(ProjectFile pf) {
        /*
         * Get a reference to the WC.LOC metric dependency.
         */
        List<Metric> locMetric = new ArrayList<Metric>();
        AlitheiaPlugin locPlugin = core.getPluginAdmin().getImplementingPlugin(
                DEP_WC_LOC);
        if (locPlugin != null) {
            locMetric = locPlugin.getSupportedMetrics();
        } else {
            log.error("Could not find the " + DEP_WC_LOC + " metric's plug-in");
            return;
        }

        /*
         * Retrieve the DAO of the folder which was affected by this file
         * change.
         */
        ProjectFile affectedDir;
        ProjectVersion affectedVer = pf.getProjectVersion();
        if (pf.getIsDirectory()) {
            affectedDir = pf;
        } else {
            affectedDir = pf.getParentFolder();
        }

        /*
         * Skip upon invalid folder DAO. Note: A project source tree's root
         * folder is skipped as well.
         */
        if (affectedDir == null) {
            Directory parentDir = pf.getDir();
            Directory rootDir = Directory.getDirectory(Directory.SCM_ROOT,
                    false);
            if ((parentDir == null) || (rootDir == null)
                    || (parentDir.getId() != rootDir.getId()))
                log.error("Could not get enclosing directory for pf.id="
                        + pf.getId());
            return;
        }

        /*
         * Get the list of files residing in the affected directory in the
         * changed file's version
         */
        List<ProjectFile> pfs = ProjectFile.getFilesForVersion(affectedVer,
                affectedDir.toDirectory(), ProjectFile.MASK_FILES);

        /*
         * Calculate the metric results
         */
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
                    loc += locPlugin.getResult(f, locMetric).getRow(0).get(0)
                            .getInteger();
                }
            } catch (MetricMismatchException e) {
                log.error("Results of " + DEP_WC_LOC + " metric for project: "
                        + f.getProjectVersion().getProject().getName()
                        + " file: " + f.getFileName() + "," + " version: "
                        + f.getProjectVersion().getRevisionId()
                        + " could not be retrieved: " + e.getMessage());
                // Do not store partial results
                return;
            }
        }

        /*
         * Store the "NOF" metric result.
         * NOTE: Evaluated on demand, therefore just add an evaluation mark.
         */
        markEvaluation(
                Metric.getMetricByMnemonic(MET_MNOF),
                affectedVer.getProject());

        /*
         * Store the "NOL" metric result.
         */
        HashMap<String, Object> nol_filter = new HashMap<String, Object>();
        nol_filter.put("projectFile", affectedDir);
        nol_filter.put("projectVersion", affectedVer);
        List<ModuleNOL> nol_exists = db.findObjectsByProperties(
                ModuleNOL.class, nol_filter);
        if ((nol_exists.isEmpty()) && (foundSource)) {
            Metric metric = Metric.getMetricByMnemonic(MET_MNOL);
            ModuleNOL mnol = new ModuleNOL(affectedDir, affectedVer, String
                    .valueOf(loc));
            db.addRecord(mnol);
            markEvaluation(metric, affectedVer.getProject());
        }
    }

    public void run(ProjectVersion pv) {
   /*     System.out.println ("Run AMS on DAO Id: " + pv.getId()
                + " Ver: " + pv.getRevisionId());
        /*
         * Get a reference to the MNOL metric dependency.
         *        
        List<Metric> MNOL = new ArrayList<Metric>();
        AlitheiaPlugin amsPlugin = core.getPluginAdmin().getImplementingPlugin(
                MET_AMS);
        if (amsPlugin != null) {
            MNOL.add(Metric.getMetricByMnemonic(MET_AMS));
        } else {
            log.error("Could not find the " + MET_AMS + " metric's plug-in");
            return;
        }

        ProjectVersion prevVersion = null;
        prevVersion = pv.getPreviousVersion();
        if (prevVersion != null) {
            List<Metric> AMS = new ArrayList<Metric>();
            AMS.add(Metric.getMetricByMnemonic(MET_AMS));
            try {
                amsPlugin.getResult(prevVersion, AMS);
            } catch (MetricMismatchException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }*/
        //System.out.println("Run AMS on version: " + pv.getRevisionId());

        /*
         * Get the list of folders which exists in the given project version.
         */
        List<ProjectFile> folders = ProjectFile.getAllDirectoriesForVersion(pv);
        
        /*
         * Calculate the metric results
         */
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
//                int mnolValue += plugin.getResult(pf, pv, MNOL)
//                        .getRow(0).get(0).getInteger();
                if (mnolValue > 0)
                    sourceModules++;
                locs += mnolValue;
            } catch (NumberFormatException ex) {
                continue;
            }
        } 

        /*
         * Store the "AMS" metric result
         */
        Metric metric = Metric.getMetricByMnemonic(MET_AMS);
        ProjectVersionMeasurement ams = new ProjectVersionMeasurement();
        ams.setMetric(metric);
        ams.setProjectVersion(pv);
        if (sourceModules > 0) {
            ams.setResult(String.valueOf(((float) (locs / sourceModules))));
        } else {
            ams.setResult(String.valueOf(0));
        }
        db.addRecord(ams);
        markEvaluation(metric, pv.getProject());
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
