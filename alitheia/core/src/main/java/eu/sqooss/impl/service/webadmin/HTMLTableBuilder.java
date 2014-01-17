package eu.sqooss.impl.service.webadmin;

public class HTMLTableBuilder extends GenericHTMLBuilder<HTMLTableBuilder> {
	static class HTMLTableRowBuilder extends GenericHTMLBuilder<HTMLTableRowBuilder> {
		protected HTMLTableRowBuilder() {
			super("tr");
		}
	}

	static class HTMLTableColumnBuilder extends GenericHTMLBuilder<HTMLTableColumnBuilder> {
		protected HTMLTableColumnBuilder() {
			super("td");
		}
		
		public HTMLTableColumnBuilder withColspan(int i) {
			return this.withAttribute("colspan", Integer.toString(i));
		}
	}
	
	protected HTMLTableBuilder() {
		super("table");
	}
	
	public static HTMLTableBuilder table() {
		return new HTMLTableBuilder();
	}

	public static HTMLTableRowBuilder tableRow() {
		return new HTMLTableRowBuilder();
	}

	public static HTMLTableColumnBuilder tableColumn() {
		return new HTMLTableColumnBuilder();
	}
}
