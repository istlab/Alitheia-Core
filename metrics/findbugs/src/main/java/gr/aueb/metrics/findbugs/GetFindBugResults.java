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

/*
** Author: dimitro
** Date: 04/01/12
*/

package gr.aueb.metrics.findbugs;
import java.io.File;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.*;
import javax.xml.xpath.*;
import javax.xml.parsers.*;
import java.io.IOException;
import org.xml.sax.SAXException;

public class GetFindBugResults {
	String outputXMLFile;
	Map <String, Map <String, Integer>> resultsMap = new HashMap <String, Map <String, Integer>> ();
	
	public GetFindBugResults (String XMLFilename){
		this.outputXMLFile = XMLFilename;
	}
	
	/**
	 * parses the XML document that contains the FindBugs report
	 * and finds bugs of security-related categories. Then creates a
	 * HashMap that includes these bug instances, the files that these
	 * bugs exist and how many times they exist in these files.
	 *
	 */

	public Map <String, Map <String, Integer>> parseResults () {
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
			doc = builder.parse(new File(this.outputXMLFile));
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
				if (!this.resultsMap.containsKey(nodesBugs.item(i).getAttributes().getNamedItem("type").getTextContent())) {
					// no Bug like this in the HashMap
					Map <String, Integer> tmp = new HashMap<String, Integer>();
					tmp.put(nodes.item(i).getAttributes().getNamedItem("sourcepath").getTextContent().toString(), 1);
					this.resultsMap.put(
							nodesBugs.item(i).getAttributes().getNamedItem("type").getTextContent().toString(), tmp);
				} else {
					// there is a bug like this in our HashMap
					Map <String, Integer> tmp = new HashMap<String, Integer>();
					tmp = this.resultsMap.get(
							nodesBugs.item(i).getAttributes().getNamedItem("type").getTextContent().toString());
					if (!tmp.containsKey(nodes.item(i).getAttributes().getNamedItem("sourcepath").getTextContent().toString())) {
						// this is a new file that contains this bug
						tmp.put(nodes.item(i).getAttributes().getNamedItem("sourcepath").getTextContent().toString(), 1);
						this.resultsMap.put(
							nodesBugs.item(i).getAttributes().getNamedItem("type").getTextContent().toString(), tmp);
					} else {
						// found this bug in more than one lines on the same file
						tmp.put(nodes.item(i).getAttributes().getNamedItem("sourcepath").getTextContent().toString(),
								tmp.get(nodes.item(i).getAttributes().getNamedItem("sourcepath").getTextContent().toString()) + 1);
						this.resultsMap.put(
								nodesBugs.item(i).getAttributes().getNamedItem("type").getTextContent().toString(), tmp);
					}
				}
			}
		}

		return resultsMap;

	}
	
}
