/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008-2008 by Sebastian Kuegler <sebas@kde.org>
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

import java.util.ArrayList;

import eu.sqooss.ws.client.datatypes.WSMetric;


class Metric {

    private Long id;
    private String name;
    private String type;
    private String description;

    /** Parses an ArrayList of WSResult and offers convenience methods to get data
     *  out of it.
     * 
     * @param data The ArrayList for one metric
     * 
     */
    public Metric (ArrayList data) {
        try {
            id = Long.parseLong(data.get(0).toString().trim()); // Urgh?
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: " + nfe.getMessage());
        }
        description = data.get(1).toString();
        type = data.get(2).toString();
    }

    public Metric (WSMetric metric) {
        name        = "NONAME"; // TODO: find out how WSMetric provides it
        id          = metric.getId();
        type        = metric.getMetricType().getType();
        description = metric.getDescription();
    }

    public Long getId () {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType () {
        return type;
    }

    public String getDescription () {
        return description;
    }

    public String getHtml() {
        StringBuilder html = new StringBuilder("<!-- Metric -->");
        html.append("<h3>Metric: " + getId() + "</h3>");
        html.append("<br />Type: " + getType());
        html.append("<br />Description: " + getDescription());
        return html.toString();
    }

}
