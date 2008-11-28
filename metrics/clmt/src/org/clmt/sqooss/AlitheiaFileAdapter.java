/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,  *                Athens, Greece.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.clmt.io.CLMTFile;

import eu.sqooss.impl.metrics.clmt.FileOps;

public final class AlitheiaFileAdapter extends CLMTFile {
    private String path;
    private FileOps fileOps;
     
    public AlitheiaFileAdapter(String path) {
        this.path = path;
        this.fileOps = FileOps.getInstance();
    }
    
    @Override
    public boolean delete() {   
        //Thread.dumpStack();
        
        return false;
    }

    @Override
    public boolean exists() {
        return fileOps.exists(path);
    }

    @Override
    public String getAbsolutePath() {
        return path;
    }

    @Override
    public InputStream getInputStream() {
        return fileOps.getFileContents(path);
    }

    @Override
    public String getName() {
        if (!path.equals(""))
            return eu.sqooss.service.util.FileUtils.basename(path);
        else 
            return "";
    }

    @Override
    public OutputStream getOutputStream() {
        File f = new File(this.path);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            return fos;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public boolean isDirectory() {
        return fileOps.isDirectory(path);
    }

    @Override
    public boolean isFile() {
        return !isDirectory();
    }

    @Override
    public CLMTFile[] listFiles() {
        
        if(!isDirectory()) {
            return new CLMTFile[] {};
        }
        
        ArrayList<CLMTFile> files = new ArrayList<CLMTFile>(); 
        
        List<String> fileList = fileOps.listFiles(path); 
        List<String> dirList = fileOps.getDirectories(path);
        
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
        //Thread.dumpStack();
        return false;
    }

    @Override
    public CLMTFile newFile(String path) {
        return new AlitheiaFileAdapter(path);
    }

}
