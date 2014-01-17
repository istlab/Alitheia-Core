package eu.sqooss.impl.service.webadmin.servlets;

import javax.servlet.Servlet;

public interface IWebadminServlet extends Servlet {
	/**
	 * Gets the path this Servlet should handle.
	 * 
	 * Relevant part from OSGI HTTP Service specification:
	 * The Servlet alias must begin with a slash and must not end with a slash
	 * When a request is processed, the HTTP Service will try to exact match the requested URI with a registered Servlet
	 * If not existent, it will remove the last '/' in the URI and everything that follows, and try to match the remaining part, and so on.
	 */
	public String getPath();
}
