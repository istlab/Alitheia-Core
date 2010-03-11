/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

public abstract class SecurityManagerDBQueries {
    
    public static final String IS_EXISTENT_RESOURCE_PARAM_URL  = "resource_url";
    
    public static final String IS_EXISTENT_RESOURCE_PARAM_USER = "user_name";
    
    public static final String IS_EXISTENT_RESOURCE_PARAM_PASS = "password";
    
    public static final String IS_EXISTENT_RESOURCE_URL = "select serviceUrl " +
                                                          "from User user, GroupUser groupUser, Group group, " +
                                                          "     GroupPrivilege groupPrivilege, ServiceUrl serviceUrl " +
                                                          "where user.id=groupUser.user.id " +
                                                          " and group.id=groupUser.group.id " +
                                                          " and group.id=groupPrivilege.group.id " +
                                                          " and serviceUrl.id=groupPrivilege.url.id " +
                                                          " and user.name=:" + IS_EXISTENT_RESOURCE_PARAM_USER +
                                                          " and user.password=:" + IS_EXISTENT_RESOURCE_PARAM_PASS +
                                                          " and serviceUrl.url=:" + IS_EXISTENT_RESOURCE_PARAM_URL;
    
    public static final String CHECK_AUTHORIZATION_RULE_PARAM_URL = "resource_url";
    
    public static final String CHECK_AUTHORIZATION_RULE_PARAM_PR_NAME = "privilege_name";
    
    public static final String CHECK_AUTHORIZATION_RULE_PARAM_PR_VALUE = "privilege_value";
    
    public static final String CHECK_AUTHORIZATION_RULE_PARAM_USER = "user_name";
    
    public static final String CHECK_AUTHORIZATION_RULE_PARAM_PASS = "password_hash";
    
    private static final String CHECK_AUTHORIZATION_RULE_BASE = "select groupPrivilege " +
                                                            "from User user, GroupUser groupUser, Group group, " +
                                                            "     Privilege privilege, PrivilegeValue privilegeValue, " +
                                                            "     GroupPrivilege groupPrivilege, ServiceUrl serviceUrl " +
                                                            "where user=groupUser.user " +
                                                            " and group=groupUser.group " +
                                                            " and privilege=privilegeValue.privilege " +
                                                            " and privilegeValue=groupPrivilege.pv " +
                                                            " and group=groupPrivilege.group " +
                                                            " and serviceUrl=groupPrivilege.url " +
                                                            " and user.name=:" + CHECK_AUTHORIZATION_RULE_PARAM_USER +
                                                            " and user.password=:" + CHECK_AUTHORIZATION_RULE_PARAM_PASS +
                                                            " and privilege.description=:" + CHECK_AUTHORIZATION_RULE_PARAM_PR_NAME +
                                                            " and serviceUrl.url=:" + CHECK_AUTHORIZATION_RULE_PARAM_URL ;
    
    public static final String CHECK_AUTHORIZATION_RULE_EQUALITY   = CHECK_AUTHORIZATION_RULE_BASE +
                                                          " and privilegeValue.value=:" + CHECK_AUTHORIZATION_RULE_PARAM_PR_VALUE;
    
    public static final String CHECK_AUTHORIZATION_RULE_INEQUALITY = CHECK_AUTHORIZATION_RULE_BASE +
                                                          " and privilegeValue.value!=:" + CHECK_AUTHORIZATION_RULE_PARAM_PR_VALUE;
    
}

//vi: ai nosi sw=4 ts=4 expandtab
