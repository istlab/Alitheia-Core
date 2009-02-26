package eu.sqooss.metrics.discussionheat;

import java.util.List;

import org.osgi.framework.BundleContext;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlreadyProcessingException;
import eu.sqooss.service.abstractmetric.MailMetric;
import eu.sqooss.service.abstractmetric.MailingListThreadMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.abstractmetric.StoredProjectMetric;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailingListThread;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.StoredProject;

/**
 * Discussion heat plug-in. 
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */ 
public class DiscussionHeat extends AbstractMetric implements 
    MailingListThreadMetric, MailMetric, StoredProjectMetric {
    
    public DiscussionHeat(BundleContext bc) {
        super(bc);        
 
        super.addActivationType(MailingListThread.class);
        super.addActivationType(MailMessage.class);
        
        super.addMetricActivationType("THRDPT1", StoredProject.class);
        super.addMetricActivationType("THRDPTM", StoredProject.class);
        super.addMetricActivationType("THRDPT3", StoredProject.class);

        super.addMetricActivationType("MSGTRH1", StoredProject.class);
        super.addMetricActivationType("MSGTHRM", StoredProject.class);
        super.addMetricActivationType("MSGTRH3", StoredProject.class);
        
        super.addMetricActivationType("HOTNESSLVL", MailingListThread.class);
    }
    
    public boolean install() {
        boolean result = super.install();
        result &= super.addSupportedMetrics("Thread Depth - 1rst Quartile",
                "THRDPT1", MetricType.Type.PROJECT_WIDE);
        result &= super.addSupportedMetrics("Thread Depth - Median",
                "THRDPTM", MetricType.Type.PROJECT_WIDE);
        result &= super.addSupportedMetrics("Thread Depth - 3rd Quartile",
                "THRDPTM", MetricType.Type.PROJECT_WIDE);
        result &= super.addSupportedMetrics("Messages Per Thread - 1rst Quartile",
                "MSGTRH1", MetricType.Type.PROJECT_WIDE);
        result &= super.addSupportedMetrics("Messages Per Thread - Median",
                "MSGTHRM", MetricType.Type.PROJECT_WIDE);
        result &= super.addSupportedMetrics("Messages Per Thread - 3rd Quartile",
                "MSGTRH3", MetricType.Type.PROJECT_WIDE);
        result &= super.addSupportedMetrics("Hotness level",
                "HOTNESSLVL", MetricType.Type.THREAD);
        return result;
    }

    public List<ResultEntry> getResult(MailingListThread mt, Metric m) {
        return getResult(mt, m, ResultEntry.MIME_TYPE_TYPE_DOUBLE);
    }

    public void run(MailingListThread m) throws AlreadyProcessingException {
        
    }

    public List<ResultEntry> getResult(MailMessage mm, Metric m) {
        return getResult(mm, m, ResultEntry.MIME_TYPE_TYPE_INTEGER);
    }

    public void run(MailMessage m) throws AlreadyProcessingException {
        //double thrDepthQ1 = getResult(m.getList().getStoredProject(), Metric.getMetricByMnemonic("THRDPT1"));
    }

    public List<ResultEntry> getResult(StoredProject s, Metric m) {
        return getResult(s, m, ResultEntry.MIME_TYPE_TYPE_DOUBLE);
    }

    public void run(StoredProject a) throws AlreadyProcessingException {
        
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

