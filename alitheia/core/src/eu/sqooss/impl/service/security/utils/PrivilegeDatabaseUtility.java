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

package eu.sqooss.impl.service.security.utils;

public class PrivilegeDatabaseUtility {

    /* create methods */
    public static long createPrivilege(String description) {
        return 0;
    }

    public static long addPrivilegeValue(long privilegeId, String privilegeValue) {
        return 0; //privilege value id
    }
    /* create methods */

    /* remove methods */
    public static void removePrivilege(long privilegeId) {
    }

    public static boolean removePrivilegeValue(long privilegeId, long privilegeValueId) {
        return false;
    }

    public static void removePrivilegeValues(long privilegeId) {

    }
    /* remove methods */

    /* get methods */
    public static String getPrivilegeDescription(long privilegeId) {
        return null;
    }

    public static long getPrivilegeValueId(long privilegeId, String value) {
        return 0;
    }

    public static String[] getPrivilegeValues(long privilegeId) {
        return null;
    }

    public static boolean isExistentPrivilege(long privilegeId) {
        return false;
    }

    public static boolean isExistentPrivilege(String privilegeName) {
//      return false;
        if (DatabaseUtility.privilegeName.equals(privilegeName)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isExistentPrivilegeValue(long privilegeValueId) {
        return false;
    }
    /* get methods */

    /* set methods */
    public static void setPrivilegeDescription(long privilegeId, String description) {
    }

    public static void setPrivilegeValues(long privilegeId, String[] privilegeValues) {
    }
    /* set methods */

}

//vi: ai nosi sw=4 ts=4 expandtab
