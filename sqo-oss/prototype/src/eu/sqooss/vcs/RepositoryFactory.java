/*$Id: */
package eu.sqooss.vcs;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 
 * @author $Author:$
 *
 */
public class RepositoryFactory {
     
    public static Repository getRepository(String localPath, String url) 
    throws InvalidRepositoryException {
	
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
	 
	//TODO: Next line has errors
	return getRepository(localPath,uri.getHost() + uri.getPath(), uri.getUserInfo(),"4",type);
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
