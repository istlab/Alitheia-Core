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

/**
 * This class is the base class for the high-level API for the webui.
 */
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

    /** Some derived datatypes have files in them, we store them in this class,
     * even though they're not used for all derived object for simplicity. Classes
     * that do possess such a file list are Version and Project.
     */
    protected int fileCount;

    /** Empty ctor.
     */
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

    /** Return an HTML representation of this Object.
     */
    public String getHtml() {
        StringBuilder html = new StringBuilder(COMMENT);
        html.append("<strong>WebuiItem:</strong> " + id);
        return html.toString();
    }

    /** Returns a short HTML representation of this object.
     */
    public String shortName () {
        return name + " " + id;
    }

    /** Returns a longer HTML representation of this object.
     */
    public String longName () {
        return getHtml(); // Yeah, we're lazy.
    }

    /** Returns an HTML link to a page that shows more information about this object.
     * The members page, reqName and id are required for this to work. Optionally,
     * you can provide a CSS class to style the link. The link is build as $page?$reqName=$id,
     * for example project.jsp?project=42 (with page="project.jsp", reqName="project", id=42.
     */
    public String link(String cssClass) {
        String css_class = "";
        if (cssClass != null) {
            css_class = " class=\"" + cssClass + "\" ";
        }
        return "<a href=\"" + page + "?" + reqName + "=" + id + "\" " + css_class + ">" + shortName() + "</a>";
    }

    /** Convenience method that returns a link without using a CSS class.
     */
    public String link() {
        return link(null);
    }

    /** Count the number of files and store this number internally.
     */
    public void setFileCount(Integer n) {
        fileCount = n;
    }

    public int getFileCount() {
        return fileCount;
    }

    /** Set the internal list of Files.
     *
     */
    public void setFiles(SortedMap<Long, File> f) {
        files = f;
    }

    /** Return an HTML snippet string representing the object icon.
     */
    public String icon(String name) {
        return Functions.icon(name);
    }

    /** Return an HTML snippet string representing the object icon at a certain size.
     */
    public String icon(String name, int size) {
        return Functions.icon(name, size);
    }

    /** Return an HTML snippet string representing the object icon at a certain size.
     * with a tooltip.
     */
    public String icon(String name, int size, String tooltip) {
        return Functions.icon(name, size, tooltip);
    }

    /** Add an error that will be displayed by this object later on. Note that in most cases,
     * you'll want to add your messages to the Terrier object. This method is there if we
     * don't have a Terrier at hand and want to message the user nevertheless.
     */
    public void addError(String html) {
        error.append(html);
    }

    /** Return the errors of this object as printable String.
     */
    public String error() {
        return error.toString();
    }

    /** Is this object initialised?
     */
    public Boolean isValid() {
        return id != null;
    }

    /** Explicitely invalidate this object.
     */
    public void invalidate() {
        id = null;
    }
}
