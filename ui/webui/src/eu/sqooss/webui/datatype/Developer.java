/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Sebastian Kuegler <sebas@kde.org>
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

import java.util.Collection;
import java.util.HashMap;

import eu.sqooss.webui.Result;
import eu.sqooss.ws.client.datatypes.WSDeveloper;
import eu.sqooss.ws.client.datatypes.WSMetricsResultRequest;

// TODO: Add JavaDoc
public class Developer extends AbstractDatatype {
    /*
     * Developer's specific meta-data. 
     */
    private String email;
    private String username;

    /*
     * Holds all evaluation results which were retrieved for this resource
     * until now.
     */
    private HashMap<String, Result> result = new HashMap<String, Result>();

    /**
     * Instantiates a new <code>Developer</code> object, and initializes it
     * with the data stored in the given <code>WSDeveloper</code> object.
     * 
     * @param developer the developer object
     */
    public Developer (WSDeveloper developer) {
        if (developer != null) {
            this.id = developer.getId();
            this.name = ((developer.getName() != null)
                    ? developer.getName()
                    : "N/A");
            this.email = ((developer.getEmail() != null)
                    ? developer.getEmail()
                    : "N/A");
            this.username = ((developer.getUsername() != null)
                    ? developer.getUsername()
                    : "N/A");
        }
    }

    /**
     * Gets the email address of this developer.
     * 
     * @return The email address, or <code>null<code> if not specified.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the developer's username (<i>usually the username of the
     * developer's SCM account</i>).
     * 
     * @return The developer's username, or <code>null<code> if not specified.
     */
    public String getUsername() {
        return username;
    }

    public String getSelectLink() {
        return "<a href=\"/developers.jsp?"
            + "selectDeveloper=" + getId() + "\">Select</a>";
    }

    public String getDeselectLink() {
        return "<a href=\"/developers.jsp?"
        + "deselectDeveloper=" + getId() + "\">Deselect</a>";
    }

    public HashMap<String, Result> getResults (
            Collection<String> mnemonics, Long resourceId) {
        /*
         * Return an empty list upon invalid parameters
         */
        if ((resourceId == null) || (mnemonics == null))
            return new HashMap<String, Result>();
        /*
         * Skip already retrieved metric results.
         */
        for (String mnemonic : result.keySet()) {
            if (mnemonics.contains(mnemonic))
                mnemonics.remove(mnemonic);
        }
        /*
         * Construct the result request's object.
         */
        if (mnemonics.size() > 0) {
            WSMetricsResultRequest reqResults = new WSMetricsResultRequest();
            reqResults.setDaObjectId(new long[]{resourceId});
            reqResults.setDeveloper(true);
            reqResults.setMnemonics(
                    mnemonics.toArray(new String[mnemonics.size()]));
            /*
             * Retrieve the evaluation results from the SQO-OSS framework
             */
            for (Result nextResult : terrier.getResults(reqResults))
                result.put(nextResult.getMnemonic(), nextResult);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.WebuiItem#getHtml(long)
     */
    @Override
    public String getHtml(long in) {
        return (sp(in) + getName());
    }
}
