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

import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import eu.sqooss.impl.plugin.util.selectors.PathSelectionContentProvider;
import eu.sqooss.impl.plugin.util.selectors.PathSelectionDialog;
import eu.sqooss.impl.plugin.util.selectors.PathSelectionLabelProvider;
import eu.sqooss.plugin.util.Constants;
import eu.sqooss.ws.client.datatypes.WSProjectFile;

public class ProfilePropertyPage
                extends AbstractProfilePropertyPage
                implements SelectionListener {
    
    PathSelectionDialog selectionDialog;
    
    /**
     * @see eu.sqooss.plugin.properties.AbstractProfilePropertyPage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
        Composite containerComposite = (Composite) super.createContents(parent);
        mainControl = mainComposite;
        configurationLink.addSelectionListener(this);
        buttonPathBrowse.addSelectionListener(this);
        enableIfPossible();
        return containerComposite;
    }
    
    /**
     * @see eu.sqooss.impl.plugin.properties.EnabledPropertyPage#setEnabled(boolean, String)
     */
    @Override
    public void setEnabled(boolean isEnabled, String errorMessage) {
        if (mainControl == null) return; //the method createContents isn't called yet
        super.setEnabled(isEnabled, errorMessage);
        if (isEnabled) {
            configurationLink.setVisible(false);
        } else {
            configurationLink.setVisible(true);
        }
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
        if (eventSource == configurationLink) {
            IWorkbenchPreferenceContainer container= (IWorkbenchPreferenceContainer)getContainer();
            container.openPage(Constants.CONFIGURATION_PROPERTY_PAGE_ID, null);
        } else if (eventSource == buttonPathBrowse) {
            if (selectionDialog == null) {
                selectionDialog = new PathSelectionDialog(getShell(),
                        new PathSelectionLabelProvider(), new PathSelectionContentProvider(connectionUtils));
            }
            Object prevSelection = textFieldPath.getData(); 
            if (prevSelection != null) {
                selectionDialog.setInitialSelection(prevSelection);
            }
            if (Window.OK == selectionDialog.open()) {
                WSProjectFile projectFile = (WSProjectFile) selectionDialog.getFirstResult();
                textFieldPath.setText(projectFile.getFileName());
                textFieldPath.setData(projectFile);
            }
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
