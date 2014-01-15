/*
 * Copyright 2009 - 2010 Organization for Free and Open Source Software,  
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
import eu.sqooss.service.abstractmetric.MetricDecl;
import eu.sqooss.service.abstractmetric.MetricDeclarations;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.abstractmetric.Result.ResultType;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectDirectory;
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
@MetricDeclarations(metrics= {
		@MetricDecl(mnemonic="TEAMSIZE1", activators={ProjectVersion.class}, descr="Active developers in the last 1 month"),
		@MetricDecl(mnemonic="TEAMSIZE3", activators={ProjectVersion.class}, descr="Active developers in the last 3 month"),
		@MetricDecl(mnemonic="TEAMSIZE6", activators={ProjectVersion.class}, descr="Active developers in the last 6 month"),
		@MetricDecl(mnemonic="EYBALL", activators={ProjectFile.class}, descr="Number of developers that worked on a file"),
		@MetricDecl(mnemonic="MODEYBALL", activators={ProjectDirectory.class}, descr="Number of developers that worked on a module")
	})
public class Developermetrics extends AbstractMetric {
    
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
    }

    public List<Result> getResult(ProjectVersion pv, Metric m) {
        return getResult(pv, ProjectVersionMeasurement.class, m, ResultType.INTEGER);
    }

    public void run(ProjectVersion v) throws AlreadyProcessingException {
        long oneMonth = (long)(30 * 24 * 60 * 60 * 1000L);
        long threeMonths = (long)(90 * 24 * 60 * 60 * 1000L);
        long sixMonths = (long)(180 * 24 * 60 * 60 * 1000L);
        
        runForPeriod(oneMonth, MNEM_TEAMSIZE1, v);
        runForPeriod(threeMonths, MNEM_TEAMSIZE3, v);
        runForPeriod(sixMonths, MNEM_TEAMSIZE6, v);
    }
    
    private void runForPeriod(long monthCount, String TeamIdentifier, ProjectVersion v )
    {
    	 Metric m = Metric.getMetricByMnemonic(TeamIdentifier);
         ProjectVersionMeasurement pvm = new ProjectVersionMeasurement(m, v, String.valueOf(commSize(v, monthCount)));
         db.addRecord(pvm);
    }
    
    private long commSize(ProjectVersion v, long ts) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("paramTS", v.getTimestamp());
        params.put("paramOld", ts);
        params.put("paramProject", v.getProject());
        
        return (Long) db.doHQL(activeLast, params).get(0);
    }

    public List<Result> getResult(ProjectFile pf, Metric m) {
        return getResult(pf, ProjectFileMeasurement.class, m, ResultType.INTEGER);
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
