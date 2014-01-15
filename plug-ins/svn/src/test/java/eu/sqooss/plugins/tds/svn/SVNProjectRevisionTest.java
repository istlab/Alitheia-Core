package eu.sqooss.plugins.tds.svn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.omg.SendingContext.RunTime;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import eu.sqooss.service.tds.CommitCopyEntry;
import eu.sqooss.service.tds.PathChangeType;
import eu.sqooss.service.tds.Revision;

@RunWith(MockitoJUnitRunner.class)
public class SVNProjectRevisionTest {

	private TestableSVNProjectRevision svnProjectRevision;

	private Date date;
	private long lowRevision;
	private long highRevision;
	private Map<String, PathChangeType> changedPaths;
	private List<CommitCopyEntry> copies;
	
	@Mock
	private CommitCopyEntry commitCopyEntry;
	
	class TestableSVNProjectRevision extends SVNProjectRevision {

		public TestableSVNProjectRevision(SVNLogEntry svnLogEntry, String string) {
			super(svnLogEntry, string);
		}

		public TestableSVNProjectRevision(long lowRevision) {
			super(lowRevision);
		}
		
		public TestableSVNProjectRevision(Date date) {
			super(date);
		}

		@Override
		protected CommitCopyEntry createCommitCopyEntry(String path,
				String copyPath, Long copyRev) {
			return commitCopyEntry;
		}
	}
	
	@Before
	public void setUp() {
		date = new Date();
		lowRevision = -1l;
		highRevision = 10l;

		changedPaths = new HashMap<String, PathChangeType>();
		copies = new ArrayList<CommitCopyEntry>();
	}
	
	@Test
	public void testGetDate() {
		svnProjectRevision = new TestableSVNProjectRevision(date);
		
		assertEquals(date, svnProjectRevision.getDate());
	}

	@Test
	public void testGetSVNRevision() {
		svnProjectRevision = new TestableSVNProjectRevision(lowRevision);
		
		assertEquals(lowRevision, svnProjectRevision.getSVNRevision());
	}
	
	@Test
	public void testConstructorNoChangedPaths() {
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(lowRevision);
		when(svnLogEntry.getChangedPaths()).thenReturn(changedPaths);
		
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
	}
	
	@Test
	public void testConstructorNoChangedPaths2() {
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		when(svnLogEntry.getChangedPaths()).thenReturn(changedPaths);
		
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
	}

	@Test
	public void testConstructorOneChangedPath() {
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		
		// Prepare the changed paths
		Map<String, SVNLogEntryPath> paths = new HashMap<String, SVNLogEntryPath>();
		String key1 = "key1";
		SVNLogEntryPath value1 = mock(SVNLogEntryPath.class);
		when(value1.getCopyPath()).thenReturn("copy-path-1");
		when(value1.getCopyRevision()).thenReturn(10l);
		
		paths.put(key1, value1);
		when(svnLogEntry.getChangedPaths()).thenReturn(paths);
		
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
	}

	@Test
	public void testConstructorMultipleChangedPaths() {
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		
		// Prepare the changed paths
		Map<String, SVNLogEntryPath> paths = new HashMap<String, SVNLogEntryPath>();
		String key1 = "key1";
		SVNLogEntryPath value1 = mock(SVNLogEntryPath.class);
		when(value1.getCopyPath()).thenReturn("copy-path-1");
		when(value1.getCopyRevision()).thenReturn(10l);
		
		String key2 = "root/key2";
		SVNLogEntryPath value2 = mock(SVNLogEntryPath.class);
		when(value2.getCopyPath()).thenReturn("copy-path-2");
		when(value2.getCopyRevision()).thenReturn(10l);
		
		paths.put(key1, value1);
		paths.put(key2, value2);
		
		when(svnLogEntry.getChangedPaths()).thenReturn(paths);
		
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
	}

	@Test
	public void testConstructorMultipleChangedPaths2() {
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		
		// Prepare the changed paths
		Map<String, SVNLogEntryPath> paths = new HashMap<String, SVNLogEntryPath>();
		String key1 = "key1";
		SVNLogEntryPath value1 = mock(SVNLogEntryPath.class);
		when(value1.getCopyPath()).thenReturn(null);
		when(value1.getCopyRevision()).thenReturn(10l);
		
		String key2 = "root/key2";
		SVNLogEntryPath value2 = mock(SVNLogEntryPath.class);
		when(value2.getCopyPath()).thenReturn("copy-path-2");
		when(value2.getCopyRevision()).thenReturn(-1l);
		
		paths.put(key1, value1);
		paths.put(key2, value2);
		
		when(svnLogEntry.getChangedPaths()).thenReturn(paths);
		
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
	}

	@Test
	public void testParseSVNLogEntryPathTYPE_ADDED() {
		char entryPathType = SVNLogEntryPath.TYPE_ADDED;
		
		svnProjectRevision = new TestableSVNProjectRevision(date);
		PathChangeType pathChangeType = svnProjectRevision.parseSVNLogEntryPath(entryPathType);
		
		assertEquals(pathChangeType, PathChangeType.ADDED);
	}
	
