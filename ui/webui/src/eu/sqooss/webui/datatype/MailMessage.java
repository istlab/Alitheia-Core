/*
 * Copyright 2008 - Organization for Free and Open Source Software,
 *                Athens, Greece.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.webui.datatype;

import java.util.*;

import eu.sqooss.webui.Result;
import eu.sqooss.webui.Terrier;
import eu.sqooss.ws.client.datatypes.WSMailMessage;
import eu.sqooss.ws.client.datatypes.WSMetricsResultRequest;

/**
 * This class represents an email message from a mailing list associated to a
 * project that has been evaluated by the SQO-OSS framework.
 * <br/>
 * It provides access to the message's meta-data, and various methods for
 * accessing and presenting relevant results.
 * 
 * @author Boryan Yotov, <tt>(ProSyst Software GmbH)</tt>
 */
public class MailMessage extends AbstractDatatype {

    /*
     * Email message's specific meta-data
     */
    protected long senderId;
    protected long listId;
    protected String messageId;
    protected String subject;
    protected long sent;
    protected long delivered;

    /**
     * Creates a new a <code>MailMessage</code> instance.
     */
    public MailMessage() {}

    /**
     * Creates a new a <code>MailMessage</code> instance, and initializes it
     * with the information provided from the given <code>WSMailMessage</code>
     * object.
     */
    public MailMessage(WSMailMessage wsVersion, Terrier terrier) {
        if (wsVersion != null) {
            id = wsVersion.getId();
            senderId = wsVersion.getSenderId();
            listId = wsVersion.getListId();
            messageId = wsVersion.getMessageId();
            subject = wsVersion.getSubject();
            sent = wsVersion.getSentTimestamp();
            delivered = wsVersion.getDeliveredTimestamp();
        }
        setTerrier(terrier);
    }

    /**
     * Gets the Id of the sender of this email message.
     * 
     * @return The sender's Id.
     */
    public long getSenderId() {
        return senderId;
    }

    /**
     * Gets the Id of the mailing list where this email message belongs.
     * 
     * @return The mailing list's Id.
     */
    public long getListId() {
        return listId;
    }

    /**
     * Gets the unique string identifier which was associated to this message
     * by the mail delivery agent.
     * 
     * @return The unique message Id.
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Gets the subject line of this email message.
     * 
     * @return The message's subject line.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the time when this message was sent.
     * 
     * @return The time when this message was sent.
     */
    public long getSentTimestamp() {
        return sent;
    }

    /**
     * Gets the time of the messsage's delivery.
     * 
     * @return The time of the messsage's delivery.
     */
    public long getDeliveredTimestamp() {
        return delivered;
    }

    //========================================================================
    // RESULTS RENDERING METHODS
    //========================================================================

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.sqooss.webui.datatype.AbstractDatatype#getResults(java.util.Collection
     * , java.lang.Long)
     */
    @Override
    public HashMap<String, Result> getResults (
            Collection<String> mnemonics, Long resourceId) {
        /*
         * Return an empty list upon invalid parameters
         */
        if ((resourceId == null) || (mnemonics == null))
            return new HashMap<String, Result>();
        /*
         * Skip already retrieved metric results.
         */
        ArrayList<String> missingMnemonics = new ArrayList<String>();
        missingMnemonics.addAll(mnemonics);
        for (String mnemonic : results.keySet()) {
            if (missingMnemonics.contains(mnemonic))
                missingMnemonics.remove(mnemonic);
        }
        /*
         * Construct the result request's object.
         */
        if (missingMnemonics.size() > 0) {
            WSMetricsResultRequest reqResults = new WSMetricsResultRequest();
            reqResults.setDaObjectId(new long[]{resourceId});
            //reqResults.setProjectVersion(true);
            reqResults.setMnemonics(
                    missingMnemonics.toArray(
                            new String[missingMnemonics.size()]));
            /*
             * Retrieve the evaluation results from the SQO-OSS framework
             */
            for (Result nextResult : terrier.getResults(reqResults))
                results.put(nextResult.getMnemonic(), nextResult);
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.sqooss.webui.WebuiItem#getHtml(long)
     */
    @Override
    public String getHtml(long in) {
        StringBuilder html = new StringBuilder("");
        html.append(sp(in) + "N/A");
        return html.toString();
    }

}
