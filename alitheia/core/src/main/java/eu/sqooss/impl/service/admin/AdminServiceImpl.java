/*
 * Copyright 2010 - Organization for Free and Open Source Software,  
 *                 Athens, Greece.
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

package eu.sqooss.impl.service.admin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.AdminAction.AdminActionStatus;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;

/**
 * Implementation of the {@link AdminService} interface. Tracks all submitted
 * actions and uses a background thread to delete old ones. Also serves as a
 * REST api producer for the admin service, through optional registration with
 * the REST service.
 * 
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 */
@Path("/api")
public class AdminServiceImpl extends Thread implements AdminService {

    Map<String, Class<? extends AdminAction>> services;
    ConcurrentMap<Long, ActionContainer> liveactions;
    AtomicLong id;

    Logger log;

    public AdminServiceImpl() {
        services = new HashMap<String, Class<? extends AdminAction>>();
        liveactions = new ConcurrentHashMap<Long, ActionContainer>();
        id = new AtomicLong();
        if (AlitheiaCore.getInstance() != null)
            log = AlitheiaCore.getInstance().getLogManager().createLogger("sqooss.admin");
        start();
    }

    @Override
    public void registerAdminAction(String uniq,
            Class<? extends AdminAction> clazz) {
        services.put(uniq, clazz);
    }

    @Override
    public void execute(AdminAction a) {
        DBService db = null;
        if (AlitheiaCore.getInstance() != null) {
            db = AlitheiaCore.getInstance().getDBService();
            db.startDBSession();
        }
        
        debug("Executing action : " + a.id() + " ");
        try{
            a.execute();
        } catch (Throwable t) {
            if (db != null)db.rollbackDBSession();
            err("Error executing action " + a.mnemonic() + ", id " + a.id());
        } finally {
            ActionContainer ac = liveactions.get(a.id());
            if (db != null)
                if (db.isDBSessionActive())
                    db.commitDBSession();
            ac.end = System.currentTimeMillis();
            debug("Action " + a.id() + " finished in " + (ac.end - ac.start) + " msec" );
        }
    }

    @GET
    @Produces({ "application/xml", "application/json" })
    @Path("/actions/")
    @Override
    public Set<AdminAction> getAdminActions() {
        Set<AdminAction> actions = new HashSet<AdminAction>();
        for (Class<? extends AdminAction> aa : services.values()) {
            try {
                actions.add(aa.newInstance());
            } catch (Exception e) {
                err("Error instantiating action: "
                        + aa.getCanonicalName() + ": " + e.getMessage());
                return null;
            }
        }
        return actions;
    }

    @GET
    @Produces({ "application/xml", "application/json" })
    @Path("/actions/{id}")
    public AdminAction show(Long id) {
        if (liveactions.get(id) != null)
            return liveactions.get(id).aa;
        return null;
    }

    @GET
    @Produces({ "application/xml", "application/json" })
    @Path("/actions/{id}/result")
    public Map<String, Object> result() {
        if (liveactions.get(id) == null)
            return null;

        if (liveactions.get(id).end == -1)
            return null;

        return liveactions.get(id).aa.results();
    }

    @GET
    @Produces({ "application/xml", "application/json" })
    @Path("/actions/{id}/status")
    public AdminActionStatus status() {
        if (liveactions.get(id) != null)
            return liveactions.get(id).aa.status();

        if (liveactions.get(id).end == -1)
            return null;
        return null;
    }

    @GET
    @Produces({ "application/xml", "application/json" })
    @Path("/actions/{id}/error")
    public Map<String, Object> error() {
        if (liveactions.get(id) != null)
            return liveactions.get(id).aa.results();
        return null;
    }

    @POST
    @Produces({ "application/xml", "application/json" })
    @Path("/actions/{uniq}")
    public AdminAction create(String uniq) {
        Class<? extends AdminAction> clazz = services.get(uniq);

        if (clazz == null)
            return null;

        try {
            long aid = id.addAndGet(1);
            AdminAction aa = clazz.newInstance();
            aa.setId(aid);

            ActionContainer ac = new ActionContainer(aa);
            liveactions.put(aa.id(), ac);
            return aa;
        } catch (Exception e) {
            return null;
        }
    }

    public final class ActionContainer {

        public ActionContainer(AdminAction aa) {
            this.aa = aa;
            this.start = System.currentTimeMillis();
            this.end = -1;
        }

        public AdminAction aa;
        public long start;
        public long end; // -1 means action not executed
    }

    @Override
    public final void run() {
        while (true) {
            gc(10 * 60 * 1000);
            try {
                sleep(10 * 60 * 1000); //10 minutes
            } catch (InterruptedException ignored) {}
        }
    }

    /* Delete actions older than time milliseconds */
    public final int gc(long time) {
        Iterator<Long> i = liveactions.keySet().iterator();
        long ts = System.currentTimeMillis();
        int count = 0;
        while (i.hasNext()) {
            long id = i.next();
            if (liveactions.get(id).end > -1 && // Action executed
                    ts - liveactions.get(id).end > time) {
                liveactions.remove(id);
                count++;
            }
        }
        info("Action gc: removed " + count +" actions");
        return count;
    }

    // Methods to help testing, not to be used elsewhere
    @Deprecated
    public ConcurrentMap<Long, ActionContainer> liveactions() {
        return liveactions;
    }
    
    private void debug(String msg) {
        if (log != null)
            log.debug(msg);
    }
    
    private void err(String msg) {
        if (log != null)
            log.error(msg);
    }
    
    private void info(String msg) {
        if (log != null)
            log.info(msg);
    }
}
