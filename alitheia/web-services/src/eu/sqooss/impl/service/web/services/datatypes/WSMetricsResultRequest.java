/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

public class WSMetricsResultRequest {
    
    private long daObjectId;
    private String[] mnemonics;
    private boolean isStoredProject;
    private boolean isProjectFile;
    private boolean isFileGroup;
    private boolean isProjectVersion;
    
    public long getDaObjectId() {
        return daObjectId;
    }
    
    public void setDaObjectId(long daObjectId) {
        this.daObjectId = daObjectId;
    }
    
    public String[] getMnemonics() {
        return mnemonics;
    }

    public void setMnemonics(String[] mnemonics) {
        this.mnemonics = mnemonics;
    }

    public boolean isStoredProject() {
        return isStoredProject;
    }
    
    public void setStoredProject(boolean isStoredProject) {
        this.isStoredProject = isStoredProject;
    }
    
    public boolean isProjectFile() {
        return isProjectFile;
    }
    
    public void setProjectFile(boolean isProjectFile) {
        this.isProjectFile = isProjectFile;
    }
    
    public boolean isFileGroup() {
        return isFileGroup;
    }
    
    public void setFileGroup(boolean isFileGroup) {
        this.isFileGroup = isFileGroup;
    }
    
    public boolean isProjectVersion() {
        return isProjectVersion;
    }
    
    public void setProjectVersion(boolean isProjectVersion) {
        this.isProjectVersion = isProjectVersion;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer stringRepresentation = new StringBuffer();
        stringRepresentation.append("DAObject id: ");
        stringRepresentation.append(daObjectId);
        stringRepresentation.append("; DAObject type: ");
        if (isFileGroup) {
            stringRepresentation.append("file group; ");
        }
        if (isProjectFile) {
            stringRepresentation.append("project file; ");
        }
        if (isProjectVersion) {
            stringRepresentation.append("project version; ");
        }
        if (isStoredProject) {
            stringRepresentation.append("stored project; ");
        }
        stringRepresentation.append("mnemonics: ");
        if (mnemonics != null) {
            for (int i = 0; i < mnemonics.length; i++) {
                stringRepresentation.append(mnemonics[i]);
                stringRepresentation.append("; ");
            }
        } else {
            stringRepresentation.append(mnemonics);
            stringRepresentation.append("; ");
        }
        return stringRepresentation.toString();
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
