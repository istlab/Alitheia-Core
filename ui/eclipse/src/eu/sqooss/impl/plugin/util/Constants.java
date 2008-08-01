/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.impl.plugin.util;

import org.eclipse.swt.SWT;

public interface Constants {

    /* ===[GUI Constants]=== */
    
    /**
     * Represents the key of the repository image.
     */
    public static final String IMG_OBJ_REPOSITORY = "repository.gif";
    
    /**
     * Represents the minimum port number of the server configuration.
     */
    public static final int SERVER_PORT_MIN = 0;
    
    /**
     * Represents the maximum port number of the server configuration.
     */
    public static final int SERVER_PORT_MAX = 65535;
    
    /**
     * Represents the default port number of the server configuration.
     */
    public static final int SERVER_PORT_DEFAULT_VALUE = Integer.valueOf(
            Messages.ConfigurationPropertyPage_Text_Server_Port_Default_Value);
    
    /**
     * Represents the style of all text fields.
     */
    public static final int TEXT_FIELD_COMMON_STYLE = SWT.SINGLE | SWT.BORDER;
    
    /* ===[Preference Constants]=== */
    /**
     * Represents the preference name of the server address.
     */
    public static final String PREFERENCE_NAME_SERVER_ADDRESS = "SERVER_ADDRESS";
    
    /**
     * Represents the preference name of the server port.
     */
    public static final String PREFERENCE_NAME_SERVER_PORT    = "SERVER_PORT";
    
    /**
     * Represents the preference name of the user name.
     */
    public static final String PREFERENCE_NAME_USER_NAME      = "USER_NAME";
    
    /**
     * Represents the preference name of the user password.
     */
    public static final String PREFERENCE_NAME_USER_PASSWORD  = "USER_PASSWORD";
    
}

//vi: ai nosi sw=4 ts=4 expandtab
