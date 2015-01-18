package eu.sqooss.impl.service.webadmin;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.logging.Logger;

public class Localization {
    // Some constants that are used internally
    private static final String NULL_PARAM_NAME = "Undefined parameter name!";
    private static Logger logger;
    
    // Names of the various resource files
    private static String RES_LABELS_FILE = "ResourceLabels";
    private static String RES_ERRORS_FILE = "ResourceErrors";
    private static String RES_MESSAGES_FILE = "ResourceMessages";
    
    // Resource bundles
    private static ResourceBundle resLbl;
    private static ResourceBundle resErr;
    private static ResourceBundle resMsg;

    /**
     * Initializes the various resource bundle with the specified locale.
     *
     * @param locale
     *            the user's locale
     */
    public static void initResources(Locale locale) {
        logger = AlitheiaCore.getInstance().getLogManager().createLogger(Logger.NAME_SQOOSS_WEBADMIN);
        resLbl = getLabelsBundle(locale);
        resErr = getErrorsBundle(locale);
        resMsg = getMessagesBundle(locale);
    }

    /**
     * Retrieves the value of the given resource property from the resource
     * bundle that stores all label strings.
     *
     * @param name
     *            the name of the resource property
     *
     * @return The property's value, when that property can be found in the
     *         corresponding resource bundle, OR the provided property name's
     *         parameter, when such property is missing.
     */
    public static String getLbl(String name) {
        if (resLbl != null) {
            try {
                return resLbl.getString(name);
            } catch (NullPointerException ex) {
                return NULL_PARAM_NAME;
            } catch (MissingResourceException ex) {
                return name;
            }
        }
        return name;
    }

    /**
     * Retrieves the value of the given resource property from the resource
     * bundle that stores all error strings.
     *
     * @param name
     *            the name of the resource property
     *
     * @return The property's value, when that property can be found in the
     *         corresponding resource bundle, OR the provided property name's
     *         parameter, when such property is missing.
     */
    public static String getErr(String name) {
        if (resErr != null) {
            try {
                return resErr.getString(name);
            } catch (NullPointerException ex) {
                return NULL_PARAM_NAME;
            } catch (MissingResourceException ex) {
                return name;
            }
        }
        return name;
    }

    /**
     * Retrieves the value of the given resource property from the resource
     * bundle that stores all message strings.
     *
     * @param name
     *            the name of the resource property
     *
     * @return The property's value, when that property can be found in the
     *         corresponding resource bundle, OR the provided property name's
     *         parameter, when such property is missing.
     */
    public static String getMsg(String name) {
        if (resMsg != null) {
            try {
                return resMsg.getString(name);
            } catch (NullPointerException ex) {
                return NULL_PARAM_NAME;
            } catch (MissingResourceException ex) {
                return name;
            }
        }
        return name;
    }

    /** The default locale. */
    private static Locale defaultLocale = Locale.ENGLISH;

    /**
     * Gets the resource bundle for a given name and locale.
     *
     * @param baseName the base name
     * @param locale the locale
     * @return the resource bundle
     */
    private static ResourceBundle getResourceBundle(String baseName, Locale locale) {
        if (null == locale) {
            locale = defaultLocale;
        }
        ResourceBundle result = null;
        try {
            result = ResourceBundle.getBundle(baseName, locale);
        } catch (MissingResourceException e) {
            result = ResourceBundle.getBundle(baseName, defaultLocale);
        } catch (NullPointerException e) {
            logger.error("Invalid arguments for getResourceBundle("+ baseName +", "+ locale +")");
            result = null;
        }
        return result;
    }

    // TODO: Move this method's logic in the initResources() once all views
    // are using the new methods.
    /**
     * Gets the labels bundle.
     *
     * @param locale the locale
     * @return the labels bundle
     */
    public static ResourceBundle getLabelsBundle(Locale locale) {
        return getResourceBundle(RES_LABELS_FILE, locale);
    }

    // TODO: Move this method's logic in the initResources() once all views
    // are using the new methods.
    /**
     * Gets the errors bundle.
     *
     * @param locale the locale
     * @return the errors bundle
     */
    public static ResourceBundle getErrorsBundle(Locale locale) {
        return getResourceBundle(RES_ERRORS_FILE, locale);
    }

    // TODO: Move this method's logic in the initResources() once all views
    // are using the new methods.
    /**
     * Gets the messages bundle.
     *
     * @param locale the locale
     * @return the messages bundle
     */
    public static ResourceBundle getMessagesBundle(Locale locale) {
        return getResourceBundle(RES_MESSAGES_FILE, locale);
    }
}
