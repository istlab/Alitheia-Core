/*
 * Copyright 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.plugins.updater.git;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.plugins.tds.git.GitAccessor;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionParent;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.InvalidAccessorException;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.Revision;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.SCMNodeType;
import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.util.FileUtils;

/**
 * A metadata updater converts raw data to Alitheia Core database metadata.
 */
public class GitUpdater implements MetadataUpdater {
    
    private StoredProject project;
    private Logger log;
    private SCMAccessor git;
    private DBService dbs;
    private float progress;
    
    public GitUpdater() {}

    public GitUpdater(DBService db, GitAccessor git, Logger log, StoredProject sp) {
        this.dbs = db;
        this.git = git;
        this.log = log;
        this.project = sp;
    }
    
    public void setUpdateParams(StoredProject sp, Logger l) {
        this.project = sp;
        this.log = l;
        try {
            git = AlitheiaCore.getInstance().getTDSService().getAccessor(sp.getId()).getSCMAccessor();
        } catch (InvalidAccessorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        dbs = AlitheiaCore.getInstance().getDBService();
    }

    public void update() throws Exception {
       
        dbs.startDBSession();
        project = dbs.attachObjectToDBSession(project);
        
        
        info("Running source update for project " + project.getName() 
                + " ID " + project.getId());
        
        //1. Compare latest DB version with the repository
        ProjectVersion latestVersion = ProjectVersion.getLastProjectVersion(project);
        Revision next;
        if (latestVersion != null) {  
            Revision r = git.getHeadRevision();
        
            /* Don't choke when called to update an up-to-date project */
            if (r.compareTo(git.newRevision(latestVersion.getRevisionId())) <= 0) {
                info("Project is already at the newest version: " 
                        + r.getUniqueId());
                dbs.commitDBSession();
                return;    
            }
            next = git.newRevision(latestVersion.getRevisionId());
        } else {
            next = git.getFirstRevision();
        }
        
        updateFromTo(next, git.getHeadRevision());
    } 
    
    public void updateFromTo(Revision from, Revision to)
            throws InvalidProjectRevisionException, InvalidRepositoryException {
        if (from.compareTo(to) > 1)
            return;
        int numRevisions = 0;

        CommitLog commitLog = git.getCommitLog("", from, to);
        if(!dbs.isDBSessionActive()) dbs.startDBSession();

        for (Revision entry : commitLog) {
        	if (ProjectVersion.getVersionByRevision(project, entry.getUniqueId()) != null) {
        		info("Skipping processed revision: " + entry.getUniqueId());
        		continue;
        	}
        	
            ProjectVersion pv = processOneRevision(entry);
            Set<ProjectFile> files = processRevisionFiles(git, entry, pv);
           
            updateValidUntil(pv, files);

            if (!dbs.commitDBSession()) {
                warn("Intermediate commit failed, failing update");
                //restart();
                return;
            }
            
            dbs.startDBSession();
            progress = (float) (((double)numRevisions / (double)commitLog.size()) * 100);
            
            numRevisions++;
        }
    }

    private ProjectVersion processOneRevision(Revision entry) {
        ProjectVersion pv = new ProjectVersion(project);
        pv.setRevisionId(entry.getUniqueId());
        pv.setTimestamp(entry.getDate().getTime());

        Developer d = getAuthor(project, entry.getAuthor());

        pv.setCommitter(d);
        
        String commitMsg = entry.getMessage();
        if (commitMsg.length() > 512) {
            commitMsg = commitMsg.substring(0, 511);
        }
        
        //if (commitMsg.contains("\n"))
        //    commitMsg = commitMsg.substring(0, commitMsg.indexOf('\n'));
        
        pv.setCommitMsg(commitMsg);
        pv.setSequence(Integer.MAX_VALUE);
        dbs.addRecord(pv);
        
        ProjectVersion prev = pv.getPreviousVersion();
        if (prev != null)
            pv.setSequence(prev.getSequence() + 1);
        else 
            pv.setSequence(1);
        
        for (String parentId : entry.getParentIds()) {
            ProjectVersion parent = ProjectVersion.getVersionByRevision(project, parentId);
            ProjectVersionParent pvp = new ProjectVersionParent(pv, parent);
            pv.getParents().add(pvp);
        }
        
        debug("Got version: " + pv.getRevisionId() + 
                " seq: " + pv.getSequence());
        return pv;
    }
    
    public Developer getAuthor(StoredProject sp, String entryAuthor) {
        InternetAddress ia = null;
        String name = null, email = null;
        try {
            ia = new InternetAddress(entryAuthor, true);
            name = ia.getPersonal();
            email = ia.getAddress();
        } catch (AddressException ignored) {
            if (entryAuthor.contains("@")) {
                //Hm, an email address that Java could not parse. Probably the result of
                //misconfigured git. e.g. scott Chacon <schacon@agadorsparticus.(none)>
                if (entryAuthor.contains("<")) {
                    name = entryAuthor.substring(0, entryAuthor.indexOf("<")).trim();
                    if (entryAuthor.contains(">"))
                        email = entryAuthor.substring(entryAuthor.indexOf("<") + 1, entryAuthor.indexOf(">")).trim();
                    else 
                        email = entryAuthor.substring(entryAuthor.indexOf("<") + 1).trim();
                } else {
                    name = entryAuthor.trim();
                }
            } else {
                email = null;
                name = entryAuthor;
            }
        }

        Developer d = null;
        
        if (email != null) {
            d = Developer.getDeveloperByEmail(email, sp, true);
            
            if (name != null) {
                if (name.contains(" ")) {
                    d.setName(name);
                } else {
                    d.setUsername(name);
                }
            }
        } else {
            if (name.contains(" ")) {
                d = Developer.getDeveloperByName(name, sp, true); 
            } else {
                d = Developer.getDeveloperByUsername(name, sp, true);
            }
        }
        return d;
    }
    
    private Set<ProjectFile> processRevisionFiles(SCMAccessor scm, Revision entry,
            ProjectVersion curVersion) throws InvalidRepositoryException {
        Set<ProjectFile> files = new HashSet<ProjectFile>();
        
        for (String chPath : entry.getChangedPaths()) {
            
            SCMNodeType t = scm.getNodeType(chPath, entry);

            ProjectFile toAdd = addFile(curVersion, chPath,
                    ProjectFileState.fromPathChangeType(entry.getChangedPathsStatus().get(chPath)), 
                    t, null);
            files.add(toAdd);
        }
        return files;
    }
    
    /**
     * Constructs a project file out of the provided elements and adds it
     * to the database. If the path has already been processed in this
     * revision, it returns the processed entry.
     */
    private ProjectFile addFile(ProjectVersion version, String fPath, 
            ProjectFileState status, SCMNodeType t, ProjectFile copyFrom) {
        ProjectFile pf = new ProjectFile(version);

        String path = FileUtils.dirname(fPath);
        String fname = FileUtils.basename(fPath);

        version.getVersionFiles().addAll(mkdirs(version, path));
        
        /* cur can point to either the current file version if the
         * file has been processed before whithin this revision
         * or the previous file version
         */
        ProjectFile cur = ProjectFile.findFile(project.getId(), fname,
        		path, version.getRevisionId());
              
        if (pathProcessedBefore(version, fPath) != null) {
        	cur.setState(decideFileStatus(cur.getState(), status));
            return cur;
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
        
        debug("addFile(): Adding entry " + pf + "(" + decided + ")");
        //dbs.addRecord(pf);
        version.getVersionFiles().add(pf);

        return pf;
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
    	
        //Check whether the directory has been re-added 
        //while processing this revision
        ProjectFile inRev = pathProcessedBefore(pv, path);
        if (inRev != null) {
        	inRev.setState(decideFileStatus(inRev.getState(), ProjectFileState.modified()));
            return files;
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
        debug("mkdirs(): Adding directory " + pf);
    	return files;
    }
    
    /**
     * Update the validUntil field after all files have been processed.
     */
    private void updateValidUntil(ProjectVersion pv, Set<ProjectFile> versionFiles) {

        ProjectVersion previous = pv.getPreviousVersion();

        for (ProjectFile pf : versionFiles) {
            if (!pf.isAdded()) {
                ProjectFile old = pf.getPreviousFileVersion();
                old.setValidUntil(previous);
            }

            if (pf.isDeleted()) {
                pf.setValidUntil(pv);
            }
        }
    }
        
    /**
     * This method finds previous recorded cases of paths within the same revision. 
     */
    private ProjectFile pathProcessedBefore(final ProjectVersion pv, String path) {
  	
    	for (ProjectFile pf : pv.getVersionFiles()) {
    		if (pf.getFileName().equals(path)) {
    			return pf;
    		}
    	}
    	return null;
    }
    
    /**
     * A path can receive several types of processing within a revision. 
     * For example, it could be deleted and then overwritten by another
     * path that includes it or added and then deleted in the same
     * revision. This method decides what the status of a file should
     * be.
     */
	private ProjectFileState decideFileStatus(ProjectFileState prev,
			ProjectFileState cur) {
		switch (prev.getStatus()) {
		case ProjectFileState.STATE_ADDED:
			switch (cur.getStatus()) {
			case ProjectFileState.STATE_ADDED:
				return ProjectFileState.added();
			case ProjectFileState.STATE_DELETED:
				return ProjectFileState.deleted();
			case ProjectFileState.STATE_MODIFIED:
				return ProjectFileState.added();
			case ProjectFileState.STATE_REPLACED:
				return ProjectFileState.added();
			}
		case ProjectFileState.STATE_DELETED:
			switch (cur.getStatus()) {
			case ProjectFileState.STATE_ADDED:
				return ProjectFileState.replaced();
			case ProjectFileState.STATE_DELETED:
				return ProjectFileState.deleted();
			case ProjectFileState.STATE_MODIFIED:
				return ProjectFileState.deleted();
			case ProjectFileState.STATE_REPLACED:
				return ProjectFileState.deleted();
			}
		case ProjectFileState.STATE_MODIFIED:
			switch (cur.getStatus()) {
			case ProjectFileState.STATE_ADDED:
				return ProjectFileState.modified();
			case ProjectFileState.STATE_DELETED:
				return ProjectFileState.deleted();
			case ProjectFileState.STATE_MODIFIED:
				return ProjectFileState.modified();
			case ProjectFileState.STATE_REPLACED:
				return ProjectFileState.modified();
			}
		case ProjectFileState.STATE_REPLACED:
			switch (cur.getStatus()) {
			case ProjectFileState.STATE_ADDED:
				return ProjectFileState.replaced();
			case ProjectFileState.STATE_DELETED:
				return ProjectFileState.deleted();
			case ProjectFileState.STATE_MODIFIED:
				return ProjectFileState.replaced();
			case ProjectFileState.STATE_REPLACED:
				return ProjectFileState.replaced();
			}
		}
    	err("Function decideFileStatus() shouldn't reach this point");
    	assert(false);
    	return null;
    }
 
    /**
     * This method should return a sensible representation of progress. 
     */
    @Override
    public int progress() {
        return (int)progress;
    }
    
    @Override
    public String toString() {
        return "GitUpdater - Project:{" + project +"}, " + progress + "%";
    }

    /** Convenience method to write warning messages per project */
    protected void warn(String message) {
            log.warn("Git:" + project.getName() + ":" + message);
    }
    
    /** Convenience method to write error messages per project */
    protected void err(String message) {
            log.error("Git:" + project.getName() + ":" + message);
    }
    
    /** Convenience method to write info messages per project */
    protected void info(String message) {
            log.info("Git:" + project.getName() + ":" + message);
    }
    
    /** Convenience method to write debug messages per project */
    protected void debug(String message) {
            log.debug("Git:" + project.getName() + ":" + message);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
