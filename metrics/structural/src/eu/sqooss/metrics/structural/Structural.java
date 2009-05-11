/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2009 - Organization for Free and Open Source Software,  
 * *                Athens, Greece.
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

/*
** That copyright notice makes sense for SQO-OSS paricipants but
** not for everyone. For the Squeleton plug-in only, the Copyright
** notice may be removed and replaced by a statement of your own
** with (compatible) license terms as you see fit; the Squeleton
** plug-in itself is insufficiently a creative work to be protected
** by Copyright.
*/

/* This is the package for this particular plug-in. Third-party
** applications will want a different package name, but it is
** *ESSENTIAL* that the package name contain the string 'metrics'
** because this is hard-coded in parts of the Alitheia core.
*/
package eu.sqooss.metrics.structural;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ProjectFileMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.FileTypeMatcher;
import eu.sqooss.service.util.FileUtils;

/**
 * The Structural complexity metrics suite implement standard complexity
 * metrics such as McCabe's Cyclomatic Complexity 
 * 
 */ 
public class Structural extends AbstractMetric implements ProjectFileMetric {
    protected static String MNEM_CC_T = "MCC_TOTAL";
    protected static String MNEM_CC_AVG = "MCC_AVG";
    protected static String MNEM_CC_MAX = "MCC_MAX";
    
    protected static String MNEM_ECC_T = "EMCC_TOTAL";
    protected static String MNEM_ECC_AVG = "EMCC_AVG";
    protected static String MNEM_ECC_MAX = "EMCC_MAX";
    
    private ProjectFile pf;

    private Pattern methodMatch;
    
    protected Pattern mcBranch = Pattern.compile("if|while|for|catch|finally");
    protected Pattern mcExt = Pattern.compile("&&|\\|\\|");
    protected Pattern mcSwitch = Pattern.compile("case|default");
    protected Pattern mcReturn = Pattern.compile("return");
    
    private static HashMap<String, String> methodDecl = new HashMap<String, String>();
    
    static {        
        methodDecl.put("java", "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+\\w+ *\\([^\\)]*\\)? *(\\{?|[^;])");
        methodDecl.put("c", "(\\w+\\s*\\*?)\\s+(\\w+)\\s*\\(.*\\)\\s*\\{");
        
    }
    
    public Structural(BundleContext bc) {
        super(bc);
        super.addActivationType(ProjectFile.class);

        super.addMetricActivationType(MNEM_CC_AVG, ProjectFile.class);
        super.addMetricActivationType(MNEM_CC_T, ProjectFile.class);
        super.addMetricActivationType(MNEM_CC_MAX, ProjectFile.class);
        super.addMetricActivationType(MNEM_ECC_AVG, ProjectFile.class);
        super.addMetricActivationType(MNEM_ECC_T, ProjectFile.class);
        super.addMetricActivationType(MNEM_ECC_MAX, ProjectFile.class);
    }
    
    public boolean install() {
        boolean result = super.install();
        
        if (result) {
            result &= super.addSupportedMetrics(
                    "Total McCabe Cyclomatic Complexity", 
                    MNEM_CC_T, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Average McCabe Cyclomatic Complexity", 
                    MNEM_CC_AVG, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Max McCabe Cyclomatic Complexity", 
                    MNEM_CC_MAX, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Total Extended McCabe Cyclomatic Complexity", 
                    MNEM_ECC_T, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Average Extended McCabe Cyclomatic Complexity", 
                    MNEM_ECC_AVG, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Max Extended McCabe Cyclomatic Complexity", 
                    MNEM_ECC_MAX, MetricType.Type.SOURCE_CODE);
        }
        return result;
    }

    public List<ResultEntry> getResult(ProjectFile a, Metric m) {
        
        return null;
    }
    
    public void run(ProjectFile pf) {
        if (pf.isDeleted() || pf.getIsDirectory() || 
                !FileTypeMatcher.getInstance().isSourceFile(pf.getName())) {
            return;
        }
        
        this.pf = pf;
        
        
        FDSService fds = AlitheiaCore.getInstance().getFDSService();
        BufferedInputStream in = new BufferedInputStream(fds.getFileContents(pf));
        
        if (in == null) {
            return;
        }

        String pattern = methodDecl.get(FileUtils.extension(pf.getFileName()));
   
        if (pattern == null) {
            log.warn("StructureMetrics:Cannot process source file "
                    + pf.getFileName() + ". No parser defined.");
            return;
        } 
   
        methodMatch = Pattern.compile(pattern, Pattern.MULTILINE);

        byte[] fileNoComments = stripComments(in);
        
        mccabe(fileNoComments);
    }
    
