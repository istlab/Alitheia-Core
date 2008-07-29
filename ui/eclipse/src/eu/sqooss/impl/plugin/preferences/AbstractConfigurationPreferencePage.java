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

package eu.sqooss.impl.plugin.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPreferencePage;

import eu.sqooss.impl.plugin.util.Messages;
import eu.sqooss.impl.plugin.util.Constants;

abstract class AbstractConfigurationPreferencePage
                    extends PreferencePage
                    implements IWorkbenchPreferencePage {

    protected Text textFieldServerAddress;
    protected Text textFieldUserName;
    protected Text textFieldPassword;
    protected Spinner spinnerServerPort;
    
    @Override
    protected Control createContents(Composite parent) {
        //create main container
        Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(
                new GridData(GridData.FILL, GridData.FILL, true, true));
        mainComposite.setLayout(new GridLayout(1, true));
        
        addServerGroup(mainComposite);
        addUserGroup(mainComposite);
        return mainComposite;
    }
    
    private void addServerGroup(Composite container) {
        Group groupServer = new Group(container, SWT.NONE);
        setLayoutData(groupServer);
        groupServer.setLayout(new GridLayout(4, false));
        groupServer.setText(Messages.ConfigurationPreferencePage_Group_Server);
        
        Label labelServerAddress = new Label(groupServer, SWT.NONE);
        labelServerAddress.setText(Messages.Configuration_Label_Server_Address);
        
        textFieldServerAddress = new Text(groupServer,
                Constants.TEXT_FIELD_COMMON_STYLE);
        setLayoutData(textFieldServerAddress);
        
        Label labelServerPort = new Label(groupServer, SWT.NONE);
        labelServerPort.setText(Messages.Configuration_Label_Server_Port);
        
        spinnerServerPort = new Spinner(groupServer, SWT.NONE);
        spinnerServerPort.setValues(
                Constants.SERVER_PORT_DEFAULT_VALUE, //set selection
                Constants.SERVER_PORT_MIN, //set minimum
                Constants.SERVER_PORT_MAX, //set maximum
                0,                         //set digits
                1,                         //set increment
                1);                        //set page increment
        /*
         * The behavior of the spinner is not system independent.
         * Here are some related bugs:
         * https://bugs.eclipse.org/bugs/show_bug.cgi?id=186634
         * https://bugs.eclipse.org/bugs/show_bug.cgi?id=100363
         * They have fixes on the windows.
         */
    }
    
    private void addUserGroup(Composite container) {
        Group groupUser = new Group(container, SWT.NONE);
        setLayoutData(groupUser);
        groupUser.setLayout(new GridLayout(2, false));
        groupUser.setText(Messages.ConfigurationPreferencePage_Group_User);
        
        Label labelUserName = new Label(groupUser, SWT.NONE);
        labelUserName.setText(Messages.Configuration_Label_User_Name);
        
        textFieldUserName = new Text(groupUser,
                Constants.TEXT_FIELD_COMMON_STYLE);
        setLayoutData(textFieldUserName);
        
        Label labelPassword = new Label(groupUser, SWT.NONE);
        labelPassword.setText(Messages.Configuration_Label_Password);
        
        textFieldPassword = new Text(groupUser,
                Constants.TEXT_FIELD_COMMON_STYLE);
        setLayoutData(textFieldPassword);
    }
    
    private static void setLayoutData(Control control) {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        control.setLayoutData(gridData);
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
