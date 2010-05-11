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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

@Path("/api")
public class StoredProjectResource {

	public StoredProjectResource() {}
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/project/")
	public List<StoredProject> getProjects() {
		DBService db = AlitheiaCore.getInstance().getDBService();
		String q = " from StoredProject";
		List<StoredProject> sp = (List<StoredProject>) db.doHQL(q);
		return sp;
	}

	@Path("/project/{id}")
	@GET
    @Produces({"application/xml", "application/json"})
	public StoredProject getProject(@PathParam("id") Long id) {
		StoredProject sp = DAObject.loadDAObyId(id, StoredProject.class);
		return sp;
	}
	
	@Path("/project/{id}/versions")
	@GET
	@Produces({"application/xml", "application/json"})
	public List<ProjectVersion> getAllVersions(@PathParam("id") Long id) {
		StoredProject sp = DAObject.loadDAObyId(id, StoredProject.class);
	
		return sp.getProjectVersions();
	}
	
	@Path("/project/{id}/versions/{vid: .+}")
	@GET
	@Produces({"application/xml", "application/json"})
	public List<ProjectVersion> getVersions(@PathParam("id") Long id,
			@PathParam("vid") String vid) {
		StoredProject sp = DAObject.loadDAObyId(id, StoredProject.class);
	
		if (sp == null)
			return Collections.EMPTY_LIST;
		
		Set<String>  ids = new HashSet<String>();
	    int count = 0;
	    for (String resourceId : vid.split(",")) {
	        ids.add(resourceId);
	        count++;
	        if (count >=64) {
	            break;
	        }
	    }
		
	    List<ProjectVersion> versions = new ArrayList<ProjectVersion>();
	    
	    for (String verid : ids) {
	    	ProjectVersion pv = ProjectVersion.getVersionByRevision(sp, verid);
	    	if (pv != null)
	    	    versions.add(pv);
	    }
	    
		return versions;
	}

	@Path("/project/{id}/version/{vid}")
	@GET
	@Produces({"application/xml", "application/json"})
	public ProjectVersion getVersion(@PathParam("id") Long prid,
			@PathParam("vid") String verid) {
		
	    if (verid.equals("first")) 
	        return ProjectVersion.getFirstProjectVersion(
	                DAObject.loadDAObyId(prid, StoredProject.class));
	    
	    if (verid.equals("latest"))
	        return ProjectVersion.getLastMeasuredVersion (
	                Metric.getMetricByMnemonic("TLOC"), //This can break, FIXME
	                DAObject.loadDAObyId(prid, StoredProject.class));
	    
		return ProjectVersion.getVersionByRevision(
		        DAObject.loadDAObyId(prid, StoredProject.class), verid);
	}

	@Path("/project/{id}/version/{vid}/files/")
    @GET
    @Produces({"application/xml", "application/json"})
    public List<ProjectFile> getAllFiles(@PathParam("id") Long prid,
            @PathParam("vid") String verid) {
        
	    ProjectVersion pv = getVersion(prid, verid);
	    if (pv == null)
	        return Collections.EMPTY_LIST;
	        
        return pv.getFiles(null, ProjectVersion.MASK_FILES);
    }

	@Path("/project/{id}/version/{vid}/files/{dir: .+}")
    @GET
    @Produces({"application/xml", "application/json"})
    public List<ProjectFile> getFilesInDir(@PathParam("id") Long prid,
            @PathParam("vid") String verid,
            @PathParam("dir") String path) {
        
        ProjectVersion pv = getVersion(prid, verid);
        if (pv == null)
            return Collections.EMPTY_LIST;
        
        if (!path.startsWith("/"))
            path = "/" + path;
        
        return pv.getFiles(Directory.getDirectory(path, false), 
                ProjectVersion.MASK_FILES);
    }

	@Path("/project/{id}/version/{vid}/files/changed")
    @GET
    @Produces({"application/xml", "application/json"})
    public Set<ProjectFile> getChangedFiles(@PathParam("id") Long prid,
            @PathParam("vid") String verid) {
        
        ProjectVersion pv = getVersion(prid, verid);
        if (pv == null)
            return Collections.EMPTY_SET;
        
        return pv.getVersionFiles();
    }

	@Path("/project/{id}/version/{vid}/dirs/")
    @GET
    @Produces({"application/xml", "application/json"})
    public List<ProjectFile> getDirs(@PathParam("id") Long prid,
            @PathParam("vid") String verid) {
        
	    ProjectVersion pv = getVersion(prid, verid);
        if (pv == null)
            return Collections.EMPTY_LIST;
 
        return pv.getFiles(null, 
                ProjectVersion.MASK_DIRECTORIES);
	}

	@Path("/project/{id}/version/{vid}/dirs/{dir: .+}")
    @GET
    @Produces({"application/xml", "application/json"})
    public List<ProjectFile> getDirs(@PathParam("id") Long prid,
            @PathParam("vid") String verid,
            @PathParam("dir") String path) {
        
        ProjectVersion pv = getVersion(prid, verid);
        if (pv == null)
            return Collections.EMPTY_LIST;
        
        if (!path.startsWith("/"))
            path = "/" + path;
        
        return pv.getFiles(Directory.getDirectory(path, false), 
                ProjectVersion.MASK_DIRECTORIES);
	}
}
