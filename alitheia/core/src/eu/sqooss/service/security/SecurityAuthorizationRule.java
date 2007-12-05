package eu.sqooss.service.security;

public interface SecurityAuthorizationRule {

    /**
     * @return the url from the authorization rule
     */
    public SecurityResourceURL getUrl();

    /**
     * @return the group from the authorization rule
     */
    public SecurityGroup getGroup();

    /**
     * @return the privilege value id from the authorization rule
     */
    public long getPrivilegeValueId();

    /**
     * Removes the association between  group, privilege value and resource URL.
     */
    public void remove();
}
