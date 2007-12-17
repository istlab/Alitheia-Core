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

package eu.sqooss.impl.service.security.utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class ParserUtility {

    /**
     * The method sorts the url parameters. It uses selection sort.
     * 
     * @return the <code>url</code> with the sorted parameters 
     */
    public static String mangleUrl(String url) {
        int firstIndexOfQuestionMark = url.indexOf('?');
        if (firstIndexOfQuestionMark == -1) {
            return url; //the url hasn't parameters
        } else {
            String resourceUrl = url.substring(0, firstIndexOfQuestionMark);
            Hashtable<String,String> arguments = parseUrlArguments(url.substring(firstIndexOfQuestionMark + 1));

            //sort the arguments according to the keys (selection sort is enough)
            String[] keys = arguments.keySet().toArray(new String[0]);
            String tmpKey;
            StringBuffer sortedArguments = new StringBuffer();
            for (int i = 0; i < keys.length; i++) {
                for (int j = i + 1; j < keys.length; j++) {
                    if (keys[i].compareTo(keys[j]) > 0) {
                        tmpKey = keys[i];
                        keys[i] = keys[j];
                        keys[j] = tmpKey;
                    }
                }
                sortedArguments.append("&" + keys[i] + "=" + arguments.get(keys[i]));
            }
            if (sortedArguments.length() > 0) {
                sortedArguments.replace(0, 1, "?");
                return resourceUrl + sortedArguments.toString();
            } else {
                return resourceUrl;
            }
        }
    }

    /**
     * This method removes the privileges from the url and puts them in the hash table.
     * Returns the mangled url without privileges.
     * The url is mangled with <code>mangleUrl(String url)</code> method.
     * @param fullUrl the url with the privileges
     * @param privileges the privileges from the url
     * @return mangled resource url
     */
    public static String mangleUrlWithPrivileges(String fullUrl, Hashtable<String,String> privileges) {
        int firstIndexOfQuestionMark = fullUrl.indexOf('?');
        if (firstIndexOfQuestionMark == -1) {
            throw new IllegalArgumentException("The url: " + fullUrl + " hasn't privilege(s)!");
        } else {
            String resourceUrl = fullUrl.substring(0, firstIndexOfQuestionMark);
            Hashtable<String,String> argumentsAndPrivileges = parseUrlArguments(fullUrl.substring(firstIndexOfQuestionMark + 1));
            StringBuffer arguments = new StringBuffer();
            String currentKey;
            for (Enumeration<String> keys = argumentsAndPrivileges.keys(); keys.hasMoreElements(); ) {
                currentKey = keys.nextElement();
                if (PrivilegeDatabaseUtility.isExistentPrivilege(currentKey)) {
                    privileges.put(currentKey, argumentsAndPrivileges.get(currentKey));
                } else {
                    arguments.append("&" + currentKey + "=" + argumentsAndPrivileges.get(currentKey));
                }
            }
            if (arguments.length() > 0) {
                arguments.replace(0, 1, "?");
                return mangleUrl(resourceUrl + arguments.toString());
            } else {
                return resourceUrl;
            }
        }
    }

    /**
     * Parses the arguments with their values from the <code>arguments</code> string and
     * puts them in the hash table 
     * @param arguments
     * @return the hash table with the arguments from the <code>arguments</code> string
     */
    private static Hashtable<String,String> parseUrlArguments(String arguments) {
        Hashtable<String,String> argumentsTable = new Hashtable<String,String>();
        StringTokenizer tokenizer = new StringTokenizer(arguments, "&");
        String currentToken;
        int firstIndexOfEquals;
        int lastIndexOfEquals;
        String key;
        String value;
        while (tokenizer.hasMoreTokens()) {
            currentToken = tokenizer.nextToken();
            firstIndexOfEquals = currentToken.indexOf('=');
            lastIndexOfEquals = currentToken.lastIndexOf('=');
            if ((firstIndexOfEquals == -1) || (firstIndexOfEquals == 0) ||
                    (firstIndexOfEquals != lastIndexOfEquals)) {
                throw new IllegalArgumentException("The parameter is not valid: " + currentToken);
            }
            key = currentToken.substring(0, firstIndexOfEquals);
            value = currentToken.substring(firstIndexOfEquals + 1);
            argumentsTable.put(key, value);
        }
        return argumentsTable;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
