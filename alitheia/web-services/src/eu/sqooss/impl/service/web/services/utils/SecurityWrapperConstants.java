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

import eu.sqooss.service.security.SecurityConstants;

public interface SecurityWrapperConstants extends SecurityConstants {
    
    public static final String GROUP_DESCRIPTION = "web services service security group";
    
    public static enum PrivilegeValue {
        
        PERMIT,
        READ,
        WRITE,
        ALL;
        
        /**
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            String name = name();
            if (name.equals(ALL.name())) {
                return ALL_PRIVILEGE_VALUES;
            } else {
                return name().toLowerCase();
            }
        }
    }
    
    public static enum Privilege {
        
        ACTION,
        PROJECT_ID,
        PROJECT_VERSION_ID,
        METRIC_MNEMONIC,
        USER_ID,
        SEND_MESSAGE,
        ALL;
        
        private static final PrivilegeValue[] VALUES_ALL = {PrivilegeValue.ALL};
        private static final PrivilegeValue[] VALUES_ACTION =
        {PrivilegeValue.ALL, PrivilegeValue.READ, PrivilegeValue.WRITE};
        private static final PrivilegeValue[] VALUES_SEND_MESSAGE =
        {PrivilegeValue.ALL, PrivilegeValue.PERMIT};
        
        /**
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            String name = name();
            if (name.equals(ALL.name())) {
                return ALL_PRIVILEGES;
            } else {
                return name.toLowerCase(); 
            }
        }
        
        public PrivilegeValue[] getValues() {
            switch (this) {
            case PROJECT_ID :         /*go to the next*/;
            case PROJECT_VERSION_ID : /*go to the next*/;
            case USER_ID :            /*go to the next*/;
            case METRIC_MNEMONIC :    /*go to the next*/;
            case ALL : return VALUES_ALL;
            case SEND_MESSAGE : return VALUES_SEND_MESSAGE;
            case ACTION : return VALUES_ACTION;
            }
            return null; //inaccessible
        }
        
    };
    
    public static enum ServiceUrl {
        
        SECURITY,
        DATABASE,
        WEBADMIN;
        
        private static final String URL_SQOOSS_DATABASE = URL_SQOOSS + ".database";
        private static final String URL_SQOOSS_SECURITY = URL_SQOOSS + ".security";
        private static final String URL_SQOOSS_WEBADMIN = URL_SQOOSS + ".webadmin";
        
        private static final Privilege[] PRIVILEEGS_SECURITY =
        {Privilege.ALL, Privilege.ACTION, Privilege.USER_ID};
        private static final Privilege[] PRIVILEEGS_DATABASE =
        {Privilege.ALL, Privilege.ACTION, Privilege.PROJECT_ID};
        private static final Privilege[] PRIVILEGES_WEBADMIN =
        {Privilege.ALL, Privilege.ACTION};
        
        /**
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            switch (this) {
            case SECURITY : return URL_SQOOSS_SECURITY;
            case WEBADMIN : return URL_SQOOSS_WEBADMIN;
            case DATABASE : return URL_SQOOSS_DATABASE;
            }
            return null; //inaccessible
        }
        
        public Privilege[] getPrivileges() {
            switch (this) {
            case SECURITY : return PRIVILEEGS_SECURITY;
            case DATABASE : return PRIVILEEGS_DATABASE;
            case WEBADMIN : return PRIVILEGES_WEBADMIN;
            }
            return null; //inaccessible
        }
        
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
