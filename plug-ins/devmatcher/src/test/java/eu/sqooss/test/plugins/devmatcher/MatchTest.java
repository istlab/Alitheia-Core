package eu.sqooss.test.plugins.devmatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import eu.sqooss.plugins.devmatcher.Match;
import eu.sqooss.test.EqualsHashCodeTest;

public class MatchTest extends EqualsHashCodeTest {
	
	private Match match;

	@Override
	protected Match makeInstance() {
		return new Match(2L, 3L);
	}

	@Override
	protected Match makeNotInstance() {
		return new Match(4L, 5L);
	}

	@Before
	@Override
	public void setUp() {
		super.setUp();
		this.match = this.makeInstance();
	}

	@Test
	public void testGetLeft() {
		assertEquals(new Long(2L), this.match.getLeft());
	}
	
	@Test
	public void testGetRight() {
		assertEquals(new Long(3L), this.match.getRight());
	}

	@Test
	public void testEqualsFalseLeft() {
		assertFalse(this.match.equals(new Match(4L, 3L)));
	}

	@Test
	public void testEqualsFalseRight() {
		assertFalse(this.match.equals(new Match(2L, 4L)));
	}

	@Test
	public void testToString() {
		assertEquals("<Match[2, 3]>", this.match.toString());
	}
}
