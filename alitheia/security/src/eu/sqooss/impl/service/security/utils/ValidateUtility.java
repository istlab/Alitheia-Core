package eu.sqooss.impl.service.security.utils;

public class ValidateUtility {
  
  /**
   * Checks the value (represents with the string).
   * 
   * @param value
   * 
   * @exception NullPointerException - if <code>value</code> is <code>null</code>
   * @exception IllegalArgumentException - if <code>value</code> is empty string
   */
  public static void validateValue(String value) {
    if (value == null) {
      throw new NullPointerException();
    } else if ("".equals(value.trim())) {
      throw new IllegalArgumentException("The value is empty!");
    }
  }
  
}
