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
import java.util.Iterator;
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
    
    /* Contains a regular expression that can detect with reasonable accuracy 
     * a method declaration for the specified extention. The regular expression
     * is expected to work in Pattern.MULTI_LINE mode.  
     */
    private static HashMap<String, String> methodDecl = new HashMap<String, String>();
    
    static {  
        methodDecl.put("java", 
                "(public|protected|private|static|\\s) +" +
                "[\\w\\<\\>\\[\\]]+\\s+\\w+ *\\([^\\)]*\\)? *(\\{?|[^;])");
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
        
        pf = db.attachObjectToDBSession(pf);
        this.pf = pf;
        
        FDSService fds = AlitheiaCore.getInstance().getFDSService();
        BufferedInputStream in = new BufferedInputStream(fds.getFileContents(pf));
        
        if (in == null) {
            return;
        }
        
        /* Get the pattern for the file type. */
        String pattern = methodDecl.get(FileUtils.extension(pf.getFileName()));
   
        if (pattern == null) {
            return;
        } 

        methodMatch = Pattern.compile(pattern, Pattern.MULTILINE);

        /* Read the input file and remove all comments */
        byte[] fileNoComments = stripComments(in);

        /* Try to detect method/function declaration lines*/
        String contents = new String(fileNoComments);
        Matcher m = methodMatch.matcher(contents);
        
        List<Integer> methodStart = new ArrayList<Integer>();
        
        /*
         * Since we are working on a byte array version of the file, we need to
         * take care of CR/LF/CR+LF line endings when counting lines.
         */
        while (m.find()) {
            int numLines = 1;
            byte prev = 0;
            for (int i = 0; i < m.end(); i++) {
                if (fileNoComments[i] == '\r')
                        numLines++;

                if (fileNoComments[i] == '\n') {
                    if (prev != '\r')
                        numLines++;
                }
                
                prev = fileNoComments[i];
            }
            
            methodStart.add(numLines);
        }

        if (methodStart.isEmpty()) {
            log.warn("Structural: " + pf + ". No methods identified.");
            return;
        }
        
        /* Call the metric calculation methods*/
        mccabe(fileNoComments, methodStart);
        halstead(fileNoComments, methodStart);
    }
    

    protected enum State {
        DEFAULT, MAYBECOMMENT, MULTICOMMENT, LINECOMMENT, MAYBECLOSEMULTI;
    }

    /**
     * A method that strips C/C++/Java comments from the input stream
     * and returns the input as an array of bytes. Works as a five
     * state state machine with predefined transitions.  
     */
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
    
    
    /**
     * Calculate the McCabe complexity and McCabe extended complexity metrics.
     * @param fileNoComments The file to run the metrics on, stripped of comments
     * @param methodStart A list of start lines for all identified 
     * functions/methods, ordered by line number.
     */
    protected void mccabe(byte[] fileNoComments, List<Integer> methodStart) {
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
        int numMethods = 0, linesRead = 0;
        Iterator<Integer> nextMethod = methodStart.iterator();
        int nextMethodLine = nextMethod.next();
        
        try {
            while ((line = lnr.readLine()) != null) {
                linesRead++;
                
                if (nextMethodLine == linesRead) {
                    /* The line read denotes the start of a function/method.
                     * Start calculating.  
                     */ 
                    inFunction = true;
                    numMethods++;
                    
                    /* Advance the next method pointer */
                    if (nextMethod.hasNext())
                        nextMethodLine = nextMethod.next();
                }
                
                if (!inFunction)
                    continue;
                
                /* The method/function end is determined by */
                if (startBlock.matcher(line).find()) {
                    blockDepth++;
                }

                if (endBlock.matcher(line).find()) {
                    blockDepth--;
                }

                /* 
                 * Method/function lines finished. Summarise results and save
                 * them in results table. 
                 */
                if (blockDepth == 0) {
                    inFunction = false;
                    int result = 1;
                    result += (mcResults.get("branch")==null)?0:mcResults.get("branch");
                    result += (mcResults.get("switch")==null)?0:mcResults.get("switch");
                    mcresult.add(result);
                    result += (mcResults.get("ext")==null)?0:mcResults.get("ext");
                    extresult.add(result);
                    mcResults = new HashMap<String, Integer>();
                    continue;
                }
                
                /* Apply heuristics to calculate the McCabe metric*/
                m = mcBranch.matcher(line);
                while (m.find()) {
                    incResult(mcResults, 1, "branch");
                }

                m = mcSwitch.matcher(line);
                while (m.find()) {
                    incResult(mcResults, 1, "switch");
                }

                m = mcExt.matcher(line);
                while (m.find()) {
                    incResult(mcResults, 1, "ext");
                }
            }
        } catch (IOException ioe) {
            log.warn("StructureMetrics: Failed to process file <" + pf.getFileName() +">", ioe);
        } 
        
        /*Summrize results per file*/
        int max = 0, total = 0;
        for (Integer i : mcresult) {
            if (i > max) {
                max = i;
            }
            total += i;
        }
        
        if (numMethods == 0) {
            return;
        }
        
        int emax = 0, etotal = 0;
        for (Integer i : extresult) {
            if (i > emax) {
                emax = i;
            }
            etotal += i;
        }
        
        /*Save them*/
        System.out.println("MNEM_CC_AVG:" + (int)total/numMethods + " MAX:" + max + " MNEM_CC_T:" + total);
        System.out.println("MNEM_ECC_AVG:" + (int)etotal/numMethods + " EMAX:" + emax + " MNEM_ECC_T:" + etotal);
    }
    
    protected void halstead(byte[] fileNoComments, List<Integer> methodStart) {
        
    }
    
    private void incResult(HashMap<String, Integer> result, int value,  String key) {
        if (result.containsKey(key)) {
            int res = result.get(key);
            result.put(key, res + value);
        } else {
            result.put(key, value);
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

