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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

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
import eu.sqooss.service.util.BidiMap;
import eu.sqooss.service.util.FileUtils;
import eu.sqooss.service.util.Pair;

/**
 * A metadata updater converts raw data to Alitheia Core database metadata.
 */
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
    
    /*
     * Branch naming helper structures 
     */
    protected BidiMap<List<Integer>, List<Integer>> branchGraph = new BidiMap<List<Integer>, List<Integer>>();
    protected Map<List<Integer>, List<Integer>> availBranchNames = new HashMap<List<Integer>, List<Integer>>();
    protected int branchseq = 0;
    
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
        		info("Skipping processed revision: " + entry.getUniqueId());
        		continue;
        	}
        	
            ProjectVersion pv = processOneRevision(entry);
            
            processCopiedFiles(git, entry, pv, pv.getPreviousVersion());
            
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

        debug("Got version: " + pv.getRevisionId() +  
                " seq: " + pv.getSequence());
        return pv;
    }
    
    /**
     * Git enables easy branching, but does not store the name of branches after
     * deleting them or pushing to remote repositories. The following algorithm
     * attempts to provide a sane naming scheme for implicit branches.
     */
    // Beware: Java Collections crap in effect
    public String getBranchName(Revision rev) throws AccessorException,
            InvalidProjectRevisionException {
        String[] parents = rev.getParentIds().toArray(new String[0]);
        String[] children = git.getCommitChidren(rev.getUniqueId());
        String name = null;

        // Not a child of an existing commit --> a new branch
        // git branch "test" && git checkout test && touch a && git commit -a -m "test"
        if (parents.length == 0) {
            name = String.valueOf(branchseq++);
            return name;
        }

        Revision previous = git.getPreviousRevision(rev);
        List<Integer> previousBranchName = null;
        
        if (git.getCommitChidren(previous.getUniqueId()).length <= 1){
            previousBranchName = branchName(previous);
        } else {
            if (children.length > 1) {
                previousBranchName = new ArrayList<Integer>();
                previousBranchName.addAll(getAvailBranchName(previous));
            }
        }
        
        if (children.length > 1) {
            // The commit has more than 1 child -> creates a branch
            List<Integer> newBranches = new ArrayList<Integer>();
            //Create branch names
            for (int i = 0; i < children.length; i++)
                newBranches.add(branchseq++);

            //Store the operation in the current branch graph
            branchGraph.put(previousBranchName, newBranches);
            debug(rev.getUniqueId() + " branch " + previousBranchName + "->" + newBranches);

            // Store the resulting branch names for the children to use when
            // they are processed.
            // Create a copy, as the copied list is manipulated afterwards
            Integer[] arr = newBranches.toArray(new Integer[1]);
            ArrayList<Integer> newBranchesCopy = new ArrayList<Integer>(Arrays.asList(arr));
            availBranchNames.put(previousBranchName, newBranchesCopy);
            name = toBranchName(previousBranchName);

        } else {
            if (parents.length > 1) {
                /*
                 * Merge commit, get parent branch names and combine them. 
                 */
                List<Integer> names = new ArrayList<Integer>();
                for (String parent : parents) {
                    /*
                     * The following means that an existing branch was merged in
                     * this commit with another one but the development
                     * continued for both branches.
                     */
                    if (Arrays.asList(git.getCommitChidren(parent)).contains(rev.getUniqueId())
                            && git.getCommitChidren(parent).length > 1) {
                        names.addAll(getAvailBranchName(git.newRevision(parent)));
                    } else {
                        names.addAll(branchName(git.newRevision(parent)));
                    }
                }
                Collections.sort(names);
                //debug("Merge request for " + names);
               
                /*
                 * Reduce potential name by taking advantage of existing branch
                 * parent-child hierarchies. To do so, the algorithm searches
                 * for the longest branch identifier sequence in the branch name
                 * that can be replaced with the name of the originating branch.
                 * For example, if we have recorded the following branch
                 * operations
                 * 
                 *   1 -> {3,4,5} 
                 *   2 -> {6,9,10}
                 * 
                 * and the merge name, as computed above, is
                 * 
                 *  {3,4,5,9}
                 * 
                 * the reduce operation will produce: {1, 9} The algorithm uses
                 * two pointers, one traversing the name array forwards and one
                 * backwards. For each iteration step of the backwards moving
                 * pointer, the string between the two pointers is compared to
                 * the values of the hashtable that stores the branching
                 * operations. If there is a match (meaning that all branches
                 * that result from a branch operation are merged) the merge
                 * name is rewritten as described above.
                 */
                Integer[] namesarr = names.toArray(new Integer[1]);
                for (int i = 0; i < namesarr.length; i++) {
                    int j = namesarr.length;
                    for (; j > i + 1; j--) {
                        Integer[] tmp = new Integer[j - i];
                        System.arraycopy(namesarr, i, tmp, 0, j - i);
                        List<Integer> tmpList = Arrays.asList(tmp);
                        List<Integer> match = branchGraph.getKey(tmpList);
                        //debug("attempting match " + tmpList);
                        if (match != null) {
                            // Found a replacement!
                            debug(rev.getUniqueId() + " merge " + tmpList + " -> " + match);
                            Integer[] toCopy = new Integer[namesarr.length - (j - i) + match.size()];
                            System.arraycopy(namesarr, 0, toCopy, 0, i);
                            System.arraycopy(match.toArray(new Integer[0]), 0, toCopy, i, match.size());
                            System.arraycopy(namesarr, j, toCopy, i + 1,namesarr.length - j);
                            namesarr = toCopy;
                            i = -1;
                            branchGraph.remove(match);
                            break;
                        }
                    }
                }
                name = toBranchName(Arrays.asList(namesarr));

            } else {
                if (git.getCommitChidren(previous.getUniqueId()).length > 1) {
                    // The previous commit generated a branch. Get the first
                    // unused branch name. All the first childs of branches 
                    // must get a new branch name.
                   name = getAvailBranchName(previous).toString();
                } else {
                    // Just re-use the branch name from the previous commit
                    name = toBranchName(previousBranchName);
                }
            }
        }
        return name;
    }
    
    /**
     * Search the list of available branch names for the children of a specific
     * branch point and return the first available.
     */
    protected  List<Integer> getAvailBranchName(Revision previous) {
        List<Integer> prevBranch = branchName(previous);
        Integer name = availBranchNames.get(prevBranch).remove(0);

        if (availBranchNames.get(prevBranch).size() == 0) {
            //debug("removing " + branchName(previous));
            availBranchNames.remove(prevBranch);
        }
        ArrayList<Integer> names = new ArrayList<Integer>();
        names.add(name);
        return names;
    }

    /**
     * Return the number of branches stored by the project
     */
    protected int getNumBranches() {
    	return project.getBranches().size();
    }
    
    /**
     * Get the name of the branch the provided revision belongs to
     */
    protected List<Integer> branchName(Revision rev) {
    	ProjectVersion v = ProjectVersion.getVersionByRevision(project, rev.getUniqueId());
    	List<Integer> branches = new ArrayList<Integer>();
    	for (Branch b : v.getIncomingBranches())
    	    branches.addAll(branchNameToList(b.getName()));
    	    
    	return branches;
    }
    
    /**
     * Convert a String (db or otherwise stored) branch name to the canonical
     * list of integers naming scheme.
     */
    protected List<Integer> branchNameToList(String name) {
        StringTokenizer st = new StringTokenizer(name, ",");
        ArrayList<Integer> result = new ArrayList<Integer>();
        
        while (st.hasMoreTokens()) {
            result.add(Integer.parseInt(st.nextToken()));
        }
        
        return result;
    }
    
    /**
     * Convert an internal representation of a branch name to a string
     */
    protected String toBranchName(List<Integer> name) {
        StringBuffer b = new StringBuffer();
        for (Integer i : name) {
            b.append(i).append(",");
        }
        b.deleteCharAt(b.length() - 1);
        return b.toString();
    }
    
    /**
     * Init the branch naming data structures.
     */
    private void initBranchNaming(Revision next)
        throws InvalidProjectRevisionException, InvalidRepositoryException,
        AccessorException {
        long ts = System.currentTimeMillis();

        branchseq = getNumBranches();
        CommitLog log = git.getCommitLog("", git.getFirstRevision(), next);

        for (Revision entry : log) {
            getBranchName(entry);
        }
        debug("initBranchNaming(): " + (System.currentTimeMillis() - ts) + " ms");
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
            copyFrom = ProjectFile.findFile(project.getId(), 
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
                    
                Directory from = Directory.getDirectory(cce.fromPath(), false);
                Directory to = Directory.getDirectory(cce.toPath(), true);

                /*
                 * Recursively copy contents and mark files as modified
                 * and directories as added
                 */
                handleDirCopy(curVersion, 
                        ProjectVersion.getVersionByRevision(curVersion.getProject(),
                        cce.fromRev().getUniqueId()), from, to, copyFrom);
            } else {
                /*
                 * Create a new entry at the new location and mark the new 
                 * entry as ADDED
                 */
                addFile(curVersion, cce.toPath(), ProjectFileState.added(), 
                		SCMNodeType.FILE, copyFrom);
            }
            
            if (cce.isMove()) {
            	debug("copyFiles(): Deleting old path " + cce.fromPath() + "->" + cce.toPath());
            	if (copyFrom.getIsDirectory())
            		curVersion.getVersionFiles().addAll(handleDirDeletion(copyFrom, curVersion));
            	else 
            		addFile(curVersion, cce.fromPath(), 
            				ProjectFileState.deleted(), SCMNodeType.FILE, null);
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
        ProjectFile cur = ProjectFile.findFile(project.getId(), fname,
        		path, version.getRevisionId(), true);

        if (cur != null && 
        	!cur.getProjectVersion().getRevisionId().equals(version.getRevisionId()) &&
        	!isValidStateTransition(cur.getState(), status)) {
        	ProjectFileState newstatus = ProjectFileState.fromStatus(invTransitionFix.get(cur.getState().getStatus()));
        	debug("addFile(): Invalid state transition (" + cur.getState() + 
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
        Directory d = Directory.getDirectory(pf.getFileName(), false);
        if (d == null) {
            warn("Directory entry " + pf.getFileName() + " in project "
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
    
