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

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class QualityPropertyPage extends AbstractQualityPropertyPage implements SelectionListener{

    public static final String PROPERTY_PAGE_ID = "eu.sqooss.plugin.properties.qualityPropertyPage";
    
    /**
     * @see eu.sqooss.plugin.properties.AbstractQualityPropertyPage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent) {
        Control control = super.createContents(parent);
        buttonCompareVersion.addSelectionListener(this);
        
        textFieldEntityPath.setText(getEntityPath());
        
        parent.forceFocus();
        
        return control;
    }

    /**
     * @see org.eclipse.jface.preference.PreferencePage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        noDefaultAndApplyButton();
        super.createControl(parent);
    }

    public void widgetDefaultSelected(SelectionEvent e) {
        //do nothing
    }

    public void widgetSelected(SelectionEvent e) {
        if (e.getSource() == buttonCompareVersion) {
            if (comboCompareVersion.isEnabled()) {
                comboCompareVersion.setEnabled(false);
            } else {
                comboCompareVersion.setEnabled(true);
            }
        }
    }
    
    private String getEntityPath() {
        IResource resource = (IResource) (getElement().getAdapter(IResource.class));
        return ProjectConverterUtility.getEntityPath(resource);
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
