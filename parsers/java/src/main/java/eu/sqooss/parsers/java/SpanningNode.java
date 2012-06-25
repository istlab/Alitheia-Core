package eu.sqooss.parsers.java;

import java.io.Serializable;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

public class SpanningNode extends CommonTree implements Serializable {

    private static final long serialVersionUID = 1L;
    int startLine;
    int endLine;
    
    public SpanningNode(Token t) {
        super(t);
        if (t != null) {
            this.startLine = t.getLine();
            this.endLine = this.startLine;
        }
    }

    public SpanningNode(Token t, int startLine, int endLine) {
        super(t);
        this.startLine = startLine;
        this.endLine = endLine;
    }
    
    public void setSpan(int startLine, int endLine) {
        this.startLine = startLine;
        this.endLine = endLine;
    }
    
    public int getStartLine() {
        return this.startLine;
    }
    
    public int getEndLine() {
        return this.endLine;
    }
}
