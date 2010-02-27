package eu.sqooss.rest;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;

@Path("/projects")
public class StoredProjectResource {

	static {
		RestServiceApp.addServiceObject(StoredProjectResource.class);
	}
	
	@GET
	@Produces("application/xml")
	public List<StoredProject> getProjects() {
		return Collections.EMPTY_LIST;
	}
	
	@Path("{id}")
	@GET
	@Produces("appication/xml")
	public StoredProject getProject(@PathParam("id") Long id) {
		DBService db = AlitheiaCore.getInstance().getDBService();
		db.startDBSession();
		StoredProject sp = DAObject.loadDAObyId(id, StoredProject.class);
		db.commitDBSession();
		return sp;
	}
}
