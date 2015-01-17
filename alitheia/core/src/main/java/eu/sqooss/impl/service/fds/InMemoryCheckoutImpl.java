/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

package eu.sqooss.impl.service.fds;

import java.util.List;
import java.util.regex.Pattern;

import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.fds.InMemoryCheckout;
import eu.sqooss.service.fds.InMemoryDirectory;

/**
 * An implementation of the InMemoryCheckout interface that uses the 
 * DB service to retrieve file information for a specific version.
 */
class InMemoryCheckoutImpl implements InMemoryCheckout {
   
    private ProjectVersion revision;
    private InMemoryDirectory root;
    private Pattern pattern;

    InMemoryCheckoutImpl(ProjectVersion pv) {
        revision = pv;
        pattern = Pattern.compile(".*");
    }

    InMemoryCheckoutImpl(ProjectVersion pv, Pattern p) {
        revision = pv;
        pattern = p;
    }

    protected void createCheckout() {
        root = new InMemoryDirectory(this);
        
        List<ProjectFile> projectFiles = revision.getFiles();
        if (projectFiles != null && projectFiles.size() != 0) {
            for (ProjectFile f : projectFiles) {
                if (pattern.matcher(f.getFileName()).matches()) {
                    if (!f.getIsDirectory()) {
                        root.createSubDirectory(f.getDir().getPath()).addFile(f.getName());
                    } else {
                        root.createSubDirectory(f.getFileName());
                    }
                }
            }
        }
    }

    /** {@inheritDoc} */
    public InMemoryDirectory getRoot() {
        if (root == null) 
            createCheckout();
        return root;
    }

    /** {@inheritDoc} */
    public ProjectFile getFile(String name) {
        if (root == null) 
            createCheckout();
        return root.getFile(name);
    }

    /** {@inheritDoc} */
    public ProjectVersion getProjectVersion() {
        return revision;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

