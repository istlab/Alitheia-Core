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
import java.sql.SQLException;
import java.sql.Statement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;

public class DBServiceImpl implements DBService {
    private LogManager logService = null;
    private Logger logger = null;
    // This is the database connection; we may want to do more pooling here.
    private Connection dbConnection = null;
    private Statement dbStatement = null;
    // Store the class and URL of the database to hand off to
    // Hibernate so that it obeys the fallback from Postgres to Derby as well.
    private String dbClass, dbURL, dbDialect;

    private SessionManager sm = null;
    
    /**
     * The simplest possible Session pool implementation. Maintains a pool of
     * active hibernate sessions and manages associations of sessions to 
     * clients.
     */
    private class SessionManager {
	
	/*Session->Session Holder mapping*/
	private HashMap<Session, Object> sessions;
	private SessionFactory sf;
	private boolean expand;
	
	/**
	 * Constructor
	 * 
	 * @param f - The factory to get sessions from
	 * @param initSessions - Initial number of sessions to maintain
	 * @param expand - Indicates whether the session manager will expand
	 * the session pool if the all sessions are in use
	 */
	public SessionManager(SessionFactory f, int initSessions, 
		boolean expand) {
	    sf = f;
	    this.expand = expand;
	    sessions = new HashMap<Session, Object>();
	    
	    for (int i = 0; i < initSessions; i++) 
		sessions.put(sf.openSession(), this);
	    
	    logger.info("Hibernate session manager init: pool size " 
			+ sessions.size());
	}
	
	/**
	 * Returns a session to the holder object
	 * @param holder The object to which the returned session is bound to 
	 */
	public synchronized Session getSession(Object holder) {
	    Iterator<Session> i = sessions.keySet().iterator(); 
	    Session s = null;
	    
	    while(i.hasNext()) {
		s  = i.next();
		if (sessions.get(s) == this)
		    break;
		s = null;
	    }
	    
	    //Pool is full, expand it
	    if (s == null && expand){
		int size = sessions.size() / 2;
		
		for (int j = 0; j < size; j++) 
			sessions.put(sf.openSession(), this);
		
		logger.info("Expanded Hibernate session pool to size " 
			+ sessions.size());
		return getSession(holder);
	    } 
	    
	    if(s != null)
		sessions.put(s, holder);
	    
	    return s;
	}
	
	/**
	 * Return a session to the session manager and release the binding to
	 * the holder object
	 * @param s
	 */
	public synchronized void returnSession(Session s) {
	    if(sessions.containsKey(s)) {
		sessions.put(s, this);
	    }
	}
    }

    private boolean getJDBCConnection(String driver, String url, String dialect) {
        if ( (driver==null) || (url==null) || (dialect==null) ) {
            dbClass = null;
            dbURL = null;
            dbDialect = null;
            dbConnection = null;
            return false;
        }

        try {
            Class.forName(driver).newInstance();
            logger.info("Created JDBC instance for " + driver);
            Connection c = DriverManager.getConnection(url);
            c.setAutoCommit(false);
            dbClass = driver;
            dbURL = url;
            dbDialect = dialect;
            dbConnection = c;
            return true;
        } catch (InstantiationException e) {
            logger.warning("Could not instantiate JDBC connection for " + driver);
        } catch (ClassNotFoundException e) {
            logger.warning("Could not get class for JDBC driver " + driver);
        } catch (IllegalAccessException e) {
            logger.warning("SEGV. Core dumped.");
        } catch (SQLException e) {
            logger.warning("SQL Exception while instantiating " + driver);
        }

        dbClass = null;
        dbURL = null;
        dbDialect = null;
        dbConnection = null;
        return false;
    }

    /**
     * Attempt to get the Derby JDBC connector and initialize
     * a connection to the Derby instance -- this is intended
     * to be a debug fallback routine during development.
     *
     * @return @c true on success
     */
    private boolean getDerbyJDBC() {
        return getJDBCConnection(
            "org.apache.derby.jdbc.EmbeddedDriver",
            "jdbc:derby:derbyDB;create=true",
            "org.hibernate.dialect.DerbyDialect");
    }

    private void initHibernate() {
	SessionFactory sf = null;
        logger.info("Initializing Hibernate");
        try {
            Configuration c = new Configuration();
            c.configure();
            // c now holds the configuration from hibernate.cfg.xml, need
            // to override some of those properties.
            c.setProperty("hibernate.connection.driver_class", dbClass);
            c.setProperty("hibernate.connection.url", dbURL);
            c.setProperty("hibernate.connection.username", "alitheia");
            c.setProperty("hibernate.connection.password", "");
            c.setProperty("hibernate.connection.dialect", dbDialect);
            sf = c.buildSessionFactory();
            sm = new SessionManager(sf, 10, true);
            
        } catch (Throwable e) {
            logger.severe("Failed to initialize Hibernate: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public DBServiceImpl( BundleContext bc ) {
        ServiceReference serviceref =
	    bc.getServiceReference("eu.sqooss.service.logging.LogManager");
        logService = (LogManager) bc.getService(serviceref);
        logger = logService.createLogger("sqooss.database");
	if (logger != null) {
            logger.info("DB service created.");
        } else {
            System.out.println("# DB service failed to get logger.");
        }

        dbURL = null;
        dbClass = null;
        dbDialect = null;
        if (!getJDBCConnection(bc.getProperty("eu.sqooss.db.driver"),
                                bc.getProperty("eu.sqooss.db.url"),
                                bc.getProperty("eu.sqooss.db.dialect"))) {
            if (!Boolean.valueOf(bc.getProperty("eu.sqooss.db.fallback.enable")) || !getDerbyJDBC()) {
                logger.severe("DB service got no JDBC connectors.");
            }
        }

        if (dbClass != null) {
            logger.info("Using JDBC " + dbClass);
            initHibernate();
        } else {
            logger.severe("Hibernate will not be initialized.");
            // TODO: Throw something to prevent the bundle from being started?
        }
    }

    public void addRecord(DAObject record) {
        Session s = sm.getSession(this);
        s.beginTransaction();
        s.save(record);
        s.getTransaction().commit();
        sm.returnSession(s);
    }

    public List doSQL(String sql) {
	Session s = sm.getSession(this);
        s.beginTransaction();
        List result = s.createSQLQuery(sql).list();
        s.getTransaction().commit();
        sm.returnSession(s);

        return result;
    }

    public List doHQL(String hql) {
	Session s = sm.getSession(this);
	s.beginTransaction();
	List result = s.createQuery(hql).list();
	s.getTransaction().commit();
	sm.returnSession(s);

	return result;
    }

    public Session getSession(Object holder) {
	return sm.getSession(holder);
    }

    public void returnSession(Session s) {
	sm.returnSession(s);
    }

    public void addRecord(Session s, DAObject record) {
	s.save(record);
    }

    public List doHQL(Session s, String hql) {
	return s.createQuery(hql).list();
    }

    public List doSQL(Session s, String sql) {
	return s.createSQLQuery(sql).list();
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

