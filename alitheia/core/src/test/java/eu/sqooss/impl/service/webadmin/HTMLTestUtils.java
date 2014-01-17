package eu.sqooss.impl.service.webadmin;

public class HTMLTestUtils {
	public static final String INPUT_REGEX = "<input([\\(\\) a-zA-Z=\\-_\\\"\\\':;0-9\\.\\?\\/\\\\]*[\\(\\) a-zA-Z=\\-_\\\"\\\':;0-9\\.\\?\\\\]+)>";

	public static String sanitizeHTML(String string) {
		String html = "<root>" + string + "</root>";		
		html = html.replaceAll("checkbox\"checked", "checkbox\" checked='true'");
		html = html.replaceAll("&nbsp;", " ");
		html = html.replaceAll("disabled(\\s*[^=])", "disabled='true'$1");
		return html;
	}
}
