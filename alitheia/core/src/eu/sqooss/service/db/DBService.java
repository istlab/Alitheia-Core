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

package eu.sqooss.service.db;

import eu.sqooss.service.db.DAObject;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.QueryException;
import org.hibernate.Session;


/**
 * This is the service providing access to the Alitheia Database,
 * including project metadata, user management, metrics data...
 * 
 * The API includes methods for retrieving objects by id or by properties,
 * adding/updating/deleting records from the database,
 * and general-purpose querying methods for lower-level database access.
 * 
 * There are two overloads for each method: one with a Session argument and one without.
 * The methods without a Session argument manage the Hibernate session and transaction
 * for the operation internally, together with exception handling. This makes them
 * convenient to use for most cases, such as one-time queries or operations.
 * On the other hand, the methods with the Session argument delegate Hibernate session
 * and transaction management to the caller, as well as exception handling. This brings
 * greater flexibility, such as support for multiple operations within a same transaction,
 * but it requires the caller to write more code and is more prone to errors,
 * so you should only use them if you have a specific need for them.
 * 
 * Regardless of their type, the methods from this interface will log all errors
 * coming from Hibernate itself or from JDBC
 * 
 * @author Romain Pokrzywka
 *
 */
public interface DBService {
    /**
     * A generic query method to retrieve a single DAObject subclass using its identifier.
     * The return value is parameterized to the actual type of DAObject queried
     * so no downcast is needed.
     * @param daoClass the actual class of the DAObject. 
     * @param id the DAObject's identifier
     * @return the DAOObject if a match for the class and the identifier was found in the database,
     *          or null otherwise or if a database access error occured
     */
    public <T extends DAObject> T findObjectById(Class<T> daoClass, long id);
    
    /**
     * A generic query method to retrieve a single DAObject subclass using its identifier.
     * The return value is parameterized to the actual type of DAObject queried
     * so no downcast is needed.
     * The caller is responsible for having a transaction initialized before calling the method,
     * and committing it or rolling it back afterwards.
     * @param s the session to use for this transaction
     * @param daoClass the actual class of the DAObject. 
     * @param id the DAObject's identifier
     * @return the DAOObject if a match for the class and the identifier was found in the database,
     *          or null otherwise
     * @throws HibernateException if a Hibernate error or database access error occurs
     */
    public <T extends DAObject> T findObjectById(Session s, Class<T> daoClass, long id)
        throws HibernateException;

    /**
     * A generic query method to retrieve a list of DAObjects of a same subclass
     * matching a set of properties.
     * The returned list contains the objects matching <b>all</b> of the properties specified.
     * It is parameterized to the actual type of DAObject queried so no downcast is needed.
     * The map key should be the property name as a string, and the value should be a value
     * with a matching type for the property. For example, if a class has a String property
     * called name (ie. a getName()/setName() accessor pair), then you would use "name" as
     * the map key and a String object as the map value.
     * If any property in the map isn't valid (either an unknown name or a value of the wrong type)
     * the call will fail and an empty list will be returned.
     * It uses its own session.
     * 
     * @param daoClass the actual class of the DAObjects
     * @param properties a map of property name/value pairs corresponding to properties
     *          of the DAObject subclass
     * @return a list of DAObjects matching the class and the set of properties,
     *          possibly empty if no match was found in the database or if the properties map
     *          contains invalid entries or if a database access error occured
     */
    public <T extends DAObject> List<T> findObjectsByProperties(Class<T> daoClass,
                                                                Map<String,Object> properties );
    
    /**
     * A generic query method to retrieve a list of DAObjects of a same subclass
     * matching a set of properties.
     * The returned list contains the objects matching <b>all</b> of the properties specified.
     * It is parameterized to the actual type of DAObject queried so no downcast is needed.
     * The map key should be the property name as a string, and the value should be a value
     * with a matching type for the property. For example, if a class has a String property
     * called name (ie. a getName()/setName() accessor pair), then you would use "name" as
     * the map key and a String object as the map value.
     * If any property in the map isn't valid (either an unknown name or a value of the wrong type)
     * the call will fail and an empty list will be returned.
     * The caller is responsible for having a transaction initialized before calling the method,
     * and committing it or rolling it back afterwards.
     * 
     * @param s the session to use for this transaction
     * @param daoClass the actual class of the DAObjects
     * @param properties a map of property name/value pairs corresponding to properties
     *          of the DAObject subclass
     * @return a list of DAObjects matching the class and the set of properties,
     *          possibly empty if no match was found in the database or if the properties map
     *          contains invalid entries
     * @throws HibernateException if a Hibernate error or database access error occurs
     */
    public <T extends DAObject> List<T> findObjectsByProperties(Session s,
                                                                Class<T> daoClass,
                                                                Map<String,Object> properties )
        throws HibernateException;

