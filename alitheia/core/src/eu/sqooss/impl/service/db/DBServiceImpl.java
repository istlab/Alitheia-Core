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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.QueryException;
import org.hibernate.JDBCException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.impl.service.logging.LoggerImpl;

/**
 * Implementation of the Database service, providing a Hibernate Session pool
 * and high-level and low-level data access APIs.
 * 
 * @author ???, Romain Pokrzywka
 * 
 */
public class DBServiceImpl implements DBService {

    private static final String DB_DRIVER_PROPERTY = "eu.sqooss.db.driver";
    private static final String DB_CONNECTION_URL_PROPERTY = "eu.sqooss.db.url";
    private static final String DB_DIALECT_PROPERTY = "eu.sqooss.db.dialect";
    private static final String HIBERNATE_CONFIG_PROPERTY = "eu.sqooss.hibernate.config";

    private Logger logger = null;
    // Store the class and URL of the database to hand off to
    // Hibernate so that it obeys the fallback from Postgres to Derby as well.
    private String dbClass, dbURL, dbDialect;
    private SessionFactory sessionFactory = null;

    private void logSQLException(SQLException e) {

        while (e != null) {
            String message = String.format("SQLException: SQL State:%s, Error Code:%d, Message:%s",
                    e.getSQLState(), e.getErrorCode(), e.getMessage());
            logger.warn(message);
            e = e.getNextException();
        }
    }
    
    private void logExceptionAndTerminateSession( Exception e ) {
        if ( e instanceof JDBCException ) {
            JDBCException jdbce = (JDBCException) e;
            logSQLException(jdbce.getSQLException());
        }
        logger.warn("Exception caught during database session: " + e.getMessage() 
                + ". Rolling back current transaction and terminating session...");
        Session s = null;
        try {
            s = sessionFactory.getCurrentSession();
            s.getTransaction().rollback();
        } catch (HibernateException e1) {
            logger.error("Error while rolling back failed transaction :" + e1.getMessage());
            if ( s != null ) {
                try {
                    s.close();
                } catch ( HibernateException e2) {}
            }
        }
        
    }
   
    private boolean checkSession() {
        if ( !isDBSessionActive() ) {
            logger.warn("Trying to call a DBService method without an active session");
            return false;
        }
        return true;
    }
    
