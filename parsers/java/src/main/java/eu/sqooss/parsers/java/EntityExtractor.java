package eu.sqooss.parsers.java;

import java.util.*;

import eu.sqooss.parsers.java.JavaTreeParser;
import org.antlr.runtime.tree.Tree;

/**
 * A class collecting class entities. The entities are 
 * collected in a map containing the CodeFragment objects constituting 
 * the classes in the current compilation unit.
 * 
 * The map has the fully qualified class name as key values, and a 
 * Deque of CodeFragment objects (i.e., Deque<CodeFragment>)
 * as values. Hence, for each class in the compilation unit we get a list
 * of code fragments. A fully qualified class name is a class name
 * prefixed by its package and all enclosing classes. The constituent
 * parts of the fully qualified class name are separated by periods.
 * 
 */
public class EntityExtractor 
    extends GenericProcessor<Map<String,  Deque<CodeFragment>>> {

    private String methodName;
    private String methodSignature;
    private String packageName;
    private String fullyQualifiedCurrentTypeName;
    private Set<String> imports = new HashSet<String>();
    private LinkedList<Tree> params = new LinkedList<Tree>();
    private LinkedList<String> enclosingTypes = new LinkedList<String>();
      
    public EntityExtractor() {
        super();
        this.results = new LinkedHashMap<String, Deque<CodeFragment>>();
        addToTable(JavaTreeParser.PACKAGE, new PACKAGEHandler());
        addToTable(JavaTreeParser.IMPORT, new IMPORTHandler());
        CLASSHandler classHandler = new CLASSHandler();
        addToTable(JavaTreeParser.CLASS, classHandler);
        addToTable(JavaTreeParser.INTERFACE, classHandler);
        addToTable(JavaTreeParser.DEFN, new DEFNHandler());
        addToTable(JavaTreeParser.PARAMS, new PARAMSHandler());
        addToTable(JavaTreeParser.PARAM, new PARAMHandler());
    }
    
    /**
     * Returns the name of the method we are currently visiting.
     * 
     * @return the name of the method we are currently visiting.
     */
    public String getCurrentMethodName() {
        return this.methodName;
    }
    
    /**
     * Sets the name of the method we are currently visiting. It also sets
     * the current method signature.
     * The method signature consists of the method name prefixed by the
     * fully qualified type name, and followed by the method parameters. 
     * The fully qualified type name is defined
     * as in the getClassesContents() method. The method name is separated
     * by the class name by "::".
     * 
     * @param methodName the name of the method we are currently visiting.
     */
    private void setCurrentMethodName(String methodName) {
        this.methodName = methodName;
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(getFullyQualifiedCurrentTypeName());
        signatureBuilder.append("::");
        signatureBuilder.append(methodName);
        signatureBuilder.append("[ ");
        for (Tree param : params) {
            signatureBuilder.append(param.toStringTree());
            signatureBuilder.append(" ");
        }
        signatureBuilder.append("]");
        String signature = signatureBuilder.toString();
        setCurrentMethodSignature(signature);
    }
    
    /**
     * Returns the method signature of the method we are currently visiting.
     * The method signature consists of the method name prefixed by the
     * fully qualified type name, and followed by the method parameters. 
     * The fully qualified type name is defined
     * as in the getClassesContents() method. The method name is separated
     * by the class name by "::".
     *  
     * @return the method signature of the method we are currently visiting
     */
    public String getCurrentMethodSignature() {
	return this.methodSignature;
    }
    
    /**
     * Returns the fully qualified name of the class or interface
     * we are currently visiting. The fully qualified type name is the type name 
     * preceded by any enclosing types and the package where it resides; 
     * name parts are separated by periods.
     * 
     * @return the fully qualified name of the type we are currently
     * visiting
     */
    public String getCurrentFullyQualifiedTypeName() {
        return this.fullyQualifiedCurrentTypeName;
    }
    
    private void setCurrentMethodSignature(String signature) {
        this.methodSignature = signature;
    }
    
    /**
     * Returns the package name of the current compilation unit.
     * 
     * @return the package name of the current compilation unit
     */
    public String getPackageName() {
        return this.packageName;
    }
    
    private void addEnclosingType(String typeName) {
        enclosingTypes.addLast(typeName);
    }
    
    private void initTypeContents(String fullyQualifiedTypeName) {
        this.results.put(fullyQualifiedTypeName,
                new LinkedList<CodeFragment>());
    }
    
    private void addToTypesContents(CodeFragment codeFragment) {
        Deque<CodeFragment> typeContents =
            this.results.get(this.fullyQualifiedCurrentTypeName);
        if (typeContents != null) {
            typeContents.addLast(codeFragment);
        }
    }
    
    private String getFullyQualifiedCurrentTypeName() {
        return this.fullyQualifiedCurrentTypeName;
    }
    
    public String createFullyQualifiedTypeName(String typeName) {
        if (typeName.contains(".")) {
            return typeName;
        }
        for (String importString : imports) {
            if (importString.endsWith(typeName)) {
                return importString;
            }
        }
        return this.packageName + "." + typeName;
    }
    
    private String createFullyQualifiedCurrentTypeName() {
        StringBuilder fullyQualifiedName = new StringBuilder();
        fullyQualifiedName.append(getPackageName()).append(".");
        Iterator<String> li = enclosingTypes.iterator();
        while (li.hasNext()) {
            fullyQualifiedName.append(li.next());
            if (li.hasNext()) {
                fullyQualifiedName.append(".");
            }
        }
        this.fullyQualifiedCurrentTypeName = fullyQualifiedName.toString();
        
        return this.fullyQualifiedCurrentTypeName;
    }
    
    protected class PACKAGEHandler extends TokenProcessorSkeleton {
        
        @Override
        public void goingDown(Tree t) {
            String packageName = t.getChild(0).getText();
            EntityExtractor.this.packageName = packageName;
            imports.add(packageName);
        }
    }
    
    protected class IMPORTHandler extends TokenProcessorSkeleton {
        
        @Override
        public void goingDown(Tree t) {
            imports.add(t.getChild(0).getText());
        }
    }
    
    protected class CLASSHandler extends TokenProcessorSkeleton {

        @Override
        public void goingDown(Tree t) {
            String className = t.getChild(0).getText();
            addEnclosingType(className);
            String fullyQualifiedClassName = 
                createFullyQualifiedCurrentTypeName();
            initTypeContents(fullyQualifiedClassName);
            SpanningNode codeNode = (SpanningNode) t;
            int startLine = codeNode.getStartLine();
            int endLine = codeNode.getEndLine();
            CodeFragment codeFragment = 
                new CodeFragment(fullyQualifiedClassName, startLine, 
                        endLine);
            addToTypesContents(codeFragment);
        }

        @Override
        public void goingUp(Tree t) {
            enclosingTypes.pop();
        }
    }

    protected class DEFNHandler extends TokenProcessorSkeleton {

        @Override
        public void goingDown(Tree t) {
            setCurrentMethodName(t.getChild(0).getText());
        }
        
        @Override
        public void goingUp(Tree t) {
            SpanningNode codeNode = (SpanningNode) t;
            int startLine = codeNode.getStartLine();
            int endLine = codeNode.getEndLine();
            String signature = getCurrentMethodSignature();
            CodeFragment codeFragment = new CodeFragment(signature, startLine, 
                    endLine);
            addToTypesContents(codeFragment);
        }
    }
    
    protected class PARAMSHandler extends TokenProcessorSkeleton {
	
        @Override
        public void goingDown(Tree t) {
            params.clear();
        }
        
        @Override
        public void goingUp(Tree t) {
            String methodName = getCurrentMethodName();
            StringBuilder signatureBuilder = new StringBuilder();
            signatureBuilder.append(getFullyQualifiedCurrentTypeName());
            signatureBuilder.append("::");
            signatureBuilder.append(methodName);
            signatureBuilder.append("[ ");
            for (Tree param : params) {
                signatureBuilder.append(param.toStringTree());
                signatureBuilder.append(" ");
            }
            signatureBuilder.append("]");
            String signature = signatureBuilder.toString();
            setCurrentMethodSignature(signature);
        }
    }

    protected class PARAMHandler extends TokenProcessorSkeleton {

        @Override
        public void goingDown(Tree t) {
            params.addLast(t.getChild(0));
        }
    }

}
