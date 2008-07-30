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
import org.eclipse.jface.preference.IPreferenceStore;

import eu.sqooss.impl.plugin.Activator;
import eu.sqooss.impl.plugin.util.Messages;
import eu.sqooss.impl.plugin.util.ProjectFileEntity;
import eu.sqooss.impl.plugin.util.ProjectVersionEntity;
import eu.sqooss.impl.plugin.util.Constants;
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
    public static final QualifiedName PROPERTY_SERVER_ADDRESS =
        new QualifiedName("", "SQO-OSS_SERVER_ADDRESS");
    public static final QualifiedName PROPERTY_SERVER_PORT  =
        new QualifiedName("", "SQO-OSS_SERVER_PORT");
    public static final QualifiedName PROPERTY_USER_NAME    =
        new QualifiedName("", "SQO-OSS_USER_NAME");
    public static final QualifiedName PROPERTY_PASSWORD     =
        new QualifiedName("", "SQO-OSS_PASSWORD");
    public static final QualifiedName PROPERTY_PROJECT_NAME    =
        new QualifiedName("", "SQO-OSS_PROJECT_NAME");
    public static final QualifiedName PROPERTY_PROJECT_VERSION =
        new QualifiedName("", "SQO-OSS_PROJECT_VERSION");
    
    private static final String PROPERTY_WEB_SERVICES_ADDRESS =
        "ConnectionUtils_WebServices_Address"; 
    private static final String SERVER_PORT_DELIMITER = ":";
    private static final String REG_EXPR_ANY_CHARACTER = ".*";
    
    private static String webServicesAddress;
    
    private String errorMessage;
    private boolean isValidAccount;
    private boolean isValidProjectVersion;
    private boolean isProjectSpecificAccount;
    private IProject project;
    private String serverAddress;
    private int serverPort;
    private String userName;
    private String password;
    private String projectName;
    private String projectVersion;
    private WSStoredProject storedProject;
    private WSProjectVersion storedProjectVersion;
    private WSSession wsSession;
    private IPreferenceStore store;
    
    /**
     * The constructor gets the values of the fields from the <code>project</code>.
     * If the project is empty then the values are read from the property file.
     * 
     * @param project - the resource object is used as store for the session properties
     */
    public ConnectionUtils(IProject project) {
        this.project = project;
        this.store = Activator.getDefault().getPreferenceStore();
        initProperties();
        load();
        validate();
    }
    
    /**
     * @return - the address of the Alitheia server 
     */
    public String getServerAddress() {
        return serverAddress;
    }
    
    /**
     * @return - the port of the Alitheia server 
     */
    public int getServerPort() {
        return serverPort;
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

    public boolean isProjectSpecificAccount() {
        return isProjectSpecificAccount;
    }

    public void setProjectSpecificAccount(boolean isProjectSpecificAccount) {
        this.isProjectSpecificAccount = isProjectSpecificAccount;
        refresh();
    }

    /**
     * The method sets the address of the Alitheia server
     * and invalidates the session.
     * 
     * @param serverAddress - the server address
     */
    public void setServerAddress(String serverAddress) {
        this.isValidAccount = false;
        this.serverAddress = serverAddress;
    }
    
    /**
     * The method sets the port of the Alitheia server
     * and invalidates the session.
     * 
     * @param serverPort - the server port
     */
    public void setServerPort(int serverPort) {
        this.isValidAccount = false;
        this.serverPort = serverPort;
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
        if (project == null) return true;
        try {
            if (isProjectSpecificAccount) {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_SERVER_ADDRESS, serverAddress);
            } else {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_SERVER_ADDRESS, null);
            }

            if (isProjectSpecificAccount) {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_SERVER_PORT,
                        Integer.toString(serverPort));
            } else {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_SERVER_PORT, null);
            }
            
            if (isProjectSpecificAccount) {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_USER_NAME, userName);
            } else {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_USER_NAME, null);
            }
            
            if (isProjectSpecificAccount) {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_PASSWORD, password);
            } else {
                project.setPersistentProperty(ConnectionUtils.PROPERTY_PASSWORD, null);
            }

            if (!Messages.
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
                if (webServicesAddress != null) {
                    String serverUrl = serverAddress + SERVER_PORT_DELIMITER +
                    Integer.toString(serverPort) + webServicesAddress;
                    wsSession = new WSSession(userName, password, serverUrl);
                } else {
                    this.errorMessage =
                        "The web services address must be configured in the configuration file!";
                    return false;
                }
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
        serverAddress = Messages.ConfigurationPropertyPage_Text_Server_Address_Default_Value;
        serverPort = Constants.SERVER_PORT_DEFAULT_VALUE;
        userName = Messages.
        ConfigurationPropertyPage_Text_User_Name_Default_Value;
        password = Messages.
        ConfigurationPropertyPage_Text_Password_Default_Value;
        projectName = Messages.
        ConfigurationPropertyPage_Text_Project_Name_Default_Value;
        projectVersion = Messages.
        ConfigurationPropertyPage_Combo_Last_Project_Version;
    }
    
    public Entity getEntity(IResource resource) {
        int resourceType = resource.getType();
        switch (resourceType) {
        case IResource.PROJECT : {
            return new ProjectVersionEntity(storedProject,
                    storedProjectVersion, wsSession);
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
            } else {
                for (WSProjectFile file : files) {
                    if ((file.getDirectory() && (resourceType == IResource.FOLDER)) ||
                            (!file.getDirectory() && (resourceType == IResource.FILE))) {
                        return new ProjectFileEntity(
                                storedProjectVersion, file, wsSession);
                    }
                }
            }
            return null;
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
        if (Messages.ConfigurationPropertyPage_Combo_Last_Project_Version.equals(projectVersion)) {
            versions = accessor.getLastProjectVersions(projectId);
            if (versions.length == 0) {
                errorMessage = "The last version is inaccessible!";
                return false;
            }
        } else if (Messages.ConfigurationPropertyPage_Combo_First_Project_Version.equals(projectVersion)) {
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
     * If the isProjectSpecificAccount variable is false
     * then the method loads the account's settings from the store.  
     */
    private void refresh() {
        if (!isProjectSpecificAccount) {
            setServerAddress(store.getString(Constants.PREFERENCE_NAME_SERVER_ADDRESS));
            setServerPort(store.getInt(Constants.PREFERENCE_NAME_SERVER_PORT));
            setUserName(store.getString(Constants.PREFERENCE_NAME_USER_NAME));
            setPassword(store.getString(Constants.PREFERENCE_NAME_USER_PASSWORD));
        }
    }
    
    /*
     * Loads the session settings from the project.
     * If the some of the settings are missing
     * then the default values are read from the property file.
     */
    private void load() {
        if (project == null) return;
        String propertyValue;
        
        try {
            propertyValue = project.getPersistentProperty(ConnectionUtils.PROPERTY_SERVER_ADDRESS);
            if (propertyValue == null) {
                propertyValue = store.getString(Constants.PREFERENCE_NAME_SERVER_ADDRESS);
            } else {
                isProjectSpecificAccount = true;
            }
            setServerAddress(propertyValue);

            propertyValue = project.getPersistentProperty(ConnectionUtils.PROPERTY_SERVER_PORT);
            try {
                setServerPort(Integer.valueOf(propertyValue));
                isProjectSpecificAccount = true;
            } catch (NumberFormatException nfe) {
                setServerPort(store.getInt(Constants.PREFERENCE_NAME_SERVER_PORT));
            }            
            
            propertyValue = project.getPersistentProperty(ConnectionUtils.PROPERTY_USER_NAME);
            if (propertyValue == null) {
                propertyValue = store.getString(Constants.PREFERENCE_NAME_USER_NAME);
            } else {
                isProjectSpecificAccount = true;
            }
            setUserName(propertyValue);

            propertyValue = project.getPersistentProperty(ConnectionUtils.PROPERTY_PASSWORD);
            if (propertyValue == null) {
                propertyValue = store.getString(Constants.PREFERENCE_NAME_USER_PASSWORD);
            } else {
                isProjectSpecificAccount = true;
            }
            setPassword(propertyValue);

            propertyValue = project.getPersistentProperty(ConnectionUtils.PROPERTY_PROJECT_NAME);
            if (propertyValue == null) {
                propertyValue = Messages.
                ConfigurationPropertyPage_Text_Project_Name_Default_Value;
            }
            setProjectName(propertyValue);
            
            propertyValue = project.getPersistentProperty(ConnectionUtils.PROPERTY_PROJECT_VERSION);
            if (propertyValue == null) {
                propertyValue = Messages.
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
            return Messages.ConfigurationPropertyPage_Message_Error_IOException;
        } else {
            String message = exception.getMessage();
            return message != null ? message :
                Messages.ConfigurationPropertyPage_Message_Error_Unknown;
        }
    }
    
    private void initProperties() {
        if (webServicesAddress == null) {
            if ((Activator.configurationProperties != null) &&
                    (Activator.configurationProperties.containsKey(PROPERTY_WEB_SERVICES_ADDRESS))) {
                webServicesAddress = Activator.configurationProperties.getProperty(PROPERTY_WEB_SERVICES_ADDRESS);
            } else {
                webServicesAddress = null;
            }
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
