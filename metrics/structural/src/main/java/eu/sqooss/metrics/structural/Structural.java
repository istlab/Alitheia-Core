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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ProjectFileMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.FileTypeMatcher;
import eu.sqooss.service.util.FileUtils;

/**
 * The Structural complexity metrics suite implement standard complexity
 * metrics such as McCabe's Cyclomatic Complexity and Halstead's Software
 * science metrics.
 */ 
public class Structural extends AbstractMetric implements ProjectFileMetric {
    protected static String MNEM_CC_T = "MCC_TOTAL";
    protected static String MNEM_CC_MAX = "MCC_MAX";
    
    protected static String MNEM_ECC_T = "EMCC_TOTAL";
    protected static String MNEM_ECC_MAX = "EMCC_MAX";
    
    protected static String MNEM_NUM_FUN = "NUMFUN";
    
    protected static String MNEM_HN = "HN";
    protected static String MNEM_HVS = "HVS";
    protected static String MNEM_HV = "HV";
    protected static String MNEM_HD = "HD";
    protected static String MNEM_HL = "HL";
    protected static String MNEM_HE = "HE";
    protected static String MNEM_HT = "HT";
    protected static String MNEM_HB = "HB";
    
    private ThreadLocal<ProjectFile> fileDAO;
    
    /* Helper array to tell metrics returning double from metrics returning
     * integer values
     */
    private static List<String> mimeTypeDouble = new ArrayList<String>();
    
    static {
        mimeTypeDouble.add(MNEM_HV);
        mimeTypeDouble.add(MNEM_HD);
        mimeTypeDouble.add(MNEM_HL);
        mimeTypeDouble.add(MNEM_HE);
        mimeTypeDouble.add(MNEM_HT);
        mimeTypeDouble.add(MNEM_HB);
    }
   
    /* Contains a regular expression that can detect with reasonable accuracy 
     * a method declaration for the specified extention. The regular expression
     * is expected to work in Pattern.MULTI_LINE mode.  
     */
    private HashMap<String, String> methodDecl = new HashMap<String, String>();
    
    /*
     * 
     */
    private HashMap<String, String> operators = new HashMap<String, String>();
    
    public Structural(BundleContext bc) {
        super(bc);
        super.addActivationType(ProjectFile.class);
        
        super.addMetricActivationType(MNEM_CC_T, ProjectFile.class);
        super.addMetricActivationType(MNEM_CC_MAX, ProjectFile.class);
        super.addMetricActivationType(MNEM_ECC_T, ProjectFile.class);
        super.addMetricActivationType(MNEM_ECC_MAX, ProjectFile.class);
        
        super.addMetricActivationType(MNEM_NUM_FUN, ProjectFile.class);
        
        super.addMetricActivationType(MNEM_HN, ProjectFile.class);
        super.addMetricActivationType(MNEM_HVS, ProjectFile.class);
        super.addMetricActivationType(MNEM_HV, ProjectFile.class);
        super.addMetricActivationType(MNEM_HD, ProjectFile.class);
        super.addMetricActivationType(MNEM_HL, ProjectFile.class);
        super.addMetricActivationType(MNEM_HE, ProjectFile.class);
        super.addMetricActivationType(MNEM_HT, ProjectFile.class);
        super.addMetricActivationType(MNEM_HB, ProjectFile.class);
        
        InputStream is = null;
        Properties p = new Properties();
        try {
            is = bc.getBundle().getResource("/config.properties").openStream();
            p.load(is);
        } catch (Exception e) {
          log.warn("Cannot find language configuration file");
        } 
        
        String[] languages = p.getProperty("languages").split(" ");
        
        for (String lang : languages) {
            String regexp = p.getProperty(lang + ".method.regexp");
            methodDecl.put(lang, regexp);
            
            String[] ops = p.getProperty(lang + ".operators").split(" ");
            StringBuilder sb = new StringBuilder();
            for (String op : ops) {
                sb.append(op).append("|");
            }
            sb.deleteCharAt(sb.lastIndexOf("|"));
            operators.put(lang, sb.toString());
        }
        
        fileDAO = new ThreadLocal<ProjectFile>();
    }
    
