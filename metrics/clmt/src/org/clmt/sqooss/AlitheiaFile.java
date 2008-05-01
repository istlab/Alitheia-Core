package org.clmt.sqooss;

import java.io.InputStream;
import java.io.OutputStream;

import org.clmt.io.CLMTFile;
import org.w3c.dom.Document;

public class AlitheiaFile extends CLMTFile {

    public AlitheiaFile(String path) {
	
    }
    
    public boolean delete() {
        return false;
    }

    public boolean exists() {
        return false;
    }

    public String getAbsolutePath() {
        return null;
    }

    public InputStream getInputStream() {
        return null;
    }

    public String getName() {
        return null;
    }

    public OutputStream getOutputStream() {
        return null;
    }

    public boolean isDirectory() {
        return false;
    }

    public boolean isFile() {
        return false;
    }

    public CLMTFile[] listFiles() {
        return null;
    }

    public boolean mkdirs() {
        return false;
    }

    public CLMTFile newFile() {
        return null;
    }

    public CLMTFile newFile(String path) {

        return null;
    }

    public Document getDocument() {
        return null;
    }

    public void setDocument(Document d) {
        
    }

}
