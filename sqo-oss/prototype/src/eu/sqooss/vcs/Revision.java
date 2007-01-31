package eu.sqooss.vcs;

import java.util.*;

public class Revision {
	public int Number;
	public String Description;
	//TODO: Change this with a custom collection
	private Vector<FileEntry> files;

	public List<FileEntry> getFiles() {
		return files;
	}

	/// <summary>
	/// 
	/// </summary>
	/// <param name="number">0 for HEAD (latest)</param>
	public Revision(int number)
	{
		if (number < 0)
		{
			throw new IllegalArgumentException();
		}
		Number = number;
		Description = Integer.toString(number);
		files = new Vector<FileEntry>();
	}

	public Revision(String description)
	{
		if (description == null)
		{
			throw new IllegalArgumentException();
		}
		Description = description;
		//TODO: parse revision number from description if possible
		files = new Vector<FileEntry>();
	}
	
}
