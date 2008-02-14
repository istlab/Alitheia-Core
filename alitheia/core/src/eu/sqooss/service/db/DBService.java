/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

public interface DBService {
    /**
     * A generic query method to retrieve a single DAObject subclass using its identifier.
     * The return value is parameterized to the actual type of DAObject queried so no downcast is needed.
     * @param s the session to use for this transaction
     * @param daoClass the actual class of the DAObject. 
     * @param id the DAObject's identifier
     * @return the DAOObject if a match for the class and the identifier was found in the database, or null otherwise
     */
    public <T extends DAObject> T findObjectById(Session s, Class<T> daoClass, long id);
    
    /**
     * A generic query method to retrieve a list of DAObjects of a same subclass matching a set of properties.
     * The returned list contains the objects matching <b>all</b> of the properties specified. It is parameterized to the actual type of DAObject queried so no downcast is needed.
     * The map key should be the property name as a string, and the value should be a value with a matching type for the property.
     * For example, if a class has a String property called name (ie. a getName()/setName() accessor pair),
     * then you would use "name" as the map key and a String object as the map value.
     * If any property in the map isn't valid (either an unknown name or a value of the wrong type) the call will fail and an empty list will be returned.
     * It uses its own session.
     * 
     * @param daoClass the actual class of the DAObjects
     * @param properties a map of property name/value pairs corresponding to properties of the DAObject subclass
     * @return a list of DAObjects matching the class and the set of properties, possibly empty if no match was found in the database or if the properties map contains invalid entries
     */
    public <T extends DAObject> List<T> findObjectByProperties(Class<T> daoClass, Map<String,Object> properties );
    
    /**
     * A generic query method to retrieve a list of DAObjects of a same subclass matching a set of properties.
     * The returned list contains the objects matching <b>all</b> of the properties specified. It is parameterized to the actual type of DAObject queried so no downcast is needed.
     * The map key should be the property name as a string, and the value should be a value with a matching type for the property.
     * For example, if a class has a String property called name (ie. a getName()/setName() accessor pair),
     * then you would use "name" as the map key and a String object as the map value.
     * If any property in the map isn't valid (either an unknown name or a value of the wrong type) the call will fail and an empty list will be returned.
     * @param s the session to use for this transaction
     * @param daoClass the actual class of the DAObjects
     * @param properties a map of property name/value pairs corresponding to properties of the DAObject subclass
     * @return a list of DAObjects matching the class and the set of properties, possibly empty if no match was found in the database or if the properties map contains invalid entries
     */
    public <T extends DAObject> List<T> findObjectByProperties(Session s, Class<T> daoClass, Map<String,Object> properties );

    /**
     * Add a new record to the system database, using the default database session.
     * This should initialize any tables that are needed for storage of project information.
     * 
     * @param record the record to persist into the database
     * @return True, if record insertion succeeded. False + log message
     *         otherwise
     */
    public boolean addRecord(DAObject record);

    /**
     * Add a new record to the system database, using a separate database session.
     * This should initialize any tables that are needed for storage of project information.
     * 
     * @param s the session to use for this transaction
     * @param record the record to persist into the database
     * @return True, if record insertion succeeded. False + log message
     *         otherwise
     */
    public boolean addRecord(Session s, DAObject record);
    
    /**
     * Add multiple new records to the system database, using the default database session.
     * This should initialize any tables that are needed for storage of project information.
     * The results will be committed only if all the insertions are successful,
     * so if any insertion fails then no record will be added.
     * 
     * @param records the list of records to persist into the database
     * @return True, if all the record insertions succeeded. False + log message
     *         otherwise
     */
    public boolean addRecords(List<DAObject> records);

    /**
     * Add multiple new records to the system database, using a separate database session.
     * This should initialize any tables that are needed for storage of project information.
     * The results will be committed only if all the insertions are successful,
     * so if any insertion fails then no record will be added.
     * 
     * @param s the session to use for this transaction
     * @param records the list of records to persist into the database
     * @return True, if all the record insertions succeeded. False + log message
     *         otherwise
     */
    public boolean addRecords(Session s, List<DAObject> records);

