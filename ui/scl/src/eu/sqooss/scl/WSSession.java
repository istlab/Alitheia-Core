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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
	private WSConnection sessionConnection;
    
	/**
	 * This constructor reads the web service url from the SCL's configuration file.
	 * @param userName
	 * @param password
	 * @throws WSException 
	 * @throws IOException if the read operation fails
	 */
    public WSSession(String userName, String password) throws WSException {
        this(userName, password, getWebServiceUrl());
    }

    public WSSession(String userName, String password, String webServiceUrl) throws WSException {
        this.userName = userName;
        this.password = password;
        this.webServiceUrl = webServiceUrl;
        sessionConnection = new WSConnectionImpl(userName, password, webServiceUrl);
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
        ArrayList<String> methodArguments = new ArrayList<String>();
        String methodName = parseWebServiceMethodUrl(webServiceMethodUrl, methodArguments);
        Class[] parameterTypes = new Class[methodArguments.size()];
        try {
            for (int i = 0; i < parameterTypes.length; i++) {
                parameterTypes[i] = Class.forName("java.lang.String");
            }
            Method method = sessionConnection.getClass().getDeclaredMethod(methodName, parameterTypes);
            return (WSResult)method.invoke(sessionConnection, methodArguments.toArray());
        } catch (Exception e) {
            throw new WSException(e);
        }
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
    
    private static String getWebServiceUrl() {
        //TODO: read the web service url from the configuration file
        return null;
    }
    
    /**
     * This method parses the url.
     * It returns the method's name and puts the method's arguments' values
     * in the <code>arguments</code> array list.
     * The arguments' position is important.
     * @param url
     * @param arguments contains the method's arguments' values
     * @return method's name
     * @throws WSException 
     */
    private String parseWebServiceMethodUrl(String url, ArrayList<String> arguments) throws WSException {
        String urlPrefix = "http://sqo-oss/";
        char questionMark = '?';
        char ampersand = '&';
        int firstIndexOfQuestionMark = url.indexOf(questionMark);
        int lastIndexOfQuestionMark = url.lastIndexOf(questionMark); 
        int firstIndexOfAmpersand = url.indexOf(ampersand);
        if (((firstIndexOfAmpersand != -1 ) && ((firstIndexOfQuestionMark == -1) || (firstIndexOfAmpersand < firstIndexOfQuestionMark))) ||
                (firstIndexOfQuestionMark != lastIndexOfQuestionMark)){
            throw new WSException("The url isn't correct: " + url);
        }
        if (!url.startsWith(urlPrefix)) {
            throw new WSException("The url doesn't start with: " + urlPrefix);
        }

        arguments.clear();
        String methodName;
        if (firstIndexOfQuestionMark == -1) {
            methodName = url.substring(urlPrefix.length());
        } else {
            methodName = url.substring(urlPrefix.length(), firstIndexOfQuestionMark);
        }
        if (firstIndexOfQuestionMark != -1) {
            String argumentsString = url.substring(firstIndexOfQuestionMark + 1);
            StringTokenizer tokenizer = new StringTokenizer(argumentsString, String.valueOf(ampersand));
            String currentToken;
            int firstIndexOfEquals;
            int lastIndexOfEquals;
            String argumentValue;
            while (tokenizer.hasMoreTokens()) {
                currentToken = tokenizer.nextToken();
                firstIndexOfEquals = currentToken.indexOf('=');
                lastIndexOfEquals = currentToken.lastIndexOf('=');
                if ((firstIndexOfEquals == -1) || (firstIndexOfEquals == 0) ||
                        (firstIndexOfEquals != lastIndexOfEquals)) {
                    throw new WSException("The parameter is not valid: " + currentToken);
                }
                argumentValue = currentToken.substring(firstIndexOfEquals + 1);
                arguments.add(argumentValue);
            }
        }
        return methodName;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
