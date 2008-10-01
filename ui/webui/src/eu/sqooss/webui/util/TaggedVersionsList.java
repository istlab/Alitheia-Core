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
     * Returns the list of tagged versions sorted by their time stamps.
     * 
     * @return The list of tagged versions.
     */
    public SortedMap<Long, TaggedVersion> sortByTimestamp() {
        SortedMap<Long, TaggedVersion> result =
            new TreeMap<Long, TaggedVersion>();
        for (TaggedVersion nextVersion : this)
            result.put(nextVersion.getTimestamp(), nextVersion);
        return result;
    }

    /**
     * Returns the list of tagged versions sorted by their SCM version Id.
     * 
     * @return The list of tagged versions.
     */
    public SortedMap<String, TaggedVersion> sortByScmId() {
        SortedMap<String, TaggedVersion> result =
            new TreeMap<String, TaggedVersion>();
        for (TaggedVersion nextVersion : this)
            result.put(nextVersion.getName(), nextVersion);
        return result;
    }

    /**
     * Returns the list of tagged versions sorted by their Id.
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
     * Gets the tagged version with the given time stamp.
     * 
     * @param timestamp the tagged version's time stamp
     * 
     * @return The tagged version object, or <code>null</code> if a tagged
     *  version with the given time stamp can not be found in this list.
     */
    public TaggedVersion getTaggedVersionByTimestamp(Long timestamp) {
        if (timestamp == null) return null;
        return sortByTimestamp().get(timestamp);
    }

    /**
     * Gets the tagged version with the given SCM version Id.
     * 
     * @param scmId the tagged version's SCM Id
     * 
     * @return The tagged version object, or <code>null</code> if a tagged
     *  version with the given SCM Id can not be found in this list.
     */
    public TaggedVersion getTaggedVersionByScmId(String scmId) {
        if (scmId == null) return null;
        return sortByScmId().get(scmId);
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
     * Gets the list of time stamps of all tagged versions in this list,
     * indexed by their tagged version Id.
     * 
     * @return The list of time stamps, or an empty list when none are found.
     */
    public Map<Long, Long> getTaggedVersionTimestamp() {
        Map<Long, Long> result = new HashMap<Long, Long>();
        for (TaggedVersion nextVersion : this)
            result.put(nextVersion.getId(), nextVersion.getTimestamp());
        return result;
    }
}
