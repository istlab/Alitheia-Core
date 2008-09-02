/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.impl.service.updater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.collections.LRUMap;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.Tag;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.tds.CommitCopyEntry;
import eu.sqooss.service.tds.CommitEntry;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.PathChangeType;
import eu.sqooss.service.tds.ProjectRevision;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.SCMNodeType;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterException;
import eu.sqooss.service.updater.UpdaterService;

final class SourceUpdater extends Job {
    
    private static final String HANDLE_COPIES_PROPERTY = "eu.sqooss.updater.handlecopies";
    
    private enum HandleCopies {
        TRUNK, BRANCHES, TAGS
    }
    
    private UpdaterServiceImpl updater;
    private StoredProject project;
    private TDSService tds;
    private DBService dbs;
    private Logger logger;
    private MetricActivator ma;
    private Scheduler sched;
    private AlitheiaCore core;
    
    private HandleCopies hc = HandleCopies.BRANCHES;
    
    /*
     * Cache project version and project file IDs for kick-starting
     * metric update jobs after the metadata update. This is done
     * to avoid holding references to huge data graphs on large
     * updates
     */
    private Set<Long> updProjectVersions = new TreeSet<Long>();
    private Set<Long> updFiles = new TreeSet<Long>();
    
    // Avoid Hibernate thrashing by caching frequently accessed objects
    private LRUMap dirCache = new LRUMap(200);
    
    //Store processed file and version IDs to inform the updater
    private Set<Long> procPVs = new TreeSet<Long>();
    private Set<Long> procPFs = new TreeSet<Long>();
    
    /* Currently processed commit log entry*/
    private CommitEntry comLogEntry;
    
    /**
     * Store jobs started by this Job (cannot be added as dependencies
     * after the job has started). 
     */
    private Vector<Job> dependedJobs = new Vector<Job>();
    
    public SourceUpdater(StoredProject project, UpdaterServiceImpl updater,
            AlitheiaCore core, Logger logger) throws UpdaterException {
        if ((project == null) || (core == null) || (logger == null)) {
            throw new UpdaterException(
                    "The components required by the updater are unavailable.");
        }

        this.project = project;
        this.updater = updater;
        this.logger = logger;
        this.tds = core.getTDSService();
        this.dbs = core.getDBService();
        this.ma = core.getMetricActivator();
        this.sched = core.getScheduler();
        this.core = core;
        
        String hcp = System.getProperty(HANDLE_COPIES_PROPERTY);
        
        if (hcp != null) {
            if (hcp.equalsIgnoreCase("trunk")) {
                hc = HandleCopies.TRUNK;
            } else if (hcp.equalsIgnoreCase("branches")) {
                hc = HandleCopies.BRANCHES;
            } else if (hcp.equalsIgnoreCase("tags")) {
                hc = HandleCopies.TAGS;
            } else {
                logger.warn("Not correct value for property " + HANDLE_COPIES_PROPERTY);
            }
        } else {
            logger.info("No value for " + HANDLE_COPIES_PROPERTY + " property.");
        }
    }

    public int priority() {
        return 1;
    }

