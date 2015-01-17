package eu.sqooss.parsers.java;

import java.util.LinkedList;

import org.antlr.runtime.tree.Tree;

public class ASTWalker {

    LinkedList<TokenProcessor> tokenProcessors = 
            new LinkedList<TokenProcessor>();

    public boolean addProcessor(TokenProcessor tokenProcessor) {
        return tokenProcessors.add(tokenProcessor);
    }

    public void walk(Tree tree) {
        for (TokenProcessor tokenProcessor : tokenProcessors) {
            tokenProcessor.goingDown(tree);
        }
        for (int i = 0; i < tree.getChildCount(); i++) {
            walk(tree.getChild(i));
        }
        for (TokenProcessor tokenProcessor : tokenProcessors) {
            tokenProcessor.goingUp(tree);
        }
    }

}
