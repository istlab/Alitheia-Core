package eu.sqooss.impl.service.webadmin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.util.StringUtils;

public class LogsView extends AbstractView {

	public LogsView(BundleContext bundlecontext, VelocityContext vc) {
		super(bundlecontext, vc);
	}

	@Override
	public void exec(HttpServletRequest req) {		
	}
	
	/**
	 * Creates an HTML safe list of the logs
	 * 
	 * @return List with HTML safe log strings
	 */
	public static List<String> getLogs() {
		String[] names = sobjLogManager.getRecentEntries();
		ArrayList<String> logs = new ArrayList<String>();
		if (names != null) {
			for (String name : names) {
				logs.add(StringUtils.makeXHTMLSafe(name));
			}
		}
		return logs;
	}

}
