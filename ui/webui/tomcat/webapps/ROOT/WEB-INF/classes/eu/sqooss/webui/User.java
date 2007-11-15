/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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


package eu.sqooss.webui;

import java.util.Map;
import java.util.TreeMap;

public class User {

    //java.util.Dictionary allUsers;
    Map<Integer,String> allUsers;
    Integer currentUserId;
    String currentUser;

    /* Sets some sample data
     * This has to go, the user data will
     * be retrieved from the database at
     * some point in the near future.
     */
    public User () {
        allUsers = new TreeMap<Integer,String>();
        // Sample data
        allUsers.put(new Integer(0), "nobody");
        allUsers.put(new Integer(1), "admin");
        allUsers.put(new Integer(2), "padams");
        allUsers.put(new Integer(3), "adridg");
        allUsers.put(new Integer(4), "sebas");
        currentUserId = new Integer(0);
    }

    public Integer getCurrentUserId () {
        return currentUserId;
    }

    public void setCurrentUserId ( Integer userId ) {
        currentUserId = userId;
    }

    public String getCurrentUser () {
        return getUser(currentUserId);
    }

    public void setCurrentUser( String user) {
        currentUser = user;
    }

    public String getUser (Integer id) {
        return (String)allUsers.get(id);
    }

    public Map getAllUsers() {
        return allUsers;
    }

    public void setAllUsers ( Map<Integer,String> users) {
        allUsers = users;
        return;
    }

    public String getInfo () {
        String info = "Currently, there are " + allUsers.size() + " users registered.";
        return info;
    }

    public boolean isLoggedIn( Integer userId ) {
        if (userId == null) {
            userId = getCurrentUserId();
        }
        // Fear this magic, all users with a high
        // user id are automatically logged in
        if (userId > 2) {
            return true;
        }
        return false;
    }
}
