package eu.sqooss.impl.service.webadmin;

import static eu.sqooss.impl.service.webadmin.HTMLTextBuilder.text;

import java.util.ArrayList;
import java.util.List;

public class GenericHTMLBuilder<CHILD extends GenericHTMLBuilder<CHILD>> {
	private String nodeName;
	private List<String[]> attributes;
	private List<GenericHTMLBuilder<?>> children;
	static final GenericHTMLBuilder<?>[] EMPTY_ARRAY = new GenericHTMLBuilder<?>[0];
	
	protected GenericHTMLBuilder(String nodeName) {
		this.nodeName = nodeName;
		this.attributes = new ArrayList<String[]>();
		this.children = new ArrayList<GenericHTMLBuilder<?>>();
	}

	@SuppressWarnings("unchecked")
	public CHILD with(GenericHTMLBuilder<?> ... children) {
		for (GenericHTMLBuilder<?> child : children) {
			this.children.add(child);
		}
		return (CHILD) this;
	}
	
	@SuppressWarnings("unchecked")
	public CHILD withAttribute(String attribute, String value) {
		if (value != null) {
			attributes.add(new String[]{ attribute, value });
		}
		return (CHILD) this;
	}

	public CHILD withId(String id) {
		return this.withAttribute("id", id);
	}
	
	public CHILD withName(String name) {
		return this.withAttribute("name", name);
	}
	
	public CHILD withClass(String clazz) {
		return this.withAttribute("class", clazz);
	}
	
	public CHILD withStyle(String style) {
		return this.withAttribute("style", style);
	}
	
	@SuppressWarnings("unchecked")
	public CHILD appendContent(String content) {
		return this.with(text(content));
	}
	
	public String build() {
		return this.build(0);
	}
	
	public String build(long in) {
		StringBuilder indent = new StringBuilder();
		for (long i = 0; i < in; i++) {
			indent.append("  ");
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append(indent);
		builder.append("<");
		builder.append(this.nodeName);
		for (String[] attr : this.attributes) {
			builder.append(" ");
			builder.append(attr[0]);
			builder.append("=\"");
			builder.append(attr[1]);
			builder.append("\"");
		}
		
		if (this.children.isEmpty()) {
			builder.append("/>");
		} else if (this.children.size() == 1 && this.children.get(0) instanceof HTMLTextBuilder) { // RENG: temp, very ugly
			builder.append(">");
			builder.append(this.children.get(0).build(in+1));
			builder.append("</");
			builder.append(this.nodeName);
			builder.append(">");
		} else {
			builder.append(">\n");
			for (GenericHTMLBuilder<?> child : this.children) {
				builder.append(child.build(in+1));
				builder.append("\n");
			}
			builder.append(indent);
			builder.append("</");
			builder.append(this.nodeName);
			builder.append(">");
		}
		
		return builder.toString();
	}
}
