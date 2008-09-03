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
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ProjectFileMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.FileTypeMatcher;
import eu.sqooss.service.pa.PluginInfo;
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
        "qsn=QString::null detector=QString *:: *null"
        // One more grepper is inserted programmatically
        // so it doesn't need to be here:
        //   pfn=Profanity detector=<profanity RE>
    } ;
    
    /**
     * This is a (hash)map of names of greps to a pair of
     * (description,pattern) and is extracted from the
     * initializer array, above. 
     */
    private HashMap<String,Pair<String,Pattern>> greps = null;
 
    /**
     * Default profanity level.
     */
    private static final String DEFAULT_PROFANITY = 
            "fuck|shit|donkey rap(e|ing)";
    
    /**
     * Mnemonic for profanity grepper.
     */
    private static final String MNEMONIC_PROFANITY = "pfn";
    
    /**
     * Description of the profanity detector.
     */
    private static final String DESCRIPTION_PROFANITY = "Profanity detector";
    
    /**
     * Convenience and consistency method to map grep mnemonics
     * (e.g. qsn) to the full metric name -- the names need to be
     * namespaced in some way because otherwise we would quickly
     * reach clashes.
     * @param mnemonic metric mnemonic
     * @return Krazy.mnemonic
     */
    private String makeMetricName(String mnemonic) {
        return "Krazy." + mnemonic;
    }
    
    public KrazyImplementation(BundleContext bc) {
        super(bc);        
        super.addActivationType(ProjectFile.class);
        
        // Munge the array into a list of patterns, storing the
        // compiled regexp each time.  We allocate one more than
        // the number of initializers in the string array
        // because we need to add the profanity detector, too.
        greps = new HashMap<String,Pair<String,Pattern>>(grep_initializer.length+1);
        for (String s : grep_initializer) {
            String[] grep = s.split("=", 3);
            if (grep.length!=3) {
                log.warn("Bad Krazy grep initializer <" + s + ">");
            } else {
                Pair<String,Pattern> p = new Pair<String,Pattern>(
                        grep[1],Pattern.compile(grep[2]));
                greps.put(grep[0],p);
                super.addMetricActivationType(makeMetricName(grep[0]), ProjectFile.class);
                log.info("Krazy grepper <" + grep[0] + "> added for <" + grep[1] + ">");
            }
        }
        greps.put(MNEMONIC_PROFANITY, new Pair<String,Pattern>(
                DESCRIPTION_PROFANITY,Pattern.compile(DEFAULT_PROFANITY)));

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
            if(!result) {
                log.warn("Failed to add supported metric <" + makeMetricName(s) + ">");
            }
        }
        addConfigEntry(MNEMONIC_PROFANITY,
                DEFAULT_PROFANITY,
                "Expressions considered profane",
                PluginInfo.ConfigurationType.STRING);

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

    private void updateProfanitySettings() {
        // This is the profanity grepper, call it a reserved name
        Pair<String,Pattern> pfn = greps.get(MNEMONIC_PROFANITY);
        if (null == pfn) {
            // Profanity has been removed from this multi-grep
            return;
        }

        // Profanity is configurable, so we need to get that
        // string from the config manager instead of keeping 
        // around the 
        PluginConfiguration profanityConfig = 
                getConfigurationOption(MNEMONIC_PROFANITY);
        
        if (profanityConfig == null) {
            log.warn("Plug-in configuration option " + 
                    MNEMONIC_PROFANITY + " not found");
            // Leave the setting alone
        } else {
            String profanity = profanityConfig.getValue().trim();
            Pattern p = null;
            if (profanity.length()>0) {
                p = Pattern.compile(profanity);
            }
            greps.put(MNEMONIC_PROFANITY, new Pair<String, Pattern>(
                    pfn.first, p));
        }
        
    }


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

        updateProfanitySettings();

        // Keep a count for each pattern
        HashMap<String,Integer> counts = new HashMap<String,Integer>(greps.size());
        for (String s : greps.keySet()) {
            counts.put(s,new Integer(0));
        }
        String line = null;
        try {
	        while ((line = lnr.readLine()) != null) {
                    for (String s : greps.keySet()) {
                        if (null != greps.get(s).second) {
                            Matcher m = greps.get(s).second.matcher(line);
                            if (m.find()) {
                                counts.put(s, counts.get(s) + 1);
                            }
                        }
                    }
	        }
        } catch (IOException e) {
        	log.warn("Could not run Krazy on <"+pf.getName()+">",e);
        }
        // Store the results
        for (String s : greps.keySet()) {
            Metric metric = Metric.getMetricByMnemonic(makeMetricName(s));
            ProjectFileMeasurement r = new ProjectFileMeasurement();
            r.setMetric(metric);
            r.setProjectFile(pf);
            r.setResult(counts.get(s).toString());
            db.addRecord(r);
            markEvaluation(metric, pf.getProjectVersion().getProject());
        
            log.info("Stored result " + counts.get(s) + " for <" + pf.getName() + ">");
        }
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

