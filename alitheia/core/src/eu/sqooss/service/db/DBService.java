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

import java.util.List;
import java.util.Map;

import org.hibernate.Session;

public interface DBService {
    /**
     * Add a new record to the system database; this should initialize
     * any tables that are needed for storage of project information.
     */
    public void addRecord(DAObject record);

    public void addRecord(Session s, DAObject record);
    
    /**
     * Delete an existing record from the system database
     */
    public void deleteRecord(DAObject record);
    
    public void deleteRecord(Session s, DAObject record);
    
    /**
     * Allows the intelligent C++ programmer to simply fire complete HQL
     * statements to the DBS. The HQL is very similar to SQL, but differs
     * in a variety of important ways. See the hibernate documentation at
     * http://www.hibernate.org/hib_docs/reference/en/html/queryhql.html
     * for details. As a rule, you do not write 'SELECT *' but only
     * 'FROM <ClassName>' (note: not the table name, the @em class).
     */
    public List doSQL(String sql);
    
    public List doSQL(String sql, Map<String, Object> params);
    
    public List doSQL(Session s, String sql);
    
    public List doSQL(Session s, String sql, Map<String, Object> params);

    /**
     * This function shall perform an HQL query
     * This shall be used to replace the doSQL function
     * which shall be repurposed to actually do SQL!
     */
    public List doHQL(String hql);
    
    public List doHQL(String hql, Map<String, Object> params);
    
    public List doHQL(Session s, String hql);
    
    public List doHQL(Session s, String hql, Map<String, Object> params);
    
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
