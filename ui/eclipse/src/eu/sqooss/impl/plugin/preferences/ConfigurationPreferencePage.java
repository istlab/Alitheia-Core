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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;

import eu.sqooss.impl.plugin.Activator;
import eu.sqooss.impl.plugin.util.Constants;
import eu.sqooss.impl.plugin.util.Messages;
import eu.sqooss.plugin.util.ConnectionUtils;

public class ConfigurationPreferencePage
                extends AbstractConfigurationPreferencePage
                implements TraverseListener,
                           SelectionListener,
                           IRunnableWithProgress {

    private IPreferenceStore store;
    private static ConnectionUtils connectionUtils;
    
    /**
     * @see eu.sqooss.impl.plugin.preferences.AbstractConfigurationPreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
        Control control = super.createContents(parent);
        spinnerServerPort.addTraverseListener(this);
        this.store = Activator.getDefault().getPreferenceStore();
        loadValues();
        return control;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
        //the preference store is accessed via doGetPreferenceStore
        setPreferenceStore(null);
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
     */
    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        //return the plug-in preference store
        return Activator.getDefault().getPreferenceStore();
    }

    /**
     * @see eu.sqooss.impl.plugin.preferences.AbstractConfigurationPreferencePage#contributeButtons(org.eclipse.swt.widgets.Composite)
     */
    protected void contributeButtons(Composite parent) {
        super.contributeButtons(parent);
        buttonValidate.addSelectionListener(this);
    }
    
    /**
     * @see org.eclipse.jface.preference.PreferencePage#performApply()
     */
    @Override
    protected void performApply() {
        this.performOk();
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        loadDefaultValue();
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        saveValues();
        return super.performOk();
    }

    /* ===[Listeners' methods]=== */
    /**
     * @see org.eclipse.swt.events.TraverseListener#keyTraversed(org.eclipse.swt.events.TraverseEvent)
     */
    public void keyTraversed(TraverseEvent e) {
        Object eventSource = e.getSource();
        if (eventSource == spinnerServerPort) {
            e.doit = e.keyCode != SWT.CR; //vetoes all CR traversals
        }
    }

    public void widgetSelected(SelectionEvent e) {
        Object eventSource = e.getSource();
        if (eventSource == buttonValidate) {
            validateConfiguration();
        }
    }
    
    public void widgetDefaultSelected(SelectionEvent e) {
        //do nothing here
    }

    /* ===[utility methods]=== */
    private void loadValues() {
        textFieldServerAddress.setText(
                store.getString(Constants.PREFERENCE_NAME_SERVER_ADDRESS));
        spinnerServerPort.setSelection(
                store.getInt(Constants.PREFERENCE_NAME_SERVER_PORT));
        textFieldUserName.setText(
                store.getString(Constants.PREFERENCE_NAME_USER_NAME));
        textFieldPassword.setText(
                store.getString(Constants.PREFERENCE_NAME_USER_PASSWORD));
    }
    
    private void saveValues() {
        store.setValue(Constants.PREFERENCE_NAME_SERVER_ADDRESS,
                textFieldServerAddress.getText());
        store.setValue(Constants.PREFERENCE_NAME_SERVER_PORT,
                spinnerServerPort.getSelection());
        store.setValue(Constants.PREFERENCE_NAME_USER_NAME,
                textFieldUserName.getText());
        store.setValue(Constants.PREFERENCE_NAME_USER_PASSWORD,
                textFieldPassword.getText());
    }
    
    private void loadDefaultValue() {
        textFieldServerAddress.setText(
                store.getDefaultString(Constants.PREFERENCE_NAME_SERVER_ADDRESS));
        spinnerServerPort.setSelection(
                store.getDefaultInt(Constants.PREFERENCE_NAME_SERVER_PORT));
        textFieldUserName.setText(
                store.getDefaultString(Constants.PREFERENCE_NAME_USER_NAME));
        textFieldPassword.setText(
                store.getDefaultString(Constants.PREFERENCE_NAME_USER_PASSWORD));
    }
    
    private void validateConfiguration() {
        if (connectionUtils == null) {
            ProgressMonitorDialog progressMonitorDialog =
                new ProgressMonitorDialog(getShell());
            try {
                progressMonitorDialog.run(true, false, this);
            } catch (Exception e) {
                connectionUtils = new ConnectionUtils(null);
            }
        }
        connectionUtils.setServerAddress(textFieldServerAddress.getText());
        connectionUtils.setServerPort(spinnerServerPort.getSelection());
        connectionUtils.setUserName(textFieldUserName.getText());
        connectionUtils.setPassword(textFieldPassword.getText());
        connectionUtils.validate();
        if (connectionUtils.isValidAccount()) {
            boolean isForSave;
            isForSave = MessageDialog.openQuestion(getShell(),
                    Messages.Configuration_MessageBox_Validate_Title,
                    Messages.Configuration_MessageBox_Validate_Pass +
                    "\n" + Messages.Configuration_MessageBox_Save_Question);
            if (isForSave) {
                saveValues();
            }
        } else {
            MessageDialog.openWarning(getShell(),
                    Messages.Configuration_MessageBox_Validate_Title,
                    Messages.Configuration_MessageBox_Validate_Fail + 
                    "\n\nReason: " + connectionUtils.getErrorMessage());
        }
    }
    
    /**
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void run(IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException {
        monitor.setTaskName(Messages.Configuration_Connection_Init_Dialog_Message);
        connectionUtils = new ConnectionUtils(null);
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
