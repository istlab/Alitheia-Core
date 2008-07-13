/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 Georgios Gousios <gousiosg@gmail.com>
 *
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

import java.sql.Timestamp;
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
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.fds.FileTypeMatcher;

public class ModuleMetricsImplementation extends AbstractMetric 
implements ModuleMetrics {
    
    private AlitheiaCore core;
    
    public ModuleMetricsImplementation(BundleContext bc) {
        super(bc);
        super.addActivationType(ProjectFile.class);
        
        super.addMetricActivationType("MNOF", ProjectFile.class);
        super.addMetricActivationType("MNOL", ProjectFile.class);
        super.addMetricActivationType("AMS", ProjectVersion.class);
        
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
                    MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Number of Lines in Module",
                    "MNOL",
                    MetricType.Type.SOURCE_CODE);
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
        if (! measurement.isEmpty()) {
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
        return null;
    }
    
    public void run(ProjectFile pf) {
        
        /*Get a ref to the wc plug-in*/
        List<Metric> locMetric = new ArrayList<Metric>();
        
        AlitheiaPlugin plugin = 
            core.getPluginAdmin().getImplementingPlugin("LOC");
        
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
            log.error("Could not get encosing directory for pf.id=" + pf.getId());
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
            if (FileTypeMatcher.getFileType(f.getName()) == 
                FileTypeMatcher.FileType.SRC) {
                /*Found one source file, treat the dir as source module*/
                foundSource = true;
            }
            /*Get measurement from the wc plugin*/
            try {
                loc += plugin.getResult(f, locMetric).getRow(0).get(0).getInteger();
            } catch (MetricMismatchException e) {
                log.error("Results of LOC metric for project: "
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
        mnof.setWhenRun(new Timestamp(System.currentTimeMillis()));
        mnof.setResult(String.valueOf(pfs.size()));

        db.addRecord(mnof);
        markEvaluation(metric, pf.getProjectVersion().getProject());
        
        if (foundSource) {
            metric = Metric.getMetricByMnemonic("MNOL");
            ProjectFileMeasurement mnol = new ProjectFileMeasurement();
            mnol.setMetric(metric);
            mnol.setProjectFile(pf);
            mnol.setWhenRun(new Timestamp(System.currentTimeMillis()));
            mnol.setResult(String.valueOf(pfs.size()));

            db.addRecord(mnol);
            markEvaluation(metric, pf.getProjectVersion().getProject());
        }
    }
    
    public void run(ProjectVersion pv) {
        /*
        metric = Metric.getMetricByMnemonic("AMS");
        ProjectFileMeasurement locc = new ProjectVersionMeasurement();
        locc.setMetric(metric);
        locc.setProjectFile(pf);
        locc.setWhenRun(new Timestamp(System.currentTimeMillis()));
        locc.setResult(String.valueOf(comments));

        db.addRecord(locc);
        markEvaluation(metric, pf.getProjectVersion().getProject());
         */
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
