package eu.sqooss.service.db;

/**
 * Instances of this class represent a measurement made against a
 * specific mail message, as stored in the database
 */
public class MailMessageMeasurement extends MetricMeasurement {
    /**
     * The thread against which the measurement was made
     */ 
    private MailMessage mail;
    
    public MailMessageMeasurement() {
        super();
    }

    /**
     * Convenience constructor to avoid having to call three methods
     * to set up sensible values in a measurement.
     * 
     * @param m Metric this measurement is from
     * @param mail Mail message this measurement is for
     * @param value (String) value representation of the measurement
     */
    public MailMessageMeasurement(Metric m, MailMessage mail, String value) {
        super();
        setMetric(m);
        setMail(mail);
        setResult(value);
    }

    public MailMessage getMail() {
        return mail;
    }

    public void setMail(MailMessage mail) {
        this.mail = mail;
    }
    
   
}
