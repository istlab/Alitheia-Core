package eu.sqooss.plugins.updater.git.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import eu.sqooss.plugins.updater.git.Transition;
import eu.sqooss.service.db.FileState;
import eu.sqooss.test.EqualsHashCodeTest;

public class TransitionTest extends EqualsHashCodeTest {
	
	private Transition transition;

	@Override
	protected Transition makeInstance() {
		return new Transition(FileState.ADDED, FileState.DELETED);
	}

	@Override
	protected Transition makeNotInstance() {
		return new Transition(FileState.MODIFIED, FileState.DELETED);
	}
	
	@Before
	@Override
	public void setUp() {
		super.setUp();
		this.transition = this.makeInstance();
	}

	@Test
	public void testGetLeft() {
		assertEquals(FileState.ADDED, this.transition.getLeft());
	}
	
	@Test
	public void testGetRight() {
		assertEquals(FileState.DELETED, this.transition.getRight());
	}

	@Test
	public void testEqualsFalseLeft() {
		assertFalse(this.transition.equals(new Transition(FileState.DELETED, FileState.DELETED)));
	}
	
	@Test
	public void testEqualsFalseRight() {
		assertFalse(this.transition.equals(new Transition(FileState.ADDED, FileState.REPLACED)));
	}

	@Test
	public void testToString() {
		assertEquals("<Transition[ADDED, DELETED]>", this.transition.toString());
	}
}
