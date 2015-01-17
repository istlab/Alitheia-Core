package eu.sqooss.parsers.java;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import eu.sqooss.parsers.java.JavaTreeParser;
import org.antlr.runtime.tree.Tree;

/**
 * A calculator for the Lack of Cohesion in Methods (LCOM) metric.
 * 
 * The calculator is run over a compilation unit, and it will count
 * the metric for all classes inside the compilation unit. The results
 * are gathered into a Map<String, Integer>, mapping the class name to the
 * LCOM value.
 */
public class LCOMCalculator 
    extends GenericProcessor<Map<String, Integer>>{
 
    /**
     * Class representing a block, where a block contains variables defined
     * in it and a reference to its parent block (if any).
     */
    private static class Block {
        private Block parent;
        private Set<String> variables;
        
        public Block() {
            this.variables = new HashSet<String>();
        }
        
        public boolean addVariable(String variable) {
            return this.variables.add(variable);
        }
        
        public boolean containsVariable(String variable) {
            return this.variables.contains(variable);
        }
        
        public void setParent(Block parent) {
            this.parent = parent;
        }
        
        public Block getParent() {
            return this.parent;
        }
    }
    
    /**
     * Class (actually just a structure) representing a variable use; 
     * a variable use is a name with a scope (block) where it is defined.
     */
    private static class VariableUse implements Comparable<VariableUse> {
        public String name;
        public Block block;
        
        public VariableUse(String name, Block block) {
            this.name = name;
            this.block = block;
        }

        @Override
        public int compareTo(VariableUse o) {
            if (this.block != o.block) {
                return -1;
            }
            return this.name.compareTo(o.name);
        }
    }
    
    private EntityExtractor entityExtractor;
    private Block currentBlock;
    /** 
     * The variables used by methods in the compilation unit.
     * The outer Map maps class names to the inner map; the inner
     * map maps method names to the set of variables they 
     * access.
     */
    private Map<String, Map<String, Set<VariableUse>>> variablesUsedPerClass;
    
    /**
     * True if inside a method, false otherwise.
     */
    private boolean inMethod;
    
    public LCOMCalculator(EntityExtractor entityExtractor) {
        super();
        this.entityExtractor = entityExtractor;
        this.results = new TreeMap<String, Integer>();
        this.variablesUsedPerClass = 
            new LinkedHashMap<String, Map<String, Set<VariableUse>>>();
        addToTable(JavaTreeParser.CU, new CUHandler());
        addToTable(JavaTreeParser.CLASS, new CLASSHandler());
        addToTable(JavaTreeParser.INTERFACE, new CLASSHandler());
        addToTable(JavaTreeParser.BLOCK, new BLOCKHandler());
        addToTable(JavaTreeParser.DEFN, new DEFNHandler());
        addToTable(JavaTreeParser.VARNAME, new VARNAMEHandler());
        addToTable(JavaTreeParser.PRIMARY, new PRIMARYHandler());
    }
    
    private Block lookUp(String variable) {
        Block definitionBlock = currentBlock;
        while (definitionBlock != null) {
            if (definitionBlock.containsVariable(variable)) {
                return definitionBlock;
            }
            definitionBlock = definitionBlock.getParent();
        }
        return definitionBlock;
    }

    private void newBlock() {
        Block newBlock = new Block();
        newBlock.setParent(currentBlock);
        currentBlock = newBlock;
    }
    
    private void calculateLCOM(String className) {
        
        int common = 0;
        int disjoint = 0;
        
        Map<String, Set<VariableUse>> perMethodSet = 
            variablesUsedPerClass.get(className);
        for (String method : perMethodSet.keySet()) {
            for (String anotherMethod : perMethodSet.keySet()) {
                if (!method.equals(anotherMethod)) {
                    Set<VariableUse> methodInstanceVariables = 
                        perMethodSet.get(method);
                    Set<VariableUse> anotherMethodInstanceVariables =
                        perMethodSet.get(anotherMethod);
                    TreeSet<VariableUse> intersection = 
                        new TreeSet<VariableUse>(methodInstanceVariables);
                    intersection.retainAll(anotherMethodInstanceVariables);
                    if (intersection.isEmpty()) {
                        System.out.println("Disjoint for " + method + " " + anotherMethod + "=" + disjoint);
                        disjoint++;
                    } else {
                        common++;
                    }
                    int result = 0;
                    if (disjoint > common) {
                        result = disjoint - common;
                    }
                    results.put("ClassName", new Integer(result));
                }
            }
        }
    }
    
    protected class CUHandler extends TokenProcessorSkeleton {
        
        @Override 
        public void goingUp(Tree t) {
            for (String className : variablesUsedPerClass.keySet()) {
                calculateLCOM(className);
            }
        }
    }
    
    protected class CLASSHandler extends TokenProcessorSkeleton {
        
        @Override
        public void goingDown(Tree t) {
            inMethod = false;
            newBlock();
            String className = 
                entityExtractor.getCurrentFullyQualifiedTypeName();
            variablesUsedPerClass.put(className, 
                    new LinkedHashMap<String, Set<VariableUse>>());
            
        }
    }
    
    protected class BLOCKHandler extends TokenProcessorSkeleton {
        
        @Override
        public void goingDown(Tree t) {
            newBlock();
        }
        
        @Override
        public void goingUp(Tree t) {
            currentBlock = currentBlock.getParent();
        }
    }
    
    private class DEFNHandler extends TokenProcessorSkeleton {

        @Override
        public void goingDown(Tree t) {
            inMethod = true;
            newBlock();
        }
        
        @Override
        public void goingUp(Tree t) {
            inMethod = false;
            currentBlock = currentBlock.getParent();
        }
    }
        
    protected class VARNAMEHandler extends TokenProcessorSkeleton {
        
        @Override
        public void goingDown(Tree t) {
            String variable = t.getChild(0).getText();
            currentBlock.addVariable(variable);
        }
    }
    
    protected class PRIMARYHandler extends TokenProcessorSkeleton {
        
        @Override
        public void goingDown(Tree t) {
            if (!inMethod) {
                return;
            }
            
            String primary = t.getText();
            String[] parts = primary.split("\\.", 2);
            String name = parts[0];
            /* Discard references or calls to super */
            if (name.equals("super")) {
                return;
            }
            /* Get instance variable prefixed with this */
            if (name.equals("this") && parts.length > 1) {
                name = parts[1];
            }
            /* Discard method calls */
            if (name.contains("(")) {
                return;
            }
            System.out.println("***LOOKING UP " + name + " from " + primary);
            Block definitionBlock = lookUp(name);
            if (definitionBlock == null) {
                return;
            }
            String currentClassName = 
                entityExtractor.getCurrentFullyQualifiedTypeName();
            Map<String, Set<VariableUse>> variablesUsedPerMethod = 
                variablesUsedPerClass.get(currentClassName);
            String methodSignature =
                entityExtractor.getCurrentMethodSignature(); 
            VariableUse variableUse = 
                new VariableUse(name, definitionBlock);
            System.out.println("*** ADDING " + variableUse.name + " in " + methodSignature);
            Set<VariableUse> variableUses = 
                variablesUsedPerMethod.get(methodSignature);
            if (variableUses == null) {
                variableUses = new HashSet<VariableUse>();
                variablesUsedPerMethod.put(methodSignature, variableUses);
            }
            variableUses.add(variableUse);
        }
    }
}
