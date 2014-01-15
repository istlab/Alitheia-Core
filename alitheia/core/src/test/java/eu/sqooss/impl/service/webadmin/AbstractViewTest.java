package eu.sqooss.impl.service.webadmin;

import static org.junit.Assert.assertEquals;

import org.apache.velocity.VelocityContext;
import org.junit.Test;
import org.osgi.framework.BundleContext;

public class AbstractViewTest {

	@Test
	public void testCheckMailNull() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		boolean check = view.checkEmail(null);

		// Assert
		assertEquals(false, check);
	}

	@Test
	public void testCheckMailSubsequentDots() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		boolean check = view.checkEmail("a..@gmail.com");

		// Assert
		assertEquals(false, check);
	}

	@Test
	public void testCheckMailMultipleAt() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		boolean check = view.checkEmail("a@yahoo@gmail.com");

		// Assert
		assertEquals(false, check);
	}

	@Test
	public void testCheckMailHeadOrTailDot() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		boolean check = view.checkEmail(".a.@yahoogmail.com.");

		// Assert
		assertEquals(false, check);
	}

	@Test
	public void testCheckMailCorrectPattern() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		boolean check = view.checkEmail("a@yahoomail.com");

		// Assert
		assertEquals(true, check);
	}

	@Test
	public void testCheckMailIncorrectPattern1() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		boolean check = view.checkEmail("a\"b@yahoomail.com");

		// Assert
		assertEquals(false, check);
	}

	@Test
	public void testCheckMailIncorrectPattern2() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		boolean check = view.checkEmail("a@yahoomail%.com");

		// Assert
		assertEquals(false, check);
	}

	@Test
	public void testCheckProjectNameNull() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		boolean check = view.checkProjectName(null);

		// Assert
		assertEquals(false, check);
	}

	// Test class to test Abstractview
	class TestAbstractView extends AbstractView {

		public TestAbstractView(BundleContext bundlecontext, VelocityContext vc) {
			super(bundlecontext, vc);
		}

	}

}
