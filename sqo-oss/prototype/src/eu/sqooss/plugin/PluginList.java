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

package eu.sqooss.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import eu.sqooss.util.ReadOnlyIterator;

import eu.sqooss.db.Plugin;

/**
 * The PluginList class initializes the plugins 
 * from the database
 */
public class PluginList extends ArrayList<Plugin> {
    private final static String JAVA     = "Java";
    private final static String EXTERNAL = "External";
    //
    private final static PluginList defaultInstance;
    
    static {
	defaultInstance = new PluginList();
    }
    
    private PluginList() {
        List pl = Plugin.getPluginList();
        Iterator i = pl.iterator();
        while(i.hasNext()) {
            addPlugin((Plugin)i.next());
        }
    }
    
    private void addPlugin(Plugin p) {
        Executor ex = null;
        OutputParser op = null;
        
        if(p.getExecutorType().compareToIgnoreCase(JAVA) == 0) {
            ex = new JavaExecutor(p.getExecutor());
        } else if(p.getExecutorType().compareToIgnoreCase(EXTERNAL) == 0) {
            ex = new ExternalExecutor(p.getExecutor());
        }
        
        if(p.getParserType().compareToIgnoreCase(JAVA) == 0) {
            op = new JavaOutputParser(p.getParser());
        } else if(p.getParserType().compareToIgnoreCase(EXTERNAL) == 0) {
            op = new ExternalOutputParser(p.getParser());
        }
        
        DefaultPlugin dp = new DefaultPlugin(ex,op);
        add(dp);
    }
    
    public ReadOnlyIterator getPlugins() {
	return (new ReadOnlyIterator(iterator()));
    }
    
    public static PluginList getInstance() {
	return defaultInstance; 
    }
}
