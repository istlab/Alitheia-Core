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

public interface GroupManagerDBQueries {
    
    public static final String GET_GROUP_PRIVILEGES = "from GroupPrivilege";
    
    public static final String ADD_USER_TO_GROUP_PARAM_USER_ID = "user_id";
    public static final String ADD_USER_TO_GROUP_PARAM_GROUP_ID = "group_id";
    public static final String ADD_USER_TO_GROUP = "select gu " +
    		                                       "from GroupUser gu " +
    		                                       "where gu.user.id=:" +
    		                                       ADD_USER_TO_GROUP_PARAM_USER_ID + 
    		                                       " and gu.group.id=:" +
    		                                       ADD_USER_TO_GROUP_PARAM_GROUP_ID;
    
    public static final String GET_GROUP_PRIVILEGE_PARAM_GROUP_ID = "group_id";
    public static final String GET_GROUP_PRIVILEGE_PARAM_URL_ID = "url_id";
    public static final String GET_GROUP_PRIVILEGE_PARAM_PRIV_VALUE_ID = "priv_value_id";
    public static final String GET_GROUP_PRIVILEGE = "select gp " +
    		                                            "from GroupPrivilege gp " +
    		                                            "where gp.group.id=:" +
    		                                            GET_GROUP_PRIVILEGE_PARAM_GROUP_ID +
    		                                            " and gp.url.id=:" +
    		                                            GET_GROUP_PRIVILEGE_PARAM_URL_ID +
    		                                            " and gp.pv.id=:" +
    		                                            GET_GROUP_PRIVILEGE_PARAM_PRIV_VALUE_ID;
}

//vi: ai nosi sw=4 ts=4 expandtab