    /**
     * Delete an existing record from the system database, using the
     * default database session.
     *
     * @param record the record to remove from the database
     */
    public boolean deleteRecord(DAObject record);
    
    /**
     * Delete an existing record from the system database, using a
     * separate database session.
     *
     * @param s session to use for this transaction
     * @param record the record to remove from the database
     */
    public boolean deleteRecord(Session s, DAObject record);
    
    /**
     * Delete multiple existing records from the system database, using the default database session.
     * The results will be committed only if all the deletions are successful,
     * so if any deletion fails then no record will be deleted.
     * 
     * @param records the list of records to remove from the database
     * @return True, if all the record deletions succeeded. False + log message
     *         otherwise
     */
    public boolean deleteRecords(List<DAObject> records);
    
    /**
     * Delete multiple existing records from the system database, using a separate database session.
     * The results will be committed only if all the deletions are successful,
     * so if any deletion fails then no record will be deleted.
     * 
     * @param s the session to use for this transaction
     * @param records the list of records to remove from the database
     * @return True, if all the record deletions succeeded. False + log message
     *         otherwise
     */
    public boolean deleteRecords(Session s, List<DAObject> records);
    
    /**
     * Allows the intelligent C++ programmer to simply fire complete SQL
     * statements to the database. This allows low-level manipulation
     * of the database contents outside of the DAO types.
     */
    public List doSQL(String sql);
    
    public List doSQL(String sql, Map<String, Object> params);
    
    public List doSQL(Session s, String sql);
    
    public List doSQL(Session s, String sql, Map<String, Object> params);

    /**
     * Do an HQL query with the default session as a single transaction.
     *
     * @param hql HQL query string
     * @see doHQL(Session, String, Map<String, Object>)
     */
    public List doHQL(String hql);
    
    /**
     * Do an HQL query with the default session as a single transaction.
     *
     * @param hql HQL query string
     * @param params parameters in the query
     * @see doHQL(Session, String, Map<String, Object>, Map<String,Collection>)
     */
    public List doHQL(String hql, Map<String, Object> params);

    /**
     * Do an HQL query with the default session as a single transaction.
     *
     * @param hql HQL query string
     * @param params parameters in the query
     * @param collectionParams list-based parameters in the query
     * @see doHQL(Session, String, Map<String, Object>, Map<String,Collection>)
     */
    public List doHQL(String hql, Map<String, Object> params,
        Map<String, Collection> collectionParams);
    
    /**
     * Do an HQL query with the given session.
     *
     * @param s session to use
     * @param hql HQL query string
     * @see doHQL(Session, String, Map<String, Object>, Map<String,Collection>)
     */
    public List doHQL(Session s, String hql);
    
    /**
     * Do an HQL query with the given session.
     *
     * @param s session to use
     * @param hql HQL query string
     * @param params parameters in the query
     * @see doHQL(Session, String, Map<String, Object>, Map<String,Collection>)
     */
    public List doHQL(Session s, String hql, Map<String, Object> params);

    /**
     * Allows the intelligent C++ programmer to simply fire complete HQL
     * statements to the DBS. The HQL is very similar to SQL, but differs
     * in a variety of important ways. See the hibernate documentation at
     * http://www.hibernate.org/hib_docs/reference/en/html/queryhql.html
     * for details. As a rule, you do not write 'SELECT *' but only
     * 'FROM <ClassName>' (note: not the table name, the @em class).
     *
     * The query is not performed as a complete transaction. The caller
     * must handle transactions.
     *
     * The query string may contain named parameters, for which values
     * will be substituted from the params and lparams arguments to this
     * method. For parameters that expect a single datum, put the mapping
     * from name to an object in the params argument. List-based parameters
     * (for instance the allowable values in a "IN ( foo, ... )" clause)
     * may be placed in the lparams argument. Either may be null if there
     * are no paramaters of that kind.
     *
     * @param s        the database session to use
     * @param hql      the HQL query string
     * @param params   named parameters to substitute in the query
     * @param lparams  list parameters to substitute in the query
     */
    public List doHQL(Session s, String hql, Map<String, Object> params,
        Map<String, Collection> lparams);
    
    /**
     * Get a session to the alitheia DB from the session manager
     * 
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
