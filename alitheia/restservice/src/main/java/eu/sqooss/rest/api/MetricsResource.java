package eu.sqooss.rest.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;

@Path("/api")
public class MetricsResource {

	public MetricsResource() {}

	@GET
    @Produces({"application/xml", "application/json"})
	@Path("/metrics/")
	public List<Metric> getMetrics() {
		DBService db = AlitheiaCore.getInstance().getDBService();
		String q = " from Metric";
		db.startDBSession();
		List<Metric> sp = (List<Metric>) db.doHQL(q);
		db.commitDBSession();
		return sp;
	}

	@Path("/metrics/{id}")
	@GET
    @Produces({"application/xml", "application/json"})
	public Metric getProject(@PathParam("id") Long id) {
		DBService db = AlitheiaCore.getInstance().getDBService();
		db.startDBSession();
		Metric sp = DAObject.loadDAObyId(id, Metric.class);
		db.commitDBSession();
		return sp;
	}
}
