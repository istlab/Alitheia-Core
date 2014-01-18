package eu.sqooss.impl.service.webadmin;

import javax.servlet.http.HttpServletRequest;


public interface IView {
	
	//TODO The String is for current test purpose, should be void (or perhaps boolean)
	public String setupVelocityContext(HttpServletRequest req);  
	
	// method that should return "true" when the implementing view is needed for the requested "path"
	public boolean isUsedForPath(String path);
}
