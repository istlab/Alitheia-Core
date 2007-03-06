package eu.sqooss.vcs;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 
 * The factory produces Repository objects which can be used to 
 * access a repository on local disk.
 */
public class RepositoryFactory {
     
    /**
     * Create a Repository object with the given URL to be stored at
     * on local disk at @p localPath.
     */
    public static Repository getRepository(String localPath, String url) 
    throws InvalidRepositoryException {
	// retrieve password from url
    String patternStr = "\\?";
    String[] fields = url.split(patternStr);
    String patternStr2 = "=";
    String[] fields2 = fields[1].split(patternStr2);
    String password = fields2[1];
	URI uri;
	try {
	    uri = new URI(url);
	} catch (URISyntaxException e) {
	   throw new InvalidRepositoryException(e.getMessage());
	}
	RepositoryType type;
	
	if(uri.getScheme().equalsIgnoreCase("svn"))
	    type = RepositoryType.SVN;
	else if(uri.getScheme().equalsIgnoreCase("CVS"))
	    type = RepositoryType.CVS;
	else 
	    throw new InvalidRepositoryException("The repository protocol" + uri.getScheme() + " is not supported");
	 
	return getRepository(localPath, uri.getHost() + uri.getPath(), uri.getUserInfo(), password, type);
    }
    
    public static Repository getRepository(String localPath, String serverPath,
	    String username, String passwd, RepositoryType type) throws InvalidRepositoryException {
	
	if(type == RepositoryType.SVN)
	    return (new SvnRepository(localPath, serverPath, username, passwd));
	else if (type == RepositoryType.CVS)
	    return (new CvsRepository(localPath, serverPath, username, passwd));
	else
	    throw new InvalidRepositoryException("The specified repository protocol is not supported");
    }

}
