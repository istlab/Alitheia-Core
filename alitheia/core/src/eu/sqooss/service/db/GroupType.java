/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Paul J. Adams <paul.adams@siriusit.co.uk>
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

package eu.sqooss.service.db;

import java.util.HashMap;
import java.util.List;

import eu.sqooss.impl.service.CoreActivator;
import eu.sqooss.service.db.DAObject;

public class GroupType extends DAObject {
    private String type;

    public enum Type {

        USER, SYSTEM, DEFINITION;

        public static Type fromString(String s) {
            if (USER.toString().equals(s.toUpperCase())) {
                return Type.USER;
            } else if (SYSTEM.toString().equals(s.toUpperCase())) {
                return Type.SYSTEM;
            } else if (DEFINITION.toString().equals(s.toUpperCase())) {
                return Type.DEFINITION;
            } else {
                return null;
            }
        }
    }

    public GroupType() {
        // Nothing to do here
    }

    public GroupType(Type t) {
        type = t.toString();
    }

    public Type getEnumType() {
        return Type.fromString(type);
    }

    public String getType() {
        return type;
    }
    
    public void setEnumType(Type type) {
        this.type = type.toString();
    }

    public void setType(String s) {
        this.type = Type.fromString(s).toString();
    }
    
    /**
     * Get the corresponding DAO for the provided group type
     * 
     * @param type - the group type
     * 
     * @return A GroupType DAO representing the group type
     */
    public static GroupType getGroupType(Type type) {
        DBService db = CoreActivator.getDBService();
        if (db == null) return null;
        HashMap<String, Object> queryParameters = new HashMap<String, Object>(1);
        queryParameters.put("type", type.toString());
        List<GroupType> result = db.findObjectsByProperties(GroupType.class, queryParameters);
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
