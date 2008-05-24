/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

public class InvocationRule extends DAObject {
    private Long prevRule = null;
    private Long nextRule = null;
    private String scope = null;
    private String value = null;
    private String action = null;
    private StoredProject project = null;
    private Plugin plugin = null;
    private MetricType metricType = null;

    public enum ActionType {
        EVAL,
        SKIP;

        public static ActionType fromString(String action) {
            if (action.equals(EVAL.toString()))
                return EVAL;
            else if (action.equals(SKIP.toString()))
                return SKIP;
            else
                return null;
        }
    };

    public enum ScopeType {
        ALL,
        EXACT,
        EACH,
        FROM,
        TO,
        RANGE;

        public static ScopeType fromString(String scope) {
            if (scope.equals(ALL.toString()))
                return ALL;
            if (scope.equals(EXACT.toString()))
                return EXACT;
            else if (scope.equals(EACH.toString()))
                return EACH;
            else if (scope.equals(FROM.toString()))
                return FROM;
            else if (scope.equals(TO.toString()))
                return TO;
            else if (scope.equals(RANGE.toString()))
                return RANGE;
            else
                return null;
        }
    };

    public Long getPrevRule() {
        return prevRule;
    }

    public void setPrevRule(Long ruleId) {
        this.prevRule = ruleId;
    }

    public Long getNextRule() {
        return nextRule;
    }

    public void setNextRule(Long ruleId) {
        this.nextRule = ruleId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public StoredProject getProject() {
        return project;
    }

    public void setProject(StoredProject project) {
        this.project = project;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    /**
     * Returns the first rule in the invocation rules chain.
     * 
     * @param db the DB components object
     * 
     * @return The <code>InvocationRule</code> DAO of the first rule in the
     *   chain, or <code>null</code> when the chain is empty or a database
     *   failure happened.
     */
    public static InvocationRule first(DBService db) {
        if (db == null) return null;
        HashMap<String,Object> properties = new HashMap<String, Object>();
        properties.put("prevRule", null);
        List<?> objects =
            db.doHQL("from " + InvocationRule.class.getName()
                    + " where prevRule is null");
        if ((objects != null) && (objects.size() > 0)) {
            return (InvocationRule) objects.get(0);
        }
        return null;
    }

    /**
     * Returns the rule that precedes the current one in the invocation rules
     * chain.
     * 
     * @param db the DB components object
     * 
     * @return The <code>InvocationRule</code> DAO of the previous rule in the
     *   chain. Or <code>null</code> when the chain is empty, when this is the
     *   first rule, or if a database failure happened.
     */
    public InvocationRule prev(DBService db) {
        if (db == null) return null;
        if (getPrevRule() != null) {
            return db.findObjectById(InvocationRule.class, getPrevRule());
        }
        return null;
    }

    /**
     * Returns the rule that follows the current one in the invocation rules
     * chain.
     * 
     * @param db the DB components object
     * 
     * @return The <code>InvocationRule</code> DAO of the next rule in the
     *   chain. Or <code>null</code> when the chain is empty, when this is the
     *   last rule, or if a database failure happened.
     */
    public InvocationRule next(DBService db) {
        if (db == null) return null;
        if (getNextRule() != null) {
            return db.findObjectById(InvocationRule.class, getNextRule());
        }
        return null;
    }

    /**
     * Returns the last rule in the invocation rules chain.
     * 
     * @param db the DB components object
     * 
     * @return The <code>InvocationRule</code> DAO of the last rule in the
     *   chain, or <code>null</code> when the chain is empty or a database
     *   failure happened.
     */
    public static InvocationRule last(DBService db) {
        if (db == null) return null;
        HashMap<String,Object> properties = new HashMap<String, Object>();
        properties.put("nextRule", null);
        List<?> objects =
            db.doHQL("from " + InvocationRule.class.getName()
                    + " where nextRule is null");
        if ((objects != null) && (objects.size() > 0)) {
            return (InvocationRule) objects.get(0);
        }
        return null;
    }
}
