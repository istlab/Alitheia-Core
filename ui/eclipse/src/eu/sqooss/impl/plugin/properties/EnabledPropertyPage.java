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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import eu.sqooss.plugin.util.ConnectionUtils;
import eu.sqooss.plugin.util.EnabledState;

/**
 * The super class of the property pages which take care of the connection state. 
 */
abstract class EnabledPropertyPage extends PropertyPage implements EnabledState, IRunnableWithProgress {
    
    private IProject resourceProject;
    protected ConnectionUtils connectionUtils;
    protected ControlEnableState controlEnableState;
    protected Control mainControl;
    
    /**
     * The method checks the connection state and calls the
     * <code>setEnabled</code> method. The parameter of the
     * <code>setEnabled</code> is <code>true</code> in case of valid connection and
     * <code>false</code> otherwise. 
     */
    protected void enableIfPossible() {
        IResource resource = (IResource) (getElement().getAdapter(IResource.class));
        resourceProject = resource.getProject();
        
        try {
            connectionUtils = (ConnectionUtils) resourceProject.
            getSessionProperty(ConnectionUtils.PROPERTY_CONNECTION_UTILS);
        } catch (CoreException e) {
            setEnabled(false);
        }
        if (connectionUtils == null) {
            ProgressMonitorDialog progressMonitorDialog =
                new ProgressMonitorDialog(getShell());
            try {
                progressMonitorDialog.run(true, false, this);
            } catch (Exception e) {
                connectionUtils = new ConnectionUtils(resourceProject);
            }
        }
        connectionUtils.save();
        setEnabled(connectionUtils.validate());
    }
    
    /**
     * @see eu.sqooss.plugin.util.EnabledState#setEnabled(boolean)
     */
    public void setEnabled(boolean isEnabled) {
        if (isEnabled) {
            //it is disabled before, enable now
            if (controlEnableState != null) {
                controlEnableState.restore();
                controlEnableState = null;
            }
        }else {
            //it is enabled, disable now
            if ((controlEnableState == null) && (mainControl != null)) {
                controlEnableState = ControlEnableState.disable(mainControl);
            }
        }
    }

    /**
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void run(IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException {
        monitor.setTaskName(PropertyPagesMessages.
                EnabledPropertyPage_Connection_Init_Dialog_Message);
        connectionUtils = new ConnectionUtils(resourceProject);
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
