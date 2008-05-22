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

import java.util.*;


public class WebuiItem {

    private static final String COMMENT = "<!-- WebuiItem -->\n";
    private StringBuilder error = new StringBuilder();
    protected Long id;
    protected String name;
    protected String page = "home.jsp";
    protected String reqName = "id";
    protected Terrier terrier;
    // Contains a sorted list of all files in this version mapped to their ID.
    protected SortedMap<Long, File> files;
    Vector<File> fs; // For convenience

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
        html.append("<strong>WebuiItem:</strong> " + id);
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
        return "<a href=\"" + page + "?" + reqName + "=" + id + "\" " + css_class + ">" + shortName() + "</a>";
    }

    public String link() {
        return link(null);
    }

    protected void getFiles () {
        terrier.addError("getFiles() in WebuiItem should not be called");
        fileCount = 0;
    }

    public void setFileCount(Integer n) {
        fileCount = n;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFiles(SortedMap<Long, File> f) {
        files = f;
    }

    public String listFiles() {
        getFiles();
        try {
            StringBuilder html = new StringBuilder();
            html.append("\n<ul>");
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

    public String fileStats() {
        getFiles();
        if (fs == null) {
            return "No files.";
        }
        int total = fileCount = fs.size();
        int added = 0;
        int modified = 0;
        int deleted = 0;

        Iterator<File> filesIterator = fs.iterator();
        while (filesIterator.hasNext()) {
            File nextFile = filesIterator.next();
            if (nextFile.getStatus().equals("MODIFIED")) {
                modified += 1;
            } else if (nextFile.getStatus().equals("ADDED")) {
                added += 1;
            } else if (nextFile.getStatus().equals("DELETED")) {
                deleted +=1;
            }
        }

        StringBuilder html = new StringBuilder("\n\n<table>");
        html.append("\n\t<tr><td>" + icon("vcs_add") + "<strong>Files added:</strong></td>\n\t<td>" + added + "</td></tr>");
        html.append("\n\t<tr><td>" + icon("vcs_update") + "<strong>Files modified:</strong></td>\n\t<td>" + modified + "</td></tr>");
        html.append("\n\t<tr><td>" + icon("vcs_remove") + "<strong>Files deleted:</strong></td>\n\t<td>" + deleted + "</td></tr>");
        html.append("\n\t<tr><td colspan=\"2\"><hr /></td></tr>");
        html.append("\n\t<tr><td>" + icon("vcs_status") + "<strong>Total files changed:</strong></td><td>" + total + "</td>\n\t</tr>");
        html.append("\n</table>");

        return html.toString();
    }

    public String icon(String name) {
        return Functions.icon(name);
    }
    
    public String icon(String name, int size) {
        return Functions.icon(name, size);
    }

    public String icon(String name, int size, String tooltip) {
        return Functions.icon(name, size, tooltip);
    }

    public void addError(String html) {
        error.append(html);
    }

    public String error() {
        return error.toString();
    }

    public Boolean isValid() {
        return id != null;
    }

    public void setInValid() {
        id = null;
    }
}
