/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Sebastian Kuegler <sebas@kde.org>
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

package eu.sqooss.webui;

import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSResultEntry;


/**
 * This class represents a Result of a Metric that has been applied to a project
 * evaluated by Alitheia.
 *
 * The Result class is part of the high-level webui API.
 */
public class Result extends WebuiItem {

    public static enum ResourceType {
        PROJECT_FILE,
        PROJECT_VERSION;

        public static ResourceType fromString(String scope) {
            if (scope.equals(PROJECT_FILE.toString()))
                return PROJECT_FILE;
            if (scope.equals(PROJECT_VERSION.toString()))
                return PROJECT_VERSION;
            else
                return null;
        }
    };

    private String mnemonic = new String();
    private String mimetype = new String();
    private String data = new String();
    private String activationType = new String();

    /** Represents a ProjectFile activated metric. */
    public static final String PROJECT_FILE = "PROJECT_FILE";

    /** Represents a ProjectVersion activated metric. */
    public static final String PROJECT_VERSION = "PROJECT_VERSION";

    /* 
     * The following list of mimetypes must be kept in sync with the mimetypes
     * definitions from eu.sqooss.service.abstractmetric.ResultEntry
     */

    /** Represents "type/integer" MIME type. */
    public static final String MIME_TYPE_TYPE_INTEGER = "type/integer";

    /** Represents "type/long" MIME type. */
    public static final String MIME_TYPE_TYPE_LONG    = "type/long";

    /** Represents "type/float" MIME type. */
    public static final String MIME_TYPE_TYPE_FLOAT   = "type/float";

    /** Represents "type/double" MIME type. */
    public static final String MIME_TYPE_TYPE_DOUBLE  = "type/double";

    /** Represents "text/plain" MIME type. */
    public static final String MIME_TYPE_TEXT_PLAIN   = "text/plain";

    /** Represents "text/html" MIME type. */
    public static final String MIME_TYPE_TEXT_HTML    = "text/html";

    /** Represents "text/xml" MIME type. */
    public static final String MIME_TYPE_TEXT_XML     = "text/xml";

    /** Represents "text/csv" MIME type. */
    public static final String MIME_TYPE_TEXT_CSV     = "text/csv";

    /** Represents "image/gif" MIME type. */
    public static final String MIME_TYPE_IMAGE_GIF    = "image/gif";

    /** Represents "image/png" MIME type. */
    public static final String MIME_TYPE_IMAGE_PNG    = "image/png";

    /** Represents "image/jpeg" MIME type. */
    public static final String MIME_TYPE_IMAGE_JPEG   = "image/jpeg";

    private static final String[] printable = {
        MIME_TYPE_TEXT_HTML,
        MIME_TYPE_TEXT_PLAIN,
        MIME_TYPE_TEXT_XML,
        MIME_TYPE_TEXT_CSV,
        MIME_TYPE_TYPE_DOUBLE,
        MIME_TYPE_TYPE_FLOAT,
        MIME_TYPE_TYPE_INTEGER,
        MIME_TYPE_TYPE_LONG };

    /**
     * Instantiates a new <code>Result</code>.
     */
    public Result () {
    }

    /**
     * Instantiates a new <code>Result</code> and initializes it with the 
     * information provided from the given <code>WSResultEntry</code>
     * instance.
     */
    public Result (WSResultEntry resultentry, Terrier t) {
        if (resultentry != null) {
            id          = resultentry.getDaoId();
            mnemonic    = resultentry.getMnemonic();
            mimetype    = resultentry.getMimeType();
            data        = resultentry.getResult();
            activationType = PROJECT_VERSION; //FIXME: read from ResultEntry
        }
        terrier = t;
    }

    public Long getId() {
        return id;
    }

    public String getString() {
        return data;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public String getMimeType() {
        return mimetype;
    }

    /** This method makes it easy to check if we can just dump the content
     * of the result to screen, or if we need to go through an extra HTML
     * request in order to send it as correct mimetype. (embedded image
     * data in HTML streams doesn't work ;-)
     */
    public Boolean getIsPrintable() {
        // Crudely search the array for the mimetype we have
        for (String t : printable) {
            if (t.equals(mimetype)) {
                return true;
            }
        }
        return false;
    }

    /**Returns an HTML string which is a link to a page displaying the result.
     */
    public String getLink() {
        return "<a href=\"/results.jsp?rid=" + getId() + "\">View Me</a>";
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.WebuiItem#getHtml(long)
     */
    public String getHtml (long in) {
        if (getIsPrintable())
            return mnemonic + ": " + getString();
        else
            return "<a href=\"renderresult.jsp?id=" + getId() + "\">Render</a>";
    }

    /**
     * Verifies if this object is equal to the given <code>Result</code>
     * object.
     * 
     * @param target the target <code>Result</code> object
     * 
     * @return <code>true<code>, if this <code>Result</code> is equal to the
     *   given <code>Result</code> object, or <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object target) {
        if (this == target) return true;
        if (target == null) return false;
        if (getClass() != target.getClass()) return false;
        Result result = (Result) target;
        if (getMnemonic().equals(result.getMnemonic()))
            if (getId().equals(result.getId()))
                if (getMimeType().equals(result.getMimeType()))
                    if (getString().equals(result.getString()))
                        return true;
        return false;
    }
}