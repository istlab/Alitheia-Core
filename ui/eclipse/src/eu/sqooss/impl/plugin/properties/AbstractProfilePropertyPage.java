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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

abstract class AbstractProfilePropertyPage extends EnabledPropertyPage {

    private static final int TEXT_FIELDS_SWT_STYLE = SWT.SINGLE | SWT.BORDER;
    
    protected Composite mainComposite;
    protected Composite configurationComposite;
    protected Combo comboProfileName;
    protected Combo comboProjectVersion;
    protected Button buttonRemoveProfile;
    protected Button buttonUpdateProfile;
    protected Button buttonPathBrowse;
    protected Text textFieldPath;
    protected Text textFieldFilesFilter;
    protected Text textFieldRecalcFreq;
    protected Link configurationLink;
    
    protected Control createContents(Composite parent) {
        GridData gridData;
        
        Composite containerComposite = createComposite(parent, 1);
        gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        containerComposite.setLayoutData(gridData);
        
        mainComposite = createComposite(containerComposite, 4);
        gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        mainComposite.setLayoutData(gridData);
        
        configurationComposite = createComposite(containerComposite, 1);
        gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
        configurationComposite.setLayoutData(gridData);
        
        addComponents(mainComposite);
        
        addConfigurationLink(configurationComposite);
        
        return containerComposite;
    }

    private void addComponents(Composite composite) {
        //add profile's components
        Label labelProfileName = new Label(composite, SWT.NONE);
        labelProfileName.setText(PropertyPagesMessages.ProfilePropertyPage_Label_Profile_Name);
        addLayoutData(labelProfileName, 4, false);
        
        comboProfileName = new Combo(composite, SWT.DROP_DOWN);
        addLayoutData(comboProfileName, 2, true);
        
        buttonRemoveProfile = new Button(composite, SWT.PUSH);
        buttonRemoveProfile.setText(PropertyPagesMessages.ProfilePropertyPage_Button_Remove_Profile);
        addLayoutData(buttonRemoveProfile, 1, false);
        
        buttonUpdateProfile = new Button(composite, SWT.PUSH);
        buttonUpdateProfile.setText(PropertyPagesMessages.ProfilePropertyPage_Button_Update_Profile);
        addLayoutData(buttonUpdateProfile, 1, false);
        
        //add profile's sub path 
        Label labelProfilePath = new Label(composite, SWT.NONE);
        labelProfilePath.setText(PropertyPagesMessages.ProfilePropertyPage_Label_Profile_Path);
        
        textFieldPath = new Text(composite, TEXT_FIELDS_SWT_STYLE);
        addLayoutData(textFieldPath, 2, true);
        
        buttonPathBrowse = new Button(composite, SWT.PUSH);
        buttonPathBrowse.setText(PropertyPagesMessages.ProfilePropertyPage_Button_Path_Browse);
        addLayoutData(buttonPathBrowse, 1, false);
        
        //add profile's files filter
        Label labelProfileFilesFilter = new Label(composite, SWT.NONE);
        labelProfileFilesFilter.setText(PropertyPagesMessages.ProfilePropertyPage_Label_Profile_Files_Filter);
        
        textFieldFilesFilter = new Text(composite, TEXT_FIELDS_SWT_STYLE);
        addLayoutData(textFieldFilesFilter, 3, true);
        
        //add profile's recalculation frequency
        Label labelProfileRecalcFreq = new Label(composite, SWT.NONE);
        labelProfileRecalcFreq.setText(PropertyPagesMessages.ProfilePropertyPage_Label_Recalc_Freq);
        
        textFieldRecalcFreq = new Text(composite, TEXT_FIELDS_SWT_STYLE);
        addLayoutData(textFieldRecalcFreq, 3, true);
        
        //add profile's project version
        Label labelProfileProjectVersion = new Label(composite, SWT.NONE);
        labelProfileProjectVersion.setText(PropertyPagesMessages.ProfilePropertyPage_Label_Project_Ver);
        
        comboProjectVersion = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        addLayoutData(comboProjectVersion, 3, true);
    }
    
    private Composite createComposite(Composite parent, int numColumns) {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = numColumns;
        composite.setLayout(layout);
        
        return composite;
    }
    
    private static void addLayoutData(Control control, int horizontalSpan,
            boolean grabExcessHorizontalSpace) {
        GridData gridData = new GridData();
        gridData.horizontalSpan = horizontalSpan;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
        control.setLayoutData(gridData);
    }
    
    private void addConfigurationLink(Composite parent) {
        configurationLink = new Link(parent, SWT.NONE);
        configurationLink.setText(PropertyPagesMessages.ProjectPropertyPage_Link_Configuration);
        addLayoutData(configurationLink, 1, true);
        configurationLink.setVisible(false);
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
