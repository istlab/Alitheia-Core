/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 *
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
package eu.sqooss.impl.service.webadmin;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.db.InvocationRule;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.InvocationRule.ActionType;
import eu.sqooss.service.db.InvocationRule.ScopeType;

public class RulesView extends AbstractView{

    public RulesView(BundleContext bundlecontext, VelocityContext vc) {
        super(bundlecontext, vc);
    }

    public static String render(HttpServletRequest req) {
        // Stores the assembled HTML content
        StringBuilder b = new StringBuilder("\n");
        // Stores the accumulated error messages
        StringBuilder e = new StringBuilder();
        // Indentation spacer
        long in = 6;

        // Create a DB session
        sobjDB.startDBSession();

        // Request parameters
        String reqParAction        = "action";
        String reqParSelRuleId     = "selectedRuleId";
        String reqParRuleScope     = "ruleScope";
        String reqParRuleAction    = "ruleAction";
        String reqParRuleValue     = "ruleValue";
        String reqParRuleProject   = "projectId";
        String reqParRulePlugin    = "pluginId";
        String reqParRuleMetric    = "metricTypeId";
        // Recognized "action" parameter's values
        String actValReqAddRule    = "createRule";
        String actValReqUpdRule    = "updateRule";
        String actValConAddRule    = "confirmRule";
        String actValConRemRule    = "removeRule";
        String actValConRuleUp     = "upOrderRule";
        String actValConRuleDown   = "downOrderRule";
        // Request values
        String reqValAction        = "";
        Long   reqValSelRuleId     = null;
        ScopeType reqValRuleScope  = null;
        ActionType reqValRuleAction= null;
        String reqValRuleValue     = null;
        Long reqValRuleProject     = null;
        Long reqValRulePlugin      = null;
        Long reqValRuleMetric      = null;
        // Static parameters
        final String conSubmitForm = "document.rules.submit();";

        // ===================================================================
        // Parse the servlet's request object
        // ===================================================================
        if (req != null) {
            // DEBUG: Dump the servlet's request parameter
            if (DEBUG) {
                b.append(debugRequest(req));
            }

            // Retrieve the selected editor's action (if any)
            reqValAction = req.getParameter(reqParAction);
            if (reqValAction == null) {
                reqValAction = "";
            };
            // Retrieve the selected rule's Id (if any)
            reqValSelRuleId = fromString(req.getParameter(reqParSelRuleId));
            // Retrieve the parameters of the created/updated rule
            reqValRuleProject = fromString(req.getParameter(reqParRuleProject));
            reqValRulePlugin = fromString(req.getParameter(reqParRulePlugin));
            reqValRuleMetric = fromString(req.getParameter(reqParRuleMetric));
            if (req.getParameter(reqParRuleScope) != null) {
                reqValRuleScope = ScopeType.fromString(
                        req.getParameter(reqParRuleScope));
            }
            if (req.getParameter(reqParRuleAction) != null) {
                reqValRuleAction = ActionType.fromString(
                        req.getParameter(reqParRuleAction));
            }
            if ((req.getParameter(reqParRuleValue) != null)
                    && req.getParameter(reqParRuleValue).length() > 0) {
                reqValRuleValue = req.getParameter(reqParRuleValue);
            }
            // Create/update rule's confirmation
            if (reqValAction.equals(actValConAddRule)) {
                boolean isValid = true;
                if (reqValRuleScope == null) {
                    e.append("Indefined rule scope!");
                    isValid = false;
                }
                if (reqValRuleAction == null) {
                    e.append("Indefined rule action!");
                    isValid = false;
                }
                if ((reqValRuleValue == null) && (
                        ((reqValRuleScope == null) 
                                && (reqValRuleScope != ScopeType.ALL)))) {
                    e.append("Indefined rule value!");
                    isValid = false;
                }
                if (isValid) {
                    InvocationRule rule = new InvocationRule();
                    if (reqValRuleProject != null) {
                        rule.setProject(sobjDB.findObjectById(
                                StoredProject.class, reqValRuleProject));
                    }
                    if (reqValRulePlugin != null) {
                        rule.setPlugin(sobjDB.findObjectById(
                                Plugin.class, reqValRulePlugin));
                    }
                    if (reqValRuleMetric != null) {
                        rule.setMetricType(sobjDB.findObjectById(
                                MetricType.class, reqValRuleMetric));
                    }
                    rule.setScope(reqValRuleScope.toString());
                    rule.setAction(reqValRuleAction.toString());
                    rule.setValue(reqValRuleValue);
                    // Get the rule, that will be preceeded by the new one
                    InvocationRule nextRule = null;
                    // Rule insert
                    if (reqValSelRuleId != null) {
                        nextRule = sobjDB.findObjectById(
                                InvocationRule.class, reqValSelRuleId);
                        if (nextRule != null) {
                            rule.setNextRule(nextRule.getId());
                        }
                    }
                    // Rule append
                    else {
                        if (InvocationRule.last(sobjDB) != null) {
                            rule.setPrevRule(
                                    InvocationRule.last(sobjDB).getId());
                        }
                    }
                    // Get the rule, that will be followed by the new one
                    InvocationRule prevRule = null;
                    // Rule insert
                    if ((reqValSelRuleId != null) && (nextRule != null)) {
                        if (nextRule.getPrevRule() != null) {
                            prevRule = sobjDB.findObjectById(
                                    InvocationRule.class,
                                    nextRule.getPrevRule());
                            if (prevRule != null) {
                                rule.setPrevRule(prevRule.getId());
                            }
                        }
                    }
                    // Rule append
                    else {
                        prevRule = InvocationRule.last(sobjDB);
                    }
                    // Validate and create the new rule
                    try {
                        rule.validate(sobjDB);
                        // Check for a duplicated rule
                        InvocationRule cmpRule = InvocationRule.first(sobjDB);
                        while (cmpRule != null) {
                            if (rule.equals(cmpRule)) {
                                throw new Exception(
                                        "The same rule already exist!");
                            }
                            cmpRule = cmpRule.next(sobjDB);
                        }
                        // Try to create the new rule
                        if (sobjDB.addRecord(rule)) {
                            sobjMetricActivator.reloadRule(rule.getId());
                            reqValSelRuleId = null;
                            // Update the previous rule
                            if (prevRule != null) {
                                prevRule.setNextRule(rule.getId());
                                sobjMetricActivator.reloadRule(prevRule.getId());
                            }
                            // Update the following rule
                            if (nextRule != null) {
                                nextRule.setPrevRule(rule.getId());
                                sobjMetricActivator.reloadRule(nextRule.getId());
                            }
                        }
                        else {
                            e.append("Rule creation"
                                    + " has failed!"
                                    + " Check log for details.");
                        }
                    }
                    catch (Exception ex) {
                        e.append(ex.getMessage());
                    }
                }
                if (e.toString().length() > 0) {
                    reqValAction = actValReqAddRule;
                }
            }
            // Remove rule's confirmation
            if (reqValAction.equals(actValConRemRule)) {
                if (reqValSelRuleId != null) {
                    InvocationRule selRule = sobjDB.findObjectById(
                            InvocationRule.class, reqValSelRuleId);
                    if (selRule != null) {
                        // Get the rule, that follow the selected one
                        InvocationRule nextRule = null;
                        if (selRule.getNextRule() != null) {
                            nextRule = sobjDB.findObjectById(
                                    InvocationRule.class,
                                    selRule.getNextRule());
                        }
                        // Get the rule, that preceed the selected one
                        InvocationRule prevRule = null;
                        if (selRule.getPrevRule() != null) {
                            prevRule = sobjDB.findObjectById(
                                    InvocationRule.class,
                                    selRule.getPrevRule());
                        }
                        // Remove the selected rule
                        if (sobjDB.deleteRecord(selRule)) {
                            sobjMetricActivator.reloadRule(selRule.getId());
                            // Update the neighbor rules
                            if ((prevRule != null) && (nextRule != null)) {
                                prevRule.setNextRule(nextRule.getId());
                                nextRule.setPrevRule(prevRule.getId());
                                sobjMetricActivator.reloadRule(prevRule.getId());
                                sobjMetricActivator.reloadRule(nextRule.getId());
                            }
                            else {
                                // Update the preceeding rule
                                if (prevRule != null) {
                                    prevRule.setNextRule(null);
                                    sobjMetricActivator.reloadRule(prevRule.getId());
                                }
                                // Update the following rule
                                if (nextRule != null) {
                                    nextRule.setPrevRule(null);
                                    sobjMetricActivator.reloadRule(nextRule.getId());
                                }
                            }
                        }
                        else {
                            e.append("Rule deletion"
                                    + " has failed!"
                                    + " Check log for details.");
                        }
                    }
                    else {
                        e.append("The selected rule can not be found in the"
                                + "database!");
                    }
                }
                else {
                    e.append("You must select a rule first!");
                }
                if (e.toString().length() > 0) {
                    reqValAction = "";
                }
            }
        }

        // ===============================================================
        // Create the form
        // ===============================================================
        b.append(sp(in++) + "<form id=\"rules\""
                + " name=\"rules\""
                + " method=\"post\""
                + " action=\"/rules\">\n");

        // ===============================================================
        // Display the accumulated error messages (if any)
        // ===============================================================
        b.append(errorFieldset(e, in));

        // ===============================================================
        // "New user" editor
        // ===============================================================
        if (reqValAction.equals(actValReqAddRule)) {
            b.append(sp(in++) + "<fieldset>\n");
            b.append(sp(in) + "<legend>New invocation rule" + "</legend>\n");
            b.append(sp(in++) + "<table class=\"borderless\">\n");
            // Project selection
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in)
                    + "<td class=\"borderless\" style=\"width:100px;\">"
                    + "<b>Project</b>"
                    + "</td>\n"
                    + sp(in)
                    + "<td class=\"borderless\">\n"
                    + sp(++in)
                    + "<select class=\"form\""
                    + " id=\"" + reqParRuleProject + "\""
                    + " name=\"" + reqParRuleProject + "\">\n");
            // Retrieve the list of all stored projects
            List<StoredProject> allProjects =
                sobjDB.findObjectsByProperties(
                        StoredProject.class, new HashMap<String, Object>());
            // "Any project" option
            b.append(sp(++in) + "<option"
                    + " value=\"ANY\""
                    + " selected>"
                    + "ANY"
                    + "</option>\n");
            if ((allProjects != null) && (allProjects.isEmpty() == false)) {
                for (StoredProject nextProject : allProjects) {
                    b.append(sp(in) + "<option"
                            + " value=\"" + nextProject.getId() + "\""
                            + ">"
                            + nextProject.getName()
                            + "</option>\n");
                }
            }
            b.append(sp(--in) + "</select>\n"
                    + sp(--in) + "</td>\n"
                    + sp(--in) + "</tr>\n");
            // Plug-in selection
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in)
                    + "<td class=\"borderless\" style=\"width:100px;\">"
                    + "<b>Plug-in</b>"
                    + "</td>\n"
                    + sp(in)
                    + "<td class=\"borderless\">\n"
                    + sp(++in)
                    + "<select class=\"form\""
                    + " id=\"" + reqParRulePlugin + "\""
                    + " name=\"" + reqParRulePlugin + "\">\n");
            // Retrieve the list of all installed plug-ins
            List<Plugin> allPlugins =
                sobjDB.findObjectsByProperties(
                        Plugin.class, new HashMap<String, Object>());
            // "Any plug-in" option
            b.append(sp(++in) + "<option"
                    + " value=\"ANY\""
                    + " selected>"
                    + "ANY"
                    + "</option>\n");
            if ((allPlugins != null) && (allPlugins.isEmpty() == false)) {
                for (Plugin nextPlugin : allPlugins) {
                    b.append(sp(in) + "<option"
                            + " value=\"" + nextPlugin.getId() + "\""
                            + ">"
                            + nextPlugin.getName()
                            + "</option>\n");
                }
            }
            b.append(sp(--in) + "</select>\n"
                    + sp(--in) + "</td>\n"
                    + sp(--in) + "</tr>\n");
            // Metric type selection
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in)
                    + "<td class=\"borderless\" style=\"width:100px;\">"
                    + "<b>Metric Type</b>"
                    + "</td>\n"
                    + sp(in)
                    + "<td class=\"borderless\">\n"
                    + sp(++in)
                    + "<select class=\"form\""
                    + " id=\"" + reqParRuleMetric + "\""
                    + " name=\"" + reqParRuleMetric + "\">\n");
            // Retrieve the list of all registered metric types
            List<MetricType> allTypes =
                sobjDB.findObjectsByProperties(
                        MetricType.class, new HashMap<String, Object>());
            // "Any metric type" option
            b.append(sp(++in) + "<option"
                    + " value=\"ANY\""
                    + " selected>"
                    + "ANY"
                    + "</option>\n");
            if ((allTypes != null) && (allTypes.isEmpty() == false)) {
                for (MetricType nextType : allTypes) {
                    b.append(sp(in) + "<option"
                            + " value=\"" + nextType.getId() + "\""
                            + ">"
                            + nextType.getType()
                            + "</option>\n");
                }
            }
            b.append(sp(--in) + "</select>"
                    + sp(--in) + "</td>\n"
                    + sp(--in) + "</tr>\n");
            // Rule scope selection
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in)
                    + "<td class=\"borderless\" style=\"width:100px;\">"
                    + "<b>Scope</b>"
                    + "</td>\n"
                    + sp(in)
                    + "<td class=\"borderless\">\n"
                    + sp(++in)
                    + "<select class=\"form\""
                    + " id=\"" + reqParRuleScope + "\""
                    + " name=\"" + reqParRuleScope + "\">\n");
            // Generate a list of all scopes
            in++;
            for (ScopeType scope : ScopeType.values()) {
                b.append(sp(in) + "<option"
                        + " value=\"" + scope.name() + "\""
                        + ">"
                        + scope.toString()
                        + "</option>\n");
            }
            b.append(sp(--in) + "</select>"
                    + sp(--in) + "</td>\n"
                    + sp(--in) + "</tr>\n");
            // Rule action selection
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in)
                    + "<td class=\"borderless\" style=\"width:100px;\">"
                    + "<b>Action</b>"
                    + "</td>\n"
                    + sp(in)
                    + "<td class=\"borderless\">\n"
                    + sp(++in)
                    + "<select class=\"form\""
                    + " id=\"" + reqParRuleAction + "\""
                    + " name=\"" + reqParRuleAction + "\">\n");
            // Generate a list of all actions
            in++;
            for (ActionType action : ActionType.values()) {
                b.append(sp(in) + "<option"
                        + " value=\"" + action.name() + "\""
                        + ">"
                        + action.toString()
                        + "</option>\n");
            }
            b.append(sp(--in) + "</select>"
                    + sp(--in) + "</td>\n"
                    + sp(--in) + "</tr>\n");
            // Rule value input
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in)
                    + "<td class=\"borderless\" style=\"width:100px;\">"
                    + "<b>Value</b>"
                    + "</td>\n");
            b.append(sp(in)+ "<td class=\"borderless\">\n"
                    + sp(++in)
                    + "<input type=\"text\""
                    + " class=\"form\""
                    + " id=\"" + reqParRuleValue + "\""
                    + " name=\"" + reqParRuleValue + "\""
                    + " value=\"\">"
                    + "</td>\n");
            b.append(sp(--in) + "</td>\n"
                    + sp(--in) + "</tr>\n");
            // Toolbar
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in++) + "<td colspan=\"2\" class=\"borderless\">\n");
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"Create\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + reqParAction + "').value='"
                    + actValConAddRule + "';"
                    + conSubmitForm + "\">\n");
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"Cancel\""
                    + " onclick=\"javascript:"
                    + conSubmitForm + "\">\n");
            b.append(sp(--in) + "</td>\n");
            b.append(sp(--in) + "</tr>\n");
            b.append(sp(--in) + "</table>\n");
            b.append(sp(--in) + "</fieldset>\n");
        }
        else {
            // Create the field-set
            b.append(sp(in) + "<fieldset>\n");
            b.append(sp(++in) + "<legend>All invocation rules</legend>\n");
            // Retrieve information for all invocation rules

            // Create the header row
            b.append(sp(in) + "<table>\n");
            b.append(sp(++in) + "<thead>\n");
            b.append(sp(++in) + "<tr class=\"head\">\n");
            b.append(sp(++in) + "<td class=\"head\""
                    + " style=\"width: 20%;\">"
                    + "Project</td>\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 20%;\">"
                    + "Plug-in</td>\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 20%;\">"
                    + "Metric Type</td>\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 5%;\">"
                    + "Scope</td>\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 20%;\">"
                    + "Value</td>\n");
            b.append(sp(in) + "<td class=\"head\""
                    + " style=\"width: 5%;\">"
                    + "Action</td>\n");
            b.append(sp(--in) + "</tr>\n");
            b.append(sp(--in) + "</thead>\n");
            b.append(sp(in++) + "<tbody>\n");
            // Create the content row
            List<InvocationRule> rules = sobjDB.findObjectsByProperties(
                    InvocationRule.class, new HashMap<String, Object>());
            if ((rules == null) || (rules.isEmpty())) {
                b.append(sp(in++) + "<tr>\n");
                b.append(sp(in++) + "<td colspan=\"6\" class=\"noattr\">\n");
                b.append(sp(in) + "There are no defined invocation rules.\n");
                b.append(sp(--in) + "</td>\n");
                b.append(sp(--in) + "</tr>\n");
            }
            else {
                InvocationRule rule = InvocationRule.first(sobjDB);
                while (rule != null) {
                    if ((reqValSelRuleId != null)
                            && (reqValSelRuleId == rule.getId())) {
                        b.append(sp(in++) + "<tr class=\"selected\">\n");
                    }
                    else {
                        b.append(sp(in++) + "<tr class=\"edit\""
                                + " onclick=\"javascript:"
                                + "document.getElementById('"
                                + reqParSelRuleId + "').value='"
                                + rule.getId() + "';"
                                + conSubmitForm + "\">\n");
                    }
                    // Project name
                    b.append(sp(in++) + "<td class=\"trans\">"
                            + ((rule.getProject() == null) ? "<b>ANY</b>"
                                    : rule.getProject().getName())
                            + "</td>\n");
                    // Plug-in name
                    b.append(sp(in) + "<td class=\"trans\">"
                            + ((rule.getPlugin() == null) ? "<b>ANY</b>"
                                    : rule.getPlugin().getName())
                            + "</td>\n");
                    // Metric type
                    b.append(sp(in) + "<td class=\"trans\">"
                            + ((rule.getMetricType() == null) ? "<b>ANY</b>"
                                    : rule.getMetricType().getType())
                            + "</td>\n");
                    // Rule scope
                    b.append(sp(in) + "<td class=\"trans\">"
                            + rule.getScope()
                            + "</td>\n");
                    // Rule value
                    b.append(sp(in) + "<td class=\"trans\">"
                            + ((rule.getValue() == null) ? "<b>NONE</b>"
                                    : rule.getValue())
                            + "</td>\n");
                    // Rule action
                    b.append(sp(in) + "<td class=\"trans\">"
                            + rule.getAction()
                            + "</td>\n");
                    rule = rule.next(sobjDB);
                    b.append(sp(--in) + "</tr>\n");
                }
            }
            // Command tool-bar
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in++) + "<td colspan=\"6\">\n");
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\"Append rule\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + reqParAction + "').value='"
                    + actValReqAddRule + "';"
                    + "document.getElementById('"
                    + reqParSelRuleId + "').value='';"
                    + conSubmitForm + "\">\n");
            if (reqValSelRuleId != null) {
                b.append(sp(in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Insert rule\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValReqAddRule + "';"
                        + conSubmitForm + "\">\n");
                InvocationRule rule = sobjDB.findObjectById(
                        InvocationRule.class, reqValSelRuleId);
                if ((rule != null) && (rule.prev(sobjDB) != null)) {
                    b.append(sp(in) + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"Move up\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValConRuleUp + "';"
                            + conSubmitForm + "\">\n");
                }
                if ((rule != null) && (rule.next(sobjDB) != null)) {
                    b.append(sp(in) + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"Move down\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValConRuleDown + "';"
                            + conSubmitForm + "\">\n");
                }
                b.append(sp(in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Delete rule\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValConRemRule + "';"
                        + conSubmitForm + "\">\n");
            }
            b.append(sp(--in) + "</td>\n");
            b.append(sp(--in) + "</tr>\n");
            // Close the table
            b.append(sp(--in) + "</tbody>\n");
            b.append(sp(--in) + "</table>\n");
            // Close the field-set
            b.append(sp(--in) + "</fieldset>\n");
        }

        // ===============================================================
        // INPUT FIELDS
        // ===============================================================
        // "Action type" input field
        b.append(sp(in) + "<input type=\"hidden\""
                + " id=\"" + reqParAction + "\""
                + " name=\"" + reqParAction + "\""
                + " value=\"\">\n");
        // "Selected plug-in's hash code" input field
        b.append(sp(in) + "<input type=\"hidden\""
                + " id=\"" + reqParSelRuleId + "\""
                + " name=\"" + reqParSelRuleId + "\""
                + " value=\""
                + ((reqValSelRuleId != null) ? reqValSelRuleId : "")
                + "\">\n");
        // "Configuration attribute's name" input field
        b.append(sp(in) + "<input type=\"hidden\""
                + " id=\"" + reqParRuleScope + "\""
                + " name=\"" + reqParRuleScope + "\""
                + " value=\""
                + ((reqValRuleScope != null) ? reqValRuleScope : "")
                + "\">\n");
        // "Configuration attribute's type" input field
        b.append(sp(in) + "<input type=\"hidden\""
                + " id=\"" + reqParRuleAction + "\""
                + " name=\"" + reqParRuleAction + "\""
                + " value=\""
                + ((reqValRuleAction != null) ? reqValRuleAction : "")
                + "\">\n");
        // "Configuration attribute's value" input field
        b.append(sp(in) + "<input type=\"hidden\""
                + " id=\"" + reqParRuleValue + "\""
                + " name=\"" + reqParRuleValue + "\""
                + " value=\""
                + ((reqValRuleValue != null) ? reqValRuleValue : "")
                + "\">\n");

        // ===============================================================
        // Close the form
        // ===============================================================
        b.append(sp(--in) + "</form>\n");

        // Close the DB session
        sobjDB.commitDBSession();

        return b.toString();
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
