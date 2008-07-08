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

package eu.sqooss.impl.service.web.services.utils;

import java.util.Properties;

import eu.sqooss.service.security.SecurityConstants;

public interface SecurityWrapperConstants extends SecurityConstants {
    
    public static enum PrivilegeValue {
        
        ALL;
        
        /**
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return ALL_PRIVILEGE_VALUES;
        }
    }
    
    public static enum Privilege {
        
        PROJECT_READ,
        PROJECTVERSION_READ,
        DIRECTORY_READ,
        GROUP_READ,
        DEVELOPER_READ,
        USER_READ,
        USER_WRITE,
        ADMIN_NOTIFY,
        ADMIN_GET_MESSAGE_OF_THE_DAY,
        METRIC_READ,
        METRICTYPE_READ;
        
        static {
            init(null);
        }
        
        private static final String PROPERTY_OBJECT_PROJECT        = "security.privilege.object.project";
        private static final String PROPERTY_OBJECT_PROJECTVERSION = "security.privilege.object.projectversion";
        private static final String PROPERTY_OBJECT_DIRECTORY      = "security.privilege.object.directory";
        private static final String PROPERTY_OBJECT_GROUP          = "security.privilege.object.group";
        private static final String PROPERTY_OBJECT_DEVELOPER      = "security.privilege.object.developer";
        private static final String PROPERTY_OBJECT_USER           = "security.privilege.object.user";
        private static final String PROPERTY_OBJECT_ADMIN          = "security.privilege.object.admin";
        private static final String PROPERTY_OBJECT_METRIC         = "security.privilege.object.metric";
        private static final String PROPERTY_OBJECT_METRICTYPE     = "security.privilege.object.metrictype";
        private static final String PROPERTY_ADMIN_ACTION_NOTIFY   = "security.privilege.admin.action.notify";
        private static final String PROPERTY_ADMIN_ACTION_GET_MESSAGE = "security.privilege.admin.action.get.message";
        
        private static String OBJECT_PROJECT         = "project";
        private static String OBJECT_PROJECTVERSION  = "projectversion";
        private static String OBJECT_DIRECTORY     = "directory";
        private static String OBJECT_GROUP         = "group";
        private static String OBJECT_DEVELOPER     = "developer";
        private static String OBJECT_USER          = "user";
        private static String OBJECT_ADMIN         = "admin";
        private static String OBJECT_METRIC        = "metric";
        private static String OBJECT_METRICTYPE    = "metrictype";
        
        private static String ADMIN_ACTION_NOTIFY = "notify";
        private static String ADMIN_ACTION_GET_MESSAGE = "getMessageOfTheDay";
        
        private static String STRING_VALUE_PROJECT_READ;
        private static String STRING_VALUE_PROJECTVERSION_READ;
        private static String STRING_VALUE_DIRECTORY_READ;
        private static String STRING_VALUE_GROUP_READ;
        private static String STRING_VALUE_DEVELOPER_READ;
        private static String STRING_VALUE_USER_READ;
        private static String STRING_VALUE_USER_WRITE;
        private static String STRING_VALUE_ADMIN_NOTIFY;
        private static String STRING_VALUE_ADMIN_GET_MESSAGE_OF_THE_DAY;
        private static String STRING_VALUE_METRIC;
        private static String STRING_VALUE_METRICTYPE;
        
        /**
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            String stringRepresentation;
            switch (this) {
            case PROJECT_READ : stringRepresentation = STRING_VALUE_PROJECT_READ; break;
            case PROJECTVERSION_READ :
                stringRepresentation = STRING_VALUE_PROJECTVERSION_READ; break;
            case DIRECTORY_READ : stringRepresentation = STRING_VALUE_DIRECTORY_READ; break;
            case GROUP_READ     : stringRepresentation = STRING_VALUE_GROUP_READ; break;
            case DEVELOPER_READ : stringRepresentation = STRING_VALUE_DEVELOPER_READ; break;
            case USER_READ      : stringRepresentation = STRING_VALUE_USER_READ; break;
            case USER_WRITE     : stringRepresentation = STRING_VALUE_USER_WRITE; break;
            case ADMIN_NOTIFY   : stringRepresentation = STRING_VALUE_ADMIN_NOTIFY; break;
            case ADMIN_GET_MESSAGE_OF_THE_DAY :
                stringRepresentation = STRING_VALUE_ADMIN_GET_MESSAGE_OF_THE_DAY; break;
            case METRIC_READ    : stringRepresentation = STRING_VALUE_METRIC; break;
            case METRICTYPE_READ: stringRepresentation = STRING_VALUE_METRICTYPE; break;
            default           : stringRepresentation = super.toString(); break;
            }
            return stringRepresentation;
        }
        
        public PrivilegeValue[] getValues() {
            return PrivilegeValue.values();
        }
        
        public static void init(Properties props) {
            if (props != null) {
                OBJECT_PROJECT        = props.getProperty(PROPERTY_OBJECT_PROJECT, OBJECT_PROJECT);
                OBJECT_PROJECTVERSION = props.getProperty(PROPERTY_OBJECT_PROJECTVERSION, OBJECT_PROJECTVERSION);
                OBJECT_DIRECTORY      = props.getProperty(PROPERTY_OBJECT_DIRECTORY, OBJECT_DIRECTORY);
                OBJECT_GROUP          = props.getProperty(PROPERTY_OBJECT_GROUP, OBJECT_GROUP);
                OBJECT_DEVELOPER      = props.getProperty(PROPERTY_OBJECT_DEVELOPER, OBJECT_DEVELOPER);
                OBJECT_USER           = props.getProperty(PROPERTY_OBJECT_USER, OBJECT_USER);
                OBJECT_ADMIN          = props.getProperty(PROPERTY_OBJECT_ADMIN, OBJECT_ADMIN);
                OBJECT_METRIC         = props.getProperty(PROPERTY_OBJECT_METRIC, OBJECT_METRIC);
                OBJECT_METRICTYPE     = props.getProperty(PROPERTY_OBJECT_METRICTYPE, OBJECT_METRICTYPE);
                ADMIN_ACTION_NOTIFY   = props.getProperty(PROPERTY_ADMIN_ACTION_NOTIFY, ADMIN_ACTION_NOTIFY);
                ADMIN_ACTION_GET_MESSAGE = props.getProperty(PROPERTY_ADMIN_ACTION_GET_MESSAGE, ADMIN_ACTION_GET_MESSAGE);
            }
            
            STRING_VALUE_PROJECT_READ =
                OBJECT_PROJECT + PrivilegeAction.DELIMITER + PrivilegeAction.READ;
            STRING_VALUE_PROJECTVERSION_READ =
                OBJECT_PROJECTVERSION + PrivilegeAction.DELIMITER + PrivilegeAction.READ;
            STRING_VALUE_DIRECTORY_READ =
                OBJECT_DIRECTORY + PrivilegeAction.DELIMITER + PrivilegeAction.READ;
            STRING_VALUE_GROUP_READ =
                OBJECT_GROUP + PrivilegeAction.DELIMITER + PrivilegeAction.READ;
            STRING_VALUE_DEVELOPER_READ =
                OBJECT_DEVELOPER + PrivilegeAction.DELIMITER + PrivilegeAction.READ;
            STRING_VALUE_USER_READ =
                OBJECT_USER + PrivilegeAction.DELIMITER + PrivilegeAction.READ;
            STRING_VALUE_USER_WRITE =
                OBJECT_USER + PrivilegeAction.DELIMITER + PrivilegeAction.WRITE;
            STRING_VALUE_ADMIN_NOTIFY =
                OBJECT_ADMIN + PrivilegeAction.DELIMITER + ADMIN_ACTION_NOTIFY;
            STRING_VALUE_ADMIN_GET_MESSAGE_OF_THE_DAY =
                OBJECT_ADMIN + PrivilegeAction.DELIMITER + ADMIN_ACTION_GET_MESSAGE;
            STRING_VALUE_METRIC =
                OBJECT_METRIC + PrivilegeAction.DELIMITER + PrivilegeAction.READ;
            STRING_VALUE_METRICTYPE =
                OBJECT_METRICTYPE + PrivilegeAction.DELIMITER + PrivilegeAction.READ;
        }
        
    };
    
    public static enum ServiceUrl {
        
        SECURITY,
        DATABASE,
        WEBADMIN,
        PLUGINADMIN;
        
        private static final String PROPERTY_URL_DB_SUFFIX          = "security.url.database.suffix";
        private static final String PROPERTY_URL_SECURITY_SUFFIX    = "security.url.security.suffix";
        private static final String PROPERTY_URL_WEBADMIN_SUFFIX    = "security.url.webadmin.suffix";
        private static final String PROPERTY_URL_PLUGINADMIN_SUFFIX = "security.url.pluginadmin.suffix";
        
        private static String URL_SQOOSS_DATABASE = URL_SQOOSS + ".database";
        private static String URL_SQOOSS_SECURITY = URL_SQOOSS + ".security";
        private static String URL_SQOOSS_WEBADMIN = URL_SQOOSS + ".webadmin";
        private static String URL_SQOOSS_PLUGINADMIN = URL_SQOOSS + ".pluginadmin";
        
        private static final Privilege[] PRIVILEEGS_SECURITY =
        {Privilege.USER_READ, Privilege.USER_WRITE, Privilege.GROUP_READ};
        private static final Privilege[] PRIVILEEGS_DATABASE =
        {Privilege.PROJECT_READ, Privilege.DIRECTORY_READ,
            Privilege.DEVELOPER_READ, Privilege.PROJECTVERSION_READ, Privilege.METRICTYPE_READ};
        private static final Privilege[] PRIVILEGES_PLUGINADMIN =
        {Privilege.METRIC_READ};
        private static final Privilege[] PRIVILEGES_WEBADMIN =
        {Privilege.ADMIN_GET_MESSAGE_OF_THE_DAY, Privilege.ADMIN_NOTIFY};
        
        /**
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            switch (this) {
            case SECURITY    : return URL_SQOOSS_SECURITY;
            case WEBADMIN    : return URL_SQOOSS_WEBADMIN;
            case PLUGINADMIN : return URL_SQOOSS_PLUGINADMIN;
            case DATABASE    : return URL_SQOOSS_DATABASE;
            }
            return null; //inaccessible
        }
        
        public Privilege[] getPrivileges() {
            switch (this) {
            case SECURITY    : return PRIVILEEGS_SECURITY;
            case DATABASE    : return PRIVILEEGS_DATABASE;
            case PLUGINADMIN : return PRIVILEGES_PLUGINADMIN;
            case WEBADMIN    : return PRIVILEGES_WEBADMIN;
            }
            return null; //inaccessible
        }
        
        public static void init(Properties props) {
            String currentProperty;
            currentProperty = props.getProperty(PROPERTY_URL_DB_SUFFIX);
            if (currentProperty != null) {
                URL_SQOOSS_DATABASE = URL_SQOOSS + currentProperty;
            }
            currentProperty = props.getProperty(PROPERTY_URL_SECURITY_SUFFIX);
            if (currentProperty != null) {
                URL_SQOOSS_SECURITY = URL_SQOOSS + currentProperty;
            }
            currentProperty = props.getProperty(PROPERTY_URL_WEBADMIN_SUFFIX);
            if (currentProperty != null) {
                URL_SQOOSS_WEBADMIN = URL_SQOOSS + currentProperty;
            }
            currentProperty = props.getProperty(PROPERTY_URL_PLUGINADMIN_SUFFIX);
            if (currentProperty != null) {
                URL_SQOOSS_PLUGINADMIN = URL_SQOOSS + currentProperty;
            }
        }
        
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
