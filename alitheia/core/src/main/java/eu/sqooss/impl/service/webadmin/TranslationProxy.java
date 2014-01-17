package eu.sqooss.impl.service.webadmin;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This is a class whose sole purpose is to provide a useful API from
 * within Velocity templates for the translation functions offered by
 * the AbstractView. Only one object needs to be created, and it
 * forwards all the label(), message() and error() calls to the translation
 * methods of the view.
 */
public class TranslationProxy {
    private ResourceBundle resLbl = null;
    private ResourceBundle resMsg = null;
    
    private String RES_LABELS_FILE = "ResourceLabels";
    private String RES_MESSAGES_FILE = "ResourceMessages";
    private String NULL_PARAM_NAME = "Undefined parameter name!";
    
	public TranslationProxy(Locale locale) {
        resLbl = ResourceBundle.getBundle(RES_LABELS_FILE, locale);
        resMsg = ResourceBundle.getBundle(RES_MESSAGES_FILE, locale);
    }
    
	/**
     * Retrieves the value of the given resource property from the
     * resource bundle that stores all label strings.
     * 
     * @param name the name of the resource property
     * 
     * @return The property's value, when that property can be found in the
     *   corresponding resource bundle, OR the provided property name's
     *   parameter, when such property is missing.
     */
	public String label(String s) {
		if (resLbl != null) {
			try {
				return resLbl.getString(s);
			}
			catch (NullPointerException ex) {
				return NULL_PARAM_NAME;
			}
			catch (MissingResourceException ex) {
				return s;
			}
		}
		return s;
	}
    
    
	/**
	 * Retrieves the value of the given resource property from the
	 * resource bundle that stores all message strings.
	 * 
	 * @param name the name of the resource property
	 * 
	 * @return The property's value, when that property can be found in the
	 *   corresponding resource bundle, OR the provided property name's
	 *   parameter, when such property is missing.
	 */
	public String getMsg (String name) {
	    if (resMsg != null) {
	        try {
	            return resMsg.getString(name);
	        }
	        catch (NullPointerException ex) {
	            return NULL_PARAM_NAME;
	        }
	        catch (MissingResourceException ex) {
	            return name;
	        }
	    }
	    return name;
	}
}