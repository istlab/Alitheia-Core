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

public class User {

    private Long id;
    private String name;
    private String email;
    private boolean isLoggedIn;

    /**
     * Simple constructor. Instantiates a new <code>User</code> object.
     */
    public User () {

    }

    /**
     * Instantiates a new <code>User</code> object, and sets id, name and email.
     */
    public User (Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = (email != null) ? email : "";
    }

    /**
     * Returns the user id.
     */
    public Long getId () {
        return id;
    }

    /**
     * Returns the user's name.
     */
    public String getName () {
        return name;
    }

    /**
     * Returns the user's email address.
     */
    public String getEmail () {
        return email;
    }

    /**
     * @return if the user is logged in.
     */
    public boolean getLoggedIn() {
        return isLoggedIn;
    }

    /**
     * @param isLoggedIn Set login statush
     */
    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    /**
     * @return An HTML representation of the user.
     */
    public String getHtml() {
        StringBuilder html = new StringBuilder("<!-- User -->\n<ul>");
        html.append("<h3>User: " + getName() + "(" + getId() + ")</h3>");
        return html.toString();
    }

    /** 
     * Copies the values of the mutable properties from an object of the same
     * type into the corresponding local properties.
     * 
     * @param from an User object used as a values source
     */
    public void copy (User from) {
        if (from != null) {
            this.id = from.getId();
            this.name = from.getName();
            this.email = from.getEmail();
        }
    }
}
