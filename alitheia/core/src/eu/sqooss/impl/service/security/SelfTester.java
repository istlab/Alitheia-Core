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

package eu.sqooss.impl.service.security;

import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.Privilege;
import eu.sqooss.service.db.PrivilegeValue;
import eu.sqooss.service.db.ServiceUrl;
import eu.sqooss.service.db.User;
import eu.sqooss.service.security.GroupManager;
import eu.sqooss.service.security.PrivilegeManager;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.security.SecurityConstants;
import eu.sqooss.service.security.ServiceUrlManager;
import eu.sqooss.service.security.UserManager;

public class SelfTester {
    
    private static final String TEST_USER  = "alitheia_test_user";
    private static final String TEST_PASS  = "alitheia_test_pass";
    private static final String TEST_MAIL  = "alihteia_test_mail";
    private static final String TEST_GROUP = "alitheia_test_group";
    
    private SecurityManager securityManager;
    private UserManager userManager;
    private GroupManager groupManager;
    private ServiceUrlManager serviceUrlManager;
    private PrivilegeManager privilegeManager;
    
    public SelfTester(SecurityManager securityManager) {
        this.securityManager = securityManager;
        this.userManager = securityManager.getUserManager();
        this.groupManager = securityManager.getGroupManager();
        this.serviceUrlManager = securityManager.getServiceUrlManager();
        this.privilegeManager = securityManager.getPrivilegeManager();
    }
    
    public String test() {
        try {
            String testResult;

            if ((testResult = testUserManager()) != null) {
                return testResult;
            }

            if ((testResult = testGroupManager()) != null) {
                return testResult;
            }

            if ((testResult = testServiceUrlManager()) != null) {
                return testResult;
            }

            if ((testResult = testPrivilegeManager()) != null) {
                return testResult;
            }

            if ((testResult = testSecurityManager()) != null) {
                return testResult;
            }
        } catch (Throwable t) {
            return "Unexpected exception: " + t.getMessage();
        }
        return null;
    }
    
    private String testUserManager() {
        User newUser = null;

        userManager.deleteUser("fldhfkjs");
        
        try {
            newUser = userManager.createUser(TEST_USER, TEST_PASS, TEST_PASS);
            if (newUser == null) {
                return "Could not create a test user!";
            }

            if (userManager.getUser(newUser.getName()).getId() != newUser.getId()) {
                return "The test user isn't created correct!";
            }

        } finally {
            testClear(newUser, null, null, null, null);
        }
        return null;
    }
    
    private String testGroupManager() {

        Group newGroup = null;
        User newUser = null;

        try {
            newGroup = groupManager.createGroup(TEST_GROUP);
            if (newGroup == null) {
                return "Could not create a test group!";
            }

            if (groupManager.getGroup(newGroup.getId()).getId() != newGroup.getId()) {
                return "The test group isn't created correct!";
            }

            newUser = userManager.createUser(TEST_USER, TEST_PASS, TEST_MAIL);

            groupManager.addUserToGroup(newGroup.getId(), newUser.getId());

            Group[] newUserGroups = groupManager.getGroups(newUser.getId());
            if ((newUserGroups == null) || (newUserGroups.length != 1)) {
                return "The user's groups are incorrect!";
            }

            if (newGroup.getId() != newUserGroups[0].getId()) {
                return "The user's groups is incorrect!";
            }

        } finally {
            testClear(newUser, newGroup, null, null, null);
        }

        return null;
        
    }
    
    private String testServiceUrlManager() {
        
        ServiceUrl newServiceUrl = null;
        
        try {
            newServiceUrl = serviceUrlManager.createServiceUrl(SecurityConstants.URL_SQOOSS);
            if (newServiceUrl == null) {
                return "Could not create a test service url!";
            }
            
            if (serviceUrlManager.getServiceUrl(newServiceUrl.getId()).getId() != newServiceUrl.getId()) {
                return "The test service url isn't created correct!";
            }
        } finally {
            testClear(null, null, newServiceUrl, null, null);
        }
        
        return null;
        
    }
    
