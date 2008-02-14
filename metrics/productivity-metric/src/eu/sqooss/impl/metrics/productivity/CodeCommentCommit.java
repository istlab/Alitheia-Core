/* This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 Georgios Gousios <gousiosg@gmail.com>
 * 
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
package eu.sqooss.impl.metrics.productivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.tds.CommitEntry;
import eu.sqooss.service.tds.Diff;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;

/**
 * Code comment commit - A commit on a src file that also includes comments
 */
public class CodeCommentCommit extends CodeCommitsJob {

    private Pattern comment1 = Pattern.compile("^\\s*#.*$");

    private Pattern comment2 = Pattern.compile("^\\s*\\/\\/.*$");

    protected CodeCommentCommit(AbstractMetric owner, ProjectVersion a,
            ProjectVersion b) {
        super(owner, a, b);
    }

    protected boolean evaluate(String path, CommitEntry entry) {

        /*
        Diff d = null;
        try {
            d = svn.getDiff(path, entry.getRevision(), entry.getRevision());
        } catch (InvalidProjectRevisionException e) {
            log.error("Invalid Project Revision: " + e.getMessage());
        } catch (InvalidRepositoryException e) {
            log.error("Invalid Repository Exception: " + e.getMessage());
        } catch (FileNotFoundException e) {
            log.error("File Not Found Exception: " + e.getMessage());
        }

        try {
            File f = d.getDiffFile();
            java.io.FileInputStream fis = new FileInputStream(f);
            CharBuffer buffer = fis.getChannel().map(MapMode.READ_ONLY, 0,
                    f.length()).asCharBuffer();

            Matcher m = comment1.matcher(buffer);
            if (m.matches())
                return true;

            m = comment2.matcher(buffer);
            if (m.matches())
                return true;

        } catch (FileNotFoundException e) {
            log.error("Diff file not found:" + e.getMessage());
        } catch (IOException e) {
            log.error("Error mapping diff file to memory:" + e.getMessage());
        }
        */
        return false;

    }
}

//vi: ai nosi sw=4 ts=4 expandtab
