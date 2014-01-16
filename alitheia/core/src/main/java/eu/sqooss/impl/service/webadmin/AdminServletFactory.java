package eu.sqooss.impl.service.webadmin;

import org.apache.velocity.app.VelocityEngine;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.webadmin.WebadminService;

public interface AdminServletFactory {
	AdminServlet create(BundleContext bc, WebadminService webadmin,
            Logger logger, VelocityEngine ve);
}
