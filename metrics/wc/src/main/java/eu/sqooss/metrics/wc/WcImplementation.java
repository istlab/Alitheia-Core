/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
 *                  Athens, Greece.
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
package eu.sqooss.metrics.wc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlreadyProcessingException;
import eu.sqooss.service.abstractmetric.MetricDecl;
import eu.sqooss.service.abstractmetric.MetricDeclarations;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.FileTypeMatcher;

@MetricDeclarations(metrics= {
	@MetricDecl(mnemonic="Wc.loc", activators={ProjectFile.class}, descr="Total lines"),
	@MetricDecl(mnemonic="Wc.locom", activators={ProjectFile.class}, descr="Comment lines"),
	@MetricDecl(mnemonic="Wc.lonb", activators={ProjectFile.class}, descr="Non-blank lines"),
	@MetricDecl(mnemonic="Wc.words", activators={ProjectFile.class}, descr="Total words"),
	@MetricDecl(mnemonic="NOF", activators={ProjectVersion.class}, descr="Number of Files"),
	@MetricDecl(mnemonic="NOSF", activators={ProjectVersion.class}, descr="Number of Source Code Files"),
	@MetricDecl(mnemonic="NODF", activators={ProjectVersion.class}, descr="Number of Documentation Files"),
	@MetricDecl(mnemonic="TL", activators={ProjectVersion.class}, descr="Total Number of Lines"),
	@MetricDecl(mnemonic="TLOC", activators={ProjectVersion.class}, descr="Total Lines of Code"),
	@MetricDecl(mnemonic="TLOCOM", activators={ProjectVersion.class}, descr="Total Lines of Comments"),
	@MetricDecl(mnemonic="TLDOC", activators={ProjectVersion.class}, descr="Total Number of Documentation Lines")
})
public class WcImplementation extends AbstractMetric {
    
    private FDSService fds;
    private FileTypeMatcher ftm = FileTypeMatcher.getInstance();

    private static final String MNEMONIC_WC_LOC   = "Wc.loc";
    private static final String MNEMONIC_WC_LOCOM = "Wc.locom";
    private static final String MNEMONIC_WC_LONB  = "Wc.lonb";
    private static final String MNEMONIC_WC_WORDS = "Wc.words";
    
    private static final String MNEMONIC_WC_PV_NOF = "NOF";
    private static final String MNEMONIC_WC_PV_NOSF = "NOSF";
    private static final String MNEMONIC_WC_PV_NODF = "NODF";
    private static final String MNEMONIC_WC_PV_TL   = "TL";
    private static final String MNEMONIC_WC_PV_TLOC = "TLOC";
    private static final String MNEMONIC_WC_PV_TLOCOM = "TLOCOM";
    private static final String MNEMONIC_WC_PV_TLDOC = "TLDOC";
    
    private static HashMap<String,String[]> commentDelimiters;
    
    /*Implements Ohloh in 500 lines*/
    public WcImplementation(BundleContext bc) {
        super(bc);
        
        ServiceReference serviceRef = null;
        serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
       
        fds = ((AlitheiaCore)bc.getService(serviceRef)).getFDSService();
        commentDelimiters = new HashMap<String,String[]>(10);
        // Fill up the comment delimiters hash with a collection
        // of delimiters for various languages.
        addCommentDelimiters("cpp|C|cc|java|hpp|h",new String[]{"//","/\\*","\\*/"});
        addCommentDelimiters("c",new String[]{null,"/\\*","\\*/"});
        addCommentDelimiters("py|sh|pl|rb",new String[]{"#",null,null});
        addCommentDelimiters("html|xml|xsl",new String[]{null,"<!--","-->"});
    }

    public List<Result> getResult(ProjectFile a, Metric m) {
        
        List<Result> results = new ArrayList<Result>();
        // Search for a matching project file measurement
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectFile", a);
        filter.put("metric", m);
        List<ProjectFileMeasurement> measurement =
            db.findObjectsByProperties(ProjectFileMeasurement.class, filter);
        
        for (ProjectFileMeasurement pfm : measurement) 
            results.add(new Result(a, m, pfm.getResult(), Result.ResultType.INTEGER));
        
    	return results;
    }

