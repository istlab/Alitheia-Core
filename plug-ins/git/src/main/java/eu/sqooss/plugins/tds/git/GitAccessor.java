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

package eu.sqooss.plugins.tds.git;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.CommitTimeRevFilter;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.AnnotatedLine;
import eu.sqooss.service.tds.CommitCopyEntry;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.Diff;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.PathChangeType;
import eu.sqooss.service.tds.Revision;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.SCMNode;
import eu.sqooss.service.tds.SCMNodeType;

/**
 * An accessor for Git repositories. Encapsulates the functionality provided by
 * the JGit library. Known limitations:
 * 
 * <ul>
 * <li>JGit does not (yet?) support resolving commits by timestamp, we
 * approximate this by walking the log file around the desired timestamp</li>
 * <li>The accessor only supports on disk mirrors of repositories, connecting
 * to remote ones is not yet supported.</li>
 * </ul>
 * 
 * @author Georgios Gousios - <gousiosg@gmail.com>
 */
public class GitAccessor implements SCMAccessor {
    public static String ACCESSOR_NAME = "GitAccessor";
    private static List<URI> supportedSchemes;
    
    private URI uri;
    private String projectname;
    private Repository git = null;
    private Logger logger = null;
    
    private Map<String, List<String>> childrenOf;
    
    static {
        supportedSchemes = new ArrayList<URI>();
        supportedSchemes.add(URI.create("git-file://www.sqo-oss.org"));
    }
    
	@Override
	public String getName() {
		return ACCESSOR_NAME;
	}

	@Override
	public List<URI> getSupportedURLSchemes() {
		return supportedSchemes;
	}

	@Override
	public void init(URI dataURL, String projectName) 
	throws AccessorException {

        doInit(dataURL, projectName);
	    this.logger = AlitheiaCore.getInstance().getLogManager().createLogger(Logger.NAME_SQOOSS_TDS);
        info("Created SCMAccessor for " + uri.toASCIIString());
        this.projectname = projectName;
	}
	
	/** {@inheritDoc} */
    public Revision newRevision(Date d) {
        if (d == null) {
            err("Cannot resolve commit with empty date");
            return null;
        }
        /*
         * Approximate revision resolution with a tree walk, as JGit does not
         * currently support revision resolution by timestamp. Given that Git stores
         * objects with millisecond accuracy, the following filter should just
         * return the revision we are looking for. 
         */
        RevWalk rw = new RevWalk(git);

        RevFilter exact = CommitTimeRevFilter.between(new Date(d.getTime() - 1), 
                new Date(d.getTime() + 1));
        rw.setRevFilter(exact);
        try {
            AnyObjectId headId = git.resolve(getHeadRevision().getUniqueId());
            RevCommit root = rw.parseCommit(headId);
            rw.markStart(root);
            RevCommit r = rw.next();

            if (r == null) {
                err("Cannot resolve commit with timestamp: " + d);
                return null;
            }
            
            return getRevision(r, false);
        } catch (Exception e) {
           err("Cannot resolve commit with timestamp: " + d + ":" 
                   + e.getMessage());
        } finally {
            rw.release();
        }
        return null;
    }

    public RevWalk createRevWalk(Repository git) {
		return new RevWalk(git);
	}

    /** {@inheritDoc} */
    public Revision newRevision(String uniqueId) {

        if (uniqueId == null || uniqueId.equals("")) {
            err("Cannot create new revision with null or empty revisionid");
            return null;
        }
        return getRevision(resolveGitRev(uniqueId), false);
    }

    /** {@inheritDoc} */
    public Revision getHeadRevision() throws InvalidRepositoryException {

        RevCommit head = resolveGitRev(Constants.HEAD);

        if (head == null) {
            throw new InvalidRepositoryException(uri.toString(),
                    "HEAD does not point to a known revision");
        }

        return getRevision(head, false);
    }

