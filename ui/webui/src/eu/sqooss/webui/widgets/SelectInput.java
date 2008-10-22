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

import java.util.SortedMap;
import java.util.TreeMap;

public class SelectInput extends AbstractIcon {
    private SortedMap<String, String> options = new TreeMap<String, String>();
    private String selected;
    private String labelText;
    private String buttonText = "Apply";

    public void setSelected(String name) {
        this.selected = name;
    }

    public void setLabelText(String text) {
        this.labelText = text;
    }

    public void setButtonText(String text) {
        this.buttonText = text;
    }

    public void addOption(String value, String name) {
        if ((value != null)
                && (name != null)
                && (options.containsKey(name) == false))
            options.put(name, value);
    }

    @Override
    public String render() {
        StringBuilder b = new StringBuilder("");

        if (getStatus()) {
            b.append("<form class=\"icoTextInput\">");
            b.append(labelText != null ? "<b>" + labelText + "</b>" : "");
            b.append("<select class=\"icoTextInput\""
                    + " name=\""
                    + ((getParameter() != null) ? getParameter() : "" )
                    + "\""
                    + ((selected != null)
                            ? " value=\"" + options.get(selected) +"\"" 
                            : "" )
                    + "/>");
            for (String name : options.keySet()) {
                b.append("<option"
                        + ((name.equals(selected)) ? " selected" : "")
                        + " value=\"" + options.get(name) + "\">"
                        + "" + name
                        + "</option>");
            }
            b.append("</select>");
            b.append("<input type=\"submit\" class=\"icoButton\""
                    + " value=\"" + buttonText + "\">");
            b.append("</form>\n");
        }
        else {
            b.append(labelText != null ? "<b>" + labelText + "</b>" : "");
        }
        return b.toString();
    }

}