    /**
     * Process an input stream, which is associated with a file
     * (or data source) with the given extension, and return a
     * array with 4 elements, one for each count of a metric
     * on the stream. The four elements are, in order,
     * loc, locom, lonb and words. 
     * 
     * @param extension Filename extension for this stream; may be null
     * @param in Input stream to read; may not ne null
     * @return Array of four metric results
     * @throws java.io.IOException On input error, means no useful 
     *      results are available.
     */
    public static int[] processStream(String extension, InputStream in) 
        throws IOException {
        int results[] = {0,0,0,0}; // loc, locom, lonb, words
        String delimiters[] = commentDelimiters.get(extension);
        if (null == delimiters) {
            delimiters = commentDelimiters.get("c");
        }
        
        /* Match start of multiline comment */
        String startMultiLine = delimiters[1];

        /* End multiline comment */
        String endMultiLine = delimiters[2];

        /* Match single line comments, C/Java/C++ style*/
        String singleLine = delimiters[0];

        Pattern singleLinePattern = null;
        if (null != singleLine) {
            singleLinePattern = Pattern.compile(singleLine);
        }

        // Measure the number of lines in the project file
        LineNumberReader lnr = 
            new LineNumberReader(new InputStreamReader(in));
        int comments = 0;
        int non_blank = 0;
        int words = 0;
        // The count of the number of lines is stored in the
        // line number reader itself.

        MultiLineMatcher mlm = null;
        if (null != startMultiLine) {
            mlm = new MultiLineMatcher(startMultiLine,endMultiLine);
        }
        String line = null;
        while ((line = lnr.readLine()) != null) {
            // Count non-blank lines
            if (line.trim().length()>0) {
                non_blank++;
            }

            // Count words -- the tokenizer is not the best approach
            words += new StringTokenizer(line).countTokens();

            // First we check for multi-line comments, then
            // for single liners if we have not already counted
            // the line as a comment.
            if ((null != mlm) && mlm.checkLineForComment(line)) {
                comments++;
            } else {
                if (null != singleLinePattern) {

                    // Find single-line comments
                    Matcher m = singleLinePattern.matcher(line);
                    /* Single line comments */
                    if (m.find()) {
                        comments++;
                    }
                }
            }
        }

        lnr.close();

        results[0]=lnr.getLineNumber();
        results[1]=comments;
        results[2]=non_blank;
        results[3]=words;
        
        return results;
    }
    
    public void run(ProjectFile pf) {
        // We do not support directories
        if (pf.getIsDirectory()) {
            return;
        }
        
        //Cannot run on deleted files
        if (pf.isDeleted()) {
            return;
        }
        
        //We don't support binary files either
        if (ftm.getFileType(pf.getName()).equals(
                FileTypeMatcher.FileType.BIN)) {
            return;
        }

        InputStream in = fds.getFileContents(pf);
        if (in == null) {
            return;
        }

        String extension = FileTypeMatcher.getFileExtension(pf.getName());
        
        int results[] = null;
        try {
            log.info("Reading file <" + pf.getName() +">");
            results = processStream(extension, in);
        } catch (IOException e) {
            log.warn("Failed to read file <" + pf.getFileName() +">",e);
        }
        

        // Store the results
        List<Metric> toUpdate = new ArrayList<Metric>();
        Metric metric = Metric.getMetricByMnemonic(MNEMONIC_WC_LOC);
        ProjectFileMeasurement locm = new ProjectFileMeasurement(
                metric,pf,String.valueOf(results[0]));
        db.addRecord(locm);
        toUpdate.add(metric);

        metric = Metric.getMetricByMnemonic(MNEMONIC_WC_LOCOM);
        ProjectFileMeasurement locc = new ProjectFileMeasurement(
                metric,pf,String.valueOf(results[1]));
        db.addRecord(locc);
        toUpdate.add(metric);
        
        metric = Metric.getMetricByMnemonic(MNEMONIC_WC_LONB);
        ProjectFileMeasurement lonb = new ProjectFileMeasurement(
                metric,pf,String.valueOf(results[2]));
        db.addRecord(lonb);
        toUpdate.add(metric);

        metric = Metric.getMetricByMnemonic(MNEMONIC_WC_WORDS);
        ProjectFileMeasurement words_measure = new ProjectFileMeasurement(
                metric,pf,String.valueOf(results[3]));
        db.addRecord(words_measure);
        toUpdate.add(metric);
    }

