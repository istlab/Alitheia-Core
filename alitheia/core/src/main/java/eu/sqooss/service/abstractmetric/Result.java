/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.service.abstractmetric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * A generic result object used both by metrics and by web services
 * 
 * <p>
 * Projects:
 * The <code>Result</code> object rows consist of fields in the following format:
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
 * The <code>Result</code>'s rows consist of fields in the following format:
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
 * The <code>Result</code>'s rows consist of fields in the following format:
 * <p>
 * <table border=1>
 *  <tr><td> Field Index </td><td> Field Type </td><td> Field value </td></tr>
 *  <tr><td>  0 </td><td> text/plain   </td><td> name        </td></tr>
 *  <tr><td>  1 </td><td> text/plain   </td><td> status      </td></tr>
 *  <tr><td>  2 </td><td> text/plain   </td><td> protection  </td></tr>
 *  <tr><td>  3 </td><td> type/integer </td><td> links       </td></tr>
 *  <tr><td>  4 </td><td> type/long    </td><td> user's id   </td></tr>
 *  <tr><td>  5 </td><td> type/long    </td><td> group's id  </td></tr>
 *  <tr><td>  6 </td><td> type/long    </td><td> access time </td></tr>
 *  <tr><td>  7 </td><td> type/long    </td><td> modification time </td></tr>
 *  <tr><td>  8 </td><td> text/plain   </td><td> file status change </td></tr>
 *  <tr><td>  9 </td><td> type/integer </td><td> size        </td></tr>
 *  <tr><td> 10 </td><td> type/integer </td><td> blocks      </td></tr>
 * </table>
 * </p><br>
 * </p>
 * 
 * <p>
 * Users:
 * The <code>Result</code>'s rows consist of fields in the following format:
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
public class Result implements Iterable<List<ResultEntry>>,
                                          Iterator<List<ResultEntry>> {

    private ArrayList<List<ResultEntry>> ResultTable;
    private int currentRow;

    private Object lockObject = new Object();

    public Result() {
        ResultTable = new ArrayList<List<ResultEntry>>();
        currentRow = -1;
    }

    /* Iterator's methods */
    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        synchronized (lockObject) {
            if (currentRow < (ResultTable.size()-1)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * @see java.util.Iterator#next()
     */
    public List<ResultEntry> next() {
        synchronized (lockObject) {
            if (hasNext()) {
                currentRow++;
                return ResultTable.get(currentRow);
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
    public Iterator<List<ResultEntry>> iterator() {
        return this;
    }
    /* Iterable's method */
    
    /**
     * This method is useful to the iteration process.
     * It moves the cursor before first row in the <code>Result</code>.
     */
    public void beforeFirst() {
        synchronized (lockObject) {
            currentRow = -1;
        }
    }
    
    /**
     * This method moves the cursor to the first row in the <code>Result</code>.
     * If the <code>Result</code> is empty then the method is similar to the {@link #beforeFirst()}
     */
    public void first() {
        synchronized (lockObject) {
            if (ResultTable.isEmpty()) {
                beforeFirst();
            } else {
                currentRow = 0;
            }
        }
    }

    /**
     * This method moves the cursor to the last row in the <code>Result</code>.
     * If the <code>Result</code> is empty the the method is similar to the {@link #beforeFirst()}
     */
    public void last() {
        synchronized (lockObject) {
            currentRow = ResultTable.size()-1;
        }
    }
    
    /**
     * Returns the current row.
     * @return The row at the current position of the cursor.
     * @throws IllegalStateException if the Result is empty.
     */
    public List<ResultEntry> get() {
        synchronized (lockObject) {
            if (currentRow == -1) {
                throw new IllegalStateException("Can't get the row with index -1!");
            } else {
                return ResultTable.get(currentRow);
            }
        }
    }

    /**
     * Returns the specified row
     * @param i index of row to return
     * @return The specified row
     * @throws IndexOutOfBoundsException if the row index is out of range
     */
    public List<ResultEntry> getRow(int i) {
        return ResultTable.get(i);
    }

    /**
     * Returns the specified result table cell
     * @param x The result table row
     * @param y The result table column
     * @return The requested field
     * @throws IndexOutOfBoundsException if <tt>x</tt> or <tt>y</tt> is out 
     * of range 
     */
    public ResultEntry getFieldAt(int x, int y) {
        List<ResultEntry> line = getRow(x);
        return line.get(y);
    }

    /**
     * Appends a result row to the end of results table
     * @param result The result row to add
     */
    public void addResultRow(List<ResultEntry> result) {
        synchronized (lockObject) {
            this.ResultTable.add(result);
        }
    }

    /**
     * Return the number of rows in the the MetricResult
     * @return The number of rows
     */
    public int getRowCount() {
        return ResultTable.size();
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
