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
        String actValConAddRule    = "confirmCreate";
        String actValConUpdRule    = "confirmUpdate";
        String actValConRemRule    = "removeRule";
        String actValConRuleUp     = "upOrderRule";
        String actValConRuleDown   = "downOrderRule";
        // Request values
        String reqValAction        = "";
        Long   reqValSelRuleId     = null;
        String reqValRuleScope     = null;
        String reqValRuleAction    = null;
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
            // ===============================================================
            // DEBUG: Dump all request parameter
            // ===============================================================
            if (DEBUG) {
                b.append(debugRequest(req));
            }

            // ===============================================================
            // Retrieve all request parameters
            // ===============================================================
            // Selected editor's action (if any)
            reqValAction = req.getParameter(reqParAction);
            if (reqValAction == null) {
                reqValAction = "";
            };
            // Selected rule's Id (if any)
            reqValSelRuleId = fromString(req.getParameter(reqParSelRuleId));
            // Parameters of the created/updated rule
            reqValRuleProject = fromString(req.getParameter(reqParRuleProject));
            reqValRulePlugin = fromString(req.getParameter(reqParRulePlugin));
            reqValRuleMetric = fromString(req.getParameter(reqParRuleMetric));
            reqValRuleScope = req.getParameter(reqParRuleScope);
            reqValRuleAction = req.getParameter(reqParRuleAction);
            if ((req.getParameter(reqParRuleValue) != null)
                    && (req.getParameter(reqParRuleValue).length() > 0)) {
                reqValRuleValue = req.getParameter(reqParRuleValue);
            }
            // ===============================================================
            // Create/update rule's confirmation
            // ===============================================================
            if ((reqValAction.equals(actValConAddRule))
                    || (reqValAction.equals(actValConUpdRule))) {
                // Check if this is the default rule
                boolean isDefaultRule = false;
                if ((reqValSelRuleId != null)
                        && (InvocationRule.getDefaultRule(sobjDB).getId()
                                == reqValSelRuleId.longValue())) {
                    isDefaultRule = true;
                }
                //------------------------------------------------------------
                // Fill the rule fields
                //------------------------------------------------------------
                InvocationRule rule = new InvocationRule();
                if (rule != null) {
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
                    rule.setScope(reqValRuleScope);
                    rule.setValue(reqValRuleValue);
                    rule.setAction(reqValRuleAction);
                }
                //------------------------------------------------------------
                // Create a new rule
                //------------------------------------------------------------
                if (reqValAction.equals(actValConAddRule)) {
                    // Get the rule, that will be preceeded by the new one
                    InvocationRule nextRule = null;
                    if (reqValSelRuleId != null) {
                        nextRule = sobjDB.findObjectById(
                                InvocationRule.class, reqValSelRuleId);
                    }
                    if (nextRule == null) {
                        nextRule = InvocationRule.getDefaultRule(sobjDB);
                    }
                    rule.setNextRule(nextRule.getId());
                    // Get the rule, that will be followed by the new one
                    InvocationRule prevRule = null;
                    if (nextRule.getPrevRule() != null) {
                        prevRule = sobjDB.findObjectById(
                                InvocationRule.class,
                                nextRule.getPrevRule());
                        if (prevRule != null) {
                            rule.setPrevRule(prevRule.getId());
                        }
                    }
                    // Validate and create the new rule
                    try {
                        rule.validate(sobjDB);
                        // Try to create the new rule
                        if (sobjDB.addRecord(rule)) {
                            compMA.reloadRule(rule.getId());
                            reqValSelRuleId = null;
                            // Update the previous rule
                            if (prevRule != null) {
                                prevRule.setNextRule(rule.getId());
                                compMA.reloadRule(
                                        prevRule.getId());
                            }
                            // Update the following rule
                            if (nextRule != null) {
                                nextRule.setPrevRule(rule.getId());
                                compMA.reloadRule(
                                        nextRule.getId());
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
                //------------------------------------------------------------
                // Update an existing rule
                //------------------------------------------------------------
                else {
                    // Retrieve the affected rule's DAO
                    InvocationRule updRule = sobjDB.findObjectById(
                            InvocationRule.class, reqValSelRuleId);
                    if (updRule != null) { 
                        try {
                            // Validate the rule
                            rule.validate(sobjDB);
                            // Modify the affected rule
                            InvocationRule.copy(rule, updRule);
                            compMA.reloadRule(updRule.getId());
                        }
                        catch (Exception ex) {
                            e.append(ex.getMessage());
                        }
                    }
                    else {
                        e.append("The selected rule was not found in the"
                                + " database!"
                                + " Check log for details.");
                    }
                }
                //------------------------------------------------------------
                // Instruct the view to show the editor again upon errors
                //------------------------------------------------------------
                if (e.toString().length() > 0) {
                    if (reqValAction.equals(actValConAddRule))
                        reqValAction = actValReqAddRule;
                    else
                        reqValAction = actValReqUpdRule;
                }
            }
            // ===============================================================
            // Remove rule's confirmation
            // ===============================================================
            if (reqValAction.equals(actValConRemRule)) {
                if ((reqValSelRuleId != null)
                        && (InvocationRule.getDefaultRule(sobjDB).getId()
                                != reqValSelRuleId)){
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
                            compMA.reloadRule(reqValSelRuleId);
                            reqValSelRuleId = null;
                            // Update the neighbor rules
                            if ((prevRule != null) && (nextRule != null)) {
                                prevRule.setNextRule(nextRule.getId());
                                nextRule.setPrevRule(prevRule.getId());
                                compMA.reloadRule(prevRule.getId());
                                compMA.reloadRule(nextRule.getId());
                            }
                            else {
                                // Update the preceeding rule
                                if (prevRule != null) {
                                    prevRule.setNextRule(null);
                                    compMA.reloadRule(prevRule.getId());
                                }
                                // Update the following rule
                                if (nextRule != null) {
                                    nextRule.setPrevRule(null);
                                    compMA.reloadRule(nextRule.getId());
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
            // ===============================================================
            // Move rule's confirmation
            // ===============================================================
            if ((reqValAction.equals(actValConRuleUp))
                    || (reqValAction.equals(actValConRuleDown))) {
                if ((reqValSelRuleId != null)
                        && (InvocationRule.getDefaultRule(sobjDB).getId()
                                != reqValSelRuleId)){
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
                        // Move the selected rule up
                        if (reqValAction.equals(actValConRuleUp)) {
                            // Update the affected rule
                            selRule.setPrevRule(prevRule.getPrevRule());
                            selRule.setNextRule(prevRule.getId());
                            compMA.reloadRule(selRule.getId());
                            // Update the old neighbor rules
                            prevRule.setPrevRule(selRule.getId());
                            prevRule.setNextRule(nextRule.getId());
                            compMA.reloadRule(prevRule.getId());
                            nextRule.setPrevRule(prevRule.getId());
                            compMA.reloadRule(nextRule.getId());
                            // Update the new neighbor rules
                            if (selRule.getPrevRule() != null) {
                                InvocationRule newPrevRule =
                                    sobjDB.findObjectById(
                                        InvocationRule.class,
                                        selRule.getPrevRule());
                                newPrevRule.setNextRule(selRule.getId());
                                compMA.reloadRule(newPrevRule.getId());
                            }
                        }
                        // Move the selected rule down
                        else {
                            // Update the affected rule
                            selRule.setPrevRule(nextRule.getId());
                            selRule.setNextRule(nextRule.getNextRule());
                            compMA.reloadRule(selRule.getId());
                            // Update the old neighbor rules
                            if (prevRule != null) {
                                prevRule.setNextRule(nextRule.getId());
                                compMA.reloadRule(prevRule.getId());
                                nextRule.setPrevRule(prevRule.getId());
                            }
                            else {
                                nextRule.setPrevRule(null);
                            }
                            nextRule.setNextRule(selRule.getId());
                            compMA.reloadRule(nextRule.getId());
                            // Update the new neighbor rules
                            if (selRule.getNextRule() != null) {
                                InvocationRule newNextRule =
                                    sobjDB.findObjectById(
                                        InvocationRule.class,
                                        selRule.getNextRule());
                                newNextRule.setPrevRule(selRule.getId());
                                compMA.reloadRule(newNextRule.getId());
                            }
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

        // ===================================================================
        // Rule editor
        // ===================================================================
        if ((reqValAction.equals(actValReqAddRule))
                || (reqValAction.equals(actValReqUpdRule))) {
            boolean isCreate = reqValAction.equals(actValReqAddRule);
            boolean isSelected = false;
            long defaultRuleId =
                InvocationRule.getDefaultRule(sobjDB).getId();
            b.append(sp(in++) + "<fieldset>\n");
            if (isCreate)
                b.append(sp(in) + "<legend>New invocation rule</legend>\n");
            else
                b.append(sp(in) + "<legend>Edit invocation rule</legend>\n");
            b.append(sp(in++) + "<table class=\"borderless\">\n");
            // Skip (project, plug-in, metric and scope), when default rule
            if (((reqValSelRuleId != null)
                    && (reqValSelRuleId.longValue() != defaultRuleId))
                    || (reqValSelRuleId == null)) {
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
                            StoredProject.class,
                            new HashMap<String, Object>());
                // "Any project" option
                b.append(sp(++in) + "<option"
                        + " value=\"ANY\""
                        + ((reqValRuleProject == null) ? " selected" : "")
                        + ">"
                        + "ANY"
                        + "</option>\n");
                if ((allProjects != null)
                        && (allProjects.isEmpty() == false)) {
                    for (StoredProject nextProject : allProjects) {
                        isSelected = ((reqValRuleProject != null) 
                                && (nextProject.getId()
                                        == reqValRuleProject.longValue()));
                        b.append(sp(in) + "<option"
                                + " value=\"" + nextProject.getId() + "\""
                                + ((isSelected) ? " selected" : "")
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
                        + ((reqValRulePlugin == null) ? " selected" : "")
                        + ">"
                        + "ANY"
                        + "</option>\n");
                if ((allPlugins != null) && (allPlugins.isEmpty() == false)) {
                    for (Plugin nextPlugin : allPlugins) {
                        isSelected = ((reqValRulePlugin != null) 
                                && (nextPlugin.getId()
                                        == reqValRulePlugin.longValue()));
                        b.append(sp(in) + "<option"
                                + " value=\"" + nextPlugin.getId() + "\""
                                + ((isSelected) ? " selected" : "")
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
                        + ((reqValRuleMetric == null) ? " selected" : "")
                        + ">"
                        + "ANY"
                        + "</option>\n");
                if ((allTypes != null) && (allTypes.isEmpty() == false)) {
                    for (MetricType nextType : allTypes) {
                        isSelected = ((reqValRuleMetric != null) 
                                && (nextType.getId()
                                        == reqValRuleMetric.longValue()));
                        b.append(sp(in) + "<option"
                                + " value=\"" + nextType.getId() + "\""
                                + ((isSelected) ? " selected" : "")
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
                    isSelected = (scope.toString().equals(reqValRuleScope));
                    b.append(sp(in) + "<option"
                            + " value=\"" + scope.name() + "\""
                            + ((isSelected) ? " selected" : "")
                            + ">"
                            + scope.toString()
                            + "</option>\n");
                }
                b.append(sp(--in) + "</select>"
                        + sp(--in) + "</td>\n"
                        + sp(--in) + "</tr>\n");
            }
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
                isSelected = ((reqValRuleAction != null) 
                        && (action.toString().equals(reqValRuleAction)));
                b.append(sp(in) + "<option"
                        + " value=\"" + action.name() + "\""
                        + ((isSelected) ? " selected" : "")
                        + ">"
                        + action.toString()
                        + "</option>\n");
            }
            b.append(sp(--in) + "</select>"
                    + sp(--in) + "</td>\n"
                    + sp(--in) + "</tr>\n");
            // Skip the rule's value, when default rule
            if (((reqValSelRuleId != null)
                    && (reqValSelRuleId.longValue() != defaultRuleId))
                    || (reqValSelRuleId == null)) {
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
                        + " value=\""
                        + ((reqValRuleValue != null) ? reqValRuleValue : "")
                        + "\">"
                        + "</td>\n");
                b.append(sp(--in) + "</td>\n"
                        + sp(--in) + "</tr>\n");
            }
            // Tool-bar
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in++) + "<td colspan=\"2\" class=\"borderless\">\n");
            b.append(sp(in) + "<input type=\"button\""
                    + " class=\"install\""
                    + " style=\"width: 100px;\""
                    + " value=\""
                    + ((isCreate) ? "Create" : "Modify")
                    + "\""
                    + " onclick=\"javascript:"
                    + "document.getElementById('"
                    + reqParAction + "').value='"
                    + ((isCreate) ? actValConAddRule : actValConUpdRule)
                    + "';"
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
        // ===================================================================
        // Rules list
        // ===================================================================
        else {
            // Create the field-set
            b.append(sp(in) + "<fieldset>\n");
            b.append(sp(++in) + "<legend>All invocation rules</legend>\n");

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

            // Retrieve information for all invocation rules
            List<InvocationRule> rules = sobjDB.findObjectsByProperties(
                    InvocationRule.class, new HashMap<String, Object>());

            // Create the content row
            InvocationRule selRule = null;
            if ((rules == null) || (rules.isEmpty())) {
                b.append(sp(in++) + "<tr>\n");
                b.append(sp(in++) + "<td colspan=\"6\" class=\"noattr\">\n");
                b.append(sp(in) + "There are no defined invocation rules.\n");
                b.append(sp(--in) + "</td>\n");
                b.append(sp(--in) + "</tr>\n");
            }
            else {
                InvocationRule rule = InvocationRule.first(sobjDB);
                InvocationRule defaultRule =
                    InvocationRule.getDefaultRule(sobjDB);
                while (rule != null) {
                    if ((reqValSelRuleId != null)
                            && (reqValSelRuleId == rule.getId())) {
                            b.append(sp(in++) + "<tr class=\"selected\">\n");
                            selRule = rule;
                    }
                    else {
                        if (rule.getId() != defaultRule.getId())
                            b.append(sp(in++) + "<tr class=\"edit\"");
                        else
                            b.append(sp(in++) + "<tr class=\"editMajor\"");
                        b.append(" onclick=\"javascript:"
                                + "document.getElementById('"
                                + reqParSelRuleId + "').value='"
                                + rule.getId() + "';"
                                + conSubmitForm + "\">\n");
                    }
                    // Normal rule
                    if (rule.getId() != defaultRule.getId()) {
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
                    }
                    // Default rule
                    else {
                        b.append(sp(in++) + "<td class=\"trans\""
                                + "colspan=\"5\">"
                                + "Default policy"
                                        + "</td>\n");
                    }
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
                    + "document.getElementById('"
                    + reqParRuleProject + "').value='';"
                    + "document.getElementById('"
                    + reqParRulePlugin + "').value='';"
                    + "document.getElementById('"
                    + reqParRuleMetric + "').value='';"
                    + "document.getElementById('"
                    + reqParRuleScope + "').value='';"
                    + "document.getElementById('"
                    + reqParRuleAction + "').value='';"
                    + "document.getElementById('"
                    + reqParRuleValue + "').value='';"
                    + conSubmitForm + "\">\n");
            if ((reqValSelRuleId != null)
                    && (InvocationRule.getDefaultRule(sobjDB).getId()
                            != reqValSelRuleId)) {
                b.append(sp(in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Insert rule\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValReqAddRule + "';"
                        + "document.getElementById('"
                        + reqParRuleProject + "').value='';"
                        + "document.getElementById('"
                        + reqParRulePlugin + "').value='';"
                        + "document.getElementById('"
                        + reqParRuleMetric + "').value='';"
                        + "document.getElementById('"
                        + reqParRuleScope + "').value='';"
                        + "document.getElementById('"
                        + reqParRuleAction + "').value='';"
                        + "document.getElementById('"
                        + reqParRuleValue + "').value='';"
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
                else {
                    b.append(sp(in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Move up\" disabled>\n");
                }
                if ((rule != null)
                        && (rule.next(sobjDB)
                                != InvocationRule.getDefaultRule(sobjDB))) {
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
                else {
                    b.append(sp(in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Move down\" disabled>\n");
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
            else {
                b.append(sp(in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Insert rule\" disabled>\n");
                b.append(sp(in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Move up\" disabled>\n");
                b.append(sp(in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Move down\" disabled>\n");
                b.append(sp(in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Delete rule\" disabled>\n");
            }
            if (reqValSelRuleId != null) {
                b.append(sp(in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Edit rule\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValReqUpdRule + "';"
                        + "document.getElementById('"
                        + reqParRuleProject + "').value='"
                        + ((selRule.getProject() != null)
                                ? selRule.getProject().getId() : "")
                        + "';"
                        + "document.getElementById('"
                        + reqParRulePlugin + "').value='"
                        + ((selRule.getPlugin() != null)
                                ? selRule.getPlugin().getId() : "")
                        + "';"
                        + "document.getElementById('"
                        + reqParRuleMetric + "').value='"
                        + ((selRule.getMetricType() != null)
                                ? selRule.getMetricType().getId() : "")
                        + "';"
                        + "document.getElementById('"
                        + reqParRuleScope + "').value='"
                        + ((selRule.getScope() != null)
                                ? selRule.getScope() : "")
                        + "';"
                        + "document.getElementById('"
                        + reqParRuleAction + "').value='"
                        + ((selRule.getAction() != null)
                                ? selRule.getAction() : "")
                        + "';"
                        + "document.getElementById('"
                        + reqParRuleValue + "').value='"
                        + ((selRule.getValue() != null)
                                ? selRule.getValue() : "")
                        + "';"
                        + conSubmitForm + "\">\n");
            }
            else {
                b.append(sp(in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Edit rule\" disabled>\n");
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
        // "Selected rule's Id" input field
        b.append(sp(in) + "<input type=\"hidden\""
                + " id=\"" + reqParSelRuleId + "\""
                + " name=\"" + reqParSelRuleId + "\""
                + " value=\""
                + ((reqValSelRuleId != null) ? reqValSelRuleId : "")
                + "\">\n");
        // "Rule's project" input field
        b.append(sp(in) + "<input type=\"hidden\""
                + " id=\"" + reqParRuleProject + "\""
                + " name=\"" + reqParRuleProject + "\""
                + " value=\""
                + ((reqValRuleProject != null) ? reqValRuleProject : "")
                + "\">\n");
        // "Rule's plug-in" input field
        b.append(sp(in) + "<input type=\"hidden\""
                + " id=\"" + reqParRulePlugin + "\""
                + " name=\"" + reqParRulePlugin + "\""
                + " value=\""
                + ((reqValRulePlugin != null) ? reqValRulePlugin : "")
                + "\">\n");
        // "Rule's metric type" input field
        b.append(sp(in) + "<input type=\"hidden\""
                + " id=\"" + reqParRuleMetric + "\""
                + " name=\"" + reqParRuleMetric + "\""
                + " value=\""
                + ((reqValRuleMetric != null) ? reqValRuleMetric : "")
                + "\">\n");
        // "Rule's scope" input field
        b.append(sp(in) + "<input type=\"hidden\""
                + " id=\"" + reqParRuleScope + "\""
                + " name=\"" + reqParRuleScope + "\""
                + " value=\""
                + ((reqValRuleScope != null) ? reqValRuleScope : "")
                + "\">\n");
        // "Rule's action" input field
        b.append(sp(in) + "<input type=\"hidden\""
                + " id=\"" + reqParRuleAction + "\""
                + " name=\"" + reqParRuleAction + "\""
                + " value=\""
                + ((reqValRuleAction != null) ? reqValRuleAction : "")
                + "\">\n");
        // "Rule's value" input field
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
