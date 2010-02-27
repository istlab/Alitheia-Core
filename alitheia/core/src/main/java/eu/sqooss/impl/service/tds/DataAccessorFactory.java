/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software, 
 *                 Athens, Greece.
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

package eu.sqooss.impl.service.tds;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.DataAccessor;

/** 
 * A factory class that registers all data accessor implementations and returns
 *  DataAccessor instances according to data store URIs.   
 */
public class DataAccessorFactory {
    
    private static Logger log;
    private static Map<String, Class<?>> implementations = null;
    
    DataAccessorFactory(Logger l) {
        DataAccessorFactory.log = l;
        implementations = new ConcurrentHashMap<String, Class<?>>();
    }
    
    /**
     * Register a DataAccessor implementation class. If there is another 
     * implementation for the provided scheme, it will be overwritten. 
     * 
     * @param scheme The URL scheme this implementation supports
     * @param impl The implementing class
     */
    public synchronized static void addImplementation(URI scheme, Class<?> impl) {
        if (implementations.containsKey(scheme)) {
            log.warn("Overwriting implementation class " 
                    + implementations.get(scheme) + " for scheme " + scheme);
        }
        log.info("Adding handler class " + impl.getName() + " for scheme "
                + scheme.getScheme());
        implementations.put(scheme.getScheme(), impl);
    }
    
    /**
     * Get a list of all supported URI schemes.
     * @return A, possibly empty, Set of supported URI schemes.
     */
    public static Set<String> getSupportedSchemes() {
        return implementations.keySet();
    }
    
    
    
    /**
     * Get the appropriate accessor for the provided URL. Will  attempt
     * to initialize the accessor.
     * @param url The location of the data store the accessor must handle
     * @param name the project name to retrieve an accessor for
     * @return The accessor instance, or null if there are errors when
     * initialising the accessor or if there is no accessor to handle
     * the provided URI.
     */
    public static DataAccessor getInstance(URI url, String name) {

        if (url == null || url.getScheme() == null) {
            log.warn("Request for accessor with empty URI");
            return null;
        }
        
        Class<?> handler = implementations.get(url.getScheme());

        if (handler == null) {
            log.warn("No accessor found for scheme " + url.getScheme());
            return null;
        }

        try {
            //DataAccessor da = (DataAccessor) handler.newInstance();
            Object o = handler.newInstance();
            DataAccessor da = (DataAccessor)o;
            da.init(url, name);
            return da;
        } catch (InstantiationException e) {
            log.error("Error instantiating data accessor class "
                    + handler.getName() + " for URI " + url.toString() + ":"
                    + e.getMessage());
            return null;
        } catch (IllegalAccessException e) {
            log.error("Error instantiating data accessor class "
                    + handler.getName() + " for URI " + url.toString() + ":"
                    + e.getMessage());
            return null;
        } catch (AccessorException e) {
            log.error("The accessor class " + handler.getName() + " failed"
                    + " while instantiating accessor for URI " + url.toString()
                    + ":" + e.getMessage());
            return null;
        }
    }
}
