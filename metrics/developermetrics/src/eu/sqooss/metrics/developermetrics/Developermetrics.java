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
package eu.sqooss.metrics.developermetrics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlreadyProcessingException;
import eu.sqooss.service.abstractmetric.ProjectFileMetric;
import eu.sqooss.service.abstractmetric.ProjectVersionMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
 

/**
 * Basic developer-related statistics, like team size in various
 * time frames and number of developers working with on specific
 * resource.
 * 
 */
public class Developermetrics extends AbstractMetric 
    implements ProjectVersionMetric, ProjectFileMetric {
    
    private static String MNEM_TEAMSIZE1 = "TEAMSIZE1";
    private static String MNEM_TEAMSIZE3 = "TEAMSIZE3";
    private static String MNEM_TEAMSIZE6 = "TEAMSIZE6";
    
    private static String MNEM_EYEBALL = "EYBALL";
    private static String MNEM_EYEBALL_MOD = "MODEYBALL";
    
    private static String fileEyeballs = "select distinct pv.committer "
       + "from ProjectVersion pv, ProjectFile pf "
       + "where pf.projectVersion = pv.id " 
       + "and pf.name = :paramName "
       + "and pf.dir = :paramDir "
       + "and pv.project = :paramProject "
       + "and pv.sequence <= (" +
       		"select pv1.sequence " +
       		"from ProjectVersion pv1, ProjectFile pf1 " +
       		"where pf1.projectVersion = pv1 and pf1.id = :paramFileId)";
    
    private static String activeLast = "select count(distinct pv.committer) " +
        " from ProjectVersion pv " +
        " where pv.timestamp > (:paramTS - :paramOld) " +
        " and pv.timestamp < :paramTS " +
        " and pv.project = :paramProject";
    
    public Developermetrics(BundleContext bc) {
        super(bc);        
 
        super.addActivationType(ProjectFile.class);
        super.addActivationType(ProjectVersion.class);
        super.addMetricActivationType(MNEM_TEAMSIZE1, ProjectVersion.class);
        super.addMetricActivationType(MNEM_TEAMSIZE3, ProjectVersion.class);
        super.addMetricActivationType(MNEM_TEAMSIZE6, ProjectVersion.class);
        super.addMetricActivationType(MNEM_EYEBALL, ProjectFile.class);
        super.addMetricActivationType(MNEM_EYEBALL_MOD, ProjectFile.class);
    }
    
    public boolean install() {
        //This should always be called to run various init tasks
        boolean result = super.install();
        
        if (result) {
            result &= super.addSupportedMetrics(
                    "Active developers in the last 6 months",
                    MNEM_TEAMSIZE6,
                    MetricType.Type.PROJECT_WIDE);
            result &= super.addSupportedMetrics(
                    "Active developers in the last 3 months",
                    MNEM_TEAMSIZE3,
                    MetricType.Type.PROJECT_WIDE);
            result &= super.addSupportedMetrics(
                    "Active developers in the last 1 months",
                    MNEM_TEAMSIZE1,
                    MetricType.Type.PROJECT_WIDE);
            result &= super.addSupportedMetrics(
                    "Number of developers that have worked on the file",
                    MNEM_EYEBALL,
                    MetricType.Type.SOURCE_CODE);
            result &= super.addSupportedMetrics(
                    "Number of developers that have worked on the module",
                    MNEM_EYEBALL_MOD,
                    MetricType.Type.SOURCE_FOLDER);
        }
        return result;
    }

    public List<ResultEntry> getResult(ProjectVersion p, Metric m) {
        
        return getResult(p, m, ResultEntry.MIME_TYPE_TYPE_INTEGER);
    }

    public void run(ProjectVersion v) throws AlreadyProcessingException {
        long oneMonth = (long)(30 * 24 * 60 * 60 * 1000L);
        long threeMonths = (long)(90 * 24 * 60 * 60 * 1000L);
        long sixMonths = (long)(180 * 24 * 60 * 60 * 1000L);
        
        Metric m = Metric.getMetricByMnemonic(MNEM_TEAMSIZE1);
        ProjectVersionMeasurement pvmOne = new ProjectVersionMeasurement(
                m, v, String.valueOf(commSize(v, oneMonth)));
        db.addRecord(pvmOne);
        markEvaluation(m, v);
        
        m = Metric.getMetricByMnemonic(MNEM_TEAMSIZE3);
        ProjectVersionMeasurement pvmThree = new ProjectVersionMeasurement(
                m, v, String.valueOf(commSize(v, threeMonths)));
        db.addRecord(pvmThree);
        markEvaluation(m, v);
        
        m = Metric.getMetricByMnemonic(MNEM_TEAMSIZE6);
        ProjectVersionMeasurement pvmSix = new ProjectVersionMeasurement(
                m, v, String.valueOf(commSize(v, sixMonths)));
        db.addRecord(pvmSix);
        markEvaluation(m, v);
    }
    
    private long commSize(ProjectVersion v, long ts) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("paramTS", v.getTimestamp());
        params.put("paramOld", ts);
        params.put("paramProject", v.getProject());
        
        return (Long) db.doHQL(activeLast, params).get(0);
    }

    public List<ResultEntry> getResult(ProjectFile pf, Metric m) {
        return getResult(pf, m, ResultEntry.MIME_TYPE_TYPE_INTEGER);
    }

    public void run(ProjectFile a) throws AlreadyProcessingException {
        int eyeballs = 0;
        Metric m = null;
        if (a.getIsDirectory()) {
            List<ProjectFile> files = a.getProjectVersion().getFiles(
                    Directory.getDirectory(a.getFileName(), false), 
                    ProjectVersion.MASK_FILES);
            
            Set<Developer> distinctdevs = new HashSet<Developer>(); 
            
            for (ProjectFile pf : files) {
                distinctdevs.addAll(fileEyeballs(pf));
            }
            eyeballs = distinctdevs.size();
            m = Metric.getMetricByMnemonic(MNEM_EYEBALL_MOD);
        }
        else { 
            eyeballs = fileEyeballs(a).size();
            m = Metric.getMetricByMnemonic(MNEM_EYEBALL);
        }
        
        ProjectFileMeasurement pfm = new ProjectFileMeasurement(m, a, 
                String.valueOf(eyeballs));
        db.addRecord(pfm);
        markEvaluation(m, a);
    }

    private List<Developer> fileEyeballs(ProjectFile a) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("paramName", a.getName());
        params.put("paramDir", a.getDir());
        params.put("paramProject", a.getProjectVersion().getProject());
        params.put("paramFileId", a.getId());
        
        return (List<Developer>) db.doHQL(fileEyeballs, params);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