    /**
     * @see eu.sqooss.service.scheduler.Job#run()
     *
     * @throws Exception as per the general contract of Job.run()
     */
    protected void run() throws Exception {
        dbs.startDBSession();
        int numRevisions = 0;

    
        /* Store version ids to be processed by fillFileForVersion */
        List<Long> latestVersionIDs = new ArrayList<Long>();
        
        logger.info("Running source update for project " + project.getName() 
                + " ID " + project.getId());
        
        CommitLog commitLog = null;
        SCMAccessor scm = null;
        
        try {
            // This is the last version we actually know about
            ProjectVersion versionDao = 
                StoredProject.getLastProjectVersion(project);
            long lastProjectVersion = 
                (versionDao != null) ? versionDao.getVersion() : 0;
                
            scm = tds.getAccessor(project.getId()).getSCMAccessor();
            long lastSCMVersion = scm.getHeadRevision();

            /* Don't choke when called to update an up-to-date project */
            if (lastProjectVersion >= lastSCMVersion) {
                dbs.commitDBSession();
                return;
            }

            commitLog = scm.getCommitLog(new ProjectRevision(
                    lastProjectVersion + 1),
                    new ProjectRevision(lastSCMVersion));

            logger.info(project.getName() + ": Log entries: "
                    + commitLog.size());
            
        } catch (InvalidRepositoryException e) {
            logger.error("Not such repository:" + e.getMessage());
            throw e;
        } catch (InvalidProjectRevisionException e) {
            logger.error("Not such repository revision:" + e.getMessage());
            throw e;
        }
        
        for (CommitEntry entry : commitLog) {
            this.comLogEntry = entry;
            ProjectVersion curVersion = new ProjectVersion(project);
            // Assertion: this value is the same as lastSCMVersion
            curVersion.setVersion(entry.getRevision().getSVNRevision());
            curVersion.setTimestamp((long) (entry.getDate().getTime() / 1000));

            String author = entry.getAuthor();
            Developer d  = Developer.getDeveloperByUsername(entry.getAuthor(), project);
            curVersion.setCommitter(d);

            /* TODO: get column length info from Hibernate */
            String commitMsg = entry.getMessage();
            if (commitMsg.length() > 512) {
                commitMsg = commitMsg.substring(0, 511);
            }

            curVersion.setCommitMsg(commitMsg);
            
            /* 
             * TODO: Fix this when the TDS starts supporting SVN properties 
             * 
             * curVersion.setProperties(entry.getProperties);
             * Use addRecord instead of adding to the list of versions in the
             * project so we don't need to load the complete list of revisions
             * (especially as we used getLastProjectVersion above) 
             */
            dbs.addRecord(curVersion);
            procPVs.add(curVersion.getId());
            latestVersionIDs.add(curVersion.getId());

            logger.debug(curVersion.getProject().getName() + ": Got version "
                    + curVersion.getVersion() + " ID " + curVersion.getId());
            
            for (String chPath : entry.getChangedPaths()) {

                //Defer processing of copied paths for after normal paths
                if (isCopiedPath(chPath)) {
                    continue;
                }
                
                SCMNodeType t = scm.getNodeType(chPath, entry.getRevision());

                /*
                 * We make the assumption that tags entries can only be
                 * directories, based on info obtained from the SVN manual See:
                 * http://svnbook.red-bean.com/en/1.1/ch04s06.html
                 */
                if (t == SCMNodeType.DIR && isTag(entry, chPath)) {

                    Tag tag = new Tag(curVersion);
                    tag.setName(chPath.split("tags/")[1]);
                    logger.debug("Creating tag <" + tag.getName() + ">");

                    dbs.addRecord(tag);
                    break;
                }

                ProjectFile pf = addFile(curVersion, chPath, 
                        entry.getChangedPathsStatus().get(chPath).toString(), t);
                
                /*
                 * Before entering the next block, examine whether the deleted
                 * file was a directory or not. If there is no path entry in the
                 * Directory table for the processed file path, this means that
                 * the path is definitely not a directory. If there is such an
                 * entry, it may be shared with another project; this case is
                 * examined upon entering
                 */
                if (pf.isDeleted() && (getDirectory(chPath, false) != null)) {
                        /*
                         * Directories, when they are deleted, do not have type DIR,
                         * but something else. So we need to check on deletes
                         * whether this name was most recently a directory.
                         */
                        ProjectFile lastIncarnation = ProjectFile.getPreviousFileVersion(pf);

                        /* If a dir was deleted, mark all children as deleted */
                        if (lastIncarnation != null
                                && lastIncarnation.getIsDirectory()) {
                            // In spite of it not being marked as a directory
                            // in the node tree right now.
                            pf.setIsDirectory(true);
                        }
                        handleDirDeletion(pf, curVersion);
                }
            }

            /*Process copy/move operations*/
            ProjectVersion prev = null;
            for (CommitCopyEntry copyOp : entry.getCopyOperations()) {
                if (prev == null) {
                    prev = ProjectVersion.getVersionByRevision(curVersion.getProject(), 
                            new ProjectRevision(curVersion.getVersion() - 1));
                }
                
                //Find the originating project file entry
                ProjectFile pf = ProjectFile.findFile(project.getId(), 
                        basename(copyOp.fromPath()), 
                        dirname(copyOp.fromPath()), prev.getVersion());
                
                if (pf == null) {
                    logger.warn("expecting 1 got " + 0 + " files for path " 
                            + copyOp.fromPath() + " (r" + prev.getVersion() + 
                            ") project " + project.getName() + " (" + project.getId() +")");
                    continue;
                }
                
                boolean isMove = false;
                
                /*
                 * An operation is marked as move when the copied path
                 * is deleted in the same commit
                 */
                for (String chPath : entry.getChangedPaths()) {
                    if (copyOp.fromPath().equals(chPath)
                            && entry.getChangedPathsStatus().get(chPath) == PathChangeType.DELETED) {
                        isMove = true;
                        break;
                    }
                }
                
                if (pf.getIsDirectory()) {
                    
                    Directory from = getDirectory(copyOp.fromPath(), false);
                    Directory to = getDirectory(copyOp.toPath(), true);
                    if (isMove) {
                        /*
                         * Delete the entry at the source location (but leave
                         * the contents intact), add a new entry for the new
                         * location and process all files as indicated above
                         */
                        logger.debug("Moving directory " + from.getPath() + " to " 
                                + to.getPath());
                        handleDirMove(curVersion, from, to);
                    } else {
                        /* 
                         * Recursively copy contents and mark files as modified
                         * and directories as added
                         */
                        logger.debug("Copying directory " + from.getPath() + " (from r" 
                                + copyOp.fromRev().getSVNRevision() + ") to " + to.getPath());
                        handleDirCopy(curVersion, ProjectVersion.getVersionByRevision(curVersion.getProject(), copyOp.fromRev()), from, to);
                    }   
                } else {
                    /*
                     * Create a new entry at the new location and mark the new 
                     * entry as modified
                     */
                    addFileIfNotExists(curVersion, copyOp.toPath(), "MODIFIED", SCMNodeType.FILE);
                    if (isMove) {
                        addFileIfNotExists(curVersion, copyOp.fromPath(), "DELETED", SCMNodeType.FILE);
                    }
                }
            }
            
            numRevisions++;

            /* Intermediate clean up */
            if (numRevisions % 5 == 0) {

                dirCache.clear();
                
                if (!dbs.commitDBSession()) {
                    logger.warn("Intermediate commit failed, restarting update");
                    restart();
                    return;
                } else {
                 //   fillFilesForVersion(latestVersionIDs);
                    updProjectVersions.addAll(procPVs);
                    updFiles.addAll(procPFs);
                }
                dbs.startDBSession();
                procPVs.clear();
                procPFs.clear();
                latestVersionIDs.clear();
            }
        }
        logger.info("Processed " + numRevisions + " revisions");
        
        if (!dbs.commitDBSession()) {
            logger.warn("Final commit failed, restarting update");
            restart();
            return;
        } 
        //fillFilesForVersion(latestVersionIDs);
        updProjectVersions.addAll(procPVs);
        updFiles.addAll(procPFs);
        
        here: for (Job j : dependedJobs) {
            if (j.state() == State.Queued || j.state() == State.Running) {
                j.waitForFinished();
                break here;
            }
        }
        
        ma.runMetrics(updProjectVersions, ProjectVersion.class);
        ma.runMetrics(updFiles, ProjectFile.class);
        
        updater.removeUpdater(project.getName(), UpdaterService.UpdateTarget.CODE);
    }

