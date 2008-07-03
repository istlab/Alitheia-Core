/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Sebastian Kuegler <sebas@kde.org>
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

package eu.sqooss.webui.view;

import java.util.ArrayList;
import java.util.Collection;

import eu.sqooss.webui.ListView;
import eu.sqooss.webui.datatypes.Developer;

// TODO: Add JavaDoc
public class DevelopersListView extends ListView {
    /** Holds the list of developers */
    Collection<Developer> developers = new ArrayList<Developer>();

    public DevelopersListView (Collection<Developer> developers) {
        if (developers != null)
            this.developers = developers;
    }

    @Override
    public String getHtml(long in) {
        StringBuilder b = new StringBuilder("");
        if (developers.isEmpty()) {
            
        }
        else {
            b.append(sp(in++) + "<div id=\"table\">\n");
            b.append(sp(in++) + "<table>\n");
            b.append(sp(in++) + "<thead>\n");
            b.append(sp(in++) + "<tr class=\"head\">\n");
            b.append(sp(in) + "<td class=\"head\" style=\"width: 60%;\">"
                    + "Real name" + "</td>\n");
            b.append(sp(in) + "<td class=\"head\" style=\"width: 40%;\">"
                    + "User name" + "</td>\n");
            b.append(sp(--in) + "</tr>\n");
            b.append(sp(--in) + "</thead>\n");
            b.append(sp(in++) + "<tbody>\n");
            for (Developer nextDev : developers) {
                b.append(sp(in++) + "<tr>\n");
                b.append(sp(in) + "<td>" + nextDev.getName() + "</td>\n");
                b.append(sp(in) + "<td>" + nextDev.getUsername() + "</td>\n");
                b.append(sp(--in) + "</tr>\n");
            }
            b.append(sp(--in) + "</tbody>\n");
            b.append(sp(--in) + "</table>\n");
            b.append(sp(--in) + "</div>\n");
        }
        return b.toString();
    }
    
}
