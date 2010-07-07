/*
 * This file is part of the Alitheia system.
 *
 * Copyright 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

package eu.sqooss.service.tds;

import eu.sqooss.impl.service.tds.diff.UnifiedDiffParser;

/**
 * Class that knows how to parse different diff formats.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 *
 */
public class DiffFactory {

    private static DiffFactory instance;
       
    public static DiffFactory getInstance() {
        if (instance == null)
            instance = new DiffFactory();
        
        return instance;
    }
    
    /**
     * 
     * 
     * @param start
     * @param end
     * @param basePath
     * @param diff
     * @return A {@link Diff} object if parsing the diff succeded or null if parsing failed.
     * 
     * @see {@link http://en.wikipedia.org/wiki/Diff#Unified_format}
     */
    public Diff doUnifiedDiff(Revision start, Revision end, 
            String basePath, String diff) {
        
        UnifiedDiffParser d = new UnifiedDiffParser(start, end, basePath, diff);
        if (d.parseDiff())
            return d;
        
        return null;
    }
}
