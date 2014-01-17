/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

package eu.sqooss.service.util;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a static utility class for various file manipulations.
 */
public class FileUtils {
    /**
     * Return the filename portion of a path. 
     * @param path The path to examine
     * @return The filename or empty string if the path is empty or null
     */
    public static String basename(String path) {
        String filename = path.substring(path.lastIndexOf('/') + 1);
        
        if (filename == null || filename.equalsIgnoreCase("")) {
            filename = "";
        }
        return filename;
    }
    
    /**
     * Return the directory portion of a path. 
     * @param path The path to examine
     * @return The directory or an empty string if the path is empty or null
     */
    public static String dirname(String path) {
        String dirPath = path.substring(0, path.lastIndexOf('/'));
        if (dirPath == null || dirPath.equalsIgnoreCase("")) {
            dirPath = "/"; 
        }
        return dirPath;
    }
    
    /**
     * Return the extension part from a filename.
     * @param path The path to return the extension for
     * @return The extension (without the preceding .) or an empty string if
     * no extention can be found 
     */
    public static String extension(String path) {
        String extension;
        
        String name = basename (path);
        
        extension = name.substring(name.lastIndexOf('.') + 1);
        if (extension == null || extension.equalsIgnoreCase(""))
            extension = "";
        
        return extension;
    }
    
    /**
     * Append two repository paths making sure that there are the path 
     * seperators are OK at the merge point. 
     * 
     * @param path The original path
     * @param toAppend The path to append to the original path
     * @return A String with the two paths appended
     */
    public static String appendPath(String path, String toAppend) {
    	if (path == null)
    		return toAppend;
    	if (toAppend == null)
    		return path;
    	
    	String newPath;
    	
    	if (path.endsWith("/") && toAppend.startsWith("/"))
    		newPath = path.concat(toAppend.substring(1));
    	else if (!path.endsWith("/") && !toAppend.startsWith("/"))
    		newPath = path.concat("/").concat(toAppend);
    	else 
    		newPath = path.concat(toAppend);
    	
    	return newPath;
    }

    /**
     * Delete a directory and its contents recursively
     *
     * @param path The file path to include
     */
    public static void deleteRecursive(File path) {
        File[] c = path.listFiles();
        for (File file : c) {
            if (file.isDirectory()) {
                deleteRecursive(file);
                file.delete();
            } else {
                file.delete();
            }
        }

        path.delete();
    }

    /**
     * Search recursively for a filename pattern in the provided path
     *
     * @return A list of files whose full path matches with the
     * provided pattern
     */
    public static List<File> findGrep(File path, Pattern p) {
        List<File> result = new ArrayList<File>();

        File[] c = path.listFiles();
        for (File file : c) {
            if (file.isDirectory()) {
                result.addAll(findGrep(file, p));
            } else {
                Matcher m = p.matcher(file.getAbsolutePath());
                if (m.find())
                    result.add(file);
            }
        }
        return result;
    }

    /**
     * Find the first file that matches with the provided pattern
     * using breadth first traversal.
     */
    public static File findBreadthFirst(File path, Pattern p) {
        File[] c = path.listFiles();
        List<File> dirs = new ArrayList<File>();
        for (File file : c) {
            if (file.isDirectory()) {
                dirs.add(file);
            } else {
                Matcher m = p.matcher(file.getAbsolutePath());
                if (m.find())
                    return file;

            }
        }
        for (File dir: dirs)
            return findBreadthFirst(dir, p);
        return null;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
