/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Sebastian Kuegler <sebas@kde.org>
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

import java.util.SortedMap;
import java.util.TreeMap;

//import eu.sqooss.ws.client.datatypes.WSProjectWebuiItem;

import eu.sqooss.webui.File;


public class WebuiItem {

    private static final String COMMENT = "<!-- WebuiItem -->\n";
    protected Long id;
    protected String page = "home.jsp";
    protected Terrier terrier;
    // Contains a sorted list of all files in this version mapped to their ID.
    protected SortedMap<Long, File> files;
    protected int fileCount;

    public WebuiItem () {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Terrier getTerrier () {
        return terrier;
    }

    public void setTerrier (Terrier t) {
        terrier = t;
    }

    public String getHtml() {
        StringBuilder html = new StringBuilder(COMMENT);
        html.append("<b>WebuiItem:</b> " + id);
        return html.toString();
    }

    public String shortName () {
        return "v" + id;
    }

    public String longName () {
        return getHtml(); // Yeah, we'r lazy.
    }

    public String link(String cssClass) {
        String css_class = "";
        if (cssClass != null) {
            css_class = " class=\"" + cssClass + "\" ";
        }
        return "<a href=\"" + page + "?id=" + id + "\" " + css_class + ">" + shortName() + "</a>";
    }

    public SortedMap<Long, File> getFiles () {
        terrier.addError("getFiles() in WebuiItem should not be called");
        fileCount = 0;
        return new TreeMap<Long, File>();
    }

    public void setFileCount(Integer n) {
        fileCount = n;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFiles() {
        this.files = getFiles();
    }

    public String listFiles() {
        setFiles();
        try {
            StringBuilder html = new StringBuilder();
            //if (files == null) {
            //    setFiles();
            //}
            html.append("\n<ul");
            for (File f: files.values()) {
                html.append("\n\t<li>" + f.getLink() + "</li>");
            }
            html.append("\n</ul>");
            return html.toString();
        } catch (NullPointerException npe) {
            terrier.addError("No files to list");
            return "No files to list.";
        }
    }
}
