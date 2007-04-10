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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.sqooss.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import eu.sqooss.vcs.*;

/**
 * @author circular
 * 
 */
public class SimpleTest {

    /**
     * @param args
     */
    private Repository repository;

    private RepositoryType type;

    private String serverPath;

    private String localPath;

    private String username;

    private String password;

    public SimpleTest() throws Exception {
        serverPath = "https://svn.sqo-oss.eu/";
        localPath = "./svntest";
        username = "svnviewer";
        password = "Sq0V13weR";
        type = RepositoryType.SVN;
        repository = RepositoryFactory.getRepository(localPath, serverPath,
                username, password, type);
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }

    private void storeProjectInfo() {
        // url, name, website, contactPoint, srcPath, mailPath
    }

    private void checkOut(long revision) {
        repository.checkout(new Revision(revision));
        Vector<String> files = new Vector<String>();
        Vector<Double> results = new Vector<Double>();
        repository.listEntries(files, /* which path? */localPath,
                new Revision(revision));
        int i = 0, size = 0;
        size = files.size();
        while (i != size) {
            results.add(runWCTool(files.elementAt(i), revision));
            i++;
        }
        // TODO: obtain list of files stored locally, run storeProjectFiles
    }

    // private void storeProjectFiles

    private double runWCTool(String file, long revision) {
        double result = 0.0;
        String args = "wc -l \"" + file + "\"";
        String output = "";
        Process p;
        try {
            p = Runtime.getRuntime().exec(args);
            BufferedReader b = new BufferedReader(new InputStreamReader(p
                    .getInputStream()));
            output = b.readLine();
            b.close();
        } catch (IOException e) {
            return 0.0;
        }

        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(output);
        if (matcher.find()) {
            result = new Double(matcher.group(0));
        } else {
            result = 0.0;
        }

        return result;

    }

    private void storeMetrics(int projectID, long revision, String file) {
        // run CW tool, get the metric value, store it in db
    }
}
