package eu.sqooss.impl.service.messaging;

import java.util.Hashtable;

import eu.sqooss.impl.service.MessagingActivator;
import eu.sqooss.impl.service.messaging.timer.Timer;
import eu.sqooss.service.messaging.Message;

/**
 * This class is used to store the messages for future reference.
 * It stores the messages for a configurable amount of time. 
 */
public class MessageHistory {

    private static final String MESSAGE_HISTORY_TIMER_NAME = "Message history timer ";

    private Object lockObject = new Object();

    private Timer timer;
    private Hashtable < Long, MessageImpl > messageHistory;
    private long preservingTime;

    public MessageHistory(long preservingTime) {
        this.preservingTime = preservingTime;
        messageHistory = new Hashtable < Long, MessageImpl >();
        timer = new Timer(MESSAGE_HISTORY_TIMER_NAME);
        timer.start();
    }

    /**
     * Stores a new message.
     * @param message
     */
    public void put(MessageImpl message) {
        if (preservingTime != 0) {
            synchronized (lockObject) {
                messageHistory.put(new Long(message.getId()), message);
                timer.addNotifyListener(message, preservingTime);
            }
        }
        MessagingActivator.log("The message (id = " + message.getId() + ") is stored!", MessagingActivator.LOGGING_INFO_LEVEL);
    }

    /**
     * Returns the stored message with specified id.
     * @param messageId
     * @return the message to which the id is mapped in this message history; null if the id is not mapped.
     */
    public Message getMessage(long messageId) {
        return (Message)messageHistory.get(new Long(messageId));
    }

    /**
     * Removes the message from the message history.
     * @param messageId
     * @return <code>false</code> - if the id is not mapped to the message in the message history,
     * <code>true</code> - otherwise
     */
    public boolean removeMessage(long messageId) {
        synchronized (lockObject) {
            if (messageHistory.remove(new Long(messageId)) == null) {
                MessagingActivator.log("The message (id = " + messageId + ") isn't stored!", MessagingActivator.LOGGING_INFO_LEVEL);
                return false;
            } else {
                MessagingActivator.log("The message (id = " + messageId + ") is removed!", MessagingActivator.LOGGING_INFO_LEVEL);
                return true;
            }
        }
    }

    /**
     * Clears the history.
     */
    public void clear() {
        synchronized (lockObject) {
            timer.stop();
            messageHistory.clear();
        }
    }

    public void setPreservingTime(long preservingTime) {
        this.preservingTime = preservingTime;
    }

}
