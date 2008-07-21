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

package eu.sqooss.impl.plugin.properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import eu.sqooss.impl.plugin.util.visualizers.Visualizer;
import eu.sqooss.impl.plugin.util.visualizers.VisualizerFactory;
import eu.sqooss.impl.plugin.util.visualizers.VisualizerFactory.Type;
import eu.sqooss.plugin.util.ConnectionUtils;
import eu.sqooss.plugin.util.Constants;
import eu.sqooss.plugin.util.Entity;
import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSResultEntry;

public class QualityPropertyPage extends AbstractQualityPropertyPage implements SelectionListener, Listener {

    private Composite parent;
    private Entity entity;
    private Visualizer visualizer;
    private int lastSelectedMetricIndex;
    
    /**
     * @see eu.sqooss.plugin.properties.AbstractQualityPropertyPage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
        this.parent = parent;
        mainControl = (Composite) super.createContents(parent);
        buttonCompareVersion.addSelectionListener(this);
        comboCompareVersion.addSelectionListener(this);
        comboMetric.addSelectionListener(this);
        comboMetric.addListener(SWT.MouseDown, this);
        comboMetric.addListener(SWT.MouseUp, this);
        parent.forceFocus();
        enableIfPossible();
        processResult(false);
        return mainControl;
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        noDefaultAndApplyButton();
        super.createControl(parent);
    }

    /**
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetDefaultSelected(SelectionEvent e) {
        //do nothing
    }

    /**
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected(SelectionEvent e) {
        Object eventSource = e.getSource(); 
        if (eventSource == buttonCompareVersion) {
            boolean isEnabledComboCompare = comboCompareVersion.isEnabled();
            comboCompareVersion.setEnabled(!isEnabledComboCompare);
            comboCompareVersion.deselectAll();
            this.visualizer.close();
            this.visualizer = null;
            processResult(false);
        } else if (eventSource == configurationLink) {
            IWorkbenchPreferenceContainer container= (IWorkbenchPreferenceContainer)getContainer();
            container.openPage(Constants.CONFIGURATION_PROPERTY_PAGE_ID, null);
        } else if (eventSource == comboMetric) {
            comboCompareVersion.deselectAll();
            processResult(comboMetric.getSelectionIndex() == lastSelectedMetricIndex);
        } else if (eventSource == comboCompareVersion) {
            processResult(false);
        }
    }
    
    /**
     * @see eu.sqooss.impl.plugin.properties.EnabledPropertyPage#setEnabled(boolean, java.lang.String)
     */
    public void setEnabled(boolean isEnabled, String errorMessage) {
        if (mainControl == null) return; //the method createContents isn't called yet
        super.setEnabled(isEnabled, errorMessage);
        if (isEnabled) {
            String internalErrorMessage = fill(); 
            if (internalErrorMessage != null) {
                setEnabled(false, internalErrorMessage);
                return;
            }
            if (configurationLink != null) {
                //remove the configuration link
                configurationLink.dispose();
                configurationLink = null;
                parent.layout();
            }
            processResult(false);
        }else {
            if (configurationLink == null) {
                //add configuration link
                configurationLink = new Link(parent, SWT.NONE);
                configurationLink.setText(PropertyPagesMessages.ProjectPropertyPage_Link_Configuration);
                configurationLink.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
                configurationLink.addSelectionListener(this);
                parent.layout();
            }
        }
    }
    
    /*
     * The method fills the GUI with the information of the entity.
     */
    private String fill() {
        if (!initEntity()) {
            return PropertyPagesMessages.
            QualityPropertyPage_Message_Error_Missing_Entity;
        }
        textFieldEntityPath.setText(this.entity.getName());
        WSMetric[] metrics = this.entity.getMetrics();
        if ((metrics == null) || (metrics.length == 0)) {
            return PropertyPagesMessages
            .QualityPropertyPage_Message_Error_Missing_Metrics;
        }
        comboMetric.removeAll();
        comboCompareVersion.removeAll();
        WSMetric currentMetric;
        String currentItem;
        for (int i = 0; i < metrics.length; i++) {
            currentMetric = metrics[i];
            currentItem = currentMetric.getMnemonic() + " (" +
            currentMetric.getDescription() + ")";
            comboMetric.add(currentItem, i);
            comboMetric.setData(Integer.toString(i), currentMetric);
        }
        Long[] versions = this.entity.getVersions();
        if (versions != null) {
            Long currentVersion;
            for (int i = 0; i < versions.length; i++) {
                currentVersion = versions[i];
                currentItem  = "ver. " + currentVersion;
                comboCompareVersion.add(currentItem, i);
                comboCompareVersion.setData(Integer.toString(i), currentVersion);
            }
        }
        comboMetric.select(0);
        return null;
    }
    
    /*
     * The method sets the resource entity.
     */
    private boolean initEntity() {
        IResource resource = (IResource) (getElement().getAdapter(IResource.class));
        ConnectionUtils connectionUtils;
        try {
            connectionUtils = (ConnectionUtils) resource.getProject().
            getSessionProperty(ConnectionUtils.PROPERTY_CONNECTION_UTILS);
        } catch (CoreException e) {
            connectionUtils = null;
        }
        if ((connectionUtils == null) ||
                (!connectionUtils.isValidProjectVersion())) {
            return false;
        } else {
            this.entity = connectionUtils.getEntity(
                    resource.getFullPath().toString());
            return (this.entity == null) ? false : true;
        }
    }
    
    /*
     * The method is used by the result visualization.
     */
    private void processResult(boolean clearResult) {
        if (controlEnableState != null) return; //the control is disabled
        setVisualizer();
        int selectedIndex = comboCompareVersion.getSelectionIndex();
        Long selectedVersion = (selectedIndex == -1) ?
                this.entity.getCurrentVersion() :
                    (Long) comboCompareVersion.getData(Integer.toString(selectedIndex));
        String metricKey = Integer.toString(comboMetric.getSelectionIndex());
        WSMetric metric = (WSMetric) comboMetric.getData(metricKey);
        if (clearResult) {
            this.visualizer.removeMetricValues(metric.getMnemonic());
        } else {
            WSResultEntry[] result = this.entity.getMetricsResults(new WSMetric[] {metric},
                    selectedVersion);
            this.visualizer.setValue(selectedVersion, result);
            this.visualizer.open();
        }
    }
    
    private void setVisualizer() {
        if (this.visualizer == null) {
            Type type = (comboCompareVersion.isEnabled()) ?
                    Type.CHART_LINE_SERIES : Type.TABLE;
            this.visualizer = VisualizerFactory.createVisualizer(
                    type, resultComposite, null, null);
        }
    }

    public void handleEvent(Event event) {
        Widget eventSource = event.widget;
        if (eventSource == comboMetric) {
            lastSelectedMetricIndex = comboMetric.getSelectionIndex();
            comboMetric.deselect(lastSelectedMetricIndex);
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
