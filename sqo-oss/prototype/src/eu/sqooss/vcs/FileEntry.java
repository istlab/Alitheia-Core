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
 * 
 * Represents a resource (file or folder) that is stored on a path of either a
 * remote SCM repository or a local working copy of a repository.
 */
public abstract class FileEntry {

    private HashMap<String, String> attributes;
    private String fullPath;
    EntryKind kind;
    private String name;
    String revision;
    int size;

    /**
     * An enumeration of the possible kinds of entries found in an SCM
     */
    public enum EntryKind {
        /**
         * Unknown kind of entry
         */
        Unknown,
        /**
         * The entry represents a file
         */
        File,
        /**
         * The entry represents a folder or other kind of collection of files
         */
        Dir
    }

    /**
     * Constructs a new instance of the class
     * 
     * @param name
     *            The name (path) of the entry
     */
    public FileEntry(String name) {
        this.name = name;
        fullPath = "";
        attributes = new HashMap<String, String>();
        kind = EntryKind.Unknown;
    }

    /**
     * @return The name (path) of the entry
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name (path) of the entry
     * 
     * @param name
     *            The new name (path) of the entry
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * @return The full path of the resource represented by the entry
     */
    public String getFullPath() {
        return fullPath;
    }

    /**
     * Sets the full path of the resource represented by the entry
     * 
     * @param fullPath
     *            The full path of the resource represented by the entry
     */
    void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    /**
     * @return A collection of key-value pairs containing attributes (metadata)
     *         for the entry
     */
    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    /**
     * @return The size (in bytes) of the resource represented by the entry
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the size (in bytes) of the resource represented by the entry
     * 
     * @param size
     *            The new size of the entry
     */
    void setSize(int size) {
        this.size = size;
    }

    /**
     * @return A string representing the revision of the entry
     */
    public String getRevision() {
        return revision;
    }

    /**
     * Sets a new revision to the entry
     * 
     * @param revision
     *            The new revision of the entry
     */
    void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * @return The kind of the entry
     */
    public EntryKind getKind() {
        return kind;
    }

    /**
     * Sets the kind of the entry
     * 
     * @param kind
     *            The new kind of the entry
     */
    void setKind(EntryKind kind) {
        this.kind = kind;
    }

    /**
     * Parses a string representing an {@link EntryKind}
     * 
     * @param kind
     *            The input string
     * @return The corresponding {@link EntryKind}
     */
    public static EntryKind parseEntryKind(String kind) {
        String k = kind.toLowerCase();

        if (k == "file") {
            return EntryKind.File;
        }

        if (k == "dir") {
            return EntryKind.Dir;
        }

        return EntryKind.Unknown;
    }
}
