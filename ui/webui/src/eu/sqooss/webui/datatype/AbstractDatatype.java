/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Sebastian Kuegler <sebas@kde.org>
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

package eu.sqooss.webui.datatype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import eu.sqooss.webui.Result;
import eu.sqooss.webui.WebuiItem;

/**
 * This class represents the base storage class for a single SQO-OSS resource
 * DAO.
 */
public abstract class AbstractDatatype extends WebuiItem {

    /**
     * Holds the list of results from metric that has been evaluated on
     * this resource item, indexed by metric mnemonic name.
     */
    protected HashMap<String, Result> results = new HashMap<String, Result>();

    /**
     * Adds a new evaluation result entry into the list of result for this
     * resource item.
     *
     * @param resultEntry the result entry
     */
    public void addResult(Result resultEntry) {
        if (results.values().contains(resultEntry) == false)
            results.put(resultEntry.getMnemonic(), resultEntry);
    }

    /**
     * Gets the list of results that are currently cached in this resource
     * item.
     * <br/>
     * The list is indexed by the mnemonic name of the metric that calculated
     * the specific result entry.
     * 
     * @return The list of results.
     */
    public HashMap<String, Result> getResults() {
        return results;
    }

    /**
     * Flushes the list of results that are currently cached in this resource
     * item.
     */
    public void flushResults() {
        results.clear();
    }

    /**
     * This method will try to retrieve from the SQO-OSS framework results
     * from all of the selected metrics (<i>specified by their mnemonics</i>)
     * that were evaluated on the project resource with the given Id.
     * <br/>
     * The list is indexed by the mnemonic name of the metric that calculated
     * the specific result entry.
     * 
     * @param mnemonics the mnemonic names of the selected metrics
     * @param resourceId the project resource's Id
     * 
     * @return The list of evaluation results, or an empty list when none are
     *   found.
     */
    public abstract HashMap<String, Result> getResults (
            Collection<String> mnemonics, Long resourceId);

    /**
     * This method will try to retrieve from the SQO-OSS framework results
     * from all of the selected metrics (<i>specified by their mnemonics</i>)
     * that were evaluated on this resource item. The item's results cache is
     * taken into consideration, thus this method will retrieve only metric
     * results that are currently missing.
     * 
     * @param mnemonics the mnemonic names of the selected metrics
     */
    public HashMap<String, Result> getResults (
            Collection<String> mnemonics) {
        if (isValid() == false) return new HashMap<String, Result>();
        /*
         * Check, if this items's results cache contains entries for all of
         * the requested metrics.
         */ 
        List<String> missingMnemonics = new ArrayList<String>(mnemonics);
        for (String nextMnemonic : mnemonics)
            if (results.keySet().contains(nextMnemonic))
                missingMnemonics.remove(nextMnemonic);
        /*
         * Retrieve all missing metric results.
         */
        if ((results.isEmpty()) || (missingMnemonics.size() > 0))
            results.putAll(getResults(missingMnemonics, this.getId()));
        return results;
    }
}