    public boolean install() {
        boolean result = super.install();
        
        if (result) {
            result &= super.addSupportedMetrics(
                    "Total McCabe Cyclomatic Complexity", 
                    MNEM_CC_T, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Max McCabe Cyclomatic Complexity", 
                    MNEM_CC_MAX, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Total Extended McCabe Cyclomatic Complexity", 
                    MNEM_ECC_T, MetricType.Type.SOURCE_CODE);            
            result &= super.addSupportedMetrics(
                    "Max Extended McCabe Cyclomatic Complexity", 
                    MNEM_ECC_MAX, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Number of functions", 
                    MNEM_NUM_FUN, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Halstead Length", 
                    MNEM_HN, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Halstead vocabulary size", 
                    MNEM_HVS, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Halstead Volume", 
                    MNEM_HV, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Halstead Difficulty Level", 
                    MNEM_HD, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Halstead Program Level", 
                    MNEM_HL, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Halstead Effort", 
                    MNEM_HE, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Halstead Time", 
                    MNEM_HT, MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Halstead Bugs Derived", 
                    MNEM_HB, MetricType.Type.SOURCE_CODE);
        }
        return result;
    }

    public List<ResultEntry> getResult(ProjectFile a, Metric m) {        
        String mime = mimeTypeDouble.contains(m.getMnemonic())?
                ResultEntry.MIME_TYPE_TYPE_DOUBLE:ResultEntry.MIME_TYPE_TYPE_INTEGER;
        
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("projectFile", a);
        props.put("metric", m);
        List<ProjectFileMeasurement> pfms = dbs.findObjectsByProperties(ProjectFileMeasurement.class, props);
        
        if (pfms.isEmpty())
            return null;
        
        ArrayList<ResultEntry> result = new ArrayList<ResultEntry>();
        
        if (mime.equals(ResultEntry.MIME_TYPE_TYPE_INTEGER))
            result.add(new ResultEntry(Integer.parseInt(pfms.get(0).getResult()), mime, m.getMnemonic()));
        else 
            result.add(new ResultEntry(Double.parseDouble(pfms.get(0).getResult()), mime, m.getMnemonic()));
        
        return result;
    }
    
    public void run(ProjectFile pf) {
        if (pf.isDeleted() || pf.getIsDirectory() || 
                !FileTypeMatcher.getInstance().isSourceFile(pf.getName())) {
            return;
        }
        
        pf = db.attachObjectToDBSession(pf);
        this.fileDAO.set(pf);
        
        FDSService fds = AlitheiaCore.getInstance().getFDSService();
        BufferedInputStream in = new BufferedInputStream(fds.getFileContents(pf));
        
        if (in == null) {
            return;
        }
        
        /* Read the input file and remove all comments */
        byte[] fileContents = stripComments(in);
        
        /* Remove string contents */
        fileContents = stripStrings(fileContents);
        
        /* Call the metric calculation methods*/
        halstead(fileContents);
        mccabe(fileContents);
    }
    

   protected enum StringState {
       DEFAULT, INSTRING, INCHAR, STRINGQ, CHARQ
   }
   
    /**
     * A method that shortens strings (identified by "") and chars (identified
     * by '') by removing their content, but leaving the string delimiters.
     */
    protected byte[] stripStrings(byte[] file) {
        StringState state = StringState.DEFAULT;
        byte[] buff = new byte[file.length];
        int index = 0;
        
        for (byte b : file) {
            switch (b) {
            case '"':
                if (state == StringState.DEFAULT) {
                    state = StringState.INSTRING;
                    buff[index++] = b;
                }
                else if (state == StringState.INSTRING) {
                    state = StringState.DEFAULT;
                }
                break;
            case '\\':
                if (state == StringState.INSTRING)
                    state = StringState.STRINGQ;
                else if (state == StringState.INCHAR)
                    state = StringState.CHARQ;
                break;
            case '\'':
                if (state == StringState.DEFAULT) { 
                    state = StringState.INCHAR;
                    buff[index++] = b;
                }
                else if (state == StringState.INCHAR) {
                    state = StringState.DEFAULT;
                }

                break;
            default:
                if (state == StringState.CHARQ)
                    state = StringState.INCHAR;
                else if (state == StringState.STRINGQ)
                    state = StringState.INSTRING;
                break;
            }
            
            if (state == StringState.DEFAULT) {
                buff[index++] = b;
            }
        }
        byte[] fileNoStrings = new byte[buff.length];
        System.arraycopy(buff, 0, fileNoStrings, 0, buff.length);
        return fileNoStrings;
    }

