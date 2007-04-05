package eu.sqooss.plugin.wordcount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.sqooss.plugin.OutputParser;

/**
 * Parsing method for the word count plugin
 */
public class WCParser implements OutputParser {

    public HashMap<String, String> parse(InputStream is) {
        try {
            HashMap<String,String> result = new HashMap<String,String>();
            BufferedReader b = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String output = b.readLine();
            b.close();
            Pattern pattern = Pattern.compile("[0-9]+");
            Matcher matcher = pattern.matcher(output);
            if(matcher.find()) {
                // This is hard coded, but this plugin returns only this metric :)
                result.put("WC", matcher.group());
            }
            
            return result;
        } catch (IOException e) {
            // TODO error logging here
            return null;
        }
    }

}
