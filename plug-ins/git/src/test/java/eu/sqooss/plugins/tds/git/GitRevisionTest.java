package eu.sqooss.plugins.tds.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.plugins.tds.git.GitAccessor;
import eu.sqooss.plugins.tds.git.GitRevision;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.CommitCopyEntry;
import eu.sqooss.service.tds.PathChangeType;
import eu.sqooss.service.tds.Revision;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RevCommit.class)
public class GitRevisionTest {

	// The object that is tested
	private GitRevision gitRevision;
	private GitRevision gitRevision2;
	
	// The objects that are passed as arguments to the constructor
	private RevCommit revCommit;
	private GitAccessor gitAccessor;
	private Map<String, PathChangeType> paths;
	private List<CommitCopyEntry> copies;

	private Date date = new Date(1);
	private PersonIdent personIdent = mock(PersonIdent.class);
	private ObjectId objectID = mock(ObjectId.class);
	private RevCommit parent1 = mock(RevCommit.class);
	private RevCommit parent2 = mock(RevCommit.class);
	
	/*
	 * Testable version of the Git Revision class.
	 * This class provides implementations of all calls to final and static methods that are made from the original class.
	 */
	class TestableGitRevision extends GitRevision {

		public TestableGitRevision(RevCommit obj, GitAccessor git) {
			super(obj, git);
		}

		public TestableGitRevision(RevCommit obj, Map<String, PathChangeType> paths,
	            List<CommitCopyEntry> copies) {
			super(obj, paths, copies);
		}
		
		@Override
		protected RevCommit[] getRevCommitParents(RevCommit obj) {
			return new RevCommit[] {parent1, parent2};
		}
		
		@Override
		protected String getRevCommitName(RevCommit s) {
			return "ParentName";
		}
		
		@Override
		protected ObjectId getRevCommitID(RevCommit obj) {
			return objectID;
		}

		@Override
		protected String getRevCommitIDName(RevCommit obj) {
			return "ID";
		}
		
		@Override
	    protected String getRevCommitFullMessage(RevCommit obj) {
			return "Full Message";
		}
	    
		@Override
		protected PersonIdent getRevCommitAuthorIdent(RevCommit obj) {
			return personIdent;
		}
	}
	
	@Before
    public void setUp() throws AccessorException, URISyntaxException {
		revCommit = mock(RevCommit.class);
		gitAccessor = mock(GitAccessor.class);
		
		paths = new HashMap<String, PathChangeType>();
		copies = new ArrayList<CommitCopyEntry>();

		// Set up the behaviour for the resolve() method
		gitRevision2 = new TestableGitRevision(revCommit, paths, copies);
		when(gitAccessor.getRevision((RevCommit) any(), eq(true))).thenReturn(gitRevision2);
		
		when(personIdent.getWhen()).thenReturn(date);
		when(personIdent.getName()).thenReturn("Name");
		when(personIdent.getEmailAddress()).thenReturn("E-Mail");
    }
	
	@Test
	public void testConstructorCommitAccessor() {
		gitRevision = new TestableGitRevision(revCommit, gitAccessor);
	}
	
	@Test
	public void testConstructorCommitPathsCopies() {
		gitRevision = new TestableGitRevision(revCommit, paths, copies);
	}
	
	@Test
	public void testIsResolved() {
		gitRevision = new TestableGitRevision(revCommit, gitAccessor);
		
		assertTrue(gitRevision.isResolved());
		// Test the second path: already resolved
		assertTrue(gitRevision.isResolved());
	}
	
	@Test
	public void testGetDate() {
		gitRevision = new TestableGitRevision(revCommit, gitAccessor);
		
		assertEquals(gitRevision.getDate(), date);
	}
	
	@Test
	public void testGetUniqueId() {
		gitRevision = new TestableGitRevision(revCommit, gitAccessor);
		
		assertEquals(gitRevision.getUniqueId(), "ID");
	}
	
	@Test
	public void testGetAuthor() {
		gitRevision = new TestableGitRevision(revCommit, gitAccessor);
		
		assertEquals(gitRevision.getAuthor(), "Name <E-Mail>");
	}
	
	@Test
	public void testGetMessage() {
		gitRevision = new TestableGitRevision(revCommit, gitAccessor);
		
		assertEquals(gitRevision.getMessage(), "Full Message");
	}
	
	@Test
	public void testGetChangedPaths() {
		gitRevision = new TestableGitRevision(revCommit, gitAccessor);
		
		assertEquals(gitRevision.getChangedPaths(), paths.keySet());
	}
	
	@Test
	public void testGetChangedPathsStatus() {
		gitRevision = new TestableGitRevision(revCommit, gitAccessor);
		
		assertEquals(gitRevision.getChangedPathsStatus(), paths);
	}
	
	@Test
	public void testGetCopyOperations() {
		gitRevision = new TestableGitRevision(revCommit, gitAccessor);
		
		assertEquals(gitRevision.getCopyOperations(), copies);
	}
	
	@Test
	public void testToString() {
		gitRevision = new TestableGitRevision(revCommit, gitAccessor);
		String expected = "ID - " + date.toString() + " - Name <E-Mail>";
		
		assertEquals(gitRevision.toString(), expected);
	}
	
	@Test
	public void testCompare() {
		gitRevision = new TestableGitRevision(revCommit, gitAccessor);
		
		// Instantiate three comparison objects, change the behaviour of 
		// thepersonIdent.getWhen method to facilitate different dates.
		GitRevision gitRevisionSame = new TestableGitRevision(revCommit, gitAccessor);
		when(personIdent.getWhen()).thenReturn(new Date(2));
		GitRevision gitRevisionLater = new TestableGitRevision(revCommit, gitAccessor);
		when(personIdent.getWhen()).thenReturn(new Date(0));
		GitRevision gitRevisionEarlier = new TestableGitRevision(revCommit, gitAccessor);

		assertEquals(gitRevision.compare(gitRevision, gitRevisionSame), 0);
		assertEquals(gitRevision.compare(gitRevision, gitRevisionEarlier), 1);
		assertEquals(gitRevision.compare(gitRevision, gitRevisionLater), -1);
	}
	
	@Test(expected=RuntimeException.class)
	public void testCompareNotGitRevision() {
		gitRevision = new TestableGitRevision(revCommit, gitAccessor);
		
		Revision noGitRevision = new Revision() {
			@Override
			public int compare(Revision o1, Revision o2) {
				return 0;
			}
			
			@Override
			public int compareTo(Revision o) {
				return 0;
			}
			
			@Override
			public String getUniqueId() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Set<String> getParentIds() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getMessage() {
				return null;
			}
			
			@Override
			public java.util.Date getDate() {
				return null;
			}
			
			@Override
			public List<CommitCopyEntry> getCopyOperations() {
				return null;
			}
			
			@Override
			public Map<String, PathChangeType> getChangedPathsStatus() {
				return null;
			}
			
			@Override
			public Set<String> getChangedPaths() {
				return null;
			}
			
			@Override
			public String getAuthor() {
				return null;
			}
		};
		
		gitRevision.compare(gitRevision, noGitRevision);
	}
	
	@Test
	public void testGetParentIds() {
		gitRevision = new TestableGitRevision(revCommit, gitAccessor);
		
		assertTrue(gitRevision.getParentIds().contains("ParentName"));
	}
}
