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

import java.util.ArrayList;
import java.util.StringTokenizer;

import eu.sqooss.scl.result.WSResult;

/**
 * This class has package visibility.
 * It wraps the <code>WSConnection</code>.
 */
class WSConnectionWrapper {
    
    private WSConnection wsConnection;
    
    public WSConnectionWrapper(WSConnection wsConnection) {
        this.wsConnection = wsConnection;
    }
    
    /**
     * @see eu.sqooss.scl.WSSession#getValue(String)
     * @param webServiceMethodUrl
     * @return
     * @throws WSException
     */
    public WSResult getValue(String webServiceMethodUrl) throws WSException {
        ArrayList<String> methodArguments = new ArrayList<String>();
        String methodName = parseWebServiceMethodUrl(webServiceMethodUrl, methodArguments);
        try {
            if (WSConnectionConstants.METHOD_NAME_EVALUATED_PROJECTS_LIST.equals(methodName)) {
                return evaluatedProjectsList();
            } else if (WSConnectionConstants.METHOD_NAME_RETRIEVE_METRICS_4_SELECTED_PROJECT.equals(methodName)) {
                return retrieveMetrics4SelectedProject(methodArguments);
            } else if (WSConnectionConstants.METHOD_NAME_RETRIEVE_SELECTED_METRIC.equals(methodName)) {
                return retrieveSelectedMetric(methodArguments);
            } else if (WSConnectionConstants.METHOD_NAME_RETRIEVE_METRICS_4_SELECTED_FILES.equals(methodName)) {
                return retrieveMetrics4SelectedFiles(methodArguments);
            } else if (WSConnectionConstants.METHOD_NAME_RETRIEVE_FILE_LIST.equals(methodName)) {
                return retrieveFileList(methodArguments);
            } else if (WSConnectionConstants.METHOD_NAME_REQUEST_EVALUATION_4_PROJECT.equals(methodName)) {
                return requestEvaluation4Project(methodArguments);
            } else if (WSConnectionConstants.METHOD_NAME_DISPLAY_USER.equals(methodName)) {
                return displayUser(methodArguments);
            } else if (WSConnectionConstants.METHOD_NAME_DELETE_USER.equals(methodName)) {
                deleteUser(methodArguments);
                return new WSResult();
            } else if (WSConnectionConstants.METHOD_NAME_MODIFY_USER.equals(methodName)) {
                modifyUser(methodArguments);
                return new WSResult();
            } else if (WSConnectionConstants.METHOD_NAME_SUBMIT_USER.equals(methodName)) {
                return submitUser(methodArguments);
            } else if (WSConnectionConstants.METHOD_NAME_RETRIEVE_PROJECT_ID.equals(methodName)) {
                return retrieveProjectId(methodArguments);
            }
        } catch (WSException wse) {
            throw wse;
        } catch (Throwable t) {
            throw new WSException(t);
        }
        throw new WSException("Invalid URL! url: " + webServiceMethodUrl);
    }
    
    private WSResult evaluatedProjectsList() throws WSException {
        return wsConnection.evaluatedProjectsList();
    }
    
    private WSResult retrieveMetrics4SelectedProject(ArrayList<String> args) throws WSException {
        int argsNumber = args.size();
        if (argsNumber != 1) {
            throw new WSException("The number of the arguments is not correct! number: " + argsNumber);
        }
        long projectId = Long.parseLong(args.get(0));
        return wsConnection.retrieveMetrics4SelectedProject(projectId);
    }
    
    private WSResult retrieveSelectedMetric(ArrayList<String> args) throws WSException {
        int argsNumber = args.size();
        if (argsNumber != 2) {
            throw new WSException("The number of the arguments is not correct! number: " + argsNumber);
        }
        long projectId = Long.parseLong(args.get(0));
        long metricId = Long.parseLong(args.get(1));
        return wsConnection.retrieveSelectedMetric(projectId, metricId);
    }
    
    private WSResult retrieveFileList(ArrayList<String> args) throws WSException {
        int argsNumber = args.size();
        if (argsNumber != 1) {
            throw new WSException("The number of the arguments is not correct! number: " + argsNumber);
        }
        long projectId = Long.parseLong(args.get(0));
        return wsConnection.retrieveFileList(projectId);
    }
    
