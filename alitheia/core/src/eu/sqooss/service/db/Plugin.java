/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Paul J. Adams <paul.adams@siriusit.co.uk>
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

import eu.sqooss.impl.service.CoreActivator;
import eu.sqooss.service.db.DAObject;

public class Plugin extends DAObject{ 
    private String name;
    private Date installdate;
    private String version;
    private String description;
    private boolean active;
    private String hashcode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getInstalldate() {
        return installdate;
    }

    public void setInstalldate(Date installdate) {
        this.installdate = installdate;
    }
    
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getHashcode() {
        return hashcode;
    }

    public void setHashcode(String hashcode) {
        this.hashcode = hashcode;
    }   
    
    public static List<Plugin> getPlugin(String name) {
        DBService db = CoreActivator.getDBService();
        HashMap<String, Object> s = new HashMap<String, Object>();
        s.put("name", name);
        return db.findObjectsByProperties(Plugin.class, s);
    }
    
    /**
     * Get Plugin by hashcode
     * 
     * @param hashcode
     *                The object's hashcode for the plugin class that implements
     *                the
     *                {@link eu.sqooss.service.abstractmetric.AlitheiaPlugin}
     *                interface
     * @return A Plugin object if the hashcode was found in the DB; null
     *         otherwise
     */
    public static Plugin getPlugin(int hashcode) {
        DBService db = CoreActivator.getDBService();
        HashMap<String, Object> s = new HashMap<String, Object>();
        s.put("hashcode", String.valueOf(hashcode));
        List<Plugin> l = db.findObjectsByProperties(Plugin.class, s); 
        if (!l.isEmpty())
            return l.get(0);
        
        return null;
    }
    
    /**
     * Get plugin configuration entries
     * @param p
     * @return
     */
    public static List<PluginConfiguration> getConfigEntries(Plugin p) {
        DBService db = CoreActivator.getDBService();
        HashMap<String, Object> s = new HashMap<String, Object>();
        s.put("plugin", p);
        return (List<PluginConfiguration>)db.findObjectsByProperties(PluginConfiguration.class, s);
    }
    
    public static List<Metric> getSupportedMetrics(Plugin p) {
        DBService db = CoreActivator.getDBService();
        HashMap<String, Object> s = new HashMap<String, Object>();
        s.put("plugin", p);
        return (List<Metric>)db.findObjectsByProperties(Metric.class, s);
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

