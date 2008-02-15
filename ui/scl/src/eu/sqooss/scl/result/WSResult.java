/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.scl.result;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import eu.sqooss.scl.WSException;

/**
 * The WSResult is similar to the MetricResult.
 * 
 * <p>
 * Projects:
 * The <code>WSResult</code>'s rows consist of fields in the following format:
     * <p>
     * <table border=1>
     *  <tr><td> Field Index </td><td> Field Type </td><td> Field value </td></tr>
     *  <tr><td> 0 </td><td> type/long    </td><td> id         </td></tr>
     *  <tr><td> 1 </td><td> text/plain   </td><td> name       </td></tr>
     *  <tr><td> 2 </td><td> text/plain   </td><td> repository </td></tr>
     *  <tr><td> 3 </td><td> text/plain   </td><td> bugs       </td></tr>
     *  <tr><td> 4 </td><td> text/plain   </td><td> mail       </td></tr>
     *  <tr><td> 5 </td><td> text/plain   </td><td> contact    </td></tr>
     *  <tr><td> 6 </td><td> text/plain   </td><td> website    </td></tr>
     *  <tr><td> 7 </td><td> type/integer </td><td> version1   </td></tr>
     *  <tr><td> 8 </td><td> type/integer </td><td> version2   </td></tr>
     *  <tr><td>...</td><td> type/integer </td><td> versionN   </td></tr>
     * <table>
     * </p><br>
 * </p>
 * 
 * <p>
 * Metrics:
 * The <code>WSResult</code>'s rows consist of fields in the following format:
 * <p>
 * <table border=1>
 *  <tr><td> Field Index </td><td> Field Type </td><td> Field value </td></tr>
 *  <tr><td> 0 </td><td> type/long    </td><td> id          </td></tr>
 *  <tr><td> 1 </td><td> text/plain   </td><td> description </td></tr>
 *  <tr><td> 2 </td><td> text/plain   </td><td> type        </td></tr>
 * <table>
 * </p><br>
 * </p>
 * 
 * <p>
 * Files:
 * The <code>WSResult</code>'s rows consist of fields in the following format:
 * <p>
 * <table border=1>
 *  <tr><td> Field Index </td><td> Field Type </td><td> Field value </td></tr>
 *  <tr><td>  0 </td><td> type/long    </td><td> file's id    </td></tr>
 *  <tr><td>  1 </td><td> text/plain   </td><td> name         </td></tr>
 *  <tr><td>  2 </td><td> text/plain   </td><td> status       </td></tr>
 *  <tr><td>  3 </td><td> type/long    </td><td> metadata's id</td></tr>
 *  <tr><td>  4 </td><td> text/plain   </td><td> protection   </td></tr>
 *  <tr><td>  5 </td><td> type/integer </td><td> links        </td></tr>
 *  <tr><td>  6 </td><td> type/long    </td><td> user's id    </td></tr>
 *  <tr><td>  7 </td><td> type/long    </td><td> group's id   </td></tr>
 *  <tr><td>  8 </td><td> type/long    </td><td> access time  </td></tr>
 *  <tr><td>  9 </td><td> type/long    </td><td> modification time </td></tr>
 *  <tr><td> 10 </td><td> text/plain   </td><td> file status change </td></tr>
 *  <tr><td> 11 </td><td> type/integer </td><td> size         </td></tr>
 *  <tr><td> 12 </td><td> type/integer </td><td> blocks       </td></tr>
 * </table>
 * </p><br>
 * </p>
 * 
 * <p>
 * Users:
 * The <code>WSResult</code>'s rows consist of fields in the following format:
 * <p>
 * <table border=1>
 *  <tr><td> Field Index </td><td> Field Type </td><td> Field value </td></tr>
 *  <tr><td> 0 </td><td> type/long    </td><td> user's id            </td></tr>
 *  <tr><td> 1 </td><td> text/plain   </td><td> user name            </td></tr>
 *  <tr><td> 2 </td><td> type/long    </td><td> group1's is          </td></tr>
 *  <tr><td> 3 </td><td> text/plain   </td><td> groups1' description </td></tr>
 *  <tr><td> 4 </td><td> type/long    </td><td> group2's is          </td></tr>
 *  <tr><td> 5 </td><td> text/plain   </td><td> groups2' description </td></tr>
 *  <tr><td>...</td><td> type/long    </td><td> groupN's is          </td></tr>
 *  <tr><td>...</td><td> text/plain   </td><td> groupsN' description </td></tr>
 * </table>
 * </p><br>
 * </p>
 * 
 */
