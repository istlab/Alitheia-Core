/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

package eu.sqooss.impl.service.tds;

import java.util.Date;

import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.Revision;

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
public class SVNProjectRevision implements Revision {

    private long revision;
    private Date date;
    private Status status;
    private Kind kind;
    
    /**
     * Default constructor, creating an invalid revision.
     */
    private SVNProjectRevision() {
        revision = -1;
        date = null;
        status = Status.UNRESOLVED;
    }

    /**
     * Create a ProjectRevision from a raw SVN revision number.
     * There is no date associated with this until the project revision
     * is applied to a specific SVN repository.
     */
    public SVNProjectRevision(long revision) {
        this();
        this.revision = revision;
        kind = Kind.FROM_REVISION;
    }

    /**
     * Create a ProjectRevision from a date. No revision number
     * is associated with the date until the project revision
     * is applied to a specific SVN repository.
     */
    public SVNProjectRevision(Date date) {
        this();
        this.date = date;
        kind = Kind.FROM_DATE;
    }

    /**
     * Copy constructor.
     */
    public SVNProjectRevision(SVNProjectRevision r) {
        this();
        kind = r.getKind();
        status = r.getStatus();
        date = r.getDate();
        revision = r.getSVNRevision();
    }
    
    /**
     * Set the revision to a specific number. This does not change
     * the kind of the project revision 
     */
    public void setSVNRevision(long r) {
        revision = r;
        if (r >= 0 &&
                kind == Kind.FROM_DATE && 
                status == Status.UNRESOLVED) {
            status = Status.RESOLVED;
        }
    }
    
    /**
     * Set the date for this project revision. Does not change its kind.
     */
    public void setDate(Date d) {
        date = d;
        if (d != null && 
                kind == Kind.FROM_REVISION && 
                status == Status.UNRESOLVED) {
            status = Status.RESOLVED;
        }
    }

    public void setResolved(Status s) {
        status = s;
    }
    
    /**
     * Retrieve the SVN revision that most closely corresponds
     * with this project revision.
     */
    public long getSVNRevision() {
        return revision;
    }
    
    /**
     * Does the project revision have a SVN revision associated?
     */
    public boolean hasSVNRevision() {
        return ((kind == Kind.FROM_REVISION) || 
                ((kind == Kind.FROM_DATE) && isResolved()));
    }
    
    /**
     * Does the project revision have a date associated?
     */
    public boolean hasDate() {
        return ((kind == Kind.FROM_DATE) || 
                ((kind == Kind.FROM_REVISION) && isResolved()));
    }

    public boolean isValid() {
        return (status == Status.INVALID);
    }
    
    public boolean isResolved() {
        return (status == Status.RESOLVED);
    }
    
    //Interface methods
    /** {@inheritDoc} */
    public Kind getKind() {
        return kind;
    }
    
    /** {@inheritDoc} */
    public Status getStatus() {
        return status;
    }

    /** {@inheritDoc}} */
    public Date getDate() {
        return date;
    }
    
    /** {@inheritDoc} */
    public String getUniqueId() {
        return String.valueOf(revision);
    }
    
    /** {@inheritDoc} */
    public String toString() {
        if (getStatus() == Status.RESOLVED) {
            return "r." + revision + " (" + date + ")";
        } else if(getStatus() == Status.UNRESOLVED) {
            switch (kind) {
            case FROM_REVISION:
                return "r." + revision;
            case FROM_DATE:
                return date.toString();
            }
        } else {
            return "invalid revision r:" + revision + " date: " + date;
        }
        return null;
    }
    
    /** {@inheritDoc} */
    public int compareTo(Revision o) 
        throws InvalidProjectRevisionException {
        if (!(o instanceof SVNProjectRevision))
            throw new InvalidProjectRevisionException(getUniqueId(), this.getClass());
        
        if (!((SVNProjectRevision)o).isResolved()) {
            throw new InvalidProjectRevisionException("Revision not resoved " 
                    + getUniqueId(), this.getClass());
        }
        
        if (!isResolved()) {
            throw new InvalidProjectRevisionException("Revision not resoved "
                    + getUniqueId(), this.getClass());
        }
        
        return (int) (revision - (((SVNProjectRevision)o).revision)); 
    }    
}

// vi: ai nosi sw=4 ts=4 expandtab

