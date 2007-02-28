/*$Id: */
package eu.sqooss;

import java.util.Date;

import eu.sqooss.vcs.InvalidRepositoryException;
import eu.sqooss.vcs.Repository;
import eu.sqooss.vcs.RepositoryFactory;
import eu.sqooss.vcs.RepositoryType;

/**
 * @author $Author:$
 *
 * Represents a software project and acts as a factory for repositories of the
 * appropriate type
 */
public class Project {

    
    private Date lastAccessed;

    private String location;

    private RepositoryType type;
    
    /**
     * Creates a new instance of the class
     */
    public Project(RepositoryType type, String location) {
	this.type = type;
	this.location = location;
    }

    public Repository getRepository() {
	try {
	    RepositoryFactory.getRepository("blahblh", "");
	}  catch (InvalidRepositoryException e) {
	    //TODO: Add error handling
	} 

	return null;
    }
}
