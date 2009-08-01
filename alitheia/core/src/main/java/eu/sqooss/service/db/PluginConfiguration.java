/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Georgios Gousios<gousiosg@gmail.com>
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

import java.util.HashMap;
import java.util.List;

import eu.sqooss.core.AlitheiaCore;

public class PluginConfiguration extends DAObject {
    private String name;
    private String value;
    private String type;
    private String msg;
    private Plugin plugin;
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin p) {
        this.plugin = p;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    /**
     * Get a PluginConfiguration entry DAO or null in 
     */
    public static PluginConfiguration getConfigurationEntry(Plugin p, HashMap<String, Object> names) {
        DBService db = AlitheiaCore.getInstance().getDBService();
        
        names.put("plugin", p);
                
        List<PluginConfiguration> l = db.findObjectsByProperties(PluginConfiguration.class, names);
        
        if(l.isEmpty()) {
            return null;
        }
        
        return l.get(0);
    }
    
    /**
     * Update a configuration entry. If the entry is found and updated 
     * successfully true will be returned. If not found or the update 
     * fails, false will be returned.
     */
    public static boolean updConfigurationEntry(Plugin p, HashMap<String, Object> names) {
        DBService db = AlitheiaCore.getInstance().getDBService();
        PluginConfiguration pc = getConfigurationEntry(p, names);
        
        if (pc == null) {
            return false;
        }
        
        HashMap<String, Object> s = new HashMap<String, Object>();
        
        names.put("plugin", p);
        
        List<PluginConfiguration> l = db.findObjectsByProperties(PluginConfiguration.class, s);
        
        if (l.isEmpty()) {
            return false;
        }
        
        return true;
    }
}
