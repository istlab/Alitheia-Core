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

//vi: ai nosi sw=4 ts=4 expandtab
