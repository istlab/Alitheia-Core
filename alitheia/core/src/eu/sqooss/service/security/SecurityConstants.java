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

package eu.sqooss.service.security;

/**
 *  These constants are used to describe the resources that require access control.
 *  They can be a part of the security URL.
 *  <p>
 *  For example:
 *  <p><code>
 *  SecurityConstants.URL_SQOOSS_DATABASE and SecurityConstants.PRIVILEGES.ACTION are used in the url:
 *  </p></code>
 *  svc://sqooss.database?action=DeleteProject
 *  </p>
 */
public interface SecurityConstants {
    
    /**
     * This character splits the resource url from the privileges in the security url.
     */
    public static final char URL_DELIMITER_RESOURCE  = '?';
    
    /**
     * This character splits the privileges in the security url. 
     */
    public static final char URL_DELIMITER_PRIVILEGE = '&';
    
    /**
     * Represents the url of the SQO-OSS system.
     */
    public static final String URL_SQOOSS = "svc://sqooss";
    
    public static final String ALL_PRIVILEGES = "<all privileges>";
    
    public static final String ALL_PRIVILEGE_VALUES = "<all privilege values>";
    
}

//vi: ai nosi sw=4 ts=4 expandtab
