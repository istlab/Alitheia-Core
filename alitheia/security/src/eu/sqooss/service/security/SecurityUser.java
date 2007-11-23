package eu.sqooss.service.security;

public interface SecurityUser {

    /**
     * @return the id of the user
     */
    public long getId();

    /**
     * @return the user name
     */
    public String getUserName();

    /**
     * Sets a new user name
     * @param userName the user name
     */
    public void setUserName(String userName);

    /**
     * Sets a new password.
     * @param password
     */
    public void setPassword(String password);

    /**
     * Adds the user to the selected group.
     * @param group
     */
    public void addToGroup(SecurityGroup group);

    /**
     * Removes the user from the selected group.
     * @param group
     */
    public void removeFromGroup(SecurityGroup group);

    /**
     * Removes the user.
     */
    public void remove();

    /**
     * @return the user's groups
     */
    public SecurityGroup[] getGroups();

}
