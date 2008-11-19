/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,  *                Athens, Greece.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.clmt.configuration.Calculation;
import org.clmt.configuration.Filename;
import org.clmt.configuration.Source;
import org.clmt.configuration.Task;
import org.clmt.configuration.TaskException;
import org.clmt.configuration.properties.CLMTProperties;
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
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.util.Pair;

public class CLMTImplementation extends AbstractMetric implements CLMT {
  
    private AlitheiaCore core;
    private static List<Pair<String, String>> metricsConversionTable;
    private static List<String> clmtPlugins;
    
    static {
        metricsConversionTable = new ArrayList<Pair<String, String>>();
        //NumberOfChildren Module
        metricsConversionTable.add(new Pair<String, String>("NOCH", "NumberOfChildren"));
        //DepthOfInheritanceTree module
        metricsConversionTable.add(new Pair<String, String>("DIT", "DepthOfInheritanceTree"));
        //NativeMethodsMetrics module
        metricsConversionTable.add(new Pair<String, String>("NMPV", "NativeMethodsPerProject"));
        metricsConversionTable.add(new Pair<String, String>("NMPC", "NativeMethodsPerCodeUnit"));
        //Instability Module
        metricsConversionTable.add(new Pair<String, String>("EFC",  "EfferentCouplings"));
        metricsConversionTable.add(new Pair<String, String>("AFC",  "AfferentCouplings"));
        metricsConversionTable.add(new Pair<String, String>("INST", "Instability"));
        //ProjectStatistics Module
        metricsConversionTable.add(new Pair<String, String>("NUMMOD", "ModuleCount"));
        metricsConversionTable.add(new Pair<String, String>("AVGLMOD", "AverageLOCperModule"));
        //ObjectOrientedProjectStatistics Module
        metricsConversionTable.add(new Pair<String, String>("AVGMETCL", "AverageMethodsPerClass"));
        metricsConversionTable.add(new Pair<String, String>("NUMENUM", "NumberOfEnumerations"));
        metricsConversionTable.add(new Pair<String, String>("NUMIFACE", "NumberOfInterfaces"));
        metricsConversionTable.add(new Pair<String, String>("NUMCL", "NumberOfClasses"));
        //WeigthedMethodsPerClass Module
        metricsConversionTable.add(new Pair<String, String>("WMC", "WeigthedMethodsPerClass"));
        
        clmtPlugins = new ArrayList<String>();
        
        clmtPlugins.add("NumberOfChildren");
        clmtPlugins.add("DepthOfInheritanceTree");
        clmtPlugins.add("NativeMethodsMetrics");
        clmtPlugins.add("Instability");
        clmtPlugins.add("ProjectStatistics");
        clmtPlugins.add("ObjectOrientedProjectStatistics");
        clmtPlugins.add("WeigthedMethodsPerClass");
    }
    
