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

import eu.sqooss.webui.datatype.Developer;

public class DevelopersList extends ArrayList<Developer> {

    /**
     * Class serial
     */
    private static final long serialVersionUID = 2751571082259271311L;

    /**
     * Returns the list of all developers sorted by their user name.
     * 
     * @return The list of developers.
     */
    public SortedMap<String, Developer> sortByUsername() {
        SortedMap<String, Developer> result = new TreeMap<String, Developer>();
        for (Developer nextDeveloper : this)
            result.put(nextDeveloper.getUsername(), nextDeveloper);
        return result;
    }

    /**
     * Returns the list of all developers sorted by their Id.
     * 
     * @return The list of developers.
     */
    public SortedMap<Long, Developer> sortById() {
        SortedMap<Long, Developer> result = new TreeMap<Long, Developer>();
        for (Developer nextDeveloper : this)
            result.put(nextDeveloper.getId(), nextDeveloper);
        return result;
    }

    /**
     * Gets the developer with the given user name.
     * 
     * @param username the developer's user name
     * 
     * @return The developer object, or <code>null</code> if a developer with
     *   the given user name can not be found in this list.
     */
    public Developer getDeveloperByUsername(String username) {
        if (username == null) return null;
        return sortByUsername().get(username);
    }

    /**
     * Gets the developer with the given Id.
     * 
     * @param id the developer's Id
     * 
     * @return The developer object, or <code>null</code> if a developer with
     *   the given Id can not be found in this list.
     */
    public Developer getDeveloperById(Long id) {
        return sortById().get(id);
    }

    /**
     * Gets the list of user names of all developers in this list, indexed by
     * their user Id.
     * 
     * @return The list of user names, or an empty list if none are found.
     */
    public Map<Long, String> getUsernames() {
        Map<Long, String> result = new HashMap<Long, String>();
        for (Developer nextDeveloper : this)
            result.put(nextDeveloper.getId(), nextDeveloper.getUsername());
        return result;
    }

}
