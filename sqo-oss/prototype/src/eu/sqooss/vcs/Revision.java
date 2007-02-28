/*$Id: */
package eu.sqooss.vcs;

import java.util.*;

public class Revision {

    public String description;

    // TODO: Change this with a custom collection
    private Vector<FileEntry> files;

    public int number;

    public Revision(int number) {
	if (number < 0) {
	    throw new IllegalArgumentException();
	}

	this.number = number;
	description = Integer.toString(number);
	files = new Vector<FileEntry>();
    }

    public Revision(String description) {
	if (description == null) {
	    throw new IllegalArgumentException();
	}

	description = description;

	// TODO: parse revision number from description if possible
	files = new Vector<FileEntry>();
    }

    public List<FileEntry> getFiles() {
	return files;
    }
}