    /** {@inheritDoc} */
    public Revision getFirstRevision() throws InvalidRepositoryException {
        RevWalk rw = new RevWalk(git);
        RevCommit c = null;
        AnyObjectId headId;
        try {
            headId = git.resolve(Constants.HEAD);
            RevCommit root = rw.parseCommit(headId);
            rw.sort(RevSort.REVERSE);
            rw.markStart(root);
            c = rw.next();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            rw.release();
        }

        return getRevision(c, false);
    }

    /** {@inheritDoc} */
    public Revision getPreviousRevision(Revision r)
        throws InvalidProjectRevisionException {
        AnyObjectId revId;  
        RevWalk rw = new RevWalk(git);

        try {
            revId = git.resolve(r.getUniqueId());
            
            if (revId == null) {
                throw new InvalidProjectRevisionException(
                        "r" + revId + " is not known", getClass());
            }
            RevCommit commit = rw.parseCommit(revId);
            rw.sort(RevSort.TOPO);
            rw.markStart(commit);
            rw.next();
            RevCommit prev = rw.next();
            return getRevision(prev, false);
        } catch (IOException e) {
            throw new InvalidProjectRevisionException(
                    "Cannot get next revision: "+ e.getMessage(), 
                    getClass());
        } finally {
            rw.release();
        }
    }

    /** {@inheritDoc} */
    public Revision getNextRevision(Revision r)
        throws InvalidProjectRevisionException {
        AnyObjectId revId;
        RevWalk rw = new RevWalk(git);
        
        try {
            /*
             * We tell JGit to return all commits whose timestamp is
             * after the provided revision date, but also in ascending
             * timestamp order (REVERSE strategy). 
             */
            revId = git.resolve(Constants.HEAD);
            RevFilter exact = CommitTimeRevFilter.after(r.getDate());
            rw.sort(RevSort.REVERSE);
            rw.setRevFilter(exact);
            if (revId == null) {
                throw new InvalidProjectRevisionException(
                        "r" + revId + " is not known", getClass());
            }

            rw.markStart(rw.parseCommit(revId));
            RevCommit start = rw.parseCommit(git.resolve(r.getUniqueId()));
            RevCommit next = rw.next();
            
            /*
             * The following conditions take care of the extremely rare case
             * where two commits share the exact same commit timestamp. The
             * loop tries to find the first commit which is not the parent
             * of the provided commit. May fail if more than 2 commits share
             * the same timestamp.
             */
            while (next.equals(start) || 
            		(start.getParentCount() > 0 && start.getParent(0).equals(next))) {
            	next = rw.next();
            }
            return getRevision(next, false);
            
        } catch (IOException e) {
            throw new InvalidProjectRevisionException(
                    "Cannot get next revision: "+ e.getMessage(), 
                    getClass());
        } finally {
            rw.release();
        }
    }
    
    public boolean isValidRevision(Revision r) {
        
        if (!(r instanceof GitRevision))
            return false;
        
        if (!((GitRevision)r).isResolved())
        	r.getChangedPaths(); //This should trigger a resolution
        
        return ((GitRevision)r).isResolved();
    }
    
