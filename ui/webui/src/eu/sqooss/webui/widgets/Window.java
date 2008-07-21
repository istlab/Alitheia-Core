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

import java.util.ArrayList;
import java.util.List;

import eu.sqooss.webui.Functions;

public class Window extends AbstractWindow {
    List<AbstractIcon> titlebar = new ArrayList<AbstractIcon>();
    List<AbstractIcon> toolbar  = new ArrayList<AbstractIcon>();

    public void addTitleIcon (AbstractIcon icon) {
        titlebar.add(icon);
    }

    public void addToolIcon (AbstractIcon icon) {
        toolbar.add(icon);
    }

    @Override
    public String render(long in) {
        StringBuilder b = new StringBuilder("");
        b.append(Functions.sp(in++) + "<div class=\"win\">\n");
        // ===================================================================
        // Display the window's title bar
        // ===================================================================
        b.append(Functions.sp(in++) + "<div class=\"winTitle\""
                + ((toolbar.size() > 0)
                        ? " style=\"border-bottom: 0 none;\""
                        : "")
                + ">\n"
                + Functions.sp(in) + title + "\n");
        if (titlebar.size() > 0) {
            b.append(Functions.sp(in++) + "<div class=\"winTitleBar\">\n");
            for (AbstractIcon icon : titlebar)
                b.append(Functions.sp(in) + icon.render());
            b.append(Functions.sp(--in) + "</div>\n");
        }
        b.append(Functions.sp(--in) + "</div>\n");
        // ===================================================================
        // Display the window's tool bar
        // ===================================================================
        if (toolbar.size() > 0) {
            b.append(Functions.sp(in++) + "<div class=\"winToolbar\">\n");
            for (AbstractIcon icon : toolbar)
                b.append(Functions.sp(in) + icon.render());
            b.append(Functions.sp(--in) + "</div>\n");
        }
        // ===================================================================
        // Display the window's content
        // ===================================================================
        if (content != null) {
            b.append(Functions.sp(in) + "<div class=\"winContent\">\n");
            b.append(content);
            b.append(Functions.sp(in) + "</div>\n");
        }
        // ===================================================================
        // Display the window's footer
        // ===================================================================
        if (footer != null) {
            b.append(Functions.sp(in++) + "<div class=\"winFooter\">\n");
            b.append(Functions.sp(in) + footer);
            b.append(Functions.sp(--in) + "</div>\n");
        }
        b.append(Functions.sp(--in) + "</div>\n");
        return b.toString();
    }

}
