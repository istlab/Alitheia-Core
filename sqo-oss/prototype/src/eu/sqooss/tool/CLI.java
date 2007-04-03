package eu.sqooss.tool;

import org.apache.commons.cli.*;

/**
 * Class that contains common code for command line
 * parsing 
 */
public class CLI {
    protected Options options;
    protected HelpFormatter formatter;
    protected String[] args;
    
    protected CLI(String[] args) {
        this.options = new Options();
        this.formatter = new HelpFormatter();
        this.args = args;
    }
    
    protected CommandLine parseArgs()) {
        try {
            CommandLineParser parser = new GnuParser();
            return parser.parse(options, args);
        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            // TODO fix formatter message
            formatter.printHelp( "", options );
            return null;
        }
    }
}
