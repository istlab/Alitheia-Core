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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.LRUMap;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

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

class SourceUpdater extends Job {
    private UpdaterServiceImpl updater;
    private StoredProject project;
    private TDSService tds;
    private DBService dbs;
    private Logger logger;
    private MetricActivator ma;

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
        
    }

    public int priority() {
        return 1;
    }

    protected void run() {
        
        int numRevisions = 0;
        
        /* 
         * Cache project version and project file IDs for kick-starting
         * metric update jobs after the metadata update. This is done
         * to avoid holding references to huge data graphs on large 
         * updates
         */
        SortedSet<Long> updProjectVersions = new TreeSet<Long>();
        SortedSet<Long> updFiles = new TreeSet<Long>();

        /*Avoid Hibernate thrasing by caching frequently accessed objects*/
        LRUMap devCache = new LRUMap(1000);
        LRUMap dirCache = new LRUMap(3000);

        logger.info("Running source update for project " + project.getName());
        Session s = dbs.getSession(this);
        long ts = System.currentTimeMillis();
        Transaction tx = s.beginTransaction();
        try {

            // This is the last version we actually know about
            ProjectVersion lastVersion = StoredProject.getLastProjectVersion(project);
            SCMAccessor scm = tds.getAccessor(project.getId()).getSCMAccessor();
            long lastSCMVersion = scm.getHeadRevision();
            CommitLog commitLog = scm.getCommitLog(
                    new ProjectRevision(lastVersion.getVersion() + 1),
                    new ProjectRevision(lastSCMVersion));

            logger.info(project.getName() + ": Log entries: " + commitLog.size());
            logger.info(project.getName() + ": Time to get log: " + 
                    (int)((System.currentTimeMillis() - ts)/1000));
            ts = System.currentTimeMillis();
            
            for (CommitEntry entry : commitLog) {
                
                ProjectVersion curVersion = new ProjectVersion();
                curVersion.setProject(project);
                // Assertion: this value is the same as lastSCMVersion
                curVersion.setVersion(entry.getRevision().getSVNRevision());
                curVersion.setTimestamp((long)(entry.getDate().getTime() / 1000));
                
                String author = entry.getAuthor();
                Developer d = null;
                d = (Developer)devCache.get(author);
                
                if (d == null) {
                    d = Developer.getDeveloperByUsername(s, entry.getAuthor(),
                            project);
                    devCache.put(author, d);
                }
                
                curVersion.setCommitter(d);
                
                /* TODO: get column length info from Hibernate */
                String commitMsg = entry.getMessage();
                if(commitMsg.length() > 512)
                	commitMsg = commitMsg.substring(0, 511);
                
                curVersion.setCommitMsg(commitMsg);
                /*TODO: Fix this when the TDS starts supporting SVN properties*/
                //curVersion.setProperties(entry.getProperties);
                s.save(curVersion);
                updProjectVersions.add(new Long(curVersion.getId()));

                logger.debug("Stored project version " + curVersion.toString());
                
                for(String chPath: entry.getChangedPaths()) {

                    SCMNodeType t = scm.getNodeType(chPath, entry.getRevision());
                    
                    /* TODO: We make the assumption that tags entries
                     * can only be directories, based on info obtained 
                     * from the SVN manual
                     * See: http://svnbook.red-bean.com/en/1.1/ch04s06.html 
                     */
                    if(t == SCMNodeType.DIR && isTag(entry, chPath)) {
                        
                        Tag tag = curVersion.addTag();
                        tag.setName(chPath.substring(5));
                        logger.info("Creating tag <" + tag.getName() + ">");
                        
                        s.save(tag);
                        break;
                    }
                    
                    ProjectFile pf = curVersion.addProjectFile();
                    String path = chPath.substring(0, chPath.lastIndexOf('/'));
                    if (path == null || path.equalsIgnoreCase("")) {
                        path = "/"; //SVN entry does not have a path
		    }
                    String fname = chPath.substring(chPath.lastIndexOf('/') + 1);

                    Directory dir = null;
                    dir = (Directory) dirCache.get(path);
                    if (dir == null) {
                        dir = Directory.getDirectory(s, path);
                        dirCache.put(path, dir);
                    }
                    
                    pf.setName(fname);
                    pf.setDir(dir);
                    pf.setStatus(entry.getChangedPathsStatus().get(chPath).toString());
                     
                    if (t == SCMNodeType.DIR) {
                        pf.setIsDirectory(true);
                    } else {
                        pf.setIsDirectory(false);
                    }
                    
                    /*If a dir was deleted, mark all children as deleted*/
                    if (t == SCMNodeType.DIR && 
                            pf.getStatus().equalsIgnoreCase("DELETED")) {
                        //markDeleted(s, pf, pf.getProjectVersion());
                    }
                    
                    s.save(pf);
                    updFiles.add(pf.getId());
                }
                
                numRevisions ++;
                
                /*Cleanup for huge projects*/
                if (numRevisions % 10000 == 0) {
                    tx.commit();
                    tx = s.beginTransaction();
                }
            }
            tx.commit();
            logger.info("Processed " + numRevisions + " revisions");
        } catch (InvalidRepositoryException e) {
            logger.error("Not such repository:" + e.getMessage());
            setState(State.Error);
        } catch (InvalidProjectRevisionException e) {
            logger.error("Not such repository revision:" + e.getMessage());
            setState(State.Error);
        } catch (HibernateException e) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (HibernateException ex) {
                    logger.error("Error while rolling back failed transaction"
                            + ". DB may be left in inconsistent state:"
                            + ex.getMessage());
                    ex.printStackTrace();
                }
                logger.error("Failed to commit updates to the database: "
                        + e.getMessage() + " Transaction rollbacked");
            }
            setState(State.Error);
        } finally {
            logger.info(project.getName() + ": Time to process entries: "
                    + (int) ((System.currentTimeMillis() - ts) / 1000));
            
            dbs.returnSession(s);
            
            /*Kickstart metrics*/
            ma.runMetrics(ProjectVersion.class, updProjectVersions);
            ma.runMetrics(ProjectFile.class, updFiles);

            updater.removeUpdater(project.getName(), UpdaterService.UpdateTarget.CODE);
        }
    }

    /**
     * Mark the contents of a directory as DELETED when the directory has
     * been DELETED 
     * @param s The Hibernate session to operate on
     * @param pf The project file representing the deleted directory
     */
    private void markDeleted(Session s, ProjectFile pf, ProjectVersion pv) {
        if (pf.getIsDirectory() == false)
            return;
        
        String paramVersion = "projectversion";
        String paramPath = "path";
        
        String query = "from ProjectFile pf where pf.projectVersion=:" +
        		paramVersion + " and pf.dir=:" + paramPath;         
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramVersion, ProjectVersion.getPreviousVersion(pv));
        parameters.put(paramPath, pf.getDir());

        List<ProjectFile> projectFiles = (List<ProjectFile>) dbs.doHQL(query, parameters);
        Iterator<ProjectFile> i = projectFiles.iterator();
        
        while (i.hasNext()) {
            ProjectFile pf1 = i.next();
            
            ProjectFile pf2 = new ProjectFile();
            pf2.setDir(pf1.getDir());
            pf2.setIsDirectory(pf1.getIsDirectory());
            pf2.setName(pf1.getName());
            pf2.setProjectVersion(pf1.getProjectVersion());
            pf2.setStatus("DELETED");
            
            s.save(pf2);
            
            if(pf1.getIsDirectory()) {
                markDeleted(s, pf1, pv);
            } 
        }
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
}

// vi: ai nosi sw=4 ts=4 expandtab
