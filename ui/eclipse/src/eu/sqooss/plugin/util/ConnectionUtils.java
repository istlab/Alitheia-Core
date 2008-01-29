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

package eu.sqooss.plugin.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

public class ConnectionUtils {
    
    public static final QualifiedName PROPERTY_SERVER_URL   =
        new QualifiedName("", "SQO-OSS_SERVER_URL");
    public static final QualifiedName PROPERTY_USER_NAME    =
        new QualifiedName("", "SQO-OSS_USER_NAME");
    public static final QualifiedName PROPERTY_PASSWORD     =
        new QualifiedName("", "SQO-OSS_PASSWORD");
    public static final QualifiedName PROPERTY_PROJECT_NAME =
        new QualifiedName("", "SQO-OSS_PROJECT_NAME");
    
    public static String validateConfiguration(String serverUrl, String userName,
            String password, String projectName) {
        return "Invalid configuration";
    }

    public static String validateConfiguration(IProject project) {
        String serverUrl;
        String userName;
        String password;
        String projectName;
        try {
            serverUrl = project.getPersistentProperty(PROPERTY_SERVER_URL);
            userName = project.getPersistentProperty(PROPERTY_USER_NAME);
            password = project.getPersistentProperty(PROPERTY_PASSWORD);
            projectName = project.getPersistentProperty(PROPERTY_PROJECT_NAME);
        } catch (CoreException ce) {
            return ce.getMessage();
        }
        return validateConfiguration(serverUrl, userName, password, projectName);
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
