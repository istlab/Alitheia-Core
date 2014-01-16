package eu.sqooss.plugins.tds.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.plugins.tds.git.GitCommitLog;
import eu.sqooss.plugins.tds.scm.SCMCommitLog;
import eu.sqooss.plugins.tds.scm.SCMProjectRevision;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.Revision;

@RunWith(MockitoJUnitRunner.class)
public class GitCommitLogTest {
	
	private SCMCommitLog gitCommitLog;
	
	private List<Revision> entries;
	@Mock
	SCMProjectRevision gitRevision1;
	@Mock
	SCMProjectRevision gitRevision2;
	@Mock
	SCMProjectRevision gitRevision3;
	
	@Before
    public void setUp() throws AccessorException, URISyntaxException {
		gitCommitLog = new GitCommitLog();
		entries = gitCommitLog.getEntries();
		entries.add(gitRevision1);
		entries.add(gitRevision2);
		entries.add(gitRevision3);
    }
	
	@Test
	public void testGetEntries() {
		List<Revision> entries = gitCommitLog.getEntries();
		assertTrue(entries.equals(this.entries));
	}
	
	@Test
	public void testGetIterator() {
		assertTrue(gitCommitLog.iterator() instanceof Iterator<?>);
		Object next = gitCommitLog.iterator().next();
		assertTrue(next instanceof Revision);
		assertEquals(next, gitRevision1);
	}
	
	@Test
	public void testGetFirst() {
		assertEquals(gitCommitLog.first(), gitRevision1);
	}

	@Test
	public void testGetFirstEmptyList() {
		entries.clear();
		assertNull(gitCommitLog.first());
	}
	
	@Test
	public void testGetLast() {
		assertEquals(gitCommitLog.last(), gitRevision3);
	}

	@Test
	public void testGetLastEmptyList() {
		entries.clear();
		assertNull(gitCommitLog.last());
	}
	
	@Test
	public void testSize() {
		assertEquals(gitCommitLog.size(), 3);
	}
}
