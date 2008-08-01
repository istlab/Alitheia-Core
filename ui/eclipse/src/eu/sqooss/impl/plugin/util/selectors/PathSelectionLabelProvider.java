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

package eu.sqooss.impl.plugin.util.selectors;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import eu.sqooss.impl.plugin.Activator;
import eu.sqooss.impl.plugin.util.Constants;
import eu.sqooss.ws.client.datatypes.WSProjectFile;

/**
 * A label provider for the resources of the system core.
 * The label provider is shared between tree viewers.
 */
public class PathSelectionLabelProvider implements ILabelProvider {

    private Image repositoryImage;
    private Image folderImage;
    private Image fileImage;
    
    public PathSelectionLabelProvider() {
        initImages();
    }
    
    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    public Image getImage(Object element) {
        if (element instanceof WSProjectFile) {
            initImages();
            WSProjectFile fileElem = (WSProjectFile) element;
            if (fileElem.getDirectory()) {
                if (fileElem.getId() == fileElem.getDirectoryId()) {
                    return repositoryImage;
                } else {
                    return folderImage;
                }
            } else {
                return fileImage;
            }
        }
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    public String getText(Object element) {
        if (element instanceof WSProjectFile) {
            WSProjectFile fileElem = (WSProjectFile) element;
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(fileElem.getShortName());
            //the root's status is null
            if (fileElem.getStatus() != null) {
                strBuilder.append(" (");
                strBuilder.append(fileElem.getStatus().toLowerCase());
                strBuilder.append(")");
            }
            return strBuilder.toString();
        }
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
        folderImage.dispose();
        fileImage.dispose();
    }

    public void addListener(ILabelProviderListener listener) {
        //do nothing here
    }
    
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
        //do nothing here
    }

    /*
     * The label provider is shared between tree viewers.
     * The label provider is disposed when the tree viewer is disposed. 
     */
    private void initImages() {
        if ((repositoryImage == null) || (repositoryImage.isDisposed())) {
            repositoryImage = Activator.getDefault().getImageDescriptor(
                    Constants.IMG_OBJ_REPOSITORY).createImage();
        }
        if ((folderImage == null) || (folderImage.isDisposed())) {
            folderImage = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                    ISharedImages.IMG_OBJ_FOLDER).createImage();
        }
        if ((fileImage == null) || (fileImage.isDisposed())) {
            fileImage = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                    ISharedImages.IMG_OBJ_FILE).createImage();
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
