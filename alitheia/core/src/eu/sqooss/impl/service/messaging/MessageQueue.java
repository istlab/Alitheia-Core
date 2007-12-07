package eu.sqooss.impl.service.messaging;

import java.util.Vector;

import eu.sqooss.service.messaging.MessagingService;

/**
 * This class represents the message queue.
 * The messages in the queue have the <code>Message.STATUS_QUEUED</code> status.
 */
public class MessageQueue {

    private Vector < MessageImpl > vector;
    private Object lockObject = new Object();
    private boolean clear;

    public MessageQueue() {
        vector = new Vector < MessageImpl >();
        clear = false;
    }

    /**
     * Inserts the message to the end of the queue.
     * @param message
     */
    public void push(MessageImpl message) {
        synchronized (lockObject) {
            vector.addElement(message);
            lockObject.notifyAll();
        }
        MessagingServiceImpl.log("The message (id = " + message.getId() + ") is in the queue!",
                MessagingService.LOGGING_INFO_LEVEL);
    }

    /**
     * Removes the message from the beginning of the queue.
     */
    public MessageImpl pop() {
        synchronized (lockObject) {
            try {
                while (isEmpty() && !clear) {
                    lockObject.wait();
                }
                if (clear) {
                    return null;
                } else {
                    MessageImpl message = (MessageImpl)vector.remove(0);
                    MessagingServiceImpl.log("The message (id = " + message.getId() + ") is removed from the queue!",
                    		MessagingService.LOGGING_INFO_LEVEL);
                    return message;
                }
            } catch (InterruptedException ie) {
            	MessagingServiceImpl.log(ie.getMessage(), MessagingService.LOGGING_WARNING_LEVEL);
                throw new RuntimeException(ie);
            }
        }
    }

    /**
     * @return <code>true</code> - if the queue is empty, <code>false</code> - otherwise
     */
    public boolean isEmpty() {
        synchronized (lockObject) {
            return vector.isEmpty();
        }
    }

    /**
     * Removes all elements from the queue.
     */
    public void clearQueue(){
        synchronized (lockObject) {
            vector.removeAllElements();
            clear = true;
            lockObject.notifyAll();
        }
    }

    /**
     * @return The number of the elements.
     */
    public int size() {
        synchronized (lockObject) {
            return vector.size();
        }
    }

}
