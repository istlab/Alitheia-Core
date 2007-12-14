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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A metric result entry. Tries to be as generic as possible by storing
 * data to the least common denominator of containers, the byte array.
 * Each entry has an assosiated mime type. Mime types are used by the
 * users of the object to convert the byte array store to something 
 * they can use. Some convenience conversion functions are provided
 * with this object.
 * 
 *  Supported mime types include:
 *  <ul>
 *      <li>type/{integer, float, double}</li>
 *      <li>text/{plain, html, xml}</li>
 *      <li>image/{gif, png, jpeg}</li>
 *  </ul>
 * 
 */
public class MetricResultEntry {

    private byte[] b;

    private String mimeType;

    /**
     * Creates a new MetricResultEntry object
     * 
     * @param value - An object encapsulating the value to be returned
     * @param mime - The mime type of the result value
     * @throws IOException - When serialization of the passed object fails
     */
    public MetricResultEntry(Object value, String mime) throws IOException {

        mimeType = mime;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(value);
        oout.flush();
        b = bout.toByteArray();
    }

    public String toString() {
        return new String(b);
    }

    public byte[] getByteArray() {
        return b;
    }
    
    public String getMime() {
        return mimeType;
    }

    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("\t\t\t\t<mre>");
        sb.append("\t\t\t\t<d>");
        sb.append(Base64.encodeBase64(b));
        sb.append("\t\t\t\t</d>\n");
        sb.append("\t\t\t\t<m>");
        sb.append(mimeType);
        sb.append("\t\t\t\t</m>\n");
        sb.append("\t\t\t</mre>\n");
        return null;
    }
    
    /**
     * Creates a MetricResultEntry object from an XML description. The XML 
     * should look like this:
     * <code>
     *  <mre>
     *    <d>Base64 encoded data (String)</d>
     *    <m>Mime Type (String)</m>
     *  </mre>
     * </code>
     * @param xml 
     * @return An instantiated object or null if the provided XML has errors
     * @throws ParserConfigurationException - Error in XML parser configuration
     * @throws IOException -  When the passed string is null (?)
     * @throws SAXException - When there is some problem with the processed XML
     */
    public static MetricResultEntry fromXML(String xml) throws 
        ParserConfigurationException, SAXException, IOException {
        
        MetricResultEntry m = null;

        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbfactory.newDocumentBuilder();
        Document doc = builder.parse(xml);

        String data = doc.getElementsByTagName("d").item(0).getNodeValue();
        String mime = doc.getElementsByTagName("m").item(0).getNodeValue();

        m = new MetricResultEntry(Base64.decodeBase64(data.getBytes()), mime);
        
        return m;
    }
}
