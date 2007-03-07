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

        URI uri;
        RepositoryType type;
        String password, username, protocol, serverPath = null;
        //String protocol = null;
        // retrieve password from url (if there is one)
        if (url.indexOf("//?") == -1){
            /* no username or password given */
            password = "";
        } else {
            String patternStr = "\\?";
            String[] fields = url.split(patternStr);
            String patternStr2 = "=";
            String[] fields2 = fields[1].split(patternStr2);
            password = fields2[1];
        }
        //check the protocol
        if (url.indexOf("http://") != -1) {
            url = url.replaceAll("http://", "");
            uri = returnURI(url);
            protocol = "http://";
        } else if (url.indexOf("https://") != -1) {
            url = url.replaceAll("https://", "");
            uri = returnURI(url);
            protocol = "https://";
        } else if (url.indexOf("svn://") != -1) {
            url = url.replaceAll("svn://", "");
            uri = returnURI(url);
            protocol = "svn://";
        } else if (url.indexOf("svn+ssh://") != -1) {
            url = url.replaceAll("svn+ssh://", "");
            uri = returnURI(url);
            protocol = "svn+ssh://";
        } else if (url.indexOf("jsjs://") != -1) {
            url = url.replaceAll("jsjs://", "");
            uri = returnURI(url);
            protocol = "jsjs://";
        } else {
            //no protocol so we assume that it is http");
            protocol = "http://";
            uri = returnURI(url);
        }

        if (uri.getUserInfo() != null) {
            /* get the username, if there is one */
            username = uri.getUserInfo();
        } else {
            username = "";
        }
        //check if there is a port given
        if (uri.getPort() == -1) {
            serverPath = protocol + uri.getHost() + uri.getPath();
        } else {
            String port = new Integer(uri.getPort()).toString();
            serverPath = protocol + uri.getHost() + uri.getPath() + ":" + port;
        }
        //check repository type
        if(uri.getScheme().equalsIgnoreCase("svn"))
            type = RepositoryType.SVN;
        else if(uri.getScheme().equalsIgnoreCase("CVS"))
            type = RepositoryType.CVS;
        else 
            throw new InvalidRepositoryException("The repository protocol" + uri.getScheme() + " is not supported");

        return getRepository(localPath, serverPath, username, password, type);
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

    public static URI returnURI (String url) throws InvalidRepositoryException{
        if (url == null){
            URI uri;
            try {
                uri = new URI(url);
            } catch (URISyntaxException e) {
                throw new InvalidRepositoryException(e.getMessage());
            }
            return uri;
        } else{
            return null;
        }	
    }
}
