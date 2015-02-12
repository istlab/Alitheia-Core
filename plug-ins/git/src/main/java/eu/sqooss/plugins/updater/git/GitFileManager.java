package eu.sqooss.plugins.updater.git;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.SCMNodeType;
import eu.sqooss.service.util.FileUtils;
import eu.sqooss.service.util.Pair;

public class GitFileManager {
	
	private StoredProject project;
    private GitMessageHandler msg;
	
    /*
     * Possible set of valid file state transitions
     */
    private static List<Pair<Integer, Integer>> validStateTransitions;
	
    /*
     * Heuristic fixes for invalid state transitions. They may or may not
     * work, depending on the examined case.
     */
    private static Map<Integer, Integer> invTransitionFix;
    
    static {        
        validStateTransitions = new ArrayList<Pair<Integer,Integer>>();
        validStateTransitions.add(new Pair(ProjectFileState.STATE_MODIFIED, ProjectFileState.STATE_MODIFIED));
        validStateTransitions.add(new Pair(ProjectFileState.STATE_MODIFIED, ProjectFileState.STATE_DELETED));
        validStateTransitions.add(new Pair(ProjectFileState.STATE_ADDED, ProjectFileState.STATE_MODIFIED));
        validStateTransitions.add(new Pair(ProjectFileState.STATE_ADDED, ProjectFileState.STATE_DELETED));
        validStateTransitions.add(new Pair(ProjectFileState.STATE_DELETED, ProjectFileState.STATE_ADDED));
        validStateTransitions.add(new Pair(ProjectFileState.STATE_MODIFIED, ProjectFileState.STATE_REPLACED));
        validStateTransitions.add(new Pair(ProjectFileState.STATE_ADDED, ProjectFileState.STATE_REPLACED));
        validStateTransitions.add(new Pair(ProjectFileState.STATE_REPLACED, ProjectFileState.STATE_DELETED));
        validStateTransitions.add(new Pair(ProjectFileState.STATE_REPLACED, ProjectFileState.STATE_REPLACED));
        validStateTransitions.add(new Pair(ProjectFileState.STATE_REPLACED, ProjectFileState.STATE_MODIFIED));
        
        invTransitionFix = new HashMap<Integer, Integer>();
        invTransitionFix.put(ProjectFileState.STATE_MODIFIED, ProjectFileState.STATE_MODIFIED);
        invTransitionFix.put(ProjectFileState.STATE_ADDED, ProjectFileState.STATE_MODIFIED);
        invTransitionFix.put(ProjectFileState.STATE_DELETED, ProjectFileState.STATE_ADDED);
        invTransitionFix.put(ProjectFileState.STATE_REPLACED, ProjectFileState.STATE_MODIFIED);
    }
	
	public GitFileManager(StoredProject project, Logger log){
		this.project = project;
		msg = new GitMessageHandler(project.getName(), log);
	}
	
    /**
     * Constructs a project file out of the provided elements and adds it
     * to the database. If the path has already been processed in this
     * revision, it returns the processed entry.
     */
    public ProjectFile addFile(ProjectVersion version, String fPath, 
            ProjectFileState status, SCMNodeType t, ProjectFile copyFrom) {
        ProjectFile pf = new ProjectFile(version);

        String path = FileUtils.dirname(fPath);
        String fname = FileUtils.basename(fPath);

        version.getVersionFiles().addAll(mkdirs(version, path));
        
        /* cur can point to either the current file version if the
         * file has been processed before within this revision
         * or the previous file version
         */
        ProjectFile cur = ProjectFile.findFile(project.getId(), fname,
        		path, version.getRevisionId(), true);

        if (cur != null && 
        	!cur.getProjectVersion().getRevisionId().equals(version.getRevisionId()) &&
        	!isValidStateTransition(cur.getState(), status)) {
        	ProjectFileState newstatus = ProjectFileState.fromStatus(invTransitionFix.get(cur.getState().getStatus()));
        	msg.debug("addFile(): Invalid state transition (" + cur.getState() + 
        			"->" + status + ") for path " + fPath + ". Setting " + 
        			"status to " + newstatus);
        	status = newstatus;
        }
        
        Directory dir = Directory.getDirectory(path, true);
        pf.setName(fname);
        pf.setDir(dir);
        pf.setState(status);
        pf.setCopyFrom(copyFrom);
        pf.setValidFrom(version);
        pf.setValidUntil(null);
        
        SCMNodeType decided = null;
        
		if (t == SCMNodeType.UNKNOWN) {
			if (status.getStatus() == ProjectFileState.STATE_DELETED)
				decided = (cur.getIsDirectory() == true ? 
						SCMNodeType.DIR : SCMNodeType.FILE);
			else 
				decided = SCMNodeType.DIR;
		} else {
			decided = t;
		}

        if (decided == SCMNodeType.DIR) {
            pf.setIsDirectory(true);
        } else {
            pf.setIsDirectory(false);
        }
        
        msg.debug("addFile(): Adding entry " + pf + "(" + decided + ")");
        version.getVersionFiles().add(pf);

        return pf;
    }
	
