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

/**
 * This class represents a Metric that has been applied to a project
 * evaluated by Alitheia.
 * It currently only provides access to metric metadata.
 *
 * The Metric class is part of the high-level webui API.
 */
public class Metric extends WebuiItem {

    /** Metric meta-data */
    private String mnemonic;
    private MetricType type;
    private String description;
    private MetricActivator activator;

    /**
     * This enumeration defines all metric types that are supported by the
     * SQO-OSS framework.
     * <br/>
     * <i>Note: Synchronize this enumeration with the SQO-OSS types set when
     * necessary.</i>
     */
    public enum MetricType {
        SOURCE_CODE,
        SOURCE_FOLDER,
        MAILING_LIST,
        BUG_DATABASE,
        PROJECT_WIDE;

        public static MetricType fromString(String type) {
            if (type.equals(SOURCE_CODE.toString()))
                return SOURCE_CODE;
            if (type.equals(SOURCE_FOLDER.toString()))
                return SOURCE_FOLDER;
            else if (type.equals(MAILING_LIST.toString()))
                return MAILING_LIST;
            else if (type.equals(BUG_DATABASE.toString()))
                return BUG_DATABASE;
            else if (type.equals(PROJECT_WIDE.toString()))
                return PROJECT_WIDE;
            else
                return null;
        }
    };

    /**
     * This enumeration defines all activation types that are supported by the
     * SQO-OSS framework.
     * <br/>
     * <i>Note: Synchronize this enumeration with the SQO-OSS types set when
     * necessary.</i>
     */
    public enum MetricActivator {
        PROJECTFILE,
        PROJECTVERSION,
        DEVELOPER;

        public static MetricActivator fromString(String type) {
            if (type.equals(PROJECTFILE.toString()))
                return PROJECTFILE;
            else if (type.equals(PROJECTVERSION.toString()))
                return PROJECTVERSION;
            else if (type.equals(DEVELOPER.toString()))
                return DEVELOPER;
            else
                return null;
        }
    };

    /**
     * Instantiates a new <code>Metric</code> object, initializes it with
     * the metric data contained in the given <code>WSMetric</code>
     * instance, and sets the specified metric type on it.
     *
     * @param metric the metric object
     * @param type the metric type
     */
    public Metric (WSMetric metric, String type) {
        if (metric != null) {
            this.mnemonic    = metric.getMnemonic();
            this.id          = metric.getId();
            this.type        = MetricType.fromString(type);
            this.description = metric.getDescription();
            this.activator   = ((metric.getActivator() != null)
                    ? MetricActivator.fromString(
                            metric.getActivator().toUpperCase())
                    : null);
        }
    }

    public Long getId() {
        return id;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public MetricType getType () {
        return type;
    }

    public String getDescription () {
        return description;
    }

    public MetricActivator getActivator() {
        return activator;
    }

    public String getScope() {
        if ((type != null) && (activator != null)) {
            if (this.type.equals(MetricType.SOURCE_CODE)) {
                if (this.activator.equals(MetricActivator.PROJECTFILE))
                    return "Source code statistics";
                if (this.activator.equals(MetricActivator.PROJECTVERSION))
                    return "Source code per version";
            }
            else if (this.type.equals(MetricType.SOURCE_FOLDER)) {
                if (this.activator.equals(MetricActivator.PROJECTFILE))
                    return "Source module statistics";
            }
            else if (this.type.equals(MetricType.PROJECT_WIDE)) {
                if (this.activator.equals(MetricActivator.DEVELOPER))
                    return "Project developer";
                if (this.activator.equals(MetricActivator.PROJECTFILE))
                    return "Project source code";
                if (this.activator.equals(MetricActivator.PROJECTVERSION))
                    return "Project version";
            }
        }
        return "N/A";
    }

    public String getLink() {
        return "<a href=\"/files.jsp?rid=" + getId() + "\">view results</a>";
        // TODO: Should go to results.jsp, with Mnem + ids
    }

    public String getSelectMetricLink() {
        return "<a href=\"/metrics.jsp?selectMetric=" + getId() + "\">Select</a>";
    }

    public String getDeselectMetricLink() {
        return "<a href=\"/metrics.jsp?deselectMetric=" + getId() + "\">Deselect</a>";
    }


    /* (non-Javadoc)
     * @see eu.sqooss.webui.WebuiItem#getHtml(long)
     */
    public String getHtml (long in) {
        return description + " (" + type + ", " + mnemonic + ")";
    }

}
