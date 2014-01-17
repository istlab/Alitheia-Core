package eu.sqooss.impl.service.webadmin;

public class HTMLInputBuilder extends GenericHTMLBuilder<HTMLInputBuilder> {
	public static final String BUTTON = "button";
	
	protected HTMLInputBuilder() {
		super("input");
	}
	
	public static HTMLInputBuilder input() {
		return new HTMLInputBuilder();
	}
	
	public HTMLInputBuilder withType(String type) {
		return this.withAttribute("type", type);
	}
	
	public HTMLInputBuilder withValue(String value) {
		return this.withAttribute("value", value);
	}
	
	public HTMLInputBuilder withDisabled(boolean disabled) {
		if (disabled) {
			return this.withAttribute("disabled", "disabled");
		} else {
			return this;
		}
	}
}
