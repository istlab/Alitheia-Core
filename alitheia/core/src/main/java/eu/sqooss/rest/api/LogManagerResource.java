package eu.sqooss.rest.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.rest.api.wrappers.JaxbString;
import eu.sqooss.service.logging.LogManager;

@Path("/api/logmanager/")
public class LogManagerResource {
	
	private LogManager logManager;
	
	public LogManagerResource() {
		logManager = AlitheiaCore.getInstance().getLogManager();
	}
	
	
	@GET
    @Produces({"application/xml", "application/json"})
	@Path("entries/recent")
	public List<JaxbString> getRecentEntries() {
		
		List<JaxbString> l = new ArrayList<JaxbString>();
        String[] names = logManager.getRecentEntries();
        
        if (names.length > 0)
        	for (String s : names)
        		l.add(new JaxbString(s));
        
        //TODO remove these
        l.add(new JaxbString("test1"));
		l.add(new JaxbString("test2"));
		
		return l;
		
	}

}
