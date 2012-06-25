package eu.sqooss.parsers.python;

import java.io.IOException;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

//import eu.sqooss.service.db.ProjectFile;
//import eu.sqooss.service.db.ProjectVersion;

/**
 * @author Panos
 */
public class PythonCounter {
    
    public static void main(String[] args) throws RecognitionException, 
    IOException {
	// Create an input character stream from input arguments
	ANTLRInputStream input = new ANTLRInputStream(System.in);
	// Create a lexer that feeds from that stream
	PythonLexer lexer = new PythonLexer(input);
	// Create a stream of tokens fed by the lexer
	CommonTokenStream tokens = new CommonTokenStream(lexer);
	// Create a parser that feeds off the token stream
	PythonParser parser = new PythonParser(tokens);
	// Begin parsing
	parser.file_input();

   
    System.out.println(lexer.getLine() 
        + ":" + lexer.commentLines.size()
        + ":" + lexer.mixedCodeCommentLines.size()
        + ":" + lexer.wsLines.size());

    }
}

