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

import java.io.InputStream;
import java.util.HashMap;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import eu.sqooss.plugin.OutputParser;

/**
 * Implements the parsing of the xml files produced by CCCC.
 *
 */
public class CCCCOutputParser implements OutputParser {

	private String target; //needed to detect the targeted elements within
	//the xml file
	
	/**
	 * Sets the module name of the XML elements that contain the values of
	 * the metrics referring to the examined project file 
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * Gets the module name of the XML elements that contain the values of
	 * the metrics referring to the examined project file 
	 */
	public String getTarget() {
		return target;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.sqooss.plugin.OutputParser#parse(java.io.InputStream)
	 */
	public HashMap<String, String> parse(InputStream is) {
		HashMap<String, String> results = new HashMap<String, String>();
		
		if(target == null)
			return results;
		// An exception should be thrown, because CCCC often includes info
		//about other classes in the output files, so if the target class
		//name is not set then we can not obtain its metrics correctly
		
		try {
			Document xmldoc;
			SAXReader reader = new SAXReader();
			xmldoc = reader.read(is);
			
			String path = "/CCCC_Project/procedural_summary/module[name='%s']"
				+ "/McCabes_Cyclomatic_Complexity";
			RetrieveMetric(results, path, "MVG", xmldoc);
			
			path = "/CCCC_Project/oo_design/module[name='%s']"
				+ "/weighted_methods_per_class_unity";
			RetrieveMetric(results, path, "WMC", xmldoc);
			
			path = "/CCCC_Project/oo_design/module[name='%s']"
				+ "/depth_of_inheritance_tree";
			RetrieveMetric(results, path, "DIT", xmldoc);
			
			path = "/CCCC_Project/oo_design/module[name='%s']"
				+ "/number_of_children";
			RetrieveMetric(results, path, "NOC", xmldoc);
			
			path = "/CCCC_Project/oo_design/module[name='%s']"
				+ "/coupling_between_objects";
			RetrieveMetric(results, path, "CBO", xmldoc);
			
			
		} catch(Exception e) {
			
		}
		return results;
	}

	/**
	 * Performs the lookup of an xml element that contains the value of a
	 * metric, extracts the value and adds it to the result list
	 * @param results The metric values container
	 * @param path The XPath query that describes the location of a metric
	 * @param metricName The identifier of the metric
	 * @param doc The Xml Document to be parsed
	 */
	private void RetrieveMetric(HashMap<String, String> results, String path,
                    			String metricName, Document doc) {
		String xpath = String.format(path, target);
		Node node = doc.selectSingleNode(xpath);
		
		String mv = ((Element)node).attributeValue("value");
		
		results.put(metricName, mv);
	}
	
}
