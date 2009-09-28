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

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.QueryException;
import org.hibernate.JDBCException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;	
import org.hibernate.mapping.AuxiliaryDatabaseObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;

/**
 * Implementation of the Database service, using Hibernate's Thread-based session handling
 * 
 * @author Romain Pokrzywka
 * 
 */
public class DBServiceImpl implements DBService, FrameworkListener {

    private static final String DB_DRIVER_PROPERTY = "eu.sqooss.db.driver";
    private static final String DB_CONNECTION_URL_PROPERTY = "eu.sqooss.db.url";
    private static final String DB_DIALECT_PROPERTY = "eu.sqooss.db.dialect";
    private static final String DB_USERNAME_PROPERTY = "eu.sqooss.db.user";
    private static final String DB_PASSWORD_PROPERTY = "eu.sqooss.db.passwd";
    private static final String HIBERNATE_CONFIG_PROPERTY = "eu.sqooss.hibernate.config";
    private static final String HIBERNATE_RESET_PROPERTY = "eu.sqooss.hibernate.reset";
    
    private Logger logger = null;
    // Store the class and URL of the database to hand off to
    // Hibernate so that it obeys the fallback from Postgres to Derby as well.
    private String dbClass, dbURL, dbDialect, dbUserName, dbPasswd;
    private SessionFactory sessionFactory = null;
    private BundleContext bc = null;
    private EventAdmin eaService = null;
    private AtomicBoolean isInitialised = new AtomicBoolean(false);
    
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
        e.printStackTrace();
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
            try {
                throw new Exception("No active session.");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }
    
    private boolean getJDBCConnection(String driver, String url, String dialect, 
            String username, String passwd) {
        
        if ((driver == null) || (url == null) || (dialect == null) || (username == null) || (passwd == null)) {
            dbClass = null;
            dbURL = null;
            dbDialect = null;
            dbUserName = null;
            dbPasswd = null;
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
            Connection c = DriverManager.getConnection(url, username, passwd);
            c.setAutoCommit(false);
            dbClass = driver;
            dbURL = url;
            dbDialect = dialect;
            dbUserName = username;
            dbPasswd = passwd;
            c.close();
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
                "org.hibernate.dialect.DerbyDialect", "", "");
    }

