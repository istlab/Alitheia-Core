package eu.sqooss.service.updater;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
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
     * An updater can depend on one or more updaters within the same update
     * stage in order to run. The updater declares those dependencies by
     * providing the mnemonics of the other updaters.
     */
    String[] dependencies() default {};
    
    /**
     * The raw data protocols this updater can handle. Applicable only to
     * IMPORT type updaters.
     */
    String[] protocols() default {};
    
    /**
     * The update stage the updater should be invoked in.
     */
    UpdaterStage stage() default UpdaterStage.DEFAULT;
}
