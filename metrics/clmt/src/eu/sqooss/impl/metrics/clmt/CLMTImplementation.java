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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.clmt.cache.CacheMap;

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
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;

import eu.sqooss.service.fds.FDSService;

import eu.sqooss.service.logging.Logger;

/**
 * The main implementation of CLMT Plug-in
 * 
 * @author Georgios Gousios (gousiosg@aueb.gr)
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 */
public class CLMTImplementation extends AbstractMetric implements CLMT {
    private AlitheiaCore core;
    private final static Map<String, String> metricsConversionTable;
    private final static String[] clmtPlugins;

    static {
        metricsConversionTable = new Hashtable<String, String>();
        
        for ( CLMTMeasurement cm : CLMTMeasurement.values() ) {
            metricsConversionTable.put(cm.getClmtMeasurementName(), cm.getMnemonic());
        }

        clmtPlugins = new String[] { "NumberOfChildren",
                                     "DepthOfInheritanceTree", 
                                     "JavaMetrics",
                                     "Instability",
                                     "ProjectStatistics",
                                     "ObjectOrientedProjectStatistics", 
                                     "WeigthedMethodsPerClass" };
    }

    public CLMTImplementation(BundleContext bc) {
        super(bc);
        this.addActivationType(ProjectVersion.class);
        this.addActivationType(ProjectFile.class);
        for ( CLMTMeasurement cm : CLMTMeasurement.values() ) {
            this.addMetricActivationType(cm.getMnemonic(), cm.getClazz());
        }

        ServiceReference sr = bc.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) bc.getService(sr);
    }

    public boolean install() {
        boolean result = super.install();
        if (result) {
            for ( CLMTMeasurement cm : CLMTMeasurement.values() ) {
                result &= super.addSupportedMetrics(cm.getDescription(), 
                                                    cm.getMnemonic(), 
                                                    cm.getType());
            }
        }

        return result;
    }

    public void run(ProjectVersion pv) {
        FDSService fds = core.getFDSService();

        List<ProjectFile> pfs = pv.allFiles();
        Map<String, ProjectFile> pfmap = new HashMap<String, ProjectFile>();
        
        for ( ProjectFile pfile : pfs ) {
            pfmap.put(pfile.getFileName(), pfile);
        }

        if (pfs.isEmpty()) {
            warn(pv, "No Supported files found (.*java$)");
            return;
        }

        FileOps fops = new FileOps();
        
        fops.setProjectFiles(pfs);
        fops.setFDS(fds);

        /* CLMT Init */
        CLMTProperties clmtProp = CLMTProperties.getInstance();
        // TODO: for the time begin, you are going to flush 
        // caches by hand
        CacheMap cacheMap = CacheMap.getInstance(); 
        clmtProp.setLogger(new AlitheiaLoggerAdapter(this, pv));
        clmtProp.setFileType(new AlitheiaFileAdapter("", fops));

        /* Construct a calculation task */
        Task task = new Task("JavaCalcTask");
        Source s = null;

        try {
            s = new Source("JavaSource", "Java");
        } catch (TaskException e) {
            warn(pv, "Error constructing Java calculation task:" + e.getMessage());
        }

        /* Add source files to the calculation task */
        Pattern p = Pattern.compile(".*java$");
        
        for (ProjectFile pf : pfs) {
            String f = pf.getFileName();
            Matcher m = p.matcher(f);
            if(m.matches()) {
                s.addFile(new Filename(f));
            }
        }
        
        if (s.getFileCount() <= 0) {
            return;
        }
        
        info(pv, "Found " + s.getFileCount() + " files");

        task.addSources(s);

        /* Parse files and store them to the parsed file cache */
        info(pv, "Starting conversion to IXR");
        
        long start = System.currentTimeMillis();
        task.toIXR();
        long end = System.currentTimeMillis();
        
        info(pv, "Sources converted to IXR (Time elapsed: " + (end - start) + " msecs)");

        /* Run metrics against the source files */
        MetricList mlist =  MetricList.getInstance();
        MetricResultList mrlist = new MetricResultList();

        for ( String cp : clmtPlugins ) {
            try {
                start = System.currentTimeMillis();
                org.clmt.metrics.Metric metric = mlist.getMetric(cp, s);
                mrlist.merge(metric.calculate());
                end = System.currentTimeMillis();
                info(pv, "Calculation of " + cp + " completed (Time elapsed: " + (end - start) + " msecs)");
            } catch (MetricInstantiationException mie) {
                warn(pv, "Could not load plugin : " + mie.getMessage());
            }
        }
        
        cacheMap.clear();
        
        String[] keys = mrlist.getFilenames();
        MetricResult[] lmr = null;
        
        List<ProjectFile> directories = pv.allDirs();
        Map<String, Metric> metricMap = new HashMap<String, Metric>();
        
        for (String file : keys) {
            lmr = mrlist.getResultsByFilename(file);

            /* Find project file in this version's project files */
            ProjectFile pf = pfmap.get(file);
            
            // TODO: quick fix, will optimize this later 
            if(pf == null) {
                for ( ProjectFile pfile : directories ) {
                    if(pfile.getFileName().endsWith(file)) {
                        pf = pfile;
                        break;
                    }
                }
           }

           for (MetricResult mr : lmr) {
               String mName = mr.getMeasurementName();
               if(!metricsConversionTable.containsKey(mName)) {
                   continue;
               }
               
               Metric m = metricMap.get(mName);

               if (m == null) {
                   m = Metric.getMetricByMnemonic(getAlitheiaMetricName(mName));
                   if (m != null) {
                       metricMap.put(mName, m);
                   } else {
                       warn(pv, "Metric " + mName + " not found. Skipping.");
                       continue;
                   }
               }

               if (mr.getMetricNameCategory() != MetricNameCategory.PROJECT_WIDE) {
                   if (pf == null) {
                       warn(pv, "Cannot find file : " + file + " - Result not stored.");
                       continue;
                   }

                   ProjectFileMeasurement pfm = new ProjectFileMeasurement();
                   pfm.setMetric(m);
                   pfm.setProjectFile(pf);
                   pfm.setResult(mr.getValue());

                   db.addRecord(pfm);
               } else {
                   ProjectVersionMeasurement meas = new ProjectVersionMeasurement();
                   meas.setProjectVersion(pv);
                   meas.setResult(mr.getValue());
                   meas.setMetric(m);

                   db.addRecord(meas);
               }
           }
        }
        
        for (CLMTMeasurement cm : CLMTMeasurement.values() ) {
            Metric m = Metric.getMetricByMnemonic(cm.getMnemonic());
            markEvaluation(m, pv);
        }
    }

    public List<ResultEntry> getResult(ProjectVersion a, Metric m) {
        ResultResolver rr = new ResultResolver();
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();

        // Search for a matching project file measurement
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectVersion", a);
        filter.put("metric", m);
        
        List<ProjectVersionMeasurement> measurements = 
            db.findObjectsByProperties(ProjectVersionMeasurement.class, filter);
        
        if (measurements == null) {
            return null;
        }

        if (!measurements.isEmpty()) {
            for (ProjectVersionMeasurement meas : measurements) {
                ResultEntry entry = null;
                String type = rr.identify(meas.getResult());
                
                if(type.compareTo(ResultEntry.MIME_TYPE_TYPE_INTEGER) == 0) {                
                    Integer value = Integer.parseInt(meas.getResult());

                    entry = new ResultEntry(value,
                                            ResultEntry.MIME_TYPE_TYPE_INTEGER, 
                                            m.getMnemonic());
                } else if (type.compareTo(ResultEntry.MIME_TYPE_TYPE_DOUBLE) == 0) {
                   Double value = Double.parseDouble(meas.getResult());

                   entry = new ResultEntry(value,
                                           ResultEntry.MIME_TYPE_TYPE_DOUBLE, 
                                           m.getMnemonic());
                } else {
                    entry = new ResultEntry(meas.getResult(),
                                            ResultEntry.MIME_TYPE_TEXT_PLAIN,
                                            m.getMnemonic());
                }                
                results.add(entry);
            }
            
            return results;
        }
        
        return null;
    }

    public List<ResultEntry> getResult(ProjectFile a, Metric m) {
        ResultResolver rr = new ResultResolver();
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();

        // Search for a matching project file measurement
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectFile", a);
        filter.put("metric", m);
        
        List<ProjectFileMeasurement> measurements = 
            db.findObjectsByProperties(ProjectFileMeasurement.class, filter);
        
        if(measurements == null) { 
            return null; 
        }

        if (!measurements.isEmpty()) {
            for (ProjectFileMeasurement pfm : measurements) {
                ResultEntry entry = null;                
                String type = rr.identify(pfm.getResult());
                
                if(type.compareTo(ResultEntry.MIME_TYPE_TYPE_INTEGER) == 0) {                
                    Integer value = Integer.parseInt(pfm.getResult());

                    entry = new ResultEntry(value,
                                            ResultEntry.MIME_TYPE_TYPE_INTEGER, 
                                            m.getMnemonic());
                } else if (type.compareTo(ResultEntry.MIME_TYPE_TYPE_DOUBLE) == 0) {
                   Double value = Double.parseDouble(pfm.getResult());

                   entry = new ResultEntry(value,
                                           ResultEntry.MIME_TYPE_TYPE_DOUBLE, 
                                           m.getMnemonic());
                } else {
                    entry = new ResultEntry(pfm.getResult(),
                                            ResultEntry.MIME_TYPE_TEXT_PLAIN,
                                            m.getMnemonic());
                }

                results.add(entry);
            }
            
            return results;
        }
        
        return null;
    }

    public void run(ProjectFile a) {
        // Nothing to do, the metric is activated by project versions only
        return;
    }

    private static String getAlitheiaMetricName(String clmtmetric) {
        return CLMTImplementation.metricsConversionTable.get(clmtmetric);
    }
    
    private String message(ProjectVersion pv, String msg) {
        String s = "CLMT (" + pv.getProject().getName() + " - " 
                            + pv.getRevisionId() + " - " 
                            + Thread.currentThread().getId() + "):" + msg;
        
        return s;
    }
    
    public void info(ProjectVersion pv, String msg) {
        log.info(message(pv, msg));        
    }

    public void warn(ProjectVersion pv, String msg) {
        log.warn(message(pv, msg));
    }

    public void error(ProjectVersion pv, String msg) {
        log.error(message(pv, msg));
    }

    public Logger getLogger() {
        return log;
    }
}

class ResultResolver {
    private Pattern intPattern;
    private Pattern decPattern;
    
    public ResultResolver() { 
        intPattern = Pattern.compile("[0-9]+");
        decPattern = Pattern.compile("[0-9]+\\.[0-9]+");
    }
    
    public String identify(String s) {
        Matcher m = intPattern.matcher(s);
        if(m.matches()) { 
            return ResultEntry.MIME_TYPE_TYPE_INTEGER;
        }
        //
        m = decPattern.matcher(s);
        if(m.matches()) {
            return ResultEntry.MIME_TYPE_TYPE_DOUBLE;
        }
        
        // else ... unknown input, treat it as a string
        
        return ResultEntry.MIME_TYPE_TEXT_PLAIN;
    }
}
