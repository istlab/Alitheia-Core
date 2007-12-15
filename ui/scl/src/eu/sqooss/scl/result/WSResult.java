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
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The WSResult is similar to the MetricResult. 
 */
public class WSResult implements Iterable<ArrayList<WSResultEntry>>,
                                          Iterator<ArrayList<WSResultEntry>> {
    
    private ArrayList<ArrayList<WSResultEntry>> wsResultTable;
    private int currentRow;
    
    private Object lockObject = new Object();

    public WSResult() {
        wsResultTable = new ArrayList<ArrayList<WSResultEntry>>();
        currentRow = -1;
    }

    public WSResult(String plainText) {
        this();
        ArrayList<WSResultEntry> row = new ArrayList<WSResultEntry>(1);
        row.add(new WSResultEntry(plainText, WSResultEntry.MIME_TYPE_TEXT_PLAIN));
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
        this.wsResultTable.add(result);
    }

    /**
     * Return the number of rows in the the MetricResult
     * @return The number of rows
     */
    public int getRowCount() {
        return wsResultTable.size();
    }

    public String toXML() {
        throw new UnsupportedOperationException("Coming soon");
    }

    public static WSResult fromXML(String xml) { 
        throw new UnsupportedOperationException("Coming soon");
    }
    
    public static boolean validate() {
        return false;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
