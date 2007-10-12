/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.impl.service.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.impl.service.logging.LogManagerConstants;

public class DBServiceImpl {
    private Logger logger = null;
    // This is the database connection; we may want to do more pooling here.
    private Connection dbConnection = null;
    private Statement dbStatement = null;

    private Connection getJDBCConnection(String driver, String url) {
        try {
            Class.forName(driver).newInstance();
            logger.info("Created JDBC instance for " + driver);
            Connection c = DriverManager.getConnection(url);
            logger.info("Created JDBC connection for " + url);
            c.setAutoCommit(false);
            return c;
        } catch (InstantiationException e) {
            logger.warning("Could not instantiate JDBC connection for " + driver);
        } catch (ClassNotFoundException e) {
            logger.warning("Could not get class for JDBC driver " + driver);
        } catch (IllegalAccessException e) {
            logger.warning("SEGV. Core dumped.");
        } catch (SQLException e) {
            logger.warning("SQL Exception while instantiating " + driver);
        }

        return null;
    }

    /**
     * Attempt to get the Postgres JDBC connector and initialize
     * a connection to the Postgres instance (just to check that
     * the DB is up and running).
     *
     * @return @c true on success
     */
    private boolean getPostgresJDBC() {
        Connection c = getJDBCConnection(
            "org.postgresql.Driver",
            "jdbc:postgresql:postgresDB");

        if (c!=null) {
            dbConnection = c;
        }
        return (c!=null);
    }

    /**
     * Attempt to get the Derby JDBC connector and initialize
     * a connection to the Derby instance -- this is intended
     * to be a debug fallback routine during development.
     *
     * @return @c true on success
     */
    private boolean getDerbyJDBC() {
        Connection c = getJDBCConnection(
            "org.apache.derby.jdbc.EmbeddedDriver",
            "jdbc:derby:derbyDB;create=true");

        if (c!=null) {
            dbConnection = c;
        }
        return (c!=null);
    }

    public DBServiceImpl() {
        logger = LogManager.getInstance().createLogger("sqooss.database");
        if (logger != null) {
            logger.info("DB service created.");
        } else {
            System.out.println("# DB service failed to get logger.");
        }

        if (!getPostgresJDBC()) {
            if (!getDerbyJDBC()) {
                logger.severe("DB service got no JDBC connectors.");
            }
        }

        try {
            dbStatement = dbConnection.createStatement();
            dbStatement.execute("create table STORED_PROJECT (ID int, NAME varchar(80))");
            logger.info("Created table STORED_PROJECT.");
        } catch (SQLException e) {
            logger.warning("SQL Exception while creating table.");
        }
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

