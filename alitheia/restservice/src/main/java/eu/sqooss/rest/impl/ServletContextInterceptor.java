package eu.sqooss.rest.impl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Dummy servlet used to intercept ServletContext.
 * @author <a href="mailto:baldin@gmail.com">Davi Baldin H. Tavares</a> 
 */
public class ServletContextInterceptor extends HttpServlet {

	private static final long serialVersionUID = -8642202569373425062L;
	private ServletConfig config = null;
			
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		if (config != null) {
			this.config = config;
		}
	}
	
	@Override
	public ServletContext getServletContext() {
		return config.getServletContext();
	}

}