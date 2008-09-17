/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 Athens University of Economics and Business
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

package eu.sqooss.impl.metrics.clmt;

import java.io.InputStream;
import java.util.List;

import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.fds.FDSService;

public class FileOps {
    
    private static final FileOps instance;
    
    private ThreadLocal<FDSService> fds = new ThreadLocal<FDSService>();
    private ThreadLocal<List<ProjectFile>> pfl = new ThreadLocal<List<ProjectFile>>();
    static {
        instance = new FileOps();
    }
    
    public static FileOps instance() {
        return instance;
    }
    
    public synchronized void setProjectFiles(List<ProjectFile> pfl) {
        this.pfl.set(pfl);
    }
    
    public synchronized void setFDS(FDSService fds) {
        this.fds.set(fds);
    }

    private ProjectFile getFileForPath(String path) {
        for (ProjectFile pf : pfl.get()) {
            if (pf.getFileName().equals(path)) {
                return pf;
            }
        }
        return null;
    }
    
    public boolean exists(String path) {
        //System.err.println("CLMT.FileOps.exists:" + path);
        if (getFileForPath(path) != null)
                return true;
        
        return false;
    }
    
    public boolean isDirectory(String path) {
        System.err.println("CLMT.FileOps.isDirectory:" + path);
        
        ProjectFile pf = getFileForPath(path);
        
        if ((pf != null) && pf.getIsDirectory() )
            return true;
        
        return false;
    }
    
    /**
     * List the directories in a directory (non-recursive)
     * 
     * @param path The path of the directory whose contents we want listed
     * @return A list of strings containing the directory names of the files
     * in the directory. The path is not appended. 
     */
    public List<String> getDirectories(String path) {
        System.err.println("CLMT.FileOps.getDirectories:" + path);
        
        return null;
    }
    
    /**
     * List the files in a directory. 
     * 
     * @return A list of strings containing the file names of the files
     * in the directory. The path is not appended.
     */
    public List<String> listFiles(String path) {
        System.err.println("CLMT.FileOps.listFiles:" + path);
        return null;
    }
    
    public synchronized InputStream getFileContents(String path) {
        //System.err.println("CLMT.FileOps.getFileContents" + ":" + path);
        ProjectFile pf = getFileForPath(path);
        
        return fds.get().getFileContents(pf);
    }
}
