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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import eu.sqooss.service.db.MetricType.Type;
import eu.sqooss.service.metricactivator.MetricActivator;

public class InvocationRule extends DAObject {
    private static String DEFAULT_SCOPE = "DEFAULT";
    private static String DEFAULT_ACTION = ActionType.EVAL.toString();

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
        RANGE,
        LIST;

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
            else if (scope.equals(LIST.toString()))
                return LIST;
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
     * Gets the rule that describes the default action (<i>policy</i>) of the
     * invocation rules chain. If such rule does not exist, then it is
     * silently created by this method and returned.
     * 
     * @param db the DB component's object
     * 
     * @return The default rule's <code>InvocationRule</code> DAO.
     */
    public static InvocationRule getDefaultRule(DBService db) {
        if (db == null) return null;
        HashMap<String,Object> properties = new HashMap<String, Object>();
        properties.put("scope", DEFAULT_SCOPE);
        List<?> objects = db.findObjectsByProperties(
                InvocationRule.class, properties);
        if ((objects != null) && (objects.size() > 0)) {
            return (InvocationRule) objects.get(0);
        }
        else {
            InvocationRule defaultRule = new InvocationRule();
            defaultRule.setScope(DEFAULT_SCOPE);
            defaultRule.setAction(DEFAULT_ACTION);
            // Check if there are any other rules in the chain
            // (Should happen only if the default rule were forcedly deleted)
            if (last(db) != null) {
                InvocationRule lastRule = last(db);
                defaultRule.setPrevRule(lastRule.getId());
                db.addRecord(defaultRule);
                lastRule.setNextRule(defaultRule.getId());
            }
            else
                db.addRecord(defaultRule);
            return(defaultRule);
        }
    }

    /**
     * Modifies the action (<i>policy</i>) of the default rule.
     * 
     * @param db the DB component's object
     * @param action the new action
     * 
     * @return <code>true</code>, if successfully modified,
     *   or <code>false</code> otherwise.
     */
    public static boolean setDefaultRule(DBService db, ActionType action) {
        if ((db == null) || (action == null)) return false;
        InvocationRule defaultRule = getDefaultRule(db);
        if (action.toString().equals(defaultRule.getAction()) == false)
            defaultRule.setScope(action.toString());
        return true;
    }

