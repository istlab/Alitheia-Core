package eu.sqooss.tool;

import org.apache.commons.cli.*;

/**
 * Command Line handling class for the plugin options
 */
public class PluginCLI extends CLI {
    
    private PluginCLI(String[] args) {
        super(args);
    }    
    
    public static void parse(String[] args) {
        PluginCLI pcli = new PluginCLI(args);
        CommandLine cmdLine = pcli.parseArgs();
    }
}
