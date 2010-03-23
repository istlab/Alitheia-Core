/*
 * This file is part of the Alitheia system.
 *
 * Copyright 2009 - 2010 - Organization for Free and Open Source Software,  
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
package eu.sqooss.metrics.discussionheat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.AlreadyProcessingException;
import eu.sqooss.service.abstractmetric.MetricDecl;
import eu.sqooss.service.abstractmetric.MetricDeclarations;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailingListThread;
import eu.sqooss.service.db.MailingListThreadMeasurement;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.StoredProject;

/**
 * Discussion heat plug-in. 
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */ 
@MetricDeclarations(metrics={
    @MetricDecl(mnemonic="VERLOC", activators={ProjectVersion.class}, descr="Locs changed in version", dependencies={"Wc.loc"}),
    @MetricDecl(mnemonic="HOTNESS", activators={MailingListThread.class}, descr="Hotness level"),
    @MetricDecl(mnemonic="HOTEFFECT", activators={MailingListThread.class}, descr="+/- loc change rate due to hot discussion")
})
public class DiscussionHeat extends AbstractMetric {
    
    private static final String thrDepth = "select distinct m.depth" +
    		" from MailMessage m, MailingListThread mt " +
    		" where mt.list = :lst " +
    		" and m.thread = mt order by m.depth";
    
    private static final String numMails = "select distinct count(mm) " +
    		"from MailMessage mm, MailingListThread mt " +
    		"where mm.thread = mt " +
    		"and mt.list = :lst " +
    		"group by mt " +
    		"order by count (mm)";
    
    private static final String getPVbyDate = "select pv " +
    		"from ProjectVersion pv " +
    		"where pv.timestamp <= :ts " +
    		"and pv.project = :sp " +
    		"order by pv.sequence desc";
    
    private DBService dbs;
    
    public DiscussionHeat(BundleContext bc) {
        super(bc);        
        dbs = AlitheiaCore.getInstance().getDBService();
    }

    public List<ResultEntry> getResult(MailingListThread mt, Metric m) {
        return getResult(mt, m, ResultEntry.MIME_TYPE_TYPE_INTEGER);
    }
    
    public List<ResultEntry> getResult(ProjectVersion pv, Metric m) {
        return getResult(pv, m, ResultEntry.MIME_TYPE_TYPE_INTEGER);
    }
    
    public void run(MailingListThread m) throws AlreadyProcessingException {
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("lst", m.getList());
        
        List<Integer> thrDepths = (List<Integer>)db.doHQL(thrDepth, params);
        List<Long> mailsPerList = (List<Long>)db.doHQL(numMails, params);
        
        //Get one day's worth of messages
        List<MailMessage> msgs = m.getMessagesByArrivalOrder();
        List<MailMessage> oneDayMsgs = new ArrayList<MailMessage>();
        int depth = 0; MailMessage first = null;
        
        for (MailMessage msg : msgs) {
            if (first != null) {
                if (msg.getSendDate().getTime() - 
                        first.getSendDate().getTime() < (24L * 3600 * 1000)) {
                    oneDayMsgs.add(msg);
                } else {
                    break;
                }
            } else {
                first = msg;
                oneDayMsgs.add(msg);
            }
        }
        
        int score = getQuartile(thrDepths, depth) 
                + getQuartile(mailsPerList, oneDayMsgs.size());
        
        Metric hotness = Metric.getMetricByMnemonic("HOTNESS");
        
        MailingListThreadMeasurement mm = new MailingListThreadMeasurement(
                hotness, m, String.valueOf(score));
        
        dbs.addRecord(mm);
        
        if (score < 6)
            return;
        
        //Get the version closest to thread start
        ProjectVersion pv = getVersionByDate(m.getStartingEmail().getSendDate(), 
                m.getList().getStoredProject());
        
        int locsLastMonth = getLocsForVersions(getPreviousMonthVersions(pv));
        int locsNextWeek = getLocsForVersions(getNextWeekVersions(pv));
        
        int result = (locsLastMonth/30) - (locsNextWeek/7);
        
        Metric hoteffect = Metric.getMetricByMnemonic("HOTEFFECT");
        MailingListThreadMeasurement mltm = new MailingListThreadMeasurement(
                hoteffect, m, String.valueOf(result));
        
        dbs.addRecord(mltm);
    }
    
