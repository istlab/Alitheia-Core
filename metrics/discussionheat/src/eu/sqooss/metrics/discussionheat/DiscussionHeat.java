package eu.sqooss.metrics.discussionheat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlreadyProcessingException;
import eu.sqooss.service.abstractmetric.MailingListThreadMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MailingListThread;
import eu.sqooss.service.db.MailingListThreadMeasurement;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;

/**
 * Discussion heat plug-in. 
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */ 
public class DiscussionHeat extends AbstractMetric implements 
    MailingListThreadMetric {
    
    private static final String thrDepth = "select distinct m.depth" +
    		" from MailMessage m, MailingListThread mt " +
    		" where mt.list.storedProject = :sp " +
    		" and m.thread = mt order by m.depth";
    
    private static final String numMails = "select distinct count(mm) " +
    		"from MailMessage mm, MailingListThread mt " +
    		"where mm.thread = mt " +
    		"and mt.list.storedProject = :sp " +
    		"group by mt " +
    		"order by count (mm)";
    
    public DiscussionHeat(BundleContext bc) {
        super(bc);        
 
        super.addActivationType(MailingListThread.class);
        super.addMetricActivationType("HOTNESS", MailingListThread.class);
    }
    
    public boolean install() {
        boolean result = super.install();

        result &= super.addSupportedMetrics("Hotness level",
                "HOTNESS", MetricType.Type.THREAD);
        return result;
    }

    public List<ResultEntry> getResult(MailingListThread mt, Metric m) {
        return getResult(mt, m, ResultEntry.MIME_TYPE_TYPE_INTEGER);
    }

    public void run(MailingListThread m) throws AlreadyProcessingException {
        
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sp", m.getList().getStoredProject());
        
        List<Integer> thrDepths = (List<Integer>)db.doHQL(thrDepth, params);
        List<Long> mailsPerList = (List<Long>)db.doHQL(numMails, params);
        
        int score = getQuartile(thrDepths, m.getThreadDepth()) 
                + getQuartile(mailsPerList, m.getMessages().size());
        
        Metric hotness = Metric.getMetricByMnemonic("HOTNESS");
        
        MailingListThreadMeasurement mm = new MailingListThreadMeasurement(
                hotness, m, String.valueOf(score));
        
        dbs.addRecord(mm);
        
        markEvaluation(Metric.getMetricByMnemonic("HOTNESS"),
                m.getList().getStoredProject());        
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
}

// vi: ai nosi sw=4 ts=4 expandtab