    /**
     * Add a new record to the system database, using the default database session.
     * This should initialize any tables that are needed for storage of project information.
     * 
     * @param record the record to persist into the database
     * @return true if the record insertion succeeded, false otherwise
     */
    public boolean addRecord(DAObject record);

    /**
     * Add a new record to the system database using a separate database session.
     * The transaction is still created and managed internally.
     * This should initialize any tables that are needed for storage of project information.
     * 
     * @param s the session to use for this transaction
     * @param record the record to persist into the database
     * @return true if the record insertion succeeded, false otherwise
     */
    public boolean addRecord(Session s, DAObject record);
    
    /**
     * Add multiple new records to the system database, using the default database session.
     * This should initialize any tables that are needed for storage of project information.
     * The results will be committed only if all the insertions are successful,
     * so if any insertion fails then no record will be added.
     * 
     * @param records the list of records to persist into the database
     * @return true if all the record insertions succeeded, false otherwise
     */
    public boolean addRecords(List<DAObject> records);

    /**
     * Add multiple new records to the system database, using a separate database session.
     * The transaction is still created and managed internally.
     * This should initialize any tables that are needed for storage of project information.
     * The results will be committed only if all the insertions are successful,
     * so if any insertion fails then no record will be added.
     * 
     * @param s the session to use for this transaction
     * @param records the list of records to persist into the database
     * @return true if all the record insertions succeeded, false otherwise
     */
    public boolean addRecords(Session s, List<DAObject> records);

    /**
     * Update an existing record in the system database, using the default database session.
     *
     * @param record the record to update in the database
     * @return true if the record update succeeded, false otherwise
     */
    public boolean updateRecord(DAObject record);
    
    /**
     * Update an existing record in the system database, using a separate database session.
     * The transaction is still created and managed internally.
     *
     * @param s session to use for this transaction
     * @param record the record to update in the database
     * @return true if the record update succeeded, false otherwise
     */
    public boolean updateRecord(Session s, DAObject record);
    
    /**
     * Update multiple existing records in the system database, using the default database session.
     * The results will be committed only if all the updates are successful,
     * so if any update fails then no record will be updated.
     * 
     * @param records the list of records to update in the database
     * @return true if all the record updates succeeded, false otherwise
     */
    public boolean updateRecords(List<DAObject> records);
    
    /**
     * Update multiple existing records in the system database, using a separate database session.
     * The transaction is still created and managed internally.
     * The results will be committed only if all the updates are successful,
     * so if any update fails then no record will be updated.
     * 
     * @param s the session to use for this transaction
     * @param records the list of records to update in the database
     * @return true if all the record updates succeeded, false otherwise
     */
    public boolean updateRecords(Session s, List<DAObject> records);

    /**
     * Delete an existing record from the system database, using the default database session.
     *
     * @param record the record to remove from the database
     * @return true if the record deletion succeeded, false otherwise
     */
    public boolean deleteRecord(DAObject record);
    
    /**
     * Delete an existing record from the system database, using a separate database session.
     * The transaction is still created and managed internally.
     *
     * @param s session to use for this transaction
     * @param record the record to remove from the database
     * @return true if the record deletion succeeded, false otherwise
     */
    public boolean deleteRecord(Session s, DAObject record);
    
    /**
     * Delete multiple existing records from the system database, using the default database session.
     * The results will be committed only if all the deletions are successful,
     * so if any deletion fails then no record will be deleted.
     * 
     * @param records the list of records to remove from the database
     * @return true if all the record deletions succeeded, false otherwise
     */
    public boolean deleteRecords(List<DAObject> records);
    
