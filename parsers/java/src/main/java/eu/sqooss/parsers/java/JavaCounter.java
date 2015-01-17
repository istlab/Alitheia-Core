package eu.sqooss.parsers.java;

import java.io.IOException;
import java.util.Deque;
import java.util.Map;

import eu.sqooss.parsers.java.JavaTreeLexer;
import eu.sqooss.parsers.java.JavaTreeParser;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;

//import eu.sqooss.service.db.ProjectFile;
//import eu.sqooss.service.db.ProjectVersion;

public class JavaCounter {
    
    public static void main(String[] args) throws RecognitionException, 
        IOException {
	// Create an input character stream from input arguments
	ANTLRInputStream input = new ANTLRInputStream(System.in);
	// Create an ExprLexer that feeds from that stream
	JavaTreeLexer lexer = new JavaTreeLexer(input);
	// Create a stream of tokens fed by the lexer
	CommonTokenStream tokens = new CommonTokenStream(lexer);
	// Create a parser that feeds off the token stream
	JavaTreeParser parser = new JavaTreeParser(tokens);
	SpanningNodeAdaptor adaptor = new SpanningNodeAdaptor();
        parser.setTreeAdaptor(adaptor);
	// Begin parsing
	JavaTreeParser.compilationUnit_return result = parser.compilationUnit();

   
	System.out.println(lexer.getLine() + ":" + lexer.commentLines.size()
                + ":" + lexer.mixedCodeCommentLines.size() + ":"
                + lexer.wsLines.size());

        Tree t = (Tree) result.getTree();
        System.out.println(t.toStringTree());

        // Walk resulting tree
        EntityExtractor entityExtractor = new EntityExtractor();
        ASTWalker walker = new ASTWalker();
        McCabeCalculator mcCabeCalculator = 
            new McCabeCalculator(entityExtractor);
        walker.addProcessor(mcCabeCalculator);
        walker.walk(t);

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
    
//    public void run(ProjectVersion pv) {
//	List<ProjectFile> files = pv.getFiles(Pattern.compile("*.java"));
//	for (ProjectFile file: files) {
//	    // TODO
//	}
//    }
}

