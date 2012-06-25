package eu.sqooss.parsers.java;

import org.antlr.runtime.tree.Tree;

public class TokenProcessorSkeleton implements TokenProcessor {
    
    protected TokenProcessorSkeleton() {
    }
    
    @Override
    public void goingDown(Tree t) {
        return;
    }

    @Override
    public void goingUp(Tree t) {
        return;
    }

}
