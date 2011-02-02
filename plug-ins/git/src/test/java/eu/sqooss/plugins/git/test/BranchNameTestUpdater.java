package eu.sqooss.plugins.git.test;

import java.util.HashMap;
import java.util.Map;

import eu.sqooss.plugins.updater.git.GitUpdater;
import eu.sqooss.service.tds.Revision;

public class BranchNameTestUpdater extends GitUpdater {

	Map<String, String> branchNames = new HashMap<String, String>();
	
	protected int getNumBranches() {
    	return branchNames.values().size();
    }
	
    protected String branchName(Revision rev) {
    	return branchNames.get(rev.getUniqueId());
    }
    
    public void addVersionBranch(String revid, String branchname) {
    	branchNames.put(revid, branchname);
    }
}
