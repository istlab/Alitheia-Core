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

package eu.sqooss.vcs.test;

import junit.framework.TestCase;
import eu.sqooss.vcs.*;


/**
 * @author k.stroggylos
 *
 */
public class SvnRepositoryTest extends TestCase {

	private SvnRepository repository;
	
	public SvnRepositoryTest(String arg0) {
		super(arg0);
	}

	@Override
	protected void setUp() throws Exception {
		repository = (SvnRepository)RepositoryFactory.getRepository("./svntest", "https://svn.sqo-oss.eu/", "svnviewer", "Sq0V13weR", RepositoryType.SVN);
	}
	
	
	/**
	 * Test method for {@link eu.sqooss.vcs.SvnRepository#checkout()}.
	 */
	public void testCheckout() {
		
		repository.checkout();
		assertEquals(repository.getCurrentVersion(false), repository.getCurrentVersion((true)));
	}

	/**
	 * Test method for {@link eu.sqooss.vcs.SvnRepository#checkout(eu.sqooss.vcs.Revision)}.
	 */
	public void testCheckoutRevision() {
		repository.checkout(new Revision(800));
		assertEquals(repository.getCurrentVersion(false), 800);
	}

	/**
	 * Test method for {@link eu.sqooss.vcs.SvnRepository#update(eu.sqooss.vcs.Revision)}.
	 */
	public void testUpdate() {
		repository.checkout(new Revision(800));
		assertEquals(repository.getCurrentVersion(false), 800);
		
		Diff diff1 = repository.diff(new Revision(801));
		
		repository.update(new Revision(801));
		assertEquals(repository.getCurrentVersion(false), 801);
		
		Diff diff2 = repository.diff(new Revision(800), new Revision(801));
		
		assertEquals(diff1.size(), diff2.size());
	}

	/**
	 * Test method for {@link eu.sqooss.vcs.SvnRepository#diff(eu.sqooss.vcs.Revision)}.
	 */
	public void testDiffRevision() {

		repository.checkout(new Revision(800));
		assertEquals(repository.getCurrentVersion(false), 800);
		Diff diff = repository.diff(new Revision(801));
		assertNotNull(diff);
		assertTrue(diff.size() > 0);
	}

	/**
	 * Test method for {@link eu.sqooss.vcs.SvnRepository#diff(eu.sqooss.vcs.Revision, eu.sqooss.vcs.Revision)}.
	 */
	public void testDiffRevisionRevision() {

		repository.checkout(new Revision(800));
		assertEquals(repository.getCurrentVersion(false), 800);
		Diff diff = repository.diff(new Revision(800), new Revision(801));
		assertNotNull(diff);
		assertTrue(diff.size() > 0);
	}

	/**
	 * Test method for {@link eu.sqooss.vcs.SvnRepository#getLog(eu.sqooss.vcs.Revision, eu.sqooss.vcs.Revision)}.
	 */
	public void testGetLog() {
		repository.checkout(new Revision(800));
		assertEquals(repository.getCurrentVersion(false), 800);
		CommitLog commitLog = 
			repository.getLog(new Revision(800), new Revision(801));
		assertNotNull(commitLog);
		assertTrue(commitLog.size() > 0);
	}

	/**
	 * Test method for {@link eu.sqooss.vcs.SvnRepository#getCurrentVersion(boolean)}.
	 */
	public void testGetCurrentVersion() {
		repository.checkout(new Revision(800));
		assertEquals(repository.getCurrentVersion(false), 800);
		repository.checkout(); //update to the HEAD revision
		assertTrue(repository.getCurrentVersion(true) > 1000);
		assertEquals(repository.getCurrentVersion(false), repository.getCurrentVersion(true));
	}

	/**
	 * Test method for {@link eu.sqooss.vcs.SvnRepository#SvnRepository(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	public void testSvnRepository() {
		fail("Not yet implemented"); // TODO
	}

}
