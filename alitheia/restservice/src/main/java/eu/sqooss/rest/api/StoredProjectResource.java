/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

package eu.sqooss.rest.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.collections.map.HashedMap;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

@Path("/api")
public class StoredProjectResource {

	public StoredProjectResource() {}
	
	@GET
	@Produces("application/xml")
	@Path("/projects/")
	public List<StoredProject> getProjects() {
		DBService db = AlitheiaCore.getInstance().getDBService();
		String q = " from StoredProject";
		db.startDBSession();
		List<StoredProject> sp = (List<StoredProject>) db.doHQL(q);
		db.commitDBSession();
		return sp;
	}

	@Path("/projects/{id}")
	@GET
	//@Produces("appication/xml")
	public StoredProject getProject(@PathParam("id") Long id) {
		DBService db = AlitheiaCore.getInstance().getDBService();
		db.startDBSession();
		StoredProject sp = DAObject.loadDAObyId(id, StoredProject.class);
		db.commitDBSession();
		return sp;
	}
	
	@Path("/projects/{id}/versions")
	@GET
	//@Produces("appication/xml")
	public List<ProjectVersion> getVersions(@PathParam("id") Long id) {
		DBService db = AlitheiaCore.getInstance().getDBService();
		db.startDBSession();
		StoredProject sp = DAObject.loadDAObyId(id, StoredProject.class);
		String q = "from ProjectVersion where project =:pr";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pr", sp);
		List<ProjectVersion> versions = (List<ProjectVersion>)db.doHQL(q, params);
		db.commitDBSession();
		return versions;
	}
	
	@Path("/projects/{id}/versions/{vid}")
	@GET
	//@Produces("appication/xml")
	public ProjectVersion getVersion(@PathParam("id") Long prid,
			@PathParam("vid") Long verid) {
		DBService db = AlitheiaCore.getInstance().getDBService();
		db.startDBSession();
		String q = "from ProjectVersion v where v.id = :id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pr", prid);
		params.put("id", verid);
		List<ProjectVersion> versions = (List<ProjectVersion>)db.doHQL(q, params);
		db.commitDBSession();
		return versions.get(0);
	}
}