    /**
     * Delete multiple existing records from the system database, using a separate database session.
     * The transaction is still created and managed internally.
     * The results will be committed only if all the deletions are successful,
     * so if any deletion fails then no record will be deleted.
     * 
     * @param s the session to use for this transaction
     * @param records the list of records to remove from the database
     * @return true if all the record deletions succeeded, false otherwise
     */
    public boolean deleteRecords(Session s, List<DAObject> records);
    
    /**
     * Execute a complete SQL query to the database, using the default database session.
     * This allows low-level manipulation of the database contents outside of the DAO types.
     * To limit risks of SQL injection exploits, please do not execute queries like
     * <code>"SELECT * FROM " + tableName</code>.
     * If you need dynamic SQL queries, please use the overload with the params argument.
     * 
     * @param sql the sql query string
     * @return a list of records. If the query contains multiple columns,
     *          the results are returned in an instance of Object[]
     * @throws SQLException if the query is invalid or a database access error occurs
     * 
     * @see doSQL(String sql, Map<String, Object> params)
     */
    public List<?> doSQL(String sql)
        throws SQLException;
    
    /**
     * Execute a parameterized SQL query to the database, using the default database session.
     * This allows low-level manipulation of the database contents outside of the DAO types.
     * 
     * @param sql the sql query string
     * @param params the map of parameters to be substituted in the SQL query
     * @return a list of records. If the query contains multiple columns,
     *          the results are returned in an instance of Object[]
     * @throws SQLException if the query is invalid or a database access error occurs
     * @throws QueryException if some parameters are missing
     */
    public List<?> doSQL(String sql, Map<String, Object> params)
        throws SQLException, QueryException;
    
    /**
     * Execute a complete SQL query to the database, using a separate database session.
     * This allows low-level manipulation of the database contents outside of the DAO types.
     * To limit risks of SQL injection exploits, please do not execute queries like
     * <code>"SELECT * FROM " + tableName</code>.
     * If you need dynamic SQL queries, please use the overload with the params argument.
     * 
     * The caller is responsible for having a transaction initialized before calling the method,
     * and committing it or rolling it back afterwards.
     * 
     * @param s the session to use for the query
     * @param sql the sql query string
     * @return a list of records. If the query contains multiple columns,
     *          the results are returned in an instance of Object[]
     * @throws HibernateException if the query is invalid,
     *          or a Hibernate error or database access error occurs
     * 
     * @see doSQL(String sql, Map<String, Object> params)
     */
    public List<?> doSQL(Session s, String sql)
        throws HibernateException;
    
    /**
     * Execute a parameterized SQL query to the database, using a separate database session.
     * This allows low-level manipulation of the database contents outside of the DAO types.
     * 
     * The caller is responsible for having a transaction initialized before calling the method,
     * and committing it or rolling it back afterwards.
     * 
     * @param s the session to use for the query
     * @param sql the sql query string
     * @param params the map of parameters to be substituted in the SQL query
     * @return a list of records. If the query contains multiple columns,
     *          the results are returned in an instance of Object[]
     * @throws HibernateException if the query is invalid,
     *          or a Hibernate error or database access error occurs
     */
    public List<?> doSQL(Session s, String sql, Map<String, Object> params)
        throws HibernateException;

    /**
     * Execute a complete HQL query to the database, using the default database session.
     * To limit risks of HQL injection exploits, please do not execute queries like
     * <code>"FROM " + objectClass</code>.
     * If you need dynamic HQL queries, please use the overload with the params argument.
     * 
     * @param hql the HQL query string
     * @return a list of {@link DAObject}. If the query contains multiple columns,
     *          the results are returned in an instance of Object[]
     *           If a Hibernate error or a database access error occurs,
     *           an empty list will be returned.
     *           
     * @throws QueryException if the query is invalid
     * 
     * @see doHQL(String, Map<String, Object>)
     */
    public List<?> doHQL(String hql)
        throws QueryException;
    
    /**
     * Execute a parameterized HQL query to the database, using the default database session.
     *
     * @param hql the HQL query string
     * @param params the map of parameters to be substituted in the HQL query
     * @return a list of {@link DAObject}. If the query contains multiple columns,
     *          the results are returned in an instance of Object[]
     *           If a Hibernate error or a database access error occurs,
     *           an empty list will be returned.
     *           
     * @throws QueryException if the query is invalid or if params contains invalid entries
     * 
     * @see doHQL(String, Map<String, Object>, Map<String,Collection>)
     */
    public List<?> doHQL(String hql, Map<String, Object> params)
        throws QueryException;