    private String testPrivilegeManager() {
        
        Privilege newPrivilege = null;
        PrivilegeValue newPrivilegeValue = null;
        
        try {
            newPrivilege = privilegeManager.createPrivilege(
                    SecurityConstants.Privilege.ALL.toString());
            if (newPrivilege == null) {
                return "Could not create a test privilege!";
            }
            
            if (privilegeManager.getPrivilege(newPrivilege.getId()).getId() != newPrivilege.getId()) {
                return "The test privilege isn't created correct!";
            }
            
            newPrivilegeValue = privilegeManager.createPrivilegeValue(newPrivilege.getId(),
                    SecurityConstants.Privilege.ALL.toString());
            if (newPrivilegeValue == null) {
                return "Could not create a test privilege value!";
            }
            
            if (privilegeManager.getPrivilegeValue(newPrivilegeValue.getId()).getId() != newPrivilegeValue.getId()) {
                return "The test privilege value isn't created correct!";
            }
        } finally {
            testClear(null, null, null, newPrivilege, newPrivilegeValue);
        }
        
        return null;
        
    }
    
    private String testSecurityManager() {
        
        if (securityManager.checkPermission("lfkdsjflksj", "user-fdkhfkj", "pass-hfjksdhf")) {
            return "Permission granted to the faked user!";
        }
        
        if (securityManager.checkPermission(SecurityConstants.URL_SQOOSS, null, null)) {
            return "Permission granted to the null user!";
        }
        
        if (!testSecurityManagerCustomRule()) {
            return "Custom test failed!";
        }
        
        return null;
    }
    
    private boolean testSecurityManagerCustomRule() {
        User user = null;
        Group group = null;
        ServiceUrl serviceUrl = null;
        Privilege privilege = null;
        PrivilegeValue privilegeValue = null;
        try {
            user = userManager.createUser(TEST_USER, TEST_PASS, TEST_MAIL);
            group = groupManager.createGroup(TEST_GROUP);
            serviceUrl = serviceUrlManager.createServiceUrl(
                    SecurityConstants.URL_SQOOSS);
            privilege = privilegeManager.createPrivilege(
                    SecurityConstants.Privilege.ALL);
            privilegeValue = privilegeManager.createPrivilegeValue(
                    privilege.getId(), SecurityConstants.Privilege.ALL.toString());

            if (!groupManager.addUserToGroup(group.getId(), user.getId())) {
                return false;
            }

            if (!groupManager.addPrivilegeToGroup(group.getId(), serviceUrl.getId(),
                    privilegeValue.getId())) {
                return false;
            }

            return securityManager.checkPermission(SecurityConstants.URL_SQOOSS_DATABASE,
                    null, TEST_USER, TEST_PASS);
        } finally {
            testClear(user, group, serviceUrl, privilege, privilegeValue);
        }
    }
    
    private void testClear(User user, Group group, ServiceUrl serviceUrl,
            Privilege privilege, PrivilegeValue privilegeValue) {
        if ((serviceUrl != null) && (group != null) && (privilegeValue != null)) {
            groupManager.deletePrivilegeFromGroup(group.getId(),
                    serviceUrl.getId(), privilegeValue.getId());
        }
        if ((user != null) && (group != null)) {
            groupManager.deleteUserFromGroup(group.getId(), user.getId());
        }
        if (user != null) {
            userManager.deleteUser(user.getId());
        }
        if (group != null) {
            groupManager.deleteGroup(group.getId());
        }
        if (serviceUrl != null) {
            serviceUrlManager.deleteServiceUrl(serviceUrl.getId());
        }
        if (privilegeValue != null) {
            privilegeManager.deletePrivilegeValue(privilegeValue.getId());
        }
        if (privilege != null) {
            privilegeManager.deletePrivilege(privilege.getId());
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
