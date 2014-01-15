package eu.sqooss.plugins.tds.scm;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.Revision;

public abstract class SCMCommitLog implements CommitLog {

	protected LinkedList<Revision> entries;

	public SCMCommitLog() {
		entries = new LinkedList<Revision>();
	}
	
	public List<Revision> getEntries() {
	    return entries;
	}

	@Override
	public Iterator<Revision> iterator() {
	    return entries.iterator();
	}

	// Interface methods
	@Override
	public Revision first() {
	    if (entries.size() < 1) {
	        return null;
	    }
	    
	    return entries.getFirst();
	}

	@Override
	public Revision last() {
	    if (entries.size() < 1) {
	        return null;
	    }
	    
	    return entries.getLast();
	}

	@Override
	public int size() {
	    return entries.size();
	}

}