	@Test
	public void testParseSVNLogEntryPathTYPE_DELETED() {
		char entryPathType = SVNLogEntryPath.TYPE_DELETED;
		
		svnProjectRevision = new TestableSVNProjectRevision(date);
		PathChangeType pathChangeType = svnProjectRevision.parseSVNLogEntryPath(entryPathType);
		
		assertEquals(pathChangeType, PathChangeType.DELETED);
	}
	
	@Test
	public void testParseSVNLogEntryPathTYPE_MODIFIED() {
		char entryPathType = SVNLogEntryPath.TYPE_MODIFIED;
		
		svnProjectRevision = new TestableSVNProjectRevision(date);
		PathChangeType pathChangeType = svnProjectRevision.parseSVNLogEntryPath(entryPathType);
		
		assertEquals(pathChangeType, PathChangeType.MODIFIED);
	}
	
	@Test
	public void testParseSVNLogEntryPathTYPE_REPLACED() {
		char entryPathType = SVNLogEntryPath.TYPE_REPLACED;
		
		svnProjectRevision = new TestableSVNProjectRevision(date);
		PathChangeType pathChangeType = svnProjectRevision.parseSVNLogEntryPath(entryPathType);
		
		assertEquals(pathChangeType, PathChangeType.REPLACED);
	}
	
	@Test
	public void testParseSVNLogEntryPathTYPE_OTHER() {
		char entryPathType = '!';
		
		svnProjectRevision = new TestableSVNProjectRevision(date);
		PathChangeType pathChangeType = svnProjectRevision.parseSVNLogEntryPath(entryPathType);
		
		assertEquals(pathChangeType, PathChangeType.UNKNOWN);
	}

	@Test
	public void testIsResolvedFail1() {
		svnProjectRevision = new TestableSVNProjectRevision(lowRevision);
		boolean result = svnProjectRevision.isResolved();
		
		assertFalse(result);
	}
	
	@Test
	public void testIsResolvedFail2() {
		svnProjectRevision = new TestableSVNProjectRevision(highRevision);
		boolean result = svnProjectRevision.isResolved();
		
		assertFalse(result);
	}
	
	@Test
	public void testIsResolvedFail3() {
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn(null);
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		when(svnLogEntry.getChangedPaths()).thenReturn(changedPaths);
		
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
		boolean result = svnProjectRevision.isResolved();
		
		assertFalse(result);
	}
	
	@Test
	public void testIsResolvedFail4() {
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn(null);
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		when(svnLogEntry.getChangedPaths()).thenReturn(changedPaths);
		
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
		boolean result = svnProjectRevision.isResolved();
		
		assertFalse(result);
	}
	
	@Test
	public void testIsResolvedSuccess() {
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		when(svnLogEntry.getChangedPaths()).thenReturn(changedPaths);
		
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
		boolean result = svnProjectRevision.isResolved();
		
		assertTrue(result);
	}
	
	@Test
	public void testGetUniqueID() {
		svnProjectRevision = new TestableSVNProjectRevision(highRevision);
		String uniqueID = svnProjectRevision.getUniqueId();
		
		assertEquals(uniqueID, String.valueOf(highRevision));
	}
	
	@Test
	public void testGetAuthor() {
		String author = "author";
		
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn(author);
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		when(svnLogEntry.getChangedPaths()).thenReturn(changedPaths);
		
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
		String resultAuthor = svnProjectRevision.getAuthor();
		
		assertEquals(resultAuthor, author);
	}
	
	@Test
	public void testGetMessage() {
		String message = "message";
		
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn(message);
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		when(svnLogEntry.getChangedPaths()).thenReturn(changedPaths);
		
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
		String resultMessage = svnProjectRevision.getMessage();
		
		assertEquals(resultMessage, message);
	}
	
	@Test
	public void testGetChangedPaths() {
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		
		// Prepare the changed paths
		Map<String, SVNLogEntryPath> paths = new HashMap<String, SVNLogEntryPath>();
		String key1 = "key1";
		SVNLogEntryPath value1 = mock(SVNLogEntryPath.class);
		when(value1.getCopyPath()).thenReturn("copy-path-1");
		when(value1.getCopyRevision()).thenReturn(10l);
		
		paths.put(key1, value1);
		when(svnLogEntry.getChangedPaths()).thenReturn(paths);
		
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
		Set<String> result = svnProjectRevision.getChangedPaths();
		
		assertEquals(result, changedPaths.keySet());
	}
	
	@Test
	public void testGetChangedPathsStatus() {
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		
		// Prepare the changed paths
		Map<String, SVNLogEntryPath> paths = new HashMap<String, SVNLogEntryPath>();
		String key1 = "key1";
		SVNLogEntryPath value1 = mock(SVNLogEntryPath.class);
		when(value1.getCopyPath()).thenReturn("copy-path-1");
		when(value1.getCopyRevision()).thenReturn(10l);
		
		paths.put(key1, value1);
		when(svnLogEntry.getChangedPaths()).thenReturn(paths);
		
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
		Map<String, PathChangeType> result = svnProjectRevision.getChangedPathsStatus();
		
		assertEquals(result, changedPaths);
	}
	
