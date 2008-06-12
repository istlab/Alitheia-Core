/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.metrics.clmt.db;

import java.util.HashMap;
import java.util.List;

import eu.sqooss.impl.service.CoreActivator;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;

public class CodeConstructType extends DAObject {
    
    public enum ConstructType {
        
        CLASS, STATEMENT, METHOD, ATTRIBUTE;
            
        public static ConstructType fromString(String s) {
            if ("CLASS".equalsIgnoreCase(s))
                return ConstructType.CLASS;
            else if ("STATEMENT".equalsIgnoreCase(s))
                return ConstructType.STATEMENT;
            else if ("METHOD".equalsIgnoreCase(s))
                return ConstructType.METHOD;
            else if ("ATTRIBUTE".equalsIgnoreCase(s))
                return ConstructType.ATTRIBUTE;
            else
                return null;
        }
    }
    
    private String type;
   
    public CodeConstructType() {
    }
    
    public CodeConstructType(ConstructType t) {
        type = t.toString();
    }

    public ConstructType getConstructType() {
        return ConstructType.fromString(type);
    }
    
    public String getType(){
        return type;
    }
    
    public void setType(String s) {
        this.type = ConstructType.fromString(s).toString();
    }
    
    public static CodeConstructType getConstructType(ConstructType t) {
        DBService db = CoreActivator.getDBService();
        HashMap<String, Object> s = new HashMap<String, Object>();
        s.put("type", t.toString());
        List<CodeConstructType> result = 
            db.findObjectsByProperties(CodeConstructType.class, s);
        if (result.isEmpty()) {
            CodeConstructType cct = new CodeConstructType(t);
            db.addRecord(cct);
            return cct;
        }
        else {
            return (CodeConstructType) result.get(0);
        }
    }
}
