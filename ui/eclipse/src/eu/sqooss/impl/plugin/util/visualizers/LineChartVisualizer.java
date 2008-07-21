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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import eu.sqooss.ws.client.datatypes.WSResultEntry;

/**
 * The class represents the quality results in the line chart.
 * This class has package visibility. 
 */
class LineChartVisualizer extends AbstractVisualizer {

    public LineChartVisualizer(Composite parent,
            String titleVersion, String titleResult) {
        super(parent, titleVersion, titleResult);
    }

    private ChartComposite chartComposite;
    private JFreeChart chart;
    private DefaultCategoryDataset dataset;
    
    /**
     * @see eu.sqooss.impl.plugin.util.visualizers.Visualizer#open()
     */
    public void open() {
        if (chartComposite != null) return;
        init();
        loadData(null);
        parent.layout();
    }
    
    /**
     * @see eu.sqooss.impl.plugin.util.visualizers.Visualizer#close()
     */
    public void close() {
        if (chartComposite != null) {
            chartComposite.dispose();
            chartComposite = null;
        }
        parent.layout();
    }

    /**
     * @see eu.sqooss.impl.plugin.util.visualizers.AbstractVisualizer#loadData(java.lang.Long)
     */
    @Override
    protected void loadData(Long version) {
        if (dataset == null) return;
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
                dataset.setValue(Double.valueOf(currentEntry.getResult()),
                        currentEntry.getMnemonic(), currentKey.toString());
            }
        }
    }

    private void init() {
        //create a dataset
        dataset = new DefaultCategoryDataset();
        //create a cahrt
        chart = ChartFactory.createLineChart(null, titleVersion, titleResult,
                dataset, PlotOrientation.VERTICAL, true, true, false);
        //add the shape render
        CategoryPlot plotter = (CategoryPlot) chart.getPlot();
        plotter.setRenderer(new LineAndShapeRenderer());
        chartComposite = new ChartComposite(
                parent,   //parent composite
                SWT.NONE, //set style
                chart,    //set JFreeCahrt
                ChartComposite.DEFAULT_WIDTH,
                ChartComposite.DEFAULT_HEIGHT,
                ChartComposite.DEFAULT_MINIMUM_DRAW_WIDTH,
                ChartComposite.DEFAULT_MINIMUM_DRAW_HEIGHT,
                ChartComposite.DEFAULT_MAXIMUM_DRAW_WIDTH,
                ChartComposite.DEFAULT_MAXIMUM_DRAW_HEIGHT,
                true,  //use buffer
                false, //no properties
                true,  //use save
                true,  //use print
                true,  //use zoom
                false //no tooltip
                );
        chartComposite.setLayout(new FillLayout());
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
