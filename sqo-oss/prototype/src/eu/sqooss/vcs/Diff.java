package eu.sqooss.vcs;

import java.util.*;

public class Diff implements Iterable<FileEntry> {

	/**
	 * hold a list of files that changed between the two revisions, 
	 * not the file differences
	 */
	private HashMap<String, FileEntry> changedFiles;

	public Diff()
	{
		changedFiles = new HashMap<String, FileEntry>();
	}

	void add(FileEntry item)
	{
		changedFiles.put(item.getName(), item);
	}

	void clear()
	{
		changedFiles.clear();
	}

	void remove(FileEntry item)
	{
		changedFiles.remove(item.getName());
	}

	public FileEntry getItem(String name) {
		return changedFiles.get(name);
	}

	public Iterator<FileEntry> iterator() {
		return changedFiles.values().iterator();
	}
}
