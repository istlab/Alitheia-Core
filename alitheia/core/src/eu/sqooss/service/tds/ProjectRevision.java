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

import java.util.Date;

/**
 * A ProjectRevision denotes a revision of a (any) project; revisions
 * may be created from dates or from SVN revision numbers. A specific
 * ProjectRevision object contains @em only dates or revisions; it is
 * not of itself associated with a specific project. If a ProjectRevision
 * is created with a date it has no SVN revision until it is applied to
 * a specific project; at that point its SVN revision @em may be set
 * by querying the SVN repository. Similarly ProjectRevisions created
 * from a specific revision number have no date until they hit a repository.
 *
 * ProjectRevisions are passed to many functions of the raw accessor
 * classes. It may be invalid to pass certain kinds of revisions
 * to some of those methods (for instance, the email accessors only
 * make sense if there is a date attached to the revision). The
 * InvalidProjectRevisionException is used to indicate problems like that.
 */
public class ProjectRevision {
    public enum Kind {
        INVALID,
        FROM_REVISION,
        FROM_DATE
    }

    /**
     * What kind of source was this project revision created with?
     * That is the definitive source of the data.
     */
    public Kind getKind() {
        return kind;
    }

    public boolean isValid() {
        return kind != Kind.INVALID;
    }

    /**
     * Retrieve the SVN revision that most closely corresponds
     * with this project revision.
     */
    public long getSVNRevision() {
        if (haveRevision) {
            return revision;
        } else {
            return -1;
        }
    }

    /**
     * Set the revision to a specific number. This does not change
     * the kind of the project revision (in particular, an INVALID one
     * will remain INVALID).
     */
    public void setSVNRevision(long r) {
        revision = r;
        haveRevision = true;
    }

    /**
     * Does the project revision have a SVN revision associated?
     */
    public boolean hasSVNRevision() {
        return haveRevision;
    }

    /**
     * Project revisions may be associated with a date; get that date.
     */
    public Date getDate() {
        if (haveDate) {
            return date;
        } else {
            return null;
        }
    }

    /**
     * Set the date for this project revision. Does not change its kind.
     */
    public void setDate(Date d) {
        date = d;
        haveDate = true;
    }

    /**
     * Does the project revision have a date associated?
     */
    public boolean hasDate() {
        return haveDate;
    }

    /**
     * "Iterator" to go back in SVN history. Only works on revisions
     * with an SVN revision number attached.
     *
     * @return the previous revision, or null if there is no revision
     *          already or null if there is no previous revision (e.g.
     *          r.0 has no previous).
     */
    public ProjectRevision prev() {
        if (!hasSVNRevision() || (revision < 1)) {
            return null;
        } else {
            return new ProjectRevision(revision - 1);
        }
    }

    /**
     * "Iterator" to go forward in SVN history. Only works on revisions
     * with an SVN revision number attached.
     *
     * @return the next project revision, or null if the revision does not
     *          have an SVN revision number already.
     */
    public ProjectRevision next() {
        if (!hasSVNRevision()) {
            return null;
        } else {
            return new ProjectRevision(revision + 1);
        }
    }

    /**
     * Default constructor, creating an invalid revision.
     */
    public ProjectRevision() {
        revision = -1;
        date = null;
        haveRevision = false;
        haveDate = false;
        kind = Kind.INVALID;
    }

    /**
     * Create a ProjectRevision from a raw SVN revision number.
     * There is no date associated with this until the project revision
     * is applied to a specific SVN repository.
     */
    public ProjectRevision(long revision) {
        this();
        this.revision = revision;
        haveRevision = true;
        kind = Kind.FROM_REVISION;
    }

    /**
     * Create a ProjectRevision from a date. No revision number
     * is associated with the date until the project revision
     * is applied to a specific SVN repository.
     */
    public ProjectRevision(Date date) {
        this();
        this.date = date;
        haveDate = true;
        kind = Kind.FROM_DATE;
    }

    /**
     * Copy constructor.
     */
    public ProjectRevision(ProjectRevision r) {
        this();
        kind = r.getKind();
        if (r.hasDate()) {
            haveDate = true;
            date = r.getDate();
        }
        if (r.hasSVNRevision()) {
            haveRevision = true;
            revision = r.getSVNRevision();
        }
    }

    /**
     * Return human-readable representation of this ProjectRevision.
     */
    public String toString() {
        switch (kind) {
        case INVALID:
            return "invalid";
        case FROM_REVISION:
            if (hasDate()) {
                return "r." + revision + " (" + date + ")";
            } else {
                return "r." + revision;
            }
        case FROM_DATE:
            if (hasSVNRevision()) {
                return date + " (r." + revision + ")";
            } else {
                return date.toString();
            }
        }
        throw new RuntimeException("Invalid kind of revision: " + kind);
    }

    private long revision;
    private Date date;
    private boolean haveRevision, haveDate;
    private Kind kind;
}

// vi: ai nosi sw=4 ts=4 expandtab

