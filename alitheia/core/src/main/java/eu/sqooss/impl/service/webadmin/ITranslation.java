package eu.sqooss.impl.service.webadmin;

import java.util.Locale;

/**
 * A translation interface for the Alitheia core webadmin. Every object should present a specific language
 */
public interface ITranslation {
	/**
	 * Retrieves the translation for the specified label key
	 * @param name the name of label key
	 * @return The translation when found OR the specified key when a translation can not be found
	 */
	public String label(String id);
	/**
	 * Retrieves the translation for the specified message key
	 * @param name the name of message key
	 * @return The translation when found OR the specified key when a translation can not be found
	 */
	public String message(String id);
	/**
	 * Retrieves the translation for the specified error key
	 * @param name the name of error key
	 * @return The translation when found OR the specified key when a translation can not be found
	 */
	public String error(String id);

	/**
	 * Get the locale this translation corresponds to
	 */
	public Locale getLocale();
}