	@Test
	public void testGetCopyOperations() {
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		
		// Prepare the changed paths
		Map<String, SVNLogEntryPath> paths = new HashMap<String, SVNLogEntryPath>();
		String key1 = "key1";
		SVNLogEntryPath value1 = mock(SVNLogEntryPath.class);
		when(value1.getCopyPath()).thenReturn("copy-path-1");
		when(value1.getCopyRevision()).thenReturn(10l);
		
		paths.put(key1, value1);
		when(svnLogEntry.getChangedPaths()).thenReturn(paths);
		
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
		List<CommitCopyEntry> result = svnProjectRevision.getCopyOperations();
		
		assertEquals(result.size(), 1);
		assertEquals(result.get(0), commitCopyEntry);
	}

	@Test
	public void testGetParentIds() {
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		when(svnLogEntry.getChangedPaths()).thenReturn(changedPaths);
		
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
		Set<String> parentIDs = svnProjectRevision.getParentIds();
		
		assertEquals(parentIDs.size(), 1);
		assertTrue(parentIDs.contains("9"));
	}

	@Test(expected=RuntimeException.class)
	public void testCompareToIncorrectClass() {
		svnProjectRevision = new TestableSVNProjectRevision(highRevision);
		Revision revision = mock(Revision.class);
		svnProjectRevision.compareTo(revision);
	}

	@Test(expected=RuntimeException.class)
	public void testCompareToOtherNotResolved() {
		svnProjectRevision = new TestableSVNProjectRevision(highRevision);
		SVNProjectRevision svnProjectRevision2 = new TestableSVNProjectRevision(date);
		
		svnProjectRevision.compareTo(svnProjectRevision2);
	}
	
	@Test(expected=RuntimeException.class)
	public void testCompareToThisNotResolved() {
		svnProjectRevision = new TestableSVNProjectRevision(highRevision);
		
		// Set up a resolved other revision
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		when(svnLogEntry.getChangedPaths()).thenReturn(changedPaths);
		SVNProjectRevision svnProjectRevision2 = new TestableSVNProjectRevision(svnLogEntry, "root");
		
		svnProjectRevision.compareTo(svnProjectRevision2);
	}
	
	@Test
	public void testCompareToBothResolved() {
		// Set up a resolved other revision
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		when(svnLogEntry.getChangedPaths()).thenReturn(changedPaths);
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
		
		// Set up a resolved other revision
		SVNLogEntry svnLogEntry2 = mock(SVNLogEntry.class);
		when(svnLogEntry2.getAuthor()).thenReturn("author");
		when(svnLogEntry2.getMessage()).thenReturn("message");
		when(svnLogEntry2.getDate()).thenReturn(date);
		when(svnLogEntry2.getRevision()).thenReturn(highRevision - 1);
		when(svnLogEntry2.getChangedPaths()).thenReturn(changedPaths);
		SVNProjectRevision svnProjectRevision2 = new TestableSVNProjectRevision(svnLogEntry2, "root");
		
		int result = svnProjectRevision.compareTo(svnProjectRevision2);
		assertEquals(result, 1);
	}
	
	@Test
	public void testCompare() {
		// Set up a resolved other revision
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		when(svnLogEntry.getChangedPaths()).thenReturn(changedPaths);
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
		
		// Set up a resolved other revision
		SVNLogEntry svnLogEntry2 = mock(SVNLogEntry.class);
		when(svnLogEntry2.getAuthor()).thenReturn("author");
		when(svnLogEntry2.getMessage()).thenReturn("message");
		when(svnLogEntry2.getDate()).thenReturn(date);
		when(svnLogEntry2.getRevision()).thenReturn(highRevision - 1);
		when(svnLogEntry2.getChangedPaths()).thenReturn(changedPaths);
		SVNProjectRevision svnProjectRevision2 = new TestableSVNProjectRevision(svnLogEntry2, "root");
		
		int result = svnProjectRevision.compareTo(svnProjectRevision2);
		int result2 = svnProjectRevision.compare(svnProjectRevision, svnProjectRevision2);
		
		assertEquals(result, result2);
	}
	
	@Test
	public void testToStringNotResolved() {
		svnProjectRevision = new TestableSVNProjectRevision(highRevision);
		String result = svnProjectRevision.toString();
		
		assertNull(result);
	}
	
	@Test
	public void testToStringResolved() {
		// Set up a resolved other revision
		SVNLogEntry svnLogEntry = mock(SVNLogEntry.class);
		when(svnLogEntry.getAuthor()).thenReturn("author");
		when(svnLogEntry.getMessage()).thenReturn("message");
		when(svnLogEntry.getDate()).thenReturn(date);
		when(svnLogEntry.getRevision()).thenReturn(highRevision);
		when(svnLogEntry.getChangedPaths()).thenReturn(changedPaths);
		
		svnProjectRevision = new TestableSVNProjectRevision(svnLogEntry, "root");
		String result = svnProjectRevision.toString();
		
		assertEquals(result, "r" + highRevision + " - (author): message");
	}
}
