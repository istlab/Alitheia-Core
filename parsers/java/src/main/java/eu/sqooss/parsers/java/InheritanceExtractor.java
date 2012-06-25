package eu.sqooss.parsers.java;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import eu.sqooss.parsers.java.JavaTreeParser;
import org.antlr.runtime.tree.Tree;

/**
 * A class collecting information on class inheritance. The information
 * comprises the superclass of each class and the interfaces the class 
 * implements. The information is collected in a queue of 
 * InheritanceExtractor.ClassInheritance objects (one object per class).
 * 
 */
public class InheritanceExtractor 
    extends GenericProcessor<Deque<InheritanceExtractor.ClassInheritance>> {

    private EntityExtractor entityExtractor;
    private ClassInheritance classInheritance;
    private Map<String, String> inheritanceStructure = 
        new HashMap<String, String>();
    
    /**
     * A class containing information on a (single) class inheritance. The 
     * class is just a collection of data for the class name, the class 
     * superclass, and the interfaces it implements.
     * 
     */
    public static class ClassInheritance {
        public String className;
        public String superClass;
        public Deque<String> interfaces = new LinkedList<String>();
    }
    
    public InheritanceExtractor(EntityExtractor entityExtractor) {
        super();
        this.entityExtractor = entityExtractor;
        this.results = new LinkedList<InheritanceExtractor.ClassInheritance>();
        addToTable(JavaTreeParser.EXTENDS, new EXTENDSHandler());
        addToTable(JavaTreeParser.IMPLEMENTS, new IMPLEMENTSHandler());
        addToTable(JavaTreeParser.CLASS, new CLASSHandler());        
    }
    
    public EntityExtractor getEntityExtractor() {
        return this.entityExtractor;
    }
    
    public Map<String, String> getInheritanceStructure() {
        return inheritanceStructure;
    }
    
    private class CLASSHandler extends TokenProcessorSkeleton {
        
        @Override
        public void goingDown(Tree t) {
            classInheritance = new ClassInheritance();
            results.addLast(classInheritance);
            classInheritance.superClass = Object.class.getCanonicalName();
            classInheritance.className = 
                entityExtractor.getCurrentFullyQualifiedTypeName();
        }
        
    }
    
    private class EXTENDSHandler extends TokenProcessorSkeleton {
        
        @Override
        public void goingDown(Tree t) {
            String superClass = t.getChild(0).getText();
            superClass = 
                entityExtractor.createFullyQualifiedTypeName(superClass);
            classInheritance.superClass = superClass;
        }
    }
    
    private class IMPLEMENTSHandler extends TokenProcessorSkeleton {
        
        @Override
        public void goingDown(Tree t) {
            for (int i = 0; i < t.getChildCount(); i++) {
                classInheritance.interfaces.addLast(t.getChild(i).getText());
            }
        }
    }
    
}
