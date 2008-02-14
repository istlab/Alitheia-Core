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

import java.net.URL;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.QueryException;
import org.hibernate.TransactionException;
import org.hibernate.JDBCException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.impl.service.logging.LoggerImpl;

public class DBServiceImpl implements DBService {

    private static final String DB_DRIVER_PROPERTY = "eu.sqooss.db.driver";
    private static final String DB_CONNECTION_URL_PROPERTY = "eu.sqooss.db.url";
    private static final String DB_DIALECT_PROPERTY = "eu.sqooss.db.dialect";
    private static final String HIBERNATE_CONFIG_PROPERTY = "eu.sqooss.hibernate.config";

    /* Those two should be runtime configuration options */
    private static final int INIT_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 100;

    private Logger logger = null;
    // This is the database connection; we may want to do more pooling here.
    private Connection dbConnection = null;
    // Store the class and URL of the database to hand off to
    // Hibernate so that it obeys the fallback from Postgres to Derby as well.
    private String dbClass, dbURL, dbDialect;
    private SessionManager sm = null;

    /**
     * The simplest possible Session pool implementation. Maintains a pool of
     * active hibernate sessions and manages associations of sessions to
     * clients. <it>It only supports one session per client object</it>
     */
    private class SessionManager {

        /* Session->Session Holder mapping */
        private HashMap<Session, Object> sessions;
        private SessionFactory sf;
        private boolean expand;

        /**
         * Constructor
         *
         * @param f -
         *            The factory to get sessions from
         * @param expand -
         *            Indicates whether the session manager will expand the
         *            session pool if the all sessions are in use
         */
        public SessionManager(SessionFactory f, boolean expand) {
            sf = f;
            this.expand = expand;
            sessions = new HashMap<Session, Object>();

            for (int i = 0; i < INIT_POOL_SIZE; i++)
                sessions.put(sf.openSession(), this);

            logger.info("Hibernate session manager init: pool size "
                    + sessions.size());
        }

        /**
         * Returns a session to the holder object
         *
         * @param holder
         *            The object to which the returned session is bound to
         * @throws Exception
         */
        public synchronized Session getSession(Object holder) throws Exception {
            Iterator<Session> i = sessions.keySet().iterator();
            Session s = null;

            while (i.hasNext()) {
                s = i.next();
                if (sessions.get(s) == this)
                    break;
                s = null;
            }

            // Pool is full, expand it
            if (s == null && expand) {
                int size = sessions.size() / 2;

                if (size + sessions.size() >= MAX_POOL_SIZE)
                    size = MAX_POOL_SIZE - sessions.size();

                if (MAX_POOL_SIZE == sessions.size())
                    throw new Exception("SessionManager: Cannot serve more "
                            + "than " + MAX_POOL_SIZE + " sessions");

                for (int j = 0; j < size; j++)
                    sessions.put(sf.openSession(), this);

                logger.info("Expanded Hibernate session pool to size "
                        + sessions.size());
                return getSession(holder);
            }

            if (s != null)
                sessions.put(s, holder);

            return s;
        }

        /**
         * Return a session to the session manager and release the binding to
         * the holder object
         *
         * @param s
         */
        public synchronized void returnSession(Session s) {
            if (sessions.containsKey(s)) {
                sessions.put(s, this);
            }
        }
    }

    private void logSQLException(SQLException e) {

        while (e != null) {
            String message = String.format("SQLException: SQL State:%s, Error Code:%d, Message:%s",
                    e.getSQLState(), e.getErrorCode(), e.getMessage());
            logger.error(message);
            e = e.getNextException();
        }
    }

    private void logExceptionAndRollbackTransaction( HibernateException e, Transaction tx) {
        if (tx != null) {
            logger.error("Error during database transaction: " + e.getMessage() + ". Rolling back transaction.");
            try {
                tx.rollback();
            } catch (HibernateException ex) {
                logger.error("Error while rolling back failed transaction."
                        + " DB may be left in inconsistent state: " + ex.getMessage());
            }
        } else {
            logger.error("Database session error: " + e.getMessage());
        }
    }
    
