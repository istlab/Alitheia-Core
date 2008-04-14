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

package eu.sqooss.impl.service.tds;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.diff.SVNDeltaProcessor;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindow;

import eu.sqooss.service.fds.InMemoryDirectory;

import eu.sqooss.service.logging.Logger;

public class CheckoutEditor implements ISVNEditor {
    private long targetRevision;
    private File localPath;
    private String repoDir; // Directory path within the repo
    private String repoFilePathName; // Filename below that dir
    private InMemoryDirectory rootDirectory;
    private SVNDeltaProcessor deltaProcessor;
    public static Logger logger;

    public CheckoutEditor(long r, File p) {
        targetRevision = r;
        localPath = p;
        deltaProcessor = new SVNDeltaProcessor();
        logger.info("Checkout editor created for r." + r + " in " + p);
    }

    public CheckoutEditor(long r, InMemoryDirectory dir) {
        targetRevision = r;
        rootDirectory = dir;
        deltaProcessor = new SVNDeltaProcessor();
        logger.info("Checkout editor created for r." + r + " in " + dir.getPath());
    }

    public void targetRevision(long revision) {
        if (revision != targetRevision) {
            logger.warn("SVN revision changed beneath us.");
        }
    }

    public void openRoot(long revision) {
    }

    public void addDir(String path, String sourcePath, long sourceRevision) {
        logger.info("Server adds directory " + path);
    }

    public void openDir(String path, long revision) {
        repoDir = path;
        logger.info("Server changed to directory " + path);
    }

    public void changeDirProperty(String name, String value) {
    }

    /**
     * Since we want to get paths relative to the original repoPath
     * for this editor, we normalise the paths received from the
     * server (which are relative to the SVN root of the repo)
     * to point within the tree repoPath.
     */
    private String normalisePath(String path) {
        if (repoDir == null) {
            return path;
        }
        if (path.startsWith(repoDir)) {
            // Empty for loop, just counts how many separatorChars
            // there are between the repoDir part and the path
            // part of the string.
            int i;
            for (i=repoDir.length(); i<path.length() &&
                path.charAt(i)==File.separatorChar; i++) ;
            return path.substring(i);
        } else {
            logger.warn("Weird path " + path + " not within repoDir (" + repoDir + ")");
            return path;
        }
    }

    public void addFile(String path, String sourcePath, long sourceRevision) {
        repoFilePathName = normalisePath(path);
        logger.info("Server adds file " + path + " (as " + repoFilePathName + ")");
    }

    public void openFile(String path, long revision) {
        logger.info("Server opens file " + path);
    }

    public void changeFileProperty(String path, String name, String value) {
    }

    static int filecount = 0;
    public void applyTextDelta(String path, String checksum)
        throws SVNException {
        if (localPath != null ) {
            repoFilePathName = normalisePath(path);
            deltaProcessor.applyTextDelta(null,new File(localPath,repoFilePathName), false);
        } else if (rootDirectory != null ) {
            rootDirectory.addFile(path);
        } else {
            logger.error("Tried to checkout to nowhere...");
        }
        filecount++;
    }

    public OutputStream textDeltaChunk(String path, SVNDiffWindow w)
        throws SVNException {
        return deltaProcessor.textDeltaChunk(w);
    }

    public void textDeltaEnd(String path) {
        deltaProcessor.textDeltaEnd();
    }

    public void closeFile(String path, String checksum) {
    }

    public void closeDir() {
    }

    public void deleteEntry(String path, long revision) {
        logger.info("Server deletes " + path);
    }

    public void absentDir(String path) {
        logger.info("Server absents " + path);
    }

    public void absentFile(String path) {
        logger.info("Server absents " + path);
    }

    public SVNCommitInfo closeEdit() {
        logger.info("Checked out " + filecount + " files.");
        return null;
    }

    public void abortEdit() {
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

