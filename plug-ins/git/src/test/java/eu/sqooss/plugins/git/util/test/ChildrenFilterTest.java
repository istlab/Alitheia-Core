package eu.sqooss.plugins.git.util.test;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.mockito.Mock;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.plugins.git.util.ChildrenFilter;

public class ChildrenFilterTest {

	@Mock
	private RevCommit revCommit;
	private ChildrenFilter childrenFilter;
	private AlitheiaCore core;
	
	@Before
	public void setUp() {
		childrenFilter = new ChildrenFilter(revCommit);

		mockStatic(AlitheiaCore.class);
		core =  mock(AlitheiaCore.class);
	}
}
