package eu.sqooss.parsers.java.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Finder {
    private final Pattern pattern;
    private List<File> results;

    public Finder(String pattern) {
        this.pattern = Pattern.compile(pattern);
        this.results = new LinkedList<File>();
    }

    public List<File> getMatchingFiles(String startingDirName)  {
        File startingDir = new File(startingDirName);
        return getMatchingFiles(startingDir);
    }
    
    public List<File> getMatchingFiles(File startingDir) {
        
        File[] filesAndDirs = startingDir.listFiles();
        if (filesAndDirs == null) {
            return results;
        }
        List<File> filesDirs = Arrays.asList(filesAndDirs);
        for (File file : filesDirs) {
            if (file.isFile()) {
                if (find(file)) {
                    System.out.println("*** ADDING " + file.getName());
                    results.add(file);
                }
            } else if (file.isDirectory()){
                getMatchingFiles(file);
            }
        }
        return results;
    }
    
    //Compares the pattern against the file.
    public boolean find(File file) {
        if (file != null) {
            Matcher m = pattern.matcher(file.getName());
            if (m.find()) {
                results.add(file);
                return true;
            }
        }
        return false;
    }

    //Invoke the pattern matching method on each file.
    public boolean visitFile(File file) {
        return find(file);
    }

}