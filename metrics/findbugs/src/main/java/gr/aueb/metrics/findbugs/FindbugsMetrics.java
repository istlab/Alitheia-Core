/*
 * Copyright 2012 - Organization for Free and Open Source Software,
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
package gr.aueb.metrics.findbugs;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.*;
import eu.sqooss.service.db.*;
import eu.sqooss.service.fds.CheckoutException;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.OnDiskCheckout;
import eu.sqooss.service.util.FileUtils;
import org.osgi.framework.BundleContext;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

@MetricDeclarations(metrics = {
//Security
@MetricDecl(mnemonic = "DCDP",    activators = {ProjectFile.class}, descr = "Dm: Hardcoded constant database password"),
@MetricDecl(mnemonic = "DEDP",    activators = {ProjectFile.class}, descr = "Dm: Empty database password"),
@MetricDecl(mnemonic = "HRPTC",   activators = {ProjectFile.class}, descr = "HRS: HTTP cookie formed from untrusted input"),
@MetricDecl(mnemonic = "HRPTHH",  activators = {ProjectFile.class}, descr = "HRS: HTTP Response splitting vulnerability"),
@MetricDecl(mnemonic = "SNSPTE",  activators = {ProjectFile.class}, descr = "SQL: Nonconstant string passed to execute method on an SQL statement"),
@MetricDecl(mnemonic = "SPSGFNS", activators = {ProjectFile.class}, descr = "SQL: A prepared statement is generated from a nonconstant String"),
@MetricDecl(mnemonic = "XRPTJW",  activators = {ProjectFile.class}, descr = "XSS: JSP reflected cross site scripting vulnerability"),
@MetricDecl(mnemonic = "XRPTSE",  activators = {ProjectFile.class}, descr = "XSS: Servlet reflected cross site scripting vulnerability in error page"),
@MetricDecl(mnemonic = "XRPTSW",  activators = {ProjectFile.class}, descr = "XSS: Servlet reflected cross site scripting vulnerability"),

//Malicious Code
@MetricDecl(mnemonic = "DCCIDP",  activators = {ProjectFile.class}, descr = "DP: Classloaders should only be created inside doPrivileged block"),
@MetricDecl(mnemonic = "DDIDP",   activators = {ProjectFile.class}, descr = "DP: Method invoked that should be only be invoked inside a doPrivileged block"),
@MetricDecl(mnemonic = "EER",     activators = {ProjectFile.class}, descr = "EI: May expose internal representation by returning reference to mutable object"),
@MetricDecl(mnemonic = "EER2",    activators = {ProjectFile.class}, descr = "EI2: May expose internal representation by incorporating reference to mutable object"),
@MetricDecl(mnemonic = "FPSBP",   activators = {ProjectFile.class}, descr = "FI: Finalizer should be protected, not public"),
@MetricDecl(mnemonic = "EESR",    activators = {ProjectFile.class}, descr = "MS: May expose internal static state by storing a mutable object into a static field"),
@MetricDecl(mnemonic = "MCBF",    activators = {ProjectFile.class}, descr = "MS: Field isn't final and can't be protected from malicious code"),
@MetricDecl(mnemonic = "MER",     activators = {ProjectFile.class}, descr = "MS: Public static method may expose internal representation by returning array"),
@MetricDecl(mnemonic = "MFP",     activators = {ProjectFile.class}, descr = "MS: Field should be both final and package protected"),
@MetricDecl(mnemonic = "MMA",     activators = {ProjectFile.class}, descr = "MS: Field is a mutable array"),
@MetricDecl(mnemonic = "MMH",     activators = {ProjectFile.class}, descr = "MS: Field is a mutable Hashtable"),
@MetricDecl(mnemonic = "MOP",     activators = {ProjectFile.class}, descr = "MS: Field should be moved out of an interface and made package protected"),
@MetricDecl(mnemonic = "MP",      activators = {ProjectFile.class}, descr = "MS: Field should be package protected"),
@MetricDecl(mnemonic = "MSBF",    activators = {ProjectFile.class}, descr = "MS: Field isn't final but should be"),

//Summarized per project versions
@MetricDecl(mnemonic = "TDCDP",    activators = {ProjectVersion.class}, descr = "Dm: Hardcoded constant database password (total)"),
@MetricDecl(mnemonic = "TDEDP",    activators = {ProjectVersion.class}, descr = "Dm: Empty database password (total)"),
@MetricDecl(mnemonic = "THRPTC",   activators = {ProjectVersion.class}, descr = "HRS: HTTP cookie formed from untrusted input (total)"),
@MetricDecl(mnemonic = "THRPTHH",  activators = {ProjectVersion.class}, descr = "HRS: HTTP Response splitting vulnerability (total)"),
@MetricDecl(mnemonic = "TSNSPTE",  activators = {ProjectVersion.class}, descr = "SQL: Nonconstant string passed to execute method on an SQL statement (total)"),
@MetricDecl(mnemonic = "TSPSGFNS", activators = {ProjectVersion.class}, descr = "SQL: A prepared statement is generated from a nonconstant String (total)"),
@MetricDecl(mnemonic = "TXRPTJW",  activators = {ProjectVersion.class}, descr = "XSS: JSP reflected cross site scripting vulnerability (total)"),
@MetricDecl(mnemonic = "TXRPTSE",  activators = {ProjectVersion.class}, descr = "XSS: Servlet reflected cross site scripting vulnerability in error page (total)"),
@MetricDecl(mnemonic = "TXRPTSW",  activators = {ProjectVersion.class}, descr = "XSS: Servlet reflected cross site scripting vulnerability (total)"),

@MetricDecl(mnemonic = "TDCCIDP",  activators = {ProjectVersion.class}, descr = "DP: Classloaders should only be created inside doPrivileged block (total)"),
@MetricDecl(mnemonic = "TDDIDP",   activators = {ProjectVersion.class}, descr = "DP: Method invoked that should be only be invoked inside a doPrivileged block (total)"),
@MetricDecl(mnemonic = "TEER",     activators = {ProjectVersion.class}, descr = "EI: May expose internal representation by returning reference to mutable object (total)"),
@MetricDecl(mnemonic = "TEER2",    activators = {ProjectVersion.class}, descr = "EI2: May expose internal representation by incorporating reference to mutable object (total)"),
@MetricDecl(mnemonic = "TFPSBP",   activators = {ProjectVersion.class}, descr = "FI: Finalizer should be protected, not public (total)"),
@MetricDecl(mnemonic = "TEESR",    activators = {ProjectVersion.class}, descr = "MS: May expose internal static state by storing a mutable object into a static field (total)"),
@MetricDecl(mnemonic = "TMCBF",    activators = {ProjectVersion.class}, descr = "MS: Field isn't final and can't be protected from malicious code (total)"),
@MetricDecl(mnemonic = "TMER",     activators = {ProjectVersion.class}, descr = "MS: Public static method may expose internal representation by returning array (total)"),
@MetricDecl(mnemonic = "TMFP",     activators = {ProjectVersion.class}, descr = "MS: Field should be both final and package protected (total)"),
@MetricDecl(mnemonic = "TMMA",     activators = {ProjectVersion.class}, descr = "MS: Field is a mutable array (total)"),
@MetricDecl(mnemonic = "TMMH",     activators = {ProjectVersion.class}, descr = "MS: Field is a mutable Hashtable (total)"),
@MetricDecl(mnemonic = "TMOP",     activators = {ProjectVersion.class}, descr = "MS: Field should be moved out of an interface and made package protected (total)"),
@MetricDecl(mnemonic = "TMP",      activators = {ProjectVersion.class}, descr = "MS: Field should be package protected (total)"),
@MetricDecl(mnemonic = "TMSBF",    activators = {ProjectVersion.class}, descr = "MS: Field isn't final but should be (total)")
})
@SchedulerHints(invocationOrder = InvocationOrder.NEWFIRST, activationOrder = {ProjectVersion.class})
public class FindbugsMetrics extends AbstractMetric {

    static String MAVEN_PATH = "";
    static String ANT_PATH = "";
    static String FINDBUGS_PATH = "";

    static {
        if (System.getProperty("findbugs.path") != null)
            FINDBUGS_PATH = System.getProperty("findbugs.path");
        else
            FINDBUGS_PATH = "findbugs";
        if (System.getProperty("mvn.path") != null)
            MAVEN_PATH = System.getProperty("mvn.path");
        else
            MAVEN_PATH = "mvn";
        if (System.getProperty("ant.path") != null)
            ANT_PATH = System.getProperty("ant.path");
        else
            ANT_PATH = "ant";
    }

    public FindbugsMetrics(BundleContext bc) {
        super(bc);
    }

    //Run per version only
    public void run(ProjectFile pf){}

    public List<Result> getResult(ProjectFile pf, Metric m) {
        return getResult(pf, ProjectFileMeasurement.class,
                m, Result.ResultType.INTEGER);
    }

    public List<Result> getResult(ProjectVersion pv, Metric m) {
        return getResult(pv, ProjectVersionMeasurement.class,
                m, Result.ResultType.INTEGER);
    }

    public void run(ProjectVersion pv) {

        List<ProjectFile> files = pv.getFiles();
        Pattern pom = Pattern.compile("pom.xml$");
        Pattern buildxml = Pattern.compile("build.xml$");
        Pattern trunk = Pattern.compile("/trunk");
        boolean foundTrunk = false, foundPom = false,
                foundBuild = false, maven_build = true;

        for(ProjectFile pf: files) {
            if (pom.matcher(pf.getFileName()).find())
                foundPom = true;

            if (trunk.matcher(pf.getFileName()).find())
                foundTrunk = true;

            if (buildxml.matcher(pf.getFileName()).find())
                foundBuild = true;
        }

        if (!foundTrunk) {
            log.info("Skipping version " + pv + "/trunk directory could be found");
            return;
        }

        if (foundPom)
            maven_build = true; // Prefer maven over ant
        else
            if (foundBuild)
                maven_build = false;
            else {
                log.info("Skipping version " + pv + " as neither pom.xml " +
                        "nor build.xml could be found");
                return;
            }

        FDSService fds = AlitheiaCore.getInstance().getFDSService();

        OnDiskCheckout odc = null;
        try {
            odc = fds.getCheckout(pv, "/trunk");
            File checkout = odc.getRoot();

            String out = pv.getProject().getName() + "-" + pv.getRevisionId() +
                    "-" + pv.getId() + "-out.txt";

            List<File> jars = null;
            if (maven_build)
                jars = compileMaven(pv, pom, checkout, out);
            else
                jars = compileAnt(pv, buildxml, checkout, out);

            for(File jar: jars) {

                //String pkgs = getPkgs(pv.getFiles(Pattern.compile("src/main/java/"),
                //        ProjectVersion.MASK_FILES));
                //pkgs = pkgs.substring(0, pkgs.length() - 1);
                String findbugsOut = pv.getRevisionId()+"-" + jar.getName() + "-" +pv.getProject().getName() + ".xml";

                List<String> findbugsArgs = new ArrayList<String>();
                findbugsArgs.add(FINDBUGS_PATH);
                findbugsArgs.add("-textui");
                //findbugsArgs.add("-onlyAnalyze");
                //findbugsArgs.add(pkgs);
                findbugsArgs.add("-xml");
                findbugsArgs.add("-output");
                findbugsArgs.add(findbugsOut);
                findbugsArgs.add(jar.getAbsolutePath());

                ProcessBuilder findbugs = new ProcessBuilder(findbugsArgs);
                findbugs.redirectErrorStream(true);
                int retVal = runReadOutput(findbugs.start(), out);

                if (retVal != 0) {
                    log.warn("Findbugs failed. See file:" + out);
                }

                File f = new File(findbugsOut);
                storeResults(parseFindbugsResults(f), files, pv);

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

    private List<File> compileMaven(ProjectVersion pv, Pattern pom,
                                    File checkout, String out) throws IOException {

        File pomFile = FileUtils.findBreadthFirst(checkout, pom);

        if (pomFile == null) {
            log.warn(pv + " No pom.xml found in checkout?!");
            return new ArrayList<File>();
        }

        ProcessBuilder maven = new ProcessBuilder(MAVEN_PATH, "install", "-DskipTests=true");
        maven.directory(pomFile.getParentFile());
        maven.redirectErrorStream(true);
        int retVal = runReadOutput(maven.start(), out);

        // project dependencies
        List<File> deps = new ArrayList<File>();
        if (retVal != 0) {
            log.warn("Build with maven failed. See file:" + out);
            return deps;
        }
        // Copy the script that gathers the dependency from
        // the resource bundle
        File copyDepsScript = new File(pomFile.getParentFile(), "copy-dependencies");
        FileOutputStream fos = new FileOutputStream(copyDepsScript);
        InputStream in = bc.getBundle().getResource("copy-dependencies").openStream();

        int read;
        byte[] buff = new byte[1024];
        while ((read = in.read(buff)) != -1) {
            fos.write(buff, 0, read);
        }

        copyDepsScript.setExecutable(true);
        fos.close();
        in.close();

        ProcessBuilder copyDeps = new ProcessBuilder("./copy-dependencies");
        copyDeps.directory(pomFile.getParentFile());
        copyDeps.redirectErrorStream(true);
        int retVal2 = runReadOutput(copyDeps.start(), out);
        if (retVal2 == 0) {
            File allDeps = new File(checkout.getPath() + "/all-deps");
            if (allDeps.exists() && allDeps.isDirectory()) {
                deps = Arrays.asList(allDeps.listFiles());
            }
        }

        List<File> jars = getMavenJars(checkout);
        jars.addAll(deps);

        return jars;
    }

    private List<File> compileAnt(ProjectVersion pv, Pattern buildxml,
                                    File checkout, String out) throws IOException {

        File antFile = FileUtils.findBreadthFirst(checkout, buildxml);

        if (antFile == null) {
            log.warn(pv + " No build.xml found in checkout?!");
            return new ArrayList<File>();
        }

        ProcessBuilder ant = new ProcessBuilder(ANT_PATH);
        ant.directory(antFile.getParentFile());
        ant.redirectErrorStream(true);
        int retVal = runReadOutput(ant.start(), out);

        if (retVal != 0) {
            log.warn("Build with ant failed. See file:" + out);
            return new ArrayList<File>();
        }

        return getAntJars(checkout);
    }

    public String getPkgs(List<ProjectFile> files) {
        Set<String> pkgs = new HashSet<String>();
        Pattern p = Pattern.compile("src/main/java/(.*\\.java)");

        for(ProjectFile f: files) {
            Matcher m = p.matcher(f.getFileName());
            if (m.find()) {
                pkgs.add(FileUtils.dirname(m.group(1)).replace('/','.')+".-");
            }
        }

        StringBuffer sb = new StringBuffer();
        for (String pkg : pkgs)
            sb.append(pkg).append(",");

        return sb.toString();
    }
    
    public List<File> getMavenJars(File checkout) {
        List<File> jars = FileUtils.findGrep(checkout, Pattern.compile("target/.*\\.jar$"));
        List<File> result = new ArrayList<File>();
        //Exclude common maven artifacts which don't contain bytecode
        for(File f: jars) {
            if (f.getName().endsWith("-sources.jar"))
                continue;
            if (f.getName().endsWith("with-dependencies.jar"))
                continue;
            if (f.getName().endsWith("-javadoc.jar"))
                continue;
            result.add(f);
        }
        return result;
    }

    public List<File> getAntJars(File checkout) {
        List<File> jars = FileUtils.findGrep(checkout, Pattern.compile("target/.*\\.jar$"));
        List<File> result = new ArrayList<File>();
        //Exclude common maven artifacts which don't contain bytecode
        for(File f: jars) {
            // Exclude libraries commonly included in Maven repositories
            if (f.getAbsolutePath().contains("/lib/"))
                continue;
            result.add(f);
        }
        return result;
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
        if (nodesBugs.getLength() == nodes.getLength()) {
            for (int i = 0; i < nodes.getLength(); i++) {
                // check if this Bug exists in our HashMap
                if (!resultsMap.containsKey(nodesBugs.item(i).getAttributes().getNamedItem("type").getTextContent())) {
                    // no Bug like this in the HashMap
                    Map <String, Integer> tmp = new HashMap<String, Integer>();
                    tmp.put(nodes.item(i).getAttributes().getNamedItem("sourcepath").getTextContent(), 1);
                    resultsMap.put(
                            nodesBugs.item(i).getAttributes().getNamedItem("type").getTextContent(), tmp);
                } else {
                    // there is a bug like this in our HashMap
                    Map <String, Integer> tmp = new HashMap<String, Integer>();
                    tmp = resultsMap.get(
                            nodesBugs.item(i).getAttributes().getNamedItem("type").getTextContent());
                    if (!tmp.containsKey(nodes.item(i).getAttributes().getNamedItem("sourcepath").getTextContent())) {
                        // this is a new file that contains this bug
                        tmp.put(nodes.item(i).getAttributes().getNamedItem("sourcepath").getTextContent(), 1);
                       resultsMap.put(
                                nodesBugs.item(i).getAttributes().getNamedItem("type").getTextContent(), tmp);
                    } else {
                        // found this bug in more than one lines on the same file
                        tmp.put(nodes.item(i).getAttributes().getNamedItem("sourcepath").getTextContent(),
                                tmp.get(nodes.item(i).getAttributes().getNamedItem("sourcepath").getTextContent()) + 1);
                        resultsMap.put(
                                nodesBugs.item(i).getAttributes().getNamedItem("type").getTextContent(), tmp);
                    }
                }
            }
        }

        //dimitro
        return resultsMap;
    }

    private void storeResults(Map <String, Map<String, Integer>> results,
                              List<ProjectFile> files, ProjectVersion pv) {
        List<Metric> metrics = getAllSupportedMetrics();
        List<ProjectFileMeasurement> fileMeasurements = new ArrayList<ProjectFileMeasurement>();
        List<ProjectVersionMeasurement> versionMeasurements = new ArrayList<ProjectVersionMeasurement>();

        for(String key: results.keySet()) {
            String mnem = bugToMnemonic(key, false);
            Metric m = findMetric(metrics, mnem);
            if (m == null) {
                log.warn("Cannot find bug " + key + " as installed metric");
                continue;
            }

            for (String fileName: results.get(key).keySet()) {
                ProjectFile file = findFile(files, fileName);
                if (file == null) {
                    log.warn("Cannot find file path " + fileName + " in DB project files");
                    continue;
                }

                if(getResult(file, m).isEmpty())
                    fileMeasurements.add(new ProjectFileMeasurement(m, file, results.get(key).get(fileName).toString()));
            }
        }

        //Summarize per version
        for(String key: results.keySet()) {
            String mnem = bugToMnemonic(key, true);
            Metric m = findMetric(metrics, mnem);
            if (m == null) {
                log.warn("Cannot find bug " + key + " as installed metric");
                continue;
            }
            Integer bugTotal = 0;
            for(Integer bugResult: results.get(key).values())
                bugTotal += bugResult;
            if (bugTotal > 0 && !pvMeasurementExists(pv, m)) {
                versionMeasurements.add(new ProjectVersionMeasurement(m, pv, bugTotal.toString()));
            }
        }
        db.addRecords(fileMeasurements);
        db.addRecords(versionMeasurements);
    }

    private boolean pvMeasurementExists(ProjectVersion pv, Metric m) {
        DBService db = AlitheiaCore.getInstance().getDBService();
        String q = "from ProjectVersionMeasurement pvm " +
                "where pvm.metric = :metric " +
                "and pvm.projectVersion = :version";

        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("metric", m);
        parameters.put("version", pv);

        return db.doHQL(q, parameters).size() > 0;
    }

    private ProjectFile findFile(List<ProjectFile> files, String path) {
        for(ProjectFile pf: files) {
            if (pf.getFileName().endsWith(path))
                return pf;
        }
        return null;
    }

    private Metric findMetric(List<Metric> metrics, String mnemonic) {
        for(Metric m : metrics) {
            if (m.getMnemonic().equals(mnemonic))
                return m;
        }
        return null;
    }

    private String bugToMnemonic(String bug, boolean version) {
        StringBuffer result = new StringBuffer();

        for (String bugChunk : bug.split("_")) {
            result.append(bugChunk.charAt(0));
        }
        if (version)
            return "T" + result.toString();
        return result.toString();
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
