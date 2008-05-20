/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008-2008 by Sebastian Kuegler <sebas@kde.org>
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

// Java imports
import java.util.Iterator;
import java.util.Vector;

public class FileListView extends ListView {

    private Vector<File> files = new Vector<File>();

    // Contains the ID of the selected project, if any
    private Long projectId;

    public FileListView () {
    }
    
    public Integer size() {
        return files.size();
    }

    public void addFile(eu.sqooss.webui.File file) {
        files.add(file);
    }

    public void setFiles(Vector<File> files) {
        this.files = files;
    }

    public Vector<String> filterItems (Vector<String> items) {
        // TODO: Remove some files from this item list, such as Makefile, COPYING,
        // or alternatively, only show a list of certain extensions, like .cpp, .h ...
        return items;
    }

    public String getHtml() {
        StringBuffer html = new StringBuffer();
        Iterator<File> filesIterator = files.iterator();
        html.append(files.size() + " Files found \n<ul>\n");
        while (filesIterator.hasNext()) {
            File nextFile = filesIterator.next();
            html.append(
                    (nextFile != null)
                    ? "\n<li>" + nextFile.getHtml() + "</li>"
                    : "");
        }
        html.append("\n</ul>");
        return html.toString();
    }
}