    private void initHibernate(URL configFileURL, boolean resetDatabase) {
        
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
            c.setProperty("hibernate.connection.username", dbUserName);
            c.setProperty("hibernate.connection.password", dbPasswd);
            c.setProperty("hibernate.connection.dialect", dbDialect);
            if (resetDatabase) {
                c.setProperty("hibernate.hbm2ddl.auto", "create");
            }
            
            // Get the list of eu.sqo-oss.metrics.* jars and add them to the config
            String equinoxInstallDirProperty = System.getProperty("osgi.install.area");
            if ( equinoxInstallDirProperty != null ) {
                File equinoxInstallDir = new File( URI.create(equinoxInstallDirProperty) );
                if ( equinoxInstallDir.exists() && equinoxInstallDir.isDirectory() ) {
                    File[] metricsJars = equinoxInstallDir.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.startsWith("eu.sqooss.metrics")  && name.endsWith(".jar");
                        }
                    });
                    for( File jarFile: metricsJars ) {
                        logger.debug("found metric bundle \"" + jarFile.getName() + "\", examining for custom DAOs");
                        
                        c.addJar(jarFile);
                    }
                } else {
                    logger.warn("couln't resolve equinox install property to a directory on disk :" + equinoxInstallDirProperty
                                + ". Custom DAOs from metrics bundles won't be initialized.");
                }
            }

            List<AuxiliaryDatabaseObject> objs = loadDBScripts();
            
            sessionFactory = c.buildSessionFactory();
            

        } catch (Throwable e) {
            logger.error("Failed to initialize Hibernate: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }
    
    private List<AuxiliaryDatabaseObject> loadDBScripts() {
    	
    	return null;
    }

    /**
     * Constructor for creating a DBServiceImpl inside the SQO-OSS system.
     * 
     * @param bc The current BundleContext
     * @param l The current Logger
     */
    public DBServiceImpl(BundleContext bc, Logger l) {
        this.bc = bc;
        logger = l;

        dbURL = null;
        dbClass = null;
        dbDialect = null;
        dbUserName = null;
        dbPasswd = null;
        if (!getJDBCConnection(bc.getProperty(DB_DRIVER_PROPERTY),
                bc.getProperty(DB_CONNECTION_URL_PROPERTY),
                bc.getProperty(DB_DIALECT_PROPERTY),
                bc.getProperty(DB_USERNAME_PROPERTY),
                bc.getProperty(DB_PASSWORD_PROPERTY))) {
            if (!Boolean
                    .valueOf(bc.getProperty("eu.sqooss.db.fallback.enable"))
                    || !getDerbyJDBC()) {
                logger.error("DB service got no JDBC connectors.");
            }
        }
        bc.addFrameworkListener(this);
        
        ServiceReference srefEAService = bc.getServiceReference(
                org.osgi.service.event.EventAdmin.class.getName());
        if (srefEAService != null) {
            eaService = (EventAdmin) bc.getService(srefEAService);
        }
        else {
            System.err.println("Could not find a Event Admin service!");
        }
    }

    public <T extends DAObject> T findObjectById(Class<T> daoClass, long id) {
        return doFindObjectById(daoClass, id, false);
    }
    
    public <T extends DAObject> T findObjectByIdForUpdate(Class<T> daoClass, long id) {
        return doFindObjectById(daoClass, id, true);
    }

    @SuppressWarnings("unchecked")
    private <T extends DAObject> T doFindObjectById(Class<T> daoClass, long id, boolean useLock) {
        if ( !checkSession() )
            return null;
        
        try {
            Session s = sessionFactory.getCurrentSession();
            return (T) (useLock ? s.get(daoClass, id, LockMode.UPGRADE) : s.get(daoClass, id));
        } catch (HibernateException e) {
            logExceptionAndTerminateSession(e);
            return null;
        }
    }

    public <T extends DAObject> List<T> findObjectsByProperties(Class<T> daoClass, Map<String,Object> properties) {
        return doFindObjectsByProperties(daoClass, properties, false);
    }

    public <T extends DAObject> List<T> findObjectsByPropertiesForUpdate(Class<T> daoClass, Map<String,Object> properties) {
        return doFindObjectsByProperties(daoClass, properties, true);
    }

    @SuppressWarnings("unchecked")
    private <T extends DAObject> List<T> doFindObjectsByProperties(Class<T> daoClass, Map<String,Object> properties, boolean useLock) {
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
            return (List<T>) doHQL( "from " + daoClass.getName() + " as foo " + whereClause, parameterMap, useLock );
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

    public int callProcedure(String procName, List<String> args, Map<String, Object> params)
			throws SQLException, QueryException {
		boolean autoSession = !isDBSessionActive();
		StringBuilder sql = new StringBuilder("call " + procName + "(");
		
		for (String arg : args) {
			sql.append(":").append(arg).append(",");
		}
		sql.deleteCharAt(sql.lastIndexOf(",")).append(")");
		
		try {
			Session s = sessionFactory.getCurrentSession();
			if (autoSession) {
				s.beginTransaction();
			}
			Query query = s.createSQLQuery(sql.toString());
			if (params != null) {
				for (String param : params.keySet()) {
					query.setParameter(param, params.get(param));
				}
			}
			int result = query.executeUpdate();
			if (autoSession) {
				s.getTransaction().commit();
			}
			return result;
		} catch (JDBCException e) {
			logExceptionAndTerminateSession(e);
			throw e.getSQLException();
		} catch (QueryException e) {
			logExceptionAndTerminateSession(e);
			throw e;
		} catch (HibernateException e) {
			logExceptionAndTerminateSession(e);
			return -1;
		}
	}
    
    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#doHQL(java.lang.String)
     */
    public List<?> doHQL(String hql)
        throws QueryException {
        return doHQL(hql, null, null, false, -1, -1);
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#doHQL(java.lang.String, java.util.Map)
     */
    public List<?> doHQL(String hql, Map<String, Object> params) 
        throws QueryException {
        return doHQL(hql, params, null, false, -1, -1);
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#doHQL(java.lang.String, java.util.Map, int)
     */
    public List<?> doHQL(String hql, Map<String, Object> params, int limit) 
        throws QueryException {
        return doHQL(hql, params, null, false, 0, limit);
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#doHQL(java.lang.String, java.util.Map, boolean)
     */
    public List<?> doHQL(String hql, Map<String, Object> params, boolean lockForUpdate) 
        throws QueryException {
        return doHQL(hql, params, null, lockForUpdate, -1, -1);
    }

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#doHQL(java.lang.String, java.util.Map, java.util.Map)
     */
    public List<?> doHQL(String hql, Map<String, Object> params,
            Map<String, Collection> collectionParams) 
        throws QueryException {
        return doHQL(hql, params, collectionParams, false, -1, -1);
    }
    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#doHQL(java.lang.String, java.util.Map, java.util.Map, boolean, int, int)
     */
    public List<?> doHQL(String hql, Map<String, Object> params,
            Map<String, Collection> collectionParams, boolean lockForUpdate, int start, int limit) 
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
            if (lockForUpdate) {
                query.setLockMode("foo", LockMode.UPGRADE);
            }
            if ( start >= 0 && limit >= 0 ) {
                query.setFirstResult(start);
                query.setMaxResults(limit);
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
    public <T extends DAObject> boolean addRecords(List<T> records) {
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
            s.flush();
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

    /* (non-Javadoc)
     * @see eu.sqooss.service.db.DBService#deleteRecords(java.util.List)
     */
    public <T extends DAObject> boolean deleteRecords(List<T> records) {
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
            s.flush();
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
            if (!s.contains(compositeKey)) {
                compositeKey = s.merge(compositeKey);
            }
            s.delete(compositeKey);
            return true;
        } catch (HibernateException e) {
            logExceptionAndTerminateSession(e);
            return false;
        }
    }
    
    public boolean startDBSession() {
        //Boot time check
        if(isInitialised.get() == false) {
            return false;
        }
        
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
    
    public boolean flushDBSession() {
        if ( !checkSession() )
            return false;
        
        Session s = null;
        try {
            s = sessionFactory.getCurrentSession();
            s.flush();
            s.clear();
        } catch (HibernateException e) {
            logger.error("flushDBSession() - error while flushing session: " + e.getMessage());
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
        //Boot time check
        if(isInitialised.get() == false) {
            return false;
        }
        
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
        
    @SuppressWarnings("unchecked")
    public <T extends DAObject> T attachObjectToDBSession(T obj) {
        if( !checkSession() )
            return null;

        try {
            Session s = sessionFactory.getCurrentSession();
            if ( s.contains(obj)) {
                return obj;
            } else {
                return (T) s.merge(obj);
            }
        } catch (HibernateException e) {
            logExceptionAndTerminateSession(e);
            return null;
        }
    }
      
    public void frameworkEvent(FrameworkEvent event) {
        
        /**
         * Start Hibernate after all other bundles have started
         */
        if(event.getType() == FrameworkEvent.STARTLEVEL_CHANGED) {
            if (dbClass != null) {
                logger.info("Using JDBC " + dbClass);
                boolean resetDatabase = false;
                if (Boolean.valueOf(bc.getProperty(HIBERNATE_RESET_PROPERTY))) {
                    resetDatabase = true;
                }
                logger.info("Caught STARTED event - Initialising Hibernate");
                
                initHibernate(bc.getBundle().getResource("hibernate.cfg.xml"), resetDatabase);
                
                isInitialised.compareAndSet(false, true);
                
                if (eaService != null) {
                    HashMap<String, Boolean> value = new HashMap<String, Boolean>();
                    value.put("value", true);
                    eaService.sendEvent(new Event(DBService.EVENT_STARTED, value));
                } else {
                    logger.error("Cannot send the" + DBService.EVENT_STARTED + 
                            "event");
                }
            } else {
                logger.error("Hibernate could not be initialized.");
                // TODO: Throw something to prevent the bundle from being started?
            }
        }
    }
    
    public int executeUpdate(String hql, Map<String, Object> params) 
    throws QueryException {
        if (!checkSession()) {
            return -1;
        }
        
        try {
            Session s = sessionFactory.getCurrentSession();
            Query query = s.createQuery(hql);
            if (params != null) {
                for (String param : params.keySet()) {
                    query.setParameter(param, params.get(param));
                }
            }
            
            return query.executeUpdate();
            
        } catch (QueryException e) {
            logExceptionAndTerminateSession(e);
            throw e;
        } catch (HibernateException e) {
            logExceptionAndTerminateSession(e);
            return -1;
        } catch (ClassCastException e) {
            // Throw a QueryException instead of forwarding the ClassCastException
            // it's more explicit
            QueryException ebis = new QueryException(
                    "Invalid HQL query parameter type: " + e.getMessage(), e);
            logExceptionAndTerminateSession(ebis);
            throw ebis;
        }
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

