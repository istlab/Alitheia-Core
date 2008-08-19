/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Georgios Gousios <gousiosg@gmail.com>
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

/**
 * A commit entry representing a copy operation 
 *
 */
public class CommitCopyEntry {
    
    private String fromPath;
    private ProjectRevision fromRev;
    
    private String toPath;
    private ProjectRevision toRev;
    
    public CommitCopyEntry(String fromP, ProjectRevision fromR, String toP, 
            ProjectRevision toR) {
        this.fromPath = fromP;
        this.fromRev = fromR;
        
        this.toPath = toP;
        this.toRev = toR;
    }
    
    /**
     * Get the path where the copied entity originally existed
     */
    public String fromPath() {
        return fromPath;
    }

    /**
     * Get the latest revision of to the original path
     */
    public ProjectRevision fromRev() {
        return fromRev;
    }
    
    /**
     * Get the path to the new location of the copied entity
     */
    public String toPath() {
        return toPath;
    }
    
    /**
     * Get the version of the copied entity after the copy operation
     */
    public ProjectRevision toRev() {
        return toRev;
    }
}
