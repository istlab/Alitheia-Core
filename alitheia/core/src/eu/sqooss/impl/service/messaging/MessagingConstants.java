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

package eu.sqooss.impl.service.messaging;

/**
 *  These constants are used for the configuration.
 *  
 *  @see eu.sqooss.service.messaging.MessagingService#setConfigurationProperty(String, String)
 */
public class MessagingConstants {

    //persistent storage for message id
    public static final String FILE_NAME_MESSAGE_ID = "id.storage";
    public static final String FILE_NAME_PROPERTIES = "messaging.properties";

    //property keys
    public static final String KEY_QUEUERING_TIME          = "queuering.time";
    public static final String KEY_MAX_THREADS_NUMBER      = "max.threads.number";
    public static final String KEY_MESSAGE_PRESERVING_TIME = "message.preserving.time";
    public static final String KEY_SMTP_HOST          = "smtp.host";
    public static final String KEY_SMTP_PORT          = "smtp.port";
    public static final String KEY_SMTP_USER          = "smtp.user";
    public static final String KEY_SMTP_PASS          = "smtp.pass";
    public static final String KEY_SMTP_REPLY         = "smtp.reply";
    public static final String KEY_SMTP_TIMEOUT       = "smtp.timeout";

    //threads factor = number of messages for one thread
    public static final String KEY_THREAD_FACTOR     = "thread.factor";

    public static final String[] KEYS = {
        KEY_QUEUERING_TIME,
        KEY_MAX_THREADS_NUMBER,
        KEY_MESSAGE_PRESERVING_TIME,
        KEY_SMTP_HOST,
        KEY_SMTP_PORT,
        KEY_SMTP_USER,
        KEY_SMTP_PASS,
        KEY_SMTP_REPLY,
        KEY_SMTP_TIMEOUT,
        KEY_THREAD_FACTOR};

}

//vi: ai nosi sw=4 ts=4 expandtab
