/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Georgios Gousios <gousiosg@gmail.com>
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import eu.sqooss.impl.service.CoreActivator;
import eu.sqooss.service.db.DAObject;

/**
 * A developer belonging to a project.  
 */
public class Developer extends DAObject{
    /**
	 * The developer's name
	 */
	private String name;

	/**
	 * The developer's email
	 */
	private String email;

	/**
	 * The developer's username
	 */
	private String username;

	/**
	 * The project this developer belongs to
	 */
	private StoredProject storedProject;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public StoredProject getStoredProject() {
		return storedProject;
	}

	public void setStoredProject(StoredProject storedProject) {
		this.storedProject = storedProject;
	}
    
    /**
	 * Return the entry in the Developer table that corresponds to the provided
	 * email. If the entry does not exist, it will be created and saved. If the
	 * email username (the part before @) exists in the database, then this
	 * record is updated with the provided email and returned.
	 * 
	 * @param email
	 *            The Developer's email
	 * @param sp
	 *            The StoredProject this Developer belongs to
	 * @return A Developer record for the specified Developer or null when:
	 *         <ul>
	 *         <li>The passed StoredProject does not exist</li>
	 *         <li>The passed email is invalid syntactically</li>
	 *         <ul>
	 */
    public static Developer getDeveloperByEmail(Session s, 
    		String email, StoredProject sp){
        DBService dbs = CoreActivator.getDBService();
        
        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("email", email);
        parameterMap.put("storedProject", sp);
        
        List<Developer> devs = dbs.findObjectByProperties(s, Developer.class,
                parameterMap);
        
        /* Developer in the DB, return it */
        if(devs.size() > 0)
            return devs.get(0);
        
        parameterMap.clear();
        String unameFromEmail = email.substring(0, email.indexOf('@'));
        
        if (unameFromEmail == "" || unameFromEmail == email)
            return null;
        
        parameterMap.put("username", unameFromEmail);
        parameterMap.put("storedProject", sp);
        
        devs = dbs.findObjectByProperties(s, Developer.class,
                parameterMap);
        
        /* Developer's uname in table, update with email and return it */
        if(devs.size() > 0) {
            Developer d = devs.get(0);
            d.setEmail(email);
            s.update(s);
            return d;
        }
        
        /* Developer email not in table, create it new developer*/ 
        Developer d = new Developer();

        d.setEmail(email);
        d.setStoredProject(sp);
        
        /*Failure here probably indicates non-existing StoredProject*/
        s.save(d);
        
        return d;
    }
    
    /** 
     * Session-less implementation of {@link  Developer.getDeveloperByEmail} 
     */
    public static Developer getDeveloperByEmail(String email, 
    		StoredProject sp) {
    	Object sessionHolder = new Object();
    	DBService dbs = CoreActivator.getDBService();
    	Session s = dbs.getSession(sessionHolder);
    	Developer d = getDeveloperByUsername(s, email, sp);
    	try {
    		s.getTransaction().commit();
    	} catch (HibernateException e) {
    		s.getTransaction().rollback();
    		d = null;
    	} finally {
    		dbs.returnSession(s);
    	}
    	return d;
    }
   
    /**
	 * Return the entry in the Developer table that corresponds to the provided
	 * username. If the entry does not exist, it will be created and saved. If
	 * the username matches the email of an existing developer in the database,
	 * then this record is updated with the provided username and returned.
	 * 
	 * This method comes in two flavours that enable its use in both 
	 * manual and automatic transaction management environments.
	 * 
	 * @param s The Session to use when accessing the database
	 * @param username The Developer's username
	 * @param sp The StoredProject this Developer belongs to
	 * @return A Developer record for the specified Developer or null on failure
	 */
    public static Developer getDeveloperByUsername(Session s, 
    		String username, StoredProject sp) {
		
		DBService dbs = CoreActivator.getDBService();

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("username", username);
		parameterMap.put("storedProject", sp);

		List<Developer> devs = dbs.findObjectByProperties(s, Developer.class,
				parameterMap);

		/* 
		 * Developer in the DB, return it
		 * Username + storedproject is unique, so only one record 
		 * can be returned by the query 
		 */
		if (devs.size() > 0)
			return devs.get(0);

		/* 
		 * Try to find a Developer whose email starts with username
		 *  
		 * TODO: "like" is NOT a Hibernate keyword. The following query might 
		 * only work with postgres  
		 */
		devs = dbs.doHQL(s, "from Developer where email like '" + username + "'");

		for (Developer d : devs) {
			String email = d.getEmail();
			/*Ok got one, update the username*/
			if (email.startsWith(username)) {
				d.setUsername(username);
				s.update(s);
				return d;
			}
		}

		/* Developer not in table, create new developer*/
		Developer d = new Developer();

		d.setUsername(username);
		d.setStoredProject(sp);

		/*Failure here probably indicates non-existing StoredProject*/
		s.save(d);
		
		return d;
	}   
    
    /** 
     * Session-less implementation of {@link  Developer.getDeveloperByUsername} 
     */
    public static Developer getDeveloperByUsername(String username, 
    		StoredProject sp) {
    	Object sessionHolder = new Object();
    	DBService dbs = CoreActivator.getDBService();
    	
    	Session s = dbs.getSession(sessionHolder);
    	Developer d = getDeveloperByUsername(s, username, sp);
    	try {
    		s.getTransaction().commit();
    	} catch (HibernateException e) {
    		s.getTransaction().rollback();
    		d = null;
    	} finally {
    		dbs.returnSession(s);
    	}
    	return d;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

