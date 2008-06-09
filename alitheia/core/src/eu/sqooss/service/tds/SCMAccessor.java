/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Adriaan de Groot <groot@kde.org>
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

package eu.sqooss.service.tds;

import java.io.File;
import java.io.FileNotFoundException;

public interface SCMAccessor extends NamedAccessor {
    /**
     * Get the numeric revision number for HEAD in this project.
     * Returns a negative value (usually -1) on error.
     */
    long getHeadRevision()
        throws InvalidRepositoryException;

    /**
     * Get the SVN revision number associated with this Project
     * Revision. May throw InvalidProjectRevision if there is
     * no way to do so, or a RuntimeException if something is
     * horribly wrong underneath.
     *
     * @return the SVN revision number in the project this
     *         SCMAccessor is attached to for the given revision @p r
     */
    long resolveProjectRevision(ProjectRevision r)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException;
    /**
     * Check if the revision is valid; this comes down to checking
     * if the revision @em number is set (not the date) to something
     * invalid (before 1 or after HEAD).
     */
    boolean isRevisionValid(ProjectRevision r)
        throws InvalidRepositoryException;

    /**
     * Retrieve a checkout of the complete source tree underneath
     * the given path, relative to the root URL of the project
     * to which this accessor is attached. The checkout is written
     * to the local path @p localPath .
     *
     * @p localPath @em must be a directory, or IllegalArgumentException
     * will be thrown. It must also exist already.
     *
     * If @p repoPath refers to a file, it is written as a single
     * file (named after its basename) in @p localPath. If @p repoPath
     * refers to a directory, the contents of the directory are placed
     * directly under @p localPath.
     *
     * This behavior mimics svn co repoUrl/repoPath/ localPath .
     */
    void getCheckout(String repoPath, ProjectRevision revision, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException;

    /**
     * Once you have a checkout you can change the revision for it
     * by calling updateCheckout, which modifies the files underneath
     * to reflect the new version. This must be called with the current
     * project revision as src, or very strange things may happen.
     */
    void updateCheckout(String repoPath, ProjectRevision src,
        ProjectRevision dst, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException;

    /**
     * Retrieve a single file from the source repository, relative
     * to the root URL of the project to which this accessor is
     * attached. The checked-out file is written to the local
     * path @p localPath.
     *
     * @param repoPath File within this repository to retrieve
     * @param revision Revision to use for the file
     * @param localPath Where to write the results.
     */
    void getFile(String repoPath, ProjectRevision revision, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException;

    /**
     * Retrieve a single file from the source repository, relative
     * to the root URL of the project to which this accessor is
     * attached. The checked-out file is written to the given stream.
     *
     * @param repoPath File within this repository to retrieve
     * @param revision Revision to use for the file
     * @param stream   Where to write the results.
     */
    void getFile(String repoPath, ProjectRevision revision, java.io.OutputStream stream)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException;

    /**
     * Get the commit log entries for revisions @p r1 to @p r2
     * for this source repository.
     */
    CommitLog getCommitLog(ProjectRevision r1, ProjectRevision r2)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException;

    /**
     * Get the commit log entries for revisions @p r1 to @p r2
     * for this source repository within the subtree identified
     * by the path @p repoPath (relative to the root URL of the
     * project this accessor is attached to).
     */
    CommitLog getCommitLog(String repoPath,
        ProjectRevision r1, ProjectRevision r2)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException;

    /**
     * Convenience method, like getCommitLog() with only
     * one revision. One entry is returned.
     */
    CommitEntry getCommitLog(String repoPath, ProjectRevision r)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException;

    /**
     * Get the diff between two revisions of a subtree within
     * the source repository. Arguments as getCommitLog(), above.
     * Passing in a null for @p r2 calculates the diff between
     * @p r1 and @p r1+1 . FileNotFoundException may also indicate
     * that the Diff could not be created (it is a temporary file).
     */
    Diff getDiff(String repoPath, ProjectRevision r1, ProjectRevision r2)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException;

    /**
     * Get the change that led to revision r. Roughly equivalent to
     * getDiff(repoPath,r.prev(),r).
     *
     * TODO: decide whether this should take repoPath into account and
     * return the most recent change in the path (like the SVN cli command
     * PREV revision) or just the r-1 revision.
     */
    Diff getChange(String repoPath, ProjectRevision r)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException;

    /**
     * Get the type of the node (File, Dir or Unknown)
     */
    SCMNodeType getNodeType(String repoPath, ProjectRevision r)
    	throws InvalidRepositoryException;

    /**
     * Get the subdirectory of the project within the SVN repsitory.
     * @throws InvalidRepositoryException
     */
    String getSubProjectPath() throws InvalidRepositoryException;
}

// vi: ai nosi sw=4 ts=4 expandtab

