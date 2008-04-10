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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import eu.sqooss.impl.service.security.utils.UserManagerDatabase;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.User;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.UserManager;

public class UserManagerImpl implements UserManager {

	private static final String CHARSET_NAME_UTF8 = "UTF-8";
	
    private UserManagerDatabase dbWrapper;
    private Logger logger;
    private MessageDigest messageDigest;
    
    public UserManagerImpl(DBService db, Logger logger) {
        super();
        this.dbWrapper = new UserManagerDatabase(db);
        this.logger = logger;
        
        try {
        	messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
        	messageDigest = null;
        }
    }

    /**
     * @see eu.sqooss.service.security.UserManager#createUser(java.lang.String, java.lang.String, java.lang.String)
     */
    public User createUser(String userName, String password, String email) {
        logger.info("Create user! username: " + userName + "; e-mail: " + email);
        String passwordHash = getHash(password);
        if (passwordHash == null) {
        	return null;
        }
        User newUser = new User();
        newUser.setName(userName);
        newUser.setPassword(passwordHash);
        newUser.setEmail(email);
        newUser.setRegistered(new Date());
        if (dbWrapper.createUser(newUser)) {
            return newUser;
        } else {
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.UserManager#createPendingUser(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean createPendingUser(String userName, String password, String email) {
        // TODO: implement
        return false;
    }

    /**
     * @see eu.sqooss.service.security.UserManager#modifyUser(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean modifyUser(String userName, String newPassword,
            String newEmail) {
        logger.debug("Modify user! userName: " + userName + "; e-mail: " + newEmail);
        return dbWrapper.modifyUser(userName, getHash(newPassword), newEmail);
    }

    /**
     * @see eu.sqooss.service.security.UserManager#deleteUser(long)
     */
    public boolean deleteUser(long userId) {
        logger.info("Delete user! user's id: " + userId);
        User user = getUser(userId);
        if (user != null) {
            return dbWrapper.deleteUser(user);
        } else {
            return false;
        }
    }

    /**
     * @see eu.sqooss.service.security.UserManager#deleteUser(java.lang.String)
     */
    public boolean deleteUser(String userName) {
        logger.info("Delete user! username: " + userName);
        User user = getUser(userName);
        if (user != null) {
            return dbWrapper.deleteUser(user);
        } else {
            return false;
        }
    }

    /**
     * @see eu.sqooss.service.security.UserManager#getUser(long)
     */
    public User getUser(long userId) {
        logger.info("Get user! user's id: " + userId);
        return dbWrapper.getUser(userId);
    }

    /**
     * @see eu.sqooss.service.security.UserManager#getUser(java.lang.String)
     */
    public User getUser(String userName) {
        logger.info("Get user! username: " + userName);
        List<User> users = dbWrapper.getUsers(userName);
        if (users.size() == 1) {
            return users.get(0);
        } else {
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.UserManager#getUsers()
     */
    public User[] getUsers() {
        return convertUsers(dbWrapper.getUsers());
    }

    /**
     * @see eu.sqooss.service.security.UserManager#getUsers(long)
     */
    public User[] getUsers(long groupId) {
        return convertUsers(dbWrapper.getUsers(groupId));
    }
    
    /**
     * @see eu.sqooss.service.security.UserManager#getHash(java.lang.String)
     */
    public String getHash(String password) {
    	if ((messageDigest == null) || (password == null)) {
    		return null;
    	}
    	byte[] passwordBytes = null;
    	try {
    		passwordBytes = password.getBytes(CHARSET_NAME_UTF8);
    	} catch (UnsupportedEncodingException uee) {
    		return null;
    	}
    	byte[] passwordHashBytes;
    	synchronized (messageDigest) {
    		messageDigest.reset();
    		passwordHashBytes = messageDigest.digest(passwordBytes);
    	}
    	StringBuilder passwordHash = new StringBuilder();
    	String currentSymbol;
    	for (byte currentByte : passwordHashBytes) {
    		currentSymbol = Integer.toHexString(currentByte & 0xff);
    		if (currentSymbol.length() == 1) {
    			passwordHash.append("0");
    		}
    		passwordHash.append(currentSymbol);
    	}
    	return passwordHash.toString();
    }
    
    private static User[] convertUsers(Collection<?> users) {
        if (users != null) {
            User[] result = new User[users.size()];
            users.toArray(result);
            return result;
        } else {
            return null;
        }
    }
    
    public boolean isPendingUser (String hashValue) {
        return dbWrapper.isPendingUser(hashValue);
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
