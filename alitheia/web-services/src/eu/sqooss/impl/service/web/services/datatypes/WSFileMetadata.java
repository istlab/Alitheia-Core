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

package eu.sqooss.impl.service.web.services.datatypes;

import eu.sqooss.service.db.FileMetadata;

/**
 * This class wraps the <code>eu.sqooss.service.db.FileMetadata</code>
 */
public class WSFileMetadata {
    
    private FileMetadata fileMetadata;
    
    public WSFileMetadata(FileMetadata fileMetadata) {
        this.fileMetadata = fileMetadata;
    }

    public long getId() {
        return fileMetadata.getId();
    }
    
    public String getProtection() {
        return fileMetadata.getProtection();
    }

    public int getLinks() {
        return fileMetadata.getLinks();
    }

    public long getUserId() {
        return fileMetadata.getUserId();
    }

    public long getGroupId() {
        return fileMetadata.getGroupId();
    }

    /**
     * @see java.util.Date#getTime()
     * @return
     */
    public long getAccessTime() {
        return fileMetadata.getAccessTime().getTime();
    }

    /**
     * @see java.util.Date#getTime()
     * @return
     */
    public long getModificationTime() {
        return fileMetadata.getModificationTime().getTime();
    }

    public int getSize() {
        return fileMetadata.getSize();
    }

    public String getFileStatusChange() {
        return fileMetadata.getFileStatusChange();
    }

    public int getBlocks() {
        return fileMetadata.getBlocks();
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
