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

package eu.sqooss.impl.service.messaging.senders.smtp.connection;

/**
 * This type of exception could occur while performing methods of the services
 * which implement the Session interface.
 */
public class SessionException extends Exception {
    private static final long serialVersionUID = 1L;

    private Exception ex;


    /**
     * Creates a new exception with a specified message.
     *
     * @param message the exception's message
     */
    public SessionException( String message ) {
        super( message );
    }


    /**
     * Creates a SessionException with a specified message and an exception,
     * which had caused an error while connecting/disconnecting.
     *
     * @param message descripion of the exception
     * @param ex exception, caused the error.
     */
    public SessionException( String message, Exception ex ) {
        super( message );
        this.ex = ex;
    }


    /**
     * Gets the exception, associated with this SessionException.
     *
     * @return the exception, caused an error, or null if there is no associated
     *      exception
     */
    public Exception getException() {
        return ex;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
