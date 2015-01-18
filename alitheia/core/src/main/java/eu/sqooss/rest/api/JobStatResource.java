package eu.sqooss.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.scheduler.SchedulerStats;

@Path("/api")
public class JobStatResource {

    public JobStatResource() {}

    /**
     * Gets the scheduler stats.
     *
     * @return the scheduler stats
     */
    @GET
    @Produces({ "application/xml", "application/json" })
    @Path("/job_stats/")
    public SchedulerStats getSchedulerStats() {
        return AlitheiaCore.getInstance().getScheduler().getSchedulerStats();
    }
}
