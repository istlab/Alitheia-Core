/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.service.tds;

import java.util.Date;
import java.util.List;

import java.io.FileNotFoundException;
import javax.mail.internet.MimeMessage;

/**
 * An interface to a project's mailing list data. The accessor is based
 * on the notion of mailing lists and messages. If the underlying data store
 * does not support mailing lists it must return a single mailing list 
 * with all email messages attached to it.
 *
 */
public interface MailAccessor extends DataAccessor {
    /**
     * Retrieves the entire raw message content of the given
     * message ID (the message ID may be relative to the project
     * that this accessor is attached to).
     * 
     * @throws IllegalArgumentException listId or id are null
     * @throws FileNotFoundException the listId and id are not found
     */
    public String getRawMessage( String listname, String msgFileName )
        throws IllegalArgumentException,
               FileNotFoundException;

    /**
     * Retrieves a raw message as a parsed MIME message. This method makes 
     * sure that important email header fields can be parsed. If it 
     * cannot parse one of the following fields it will return null. The
     * fields the method tries are: From, Sender, Subject, ReceivedDate,
     * SentDate, MessageID.
     * 
     * @param listname the list the message is in
     * @param msgFileName the message id
     * 
     * @return a parsed MIME message or null if parsing fails
     * 
     * @throws IllegalArgumentException listId or id are null
     * @throws FileNotFoundException the listId and id are not found
     */
    public MimeMessage getMimeMessage( String listname, String msgFileName )
    	throws IllegalArgumentException,
    	       FileNotFoundException;
    
    /**
     * Retrieve the list of messages that are stored in the mailing list.
     */
    public List<String> getMessages( String listname )
        throws FileNotFoundException;

    /**
     * Retrieves the list of new messages (only new messages) in the
     * given mailing list.
     * 
     * @throws FileNotFoundException if the list does not exist
     */
    public List<String> getNewMessages( String listname )
        throws FileNotFoundException;
    
    /**
     * Retrieve the list of messages in the mailing list in the interval
     * [d1,d2). The dates are envelope dates, not delivery dates.
     * 
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws FileNotFoundException if the message does not exist or
     *          if the message is not new
     */
    public List<String> getMessages(String listName, Date d1, Date d2)
        throws IllegalArgumentException,
               FileNotFoundException;

    /**
     * Mark a message - which must be in the new state - as seen, so that 
     * it will not be returned by future calls to getNewMessages().
     * 
     * @param listname the list where this message lives
     * @param msgFileName the message id
     * 
     * @return true if the marking operation was successful
     * 
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws FileNotFoundException if the message does not exist or
     *          if the message is not new
     */
    public boolean markMessageAsSeen(String listname, String msgFileName)
        throws IllegalArgumentException,
               FileNotFoundException;
    
    /**
     * Returns available mailing lists this accessor knows about.
     * 
     * @return a List with the ListIds
     */
    public List<String> getMailingLists();
}

// vi: ai nosi sw=4 ts=4 expandtab
