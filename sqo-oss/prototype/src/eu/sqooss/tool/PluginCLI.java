package eu.sqooss.tool;

import org.apache.commons.cli.*;

import eu.sqooss.plugin.PluginList;

import eu.sqooss.db.Plugin;
import eu.sqooss.db.Metric;

import eu.sqooss.util.ReadOnlyIterator;

/**
 * Command Line handling class for the plugin options
 */
public class PluginCLI extends CLI {

    PluginCLI(String[] args) {
        super(args);

        /* Arguments to plugin */
        options.addOption("l", "list", false, "List available plugins");
        options.addOption("m", "metric", true,
                "Prints information about a metric");

        /* Help */
        options.addOption("h", "help", false, "Prints the online help");
    }

    public void parse() {
        PluginCLI pcli = new PluginCLI(args);
        CommandLine cmdLine = pcli.parseArgs();

        if (!(cmdLine.hasOption("l") || cmdLine.hasOption("m"))) {
            error("One of the l, m options must be set", cmdLine);
            return;
        }

        if (cmdLine.hasOption('l')) {
            System.out.println("Print all available Modules:\n");
            PluginList pl = PluginList.getInstance();
            ReadOnlyIterator roi = pl.getPlugins();
            while (roi.hasNext()) {
                System.out.println(((Plugin) roi.next()).toString());
            }
            return;
        }

        if (cmdLine.hasOption('m')) {
            System.out.println("Metric Information: ");
            String name = getOptionValue(cmdLine, "m");

            Metric[] mr = Metric.getMetricByName(name);

            if (mr == null) {
                System.out.println("Metric " + name + " does not exist");
                return;
            }
            
            for( Metric m : mr) {
                System.out.println(m.getName() + " - " + m.getDescription()
                        + " by plugin " + m.getPlugin().getDescription() + " ("
                        + m.getName() + ")");
            }
        }
    }
}
