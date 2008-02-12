/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

import eu.sqooss.scl.result.WSResult;

/**
 * The <code>WSSession</code> cares for the <code>WSConnection</code>s.
 * The <code>WSResult</code> can be stored in the user session. 
 */
public class WSSession {
    
    private WSResult[] wsResults;
    private String webServiceUrl;
    private String userName;
    private String password;
	private WSConnectionWrapper sessionConnectionWrapper;

    public WSSession(String userName, String password, String webServiceUrl) throws WSException {
        this.userName = userName;
        this.password = password;
        this.webServiceUrl = webServiceUrl;
        sessionConnectionWrapper = new WSConnectionWrapper(
                new WSConnectionImpl(userName, password, webServiceUrl));
    }
    
    /**
     * This method gives access to the SQO-OSS's web services service via URLs.
     * The generic format of a SCL-specific URL is the following:
     * <p>
     * http://sqo-oss/wscall?arg1=value1&...&argN=valueN
     * </p>
     * The URL presented above corresponds to the following <code>WSConnection</code>'s method:
     * <p>
     * WSResult wscall(arg1, ..., argN);
     * </p>
     * @param webServiceMethodUrl the url
     * @return <code>WSResult</code>
     * @throws WSException
     * <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     * <li>if web services service throws a exception</li>
     * <li>if the arguments aren't correct</li>
     * <li>if the URL isn't valid</li>
     */
    public WSResult getValue(String webServiceMethodUrl) throws WSException {
        return sessionConnectionWrapper.getValue(webServiceMethodUrl);
    }
    
    /**
     * Adds the <code>WSResult</code> array in the session.
     * @param wsResults
     */
    public void setWSResults(WSResult[] wsResults) {
        this.wsResults = wsResults;
    }
    
    /**
     * @return <code>WSResult</code> array from the session.
     */
    public WSResult[] getWSResults() {
        return wsResults;
    }
    
    /**
     * @return New ws connection. Every time creates a new connection.
     * @throws WSException 
     */
    public WSConnection getConnection() throws WSException {
        return new WSConnectionImpl(userName, password, webServiceUrl);
    }
    
    public void addWebServiceListener(String webServiceMethodUrl, WSEventListener listener) {
        //TODO:
        throw new UnsupportedOperationException("Coming soon");
    }
    
    public void removeWebServiceListener(String webServiceMethodUrl, WSEventListener listener) {
        //TODO:
        throw new UnsupportedOperationException("Coming soon");
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
