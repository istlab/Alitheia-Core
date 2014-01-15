package eu.sqooss.plugins.git.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ChildrenFilterTest {

	@Mock
	private RevWalk revWalker;

	// RevCommit's for testing
	private RevCommit revCommit = mock(RevCommit.class);
	private RevCommit revCommit1 = mock(RevCommit.class);
	private RevCommit revCommit2 = mock(RevCommit.class);
	private RevCommit revCommit3 = mock(RevCommit.class);
	private RevCommit parent1 = mock(RevCommit.class);
	private RevCommit parent2 = mock(RevCommit.class);
	private RevCommit[] parents1 = {parent1, parent2};
	private RevCommit[] parents2 = {parent1, revCommit};
	private RevCommit[] parents3 = {revCommit, revCommit};
	
	private ChildrenFilter childrenFilter;
	
	@Before
	public void setUp() {
		childrenFilter = new TestableChildrenFilter(revCommit);
	}
	
	class TestableChildrenFilter extends ChildrenFilter {
		public TestableChildrenFilter(RevCommit c) {
			super(c);
		}

		/**
		 * Solely for the purpose of testing, this method returns a different set of parents
		 * depending on the {@link RevCommit} object that is passed in.
		 */
		@Override
		protected RevCommit[] getParents(RevCommit cmit) {
			if (cmit == revCommit1) {
				return parents1;
			}
			else if (cmit == revCommit2) {
				return parents2;
			}
			else if (cmit == revCommit3) {
				return parents3;
			}
			else {
				return parents1;
			}
		}
		
		@Override
		protected int compareRevCommits(RevCommit p) {
			return p == revCommit ? 0 : 1;
		}
	}
	
	@Test
	public void testIncludeNoMatch() throws StopWalkException, MissingObjectException, IncorrectObjectTypeException, IOException {
		boolean result = childrenFilter.include(revWalker, revCommit1);
		
		assertFalse(result);
	}
	
	@Test
	public void testIncludeOneMatch() throws StopWalkException, MissingObjectException, IncorrectObjectTypeException, IOException {
		boolean result = childrenFilter.include(revWalker, revCommit2);
		
		assertTrue(result);
	}

	@Test
	public void testIncludeMultipleMatches() throws StopWalkException, MissingObjectException, IncorrectObjectTypeException, IOException {
		boolean result = childrenFilter.include(revWalker, revCommit3);
		
		assertTrue(result);
	}
	
	@Test
	public void testClone() {
		RevFilter clonedChildrenFilter = childrenFilter.clone();
	}
}
