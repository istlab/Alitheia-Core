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

import java.text.DateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.GroupPrivilege;
import eu.sqooss.service.db.ServiceUrl;
import eu.sqooss.service.db.User;
import eu.sqooss.service.security.GroupManager;
import eu.sqooss.service.security.PrivilegeManager;
import eu.sqooss.service.security.ServiceUrlManager;
import eu.sqooss.service.security.UserManager;

public class UsersView extends AbstractView{

    public UsersView(BundleContext bundlecontext, VelocityContext vc) {
        super(bundlecontext, vc);
    }

    /**
     * Renders the various user views of the SQO-OSS WebAdmin UI:
     * <ul>
     *   <li>Users viewer
     *   <li>User editor
     *   <li>Group editor
     *   <li>Privilege editor
     * </ul>
     * 
     * @param req the servlet's request object
     * 
     * @return The current view.
     */
    public static String render(HttpServletRequest req) {
        // Stores the assembled HTML content
        StringBuilder b = new StringBuilder("\n");
        // Stores the accumulated error messages
        StringBuilder e = new StringBuilder();
        // Indentation spacer
        long in = 6;

        // Create a DB session
        sobjDB.startDBSession();
        // Get the various security managers
        UserManager secUM = sobjSecurity.getUserManager();
        GroupManager secGM = sobjSecurity.getGroupManager();
        PrivilegeManager secPM = sobjSecurity.getPrivilegeManager();
        ServiceUrlManager secSU = sobjSecurity.getServiceUrlManager();

        // Proceed only when at least the system user is available
        if (secUM.getUsers().length > 0) {
            // Request parameters
            String reqParAction        = "action";
            String reqParUserId        = "userId";
            String reqParGroupId       = "groupId";
            String reqParRightId       = "rightId";
            String reqParGroupName     = "newGroupName";
            String reqParUserName      = "userName";
            String reqParUserEmail     = "userEmail";
            String reqParUserPass      = "userPass";
            String reqParPassConf      = "passConf";
            String reqParViewList      = "showList";
            // Recognized "action" parameter's values
            String actValAddToGroup    = "addToGroup";
            String actValRemFromGroup  = "removeFromGroup";
            String actValReqNewGroup   = "reqNewGroup";
            String actValAddNewGroup   = "addNewGroup";
            String actValReqRemGroup   = "reqRemGroup";
            String actValConRemGroup   = "conRemGroup";
            String actValConEditGroup  = "conEditGroup";
            String actValReqNewUser    = "reqNewUser";
            String actValAddNewUser    = "addNewUser";
            String actValReqRemUser    = "reqRemUser";
            String actValConRemUser    = "conRemUser";
            String actValConEditUser   = "conEditUser";
            String actValReqService    = "reqService";
            String actValAddService    = "addService";
            // Request values
            Long   reqValUserId        = null;
            Long   reqValGroupId       = null;
            Long   reqValRightId       = null;
            String reqValGroupName     = null;
            String reqValUserName      = null;
            String reqValUserEmail     = null;
            String reqValUserPass      = null;
            String reqValPassConf      = null;
            String reqValViewList      = "users";
            String reqValAction        = "";
            // Selected user
            User selUser = null;
            // Selected group;
            Group selGroup = null;
            // Current colspan (max columns)
            long maxColspan = 1;

            // ===============================================================
            // Parse the servlet's request object
            // ===============================================================
            if (req != null) {
                // DEBUG: Dump the servlet's request parameter
                if (DEBUG) {
                    b.append(debugRequest(req));
                }
                // Retrieve the requested list view (if any)
                reqValViewList = req.getParameter(reqParViewList);
                if (reqValViewList == null) {
                    reqValViewList = "";
                }
                // Retrieve the selected user's DAO (if any)
                reqValUserId = fromString(req.getParameter(reqParUserId));
                if (reqValUserId != null) {
                    selUser = secUM.getUser(reqValUserId);
                }
                // Retrieve the selected group's DAO (if any)
                reqValGroupId = fromString(req.getParameter(reqParGroupId));
                if (reqValGroupId != null) {
                    selGroup = secGM.getGroup(reqValGroupId);
                }
                // Retrieve the selected editor's action
                reqValAction = req.getParameter(reqParAction);
                if (reqValAction == null) {
                    reqValAction = "";
                }
                else if (reqValAction != "") {
                    // Add the selected user to the selected group
                    if (reqValAction.equalsIgnoreCase(actValAddToGroup)) {
                        if ((selUser != null) && (selGroup != null)) {
                            sobjSecurity.getGroupManager()
                            .addUserToGroup(
                                    selGroup.getId(),
                                    selUser.getId());
                        }
                    }
                    // Remove the selected user from the selected group
                    else if (reqValAction.equalsIgnoreCase(actValRemFromGroup)) {
                        if ((selUser != null) && (selGroup != null)) {
                            sobjSecurity.getGroupManager()
                            .deleteUserFromGroup(
                                    selGroup.getId(),
                                    selUser.getId());
                        }
                    }
                    // Add new group to the system
                    else if (reqValAction.equalsIgnoreCase(actValAddNewGroup)) {
                        reqValAction = actValReqNewGroup;
                        // Retrieve the selected group name
                        reqValGroupName =
                            req.getParameter(reqParGroupName);
                        // Create the new group
                        if ((reqValGroupName != null)
                                && (reqValGroupName != "")) {
                            if (checkName(reqValGroupName) == false) {
                                e.append(sp(in)
                                        + "<b>Incorrect syntax:</b>"
                                        + "&nbsp;"
                                        + reqValGroupName
                                        + "<br/>\n");
                            }
                            else if (secGM.getGroup(reqValGroupName) == null) {
                                Group group =
                                    secGM.createGroup(reqValGroupName);
                                if (group != null) {
                                    selGroup = group;
                                    reqValAction = actValAddNewGroup;
                                }
                                else {
                                    e.append(sp(in)
                                            + "<b>Can not create group:</b>"
                                            + "&nbsp;"
                                            + reqValGroupName
                                            + "<br/>\n");
                                }
                            }
                            else {
                                e.append(sp(in)
                                        + "<b>This group already exists:</b>"
                                        + "&nbsp;"
                                        + reqValGroupName
                                        + "<br/>\n");
                            }
                        }
                        else {
                            e.append(sp(in)
                                    + "<b>You must specify a group name!</b>"
                                    + "<br/>\n");
                        }
                    }
                    // Remove existing group from the system
                    else if (reqValAction.equalsIgnoreCase(actValConRemGroup)) {
                        // Remove the selected group
                        if (selGroup != null) {
                            // Check (ignore case) if this is the system group
                            if (selGroup.getDescription().equalsIgnoreCase(
                                    sobjSecurity.getSystemGroup())) {
                                e.append(sp(in)
                                        + "<b>Denied system group removal!</b>"
                                        + "<br/>\n");
                            }
                            // Delete the selected group
                            else  {
                                if (secGM.deleteGroup(selGroup.getId())) {
                                    selGroup = null;
                                }
                                else {
                                    e.append(sp(in)
                                            + "<b>Can not remove group:</b>"
                                            + "&nbsp;"
                                            + reqValGroupName
                                            + "<br/>\n");
                                }
                            }
                        }
                        else {
                            e.append(sp(in)
                                    + "<b>You must select a group name!</b>"
                                    + "<br/>\n");
                        }
                    }
                    // Add new user to the system
                    else if (reqValAction.equalsIgnoreCase(actValAddNewUser)) {
                        reqValAction = actValReqNewUser;
                        // Retrieve the selected user parameters
                        reqValUserName =
                            req.getParameter(reqParUserName);
                        reqValUserEmail =
                            req.getParameter(reqParUserEmail);
                        reqValUserPass =
                            req.getParameter(reqParUserPass);
                        reqValPassConf =
                            req.getParameter(reqParPassConf);

                        // Check the user name
                        if ((reqValUserName == null)
                                || (reqValUserName.length() == 0)) {
                            e.append(sp(in)
                                    + "<b>You must specify an user name!</b>"
                                    + "<br/>\n");
                        }
                        else if (checkName(reqValUserName) == false) {
                            e.append(sp(in)
                                    + "<b>Incorrect syntax:</b>"
                                    + "&nbsp;"
                                    + reqValUserName
                                    + "<br/>\n");
                        }
                        // Check the email address
                        if ((reqValUserEmail == null)
                                || (reqValUserEmail.length() == 0)) {
                            e.append(sp(in)
                                    + "<b>You must specify an email address!</b>"
                                    + "<br/>\n");
                        }
                        else if (checkEmail(reqValUserEmail) == false) {
                            e.append(sp(in)
                                    + "<b>Incorrect syntax:</b>"
                                    + "&nbsp;"
                                    + reqValUserEmail
                                    + "<br/>\n");
                        }
                        // Check the passwords
                        if ((reqValUserPass == null)
                                || (reqValUserPass.length() == 0)) {
                            e.append(sp(in)
                                    + "<b>You must specify an account password!</b>"
                                    + "<br/>\n");
                        }
                        else if ((reqValPassConf == null)
                                || (reqValPassConf.length() == 0)) {
                            e.append(sp(in)
                                    + "<b>You must specify a confirmation password!</b>"
                                    + "<br/>\n");
                        }
                        else if (reqValUserPass.equals(reqValPassConf) == false) {
                            e.append(sp(in)
                                    + "<b>Both passwords do not match!</b>"
                                    + "<br/>\n");
                            reqValUserPass = null;
                            reqValPassConf = null;
                        }

                        // Create the new user
                        if (e.toString().length() == 0) {
                            if (secUM.getUser(reqValUserName) == null) {
                                User user =
                                    secUM.createUser(
                                            reqValUserName,
                                            reqValUserPass,
                                            reqValUserEmail);
                                if (user != null) {
                                    selUser = user;
                                    reqValAction = actValAddNewUser;
                                }
                                else {
                                    e.append(sp(in)
                                            + "<b>Can not create user:</b>"
                                            + "&nbsp;"
                                            + reqValUserName
                                            + "<br/>\n");
                                }
                            }
                            else {
                                e.append(sp(in)
                                        + "<b>Such user already exists:</b>"
                                        + "&nbsp;"
                                        + reqValUserName
                                        + "<br/>\n");
                            }
                        }
                    }
                    // Remove existing user from the system
                    else if (reqValAction.equalsIgnoreCase(actValConRemUser)) {
                        // Remove the selected user
                        if (selUser != null) {
                            // Check if this is the system user
                            if (selUser.getName().equals(
                                    sobjSecurity.getSystemUser())) {
                                e.append(sp(in)
                                        + "<b>Denied system user removal!</b>"
                                        + "<br/>\n");
                            }
                            // Delete the selected user
                            else  {
                                if (secUM.deleteUser(selUser.getId())) {
                                    selUser = null;
                                }
                                else {
                                    e.append(sp(in)
                                            + "<b>Can not remove user:</b>"
                                            + "&nbsp;"
                                            + reqValUserName
                                            + "<br/>\n");
                                }
                            }
                        }
                        else {
                            e.append(sp(in)
                                    + "<b>You must select an user name!</b>"
                                    + "<br/>\n");
                        }
                    }
                }
            }

            // ===============================================================
            // Create the form
            // ===============================================================
            b.append(sp(in) + "<form id=\"users\""
                    + " name=\"users\""
                    + " method=\"post\""
                    + " action=\"/users\">\n");

            // ===============================================================
            // Display the accumulated error messages (if any)
            // ===============================================================
            b.append(errorFieldset(e, ++in));

            // ===============================================================
            // "New group" editor
            // ===============================================================
            if ((reqValAction != null)
                    && (reqValAction.equalsIgnoreCase(actValReqNewGroup))) {
                b.append(sp(in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>New group" + "</legend>\n");
                b.append(sp(in) + "<table class=\"borderless\">");
                // Group name
                b.append(sp(++in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Group name</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">\n"
                        + sp(++in)
                        + "<input type=\"text\""
                        + " class=\"form\""
                        + " id=\"" + reqParGroupName + "\""
                        + " name=\"" + reqParGroupName + "\""
                        + " value=\"\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                // Toolbar
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td colspan=\"2\" class=\"borderless\">"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Apply\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValAddNewGroup + "';"
                        + "document.users.submit();\">"
                        + "&nbsp;"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Cancel\""
                        + " onclick=\"javascript:"
                        + "document.users.submit();\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                b.append(sp(--in) + "</table>");
                b.append(sp(--in) + "</fieldset>\n");
            }
            // ===============================================================
            // "Remove group" editor
            // ===============================================================
            else if ((reqValAction != null)
                    && (reqValAction.equalsIgnoreCase(actValReqRemGroup))) {
                b.append(sp(in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>Remove group" + "</legend>\n");
                b.append(sp(in) + "<table class=\"borderless\">");
                // Group name
                b.append(sp(++in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Group name</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">\n"
                        + sp(++in)
                        + "<select class=\"form\""
                        + " id=\"removeGroup\""
                        + " name=\"removeGroup\">\n");
                for (Group group : secGM.getGroups()) {
                    // Do not display the SQO-OSS system group
                    if (group.getDescription().equalsIgnoreCase(
                            sobjSecurity.getSystemGroup()) == false) {
                        b.append(sp(in) + "<option"
                                + " value=\"" + group.getId() + "\""
                                + (((selGroup != null)
                                        && (selGroup.getId() == group.getId()))
                                        ? " selected"
                                        : "")
                                + ">"
                                + group.getDescription()
                                + "</option>\n");
                    }
                }
                b.append(sp(in) + "</select>\n"
                        + sp(--in)
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                // Toolbar
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td colspan=\"2\" class=\"borderless\">"
                        + "<input type=\"button\""
                        + " style=\"width: 100px;\""
                        + " value=\"Remove\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValConRemGroup + "';"
                        + "document.getElementById('"
                        + reqParGroupId + "').value="
                        + "document.getElementById('removeGroup').value;"
                        + "document.users.submit();\">"
                        + "&nbsp;"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Cancel\""
                        + " onclick=\"javascript:"
                        + "document.users.submit();\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                b.append(sp(--in) + "</table>");
                b.append(sp(--in) + "</fieldset>\n");
            }
            // ===============================================================
            // "New user" editor
            // ===============================================================
            else if ((reqValAction != null)
                    && (reqValAction.equalsIgnoreCase(actValReqNewUser))) {
                b.append(sp(in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>New user" + "</legend>\n");
                b.append(sp(in) + "<table class=\"borderless\">");
                // User name
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Name</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">"
                        + "<input type=\"text\""
                        + " class=\"form\""
                        + " id=\"" + reqParUserName + "\""
                        + " name=\"" + reqParUserName + "\""
                        + " value=\""
                        + ((reqValUserName != null) ? reqValUserName : "" )
                        + "\">"
                        + "</td>\n"
                        + sp(--in) + "</tr>\n");
                // Email address
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Email</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">"
                        + "<input type=\"text\""
                        + " class=\"form\""
                        + " id=\"" + reqParUserEmail + "\""
                        + " name=\"" + reqParUserEmail + "\""
                        + " value=\""
                        + ((reqValUserEmail != null) ? reqValUserEmail : "" )
                        + "\">"
                        + "</td>\n"
                        + sp(--in) + "</tr>\n");
                // Account password
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Password</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">"
                        + "<input type=\"password\""
                        + " class=\"form\""
                        + " id=\"" + reqParUserPass + "\""
                        + " name=\"" + reqParUserPass + "\""
                        + " value=\""
                        + ((reqValUserPass != null) ? reqValUserPass : "" )
                        + "\">"
                        + "</td>\n"
                        + sp(--in) + "</tr>\n");
                // Confirmation password
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>Confirm</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">"
                        + "<input type=\"password\""
                        + " class=\"form\""
                        + " id=\"" + reqParPassConf + "\""
                        + " name=\"" + reqParPassConf + "\""
                        + " value=\""
                        + ((reqValPassConf != null) ? reqValPassConf : "" )
                        + "\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                // Toolbar
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td colspan=\"2\" class=\"borderless\">"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Apply\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValAddNewUser + "';"
                        + "document.users.submit();\">"
                        + "&nbsp;"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Cancel\""
                        + " onclick=\"javascript:"
                        + "document.users.submit();\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                b.append(sp(--in) + "</table>");
                b.append(sp(--in) + "</fieldset>\n");
            }
            // ===============================================================
            // "Remove user" editor
            // ===============================================================
            else if ((reqValAction != null)
                    && (reqValAction.equalsIgnoreCase(actValReqRemUser))) {
                b.append(sp(in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>Remove user" + "</legend>\n");
                b.append(sp(in) + "<table class=\"borderless\">");
                // User name
                b.append(sp(++in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>User name</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">\n"
                        + sp(++in)
                        + "<select class=\"form\""
                        + " id=\"removeUser\""
                        + " name=\"removeUser\">\n");
                for (User user : secUM.getUsers()) {
                    // Do not display the SQO-OSS system group
                    if (user.getName().equalsIgnoreCase(
                            sobjSecurity.getSystemUser()) == false) {
                        b.append(sp(in) + "<option"
                                + " value=\"" + user.getId() + "\""
                                + (((selUser != null)
                                        && (selUser.getId() == user.getId()))
                                        ? " selected"
                                        : "")
                                + ">"
                                + user.getName()
                                + "</option>\n");
                    }
                }
                b.append(sp(in) + "</select>\n"
                        + sp(--in)
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                // Toolbar
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td colspan=\"2\" class=\"borderless\">"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Remove\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValConRemUser + "';"
                        + "document.getElementById('"
                        + reqParUserId + "').value="
                        + "document.getElementById('removeUser').value;"
                        + "document.users.submit();\">"
                        + "&nbsp;"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Cancel\""
                        + " onclick=\"javascript:"
                        + "document.users.submit();\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                b.append(sp(--in) + "</table>");
                b.append(sp(--in) + "</fieldset>\n");
            }
            // ===============================================================
            // "Add service" editor
            // ===============================================================
            else if ((reqValAction != null)
                    && (reqValAction.equalsIgnoreCase(actValReqService))) {
                b.append(sp(in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>Add service"
                        + ((selGroup != null) 
                                ? " to group" + selGroup.getDescription() 
                                        : "")
                        + "</legend>\n");
                b.append(sp(in) + "<table class=\"borderless\">");
                // Service name
                b.append(sp(++in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>User name</b>"
                        + "</td>\n"
                        + sp(in)
                        + "<td class=\"borderless\">\n"
                        + sp(++in)
                        + "<select class=\"form\""
                        + " id=\"addService\""
                        + " name=\"addService\">\n");
                for (ServiceUrl service : secSU.getServiceUrls()) {
                        b.append(sp(in) + "<option"
                                + " value=\"" + service.getId() + "\""
                                + ">"
                                + service.getUrl()
                                + "</option>\n");
                }
                b.append(sp(in) + "</select>\n"
                        + sp(--in)
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                // Toolbar
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td colspan=\"2\" class=\"borderless\">"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Remove\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValConRemUser + "';"
                        + "document.getElementById('"
                        + reqParUserId + "').value="
                        + "document.getElementById('removeUser').value;"
                        + "document.users.submit();\">"
                        + "&nbsp;"
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Cancel\""
                        + " onclick=\"javascript:"
                        + "document.users.submit();\">"
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                b.append(sp(--in) + "</table>");
                b.append(sp(--in) + "</fieldset>\n");
            }
            // ===============================================================
            // Main viewers and editors
            // ===============================================================
            else {
                // Create the fieldset for the "user" views
                if ((reqValViewList.equals("users")) || (selUser != null)) {
                    b.append(sp(++in) + "<fieldset>\n");
                    b.append(sp(++in) + "<legend>"
                            + ((selUser != null)
                                    ? "User " + selUser.getName()
                                    : "All users")
                            + "</legend>\n");
                }
                // Create the fieldset for the "group" views
                else if ((reqValViewList.equals("groups")) || (selGroup != null)) {
                    b.append(sp(++in) + "<fieldset>\n");
                    b.append(sp(++in) + "<legend>"
                            + ((selGroup != null)
                                    ? "Group " + selGroup.getDescription()
                                    : "All groups")
                            + "</legend>\n");
                }

                b.append(sp(in) + "<table>\n");
                b.append(sp(++in) + "<thead>\n");
                b.append(sp(++in) + "<tr class=\"head\">\n");

                // ===========================================================
                // User editor - header row
                // ===========================================================
                if (selUser != null) {
                    b.append(sp(++in) + "<td class=\"head\""
                            + " style=\"width: 40%;\">"
                            + "Account Details</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "Member Of</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "Available Groups</td>\n");
                    maxColspan = 3;
                }
                // ===========================================================
                // Users list - header row
                // ===========================================================
                else if (reqValViewList.equals("users")) {
                    b.append(sp(++in) + "<td class=\"head\""
                            + " style=\"width: 10%;\">"
                            + "User Id</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "User Name</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "User Email</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "Created</td>\n");
                    maxColspan = 4;
                }
                // ===========================================================
                // Group editor - header row
                // ===========================================================
                else if (selGroup != null) {
                    b.append(sp(++in) + "<td class=\"head\""
                            + " style=\"width: 40%;\">"
                            + "Resource Name</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "Privilege Type</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "Privilege Value</td>\n");
                    maxColspan = 3;
                }
                // ===========================================================
                // Groups list - header row
                // ===========================================================
                else if (reqValViewList.equals("groups")) {
                    b.append(sp(++in) + "<td class=\"head\""
                            + " style=\"width: 10%;\">"
                            + "Group Id</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 90%;\">"
                            + "Group Name</td>\n");
                    maxColspan = 2;
                }

                b.append(sp(--in) + "</tr>\n");
                b.append(sp(--in) + "</thead>\n");
                b.append(sp(in) + "<tbody>\n");

                // ===========================================================
                // User editor - content rows
                // ===========================================================
                if (selUser != null) {
                    b.append(sp(++in) + "<tr>\n");
                    b.append(sp(++in) + "<td>\n");
                    b.append(sp(++in) + "<table>\n");
                    b.append(sp(++in) + "<tr>\n"
                            + sp(++in)
                            + "<td class=\"name\">User Id</td>\n"
                            + sp(in) + "<td>&nbsp;"
                            + selUser.getId() + "</td>\n"
                            + sp(--in) + "</tr>\n");
                    b.append(sp(in) + "<tr>\n"
                            + sp(++in)
                            + "<td class=\"name\">User Name</td>\n"
                            + sp(in) + "<td>&nbsp;"
                            + selUser.getName() + "</td>\n"
                            + sp(--in) + "</tr>\n");
                    b.append(sp(in) + "<tr>\n"
                            + sp(++in)
                            + "<td class=\"name\">User Email</td>\n"
                            + sp(in) + "<td>&nbsp;"
                            + selUser.getEmail() + "</td>\n"
                            + sp(--in) + "</tr>\n");
                    DateFormat date = DateFormat.getDateInstance();
                    b.append(sp(in) + "<tr>\n"
                            + sp(++in)
                            + "<td class=\"name\">Created</td>\n"
                            + sp(in) + "<td>&nbsp;"
                            + date.format(selUser.getRegistered()) + "</td>\n"
                            + sp(--in) + "</tr>\n");
                    b.append(sp(--in) + "</table>\n");
                    b.append(sp(--in) + "</td>\n");
                    // Display all groups where the selected user is a member
                    b.append(sp(in) + "<td>\n");
                    b.append(sp(++in) + "<select"
                            + " id=\"attachedGroups\" name=\"attachedGroups\""
                            + " size=\"4\""
                            + " style=\"width: 100%; border: 0;\""
                            + "onchange=\""
                            + "document.getElementById('"
                            + reqParGroupId + "').value="
                            + "document.getElementById('attachedGroups').value;"
                            + "document.users.submit();\""
                            + "\""
                            + ">\n");
                    sp(++in);
                    for (Object memberOf : selUser.getGroups()) {
                        Group group = (Group) memberOf;
                        boolean selected = ((selGroup != null)
                                && (selGroup.getId() == group.getId()));
                        b.append(sp(in) + "<option"
                                + " value=\"" + group.getId() + "\""
                                + ((selected) ? " selected" : "")
                                + ">"
                                + group.getDescription()
                                + "</option>\n");
                    }
                    b.append(sp(--in) + "</select>\n");
                    b.append(sp(--in) + "</td>\n");
                    // Display all group where the selected user is not a member
                    b.append(sp(in) + "<td>\n");
                    b.append(sp(++in) + "<select"
                            + " id=\"availableGroups\" name=\"availableGroups\""
                            + " size=\"4\""
                            + " style=\"width: 100%; border: 0;\""
                            + "onchange=\""
                            + "document.getElementById('"
                            + reqParGroupId + "').value="
                            + "document.getElementById('availableGroups').value;"
                            + "document.users.submit();\""
                            + "\""
                            + ">\n");
                    sp(++in);
                    for (Group group : secGM.getGroups()) {
                        // Skip groups where this user is already a member 
                        if (selUser.getGroups().contains(group) == false) {
                            boolean selected = ((selGroup != null)
                                    && (selGroup.getId() == group.getId()));
                            b.append(sp(in) + "<option"
                                    + " value=\"" + group.getId() + "\""
                                    + ((selected) ? " selected" : "")
                                    + ">"
                                    + group.getDescription()
                                    + "</option>\n");
                        }
                    }
                    b.append(sp(--in) + "</select>\n");
                    b.append(sp(--in) + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                }
                // ===========================================================
                // Users list -content rows
                // ===========================================================
                else if (reqValViewList.equals("users")) {
                    for (User nextUser : secUM.getUsers()) {
                        String htmlEditUser = "<td class=\"edit\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParUserId + "').value='"
                            + nextUser.getId() + "';"
                            + "document.users.submit();\">"
                            + "<img src=\"/edit.png\" alt=\"[Edit]\"/>"
                            + nextUser.getName()
                            + "</td>\n";
                        b.append(sp(++in) + "<tr>\n");
                        b.append(sp(++in) + "<td>" + nextUser.getId() + "</td>\n");
                        b.append(sp(in) + htmlEditUser);
                        b.append(sp(in) + "<td>" + nextUser.getEmail() + "</td>\n");
                        DateFormat date = DateFormat.getDateInstance();
                        b.append(sp(in) + "<td>"
                                + date.format(nextUser.getRegistered())
                                + "</td>\n");
                        b.append(sp(--in) + "</tr>\n");
                    }
                }
                // ===========================================================
                // Group editor - content rows
                // ===========================================================
                else if (selGroup != null) {
                    if (selGroup.getGroupPrivileges().isEmpty()) {
                        b.append(sp(++in) + "<tr>\n");
                        b.append(sp(++in) + "<td"
                                + " colspan=\"" + maxColspan + "\""
                                + " class=\"noattr\""
                                + ">"
                                + "This group has no attached resources."
                                + "</td>\n");
                        b.append(sp(--in) + "</tr>\n");
                    }
                    else {
                        for (Object priv : selGroup.getGroupPrivileges()) {
                            b.append(sp(++in) + "<tr>\n");
                            // Cast to a GroupPrivilege and display it
                            GroupPrivilege grPriv = (GroupPrivilege) priv;
                            // Service name
                            b.append(sp(++in) + "<td>"
                                    + grPriv.getUrl().getUrl()
                                    + "</td>\n");
                            // Privilege type
                            b.append(sp(in) + "<td>"
                                    + grPriv.getPv().getPrivilege().getDescription()
                                    + "</td>\n");
                            // Privilege value
                            b.append(sp(in) + "<td>"
                                    + grPriv.getPv().getValue()
                                    + "</td>\n");
                            b.append(sp(--in) + "</tr>\n");
                        }
                    }
                }
                // ===========================================================
                // Groups list -content rows
                // ===========================================================
                else if (reqValViewList.equals("groups")) {
                    for (Group nextGroup : secGM.getGroups()) {
                        String htmlEditGroup = "<td class=\"edit\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParGroupId + "').value='"
                            + nextGroup.getId() + "';"
                            + "document.users.submit();\">"
                            + "<img src=\"/edit.png\" alt=\"[Edit]\"/>"
                            + nextGroup.getDescription()
                            + "</td>\n";
                        b.append(sp(++in) + "<tr>\n");
                        b.append(sp(++in) + "<td>"
                                + nextGroup.getId()
                                + "</td>\n");
                        b.append(sp(in) + htmlEditGroup);
                        b.append(sp(--in) + "</tr>\n");
                    }
                }

                // ===============================================================
                // User editor - toolbar
                // ===============================================================
                if ((selUser != null)
                    && (selUser.getName().equals(
                            sobjSecurity.getSystemUser()) == false)) {
                    // Create the toolbar
                    b.append(sp(in) + "<tr>\n");
                    // User modifications
                    b.append(sp(++in) + "<td>\n");
                    b.append(sp(++in) + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"Edit\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValConEditUser + "';"
                            + "document.users.submit();\""
                            + ">\n");
                    b.append(sp(in) + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"Remove\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValConRemUser + "';"
                            + "document.users.submit();\""
                            + ">\n");
                    b.append(sp(--in) + "</td>\n");
                    // Detach group
                    b.append(sp(in) + "<td>\n");
                    if ((selGroup != null)
                            && (selUser.getGroups().contains(selGroup) == true)) {
                        b.append(sp(++in) + "<input type=\"button\""
                                + " class=\"install\""
                                + " style=\"width: 100px;\""
                                + " value=\"Detach\""
                                + " onclick=\"javascript:"
                                + "document.getElementById('"
                                + reqParAction + "').value='"
                                + actValRemFromGroup + "';"
                                + "document.users.submit();\""
                                + ">\n");
                        in--;
                    }
                    b.append(sp(in) + "</td>\n");
                    // Assign group 
                    b.append(sp(in) + "<td>\n");
                    if ((selGroup != null)
                            && (selUser.getGroups().contains(selGroup) == false)) {
                        b.append(sp(++in) + "<input type=\"button\""
                                + " class=\"install\""
                                + " style=\"width: 100px;\""
                                + " value=\"Assign\""
                                + " onclick=\"javascript:"
                                + "document.getElementById('"
                                + reqParAction + "').value='"
                                + actValAddToGroup + "';"
                                + "document.users.submit();\""
                                + ">\n");
                        in--;
                    }
                    b.append(sp(in) + "</td>\n");
                    // Close the toolbar
                    b.append(sp(--in) + "</tr>\n");
                }
                // ===============================================================
                // Group editor - toolbar
                // ===============================================================
                else if ((selGroup != null)
                    && (selGroup.getDescription().equals(
                            sobjSecurity.getSystemGroup()) == false)) {
                    // Create the toolbar
                    b.append(sp(in) + "<tr>\n");
                    // Group modifications
                    b.append(sp(in++) + "<td colspan=\""
                            + maxColspan
                            + "\">\n");
                    b.append(sp(++in) + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"Add Resource\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValReqService + "';"
                            + "document.users.submit();\""
                            + ">\n");
                    b.append(sp(in) + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"Remove\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValConRemGroup + "';"
                            + "document.users.submit();\""
                            + ">\n");
                    // Close the toolbar
                    b.append(sp(--in) + "</tr>\n");
                }

                // ===========================================================
                // Common toolbar
                // ===========================================================
                b.append(sp(in++) + "<tr class=\"subhead\">\n");
                b.append(sp(in++) + "<td colspan=\"" + maxColspan + "\">\n"
                        // List users
                        + sp(in)
                        + ((reqValViewList.equals("users") == false)
                                ? "<input type=\"button\""
                                        + " class=\"install\""
                                        + " style=\"width: 100px;\""
                                        + " value=\"Users list\""
                                        + " onclick=\"javascript:"
                                        + " document.getElementById('"
                                        + reqParViewList + "').value='users';"
                                        + " document.getElementById('"
                                        + reqParUserId + "').value='';"
                                        + " document.getElementById('"
                                        + reqParGroupId + "').value='';"
                                        + "document.users.submit();\">\n"
                                        : ""
                        )
                        // List groups
                        + sp(in)
                        + ((reqValViewList.equals("groups") == false)
                                ? "<input type=\"button\""
                                        + " class=\"install\""
                                        + " style=\"width: 100px;\""
                                        + " value=\"Groups list\""
                                        + " onclick=\"javascript:"
                                        + " document.getElementById('"
                                        + reqParViewList + "').value='groups';"
                                        + " document.getElementById('"
                                        + reqParUserId + "').value='';"
                                        + " document.getElementById('"
                                        + reqParGroupId + "').value='';"
                                        + "document.users.submit();\">\n"
                                        : ""
                        )
                        // Add User
                        + sp(in)
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Add user\""
                        + " onclick=\"javascript:"
                        + " document.getElementById('"
                        + reqParGroupId + "').value='';"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValReqNewUser + "';"
                        + "document.users.submit();\">\n"
                        // Remove User
                        + sp(in)
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Remove user\""
                        + " onclick=\"javascript:"
                        + " document.getElementById('"
                        + reqParGroupId + "').value='';"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValReqRemUser + "';"
                        + "document.users.submit();\">\n"
                        // Add group
                        + sp(in)
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Add group\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValReqNewGroup + "';"
                        + "document.users.submit();\">\n"
                        // Remove Group
                        + sp(in)
                        + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"Remove group\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValReqRemGroup + "';"
                        + "document.users.submit();\">\n"
                        + sp(--in)
                        + "</td>\n");
                b.append(sp(--in) + "</tr>\n");

                // Close the table
                b.append(sp(--in) + "</tbody>\n");
                b.append(sp(--in) + "</table>\n");
                b.append(sp(--in) + "</fieldset>\n");

                // ===============================================================
                // "Selected group" viewer
                // ===============================================================
                if ((selUser != null) && (selGroup != null)) {
                    b.append(sp(in) + "<fieldset>\n");
                    b.append(sp(++in) + "<legend>Group "
                            + selGroup.getDescription() + "</legend\n>");
                    b.append(sp(in) + "<table>\n");

                    b.append(sp(++in) + "<thead>\n");
                    b.append(sp(++in) + "<td class=\"head\""
                            + " style=\"width: 40%;\">"
                            + "Resource Name</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "Privilege Type</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + "Privilege Value</td>\n");
                    b.append(sp(--in) + "</thead>\n");
                    maxColspan = 3;

                    b.append(sp(in) + "<tbody>\n");
                    if (selGroup.getGroupPrivileges().isEmpty()) {
                        b.append(sp(++in) + "<tr>\n");
                        b.append(sp(++in) + "<td"
                                + " colspan=\"" + maxColspan + "\""
                                + " class=\"noattr\""
                                + ">"
                                + "This group has no attached resources."
                                + "</td>\n");
                        b.append(sp(--in) + "</tr>\n");
                    }
                    else {
                        for (Object priv : selGroup.getGroupPrivileges()) {
                            b.append(sp(++in) + "<tr>\n");
                            // Cast to a GroupPrivilege and display it
                            GroupPrivilege grPriv = (GroupPrivilege) priv;
                            // Service name
                            b.append(sp(++in) + "<td>"
                                    + grPriv.getUrl().getUrl()
                                    + "</td>\n");
                            // Privilege type
                            b.append(sp(in) + "<td>"
                                    + grPriv.getPv().getPrivilege().getDescription()
                                    + "</td>\n");
                            // Privilege value
                            b.append(sp(in) + "<td>"
                                    + grPriv.getPv().getValue()
                                    + "</td>\n");
                            b.append(sp(--in) + "</tr>\n");
                        }
                    }

//                    // "Available rights" header
//                    if (secPM.getPrivileges().length > 0) {
//                        b.append(sp(in) + "<tr class=\"subhead\">\n");
//                        b.append(sp(++in) + "<td class=\"subhead\" colspan=\"4\">"
//                                + "Available</td>");
//                        b.append(sp(--in) + "</tr>\n");
//                        // "Available rights" list
//                        for (Privilege privilege : secPM.getPrivileges()) {
//                            b.append(sp(in) + "<tr>\n");
//                            // Action bar
//                            b.append(sp(++in) + "<td>"
//                                    + "&nbsp;"
//                                    + "</td>\n");
//                            // Available services
//                            b.append(sp(in) + "<td>"
//                                    + "&nbsp;"
//                                    + "</td>\n");
//                            // Available privileges
//                            b.append(sp(in) + "<td>"
//                                    + privilege.getDescription()
//                                    + "</td>\n");
//                            // Available rights
//                            b.append(sp(in) + "<td>\n");
//                            PrivilegeValue[] values =
//                                secPM.getPrivilegeValues(privilege.getId());
//                            if ((values != null) && (values.length > 0)) {
//                                b.append(sp(++in) + "<select"
//                                        + " style=\"width: 100%; border: 0;\""
//                                        + ">\n");
//                                in++;
//                                for (PrivilegeValue value : values) {
//                                    b.append(sp(in) + "<option"
//                                            + " value=\"" + value.getId() + "\""
//                                            + " onclick=\"javascript:"
//                                            + " document.getElementById('"
//                                            + reqParRightId + "').value='"
//                                            + value.getId() + "';"
//                                            + "document.users.submit();\""
//                                            + ">"
//                                            + value.getValue()
//                                            + "</option>\n");
//                                }
//                                b.append(sp(--in) + "</select>\n");
//                            }
//                            else {
//                                b.append(sp(++in) + "<b>NA</b>");
//                            }
//                            b.append(sp(--in) + "</td>\n");
//                            b.append(sp(--in) + "</tr>\n");
//                        }
//                    }

                    b.append(sp(--in) + "</tbody>\n");

                    b.append(sp(--in) + "</table>\n");
                    b.append(sp(--in) + "</fieldset>\n");
                }
            }

            // ===============================================================
            // INPUT FIELDS
            // ===============================================================
            // "Action type" input field
            b.append(sp(in) + "<input type=\"hidden\""
                    + " id=\"" + reqParAction + "\"" 
                    + " name=\"" + reqParAction + "\""
                    + " value=\"\">\n");
            // "User Id" input field
            b.append(sp(in) + "<input type=\"hidden\""
                    + " id=\"" + reqParUserId + "\"" 
                    + " name=\"" + reqParUserId + "\""
                    + " value=\""
                    + ((selUser != null) ? selUser.getId() : "")
                    + "\">\n");
            // "Group Id" input field
            b.append(sp(in) + "<input type=\"hidden\""
                    + " id=\"" + reqParGroupId + "\"" 
                    + " name=\"" + reqParGroupId + "\""
                    + " value=\""
                    + ((selGroup != null) ? selGroup.getId() : "")
                    + "\">\n");
            // "Right Id" input field
            b.append(sp(in) + "<input type=\"hidden\""
                    + " id=\"" + reqParRightId + "\"" 
                    + " name=\"" + reqParRightId + "\""
                    + " value=\"\">\n");
            // "View list" input field
            b.append(sp(in) + "<input type=\"hidden\""
                    + " id=\"" + reqParViewList + "\"" 
                    + " name=\"" + reqParViewList + "\""
                    + " value=\"\">\n");

            // ===============================================================
            // Close the form
            // ===============================================================
            b.append(sp(--in) + "</form>\n");
        }
        else {
            b.append(normalFieldset(
                    "Users list",
                    null,
                    new StringBuilder("No users found!"),
                    in));
        }
        // Close the DB session
        sobjDB.commitDBSession();

        return b.toString();
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
