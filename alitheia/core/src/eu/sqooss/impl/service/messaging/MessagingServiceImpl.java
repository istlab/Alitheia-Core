package eu.sqooss.impl.service.messaging;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.messaging.senders.smtp.SMTPSender;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.messaging.Message;
import eu.sqooss.service.messaging.MessageListener;
import eu.sqooss.service.messaging.MessagingService;
import eu.sqooss.service.messaging.sender.MessageSender;

/**
 * This class implements the <code>MessagingService</code> interface.
 */
public class MessagingServiceImpl implements MessagingService {

    private Object lockObjectListeners = new Object();
    private Object lockObjectThreads = new Object();

    private long queueringTime;
    private int maxThreadsNumber;
    private long id;
    private Properties properties;
    private Vector < MessageListener > messageListeners;
    private MessageHistory messageHistory;
    private MessageQueue messageQueue;
    private Vector < MessagingServiceThread > messagingThreads;
    private BundleContext bc;
    private SMTPSender defaultSender;
	private ServiceRegistration sRegSMTPSenderService;
	private static Logger logger;

    public MessagingServiceImpl(BundleContext bc){
         SMTPSender defaultSender = new SMTPSender(bc);
         Properties serviceProps = new Properties();
         serviceProps.setProperty(MessageSender.PROTOCOL_PROPERTY, SMTPSender.PROTOCOL_PROPERTY_VALUE);
        
         this.id = readId(bc);
         this.bc = bc;
         this.defaultSender = defaultSender;
         this.maxThreadsNumber = 10; //default value
         this.queueringTime = 60*1000; //default value
         this.properties = new Properties();
         initProperties(bc);
         messageListeners = new Vector < MessageListener >();
         messageHistory = new MessageHistory(0);
         messageQueue = new MessageQueue();
         messagingThreads = new Vector < MessagingServiceThread >();
         startThreadIfNeeded();
         
         ServiceReference coreRef = bc.getServiceReference(AlitheiaCore.class.getName());
         if (logger == null && coreRef != null)
         {
        	 LogManager logManager = ((AlitheiaCore)bc.getService(coreRef)).getLogManager();
        	 logger = logManager.createLogger(Logger.NAME_SQOOSS_MESSAGING);
         }
    }

    /**
     * Reads the last message id from the file.
     */
    private long readId(BundleContext bc)  {
        File idFile = bc.getDataFile(MessagingConstants.FILE_NAME_MESSAGE_ID);
        DataInputStream in = null;
        if (idFile.exists()) {
        	long result = 0;
            try {
                in = new DataInputStream(new FileInputStream(idFile));
                result = in.readLong();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
                result = 0;
            }
            return result;
        } else {
            return 1;
        }
    }


    /**
     * @see eu.sqooss.service.messaging.MessagingService#sendMessage(Message)
     */
    public void sendMessage(Message message) {
        if (message == null) {
            throw new NullPointerException("The message is null!");
        }
        if (!(message instanceof MessageImpl)) {
            throw new IllegalArgumentException("The message must be created with the Message.getInstance method!");
        }
        MessageImpl createdMessage = (MessageImpl)message;
        createdMessage.setStatus(Message.STATUS_QUEUED);
        createdMessage.setId(getNextId());
        createdMessage.setQueueTime(System.currentTimeMillis());
        validateMessage(message);
        messageHistory.put(createdMessage);
        messageQueue.push(createdMessage);
        notifyListeners(createdMessage, Message.STATUS_QUEUED);
    }

    /**
     * @see eu.sqooss.service.messaging.MessagingService#addMessageListener(MessageListener)
     */
    public void addMessageListener(MessageListener listener) {
        synchronized (lockObjectListeners) {
            messageListeners.addElement(listener);
        }
    }

    /**
     * @see eu.sqooss.service.messaging.MessagingService#removeMessageListener(MessageListener)
     */
    public boolean removeMessageListener(MessageListener listener) {
        synchronized (lockObjectListeners) {
            return messageListeners.removeElement(listener);
        }
    }

    /**
     * @see eu.sqooss.service.messaging.MessagingService#getConfigurationProperty(String)
     */
    public String getConfigurationProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * @see eu.sqooss.service.messaging.MessagingService#getConfigurationKeys()
     */
    public String[] getConfigurationKeys() {
        return MessagingConstants.KEYS;
    }

