/* This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 Georgios Gousios <gousiosg@gmail.com>
 * 
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

package eu.sqooss.impl.metrics.productivity;

import java.util.HashMap;
import java.util.Iterator;

import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.ProjectRevision;

/**
 * Code commit - A commit that affects at least 1 source code file
 */
public class CodeCommits extends ProductivityBase implements Runnable {

    HashMap<String, Integer> authorCommits = new HashMap<String, Integer>();

    protected CodeCommits() {
        super();
    }
    
    public void run() {
        System.out.println("Calculating Code Commits per developer...");
      //  SVNLogEntry entry = null;

        CommitLog log = null;
        
        try {
            log = svn.getCommitLog(new ProjectRevision(21000), 
                    new ProjectRevision(svn.getHeadRevision()));
        } catch (InvalidProjectRevisionException e) {
        } catch (InvalidRepositoryException e) {
        }
        
        System.out.println("Got " + log + " log entries");
/*
        //Get log entries
        Iterator<SVNLogEntry> i = log.iterator();

        while (i.hasNext()) {
            entry = i.next();
            //Iterate over changed paths
            Iterator<String> j = entry.getChangedPaths().keySet().iterator();
            while (j.hasNext()) {
                String path = j.next();
                //Get the entry type (binary or text)
                //  System.err.println("Path " + path + " is a " + FileTypeMatcher.getFileType(path) + " file");

                if (evaluate(path, entry))
                    //Author marked as having committed code, now break to next commit
                    break;
            }
        }

        System.err.println("Code Commits per developer");
        Iterator<String> k = authorCommits.keySet().iterator();
        int commits = 0;
        while (k.hasNext()) {
            String author = k.next();
            commits += authorCommits.get(author).intValue();
            System.out.printf("%d %s\n", authorCommits.get(author).intValue(),
                    author);
        }*/

     //   System.err.println("Total code commits:" + commits);
    }

 /*   protected boolean evaluate(String path, SVNLogEntry entry) {
        
        if (FileTypeMatcher.getFileType(path) == FileTypeMatcher.FileType.SRC) {
            if (authorCommits.containsKey(entry.getAuthor()))
                authorCommits.put(entry.getAuthor(),authorCommits.get(entry.getAuthor()) + 1);
            else
                authorCommits.put(entry.getAuthor(), new Integer(1));

            return true;
        }
        return false;
    }*/
}
