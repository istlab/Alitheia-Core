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

package eu.sqooss.impl.plugin.util.visualizers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import eu.sqooss.ws.client.datatypes.WSResultEntry;

/**
 * The class implements the <code>setValue</code> method.
 * It has a package visibility. 
 */
abstract class AbstractVisualizer implements Visualizer {
    
    protected Map<Long, List<WSResultEntry>> values;
    protected Composite parent;
    protected String titleVersion;
    protected String titleResult;
    
    public AbstractVisualizer(Composite parent, String titleVersion, String titleResult) {
        this.parent = parent;
        this.titleVersion = titleVersion;
        this.titleResult = titleResult;
        this.values = new Hashtable<Long, List<WSResultEntry>>();
    }
    
    /**
     * @see eu.sqooss.impl.plugin.util.visualizers.Visualizer#setValue(java.lang.Long, eu.sqooss.ws.client.datatypes.WSResultEntry)
     */
    public void setValue(Long version, WSResultEntry data) {
        List<WSResultEntry> storedData = values.get(version);
        if (storedData == null) {
            storedData = new ArrayList<WSResultEntry>();
            storedData.add(data);
            values.put(version, storedData);
        } else {
            boolean found = false;
            for (WSResultEntry currentStoredEntry : storedData) {
                if ((data.getMnemonic().equals(currentStoredEntry.getMnemonic())) &&
                        (data.getDaoId() == currentStoredEntry.getDaoId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                storedData.add(data);
            }
        }
        loadData(version);
    }
    
    /**
     * @see eu.sqooss.impl.plugin.util.visualizers.Visualizer#removeMetricValues(java.lang.String)
     */
    public void removeMetricValues(String metricMnemonic) {
        Iterator<Long> keyIterator = values.keySet().iterator();
        List<WSResultEntry> currentEntries;
        Long currentVersion;
        while (keyIterator.hasNext()) {
            currentVersion = keyIterator.next();
            currentEntries = values.get(currentVersion);
            int i = 0;
            while (i < currentEntries.size()) {
                if (currentEntries.get(i).getMnemonic().equals(metricMnemonic)) {
                    currentEntries.remove(i);
                } else {
                    ++i;
                }
            }
        }
        loadData(null);
    }

    /*
     * Loads the <code>WSResultEntry</code>s for a specified version.
     * When the version is null then loads all <code>WSResultEntry</code>s.
     */
    protected abstract void loadData(Long version);
    
}

//vi: ai nosi sw=4 ts=4 expandtab
