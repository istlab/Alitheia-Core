package eu.sqooss.impl.service.webadmin;

public class HTMLFormBuilder extends GenericHTMLBuilder<HTMLFormBuilder> {
	public static final String POST = "post";

	protected HTMLFormBuilder() {
		super("form");
	}

	public static HTMLFormBuilder form() {
		return new HTMLFormBuilder();
	}
	
	public HTMLFormBuilder withMethod(String method) {
		return this.withAttribute("method", method);
	}
	
	public HTMLFormBuilder withAction(String action) {
		return this.withAttribute("action", action);
	}
}
