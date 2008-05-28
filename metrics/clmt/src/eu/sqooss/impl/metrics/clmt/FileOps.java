/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Georgios Gousios <gousiosg@gmail.com>
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
import java.util.ArrayList;
import java.util.List;

import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.InMemoryCheckout;
import eu.sqooss.service.fds.InMemoryDirectory;

public class FileOps {
    
    private static final FileOps instance;
    
    private ThreadLocal<InMemoryCheckout> imc = new ThreadLocal<InMemoryCheckout>();
    private ThreadLocal<FDSService> fds = new ThreadLocal<FDSService>();
    
    static {
        instance = new FileOps();
    }
    
    public static FileOps instance() {
        return instance;
    }
    
    public synchronized void setInMemoryCheckout(InMemoryCheckout imc) {
        this.imc.set(imc);
    }
    
    public synchronized void setFDS(FDSService fds) {
        this.fds.set(fds);
    }

    public boolean exists(String path) {
        return imc.get().getRoot().pathExists(path);
    }
    
    public boolean isDirectory(String path) {

        InMemoryDirectory imd = imc.get().getRoot().getSubdirectoryByName(path);
        
        if (imd == null)
            return false;
        return true;
    }
    
    /**
     * List the directories in a directory (non-recursive)
     * 
     * @param path The path of the directory whose contents we want listed
     * @return A list of strings containing the directory names of the files
     * in the directory. The path is not appended. 
     */
    public List<String> getDirectories(String path) {
        
        List<String> files = new ArrayList<String>();
        List<InMemoryDirectory> imdList = 
            imc.get().getRoot().getSubdirectoryByName(path).getSubDirectories();
        
        for(InMemoryDirectory imd : imdList) {
            files.add(imd.getName());
        }
        
        return files;
    }
    
    /**
     * List the files in a directory. 
     * 
     * @return A list of strings containing the file names of the files
     * in the directory. The path is not appended.
     */
    public List<String> listFiles(String path) {
        
        return imc.get().getRoot().getSubdirectoryByName(path).getFileNames();
    }
    
    public synchronized InputStream getFileContents(String path) {
        ProjectFile f = imc.get().getRoot().getFile(path);
        return fds.get().getFileContents(f);
    }
}
