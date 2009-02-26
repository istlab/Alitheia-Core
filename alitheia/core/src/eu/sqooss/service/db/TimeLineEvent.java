package eu.sqooss.service.db;

import java.util.Date;

/**
 * An event in a project's time line.
 */
public class TimeLineEvent extends DAObject {
    
    private long sequenceNum;
    private Date timestamp;
    private StoredProject project;
    private long eventId;
    private TimeLineEventType type;
    
    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public TimeLineEventType getType() {
        return type;
    }

    public void setType(TimeLineEventType type) {
        this.type = type;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public StoredProject getProject() {
        return project;
    }

    public void setProject(StoredProject sp) {
        this.project = sp;
    }
    
    public long getSequenceNum() {
        return sequenceNum;
    }
    
    public void setSequenceNum(long sequenceNumber) {
        this.sequenceNum = sequenceNumber;
    }
    
    public static void addTimeLineEvent(ProjectVersion pv) {
        addEvent(pv.getProject(), pv.getDate(), pv.getId());
    }
    
    public static void addTimeLineEvent(MailMessage m) {
        addEvent(m.getList().getStoredProject(), m.getSendDate(), m.getId());
    }
    
    public static void addTimeLineEvent(Bug b) {
        addEvent(b.getProject(), b.getCreationTS(), b.getId());
    }
    
    private static void addEvent(StoredProject sp, Date timestamp, long eventId) {
        
    }
}
