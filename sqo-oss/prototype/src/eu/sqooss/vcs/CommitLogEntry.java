/*$Id: */
package eu.sqooss.vcs;

import java.util.*;

/**
 * Represents a log entry containing comments, author and date/time
 * information
 *
 * If one of the fields of a commit log entry could be used as a
 * unique ID (the date is not a safe option, perhaps a
 * hash/combination of author and date could do) then the CommitLog
 * could hold a list of entries sorted by this ID to get a
 * chronologically ordered list.
 */
public class CommitLogEntry {

    /**
     * Information about the author who performed a commit
     */
    public String Author;

    /**
     * The comment logged by a commiter
     */
    public String Comment;

    /**
     * The date and time when the commit was performed
     */
    public Date Date;

    /**
     * The identifier of the revision / commit
     */
    public String Revision;

    /**
     * Constructs a new instance of the class
     * @param author
     * @param comment
     * @param date
     * @param revision
     */
    public CommitLogEntry(String author, String comment, Date date,
	    String revision) {
	Author = author;
	Comment = comment;
	Date = date;
	Revision = (revision == null) ? "" : revision;
    }
}
