/*
 * Copyright 2009 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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
package eu.sqooss.metrics.mi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.AlreadyProcessingException;
import eu.sqooss.service.abstractmetric.ProjectFileMetric;
import eu.sqooss.service.abstractmetric.ProjectVersionMetric;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.fds.FileTypeMatcher;
import eu.sqooss.service.pa.PluginInfo;

/**
 * Implements module and project wide maintainability index calculation
 * according to the formula presented in
 * 
 * Coleman, D., Ash, D., Lowther, B., and Oman, P. (1994). Using metrics to 
 * evaluate software system maintainability. Computer, 27(8):44Ð49.
 * 
 * @author Georgios Gousios - <gousiosg@gmail.com>
 */ 
public class Mi extends AbstractMetric implements ProjectFileMetric, 
    ProjectVersionMetric {
    
    /*Metrics defined*/
    private static String MNEMONIC_MI = "MI";
    private static String MNEMONIC_MODMI = "MODMI";
    
    /*Dependencies*/
    private static final String MNEM_LOCOM = "Wc.locom";
    private static final String MNEM_LOC = "Wc.loc";
    private static final String MNEM_ECC = "EMCC_TOTAL";
    private static final String MNEM_HV = "HV";
    private static final String MNEM_ISSRC = "ISSRCMOD";
        
    public Mi(BundleContext bc) {
        super(bc);        
 
        super.addActivationType(ProjectFile.class);
        super.addActivationType(ProjectVersion.class);
        
        super.addMetricActivationType(MNEMONIC_MI, ProjectVersion.class);
        super.addMetricActivationType(MNEMONIC_MODMI, ProjectFile.class);
        
        super.addDependency(MNEM_LOCOM);
        super.addDependency(MNEM_ECC);
        super.addDependency(MNEM_HV);
        super.addDependency(MNEM_ISSRC);
        super.addDependency(MNEM_LOC);
    }
    
    public boolean install() {
        //This should always be called to run various init tasks
        boolean result = super.install();
        
        if (result) {
            result &= super.addSupportedMetrics(
                    "Maintainability Index for the project",
                    MNEMONIC_MI,
                    MetricType.Type.PROJECT_WIDE);
            result &= super.addSupportedMetrics(
                    "Maintainability Index for a module",
                    MNEMONIC_MODMI,
                    MetricType.Type.SOURCE_FOLDER);
        }
        return result;
    }

    public List<ResultEntry> getResult(ProjectFile pf, Metric m) {
        // Prepare an array for storing the retrieved measurement results
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();

        if (!pf.getIsDirectory())
            return null;

        // Search for a matching measurement results
        List<ProjectFileMeasurement> measurement = null;
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectFile", pf);
        filter.put("metric", m);
        measurement = db.findObjectsByProperties(
                ProjectFileMeasurement.class, filter);

        // Convert the measurement into a result object
        if (!measurement.isEmpty()) {
            results.add(new ResultEntry(
                    Double.parseDouble(measurement.get(0).getResult()),
                    ResultEntry.MIME_TYPE_TYPE_DOUBLE,
                    m.getMnemonic()));
        }

        return results.isEmpty() ? null : results;
        
    }
    
    public void run(ProjectFile pf) throws AlreadyProcessingException {
        
        pf = db.attachObjectToDBSession(pf);
        
        /*MI works at the module level for src directories*/
        if (!pf.getIsDirectory())
            return;
        
        AlitheiaPlugin modulemetrics = pa.getImplementingPlugin(MNEM_ISSRC);
        Metric issrc = Metric.getMetricByMnemonic(MNEM_ISSRC);
        Integer result = getResult(modulemetrics, issrc, pf, Integer.class);

        if (result == null)
            return;

        if (result == 0)
            return;

        /* We now know that we are working with a dir*/
        List<ProjectFile> fileList = pf.getProjectVersion().getFiles(
                Directory.getDirectory(pf.getFileName(), false), 
                ProjectVersion.MASK_FILES);
        
        AlitheiaPlugin loc =  pa.getImplementingPlugin(MNEM_LOC);
        Metric locmetric = Metric.getMetricByMnemonic(MNEM_LOC);
        Metric locommetric = Metric.getMetricByMnemonic(MNEM_LOCOM);
        
        AlitheiaPlugin structure = pa.getImplementingPlugin(MNEM_HV);
        Metric hvmetric = Metric.getMetricByMnemonic(MNEM_HV);
        Metric eccmetric = Metric.getMetricByMnemonic(MNEM_ECC);
        
        /*Empty directory*/
        if (fileList.size() == 0)
            return;
        
        double aveV = 0, aveG = 0, perCM = 0, aveLOC = 0 ;
        double totalV = 0;
        int totalLoCom = 0, totalG = 0, totalLoC = 0;
        FileTypeMatcher ftm = FileTypeMatcher.getInstance();
        
        for (ProjectFile f : fileList) {
                        
            if (f.getIsDirectory() || !ftm.isSourceFile(f.getFileName()))
                continue;
            
            Double HV = getResult(structure, hvmetric, f, Double.class);
            
            if (HV == null) {
                log.warn("Error getting metric " + MNEM_HV 
                        + " for file " + f);
                continue;
            } else {
                totalV += HV;
            }
            
            Integer ECC_TOTAL = getResult(structure, eccmetric, f, Integer.class);
            
            if (ECC_TOTAL == null) {
                log.warn("Error getting metric " + MNEM_ECC 
                        + " for file " + f);
                totalV -= HV;
                continue;
            } else {
                totalG += ECC_TOTAL;
            }
            
            Integer LOC = getResult(loc, locmetric, f, Integer.class);
            
            if (LOC == null) {
                log.warn("Error getting metric " + MNEM_LOC
                        + " for file " + f);
                totalV -= HV;
                totalG -= ECC_TOTAL;
                continue;
            } else {
                totalLoC += LOC;
            }
            
            Integer LOCOM = getResult(loc, locommetric , f, Integer.class);
            
            if (LOCOM == null) {
                log.warn("Error getting metric " + MNEM_LOCOM 
                        + " for file " + f);
                totalV -= HV;
                totalG -= ECC_TOTAL;
                totalLoC -= LOC;
                continue;
            } else {
                totalLoCom += LOCOM;
            }
        }
         
        /* This means that while the module is a source module
         * no parser has been defined in the Structural metrics
         * plugin to support the language this module is written into 
         */
        if (totalV == 0 || totalG == 0) {
            return;
        }
        
        aveLOC = (double)(totalLoC / fileList.size());
        aveV = (double)(totalV / fileList.size());
        aveG = (double)(totalG / fileList.size());
        perCM = (double)(totalLoCom / fileList.size());
        
        double MI = 171 - 
            5.2 * Math.log(aveV) - 
            0.23 * aveG - 
            16.2 * Math.log(aveLOC) + 
            50 * Math.sin(Math.sqrt(2.4 * perCM));
        
        Metric m = Metric.getMetricByMnemonic(MNEMONIC_MODMI);
        ProjectFileMeasurement pfm = new ProjectFileMeasurement(m, pf, 
                String.valueOf(MI));
        db.addRecord(pfm);
        markEvaluation(m, pf);
        
    }

    public List<ResultEntry> getResult(ProjectVersion pv, Metric m) {
        
     // Prepare an array for storing the retrieved measurement results
        ArrayList<ResultEntry> results = new ArrayList<ResultEntry>();

        // Search for a matching measurement results
        List<ProjectVersionMeasurement> measurement = null;
        HashMap<String, Object> filter = new HashMap<String, Object>();
        filter.put("projectVersion", pv);
        filter.put("metric", m);
        measurement = db.findObjectsByProperties(
                ProjectVersionMeasurement.class, filter);

        // Convert the measurement into a result object
        if (!measurement.isEmpty()) {
            results.add(new ResultEntry(
                    Double.parseDouble(measurement.get(0).getResult()),
                    ResultEntry.MIME_TYPE_TYPE_DOUBLE,
                    m.getMnemonic()));
        }

        return results.isEmpty() ? null : results;
    }

    public void run(ProjectVersion pv) throws AlreadyProcessingException {
        
        String paramIsDirectory = "is_directory";
        String paramMNOL = "paramMNOL";
        String paramISSRCDIR = "paramISSRCDIR";
        String paramVersionId = "paramVersionId";
        String paramProjectId = "paramProjectId";
        String paramState = "paramStatus";
        
        StringBuffer q = new StringBuffer("select pfm ");
        q.append(" from ProjectVersion pv, ProjectVersion pv2,");
        q.append(" ProjectVersion pv3, ProjectFile pf, ");
        q.append(" ProjectFileMeasurement pfm ");
        q.append(" where pv.project.id = :").append(paramProjectId);
        q.append(" and pv.id = :").append(paramVersionId);
        q.append(" and pv2.project.id = :").append(paramProjectId);
        q.append(" and pv3.project.id = :").append(paramProjectId);
        q.append(" and pf.validFrom.id = pv2.id");
        q.append(" and pf.validUntil.id = pv3.id");
        q.append(" and pv2.sequence <= pv.sequence");
        q.append(" and pv3.sequence >= pv.sequence");
        q.append(" and pf.state <> :").append(paramState);
        q.append(" and pf.isDirectory = :").append(paramIsDirectory);
        q.append(" and pfm.projectFile = pf");
        q.append(" and pfm.metric = :").append(paramMNOL);
        q.append(" and exists (select pfm1 ");
        q.append(" from ProjectFileMeasurement pfm1 ");
        q.append(" where pfm1.projectFile = pfm.projectFile ");
        q.append(" and pfm1.metric = :").append(paramISSRCDIR).append(")");
                
        Map<String,Object> params = new HashMap<String,Object>();
        params.put(paramProjectId, pv.getProject().getId());
        params.put(paramVersionId, pv.getId());
        params.put(paramState, ProjectFileState.deleted());
        params.put(paramIsDirectory, true);
        params.put(paramMNOL, Metric.getMetricByMnemonic(MNEMONIC_MODMI));
        params.put(paramISSRCDIR, Metric.getMetricByMnemonic(MNEM_ISSRC));
        
        
        // Get the list of folders which exist in this project version.
        List<ProjectFileMeasurement> srcDirs = 
            (List<ProjectFileMeasurement>) db.doHQL(q.toString(), params);

        // Calculate the metric results
        double miTotal = 0;
       
        //For source directory directory
        for (ProjectFileMeasurement pfm : srcDirs) {
            double mi = Double.parseDouble(pfm.getResult());
            // Try to retrieve the MNOL measurement for this folder
            if (mi > 0)
                miTotal += mi;
        } 
        
        if (miTotal > 0) {

            Metric metric = Metric.getMetricByMnemonic(MNEMONIC_MI);
            ProjectVersionMeasurement ams = new ProjectVersionMeasurement(
                    metric, pv, String.valueOf(0));
            
            ams.setResult(String.valueOf(((float) (miTotal / srcDirs.size()))));
            db.addRecord(ams);
            markEvaluation(metric, pv.getProject());
        }
    }
    
    
  
    /**
     * Convenience method that returns the (numeric) result for a single metric
     * for which we now the return type. This method will not trigger a metric
     * calculation if the result in not in the DB.
     * 
     * @param <E> The type that the metric stores its result
     * @param mnemonic The metric to return the result for
     * @param c The DAObject to return the result for
     * @param resultType The type to return
     * 
     * @return The metric result or null if no result is in the database.
     */
    private <E extends Number> E getResult(AlitheiaPlugin plugin, Metric m, 
            DAObject c, Class<E> resultType) {
        
        if (plugin == null) {
            log.error("Could not find the " + m.getMnemonic() 
                    + " metric's plug-in");
            return null;
        }

        List<Metric> l = new ArrayList<Metric>();
        l.add(m);
        try {
            Result r = plugin.getResultIfAlreadyCalculated(c, l);

            if (r == null)
                return null;
            
            if (!r.hasNext())
                return null;

            List<ResultEntry> resline = r.next();

            if (!resline.iterator().hasNext())
                return null;

            String s = resline.iterator().next().toString();

            if (resultType.equals(Double.class))
                return (E) new Double(s);

            if (resultType.equals(Integer.class))
                return (E) new Integer(s);

            if (resultType.equals(Float.class))
                return (E) new Float(s);

            return null;
            
        } catch (Exception e) {
            log.error(this.getName() + ": Result for metric " + m.getMnemonic()
                    + " cannot be retrieved for DAO " + c.toString());
            return null;
        }
    }    
}
