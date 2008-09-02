/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.impl.metrics.clmt;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.clmt.cache.Cache;
import org.clmt.configuration.Calculation;
import org.clmt.configuration.Filename;
import org.clmt.configuration.Source;
import org.clmt.configuration.Task;
import org.clmt.configuration.TaskException;
import org.clmt.configuration.properties.CLMTProperties;
import org.clmt.configuration.properties.Config;
import org.clmt.metrics.MetricInstantiationException;
import org.clmt.metrics.MetricList;
import org.clmt.metrics.MetricNameCategory;
import org.clmt.metrics.MetricResult;
import org.clmt.metrics.MetricResultList;
import org.clmt.sqooss.AlitheiaFileAdapter;
import org.clmt.sqooss.AlitheiaLoggerAdapter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.metrics.clmt.CLMT;
import eu.sqooss.metrics.clmt.db.CodeConstructType;
import eu.sqooss.metrics.clmt.db.CodeUnitMeasurement;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.fds.FDSService;

public class CLMTImplementation extends AbstractMetric implements CLMT {
  
    private AlitheiaCore core;
    
    public CLMTImplementation(BundleContext bc) {
        super(bc);      
        this.addActivationType(ProjectVersion.class);
        this.addActivationType(ProjectFile.class);        
        
        this.addMetricActivationType("NOCL", ProjectVersion.class);
        this.addMetricActivationType("NOPA", ProjectFile.class);
        this.addMetricActivationType("NOC",  ProjectFile.class);
        this.addMetricActivationType("NPM", ProjectFile.class);
        this.addMetricActivationType("NOPRM",ProjectFile.class);
        this.addMetricActivationType("WMC",  ProjectFile.class);
        
        ServiceReference sr = bc.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) bc.getService(sr);
    }
    
    public boolean install() {
        boolean result = super.install();
        if (result) {
            result &= super.addSupportedMetrics(
                    "Number of Classes",
                    "NOCL",
                    MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Number of Public Attributes",
                    "NOPA",
                    MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Number of Children",
                    "NOC",
                    MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Number of Public Methods",
                    "NPM",
                    MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Number of Protected Methods",
                    "NOPRM",
                    MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Weighted Methods per Class",
                    "WMC",
                    MetricType.Type.SOURCE_CODE);
        }
        return result;
    }
    
    public boolean cleanup(DAObject o) {
        boolean result = true;
        
        if (!(o instanceof StoredProject)) {
            log.warn("We only support cleaning up per stored project for now");
            return false;
        }
        
        Map<String,Object> params = new HashMap<String,Object>();
        List<ProjectVersion> pvs = ((StoredProject)o).getProjectVersions();
        
        for (ProjectVersion pv : pvs) {
            Set<ProjectFile> pfs = pv.getVersionFiles();
            for (ProjectFile pf : pfs) {
                params.put("projectFile", pf);
                List<CodeUnitMeasurement> cms = db.findObjectsByProperties(
                        CodeUnitMeasurement.class, params);
                if (!cms.isEmpty()) {
                    for (CodeUnitMeasurement cm : cms) {
                        result &= db.deleteRecord(cm);
                    }
                }
                params.clear();
            }
        }

        return result;
    }
    
    public void run(ProjectVersion pv) {
        
        FDSService fds = core.getFDSService();
        Pattern p = Pattern.compile(".*java$");
        
        List<ProjectFile> pfs = ProjectFile.getFilesForVersion(pv, p);
        
        if (pfs.isEmpty()) {
            /*No supported files found*/
            return;
        }
        
        FileOps.instance().setProjectFiles(pfs);
        FileOps.instance().setFDS(fds);
        
        /*CMLT Init*/
        CLMTProperties clmtProp = CLMTProperties.getInstance();
        clmtProp.setLogger(new AlitheiaLoggerAdapter(log));
        clmtProp.setFileType(new AlitheiaFileAdapter(""));
        MetricList.getInstance();
        Cache cache = Cache.getInstance();
        cache.setCacheSize(Integer.valueOf(clmtProp.get(Config.CACHE_SIZE)));
        
        /*Construct a calculation task*/
        Task task = new Task("JavaCalcTask");
        Source s = null;
        
        try {
            s = new Source("JavaSource", "Java");
        } catch (TaskException e) {
            log.warn(e.getMessage());
        }
        
        /*Add source files to the calculation task*/
        for (ProjectFile pf : pfs) {
            s.addFile(new Filename(pf.getFileName()));
        }
        
        task.addSources(s);
        
        /*Add metrics to the calculation task*/
        for (Metric m : this.getSupportedMetrics()) {
            task.addCalculation(new Calculation(m.getMnemonic(), "JavaSource"));
        }
        
        /*Parse files and store them to the parsed file cache*/
        if (!task.toIXR()) {
            log.error("Failed to parse source files");
            return;
        }
            
        /*Run metrics against the source files*/
        MetricList mlist = MetricList.getInstance();
        MetricResultList mrlist = new MetricResultList();
        for (Calculation calc : task.getCalculations()) {
            try {
                Source source = task.getSourceById(calc.getID());
                org.clmt.metrics.Metric metric = mlist.getMetric(calc.getName(),source);
                mrlist.merge(metric.calculate());
            } catch (MetricInstantiationException mie) {
                log.warn("Could not load plugin - " + mie.getMessage());
            }
        }
        
        String[] keys = mrlist.getFilenames();
        MetricResult[] lmr = null;
        for(String file: keys) {
            lmr = mrlist.getResultsByFilename(file);
            
            /*Find project file in this version's project files*/
            ProjectFile pf = null;
            for (ProjectFile pf1 : pfs) {
                if (pf1.getFileName().equals(file)) {
                    pf = pf1;
                }
            }
            
            for(MetricResult mr : lmr) {
                Metric m =  Metric.getMetricByMnemonic(mr.getName());

                if (mr.getNameCategory() != MetricNameCategory.PROJECT_WIDE) {

                    //This measurement is not to be stored yet
                    if (m == null) {
                        return;
                    }
                    
                    if (pf == null) {
                        log.warn("Cannot find path: " + file + " for project:" +
                                pv.getProject().getName() +" version:" + 
                                pv.getVersion() + ". Result not stored.");
                        return;
                    }
                    
                    MetricNameCategory mnc = mr.getNameCategory();
                            
                    CodeUnitMeasurement meas = new CodeUnitMeasurement();
                    meas.setMetric(m);
                    meas.setProjectFile(pf);
                    meas.setResult(mr.getValue());
                    meas.setCodeConstructName(mr.getFilename());
                    meas.setCodeConstructType(
                            CodeConstructType.getConstructType(
                                    CodeConstructType.ConstructType.fromString(
                                            mnc.toString())));
                    meas.setWhenRun(new Timestamp(System.currentTimeMillis()));
                    
                    db.addRecord(meas);
                } else {
  
                    //This measurement is not to be stored yet
                    if (m == null) {
                        return;
                    }
                    
                    ProjectVersionMeasurement meas = new ProjectVersionMeasurement();
                    meas.setProjectVersion(pv);
                    meas.setResult(mr.getValue());
                    meas.setWhenRun(new Timestamp(System.currentTimeMillis()));
                    meas.setMetric(m);
                    
                    db.addRecord(meas);
                }
                markEvaluation(m, pv.getProject());
            }
        }
    }
    
    public List<ResultEntry> getResult(ProjectVersion a, Metric m) {
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();
        // Search for a matching project file measurement
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectVersion", a);
        filter.put("metric", m);
        List<ProjectVersionMeasurement> measurements =
            db.findObjectsByProperties(ProjectVersionMeasurement.class, filter);

        if (! measurements.isEmpty()) {
            for (ProjectVersionMeasurement meas : measurements) {
                Integer value = Integer.parseInt(meas.getResult());
                ResultEntry entry = new ResultEntry(value, 
                        ResultEntry.MIME_TYPE_TYPE_INTEGER, 
                        m.getMnemonic());
                results.add(entry);
            }
            return results;
        }
        return null;
    }

    public List<ResultEntry> getResult(ProjectFile a, Metric m) {
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();
        // Search for a matching project file measurement
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectFile", a);
        filter.put("metric", m);
        List<CodeUnitMeasurement> measurements =
            db.findObjectsByProperties(CodeUnitMeasurement.class, filter);

        if (! measurements.isEmpty()) {
            for (CodeUnitMeasurement meas : measurements) {
                Integer value = Integer.parseInt(meas.getResult());
                ResultEntry entry = new ResultEntry(value, 
                        ResultEntry.MIME_TYPE_TYPE_INTEGER, 
                        m.getMnemonic());

                results.add(entry);
            }
            return results;
        }
        return null;
    }
    
    public void run(ProjectFile a) {
        //Nothing to do, the metric is activated by project versions only
        return;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

