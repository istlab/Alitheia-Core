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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.plugins.tds.git.GitAccessor;
import eu.sqooss.service.db.Branch;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionParent;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.Tag;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.CommitCopyEntry;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.InvalidAccessorException;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.Revision;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.SCMNodeType;
import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;
import eu.sqooss.service.util.FileUtils;
import eu.sqooss.service.util.Pair;

/**
 * A metadata updater converts raw data to Alitheia Core database metadata.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Updater(descr = "Metadata updater for Git repositories", 
        mnem ="GIT", 
        protocols = {"git-file"}, 
        stage = UpdaterStage.IMPORT)
public class GitUpdater implements MetadataUpdater {
    
    private StoredProject project;
    private Logger log;
    private GitAccessor git;
    private DBService dbs;
    private float progress;
    
    /*
     * Possible set of valid file state transitions
     */
    private static List<Pair<Integer, Integer>> validStateTransitions;
    
    /*
     * Heuristic fixes for invalid state transitions. They may or may not
     * work, depending on the examined case.
     */
    private static Map<Integer, Integer> invTransitionFix;
    
    /* 
     * State weights to use when evaluating duplicate project file entries
     * in a single revision
     */
    private static Map<Integer, Integer> stateWeights ;
    
    static {
        stateWeights = new HashMap<Integer, Integer>();

        stateWeights.put(ProjectFileState.STATE_MODIFIED, 2);
        stateWeights.put(ProjectFileState.STATE_ADDED, 4);
        stateWeights.put(ProjectFileState.STATE_REPLACED, 8);
        stateWeights.put(ProjectFileState.STATE_DELETED, 16);
        
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
            git = (GitAccessor) AlitheiaCore.getInstance().getTDSService().getAccessor(sp.getId()).getSCMAccessor();
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
        
        //Compare latest DB version with the repository
        ProjectVersion latestVersion = ProjectVersion.getLastProjectVersion(dbs, project);
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

        //Init the branch naming related data structures
        //initBranchNaming(next);
       
        updateFromTo(next, git.getHeadRevision());  
    } 

    public void updateFromTo(Revision from, Revision to)
            throws InvalidProjectRevisionException, InvalidRepositoryException, AccessorException {
        if (from.compareTo(to) > 1)
            return;
        int numRevisions = 0;

        CommitLog commitLog = git.getCommitLog("", from, to);
        if(!dbs.isDBSessionActive()) dbs.startDBSession();

        for (Revision entry : commitLog) {
        	if (ProjectVersion.getVersionByRevision(dbs, project, entry.getUniqueId()) != null) {
        		info("Skipping processed revision: " + entry.getUniqueId());
        		continue;
        	}
        	
            ProjectVersion pv = processOneRevision(entry);
            
            processCopiedFiles(git, entry, pv, pv.getPreviousVersion(dbs));
            
            processRevisionFiles(git, entry, pv);
            
            replayLog(pv);
            
            updateValidUntil(pv, pv.getVersionFiles());

            if (!dbs.commitDBSession()) {
                warn("Intermediate commit failed, failing update");
                return;
            }
            
            dbs.startDBSession();
            progress = (float) (((double)numRevisions / (double)commitLog.size()) * 100);
            
            numRevisions++;
        }
    }

    private ProjectVersion processOneRevision(Revision entry) 
    	throws AccessorException, InvalidProjectRevisionException {
        
        //Basic stuff
        ProjectVersion pv = new ProjectVersion(project);
        pv.setRevisionId(entry.getUniqueId());
        pv.setTimestamp(entry.getDate().getTime());

        Developer d = getAuthor(project, entry.getAuthor());
        pv.setCommitter(d);
        
        String commitMsg = entry.getMessage();
        if (commitMsg.length() > 512) {
            commitMsg = commitMsg.substring(0, 511);
        }

        pv.setCommitMsg(commitMsg);
        pv.setSequence(Integer.MAX_VALUE);
        dbs.addRecord(pv);
        
        //Tags
        String tag = git.allTags().get(entry.getUniqueId());
        if (tag != null) {
            Tag t = new Tag(pv);
            t.setName(tag);
            dbs.addRecord(t);
            pv.getTags().add(t);
        }
        
        //Sequencing
        ProjectVersion prev = pv.getPreviousVersion(dbs);
        if (prev != null)
            pv.setSequence(prev.getSequence() + 1);
        else 
            pv.setSequence(1);
              
        //Branches and parent-child relationships
        for (String parentId : entry.getParentIds()) {
            ProjectVersion parent = ProjectVersion.getVersionByRevision(dbs, project, parentId);
            ProjectVersionParent pvp = new ProjectVersionParent(pv, parent);
            pv.getParents().add(pvp);
            
            //Parent is a branch
            if (git.getCommitChidren(parentId).length > 1) {
                Branch b = new Branch(project, Branch.suggestName(dbs, project));
                dbs.addRecord(b);
                parent.getOutgoingBranches().add(b);
                pv.getIncomingBranches().add(b);
            } else {
                pv.getIncomingBranches().add(parent.getBranch());
            }
        }

        if (entry.getParentIds().size() > 1) {
            //A merge commit
            Branch b = new Branch(project, Branch.suggestName(dbs, project));
            pv.getOutgoingBranches().add(b);
        } else {
            //New line of development
            if (entry.getParentIds().size() == 0) {
                Branch b = new Branch(project, Branch.suggestName(dbs, project));
                dbs.addRecord(b);
                pv.getOutgoingBranches().add(b);
            } else {
                pv.getOutgoingBranches().addAll(pv.getIncomingBranches());
                //TODO: Add branch to Branch, need to convert it to List :-(
            }
        }

        debug("Got version: " + pv.getRevisionId() +  
                " seq: " + pv.getSequence());
        return pv;
    }
    
    /**
     * Do our best to fill in the Developer object with good information.
     */
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
            d = Developer.getDeveloperByEmail(dbs, email, sp, true);
            
            if (name != null) {
                if (name.contains(" ")) {
                    d.setName(name);
                } else {
                    d.setUsername(name);
                }
            }
        } else {
            if (name.contains(" ")) {
                d = Developer.getDeveloperByName(dbs, name, sp, true); 
            } else {
                d = Developer.getDeveloperByUsername(dbs, name, sp, true);
            }
        }
        return d;
    }
    
    /*
     * Copy operations copy or move files or directories accross
     * the virtual filetree generated by the SCM.
     */
    private void processCopiedFiles(SCMAccessor scm, Revision entry,
            ProjectVersion curVersion, ProjectVersion prev)
            throws InvalidProjectRevisionException, InvalidRepositoryException {
        for (CommitCopyEntry cce : entry.getCopyOperations()) {
            
        	/* We only want to process copies within allowed paths or
        	 *  from anywhere to an allowed path
        	 */
        	if (!canProcessCopy(cce.fromPath(), cce.toPath()) && 
        			!canProcessPath(cce.toPath())) {
        		debug("Ignoring copy from " + cce.fromPath() + " to " 
        				+ cce.toPath() + " due to project config");
        		continue;
        	}
        	
            ProjectFile copyFrom = null;
            copyFrom = ProjectFile.findFile(dbs, project.getId(), 
                        FileUtils.basename(cce.fromPath()), 
                        FileUtils.dirname(cce.fromPath()), 
                        cce.fromRev().getUniqueId());
                
            /* Source location is an entry we do not have info for, 
             * due to updater settings. Use the SCM to retrieve
             * the missing info.
             *
            if (copyFrom == null) {
                warn("expecting 1 got " + 0 + " files for path " 
                        + cce.fromPath() + " " + prev.toString());
                SCMNodeType type = scm.getNodeType(cce.fromPath(), cce.fromRev());
                
                if (type.equals(SCMNodeType.FILE)) {
                	addFile(curVersion, cce.toPath(), ProjectFileState.added(), 
                			SCMNodeType.FILE, copyFrom);
                } else if (type.equals(SCMNodeType.DIR)) {
                	
                	SCMNode n = scm.getNode(cce.fromPath(), cce.fromRev());
                	
                	if (n != null) {
                		debug("Copying directory "+ n.getPath() +" from repository");
                		handleDirCopyFromRepository(curVersion, n, cce.toPath());
                	} else {
                		warn("Directory " + cce.fromPath() + " cannot be found" +
                				" in project repository!");
                	}
                } else {
                	warn("Path " + cce.fromPath() + " is of uknown type " 
                			+ type + " which the updater cannot process");
                }
                
                continue;
            } */ //TODO: Take care of this later on
            
            debug("copyFiles(): Copying " + cce.fromPath() + "->" + cce.toPath());
            if (copyFrom.getIsDirectory()) {
                    
                Directory from = Directory.getDirectory(dbs, cce.fromPath(), false);
                Directory to = Directory.getDirectory(dbs, cce.toPath(), true);

                /*
                 * Recursively copy contents and mark files as modified
                 * and directories as added
                 */
                handleDirCopy(curVersion, 
                        ProjectVersion.getVersionByRevision(dbs, curVersion.getProject(),
                        cce.fromRev().getUniqueId()), from, to, copyFrom);
            } else {
                /*
                 * Create a new entry at the new location and mark the new 
                 * entry as ADDED
                 */
                addFile(curVersion, cce.toPath(), ProjectFileState.added(dbs), 
                		SCMNodeType.FILE, copyFrom);
            }
            
            if (cce.isMove()) {
            	debug("copyFiles(): Deleting old path " + cce.fromPath() + "->" + cce.toPath());
            	if (copyFrom.getIsDirectory())
            		curVersion.getVersionFiles().addAll(handleDirDeletion(copyFrom, curVersion));
            	else 
            		addFile(curVersion, cce.fromPath(), 
            				ProjectFileState.deleted(dbs), SCMNodeType.FILE, null);
            }
        }
    }
    
    private boolean canProcessCopy(String path, String to) {
    	return true;
    }
    
    private boolean canProcessPath(String path) {
    	return true;
    }
    
    private void processRevisionFiles(SCMAccessor scm, Revision entry,
            ProjectVersion curVersion) throws InvalidRepositoryException {
       
        for (String chPath : entry.getChangedPaths()) {
            
            SCMNodeType t = scm.getNodeType(chPath, entry);

            ProjectFile file = addFile(curVersion, chPath,
                    ProjectFileState.fromPathChangeType(dbs, entry.getChangedPathsStatus().get(chPath)), 
                    t, null);
            /*
             * Before entering the next block, examine whether the deleted
             * file was a directory or not. If there is no path entry in the
             * Directory table for the processed file path, this means that
             * the path is definitely not a directory. If there is such an
             * entry, it may be shared with another project; this case is
             * examined upon entering
             */
            if (file.isDeleted() && (Directory.getDirectory(dbs, chPath, false) != null)) {
                /*
                 * Directories, when they are deleted, do not have type DIR,
                 * but something else. So we need to check on deletes
                 * whether this name was most recently a directory.
                 */
                ProjectFile lastVersion = file.getPreviousFileVersion(dbs);
                
                /*
                 * If a directory is deleted and its previous incarnation cannot
                 * be found in a previous revision, this means that the
                 * directory is deleted in the same revision it was added
                 * (probably copied first)! Search in the current
                 * revision files then.
                 */
                boolean delAfterCopy = false;
                if (lastVersion == null) {
                    for (ProjectFile pf : curVersion.getVersionFiles()) {
                        if (pf.getFileName().equals(file.getFileName())
                                && pf.getIsDirectory()
                                && pf.isAdded()) {
                            lastVersion = pf;
                            delAfterCopy = true;
                            break;
                        }
                    }
                }
                    
                /* If a dir was deleted, mark all children as deleted */
                if (lastVersion != null
                        && lastVersion.getIsDirectory()) {
                    // In spite of it not being marked as a directory
                    // in the node tree right now.
                    file.setIsDirectory(true);
                } else if (!delAfterCopy) {
                    warn("Cannot find previous version of DELETED" +
                                " directory " + file.getFileName());
                }
                
                if (!delAfterCopy) {
                    curVersion.getVersionFiles().addAll(handleDirDeletion(file, curVersion));
                } else {
                	warn("FIXME: DELETED DIRECTORY AFTER COPY");
                    //handleCopiedDirDeletion(toAdd);
                }
            }
        }
    }
    
    private void replayLog(ProjectVersion curVersion) {
    	 /*Find duplicate projectfile entries*/
        HashMap<String, Integer> numOccurs = new HashMap<String, Integer>();
        for (ProjectFile pf : curVersion.getVersionFiles()) {
            if (numOccurs.get(pf.getFileName()) != null) {
                numOccurs.put(pf.getFileName(), numOccurs.get(pf.getFileName()).intValue() + 1);
            } else {
                numOccurs.put(pf.getFileName(), 1);
            }
        }
        
        /* Copy list of files to be added to the DB in a tmp array,
         * to use for iterating
         */
        List<ProjectFile> tmpFiles = new ArrayList<ProjectFile>();
        tmpFiles.addAll(curVersion.getVersionFiles());
        
        for (String fpath : numOccurs.keySet()) {
            if (numOccurs.get(fpath) <= 1) { 
                continue;
            }
            debug("replayLog(): Multiple entries for file " + fpath);
            
            int points = 0;
            
            ProjectFile copyFrom = null;
            ProjectFile winner = null; 
            //dbs.addRecord(pf);

            for (ProjectFile f: tmpFiles) {
                
                if (!f.getFileName().equals(fpath)) { 
                    continue;
                }
                
                debug("  " + f);
                
                if (stateWeights.get(f.getState().getStatus()) > points) {
                    points = stateWeights.get(f.getState().getStatus());
                    if (winner != null)
                    	curVersion.getVersionFiles().remove(winner);
                    winner = f;
                } else {
                    curVersion.getVersionFiles().remove(f);
                }
                
                if (f.getCopyFrom() != null) {
                    copyFrom = f.getCopyFrom();
                }
            }
            
			/*
			 * Check whether a DELETED path is part of some path that has been
			 * modified later on and mark it REPLACED. Moreover, mark the
			 * deleted file as a directory (JGit will return it as file).
			 * This takes care of scenarios like the following: 
			 * D /a/dir 
			 * A /a/dir/other/file.txt
			 * where a directory has been deleted and then a file has been 
			 * added to a path that includes the directory. Such cases 
			 * might indicated the replacement of a Git submodule with a
			 * locally versioned path. 
			 */
            if (winner.getState().getStatus() == ProjectFileState.STATE_DELETED) {
            	for (ProjectFile f: curVersion.getVersionFiles()) {
            		if (!f.equals(winner) &&
            			 f.getFileName().startsWith(winner.getFileName()) &&
            			 f.getState().getStatus() != ProjectFileState.STATE_DELETED) {
            			debug("replayLog(): Setting status of " + winner + " to " 
            					+ ProjectFileState.replaced(dbs) + " as " +
            					"file " + f + " uses its path");
            			winner.setState(ProjectFileState.replaced(dbs));
            			winner.setIsDirectory(true);
            			break;
            		}
            	}
            }
            
            /*Update file to be added to the DB with copy-from info*/
            if (copyFrom != null) {
            	curVersion.getVersionFiles().remove(winner);
                winner.setCopyFrom(copyFrom);
                curVersion.getVersionFiles().add(winner);
            }
            debug("replayLog(): Keeping file " + winner);
        }
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
        ProjectFile cur = ProjectFile.findFile(dbs, project.getId(), fname,
        		path, version.getRevisionId(), true);

        if (cur != null && 
        	!cur.getProjectVersion().getRevisionId().equals(version.getRevisionId()) &&
        	!isValidStateTransition(cur.getState(), status)) {
        	ProjectFileState newstatus = ProjectFileState.fromStatus(dbs, invTransitionFix.get(cur.getState().getStatus()));
        	debug("addFile(): Invalid state transition (" + cur.getState() + 
        			"->" + status + ") for path " + fPath + ". Setting " + 
        			"status to " + newstatus);
        	status = newstatus;
        }
        
        Directory dir = Directory.getDirectory(dbs, path, true);
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
    	
    	ProjectVersion previous = pv.getPreviousVersion(dbs);

        if (previous == null) { // Special case for first version
            previous = pv;
        }
        
    	ProjectFile prev = ProjectFile.findFile(dbs, project.getId(),
    			filename, pathname, previous.getRevisionId());
    	
    	ProjectFile pf = new ProjectFile(pv);
    	
    	if (prev == null) {
            pf.setState(ProjectFileState.added(dbs));
            //Recursion reached the root directory
            if (!(pathname.equals("/") && filename.equals(""))) 
            	files.addAll(mkdirs(pv, pathname));

    	} else {
    		pf.setState(ProjectFileState.modified(dbs));
    	}

        pf.setDirectory(true);
        pf.setDir(Directory.getDirectory(dbs, pathname, true));
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

        ProjectVersion previous = pv.getPreviousVersion(dbs);

        for (ProjectFile pf : versionFiles) {
            if (!pf.isAdded()) {
                ProjectFile old = pf.getPreviousFileVersion(dbs);
                old.setValidUntil(previous);
            }

            if (pf.isDeleted()) {
                pf.setValidUntil(pv);
            }
        }
    }
    
    /**
     * Mark the contents of a directory as DELETED when the directory has been
     * DELETED
     * 
     * @param pf The project file representing the deleted directory
     */
    private Set<ProjectFile> handleDirDeletion(final ProjectFile pf, final ProjectVersion pv) {
    	Set<ProjectFile> files = new HashSet<ProjectFile>();

		if (pf == null || pv == null) {
			return files;
		}
        
        if (pf.getIsDirectory() == false) {
            return files;
        }
        
        debug("Deleting directory " + pf.getFileName() + " ID "
                + pf.getId());
        Directory d = Directory.getDirectory(dbs, pf.getFileName(), false);
        if (d == null) {
            warn("Directory entry " + pf.getFileName() + " in project "
                    + pf.getProjectVersion().getProject().getName()
                    + " is missing in Directory table.");
            return files;
        }

        ProjectVersion prev = pv.getPreviousVersion(dbs);
        
        List<ProjectFile> dirFiles = prev.getFiles(dbs, d);
        
        for (ProjectFile f : dirFiles) {
            if (f.getIsDirectory()) {
                files.addAll(handleDirDeletion(f, pv));
            }
            ProjectFile deleted = new ProjectFile(f, pv);
            deleted.setState(ProjectFileState.deleted(dbs));
            files.add(deleted);
        }
        return files;
    }
    
    /**
     * Checks whether file state transitions are valid, at least for what 
     * Alitheia Core expects.
     */
    private boolean isValidStateTransition(ProjectFileState a, ProjectFileState b) {
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
    private void handleDirCopy(ProjectVersion pv, ProjectVersion fromVersion,
            Directory from, Directory to, ProjectFile copyFrom) {
        
        if (!canProcessCopy(from.getPath(), to.getPath())) 
            return;
       
        addFile(pv, to.getPath(), ProjectFileState.added(dbs), SCMNodeType.DIR, copyFrom);
        
        /*Recursively copy directories*/
        List<ProjectFile> fromPF = fromVersion.getFiles(dbs, from, ProjectVersion.MASK_DIRECTORIES);
        
        for (ProjectFile f : fromPF) {
            handleDirCopy(pv, fromVersion, Directory.getDirectory(dbs, f.getFileName(), false), 
            		Directory.getDirectory(dbs, to.getPath() + "/" + f.getName(), true), f);
        }
        
        fromPF = fromVersion.getFiles(dbs, from, ProjectVersion.MASK_FILES);
        
        for (ProjectFile f : fromPF) {
            addFile(pv, to.getPath() + "/" + f.getName(),
                    ProjectFileState.added(dbs), SCMNodeType.FILE, f);
        }
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
        if (log != null)
            log.debug("Git:" + project.getName() + ":" + message);
        else
            System.err.println(message);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
    
