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

public class WSFileGroup {
    
    private long id;
    private String name;
    private int recalcFreq;
    private String subPath;
    private long lastUsed;
    private String regularExpression;
    private long projectVersionId;
    
    /**
     * @return the id
     */
    public long getId() {
        return id;
    }
    
    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the recalcFreq
     */
    public int getRecalcFreq() {
        return recalcFreq;
    }
    
    /**
     * @param recalcFreq the recalcFreq to set
     */
    public void setRecalcFreq(int recalcFreq) {
        this.recalcFreq = recalcFreq;
    }
    
    /**
     * @return the subPath
     */
    public String getSubPath() {
        return subPath;
    }
    
    /**
     * @param subPath the subPath to set
     */
    public void setSubPath(String subPath) {
        this.subPath = subPath;
    }
    
    /**
     * @return the lastUsed
     */
    public long getLastUsed() {
        return lastUsed;
    }
    
    /**
     * @param lastUsed the lastUsed to set
     */
    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }
    
    /**
     * @return the regularExpression
     */
    public String getRegularExpression() {
        return regularExpression;
    }
    
    /**
     * @param regularExpression the regularExpression to set
     */
    public void setRegularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
    }
    
    /**
     * @return the projectVersionId
     */
    public long getProjectVersionId() {
        return projectVersionId;
    }
    
    /**
     * @param projectVersionId the projectVersionId to set
     */
    public void setProjectVersionId(long projectVersionId) {
        this.projectVersionId = projectVersionId;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
