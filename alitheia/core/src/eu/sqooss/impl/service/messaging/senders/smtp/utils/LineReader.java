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