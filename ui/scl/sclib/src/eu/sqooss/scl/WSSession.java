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

package eu.sqooss.scl;

import eu.sqooss.scl.accessor.WSAccessor;
import eu.sqooss.scl.accessor.WSMetricAccessor;
import eu.sqooss.scl.accessor.WSProjectAccessor;
import eu.sqooss.scl.accessor.WSUserAccessor;
import eu.sqooss.ws.client.datatypes.WSUser;

public class WSSession {
    
    private WSProjectAccessor projectAccessor;
    private WSMetricAccessor metricAccessor;
    private WSUserAccessor userAccessor;
    private WSUser sessionUser;

    /**
     * @param userName - the name of the user
     * @param password - the password of the user
     * @param webServiceUrl - the URL to the web services service of the SQO-OSS framework
     * 
     * @throws WSException - if some of the parameters are not valid
     */
    public WSSession(String userName, String password, String webServiceUrl) throws WSException {
        this.projectAccessor = new WSProjectAccessorImpl(userName, password, webServiceUrl);
        this.metricAccessor  = new WSMetricAccessorImpl(userName, password, webServiceUrl);
        this.userAccessor    = new WSUserAccessorImpl(userName, password, webServiceUrl);
        initSessionUser(userName);
    }
    
    /**
     * This method returns a specific accessor.
     * The user must cast the result to the desired accessor.
     *   
     * @param type - describes the type of the accessor
     * 
     * @return - the specific accessor
     */
    public WSAccessor getAccessor(WSAccessor.Type type) {
        switch (type) {
        case PROJECT : return projectAccessor;
        case METRIC  : return metricAccessor;
        case USER    : return userAccessor;
        default      : return null;
        }
    }
    
    /**
     * This method returns all known information about the session user.
     * 
     * @return - the session user, can't be null
     */
    public WSUser getUser() {
        return sessionUser;
    }
    
    public void addWebServiceListener(String webServiceMethodUrl, WSEventListener listener) {
        //TODO:
        throw new UnsupportedOperationException("Coming soon");
    }
    
    public void removeWebServiceListener(String webServiceMethodUrl, WSEventListener listener) {
        //TODO:
        throw new UnsupportedOperationException("Coming soon");
    }
    
    private void initSessionUser(String userName) throws WSException {
        try {
            sessionUser = userAccessor.getUserByName(userName);
        } catch (WSException wse) {
            throw wse;
        }
        if (sessionUser == null) {
            throw new WSException("Can't find the user: " + userName);
        }
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
