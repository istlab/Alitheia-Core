package eu.sqooss.impl.service.webadmin.servlets;

import java.util.Date;
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
 * This servlet provides the sidebar with some status information in the webadmin
 */
@SuppressWarnings("serial")
public class StatusServlet extends AbstractWebadminServlet {

	/**
	 * Represents the system time at which the WebAdminRender (and
	 * thus the system) was started. This is required for the system
	 * uptime display.
	 */
	private final long startTime = new Date().getTime();

	private static final String ROOT_PATH = "/status";
	private static final String PAGE_SIDEBAR = ROOT_PATH;
	private static final Map<String, String> templates = new ImmutableMap.Builder<String, String>()
			.put(PAGE_SIDEBAR, "/status.vm")
			.build();

	private Scheduler sobjSched;

	public StatusServlet(VelocityEngine ve, AlitheiaCore core) {
		super(ve, core);
		sobjSched = core.getScheduler();
	}

	/**
	 * Returns a string representing the uptime of the Alitheia core
	 * in dd:hh:mm:ss format
	 */
	private String getUptime() {
		long remainder;
		long currentTime = new Date().getTime();
		long timeRunning = currentTime - startTime;

		// Get the elapsed time in days, hours, mins, secs
		int days = new Long(timeRunning / 86400000).intValue();
		remainder = timeRunning % 86400000;
		int hours = new Long(remainder / 3600000).intValue();
		remainder = remainder % 3600000;
		int mins = new Long(remainder / 60000).intValue();
		remainder = remainder % 60000;
		int secs = new Long(remainder / 1000).intValue();

		return String.format("%d:%02d:%02d:%02d", days, hours, mins, secs);
	}

	@Override
	public String getPath() {
		return PAGE_SIDEBAR;
	}


	@Override
	protected Template render(HttpServletRequest req, VelocityContext vc) throws PageNotFoundException {
		// Switch over the URI
		switch(req.getRequestURI()) {
		case PAGE_SIDEBAR:
			return PageSidebar(req, vc);
		default:
			throw new PageNotFoundException();
		}
	}

	private Template PageSidebar(HttpServletRequest req, VelocityContext vc) {
		// Load template
		Template t = loadTemplate(templates.get(PAGE_SIDEBAR));

		// Add uptime
		vc.put("uptime", getUptime());

		// Add scheduler
		vc.put("scheduler", sobjSched.getSchedulerStats());

		return t;
	}
}
