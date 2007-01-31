package eu.sqooss.vcs;

import java.util.Date;

/**
 * @author circular
 *
 * Represents a software project and acts as a factory for repositories of the 
 * appropriate type
 */
public class Project {

	private ProjectType type;
	private String location;
	private Date lastAccessed;
	
	/**
	 * Creates a new instance of the class
	 */
	public Project(ProjectType type, String location) {
		this.type = type;
		this.location = location;
	}

	public Repository getRepository()
	{
		if(type == ProjectType.CVS)
			return new CvsRepository(location, "");
			
		if (type == ProjectType.SVN)
			return new SvnRepository(location, "");
		
		return null;
	}
}