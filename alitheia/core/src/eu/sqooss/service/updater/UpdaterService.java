/*
 *  This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Georgios Gousios <gousiosg@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package eu.sqooss.service.updater;

import eu.sqooss.service.db.StoredProject;

/**
 * The updater service is the gateway in Alitheia to tell the system
 * that the raw data available to the system has changed; usually this
 * means new source code revisions, new email messages or new bug reports.
 * The updater offers an HTTP interface to prompt the system. The URL
 * supported by the updater service lives underneath the web administration
 * site (which is localhost:8088 in the default Alitheia installation)
 * as /updater. Which data is updated depends on the value of the GET
 * parameter target; the project name is passed as GET parameter project.
 * The acceptable values of target are taken from the UpdateTarget enum.
 * Sample updater URLs are:
 *
 *  http://localhost:8088/updater?target=ALL&project=kde
 *  http://localhost:8088/updater?target=code&project=postgres
 *
 * Note that target values are not case-sensitive (but they must match the
 * enum names exactly).
 *
 * The rest of the interface contains implementation parts which typically
 * won't be called from code; it would be unusual for an internal part
 * of the system to call the updater, as it is intended as a way to poke
 * the system from the outside.
 */
public interface UpdaterService {

    /**
     * Targets for an update request. These names are used in the updater
     * URLs (case-insensitive) and in the rest of the system code.
     *
     */
    public enum UpdateTarget {
        /** Request to update source code metadata */
        CODE,
        /** Request to update mailing list metadata */
        MAIL,
        /** Request to update bug metadata */
        BUGS,
        /** Meta-target to update all metadata */
        ALL, 
        /** Request to update Developer alias mappings */
        DEVS,
        /** Request to update Ohloh metadata */
        OHLOH;
        
        public static String[] toStringArray() {
            String[] targets = new String[4];
            targets[0] = "CODE";
            targets[1] = "MAIL";
            targets[2] = "BUGS";
            targets[3] = "ALL";
            targets[4] = "DEVS";
            targets[5] = "OHLOH";
            return targets;
        }
    }

    /**
     * Inform the updater service that project data has changed. The
     * given project is queried for the new data; which new data is
     * queried is controlled by the target parameter. There is a
     * one-to-one mapping of /updater?target=foo&project=bar and a
     * method call update(bar,UpdateTarget.FOO).
     *
     * @param project   The project name that has been updated
     * @param target    Specifies which project resource has been updated

     *
     * @return false if basic validation of the request failed
     * @return true if the update jobs were started successfully -- this
     *          does not mean that the jobs themselves were successful,
     *          as they run asynchronously.
     */
    boolean update(StoredProject project, UpdateTarget target);

    /**
     * Checks if an update is running for the specified project
     * on the given resource target.
     * 
     * @param project the project DAO
     * @param target the resource target
     * 
     * @return <code>true</code>, if an update is currently running,
     *   or <code>false</code> otherwise.
     */
    boolean isUpdateRunning (StoredProject project, UpdateTarget target);
}
