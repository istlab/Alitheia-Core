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

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.ProjectDeleteJob;
import eu.sqooss.rest.api.wrappers.ResponseBuilder;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.service.admin.actions.UpdateProject;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.SchedulerException;

@Path("/api")
public class StoredProjectResource {

	public StoredProjectResource() {}
	
	@GET
	@Produces({"application/xml", "application/json"})
	@Path("/projects/")
	public List<StoredProject> getProjects() {
		DBService db = AlitheiaCore.getInstance().getDBService();
		String q = " from StoredProject";
		List<StoredProject> sp = (List<StoredProject>) db.doHQL(q);
		return sp;
	}
	
	@Path("/projects/updateAllResources")
	@GET
	@Produces({"application/xml", "application/json"})
	public Response updateAllProjectsOnThisNode() {
		Set<StoredProject> projectList = ClusterNode.thisNode().getProjects();
		
		for (StoredProject project : projectList) {
			updateAllResourcesProject(Long.toString(project.getId()));
		}
		
		return ResponseBuilder.simpleResponse(200, "All resources of this node projects, have been updated");
	}

	@Path("/project/{id}")
	@GET
    @Produces({"application/xml", "application/json"})
	public StoredProject getProject(@PathParam("id") String id) {
		StoredProject sp = null;
		if (id.matches("^[0-9]*$")){ //numeric id
			sp = DAObject.loadDAObyId(Long.valueOf(id), StoredProject.class);
		} else {
			sp = StoredProject.getProjectByName(id);
		}
		return sp;
	}
	
	@Path("/projects/add/{scm}/{name}/{bts}/{mail}/{contact}/{web}")
	@POST
	@Produces({"application/xml", "application/json"})
	public StoredProject addProject(@PathParam("scm") String scm, @PathParam("name") String name,
			@PathParam("bts") String bts, @PathParam("mail") String mail, 
			@PathParam("contact") String contact, @PathParam("web") String web) {
		
		AdminService as = AlitheiaCore.getInstance().getAdminService();
    	AdminAction aa = as.create(AddProject.MNEMONIC);
    	aa.addArg("scm", scm);
    	aa.addArg("name", name);
    	aa.addArg("bts", bts);
    	aa.addArg("mail", mail);
    	aa.addArg("web", web);
    	as.execute(aa);
    	
    	if (aa.hasErrors()) {
            return null;
    	} else { 
            return StoredProject.getProjectByName(name);
    	}
	}
	
	@Path("/project/{id}/delete")
	@DELETE
	@Produces({"application/xml", "application/json"})
	public Response deleteProject(@PathParam("id") String id) {
		StoredProject sp = DAObject.loadDAObyId(Long.valueOf(id), StoredProject.class);
		if(sp != null){
			AlitheiaCore core = AlitheiaCore.getInstance();
			ProjectDeleteJob pdj = new ProjectDeleteJob(core, sp);
			try {
				core.getScheduler().enqueue(pdj);
			} catch (SchedulerException e1) {
				return ResponseBuilder.internalServerErrorResponse("Project delete failed with error: " + e1.getMessage());
			}
		} else {
			return ResponseBuilder.internalServerErrorResponse("Project does not exist");
		}
		return ResponseBuilder.simpleResponse(200, "Project deleted with success");
	}
	
	@Path("/project/{id}/updateResource/{mnem}")
	@POST
	@Produces({"application/xml", "application/json"})
	public Response updateResourceProject(@PathParam("id") String id, @PathParam("mnem") String mnem) {
		AdminService as = AlitheiaCore.getInstance().getAdminService();
		AdminAction aa = as.create(UpdateProject.MNEMONIC);
		aa.addArg("project", id);
		aa.addArg("updater", mnem);
		as.execute(aa);

		if (aa.hasErrors()) {
			return ResponseBuilder.internalServerErrorResponse("Update resource on project failed");
        } else { 
        	return ResponseBuilder.simpleResponse(200, "Project resource updated with success");
        }
	}
	
