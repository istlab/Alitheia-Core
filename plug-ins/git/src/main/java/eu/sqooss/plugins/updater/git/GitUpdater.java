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
import java.util.List;

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

        // 2. Get commit log for dbversion < v < repohead
        CommitLog commitLog = git.getCommitLog("", from, to);
        if(!dbs.isDBSessionActive()) dbs.startDBSession();

        for (Revision entry : commitLog) {
            ProjectVersion pv = processOneRevision(entry);
            List<ProjectFile> files = processRevisionFiles(git, entry, pv);
           
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
            dbs.addRecord(pvp);
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
    
    private List<ProjectFile> processRevisionFiles(SCMAccessor scm, Revision entry,
            ProjectVersion curVersion) throws InvalidRepositoryException {
        List<ProjectFile> files = new ArrayList<ProjectFile>();
        
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
     * to the project file cache.
     */
    private ProjectFile addFile(ProjectVersion version, String fPath, 
            ProjectFileState status, SCMNodeType t, ProjectFile copyFrom) {
        ProjectFile pf = new ProjectFile(version);

        String path = FileUtils.dirname(fPath);
        String fname = FileUtils.basename(fPath);

        mkdirs(version, path, status);
        Directory dir = Directory.getDirectory(path, true);
        pf.setName(fname);
        pf.setDir(dir);
        pf.setState(status);
        pf.setCopyFrom(copyFrom);
        pf.setValidFrom(version);
        pf.setValidUntil(null);
        
        if (t == SCMNodeType.DIR) {
            pf.setIsDirectory(true);
            Directory.getDirectory(pf.getFileName(), true);
        } else {
            pf.setIsDirectory(false);
        }
        
        debug("Adding file " + pf);
        dbs.addRecord(pf);
        return pf;
    }
    
    /**
     * Adds or updates directories leading to path. Similar to 
     * mkdir -p cmd line command.
     */
    public List<ProjectFile> mkdirs(ProjectVersion pv, String path, ProjectFileState status) {
        List<ProjectFile> dirs = new ArrayList<ProjectFile>();

        String[] directories = path.split("/");
        if (directories.length == 0) {
            String[] tmp = {""};
            directories = tmp;
        }
            
        ProjectVersion previous = pv.getPreviousVersion();

        if (previous == null) { // Special case for first version
            previous = pv;
        }

        String constrPath = "/";

        for (int i = 0; i < directories.length; i++) {
            String name = null;
            if (!directories[i].equals("")) //The first entry is always empty
                name = directories[i];
            else 
                name = "";

            ProjectFile prev = ProjectFile.findFile(project.getId(),
            		name, constrPath, previous.getRevisionId());

            ProjectFile cur = ProjectFile.findFile(project.getId(), name,
            		constrPath, pv.getRevisionId());

            //Check whether the directory has been re-added 
            //while processing this revision
            if (cur != null) {
                continue;
            }
            
            ProjectFile pf = new ProjectFile(pv);
            pf.setDirectory(true);
            pf.setDir(Directory.getDirectory(FileUtils.dirname(constrPath), true));
            pf.setName(name);
            
            if (prev == null) {
                //We don't have a previous version, so the dir 
                //or the dir hierarchy from this dir upwards is new
                pf.setState(ProjectFileState.added());
            } else {
                //We only need to affect the last path entry
                if (i < directories.length - 1)
                    pf.setState(status);
            }

            if (i < directories.length - 1)
                constrPath += directories[i + 1];

            dirs.add(pf);
            debug("Adding directory " + pf);
        }
        dbs.addRecords(dirs);
        return dirs;
    }
    
    /**
     * Update the validUntil field after all files have been processed.
     */
    private void updateValidUntil(ProjectVersion pv, List<ProjectFile> versionFiles) {

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
