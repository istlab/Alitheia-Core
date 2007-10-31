package eu.sqooss.impl.service.messaging.senders.smtp.connection;

import java.util.Properties;

/**
 * This interface provides the basic functionality
 * needed for implementing SASL (Simple Authentication and Security Layer) mechanisms.
 * SASL is described in detail in RFC2222
 */
public interface SASL{

    /**
     * This method returns the client response according
     * to the mechanism. If the Server response is null or
     * empty this means that the caller wants to send an
     * initial response. If the mechanism does not support 
     * initial response a null value should be returned. 
     * 
     * @param props session properties. All possible properties 
     * are described in the Constants class. Most oftenly used are
     * Constants.USER and Constants.PASS
     * @param serverResponse this argument contains the server 
     * response. The data in the response may be used to create
     * the client response
     * @return the client response according to the mechanism.
     * This MUST be a single BASE64 encoded line.
     */
    public String getResponse(Properties props, String serverResponse);

    /**
     * Returns the ID for the selected mechanism according to 
     * its specification. The ID should be the same that 
     * is returned by the server AUTH EHLO reply.
     * @return one line string.
     */
    public String getID();
}

