/**
 * 
 */
package eu.sqooss.simple;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * @author circular
 *
 */
public class SimpleTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    private void storeProjectInfo() {
        //url, name, website, contactPoint, srcPath, mailPath
    }
    
    private void checkOut(long revision) {
        
    }
    
    private void storeProjectFiles(Vector<String> files) {
        
    }
    
    private double runWCTool(String file, long revision) {
        double result = 0.0;
        
        String [] args = new String[2];
        args[0] = "wc -l";
        args[1] = file;
        
        String output = "";
        
        Process p;
        try {
            p = Runtime.getRuntime().exec(args);
            if(p != null) {
                BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
                output = b.readLine();
                b.close();
            }
        } catch (IOException e) {
            return 0.0;
        }
        
        //parse the output
        
        return result;
    }
    
    private void storeMetrics(int projectID, long revision, String file) {
        //run CW tool, get the metric value, store it in db
    }
}
