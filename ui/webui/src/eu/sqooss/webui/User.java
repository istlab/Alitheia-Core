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

package eu.sqooss.webui;

import eu.sqooss.ws.client.datatypes.WSUser;

public class User extends WebuiItem {

    // Hold the user's email address
    private String email;

    // Determines if this user is currently logged into the SQO-OSS framework
    private boolean isLoggedIn;

    /**
     * Instantiates a new user.
     */
    public User() {
        super();
    }

    /**
     * Instantiates a new user, and initializes it with the information stored
     * in the given <code>WSUser</code> object
     *
     * @param user the <code>WSUser<code> object
     */
    public User (WSUser user) {
        this.id = user.getId();
        this.name = user.getUserName();
        this.email = user.getEmail();
    }

    /**
     * Copies the values of the mutable properties from an object of the same
     * type into the corresponding local properties.
     * 
     * @param from an <code>User</code> object used as a values source
     */
    public void copy (User from) {
        if (from != null) {
            this.id = from.getId();
            this.name = from.getName();
            this.email = from.getEmail();
        }
    }

    /**
     * Returns the user's email address.
     */
    public String getEmail () {
        return email;
    }

    /**
     * Checks if this user is currently logged into the attached SQO-OSS
     * framework.
     *
     * @return <code>true</code> if the user is logged in,
     *   or <code>false</code> otherwise.
     */
    public boolean getLoggedIn() {
        return isLoggedIn;
    }

    /**
     * Modifies the login status of this user.
     *
     * @param isLoggedIn the new login status
     */
    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }


    /* (non-Javadoc)
     * @see eu.sqooss.webui.WebuiItem#getHtml(long)
     */
    public String getHtml(long in) {
        StringBuilder html = new StringBuilder("");
        html.append(sp(in) + "<h3>"
                + "User: " + getName() + "(" + getId() + ")"
                + "</h3>");
        return html.toString();
    }

}
