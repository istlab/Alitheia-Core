package org.clmt.sqooss;

import java.io.InputStream;
import java.io.OutputStream;

import org.clmt.io.CLMTFile;

public final class AlitheiaFileAdapter extends CLMTFile {

    private String path;
    
    public AlitheiaFileAdapter(String p) {
        path = p;
    }
    
    @Override
    public boolean delete() {   
        Thread.dumpStack();
        return false;
    }

    @Override
    public boolean exists() {
        Thread.dumpStack();
        return false;
    }

    @Override
    public String getAbsolutePath() {
        Thread.dumpStack();
        return null;
    }

    @Override
    public InputStream getInputStream() {
        Thread.dumpStack();
        return null;
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
        Thread.dumpStack();
        return false;
    }

    @Override
    public boolean isFile() {
        Thread.dumpStack();
        return false;
    }

    @Override
    public CLMTFile[] listFiles() {
        Thread.dumpStack();
        return null;
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
