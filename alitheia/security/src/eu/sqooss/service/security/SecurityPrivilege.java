package eu.sqooss.service.security;

public interface SecurityPrivilege {
  
  /**
   * @return the id of the privilege
   */
  public long getId();
  
  /**
   * @return the description of the group
   */
  public String getDescription();
  
  /**
   * Sets a new privilege description.
   * @param description the new description
   */
  public void setDescription(String description);
  
  /**
   * Sets a new privilege values. The old values are removed.
   * @param values the new privilege values
   */
  public void setValues(String[] values);
  
  /**
   * @return the privilege values
   */
  public String[] getValues();
  
  /**
   * Adds a new privilege value.
   * @param value the new privilege value
   * @return the id of the new privilege value  
   */
  public long addValue(String value);
  
  /**
   * Removes the privilege value.
   * @param value
   * @return <code>true</code> if the privilege exists and is removed successfully,
   * <code>false</code> otherwise
   */
  public boolean removeValue(String value);
  
  /**
   * Removes the privilege value with given id.
   * @param id
   * @return <code>true</code> if the privilege exists and is removed successfully,
   * <code>false</code> otherwise
   */
  public boolean removeValue(long id);
  
  /**
   * @param value
   * @return the id of the privilege value
   */
  public long getValueId(String value);
  
  /**
   * Removes the privilege.
   */
  public void remove();
  
}
