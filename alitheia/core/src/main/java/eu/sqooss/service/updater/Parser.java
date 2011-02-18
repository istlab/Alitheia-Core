package eu.sqooss.service.updater;

import eu.sqooss.service.db.Language;

/**
 * Annotation to mark updaters that can parse source code.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public @interface Parser {

    /**
     * The languages this parser can process
     */
    Language[] languages();
}