    /**
     * Checks whether the file to be added already exists, then calls 
     * {@link #addFile(ProjectVersion, String, String, SCMNodeType)}
     */
    private ProjectFile addFileIfNotExists(ProjectVersion version, String fPath, 
            String status, SCMNodeType t) {
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("projectVersion", version);
        properties.put("name", basename(fPath));
        properties.put("dir", getDirectory(dirname(fPath), false));
        properties.put("isDirectory", (t.equals(SCMNodeType.DIR)?true:false));
        List<ProjectFile> pfs = dbs.findObjectsByProperties(ProjectFile.class, properties);
        
        if (pfs.isEmpty()) {
            return addFile(version, fPath, status, t);
        }
        logger.debug("File " + basename(fPath) + " exists in " + dirname(fPath) + " for r" + 
                pfs.get(0).getProjectVersion().getVersion());
        return pfs.get(0);
    }
    
    /**
     * Constructs a project file out of provided elements
     */
    private ProjectFile addFile(ProjectVersion version, String fPath, 
            String status, SCMNodeType t) {
        ProjectFile pf = new ProjectFile(version);

        String path = dirname(fPath);
        String fname = basename(fPath);

        Directory dir = getDirectory(path, true);
        pf.setName(fname);
        pf.setDir(dir);
        pf.setStatus(status);

        if (t == SCMNodeType.DIR) {
            pf.setIsDirectory(true);
            getDirectory(pf.getFileName(), true);
        } else {
            pf.setIsDirectory(false);
        }

        dbs.addRecord(pf);
        procPFs.add(pf.getId());
        
        return pf;
    }
    