    protected enum CommentState {
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
            CommentState state = CommentState.DEFAULT;
            while ((b = (byte) in.read()) != -1) {
                switch (b) {
                case '/':
                    if (state == CommentState.MAYBECOMMENT) {
                        state = CommentState.LINECOMMENT;
                    }
                    else if (state == CommentState.MAYBECLOSEMULTI) {
                        state = CommentState.DEFAULT;
                        b = '\n';
                    }
                    else if (state == CommentState.DEFAULT)
                        state = CommentState.MAYBECOMMENT;
                    break;
                case '*':
                    if (state == CommentState.MAYBECOMMENT)
                        state = CommentState.MULTICOMMENT;
                    else if (state == CommentState.MULTICOMMENT)
                        state = CommentState.MAYBECLOSEMULTI;
                    break;
                case '\n':
                    if (state == CommentState.LINECOMMENT) {
                        state = CommentState.DEFAULT;
                        b = '\n';
                    }
                    break;
                default:
                    if (state == CommentState.MAYBECOMMENT)
                        state = CommentState.DEFAULT;
                    else if (state == CommentState.MAYBECLOSEMULTI)
                        state = CommentState.MULTICOMMENT;
                }

                if (state == CommentState.DEFAULT) {
                    buffer[counter] = b;
                    counter++;
                }

            }
            byte[] fileNoComments = new byte[counter];
            System.arraycopy(buffer, 0, fileNoComments, 0, counter);
            return fileNoComments;
        } catch (IOException ioe) {
            log.warn("StructureMetrics: Failed to read file <" + 
                    fileDAO.get().getFileName() +">", ioe);
            return null;
        }
    }
    
    /**
     * Calculate the McCabe complexity and McCabe extended complexity metrics.
     * @param fileNoComments The file to run the metrics on, stripped of comments
     * @param methodStart A list of start lines for all identified 
     * functions/methods, ordered by line number.
     */
    protected void mccabe(byte[] fileNoComments) {
        /*Regexps to identify various program portions*/
        Pattern startBlock = Pattern.compile("\\{");
        Pattern endBlock = Pattern.compile("\\}");
        Pattern mcBranch = Pattern.compile("if|while|for|catch|finally");
        Pattern mcExt = Pattern.compile("&&|\\|\\|");
        Pattern mcSwitch = Pattern.compile("case|default");
        Pattern mcReturn = Pattern.compile("return");
        
        /*1. Constuct a list of method start locations*/
        /* Get the pattern for the file type. */
        String pattern = methodDecl.get(
                FileUtils.extension(fileDAO.get().getFileName()));
   
        if (pattern == null) {
            return;
        } 

        Pattern methodMatch = Pattern.compile(pattern, Pattern.MULTILINE);

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
            log.warn("Structural: " + fileDAO.get() + 
                    ". No methods identified.");
            return;
        }
        
        /*2. Calculate*/
        HashMap<String, Integer> mcResults = new HashMap<String, Integer>();
        LineNumberReader lnr = new LineNumberReader(new InputStreamReader(
                new ByteArrayInputStream(fileNoComments)));
        List<Integer> mcresult = new ArrayList<Integer>();
        List<Integer> extresult = new ArrayList<Integer>();
        int numMethods = 0, linesRead = 0;
        Iterator<Integer> nextMethod = methodStart.iterator();
        int nextMethodLine = nextMethod.next();
        boolean inFunction = false;
        String line = "";
        int blockDepth = 0;
        
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
            log.warn("StructureMetrics: Failed to process file <" + fileDAO.get().getFileName() +">", ioe);
        } 
        
        /*Summarize results per file*/
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
        
        addRecord(MNEM_CC_MAX, fileDAO.get(), String.valueOf(max));
        addRecord(MNEM_CC_T, fileDAO.get(), String.valueOf(total));
        addRecord(MNEM_NUM_FUN, fileDAO.get(), String.valueOf(numMethods));
        addRecord(MNEM_ECC_MAX, fileDAO.get(), String.valueOf(emax));
        addRecord(MNEM_ECC_T, fileDAO.get(), String.valueOf(etotal));
    }
    
    private void incResult(HashMap<String, Integer> result, int value,  String key) {
        if (result.containsKey(key)) {
            int res = result.get(key);
            result.put(key, res + value);
        } else {
            result.put(key, value);
        }
    }
    
    /**
     * Calculates Halstread's software science metrics. 
     */
    protected void halstead(byte[] fileNoComments) {
        
        byte[] fileNoNewLines = new byte[fileNoComments.length];
        int j = 0;
        
        //Remove line delimeters
        
        for (int i = 0; i < fileNoComments.length; i++) {
            if (fileNoComments[i] == '\r' || fileNoComments[i] == '\n')
                continue;
            fileNoNewLines[j] = fileNoComments[i];
            j++;
        }
       

        /* Convert to a string for tokenisation*/
        String contents = new String(fileNoNewLines, 0, j);

        /* Get the tokenisation regexp suitable for the processed file type*/
        String regexp = operators.get(FileUtils.extension(fileDAO.get().getFileName()));
        
        if (regexp == null) {
            return;
        }
        
        Pattern tokenizer = Pattern.compile(regexp);
         
        Matcher m = tokenizer.matcher(contents);
        StringBuffer toTokenize = new StringBuffer();
        int last = 0;
        
        
        List<String> operators = new ArrayList<String>();
        List<String> operands = new ArrayList<String>();
        /*
         * Tokenize based on (greedy) regexp matching. Add spaces around
         * identified language keywords to help tokenization later on.
         */
        while (m.find()) {
            if (m.start() > last + 1) { 
                toTokenize.append(contents.subSequence(last, m.start()));
                toTokenize.append(" ");
            }
   
            toTokenize.append(contents.subSequence(m.start(), m.end()));
            toTokenize.append(" ");
            last = m.end();
        }
        
        if (last < contents.length())
            toTokenize.append(contents.subSequence(last, contents.length() - 1));

        /* Finally, tokens! */
        String[] tokens = toTokenize.toString().split(" ");

        
        /* Split tokens in operators and operands */
        for (String t : tokens) {
            t = t.trim();
            
            if (t.length() <= 0)
                continue;
            
            if (tokenizer.matcher(t).find()) {
                operators.add(t);
            } else {
                operands.add(t);
            }
        }
       
        /*
         * Halstead metric notation:
         * N1 = the total number of operators
         * N2 = the total number of operands
         * n1 = the number of distinct operators
         * n2 = the number of distinct operands
         */
        int N1 = operators.size();
        int N2 = operands.size();
        int n1 = uniq(operators).size();
        int n2 = uniq(operands).size();
        
        /* Program Length*/
        int N = N1 + N2;
        
        /* Program Vocabulary*/
        int n = n1 + n2;
        
        /* Program Volume*/
        double V = N * (double)(Math.log(n)/Math.log(2));
        
        /* Difficulty */
        double D = (double)(n1 / 2) * (double)(N2 / n2);

        /* Level */
        double L = (double)(1 / D);
        
        /* Effort */
        double E = (double)(D * V);
        
        /* Time to implement */
        double T = (double)E/18;
        
        /* Bugs */
        double B = (double)(( E * (double)(2/3) ) / 3000);
        
        addRecord(MNEM_HN, fileDAO.get(), String.valueOf(N));
        addRecord(MNEM_HVS, fileDAO.get(), String.valueOf(n));
        addRecord(MNEM_HV, fileDAO.get(), String.valueOf(V));
        addRecord(MNEM_HD, fileDAO.get(), String.valueOf(D));
        addRecord(MNEM_HL, fileDAO.get(), String.valueOf(L));
        addRecord(MNEM_HE, fileDAO.get(), String.valueOf(E));
        addRecord(MNEM_HT, fileDAO.get(), String.valueOf(T));
        addRecord(MNEM_HB, fileDAO.get(), String.valueOf(B));
    }
    
    private Set<String> uniq(List<String> arlList) {
        HashSet<String> h = new HashSet<String>(arlList);
        return h;
    }
    
    private void addRecord(String mnem, ProjectFile pf, String value) {
        Metric m = Metric.getMetricByMnemonic(mnem);
        ProjectFileMeasurement pfm = new ProjectFileMeasurement(m, pf, value);
        db.addRecord(pfm); 
        markEvaluation(m, pf);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

