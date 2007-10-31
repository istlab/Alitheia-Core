package eu.sqooss.impl.service.messaging;

import java.util.Vector;

import eu.sqooss.impl.service.messaging.timer.TimerListener;
import eu.sqooss.service.messaging.Message;

/**
 * This class extends the <code>Message</code> class and implements the <code>TimerListener</code> interface.
 * The messages can be stored for a configurable amount of time. 
 */
public class MessageImpl extends Message implements TimerListener {

    private int status;
    private long id;
    private long queueTime;

    private String body;
    private Vector recipients;
    private String title;
    private String protocol;
    private MessageHistory messageHistory;

    public MessageImpl(String body, Vector recipients, String title, String protocol) {
        setBody(body);
        setRecipients(recipients);
        setTitle(title);
        setProtocol(protocol);

        this.status = STATUS_NEW;
        this.id = 0;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#getBody()
     */
    public String getBody() {
        return body;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#getId()
     */
    public long getId() {
        return id;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#getProtocol()
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#getRecipients()
     */
    public Vector getRecipients() {
        return recipients;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#getStatus()
     */
    public int getStatus() {
        return status;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#getTitle()
     */
    public String getTitle() {
        return title;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#getBody()
     */
    public void setBody(String body) {
        if (body == null) {
            throw new NullPointerException("The message's body is null!");
        }
        if (body.trim().equals("")) {
            throw new IllegalArgumentException("The message's body is empty!");
        }
        this.body = body;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#setProtocol(java.lang.String)
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#setRecipients(java.util.Vector)
     */
    public void setRecipients(Vector recipients) {
        if (recipients == null) {
            throw new NullPointerException("The recipients vector is null!");
        }
        if (recipients.contains(null)) {
            throw new NullPointerException("The recipients vector contains null recipient!");
        }
        if (recipients.size() == 0) {
            throw new IllegalArgumentException("The recipients vector is empty!");
        }
        this.recipients = recipients;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#setTitle(java.lang.String)
     */
    public void setTitle(String title) {
        if (title == null) {
            throw new NullPointerException("The message's title is null!");
        }
        if (title.trim().equals("")) {
            throw new IllegalArgumentException("The message's title is empty!");
        }
        this.title = title;
    }

    /**
     * Sets the message status. The message status can be:
     * <ul>
     * <li> <code>Message.STATUS_SENT</code>
     * <li> <code>Message.STATUS_QUEUED</code>
     * <li> <code>Message.STATUS_FAILED</code>
     * </ul>
     * 
     * @param status
     * 
     * @exception IllegalArgumentException - if the message status is not correct
     */
    public void setStatus(int status) {
        if ((status != Message.STATUS_SENT) && (status != Message.STATUS_QUEUED) && 
            (status != Message.STATUS_FAILED)) {
            throw new IllegalArgumentException("Invalid message status: " + status);
        }
        this.status = status;
    }

    /**
     * Sets the message identifier.
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Sets the queuering time of the message.
     * @param time
     */
    public void setQueueTime(long time) {
        this.queueTime = time;
    }

    /**
     * @return the queuering time of the message
     */
    public long getQueueTime() {
        return queueTime;
    }

    /**
     * Two <code>MessageImpl</code> objects are equal if their identifiers are equal.
     * If some of the identifiers is 0 then references are compared.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof MessageImpl)) {
            return false;
        }
        MessageImpl message = (MessageImpl)obj;
        if ((this.id == 0) || (message.id == 0)){
            return this == message;
        }
        return this.id == message.id;
    }

    public int hashCode() {
        if (id == 0) {
            return super.hashCode();
        } else {
            return new Long(id).hashCode();
        }
    }

    /* Message history methods */

    public void setMessageHistory(MessageHistory messageHistory) {
        this.messageHistory = messageHistory;
    }

    /**
     * @see eu.sqooss.impl.service.messaging.timer.TimerListener#timer()
     */
    public void timer() {
        if (messageHistory != null) {
            messageHistory.removeMessage(id);
        }
    }

    /* Message history methods */

}
