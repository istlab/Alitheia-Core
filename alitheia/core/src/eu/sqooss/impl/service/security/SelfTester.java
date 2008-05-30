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

import eu.sqooss.service.db.DBService;
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
    
    private static final String PROPERTY_DEFAULT_USER_NAME     = "eu.sqooss.security.user.name";
    private static final String PROPERTY_DEFAULT_USER_PASSWORD = "eu.sqooss.security.user.password";
    private static final String PROPERTY_DEFAULT_USER_EMAIL    = "eu.sqooss.security.user.email";
    private static final String PROPERTY_DEFAULT_USER_GROUP    = "eu.sqooss.security.user.group";
    private static final String PROPERTY_ENABLE                = "eu.sqooss.security.enable";
    
    private static final String TEST_USER  = "alitheia_test_user";
    private static final String TEST_PASS  = "alitheia_test_pass";
    private static final String TEST_MAIL  = "alihteia_test_mail";
    private static final String TEST_GROUP = "alitheia_test_group";
    private static final String TEST_SERVICE_URL = "alitheia_test_url";
    
    private DBService db;
    private SecurityManager securityManager;
    private UserManager userManager;
    private GroupManager groupManager;
    private ServiceUrlManager serviceUrlManager;
    private PrivilegeManager privilegeManager;
    private boolean isEnable;
    private long newUsersGroupId;
    
    public SelfTester(SecurityManager securityManager, DBService db) {
        this.db = db;
        this.securityManager = securityManager;
        this.userManager = securityManager.getUserManager();
        this.groupManager = securityManager.getGroupManager();
        this.serviceUrlManager = securityManager.getServiceUrlManager();
        this.privilegeManager = securityManager.getPrivilegeManager();
        this.isEnable = Boolean.valueOf(System.getProperty(PROPERTY_ENABLE, "true"));
    }
    
    public String test() {
        if (!isEnable) {
            return null; //the security control is not enabled
        }
        db.startDBSession();
        try {
            
            init();
            
            String testResult;

            if ((testResult = testDefaultUser()) != null) {
                return testResult;
            }
            
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
        } finally {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
        }
        return null;
    }
    
    private String testDefaultUser() {
        String defaultUserName = System.getProperty(PROPERTY_DEFAULT_USER_NAME);
        String defaultUserPassword = System.getProperty(PROPERTY_DEFAULT_USER_PASSWORD);
        String defaultUserEmail = System.getProperty(PROPERTY_DEFAULT_USER_EMAIL);
        String defaultUserGroupDescription = System.getProperty(PROPERTY_DEFAULT_USER_GROUP);
        if ((defaultUserName == null) || (defaultUserPassword == null)
                || (defaultUserEmail == null) || (defaultUserGroupDescription == null)) {
            return "Can't find the properties of the default user!";
        }
        //check default user
        User defaultUser = userManager.getUser(defaultUserName);
        if (defaultUser == null) {
            return "Can't find default user!";
        }
        if (!defaultUserName.equals(defaultUser.getName())) {
            return "The default user's name is not correct!";
        }
        if (!userManager.getHash(defaultUserPassword).equals(defaultUser.getPassword())) {
            return "The default user's password is not correct!";
        }
        if (!defaultUserEmail.equals(defaultUser.getEmail())) {
            return "The default user's e-mail is not correct!";
        }
        //check default user's group
        Group defaultUserGroup = null;
        Group[] groups = groupManager.getGroups(defaultUser.getId());
        if (groups.length == 0) {
            return "Can't find default user's group!";
        }
        for (int i = 0; i < groups.length; i++) {
            if (defaultUserGroupDescription.equals(groups[i].getDescription())) {
                defaultUserGroup = groups[i];
            }
        }
        if (defaultUserGroup == null) {
            return "Can't find default user's group!";
        }
        return null;
    }
    
    private String testUserManager() {
        User newUser = null;

        if (!testPasswordHash()) {
    		return "The hash function doesn't work correctly";
    	}
        
        try {
            newUser = userManager.createUser(TEST_USER, TEST_PASS, TEST_MAIL);
            if (newUser == null) {
                return "Could not create a test user!";
            }

            if (userManager.getUser(newUser.getId()).getId() != newUser.getId()) {
                return "The test user isn't created correct!";
            }
            
            if (userManager.getUser(newUser.getName()).getId() != newUser.getId()) {
                return "The test user isn't created correct!";
            }

            String modifiedMail = TEST_MAIL + "_modify";
            
            userManager.modifyUser(newUser.getName(), TEST_PASS, modifiedMail);
            
            if (!modifiedMail.equals(userManager.getUser(newUser.getName()).getEmail())) {
                return "The test user isn't modified correct!";
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
            if ((newUserGroups == null) || (newUserGroups.length != 2)) {
                return "The user's groups are incorrect!";
            }

        } finally {
            testClear(newUser, newGroup, null, null, null);
        }

        return null;
        
    }
    
    private String testServiceUrlManager() {
        
        ServiceUrl newServiceUrl = serviceUrlManager.getServiceUrl(SecurityConstants.URL_SQOOSS);
        if (newServiceUrl == null) {
            return "Could not create a test service url!";
        }

        if (serviceUrlManager.getServiceUrl(newServiceUrl.getId()).getId() != newServiceUrl.getId()) {
            return "The test service url isn't created correct!";
        }
        return null;
    }
    
    private String testPrivilegeManager() {
        
        Privilege newPrivilege = null;
        PrivilegeValue newPrivilegeValue = null;
        
        try {
            newPrivilege = privilegeManager.getPrivilege(
                    SecurityConstants.ALL_PRIVILEGES);
            if (newPrivilege == null) {
                return "Could not create a test privilege!";
            }
            
            if (privilegeManager.getPrivilege(newPrivilege.getId()).getId() != newPrivilege.getId()) {
                return "The test privilege isn't created correct!";
            }
            
            newPrivilegeValue = privilegeManager.createPrivilegeValue(newPrivilege.getId(),
                    SecurityConstants.ALL_PRIVILEGES);
            if (newPrivilegeValue == null) {
                return "Could not create a test privilege value!";
            }
            
            if (privilegeManager.getPrivilegeValue(newPrivilegeValue.getId()).getId() != newPrivilegeValue.getId()) {
                return "The test privilege value isn't created correct!";
            }
        } finally {
            testClear(null, null, null, null, newPrivilegeValue);
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
    
    private boolean testPasswordHash() {
    	//SHA-256 hashes
    	String firstSequence = "The quick brown fox jumps over the lazy dog";
    	String firstSequenceHash = "d7a8fbb307d7809469ca9abcb0082e4f8d5651e46d3cdb762d02d0bf37c9e592";
    	String secondSequence = "The quick brown fox jumps over the lazy cog";
    	String secondSequenceHash = "e4c4d8f3bf76b692de791a173e05321150f7a345b46484fe427f6acc7ecc81be";
    	String thirdSequence = "";
    	String thirdSequenceHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
    	
    	return ((firstSequenceHash.equals(userManager.getHash(firstSequence))) &&
    			(secondSequenceHash.equals(userManager.getHash(secondSequence))) &&
    			(thirdSequenceHash.equals(userManager.getHash(thirdSequence))));
    	
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
            serviceUrl = serviceUrlManager.getServiceUrl(
                    SecurityConstants.URL_SQOOSS);
            privilege = privilegeManager.getPrivilege(
                    SecurityConstants.ALL_PRIVILEGES);
            privilegeValue = privilegeManager.getPrivilegeValue(
                    privilege.getId(), SecurityConstants.ALL_PRIVILEGE_VALUES);

            if (!groupManager.addUserToGroup(group.getId(), user.getId())) {
                return false;
            }

            if (!groupManager.addPrivilegeToGroup(group.getId(), serviceUrl.getId(),
                    privilegeValue.getId())) {
                return false;
            }

            if (!securityManager.checkPermission(SecurityConstants.URL_SQOOSS,
                    null, TEST_USER, TEST_PASS)) {
                return false;
            }
            
            if (!securityManager.checkPermission(TEST_SERVICE_URL,
                    null, TEST_USER, TEST_PASS)) {
                return false;
            }
            
            if (!securityManager.deleteSecurityConfiguration(group.getDescription(),
                    privilege.getDescription(), privilegeValue.getValue(), serviceUrl.getUrl())) {
                return false;
            }
            
            if (!securityManager.createSecurityConfiguration(group.getDescription(),
                    privilege.getDescription(), privilegeValue.getValue(), serviceUrl.getUrl())) {
                return false;
            }
            
            if (!securityManager.checkPermission(SecurityConstants.URL_SQOOSS,
                    null, TEST_USER, TEST_PASS)) {
                return false;
            }
            
            if (!securityManager.checkPermission(TEST_SERVICE_URL,
                    null, TEST_USER, TEST_PASS)) {
                return false;
            }
            
            if (!securityManager.deleteSecurityConfiguration(group.getDescription(),
                    privilege.getDescription(), privilegeValue.getValue(), serviceUrl.getUrl())) {
                return false;
            }
            
        } finally {
            testClear(user, group, null, null, null);
        }
        return true;
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
            if (newUsersGroupId >= 0) {
                groupManager.deleteUserFromGroup(newUsersGroupId, user.getId());
            }
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
    
    private void init() {
        Group group = groupManager.getGroup(securityManager.getNewUsersGroup());
        if (group != null) {
            newUsersGroupId = group.getId();
        } else {
            newUsersGroupId = -1;
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