    /**
     * Execute a parameterized HQL query to the database, using the default database session.
     *
     * @param hql HQL query string
     * @param params the map of parameters to be substituted in the HQL query
     * @param collectionParams the map of collection parameters to be substituted in the HQL query
     * @return a list of {@link DAObject}. If the query contains multiple columns,
     *          the results are returned in an instance of Object[]
     *           If a Hibernate error or a database access error occurs,
     *           an empty list will be returned.
     *           
     * @throws QueryException if the query is invalid or if params or collectionParams
     *                          contain invalid entries
     */
    public List<?> doHQL(String hql, Map<String, Object> params,
                          Map<String, Collection> collectionParams)
        throws QueryException;
    
    /**
     * Execute a complete HQL query to the database, using a separate database session.
     * To limit risks of HQL injection exploits, please do not execute queries like
     * <code>"FROM " + objectClass</code>.
     * If you need dynamic HQL queries, please use the overload with the params argument.
     * 
     * The caller is responsible for having a transaction initialized before calling the method,
     * and committing it or rolling it back afterwards.
     * 
     * @param s the session to use for the query
     * @param hql the HQL query string
     * @return a list of {@link DAObject}. If the query contains multiple columns,
     *          the results are returned in an instance of Object[]
     * @throws HibernateException if the query is invalid,
     *          or a Hibernate error or database access error occurs
     * 
     * @see doHQL(Session, String, Map<String, Object>)
     */
    public List<?> doHQL(Session s, String hql)
        throws HibernateException;
    
    /**
     * Execute a parameterized HQL query to the database, using a separate database session.
     *
     * The caller is responsible for having a transaction initialized before calling the method,
     * and committing it or rolling it back afterwards. 
     * 
     * @param s the session to use for the query
     * @param hql the HQL query string
     * @param params the map of parameters to be substituted in the HQL query
     * @return a list of {@link DAObject}. If the query contains multiple columns,
     *          the results are returned in an instance of Object[]
     * @throws HibernateException if the query is invalid, if params contains invalid entries,
     *          or a Hibernate error or database access error occurs
     * 
     * @see doHQL(Session, String, Map<String, Object>, Map<String,Collection>)
     */
    public List<?> doHQL(Session s, String hql, Map<String, Object> params)
        throws HibernateException;

    /**
     * Execute a parameterized HQL query to the database, using a separate database session.
     * HQL is very similar to SQL, but differs in a variety of important ways.
     * See the hibernate documentation at
     * http://www.hibernate.org/hib_docs/reference/en/html/queryhql.html
     * for details. As a rule, you do not write 'SELECT *' but only
     * 'FROM <ClassName>' (note: not the table name, the @em class).
     *
     * The query string may contain named parameters, for which values
     * will be substituted from the params and lparams arguments to this
     * method. For parameters that expect a single datum, put the mapping
     * from name to an object in the params argument. List-based parameters
     * (for instance the allowable values in a "IN ( foo, ... )" clause)
     * may be placed in the lparams argument. Either may be null if there
     * are no paramaters of that kind.
     * 
     * The caller is responsible for having a transaction initialized before calling the method,
     * and committing it or rolling it back afterwards.
     * 
     * @param s the session to use for the query
     * @param hql the HQL query string
     * @param params the map of parameters to be substituted in the HQL query
     * @param collectionParams the map of collection parameters to be substituted in the HQL query
     * @return a list of {@link DAObject}. If the query contains multiple columns,
     *          the results are returned in an instance of Object[]
     * @throws HibernateException if the query is invalid, if params or lparams contain invalid entries,
     *          or a Hibernate error or database access error occurs
     */
    public List<?> doHQL(Session s, String hql, Map<String, Object> params,
                          Map<String, Collection> lparams)
        throws HibernateException;
    
    /**
     * Get a session to the alitheia DB from the session manager
     * 
     * @param holder the object this session belongs to
     * @return An initialised hibernate session to the SQO-OSS DB
     */
    public Session getSession(Object holder);
    
    /**
     * Return a session to the session manager
     * @param s The session to be returned
     */
    public void returnSession(Session s);
}

// vi: ai nosi sw=4 ts=4 expandtab
