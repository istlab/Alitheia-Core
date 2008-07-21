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

package eu.sqooss.impl.plugin.util;

import java.util.Hashtable;
import java.util.Map;

import eu.sqooss.ws.client.datatypes.WSResultEntry;

/**
 * A simple wrapper around the hash table.
 */
class MetricResultHashtable {
    
    private Map<String, WSResultEntry> store;
    
    public MetricResultHashtable() {
        store = new Hashtable<String, WSResultEntry>();
    }
    
    /**
     * Stores the given version and the result entry.
     * 
     * @param version - represent the version of the result entry
     * @param resultEntry - represent the quality result from the Alitheia system.
     */
    public void put(Long version, WSResultEntry resultEntry) {
        String key = getKey(version, resultEntry.getMnemonic());
        store.put(key, resultEntry);
    }
    
    /**
     * Returns the result entry.
     * 
     * @param version - the version of the result entry
     * @param metricMnemonic - the metric mnemonic
     * 
     * @return - the stored result entry
     */
    public WSResultEntry get(Long version, String metricMnemonic) {
        String key = getKey(version, metricMnemonic);
        return store.get(key);
    }
    
    private String getKey(Long version, String metricMnemonic) {
        return version.toString() + metricMnemonic;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