    /**
     * Check whether a path is in the list of copy operations for this revision
     */
    private boolean isCopiedPath(String path) {
        boolean copied = false;
        for (CommitCopyEntry copyOp : comLogEntry.getCopyOperations()) {
            if (path.equals(copyOp.fromPath()) || path.equals(copyOp.toPath())) {
                copied = true;
                break;
            }
        }

        return copied;
    }
    
    /**
     * Mark the contents of a directory as DELETED when the directory has been
     * DELETED
     * 
     * @param pf The project file representing the deleted directory
     */
    private void handleDirDeletion(ProjectFile pf, ProjectVersion pv) {
        
        if (pf==null || pv==null) {
            throw new IllegalArgumentException("ProjectFile or Version is null in markDeleted()");
        }
        
        if (pf.getIsDirectory() == false) {
            return;
        }
        
        /*
         * Don't delete subdirectories that have been copied elsewhere 
         * before the parent was deleted 
         */
        if (isCopiedPath(pf.getFileName())) {
            return;
        }
        
        // Check that the pf and the pv are consistent.
        if (pf.getProjectVersion().getProject().getId() != pv.getProject()
                .getId()) {
            throw new IllegalArgumentException("ProjectFile project "
                    + pf.getProjectVersion().getProject().getId()
                    + " and ProjectVersion project " + pv.getProject().getId()
                    + " mismatch.");
        }
        
        logger.debug("Deleting directory " + pf.getFileName() + " ID "
                + pf.getId());
        Directory d = getDirectory(pf.getFileName(), false);
        if (d == null) {
            logger.warn("Directory entry " + pf.getFileName() + " in project "
                    + pf.getProjectVersion().getProject().getName()
                    + " is missing in directory table.");
            return;
        }

        List<ProjectFile> files = ProjectFile.getFilesForVersion(pv, d);
        
        for (ProjectFile f : files) {
            if (f.getIsDirectory()) {
                handleDirDeletion(f, pv);
                ProjectFile mark = new ProjectFile(f, pv);
                mark.makeDeleted();
                dbs.addRecord(mark);
            }
        }
        for (ProjectFile f : files) {
            if (!f.getIsDirectory()) {
                ProjectFile mark = new ProjectFile(f, pv);
                mark.makeDeleted();
                /*
                 * Don't store a delete entry for files that have been 
                 * copied by a yet unprocessed operation 
                 */
                if (isCopiedPath(mark.getFileName())){
                    continue;
                }
                dbs.addRecord(mark);
            }
        }
    }

    /**
     * Handle directory moves 
     */
    private void handleDirMove(ProjectVersion pv, Directory from, Directory to) {
       
        if (!canCopy(from, to)) 
            return;
        
        /*Remove the directory*/
        addFile(pv, ProjectFile.findFile(project.getId(), 
                        basename(from.getPath()), 
                        dirname(from.getPath()), 
                        pv.getVersion()).getFileName(), 
                "DELETED", SCMNodeType.DIR);
        
        /*Add the directory to the new location*/
        addFileIfNotExists(pv, to.getPath(), "ADDED", SCMNodeType.DIR);
        
        /*Recursively remove all directories*/
        List<ProjectFile> fromPF = ProjectFile.getFilesForVersion(pv, from, ProjectFile.MASK_DIRECTORIES);
        
        for (ProjectFile f : fromPF) {
            handleDirMove(pv, getDirectory(f.getFileName(), false), 
                    getDirectory(to.getPath() + "/" + f.getName(), true));
        }
        
        /*Move the files from the source directory to the new location*/
        fromPF = ProjectFile.getFilesForVersion(pv, from, ProjectFile.MASK_FILES);
        
        for (ProjectFile f : fromPF) {
            addFile(pv, to.getPath() + "/" + f.getName(),
                    "MODIFIED", SCMNodeType.FILE);
        }
    }
    
