/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2008 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.BTSAccessor;
import eu.sqooss.service.tds.BTSEntry;
import eu.sqooss.service.tds.BTSEntry.BTSEntryAttachement;
import eu.sqooss.service.tds.BTSEntry.BTSEntryComment;

/**
 * A parser for Bugzilla XML bug descriptions. This accessor expects to find a
 * directory with XML bug reports, one XML file per bug report, whose name is
 * equal to the bug id in the source bugzilla system (e.g. 12345.xml). The
 * accessor uses the filesystem to retrieve file modification time information,
 * therefore if a bug report has been updated, the accessor will report the
 * change.
 */
public class BugzillaXMLParser implements BTSAccessor {
    private Logger logger;
    
    private File location;
    private String name;

    private static final List<URI> supportedSchemes;
    
    static {
        supportedSchemes = new ArrayList<URI>();
        supportedSchemes.add(URI.create("bugzillaxml://www.sqo-oss.org"));
    }
    
    /** {@inheritDoc} */
    public void init(URI dataURL, String name) throws AccessorException {
        this.name = name;
        File f = new File(dataURL.getPath());
        
        if (!f.exists() || !f.isDirectory() || !f.canRead()) {
            throw new AccessorException(this.getClass(), "Invalid path "
                    + f.getPath() + " Either not exists or not a readable " +
                    		"directory" );
        }
        
        logger = AlitheiaCore.getInstance().getLogManager().createLogger(
                Logger.NAME_SQOOSS_TDS);
        if (logger != null) {
            logger.info("Created BTSAccessor for " + dataURL.toString());
        }
        
        location = f;
    }
    
    /** {@inheritDoc} */
    public BTSEntry getBug(String bugID) {
        
        File f = new File(location.getAbsolutePath() + File.separator
                + bugID + ".xml");
        
        if (!f.exists() || !f.isFile() || !f.canRead()) {
            logger.warn("Cannot find bug file " + f.getAbsolutePath() 
                    + " in project " + name);
            return null;
        }
     
        return processBug(f);
    }

    /** {@inheritDoc} */
    public List<String> getBugsNewerThan(Date d) {
        BugzillaXMLFileFilter filter = new BugzillaXMLFileFilter(d.getTime());
        File[] files = location.listFiles(filter);
        List<String> bugIds = new ArrayList<String>();
        
        for (File f : files) {
            //At this point we know that all files are named like 123.xml
            //so no validation is necessary.
            bugIds.add(f.getName().split("\\.")[0]);
        }
        
        return bugIds;
    }

    /** {@inheritDoc} */
    public List<String> getAllBugs() {
        File[] files = location.listFiles();
        List<String> bugIds = new ArrayList<String>();
        
        for (File f : files) {
            //At this point we know that all files are named like 123.xml
            //so no validation is necessary.
            bugIds.add(f.getName().split("\\.")[0]);
        }
        
        return bugIds;
    } 
    
    /** {@inheritDoc} */
    public List<URI> getSupportedURLSchemes() {
        return supportedSchemes;
    }
    