    private int getLocsForVersions(List<ProjectVersion> versions) throws AlreadyProcessingException {
        Metric metric = Metric.getMetricByMnemonic("VERLOC");
        List<Metric> metricList = new ArrayList<Metric>();
        metricList.add(metric);
        int result = 0;
        try {
            for (ProjectVersion version : versions) {
                Result r = getResult(version, metricList);
                if (r != null) {
                    result += r.getRow(0).get(0).getInteger();
                }
            }
        } catch (MetricMismatchException e) {
            e.printStackTrace();
        } catch (AlreadyProcessingException ape) {
            throw ape;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private List<ProjectVersion> getPreviousMonthVersions(ProjectVersion pv) {
        List<ProjectVersion> versions = new ArrayList<ProjectVersion>();
        versions.add(pv);
        ProjectVersion prev = pv.getPreviousVersion();
        long monthsecs = 3600 * 24 * 30;
        while (true) {
            //Diff in seconds
            long diff = (pv.getTimestamp() - prev.getTimestamp()) / 1000;
            if (prev != null && diff <  monthsecs ) {
                versions.add(prev);
            } else {
                break;
            }
            prev = prev.getPreviousVersion();
        }
        return versions;
    }
    
    private List<ProjectVersion> getNextWeekVersions(ProjectVersion pv) {
        List<ProjectVersion> versions = new ArrayList<ProjectVersion>();
        ProjectVersion next = pv.getNextVersion();
        long weeksecs = 3600 * 24 * 7;
        while (true) {
            long diff = (next.getTimestamp() - pv.getTimestamp()) / 1000;
            if (next != null && diff < weeksecs) {
                versions.add(next);
            } else {
                break;
            }
            next = next.getNextVersion();
        }
        
        return versions;
    }
    
    private ProjectVersion getVersionByDate(Date ts, StoredProject sp) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ts", ts.getTime());
        params.put("sp", sp);
        List<ProjectVersion> vers = (List<ProjectVersion>) dbs.doHQL(getPVbyDate, params, 1);
        
        if (vers.size() > 0)
            return vers.get(0);
        
        return null;
    }
    
    private int getQuartile(List<? extends Number> distrib, int num) {
        int median = distrib.size() / 2;
        int quart3 = median + ((distrib.size() - median)/2);
        int quart1 = median - (median/2);
        
        if (num > distrib.get(quart3).intValue())
            return 4;
        if (num > distrib.get(median).intValue())
            return 3;
        if (num > distrib.get(quart1).intValue())
            return 2;
        
        return 1;
    }

    public void run(ProjectVersion pv) throws AlreadyProcessingException {
        Metric m = Metric.getMetricByMnemonic("VERLOC");
        List<Metric> locMetric = new ArrayList<Metric>();
        AlitheiaPlugin plugin = AlitheiaCore.getInstance().getPluginAdmin().getImplementingPlugin("Wc.loc");
        
        if (plugin != null) {
            locMetric.add(Metric.getMetricByMnemonic("Wc.loc"));
        } else {
            return;
        }
        
        int linesChanged = 0;
        try {
            // Get difference in number of lines for all file changes
            for (ProjectFile pf : pv.getVersionFiles()) {
                if (pf.getIsDirectory())
                    continue;
                if (pf.isDeleted()) {
                    linesChanged += getLOCResult(pf.getPreviousFileVersion(),
                            plugin, locMetric);
                } else if (pf.isAdded()) {
                    linesChanged += getLOCResult(pf, plugin, locMetric);
                } else { // MODIFIED or REPLACED
                    linesChanged += Math.abs(
                            getLOCResult(pf, plugin, locMetric)
                            - getLOCResult(pf.getPreviousFileVersion(),
                            plugin, locMetric));
                }
            }
        } catch (MetricMismatchException e) {
            e.printStackTrace();
        } catch (AlreadyProcessingException ape) {
            throw ape;
        }
          catch (Exception e) {
            e.printStackTrace();
        }

        ProjectVersionMeasurement pvm = new ProjectVersionMeasurement(m, pv,
                String.valueOf(linesChanged));

        dbs.addRecord(pvm);
    }
    
    private int getLOCResult(ProjectFile pf, AlitheiaPlugin plugin, 
            List<Metric> locMetric) 
        throws MetricMismatchException, AlreadyProcessingException, Exception {
      //Get lines of current version of the file from the wc metric
        Result r = plugin.getResult(pf, locMetric);
        if (r != null && r.hasNext()) {
            return r.getRow(0).get(0).getInteger();
        }
        else {
            return 0;
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

