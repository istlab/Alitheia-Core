/*
 * Copyright 2008 - Organization for Free and Open Source Software,
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.sqooss.core.AlitheiaCore;

/**
 * Entity that holds information about a mailing list thread.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public class MailingListThread extends DAObject {

    /** The mailing list this thread belongs to */
    private MailingList list;

    /** Flag to identify a thread as a flamewar */
    private boolean isFlameWar;
    
    public MailingListThread(MailingList l) {
        this.list = l;
        this.isFlameWar = false;
    }

    public MailingList getList() {
        return list;
    }

    public void setList(MailingList list) {
        this.list = list;
    }

    public boolean getIsFlameWar() {
        return isFlameWar;
    }

    public void setIsFlameWar(boolean isFlameWar) {
        this.isFlameWar = isFlameWar;
    }
    
    /** 
     * Get an unsorted list of messages in this thread.
     */
    public List<MailMessage> getMessages() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramThread = "paramThread";
        
        String query = " select mm " + 
                " from MailMessage mm, MailThread mt " +
                " where mt.mail = mm " +
                " and mt.thread = :" + paramThread;
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(paramThread, this);

        List<MailMessage> mm = (List<MailMessage>) dbs.doHQL(query, params);

        if (mm != null && !mm.isEmpty())
            return mm;

        return Collections.emptyList();
    }
    
    /**
     * Get the email that kickstarted this thread.
     */
    public MailMessage getStartingEmail() {

        DBService dbs = AlitheiaCore.getInstance().getDBService();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("thread", this);
        params.put("parent", null);

        List<MailThread> mt = dbs.findObjectsByProperties(MailThread.class,
                params);

        if (!mt.isEmpty())
            return mt.get(0).getMail();

        return null;
    }
    
    /**
     * Get the email that closed this thread. The ordering is
     * done by date.
     * 
     * @return The last MailMessage in a thread.
     */
    public MailMessage getLastEmail() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramThread = ":paramThread";
        
        String query = "select mm " +
                " from MailMessage mm, MailThread mt " +
                " where mt.mail = mm " +
                " and mt.thread = :" + paramThread + 
                " order by mm.sendDate desc" ;
        Map<String,Object> params = new HashMap<String, Object>(1);
        params.put(paramThread, this);
        
        List<MailMessage> mm = (List<MailMessage>) dbs.doHQL(query, params, 1);
        
        if (mm == null || mm.isEmpty())
            return null;
        
        return mm.get(0);
    }
    
    /**
     * Get the number of levels in the reply tree.
     */
    public int getThreadDepth() {
        
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramThread = ":paramThread";
        
        String query = "select max(mm.depth) " +
                " from MailThread mt " +
                " where mt.thread = :" + paramThread;
        Map<String,Object> params = new HashMap<String, Object>(1);
        params.put(paramThread, this);
        
        List<Integer> mm = (List<Integer>) dbs.doHQL(query, params, 1);
        
        if (mm == null || mm.isEmpty())
            return 0;
        
        return mm.get(0).intValue();
    }
    
    /**
     * Get all emails at the provided depth. 
     * @param level 
     * @return 
     */
    public List<MailMessage> getMessagesAtLevel(int level) {
        
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramThread = ":paramThread";
        String paramDepth = ":paramDepth";
        
        String query = "select mm " +
                " from MailThread mt " +
                " where mt.thread = :" + paramThread + 
                " and mt.depth = :" + paramDepth ;
        
        Map<String,Object> params = new HashMap<String, Object>(1);
        params.put(paramThread, this);
        params.put(paramDepth, level);
        
        List<MailMessage> mm = (List<MailMessage>) dbs.doHQL(query, params);
        
        if (mm == null || mm.isEmpty())
            return Collections.emptyList();
        
        return mm;
    }
}