    /**
     * @see eu.sqooss.service.messaging.MessagingService#setConfigurationProperty(String, String)
     */
    public void setConfigurationProperty(String key, String value) {
        log("Set a configuration property: key=" + key + ", value=" + value,
                MessagingService.LOGGING_INFO_LEVEL);
        if (key.equals(MessagingConstants.KEY_QUEUERING_TIME)) {
            try {
                long qTime = Long.parseLong(value);
                if (qTime > 0) {
                    properties.setProperty(key, value);
                    queueringTime = qTime;
                    refreshThreadsQueueringTime(qTime);
                } else {
                    throw new IllegalArgumentException("The queuering time must be positive!");
                }
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("The string value does not contain a parsable long!");
            }
        } else if (key.equals(MessagingConstants.KEY_THREAD_FACTOR)) {
            try {
                int threadFactor = Integer.parseInt(value);
                if (threadFactor > 0) {
                    properties.setProperty(key, value);
                    MessagingServiceThread.threadFactor = threadFactor;
                } else {
                    throw new IllegalArgumentException("The thread's factor must be positive!");
                }
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("The string value does not contain a parsable int!");
            }
        } else if (key.equals(MessagingConstants.KEY_MAX_THREADS_NUMBER)) {
            try {
                int maxThreads = Integer.parseInt(value);
                if (maxThreads > 0) {
                    properties.setProperty(key, value);
                    maxThreadsNumber = maxThreads;
                } else {
                    throw new IllegalArgumentException("The threads number must be positive!");
                }
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("The string value does not contain a parsable int!");
            }
        } else if (key.equals(MessagingConstants.KEY_MESSAGE_PRESERVING_TIME)) {
            try {
                long preservingTime = Long.parseLong(value);
                if (preservingTime >= 0) {
                    properties.setProperty(key, value);
                    messageHistory.setPreservingTime(preservingTime);
                } else {
                    throw new IllegalArgumentException("The preserving time must not be negative!");
                }
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("The string value does not contain a parsable long!");
            }
        } else if (key.equals(MessagingConstants.KEY_SMTP_HOST)) {
            properties.setProperty(key, value);
            defaultSender.setSessionHost(value);
        } else if (key.equals(MessagingConstants.KEY_SMTP_PORT)) {
            properties.setProperty(key, value);
            defaultSender.setSessionPort(value);
        } else if (key.equals(MessagingConstants.KEY_SMTP_REPLY)) {
            properties.setProperty(key, value);
            defaultSender.setReply(value);
        } else if (key.equals(MessagingConstants.KEY_SMTP_USER)) {
            properties.setProperty(key, value);
            defaultSender.setSessionUser(value);
        } else if (key.equals(MessagingConstants.KEY_SMTP_PASS)) {
            properties.setProperty(key, value);
            defaultSender.setSessionPass(value);
        } else if (key.equals(MessagingConstants.KEY_SMTP_TIMEOUT)) {
            try {
                long timeout = Long.parseLong(value);
                if (timeout > 0) {
                    defaultSender.setSessionTimeout(timeout);
                    properties.setProperty(key, value);
                } else {
                    throw new IllegalArgumentException("The timeout must be positive!");
                }
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("The string value does not contain a parsable long!");
            }
        } else {
            properties.setProperty(key, value);
        }
    }

    /**
     * This method: stops the all message threads;
     * clears the message queue, listeners and message history; saves the settings
     *
     * @return the last message id
     */
    public long stopService() {
        synchronized (lockObjectThreads) {
            MessagingServiceThread messThread;
            for (int i = 0; i < messagingThreads.size(); i++) {
                messThread = (MessagingServiceThread)messagingThreads.elementAt(i);
                messThread.stop(true);
            }
            messagingThreads.removeAllElements();
        }
        messageQueue.clearQueue();
        messageListeners.removeAllElements();
        messageHistory.clear();
        try {
            properties.store(new FileOutputStream(bc.getDataFile(MessagingConstants.FILE_NAME_PROPERTIES)), null);
        } catch (IOException ioe) {
            log("An error occurs while saving the properties: " + ioe.getMessage(),
            		MessagingService.LOGGING_WARNING_LEVEL);
        }
        return id;
    }

