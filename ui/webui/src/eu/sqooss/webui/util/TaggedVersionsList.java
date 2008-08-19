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

package eu.sqooss.webui.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import eu.sqooss.webui.datatype.TaggedVersion;

public class TaggedVersionsList extends ArrayList<TaggedVersion> {

    /**
     * Class serial
     */
    private static final long serialVersionUID = -2171890702868485954L;

    /**
     * Returns the list of all tagged versions sorted by their version number.
     * 
     * @return The list of tagged versions.
     */
    public SortedMap<Long, TaggedVersion> sortByNumber() {
        SortedMap<Long, TaggedVersion> result =
            new TreeMap<Long, TaggedVersion>();
        for (TaggedVersion nextVersion : this)
            result.put(nextVersion.getNumber(), nextVersion);
        return result;
    }

    /**
     * Returns the list of all tagged versions sorted by their Id.
     * 
     * @return The list of tagged versions.
     */
    public SortedMap<Long, TaggedVersion> sortById() {
        SortedMap<Long, TaggedVersion> result =
            new TreeMap<Long, TaggedVersion>();
        for (TaggedVersion nextVersion : this)
            result.put(nextVersion.getId(), nextVersion);
        return result;
    }

    /**
     * Gets the tagged version with the given version number.
     * 
     * @param number the tagged version's number
     * 
     * @return The tagged version object, or <code>null</code> if a tagged
     *  version with the given version number can not be found in this list.
     */
    public TaggedVersion getTaggedVersionByNumber(Long number) {
        if (number == null) return null;
        return sortByNumber().get(number);
    }

    /**
     * Gets the tagged version with the given Id.
     * 
     * @param id the tagged version's Id
     * 
     * @return The tagged version object, or <code>null</code> if a tagged
     *   version with the given Id can not be found in this list.
     */
    public TaggedVersion getTaggedVersionById(Long id) {
        return sortById().get(id);
    }

    /**
     * Gets the list of version numbers of all tagged versions in this list,
     * indexed by their tagged version Id.
     * 
     * @return The list of version numbers, or an empty list when none are
     *   found.
     */
    public Map<Long, Long> getTaggedVersionNumber() {
        Map<Long, Long> result = new HashMap<Long, Long>();
        for (TaggedVersion nextVersion : this)
            result.put(nextVersion.getId(), nextVersion.getNumber());
        return result;
    }
}
