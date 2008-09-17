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
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.metrics.modulemetrics.ModuleMetrics;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.fds.FileTypeMatcher;

public class ModuleMetricsImplementation extends AbstractMetric 
implements ModuleMetrics {
    
    private AlitheiaCore core;
    
    public ModuleMetricsImplementation(BundleContext bc) {
        super(bc);
        super.addActivationType(ProjectFile.class);
        super.addActivationType(ProjectVersion.class);
        
        super.addMetricActivationType("MNOF", ProjectFile.class);
        super.addMetricActivationType("MNOL", ProjectFile.class);
        super.addMetricActivationType("AMS", ProjectVersion.class);
        
        super.addDependency("Wc.loc");
        
        ServiceReference serviceRef = null;
        serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) bc.getService(serviceRef);
    }

    public boolean install() {
        boolean result = super.install();
        if (result) {
            result &= super.addSupportedMetrics(
                    "Number of Files in Module",
                    "MNOF",
                    MetricType.Type.SOURCE_FOLDER);
            result &= super.addSupportedMetrics(
                    "Number of Lines in Module",
                    "MNOL",
                    MetricType.Type.SOURCE_FOLDER);
            result &= super.addSupportedMetrics(
                    "Average Module Size", 
                    "AMS",
                    MetricType.Type.PROJECT_WIDE);
        }
        return result;
    }

    public List<ResultEntry> getResult(ProjectFile a, Metric m) {
        
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();
        // Search for a matching project file measurement
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectFile", a);
        filter.put("metric", m);
        List<ProjectFileMeasurement> measurement =
            db.findObjectsByProperties(ProjectFileMeasurement.class, filter);

        // Convert the measurement into a result object
        if (!measurement.isEmpty()) {
            // There is only one measurement per metric and project file
            Integer value = Integer.parseInt(measurement.get(0).getResult());
            // ... and therefore only one result entry
            ResultEntry entry = 
                new ResultEntry(value, ResultEntry.MIME_TYPE_TYPE_INTEGER, m.getMnemonic());
            results.add(entry);
            return results;
        }
        return null;
    }

    public List<ResultEntry> getResult(ProjectVersion v, Metric m) {
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();
        // Search for a matching project version measurement
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectVersion", v);
        filter.put("metric", m);
        List<ProjectVersionMeasurement> measurement =
            db.findObjectsByProperties(ProjectVersionMeasurement.class, filter);

        // Convert the measurement into a result object
        if (!measurement.isEmpty()) {
            // There is only one measurement per metric and project file
            Integer value = Integer.parseInt(measurement.get(0).getResult());
            // ... and therefore only one result entry
            ResultEntry entry = 
                new ResultEntry(value, ResultEntry.MIME_TYPE_TYPE_FLOAT, m.getMnemonic());
            results.add(entry);
            return results;
        }
        return null;
    }
    
    public void run(ProjectFile pf) {
        
        /*Get a ref to the wc plug-in*/
        List<Metric> locMetric = new ArrayList<Metric>();
        
        AlitheiaPlugin plugin = 
            core.getPluginAdmin().getImplementingPlugin("Wc.loc");
        
        if (plugin != null) {
            locMetric = plugin.getSupportedMetrics();
        } else {
            log.error("Could not find the WC plugin");
            return;
        }
        
        ProjectFile parent;
        
        if (!pf.getIsDirectory()) {
            parent = ProjectFile.getParentFolder(pf);
        } else {
            parent = pf;
        }
        
        if (parent == null) {
            Directory parentDir = pf.getDir();
            Directory rootDir = Directory.getDirectory(
                    Directory.SCM_ROOT, false);
            if ((parentDir == null) 
                    || (rootDir == null) 
                    || (parentDir.getId() != rootDir.getId()))
                log.error("Could not get enclosing directory for pf.id="
                        + pf.getId());
            return;
        }
        
        /*Get a list of files for the directory*/
        List<ProjectFile> pfs = ProjectFile.getFilesForVersion(
                parent.getProjectVersion(), parent.getDir(), 
                ProjectFile.MASK_FILES);
        
        int loc = 0;
        boolean foundSource = false;
        Iterator<ProjectFile> i = pfs.iterator();
        
        while (i.hasNext()) {
            ProjectFile f = i.next();
            if (FileTypeMatcher.getFileType(f.getName()) == FileTypeMatcher.FileType.SRC) {
                /*Found one source file, treat the dir as source module*/
                foundSource = true;
            }
            /*Get measurement from the wc plugin*/
            try {
                if (FileTypeMatcher.isTextType(f.getName())) {
                    loc += plugin.getResult(f, locMetric).getRow(0).get(0).getInteger();
                }
            } catch (MetricMismatchException e) {
                log.error("Results of wc.loc metric for project: "
                        + f.getProjectVersion().getProject().getName() + " file: "
                        + f.getFileName() + ", Version: "
                        + f.getProjectVersion().getVersion() + " could not be retrieved: "
                        + e.getMessage());
              //Do not store partial results
              return;
            } 
        }
        
        // Store the results
        Metric metric = Metric.getMetricByMnemonic("MNOF");
        ProjectFileMeasurement mnof = new ProjectFileMeasurement();
        mnof.setMetric(metric);
        mnof.setProjectFile(pf);
        mnof.setResult(String.valueOf(pfs.size()));

        db.addRecord(mnof);
        markEvaluation(metric, pf.getProjectVersion().getProject());
        
        if (foundSource) {
            metric = Metric.getMetricByMnemonic("MNOL");
            ProjectFileMeasurement mnol = new ProjectFileMeasurement();
            mnol.setMetric(metric);
            mnol.setProjectFile(pf);
            mnol.setResult(String.valueOf(pfs.size()));

            db.addRecord(mnol);
            markEvaluation(metric, pf.getProjectVersion().getProject());
        }
    }
    
    public void run(ProjectVersion pv) {
       
        AlitheiaPlugin plugin = 
            core.getPluginAdmin().getImplementingPlugin("MNOL");
        
        if (plugin == null) {
            log.error("Could not find the ModuleMetrics plugin");
            return;
        }
        
        int locs = 0;

        List<Metric> MNOL = new ArrayList<Metric>();
        MNOL.add(Metric.getMetricByMnemonic("MNOL"));
        
        List<ProjectFile> dirs = ProjectFile.getFilesForVersion(pv, 
                Directory.getDirectory("/", false), 
                ProjectFile.MASK_DIRECTORIES);
        int sourceModules = 0;
        
        for (ProjectFile dir : dirs) {
            boolean isSourceModule = false;
            
            /*Check whether the current dir contains source code files*/
            List<ProjectFile> files = ProjectFile.getFilesForVersion(pv, 
                    dir.getDir(), ProjectFile.MASK_FILES);
            
            for (ProjectFile file : files) {
                if (FileTypeMatcher.getFileType(file.getFileName()).equals(
                        FileTypeMatcher.FileType.SRC)) {
                    isSourceModule = true;
                    sourceModules ++;
                    break;
                }
            }
            
            if (!isSourceModule) {
                continue;
            }
            
            //Get the MNOL measurement for this dir from the DB
            try {
                locs += plugin.getResult(dir, MNOL).getRow(0).get(0).getInteger();
            } catch (MetricMismatchException e) {
                log.error("Results of MNOL metric for project: "
                        + dir.getProjectVersion().getProject().getName() + " file: "
                        + dir.getFileName() + ", Version: "
                        + dir.getProjectVersion().getVersion() + " could not be retrieved: "
                        + e.getMessage());
            }
        } 
       
        Metric metric = Metric.getMetricByMnemonic("AMS");
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
