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

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.List;
import java.util.regex.Pattern;

import org.clmt.cache.Cache;
import org.clmt.cache.CacheException;
import org.clmt.configuration.Calculation;
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
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.InMemoryCheckout;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.ProjectRevision;

public class CLTMImplementation extends AbstractMetric implements CLMT {
    
    private final String XMLTaskProto = "<task>\n"     +
    "  <description>%s </description>\n"               +
    "   <source id=\"%s\" language=\"%s\">\n"          +
    "    <directory path=\"%s\" recursive=\"true\">\n" +
    "      <include mask=\"%s\" />\n"                  +
    "    </directory>\n"                               +
    "   </source>\n"                                   + 
    "%s\n" +
    "</task>";
    
    private final String XMLCalcProto = 
        "<calculation name=\"%s\" ids=\"%s\" />"; 
    
    private AlitheiaCore core;
    
    public CLTMImplementation(BundleContext bc) {
        super(bc);      
        this.addActivationType(ProjectVersion.class);
        this.addActivationType(ProjectFile.class);
        
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
                    "Number of Projected Methods",
                    "NOPRM",
                    MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Weighted Methods per Class",
                    "WMC",
                    MetricType.Type.SOURCE_CODE);
        }
        return result;
    }
    
    public void run(ProjectVersion pv) {
        FDSService fds = core.getFDSService();
        List<Metric> lm = getSupportedMetrics();
        StringBuilder metricCalc = new StringBuilder();
        InMemoryCheckout imc = null;
        
        /*Get a checkout for this revision*/
        try {
            Pattern p = Pattern.compile(".*java$");
            imc = fds.getInMemoryCheckout(pv.getProject().getId(), 
                    new ProjectRevision(pv.getVersion()), p);
        } catch (InvalidRepositoryException e) {
            log.error("Cannot get in memory checkout for project " + 
                    pv.getProject().getName() + " revision " + pv.getVersion() 
                    + ":" + e.getMessage());
        } catch (InvalidProjectRevisionException e) {
            log.error("Cannot get in memory checkout for project " + 
                    pv.getProject().getName() + " revision " + pv.getVersion() 
                    + ":" + e.getMessage());   
        }
       
        FileOps.instance().setInMemoryCheckout(imc);
        FileOps.instance().setFDS(fds);
        
        /*CMLT Init*/
        CLMTProperties clmtProp = CLMTProperties.getInstance();
        clmtProp.setLogger(new AlitheiaLoggerAdapter(log));
        clmtProp.setFileType(new AlitheiaFileAdapter(""));
        MetricList.getInstance();
        Cache cache = Cache.getInstance();
        cache.setCacheSize(Integer.valueOf(clmtProp.get(Config.CACHE_SIZE)));
        
        /*Construct task for parsing Java files*/
        for(Metric m : lm) {
            metricCalc.append(String.format(XMLCalcProto, 
                    m.getMnemonic(),
                    pv.getProject().getName()+"-Java"));
            metricCalc.append("\n");
        }
        
        /*Yes, string based XML construction and stuff*/
        String javaTask = String.format(XMLTaskProto, 
                pv.getProject().getName(), 
                pv.getProject().getName()+"-Java", 
                "Java",
                "",
                ".*java",
                metricCalc);
        Task t = null;
        try {
            t = new Task(pv.getProject().getName(), 
                    new ByteArrayInputStream(javaTask.getBytes()));
        } catch (TaskException e) {
            log.error(this.getClass().getName() + ": Invalid task file:" 
                    + e.getMessage());
            return;
        }
        
        for (Source s : t.getSource()) {
            try {
                cache.add(s);
            } catch (CacheException ce) {
                log.warn(ce.getMessage());
            }
        }
        
        MetricList mlist = MetricList.getInstance();
        MetricResultList mrlist = new MetricResultList();
        for (Calculation calc : t.getCalculations()) {
            try {
                Source[] sources = t.getSourceByIds(calc.getIDs());
                org.clmt.metrics.Metric metric = mlist.getMetric(calc.getName(),sources);
                mrlist.merge(metric.calculate());
            } catch (MetricInstantiationException mie) {
                log.warn("Could not load plugin - " + mie.getMessage());
            }
        }
        
        System.out.println(mrlist.toString());
        
        String[] keys = mrlist.getFilenames();
        MetricResult[] lmr = null;
        for(String file: keys) {
            lmr = mrlist.getResultsByFilename(file);
            
            for(MetricResult mr : lmr) {
                Metric m =  Metric.getMetricByMnemonic(mr.getName());

                if (mr.getNameCategory() != MetricNameCategory.PROJECT_WIDE) {

                    //This measurement is not to be stored yet
                    if (m == null) {
                        return;
                    }
                    
                    ProjectFile pf = ProjectFile.getLatestVersion(pv, file);
                    
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
                    meas.setCodeConstructType(CodeConstructType.getConstructType(CodeConstructType.ConstructType.fromString(mnc.toString())));
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
        
        return null;
    }

    public void run(ProjectFile a) {
        //Nothing to do, the metric is activated by project versions only
    }

    public List<ResultEntry> getResult(ProjectFile a, Metric m) {
        return null;
    }    
}

// vi: ai nosi sw=4 ts=4 expandtab

