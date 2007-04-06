/*
 * Copyright (c) Members of the SQO-OSS Collaboration, 2007
 * All rights reserved by respective owners.
 * See http://www.sqo-oss.eu/ for details on the copyright holders.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the SQO-OSS project nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.sqooss.db;

import org.hibernate.Session;
import org.hibernate.Query;

import eu.sqooss.plugin.PluginException;
import eu.sqooss.util.HibernateUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Class representation of the Plugin Table in the
 * database
 */
public class Plugin {
    protected long id;
    protected String path;
    protected String executor;
    protected String executorType;
    protected String parser;
    protected String parserType;
    protected String description;
    protected String name;
    
    public Plugin() { /* empty default constructor */ }

    // setters and getters
    
    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public String getExecutor() {
	return executor;
    }

    public void setExecutor(String executor) {
	this.executor = executor;
    }

    public String getExecutorType() {
	return executorType;
    }

    public void setExecutorType(String executorType) {
	this.executorType = executorType;
    }

    public String getParser() {
	return parser;
    }

    public void setParser(String parser) {
	this.parser = parser;
    }

    public String getParserType() {
	return parserType;
    }

    public void setParserType(String parserType) {
	this.parserType = parserType;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    // other methods
    /**
     * Empty method, override in abstract plugin
     */
    public HashMap<String,String> run(ProjectFile file) throws PluginException {
        return new HashMap<String,String>();
    }
    
    /**
     * Get the available metrics for the Plugin
     * 
     * @return an array of metrics
     */
    public Metric[] getMetrics() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        // TODO check this query
        Query q = session.createQuery("from Metric metric where metric.Plugin.id = :plugin");
        q.setLong("plugin", id);
        List result = q.list();
        //session.getTransaction().commit();
        
        return (Metric[])result.toArray(new Metric[] {});
    }
    
    /**
     * Get a list with all the available plugins
     * 
     */
    public static List getPluginList() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List result = session.createQuery("from Plugin").list();
        session.getTransaction().commit();
        
        return result;
    }
    
    public Plugin copy(Plugin p) {
        p.id = id;
        p.name = name;
        p.description = description;
        p.executor = executor;
        p.executorType = executorType;
        p.path = path;
        p.parser = parser;
        p.parserType = parserType;
        
        return p;
    }
    
    /**
     * Overrides toString()
     */
    public String toString() {
        StringBuilder strbld = new StringBuilder();
        strbld.append(getName()).append(" - ");
        strbld.append(getDescription()).append(" ( ");
        
        for( Metric m : getMetrics() ) {
            strbld.append(m.getName()).append(" ");
        }
        
        strbld.append(")");
        
        return strbld.toString(); 
    }
}
