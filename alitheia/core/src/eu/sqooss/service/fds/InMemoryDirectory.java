/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Christoph Schleifenbaum <christoph@kdab.net>
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

package eu.sqooss.service.fds;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.fds.InMemoryCheckout;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

import eu.sqooss.impl.service.CoreActivator;

public class InMemoryDirectory {
    
    private InMemoryCheckout checkout;
    private InMemoryDirectory parentDirectory;
   
    private String name;
    
    private List<String> files;
    private List<InMemoryDirectory> directories;
   
    public InMemoryDirectory() {
        name = new String();
        files = new LinkedList<String>();
        directories = new LinkedList<InMemoryDirectory>();
    }
    
    public InMemoryDirectory(String name) {
        this();
        this.name = name;
    }
    
    public InMemoryDirectory(InMemoryCheckout checkout) {
        this("");
        this.checkout = checkout;
    }
    
    public InMemoryDirectory(InMemoryDirectory parent, String name) {
        this(name);
        this.parentDirectory = parent;
    }
    
    /**
     * Returns the name of the directory.
     * Returns an empty string for the project's root directory.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the complete path of this directory.
     */
    public String getPath() {
    	if (parentDirectory==null) {
    		return "/" + getName();
    	} else {
    		String parentPath = parentDirectory.getPath();
    		if (!parentPath.endsWith("/")) {
    			parentPath = parentPath + "/";
    		}
    		return parentPath + getName();
    	}
    }
   
    /**
     * Returns this directory's parent directory.
     * Might be null, if this is the root directory.
     */
    public InMemoryDirectory getParentDirectory() {
        return parentDirectory;
    }
    
    /**
     * Returns the checkout this directory belongs to.
     */
    public InMemoryCheckout getCheckout() {
        return checkout == null ? parentDirectory.getCheckout() : checkout;
    }

    /**
     * Returns the list of subdirectories this directory has.
     */
    public List<InMemoryDirectory> getSubDirectories() {
        return directories;
    }

    /**
     * Returns one file living in this directory or below.
     * @param name The filename relative to this directory.
     * @return A reference to a ProjectFile
     */
    public ProjectFile getFile(String name) {
        
        /*Recursively traverse the directories of the provided file path*/
        if (name.indexOf('/') != -1 ) {
            String pathName = name.substring(0, name.indexOf('/'));
            String fileName = name.substring(name.indexOf('/') + 1);
            InMemoryDirectory dir = getSubdirectoryByName(pathName);
            return dir == null ? null : dir.getFile(fileName);
        }
        
        DBService dbs = CoreActivator.getDBService();
        
        StoredProject project = getCheckout().getProject();
        ProjectVersion version = ProjectVersion.getVersionByRevision(project, getCheckout().getRevision() );
        
        String paramRevision = "project_revision";
        String paramProjectId = "project_id";
        String paramName = "file_name";
        String paramPath = "path_name";

        String query = "select pf " +
                       "from ProjectFile pf " +
                       "where pf.projectVersion.version<=:" + paramRevision + " and " +
                       "pf.name=:" + paramName + " and " +
                       "pf.dir.path=:" + paramPath + " and " +
                       "pf.projectVersion.project.id=:" + paramProjectId + " " +
                       "order by pf.projectVersion.version desc";

        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramRevision, version.getVersion() );
        parameters.put(paramProjectId, project.getId() );
        parameters.put(paramName, name);
        parameters.put(paramPath, getPath());
        
        List<?> projectFiles = dbs.doHQL(query, parameters);
        if (projectFiles != null && projectFiles.size() != 0) {
        	return (ProjectFile)projectFiles.get(0);
        }
        
