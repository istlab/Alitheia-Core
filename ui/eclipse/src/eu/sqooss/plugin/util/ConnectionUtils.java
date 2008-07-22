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

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import eu.sqooss.impl.plugin.properties.PropertyPagesMessages;
import eu.sqooss.impl.plugin.util.ProjectFileEntity;
import eu.sqooss.impl.plugin.util.ProjectVersionEntity;
import eu.sqooss.scl.WSException;
import eu.sqooss.scl.WSSession;
import eu.sqooss.scl.accessor.WSAccessor;
import eu.sqooss.scl.accessor.WSProjectAccessor;
import eu.sqooss.ws.client.datatypes.WSProjectFile;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSStoredProject;

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
    public static final QualifiedName PROPERTY_PROJECT_NAME    =
        new QualifiedName("", "SQO-OSS_PROJECT_NAME");
    public static final QualifiedName PROPERTY_PROJECT_VERSION =
        new QualifiedName("", "SQO-OSS_PROJECT_VERSION");
    
    private static final String REG_EXPR_ANY_CHARACTER = ".*";
    
    private String errorMessage;
    private boolean isValidAccount;
    private boolean isValidProjectVersion;
    private IProject project;
    private String serverUrl;
    private String userName;
    private String password;
    private String projectName;
    private String projectVersion;
    private WSStoredProject storedProject;
    private WSProjectVersion storedProjectVersion;
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

    public String getProjectVersion() {
        return projectVersion;
    }
    
    /**
     * @return - indicates the session state
     */
    public boolean isValidAccount() {
        return isValidAccount;
    }

    public boolean isValidProjectVersion() {
        return isValidProjectVersion;
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
        this.isValidAccount = false;
        this.serverUrl = serverUrl;
    }

    /**
     * The method sets the user's name and invalidates the session.
     * 
     * @param userName - the user's name used for authentication
     */
    public void setUserName(String userName) {
        this.isValidAccount = false;
        this.userName = userName;
    }

    /**
     * The method sets the user's password and invalidates the session.
     * 
     * @param password - the user's password used for authentication
     */
    public void setPassword(String password) {
        this.isValidAccount = false;
        this.password = password;
    }

    /**
     * The method sets the name of the project and invalidates the session.
     * 
     * @param projectName - the name of the project as stored in the framework
     */
    public void setProjectName(String projectName) {
        this.isValidProjectVersion = false;
        this.projectName = projectName;
    }

    public void setProjectVersion(String newVersion) {
        this.isValidProjectVersion = false;
        this.projectVersion = newVersion;
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
            
            if ((projectVersion != null) &&
                    (projectVersion.trim().length() != 0)) {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_PROJECT_VERSION, projectVersion);
            } else {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_PROJECT_VERSION, null);
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
            if (!isValidAccount) {
                wsSession = new WSSession(userName, password, serverUrl);
            }
            WSProjectAccessor projectAccessor =
                ((WSProjectAccessor) wsSession.getAccessor(WSAccessor.Type.PROJECT));
            try {
                storedProject = projectAccessor.getProjectByName(projectName);
            } catch (WSException wse) {
                storedProject = null;
            }
            if(!validateProjectVersion(projectAccessor)) {
                isValidAccount = true;
                isValidProjectVersion = false;
            } else {
                isValidAccount = true;
                isValidProjectVersion = true;
                errorMessage = null;
            }
        } catch (WSException wse) {
            isValidAccount = false;
            isValidProjectVersion = false;
            errorMessage = getExceptionDump(wse); 
        }
        return (isValidAccount && isValidProjectVersion);
    }
    
    /**
     * Sets the default values of all settings.
     */
    public void setDefaultValues() {
        serverUrl = PropertyPagesMessages.
        ConfigurationPropertyPage_Text_Server_Url_Default_Value;
        userName = PropertyPagesMessages.
        ConfigurationPropertyPage_Text_User_Name_Default_Value;
        password = PropertyPagesMessages.
        ConfigurationPropertyPage_Text_Password_Default_Value;
        projectName = PropertyPagesMessages.
        ConfigurationPropertyPage_Text_Project_Name_Default_Value;
        projectVersion = PropertyPagesMessages.
        ConfigurationPropertyPage_Combo_Last_Project_Version;
    }
    
    public Entity getEntity(IResource resource) {
        switch (resource.getType()) {
        case IResource.PROJECT : {
            return new ProjectVersionEntity(storedProject,
                    storedProjectVersion);
        }
        case IResource.FILE    : //next
        case IResource.FOLDER  : {
            String filePathname = resource.getProjectRelativePath().toString();
            WSProjectAccessor projectAccessor =
                ((WSProjectAccessor) wsSession.getAccessor(WSAccessor.Type.PROJECT));
            WSProjectFile[] files;
            try {
                files = projectAccessor.getFilesByRegularExpression(
                        storedProjectVersion.getId(),
                        REG_EXPR_ANY_CHARACTER + filePathname);
            } catch (WSException e) {
                errorMessage = e.getMessage();
                return null;
            }
            if (files.length == 0) {
                errorMessage = "The file does not exist!";
                return null;
            } else {
                //TODO: in case of more files, choose the first - improve
                return new ProjectFileEntity(
                        storedProjectVersion, files[0], wsSession);
            }
        }
        default : return null;
        }
    }
    
    private boolean validateProjectVersion(WSProjectAccessor accessor) throws WSException {
        if (storedProject == null) {
            errorMessage = "The project doesn't exist!";
            return false;
        }
        WSProjectVersion[] versions = null;
        long[] projectId = new long[] {storedProject.getId()};
        if (PropertyPagesMessages.
                ConfigurationPropertyPage_Combo_Last_Project_Version.equals(projectVersion)) {
            versions = accessor.getLastProjectVersions(projectId);
            if (versions.length == 0) {
                errorMessage = "The last version is inaccessible!";
                return false;
            }
        } else if (PropertyPagesMessages.
                ConfigurationPropertyPage_Combo_First_Project_Version.equals(projectVersion)) {
            versions = accessor.getFirstProjectVersions(projectId);
            if (versions.length == 0) {
                errorMessage = "The first version is inaccessible!";
                return false;
            }
        } else {
            try {
                long[] versionNumber = new long[] {Long.valueOf(projectVersion)};
                versions = accessor.getProjectVersionsByVersionNumbers(projectId[0], versionNumber);
            } catch (NumberFormatException nfe) { /*do nothing here*/}
            if ((versions == null) || (versions.length == 0)) {
                WSProjectVersion[] firstVersion = accessor.getFirstProjectVersions(projectId);
                WSProjectVersion[] lastVersion = accessor.getLastProjectVersions(projectId);
                errorMessage = "Incorect version! ";
                if (firstVersion.length != 0) {
                    errorMessage += "first-" + firstVersion[0].getVersion() + "; ";
                }
                if (lastVersion.length != 0) {
                    errorMessage += "last-" + lastVersion[0].getVersion() + "; ";
                }
                return false;
            }
        }
        storedProjectVersion = versions[0];
        errorMessage = "";
        return true;
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
            
            propertyValue = project.getPersistentProperty(ConnectionUtils.PROPERTY_PROJECT_VERSION);
            if (propertyValue == null) {
                propertyValue = PropertyPagesMessages.
                ConfigurationPropertyPage_Combo_Last_Project_Version;
            }
            setProjectVersion(propertyValue);
        } catch (CoreException ce) {
            setDefaultValues();
        }
    }
    
    /*
     * Return the exception message. If the message id null
     * then return the string representation of the exception.
     */
    private static String getExceptionDump(WSException exception) {
        Throwable cause = exception.getCause();
        if ((cause != null) && ((cause.getCause() instanceof IOException))) {
            return PropertyPagesMessages.
            ConfigurationPropertyPage_Message_Error_IOException;
        } else {
            String message = exception.getMessage();
            return message == null ? exception.toString() : message;
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
