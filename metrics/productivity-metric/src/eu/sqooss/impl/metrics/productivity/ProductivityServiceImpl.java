package eu.sqooss.impl.metrics.productivity;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;

public class ProductivityServiceImpl extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ServiceReference serviceRef = null;

    private LogManager logService = null;

    private Logger logger = null;

    private HttpService httpService = null;

    public ProductivityServiceImpl(BundleContext bc) throws ServletException,
            NamespaceException {
        /*Get a reference to the logging service*/
        serviceRef = bc.getServiceReference(LogManager.class.getName());
        logService = (LogManager) bc.getService(serviceRef);

        if (logService != null) {
            logger = logService.createLogger(Logger.NAME_SQOOSS_UPDATER);

            if (logger != null)
                logger.info("Got a valid reference to the logger");
        }

        if (logger == null) {
            System.out.println("ERROR: Got no logger");
        }

        /*Get a reference to the HTTP service*/
        serviceRef = bc
                .getServiceReference("org.osgi.service.http.HttpService");

        if (serviceRef != null) {
            httpService = (HttpService) bc.getService(serviceRef);
            httpService.registerServlet("/productivity", (Servlet) this, null,
                    null);
        } else {
            logger.severe("Could not load the HTTP service.");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String t = request.getParameter("target");
        
    }

}
