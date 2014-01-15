package eu.sqooss.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public abstract class EqualsHashCodeTest {

	private Object x;
	private Object y;
	private Object z;
	private Object notX;

	protected abstract Object makeInstance();

	protected abstract Object makeNotInstance();

	@Before
	public void setUp() {
		this.x = this.makeInstance();
		this.y = this.makeInstance();
		this.z = this.makeInstance();
		this.notX = this.makeNotInstance();
	}

	@Test
	public void testEqualsReflexive() {
		assertTrue(this.x.equals(this.x));
	}

	@Test
	public void testEqualsSymmetric() {
		assertTrue(this.x.equals(this.y));
		assertTrue(this.y.equals(this.x));
	}

	@Test
	public void testEqualsTransitive() {
		assertTrue(this.x.equals(this.y));
		assertTrue(this.y.equals(this.z));
		assertTrue(this.x.equals(this.z));
	}

	@Test
	public void testEqualsConsistent() {
		assertTrue(this.x.equals(this.y));
		assertTrue(this.x.equals(this.y));
		assertTrue(this.x.equals(this.y));

		assertFalse(this.notX.equals(this.x));
		assertFalse(this.notX.equals(this.x));
		assertFalse(this.notX.equals(this.x));
	}

	@Test
	public void testEqualsNull() {
		assertFalse(this.x.equals(null));
	}

	@Test
	public void testEqualsOtherType() {
		assertFalse(this.x.equals("string"));
	}

	@Test
	public void testHashCodeConsistency() {
		int initHashCode = this.x.hashCode();

		assertEquals(initHashCode, this.x.hashCode());
		assertEquals(initHashCode, this.x.hashCode());
	}

	@Test
	public void testHashCodeSame() {
		assertEquals(this.x.hashCode(), this.y.hashCode());
	}
}
