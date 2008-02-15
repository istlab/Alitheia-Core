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

import eu.sqooss.service.security.SecurityAuthorizationRule;
import eu.sqooss.service.security.SecurityGroup;
import eu.sqooss.service.security.SecurityPrivilege;
import eu.sqooss.service.security.SecurityResourceURL;

public class DatabaseUtility {

    public static String userName = "anonymous";
    public static String password = "anonymous";
    public static String resourceUrl = "svc://sqooss.db";
    public static String privilegeName = "testPrivilege";
    public static String privilegeValue = "testPrivilegeValue";

    /* create methods */
    public static void initializeDatabase() {

    }

    public static long createUser(String userName, String password) {
        return 0;
    }

    public static long createGroup(String description) {
        return 0;
    }

    public static long createURL(String url) {
        return 0;
    }

    public static void addUserToGroup(long userId, long groupId) {
    }

    public static void createAuthorizationRule(SecurityGroup group, SecurityResourceURL url,
            SecurityPrivilege privilege) {
        //check whether the group is from SecurityGroupImpl class,...
    }
    /* create methods */

    /* remove methods */
    public static void removeUser(long userId) {
    }

    public static void removeGroup(long groupId) {
    }

    public static void removeURL(long urlId) {
    }

    public static void removeUserFromGroup(long userId, long groupId) {
    }

    public static void removeAuthorizationRule(long groupId, long urlId, long privilegeId) {

    }
    /* remove methods */

    /* get methods */
    public static String getUserName(long userId) {
        return null;
    }

    public static long[] getUserGroupsId(long userId) {
        return null;
    }

    public static String getGroupDescription(long groupId) {
        return null;
    }

    public static String getURL(long id) {
        return null;
    }

    public static SecurityAuthorizationRule[] getAuthorizationRules() {
        return null;
    }

    public static boolean isExistentGroup(long groupId) {
        return false;
    }

    public static boolean isExistentResourceUrl(long urlId) {
        return false;
    }

    public static boolean isExistentResourceUrl(String resourceUrl) {
        if (DatabaseUtility.resourceUrl.equals(resourceUrl)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isExistentUser(long userId) {
        return false;
    }
    /* get methods */

    /* set methods */
    public static void setGroupDescription(long groupId, String description) {
    }

    public static void setResourceUrl(long resourceUrlId, String url) {
    }

    public static void setUserPassword(long userId, String password) {
    }

    public static void setUserName(long userId, String userName) {
    }
    /* set methods */

    public static boolean checkAuthorizationRule(String resourceUrl, String privilegeName,
            String privilegeValue, String userName, String password) {
        if ((DatabaseUtility.resourceUrl.equals(resourceUrl) &&
                (DatabaseUtility.privilegeName.equals(privilegeName)) &&
                (DatabaseUtility.privilegeValue.equals(privilegeValue)) &&
                (DatabaseUtility.userName.equals(userName)) &&
                (DatabaseUtility.password.equals(password)))) {
            return true;
        } else {
            return false;
        }
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
