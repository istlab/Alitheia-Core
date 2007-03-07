/*$Id: */
package eu.sqooss.vcs;

import java.util.*;

public class Revision {

    private String description;
    private long number;
    // TODO: Change this with a custom collection
    private Vector<FileEntry> files;

    public Revision(long number) {
        if (number < 0) {
            throw new IllegalArgumentException();
        }

        this.number = number;
        description = String.valueOf(number);
        files = new Vector<FileEntry>();
    }

    public Revision(String description) {
        if (description == null) {
            throw new IllegalArgumentException();
        }

        this.description = description;
        this.number = 0;
        // TODO: parse revision number from description if possible
        files = new Vector<FileEntry>();
    }

    public List<FileEntry> getFiles() {
        return files;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        if(description != "") {
            return description;
        }
        else if (number > 0) {
            return String.valueOf(number);
        }
        else {
            return "";
        }
    }

    /**
     * @return the number
     */
    public long getNumber() {
        return number;
    }
}
