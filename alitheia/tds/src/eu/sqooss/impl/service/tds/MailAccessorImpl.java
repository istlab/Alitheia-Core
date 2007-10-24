/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 by Adriaan de Groot <groot@kde.org>
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.StringBuilder;
import java.util.Date;
import java.util.List;

import eu.sqooss.service.tds.MailAccessor;

public class MailAccessorImpl implements MailAccessor {
    private File maildirRoot;
    private String[] subdirs = { "cur", "new", "tmp" };

    public MailAccessorImpl( File root ) {
        maildirRoot = root;
    }

    private String readFile( File f )
        throws FileNotFoundException {
        BufferedReader in = new BufferedReader(new FileReader(f));
        StringBuilder s = new StringBuilder();
        String line;

        try {
            while ( (line=in.readLine()) != null ) {
                s.append(line);
            }
        } catch (IOException e) {
            // Repurpose, pretend it was not found
            throw new FileNotFoundException(e.getMessage());
        }

        return s.toString();
    }

    public String getRawMessage( String listId, String id )
        throws IllegalArgumentException,
               FileNotFoundException {
        File listDir = new File(maildirRoot, listId);
        if (!listDir.exists() || !listDir.isDirectory()) {
            throw new IllegalArgumentException("ListID <" + listId + "> does not exist.");
        }

        for(String s : subdirs) {
            File msgFile = new File(listDir, s + File.separator + id);
            if (msgFile.exists()) {
                return readFile(msgFile);
            }
        }
        throw new FileNotFoundException("No message <" + id + ">");
        // return null;
    }

    // Remainder is all stubs
    public List<String> getMessages( String listId ) {
        return null;
    }

    public List<String> getMessages( String listId, Date d1, Date d2 ) {
        return null;
    }

    public String getSender( String listId, String id ) {
        return null;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

