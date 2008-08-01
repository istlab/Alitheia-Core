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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PropertyPage;

import eu.sqooss.impl.plugin.util.Messages;

abstract class AbstractProjectPropertyPage extends PropertyPage {

    protected Link linkConfigurationPropertyPage;
    protected Link linkProfilePropertyPage;
    protected Link linkQualityPropertyPage;
    
    protected Control createContents(Composite parent) {
        GridData gridData;
        
        Composite composite  = createComposite(parent);
        gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        composite.setLayoutData(gridData);
        
        //add caption
        Label labelCaption = new Label(composite, SWT.NONE);
        labelCaption.setText(Messages.ProjectPropertyPage_Label_Caption);
        setLayoutData(labelCaption);
     
        addSeparator(composite);
        
        //add configuration link
        linkConfigurationPropertyPage = new Link(composite, SWT.NONE);
        linkConfigurationPropertyPage.setText(Messages.ProjectPropertyPage_Link_Configuration);
        setLayoutData(linkConfigurationPropertyPage);
        
        //add profile link
        linkProfilePropertyPage = new Link(composite, SWT.NONE);
        linkProfilePropertyPage.setText(Messages.ProjectPropertyPage_Link_Profile);
        setLayoutData(linkProfilePropertyPage);
        
        //add quality link
        linkQualityPropertyPage = new Link(composite, SWT.NONE);
        linkQualityPropertyPage.setText(Messages.ProjectPropertyPage_Link_Quality);
        setLayoutData(linkQualityPropertyPage);
        
        return composite;
    }

    private void addSeparator(Composite parent) {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        setLayoutData(separator);
    }
    
    private Composite createComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout);

        return composite;
    }

    private void setLayoutData(Control control) {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        control.setLayoutData(gridData);
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