    /**
     * This method notifies the all listeners.
     * @param message
     * @param status
     */
    public void notifyListeners(Message message, int status) {
        synchronized (lockObjectListeners) {
            MessageListener currentListener;
            if (status == Message.STATUS_SENT) {
                for (int i = 0; i < messageListeners.size(); i++) {
                    currentListener = (MessageListener)messageListeners.elementAt(i);
                    currentListener.messageSent(message);
                }
            } else if (status == Message.STATUS_QUEUED) {
                for (int i = 0; i < messageListeners.size(); i++) {
                    currentListener = (MessageListener)messageListeners.elementAt(i);
                    currentListener.messageQueued(message);
                }
            } else if (status == Message.STATUS_FAILED) {
                for (int i = 0; i < messageListeners.size(); i++) {
                    currentListener = (MessageListener)messageListeners.elementAt(i);
                    currentListener.messageFailed(message);
                }
            }
        }
    }

    /**
     * Generates the identifiers of the messages
     * @return the next identifier
     */
    private long getNextId() {
        return id++;
    }

    /**
     * Validates the message's fields.
     *
     * @param message
     *
     * @exception IllegalArgumentException - if the protocol's string isn't valid
     */
    private void validateMessage(Message message) {
        //validate the protocol
        String messageProtocol = message.getProtocol();
        if ((messageProtocol == null) || (messageProtocol.trim().equals(""))) {
            return;
        }
        try {
            String filter = "(" + MessageSender.PROTOCOL_PROPERTY + "=" + messageProtocol + ")";
            bc.createFilter(filter);
        } catch (InvalidSyntaxException ise) {
            throw new IllegalArgumentException("Invalid message protocol string: " + messageProtocol);
        }
    }

    /**
     * This method checks the number of the threads and the number of the messages in the queue.
     * In case of need starts a new thread.
     */
    public void startThreadIfNeeded() {
        synchronized (lockObjectThreads) {
            if ((messagingThreads.size() < maxThreadsNumber) &&
                    (((messageQueue.size()/MessagingServiceThread.threadFactor)+1) > messagingThreads.size())) {
                MessagingServiceThread messThread;
                messThread = new MessagingServiceThread(this, messageQueue, defaultSender, bc, queueringTime);
                messagingThreads.addElement(messThread);
                Thread thread = new Thread(messThread);
                thread.setName("Messaging thread: " + (messagingThreads.size()-1));
                thread.start();
            }
        }
    }

    /**
     * This method checks the number of the threads and the number of the messages in the queue.
     * In case of need stops a thread.
     */
    public void stopThreadIfNeeded(MessagingServiceThread thread) {
        synchronized (lockObjectThreads) {
            if ((messagingThreads.size() > 1) &&
                    (((messageQueue.size()/MessagingServiceThread.threadFactor)+1) < messagingThreads.size())) {
                messagingThreads.removeElement(thread);
                thread.stop(false);
            }
        }
    }

    /**
     * @see eu.sqooss.service.messaging.MessagingService#getMessage(long)
     */
    public Message getMessage(long id) {
        return messageHistory.getMessage(id);
    }

    private void refreshThreadsQueueringTime(long queueringTime) {
        MessagingServiceThread messThread;
        synchronized (lockObjectThreads) {
            for (int i = 0; i < messagingThreads.size(); i++) {
                messThread = (MessagingServiceThread)messagingThreads.elementAt(i);
                messThread.setQueueringTime(queueringTime);
            }
        }
    }

    /**
     * Loads the configuration settings from the configuration file.
     * @param bc
     */
    private void initProperties(BundleContext bc) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(bc.getDataFile(MessagingConstants.FILE_NAME_PROPERTIES)));
        } catch (FileNotFoundException fnfe) {
            log("The properties file doesn't exist!", MessagingService.LOGGING_INFO_LEVEL);
            //the properties must be set manual
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        Enumeration keys = props.propertyNames();
        String currentKey;
        while (keys.hasMoreElements()) {
            currentKey = (String)keys.nextElement();
            setConfigurationProperty(currentKey, props.getProperty(currentKey));
        }
    }
    
    /**
     * This method logs the messages from the specified logging level.
     * @param message the text
     * @param level the logging level
     */
    public static void log(String message, int level) {
        /*synchronized (lockObject)*/ {
            if (logger != null) {
                switch (level) {
                case MessagingService.LOGGING_INFO_LEVEL: logger.info(message); break;
                case MessagingService.LOGGING_CONFIG_LEVEL: logger.config(message); break;
                case MessagingService.LOGGING_WARNING_LEVEL: logger.warning(message); break;
                case MessagingService.LOGGING_SEVERE_LEVEL: logger.severe(message); break;
                default: logger.info(message); break;
                }
            }
        }
    }
}
