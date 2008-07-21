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

package eu.sqooss.webui.view;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import eu.sqooss.webui.Functions;
import eu.sqooss.webui.ListView;
import eu.sqooss.webui.util.Directory;
import eu.sqooss.webui.widgets.TextIcon;
import eu.sqooss.webui.widgets.WinIcon;

public class DirBrowserView extends ListView {
    private HashMap<Long, Directory> dirTree = null;
    private Long selectedDir = null;

    public DirBrowserView(HashMap<Long, Directory> dirTree) {
        super();
        this.dirTree = dirTree;
    }

    private Directory getRootDir() {
        for (Directory nextDir : dirTree.values())
            if (nextDir.isRoot()) return nextDir;
        return null;
    }

    public Long getSelectedDir() {
        return selectedDir;
    }

    public void setSelectedDir(Long selectedDir) {
        this.selectedDir = selectedDir;
    }

    private String getTree(long in, int depth, Directory dir) {
        StringBuffer b = new StringBuffer("");
        // Prepare the status icon
        WinIcon icoStatus = new WinIcon();
        icoStatus.setPath(getServletPath());
        icoStatus.setParameter("dst");
        icoStatus.setValue(dir.getId().toString());
        icoStatus.setAlt("");
        // Prepare the label icon
        TextIcon icoLabel = new TextIcon();
        icoLabel.setPath(getServletPath());
        icoLabel.setParameter("did");
        icoLabel.setValue(dir.getId().toString());
        icoLabel.setText(dir.getName());
        if ((selectedDir != null) && (selectedDir.equals(dir.getId())))
            icoLabel.setStatus(false);
        // Display the given directory
        if (dir.isCollapsed()) {
            icoStatus.setImage("/img/icons/16x16/expand.png");
            icoStatus.setAlt("Expand folder");
            b.append(sp(in) + "<div class=\"browserLine\""
                    + " style=\"padding-left: " + depth + "em;\">"
                    + sp(in) + icoStatus.render() + icoLabel.render()
                    + sp(--in) + "</div>\n");
        }
        else {
            icoStatus.setImage("/img/icons/16x16/collapse.png");
            icoStatus.setAlt("Collapse folder");
            b.append(sp(in++) + "<div class=\"browserLine\""
                    + " style=\"padding-left: " + depth + "em;\">"
                    + sp(in) + icoStatus.render() + icoLabel.render()
                    + sp(--in) + "</div>\n");
            SortedMap<String, Long> sortedChilds =
                new TreeMap<String, Long>();
            for (Long child : dir.getChilds())
                sortedChilds.put(dirTree.get(child).getName(), child);
            for (Long child : sortedChilds.values())
                b.append(getTree(in + 2, depth + 1, dirTree.get(child)));
        }
        return b.toString();
    }

    @Override
    public String getHtml(long in) {
        StringBuffer b = new StringBuffer("");
        Directory rootDir = getRootDir();
        if (rootDir == null) {
            b.append(sp(in) + Functions.error(
                    "This project has no source repository!"));
        }
        else
            b.append(sp(in) + getTree(in + 2, 0, rootDir));

        return b.toString();
    }

}