    /**
     * For a list of file extensions, register the three delimiters
     * as the single-line and multi-line comment delimiters. For the
     * comment matching, this will be used to power the multi-line
     * matcher and the single-line comment matchers.
     * 
     * (Not static because we want to be able to log errors)
     * 
     * @param extensions String listing file extensions separated by |
     * @param delimiters Three-element array of delimiter regexps; any
     *          one or more of these may be null.
     */
    private void addCommentDelimiters(String extensions, String[] delimiters) {
        if (delimiters.length != 3) {
            log.error("The number of delimiters for languages <" + extensions + "> is wrong (must be 3)");
            return;
        }
        
        String[] l = extensions.split("|");
        for (String e : l) {
            commentDelimiters.put(e,delimiters);
        }
    }

    /**
     * This is a very simple finite state machine that tracks
     * multi-line comments; the machine is either inside or
     * outside a multi-line comment, and it processes input line-by-line
     * to maintain its state. For each line, call checkLineForComment()
     * which returns true if the line may be considered a comment.
     * 
     * The FSM is parameterized by two regular expressions which should 
     * match the beginning and end of a multi-line comment.
     * 
     * No effort is made to handle quoting, strings, or the effects
     * of single-line comments, so the FSM may be confused by
     * commented comment symbols.
     */
    public static class MultiLineMatcher {
        private Pattern startRE;
        private Pattern endRE;
        private boolean inside;
        
        /**
         * Create a FSM with the given start and end regexps
         * for detecting the multi-line comment.
         * @param start Start-of-comment regexp; may not be null
         * @param end End-of-comment regexp; may not be null
         */
        MultiLineMatcher(String start, String end) {
            // It doesn't make sense to have null patterns
            // either for start or end, so don't check for null.
            this.startRE = Pattern.compile(start);
            this.endRE = Pattern.compile(end);
            this.inside = false;
        }
        
        /**
         * Given a line of input, move over it and maintain the
         * state of the machine (inside or out). Returns true if
         * the line is commented -- that means that a comment begins,
         * is in progress, or ends on the line.
         * 
         * @param line Line of text
         * @return true if the line may be considered a comment
         */
        public boolean checkLineForComment(String line) {
            // If we are inside at the start of the line, then
            // this is a comment line, regardless. Otherwise,
            // this will be counted as a comment only if a comment
            // starts on this line.
            boolean r = inside;
            
            Matcher m_start = startRE.matcher(line);
            Matcher m_end = endRE.matcher(line);
            
            // Set up a two-element array where toggle[0]
            // is the next relevant token to be looking for.
            // This is start if we're outside, and end if
            // we're inside a comment right now.
            Matcher toggle[] = { m_start, m_end } ;
            if (inside) {
                toggle[0] = m_end;
                toggle[1] = m_start;
            }
            
            // Looking from point forward in the line,
            // we keep looking for the next (toggle[0])
            // relevant token.
            int point = 0;
            while (toggle[0].find(point)) {
                // If we found it, advance point,
                // note that this line was a comment 
                // (if we were inside, then it already was;
                // and if we were outside, then we've just moved
                // inside. Change states from in to out or vice-versa.
                point=toggle[0].start()+1;
                r = true;
                inside = !inside;
                // Swap around the two relevant matchers
                Matcher temp = toggle[0];
                toggle[0]=toggle[1];
                toggle[1]=temp;
            }
            
            return r;
        }
    }

    public List<Result> getResult(ProjectVersion p, Metric m) {
        ArrayList<Result> results = new ArrayList<Result>();
        // Search for a matching project version measurement
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectVersion", p);
        filter.put("metric", m);
        List<ProjectVersionMeasurement> measurement =
            db.findObjectsByProperties(ProjectVersionMeasurement.class, filter);
        
        for (ProjectVersionMeasurement pfm : measurement) 
            results.add(new Result(p, m, pfm.getResult(), Result.ResultType.INTEGER));
        
        return results;
    }