    /**
     * Reads a Bugzilla XML bug description from a file and 
     * returns a bug entry. 
     */
    protected BTSEntry processBug(File f) {
        SAXReader reader = new SAXReader(false);
        Document document = null;
        
        //Dummy entity resolver to avoid downloading the bugzilla DTD from 
        //the web on parsing a bug
        EntityResolver resolver = new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) {
                InputStream in = new ByteArrayInputStream("".getBytes());
                return new InputSource(in);
            }
        };
        
        reader.setValidation(false);
        reader.setEntityResolver(resolver);
        reader.setIncludeExternalDTDDeclarations(false);
        reader.setIncludeInternalDTDDeclarations(false);
        reader.setStripWhitespaceText(true);
        
        //Parse the file
        try {
            document = reader.read(new FileReader(f));
        } catch (FileNotFoundException fex) {
            logger.error("Cannot read file " + f.getAbsolutePath() + 
                    fex.toString());
            return null;
        } catch (DocumentException e) {
            logger.warn("Cannot parse bug report " + f.getAbsolutePath() 
                    + " " + e.getMessage());
            return null;
        }
        
        Element root = document.getRootElement();
        BTSEntry bug = new BTSEntry();
        bug.bugID = f.getName().split("\\.")[0];
        
        //Each bug file has just 1 bug element
        Element element = (Element) root.elementIterator("bug").next();

        //Must be reading some other XML
        if (element == null)
            return null;
        
        String elementValue;
            
        /* Read all the values we can and call the related setter */
        elementValue = getElementValueAsString(element.element("bug_severity"));
        bug.severity = BTSEntry.BugSeverity.fromString(elementValue);

        elementValue = getElementValueAsString(element.element("bug_status"));
        bug.state = BTSEntry.BugStatus.fromString(elementValue);

        elementValue = getElementValueAsString(element.element("creation_ts"));
        bug.creationTimestamp = parseDate(elementValue);
        
        
        elementValue = getElementValueAsString(element.element("delta_ts"));
        bug.latestUpdateTimestamp = parseDate(elementValue);

        elementValue = getElementValueAsString(element.element("priority"));
        bug.priority = BTSEntry.BugPriority.fromString(elementValue);

        elementValue = getElementValueAsString(element.element("resolution"));
        bug.resolution = BTSEntry.BugResolution.fromString(elementValue);
        
        elementValue = getElementValueAsString(element.element("product"));
        bug.product = elementValue;

        elementValue = getElementValueAsString(element.element("component"));
        bug.component = elementValue;

        elementValue = getElementValueAsString(element.element("reporter"));
        bug.reporter = elementValue;

        elementValue = getElementValueAsString(element.element("assignee"));
        bug.assignee = elementValue;
        
        Iterator<Element> i = element.elementIterator("long_desc");
        
        while (i.hasNext()) {
            Element comment = i.next();
            BTSEntryComment c =  bug.new BTSEntryComment();
            
            elementValue = getElementValueAsString(comment.element("who"));
            c.commentAuthor = elementValue;
            
            elementValue = getElementValueAsString(comment.element("bug_when"));
            c.commentTS = parseDate(elementValue);
            
            elementValue = getElementValueAsString(comment.element("thetext"));
            c.comment = elementValue;
            
            bug.commentslist.add(c);
        }
        
        i = element.elementIterator("attachement");
        
        while (i.hasNext()) {
            Element comment = i.next();
            BTSEntryAttachement a =  bug.new BTSEntryAttachement();
            
            elementValue = getElementValueAsString(comment.element("date"));
            a.date = parseDate(elementValue);
            
            elementValue = getElementValueAsString(comment.element("desc"));
            a.description = elementValue;
            
            elementValue = getElementValueAsString(comment.element("type"));
            a.type = elementValue;
        }
        
        return bug;
    }

    private Date parseDate(String date) {
        //Bugzilla stores dates as: 2003-11-07 14:35 UTC
        SimpleDateFormat dateParser1 = new SimpleDateFormat("y-M-d k:m z");
        //or as: 2003-11-07 14:35:22 UTC
        SimpleDateFormat dateParser2 = new SimpleDateFormat("y-M-d k:m:s z");
        Date d = null;
        try {
            d = dateParser1.parse(date);
        } catch (ParseException pex) {
            try {
                d = dateParser2.parse(date);
            } catch (ParseException e) {
                logger.warn("BugzillaXMLParser: Could not parse date string " 
                        + date);
            }
        }
        
        return d;
    }
    
    private String getElementValueAsString(Element element) {
        if (element != null) {
            return element.getStringValue();
        } else {
            return "";
        }
    }
    
    /**
     * Implements a file filter for directory listing operations.
     */
    private class BugzillaXMLFileFilter implements FileFilter  {

        private Long timestamp;
        private Pattern format;
        
        BugzillaXMLFileFilter(long timestamp) {
            this.timestamp = timestamp;
            format = Pattern.compile("^[0-9]+\\.xml$");
        }
        
        /** {@inheritDoc} */
        public boolean accept(File f) {
            if (f.lastModified() < timestamp)
                return false;
            
            if (!format.matcher(f.getName()).matches())
                return false;
            
            return true;
        }
    }
    
    public String getName() {
    	return "BugzillaXMLAccessor";
    }
}
