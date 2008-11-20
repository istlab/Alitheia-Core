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

package eu.sqooss.webui.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import eu.sqooss.webui.datatype.MailMessage;

/**
 * 
 * @author Boryan Yotov, <tt>(ProSyst Software GmbH)</tt>
 */
public class MailMessagesList extends ArrayList<MailMessage> {

    /**
     * Class serial
     */
    private static final long serialVersionUID = -2171890702868485954L;

    /**
     * Returns the list of email messages sorted by their delivery date.
     * 
     * @return The list of email messages.
     */
    public SortedMap<Long, MailMessage> sortByTimestamp() {
        SortedMap<Long, MailMessage> result =
            new TreeMap<Long, MailMessage>();
        for (MailMessage nextMail : this)
            result.put(nextMail.getDeliveredTimestamp(), nextMail);
        return result;
    }

    /**
     * Returns the list of email messages sorted by their Id.
     * 
     * @return The list of email messages.
     */
    public SortedMap<Long, MailMessage> sortById() {
        SortedMap<Long, MailMessage> result =
            new TreeMap<Long, MailMessage>();
        for (MailMessage nextMail : this)
            result.put(nextMail.getId(), nextMail);
        return result;
    }

    /**
     * Gets the email message with the given delivery date.
     * 
     * @param timestamp the email message's delivery date
     * 
     * @return The email message's object, or <code>null</code> if an email
     *   message with the given delivery date can not be found in this list.
     */
    public MailMessage getMailByTimestamp(Long timestamp) {
        if (timestamp == null) return null;
        return sortByTimestamp().get(timestamp);
    }

    /**
     * Gets the email message with the given Id.
     * 
     * @param id the email message's Id
     * 
     * @return The email message object, or <code>null</code> if an email
     *   message with the given Id can not be found in this list.
     */
    public MailMessage getVersionById(Long id) {
        return sortById().get(id);
    }

    /**
     * Gets the list of time stamps of all email messages in this list,
     * indexed by their email message Id.
     * 
     * @return The list of time stamps, or an empty list when none are found.
     */
    public Map<Long, Long> getTimestamps() {
        Map<Long, Long> result = new HashMap<Long, Long>();
        for (MailMessage nextMail : this)
            result.put(nextMail.getId(), nextMail.getDeliveredTimestamp());
        return result;
    }
}