    private boolean getJDBCConnection(String driver, String url, String dialect) {
        
        if ((driver == null) || (url == null) || (dialect == null)) {
            dbClass = null;
            dbURL = null;
            dbDialect = null;
            dbConnection = null;
            return false;
        }
        
        try {
            Class.forName(driver).newInstance();
            logger.info("Created instance of " + driver);
        } catch (InstantiationException e) {
            logger.error("Unable to instantiate the JDBC driver " + driver
                    + " : " + e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            System.err.println("Unable to load the JDBC driver");
            return false;
        } catch (IllegalAccessException e) {
            System.err.println("Not allowed to access the JDBC driver");
            return false;
        }
        
        try {
            Connection c = DriverManager.getConnection(url);
            c.setAutoCommit(false);
            dbClass = driver;
            dbURL = url;
            dbDialect = dialect;
            dbConnection = c;
            return true;
        } catch (SQLException e) {
            logSQLException(e);
        }

        dbClass = null;
        dbURL = null;
        dbDialect = null;
        dbConnection = null;
        return false;
    }

    /**
     * Attempt to get the Derby JDBC connector and initialize a connection to
     * the Derby instance -- this is intended to be a debug fallback routine
     * during development.
     * 
     * @return
     * @c true on success
     */
    private boolean getDerbyJDBC() {
        return getJDBCConnection("org.apache.derby.jdbc.EmbeddedDriver",
                "jdbc:derby:derbyDB;create=true",
        "org.hibernate.dialect.DerbyDialect");
    }

    private void initHibernate(URL configFileURL) {
        SessionFactory sf = null;
        logger.info("Initializing Hibernate");
        try {
            Configuration c = new Configuration().configure(configFileURL);
            // c now holds the configuration from hibernate.cfg.xml, need
            // to override some of those properties.
            c.setProperty("hibernate.connection.driver_class", dbClass);
            c.setProperty("hibernate.connection.url", dbURL);
            c.setProperty("hibernate.connection.username", "alitheia");
            c.setProperty("hibernate.connection.password", "");
            c.setProperty("hibernate.connection.dialect", dbDialect);
            sf = c.buildSessionFactory();
            sm = new SessionManager(sf, true);

        } catch (Throwable e) {
            logger.error("Failed to initialize Hibernate: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Constructor for creating a DBServiceImpl outside the SQO-OSS system, e.g.,
     * for testing purposes.
     */
    public DBServiceImpl() {
        String dbDriverProp;
        String dbConnectionURLProp;
        String dbDialectProp;
        String hibernateConfigURLProp;
        URL hibernateConfigURL = null;

        this.logger = new LoggerImpl("standalone");
        
        dbDriverProp = System.getProperty(DB_DRIVER_PROPERTY);
        if (dbDriverProp == null) {
            System.err.println("Could not get " + DB_DRIVER_PROPERTY + " property.");
            System.exit(1);
        }
        dbConnectionURLProp = System.getProperty(DB_CONNECTION_URL_PROPERTY);
        if (dbConnectionURLProp == null) {
            System.err.println("Could not get " + DB_CONNECTION_URL_PROPERTY + " property.");
            System.exit(1);
        }
        dbDialectProp = System.getProperty(DB_DIALECT_PROPERTY);
        if (dbDialectProp == null) {
            System.err.println("Could not get " + DB_DIALECT_PROPERTY + " property.");
            System.exit(1);
        }
        if (!getJDBCConnection(dbDriverProp, dbConnectionURLProp, dbDialectProp)) {
            System.err.println("Could not get JDBC connection.");
            System.exit(1);
        }

        hibernateConfigURLProp = System.getProperty(HIBERNATE_CONFIG_PROPERTY);

        try {
            hibernateConfigURL = new URL(hibernateConfigURLProp);
        } catch (MalformedURLException e) {
            System.err.println("Could not get hibernate configuration file: " + HIBERNATE_CONFIG_PROPERTY);
            System.exit(1);
        }

        initHibernate(hibernateConfigURL);
    }

    /**
     * Constructor for creating a DBServiceImpl inside the SQO-OSS system.
     * 
     * @param bc The current BundleContext
     * @param l The current Logger
     */
    public DBServiceImpl(BundleContext bc, Logger l) {
        logger = l;

        dbURL = null;
        dbClass = null;
        dbDialect = null;
        if (!getJDBCConnection(bc.getProperty(DB_DRIVER_PROPERTY),
                bc.getProperty(DB_CONNECTION_URL_PROPERTY),
                bc.getProperty(DB_DIALECT_PROPERTY))) {
            if (!Boolean
                    .valueOf(bc.getProperty("eu.sqooss.db.fallback.enable"))
                    || !getDerbyJDBC()) {
                logger.error("DB service got no JDBC connectors.");
            }
        }

        if (dbClass != null) {
            logger.info("Using JDBC " + dbClass);
            initHibernate(bc.getBundle().getEntry("HIBERNATE_CONFIG_PROPERTY"));
        } else {
            logger.error("Hibernate will not be initialized.");
            // TODO: Throw something to prevent the bundle from being started?
        }
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#findObjectById(java.lang.Class, long)
     */
    public <T extends DAObject> T findObjectById(Class<T> daoClass, long id) {
        Session s = getSession(this);
        try {
            s.beginTransaction();
            T obj = findObjectById(s, daoClass, id);
            s.getTransaction().commit();
            return obj;
        } finally {
            returnSession(s);
        }
    }
    
    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#findObjectById(org.hibernate.Session, java.lang.Class, long)
     */
    @SuppressWarnings("unchecked")
    public <T extends DAObject> T findObjectById(Session s, Class<T> daoClass, long id) {
        return (T) s.get(daoClass, id);
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#findObjectByProperties(org.hibernate.Session, java.lang.Class, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    public <T extends DAObject> List<T> findObjectByProperties(Class<T> daoClass, Map<String,Object> properties ) {

        Session s = getSession(this);
        try {
            s.beginTransaction();
            List result = findObjectByProperties(s, daoClass, properties);
            s.getTransaction().commit();
            return result;
        } finally {
            returnSession(s);
        }
    }  

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#findObjectByProperties(org.hibernate.Session, java.lang.Class, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    public <T extends DAObject> List<T> findObjectByProperties(Session s, Class<T> daoClass, Map<String,Object> properties ) {

        if ( !DAObject.class.isAssignableFrom(daoClass) ) {
            // throw an exception instead ?
            return new ArrayList<T>(0);
        }

        // TODO maybe check that the properties are valid (e.g. with java.bean.PropertyDescriptor)

        Map<String,Object> parameterMap = new HashMap<String,Object>();
        StringBuffer whereClause = new StringBuffer();
        for (String key : properties.keySet()) {
            whereClause.append( whereClause.length() == 0 ? " where " : " and " );
            whereClause.append( daoClass.getName() + "." + key + "=:_" + key );
            parameterMap.put( "_" + key, properties.get(key) );
        }
        return (List<T>) doHQL( s, "from " + daoClass.getName() + whereClause, parameterMap );
    }

    public List doSQL(String sql) {
        Session s = getSession(this);
        Transaction tx = null;
        try {
            tx = s.beginTransaction();
            List result = doSQL(s, sql);
            tx.commit();
            return result;
        } catch( TransactionException e ) {
            logger.error("Transaction error: " + e.getMessage());
            throw e;
        } catch( HibernateException e ) {
            logExceptionAndRollbackTransaction(e,tx);
            throw e;
        } finally {
            returnSession(s);
        }
    }

    public List doSQL(Session s, String sql) {
        try {
            return s.createSQLQuery(sql).list();
        } catch( JDBCException e ) {
            logSQLException(e.getSQLException());
            throw e;
        }
    }

    public List doSQL(String sql, Map<String, Object> params) {
        Session s = getSession(this);
        Transaction tx = null;
        try {
            tx = s.beginTransaction();
            List result = doSQL(s, sql, params);
            tx.commit();
            return result;
        } catch( TransactionException e ) {
            logger.error("Transaction error: " + e.getMessage());
            throw e;
        } catch( HibernateException e ) {
            logExceptionAndRollbackTransaction(e,tx);
            throw e;
        } finally {
            returnSession(s);
        }
    }

    public List doSQL(Session s, String sql, Map<String, Object> params) {
        try {
            Query query = s.createSQLQuery(sql);
            Iterator<String> i = params.keySet().iterator();
            while(i.hasNext()) {
                String paramName = i.next();
                query.setParameter(paramName, params.get(paramName));
            }
            return query.list();
        } catch (JDBCException e) {
            logSQLException(e.getSQLException());
            throw e;
        }
    }

    public List doHQL(String hql) {
        return doHQL(hql, null, null);
    }

    public List doHQL(String hql, Map<String, Object> params) {
        return doHQL(hql, params, null);
    }

    public List doHQL(String hql, Map<String, Object> params,
            Map<String, Collection> collectionParams) {
        Session s = getSession(this);
        Transaction tx = null;
        try {
            tx = s.beginTransaction();
            List result = doHQL(s, hql, params, collectionParams);
            tx.commit();
            return result;
        } catch( TransactionException e ) {
            logger.error("Transaction error: " + e.getMessage());
            throw e;
        } catch( HibernateException e ) {
            logExceptionAndRollbackTransaction(e,tx);
            throw e;
        } finally {
            returnSession(s);
        }
    }

    public List doHQL(Session s, String hql) {
        return doHQL(s, hql, null, null);
    }

    public List doHQL(Session s, String hql, Map<String, Object> params) {
        return doHQL(s, hql, params, null);
    }

    public List doHQL(Session s, String hql, Map<String, Object> params,
            Map<String, Collection> collectionParams) {
        try {
            Query query = s.createQuery(hql);
            if (params != null) {
                for ( String param : params.keySet() ) {
                    query.setParameter(param, params.get(param));
                }
            }
            if (collectionParams != null) {
                for ( String param : collectionParams.keySet() ) {
                    query.setParameterList(param, collectionParams.get(param));
                }
            }
            return query.list();
        } catch (JDBCException e) {
            logSQLException(e.getSQLException());
            throw e;
        } catch (QueryException e) {
            logger.error("Error while executing HQL query: " +  e.getMessage() + ". HQL query was : " + e.getQueryString());
            throw e;
        }
    }

    public Session getSession(Object holder) {
        Session s = null;
        try {
            s = sm.getSession(holder);
        } catch (Exception e) {
            logger.error("getSession(): " + e.getMessage());
        }
        return s;
    }

    public void returnSession(Session s) {
        sm.returnSession(s);
    }

    public boolean addRecord(DAObject record) {
        Session s = getSession(this);
        try {
            return addRecord(s, record);
        } finally {
            returnSession(s);
        }
    }

    public boolean addRecord(Session s, DAObject record) {
        ArrayList<DAObject> tmpList = new ArrayList<DAObject>(1);
        tmpList.add(record);
        return addRecords(s, tmpList);
    }

    public boolean deleteRecord(DAObject record) {
        Session s = getSession(this);
        try {
            return deleteRecord(s, record);            
        } finally {
            returnSession(s);            
        }
    }

    public boolean deleteRecord(Session s, DAObject record) {
        ArrayList<DAObject> tmpList = new ArrayList<DAObject>(1);
        tmpList.add(record);
        return deleteRecords(s, tmpList);
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#addRecords(java.util.List)
     */

    public boolean addRecords(List<DAObject> records) {
        Session s = getSession(this);
        try {
            return addRecords(s, records);
        } finally {
            returnSession(s);
        }
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#addRecords(org.hibernate.Session, java.util.List)
     */

    public boolean addRecords(Session s, List<DAObject> records) {

        if( s == null )
            return false;

        Transaction tx = null;
        DAObject lastRecord = null;
        try {
            tx = s.beginTransaction();
            for (DAObject record : records) {
                lastRecord = record;
                s.save(record);				
            }
            lastRecord = null;
            tx.commit();
        }
        catch (HibernateException e) {
            if (tx != null) {
                if (lastRecord != null) {
                    logger.error("Failed to add object "
                            + "[" + lastRecord.getClass().getName() + ":" + lastRecord.getId() + "]"
                            + " to the database: " + e.getMessage());
                } else {
                    logger.error("Error while commiting database transaction:"
                            + e.getMessage());
                }
                try {
                    tx.rollback();
                } catch (HibernateException ex) {
                    logger.error("Error while rolling back failed transaction."
                            + " DB may be left in inconsistent state:" + ex.getMessage());
                }
            } else {
                logger.error("Error while initializing database transaction:" + e.getMessage());
            }
            return false;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#deleteRecords(java.util.List)
     */

    public boolean deleteRecords(List<DAObject> records) {
        Session s = getSession(this);
        try {
            return deleteRecords(s, records);
        } finally {
            returnSession(s);
        }
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#deleteRecords(org.hibernate.Session, java.util.List)
     */

    public boolean deleteRecords(Session s, List<DAObject> records) {

        if( s == null )
            return false;

        Transaction tx = null;
        DAObject lastRecord = null;
        try {
            tx = s.beginTransaction();
            for (DAObject record : records) {
                lastRecord = record;
                s.delete(record);				
            }
            lastRecord = null;
            tx.commit();
        }
        catch (HibernateException e) {
            if (tx != null) {
                if (lastRecord != null) {
                    logger.error("Failed to remove object "
                            + "[" + lastRecord.getClass().getName() + ":" + lastRecord.getId() + "]"
                            + " from the database: " + e.getMessage());
                } else {
                    logger.error("Error while commiting database transaction:"
                            + e.getMessage());
                }
                try {
                    tx.rollback();
                } catch (HibernateException ex) {
                    logger.error("Error while rolling back failed transaction."
                            + " DB may be left in inconsistent state:" + ex.getMessage());
                }
            } else {
                logger.error("Error while initializing database transaction:" + e.getMessage());
            }
            return false;
        }

        return true;
    }

    public Object selfTest() {
        Object[] o = new Object[INIT_POOL_SIZE + 1];
        Session[] s = new Session[INIT_POOL_SIZE + 1];

        try {
            for (int i = 0; i < INIT_POOL_SIZE + 1; i++) {
                s[i] = null;
            }

            for (int i = 0; i < INIT_POOL_SIZE + 1; i++) {
                s[i] = getSession(o[i]);
            }

            for (int i = 0; i < INIT_POOL_SIZE + 1; i++) {
                if (s[i] == null) {
                    return "Tests failed, a session is null";
                }
            }
        } catch (Exception e) {
            return "Tests failed: " + e.getMessage();
        }

        if (sm.sessions.size() != (INIT_POOL_SIZE + (INIT_POOL_SIZE / 2))) {
            return "Tests failed: Session pool size should be "
            + (INIT_POOL_SIZE + (INIT_POOL_SIZE / 2)) + ", it is "
            + sm.sessions.size();
        }

        o = new Object[MAX_POOL_SIZE + 3];
        s = new Session[MAX_POOL_SIZE + 3];

        for (int i = 0; i < MAX_POOL_SIZE + 3; i++) {
            s[i] = getSession(o[i]);
        }

        if (s[MAX_POOL_SIZE + 2] != null) {
            return ("Tests failed, the session pool should have returned null");
        }

        for (int i = 0; i < MAX_POOL_SIZE + 3; i++) {
            returnSession(s[i]);
        }

        return null;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

