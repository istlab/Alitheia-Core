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

import org.eclipse.swt.widgets.Composite;

import eu.sqooss.impl.plugin.util.Messages;

/**
 * The class is used as a factory for the visualizers.
 * The <code>enum VisualizerFactory.Type</code> specifies the different visualizers. 
 */
public final class VisualizerFactory {
    
    private VisualizerFactory() { /* hides the constructor */ }
    
    /**
     * Specifies the different visualizers.
     * Currently they are:
     * <ul>
     *  <li><code>Type.TABLE</code> - represents the results in the table</li>
     *  <li><code>Type.CHART_LINE_SERIES</code></li> - represents the results with the line chart</li>
     * </ul>
     */
    public enum Type {
        TABLE,
        CHART_LINE_SERIES;
    }
    
    /**
     * The method creates a new visualizer.
     * The visualizer is specified from the type argument. 
     *  
     * @param type - specifies the visualizer type
     * @param parent - specifies the root composite 
     * @param titleVersion - represents the version title;
     * <code>PropertyPagesMessages.VisualizerFactory_Title_Version</code> is used in case of null
     * @param titleResult - represents the result title;
     * <code>PropertyPagesMessages.VisualizerFactory_Title_Result</code> is used in case of null
     * 
     * @return the concrete visualizer or null if the type is unknown
     */
    public static AbstractVisualizer createVisualizer(
            Type type,           //the type of the visualizer
            Composite parent,    //the parent composite
            String titleVersion, //can be a column title or axis title
            String titleResult   //can be a column title or axis title
            ) {
        
        titleVersion = (titleVersion != null) ? titleVersion :
            Messages.VisualizerFactory_Title_Version;
        
        titleResult = (titleResult != null) ? titleResult :
            Messages.VisualizerFactory_Title_Result;
        
        switch (type) {
        case TABLE             : return new TableVisualizer(parent,
                titleVersion, titleResult);
        case CHART_LINE_SERIES : return new LineChartVisualizer(parent,
                titleVersion, titleResult);
        default                : return null;
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
