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

import java.util.ArrayList;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class is just for static strings and string formatting
 * functions. The static strings are various shared messages,
 * and the formatters take any strings and put HTML fanciness around them.
 */
public class Functions {

    // Resource bundles
    private static ResourceBundle resLbl = null;
    private static ResourceBundle resMsg = null;
    private static ResourceBundle resErr = null;

    // Some constants that are used internally
    private static String NULL_PARAM_NAME = "Undefined parameter name!";

    public static final String NOT_YET_EVALUATED =
        "This project has not yet been evaluated!";

    public static final String NO_INSTALLED_METRICS =
        "No installed metrics has been found!";

    /**
     * Initializes the various resource bundle with the specified locale.
     * 
     * @param locale the user's locale
     */
    public static void initResources (Locale locale) {
        if (locale != null)
            resLbl = ResourceBundle.getBundle("file_name", locale);
        else
            resLbl = ResourceBundle.getBundle("file_name");
    }

    /**
     * Retrieves the value of the given resource property from the
     * resource bundle that stores all label strings.
     * 
     * @param name the name of the resource property
     * 
     * @return The property's value, when that property can be found in the
     *   corresponding resource bundle, OR the provided property name's
     *   parameter, when such property is missing.
     */
    public static String getLbl (String name) {
        if (resLbl != null) {
            try {
                return resLbl.getString(name);
            }
            catch (NullPointerException ex) {
                return NULL_PARAM_NAME;
            }
            catch (MissingResourceException ex) {
                return name;
            }
        }
        return name;
    }

    /**
     * Retrieves the value of the given resource property from the
     * resource bundle that stores all error strings.
     * 
     * @param name the name of the resource property
     * 
     * @return The property's value, when that property can be found in the
     *   corresponding resource bundle, OR the provided property name's
     *   parameter, when such property is missing.
     */
    public static String getErr (String name) {
        if (resErr != null) {
            try {
                return resErr.getString(name);
            }
            catch (NullPointerException ex) {
                return NULL_PARAM_NAME;
            }
            catch (MissingResourceException ex) {
                return name;
            }
        }
        return name;
    }

    /**
     * Retrieves the value of the given resource property from the
     * resource bundle that stores all message strings.
     * 
     * @param name the name of the resource property
     * 
     * @return The property's value, when that property can be found in the
     *   corresponding resource bundle, OR the provided property name's
     *   parameter, when such property is missing.
     */
    public static String getMsg (String name) {
        if (resMsg != null) {
            try {
                return resMsg.getString(name);
            }
            catch (NullPointerException ex) {
                return NULL_PARAM_NAME;
            }
            catch (MissingResourceException ex) {
                return name;
            }
        }
        return name;
    }

    public static String error(String content) {
        StringBuilder b = new StringBuilder("");
        b.append("<div class=\"win\" style=\"margin-bottom:0;\">");
        // Display the window title
        b.append("<div class=\"winTitle\""
                + " style=\"padding-left: 1.5em;"
                + " background: #FF6633 url(/img/icons/16x16/dialog-error.png) no-repeat;"
                + " background-position: 0.2em 50%;\""
                + ">"
                + "Error"
                + "</div>");
        b.append("<div class=\"winContent\">" + content + "</div>");
        b.append("</div>\n");
        return b.toString();
    }

    public static String warning(String content) {
        StringBuilder b = new StringBuilder("");
        b.append("<div class=\"win\" style=\"margin-bottom:0;\">");
        // Display the window title
        b.append("<div class=\"winTitle\""
                + " style=\"padding-left: 1.5em;"
                + " background: #FFFF99 url(/img/icons/16x16/dialog-warning.png) no-repeat;"
                + " background-position: 0.2em 50%;\""
                + ">"
                + "Warning"
                + "</div>");
        b.append("<div class=\"winContent\">" + content + "</div>");
        b.append("</div>\n");
        return b.toString();
    }

    public static String information(String content) {
        StringBuilder b = new StringBuilder("");
        b.append("<div class=\"win\" style=\"margin-bottom:0;\">");
        // Display the window title
        b.append("<div class=\"winTitle\""
                + " style=\"padding-left: 1.5em;"
                + " background: #CCCCCC url(/img/icons/16x16/dialog-information.png) no-repeat;"
                + " background-position: 0.2em 50%;\""
                + ">"
                + "Information"
                + "</div>");
        b.append("<div class=\"winContent\">" + content + "</div>");
        b.append("</div>\n");
        return b.toString();
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
    public static String sp (long num) {
        StringBuilder b = new StringBuilder();
        for (long i = 0; i < num; i++)
            b.append("  ");
        return b.toString();
    }

    public static String icon(String name) {
        return icon(name, 0);
    }

    public static String icon(String name, int size) {
        return icon(name, size, name); // Just use name as tooltip for now
    }

    public static String icon(String name, int size, String tooltip) {
        if (size == 0) {
            size = 16;
        }
        StringBuilder html = new StringBuilder("<img src=\"/img/icons/");
        html.append(size + "x" + size + "/");
        html.append(name + ".png\" ");
        if (tooltip != null) {
            html.append("alt=\"" + tooltip + "\" ");
            html.append("title=\"" + tooltip + "\" ");
        }
        html.append("class=\"icon\" />");
        return html.toString();
    }

    public static Long[] strToLongArray (String str, String separator) {
        if (str == null) return null;

        String[] tokens = str.split(separator);
        ArrayList<Long> result = new ArrayList<Long>();
        for (String token: tokens) {
            try {
                result.add(new Long(token));
            }
            catch (NumberFormatException ex) { /* Do nothing */ }
        }

        if (result.isEmpty()) return null;
        return result.toArray(new Long[result.size()]);
    }
}
