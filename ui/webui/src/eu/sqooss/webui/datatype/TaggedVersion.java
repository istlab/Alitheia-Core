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

package eu.sqooss.webui.datatype;

import java.util.*;

import eu.sqooss.webui.Terrier;
import eu.sqooss.ws.client.datatypes.WSTaggedVersion;

/**
 * This class represents a tagged version of a project that has been evaluated
 * by the SQO-OSS framework.
 * <br/>
 * It provides access to the version's meta-data, source files contained in
 * this version, and various methods for accessing and presenting version
 * and file based results.
 */
public class TaggedVersion extends Version {

    /*
     * Tagged version's meta-data
     */
    private List<String> tagNames;

    /**
     * Creates a new a <code>TaggedVersion</code> instance.
     */
    public TaggedVersion() {
        
    }

    /**
     * Creates a new a <code>TaggedVersion</code> instance, and initializes it
     * with the information stored in the given <code>WSTaggedVersion</code>
     * object.
     */
    public TaggedVersion(WSTaggedVersion wsVersion, Terrier terrier) {
        if (wsVersion != null) {
            id = wsVersion.getId();
            try {
                number = wsVersion.getVersion();
            }
            catch (NumberFormatException ex) { /* Do nothing */}
            name = ((number != null) ? number.toString() : "N/A");
            projectId = wsVersion.getProjectId();
            committerId = wsVersion.getCommitterId();
            timestamp = new Date(wsVersion.getTimestamp());
            tagNames = Arrays.asList(wsVersion.getTags());
        }
        setTerrier(terrier);
    }

    public TaggedVersion(Version version, Terrier terrier) {
        if (version != null) {
            id = version.getId();
            number = version.getNumber();
            name = ((number != null) ? number.toString() : "N/A");
            projectId = version.getProjectId();
            committerId = version.getCommitterId();
            timestamp = version.getTimestamp();
            // Forged tag name
            tagNames = new ArrayList<String>();
            tagNames.add(number.toString());
        }
        setTerrier(terrier);
    }

    /**
     * Returns the list of tags that were created on this project version.
     * 
     * @return the list of tags
     */
    public List<String> getTags() {
        return tagNames;
    }

    /**
     * Returns the first tag in the list of tags that were created on this
     * project version.
     * 
     * @return the first tag
     */
    public String getTag() {
        return tagNames.get(0);
    }

}
