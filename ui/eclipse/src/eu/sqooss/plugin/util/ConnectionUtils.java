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

package eu.sqooss.plugin.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import eu.sqooss.impl.plugin.properties.PropertyPagesMessages;
import eu.sqooss.scl.WSException;
import eu.sqooss.scl.WSSession;
import eu.sqooss.scl.accessor.WSAccessor;
import eu.sqooss.scl.accessor.WSProjectAccessor;

/**
 * <code>ConnectionUtils</code> is a utility class which
 * wraps the <code>WSSession</code> and its accessors.
 * The class takes care of the consistent state of the session.
 * The user can update, save and validate all settings of the session.
 */
public class ConnectionUtils {
    
    public static final QualifiedName PROPERTY_CONNECTION_UTILS =
        new QualifiedName("", "SQO-OSS_CONNECTION_UTILS");
    public static final QualifiedName PROPERTY_SERVER_URL   =
        new QualifiedName("", "SQO-OSS_SERVER_URL");
    public static final QualifiedName PROPERTY_USER_NAME    =
        new QualifiedName("", "SQO-OSS_USER_NAME");
    public static final QualifiedName PROPERTY_PASSWORD     =
        new QualifiedName("", "SQO-OSS_PASSWORD");
    public static final QualifiedName PROPERTY_PROJECT_NAME =
        new QualifiedName("", "SQO-OSS_PROJECT_NAME");
    
    private String errorMessage;
    private boolean isValid;
    private IProject project;
    private String serverUrl;
    private String userName;
    private String password;
    private String projectName;
    private long projectId;
    private WSSession wsSession;
    
    /**
     * The constructor gets the values of the fields from the <code>project</code>.
     * If the project is empty then the values are read from the property file.
     * 
     * @param project - the resource object is used as store for the session properties
     */
    public ConnectionUtils(IProject project) {
        this.project = project;
        load(project);
        validate();
    }
    
    /**
     * @return - the URL of the framework web service
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * @return - the user's name.
     * The user's name is used for authentication.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return - the user's password.
     * The password is used for authentication.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return - the project name.
     * All other methods use the project name for theirs results.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @return - indicates the session state
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * If the session is not valid then the error message contains the reason.
     * The message can't be null.
     * 
     * @return - the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * The method sets the URL of the framework web service
     * and invalidates the session.
     * 
     * @param serverUrl - the URL of the framework web service
     */
    public void setServerUrl(String serverUrl) {
        this.isValid = false;
        this.serverUrl = serverUrl;
    }

    /**
     * The method sets the user's name and invalidates the session.
     * 
     * @param userName - the user's name used for authentication
     */
    public void setUserName(String userName) {
        this.isValid = false;
        this.userName = userName;
    }

    /**
     * The method sets the user's password and invalidates the session.
     * 
     * @param password - the user's password used for authentication
     */
    public void setPassword(String password) {
        this.isValid = false;
        this.password = password;
    }

    /**
     * The method sets the name of the project and invalidates the session.
     * 
     * @param projectName - the name of the project as stored in the framework
     */
    public void setProjectName(String projectName) {
        this.isValid = false;
        this.projectName = projectName;
    }

    /**
     * The method stores the session settings in the <code>project</code>.
     * The default settings aren't stored.
     * 
     * @return - <code>true</code> if the settings are stored successfully,
     * <code>false</code> if an error occurs.
     */
    public boolean save() {
        try {
            if (!PropertyPagesMessages.
                    ConfigurationPropertyPage_Text_Server_Url_Default_Value.equals(serverUrl)) {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_SERVER_URL, serverUrl);
            } else {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_SERVER_URL, null);
            }

            if (!PropertyPagesMessages.
                    ConfigurationPropertyPage_Text_User_Name_Default_Value.equals(userName)) {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_USER_NAME, userName);
            } else {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_USER_NAME, null);
            }
            
            if (!PropertyPagesMessages.
                    ConfigurationPropertyPage_Text_Password_Default_Value.equals(password)) {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_PASSWORD, password);
            } else {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_PASSWORD, null);
            }

            if (!PropertyPagesMessages.
                    ConfigurationPropertyPage_Text_Project_Name_Default_Value.equals(projectName)) {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_PROJECT_NAME, projectName);
            } else {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_PROJECT_NAME, null);
            }
            
            project.setSessionProperty(ConnectionUtils.PROPERTY_CONNECTION_UTILS, this);
            return true;
        } catch (CoreException ce) {
            return false;
        }
    }
    
    /**
     * The method creates a new <code>WSSession</code> and gets the project identifier.
     * If an error occurs then sets the error message and returns <code>false</code>.
     * 
     * @return - <code>true</code> if the settings of the session are valid,
     * <code>false</code> otherwise.
     */
    public boolean validate() {
        try {
            wsSession = new WSSession(userName, password, serverUrl);
            WSProjectAccessor projectAccessor =
                ((WSProjectAccessor) wsSession.getAccessor(WSAccessor.Type.PROJECT));
            projectId = projectAccessor.getProjectIdByName(projectName);
            isValid = true;
            errorMessage = "";
        } catch (WSException wse) {
            isValid = false;
            errorMessage = getExceptionDump(wse); 
        }
        return isValid;
    }
    
    /*
     * Loads the session settings from the project.
     * If the some of the settings are missing
     * then the default values are read from the property file.
     */
    private void load(IProject project) {
        String propertyValue;
        
        try {
            propertyValue = project.getPersistentProperty(ConnectionUtils.PROPERTY_SERVER_URL);
            if (propertyValue == null) {
                propertyValue = PropertyPagesMessages.
                ConfigurationPropertyPage_Text_Server_Url_Default_Value;
            }
            setServerUrl(propertyValue);

            propertyValue = project.getPersistentProperty(ConnectionUtils.PROPERTY_USER_NAME);
            if (propertyValue == null) {
                propertyValue = PropertyPagesMessages.
                ConfigurationPropertyPage_Text_User_Name_Default_Value;
            }
            setUserName(propertyValue);

            propertyValue = project.getPersistentProperty(ConnectionUtils.PROPERTY_PASSWORD);
            if (propertyValue == null) {
                propertyValue = PropertyPagesMessages.
                ConfigurationPropertyPage_Text_Password_Default_Value;
            }
            setPassword(propertyValue);

            propertyValue = project.getPersistentProperty(ConnectionUtils.PROPERTY_PROJECT_NAME);
            if (propertyValue == null) {
                propertyValue = PropertyPagesMessages.
                ConfigurationPropertyPage_Text_Project_Name_Default_Value;
            }
            setProjectName(propertyValue);
        } catch (CoreException ce) {
            setDefaultValues();
        }
    }
    
    /*
     * Sets the default values of all settings.
     */
    private void setDefaultValues() {
        serverUrl = PropertyPagesMessages.
        ConfigurationPropertyPage_Text_Server_Url_Default_Value;
        userName = PropertyPagesMessages.
        ConfigurationPropertyPage_Text_User_Name_Default_Value;
        password = PropertyPagesMessages.
        ConfigurationPropertyPage_Text_Password_Default_Value;
        projectName = PropertyPagesMessages.
        ConfigurationPropertyPage_Text_Project_Name_Default_Value;
    }
    
    /*
     * Return the exception message. If the message id null
     * then return the string representation of the exception.
     */
    private static String getExceptionDump(Throwable exception) {
        String message = exception.getMessage();
        return message == null ? exception.toString() : message;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
