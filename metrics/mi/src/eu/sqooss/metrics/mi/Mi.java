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
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.AlreadyProcessingException;
import eu.sqooss.service.abstractmetric.ProjectFileMetric;
import eu.sqooss.service.abstractmetric.ProjectVersionMetric;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.pa.PluginAdmin;

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
    private static final String MNEM_ECC = "EMCC_TOTAL";
    private static final String MNEM_HV = "HV";
    private static final String MNEM_ISSRC = "ISSRCMOD";
    private static final String MNEM_AMS = "AMS";
    
    // Holds the instance of the Alitheia core service
    private AlitheiaCore core;
    
    public Mi(BundleContext bc) {
        super(bc);        
 
        super.addActivationType(ProjectFile.class);
        //super.addActivationType(ProjectFile.class);
        
        super.addMetricActivationType(MNEMONIC_MI, ProjectVersion.class);
        super.addMetricActivationType(MNEMONIC_MODMI, ProjectFile.class);
        
        super.addDependency(MNEM_LOCOM);
        super.addDependency(MNEM_ECC);
        super.addDependency(MNEM_HV);
        super.addDependency(MNEM_ISSRC);
        super.addDependency(MNEM_AMS);
        
     // Retrieve the instance of the Alitheia core service
        ServiceReference serviceRef = bc.getServiceReference(
                AlitheiaCore.class.getName());
        if (serviceRef != null)
            core = (AlitheiaCore) bc.getService(serviceRef);
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
                    MetricType.Type.SOURCE_CODE);
        }
        return result;
    }

    public List<ResultEntry> getResult(ProjectFile a, Metric m) {
        
        return null;
    }
    
    public void run(ProjectFile pf) throws AlreadyProcessingException {
        /*MI works at the module level for src directories*/
        if (!pf.getIsDirectory() || !isSrcDir(pf))
            return;
        
        List<ProjectFile> fileList = pf.getProjectVersion().getFiles(pf.getDir());
        
        /*Empty directory*/
        if (fileList.size() == 0)
            return;
        
        double aveV = 0, aveG = 0, perCM = 0;
        double totalV = 0;
        Float aveLOC = new Float(0);
        int totalLoCom = 0, totalG = 0;
        
        for (ProjectFile f : fileList) {
            Double V = getResult(MNEM_HV, f, Double.class);
            
            if (V == null) {
                System.err.println("Error getting metric " + MNEM_HV 
                        + " for file " + f);
                return;
            }
            
            totalV += V;
            
            Integer ECC_TOTAL = getResult(MNEM_ECC, f, Integer.class);
            
            if (ECC_TOTAL == null) {
                System.err.println("Error getting metric " + MNEM_ECC 
                        + " for file " + f);
                return;
            }
            
            totalG += ECC_TOTAL;
            
            Integer LOCOM = getResult(MNEM_LOCOM, pf, Integer.class);
            
            if (LOCOM == null) {
                System.err.println("Error getting metric " + MNEM_LOCOM 
                        + " for file " + f);
                return;
            }
            
            totalLoCom += LOCOM;
        }
        
        aveLOC = getResult(MNEM_AMS, pf , Float.class);
        
        if (aveLOC == null) {
            System.err.println("Error getting metric " + MNEM_AMS 
                    + " for module " + pf);
            return;
        }
        
        aveV = (double)(totalV / fileList.size());
        aveG = (double)(totalG / fileList.size());
        perCM = (double)(totalLoCom / fileList.size());
        
        double MI = 171 - 
            5.2 * Math.log(aveV) - 
            0.23 * aveG - 
            16.2 * Math.log(aveLOC) + 
            50 * Math.sin(Math.sqrt(2.4 * perCM));
        
        System.err.println("MI " + MI + " dir " + pf.getFileName());
    }

    public List<ResultEntry> getResult(ProjectVersion p, Metric m) {
        
        return null;
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
    
    private boolean isSrcDir(ProjectFile pf) throws AlreadyProcessingException {

        Integer result = getResult(MNEM_ISSRC, pf, Integer.class);

        if (result == null)
            return false;

        if (result == 0)
            return false;

        return true;
    }
    
    private <E extends Number> E getResult(String mnemonic,
            DAObject c, Class<E> resultClass)
            throws AlreadyProcessingException {

        PluginAdmin  pa = core.getPluginAdmin();
        AlitheiaPlugin plugin = pa.getImplementingPlugin(mnemonic);
        if (plugin == null) {
            log.error("Could not find the " + mnemonic + " metric's plug-in");
            return null;
        }

        List<Metric> l = new ArrayList<Metric>();
        l.add(Metric.getMetricByMnemonic(mnemonic));
        try {
            Result r = plugin.getResult(c, l);
            
            if (r == null || r.get() == null)
                return null;
            
            String s = r.getRow(0).get(0).getString();
            
            if (resultClass.equals(Double.class))
                return (E) new Double(s);
            
            if (resultClass.equals(Integer.class))
                return (E) new Integer(s);
            
            if (resultClass.equals(Float.class))
                return (E) new Float(s);
            
        } catch (AlreadyProcessingException e) {
            throw e;
        } catch (Exception e) {
            log.error("MI: Results of " + mnemonic
                    + " cannot be retrieved for DAO " + c.toString());
            return null;
        }

        return null;
    }
}
