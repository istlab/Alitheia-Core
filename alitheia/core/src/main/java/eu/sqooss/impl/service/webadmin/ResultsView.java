package eu.sqooss.impl.service.webadmin;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;

public class ResultsView extends AbstractView {

	public ResultsView(BundleContext bundlecontext, VelocityContext vc) {
		super(bundlecontext, vc);
	}

	@Override
	public void exec(HttpServletRequest req) {
        String query = req.getPathInfo();
		
		if (query.startsWith("/stop")) {
            vc.put("RESULTS", "<p>Alitheia Core is now shutdown.</p>");

            // Now stop the system            
            sobjLogger.info("System stopped by user request to webadmin.");
            try {
                bc.getBundle(0).stop();
            } catch (BundleException be) {
            	sobjLogger.warn("Could not stop bundle 0.");
                // And ignore
            }
        } else if (query.startsWith("/restart")) {
            vc.put("RESULTS", "<p>Alitheia Core is now restarting.</p>");
            //FIXME: How do we do a restart?
        } else if (query.startsWith("/addproject")) {
            //addProject(request);
        } else if (query.startsWith("/diraddproject")) {
            AdminService as = sobjCore.getAdminService();
            AdminAction aa = as.create(AddProject.MNEMONIC);
            aa.addArg("dir", req.getParameter("properties"));
            as.execute(aa);
            if (aa.hasErrors())
            	vc.put("RESULTS", aa.errors());
            else
            	vc.put("RESULTS", aa.results());
        }
	}

}
