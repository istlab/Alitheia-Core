/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 by Adriaan de Groot <groot@kde.org>
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

package eu.sqooss.service.tds;

import java.util.Date;
import java.util.List;

import java.io.FileNotFoundException;
import javax.mail.internet.MimeMessage;

public interface MailAccessor extends NamedAccessor {
    /**
     * Retrieves the entire raw message content of the given
     * message ID (the message ID may be relative to the project
     * that this accessor is attached to).
     */
    public String getRawMessage( String listId, String id )
        throws IllegalArgumentException,
               FileNotFoundException;

    /**
     * Retrieves a raw message as a parsed MIME message.
     * 
     * @param listId the list the message is in
     * @param id the message id
     * @return a parsed MIME message or null if parsing fails
     * @throws IllegalArgumentException listId or id are null
     * @throws FileNotFoundException the listId and id are not found
     */
    public MimeMessage getMimeMessage( String listId, String id )
    	throws IllegalArgumentException,
    	       FileNotFoundException;
    
    /**
     * Retrieve the list of messages that are stored in the mailing list.
     */
    public List<String> getMessages( String listId )
        throws FileNotFoundException;

    /**
     * Retrieve the list of messages in the mailing list in the interval
     * [d1,d2). The dates are @em envelope dates, not delivery dates.
     */
    public List<String> getMessages( String listId, Date d1, Date d2 )
        throws IllegalArgumentException,
               FileNotFoundException;

    /**
     * Get the message sender for the given message.
     *
     * TODO return more metadata since we're parsing headers anyway
     */
    public String getSender( String listId, String id )
        throws IllegalArgumentException,
               FileNotFoundException;

    /*
     * The following methods from D5 are not implemented:
     *
     * getSenders( String listId )
     * getSenders( String listId, Date, Date )
     *      This method requires retrieving all of the individual
     *      messages and parsing them; it is better done at a higher
     *      level either by the updater or the FDS.
     *
     * getMessages( String listId, String sender )
     *      Similar - this should be calculated and cached elsewhere.
     *
     * getNextMessage( String listId, String id, boolean mode )
     *      No idea what this is supposed to do. Jump to next
     *      message Id after @p id?
     */
}

// vi: ai nosi sw=4 ts=4 expandtab

