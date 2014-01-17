package eu.sqooss.impl.service.webadmin;

class HTMLTextBuilder extends GenericHTMLBuilder<HTMLTextBuilder> {
	private String text;
	
	protected HTMLTextBuilder(String text) {
		super(null);
		
		this.text = text;
	}
	
	public static HTMLTextBuilder text(String text) {
		return new HTMLTextBuilder(text);
	}

	@Override
	public String build(long indentation) {
		return this.text;
	}
}