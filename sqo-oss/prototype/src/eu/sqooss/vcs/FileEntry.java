/*$Id: */
package eu.sqooss.vcs;

import java.util.*;

public abstract class FileEntry {

    private HashMap<String, String> attributes;

    private String fullPath;

    EntryKind kind;

    private String name;

    String revision;

    int size;

    public enum EntryKind {
	Unknown, File, Dir
    }

    public enum InputDataFormat {
	Plain, Svn, SvnXml, SvnLogXml, Cvs
    }

    public FileEntry(String name) {
	this.name = name;
	fullPath = "";
	attributes = new HashMap<String, String>();
	kind = EntryKind.Unknown;
    }

    public String getName() {
	return name;
    }

    void setName(String name) {
	this.name = name;
    }

    public String getFullPath() {
	return fullPath;
    }

    void setFullPath(String fullPath) {
	this.fullPath = fullPath;
    }

    public HashMap<String, String> getAttributes() {
	return attributes;
    }

    public int getSize() {
	return size;
    }

    void setSize(int size) {
	this.size = size;
    }

    public String getRevision() {
	return revision;
    }

    void setRevision(String revision) {
	this.revision = revision;
    }

    public EntryKind getKind() {
	return kind;
    }

    void setKind(EntryKind kind) {
	this.kind = kind;
    }

    public static EntryKind parseEntryKind(String kind) {
	String k = kind.toLowerCase();

	if (k == "file") {
	    return EntryKind.File;
	}

	if (k == "dir") {
	    return EntryKind.Dir;
	}

	return EntryKind.Unknown;
    }
}
