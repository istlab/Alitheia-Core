package eu.sqooss.services.webui;

public interface WebUI {
  /**
   * Sets the service property indicated by the specified key.
   * @param key the name of the service property
   * @param value the value of the service property
   */
  public void setConfigurationProperty(String key, String value);

  /**
   * Gets the service property indicated by the key.
   * @param key the name of the service property
   * @return the value of the service property; <code>null</code> if there is no
 property with that key.
   */
  public String getConfigurationProperty(String key);

  /**
   * Returns a string array of the configuration keys.
   * @return the configuration keys
   */
  public String[] getConfigurationKeys();
}
