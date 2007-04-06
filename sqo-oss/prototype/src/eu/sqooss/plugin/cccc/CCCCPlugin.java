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

package eu.sqooss.plugin.cccc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import eu.sqooss.db.Plugin;
import eu.sqooss.db.ProjectFile;
import eu.sqooss.plugin.PluginException;

/**
 * Implements a plugin that handles the execution and parsing of the results of
 * the C/C++ Code Counter tool {@link http://sourceforge.net/projects/cccc} The
 * tasks performed by both the executor and the parser are implemented here so
 * there is no need for a separate executor and parser.
 */
public class CCCCPlugin extends Plugin {

    private String cmd;

    private HashMap<String, String> metricPaths;

    /**
     * Creates a new instance of the class. It is parameterless to allow
     * creating instances through reflection.
     */
    public CCCCPlugin() {
        cmd = "cccc";
        metricPaths = new HashMap<String, String>();
        metricPaths.put("MVG", "/CCCC_Project/procedural_summary/module"
                + "[name='%s']/McCabes_Cyclomatic_Complexity");
        metricPaths.put("WMC", "/CCCC_Project/oo_design/module[name='%s']"
                + "/weighted_methods_per_class_unity");
        metricPaths.put("DIT", "/CCCC_Project/oo_design/module[name='%s']"
                + "/depth_of_inheritance_tree");
        metricPaths.put("NOC", "/CCCC_Project/oo_design/module[name='%s']"
                + "/number_of_children");
        metricPaths.put("CBO", "/CCCC_Project/oo_design/module[name='%s']"
                + "/coupling_between_objects");
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.sqooss.db.Plugin#run(eu.sqooss.db.ProjectFile)
     */
    @Override
    public HashMap<String, String> run(ProjectFile file) throws PluginException {
        // set the target of the parser
        File f = new File(file.getName());
        String target = f.getName().substring(f.getPath().length());
        int pos = target.lastIndexOf(".");
        if (pos > 0)
            target = target.substring(0, pos);

        InputStream is = execute(f);
        if (is == null)
            throw new PluginException("The execution of the cccc tool failed");

        return parse(is, target);
    }

    /**
     * Executes the cccc tool and returns a stream from the xml file produced
     * 
     * @param file
     *            The
     * @return
     */
    private InputStream execute(File file) {
        StringBuilder target = new StringBuilder();
        target.append(cmd);
        String outputPath = System.getProperty("java.io.tmpdir");
        if (!outputPath.endsWith(System.getProperty("file.separator")))
            outputPath += System.getProperty("file.separator");
        outputPath += "cccc";
        outputPath += System.getProperty("file.separator");
        target.append(" --outdir=");
        target.append(outputPath);
        target.append(" " + file.toString());

        try {
            Process p = Runtime.getRuntime().exec(target.toString());
            p.waitFor();
            FileInputStream result = new FileInputStream(outputPath
                    + "cccc.xml");
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    private HashMap<String, String> parse(InputStream is, String target) {
        HashMap<String, String> results = new HashMap<String, String>();

        try {
            Document xmldoc;
            SAXReader reader = new SAXReader();
            xmldoc = reader.read(is);

            for (Entry<String, String> entry : metricPaths.entrySet()) {
                try {
                    RetrieveMetric(results, entry.getValue(), entry.getKey(),
                            target, xmldoc);
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            // an error occured while reading the xml document
        }
        return results;
    }

    /**
     * Performs the lookup of an xml element that contains the value of a
     * metric, extracts the value and adds it to the result list
     * 
     * @param results
     *            The metric values container
     * @param path
     *            The XPath query that describes the location of a metric
     * @param metricName
     *            The identifier of the metric
     * @param target
     *            The name of the target element
     * @param doc
     *            The Xml Document to be parsed
     */
    private void RetrieveMetric(HashMap<String, String> results, String path,
            String metricName, String target, Document doc) {
        String xpath = String.format(path, target);
        Node node = doc.selectSingleNode(xpath);
        String mv = node.valueOf("@value");
        results.put(metricName, mv);
    }

}
