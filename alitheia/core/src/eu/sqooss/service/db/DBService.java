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
     * Starts a new work session with the DBService for the current thread.
     * This method should be called before any other method from DBService, to ensure all resources
     * are properly set up and ready, such as database connection, active transaction...
     * (the only exception is doSQL, which manages the session internally)
     * Only one session per thread can be active at a time, so calling startDBSession with a
     * previously started session in the same thread has no effect and will assume usage
     * of the existing session. (e.g. if a previous session was not closed properly)
     * 
     * This method is thread-safe, and it creates session for a specific thread only.
     * It will also start a database transaction, ensuring that only the current thread has access
     * to the database for the duration of the session, therefore simplifying concurrency issues.
     * 
     * @return true if the session was correctly started ;
     *         false if a session was already started for this thread, 
     *          or if the session couldn't be started
     */
    public boolean startDBSession();
    
    /**
     * Commits the changes made in the current work session into the database and closes the session,
     * also releasing the transaction lock on the database.
     * 
     * This method is thread-safe, and it will always close the current session (if any)
     * and release any lock on the database, even if an error occurs.
     * 
     * @return true if the commit was successful and the session correctly closed,
     *         false if there was no active session or if an error occured.
     */
    public boolean commitDBSession();
    
    /**
     * Closes the current work session without committing the changes into the database,
     * also releasing the transaction lock on the database.
     * 
     * Note that any DAOs loaded and modified during the session will NOT be reset to
     * their state at load-time. In other words, modifications to the DAOs are NOT cancelled,
     * however these modifications will not be persisted in the database.
     * 
     * This method is thread-safe, and it will always close the current session (if any)
     * and release any lock on the database, even if an error occurs.
     * 
     * @return true if the session correctly closed,
     *         false if there was no active session or if an error occured.
     */
    public boolean rollbackDBSession();
    
    public boolean flushDBSession();
    
    /**
     * Returns the state of the work session for the current thread.
     * @return true if a session was started and is still active,
     *         false otherwise
     */
    public boolean isDBSessionActive();
    
    
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
     * Add a new record to the system database, using the default database session.
     * This should initialize any tables that are needed for storage of project information.
     * 
     * @param record the record to persist into the database
     * @return true if the record insertion succeeded, false otherwise
     */
    public boolean addRecord(DAObject record);
    
    /**
     * Add multiple new records to the system database, using the default database session.
     * This should initialize any tables that are needed for storage of project information.
     * The results will be committed only if all the insertions are successful,
     * so if any insertion fails then no record will be added.
     * 
     * @param records the list of records to persist into the database
     * @return true if all the record insertions succeeded, false otherwise
     */
    public <T extends DAObject> boolean addRecords(List<T> records);

//    /**
//     * Update an existing record in the system database, using the default database session.
//     *
//     * @param record the record to update in the database
//     * @return true if the record update succeeded, false otherwise
//     */
//    public boolean updateRecord(DAObject record);
//    
//    /**
//     * Update multiple existing records in the system database, using the default database session.
//     * The results will be committed only if all the updates are successful,
//     * so if any update fails then no record will be updated.
//     * 
//     * @param records the list of records to update in the database
//     * @return true if all the record updates succeeded, false otherwise
//     */
//    public boolean updateRecords(List<DAObject> records);
    
    /**
     * Delete an existing record from the system database, using the default database session.
     *
     * @param record the record to remove from the database
     * @return true if the record deletion succeeded, false otherwise
     */
    public boolean deleteRecord(DAObject record);
    
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
     * Add a new composite-key association to the system database.
     * This should initialize any tables that are needed for storage of project information.
     * 
     * @param compositeKey the composite key object to persist into the database
     * @return true if the association was successfully added, false otherwise
     */
    public boolean addAssociation(Object compositeKey);
    
    /**
     * Delete an existing composite-key association from the system database.
     * 
     * @param compositeKey the composite key object to delete from the database
     * @return true if the association was successfully deleted, false otherwise
     */
    public boolean deleteAssociation(Object compositeKey);

    /**
     * Attach a disconnected object to the current Session. If the corresponding
     * row exists, then the returned object will merge the persistent and 
     * the disconnected object fields. Preference will be given to the field
     * values of the detached object. If the detached object contains 
     * references to other DAOs, the attach operation will cascade.
     *  
     * @param obj The object to connect
     * @return The connect instance of the object
     */
    public <T extends DAObject> T attachObjectToDBSession(T obj);

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
        
}

// vi: ai nosi sw=4 ts=4 expandtab
