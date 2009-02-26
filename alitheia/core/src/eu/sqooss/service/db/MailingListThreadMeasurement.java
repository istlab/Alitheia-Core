package eu.sqooss.service.db;

/**
 * Instances of this class represent a measurement made against a
 * specific mailing list thread, as stored in the database
 */
public class MailingListThreadMeasurement extends MetricMeasurement {
    /**
     * The thread against which the measurement was made
     */ 
    private MailingListThread thread;
    
    public MailingListThreadMeasurement() {
        super();
    }

    /**
     * Convenience constructor to avoid having to call three methods
     * to set up sensible values in a measurement.
     * 
     * @param m Metric this measurement is from
     * @param mt Thread this measurement is for
     * @param value (String) value representation of the measurement
     */
    public MailingListThreadMeasurement(Metric m, MailingListThread mt, String value) {
        super();
        setMetric(m);
        setThread(mt);
        setResult(value);
    }
    
    public MailingListThread getThread() {
        return thread;
    }

    public void setThread(MailingListThread thread) {
        this.thread = thread;
    }
}
