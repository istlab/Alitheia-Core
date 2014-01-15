package eu.sqooss.test.service.abstractmetric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import eu.sqooss.service.abstractmetric.LockPair;
import eu.sqooss.test.EqualsHashCodeTest;

public class LockPairTest extends EqualsHashCodeTest {
	
	private Object left = new Object();
	private LockPair lockPair;

	@Override
	protected LockPair makeInstance() {
		return new LockPair(this.left, 2L);
	}

	@Override
	protected LockPair makeNotInstance() {
		return new LockPair(this.left, 3L);
	}

	@Before
	@Override
	public void setUp() {
		super.setUp();
		this.lockPair = this.makeInstance();
	}

	@Test
	public void testGetLeft() {
		assertSame(this.left, this.lockPair.getLeft());
	}

	@Test
	public void testGetRight() {
		assertEquals(new Long(2L), this.lockPair.getRight());
	}
	
	@Test
	public void testEqualsFalseLeft() {
		assertFalse(this.lockPair.equals(new LockPair(new Object(), 2L)));
	}
	
	@Test
	public void testEqualsFalseRight() {
		assertFalse(this.lockPair.equals(new LockPair(this.left, 3L)));
	}

	@Test
	public void testToString() {
		assertEquals("<LockPair[" + this.left.toString() + ", 2]>", this.lockPair.toString());
	}
}
