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

package eu.sqooss.impl.plugin.util.visualizers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import eu.sqooss.impl.plugin.properties.PropertyPagesMessages;
import eu.sqooss.ws.client.datatypes.WSResultEntry;

/**
 * The class represents the quality results in the table.
 * This class has package visibility.
 */
class TableVisualizer extends AbstractVisualizer {
    
    private static final int TABLE_COLUMN_METRIC_MNEMONIC_INDEX = 0;
    private static final int TABLE_COLUMN_VERSION_INDEX         = 1;
    private static final int TABLE_COLUMN_RESULT_INDEX          = 2;
    
    private Composite internalComposite;
    private Table tableData;
    private TableColumn tableColumnMetricMnemonic;
    private TableColumn tableColumnVersion;
    private TableColumn tableColumnResult;
    private List<TableItem> tableItems;

    public TableVisualizer(Composite parent, String titleVersion, String titleResult) {
        super(parent, titleVersion, titleResult);
        tableItems = new ArrayList<TableItem>();
    }
    
    /**
     * @see eu.sqooss.impl.plugin.util.visualizers.Visualizer#open()
     */
    public void open() {
        if (internalComposite != null) return;
        init();
        loadData(null);
        parent.layout();
    }
    
    /**
     * @see eu.sqooss.impl.plugin.util.visualizers.Visualizer#close()
     */
    public void close() {
        if (internalComposite == null) return;
        tableData = null;
        internalComposite.dispose();
        internalComposite = null;
        parent.layout();
    }
    
    private void init() {
        //init composite
        internalComposite = new Composite(parent, SWT.NONE);
        internalComposite.setLayout(new FillLayout());
        
        //init data table
        tableData = new Table(internalComposite, SWT.SINGLE | SWT.FULL_SELECTION);
        tableData.setHeaderVisible(true);
        tableData.setLinesVisible(true);
        
        tableColumnMetricMnemonic = new TableColumn(tableData, SWT.CENTER,
                TABLE_COLUMN_METRIC_MNEMONIC_INDEX);
        tableColumnMetricMnemonic.setText(PropertyPagesMessages.
                TableVisualizer_Title_Metric);
        tableColumnMetricMnemonic.pack();
        
        tableColumnVersion = new TableColumn(tableData, SWT.CENTER,
                TABLE_COLUMN_VERSION_INDEX);
        tableColumnVersion.setText(titleVersion);
        tableColumnVersion.pack();
        
        tableColumnResult = new TableColumn(tableData, SWT.CENTER,
                TABLE_COLUMN_RESULT_INDEX);
        tableColumnResult.setText(titleResult);
        tableColumnResult.pack();
    }
    
    /**
     * @see eu.sqooss.impl.plugin.util.visualizers.AbstractVisualizer#loadData(java.lang.Long)
     */
    protected void loadData(Long version) {
        if (tableData == null) return;
        tableData.clearAll();
        for (TableItem currentItem : tableItems) {
            currentItem.dispose();
        }
        tableItems.clear();
        Map<Long, List<WSResultEntry>> data = new Hashtable<Long, List<WSResultEntry>>();
        if (version != null) {
            data.put(version, this.values.get(version));
        } else {
            data = this.values;
        }
        Iterator<Long> keysIterator = data.keySet().iterator();
        Long currentKey;
        List<WSResultEntry> currentData;
        while (keysIterator.hasNext()) {
            currentKey = keysIterator.next();
            currentData = data.get(currentKey);
            for (WSResultEntry currentEntry : currentData) {
                TableItem newTableItem  = new TableItem(tableData, SWT.NONE);
                newTableItem.setText(TABLE_COLUMN_METRIC_MNEMONIC_INDEX,
                        currentEntry.getMnemonic());
                newTableItem.setText(TABLE_COLUMN_VERSION_INDEX,
                        currentKey.toString());
                newTableItem.setText(TABLE_COLUMN_RESULT_INDEX,
                        currentEntry.getResult());
                tableItems.add(newTableItem);
            }
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
