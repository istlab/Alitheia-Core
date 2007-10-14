/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * 
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

package eu.sqooss.impl.service.db;

import eu.sqooss.service.db.FSAccessData;

class FSAccessDataImpl implements FSAccessData {
    private static String[] SVNUrls = { "svn://anonsvn.kde.org/" };
    private static String[] IMAPPaths = { "" };
    private static String[] BTSUrls = { "" };

    private String SVNUrl;
    private String IMAPPath;
    private String BTSUrl;

    public String getBTSUrl() {
        return BTSUrl;
    }

    public String getIMAPPath() {
        return IMAPPath;
    }

    public String getSVNUrl() {
        return SVNUrl;
    }

    public FSAccessDataImpl(int id) {
        if (id==0) {
            throw new IllegalArgumentException("That ID is bad");
        }
        if (id<=SVNUrls.length) {
            SVNUrl = SVNUrls[id-1];
            IMAPPath = IMAPPaths[id-1];
            BTSUrl = BTSUrls[id-1];
        } else {
            throw new IllegalArgumentException("That ID is bad");
        }
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