    /**
     * Checks whether file state transitions are valid, at least for what 
     * Alitheia Core expects.
     */
    public boolean isValidStateTransition(ProjectFileState a, ProjectFileState b) {
    	for (Pair<Integer, Integer> p: validStateTransitions) {
    		if (p.first == a.getStatus())
    			if (p.second == b.getStatus())
    				return true;
    	}
    	return false;
    }
	
    /**
     * Handle directory copies
     */
    public void handleDirCopy(ProjectVersion pv, ProjectVersion fromVersion,
            Directory from, Directory to, ProjectFile copyFrom) {
        
        if (!canProcessCopy(from.getPath(), to.getPath())) 
            return;
       
        addFile(pv, to.getPath(), ProjectFileState.added(), SCMNodeType.DIR, copyFrom);
        
        /*Recursively copy directories*/
        List<ProjectFile> fromPF = fromVersion.getFiles(from, ProjectVersion.MASK_DIRECTORIES);
        
        for (ProjectFile f : fromPF) {
            handleDirCopy(pv, fromVersion, Directory.getDirectory(f.getFileName(), false), 
            		Directory.getDirectory(to.getPath() + "/" + f.getName(), true), f);
        }
        
        fromPF = fromVersion.getFiles(from, ProjectVersion.MASK_FILES);
        
        for (ProjectFile f : fromPF) {
            addFile(pv, to.getPath() + "/" + f.getName(),
                    ProjectFileState.added(), SCMNodeType.FILE, f);
        }
    }
	
    /**
     * Mark the contents of a directory as DELETED when the directory has been
     * DELETED
     * 
     * @param pf The project file representing the deleted directory
     */
    public Set<ProjectFile> handleDirDeletion(final ProjectFile pf, final ProjectVersion pv) {
    	Set<ProjectFile> files = new HashSet<ProjectFile>();

		if (pf == null || pv == null) {
			return files;
		}
        
        if (pf.getIsDirectory() == false) {
            return files;
        }
        
        msg.debug("Deleting directory " + pf.getFileName() + " ID "
                + pf.getId());
        Directory d = Directory.getDirectory(pf.getFileName(), false);
        if (d == null) {
        	msg.warn("Directory entry " + pf.getFileName() + " in project "
                    + pf.getProjectVersion().getProject().getName()
                    + " is missing in Directory table.");
            return files;
        }

        ProjectVersion prev = pv.getPreviousVersion();
        
        List<ProjectFile> dirFiles = prev.getFiles(d);
        
        for (ProjectFile f : dirFiles) {
            if (f.getIsDirectory()) {
                files.addAll(handleDirDeletion(f, pv));
            }
            ProjectFile deleted = new ProjectFile(f, pv);
            deleted.setState(ProjectFileState.deleted());
            files.add(deleted);
        }
        return files;
    }
	
    /**
     * Adds or updates directories leading to path. Similar to 
     * mkdir -p cmd line command.
     */
    public Set<ProjectFile> mkdirs(final ProjectVersion pv, String path) {
    	Set<ProjectFile> files = new HashSet<ProjectFile>();
    	String pathname = FileUtils.dirname(path);
    	String filename = FileUtils.basename(path);
    	
    	ProjectVersion previous = pv.getPreviousVersion();

        if (previous == null) { // Special case for first version
            previous = pv;
        }
        
    	ProjectFile prev = ProjectFile.findFile(project.getId(),
    			filename, pathname, previous.getRevisionId());
    	
    	ProjectFile pf = new ProjectFile(pv);
    	
    	if (prev == null) {
            pf.setState(ProjectFileState.added());
            //Recursion reached the root directory
            if (!(pathname.equals("/") && filename.equals(""))) 
            	files.addAll(mkdirs(pv, pathname));

    	} else {
    		pf.setState(ProjectFileState.modified());
    	}

        pf.setDirectory(true);
        pf.setDir(Directory.getDirectory(pathname, true));
        pf.setName(filename);
        pf.setValidFrom(pv);
        
        files.add(pf);
        msg.debug("mkdirs(): Adding directory " + pf);
    	return files;
    }
    
    public boolean canProcessCopy(String path, String to) {
    	return true;
    }
    
    public boolean canProcessPath(String path) {
    	return true;
    }
}
