package eu.sqooss.plugins.tds.scm;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.sqooss.service.tds.CommitCopyEntry;
import eu.sqooss.service.tds.PathChangeType;
import eu.sqooss.service.tds.Revision;

public abstract class SCMProjectRevision implements Revision {

	public Date date;
	public String author;
	public String message;
	public Map<String, PathChangeType> changedPaths;
	public List<CommitCopyEntry> copyOps;
	protected Set<String> parents;

	public abstract boolean isResolved();

	/** {@inheritDoc}} */
	public Date getDate() {
	    return date;
	}

	@Override
	public String getAuthor() {
	    return author;
	}

	@Override
	public String getMessage() {
	    return message;
	}

	@Override
	public Set<String> getParentIds() {
	    return parents;
	}

	@Override
	public Set<String> getChangedPaths() {
	    resolve();
	    return changedPaths.keySet();
	}

	@Override
	public Map<String, PathChangeType> getChangedPathsStatus() {
	    resolve();
	    return changedPaths;
	}

	@Override
	public List<CommitCopyEntry> getCopyOperations() {
	    resolve();
	    return copyOps;
	}

	@Override
	public int compare(Revision o1, Revision o2) {
	    return o1.compareTo(o2);
	}
	
	public abstract void resolve();
}
