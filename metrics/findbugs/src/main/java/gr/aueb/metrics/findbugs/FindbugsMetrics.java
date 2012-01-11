/*
 * Copyright 2008 - Organization for Free and Open Source Software,  
 *                  Athens, Greece.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

/*
** That copyright notice makes sense for code residing in the 
** main SQO-OSS repository. For the FindbugsMetrics plug-in only, the Copyright
** notice may be removed and replaced by a statement of your own
** with (compatible) license terms as you see fit; the FindbugsMetrics
** plug-in itself is insufficiently a creative work to be protected
** by Copyright.
*/

/* This is the package for this particular plug-in. Third-party
** applications will want a different package name, but it is
** *ESSENTIAL* that the package name contain the string '.metrics.'
** because this is how Alitheia Core discovers the metric plug-ins. 
*/
package gr.aueb.metrics.findbugs;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.fds.CheckoutException;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.OnDiskCheckout;
import eu.sqooss.service.util.FileUtils;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.MetricDecl;
import eu.sqooss.service.abstractmetric.MetricDeclarations;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.db.Metric;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

@MetricDeclarations(metrics = {
        @MetricDecl(mnemonic = "SECBUG", activators = {ProjectVersion.class},
                descr = "FindbugsMetrics Metric")
})
public class FindbugsMetrics extends AbstractMetric {

    final String MAVEN_PATH = "mvn";
    final String FINDBUGS_PATH = "findbugs";

    public FindbugsMetrics(BundleContext bc) {
        super(bc);
    }

    public List<Result> getResult(ProjectVersion pv, Metric m) {
        return getResult(pv, ProjectVersionMeasurement.class,
                m, Result.ResultType.INTEGER);
    }

    public void run(ProjectVersion pv) {

        if (pv.getFiles(Pattern.compile("pom.xml$"), ProjectVersion.MASK_FILES).isEmpty()) {
            log.info("Skipping version " + pv + " as no pom.xml " +
                    "file could be found");
            return;
        }

        FDSService fds = AlitheiaCore.getInstance().getFDSService();

        OnDiskCheckout odc = null;
        try {
            odc = fds.getCheckout(pv, "/trunk");
            File checkout = odc.getRoot();

            File pom = FileUtils.findBreadthFirst(checkout, Pattern.compile("pom.xml"));

            if (pom == null) {
                log.warn(pv +" No pom.xml found in checkout?!");
                return;
            }

            String out = pv.getProject().getName() + "-" + pv.getRevisionId() +
                    "-" + pv.getId() + "-out.txt";

            ProcessBuilder maven = new ProcessBuilder(MAVEN_PATH, "install", "-DskipTests=true");
            maven.directory(pom.getParentFile());
            maven.redirectErrorStream(true);
            int retVal = runReadOutput(maven.start(), out);

            if (retVal != 0) {
                log.warn("Build with maven failed. See file:" + out);
            }

            List<File> jars = FileUtils.findGrep(checkout, Pattern.compile("target/.*\\.jar$"));
            
            for(File jar: jars) {

                String pkgs = getPkgs(pv.getFiles(Pattern.compile("src/main/java/"),
                        ProjectVersion.MASK_FILES));

                List<String> findbugsArgs = new ArrayList<String>();
                findbugsArgs.add(FINDBUGS_PATH);
                findbugsArgs.add("-textui");
                findbugsArgs.add("-xml");
                findbugsArgs.add("-onlyAnalyze"); findbugsArgs.add(pkgs);
                findbugsArgs.add("-output"); findbugsArgs.add(pv.getProject().getName() + ".xml");
                findbugsArgs.add(jar.getAbsolutePath());
                ProcessBuilder findbugs = new ProcessBuilder(findbugsArgs);
                findbugs.directory(pom.getParentFile());
                findbugs.redirectErrorStream(true);
                retVal = runReadOutput(findbugs.start(), out);

                if (retVal != 0) {
                    log.warn("Build with findbugs failed. See file:" + out);
                }

                File f = new File(pv.getProject().getName() + ".xml");
                parseFindbugsResults(f);
            }

        } catch (CheckoutException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (odc != null)
                fds.releaseCheckout(odc);
        }
    }