public class WSResult implements Iterable<ArrayList<WSResultEntry>>,
                                          Iterator<ArrayList<WSResultEntry>> {
    
    private static final String XML_DECLARATION         = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    private static final String XML_ELEM_NAME_ROOT   = "WSResult";
    private static final String XML_ELEM_NAME_ROOT_ROW    = "Row";
    private static final String XML_ELEM_NAME_ROOT_ROW_FIELD  = "Field";
    private static final String XML_ELEM_NAME_ROOT_ROW_FIELD_MIME  = "MimeType";
    private static final String XML_ELEM_NAME_ROOT_ROW_FIELD_VALUE = "Value";
    
    private ArrayList<ArrayList<WSResultEntry>> wsResultTable;
    private int currentRow;

    private Object lockObject = new Object();

    private static SAXParser saxParser;
    private static SAXDefaultHandler saxHandler = new SAXDefaultHandler(XML_ELEM_NAME_ROOT_ROW,
            XML_ELEM_NAME_ROOT_ROW_FIELD, XML_ELEM_NAME_ROOT_ROW_FIELD_MIME,
            XML_ELEM_NAME_ROOT_ROW_FIELD_VALUE);
    
    public WSResult() {
        wsResultTable = new ArrayList<ArrayList<WSResultEntry>>();
        currentRow = -1;
    }

    public WSResult(String plainText) {
        this();
        if (plainText != null) {
            ArrayList<WSResultEntry> row = new ArrayList<WSResultEntry>(1);
            row.add(new WSResultEntry(plainText, WSResultEntry.MIME_TYPE_TEXT_PLAIN));
            addResultRow(row);
        }
    }
    
    public WSResult(long longValue) {
        this();
        ArrayList<WSResultEntry> row = new ArrayList<WSResultEntry>(1);
        row.add(new WSResultEntry(longValue, WSResultEntry.MIME_TYPE_TYPE_LONG));
        addResultRow(row);
    }
    
    /* Iterator's methods */
    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        synchronized (lockObject) {
            if (currentRow < (wsResultTable.size()-1)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * @see java.util.Iterator#next()
     */
    public ArrayList<WSResultEntry> next() {
        synchronized (lockObject) {
            if (hasNext()) {
                currentRow++;
                return wsResultTable.get(currentRow);
            } else {
                throw new NoSuchElementException("There are no more elements!");
            }
        }
    }

    /**
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException("The remove operation is unsupported!");
    } 
    /* Iterator's methods */
    
    /* Iterable's method */
    /**
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<ArrayList<WSResultEntry>> iterator() {
        return this;
    }
    /* Iterable's method */
    
    /**
     * This method is useful to the iteration process.
     * It moves the cursor before first row in the <code>WSResult</code>.
     */
    public void beforeFirst() {
        synchronized (lockObject) {
            currentRow = -1;
        }
    }
    
    /**
     * This method moves the cursor to the first row in the <code>WSResult</code>.
     * If the <code>WSResult</code> is empty then the method is similar to the {@link #beforeFirst()}
     */
    public void first() {
        synchronized (lockObject) {
            if (wsResultTable.isEmpty()) {
                beforeFirst();
            } else {
                currentRow = 0;
            }
        }
    }

    /**
     * This method moves the cursor to the last row in the <code>WSResult</code>.
     * If the <code>WSResult</code> is empty the the method is similar to the {@link #beforeFirst()}
     */
    public void last() {
        synchronized (lockObject) {
            currentRow = wsResultTable.size()-1;
        }
    }
    
    /**
     * Returns the current row.
     * @return The row at the current position of the cursor.
     * @throws IllegalStateException if the WSResult is empty.
     */
    public ArrayList<WSResultEntry> get() {
        synchronized (lockObject) {
            if (currentRow == -1) {
                throw new IllegalStateException("Can't get the row with index -1!");
            } else {
                return wsResultTable.get(currentRow);
            }
        }
    }

    /**
     * Returns the specified row
     * @param i index of row to return
     * @return The specified row
     * @throws IndexOutOfBoundsException if the row index is out of range
     */
    public ArrayList<WSResultEntry> getRow(int i) {
        return wsResultTable.get(i);
    }

    /**
     * Returns the specified result table cell
     * @param x The result table row
     * @param y The result table column
     * @return The requested field
     * @throws IndexOutOfBoundsException if <tt>x</tt> or <tt>y</tt> is out 
     * of range 
     */
    public WSResultEntry getFieldAt(int x, int y) {
        ArrayList<WSResultEntry> line = getRow(x);
        return line.get(y);
    }

    /**
     * Appends a result row to the end of results table
     * @param result The result row to add
     */
    public void addResultRow(ArrayList<WSResultEntry> result) {
        synchronized (lockObject) {
            this.wsResultTable.add(result);
        }
    }

    /**
     * Return the number of rows in the the MetricResult
     * @return The number of rows
     */
    public int getRowCount() {
        return wsResultTable.size();
    }

    /**
     * This method returns the XML representation of the <code>WSResult</code>.
     * 
     * @return - the string represents the WSResult in XML format
     */
    public String toXML() {
        String rootStartTag     = "<" + XML_ELEM_NAME_ROOT + ">\n";
        String rootEndTag       = "</" + XML_ELEM_NAME_ROOT + ">\n";;
        String rowStartTag      = "\t<" + XML_ELEM_NAME_ROOT_ROW + ">\n";
        String rowEndTag        = "\t</" + XML_ELEM_NAME_ROOT_ROW + ">\n";
        String fieldStartTag    = "\t\t<" + XML_ELEM_NAME_ROOT_ROW_FIELD + ">\n";
        String fieldEndTag      = "\t\t</" + XML_ELEM_NAME_ROOT_ROW_FIELD + ">\n";
        String mimeTypeStartTag = "\t\t\t<" + XML_ELEM_NAME_ROOT_ROW_FIELD_MIME + ">\n";
        String mimeTypeEndTag   = "\t\t\t</" + XML_ELEM_NAME_ROOT_ROW_FIELD_MIME + ">\n";
        String valueStartTag    = "\t\t\t<" + XML_ELEM_NAME_ROOT_ROW_FIELD_VALUE + ">\n";
        String valueEndTag      = "\t\t\t</" + XML_ELEM_NAME_ROOT_ROW_FIELD_VALUE + ">\n";
        String wsResultEntryIndentation = "\t\t\t\t";
        StringBuffer result   = new StringBuffer();
        result.append(XML_DECLARATION);
        result.append(rootStartTag);
        for (ArrayList<WSResultEntry> currentRow : wsResultTable) {
            result.append(rowStartTag);
            for (WSResultEntry currentField : currentRow) {
                result.append(fieldStartTag);
                
                result.append(mimeTypeStartTag);
                result.append(wsResultEntryIndentation);
                result.append(currentField.getMimeType());
                result.append('\n');
                result.append(mimeTypeEndTag);
                
                result.append(valueStartTag);
                result.append(wsResultEntryIndentation);
                result.append(currentField.toString());
                result.append('\n');
                result.append(valueEndTag);
                
                result.append(fieldEndTag);
            }
            result.append(rowEndTag);
        }
        result.append(rootEndTag);
        return result.toString();
    }

    /**
     * This method creates a new object from a XML description.
     * 
     * @param xml
     * @return
     * @throws WSException
     */
    public static WSResult fromXML(String xml) throws WSException { 
        try {
            if (saxParser == null) {
                SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                //TODO: set the xml schema file
                Schema wsResultSchema = schemaFactory.newSchema(new File("./WSResult.xsd"));
                saxParserFactory.setValidating(false);
                saxParserFactory.setSchema(wsResultSchema);
                saxParser = saxParserFactory.newSAXParser();
            }
            String xmlEncoding = getEncodingFromXMLDeclaration(xml);
            byte[] xmlInBytes;
            try {
                xmlInBytes = xml.getBytes(xmlEncoding);
            } catch (UnsupportedEncodingException uee) {
                xmlInBytes = xml.getBytes("UTF-8");
            }
            InputStream xmlInputStream = new ByteArrayInputStream(xmlInBytes);            
            saxParser.parse(xmlInputStream, saxHandler);
            return saxHandler.getParsedWSResult();
        } catch(Exception e) {
            throw new WSException(e);
        }
    }
    
    /**
     * This method returns the encoding from the XML's declaration.
     * If the encoding is missing then returns UTF-8.
     * @param xml
     * @return
     */
    private static String getEncodingFromXMLDeclaration(String xml) {
        String encoding = "UTF-8";
        StringBuffer xmlDeclaration = new StringBuffer(xml.substring(0, xml.indexOf('>') + 1));
        int encodingIndex = xmlDeclaration.indexOf("encoding");
        if (encodingIndex != -1) {
            xmlDeclaration.delete(0, encodingIndex);
            xmlDeclaration.delete(0, xmlDeclaration.indexOf("\"") + 1);
            encoding = xmlDeclaration.substring(0, xmlDeclaration.indexOf("\"")).trim();
        }
        return encoding;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