    /**
     * Handle directory copies
     */
    private void handleDirCopy(ProjectVersion pv, ProjectVersion fromVersion,
            Directory from, Directory to) {
        
        if (!canCopy(from, to)) 
            return;
        
        ProjectFile copy = addFileIfNotExists(pv, to.getPath(), "ADDED", SCMNodeType.DIR);
        
        /*Recursively copy directories*/
        List<ProjectFile> fromPF = ProjectFile.getFilesForVersion(fromVersion, from, ProjectFile.MASK_DIRECTORIES);
        
        for (ProjectFile f : fromPF) {
            handleDirCopy(pv, fromVersion, getDirectory(f.getFileName(), false), 
                    getDirectory(to.getPath() + "/" + f.getName(), true));
        }
        
        fromPF = ProjectFile.getFilesForVersion(pv, from, ProjectFile.MASK_FILES);
        
        for (ProjectFile f : fromPF) {
            addFileIfNotExists(pv, to.getPath() + "/" + f.getName(),
                    "MODIFIED", SCMNodeType.FILE);
        }
    }
    
    /**
     * Decide whether a path can be copied depending on the value of 
     * the eu.sqooss.updater.handlecopies property.
     */
    private boolean canCopy(Directory from, Directory to) {
        
        if (hc.equals(HandleCopies.TAGS)) {
            return true;
        }
        
        if (hc.equals(HandleCopies.TRUNK) && 
                from.getPath().contains("trunk") &&
                from.getPath().contains("trunk")) {
                return true;
        }
        
        if (hc.equals(HandleCopies.BRANCHES)) {
            if (to.getPath().contains("tags")) {
                return false;
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Wrapper around 
     * {@link eu.sqooss.service.db.Directory#getDirectory(String, boolean)} 
     * that uses the local object cache before hitting the DB. 
     */
    private Directory getDirectory(String path, boolean create) {
       
        Directory dir = null;
        dir = (Directory) dirCache.get(path);
        if (dir == null) {
            dir = Directory.getDirectory(path, create);
            if (dir != null) {
                dirCache.put(path, dir);
            }
        } 
        return dir;
    }
    
    /**
     * Tell tags from regular commits (heuristic based)
     *  
     * @param entry
     * @param path
     * @return True if <tt>entry</tt> represents a tag
     */
    private boolean isTag(CommitEntry entry, String path) {
        if(!path.contains("/tags/"))
            return false;

        /* Prevent commits that create the tags/ directory
         * from being classified as tags
         */
        if(path.length() <= 5)
            return false;

        /* Tags can only be added (for the time being at least)*/
        if(entry.getChangedPathsStatus().get(path) != PathChangeType.ADDED)
            return false;

        /* If a path is not the prefix for all changed files
         * in a commit, then it is a leaf node (and therefore
         * not a tag)
         */
        for(String chPath: entry.getChangedPaths())
            if(!chPath.startsWith(path))
                return false;

        return true;
    }
    
   
    private void fillFilesForVersion(List<Long> toProcess) {
        Iterator<Long> i = toProcess.iterator();
        
        while(i.hasNext()) {
            Long projectVersion = i.next();
            FilesForVersionJob ffv = new FilesForVersionJob(projectVersion, core);
            dependedJobs.add(ffv);
            try {
                sched.enqueue(ffv);
            } catch (SchedulerException e) {
                logger.error("Error scheduling FilesForVersion table update job" 
                        + e.getMessage());
            }
        }
    }
    
    private String basename(String path) {
        String filename = path.substring(path.lastIndexOf('/') + 1);
        
        if (filename == null || filename.equalsIgnoreCase("")) {
            filename = "";
        }
        return filename;
    }
    
    private String dirname(String path) {
        String dirPath = path.substring(0, path.lastIndexOf('/'));
        if (dirPath == null || dirPath.equalsIgnoreCase("")) {
            dirPath = "/"; // SVN entry does not have a path
        }
        return dirPath;
    }
    
    private class FilesForVersionJob extends Job {

        private long versionID;
        private AlitheiaCore core; 
        
        public FilesForVersionJob(long versionID, AlitheiaCore core) {
            this.versionID = versionID;
            this.core = core;
        }
        
        @Override
        public int priority() {
            return 0xfda;
        }

        @Override
        protected void run() throws Exception {
            DBService dbs = core.getDBService();
            TDSService tds = core.getTDSService();
            
            dbs.startDBSession();
            ProjectVersion pv = dbs.findObjectById(ProjectVersion.class, versionID);
            SCMAccessor scm = tds.getAccessor(pv.getProject().getId()).getSCMAccessor();
       //     SCMNode root = scm.getInMemoryCheckout("/", new ProjectRevision(pv.getVersion()));
            
            if(!dbs.commitDBSession()) {
                logger.warn("commit failed - restarting job");
                restart();
            }
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
