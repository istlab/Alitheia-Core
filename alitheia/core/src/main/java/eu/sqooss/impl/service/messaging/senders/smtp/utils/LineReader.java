/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.impl.service.messaging.senders.smtp.utils;

import java.io.InputStream;
import java.io.IOException;

/**
 * Class used to read lines from InputStream.
 */
public class LineReader {
    private InputStream is = null;
    private static char buffered = '\0';
    private boolean wasCRLF = false;


    /**
     * Constructs a new LineReader on the InputStream is
     *
     * @param   is  the InputStream to read lines from.
     */
    public LineReader (InputStream is) {
        this.is = is;
    }


    /**
     * Answers a String representing the next line of text available.
     * A line is represented by 0 or more characters followed by
     * '\n', '\r', "\r\n" or end of stream. The String does not include
     * the newline sequence.
     *
     * @return     the contents of the line or null if no characters were read.
     * @exception   IOException  if some IO error occurs.
     */
    public String readLine() throws IOException {
        StringBuffer buff = new StringBuffer();
        wasCRLF = false;
        if (buffered != '\0') {
            buff.append(buffered);
            buffered = '\0'; 
        }
        if (is != null) {
            int temp;    
            while ((temp = is.read()) != -1){
                if (temp == '\r') {
                    if ((temp = is.read()) != '\n') {
                        buffered = (char) temp;
                    } else {
                        wasCRLF = true;
                    }
//                  System.out.println("1> "+buff.toString());
                    return buff.toString();
                }
                if (temp == '\n' ) {
                    buffered = '\0';
                    return buff.toString();        
                }      
                buff.append((char) temp);
            }
            buffered = '\0';
        }
        return "";
    }

    /**
     * Close the InputStream.given in the constructor
     *
     * @exception   IOException If an error occurs attempting to close the InputStream
     */
    public void close() throws IOException {
        if (is != null) is.close();
    }

    public boolean wasCRLF() {
        return wasCRLF;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
