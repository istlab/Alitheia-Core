/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Georgios Gousios <gousiosg@gmail.org>
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
import java.util.List;

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
import org.clmt.metrics.MetricResult;
import org.clmt.metrics.MetricResultList;
import org.clmt.sqooss.AlitheiaFileAdapter;
import org.clmt.sqooss.AlitheiaLoggerAdapter;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AbstractMetricJob;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.fds.InMemoryCheckout;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.ProjectRevision;

public class CLTMJob extends AbstractMetricJob {

    // Reference to the plugin that created this job
    private AbstractMetric parent = null;
    
    private ProjectVersion pv;
    
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

    public CLTMJob(AbstractMetric owner, ProjectVersion p) {
        super(owner);
        parent = owner;
        this.pv = p;
    }

    public int priority() {
        return 0xbeef;
    }

    public void run() {
        if(!db.startDBSession()) {
            log.error("No DBSession could be opened!");
            return;
        }
        List<Metric> lm = parent.getSupportedMetrics();
        StringBuilder metricCalc = new StringBuilder();
        InMemoryCheckout imc = null;
        
        /*Get a checkout for this revision*/
        try {
            imc = fds.getInMemoryCheckout(pv.getProject().getId(), 
                    new ProjectRevision(pv.getVersion()));
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
                "/",
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
        
        String[] keys = mrlist.getNames();
        MetricResult[] lmr = null;
        for(String key: keys) {
            lmr = mrlist.getResult(key);
            
        }
        
        db.commitDBSession();
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
