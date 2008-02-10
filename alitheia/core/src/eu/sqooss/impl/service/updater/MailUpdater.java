/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 Georgios Gousios <gousiosg@aueb.gr>
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

package eu.sqooss.impl.service.updater;

import java.io.FileNotFoundException;
import java.util.List;

import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;

import eu.sqooss.service.db.DAOException;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;

import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.tds.TDAccessor;
import eu.sqooss.service.updater.UpdaterException;

/**
 * Synchronises raw mails with the database
 *
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 */
class MailUpdater extends Job {
    private StoredProject project;
    private AlitheiaCore core;
    private Logger logger;

    public MailUpdater(StoredProject project,
                        AlitheiaCore core,
                        Logger logger) throws UpdaterException {
        if (project == null || core == null || logger == null) {
            throw new UpdaterException("Cannot initialise MailUpdater (path/core/logger is null)");
        }

        this.core = core;
        this.project = project;
        this.logger = logger;
    }

    public int priority() {
        return 0;
    }

    protected void run() {
        try {
            TDAccessor spAccessor = core.getTDSService().getAccessor(project.getId());
            MailAccessor mailAccessor = spAccessor.getMailAccessor();
            List<MailingList> mllist = MailingList.getListsPerProject(project);
            for ( MailingList ml : mllist ) {
                String listId = ml.getListId();
                processList(mailAccessor, listId);
            }
        } catch ( DAOException daoe ) {
            logger.warn(daoe.getMessage());
        }
    }

    protected void processList(MailAccessor mailAccessor, String listId) {
        List<String> messageIds = null;
        try {
            messageIds = mailAccessor.getMessages(listId);
        } catch (FileNotFoundException e) {
            logger.warn("Mailing list <" + listId + "> vanished.");
            return;
        }

        for ( String messageId : messageIds ) {
            try {
                String raw = mailAccessor.getRawMessage( listId, messageId);
                // TODO: parse the message & add it to the database
            } catch (FileNotFoundException e) {
                logger.warn("Message <" + messageId + "> in list <" + listId +
                    "> not found.");
                // Ignore, just carry on
            }
        }
    }

}