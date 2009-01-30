/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2009 - Organization for Free and Open Source Software,  
 *                 Athens, Greece.
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

package eu.sqooss.metrics.contrib.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.ProjectFile;

/**
 * Helper table to keep trak of the number of lines 
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 *
 */
public class ContribLinesPerDevPerFile extends DAObject{
	private Developer developer;
	private ProjectFile file;
	private Integer numLines;
	
	public ContribLinesPerDevPerFile() {}
	
	public ContribLinesPerDevPerFile(ProjectFile pf, Developer d, 
			Integer lines) {
		this.developer = d;
		this.file = pf;
		this.numLines = lines;
	}
	
	public Developer getDeveloper() {
		return developer;
	}
	public void setDeveloper(Developer dev) {
		this.developer = dev;
	}
	public ProjectFile getFile() {
		return file;
	}
	public void setFile(ProjectFile file) {
		this.file = file;
	}
	public Integer getNumLines() {
		return numLines;
	}
	public void setNumLines(Integer numLines) {
		this.numLines = numLines;
	}

	public static Integer getLinesPerDevPerFile(ProjectFile pf, Developer d) {
		DBService dbs = AlitheiaCore.getInstance().getDBService();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("file", pf);
		params.put("developer", d);
		
		List<ContribLinesPerDevPerFile> lines = dbs.findObjectsByProperties(
				ContribLinesPerDevPerFile.class, params);
		
		if (lines.isEmpty())
			return null;
		
		return lines.get(0).getNumLines();
	}
}
