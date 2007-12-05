/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 by Adriaan de Groot <groot@kde.org>
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

package eu.sqooss.service.tds;

import eu.sqooss.service.tds.ProjectRevision;

/**
 * This class flags invalid revisions which are passed to
 * methods of the SCMAccessor. Invalid revisions may be
 * of the wrong kind or indicate a revision that is not
 * within the range of the accessor (or other data structures).
 */
public class InvalidProjectRevisionException extends TDSException {
    private static final long serialVersionUID = 1L;
    private ProjectRevision.Kind kind;
    private String projectName;

    /**
     * Constructor. Create an InvalidProjectRevisionException
     * for the indicated @p project. The expected @p kind of
     * revision may be null, meaning "any valid revision".
     */
    public InvalidProjectRevisionException(String project,
        ProjectRevision.Kind k) {
        super("Invalid project revision");
        kind = k;
        projectName = project;
    }

    public String getMessage() {
        if (kind != null) {
            return super.getMessage() + " " + projectName +
                " expected revision kind " + kind;
        } else {
            return super.getMessage() + " " + projectName +
                " expected a valid revision.";
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

