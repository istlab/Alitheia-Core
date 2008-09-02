/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.impl.metrics.wc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.metrics.wc.Wc;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.FileTypeMatcher;

public class WcImplementation extends AbstractMetric implements Wc {
    
    private FDSService fds;
	
    public WcImplementation(BundleContext bc) {
        super(bc);
        super.addActivationType(ProjectFile.class);
        
        super.addMetricActivationType("LOC", ProjectFile.class);
        super.addMetricActivationType("LOCOM", ProjectFile.class);
        
        ServiceReference serviceRef = null;
        serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
       
        
        fds = ((AlitheiaCore)bc.getService(serviceRef)).getFDSService();    
    }

    public boolean install() {
        boolean result = super.install();
        if (result) {
            result &= super.addSupportedMetrics(
                    "Lines of Code",
                    "LOC",
                    MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Lines of Comments", 
                    "LOCOM",
                    MetricType.Type.SOURCE_CODE);
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
    	return convertMeasurements(measurement,m.getMnemonic());
    }

    public void run(ProjectFile pf) {
        // We do not support directories
        if (pf.getIsDirectory()) {
            return;
        }
        
        //We don't support binary files either
        if (FileTypeMatcher.getFileType(pf.getName()).equals(
                FileTypeMatcher.FileType.BIN)) {
            return;
        }
        
        InputStream in = fds.getFileContents(pf);
        if (in == null) {
            return;
        }
        // Create an input stream from the project file's content
        try {
            log.info(this.getClass().getName() + " Measuring: "
                    + pf.getFileName());
            
            /* Match start of multiline comment */
            String startMultiLine = "/\\*+|<!--";
           
            /* End multiline comment */
            String endMultiline = ".*\\*/|-->";
            
            /* Match single line comments, C/Java/C++ style*/
            String singleLine = "//.*$|#.*$|/\\*.*\\*/";
            
            Pattern startMultiLinePattern = Pattern.compile(startMultiLine);
            Pattern endMultiLinePattern = Pattern.compile(endMultiline);
            Pattern singleLinePattern = Pattern.compile(singleLine);
            
            // Measure the number of lines in the project file
            LineNumberReader lnr = 
                new LineNumberReader(new InputStreamReader(in));
            int comments = 0;
            
            String line = null;
            while ((line = lnr.readLine()) != null) {
                
                Matcher m = singleLinePattern.matcher(line);
                /* Single line comments */
                if (m.find()) {
                    comments++;
                    continue;
                }
                
                m = startMultiLinePattern.matcher(line);
                Matcher m2 = endMultiLinePattern.matcher(line);
                
                /* Multiline comments */
                if (m.find()) {
                    comments++;

                    while((line = lnr.readLine()) != null && !m2.find()) {
                            m2 = endMultiLinePattern.matcher(line);
                            comments++;
                    }
                }
            }
            
            lnr.close();

            // Store the results
            Metric metric = Metric.getMetricByMnemonic("LOC");
            ProjectFileMeasurement locm = new ProjectFileMeasurement();
            locm.setMetric(metric);
            locm.setProjectFile(pf);
            locm.setWhenRun(new Timestamp(System.currentTimeMillis()));
            locm.setResult(String.valueOf(lnr.getLineNumber()));

            db.addRecord(locm);
            markEvaluation(metric, pf.getProjectVersion().getProject());
            
            metric = Metric.getMetricByMnemonic("LOCOM");
            ProjectFileMeasurement locc = new ProjectFileMeasurement();
            locc.setMetric(metric);
            locc.setProjectFile(pf);
            locc.setWhenRun(new Timestamp(System.currentTimeMillis()));
            locc.setResult(String.valueOf(comments));
            
            db.addRecord(locc);
            markEvaluation(metric, pf.getProjectVersion().getProject());

        } catch (IOException e) {
            log.error(this.getClass().getName() + " IO Error <" + e
                    + "> while measuring: " + pf.getFileName());
        
        }
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
