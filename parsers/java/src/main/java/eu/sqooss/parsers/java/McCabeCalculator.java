package eu.sqooss.parsers.java;


import java.util.SortedMap;
import java.util.TreeMap;

import eu.sqooss.parsers.java.JavaTreeParser;
import org.antlr.runtime.tree.Tree;

public class McCabeCalculator 
    extends GenericProcessor<SortedMap<String, Integer>> {

    private EntityExtractor entityExtractor;
    private int decisionPoints = 0;
    private int exitPoints = 0;
    private int numMethods = 0;
    /*
     * If the last statement of a method is not a return, we need to add one
     * to the number of exit points.
     */
    private boolean isLastStmtReturn;
    
    public McCabeCalculator(EntityExtractor entityExtractor) {
        super();
        this.entityExtractor = entityExtractor;
        this.results = new TreeMap<String, Integer>();
        IFHandler ifHandler = new IFHandler();
        addToTable(JavaTreeParser.DEFN, new DEFNHandler());
        addToTable(JavaTreeParser.IF, ifHandler);
        addToTable(JavaTreeParser.WHILE, ifHandler);
        addToTable(JavaTreeParser.DO, ifHandler);
        addToTable(JavaTreeParser.RETURN, new RETURNHandler());
        addToTable(JavaTreeParser.CASE, ifHandler);
    }

    private class DEFNHandler extends TokenProcessorSkeleton {

        @Override
        public void goingDown(Tree t) {
            decisionPoints = 0;
            exitPoints = 0;
        }

        @Override
        public void goingUp(Tree t) {
            numMethods++;
            if (isLastStmtReturn != true) {
                exitPoints++;
            }
            results.put(entityExtractor.getCurrentMethodSignature(),
                    new Integer(decisionPoints - exitPoints + 2));
        }
    }

    private class IFHandler extends TokenProcessorSkeleton {

        @Override
        public void goingUp(Tree t) {
            decisionPoints++;
        }
    }

    private class RETURNHandler extends TokenProcessorSkeleton {

        @Override
        public void goingUp(Tree t) {
            isLastStmtReturn = true;
            exitPoints++;
        }
    }
}
