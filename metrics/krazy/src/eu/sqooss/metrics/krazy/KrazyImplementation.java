/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Adriaan de Groot <groot@kde.org>
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

package eu.sqooss.metrics.krazy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.ProjectFileMetric;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.FileTypeMatcher;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.util.Pair;


public class KrazyImplementation extends AbstractMetric implements ProjectFileMetric  {
    private FDSService fds;

    /**
     * This is an array of things to grep for; each one creates a 
     * separate metric in this plug-in. The structure of each 
     * string is a (short) mnemonic, an = sign, then a metric description,
     * then a regexp.
     */
    private static final String[] grep_initializer = {
        "qsn=QString::null detector=QString *:: *null",
        "pfn=Profanity detector=fuck|shit|donkey rap(ing|e)"
    } ;
    
    /**
     * This is a (hash)map of names of greps to a pair of
     * (description,pattern) and is extracted from the
     * initializer array, above. 
     */
    private HashMap<String,Pair<String,Pattern>> greps = null;
 
    private String makeMetricName(String mnemonic) {
        return "Krazy." + mnemonic;
    }
    
    public KrazyImplementation(BundleContext bc) {
        super(bc);        
        super.addActivationType(ProjectFile.class);
        
        // Munge the array into a list of patterns, storing the
        // compiled regexp each time. We ignore the description
        // field here, because that is only needed in the 
        greps = new HashMap<String,Pair<String,Pattern>>(grep_initializer.length);
        for (String s : grep_initializer) {
            String[] grep = s.split("=", 3);
            if (grep.length!=3) {
                log.warn("Bad Krazy grep initializer <" + s + ">");
            } else {
                Pair<String,Pattern> p = new Pair<String,Pattern>(
                        grep[1],Pattern.compile(grep[2]));
                greps.put(grep[0],p);
                super.addMetricActivationType(makeMetricName(grep[0]), ProjectFile.class);
            }
        }

        ServiceReference serviceRef = null;
        serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        fds = ((AlitheiaCore)bc.getService(serviceRef)).getFDSService();    
    }
    
    public boolean install() {
        boolean result = super.install();
        for (String s : greps.keySet()) {
            // Bail out if adding one fails
            if (!result) {
                break;
            }
            result &= super.addSupportedMetrics(
                greps.get(s).first,
                makeMetricName(s),
                MetricType.Type.SOURCE_CODE);
        }
        return result;
    }

    public boolean remove() {
        return super.remove();
    }

    public boolean update() {
        return remove() && install(); 
    }

    private static String CPPExtensions[] = {
        ".h",".cc",".cpp",".C"
    } ;
    
    public void run(ProjectFile pf) {
        // Don't support directories
        if (pf.getIsDirectory()) {
            return;
        }

        // Check for a usable filetype
        boolean found = false;
        if (FileTypeMatcher.getFileType(pf.getName())
                .equals(FileTypeMatcher.FileType.SRC)) {
            String extension = FileTypeMatcher.getFileExtension(pf.getFileName());
            for(String s : CPPExtensions) {
                if (s.equals(extension)) {
                    found=true;
                    break;
                }
            }
        }
        if (!found) {
            return;
        }
        // So here we know we are dealing with a C++ source file
        // (for limited values of "know", and .h files may still be
        // C files in reality).
        log.info("Reading file <" + pf.getName() + ">");
        
        InputStream in = fds.getFileContents(pf);
        if (in == null) {
            return;
        }
        // Measure the number of lines in the project file
        LineNumberReader lnr = 
            new LineNumberReader(new InputStreamReader(in));
        int CountQString_null = 0;
        Pattern MatchQString_null = Pattern.compile("QString *:: *null"); 
        String line = null;
        try {
	        while ((line = lnr.readLine()) != null) {
	        	Matcher m = MatchQString_null.matcher(line);
	        	if (m.find()) {
	        		CountQString_null++;
	        	}
	        }
        } catch (IOException e) {
        	log.warn("Could not run Krazy on <"+pf.getName()+">",e);
        }
        // Store the results
        Metric metric = Metric.getMetricByMnemonic(KrazyQString_null);
        ProjectFileMeasurement r = new ProjectFileMeasurement();
        r.setMetric(metric);
        r.setProjectFile(pf);
        r.setResult(String.valueOf(CountQString_null));
        db.addRecord(r);
        markEvaluation(metric, pf.getProjectVersion().getProject());
        
        log.info("Stored result " + CountQString_null + " for <" + pf.getName() + ">");
    }

    public List<ResultEntry> getResult(ProjectFile a, Metric m) {
    	HashMap<String,Object> properties = new HashMap<String,Object>();
    	properties.put("metric",m);
    	properties.put("projectFile",a);
    	List<ProjectFileMeasurement> l = 
    		db.findObjectsByProperties(ProjectFileMeasurement.class,properties);
    	return convertMeasurements(l,m.getMnemonic());
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

