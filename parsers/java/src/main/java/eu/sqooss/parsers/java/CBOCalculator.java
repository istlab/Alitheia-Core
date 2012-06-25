package eu.sqooss.parsers.java;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import eu.sqooss.parsers.java.JavaTreeParser;
import org.antlr.runtime.tree.Tree;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * A calculator for the Coupling Between Objects (CBO) metric.
 * 
 * The calculator is run over a compilation unit, and it will count
 * the metric for all classes inside the compilation unit. The results
 * are gathered into a Map<String, Integer>, mapping the class name to the
 * BOM value.
 */
public class CBOCalculator extends GenericProcessor<Map<String, Integer>> {

    //final static Logger logger = LoggerFactory.getLogger(CBOCalculator.class);
    
    /** 
     * Collects types used by class. Key is the class name, value is
     * the set of all parameter types in the class.
     */
    private Map<String, Set<String>> typeSetPerClass;
    
    private EntityExtractor entityExtractor;
    
    private InheritanceExtractor inheritanceExtractor;
    
    private static String[] primitiveTypesArray = {
            "boolean",
             "char",
             "byte",
             "short",
             "int",
             "long",
             "float",
             "double"
    };
    
    private static Set<String> primitiveTypes = 
        new TreeSet<String>(Arrays.asList(primitiveTypesArray));
    
    public CBOCalculator(InheritanceExtractor inheritanceExtractor) {
        super();
        this.entityExtractor = inheritanceExtractor.getEntityExtractor();
        this.inheritanceExtractor = inheritanceExtractor;
        this.results = new TreeMap<String, Integer>();
        this.typeSetPerClass = new HashMap<String, Set<String>>();
        addToTable(JavaTreeParser.CU, new CUHandler());
        CLASSHandler classHandler = new CLASSHandler();
        addToTable(JavaTreeParser.CLASS, classHandler);
        addToTable(JavaTreeParser.INTERFACE, classHandler);
        addToTable(JavaTreeParser.PARAM, new PARAMHandler());
        addToTable(JavaTreeParser.DEFVAR, new DEFVARHandler());
        addToTable(JavaTreeParser.THROWS, new THROWSHandler());
    }
    
    private boolean addType(String typeName) {
        
        boolean result = false;
        
        if (primitiveTypes.contains(typeName)) {
            return result;
        }

        String enclosingTypeName = 
            entityExtractor.getCurrentFullyQualifiedTypeName();
        Set<String> typeSet = typeSetPerClass.get(enclosingTypeName);
        result = typeSet.add(typeName);
        //logger.debug("Considering {} for addition: {} ", typeName, result);
        return result;
    }
    
    protected class CUHandler extends TokenProcessorSkeleton {
        
        @Override 
        public void goingUp(Tree t) {
            for (String className : typeSetPerClass.keySet()) {
                results.put(className, typeSetPerClass.get(className).size());
            }
        }
    }
    protected class CLASSHandler extends TokenProcessorSkeleton {
        
        @Override
        public void goingDown(Tree t) {
            String className = 
                entityExtractor.getCurrentFullyQualifiedTypeName();
            typeSetPerClass.put(className, new TreeSet<String>());
            
        }
        
        @Override
        public void goingUp(Tree t) {
            
            Deque<InheritanceExtractor.ClassInheritance> inhResults =
                inheritanceExtractor.getResults();
            for (InheritanceExtractor.ClassInheritance inh : inhResults) {
                addType(inh.superClass);
                for (String typeName : inh.interfaces) {
                    addType(typeName);
                }
            }
        }
    }
    
    protected class PARAMHandler extends TokenProcessorSkeleton {

        @Override
        public void goingDown(Tree t) {
            
            String paramType = t.getChild(0).getText();
            addType(paramType);
        }
    }
    
    protected class DEFVARHandler extends TokenProcessorSkeleton {
        
        @Override
        public void goingDown(Tree t) {
            
            String variableType = t.getChild(0).getText();
            addType(variableType);
        }
    }
    
    protected class THROWSHandler extends TokenProcessorSkeleton {
        
        @Override
        public void goingDown(Tree t) {
            
            for (int i = 0; i < t.getChildCount(); i++) {
                String typeName = t.getChild(i).getText();
                addType(typeName);
            }
        }
    }
    
    protected class RETURNSHandler extends TokenProcessorSkeleton {
        
        @Override
        public void goingDown(Tree t) {
            
            String typeName = t.getChild(0).getText();
            if (!typeName.equals("void")) {
                addType(typeName);
            }
        }
    }
}
