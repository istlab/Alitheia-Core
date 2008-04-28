package eu.sqooss.service.messaging.sender;

import eu.sqooss.service.messaging.Message;

/**
 * A class which implement this interface, provide method that deal with
 * message delivery over single messaging protocol.<br/><br/>
 * A <code>MessageSender</code>'s implementation object must be registered
 * as an OSGi services, with a <code>MessageSender.PROTOCOL_PROPERTY</code>
 * service's property. The <code>MessageSender.PROTOCOL_PROPERTY</code> must
 * contain the protocol signature as a value (i.e. class name). Thus each
 * <code>MessagingService</code> object instance will be able to discover
 * and use this <code>MessageSender</code>, when it has to send a message,
 * that insists to be delivered over that protocol.<br/><br/>
 * Note: All <code>MessageSender</code> implementation must be thread safe.
 */
public interface MessageSender {

    public static final String PROTOCOL_PROPERTY = "protocol";

    /**
     * This method attempts to send the given message. Depending on the
     * delivery success it returns one of the following message statuses:
     * <ul>
     *  <li><code>Message.STATUS_SENT</code> - when the message has been sent
     *  successfully
     *  <li><code>Message.STATUS_FAILED</code> - when the message's delivery
     *  failed.
     * </ul>
     * 
     * @param message the message that has to be sent
     * @return message status (see the constants mentioned above)
     */
    public int sendMessage(Message message);

}
