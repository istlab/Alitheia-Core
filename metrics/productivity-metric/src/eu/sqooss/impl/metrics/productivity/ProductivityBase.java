/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.impl.metrics.productivity;

import java.util.Date;

import eu.sqooss.metrics.abstractmetric.MetricResult;
import eu.sqooss.metrics.productivity.ProductivityMetric;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.TDSService;

import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;

public class ProductivityBase implements ProductivityMetric {
  
    protected TDSService service;
    protected SCMAccessor svn;
    protected Logger log;
    private String revision = "$Revision: ";
    
    protected ProductivityBase() {
        
	//FIXME: Dummy getAccessor value to fix the build 
	svn = (SCMAccessor) service.getAccessor(1);
    }
    
    protected void run() {
        
    }

    public String getAuthor() {
        return "Georgios Gousios";
    }

    public Date getDateInstalled() {
        return null;
    }

    public String getDescription() {
        return "Assesses programmer productivity based on an array of " +
        		"evaluation criteria.";
    }

    public String getName() {
        return "Productivity Metric";
    }

    public String getVersion() {
        return this.revision.split(":")[1];
    }

    public boolean install() {
        return false;
    }

    public boolean remove() {
        return false;
    }

    public boolean update() {
        return false;
    }
    
    public boolean run (ProjectVersion a) {
        return false;   
    }
    
    public MetricResult getResult(ProjectVersion a) {
        return null;
        
    }

    public boolean delete(StoredProject a) {
        return false;
    }

    public MetricResult getResult(StoredProject a) {
        return null;
    }

    public boolean run(StoredProject a) {
        return false;
    }

    public boolean delete(ProjectVersion a) {
        return false;
    }

    public boolean run(ProjectVersion a, ProjectVersion b) {
        return false;
    }
}
