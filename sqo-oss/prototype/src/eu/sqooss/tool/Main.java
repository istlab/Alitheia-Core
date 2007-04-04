/*
 * Copyright (c) Members of the SQO-OSS Collaboration, 2007
 * All rights reserved by respective owners.
 * See http://www.sqo-oss.eu/ for details on the copyright holders.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the SQO-OSS project nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.sqooss.tool;

/**
 * Main entry point class for the sqo-oss tool.
 * It provides access to the sqo-oss quality
 * measurement tool
 */
public class Main {
    
    public static void main(String[] args) {
        //
        // basic options are:
        //  - projects, plugins, task, results
        if(args.length != 0) {
            if(args[0].compareTo("projects") == 0) {
                // TODO add command line handling for projects
                return;
            }
            if(args[0].compareTo("plugins") == 0) {
                PluginCLI.parse(args);
                return;
            }
            if(args[0].compareTo("task") == 0) {
                // TODO add command line handling for tasks
                return;
            }
            if(args[0].compareTo("results") == 0) {
                // TODO add command line handling for results
                return;
            }
            if(args[0].compareTo("filegroup") == 0) {
                // TODO add command line handling for file groups
            }
            if(args[0].compareTo("help") == 0) {
                System.out.println(CLI.HEADER);
                System.out.println("Available arguments are:\n\n projects - " +
                                "configure stored projects\n plugin - configure/list " +
                                "available plugins\n task - execute a " +
                                "measurement task\n results - browse store results\n" +
                                " filegroup - configure file groups\n" +
                                " help - prints online help\n");              
                return;
            }
        }
        
        System.out.println(PluginCLI.HEADER);
        System.out.println("type 'tool.sh help' to see available commands\n");
/*    	
        
        // print the metrics
        if(cmdline.hasOption("m")) {
            System.out.println("Print all available Modules:\n");
            PluginList pl = PluginList.getInstance();
            ReadOnlyIterator roi = pl.getPlugins();
            
            while(roi.hasNext()) {
        	System.out.println(((Plugin)roi.next()).toString());
            }
            return;
        } */
        
        
    }    
}
