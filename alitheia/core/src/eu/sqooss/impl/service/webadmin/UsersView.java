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
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.GroupPrivilege;
import eu.sqooss.service.db.Privilege;
import eu.sqooss.service.db.PrivilegeValue;
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
     * Renders the various user's views of the SQO-OSS WebAdmin UI:
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

        // Get the required security managers
        UserManager secUM = sobjSecurity.getUserManager();
        GroupManager secGM = sobjSecurity.getGroupManager();
        PrivilegeManager secPM = sobjSecurity.getPrivilegeManager();
        ServiceUrlManager secSU = sobjSecurity.getServiceUrlManager();

        // Get the required resource bundles
        ResourceBundle resLbl = getLabelsBundle(req.getLocale());
        ResourceBundle resErr = getErrorsBundle(req.getLocale());
        ResourceBundle resMsg = getMessagesBundle(req.getLocale());

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
        String actValConRemGroup   = "conRemGroup";
        String actValConEditGroup  = "conEditGroup";
        String actValReqNewUser    = "reqNewUser";
        String actValAddNewUser    = "addNewUser";
        String actValConRemUser    = "conRemUser";
        String actValReqEditUser   = "reqEditUser";
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
        String reqValViewList      = null;
        String reqValAction        = "";
        // Selected user
        User selUser = null;
        // Selected group;
        Group selGroup = null;
        // Current colspan (max columns)
        long maxColspan = 1;

        // Proceed only if at least the system user is available
        if (secUM.getUsers().length > 0) {
            // ===============================================================
            // Parse the servlet's request object
            // ===============================================================
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

                // Retrieve the selected user's DAO (if any)
                reqValUserId = fromString(req.getParameter(reqParUserId));
                if (reqValUserId != null) {
                    selUser = secUM.getUser(reqValUserId);
                    // Retrieve the selected user's parameters
                    if (selUser != null) {
                        reqValUserName = selUser.getName();
                        reqValUserEmail = selUser.getEmail();
                        reqValUserPass = null;
                        reqValPassConf = null;
                    }
                }

                // Retrieve the selected group's DAO (if any)
                reqValGroupId = fromString(req.getParameter(reqParGroupId));
                if (reqValGroupId != null) {
                    selGroup = secGM.getGroup(reqValGroupId);
                }

                // Retrieve the requested list view (if any)
                reqValViewList = req.getParameter(reqParViewList);
                if ((reqValViewList == null)
                        || (reqValViewList.length() == 0)) {
                    if ((selUser != null) || (selGroup != null))
                        reqValViewList = "";
                    else
                        reqValViewList = "users";
                }

                // ===========================================================
                // Add a selected user to a selected group
                // ===========================================================
                if (reqValAction.equals(actValAddToGroup)) {
                    if ((selUser != null) && (selGroup != null)) {
                        if (secGM.addUserToGroup(
                                selGroup.getId(), selUser.getId()) == false) {
                            e.append(sp(in) 
                                    + resErr.getString("e0001")
                                    + " " + resMsg.getString("m0001")
                                    + "<br/>\n");
                        }
                    }
                    if (selUser == null)
                        e.append(sp(in) + resErr.getString("e0004")
                                + "<br/>\n");
                    if (selGroup == null)
                        e.append(sp(in) + resErr.getString("e0003")
                                + "<br/>\n");
                }
                // ===========================================================
                // Remove a selected user from a selected group
                // ===========================================================
                else if (reqValAction.equals(actValRemFromGroup)) {
                    if ((selUser != null) && (selGroup != null)) {
                        if (secGM.deleteUserFromGroup(
                                selGroup.getId(), selUser.getId()) == false) {
                            e.append(sp(in) 
                                    + resErr.getString("e0002")
                                    + " " + resMsg.getString("m0001")
                                    + "<br/>\n");
                        }
                    }
                    if (selUser == null)
                        e.append(sp(in) + resErr.getString("e0004")
                                + "<br/>\n");
                    if (selGroup == null)
                        e.append(sp(in) + resErr.getString("e0003")
                                + "<br/>\n");
                }
                // ===========================================================
                // Add new group to the system
                // ===========================================================
                else if (reqValAction.equals(actValAddNewGroup)) {
                    reqValAction = actValReqNewGroup;
                    // Retrieve the selected group name
                    reqValGroupName = req.getParameter(reqParGroupName);
                    // Create a new group with the specified name
                    if ((reqValGroupName != null)
                            && (reqValGroupName.length() > 0)) {
                        // Check the name syntax
                        if (checkName(reqValGroupName) == false) {
                            e.append(sp(in) + resErr.getString("e0005")
                                    + "<br/>\n");
                        }
                        // Check if a group with the same name already exist
                        else if (secGM.getGroup(reqValGroupName) == null) {
                            Group group = secGM.createGroup(reqValGroupName);
                            if (group != null) {
                                selGroup = group;
                                reqValViewList = "";
                                reqValAction = "";
                            }
                            else {
                                e.append(sp(in) + resErr.getString("e0006")
                                        + " " + resMsg.getString("m0001")
                                        + "<br/>\n");
                            }
                        }
                        else {
                            e.append(sp(in) + resErr.getString("e0007") 
                                    + "<br/>\n");
                        }
                    }
                    else {
                        e.append(sp(in) + resErr.getString("e0008")
                                + "<br/>\n");
                    }
                }
                // ===========================================================
                // Remove an existing group from the system
                // ===========================================================
                else if (reqValAction.equals(actValConRemGroup)) {
                    // Remove the selected group
                    if (selGroup != null) {
                        // Check if this is the system group
                        if (selGroup.getDescription().equals(
                                sobjSecurity.getSystemGroup())) {
                            e.append(sp(in) + resErr.getString("e0009")
                                    + "<br/>\n");
                        }
                        // Try to delete the selected group
                        else {
                            if (secGM.deleteGroup(selGroup.getId())) {
                                selGroup = null;
                                reqValViewList = "groups";
                            }
                            else {
                                e.append(sp(in) + resErr.getString("e0010")
                                        + " " + resMsg.getString("m0001")
                                        + "<br/>\n");
                            }
                        }
                    }
                    else {
                        e.append(sp(in) + resErr.getString("e0011")
                                + "<br/>\n");
                    }
                }
                // ===========================================================
                // Add new user to the system or change an existing one
                // ===========================================================
                else if ((reqValAction.equals(actValAddNewUser))
                        || (reqValAction.equals(actValConEditUser))) {
                    // Check if changing an existing user
                    boolean existing = false;
                    if ((selUser != null)
                            && (reqValAction.equals(actValConEditUser))) {
                        existing = true;
                    }
                    // Retrieve the supplied user's parameters
                    if (existing)
                        reqValUserName = selUser.getName();
                    else
                        reqValUserName = req.getParameter(reqParUserName);
                    reqValUserEmail = req.getParameter(reqParUserEmail);
                    reqValUserPass = req.getParameter(reqParUserPass);
                    reqValPassConf = req.getParameter(reqParPassConf);
                    // Check if a user name is specified
                    if ((reqValUserName == null)
                            || (reqValUserName.length() == 0)) {
                        e.append(sp(in) + resErr.getString("e0012")
                                + "<br/>\n");
                    }
                    // Check for a valid user name
                    else if (checkName(reqValUserName) == false) {
                        e.append(sp(in) + resErr.getString("e0013")
                                + "<br/>\n");
                    }
                    // Check if an email address is specified
                    if ((reqValUserEmail == null)
                            || (reqValUserEmail.length() == 0)) {
                        e.append(sp(in) + resErr.getString("e0014")
                                + "<br/>\n");
                    }
                    // Check for a valid email address
                    else if (checkEmail(reqValUserEmail) == false) {
                        e.append(sp(in) + resErr.getString("e0015")
                                + "<br/>\n");
                    }
                    // Check if the passwords should be left untouched
                    if ((existing)
                            && (reqValUserPass.length() == 0)
                            && (reqValPassConf.length() == 0)) {
                        reqValUserPass = null;
                        reqValPassConf = null;
                    }
                    // Check if both passwords are specified
                    else if ((reqValUserPass == null)
                            || (reqValUserPass.length() == 0)) {
                        e.append(sp(in) + resErr.getString("e0016")
                                + "<br/>\n");
                    }
                    else if ((reqValPassConf == null)
                            || (reqValPassConf.length() == 0)) {
                        e.append(sp(in) + resErr.getString("e0017")
                                + "<br/>\n");
                    }
                    // Check if both passwords are equal
                    else if (reqValUserPass.equals(reqValPassConf) == false) {
                        e.append(sp(in) + resErr.getString("e0018")
                                + "<br/>\n");
                        reqValUserPass = null;
                        reqValPassConf = null;
                    }
                    // Try to create the new user
                    if (e.toString().length() == 0) {
                        // Change an existing user account
                        if (existing) {
                            // Try to modify the selected user's account
                            if (secUM.modifyUser(
                                    reqValUserName,
                                    reqValUserPass,
                                    reqValUserEmail)) {
                                reqValAction = "";
                            }
                            else 
                                e.append(sp(in) + resErr.getString("e0024")
                                        + " " + resMsg.getString("m0001")
                                        + "<br/>\n");
                        }
                        // Create new user account
                        else if (secUM.getUser(reqValUserName) == null) {
                            User user =
                                secUM.createUser(
                                        reqValUserName,
                                        reqValUserPass,
                                        reqValUserEmail);
                            // Successfully created
                            if (user != null) {
                                selUser = user;
                                reqValViewList = "";
                                reqValAction = "";
                            }
                            else {
                                e.append(sp(in) + resErr.getString("e0019")
                                        + " " + resMsg.getString("m0001")
                                        + "<br/>\n");
                            }
                        }
                        // Detected an attempt for creating a duplicated user
                        else {
                            e.append(sp(in) + resErr.getString("e0020")
                                    + "<br/>\n");
                        }
                    }
                    // Return to the proper editor upon error
                    if (e.toString().length() > 0) {
                        if (existing)
                            reqValAction = actValReqEditUser;
                        else
                            reqValAction = actValReqNewUser;
                    }
                }
                // ===========================================================
                // Remove an existing user from the system
                // ===========================================================
                else if (reqValAction.equalsIgnoreCase(actValConRemUser)) {
                    // Remove the selected user
                    if (selUser != null) {
                        // Check if this is the system user
                        if (selUser.getName().equals(
                                sobjSecurity.getSystemUser())) {
                            e.append(sp(in) + resErr.getString("e0021")
                                    + "<br/>\n");
                        }
                        // Delete the selected user
                        else {
                            // TODO. Remove this cycle and the check for group
                            // membership (below) once we have a cascading
                            // delete.
                            for (Object nextGroup :
                                selUser.getGroups().toArray()) {
                                // Remove the user from this group
                                secGM.deleteUserFromGroup(
                                        ((Group) nextGroup).getId(),
                                        selUser.getId());
                            }
                            if ((selUser.getGroups().isEmpty())
                                    && (secUM.deleteUser(selUser.getId()))) {
                                selUser = null;
                                selGroup = null;
                                reqValViewList = "users";
                            }
                            else {
                                e.append(sp(in) + resErr.getString("e0022")
                                        + " " + resMsg.getString("m0001")
                                        + "<br/>\n");
                            }
                        }
                    }
                    else {
                        e.append(sp(in) + resErr.getString("e0023")
                                + "<br/>\n");
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
            if (reqValAction.equalsIgnoreCase(actValReqNewGroup)) {
                b.append(sp(in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>" + resLbl.getString("l0028")
                        + "</legend>\n");
                b.append(sp(in) + "<table class=\"borderless\">");
                // Group name
                b.append(sp(++in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>" + resLbl.getString("l0029") + "</b>"
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
                //------------------------------------------------------------
                // Tool-bar
                //------------------------------------------------------------
                b.append(sp(in) + "<tr>\n");
                b.append(sp(++in)
                        + "<td colspan=\"2\" class=\"borderless\">\n");
                // Apply button
                b.append(sp(++in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"" + resLbl.getString("l0003") + "\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + actValAddNewGroup + "';"
                        + "document.users.submit();\">\n");
                // Cancel button
                b.append(sp(in--) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"" + resLbl.getString("l0004") + "\""
                        + " onclick=\"javascript:"
                        + "document.users.submit();\">\n");
                b.append(sp(in--) + "</td>\n");
                b.append(sp(in--) + "</tr>\n");
                b.append(sp(in--) + "</table>");
                b.append(sp(in) + "</fieldset>\n");
            }
            // ===============================================================
            // Editor for creating new or modifying an existing user account
            // ===============================================================
            else if ((reqValAction.equals(actValReqNewUser))
                    || (reqValAction.equals(actValReqEditUser))) {
                // Check if modifying an existing user
                boolean existing = false;
                if ((selUser != null)
                        && (reqValAction.equals(actValReqEditUser))) {
                    existing = true;
                }
                // Create the field-set
                b.append(sp(in) + "<fieldset>\n");
                b.append(sp(++in) + "<legend>"
                        + ((existing)
                                ? resLbl.getString("l0050")
                                        : resLbl.getString("l0030"))
                        + "</legend>\n");
                b.append(sp(in) + "<table class=\"borderless\">");
                // User name
                b.append(sp(in) + "<tr>\n");
                b.append(sp(++in) + "<td class=\"borderless\""
                        + " style=\"width:100px;\">"
                        + "<b>" + resLbl.getString("l0031") + "</b>"
                        + "</td>\n");
                b.append(sp(in)
                        + "<td class=\"borderless\">\n");
                b.append(sp(++in) + "<input type=\"text\""
                        + " class=\"form\""
                        + " id=\"" + reqParUserName + "\""
                        + " name=\"" + reqParUserName + "\""
                        + ((existing) ? " disabled" : "")
                        + " value=\""
                        + ((reqValUserName != null) ? reqValUserName : "" )
                        + "\">\n");
                b.append(sp(--in) + "</td>\n");
                b.append(sp(--in) + "</tr>\n");
                // Email address
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>" + resLbl.getString("l0032") + "</b>"
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
                        + "<b>" + resLbl.getString("l0033") + "</b>"
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
                        + ((existing) ? "&nbsp;<sup>1</sup>" : "")
                        + "</td>\n"
                        + sp(--in) + "</tr>\n");
                // Confirmation password
                b.append(sp(in) + "<tr>\n"
                        + sp(++in)
                        + "<td class=\"borderless\" style=\"width:100px;\">"
                        + "<b>" + resLbl.getString("l0034") + "</b>"
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
                        + ((existing) ? "&nbsp;<sup>1</sup>" : "")
                        + "</td>\n"
                        + sp(--in)
                        + "</tr>\n");
                //------------------------------------------------------------
                // Toolbar
                //------------------------------------------------------------
                b.append(sp(in) + "<tr>\n");
                b.append(sp(++in)
                        + "<td colspan=\"2\" class=\"borderless\">\n");
                // Apply button
                b.append(sp(++in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"" + resLbl.getString("l0003") + "\""
                        + " onclick=\"javascript:"
                        + "document.getElementById('"
                        + reqParAction + "').value='"
                        + ((existing) ? actValConEditUser : actValAddNewUser)
                        + "';"
                        + "document.users.submit();\">\n");
                // Cancel button
                b.append(sp(in--) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"" + resLbl.getString("l0004") + "\""
                        + " onclick=\"javascript:"
                        + "document.users.submit();\">\n");
                b.append(sp(in--) + "</td>\n");
                b.append(sp(in--) + "</tr>\n");
                b.append(sp(in--) + "</table>");
                // Legend field (for existing accounts only)
                if (existing)
                    b.append(sp(in) + "<p><sup>1</sup> "
                            + resMsg.getString("m0004") + "</p>");
                b.append(sp(in) + "</fieldset>\n");
            }
            // ===============================================================
            // "Add service" editor
            // ===============================================================
            else if (reqValAction.equalsIgnoreCase(actValReqService)) {
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
                // Create the field-set for the "user" views
                if ((reqValViewList.equals("users"))
                        || (selUser != null)) {
                    b.append(sp(++in) + "<fieldset>\n");
                    b.append(sp(++in) + "<legend>"
                            + ((selUser != null)
                                    ? resLbl.getString("l0024") + ": "
                                            + selUser.getName()
                                    : resLbl.getString("l0035"))
                            + "</legend>\n");
                }
                // Create the field-set for the "group" views
                else if ((reqValViewList.equals("groups"))
                        || (selGroup != null)) {
                    b.append(sp(++in) + "<fieldset>\n");
                    b.append(sp(++in) + "<legend>"
                            + ((selGroup != null)
                                    ? resLbl.getString("l0026") + ": "
                                            + selGroup.getDescription()
                                    : resLbl.getString("l0036"))
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
                            + resLbl.getString("l0040") + "</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + resLbl.getString("l0041") + "</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + resLbl.getString("l0042") + "</td>\n");
                    maxColspan = 3;
                }
                // ===========================================================
                // Group editor - header row
                // ===========================================================
                else if (selGroup != null) {
                    b.append(sp(++in) + "<td class=\"head\""
                            + " style=\"width: 40%;\">"
                            + resLbl.getString("l0043") + "</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + resLbl.getString("l0044") + "</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + resLbl.getString("l0045") + "</td>\n");
                    maxColspan = 3;
                }
                // ===========================================================
                // Users list - header row
                // ===========================================================
                else if (reqValViewList.equals("users")) {
                    b.append(sp(++in) + "<td class=\"head\""
                            + " style=\"width: 10%;\">"
                            + resLbl.getString("l0038") + "</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + resLbl.getString("l0031") + "</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + resLbl.getString("l0032") + "</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + resLbl.getString("l0039") + "</td>\n");
                    maxColspan = 4;
                }
                // ===========================================================
                // Groups list - header row
                // ===========================================================
                else if (reqValViewList.equals("groups")) {
                    b.append(sp(++in) + "<td class=\"head\""
                            + " style=\"width: 10%;\">"
                            + resLbl.getString("l0037") + "</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 90%;\">"
                            + resLbl.getString("l0029") + "</td>\n");
                    maxColspan = 2;
                }

                b.append(sp(--in) + "</tr>\n");
                b.append(sp(--in) + "</thead>\n");
                b.append(sp(in) + "<tbody>\n");

                // ===========================================================
                // User editor - content rows
                // ===========================================================
                if (selUser != null) {
                    String btnDisabled = null;
                    b.append(sp(++in) + "<tr>\n");
                    b.append(sp(++in) + "<td>\n");
                    b.append(sp(++in) + "<table class=\"borderless\">\n");
                    b.append(sp(++in) + "<tr>\n"
                            + sp(++in)
                            + "<td class=\"borderless\">"
                            + "<b>" + resLbl.getString("l0038") + "</b>"
                            + "</td>\n"
                            + sp(in) + "<td class=\"borderless\">"
                            + selUser.getId() + "</td>\n"
                            + sp(--in) + "</tr>\n");
                    b.append(sp(in) + "<tr>\n"
                            + sp(++in)
                            + "<td class=\"borderless\">"
                            + "<b>" + resLbl.getString("l0031") + "</b>"
                            + "</td>\n"
                            + sp(in) + "<td class=\"borderless\">"
                            + selUser.getName() + "</td>\n"
                            + sp(--in) + "</tr>\n");
                    b.append(sp(in) + "<tr>\n"
                            + sp(++in)
                            + "<td class=\"borderless\">"
                            + "<b>" + resLbl.getString("l0032") + "</b>"
                            + "</td>\n"
                            + sp(in) + "<td class=\"borderless\">"
                            + selUser.getEmail() + "</td>\n"
                            + sp(--in) + "</tr>\n");
                    DateFormat date = DateFormat.getDateInstance();
                    b.append(sp(in) + "<tr>\n"
                            + sp(++in)
                            + "<td class=\"borderless\">"
                            + "<b>" + resLbl.getString("l0039") + "</b>"
                            + "</td>\n"
                            + sp(in) + "<td class=\"borderless\">"
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
                    // Detach button
                    btnDisabled = " disabled";
                    if ((selGroup != null)
                            && (selUser.getGroups().contains(selGroup)
                                    == true)) {
                        btnDisabled = "";
                    }
                        b.append(sp(in) + "<br/>\n" + sp(in)
                                + "<input type=\"button\""
                                + " class=\"install\""
                                + " style=\"width: 100px;\""
                                + " value=\"Detach\""
                                + btnDisabled
                                + " onclick=\"javascript:"
                                + "document.getElementById('"
                                + reqParAction + "').value='"
                                + actValRemFromGroup + "';"
                                + "document.users.submit();\""
                                + ">\n");
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
                    // Attach button
                    btnDisabled = " disabled";
                    if ((selGroup != null)
                            && (selUser.getGroups().contains(selGroup)
                                    == false)) {
                        btnDisabled = "";
                    }
                    b.append(sp(in) + "<br/>\n" + sp(in)
                            + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"Assign\""
                            + btnDisabled
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValAddToGroup + "';"
                            + "document.users.submit();\""
                            + ">\n");
                    b.append(sp(--in) + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                    
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
                                + resMsg.getString("m0002")
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
                // Users list -content rows
                // ===========================================================
                else if (reqValViewList.equals("users")) {
                    for (User nextUser : secUM.getUsers()) {
                        b.append(sp(++in) + "<tr class=\"edit\""
                                + " onclick=\"javascript:"
                                + "document.getElementById('"
                                + reqParUserId + "').value='"
                                + nextUser.getId() + "';"
                                + "document.users.submit();\""
                                + ">\n");
                        // User's Id
                        b.append(sp(++in) + "<td class=\"trans\">"
                                + "<img src=\"/edit.png\" alt=\"[Edit]\"/>"
                                + "&nbsp;" + nextUser.getId()
                                + "</td>\n");
                        // User's name
                        b.append(sp(in) + "<td class=\"trans\">"
                                + nextUser.getName()
                                + "</td>\n");
                        // User's email
                        b.append(sp(in) + "<td class=\"trans\">"
                                + nextUser.getEmail()
                                + "</td>\n");
                        // User's registration date
                        DateFormat date = DateFormat.getDateInstance();
                        b.append(sp(in) + "<td class=\"trans\">"
                                + date.format(nextUser.getRegistered())
                                + "</td>\n");
                        b.append(sp(--in) + "</tr>\n");
                    }
                }
                // ===========================================================
                // Groups list - content rows
                // ===========================================================
                else if (reqValViewList.equals("groups")) {
                    for (Group nextGroup : secGM.getGroups()) {
                        b.append(sp(++in) + "<tr class=\"edit\""
                                + " onclick=\"javascript:"
                                + "document.getElementById('"
                                + reqParGroupId + "').value='"
                                + nextGroup.getId() + "';"
                                + "document.users.submit();\""
                                + ">\n");
                        // Group's Id
                        b.append(sp(++in) + "<td class=\"trans\">"
                                + "<img src=\"/edit.png\" alt=\"[Edit]\"/>"
                                + "&nbsp;" + nextGroup.getId()
                                + "</td>\n");
                        // Group name
                        b.append(sp(in) + "<td class=\"trans\">"
                                + nextGroup.getDescription()
                                + "</td>\n");
                        b.append(sp(--in) + "</tr>\n");
                    }
                }

                // ===========================================================
                // Common tool-bar
                // ===========================================================
                b.append(sp(in++) + "<tr class=\"subhead\">\n");
                b.append(sp(in++) + "<td colspan=\"" + maxColspan + "\">\n");
                String btnDisabled = null;
                // List users button
                btnDisabled = "";
                if (reqValViewList.equals("users")) {
                    btnDisabled = " disabled";
                }
                b.append(sp(in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"" + resLbl.getString("l0035") + "\""
                        + btnDisabled
                        + " onclick=\"javascript:"
                        + " document.getElementById('"
                        + reqParViewList + "').value='users';"
                        + " document.getElementById('"
                        + reqParUserId + "').value='';"
                        + " document.getElementById('"
                        + reqParGroupId + "').value='';"
                        + "document.users.submit();\""
                        + ">\n");
                // List groups button
                btnDisabled = "";
                if (reqValViewList.equals("groups")) {
                    btnDisabled = " disabled";
                }
                b.append(sp(in) + "<input type=\"button\""
                        + " class=\"install\""
                        + " style=\"width: 100px;\""
                        + " value=\"" + resLbl.getString("l0036") + "\""
                        + btnDisabled
                        + " onclick=\"javascript:"
                        + " document.getElementById('"
                        + reqParViewList + "').value='groups';"
                        + " document.getElementById('"
                        + reqParUserId + "').value='';"
                        + " document.getElementById('"
                        + reqParGroupId + "').value='';"
                        + "document.users.submit();\""
                        + ">\n");
                // Add user button
                if (reqValViewList.equals("users")) {
                    b.append(sp(in)
                            + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"" + resLbl.getString("l0046") + "\""
                            + " onclick=\"javascript:"
                            + " document.getElementById('"
                            + reqParGroupId + "').value='';"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValReqNewUser + "';"
                            + "document.users.submit();\">\n");
                }
                // Add group button
                if (reqValViewList.equals("groups")) {
                    b.append(sp(in)
                            + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"" + resLbl.getString("l0047") + "\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValReqNewGroup + "';"
                            + "document.users.submit();\">\n");
                }
                // Additional buttons for the user editor
                if ((selUser != null)
                        && (selUser.getName().equals(
                                sobjSecurity.getSystemUser()) == false)) {
                    // Edit user
                    b.append(sp(++in) + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"" + resLbl.getString("l0048") + "\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValReqEditUser + "';"
                            + "document.users.submit();\""
                            + ">\n");
                    // Remove user
                    b.append(sp(in) + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"" + resLbl.getString("l0049") + "\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValConRemUser + "';"
                            + "document.users.submit();\""
                            + ">\n");
                }
                // Additional buttons for the group editor
                if ((selUser == null)
                        && (selGroup != null)
                        && (selGroup.getDescription().equals(
                                sobjSecurity.getSystemGroup()) == false)) {
                    // Add resource
                    b.append(sp(in) + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"" + resLbl.getString("l0060") + "\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValReqService + "';"
                            + "document.users.submit();\""
                            + ">\n");
                    // Remove group
                    b.append(sp(in) + "<input type=\"button\""
                            + " class=\"install\""
                            + " style=\"width: 100px;\""
                            + " value=\"" + resLbl.getString("l0049") + "\""
                            + " onclick=\"javascript:"
                            + "document.getElementById('"
                            + reqParAction + "').value='"
                            + actValConRemGroup + "';"
                            + "document.users.submit();\""
                            + ">\n");
                }
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
                    b.append(sp(++in) + "<legend>"
                            + resLbl.getString("l0026") + ": "
                            + selGroup.getDescription() + "</legend\n>");
                    b.append(sp(in) + "<table>\n");
                    // Header row
                    b.append(sp(++in) + "<thead>\n");
                    b.append(sp(++in) + "<tr class=\"head\">\n");
                    b.append(sp(++in) + "<td class=\"head\""
                            + " style=\"width: 40%;\">"
                            + resLbl.getString("l0043")
                            + "</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + resLbl.getString("l0044")
                            + "</td>\n");
                    b.append(sp(in) + "<td class=\"head\""
                            + " style=\"width: 30%;\">"
                            + resLbl.getString("l0045")
                            + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                    b.append(sp(--in) + "</thead>\n");
                    maxColspan = 3;
                    // Content rows
                    b.append(sp(in) + "<tbody>\n");
                    if (selGroup.getGroupPrivileges().isEmpty()) {
                        b.append(sp(++in) + "<tr>\n");
                        b.append(sp(++in) + "<td"
                                + " colspan=\"" + maxColspan + "\""
                                + " class=\"noattr\">"
                                + resMsg.getString("m0002")
                                + "</td>\n");
                        b.append(sp(--in) + "</tr>\n");
                    }
                    else {
                        for (Object priv : selGroup.getGroupPrivileges()) {
                            b.append(sp(++in) + "<tr>\n");
                            // Cast to a GroupPrivilege and display it
                            GroupPrivilege grPriv = (GroupPrivilege) priv;
                            // Service name
                            ServiceUrl pUrl = grPriv.getUrl();
                            b.append(sp(++in) + "<td>"
                                    + pUrl.getUrl()
                                    + "</td>\n");
                            // Privilege type
                            Privilege pType = grPriv.getPv().getPrivilege();
                            b.append(sp(in) + "<td>"
                                    + pType.getDescription()
                                    + "</td>\n");
                            // Privilege value
                            PrivilegeValue pValue = grPriv.getPv();
                            b.append(sp(in) + "<td>"
                                    + pValue.getValue()
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
                    resLbl.getString("l0035"),
                    null,
                    new StringBuilder(resMsg.getString("m0003")),
                    in));
        }

        // Close the DB session
        sobjDB.commitDBSession();

        return b.toString();
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
