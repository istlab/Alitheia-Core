package eu.sqooss.impl.service.webadmin;

import java.util.HashMap;
import java.util.List;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.InvocationRule;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ClusterNodeProject;
import eu.sqooss.service.scheduler.Job;

public class ProjectDeleteJob extends Job {

	private StoredProject sp;
	private AlitheiaCore core;
	
	ProjectDeleteJob(AlitheiaCore core, StoredProject sp) {
		this.sp = sp;
		this.core = core;
	}
	
	@Override
	public int priority() {
		return 0xff;
	}

	@Override
	protected void run() throws Exception {
        DBService dbs = core.getDBService();

        if(!dbs.isDBSessionActive()) {
			dbs.startDBSession();
		}
		
		sp = dbs.attachObjectToDBSession(sp);
		 // Delete any associated invocation rules first
        HashMap<String,Object> properties =
            new HashMap<String, Object>();
        properties.put("project", sp);
        List<?> assosRules = dbs.findObjectsByProperties(
                InvocationRule.class, properties);
        if ((assosRules != null) && (assosRules.size() > 0)) {
            for (Object nextDAO: assosRules) {
                InvocationRule.deleteInvocationRule(
                        dbs, core.getMetricActivator(), 
                        (InvocationRule) nextDAO);
            }
        }
        
        
        boolean success = true;
        // Delete project's assignments
        ClusterNodeProject cnp = ClusterNodeProject.getProjectAssignment(sp);
        if (cnp!=null) {            
            success &= dbs.deleteRecord(cnp);
        }        
        // Delete the selected project
        success &= dbs.deleteRecord(sp);
        
        if (success) {
            dbs.commitDBSession();
        } else {
            dbs.rollbackDBSession();
        }
        
        
        
        
        
	}
}
