/*
 * Copyright (c) Members of the SQO-OSS Collaboration, 2007
 * All rights reserved by respective owners.
 * See http://www.sqo-oss.eu/ for details on the copyright holders.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the SQO-OSS project nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package eu.sqooss.vcs;

import java.util.Vector;

/**
 * An abstract repository representation.
 */
public abstract class Repository {

    public String password;

    public String username;

    /* The path of the repository on the local end */
    protected String localPath;

    /* The path of the repository on the remote end */
    protected String serverPath;

    /* The current repository revision on the local end */
    protected Revision revision;

    /**
     * Initializes the Repository class attributes
     * 
     * @param localpath
     *            The path of the repository on the local end
     * @param serverpath
     *            The path of the repository on the remote end
     * @param user
     *            The username that is used to connect to the Repository
     * @param pass
     *            The password that is used to connect to the Repository
     */
    public Repository(String localpath, String serverpath, String user,
            String pass) {
        this.localPath = localpath;
        this.serverPath = serverpath;
        this.username = user;
        this.password = pass;
    }

    /**
     * Initialises a local copy of a repository, by checking out the current
     * revision of the repository server
     * 
     */
    public abstract void checkout();

    /**
     * Initialises a local copy of the repository, by checking out
     * 
     * @param rev
     *            The requested revision
     */
    public abstract void checkout(Revision rev);

    /**
     * Fetches the latest revision from the main repository server
     * 
     * @param rev
     *            The requested revision
     */
    public abstract void update(Revision rev);

    /**
     * Returns a Diff object between the current repository revision and an
     * older one revision
     * 
     * @param rev
     *            The requested revision
     * @return a Diff object
     */
    public abstract Diff diff(Revision rev);

    /**
     * Returns a Diff object between the start and end revisions
     * 
     * @param start
     *            The start revision
     * @param end
     *            The end revision
     * @return a Diff object
     */
    public abstract Diff diff(Revision start, Revision end);

    /**
     * Returns the commit log for all the commits between revisions start and
     * end
     * 
     * @param start
     *            The start revision
     * @param end
     *            The end revision
     * @return A CommitLog object
     */
    public abstract CommitLog getLog(Revision start, Revision end);

    /**
     * Returns the current version of either the remote or local version of the
     * repository
     * 
     * @param remote
     *            If remote is true get the remote current version. If it is
     *            false, get the local one
     * @return the current version as a long
     */
    public abstract long getCurrentVersion(boolean remote);

    /**
     * Obtains all files that exist in the repository tree
     * 
     * @param files
     *            A string vector where the filenames will be stored
     * @param path
     *            The path of the Repository
     * @param revision
     *            The requested revision
     */
    public abstract void listEntries(Vector<String> files, String path,
            Revision revision);

}
