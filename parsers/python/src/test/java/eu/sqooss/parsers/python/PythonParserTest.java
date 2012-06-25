package eu.sqooss.parsers.python;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;
import org.junit.BeforeClass;
import org.junit.Test;

public class PythonParserTest {

    static InputStream in;

    @BeforeClass
    public static void setup() throws FileNotFoundException {
	in = new FileInputStream("/Users/Panos/Documents/Work/grnet/synnefo/db/models.py");
    }

    @Test
    public void testParser() throws IOException, RecognitionException {

	assertNotNull(in);

	// Create an input character stream from standard in
	ANTLRInputStream input = new ANTLRInputStream(in);
	// Create an ExprLexer that feeds from that stream
	PythonLexer lexer = new PythonLexer(input);
	// Create a stream of tokens fed by the lexer
	CommonTokenStream tokens = new CommonTokenStream(lexer);
	// Create a parser that feeds off the token stream
	PythonParser parser = new PythonParser(tokens);
	// Begin parsing at rule prog
	parser.file_input();

	System.out.println(lexer.getLine() + ":" + lexer.commentLines.size()
		+ ":" + lexer.mixedCodeCommentLines.size() + ":"
		+ lexer.wsLines.size());
    }
}
