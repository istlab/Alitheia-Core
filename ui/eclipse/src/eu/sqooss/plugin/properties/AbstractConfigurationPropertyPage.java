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

package eu.sqooss.plugin.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

abstract class AbstractConfigurationPropertyPage extends PropertyPage {

    private static final int TEXT_FIELDS_SWT_STYLE = SWT.SINGLE | SWT.BORDER;
    
    protected Text textFieldServerUrl;
    protected Text textFieldUserName;
    protected Text textFieldPassword;
    protected Text textFieldProjectName;
    
    protected Button buttonValidate;
    
    public AbstractConfigurationPropertyPage() {
        super();
    }
    
    /**
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent) {
        Composite composite = createComposite(parent);
        addComponents(composite);
        return composite;
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#contributeButtons(org.eclipse.swt.widgets.Composite)
     */
    protected void contributeButtons(Composite parent) {
        super.contributeButtons(parent);
        ((GridLayout) parent.getLayout()).numColumns++;
        buttonValidate = new Button(parent, SWT.PUSH);
        buttonValidate.setText(PropertyPagesMessages.ConfigurationPropertyPage_Button_Validate);
    }

    private void addComponents(Composite composite) {
        
        // add server url's components
        Label labelServerUrl = new Label(composite, SWT.NONE);
        labelServerUrl.setText(PropertyPagesMessages.ConfigurationPropertyPage_Label_Server_Url);
        textFieldServerUrl = new Text(composite, TEXT_FIELDS_SWT_STYLE);
        setLayoutData(textFieldServerUrl);
        
        // add user name's components
        Label labelUserName = new Label(composite, SWT.NONE);
        labelUserName.setText(PropertyPagesMessages.ConfigurationPropertyPage_Label_User_Name);
        textFieldUserName = new Text(composite, TEXT_FIELDS_SWT_STYLE);
        setLayoutData(textFieldUserName);

        // add password's components
        Label labelPassword = new Label(composite, SWT.NONE);
        labelPassword.setText(PropertyPagesMessages.ConfigurationPropertyPage_Label_Password);
        textFieldPassword = new Text(composite, TEXT_FIELDS_SWT_STYLE | SWT.PASSWORD);
        setLayoutData(textFieldPassword);
        
        // add project's components
        Label labelProjectName = new Label(composite, SWT.NONE);
        labelProjectName.setText(PropertyPagesMessages.ConfigurationPropertyPage_Label_Project_Name);
        textFieldProjectName = new Text(composite, TEXT_FIELDS_SWT_STYLE);
        setLayoutData(textFieldProjectName);
    }
    
    private Composite createComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);

        return composite;
    }
    
    private void setLayoutData(Control control) {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        control.setLayoutData(gridData);
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
