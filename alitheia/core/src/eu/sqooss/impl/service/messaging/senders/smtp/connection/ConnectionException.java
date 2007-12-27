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

public abstract class ConnectionException extends Exception {

    public static int TYPE_POP3 = 1;
    public static int TYPE_SMTP = 2;
    public static int TYPE_MIME = 4;
    public static int TYPE_MAIL = 8;
    public static int TYPE_CONTACTS = 16;
    public static int TYPE_ACCOUNT = 32;
    public static int TYPE_STORAGE = 64;

    protected int type = 0;


    private Throwable cause;
    protected int errorCode = 0;
    protected String errorMessage = null;

    public ConnectionException() {
        super();
    }

    public ConnectionException(String arg0) {
        super(arg0);
        errorMessage = arg0;
    }

    public ConnectionException(Throwable arg0) {
        this((arg0 != null)? arg0.getMessage(): null);
        this.cause = arg0;
    }

    public ConnectionException(String arg0, Throwable arg1) {
        this(arg0);
        this.cause = arg1;
    }

    public Throwable getCause() {
        return cause;
    }

    public int getType() {
        return type;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setType(int type) {
        this.type = type;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