    public void getCheckout(String repoPath, Revision revision, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {return;}

    public void updateCheckout(String repoPath, Revision src,
        Revision dst, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {return;}

    public void getFile(String repoPath, Revision revision, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {return;}

    public void getFile(String repoPath, Revision revision, OutputStream stream)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {return;}
    
    public CommitLog getCommitLog(String repoPath, Revision r1, Revision r2)
    throws InvalidProjectRevisionException, InvalidRepositoryException  {
    	long time = System.currentTimeMillis();
        repoPath = toGitPath(repoPath);
        RevWalk rw = new RevWalk(git);
        try {
            
            if (r1 == null) {
                r1 = getHeadRevision();
            } 
           
            if (!((GitRevision) r1).isResolved())
                throw new InvalidProjectRevisionException(r1.getUniqueId(),
                        this.getClass());

            if (r2 != null && !((GitRevision) r2).isResolved())
                throw new InvalidProjectRevisionException(r2.getUniqueId(),
                        this.getClass());
            
            if (repoPath != null && !repoPath.isEmpty()) {
                rw.setTreeFilter(AndTreeFilter.create(PathFilter.create(repoPath), TreeFilter.ANY_DIFF));
            }

            if (r2 != null) {
                RevFilter exact = CommitTimeRevFilter.between(r1.getDate(), r2.getDate());
                rw.setRevFilter(exact);
            }
            
            if (r2 == null)
                rw.markStart(rw.parseCommit(git.resolve(r1.getUniqueId())));
            else if (r2.getUniqueId().equals(getHeadRevision().getUniqueId())) {
                rw.markStart(rw.parseCommit(git.resolve(r2.getUniqueId())));
            } else{
                rw.markStart(rw.parseCommit(git.resolve(getNextRevision(r2).getUniqueId())));
            }
            
            Iterator<RevCommit> i = rw.iterator();

            GitCommitLog log = new GitCommitLog();

            while (i.hasNext()) {
                Revision r = getRevision(i.next(), false);
                log.entries().add(r);
                if (r2 == null)
                    break;
            }

            Collections.reverse(log.entries());
            return log;

        } catch (IOException ew) {
            throw new InvalidRepositoryException(this.uri.toString(),
                    ew.getMessage());
        } finally {
            rw.release();
            debug("getCommitLog(): " + (System.currentTimeMillis() - time) + "ms");
        }
    }

    public Diff getDiff(String repoPath, Revision r1, Revision r2)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {return null;}

    public SCMNodeType getNodeType(String repoPath, Revision r)
        throws InvalidRepositoryException {
        
        if (!isValidRevision(r))
            throw new InvalidRepositoryException(repoPath, 
                    "The provided revision is not valid: " + r);
        
        RevTree a = resolveGitRev(r.getUniqueId()).getTree();
        TreeWalk tw = null;
        try {
        	String path = toGitPath(repoPath);
        	
        	if (path.isEmpty()) //Only the root dir can have an empty path
        		return SCMNodeType.DIR;
        	
            tw = TreeWalk.forPath(git, path, a);
            
            if (tw == null) 
                return SCMNodeType.UNKNOWN;
            
            FileMode fm = tw.getFileMode(0);

            if (fm.equals(FileMode.REGULAR_FILE))
            	return SCMNodeType.FILE;
            if (fm.equals(FileMode.TREE))
            	return SCMNodeType.DIR;
            if(fm.equals(FileMode.GITLINK))
            	return SCMNodeType.DIR;
            if (fm.equals(FileMode.SYMLINK))
            	return SCMNodeType.FILE; //FIXME: Need to track down link target
            
        } catch (Exception e) {
            warn("Path " + repoPath + " does not exist in revision " 
                    + r.getUniqueId() + ":" + e.getMessage());
        } finally {
            if (tw != null) tw.release();
        }
        return SCMNodeType.UNKNOWN;
    }

    public String getSubProjectPath() throws InvalidRepositoryException 
        {return null;}
    
    public List<SCMNode> listDirectory(SCMNode dir)
        throws InvalidRepositoryException,
        InvalidProjectRevisionException  {
        return null;
    }
    
    @Override
    public SCMNode getNode(String path, Revision r)
            throws InvalidRepositoryException, InvalidProjectRevisionException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public List<AnnotatedLine> getNodeAnnotations(SCMNode s) {return null;}
    
    /*Methods available only to clients GitAccessor clients*/
    
    /**
     * Get a Map <code>{revisionId -> tagname}</code> for all revisions that 
     * are marked with a tag 
     */
    public Map<String, String> allTags() {
    	Map<String, Ref> all = git.getAllRefs();
		Map<String, String> result = new HashMap<String, String>();
		
		for (String ref : all.keySet()) {
			if (!ref.contains("/tags/"))
				continue;
			String tagname = ref.substring(ref.lastIndexOf('/') + 1);
			result.put(all.get(ref).getObjectId().getName(), tagname);
		}
		
		return result;
    }
    
    /**
     * Get the children (commits whose parents is the provided commit) 
     * of a commit. The returned array is sorted by commit time, so the
     * first entry corresponds to the first commit that is the immediate
     * child of the provided commit.
     * 
     * <b>Warning:</b> This method can cause the accessor to use a lot 
     * of memory in very large repositories, as it calculates and 
     * caches all parent-childrelationships beforehand. 
     * 
     * @throws AccessorException When an error occurs during 
     */
    public String[] getCommitChidren(String revisionId) throws AccessorException {
    	if (childrenOf == null) {
            childrenOf = new HashMap<String, List<String>>();
    		resolveChildren();
    	}
    	
    	Revision[] children = new Revision[childrenOf.get(revisionId).size()];
    	int i = 0;
    	for (String childid : childrenOf.get(revisionId)) {
    		children[i] = getRevision(resolveGitRev(childid), false);
    		i++;
    	}
    	
    	Arrays.sort(children, children[0]);
    	
    	String[] chIds = new String[children.length];
    	i = 0;
    	for (Revision r : children) {
    		chIds[i] = r.getUniqueId();
    		i++;
    	}
    	
    	return chIds;
    }
    
    private void resolveChildren() throws AccessorException {
    	Long start = System.currentTimeMillis();
    	RevWalk rw = new RevWalk(git);
    	try {
    		ObjectId revId = git.resolve(Constants.HEAD);
            rw.sort(RevSort.COMMIT_TIME_DESC); //Doesn't really do anything
            rw.markStart(rw.parseCommit(revId));
            RevCommit c;
            
            while((c = rw.next()) != null) {
            	for (RevCommit parent : c.getParents()) {
            		if (!childrenOf.containsKey(parent.getName())) {
            			childrenOf.put(parent.getName(), new ArrayList<String>());
            		}
            		childrenOf.get(parent.getName()).add(c.getName());
            	}
            }
    	} catch (Exception e) {
    		throw new AccessorException(this.getClass(), "Error getting " +
    				"commit children: " + e.getMessage());
		} finally {
    		rw.release();
    	}
    	Long msec = System.currentTimeMillis() - start;
    	debug("resolveChildren(): " + msec + " msec");
    }
    
    /* Accessor internal methods*/
    
    /*Init a test repository when unit testing*/
    public void testInit(URI dataURL, String projectName) 
    throws AccessorException {
        doInit(dataURL, projectName);
    }
    
    /*
     * Actual repo initialization code, construct a repository instance per
     * tracked project.
     */
    private void doInit(URI dataURL, String projectName) 
    throws AccessorException {
        this.uri = dataURL;
        this.projectname = projectName;
        try {
            RepositoryBuilder builder = new RepositoryBuilder();
            git = builder.setGitDir(toGitRepo(uri))
                .findGitDir() // scan up the file system tree
                .build();

        } catch (IOException e) {
            throw new AccessorException(this.getClass(), 
                    "Cannot initialise accessor for URL " + uri.toASCIIString());
        }
    }
   
    /*
     * Construct a full Revision object by analysing a commit's contents. Ideas 
     * and some code from JGit's log command implementation.
     */
    GitRevision getRevision(RevCommit commit, boolean resolve) {
    	if (commit == null)
            return null;
    	
    	if (!resolve)
    		return new GitRevision(commit, this);
    	    
        Map<String, PathChangeType> events = new HashMap<String, PathChangeType>();
        List<CommitCopyEntry> copies = new ArrayList<CommitCopyEntry>();
        
        //Special case for first revision, use a tree walk and mark all files
        //as added. 
        if (commit.getParentCount() == 0) {
            RevTree a = commit.getTree();
            TreeWalk tw = null;
            try {
                tw = new TreeWalk(git);
                tw.addTree(a);
                tw.setRecursive(true);
                while (tw.next()) {
                    //Paths in Alitheia Core are not relative to root
                    events.put("/" + tw.getPathString(), PathChangeType.ADDED);
                }
                events.put("/" + tw.getPathString(), PathChangeType.ADDED);
            } catch (Exception e) {
                err("Cannot get files for revision " + commit.getName() + ": " + e.getMessage());
            } finally {
                tw.release();
            }
            return new GitRevision(commit, events, copies);
        } 
        
        //General case, get the revision files by constructing a diff between 
        //the revision we are asking for and its first parent. 
        RevCommit c = resolveGitRev(commit.getParent(0).name());
        
        final RevTree a = c.getTree(); //We hope that the parent is resolvable.
        final RevTree b = commit.getTree();
        
        DiffFormatter diffFmt = new DiffFormatter( 
                new BufferedOutputStream(System.out));
        diffFmt.setRepository(git);
        diffFmt.setDetectRenames(true);
        diffFmt.getRenameDetector().setRenameLimit(1000);
        
        List<DiffEntry> entries;
        try {
            entries = diffFmt.scan(a, b);
        } catch (IOException e) {
            err("Cannot parse commit " + commit.getId());
            return null;
        }

        String path = null; PathChangeType pct = null;
        CommitCopyEntry cce = null;
        boolean isCopy = false;
        
        GitRevision gitrev = new GitRevision(commit, events, copies);
        
        for (DiffEntry ent : entries) {
            switch (ent.getChangeType()) {
            case ADD:
                path =  ent.getNewPath();
                pct = PathChangeType.ADDED;
                break;
            case DELETE:
                path =  ent.getOldPath();
                pct = PathChangeType.DELETED;
                break;
            case MODIFY:
                path =  ent.getNewPath();
                pct = PathChangeType.MODIFIED;
                break;
            case COPY:
              //Paths in Alitheia Core are not relative to root
                cce = new CommitCopyEntry(
                        "/" + ent.getOldPath(), 
                        newRevision(commit.getParent(0).getId().name()), 
                        "/" + ent.getNewPath(), 
                        gitrev);
                isCopy = true;
                break;
            case RENAME:
                cce = new CommitCopyEntry(
                        "/" + ent.getOldPath(), 
                        newRevision(commit.getParent(0).getId().name()), 
                        "/" + ent.getNewPath(), 
                        gitrev);
                cce.setMove();
                isCopy = true;
                break;
            }
            if (!isCopy)
              //Paths in Alitheia Core are not relative to root 
                events.put("/" + path, pct); 
            else 
                copies.add(cce);
        }

        return gitrev;
    }
    
    private RevCommit resolveGitRev(String rev) {
        RevWalk rw = new RevWalk(git);

        try {
            ObjectId obj = git.resolve(rev);
            RevCommit c = rw.parseCommit(obj);
            return c;
        } catch (Exception e) {
            warn("Cannot resolve revision: " + rev);
            return null;
        } finally {
            rw.release();
        }
    }
    
    private String toGitPath(String path) {
        
        if (path == null)
            return null;
        
        if (!path.startsWith("/"))
            return path;
        
        int i = 0;
        while (i < path.length() && path.charAt(i) == '/') {
            i++;
        }
        return path.substring(i);
    }
    
    /** Convert an Alitheia Core Git repository URL to an on-disk path*/
    private File toGitRepo(URI url) {
        File f = new File(url.getPath(), Constants.DOT_GIT);
        return f;
    }
    
    private void warn(String msg) {
        if (logger != null)
            logger.warn("GIT:" + msg);
    }
    
    private void err(String msg) {
        if (logger != null)
            logger.error("GIT:" +msg);
    }
    
    private void info(String msg) {
        if (logger != null)
            logger.info("GIT:" +msg);
    }
    
    private void debug(String msg) {
        if (logger != null)
            logger.debug("GIT:" +msg);
        else 
        	System.err.println("GIT: " + msg);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
