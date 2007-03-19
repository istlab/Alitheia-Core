/**
 * 
 */
package eu.sqooss.vcs.test;

import eu.sqooss.vcs.*;
import junit.framework.TestCase;

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
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link eu.sqooss.vcs.SvnRepository#getCurrentVersion(boolean)}.
	 */
	public void testGetCurrentVersion() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link eu.sqooss.vcs.SvnRepository#SvnRepository(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	public void testSvnRepository() {
		fail("Not yet implemented"); // TODO
	}

}
