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

import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import eu.sqooss.plugin.util.ConnectionUtils;
import eu.sqooss.ws.client.datatypes.WSProjectFile;

/**
 * The content provider gets the data from the system core. 
 */
public class PathSelectionContentProvider implements ITreeContentProvider {

    private class Pair<F, S> {
        
        private F first;
        private S second;
        
        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
        
        public F getFirst() {
            return first;
        }
        public void setFirst(F first) {
            this.first = first;
        }
        public S getSecond() {
            return second;
        }
        public void setSecond(S second) {
            this.second = second;
        }
    }
    
    private static final WSProjectFile[] EMPTY_ARRAY = new WSProjectFile[0];
    
    private ConnectionUtils connectionUtils;
    private WSProjectFile[] root;
    
    //key<->parent id, value<->parent object, parent's children
    private Map<Long, Pair<WSProjectFile, WSProjectFile[]>> elements;
    
    public PathSelectionContentProvider(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
        elements = new Hashtable<Long, Pair<WSProjectFile, WSProjectFile[]>>();
    }
    
    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof WSProjectFile) {
            WSProjectFile parent = (WSProjectFile) parentElement;
            if (parent.getDirectory()) {
                Long parentId = Long.valueOf(parent.getToDirectoryId()); //get directory id (not project file)
                if (!elements.containsKey(parentId)) {
                    WSProjectFile[] children = connectionUtils.
                        getFilesInDirectory(parent);
                    if (children != null) {
                        elements.put(parentId, new Pair<WSProjectFile, WSProjectFile[]>(parent, children));
                    }
                }
                if (elements.containsKey(parentId)) {
                    return elements.get(parentId).getSecond();
                }
            }
        }
        return EMPTY_ARRAY;
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        if (element instanceof WSProjectFile) {
            WSProjectFile fileElem = ((WSProjectFile) element);
            if (fileElem.getId() == fileElem.getDirectoryId()) {
                return null;
            }
            Long parentId = Long.valueOf(fileElem.getDirectoryId()); //get the parent id (it is directory id, not project file id)
            if (elements.containsKey(parentId)) {
                return elements.get(parentId).getFirst();
            }
        }
        return null; //no parent
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
        if (element instanceof WSProjectFile) {
            return ((WSProjectFile) element).getDirectory();
        } else {
            return false;
        }
    }

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        if (root == null) {
            root = connectionUtils.getFilesInDirectory(null);//get root directory
        }
        return (root != null)? root : EMPTY_ARRAY;
    }

    public void dispose() {
        //do nothing here
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        //do nothing here
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
