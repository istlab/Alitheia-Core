/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2009 - Organization for Free and Open Source Software,  
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
package eu.sqooss.impl.service.updater;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.DeveloperAlias;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.scheduler.Job;

/**
 * Heuristic based matcher for developer identities. Will lock all developer
 * records per project to avoid concurrent access when running.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public class DeveloperMatcher extends UpdaterBaseJob {

    private StoredProject sp;
    private DBService dbs;
    
    private Map<String, Developer> emailToDev = new TreeMap<String, Developer>();
    private Map<String, Developer> unameToDev = new TreeMap<String, Developer>();
    private Map<String, Developer> nameToDev = new TreeMap<String, Developer>();
    private Map<String, Developer> emailprefToDev = new TreeMap<String, Developer>();
    
    public DeveloperMatcher() {
        dbs = AlitheiaCore.getInstance().getDBService();
    }
    
    @Override
    public int priority() {
        return 3;
    }

    @Override
    protected void run() throws Exception {
        dbs.startDBSession();
        
        String query = "from Developer d where d.storedProject = :sp" ;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sp", sp);
        List<Developer> devs = (List<Developer>) dbs.doHQL(query, params, true);
        
        //Fill in indices
        for (Developer d : devs) {
            for (DeveloperAlias da : d.getAliases()) {
                emailToDev.put(da.getEmail(), d);
                emailprefToDev.put(da.getEmail().substring(0, da.getEmail().indexOf('@')).toLowerCase(), d);
            }
            
            if (d.getName() != null) {
                unameToDev.put(d.getUsername().toLowerCase(), d);
            }
            
            if (d.getName() != null) {
                nameToDev.put(d.getName().toLowerCase(), d);
            }
        }
        
        for (Developer d : devs) {
            
            //Developer is registered by username 
            if (d.getAliases().isEmpty()) {
                
            }
            
            //We have the developer's name, check if it matches a user name
            if (d.getName() != null || d.getName().length() > 0) {
                String[] nameParts = d.getName().split(" ");
                int namePartNo = nameParts.length;
                
                //name.surname@email.com
                if (emailprefToDev.containsKey(nameParts[0] + "." + nameParts[namePartNo - 1])) {
                    
                }
                
                //nsurname@email.com
                if (emailprefToDev.containsKey(nameParts[namePartNo - 1] + "." + nameParts[0])) {
                    
                }
                
                //nsurname@email.com
                if (emailprefToDev.containsKey(nameParts[namePartNo - 1] + "." + nameParts[0])) {
                    
                }
                
            }
        }
    }

    @Override
    public Job getJob() {
        return this;
    }
}
