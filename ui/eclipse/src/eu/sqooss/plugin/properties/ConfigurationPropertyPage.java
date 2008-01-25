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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ConfigurationPropertyPage extends AbstractConfigurationPropertyPage implements SelectionListener{

    public static final String PROPERTY_PAGE_ID = "eu.sqooss.plugin.properties.configurationPropertyPage";
    
    private static final String TEXT_FIELD_SERVER_URL_DEFAULT_VALUE   = "http://";
    private static final String TEXT_FIELD_USER_NAME_DEFAULT_VALUE    = "";
    private static final String TEXT_FIELD_PASSWORD_DEFAULT_VALUE     = "";
    private static final String TEXT_FIELD_PROJECT_NAME_DEFAULT_VALUE = "";
    
    public static final QualifiedName PROPERTY_SERVER_URL   =
        new QualifiedName("", "SQO-OSS_SERVER_URL");
    public static final QualifiedName PROPERTY_USER_NAME    =
        new QualifiedName("", "SQO-OSS_USER_NAME");
    public static final QualifiedName PROPERTY_PASSWORD     =
        new QualifiedName("", "SQO-OSS_PASSWORD");
    public static final QualifiedName PROPERTY_PROJECT_NAME =
        new QualifiedName("", "SQO-OSS_PROJECT_NAME");
    
	public ConfigurationPropertyPage() {
		super();
	}

	
    /**
     * @see eu.sqooss.plugin.properties.AbstractConfigurationPropertyPage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
        Control control = super.createContents(parent);
        try {
            String propertyValue;
            IResource resource = (IResource) (getElement().getAdapter(IResource.class));
            IProject resourceProject = resource.getProject();
            
            propertyValue = resourceProject.getPersistentProperty(PROPERTY_SERVER_URL);
            if (propertyValue == null) {
                propertyValue = TEXT_FIELD_SERVER_URL_DEFAULT_VALUE;
            }
            textFieldServerUrl.setText(propertyValue);
            
            propertyValue = resourceProject.getPersistentProperty(PROPERTY_USER_NAME);
            if (propertyValue == null) {
                propertyValue = TEXT_FIELD_USER_NAME_DEFAULT_VALUE;
            }
            textFieldUserName.setText(propertyValue);
            
            propertyValue = resourceProject.getPersistentProperty(PROPERTY_PASSWORD);
            if (propertyValue == null) {
                propertyValue = TEXT_FIELD_PASSWORD_DEFAULT_VALUE;
            }
            textFieldPassword.setText(propertyValue);
            
            propertyValue = resourceProject.getPersistentProperty(PROPERTY_PROJECT_NAME);
            if (propertyValue == null) {
                propertyValue = TEXT_FIELD_PROJECT_NAME_DEFAULT_VALUE;
            }
            textFieldProjectName.setText(propertyValue);
        } catch (CoreException ce) {
            performDefaults();
        }
        return control;
    }

    /**
     * @see eu.sqooss.plugin.properties.AbstractConfigurationPropertyPage#contributeButtons(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void contributeButtons(Composite parent) {
        super.contributeButtons(parent);
        buttonValidate.addSelectionListener(this);
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        super.performDefaults();
        textFieldServerUrl.setText(TEXT_FIELD_SERVER_URL_DEFAULT_VALUE);
        textFieldUserName.setText(TEXT_FIELD_USER_NAME_DEFAULT_VALUE);
        textFieldPassword.setText(TEXT_FIELD_PASSWORD_DEFAULT_VALUE);
        textFieldProjectName.setText(TEXT_FIELD_PROJECT_NAME_DEFAULT_VALUE);
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        if (super.performOk()) {
            return saveConfiguration();
        } else {
            return false;
        }
    }

    public void widgetDefaultSelected(SelectionEvent e) {
        //do nothing
    }

    public void widgetSelected(SelectionEvent e) {
        if (e.getSource() == buttonValidate) {
            validateConfiguration();
        }
    }
	
    private boolean validateConfiguration() {
        //TODO: implement
        String url = textFieldServerUrl.getText();
        String userName = textFieldUserName.getText();
        String password = textFieldPassword.getText();
        String projectName = textFieldProjectName.getText();
        System.out.println("validate configuration: " + url + "; " + userName + "; " +
                password + "; " + projectName);
        return true;
    }
    
    private boolean saveConfiguration() {
        if (validateConfiguration()) {
            try {
                String propertyValue;
                IResource resource = (IResource) (getElement().getAdapter(IResource.class));
                IProject resourceProject = resource.getProject();
                
                propertyValue = textFieldServerUrl.getText().trim();
                if (!TEXT_FIELD_SERVER_URL_DEFAULT_VALUE.equals(propertyValue)) {
                    resourceProject.setPersistentProperty(PROPERTY_SERVER_URL, propertyValue);
                } else {
                    resourceProject.setPersistentProperty(PROPERTY_SERVER_URL, null);
                }
                
                propertyValue = textFieldUserName.getText().trim();
                if (!TEXT_FIELD_USER_NAME_DEFAULT_VALUE.equals(propertyValue)) {
                    resourceProject.setPersistentProperty(PROPERTY_USER_NAME, propertyValue);
                } else {
                    resourceProject.setPersistentProperty(PROPERTY_USER_NAME, null);
                }
                
                propertyValue = textFieldPassword.getText().trim();
                if (!TEXT_FIELD_PASSWORD_DEFAULT_VALUE.equals(propertyValue)) {
                    resourceProject.setPersistentProperty(PROPERTY_PASSWORD, propertyValue);
                } else {
                    resourceProject.setPersistentProperty(PROPERTY_PASSWORD, null);
                }
                
                propertyValue = textFieldProjectName.getText();
                if (!TEXT_FIELD_PROJECT_NAME_DEFAULT_VALUE.equals(propertyValue)) {
                    resourceProject.setPersistentProperty(PROPERTY_PROJECT_NAME, propertyValue);
                } else {
                    resourceProject.setPersistentProperty(PROPERTY_PROJECT_NAME, null);
                }
                return true;
            } catch (CoreException ce) {
                return false;
            }
        } else {
            return false;
        }
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
