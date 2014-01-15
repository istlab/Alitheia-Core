package eu.sqooss.test.metrics.java;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import eu.sqooss.metrics.java.ReducePair;
import eu.sqooss.test.EqualsHashCodeTest;

public class ReducePairTest extends EqualsHashCodeTest {
	
	private ReducePair<String, String> reducePair;

	@Override
	protected ReducePair<String, String> makeInstance() {
		return new ReducePair<>("foo", "bar");
	}

	@Override
	protected ReducePair<String, String> makeNotInstance() {
		return new ReducePair<>("test", "123");
	}

	@Before
	@Override
	public void setUp() {
		super.setUp();
		this.reducePair = this.makeInstance();
	}

	@Test
	public void testGetLeft() {
		assertEquals("foo", this.reducePair.getLeft());
	}
	
	@Test
	public void testGetRight() {
		assertEquals("bar", this.reducePair.getRight());
	}

	@Test
	public void testEqualsFalseLeft() {
		assertFalse(this.reducePair.equals(new ReducePair<>("foo2", "bar")));
	}

	@Test
	public void testEqualsFalseRight() {
		assertFalse(this.reducePair.equals(new ReducePair<>("foo", "bar2")));
	}

	@Test
	public void testToString() {
		assertEquals("<ReducePair[foo, bar]>", this.reducePair.toString());
	}
}
