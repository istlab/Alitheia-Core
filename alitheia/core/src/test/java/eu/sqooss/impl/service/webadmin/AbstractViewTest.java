package eu.sqooss.impl.service.webadmin;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.xpath.HasXPath.hasXPath;

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

	@Test
	public void testCheckProjectNameHead() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		boolean check = view.checkProjectName("_project");

		// Assert
		assertEquals(false, check);
	}

	@Test
	public void testCheckProjectNameFoot() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		boolean check = view.checkProjectName("project_");

		// Assert
		assertEquals(false, check);
	}

	@Test
	public void testCheckNameNull() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		boolean check = view.checkName(null);

		// Assert
		assertEquals(false, check);
	}

	@Test
	public void testCheckNameHead() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		boolean check = view.checkName("_project");

		// Assert
		assertEquals(false, check);
	}

	@Test
	public void testCheckNameFoot() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		boolean check = view.checkName("project_");

		// Assert
		assertEquals(false, check);
	}

	@Test
	public void testCheckProjectNameCorrectName() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		boolean check = view.checkProjectName("project");

		// Assert
		assertEquals(true, check);
	}

	@Test
	public void testFromStringIncorrectString() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		Long check = view.fromString("project");

		// Assert
		assertEquals(null, check);
	}

	@Test
	public void testFromStringCorrectString() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		Long check = view.fromString("400000");

		// Assert
		assertEquals(new Long(400000), check);
	}

	@Test
	public void testNormalFieldSetNullContent() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);

		// Act
		String fieldSet = view.normalFieldset("name", "test", null, 3L);

		// Assert
		assertEquals("", fieldSet);
	}

	@Test
	public void testNormalFieldSetNoContent() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);
		StringBuilder content = new StringBuilder();

		// Act
		String fieldSet = view.normalFieldset("name", "test", content, 3L);

		// Assert
		assertEquals("", fieldSet);
	}

	@Test
	public void testNormalFieldSetNullCSS() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);
		StringBuilder content = new StringBuilder();
		content.append(" ");
		String name = "name";

		// Act
		String fieldSet = view.normalFieldset(name, null, content, 3L);

		// Assert
		String html = HTMLTestUtils.sanitizeHTML(fieldSet);
		// Check that the legend inner HTML is the string set
		assertThat(the(html), hasXPath("/root/fieldset/legend", equalTo(name)));
	}

	@Test
	public void testNormalFieldSetNullName() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);
		StringBuilder content = new StringBuilder();
		content.append(" ");
		String name = null;
		String css = "test";

		// Act
		String fieldSet = view.normalFieldset(name, css, content, 3L);

		// Assert
		String html = HTMLTestUtils.sanitizeHTML(fieldSet);
		// Check that the legend inner HTML is the string set
		assertThat(the(html),
				hasXPath("/root/fieldset/legend", equalTo("NONAME")));
	}

	@Test
	public void testNormalInfoRowSetStrings() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);
		StringBuilder content = new StringBuilder();
		content.append(" ");
		String title = "title";
		String value = "value";

		// Act
		String fieldSet = view.normalInfoRow(title, value, 2L);

		// Assert
		String html = HTMLTestUtils.sanitizeHTML(fieldSet);
		// Check that the number of td is 2
		assertThat(the(html), hasXPath("count(/root/tr/td)", equalTo("2")));
		// Check that the number of tr is 1
		assertThat(the(html), hasXPath("count(/root/tr)", equalTo("1")));
		// Check that td 1 is the same as the set string
		assertThat(the(html), hasXPath("/root/tr/td[1]/b", equalTo(title)));
		// Check that td 2 is the same as the set string
		assertThat(the(html),
				hasXPath("/root/tr/td[2]", equalToIgnoringWhiteSpace(value)));
	}

	@Test
	public void testNormalInfoRowNullTitle() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);
		StringBuilder content = new StringBuilder();
		content.append(" ");
		String title = null;
		String value = "value";

		// Act
		String fieldSet = view.normalInfoRow(title, value, 2L);

		// Assert
		String html = HTMLTestUtils.sanitizeHTML(fieldSet);
		// Check that the number of td is 2
		assertThat(the(html), hasXPath("count(/root/tr/td)", equalTo("2")));
		// Check that the number of tr is 1
		assertThat(the(html), hasXPath("count(/root/tr)", equalTo("1")));
		// Check that td 1 is the same as the set string
		assertThat(the(html), hasXPath("/root/tr/td[1]/b", equalTo("")));
		// Check that td 2 is the same as the set string
		assertThat(the(html),
				hasXPath("/root/tr/td[2]", equalToIgnoringWhiteSpace(value)));
	}

	@Test
	public void testNormalInfoRowNullValue() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);
		StringBuilder content = new StringBuilder();
		content.append(" ");
		String title = "title";
		String value = null;

		// Act
		String fieldSet = view.normalInfoRow(title, value, 2L);

		// Assert
		String html = HTMLTestUtils.sanitizeHTML(fieldSet);
		// Check that the number of td is 2
		assertThat(the(html), hasXPath("count(/root/tr/td)", equalTo("2")));
		// Check that the number of tr is 1
		assertThat(the(html), hasXPath("count(/root/tr)", equalTo("1")));
		// Check that td 1 is the same as the set string
		assertThat(the(html), hasXPath("/root/tr/td[1]/b", equalTo("title")));
		// Check that td 2 is the same as the set string
		assertThat(the(html),
				hasXPath("/root/tr/td[2]", equalToIgnoringWhiteSpace("")));
	}

	@Test
	public void testNormalInputRowSetStrings() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);
		StringBuilder content = new StringBuilder();
		content.append(" ");
		String title = "title";
		String value = "value";
		String parvalue = "parvalue";

		// Act
		String fieldSet = view.normalInputRow(title, value, parvalue, 2L);

		// Assert
		String html = HTMLTestUtils.sanitizeHTML(fieldSet);
		// Check that the number of td is 2
		assertThat(the(html), hasXPath("count(/root/tr/td)", equalTo("2")));
		// Check that the number of tr is 1
		assertThat(the(html), hasXPath("count(/root/tr)", equalTo("1")));
		// Check that td 1 is the same as the set string
		assertThat(the(html), hasXPath("/root/tr/td[1]/b", equalTo(title)));
		// Check that id is the same as the set string
		assertThat(the(html),
				hasXPath("/root/tr/td[2]/input/@id", equalTo(value)));
		// Check that name is the same as the set string
		assertThat(the(html),
				hasXPath("/root/tr/td[2]/input/@name", equalTo(value)));
		// Check that value is the same as the set string
		assertThat(the(html),
				hasXPath("/root/tr/td[2]/input/@value", equalTo(parvalue)));
	}

	@Test
	public void testNormalInputRowNullTitle() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);
		StringBuilder content = new StringBuilder();
		content.append(" ");
		String title = null;
		String value = "value";
		String parvalue = "parvalue";

		// Act
		String fieldSet = view.normalInputRow(title, value, parvalue, 2L);

		// Assert
		String html = HTMLTestUtils.sanitizeHTML(fieldSet);
		// Check that the number of td is 2
		assertThat(the(html), hasXPath("count(/root/tr/td)", equalTo("2")));
		// Check that the number of tr is 1
		assertThat(the(html), hasXPath("count(/root/tr)", equalTo("1")));
		// Check that td 1 is the same as the set string
		assertThat(the(html), hasXPath("/root/tr/td[1]/b", equalTo("")));
		// Check that id is the same as the set string
		assertThat(the(html),
				hasXPath("/root/tr/td[2]/input/@id", equalTo(value)));
		// Check that name is the same as the set string
		assertThat(the(html),
				hasXPath("/root/tr/td[2]/input/@name", equalTo(value)));
		// Check that value is the same as the set string
		assertThat(the(html),
				hasXPath("/root/tr/td[2]/input/@value", equalTo(parvalue)));
	}

	@Test
	public void testNormalInputRowNullParValue() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);
		StringBuilder content = new StringBuilder();
		content.append(" ");
		String title = "title";
		String value = "value";
		String parvalue = null;

		// Act
		String fieldSet = view.normalInputRow(title, value, parvalue, 2L);

		// Assert
		String html = HTMLTestUtils.sanitizeHTML(fieldSet);
		// Check that the number of td is 2
		assertThat(the(html), hasXPath("count(/root/tr/td)", equalTo("2")));
		// Check that the number of tr is 1
		assertThat(the(html), hasXPath("count(/root/tr)", equalTo("1")));
		// Check that td 1 is the same as the set string
		assertThat(the(html), hasXPath("/root/tr/td[1]/b", equalTo(title)));
		// Check that id is the same as the set string
		assertThat(the(html),
				hasXPath("/root/tr/td[2]/input/@id", equalTo(value)));
		// Check that name is the same as the set string
		assertThat(the(html),
				hasXPath("/root/tr/td[2]/input/@name", equalTo(value)));
		// Check that value is the same as the set string
		assertThat(the(html),
				hasXPath("/root/tr/td[2]/input/@value", equalTo("")));
	}

	@Test
	public void testNormalInputRowNullParName() {
		// Arrange
		TestAbstractView view = new TestAbstractView(null, null);
		StringBuilder content = new StringBuilder();
		content.append(" ");
		String title = "title";
		String parname = null;
		String parvalue = "parvalue";

		// Act
		String fieldSet = view.normalInputRow(title, parname, parvalue, 2L);

		// Assert
		assertEquals("\n", fieldSet);

	}

	// Test class to test Abstractview
	class TestAbstractView extends AbstractView {

		public TestAbstractView(BundleContext bundlecontext, VelocityContext vc) {
			super(bundlecontext, vc);
		}

	}

}
