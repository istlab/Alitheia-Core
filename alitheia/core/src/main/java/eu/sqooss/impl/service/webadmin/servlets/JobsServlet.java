package eu.sqooss.impl.service.webadmin.servlets;

import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.ImmutableMap;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.servlets.exceptions.PageNotFoundException;
import eu.sqooss.service.scheduler.Scheduler;

/**
 * This servlet is responsible for jobs and provides pages to see the current jobs and failed jobs
 */
@SuppressWarnings("serial")
public class JobsServlet extends AbstractWebadminServlet {

	private static final String ROOT_PATH = "/jobs";
	private static final String PAGE_JOBS = ROOT_PATH;
	private static final String PAGE_FAILEDJOBS = ROOT_PATH + "/failed";
	private static final Map<String, String> templates = new ImmutableMap.Builder<String, String>()
			.put(PAGE_JOBS, "/jobs/jobs.vm")
			.put(PAGE_FAILEDJOBS, "/jobs/failedjobs.vm")
			.build();

	private final Scheduler sobjSched;

	public JobsServlet(VelocityEngine ve, AlitheiaCore core) {
		super(ve, core);
		sobjSched = core.getScheduler();
	}

	@Override
	public String getPath() {
		return ROOT_PATH;
	}

	@Override
	protected Template render(HttpServletRequest req, VelocityContext vc) throws PageNotFoundException {
		// Switch over the URI
		switch(req.getRequestURI()) {
		case PAGE_JOBS:
			return PageJobs(req, vc);
		case PAGE_FAILEDJOBS:
			return PageFailedJobs(req, vc);
		default:
			throw new PageNotFoundException();
		}
	}

	private Template PageJobs(HttpServletRequest req, VelocityContext vc) {
		// Load template
		Template t = loadTemplate(templates.get(PAGE_JOBS));

		// Add scheduler stats
		vc.put("scheduler", sobjSched.getSchedulerStats());
		
		return t;
	}

	private Template PageFailedJobs(HttpServletRequest req, VelocityContext vc) {
		// Load template
		Template t = loadTemplate(templates.get(PAGE_FAILEDJOBS));

		// Add scheduler stats
		vc.put("failedJobs", Arrays.asList(sobjSched.getFailedQueue()));
		return t;
	}

}