    private boolean getJDBCConnection(String driver, String url, String dialect) {
        
        if ((driver == null) || (url == null) || (dialect == null)) {
            dbClass = null;
            dbURL = null;
            dbDialect = null;
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
            return true;
        } catch (SQLException e) {
            logSQLException(e);
        }

        dbClass = null;
        dbURL = null;
        dbDialect = null;
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
        logger.info("Initializing Hibernate with URL <" + configFileURL + ">");
        if (configFileURL == null) {
            logger.warn("Ignoring null URL.");
            return;
        }
        try {
            Configuration c = new Configuration().configure(configFileURL);
            // c now holds the configuration from hibernate.cfg.xml, need
            // to override some of those properties.
            c.setProperty("hibernate.connection.driver_class", dbClass);
            c.setProperty("hibernate.connection.url", dbURL);
            c.setProperty("hibernate.connection.username", "alitheia");
            c.setProperty("hibernate.connection.password", "");
            c.setProperty("hibernate.connection.dialect", dbDialect);
            sessionFactory = c.buildSessionFactory();

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
        if (hibernateConfigURLProp == null) {
            System.err.println("Could not get " + HIBERNATE_CONFIG_PROPERTY + " property.");
            System.exit(1);
        }

        try {
            // The following is necessary for MS-Windows environments
            hibernateConfigURLProp = hibernateConfigURLProp.replaceFirst("^file://[a-zA-z]:", "file://");
            hibernateConfigURL = new URL(hibernateConfigURLProp);
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL for hibernate configuration file: " + hibernateConfigURLProp);
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
            initHibernate(bc.getBundle().getEntry("/hibernate.cfg.xml"));
        } else {
            logger.error("Hibernate will not be initialized.");
            // TODO: Throw something to prevent the bundle from being started?
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends DAObject> T findObjectById(Class<T> daoClass, long id) {
        if ( !checkSession() )
            return null;
        
        try {
            Session s = sessionFactory.getCurrentSession();
            return (T) s.get(daoClass, id);
        } catch (HibernateException e) {
            logExceptionAndTerminateSession(e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends DAObject> List<T> findObjectsByProperties(Class<T> daoClass, Map<String,Object> properties ) {
        if( !checkSession() )
            return Collections.emptyList();

        // TODO maybe check that the properties are valid (e.g. with java.bean.PropertyDescriptor)

        Map<String,Object> parameterMap = new HashMap<String,Object>();
        StringBuffer whereClause = new StringBuffer();
        for (String key : properties.keySet()) {
            whereClause.append( whereClause.length() == 0 ? " where " : " and " );
            // We use "foo" as the name of the object
            whereClause.append("foo" + "." + key + "=:_" + key );
            parameterMap.put( "_" + key, properties.get(key) );
        }
        try {
            // We use "foo" as the name of the object
            return (List<T>) doHQL( "from " + daoClass.getName() + " as foo " + whereClause, parameterMap );
        } catch (QueryException e) {
            logger.warn("findObjectsByProperties(): invalid properties map. Restarting session...");
            // Automatically restart a session
            // (just be careful with preloaded DAOs that become detached)
            startDBSession();
            return Collections.emptyList();
        }
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#doSQL(java.lang.String)
     */
    public List<?> doSQL(String sql)
        throws SQLException {
        return doSQL(sql, null);
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#doSQL(java.lang.String, java.util.Map)
     */
    public List<?> doSQL(String sql, Map<String, Object> params)
        throws SQLException, QueryException {
        boolean autoSession = !isDBSessionActive();
        try {
            Session s = sessionFactory.getCurrentSession();
            if (autoSession) {
                s.beginTransaction();
            }
            Query query = s.createSQLQuery(sql);
            if ( params != null ) {
                for ( String param : params.keySet() ) {
                    query.setParameter(param, params.get(param));
                }
            }
            List<?> result = query.list();
            if (autoSession) {
                s.getTransaction().commit();
            }
            return result;
        } catch ( JDBCException e ) {
            logExceptionAndTerminateSession(e);
            throw e.getSQLException();
        } catch ( QueryException e ) {
            logExceptionAndTerminateSession(e);
            throw e;
        } catch( HibernateException e ) {
            logExceptionAndTerminateSession(e);
            return Collections.emptyList();
        }
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#doHQL(java.lang.String)
     */
    public List<?> doHQL(String hql)
        throws QueryException {
        return doHQL(hql, null, null);
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#doHQL(java.lang.String, java.util.Map)
     */
    public List<?> doHQL(String hql, Map<String, Object> params) 
        throws QueryException {
        return doHQL(hql, params, null);
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#doHQL(java.lang.String, java.util.Map, java.util.Map)
     */
    public List<?> doHQL(String hql, Map<String, Object> params,
            Map<String, Collection> collectionParams) 
        throws QueryException {
        if ( !checkSession() ) {
            return Collections.emptyList();
        }
        try {
            Session s = sessionFactory.getCurrentSession();
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
        } catch ( QueryException e ) {
            logExceptionAndTerminateSession(e);
            throw e;
        } catch( HibernateException e ) {
            logExceptionAndTerminateSession(e);
            return Collections.emptyList();
        } catch (ClassCastException e) {
            // Throw a QueryException instead of forwarding the ClassCastException
            // it's more explicit
            QueryException ebis = new QueryException("Invalid HQL query parameter type: "
                                                    + e.getMessage(), e);
            logExceptionAndTerminateSession(ebis);
            throw ebis;
        }
        
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#addRecord(eu.sqooss.service.db.DAObject)
     */
    public boolean addRecord(DAObject record) {
        ArrayList<DAObject> tmpList = new ArrayList<DAObject>(1);
        tmpList.add(record);
        return addRecords(tmpList);
    }

//    /* (non-Javadoc)
//     * @see eu.sqooss.service.db.DBService#updateRecord(eu.sqooss.service.db.DAObject)
//     */
//    public boolean updateRecord(DAObject record) {
//        ArrayList<DAObject> tmpList = new ArrayList<DAObject>(1);
//        tmpList.add(record);
//        return updateRecords(tmpList);
//    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#deleteRecord(eu.sqooss.service.db.DAObject)
     */
    public boolean deleteRecord(DAObject record) {
        ArrayList<DAObject> tmpList = new ArrayList<DAObject>(1);
        tmpList.add(record);
        return deleteRecords(tmpList);
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#addRecords(java.util.List)
     */
    public boolean addRecords(List<DAObject> records) {
        if( !checkSession() )
            return false;

        DAObject lastRecord = null;
        try {
            Session s = sessionFactory.getCurrentSession();
            for (DAObject record : records) {
                lastRecord = record;
                s.save(record);				
            }
            lastRecord = null;
            return true;
        } catch (HibernateException e) {
            if (lastRecord != null) {
                logger.error("Failed to add object "
                        + "[" + lastRecord.getClass().getName() + ":" + lastRecord.getId() + "]"
                        + " to the database: " + e.getMessage());
            }
            logExceptionAndTerminateSession(e);
            return false;
        }
    }

//    /* (non-Javadoc)
//     * @see eu.sqooss.service.db.DBService#updateRecords(java.util.List)
//     */
//    public boolean updateRecords(List<DAObject> records) {
//        if( !checkSession() )
//            return false;
//
//        DAObject lastRecord = null;
//        try {
//            Session s = sessionFactory.getCurrentSession();
//            for (DAObject record : records) {
//                lastRecord = record;
//                s.update(record);               
//            }
//            lastRecord = null;
//            return true;
//        } catch (HibernateException e) {
//            if (lastRecord != null) {
//                logger.error("Failed to update object "
//                        + "[" + lastRecord.getClass().getName() + ":" + lastRecord.getId() + "]"
//                        + " in the database: " + e.getMessage());
//            }
//            logExceptionAndTerminateSession(e);
//            return false;
//        }
//    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#deleteRecords(java.util.List)
     */
    public boolean deleteRecords(List<DAObject> records) {
        if( !checkSession() )
            return false;

        DAObject lastRecord = null;
        try {
            Session s = sessionFactory.getCurrentSession();
            for (DAObject record : records) {
                lastRecord = record;
                s.delete(record);				
            }
            lastRecord = null;
            return true;
        } catch (HibernateException e) {
            if (lastRecord != null) {
                logger.error("Failed to remove object "
                        + "[" + lastRecord.getClass().getName() + ":" + lastRecord.getId() + "]"
                        + " from the database: " + e.getMessage());
            }
            logExceptionAndTerminateSession(e);
            return false;
        }
    }
    
    public boolean addAssociation(Object compositeKey) {
        if( !checkSession() )
            return false;

        try {
            Session s = sessionFactory.getCurrentSession();
            s.save(compositeKey);
            return true;
        } catch (HibernateException e) {
            logExceptionAndTerminateSession(e);
            return false;
        }
    }

    public boolean deleteAssociation(Object compositeKey) {
        if( !checkSession() )
            return false;

        try {
            Session s = sessionFactory.getCurrentSession();
            s.delete(compositeKey);
            return true;
        } catch (HibernateException e) {
            logExceptionAndTerminateSession(e);
            return false;
        }
    }

    public Object selfTest() {
        
        if ( !startDBSession() )
            return "couldn't initialize DB session";
        
        StoredProject testProject = findObjectById(StoredProject.class, 1);
        if ( testProject != null ) {
            logger.info("found a project with ID 1 : " + testProject.getName());
        } else {
            // Not an error, there may just not be any projects installed yet
            logger.info("found no project with ID 1, findObjectById() returned null");
            rollbackDBSession();
            return null;
        }
        // This one should fail
        StoredProject unknownProject = findObjectById(StoredProject.class, -1);
        if ( unknownProject != null ) {
            rollbackDBSession();
            return "found a project with ID -1 !";
        }
        // This doesn't even compile - good
        //ProjectVersion testVersion = findObjectById(StoredProject.class, 1);
        
        Map<String, Object> props = Collections.emptyMap();
        List<StoredProject> projectList = findObjectsByProperties(StoredProject.class, props);
        if ( projectList.size() != StoredProject.getProjectCount() ) {
            rollbackDBSession();
            return "findObjectsByProperties() empty params test failed";
        }
        
        props = new HashMap<String, Object>();
        props.put("name", testProject.getName());
        projectList = findObjectsByProperties(StoredProject.class, props);
        if ( projectList.size() != 1 || projectList.get(0).getId() != testProject.getId() ) {
            rollbackDBSession();
            return "findObjectsByProperties() name param test failed";
        }
        props.put("name", "no_project_would_ever_be_called_that");
        projectList = findObjectsByProperties(StoredProject.class, props);
        if ( !projectList.isEmpty() ) {
            rollbackDBSession();
            return "findObjectsByProperties invalid name param test failed";
        }
        props.clear();
        props.put("name", testProject.getName());
        props.put("id", testProject.getId());
        projectList = findObjectsByProperties(StoredProject.class, props);
        if ( projectList.size() != 1 || projectList.get(0).getId() != testProject.getId() ) {
            rollbackDBSession();
            return "findObjectsByProperties() name+id param test failed";
        }
        
        props.put("id", 0L);
        projectList = findObjectsByProperties(StoredProject.class, props);
        if ( !projectList.isEmpty() ) {
            rollbackDBSession();
            return "findObjectsByProperties() invalid id param test failed";
        }

        props.put("id", "oops");
        projectList = findObjectsByProperties(StoredProject.class, props);
        if ( !projectList.isEmpty() ) {
            rollbackDBSession();
            return "findObjectsByProperties() invalid param type test failed";
        }

        // Watch out for that one : 1 is an int constant, so it gets boxed in an Integer
        // however the id is a Long, so this yields an exception when the query is executed
        props.put("id", 1);
        projectList = findObjectsByProperties(StoredProject.class, props);
        if ( !projectList.isEmpty() ) {
            rollbackDBSession();
            return "findObjectsByProperties() int constant param test failed";
        }
        // This is correct
        props.put("id", 1L);
        projectList = findObjectsByProperties(StoredProject.class, props);
        if ( projectList.isEmpty() ) {
            rollbackDBSession();
            return "findObjectsByProperties() long constant param test failed";
        }
        
        props.clear();
        props.put("thatonedoesntexist", "duh");
        projectList = findObjectsByProperties(StoredProject.class, props);
        if ( !projectList.isEmpty() ) {
            rollbackDBSession();
            return "findObjectsByProperties() invalid param name failed";
        }
        
        rollbackDBSession();
        // TODO: add some out-of-session tests
        
        // Test doHQL and doSQL error handling

        startDBSession();
        boolean exceptionThrown = false;
        try {
            doHQL("from Something as a where a.id = 1");
        } catch (QueryException e) {
            exceptionThrown = true;
            if (isDBSessionActive())
                return "DB session still active after exception";
        } catch (Exception e) {
            return "unexpected exception thrown during doHQL() invalid query test"; 
        }
        if (!exceptionThrown) {
            rollbackDBSession();
            return "doHQL() invalid query failed";
        }

        startDBSession();
        exceptionThrown = false;
        props.clear();
        props.put("id", "duh");
        try {
            doHQL("from StoredProject as p where p.id = :id", props);
        } catch (QueryException e) {
            exceptionThrown = true;
            if (isDBSessionActive())
                return "DB session still active after exception";
        } catch (Exception e) {
            return "unexpected exception thrown during doHQL() invalid param test"; 
        }
        if (!exceptionThrown) {
            rollbackDBSession();
            return "doHQL() invalid param failed";
        }

        startDBSession();
        exceptionThrown = false;
        props.clear();
        try {
            doHQL("from StoredProject as p where p.id = :id", props);
        } catch (QueryException e) {
            exceptionThrown = true;
            if (isDBSessionActive())
                return "DB session still active after exception";
        } catch (Exception e) {
            return "unexpected exception thrown during doHQL() missing param test"; 
        }
        if (!exceptionThrown) {
            rollbackDBSession();
            return "doHQL() missing param failed";
        }
        
        startDBSession();
        exceptionThrown = false;
        try {
            doSQL("SELECT * FROM NADA");
        } catch (SQLException e) {
            exceptionThrown = true;
            if (isDBSessionActive())
                return "DB session still active after exception";
        } catch (Exception e) {
            return "unexpected exception thrown during doSQL() invalid query test"; 
        }
        if (!exceptionThrown) {
            rollbackDBSession();
            return "doSQL() invalid query failed";
        }
        
        startDBSession();
        exceptionThrown = false;
        props.clear();
        props.put("id", "duh");
        try {
            doSQL("SELECT * FROM STORED_PROJECT WHERE PROJECT_ID=:id", props);
        } catch (SQLException e) {
            exceptionThrown = true;
            if (isDBSessionActive())
                return "DB session still active after exception";
        } catch (Exception e) {
            return "unexpected exception thrown during doSQL() invalid param test"; 
        }
        if (!exceptionThrown) {
            rollbackDBSession();
            return "doSQL() invalid param failed";
        }

        startDBSession();
        exceptionThrown = false;
        props.clear();
        try {
            doSQL("SELECT * FROM STORED_PROJECT WHERE PROJECT_ID=:id", props);
        } catch (QueryException e) {
            exceptionThrown = true;
            if (isDBSessionActive())
                return "DB session still active after exception";
        } catch (Exception e) {
            return "unexpected exception thrown during doSQL() missing param test"; 
        }
        if (!exceptionThrown) {
            rollbackDBSession();
            return "doSQL() missing param failed";
        }
        
        startDBSession();

        // Now test the addRecord/updateRecord/deleteRecord methods
        
        final String testProjectName = "selfTestTempProject";
        props.clear();
        props.put("name", testProjectName);
        projectList = findObjectsByProperties(StoredProject.class, props);
        if ( !projectList.isEmpty() ) {
            rollbackDBSession();
            return "a project named 'selfTestTempProject' already exists, aborting tests";
        }
        
        testProject = new StoredProject(testProjectName);
        testProject.setBugs("bugz");
        testProject.setContact("kontactz");
        testProject.setMail("mailz");
        testProject.setRepository("repoz");
        testProject.setWebsite("webz");

        // Should still not be in the db yet
        projectList = findObjectsByProperties(StoredProject.class, props);
        if ( !projectList.isEmpty() ) {
            rollbackDBSession();
            return "project 'selfTestTempProject' in the database before calling addRecord ?!";
        }

        if ( !addRecord(testProject) ) {
            return "error while calling addRecord()";
        }
        
        // The following test fails, because of a weird Hibernate behavior:
        // The object is found in the session cache, so Hibernate returns it
        // although it actually doesn't exist in the DB yet (we haven't committed the results)
        // --> So always make sure to commit your changes before the next query
//        projectList = findObjectsByProperties(StoredProject.class, props);
//        if ( !projectList.isEmpty() ) {
//            rollbackDBSession();
//            return "project 'selfTestTempProject' saved in the db before calling commit";
//        }

        if ( !commitDBSession() ) {
            return "error while committing db session after adding a record";
        }
        
        startDBSession();
        projectList = findObjectsByProperties(StoredProject.class, props);
        if ( projectList.isEmpty() ) {
            rollbackDBSession();
            return "project 'selfTestTempProject' not saved in the db";
        }

        // testProject is detached at that point (it belongs to the previous session)
        // so changes should NOT be persisted in the db at the next commit
        testProject.setContact("duh");
        if ( !commitDBSession() ) {
            return "error while committing session afer change to project";
        }        

        props.clear();
        props.put("contact", "duh");
        startDBSession();
        projectList = findObjectsByProperties(StoredProject.class, props);
        if ( !projectList.isEmpty() ) {
            rollbackDBSession();
            return "new contact property for detached DAO was updated in the db";
        }

        // Get a new testProject for the current session
        props.clear();
        props.put("name", testProjectName);
        projectList = findObjectsByProperties(StoredProject.class, props);
        if ( projectList.isEmpty() ) {
            rollbackDBSession();
            return "couldn't find test project";
        }
        testProject = projectList.get(0);
        testProject.setContact("duh");        

        props.clear();
        props.put("contact", "duh");

        // The following test fails, because of a weird Hibernate behavior:
        // The object is found in the session cache, so Hibernate returns it
        // although it actually doesn't exist in the DB yet (we haven't committed the results)
        // --> So always make sure to commit your changes before the next query
//        projectList = findObjectsByProperties(StoredProject.class, props);
//        if ( !projectList.isEmpty() ) {
//            rollbackDBSession();
//            return "new contact property was updated in the db before session commit";
//        }

        if ( !commitDBSession() ) {
            return "error while committing session afer change to project";
        }
        
        startDBSession();
        projectList = findObjectsByProperties(StoredProject.class, props);
        if ( projectList.isEmpty() ) {
            rollbackDBSession();
            return "new contact property was not updated in the db";
        }
        
        if ( !deleteRecord(projectList.get(0)) ) {
            return "error while calling deleteRecord()";
        }

        // The following test fails, because of a weird Hibernate behavior:
        // The object is found in the session cache, so Hibernate returns it
        // although it actually doesn't exist in the DB yet (we haven't committed the results)
        // --> So always make sure to commit your changes before the next query
//        projectList = findObjectsByProperties(StoredProject.class, props);
//        if ( projectList.isEmpty() ) {
//            rollbackDBSession();
//            return "project 'selfTestTempProject' deleted in the db before session commit";
//        }
        
        if ( !commitDBSession() ) {
            return "error while committing session after deleting project";
        }
        
        startDBSession();
        projectList = findObjectsByProperties(StoredProject.class, props);
        if ( !projectList.isEmpty() ) {
            rollbackDBSession();
            return "project 'selfTestTempProject' not deleted in the db";
        }
        
        commitDBSession();
        return null;
    }

    public boolean startDBSession() {
        if( isDBSessionActive() ) {
            logger.debug("startDBSession() - a session was already started for that thread");
            return true;
        }
        
        Session s = null;
        try {
            s = sessionFactory.getCurrentSession();
            //logger.debug("startDBSession: " + s + "[hashcode=" + s.hashCode() + ",open=" + s.isOpen() + "]");
            s.beginTransaction();
        } catch (HibernateException e) {
            logger.error("startDBSession() - error while initializing session: " + e.getMessage());
            if ( s != null ) {
                try {
                    s.close();
                } catch (HibernateException e1) {
                }
            }
            return false;
        }
        return true;
    }

    public boolean commitDBSession() {
        if ( !checkSession() )
            return false;
        
        Session s = null;
        try {
            s = sessionFactory.getCurrentSession();
            //logger.debug("commitDBSession: " + s + "[hashcode=" + s.hashCode() + ",open=" + s.isOpen() + "]");
            s.getTransaction().commit();
        } catch (HibernateException e) {
            logger.error("commitDBSession() - error while committing transaction: " + e.getMessage());
            if ( s != null ) {
                // The docs say to do so
                try {
                    s.getTransaction().rollback();
                } catch (HibernateException e1) {
                    try {
                        s.close();
                    } catch (HibernateException e2) {
                    }
                }
            }
            return false;
        }
        return true;
    }

    public boolean rollbackDBSession() {
        if ( !checkSession() )
            return false;
        
        Session s = null;
        try {
            s = sessionFactory.getCurrentSession();
            s.getTransaction().rollback();
        } catch (HibernateException e) {
            logger.error("commitDBSession() - error while rolling back transaction: " + e.getMessage());
            if ( s != null ) {
                try {
                    s.close();
                } catch (HibernateException e1) {
                }
            }
            return false;
        }
        return true;
    }

    public boolean isDBSessionActive() {
        Session s = null;
        try {
            s = sessionFactory.getCurrentSession();
            return s.getTransaction() != null && s.getTransaction().isActive();
        } catch (HibernateException e) {
            logger.error("isDBSessionActive() - error while checking session status: " + e.getMessage());
            if ( s != null ) {
                try {
                    s.close();
                } catch (HibernateException e1) {
                }
            }
            return false;
        }
    }
    
    public Session getDBSession() {
        return sessionFactory.getCurrentSession();
    }
    
    public <T extends DAObject> T attach(T obj) {
        Session s = sessionFactory.getCurrentSession();
        T objMerged = null;
        try {
            objMerged = (T) s.merge(obj);
        } catch (HibernateException e) {
            logExceptionAndTerminateSession(e);
        }
        return objMerged;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab

