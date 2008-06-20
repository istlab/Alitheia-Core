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
 * This class is just for static strings and string formatting
 * functions. The static strings are various shared messages,
 * and the formatters take any strings and put HTML fanciness around them.
 */
public class Functions {

    public static final String NOT_YET_EVALUATED =
        "This project has not yet been evaluated!";

    public static final String NO_INSTALLED_METRICS =
        "No installed metrics has been found!";

    public static String error(String msg) {
        return "<strong><font color=\"red\">" + msg + "</font></strong>";
    }

    public static String debug(String msg) {
        return "<strong><font color=\"orange\">" + msg + "</font></strong>";
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

}
