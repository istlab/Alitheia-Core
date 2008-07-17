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
package eu.sqooss.webui.widgets;

public class WinIcon extends AbstractIcon {
    private String image;
    private String alt;

    public static String MAXIMIZE       = "true";
    public static String MINIMIZE       = "false";
    
    public static String MAXIMIZE_ICON  = "/img/icons/16x16/window-maximize.png";
    public static String MINIMIZE_ICON  = "/img/icons/16x16/window-minimize.png";

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getAlt() {
        return alt;
    }
    public void setAlt(String alt) {
        this.alt = alt;
    }

    public static WinIcon maximize (String path, String parameter) {
        WinIcon icon = new WinIcon();
        icon.setPath(path);
        icon.setParameter(parameter);
        icon.setValue(MAXIMIZE);
        icon.setImage(MAXIMIZE_ICON);
        icon.setAlt("Show");
        return icon;
    }

    public static WinIcon minimize (String path, String parameter) {
        WinIcon icon = new WinIcon();
        icon.setPath(path);
        icon.setParameter(parameter);
        icon.setValue(MINIMIZE);
        icon.setImage(MINIMIZE_ICON);
        icon.setAlt("Hide");
        return icon;
    }

    @Override
    public String render() {
        StringBuilder b = new StringBuilder("");
        String strAlt = (alt != null) ? " alt=\"" + alt + "\"" : "";
        String strTitle = (alt != null) ? " title=\"" + alt + "\"" : "";
        if (getStatus()) {
            String action = "";
            if ((getParameter() != null) && (getParameter().length() > 0)) {
                action += "?" + getParameter();
                if ((getValue() != null) && (getValue().length() > 0))
                    action += "=" + getValue();
            }
            if (action.length() > 0)
                b.append("<a class=\"icon\""
                        + " href=\"" + getPath() + action + "\">"
                        + "<img"
                        + strAlt
                        + strTitle
                        + " src=\"" + getImage() + "\">"
                        + "</a>\n");
            else
                b.append("<img class=\"icon\""
                        + strAlt
                        + " src=\"" + getImage() + "\">\n");
        }
        else {
            b.append("<img class=\"icon_disabled\""
                    + strAlt
                    + " src=\"" + getImage() + "\">\n");
        }
        return b.toString();
    }
}
