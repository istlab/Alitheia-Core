package eu.sqooss.impl.service.webadmin;

import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;

public class AdminActionController extends ActionController {
    private BundleContext bundleContext = null;
    
    public AdminActionController(BundleContext bundleContext) {
        super("results.html");
        this.bundleContext = bundleContext;
    }

    @Action("stop")
    public void stop(Map<String, String> requestParameters, VelocityContext velocityContext) {
        velocityContext.put("RESULTS", Localization.getMsg("AlitheiaCoreShutdown"));
        //sendPage(response, request, "/results.html");

        // Now stop the system
        //logger.info("System stopped by user request to webadmin.");
        try {
            bundleContext.getBundle(0).stop();
        } catch (BundleException be) {
            //logger.warn("Could not stop bundle 0.");
            // And ignore
        }
    }

    @Action("restart")
    public void restart(Map<String, String> requestParameters, VelocityContext velocityContext) {
        //System.out.println("CALLED HOME");

        velocityContext.put("RESULTS", Localization.getMsg("AlitheiaCoreRestart"));
        //sendPage(response, request, "/results.html");

        // FIXME: How do we do a restart?
        return;
    }
}
