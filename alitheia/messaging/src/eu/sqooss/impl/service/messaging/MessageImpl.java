package eu.sqooss.impl.service.messaging;

import java.util.Vector;

import eu.sqooss.services.messaging.Message;

public class MessageImpl extends Message {

  private int status;
  private long id;
  private long queueTime;
  
  private String body;
  private Vector recipients;
  private String title;
  private String protocol;
  
  public MessageImpl(String body, Vector recipients, String title, String protocol) {
    setBody(body);
    setRecipients(recipients);
    setTitle(title);
    setProtocol(protocol);
    
    this.status = STATUS_NEW;
    this.id = 0;
  }

  public String getBody() {
    return body;
  }

  public long getId() {
    return id;
  }

  public String getProtocol() {
    return protocol;
  }

  public Vector getRecipients() {
    return recipients;
  }

  public int getStatus() {
    return status;
  }

  public String getTitle() {
    return title;
  }

  public void setBody(String body) {
    if (body == null) {
      throw new NullPointerException("The message's body is null!");
    }
    if (body.trim().equals("")) {
      throw new IllegalArgumentException("The message's body is empty!");
    }
    this.body = body;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

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

  public void setTitle(String title) {
    if (title == null) {
      throw new NullPointerException("The message's title is null!");
    }
    if (title.trim().equals("")) {
      throw new IllegalArgumentException("The message's title is empty!");
    }
    this.title = title;
  }
  
  public void setStatus(int status) {
    if ((status != Message.STATUS_SENT) && (status != Message.STATUS_QUEUED) && 
        (status != Message.STATUS_FAILED)) {
      throw new IllegalArgumentException("Invalid message status: " + status);
    }
    this.status = status;
  }
  
  public void setId(long id) {
    this.id = id;
  }
  
  public void setQueueTime(long time) {
    this.queueTime = time;
  }
  
  public long getQueueTime() {
    return queueTime;
  }

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
  
}