    public void run(ProjectVersion v) throws AlreadyProcessingException {
        
        String paramVersionId = "paramVersion";
        String paramMetricLoC = "paramMetricLoC";
        String paramMetricLoCom = "paramMetricLoCom";
        String paramIsDirectory = "paramIsDirectory";
        String paramProjectId = "paramProjectId";
        String paramState = "paramState";
        Map<String, Object> params = new HashMap<String, Object>();
       
        /* Get all measurements for live version files for metrics LoC and LoCom*/ 
        StringBuffer q = new StringBuffer("select pfm ");
        if (v.getSequence() == ProjectVersion.getLastProjectVersion(v.getProject()).getSequence()) {
            q.append(" from ProjectFile pf, ProjectFileMeasurement pfm");
            q.append(" where pf.validUntil is null ");
        } else {
            q.append(" from ProjectVersion pv, ProjectVersion pv2,");
            q.append(" ProjectVersion pv3, ProjectFile pf, ProjectFileMeasurement pfm ");
            q.append(" where pv.project.id = :").append(paramProjectId);
            q.append(" and pv.id = :").append(paramVersionId);
            q.append(" and pv2.project.id = :").append(paramProjectId);
            q.append(" and pv3.project.id = :").append(paramProjectId);
            q.append(" and pf.validFrom.id = pv2.id");
            q.append(" and pf.validUntil.id = pv3.id");
            q.append(" and pv2.sequence <= pv.sequence");
            q.append(" and pv3.sequence >= pv.sequence");
            
            params.put(paramProjectId, v.getProject().getId());
            params.put(paramVersionId, v.getId());
        }
        q.append(" and pfm.projectFile = pf ");
        q.append(" and pf.state <> :").append(paramState);
        q.append(" and pf.isDirectory = :").append(paramIsDirectory);
        q.append(" and (pfm.metric.id = :").append(paramMetricLoC);
        q.append(" or pfm.metric.id = :").append(paramMetricLoCom).append(")");

        params.put(paramMetricLoC, Metric.getMetricByMnemonic(MNEMONIC_WC_LOC).getId());
        params.put(paramMetricLoCom, Metric.getMetricByMnemonic(MNEMONIC_WC_LOCOM).getId());
        params.put(paramIsDirectory, Boolean.FALSE);
        params.put(paramState, ProjectFileState.deleted());
        
        List<ProjectFileMeasurement> results = 
            (List<ProjectFileMeasurement>) db.doHQL(q.toString(), params);
        
        long nof = 0;            //Number of files
        int nosf = 0;           //Number of source code files
        int nodf = 0;           //Number of documentation files
        int totalLoC = 0;       //Total Lines of code
        int totalLoComm = 0;    //Total Lines of comments
        int totalLocDoc = 0;    //Total Lines of doc
        
        params.remove(paramMetricLoCom);
        params.remove(paramMetricLoC);
        nof = v.getLiveFilesCount();
        
        for (ProjectFileMeasurement pfm : results) {
            String fname = pfm.getProjectFile().getName();
            int result = Integer.parseInt(pfm.getResult());
            if (ftm.getFileType(fname).equals(
                            FileTypeMatcher.FileType.SRC)) {
                nosf ++;
                if (pfm.getMetric().getMnemonic().equals(MNEMONIC_WC_LOC)) {
                    totalLoC += result;
                } else {
                    totalLoComm += result;
                }
            }
            
            if (ftm.getFileType(fname).equals(
                            FileTypeMatcher.FileType.DOC)) {
                nodf ++;
                totalLocDoc += result;
            }
        }
        
        List<Metric> toUpdate = new ArrayList<Metric>();
        
        toUpdate.add(addPVMeasurement(MNEMONIC_WC_PV_NODF, v, nodf));
        toUpdate.add(addPVMeasurement(MNEMONIC_WC_PV_NOF, v, (int)nof));
        toUpdate.add(addPVMeasurement(MNEMONIC_WC_PV_NOSF, v, nosf));
        toUpdate.add(addPVMeasurement(MNEMONIC_WC_PV_TL, v, totalLocDoc + totalLoC));
        toUpdate.add(addPVMeasurement(MNEMONIC_WC_PV_TLDOC, v, totalLocDoc));
        toUpdate.add(addPVMeasurement(MNEMONIC_WC_PV_TLOC, v, totalLoC));
        toUpdate.add(addPVMeasurement(MNEMONIC_WC_PV_TLOCOM, v, totalLoComm));
    }
    
    private Metric addPVMeasurement(String s, ProjectVersion pv, int value) {
        Metric m = Metric.getMetricByMnemonic(s); 
        ProjectVersionMeasurement pvm = new ProjectVersionMeasurement(m , pv, 
                String.valueOf(value));
        db.addRecord(pvm);
        return m;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
