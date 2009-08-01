/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.service.tds;

import java.util.ArrayList;
import java.util.List;

/**
 * A node in an SCM system. The node can either be a file or a 
 * directory. It can hold references to other nodes enclosed 
 * in the represented node.  
 *
 */
public class SCMNode {
    
    /**
     * The type of the node
     */
    private SCMNodeType type;
    
    /**
     * The path represented by the node
     */
    private String path;
    
    /**
     * The SCM revision this node 
     */
    private Revision revision;
    
    /**
     * The list of children of the node, if the node is a directory
     */
    private List<SCMNode> children;
    
    public SCMNode(String path, SCMNodeType type, Revision revision) {
        this.path = path;
        this.type = type;
        this.revision = revision;
        
        if (type.equals(SCMNodeType.DIR)) {
            children = new ArrayList<SCMNode>();
        }
    }
    
    /**
     * Add a node to the list of children of this node. If the node to 
     * be added belongs to a node that is deeper in the hierarchy, then
     * the path between the current node and the node to be added is
     * constructed by adding intermediate directory nodes. If the node
     * to be added belongs to a different part of the tree, then
     * it is not added to the children of  current node.
     * 
     * @param child The node to append to the current node
     */
    public void appendChild(SCMNode child) {
        
        /*Files do not have children*/
        if (type.equals(SCMNodeType.FILE)) {
            return;
        }
        
        /* Return if the child exists or if we try to append the node on itself*/
        if (childExists(child) || equals(child)) {
            return;
        }
        
        /* The child's path should point somewhere under our path */
        if (!child.getPath().startsWith(path)) {
            return;
        }
        
        if (child.type.equals(SCMNodeType.DIR)) {
            createDir(child.path);
        } else if (child.type.equals(SCMNodeType.FILE)) {
            createFile(child.path);
        } else {
            System.err.println("SCMNode: Uknown node type");
        }
    }
    
    protected void createDir(String path) {
        
        String parentPath = getPath();
        String toAddPath = path.substring(getPath().length());
        String[] pathElems = toAddPath.split("/");
        String workingPath = parentPath;
        
        for (String dir : pathElems) {
            workingPath += "/" + dir;
            SCMNode d = getNodeByPath(workingPath);
            
            if (d == null) {
                SCMNode n = new SCMNode(workingPath, SCMNodeType.DIR, revision);
                d = getNodeByPath(parentPath);
                d.children.add(n);
            }
            parentPath = workingPath;
        }
    }
    
    protected void createFile(String path) {
        
        String dirPath = path.substring(0, path.lastIndexOf('/') + 1);
        createDir(dirPath);
        SCMNode parent = getNodeByPath(dirPath);
        SCMNode file = new SCMNode(path, SCMNodeType.FILE, getRevision());
        parent.children.add(file);
    }
    
    /**
     * Get a node by path name
     * 
     * @param path The path to search for
     * @return The node represented by the path if found; null otherwise or if
     * the provided path is null.
     */
    public SCMNode getNodeByPath(String path) {
        
        if (path.equals(this.path) || path.equals("")) { 
            return this;
        }
        
        if (path == null || !path.startsWith(getPath())) {
            return null;
        }
        
        String[] pathElems = path.split("/");
        String curPath = getPath();
        SCMNode curNode = this;
        boolean found = false;
        
        for (String d : pathElems) {
            for (SCMNode n : curNode.children) {
                if (n.getPath().equals(curPath + "/" + d)) {
                    curPath += "/" + d;
                    curNode = n;
                    found = true;
                    break;
                }
            }
            
            if (found == false) 
                return null;
        }
        
        return curNode;
    }
    
    /**
     * Flattens the directory hierarchy and returns all the nodes contained in 
     * this node and below as a list. The order of the nodes in the returned
     * list is unspecified.
     * 
     * @param node
     * @return A list of nodes, possibly empty if the provided node is null
     */
    public List<SCMNode> getSCMNodeList(SCMNode node) {
        List<SCMNode> nodeList = new ArrayList<SCMNode>();
        
        if (node == null) {
            return nodeList;
        }
        
        for (SCMNode n : node.getChildren()) {
            nodeList.add(n);
            
            if (n.getType().equals(SCMNodeType.DIR)) {
                nodeList.addAll(getSCMNodeList(n));
            }
        }
        
        nodeList.add(node);
        
        return nodeList;
    }
    
    /**
     * Check if the provided node belonds to  the children of this node 
     * recursively.
     * 
     * @param child The node to check for existence
     * 
     * @return True, if the node is found in the hierarchy of nodes below
     * this node; false otherwise.
     */
    public boolean childExists(SCMNode child) {
       if (child == null) {
           return false;
       }
       
       for (SCMNode node : children) {
           if (node.equals(child)) {
               return true;
           }
           
           if (node.getType().equals(SCMNodeType.DIR)) {
               node.childExists(child);
           }
       }
        
       return false; 
    }

    /**
     * @return the type of the node
     */
    public SCMNodeType getType() {
        return type;
    }

    /**
     * @return the path of the node
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the revision
     */
    public Revision getRevision() {
        return revision;
    }

    /**
     * @return the children of this node, if the node
     * is a directory, else null;
     */
    public List<SCMNode> getChildren() {
        return children;
    }
    
    /** {@inheritDoc} */
    public boolean equals(SCMNode node) {
        if (this.path.equals(node.getPath()) &&
            this.revision == node.getRevision() &&
            this.type.equals(node.getType())) {
            return true;
        } else {
            return false;
        }       
    }
    
    /**
     * Nice formatting of this directory including subdirectories and files.
     * Shameless copy from InMemoryDirectory.toString().
     * @param indentation The indentation of the root.
     * @return A String containing a nicely formatted directory tree.
     */
    protected String toString(int indentation) {
        String result = "";
        String indent = "";
        for (int i=0; i < indentation; ++i)
                indent = indent + " ";
        
        String name = path.substring(path.lastIndexOf("/"), path.length());
        
        result = result + indent + name + "\n";
        
        for (SCMNode n: children) {
            if(n.type == SCMNodeType.DIR) {
                result = result + n.toString(indentation + 1);
            } else {
                name = n.path.substring(n.path.lastIndexOf("/"), n.path.length());
                result = result + indent + " " + name + "\n";
            }
        }
        
        return result;
    }
   
    /** {@inheritDoc} */
    public String toString() {
        return toString(0);
    }
}
