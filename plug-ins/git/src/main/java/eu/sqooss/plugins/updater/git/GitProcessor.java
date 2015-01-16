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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

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
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.Revision;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.SCMNodeType;
import eu.sqooss.service.util.FileUtils;

public class GitProcessor {
    
	private StoredProject project;
    private GitAccessor git;
    private DBService dbs;
    private GitMessageHandler msg;
    private GitFileManager filem;
    
    public GitProcessor(StoredProject project, GitAccessor git, DBService dbs, Logger log) {
    	this.project = project;
    	this.git = git;
    	this.dbs = dbs;
    	msg = new GitMessageHandler(project.getName(), log);
    	filem = new GitFileManager(project, log);
    }
    
    public ProjectVersion processOneRevision(Revision entry) 
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
        ProjectVersion prev = pv.getPreviousVersion();
        if (prev != null)
            pv.setSequence(prev.getSequence() + 1);
        else 
            pv.setSequence(1);
              
        //Branches and parent-child relationships
        for (String parentId : entry.getParentIds()) {
            ProjectVersion parent = ProjectVersion.getVersionByRevision(project, parentId);
            ProjectVersionParent pvp = new ProjectVersionParent(pv, parent);
            pv.getParents().add(pvp);
            
            //Parent is a branch
            if (git.getCommitChidren(parentId).length > 1) {
                Branch b = new Branch(project, Branch.suggestName(project));
                dbs.addRecord(b);
                parent.getOutgoingBranches().add(b);
                pv.getIncomingBranches().add(b);
            } else {
                pv.getIncomingBranches().add(parent.getBranch());
            }
        }

        if (entry.getParentIds().size() > 1) {
            //A merge commit
            Branch b = new Branch(project, Branch.suggestName(project));
            pv.getOutgoingBranches().add(b);
        } else {
            //New line of development
            if (entry.getParentIds().size() == 0) {
                Branch b = new Branch(project, Branch.suggestName(project));
                dbs.addRecord(b);
                pv.getOutgoingBranches().add(b);
            } else {
                pv.getOutgoingBranches().addAll(pv.getIncomingBranches());
                //TODO: Add branch to Branch, need to convert it to List :-(
            }
        }

        msg.debug("Got version: " + pv.getRevisionId() +  
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
    
    /*
     * Copy operations copy or move files or directories accross
     * the virtual filetree generated by the SCM.
     */
    public void processCopiedFiles(SCMAccessor scm, Revision entry,
            ProjectVersion curVersion, ProjectVersion prev)
            throws InvalidProjectRevisionException, InvalidRepositoryException {
        for (CommitCopyEntry cce : entry.getCopyOperations()) {
            
        	/* We only want to process copies within allowed paths or
        	 *  from anywhere to an allowed path
        	 */
        	if (!filem.canProcessCopy(cce.fromPath(), cce.toPath()) && 
        			!filem.canProcessPath(cce.toPath())) {
        		msg.debug("Ignoring copy from " + cce.fromPath() + " to " 
        				+ cce.toPath() + " due to project config");
        		continue;
        	}
        	
            ProjectFile copyFrom = null;
            copyFrom = ProjectFile.findFile(project.getId(), 
                        FileUtils.basename(cce.fromPath()), 
                        FileUtils.dirname(cce.fromPath()), 
                        cce.fromRev().getUniqueId());
                
            /* Source location is an entry we do not have info for, 
             * due to updater settings. Use the SCM to retrieve
             * the missing info.
             */
            /*if (copyFrom == null) {
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
            
            msg.debug("copyFiles(): Copying " + cce.fromPath() + "->" + cce.toPath());
            if (copyFrom.getIsDirectory()) {
                    
                Directory from = Directory.getDirectory(cce.fromPath(), false);
                Directory to = Directory.getDirectory(cce.toPath(), true);

                /*
                 * Recursively copy contents and mark files as modified
                 * and directories as added
                 */
                filem.handleDirCopy(curVersion, 
                        ProjectVersion.getVersionByRevision(curVersion.getProject(),
                        cce.fromRev().getUniqueId()), from, to, copyFrom);
            } else {
                /*
                 * Create a new entry at the new location and mark the new 
                 * entry as ADDED
                 */
                filem.addFile(curVersion, cce.toPath(), ProjectFileState.added(), 
                		SCMNodeType.FILE, copyFrom);
            }
            
            if (cce.isMove()) {
            	msg.debug("copyFiles(): Deleting old path " + cce.fromPath() + "->" + cce.toPath());
            	if (copyFrom.getIsDirectory())
            		curVersion.getVersionFiles().addAll(filem.handleDirDeletion(copyFrom, curVersion));
            	else 
            		filem.addFile(curVersion, cce.fromPath(), 
            				ProjectFileState.deleted(), SCMNodeType.FILE, null);
            }
        }
    }
    
    public void processRevisionFiles(SCMAccessor scm, Revision entry,
            ProjectVersion curVersion) throws InvalidRepositoryException {
       
        for (String chPath : entry.getChangedPaths()) {
            
            SCMNodeType t = scm.getNodeType(chPath, entry);

            ProjectFile file = filem.addFile(curVersion, chPath,
                    ProjectFileState.fromPathChangeType(entry.getChangedPathsStatus().get(chPath)), 
                    t, null);
            /*
             * Before entering the next block, examine whether the deleted
             * file was a directory or not. If there is no path entry in the
             * Directory table for the processed file path, this means that
             * the path is definitely not a directory. If there is such an
             * entry, it may be shared with another project; this case is
             * examined upon entering
             */
            if (file.isDeleted() && (Directory.getDirectory(chPath, false) != null)) {
                /*
                 * Directories, when they are deleted, do not have type DIR,
                 * but something else. So we need to check on deletes
                 * whether this name was most recently a directory.
                 */
                ProjectFile lastVersion = file.getPreviousFileVersion();
                
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
                    msg.warn("Cannot find previous version of DELETED" +
                                " directory " + file.getFileName());
                }
                
                if (!delAfterCopy) {
                    curVersion.getVersionFiles().addAll(filem.handleDirDeletion(file, curVersion));
                } else {
                	msg.warn("FIXME: DELETED DIRECTORY AFTER COPY");
                    //handleCopiedDirDeletion(toAdd);
                }
            }
        }
    }
    
}
 