        return null;
    }
    
    /**
     * Returns the list of files this directory contains.
     */
    public List<ProjectFile> getFiles() {
        ArrayList<ProjectFile> result = new ArrayList<ProjectFile>(files.size());

        DBService dbs = CoreActivator.getDBService();
      
        StoredProject project = getCheckout().getProject();
        ProjectVersion version = ProjectVersion.getVersionByRevision(project, getCheckout().getRevision() );
        
        String paramRevision = "project_revision";
        String paramProjectId = "project_id";
        String paramName = "file_name";
        String paramPath = "path_name";

        String query = "select pf " +
                       "from ProjectFile pf " +
                       "where pf.projectVersion.version<=:" + paramRevision + " and " +
                       "pf.name=:" + paramName + " and " +
                       "pf.dir.path=:" + paramPath + " and " +
                       "pf.projectVersion.project.id=:" + paramProjectId + " " +
                       "order by pf.projectVersion.version desc";

        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramRevision, version.getVersion() );
        parameters.put(paramProjectId, project.getId() );
        parameters.put(paramPath, getPath());

        for (String file: files) {
            parameters.put(paramName, file);
        
            List<?> projectFiles = dbs.doHQL(query, parameters);
            if (projectFiles != null && projectFiles.size() != 0) {
                result.add((ProjectFile)projectFiles.get(0));
            }
        }
        
        return result;
    }

    public List<String> getFileNames() {
        return this.files;
    }
    
    /**
     * Search if the provided path exists in this directory or below  
     * 
     * @return true, if the provided path can be reached from the current 
     * directory 
     */
    public boolean pathExists(String path) {
        //Check if the path points to a dir first
        InMemoryDirectory dir = getSubdirectoryByName(path);
        
        if (dir != null) {
            return true;
        }
        
        // Split directory part from (possible) file part and re-check
        String file = path.substring(path.lastIndexOf('/') + 1, path.length());
        path = path.substring(0, path.lastIndexOf('/'));
        dir = getSubdirectoryByName(path);

        if (dir == null) {
            // Dir not found
            return false;
        }

        //Dir found, search files for matching file name
        if (dir.getFiles().contains(file)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Adds a file.
     * 
     * @param path The filename relative to this directory.
     */
    public void addFile(String path) {
        if (path.indexOf('/') == -1 ) {
        	if (!files.contains(path)) {
        		files.add(path);
        	}
        } else {
            String pathName = path.substring(0, path.indexOf('/'));
            String fileName = path.substring(path.indexOf('/') + 1);
            InMemoryDirectory dir = getOrCreateSubdirectoryByName(pathName);
            dir.addFile(fileName);
        }
    }

    /**
     * Deletes a file.
     * @param path The filename relative to this directory.
     */
    public void deleteFile(String path) {
    	if (path.indexOf('/') == -1) {
    		// might be a file
    		files.remove(path);
    		// but it might even be a directory...
    		directories.remove(getSubdirectoryByName(path));
    	} else {
    		String pathName = path.substring(0, path.indexOf('/'));
    		String fileName = path.substring(path.indexOf('/') + 1);
    		InMemoryDirectory dir = getSubdirectoryByName(pathName);
    		if (dir != null ) {
    			dir.deleteFile(fileName);
    		}
    	}
    }
    
    /**
     * Gets a subdirectory.
     * @param path The name of the directory.
     * @return An InMemoryDirectory reference.
     */
    public InMemoryDirectory getSubdirectoryByName(String path) {
    	if (path == null || path.equals("")) {
    		return this;
    	} else if (path.indexOf('/') == -1 ) {
            for (InMemoryDirectory dir : directories) {
                if (dir.getName().equals(path)) {
                    return dir;
                }
            }
            return null;
    	}
    	String pathName = path.substring(0, path.indexOf('/'));
    	String fileName = path.substring(path.indexOf('/') + 1);

    	return getSubdirectoryByName(pathName).getSubdirectoryByName(fileName);
    }
 
    /**
     * Creates a subdirectory
     * @param name The name of the subdirectory to create.
     * @return A reference to the new directory.
     */
    public InMemoryDirectory createSubDirectory(String name) {
    	if (name == null || name.equals("")) {
    		return this;
    	} else if (name.indexOf('/') == -1) {
          	InMemoryDirectory dir = getSubdirectoryByName(name);
          	if (dir == null ) {
          		dir = new InMemoryDirectory(this, name);
          	}
          	if (!directories.contains(dir)) {
          		directories.add(dir);
          	}
        	return getSubdirectoryByName(name);
    	} else {
    		String pathName = name.substring(0, name.indexOf('/'));
    		String fileName = name.substring(name.indexOf('/') + 1);
    		InMemoryDirectory dir = getOrCreateSubdirectoryByName(pathName);
    		return dir.createSubDirectory(fileName);
    	}
 
    }
    
    /**
     * Gets a subdirectory. If the wanted one couldn't be found, it's created.
     * @param name The name of the directory.
     * @return An InMemoryDirectory reference.
     */
    protected InMemoryDirectory getOrCreateSubdirectoryByName(String name) {
    	// if it's empty, it's us!
    	if (name.length() == 0) {
    		return this;
    	}
    	
        for (InMemoryDirectory dir : directories) {
            if (dir.getName().equals(name) ) {
                return dir;
            }
        }

        // not found? create it
        return createSubDirectory(name);
    }
    
    /**
     * Nice formatting of this directory including subdirectories and files.
     * @param indentation The indentation of the root.
     * @return A String containing a nicely formatted directory tree.
     */
    protected String toString(int indentation) {
    	String result = "";
    	String indent = "";
    	for (int i=0; i < indentation; ++i)
    		indent = indent + " ";
    	
    	result = result + indent + getName() + "\n";
    	
    	for (InMemoryDirectory d: directories) {
    		result = result + d.toString(indentation + 1);
    	}
    	for (String file: files) {
    		result = result + indent + " " + file + "\n";
    	}
    	
    	return result;
    }
   
    /** {@inheritDoc} */
    public String toString() {
    	return toString(0);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
