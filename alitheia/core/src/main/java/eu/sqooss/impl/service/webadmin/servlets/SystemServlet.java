package eu.sqooss.impl.service.webadmin.servlets;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.servlets.exceptions.PageNotFoundException;

/**
 * This servlet provides action which directly impact the Alitheia system, like restarting Alitheia
 */
@SuppressWarnings("serial")
public class SystemServlet extends AbstractWebadminServlet {

	private static final String ROOT_PATH = "/system";

	private static final String ACTION_SHUTDOWN = ROOT_PATH + "/shutdown";
	private static final String ACTION_RESTART = ROOT_PATH + "/restart";

	private final BundleContext bc;

	public SystemServlet(VelocityEngine ve, AlitheiaCore core, BundleContext bc) {
		super(ve, core);
		this.bc = bc;
	}

	@Override
	public String getPath() {
		return ROOT_PATH;
	}

	@Override
	protected Template render(HttpServletRequest req, VelocityContext vc)
			throws PageNotFoundException {
		switch(req.getRequestURI()) {
		case ACTION_SHUTDOWN:
			return shutdownAlitheia(vc);
		case ACTION_RESTART:
			return restartAlitheia(vc);
		default:
			throw new PageNotFoundException();
		}
	}

	private Template shutdownAlitheia(VelocityContext vc) {
		// Now stop the system
		getLogger().info("System stopped by user request to webadmin.");

		try {
			bc.getBundle(0).stop();
			// Don't know how far we'll get, but lets try
			return makeSuccessMsg(vc, "Alitheia shutdown started");
		} catch (BundleException be) {
			getLogger().warn("Could not stop bundle 0.");
			return makeErrorMsg(vc, "Could not stop Alitheia");
		}
	}

	private Template restartAlitheia(VelocityContext vc) {
		// This was broken already before the refactoring
		//FIXME: How do we do a restart?
		return makeErrorMsg(vc, "Restart is not implemented");
	}

}
