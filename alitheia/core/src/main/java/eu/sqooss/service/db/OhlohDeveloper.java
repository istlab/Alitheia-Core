/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.service.db;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.core.AlitheiaCore;

/**
 * Holds data imported from Ohloh to help with resolving repository account
 * names to emails.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public class OhlohDeveloper extends DAObject {

    /** The developer's user name */
    private String uname;
    
    /** A SHA-1 hash of the email the developer registered with*/
    private String emailHash;
    
    /** The developer's ID in Ohloh*/
    private String ohlohId;
    
    /** The latest update timestamp for this account*/
    private Date timestamp;

    public OhlohDeveloper() {}
    
    public OhlohDeveloper(String uname, String hash, 
            String ohlohId) {
        this.uname = uname;
        this.emailHash = hash;
        this.ohlohId = ohlohId;
        this.timestamp = new Date();
    }
    
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEmailHash() {
        return emailHash;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }

    public String getOhlohId() {
        return ohlohId;
    }

    public void setOhlohId(String ohlohId) {
        this.ohlohId = ohlohId;
    }
    
    public static OhlohDeveloper getByOhlohId(String id) {
       return getBy("ohlohId", id);
    }
    
    public static OhlohDeveloper getByEmailHash(String hash) {
        return getBy("emailHash", hash);
    }
    
    public static List<OhlohDeveloper> getByUserName(String uname) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uname", uname);
        return dbs.findObjectsByProperties(OhlohDeveloper.class, params);
    }
    
    private static OhlohDeveloper getBy(String name, String value) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(name, value);
        List<OhlohDeveloper> l = dbs.findObjectsByProperties(OhlohDeveloper.class, params);
        
        if (!l.isEmpty())
            return l.get(0);
        return null;
    }
}
