package eu.sqooss.service.security;

public interface SecurityGroup {

    /**
     * @return the id of the group
     */
    public long getId();

    /**
     * @return the description of the group
     */
    public String getDescription();

    /**
     * Sets a new group description.
     * @param description the new description
     */
    public void setDescription(String description);

    /**
     * Removes the group.
     */
    public void remove();

}
