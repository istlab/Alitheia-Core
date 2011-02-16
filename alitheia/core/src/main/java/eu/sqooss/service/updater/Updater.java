package eu.sqooss.service.updater;

import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

public @interface Updater {

    /**
     * Short description of the updater, for system use.
     */
    String mnem();
    
    /**
     * Human friendly updater description
     */
    String descr() default "";
    
    /**
     * A list of updaters this updater depends on. Alitheia Core will use
     * this information to invoke the updaters in the correct order. The
     * dependencies are declared by means of updater mnemonics.
     */
    String[] dependencies() default {};
    
    /**
     * The raw data protocols this updater can handle. Applicable only to
     * IMPORT type updaters.
     */
    String[] protocols() default {};
    
    /**
     * The stage this updater should be invoked in
     */
    UpdaterStage stage() default UpdaterStage.IMPORT;
}
