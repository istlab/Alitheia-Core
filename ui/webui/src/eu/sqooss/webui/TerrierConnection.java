/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Sebastian Kuegler <sebas@kde.org>
 * Copyright 2008 by Adriaan de Groot <groot@kde.org>
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

import eu.sqooss.scl.WSException;
import eu.sqooss.scl.WSSession;
import eu.sqooss.scl.accessor.WSAccessor;
import eu.sqooss.scl.accessor.WSMetricAccessor;
import eu.sqooss.scl.accessor.WSProjectAccessor;
import eu.sqooss.scl.accessor.WSUserAccessor;
import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSProjectFile;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSStoredProject;
import eu.sqooss.ws.client.datatypes.WSUser;


/**
 * This class manages a connection from Terrier to the Alitheia
 * core. It handles login and logout (basically setting and resetting
 * the username and password) and connecting on an as-needed basis;
 * when the connection is up there are accessors for the different
 * kinds of data that the core provides.
 */
public class TerrierConnection {
    private WSSession session;

    private WSProjectAccessor projectAccessor;
    private WSMetricAccessor metricAccessor;
    private WSUserAccessor userAccessor;

    private String error = "";
    private String debug = "";

    /**
     * User name and password used for establishing a session between the
     * WebUI and the SQO-OSS framework.
     *
     */
    private String sessionUser = null;
    private String sessionPass = null;

    /**
     * Default settings for the connection; set by the constructor.
     */
    private String defaultUser, defaultPass;
    private String connectionURL;

    /**
     * Simple constructor. Instantiates a new <code>TerrierConnection</code>
     * object and connects to the core as the default (unprivileged) user.
     *
     * @param url URL of the connection.
     * @param user Default user name for the connection.
     * @param pass Password for that default user.
     */
    public TerrierConnection (String url, String user, String pass) {
        connectionURL = url;
        defaultUser = user;
        defaultPass = pass;
        connect();
    }

    /**
     * Check the connection and if not connected, try to (re)connect, then
     * return the connection status.
     */
    public boolean isConnected () {
        if (session == null) {
            connect();
        }
        if (session == null) {
            debug = "noconnection ";
            error = "Connection to Alitheia failed.";
            return false;
        }
        return true;
    }

    /**
     * Forcibly disconnect from the Alitheia core.
     */
    public void disconnect() {
        session = null;
        projectAccessor = null;
        metricAccessor = null;
        userAccessor = null;
    }

    /**
     * Connects to the SQO-OSS framework with the specified user account.
     * Prior successful user login, this function will use the system account.
     *
     * Note: The system account grants only an unprivileged session, but
     * enough for performing a user login or user registration.
     */
    private void connect() {
        try {
            // Try to establish a session with the logged user's account
            if ((sessionUser != null) && (sessionPass != null)) {
                session =
                    new WSSession(
                            sessionUser,
                            sessionPass,
                            connectionURL);
            }
            // Fall back to the system account
            else if (session == null) {
                session =
                    new WSSession(
                            defaultUser,
                            defaultPass,
                            connectionURL);
            }
        } catch (WSException wse) {
            error = "Couldn't establish a session with Alitheia.";
            debug += "nosession";
            wse.printStackTrace();
            disconnect();
            return;
        }
        projectAccessor = (WSProjectAccessor) session.getAccessor(WSAccessor.Type.PROJECT);
        metricAccessor = (WSMetricAccessor) session.getAccessor(WSAccessor.Type.METRIC);
        userAccessor = (WSUserAccessor) session.getAccessor(WSAccessor.Type.USER);
    }

    /**
     * This method will try to establish a session with the SQO-OSS framework
     * by using the specified user credentials. Upon login failure it will
     * exit with <code>false<code> as return value, but prior that it will
     * re-establish the non-privileged user's session.
     *
     * @param username the username
     * @param password the password
     *
     * @return <code>true</code>, if the session is successfully established,
     *   or <code>false</code> otherwise.
     */
    public boolean loginUser (String username, String password) {
        // Clean up the old session (if any)
        disconnect();

        // Try to login with the specified account
        sessionUser = username;
        sessionPass = password;
        if (isConnected()) {
            return true;
        }

        // Fall back to the system account
        sessionUser = null;
        sessionPass = null;
        connect();
        return false;
    }

    /**
     * Find out what user is in use for this connection.
     */
    public String getUserName() {
        return sessionUser;
    }

    /**
     * This method will terminate the current user session, and will then
     * establish a session by using the non-privileged user credentials.
     */
    public void logoutUser () {
        // Clean up the old session (if any)
        disconnect();

        // Fall back to the system account
        sessionUser = null;
        sessionPass = null;
        connect();
    }

    /** Gets the most recent error message from the connection. */
    public String getError() {
        return error;
    }

    /** Returns debugging messages from the system. */
    public String getDebug() {
        return debug;
    }

    /** Accessor to hand off the project accessor to the outside. */
    public WSProjectAccessor getProjectAccessor() {
        return projectAccessor;
    }

    /** Accessor to hand off the metrics accessor to the outside. */
    public WSMetricAccessor getMetricAccessor() {
        return metricAccessor;
    }

    /** Accessor to hand off the user accessor to the outside. */
    public WSUserAccessor getUserAccessor() {
        return userAccessor;
    }
}
