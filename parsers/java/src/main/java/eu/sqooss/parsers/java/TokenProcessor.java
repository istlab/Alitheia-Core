package eu.sqooss.parsers.java;

import org.antlr.runtime.tree.Tree;


public interface TokenProcessor {
    void goingDown(Tree t);
    void goingUp(Tree t);
}
