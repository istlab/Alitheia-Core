package eu.sqooss.impl.service.webadmin;

import java.util.*;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.logging.Logger;

/**
 * Implements a translation for the webadmin interface
 */
public enum Translation implements ITranslation {
	EN(Locale.ENGLISH),
	DE(Locale.GERMAN);
	// Add more locales through Locale.CONSTANT or new Locale("ISO 639 country code");

	// Names of the various resource files
	private static final String RES_DIR			  = "/translation/webadmin/";
	private static final String RES_LABELS_FILE   = RES_DIR+"labels";
	private static final String RES_ERRORS_FILE   = RES_DIR+"errors";
	private static final String RES_MESSAGES_FILE = RES_DIR+"messages";

	// Resource bundles
	private ResourceBundle resLbl;
	private ResourceBundle resMsg;
	private ResourceBundle resErr;

	private Locale locale;

	private Logger logger;

	Translation(Locale locale) {
		this.locale = locale;

		try{
			logger = AlitheiaCore.getInstance().getLogManager().createLogger(Logger.NAME_SQOOSS_WEBADMIN);
		}
		catch(Exception e2){
			// We can't even get a logger...
			// Silently fail, errors will become apparent because of missing translations
		}

		try {
			resLbl = ResourceBundle.getBundle(RES_LABELS_FILE,locale);
		}
		catch(MissingResourceException e) {
			if(logger != null) { logger.warn("Missing resource " + RES_LABELS_FILE + " for locale " + locale); }
		}
		try {
			resMsg = ResourceBundle.getBundle(RES_MESSAGES_FILE,locale);
		}
		catch(MissingResourceException e) {
			if(logger != null) { logger.warn("Missing resource " + RES_MESSAGES_FILE + " for locale " + locale); }
		}
		try {
			resErr = ResourceBundle.getBundle(RES_ERRORS_FILE,locale);
		}
		catch(MissingResourceException e) {
			if(logger != null) { logger.warn("Missing resource " + RES_MESSAGES_FILE + " for locale " + locale); }
		}
	}

	@Override
	public String label(String id) {
		return getTranslation(resLbl, id);
	}

	@Override
	public String message(String id) {
		return getTranslation(resMsg, id);
	}

	@Override
	public String error(String id) {
		return getTranslation(resErr, id);
	}

	private String getTranslation(ResourceBundle rb, String id) {
		try {
			return rb.getString(id);
		}
		catch (NullPointerException | MissingResourceException ex) {
			return id;
		}
	}

	@Override
	public Locale getLocale() {
		return locale;
	}
}
