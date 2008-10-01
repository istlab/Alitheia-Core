/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2008 Athens University of Economics and Business
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

package eu.sqooss.impl.service.tds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import eu.sqooss.service.tds.BTSEntry;
import eu.sqooss.service.tds.BTSAccessor;

/** 
 * A bug parser for Bugzilla XML bug descriptions. 
 */
public class BugzillaXMLParser implements BTSAccessor {
    
    private String location;

    private static final List<URI> supportedSchemes;
    
    static {
        supportedSchemes = new ArrayList<URI>();
        supportedSchemes.add(URI.create("bugzillaxml://www.sqo-oss.org"));
    }
    
    public void init(String name, Long id, URI btsURL) {
        this.location = btsURL.getPath();
    }  
    
    public void init(URI dataURL, String name) {
        
    }
    
    public BTSEntry getBug(String bugID) {
        return null;
    }

    public List<String> getBugsNewerThan(Date d) {
        return null;
    }

    public List<String> getAllBugs() {
        return null;
    } 
    
    public List<URI> getSupportedURLSchemes() {
        return supportedSchemes;
    }
    
    private void processBug(File bug) {
        
        SAXReader reader = new SAXReader();
        Document document = null;
        
        reader.setIncludeExternalDTDDeclarations(false);
        reader.setValidation(false);
        reader.setIncludeExternalDTDDeclarations(false);
        reader.setStripWhitespaceText(true);
        try {
            URL url = new URL(location);
            document = reader.read(url);
        } catch(MalformedURLException ex) {
            try {
                document = reader.read(new FileReader(this.location));
            } catch (FileNotFoundException fex) {
                
            } catch (DocumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Element root = document.getRootElement();

        for (Iterator<Element> i = root.elementIterator("bug"); i.hasNext(); ) {
            Element element = i.next();
            //Bug bug = new Bug();
            
            /* Read all the values we can and call the related setter */
         /*   String elementValue = getElementValueAsString(element.element("bug_file_loc"));
            bug.setBugFileLoc(elementValue);
            elementValue = getElementValueAsString(element.element("bug_severity"));
            bug.setSeverity(elementValue);
            elementValue = getElementValueAsString(element.element("bug_status"));
            bug.setStatus(elementValue);
            elementValue = getElementValueAsString(element.element("creation_ts"));
            try { 
                bug.setCreationTS(DATE_FORMAT.parse(elementValue));
            } catch (ParseException pex) {
                bug.setCreationTS(DEFAULT_DATE);
            }
            elementValue = getElementValueAsString(element.element("deadline"));
            try { 
                bug.setDeadline(DATE_FORMAT.parse(elementValue));
            } catch (ParseException pex) {
                bug.setDeadline(DEFAULT_DATE);
            }
            elementValue = getElementValueAsString(element.element("delta_ts"));
            try { 
                bug.setDeltaTS(DATE_FORMAT.parse(elementValue));
            } catch (ParseException pex) {
                bug.setDeltaTS(DEFAULT_DATE);
            }
            elementValue = getElementValueAsString(element.element("estimated_time"));
            try {
                bug.setEstimatedTime(Float.parseFloat(elementValue));
            } catch (NumberFormatException nfex) {
                bug.setEstimatedTime(0);
            }
            elementValue = getElementValueAsString(element.element("keywords"));
            bug.setKeywords(elementValue);
            elementValue = getElementValueAsString(element.element("op_sys"));
            bug.setOperatingSystem(elementValue);
            elementValue = getElementValueAsString(element.element("priority"));
            bug.setPriority(elementValue);
            elementValue = getElementValueAsString(element.element("product"));
            bug.setProduct(elementValue);
            elementValue = getElementValueAsString(element.element("remaining_time"));
            try {
                bug.setRemainingTime(Float.parseFloat(elementValue));       
            } catch (NumberFormatException nfex) {
                bug.setRemainingTime(0);
            }
            elementValue = getElementValueAsString(element.element("rep_platform"));
            bug.setReportPlatform(elementValue);
            elementValue = getElementValueAsString(element.element("reporter"));
            bug.setReporter(elementValue);
            elementValue = getElementValueAsString(element.element("resolution"));
            bug.setResolution(elementValue);*/
        }
    }

    private String getElementValueAsString(Element element) {
        if (element != null) {
            return element.getStringValue();
        } else {
            return "";
        }
    }
}
