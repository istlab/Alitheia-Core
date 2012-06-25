package eu.sqooss.parsers.java;

import java.io.Serializable;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;

public class SpanningNodeAdaptor extends CommonTreeAdaptor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public Object create(Token payLoad) {
        return new SpanningNode(payLoad);
    }
    
    public Object create(Token payLoad, int startLine, int endLine) {
        return new SpanningNode(payLoad, startLine, endLine);
    }

}
