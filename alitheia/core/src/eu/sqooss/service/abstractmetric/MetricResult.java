/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 Georgios Gousios <gousiosg@aueb.gr>
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

package eu.sqooss.service.abstractmetric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * A generic container encapsulating metric results. 
 * 
 * Metric results can be everything: from a simple integer value to an array 
 * of png images. For this reason, we mimic the JDBC {@link java.sql.ResultSet}
 * by storing the results in rows and columns. Each row contains the metric 
 * results for one run of the metric on the project entity the metric supports.
 * Each column contains a specific measurement result. Please note that 
 * <it>each line can have a different number of fields</it>
 * 
 * The MetricResult class follows the JDBC cursor paradigm to enable
 * browsing through the results. The results are also Iterable 
 * (per row) and therefore the foreach statement can be used the MetricResult 
 * object.  
 * 
 * The result object can be serialized to XML for transfering over SOAP to 
 * the GUIs. A static deserializer is also provided to return a concrete 
 * object from an XML description. An XML Schema description of the XML is
 * also provided for clients that need to validate the serialized XML.   
 *
 */
public class MetricResult implements Iterable<ArrayList<MetricResultEntry>>,
        Iterator<ArrayList<MetricResultEntry>> {

    private ArrayList<ArrayList<MetricResultEntry>> result;

    int line;

    int field;

    public MetricResult() {
        result = new ArrayList<ArrayList<MetricResultEntry>>();
    }

    public void first() {
        line = 1;
    }

    public void last() {
        line = result.size();
    }

    public boolean hasNext() {
        if (line <= result.size()) {
            return true;
        }

        return false;
    }

    /**
     * Return and Iterator for accessing the lines of the object
     */
    public Iterator<ArrayList<MetricResultEntry>> iterator() {
        return this;
    }

    public ArrayList<MetricResultEntry> next() {
        if (hasNext()) {
            line++;
            return result.get(line);
        } else {
            return result.get(line);
        }
    }

    /**
     * Returns the current row.
     * @return The row at the current position of the cursor
     */
    public ArrayList<MetricResultEntry> get() {
        if (line <= result.size()) {
            return result.get(line);
        } else {
            return result.get(result.size());
        }
    }

    /**
     * Returns the specified row
     * @param i index of row to return
     * @return The specified row or null if the row index is out of range 
     */
    public ArrayList<MetricResultEntry> getRow(int i) {
        if (i > 0 && i <= result.size()) {
            return result.get(i);
        } else {
            return null;
        }
    }

    /**
     * Returns the specified result table cell
     * @param x The result table row
     * @param y The result table column
     * @return The requested field or null if <tt>x</tt> or <tt>y</tt> is out 
     * of range 
     */
    public MetricResultEntry getFieldAt(int x, int y) {
        ArrayList<MetricResultEntry> line = getRow(x);
        if (line == null) {
            return null;
        }

        if (y > line.size()) {
            return null;
        }

        return line.get(y);
    }

    /**
     * Adds a result row to the results table
     * @param result The result row to add
     */
    public void addResultRow(ArrayList<MetricResultEntry> result) {
        this.result.add(result);
    }

    /**
     * Return the number of rows in the the MetricResult
     * @return The number of rows
     */
    public int numRows() {
        return result.size();
    }

    /**
     * Serialise the object to XML
     * @return An XML representation of the object
     */
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<metricresult>\n");
        for (ArrayList<MetricResultEntry> i : this) {
            sb.append("\t<r>\n");
            for (MetricResultEntry r : i) {
                sb.append("\t\t<c>\n");
                r.toXML();
                sb.append("\t\t</c>\n");
            }
            sb.append("\t</r>\n");
        }

        sb.append("</metricresult>");

        return sb.toString();
    }

    /**
     * Static deserialiser 
     * @param xml An MetricResult object serialised to XML
     * @return A Metric Result object or null if and error in the 
     * deserialisation occurs
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     */
    public static MetricResult fromXML(String xml) throws 
        ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbfactory.newDocumentBuilder();
        Document doc = builder.parse(xml);
        
        NodeList nl = doc.getElementsByTagName("metricresult").item(0).getChildNodes();
        
        for(int i = 0; i < nl.getLength(); i++) {
            nl.item(i).getNodeType();
        }
        //TODO: Continue from there tommorrow
        return null;

    }

    public void remove() {

    }    
}
