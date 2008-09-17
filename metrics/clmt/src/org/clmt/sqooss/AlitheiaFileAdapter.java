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

package org.clmt.sqooss;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.clmt.io.CLMTFile;

import eu.sqooss.impl.metrics.clmt.FileOps;

public final class AlitheiaFileAdapter extends CLMTFile {

    private String path;
    InputStream contents;
     
    public AlitheiaFileAdapter(String path) {
        this.path = path;
    }
    
    @Override
    public boolean delete() {   
        Thread.dumpStack();
        return false;
    }

    @Override
    public boolean exists() {
        return FileOps.instance().exists(path);
    }

    @Override
    public String getAbsolutePath() {
        return path;
    }

    @Override
    public InputStream getInputStream() {
        return contents = FileOps.instance().getFileContents(path);
    }

    @Override
    public String getName() {
        Thread.dumpStack();
        return null;
    }

    @Override
    public OutputStream getOutputStream() {
        Thread.dumpStack();
        return null;
    }

    @Override
    public boolean isDirectory() {
        return FileOps.instance().isDirectory(path);
    }

    @Override
    public boolean isFile() {
        return !isDirectory();
    }

    @Override
    public CLMTFile[] listFiles() {
        
        if(!isDirectory()) 
            return null;
        
        ArrayList<CLMTFile> files = new ArrayList<CLMTFile>(); 
        
        List<String> fileList = FileOps.instance().listFiles(path); 
        List<String> dirList = FileOps.instance().getDirectories(path);
        
        for (String s : fileList) {
            files.add(newFile(this.path + "/" +s));
        }
        
        for (String s : dirList) {
            files.add(newFile(this.path + "/" + s));
        }
        
        return files.toArray(new CLMTFile[]{});
    }

    @Override
    public boolean mkdirs() {
        Thread.dumpStack();
        return false;
    }

    @Override
    public CLMTFile newFile(String path) {
        return new AlitheiaFileAdapter(path);
    }

}
