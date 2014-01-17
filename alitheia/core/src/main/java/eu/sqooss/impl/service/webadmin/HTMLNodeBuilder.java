package eu.sqooss.impl.service.webadmin;

public class HTMLNodeBuilder extends GenericHTMLBuilder<HTMLNodeBuilder> {
	protected HTMLNodeBuilder(String nodeName) {
		super(nodeName);
	}
	
	public static HTMLNodeBuilder node(String nodeName) {
		return new HTMLNodeBuilder(nodeName);
	}
}
