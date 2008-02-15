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


public class Project {

    Long id;
    String name;
    String bts;
    String scm;
    String mail;
    String contact;
    String website;

    /** Parses an ArrayList of WSResult and offers convenience methods to get data
     *  out of it.
     * 
     * @param data The ArrayList for one project
     * 
     */
    public Project (ArrayList data) {
        try {
            id = Long.parseLong(data.get(0).toString().trim()); // Urgh?
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: " + nfe.getMessage());
        }
        name = data.get(1).toString();
        bts = data.get(2).toString(); // FIXME: Is this really the BTS?
        scm = data.get(3).toString();
        mail = data.get(4).toString();
        contact = data.get(5).toString();
        website = data.get(6).toString();
    }

    public String getName () {
        return name;
    }

    public Long getId () {
        return id;
    }

    public String getWebsite () {
        return website;
    }

    public String getMail () {
        return mail;
    }

    public String getContact () {
        return contact;
    }

    public String getHtml() {
        StringBuilder html = new StringBuilder("<!-- Project -->\n<ul>");
        html.append("<h3>" + getName() + " (" + getId() + ")</h3>");
        html.append("<br />Website:<a href=\"" + getWebsite() + "\">" + getWebsite() + "</a>");
        html.append("<br />Contact:<a href=\"" + getContact() + "\">" + getContact() + "</a>");
        return html.toString();
    }
}
