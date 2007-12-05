package eu.sqooss.service.security;

import java.util.Hashtable;

/**
 * The <code>SecurityManager</code> class is used for validating the access to the SQO-OSS resources and
 * their management.
 */
public interface SecurityManager {

    /**
     * Creates a new user with <code>userName</code> and <code>password</code>.
     * A SecurityUser object represents the new user.
     * @param userName the user name must be unique
     * @param password the user's password - the security manager doesn't store the password in plain text
     * @return The new security user represents with SecurityUser object.
     * @exception IllegalArgumentException - if the user name exists  
     */
    public SecurityUser createUser(String userName, String password);

    /**
     * @param id
     * @return The security user with given id.
     * <code>null</code> if the user with given id doesn't exist. 
     */
    public SecurityUser getUser(long id);

    /**
     * Creates a new security group with <code>description</code>.
     * A SecurityGroup object represents the new group.
     * @param description the description of the security group
     * @return The new security group represents with SecurityGroup object.
     */
    public SecurityGroup createGroup(String description);

    /**
     * @param id
     * @return The security group with given id.
     * <code>null</code> if the group with given id doesn't exist.
     */
    public SecurityGroup getGroup(long id);

    /**
     * Creates a new resource url with <code>resourceURL</code>.
     * A SecurityResourceURL object represents the new resource.
     * @param resourceURL the url of the resource
     * @return The new resource url represents with SecurityResourceURL.
     */
    public SecurityResourceURL createResourceURL(String resourceURL);

    /**
     * @param id
     * @return The resource url with given id.
     * <code>null</code> if the resource url with given id doesn't exist.
     */
    public SecurityResourceURL getReourceURL(long id);

    /**
     * Creates a new privilege with <code>description</code>.
     * A SecurityPrivilege object represents the new privilege.
     * @param description the description of the privilege
     * @return The new privilege represents with SecurityPrivilege object.
     */
    public SecurityPrivilege createPrivilege(String description);

    /**
     * @param id
     * @return The privilege with given id.
     * <code>null</code> if the privilege with given id doesn't exist.
     */
    public SecurityPrivilege getPrivilege(long id);

    /**
     * Creates a new association between <code>group</code>, privilege value (represents with your id) and
     * <code>resourceURL</code>.  
     * @param group
     * @param privilegeValueId
     * @param resourceURL
     */
    public SecurityAuthorizationRule createAuthorizationRule(SecurityGroup group, long privilegeValueId, SecurityResourceURL resourceURL);

    /**
     * @return all used authorization rules
     */
    public SecurityAuthorizationRule[] getAuthorizationRules();

    /**
     * Validate the access to the SQO-OSS resource based on the full URL (with privileges), user name and password.
     * @param fullURL the full URL contains the privileges
     * @param userName
     * @param password
     * @return <code>true</code> if the access is allowed, <code>false</code> otherwise
     */
    public boolean checkPermission(String fullURL, String userName, String password);

    /**
     * Validates the access to the SQO-OSS resource based on the resourceURL, privileges, user name and password. 
     * @param resourceURL
     * @param privileges
     * @param userName
     * @param password
     * @return <code>true</code> if the access is allowed, <code>false</code> otherwise
     */
    public boolean checkPermission(String resourceURL, Hashtable privileges, String userName, String password);

}
