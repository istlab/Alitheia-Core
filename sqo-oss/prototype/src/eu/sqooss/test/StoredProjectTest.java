/*
 * Copyright (c) Members of the SQO-OSS Collaboration, 2007
 * All rights reserved by respective owners.
 * See http://www.sqo-oss.eu/ for details on the copyright holders.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the SQO-OSS project nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.sqooss.test;

import org.hibernate.Session;

import eu.sqooss.util.HibernateUtil;

import eu.sqooss.db.StoredProject;

public class StoredProjectTest {

    public static void main(String[] args) {

	StoredProjectTest test = new StoredProjectTest();

	if (args[0].equals("store")) {
	    test.createAndStoreProject("My project", "My website",
				       "My contact point", "My src path",
				       "My mail path");
	}

	HibernateUtil.getSessionFactory().close();

    }

    private void createAndStoreProject(String name, String website,
				       String contactPoint, String srcPath,
				       String mailPath) {

	Session session =
	    HibernateUtil.getSessionFactory().getCurrentSession();

	session.beginTransaction();

	StoredProject theProject = new StoredProject();
	theProject.setName(name);
	theProject.setWebsite(website);
	theProject.setContactPoint(contactPoint);
	//theProject.(srcPath);
	theProject.setMailPath(mailPath);

	session.save(theProject);

	session.getTransaction().commit();
    }
}
	    
