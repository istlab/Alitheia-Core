package eu.sqooss.impl.service.webadmin.servlets;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.html.HtmlEscapers;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.servlets.exceptions.PageNotFoundException;
import eu.sqooss.service.logging.LogManager;

/**
 * This servlet shows the alitheia logs
 */
@SuppressWarnings("serial")
// TODO: Alter this servlet so every log can be completely viewed, not just the recent log entries
public class LogsServlet extends AbstractWebadminServlet {

	private static final String ROOT_PATH = "/logs";

	private static final String PAGE_LOGS = ROOT_PATH;
	private final LogManager sobjLogManager;

	private static final Map<String, String> templates = new ImmutableMap.Builder<String, String>()
			.put(PAGE_LOGS, "/logs.vm")
			.build();

	public LogsServlet(VelocityEngine ve, AlitheiaCore core) {
		super(ve, core);
		sobjLogManager = core.getLogManager();
	}

	@Override
	public String getPath() {
		return ROOT_PATH;
	}

	@Override
	protected Template render(HttpServletRequest req, VelocityContext vc) throws PageNotFoundException {
		switch (req.getRequestURI()) {
		case PAGE_LOGS:
			return pageLogs(vc);
		default:
			throw new PageNotFoundException();
		}
	}

	private Template pageLogs(VelocityContext vc) {
		// Load the template
		Template t = loadTemplate(templates.get(PAGE_LOGS));

		// Get the logs
		Collection<String> logs = Arrays.asList(sobjLogManager.getRecentEntries());
		if(logs == null) {
			getLogger().warn("Could not get recent log entries from log manager");
			logs = new ArrayList<>();
		}

		// Escape HTML entities
		logs = Collections2.transform(logs, HtmlEscapers.htmlEscaper().asFunction());

		vc.put("logs", logs);
		return t;
	}

}
