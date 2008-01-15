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

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class has a package visibility. 
 */
class SAXDefaultHandler extends DefaultHandler {
    
    private String rowElemName;
    private String fieldElemName;
    private String mimeTypeElemName;
    private String valueElemName;
    
    private String currentElemName;
    private StringBuffer currentMimeTypeElemVal;
    private StringBuffer currentValueElemVal;
    
    private WSResult tableResult;
    private ArrayList<WSResultEntry> currentRow;

    public SAXDefaultHandler(String rowElemName, String fieldElemName,
            String mimeTypeElemName, String valueElemName) {
        this.rowElemName = rowElemName;
        this.fieldElemName = fieldElemName;
        this.mimeTypeElemName = mimeTypeElemName;
        this.valueElemName = valueElemName;
        
        currentMimeTypeElemVal = new StringBuffer();
        currentValueElemVal = new StringBuffer();
    }
    
    /**
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        tableResult = new WSResult();
    }

    /**
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);
        
        if (rowElemName.equals(name)) {
            currentRow = new ArrayList<WSResultEntry>();
        }
        currentElemName = name;
    }
    
    /**
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        
        if (mimeTypeElemName.equals(currentElemName)) {
            currentMimeTypeElemVal.append(new String(ch, start, length).trim());
        } else if (valueElemName.equals(currentElemName)) {
            currentValueElemVal.append(new String(ch, start, length).trim());
        }
    }
    
    /**
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        super.endElement(uri, localName, name);
        
        if (rowElemName.equals(name)) {
            tableResult.addResultRow(currentRow);
            currentRow = null;
        } else if ((fieldElemName.equals(name)) && (currentRow != null)) {
            String mimeType = currentMimeTypeElemVal.toString();
            currentRow.add(WSResultEntry.fromString(currentValueElemVal.toString(), mimeType));
            currentValueElemVal.setLength(0);
            currentMimeTypeElemVal.setLength(0);
        }
    }

    /**
     * This method returns the last parsed WSResult or null if there isn't.
     * 
     * @return
     */
    public WSResult getParsedWSResult() {
        return tableResult;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