    /**
     * Returns the first rule in the invocation rules chain.
     * 
     * @param db the DB component's object
     * 
     * @return The <code>InvocationRule</code> DAO of the first rule in the
     *   chain, or <code>null</code> when the chain is empty or a database
     *   failure happened.
     */
    public static InvocationRule first(DBService db) {
        if (db == null) return null;
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
     * @param db the DB component's object
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
     * @param db the DB component's object
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
     * Returns the last rule in the invocation rules chain. During normal
     * operation the default rule will always be the last rule in the chain.
     * 
     * @param db the DB component's object
     * 
     * @return The <code>InvocationRule</code> DAO of the last rule in the
     *   chain, or <code>null</code> when the chain is empty or a database
     *   failure happened.
     */
    public static InvocationRule last(DBService db) {
        if (db == null) return null;
        List<?> objects =
            db.doHQL("from " + InvocationRule.class.getName()
                    + " where nextRule is null");
        if ((objects != null) && (objects.size() > 0)) {
            return (InvocationRule) objects.get(0);
        }
        return null;
    }

    /**
     * Deletes the given invocation rule. After a successful deletion,
     * both neighbor rules are modified so they reference each other instead.
     * <br/>
     * If the given rule had only one neighbor i.e. when the given rule was
     * the first rule in the chain, then that neighbor rule will be set as a
     * chain's first.
     * <br/>
     * <br/>
     * For any rule that is being modified (or deleted) by this method, a
     * notification is sent to the <code>MetricActivator</code> component,
     * so it can update its rules list (reload the modified rules).
     * 
     * @param db the DB component's object
     * @param ma the MetricActivator component's object
     * @param rule the rule that has to be removed
     * 
     * @return <code>true</code>, if the rule was successfully removed,
     *   or <code>false</code> otherwise.
     */
    public static boolean deleteInvocationRule (
            DBService db, MetricActivator ma, InvocationRule rule) {
        if (rule != null) {
            long ruleId = rule.getId();
            // Get the rule, that follow the selected one
            InvocationRule nextRule = null;
            if (rule.getNextRule() != null) {
                nextRule = db.findObjectById(
                        InvocationRule.class,
                        rule.getNextRule());
            }
            // Get the rule, that preceed the selected one
            InvocationRule prevRule = null;
            if (rule.getPrevRule() != null) {
                prevRule = db.findObjectById(
                        InvocationRule.class,
                        rule.getPrevRule());
            }
            // Remove the selected rule
            if (db.deleteRecord(rule)) {
                ma.reloadRule(ruleId);
                // Update the neighbor rules
                if ((prevRule != null) && (nextRule != null)) {
                    prevRule.setNextRule(nextRule.getId());
                    nextRule.setPrevRule(prevRule.getId());
                    ma.reloadRule(prevRule.getId());
                    ma.reloadRule(nextRule.getId());
                }
                else {
                    // Update the preceeding rule
                    if (prevRule != null) {
                        prevRule.setNextRule(null);
                        ma.reloadRule(prevRule.getId());
                    }
                    // Update the following rule
                    if (nextRule != null) {
                        nextRule.setPrevRule(null);
                        ma.reloadRule(nextRule.getId());
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Validates a singleton based scope against the given rule's value and
     * metric's type.
     * <br/>
     * The given metric's type determines the value content like:
     * <ul>
     *  <li><code>PROJECT_WIDE</code> and <code>SOURCE_CODE</code> expect a
     *  a single numeric project versions as a rule's value.
     * </ul>
     * 
     * @param value the rule's value
     * @param type the metric's type
     * 
     * @return <code>true</code> upon successful validation,
     *   or <code>false</code> otherwise.
     */
    private static boolean isSingleScope(String value, Type type) {
        if (value != null) {
            switch (type) {
            case PROJECT_WIDE:
            case SOURCE_CODE:
                try {
                    new Long(value); return true;
                }
                catch (NumberFormatException ex) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Validates a range based scope against the given rule's value and
     * metric's type.
     * <br/>
     * The given metric's type determines the value content like:
     * <ul>
     *  <li><code>PROJECT_WIDE</code> and <code>SOURCE_CODE</code> expect a
     *  hyphen ('-') separated range of numeric project versions
     *  (<i>exactly two</i>) as a rule's value.
     * </ul>
     * 
     * @param value the rule's value
     * @param type the metric's type
     * 
     * @return <code>true</code> upon successful validation,
     *   or <code>false</code> otherwise.
     */
    private static boolean isRangeScope(String value, Type type) {
        if (value != null) {
            switch (type) {
            case PROJECT_WIDE:
            case SOURCE_CODE:
                String[] values = value.split("-");
                if (values.length == 2) {
                    for (String nextVal : values) {
                        try {
                            new Long(nextVal);
                        }
                        catch (NumberFormatException ex) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Validates a list based scope against the given rule's value and
     * metric's type.
     * <br/>
     * The given metric's type determines the value content like:
     * <ul>
     *  <li><code>PROJECT_WIDE</code> and <code>SOURCE_CODE</code> expect a
     *  comma (',') separated list of numeric project versions
     *  (<i>at least two</i>) as a rule's value.
     * </ul>
     * 
     * @param value the rule's value
     * @param type the metric's type
     * 
     * @return <code>true</code> upon successful validation,
     *   or <code>false</code> otherwise.
     */
    private static boolean isListScope(String value, Type type) {
        if (value != null) {
            switch (type) {
            case PROJECT_WIDE:
            case SOURCE_CODE:
                String[] values = value.split(",");
                if (values.length > 1) {
                    for (String nextVal : values) {
                        try {
                            new Long(nextVal);
                        }
                        catch (NumberFormatException ex) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Validates the current rule.
     * 
     * @param db the DB component's object
     * 
     * @throws <code>Exception</code>, which describes the reason for the
     *   validation failure.
     */
    public void validate(DBService db) throws Exception {
        //====================================================================
        // Assemble the rule components
        //====================================================================
        // Assemble the metric type
        Type type = null;
        if (metricType != null) {
            type = Type.fromString(metricType.getType());
        }
        // Assemble the rule value
        String value = getValue();
        // Assemble the rule scope
        ScopeType scope = null;
        if (getScope() != null) {
            scope = ScopeType.fromString(getScope());
        }
        // Assemble the rule scope
        ActionType action = null;
        if (getAction() != null) {
            action = ActionType.fromString(getAction());
        }
        //====================================================================
        // Validate the rule's scope
        //====================================================================
        // Check for invalid ("null") action
        if (action == null) {
            throw new Exception("Invalid action type!");
        }
        // Skip the rest of the validation process when default rule
        if (getScope().equals(DEFAULT_SCOPE)) {
            return;
        }
        // Check for invalid ("null") scope 
        if (scope == null) {
            throw new Exception("Invalid scope type!");
        }
        // Check for selected scope without defined target metric's type
        if ((scope != ScopeType.ALL) && (type == null)) {
            throw new Exception("A scope is selected but a metric's type"
                    + " is not defined!");
        }
        // Check the value's content and format
        switch (scope) {
        case ALL:
            break;
        case EXACT:
        case EACH:
        case FROM:
        case TO:
            if (isSingleScope(value, type) == false) {
                throw new Exception("Invalid value for that scope!");
            }
            break;
        case RANGE:
            if (isRangeScope(value, type) == false) {
                throw new Exception(
                        "Invalid range of values for that scope!");
            }
            break;
        case LIST:
            if (isListScope(value, type) == false) {
                throw new Exception(
                        "Invalid list of values for that scope!");
            }
            break;
        default:
            throw new Exception("Unknown scope type!");
        }
        // Check for a duplicated rule
        InvocationRule cmpRule = first(db);
        while (cmpRule != null) {
            if (this.equals(cmpRule)) {
                throw new Exception("A rule with the same properties"
                        + " exist already!");
            }
            cmpRule = cmpRule.next(db);
        }
    }

    /**
     * Compares this rule to the given one. This method returns
     * <code>true</code> only if the given object is not <code>null</code>
     * and the following rule fields are equal:
     * <ul>
     *   <li> project - integer comparison by project Id
     *   <li> plug-in - integer comparison by plug-in Id
     *   <li> metric type - string comparison by metric's type
     *   <li> scope - string comparison by rule's scope
     *   <li> action - string comparison by rule's action
     *   <li> value - string comparison by rule's value
     * </ul>
     * 
     * @param rule the rule to compare
     * 
     * @return <code>true</code>, if equal, or <code>false</code> otherwise.
     */
    public boolean equals(InvocationRule rule) {
        // Check for a valid input parameter
        if (rule == null) {
            return false;
        }
        // Compare the project
        if ((rule.getProject() != null) && (getProject() != null)) {
            if (rule.getProject().getId() != getProject().getId()) {
                return false;
            }
        }
        else if ((rule.getProject() != null) || (getProject() != null)) {
            return false;
        }
        // Compare the plug-in
        if ((rule.getPlugin() != null) && (getPlugin() != null)) {
            if (rule.getPlugin().getId() != getPlugin().getId()) {
                return false;
            }
        }
        else if ((rule.getPlugin() != null) || (getPlugin() != null)) {
            return false;
        }
        // Compare the metric type
        if ((rule.getMetricType() != null) && (getMetricType() != null)) {
            if (rule.metricType.getType().equals(
                    getMetricType().getType()) != true) {
                return false;
            }
        }
        else if ((rule.getMetricType() != null)
                || (getMetricType() != null)) {
            return false;
        }
        // Compare the scope
        if ((rule.getScope() != null) && (getScope() != null)) {
            if (rule.getScope().equals(getScope()) != true) {
                return false;
            }
        }
        else if ((rule.getScope() != null) || (getScope() != null)) {
            return false;
        }
        // Compare the action
        if ((rule.getAction() != null) && (getAction() != null)) {
            if (rule.getAction().equals(getAction()) != true) {
                return false;
            }
        }
        else if ((rule.getAction() != null) || (getAction() != null)) {
            return false;
        }
        // Compare the value
        if ((rule.getValue() != null) && (getValue() != null)) {
            if (rule.getValue().equals(getValue()) != true) {
                return false;
            }
        }
        else if ((rule.getValue() != null) || (getValue() != null)) {
            return false;
        }
        return true;
    }

    /**
     * Copies the source rule into the target rule, while preserving the
     * the target rule's Id and location (the Ids of the previous and next
     * rule).
     * 
     * @param source the source rule
     * @param target the target rule
     */
    public static void copy(InvocationRule source, InvocationRule target) {
        target.setProject(source.getProject());
        target.setPlugin(source.getPlugin());
        target.setMetricType(source.getMetricType());
        target.setScope(source.getScope());
        target.setValue(source.getValue());
        target.setAction(source.getAction());
    }

    /**
     * Matches a project file's version against the given rule's scope and
     * rule's value.
     * <br/>
     * The rule's scope determines how the project file's version will be
     * compared, while the rule's value defines against which project
     * version(s) to perform that comparison.
     * <br/>
     * <br/>
     * Depending on the rule's scope the following logics are followed:
     * <ul>
     *   <li><b>ALL</b> always return a positive match
     *   <li><b>EXACT</b> returns a positive match only on project files that
     *     are older or equal to given project version, and were not deleted
     *     prior that project version.
     *   <li><b>EACH</b> EACH is like LIST (<i>see LIST</i>), but requires
     *     only a base project version <b>N</b> as an input value, while
     *     recalculating the list of each <b>N<sup>th</sup></b> versions on
     *     its own. The match is then performed against the accumulated list.
     *   <li><b>FROM</b> returns a positive match only on project files that
     *     were not deleted prior the given project version.
     *   <li><b>TO</b> returns a positive match only on project files that
     *     are older or equal to the given project version.
     *   <li><b>RANGE</b> returns a positive match only on project files
     *     that are older or equal to the newer project version and were not
     *     deleted prior the older project version in the given range.
     *   <li><b>LIST</b> returns a positive match only on project files that
     *     that are older or equal to at least one of the listed project
     *     versions and were not deleted prior that version.
     * </ul>
     *
     * <i>Note: this method doesn't distinguish between a file and folder, so
     * both of them can be successfully matched. It's up to the metrics, to
     * decide if they will evaluate that project file or not.</i>
     * <br/>
     * @param scp the rule's scope
     * @param val the rule's value (<i>one or more project versions</i>)
     * @param res the project file's DAO
     * 
     * @return <code>true</code>, if successfully matched,
     *   or <code>false<code> it there is no match.
     */
    public boolean match(ScopeType scp, String val, ProjectFile res) {
        // Always match on "ALL" scope
        if (scp == ScopeType.ALL) return true;

        // Get the project version where the given file was deleted (if any)
        Long deletionVersion = ProjectFile.getDeletionVersion(res);
        // Get the project version of the given file
        long fileVersion = res.getProjectVersion().getVersion();

        // Compare the rule value to the project version of the given file
        switch (scp) {
        case EXACT:
            // Get the project version from the scope value
            long exact = parseIntValue(val);
            // Match only files that are older or equal to the selected
            // project version and were not deleted prior that version.
            if (deletionVersion != null) {
                return ((fileVersion <= exact)
                        && (exact < deletionVersion.longValue()));
            }
            return (fileVersion <= exact);
        case EACH:
            // Retrieve the latest project version
            ProjectVersion lastPrjVer = StoredProject.getLastProjectVersion(
                    res.getProjectVersion().getProject());
            long eachBase = parseIntValue(val);
            long eachNext = eachBase;
            List<Long> eachList = new ArrayList<Long>();
            while (eachNext <= lastPrjVer.getVersion()) {
                eachList.add(eachNext);
                eachNext += eachBase;
            }
            for (long nextVer : eachList) {
                // Match only files that are older or equal to the listed
                // project version and were not deleted prior that version.
                if (deletionVersion != null) {
                    if ((fileVersion <= nextVer)
                            && (nextVer < deletionVersion.longValue())) {
                        return true;
                    }
                }
                if (fileVersion <= nextVer) return true;
            }
            return false;
        case FROM:
            // Get the project version from the scope value
            long fromVersion = parseIntValue(val);
            // Match only files that were not deleted prior the selected
            // project version.
            if (deletionVersion != null) {
                return (fromVersion < deletionVersion.longValue());
            }
            return true;
        case TO:
            // Get the project version from the scope value
            long uptoVersion = parseIntValue(val);
            // Match only files that are older or equal to the selected
            // project version.
            return (fileVersion <= uptoVersion);
        case RANGE:
            // Get the project version's range from the scope's value
            long[] range = parseIntRange(val);
            // Match only files that are older or equal to the newer project
            // version (the upper value in the range) and were not deleted
            // prior the older project version (the lower value in the range)
            if (deletionVersion != null) {
                if ((fileVersion <= range[1])
                        && (range[0] < deletionVersion.longValue())) {
                    return true;
                }
                return false;
            }
            if (fileVersion <= range[1])
                return true;
            else 
                return false;
        case LIST:
            // Get the list of project versions from the scope's value
            long[] values = parseIntList(val);
            for (int i = 0; i < values.length ; i++) {
                // Match only files that are older or equal to the listed
                // project version and were not deleted prior that version.
                if (deletionVersion != null) {
                    if ((fileVersion <= values[i])
                            && (values[i] < deletionVersion.longValue())) {
                        return true;
                    }
                }
                if (fileVersion <= values[i]) return true;
            }
            return false;
        }
        // Unrecognized scope types are dealt here
        return false;
    }

    /**
     * Converts the given rule's value into a single integer.
     * 
     * @param val the rule's value
     * 
     * @return The integer representation of the rule's value.
     */
    private long parseIntValue(String val) {
        return new Long(val).longValue();
    }

    /**
     * Converts the given rule's value into a two member array of integer
     * values, where the least indexed integer represents the range begin and
     * the top indexed - the range end.
     * 
     * @param val the rule's value
     * 
     * @return The pair of integers representing the rule's values range.
     */
    private long[] parseIntRange(String val) {
        long[] result = new long[2];
        String[] values = val.split("-");
        long n1 = new Long(values[0]);
        long n2 = new Long(values[1]);
        if (n2 > n1) {
            result[0] = n1; result[1] = n2;
        }
        else {
            result[0] = n2; result[1] = n1;
        }
        return result;
    }

    /**
     * Converts the given rule's value into a list of integer values.
     * 
     * @param val the rule's value
     * 
     * @return The list of integers representing the rule's values set.
     */
    private long[] parseIntList(String val) {
        String[] values = val.split(",");
        long[] result = new long[values.length];
        for (int i = 0; i < values.length ; i++) {
            result[i] = new Long(values[i]);
        }
        return result;
    }
}
