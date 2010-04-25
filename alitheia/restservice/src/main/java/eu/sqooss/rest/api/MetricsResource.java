package eu.sqooss.rest.api;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.MetricType.Type;

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
	
	@GET
    @Produces({"application/xml", "application/json"})
	@Path("/metrics/types")
	public List<MetricType> getMetricTypes() {
		DBService db = AlitheiaCore.getInstance().getDBService();
		String q = " from MetricType";
		db.startDBSession();
		List<MetricType> sp = (List<MetricType>) db.doHQL(q);
		db.commitDBSession();
		return sp;
	}

	@Path("/metrics/by-id/{id}")
	@GET
    @Produces({"application/xml", "application/json"})
	public Metric getMetricById(@PathParam("id") Long id) {
		DBService db = AlitheiaCore.getInstance().getDBService();
		db.startDBSession();
		Metric sp = DAObject.loadDAObyId(id, Metric.class);
		db.commitDBSession();
		return sp;
	}
	
	@Path("/metrics/by-mnem/{mnem}")
	@GET
    @Produces({"application/xml", "application/json"})
	public Metric getMetricByMnem(@PathParam("mnem") String name) {
		DBService db = AlitheiaCore.getInstance().getDBService();
		db.startDBSession();
		Metric m = Metric.getMetricByMnemonic(name);
		db.commitDBSession();
		return m;
	}
	
	@Path("/metrics/by-type/{type}")
	@GET
    @Produces({"application/xml", "application/json"})
	public Set<Metric> getProject(@PathParam("type") String type) {
		DBService db = AlitheiaCore.getInstance().getDBService();
		db.startDBSession();
		Set<Metric> metrics = Collections.EMPTY_SET;
		
		MetricType mt = MetricType.getMetricType(Type.fromString(type));
		metrics = mt.getMetrics();
		
		db.commitDBSession();
		return metrics;
	}
}