    public CLMTImplementation(BundleContext bc) {
        super(bc);      
        this.addActivationType(ProjectVersion.class);
        
        this.addMetricActivationType("NOCH", ProjectVersion.class);
        this.addMetricActivationType("DIT",  ProjectFile.class);
        this.addMetricActivationType("NMPV", ProjectVersion.class);
        this.addMetricActivationType("NMPC", ProjectFile.class);
        
        this.addMetricActivationType("EFC", ProjectFile.class);
        this.addMetricActivationType("AFC",ProjectFile.class);
        this.addMetricActivationType("INST",  ProjectFile.class);
        
        this.addMetricActivationType("NUMMOD",ProjectVersion.class);
        this.addMetricActivationType("AVGLMOD",  ProjectVersion.class);
        
        this.addMetricActivationType("AVGMETCL",ProjectVersion.class);
        this.addMetricActivationType("NUMENUM",  ProjectVersion.class);
        this.addMetricActivationType("NUMIFACE",ProjectVersion.class);
        this.addMetricActivationType("NUMCL",  ProjectVersion.class);
        
        this.addMetricActivationType("WMC",  ProjectFile.class);
        
        ServiceReference sr = bc.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) bc.getService(sr);
    }
    
    public boolean install() {
        boolean result = super.install();
        if (result) {
            result &= super.addSupportedMetrics("Number of Children",
                    "NOCH", MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics("Depth Of Inheritance Tree",
                    "DIT",MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics("Number of Native Methods",
                    "NMPV",MetricType.Type.PROJECT_WIDE);
            result &= super.addSupportedMetrics("Number of Native Methods Per Class",
                    "NMPC",MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics("Efferent Couplings",
                    "EFC", MetricType.Type.SOURCE_FOLDER);
            result &= super.addSupportedMetrics("Afferent Couplings",
                    "AFC", MetricType.Type.SOURCE_FOLDER);
            result &= super.addSupportedMetrics("Instability Metric",
                    "INST", MetricType.Type.SOURCE_FOLDER);
            result &= super.addSupportedMetrics("Number of Modules/Namespaces",
                    "NUMMOD",MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics("Average Lines of Code per Module",
                    "AVGLMOD",MetricType.Type.PROJECT_WIDE);
            result &= super.addSupportedMetrics("Average Methods per Class",
                    "AVGMETCL",MetricType.Type.PROJECT_WIDE);
            result &= super.addSupportedMetrics("Number of Enumerations",
                    "NUMENUM",MetricType.Type.PROJECT_WIDE);
            result &= super.addSupportedMetrics("Number of Interfaces",
                    "NUMIFACE",MetricType.Type.PROJECT_WIDE);
            result &= super.addSupportedMetrics("Number of Classes",
                    "NUMCL",MetricType.Type.PROJECT_WIDE);
            result &= super.addSupportedMetrics("Weighted Methods per Class",
                    "WMC", MetricType.Type.SOURCE_CODE);
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
        clmtProp.setLogger(new AlitheiaLoggerAdapter(this, pv));
        clmtProp.setFileType(new AlitheiaFileAdapter(""));
        MetricList.getInstance();
        
        /*Construct a calculation task*/
        Task task = new Task("JavaCalcTask");
        Source s = null;
        
        try {
            s = new Source("JavaSource", "Java");
        } catch (TaskException e) {
            warn(pv, "Error constructing Java calculation task:" 
                    + e.getMessage());
        }
        
        /*Add source files to the calculation task*/
        for (ProjectFile pf : pfs) {
            s.addFile(new Filename(pf.getFileName()));
        }
        
        task.addSources(s);
        
        /*Add metrics to the calculation task*/        
        for (String plugin : clmtPlugins) {
            task.addCalculation(new Calculation(plugin, "JavaSource"));
        }
        
        /*Parse files and store them to the parsed file cache*/
        task.toIXR();
            
        /*Run metrics against the source files*/
        MetricList mlist = MetricList.getInstance();
        MetricResultList mrlist = new MetricResultList();
        for (Calculation calc : task.getCalculations()) {
            try {
                Source source = task.getSourceById(calc.getID());
                org.clmt.metrics.Metric metric = mlist.getMetric(calc.getName(),source);
                mrlist.merge(metric.calculate());
            } catch (MetricInstantiationException mie) {
                warn(pv, "Could not load plugin :" + mie.getMessage());
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
                Metric m =  Metric.getMetricByMnemonic(getAlitheiaMetricName(mr.getName()));

                if (mr.getNameCategory() != MetricNameCategory.PROJECT_WIDE) {

                    //This measurement is not to be stored yet
                    if (m == null) {
                        return;
                    }
                    
                    if (pf == null) {
                        warn(pv, "Cannot find file:" + file + 
                                " Result not stored.");
                        continue;
                    }
                    
                    ProjectFileMeasurement pfm = new ProjectFileMeasurement();
                    pfm.setMetric(m);
                    pfm.setProjectFile(pf);
                    pfm.setResult(mr.getValue());
                    
                    db.addRecord(pfm);
                } else {
  
                    //This measurement is not to be stored yet
                    if (m == null) {
                        return;
                    }
                    
                    ProjectVersionMeasurement meas = new ProjectVersionMeasurement();
                    meas.setProjectVersion(pv);
                    meas.setResult(mr.getValue());
                    meas.setMetric(m);
                    
                    db.addRecord(meas);
                }
                markEvaluation(m, pv);
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
        List<ProjectFileMeasurement> measurements =
            db.findObjectsByProperties(ProjectFileMeasurement.class, filter);

        if (! measurements.isEmpty()) {
            for (ProjectFileMeasurement pfm : measurements) {
                Integer value = Integer.parseInt(pfm.getResult());
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
    
    private static String getAlitheiaMetricName(String clmtmetric) {
        for (Pair<String, String> p : metricsConversionTable) {
            if (p.second.equals(clmtmetric))
                return p.first;
        }
        return null;
    }
    
    public void warn(ProjectVersion pv, String msg) {
        log.warn("CLMT (" + pv.getProject().getName() + " - " 
                + pv.getRevisionId() + "):" + msg);
    }
    
    public void error(ProjectVersion pv, String msg) {
        log.error("CLMT (" + pv .getProject().getName() + " - " 
                + pv.getRevisionId() + "):" + msg);
    }
    
    public Logger getLogger() {
        return log;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

