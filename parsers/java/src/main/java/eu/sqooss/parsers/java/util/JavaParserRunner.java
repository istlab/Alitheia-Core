package eu.sqooss.parsers.java.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

import eu.sqooss.parsers.java.ASTWalker;
import eu.sqooss.parsers.java.CBOCalculator;
import eu.sqooss.parsers.java.CodeFragment;
import eu.sqooss.parsers.java.EntityExtractor;
import eu.sqooss.parsers.java.InheritanceExtractor;
import eu.sqooss.parsers.java.JavaTreeLexer;
import eu.sqooss.parsers.java.JavaTreeParser;
import eu.sqooss.parsers.java.LCOMCalculator;
import eu.sqooss.parsers.java.McCabeCalculator;
import eu.sqooss.parsers.java.SpanningNodeAdaptor;

public class JavaParserRunner {

    private Tree tree;
    private EntityExtractor entityExtractor;
    private InheritanceExtractor inheritanceExtractor;

    public String testString(String bla, String blue, String[] rest,
            String[][] restOfRest) {
        return bla;
    }

    public void runParser(String fileName) throws IOException, RecognitionException {

        InputStream in = new FileInputStream(fileName);
        runParser(in);
        System.out.println(tree.toStringTree());
        in.close();
    }
    
    public void runParser(InputStream in) throws RecognitionException, 
        IOException {
        
        ANTLRInputStream input = new ANTLRInputStream(in);
        // Create a lexer that feeds from that stream
        JavaTreeLexer lexer = new JavaTreeLexer(input);
        // Create a stream of tokens fed by the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Create a parser that feeds off the token stream
        JavaTreeParser parser = new JavaTreeParser(tokens);
        SpanningNodeAdaptor adaptor = new SpanningNodeAdaptor();
        parser.setTreeAdaptor(adaptor);
        // Begin parsing
        JavaTreeParser.compilationUnit_return result = parser.compilationUnit();
        tree = (Tree) result.getTree();    
    }
    
    public void runEntityExtractor() {

        ASTWalker walker = new ASTWalker();
        entityExtractor = new EntityExtractor();
        walker.addProcessor(entityExtractor);
        walker.walk(tree);
    }
    
    public void runInheritanceExtractor() {
        ASTWalker walker = new ASTWalker();
        inheritanceExtractor = new InheritanceExtractor(entityExtractor);
        walker.addProcessor(inheritanceExtractor);
        walker.walk(tree);
        Deque<InheritanceExtractor.ClassInheritance> classInheritance = 
            inheritanceExtractor.getResults();
        for (InheritanceExtractor.ClassInheritance ci : classInheritance) {
            System.out.println(ci.className + " EXTENDS " + ci.superClass 
                    + " IMPLEMENTS " + ci.interfaces);
        }
    }

    public void runMcCabeCalculator() {
        ASTWalker walker = new ASTWalker();
        McCabeCalculator mcCabeCalculator =
            new McCabeCalculator(entityExtractor);
        walker.addProcessor(mcCabeCalculator);
        walker.walk(tree);
        
        Map<String, Deque<CodeFragment>> classCodeFragments =
            entityExtractor.getResults();
        Map<String, Integer> measurements = mcCabeCalculator.getResults();
        for (Map.Entry<String, Deque<CodeFragment>> codeFragments : 
            classCodeFragments.entrySet()) {
            for (CodeFragment codeFragment : codeFragments.getValue()) {
                String fullyQualifiedName = 
                    codeFragment.getFullyQualifiedName();
                Integer measurement = measurements.get(fullyQualifiedName);
                if (measurement != null) {
                    System.out.println(fullyQualifiedName + " " + measurement);
                }
            }
        }
    }
    
    
    public void runLCOMCalculator() {
        ASTWalker walker = new ASTWalker();
        LCOMCalculator lcomCalculator =
            new LCOMCalculator(entityExtractor);
        walker.addProcessor(lcomCalculator);
        walker.walk(tree);
        Map<String, Integer> lcom = lcomCalculator.getResults();
        for (Map.Entry<String, Integer> lcomResult : lcom.entrySet()) {
            System.out.println("LCOM " + lcomResult.getKey() + "=" 
                    + lcomResult.getValue());
        }
    }
    
    public void runCBOCalculator() {
        ASTWalker walker = new ASTWalker();
        CBOCalculator cboCalculator =
            new CBOCalculator(inheritanceExtractor);
        walker.addProcessor(cboCalculator);
        walker.walk(tree);
        Map<String, Integer> cbo = cboCalculator.getResults();
        for (Map.Entry<String, Integer> cboResult : cbo.entrySet()) {
            System.out.println("CBO " + cboResult.getKey() + "=" 
                    + cboResult.getValue());
       }
    }
    
    public static void main(String args[]) throws IOException, RecognitionException {
        
        JavaParserRunner javaParserTest = new JavaParserRunner();
        if (args.length != 2) 
            return;
        String startingDir = args[0];
        String filePattern = args[1];
        Finder finder = new Finder(filePattern);
        List<File> files = finder.getMatchingFiles(startingDir);
        System.out.println("SIZE=" + files.size());
        for (File file : files) {
            System.out.println("Handling " + file.getCanonicalPath());
            javaParserTest.runParser(file.getCanonicalPath());
            javaParserTest.runEntityExtractor();
            javaParserTest.runInheritanceExtractor();
            javaParserTest.runCBOCalculator();
        }
    }
}