    private WSResult retrieveMetrics4SelectedFiles(ArrayList<String> args) throws WSException {
        int argsNumber = args.size();
        if (argsNumber != 3) {
            throw new WSException("The number of the arguments is not correct! number: " + argsNumber);
        }
        long projectId = Long.parseLong(args.get(0));
        String folderNames = args.get(1);
        String fileNames = args.get(2);
        return wsConnection.retrieveMetrics4SelectedFiles(projectId, folderNames, fileNames);
    }
    
    private WSResult requestEvaluation4Project(ArrayList<String> args) throws WSException {
        int argsNumber = args.size();
        if (argsNumber != 7) {
            throw new WSException("The number of the arguments is not correct! number: " + argsNumber);
        }
        String projectName = args.get(0);
        int projectVersion = Integer.parseInt(args.get(1));
        String srcRepositoryLocation = args.get(2);
        String mailingListLocation = args.get(3);
        String BTSLocation = args.get(4);
        String userEmailAddress = args.get(5);
        String website = args.get(6);
        return wsConnection.requestEvaluation4Project(projectName, projectVersion,
                srcRepositoryLocation, mailingListLocation, BTSLocation, userEmailAddress, website);
    }
    
    //5.1.10
    private WSResult submitUser(ArrayList<String> args) throws WSException {
        int argsNumber = args.size();

        String newUserName;
        String newNames;
        String newPassword;
        String newUserClass;
        String newOtherInfo;

        if (argsNumber == 4) {
            newUserName = args.get(0);
            newNames = null; //optional
            newPassword = args.get(1);
            newUserClass = args.get(2);
            newOtherInfo = args.get(3);
        } else if (argsNumber == 5) {
            newUserName = args.get(0);
            newNames = args.get(1);
            newPassword = args.get(2);
            newUserClass = args.get(3);
            newOtherInfo = args.get(4);
        } else {
            throw new WSException("The number of the arguments is not correct! number: " + argsNumber);
        }

        return wsConnection.submitUser(newUserName, newNames, newPassword,
                newUserClass, newOtherInfo);
    }
    //5.1.10
    
    //5.1.11
    private WSResult displayUser(ArrayList<String>  args) throws WSException {
        int argsNumber = args.size();
        if (argsNumber != 1) {
            throw new WSException("The number of the arguments is not correct! number: " + argsNumber);
        }
        long userId = Long.parseLong(args.get(0));
        return wsConnection.displayUser(userId);
    }
    
    private void modifyUser(ArrayList<String> args) throws WSException {
        int argsNumber = args.size();
        
        String newUserName;
        String newNames;
        String newPassword;
        String newUserClass;
        String newOtherInfo;
        
        if (argsNumber == 4) {
            newUserName = args.get(0);
            newNames = null; //optional
            newPassword = args.get(1);
            newUserClass = args.get(2);
            newOtherInfo = args.get(3);
        } else if (argsNumber == 5) {
            newUserName = args.get(0);
            newNames = args.get(1);
            newPassword = args.get(2);
            newUserClass = args.get(3);
            newOtherInfo = args.get(4);
        } else {
            throw new WSException("The number of the arguments is not correct! number: " + argsNumber);
        }
        
        wsConnection.modifyUser(newUserName, newNames, newPassword, newUserClass, newOtherInfo);
    }
    
    private void deleteUser(ArrayList<String> args) throws WSException {
        int argsNumber = args.size();
        if (argsNumber != 1) {
            throw new WSException("The number of the arguments is not correct! number: " + argsNumber);
        }
        long userId = Long.parseLong(args.get(0));
        wsConnection.deleteUser(userId);
    }
    //5.1.11
    
    //validation
    private WSResult retrieveProjectId(ArrayList<String> args) throws WSException {
        int argsNumber = args.size();
        if (argsNumber != 1) {
            throw new WSException("The number of the arguments is not correct! number: " + argsNumber);
        }
        return wsConnection.retrieveProjectId(args.get(0));
    }
    //validation
    
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