    protected enum State {
        DEFAULT, MAYBECOMMENT, MULTICOMMENT, LINECOMMENT, MAYBECLOSEMULTI;
    }

    protected byte[] stripComments(InputStream in) {

        try {
            byte[] buffer = new byte[in.available()];
            byte b;
            int counter = 0;
            State state = State.DEFAULT;
            while ((b = (byte) in.read()) != -1) {
                switch (b) {
                case '/':
                    if (state == State.MAYBECOMMENT) {
                        state = State.LINECOMMENT;
                    }
                    else if (state == State.MAYBECLOSEMULTI) {
                        state = State.DEFAULT;
                        b = '\n';
                    }
                    else if (state == State.DEFAULT)
                        state = State.MAYBECOMMENT;
                    break;
                case '*':
                    if (state == State.MAYBECOMMENT)
                        state = State.MULTICOMMENT;
                    else if (state == State.MULTICOMMENT)
                        state = State.MAYBECLOSEMULTI;
                    break;
                case '\n':
                    if (state == State.LINECOMMENT) {
                        state = State.DEFAULT;
                        b = '\n';
                    }
                    break;
                default:
                    if (state == State.MAYBECOMMENT)
                        state = State.DEFAULT;
                    else if (state == State.MAYBECLOSEMULTI)
                        state = State.MULTICOMMENT;
                }

                if (state == State.DEFAULT) {
                    buffer[counter] = b;
                    counter++;
                }

            }
            byte[] fileNoComments = new byte[counter];
            System.arraycopy(buffer, 0, fileNoComments, 0, counter);
            return fileNoComments;
        } catch (IOException ioe) {
            log.warn("StructureMetrics: Failed to read file <" + 
                    pf.getFileName() +">", ioe);
            return null;
        }
    }
    
    protected void mccabe(byte[] fileNoComments) {
        Pattern startBlock = Pattern.compile("\\{");
        Pattern endBlock = Pattern.compile("\\}");
        Matcher m = null;
        boolean inFunction = false;
        String line = "";
        int blockDepth = 0;

        HashMap<String, Integer> mcResults = new HashMap<String, Integer>();
        LineNumberReader lnr = new LineNumberReader(new InputStreamReader(
                new ByteArrayInputStream(fileNoComments)));
        List<Integer> mcresult = new ArrayList<Integer>();
        List<Integer> extresult = new ArrayList<Integer>();
        int numMethods = 0;
        
        try {
            while ((line = lnr.readLine()) != null) {
                if (!inFunction) {
                    m = methodMatch.matcher(line);
                    
                    if (m.find()) {
                        inFunction = true;
                        numMethods++;
                    }
                    continue;
                }

                if (inFunction && startBlock.matcher(line).find()) {
                    blockDepth++;
                }

                if (endBlock.matcher(line).find()) {
                    blockDepth--;
                }

                if (blockDepth == 0) {
                    inFunction = false;
                    int result = 1;
                    result += (mcResults.get("branch")==null)?0:mcResults.get("branch");
                    result += (mcResults.get("switch")==null)?0:mcResults.get("switch");
                    mcresult.add(result);
                    result += (mcResults.get("ext")==null)?0:mcResults.get("ext");
                    extresult.add(result);
                    
                }
                
                if (inFunction) {
                    if (mcBranch.matcher(line).find()) {
                        incResult(mcResults, "branch");
                    }
                    
                    if (mcSwitch.matcher(line).find()) {
                        incResult(mcResults, "switch");
                    }
                }
            }
        } catch (IOException ioe) {
            log.warn("StructureMetrics: Failed to process file <" + pf.getFileName() +">", ioe);
        }
        
        int max = 0, avg, total = 0;
        for (Integer i : mcresult) {
            if (i > max) {
                max = i;
            }
            total += i;
        }
        
        if (numMethods == 0) {
            return;
        }
        
        System.out.println(MNEM_CC_AVG + ":" + (int)total/numMethods + " " 
                + MNEM_CC_MAX + ":" + max + " " + MNEM_CC_T + ":" + total);
    }
    
    private void incResult(HashMap<String, Integer> result, String key) {
        if (result.containsKey(key)) {
            int res = result.get(key);
            result.put(key, res);
        } else {
            result.put(key, 1);
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

