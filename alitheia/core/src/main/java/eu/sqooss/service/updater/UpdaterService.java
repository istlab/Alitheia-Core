/*
 * This file is part of the Alitheia system, developed by the SQO-OSS consortium
 * as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,
 * Athens, Greece.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package eu.sqooss.service.updater;

import java.util.Set;

import eu.sqooss.core.AlitheiaCoreService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

/**
 * The updater service is the gateway in Alitheia core to tell the system that
 * the raw data available to the system has changed; usually this means new
 * source code revisions, new email messages. Updating the metadata is a
 * multi-stage process. Each stage can contain several updaters, each one of
 * which can have dependencies to other updaters within the same stage. The
 * update process goes through the stages in the following order:
 * 
 * <ol>
 *  <li>{@link UpdaterStage.IMPORT} </li>
 *  <li>{@link UpdaterStage.PARSE} </li>
 *  <li>{@link UpdaterStage.INFERENCE} </li>
 * </ol>
 * 
 * At each stage, the updater uses the dependency information provided by the
 * updaters to order the update operations correctly.
 * 
 * The updater service acts as a registry of available updaters and knows what
 * updater to call based on the underlying metadata. The service is registration
 * based, so interested plug-ins must register {@link MetadataUpdater}
 * implementations for a set of protocols that the updater is capable of
 * processing. The protocol descriptions must match those exported to the
 * {@link TDSService}.
 * 
 */
public interface UpdaterService extends AlitheiaCoreService {

    /**
     * The Updater stages supported by Alitheia Core.
     */
    public enum UpdaterStage {
        /**
         * Default updater stage for all updaters. Means that the updater can
         * run at any time during the update process.
         */
        DEFAULT,

        /** Raw data to metadata (DB) stage */
        IMPORT,

        /** Source code parsing stage */
        PARSE,

        /** Metadata relationship inference stage */
        INFERENCE;
    }

    /**
     * Register a new metadata updater. If a plug-in supports more that one
     * updaters, then this method should be called for each one of the updaters
     * implemented.
     */
    void registerUpdaterService(Class<? extends MetadataUpdater> clazz);

    /**
     * Unregister an updater class.
     * 
     * @param clazz The updater to unregister
     */
    void unregisterUpdaterService(Class<? extends MetadataUpdater> clazz);

    /**
     * Run all updaters that can process data from the provided StoredProject.
     * 
     * @param project
     *            The project name that has been updated
     * 
     * @return true if the update jobs were scheduled successfully, false
     *         otherwise     */
    boolean update(StoredProject project);

    /**
     * Inform the updater service that project data has changed. The given
     * project is queried for the new data; which new data is queried is
     * controlled by the target parameter.
     * 
     * @return true if the update jobs were scheduled successfully, false
     *         otherwise
     */
    boolean update(StoredProject project, UpdaterStage stage);

    /**
     * Run a specific updater (and its dependencies within the same
     * {@link UpdaterStage}, if applicable)
     * 
     * @return true if the update jobs were scheduled successfully, false
     *         otherwise
     */
    boolean update(StoredProject sp, Updater u);

    /**
     * Run a specific updater by its mnemonic (and its dependencies within the
     * same {@link UpdaterStage}, if applicable)
     * 
     * @return true if the update jobs were scheduled successfully, false
     *         otherwise
     */
    boolean update(StoredProject sp, String updater);

    /**
     * Get a list of all updaters that can be applied to the specified project.
     */
    Set<Updater> getUpdaters(StoredProject sp);

    /**
     * Get a list of all updaters that can be applied to the specified project.
     */
    Set<Updater> getUpdaters(StoredProject sp, UpdaterStage st);
}
