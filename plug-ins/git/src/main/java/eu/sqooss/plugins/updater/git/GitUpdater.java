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
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.plugins.tds.git.GitAccessor;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.InvalidAccessorException;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.Revision;
import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

/**
 * A metadata updater converts raw data to Alitheia Core database metadata.
 */
@Updater(descr = "Metadata updater for Git repositories", 
        mnem ="GIT", 
        protocols = {"git-file"}, 
        stage = UpdaterStage.IMPORT)
public class GitUpdater implements MetadataUpdater {
    
    private StoredProject project;
    private GitAccessor git;
    private DBService dbs;
    private GitMessageHandler msg;
    private GitProcessor proc;
    private float progress;
    
    public GitUpdater() {}

    public GitUpdater(DBService db, GitAccessor git, Logger log, StoredProject sp) {
        this.dbs = db;
        this.git = git;
        this.project = sp;
        msg = new GitMessageHandler(project.getName(), log);
        proc = new GitProcessor(project, git, dbs, log);
    }
    
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
    }
    
    public void setUpdateParams(StoredProject sp, Logger l) {
        this.project = sp;
        msg = new GitMessageHandler(sp.getName(), l);
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
        
        
        msg.info("Running source update for project " + project.getName() 
                + " ID " + project.getId());
        
        //Compare latest DB version with the repository
        ProjectVersion latestVersion = ProjectVersion.getLastProjectVersion(project);
        Revision next;
        if (latestVersion != null) {  
            Revision r = git.getHeadRevision();
        
            /* Don't choke when called to update an up-to-date project */
            if (r.compareTo(git.newRevision(latestVersion.getRevisionId())) <= 0) {
            	msg.info("Project is already at the newest version: " 
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
        	if (ProjectVersion.getVersionByRevision(project, entry.getUniqueId()) != null) {
        		msg.info("Skipping processed revision: " + entry.getUniqueId());
        		continue;
        	}
        	
            ProjectVersion pv = proc.processOneRevision(entry);
            
            proc.processCopiedFiles(git, entry, pv, pv.getPreviousVersion());
            
            proc.processRevisionFiles(git, entry, pv);
            
            replayLog(pv);
            
            updateValidUntil(pv, pv.getVersionFiles());

            if (!dbs.commitDBSession()) {
                msg.warn("Intermediate commit failed, failing update");
                return;
            }
            
            dbs.startDBSession();
            progress = (float) (((double)numRevisions / (double)commitLog.size()) * 100);
            
            numRevisions++;
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
            msg.debug("replayLog(): Multiple entries for file " + fpath);
            
            int points = 0;
            
            ProjectFile copyFrom = null;
            ProjectFile winner = null; 
            //dbs.addRecord(pf);

            for (ProjectFile f: tmpFiles) {
                
                if (!f.getFileName().equals(fpath)) { 
                    continue;
                }
                
                msg.debug("  " + f);
                
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
            			msg.debug("replayLog(): Setting status of " + winner + " to " 
            					+ ProjectFileState.replaced() + " as " +
            					"file " + f + " uses its path");
            			winner.setState(ProjectFileState.replaced());
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
            msg.debug("replayLog(): Keeping file " + winner);
        }
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
}

// vi: ai nosi sw=4 ts=4 expandtab
    
