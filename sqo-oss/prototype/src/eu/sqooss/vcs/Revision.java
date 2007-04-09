/*
 * Copyright (c) Members of the SQO-OSS Collaboration, 2007
 * All rights reserved by respective owners.
 * See http://www.sqo-oss.eu/ for details on the copyright holders.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the SQO-OSS project nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package eu.sqooss.vcs;

import java.util.*;

/**
 * Represents a revision of a resource (file or module) stored in a software
 * configuration management system (SCM). Depending on the type of SCM a
 * revision may refer to a single item (file or folder) or the entire
 * repository.
 */
public class Revision {

    private String description;

    private long number;

    // TODO: Change this with a custom collection
    private Vector<FileEntry> files;

    /**
     * The latest revision of the resource
     */
    public static final long HEAD = -1;

    /**
     * The base revision of the resource (i.e. the last revision that was
     * successfully checked out)
     */
    public static final long BASE = -2;

    /**
     * Creates a new instance of the class
     * 
     * @param number The number of the revision
     */
    public Revision(long number) {
        if (number < -2) {
            throw new IllegalArgumentException();
        }

        this.number = number;
        description = String.valueOf(number);
        files = new Vector<FileEntry>();
    }
    
    /**
     * Creates a new instance of the class
     * @param description The description of the revision
     */
    public Revision(String description) {
        if(description == null || description == "" || 
                description.equalsIgnoreCase("HEAD")) {
            number = HEAD;
            this.description = "HEAD";
        } else if (description == "BASE") {
            number = BASE;
            this.description = "BASE";
        } else {
            long n = 0;
            try {
                n = Long.parseLong(description);
                number = n;
                this.description = String.valueOf(number);
            } catch (Exception e) {
                number = HEAD;
                this.description = "HEAD";
            }
        }
    }

    /**
     * Gets a list of all the files contained in the given revision
     * 
     * @return A list of {@link FileEntry} objects, each representing a resource
     *         (file or folder) that is contained in the given revision
     */
    public List<FileEntry> getFiles() {
        return files;
    }

    /**
     * @return A textual representation of the revision
     */
    public String getDescription() {
        if (description != "") {
            return description;
        } else if (number > 0) {
            return String.valueOf(number);
        } else {
            return "";
        }
    }

    /**
     * @return A numeric representation of the revision
     */
    public long getNumber() {
        return number;
    }
}
