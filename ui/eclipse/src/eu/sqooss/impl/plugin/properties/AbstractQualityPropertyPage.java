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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

class AbstractQualityPropertyPage extends EnabledPropertyPage {

    protected Text textFieldEntityPath;
    protected Combo comboMetric;
    protected Combo comboCompareVersion;
    protected Button buttonCompareVersion;
    protected Link configurationLink;
    protected Composite resultComposite;
    
    protected Control createContents(Composite parent) {
        GridData gridData;
        
        Composite mainComposite = createComposite(parent, SWT.NULL, 2);
        gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        mainComposite.setLayoutData(gridData);
        
        addFirstSection(mainComposite);
        addSeparator(mainComposite);
        addSecondSection(mainComposite);
        
        return mainComposite;
    }

    private void addFirstSection(Composite parent) {
        Label labelPath = new Label(parent, SWT.NONE);
        labelPath.setText(PropertyPagesMessages.QualityPropertyPage_Label_Entity_Path);
        
        textFieldEntityPath = new Text(parent, SWT.WRAP | SWT.READ_ONLY);
        setLayoutData(textFieldEntityPath, 1, true, false);
    }
    
    private void addSeparator(Composite parent) {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        setLayoutData(separator, 2, true, false);
    }
    
    private void addSecondSection(Composite parent) {
        Composite secondSectionComposite = createComposite(parent, SWT.NULL, 3);
        setLayoutData(secondSectionComposite, 2, true, true);
        
        //add metric's components
        Label labelMetric = new Label(secondSectionComposite, SWT.NONE);
        labelMetric.setText(PropertyPagesMessages.QualityPropertyPage_Label_Metric);
        comboMetric = new Combo(secondSectionComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
        setLayoutData(comboMetric, 2, true, false);
        
        //add comparison's components
        buttonCompareVersion = new Button(secondSectionComposite, SWT.CHECK);
        buttonCompareVersion.setText(PropertyPagesMessages.QualityPropertyPage_Button_Compare);
        setLayoutData(buttonCompareVersion, 2, false, false);
        comboCompareVersion = new Combo(secondSectionComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
        setLayoutData(comboCompareVersion, 1, true, false);
        comboCompareVersion.setEnabled(false);
        
        //add result's area
        resultComposite = createComposite(secondSectionComposite, SWT.BORDER, 1);
        resultComposite.setLayout(new FillLayout());
        setLayoutData(resultComposite, 3, true, true);
    }
    
    private Composite createComposite(Composite parent, int style, int columnsNumber) {
        Composite composite = new Composite(parent, style);
        GridLayout layout = new GridLayout();
        layout.numColumns = columnsNumber;
        composite.setLayout(layout);

        return composite;
    }
    
    private void setLayoutData(Control control, int horizontalSpan,
            boolean grabExcessHorizontalSpace, boolean grabExcessVerticalSpace) {
        GridData gridData = new GridData();
        gridData.horizontalSpan = horizontalSpan;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = grabExcessVerticalSpace;
        control.setLayoutData(gridData);
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