	@Path("/project/{id}/updateAllResources")
	@POST
	@Produces({"application/xml", "application/json"})
	public Response updateAllResourcesProject(@PathParam("id") String id) {
		AdminService as = AlitheiaCore.getInstance().getAdminService();
		AdminAction aa = as.create(UpdateProject.MNEMONIC);
		aa.addArg("project", id);
		as.execute(aa);

		if (aa.hasErrors()) {
			return ResponseBuilder.internalServerErrorResponse("Update project resources failed");
        } else { 
        	return ResponseBuilder.simpleResponse(200, "Project resources updated with success");
        }
	}
	
	@Path("/project/{id}/syncPlugin/{plugin}")
	@POST
	@Produces({"application/xml", "application/json"})
	public Response syncPlugin(@PathParam("id") String id, @PathParam("plugin") String plugin) {
		StoredProject sp = DAObject.loadDAObyId(Long.valueOf(id), StoredProject.class);
		AlitheiaCore core = AlitheiaCore.getInstance();
		PluginInfo pInfo = core.getPluginAdmin().getPluginInfo(plugin);
		if (pInfo != null) {
			AlitheiaPlugin pObj = core.getPluginAdmin().getPlugin(pInfo);
			if (pObj != null) {
				core.getMetricActivator().syncMetric(pObj, sp);
				core.getLogManager().createLogger(Logger.NAME_SQOOSS_WEBADMIN).debug("Syncronise plugin (" + pObj.getName()
						+ ") on project (" + sp.getName() + ").");
			}
			else{
				return ResponseBuilder.internalServerErrorResponse("Plugin failed to sync!");
			}
		}
		else{
			return ResponseBuilder.internalServerErrorResponse("Plugin failed to sync!");
		}
		return ResponseBuilder.simpleResponse("Plugin synced with success");
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
	public ProjectVersion getVersion(@PathParam("id") String prid,
			@PathParam("vid") String verid) {
		
	    if (verid.equals("first")) 
	        return ProjectVersion.getFirstProjectVersion(getProject(prid));
	    
	    if (verid.equals("latest"))
	        return ProjectVersion.getLastMeasuredVersion (
	                Metric.getMetricByMnemonic("TLOC"), //This can break, FIXME
	                getProject(prid));
	    
		return ProjectVersion.getVersionByRevision(getProject(prid), verid);
	}

	@Path("/project/{id}/version/{vid}/files/")
    @GET
    @Produces({"application/xml", "application/json"})
    public List<ProjectFile> getAllFiles(@PathParam("id") String prid,
            @PathParam("vid") String verid) {
        
	    ProjectVersion pv = getVersion(prid, verid);
	    if (pv == null)
	        return Collections.EMPTY_LIST;
	        
        return pv.getFiles((Directory)null, ProjectVersion.MASK_FILES);
    }

	@Path("/project/{id}/version/{vid}/files/{dir: .+}")
    @GET
    @Produces({"application/xml", "application/json"})
    public List<ProjectFile> getFilesInDir(@PathParam("id") String prid,
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
    public Set<ProjectFile> getChangedFiles(@PathParam("id") String prid,
            @PathParam("vid") String verid) {
        
        ProjectVersion pv = getVersion(prid, verid);
        if (pv == null)
            return Collections.EMPTY_SET;
        
        return pv.getVersionFiles();
    }

	@Path("/project/{id}/version/{vid}/dirs/")
    @GET
    @Produces({"application/xml", "application/json"})
    public List<ProjectFile> getDirs(@PathParam("id") String prid,
            @PathParam("vid") String verid) {
        
	    ProjectVersion pv = getVersion(prid, verid);
        if (pv == null)
            return Collections.EMPTY_LIST;
 
        return pv.getFiles((Directory)null,
                ProjectVersion.MASK_DIRECTORIES);
	}

	@Path("/project/{id}/version/{vid}/dirs/{dir: .+}")
    @GET
    @Produces({"application/xml", "application/json"})
    public List<ProjectFile> getDirs(@PathParam("id") String prid,
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
