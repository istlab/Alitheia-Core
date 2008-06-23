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

/**
 * This class represents the base storage class for a single SQO-OSS DAO.
 */
public abstract class WebuiItem {

    // Hold the object Id
    protected Long id;

    // Holds the object name
    protected String name;

    // Contains the servlet path of the page where this object will be shown
    private String servletPath;

    // Contains the HTML request parameter's name that references this object
    protected String reqParName;

    // Holds the terrier's instance.
    protected Terrier terrier;

    // Holds the user settings for this session
    protected SelectedSettings settings = new SelectedSettings();

    /**
     * Gets the Id of this DAO object.
     * 
     * @return the DAO Id, or <code>null</code> when the
     *   object is not yet initialized.
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the Id of this DAO object.
     * 
     * @param id the Id value
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of this DAO object.
     * 
     * @return the DAO name, or <code>null</code> when the
     *   object is not yet initialized.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this DAO object.
     * 
     * @param name the name value
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the <code>Terrier</code> connector instance.
     * 
     * @return The <code>Terrier<code> instance.
     */
    public Terrier getTerrier () {
        return terrier;
    }

    /**
     * Sets the <code>Terrier</code> connector instance.
     * 
     * @param terrier the <code>Terrier<code> instance
     */
    public void setTerrier (Terrier terrier) {
        this.terrier = terrier;
    }

    /**
     * Sets the the user settings for this session.
     * 
     * @param settings the new settings
     */
    public void setSettings(SelectedSettings settings) {
        this.settings = settings;
    }

    /**
     * Renders the data stored in this object into HTML.
     * 
     * @param indentationDepth indentation depth of the generated HTML
     *   content.
     * 
     * @return The HTML content.
     */
    public abstract String getHtml(long in);

    /**
     * Generates an HTML link to a page that shows detailed information about
     * this object.
     * The members page, reqName and id are required for this to work. Optionally,
     * you can provide a CSS class to style the link. The link is build as $page?$reqName=$id,
     * for example project.jsp?project=42 (with page="project.jsp", reqName="project", id=42.
     * 
     * @param cssClass the CSS class to be used on the link
     * 
     * @return The link's HTML content.
     */
    public String link(String cssClass) {
        String css_class = "";
        if (cssClass != null)
            css_class = " class=\"" + cssClass + "\" ";
        return "<a href=\"" + servletPath + "?" + reqParName + "=" + id + "\""
            + " " + css_class + ">"
            + getName() + "</a>";
    }

    /**
     * Convenience method that returns a link without using a CSS class.
     */
    public String link() {
        return link(null);
    }

    /**
     * Return an HTML snippet string representing the object icon.
     * 
     * @param name the icon's filename (without the ".png" extension)
     * 
     * @return the icon's HTML string
     */
    public String icon(String name) {
        return Functions.icon(name);
    }

    /**
     * Return an HTML snippet string representing the object icon at
     * a certain size.
     * 
     * @param name the icon's filename (without the ".png" extension)
     * @param size the icon's size (in pixels)
     * 
     * @return the icon's HTML string
     */
    public String icon(String name, int size) {
        return Functions.icon(name, size);
    }

    /**
     * Renders an HTML snippet string representing the object icon at
     * a certain size and with a HTML tooltip.
     * 
     * @param name the icon's filename (without the ".png" extension)
     * @param size the icon's size (in pixels)
     * @param tooltip the icon's tooltip text
     * 
     * @return the icon's HTML string
     */
    public String icon(String name, int size, String tooltip) {
        return Functions.icon(name, size, tooltip);
    }

    /**
     * Returns the initialization state of this object.
     * 
     * @return <code>true</code> when this object is initialized,
     *   or <code>false</code> otherwise.
     */
    public boolean isValid() {
        return id != null;
    }

    /**
     * Explicitely invalidate this object.
     */
    public void invalidate() {
        id = null;
    }

    /**
     * Gets the servlet path of the page where this view will be displayed.
     * 
     * @return the servlet path
     */
    protected String getServletPath() {
        return servletPath;
    }

    /**
     * Sets the servlet path of the page where this view will be displayed.
     * 
     * @param servletPath the new servlet path
     */
    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    /**
     * Generates a string that contains a <b>2*num</b> spaces.
     * <br/>
     * <i>Used for indentation of the HTML content that is generated by the
     * various views.</i>
     * 
     * @param num the indentation depth
     * 
     * @return The indentation string.
     */
    protected static String sp (long num) {
        StringBuilder b = new StringBuilder();
        for (long i = 0; i < num; i++)
            b.append("  ");
        return b.toString();
    }

}
