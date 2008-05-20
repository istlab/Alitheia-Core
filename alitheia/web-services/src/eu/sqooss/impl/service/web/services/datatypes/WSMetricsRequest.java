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

import java.util.Arrays;

public class WSMetricsRequest {
    
    long[] resourcesIds;
    private boolean isStoredProject;
    private boolean isProjectFile;
    private boolean isFileGroup;
    private boolean isProjectVersion;
    private boolean skipResourcesIds;
    
    /**
     * @return the resourcesIds
     */
    public long[] getResourcesIds() {
        return resourcesIds;
    }
    
    /**
     * @param resourcesIds the resourcesIds to set
     */
    public void setResourcesIds(long[] resourcesIds) {
        this.resourcesIds = resourcesIds;
    }
    
    /**
     * @return the skipResourcesIds
     */
    public boolean getSkipResourcesIds() {
        return skipResourcesIds;
    }

    /**
     * @param skipResourcesIds the skipResourcesIds to set
     */
    public void setSkipResourcesIds(boolean skipResourcesIds) {
        this.skipResourcesIds = skipResourcesIds;
    }

    /**
     * @return the isStoredProject
     */
    public boolean getIsStoredProject() {
        return isStoredProject;
    }
    
    /**
     * @param isStoredProject the isStoredProject to set
     */
    public void setIsStoredProject(boolean isStoredProject) {
        this.isStoredProject = isStoredProject;
    }
    
    /**
     * @return the isProjectFile
     */
    public boolean getIsProjectFile() {
        return isProjectFile;
    }
    
    /**
     * @param isProjectFile the isProjectFile to set
     */
    public void setIsProjectFile(boolean isProjectFile) {
        this.isProjectFile = isProjectFile;
    }
    
    /**
     * @return the isFileGroup
     */
    public boolean getIsFileGroup() {
        return isFileGroup;
    }
    /**
     * @param isFileGroup the isFileGroup to set
     */
    public void setIsFileGroup(boolean isFileGroup) {
        this.isFileGroup = isFileGroup;
    }
    
    /**
     * @return the isProjectVersion
     */
    public boolean getIsProjectVersion() {
        return isProjectVersion;
    }
    
    /**
     * @param isProjectVersion the isProjectVersion to set
     */
    public void setIsProjectVersion(boolean isProjectVersion) {
        this.isProjectVersion = isProjectVersion;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer stringRepresentation = new StringBuffer();
        stringRepresentation.append("Resources' ids: ");
        stringRepresentation.append(Arrays.toString(resourcesIds));
        stringRepresentation.append("; isStoredProject");
        stringRepresentation.append(isStoredProject);
        stringRepresentation.append("; isProjectFile");
        stringRepresentation.append(isProjectFile);
        stringRepresentation.append("; isFileGroup");
        stringRepresentation.append(isFileGroup);
        stringRepresentation.append("; isProjectVersion");
        stringRepresentation.append(isProjectVersion);
        stringRepresentation.append("; skipResourcesIds");
        stringRepresentation.append(skipResourcesIds);
        return stringRepresentation.toString();
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