    public String getPkgs(List<ProjectFile> files) {
        Set<String> pkgs = new HashSet<String>();
        Pattern p = Pattern.compile("src/main/java/(.*\\.java)");

        for(ProjectFile f: files) {
            Matcher m = p.matcher(f.getFileName());
            if (m.find()) {
                pkgs.add(FileUtils.dirname(m.group(1)).replace('/','.'));
            }
        }

        StringBuffer sb = new StringBuffer();
        for (String pkg : pkgs)
            sb.append(pkg).append(",");

        return sb.toString();
    }

    public int runReadOutput(Process pr, String name) throws IOException {
        OutReader outReader = new OutReader(pr.getInputStream(), name);
        outReader.start();
        int retVal = -1;
        while (retVal == -1) {
            try {
                retVal = pr.waitFor();
            } catch (Exception ignored) {}
        }
        return retVal;
    }

    /**
     * parses the XML document that contains the FindBugs report
     * and finds bugs of security-related categories. Then creates a
     * HashMap that includes these bug instances, the files that these
     * bugs exist and how many times they exist in these files.
     *
     */
    public Map <String, Map<String, Integer>> parseFindbugsResults (File results) {
        Map <String, Map <String, Integer>> resultsMap = new HashMap <String, Map <String, Integer>> ();
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = null;
        Document doc = null;
        Object resultBugs = null;
        Object resultDetails = null;
        try {
            builder = domFactory.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        try {
            //parse the XML file
            doc = builder.parse(results);
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        doc.getDocumentElement().normalize();
        XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression exprBugs = null;
		XPathExpression exprDetails = null;
		try {
			//get the nodes that fall into the categories that we need
			exprBugs = xpath.compile("//BugCollection/BugInstance" +
									 "[@category = \"MALICIOUS_CODE\" or @category = \"SECURITY\"]");
			resultBugs = exprBugs.evaluate(doc, XPathConstants.NODESET);
			//get the nodes that contain the source path and the line where the bug starts
			exprDetails = xpath.compile("//BugCollection/BugInstance" +
										"[@category = \"MALICIOUS_CODE\" or @category = \"SECURITY\"]/Class/SourceLine");
			resultDetails = exprDetails.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException xpee) {
			xpee.printStackTrace();
		}

        NodeList nodes = (NodeList) resultDetails;
        NodeList nodesBugs = (NodeList) resultBugs;
        if (nodes.getLength() == nodesBugs.getLength()) {
            for (int i = 0; i < nodes.getLength(); i++) {
                // check if this Bug exists in our HashMap
                if (!resultsMap.containsKey(nodesBugs.item(i).getAttributes().getNamedItem("type").getTextContent())) {
                    // no Bug like this in the HashMap
                    Map <String, Integer> tmp = new HashMap<String, Integer>();
                    tmp.put(nodes.item(i).getAttributes().getNamedItem("sourcepath").getTextContent().toString(), 1);
                    resultsMap.put(
                            nodesBugs.item(i).getAttributes().getNamedItem("type").getTextContent().toString(), tmp);
                } else {
                    // there is a bug like this in our HashMap
                    Map <String, Integer> tmp = new HashMap<String, Integer>();
                    tmp = resultsMap.get(
                            nodesBugs.item(i).getAttributes().getNamedItem("type").getTextContent().toString());
                    if (!tmp.containsKey(nodes.item(i).getAttributes().getNamedItem("sourcepath").getTextContent().toString())) {
                        // this is a new file that contains this bug
                        tmp.put(nodes.item(i).getAttributes().getNamedItem("sourcepath").getTextContent().toString(), 1);
                       resultsMap.put(
                                nodesBugs.item(i).getAttributes().getNamedItem("type").getTextContent().toString(), tmp);
                    } else {
                        // found this bug in more than one lines on the same file
                        tmp.put(nodes.item(i).getAttributes().getNamedItem("sourcepath").getTextContent().toString(),
                                tmp.get(nodes.item(i).getAttributes().getNamedItem("sourcepath").getTextContent().toString()) + 1);
                        resultsMap.put(
                                nodesBugs.item(i).getAttributes().getNamedItem("type").getTextContent().toString(), tmp);
                    }
                }
            }
        }
        return resultsMap;
    }

    private class OutReader extends Thread {
        String name;
        InputStream input;

        public OutReader(InputStream in, String name) {
            this.name = name;
            this.input = in;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(input));
                FileWriter out = new FileWriter(new File(name), true);

                char[] buf = new char[8192];
                while (true) {
                    int length = in.read(buf);
                    if (length < 0)
                        break;
                    out.write(buf, 0, length);
                    out.flush();
                }
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
