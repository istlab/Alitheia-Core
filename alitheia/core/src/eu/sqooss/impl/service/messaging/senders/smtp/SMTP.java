/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.impl.service.messaging.senders.smtp;

import eu.sqooss.impl.service.messaging.senders.smtp.connection.SessionException;

import java.util.Vector;
import java.io.InputStream;

/**
 * SMTP Service Interface allows sending messages to a SMTP Server.
 */
public interface SMTP {

    /**
     * Sends a message, using the specified data.
     *
     * @param   receivers  the receivers of the message
     * @param   reply  the reply address
     * @param   message  the message to be sent
     *
     * @exception   SMTPException
     *               Thrown when an error occurs while executing SMTP
     *               service.
     * @exception   SessionException  if the current SMTP session is invalid
     */
    public void send(Vector<String> receivers, String reply, String message)
    throws SMTPException, SessionException;

    /**
     * Sends a message, using the specified data.
     *
     * @param   receivers  the receivers of the message
     * @param   reply  the reply address
     * @param   message  the message to be sent
     *
     * @exception   SMTPException
     *               Thrown when an error occurs while executing SMTP
     *               service.
     * @exception   SessionException  if the current SMTP session is invalid
     */
    public void send(Vector<String> receivers, String reply, InputStream message)
    throws SMTPException, SessionException;

    /**
     * Initializes user's session
     *
     * @exception SessionException on error
     */
    public void open() throws SessionException;


    /**
     * Disconnects current session connection(s)
     *
     * @exception SessionException if session cannot be closed. If this exception
     *      occurs application should try at least once again to close the
     *      session and then consider it closed.
     */
    public void close() throws SessionException;

}

//vi: ai nosi sw=4 ts=4 expandtab
