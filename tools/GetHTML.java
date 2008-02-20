import java.net.*;
import java.io.*;

public class GetHTML {
    public static void main(String[] args) throws Exception {
        URL wssURL = new URL(args[0]);
        URLConnection wssCon = wssURL.openConnection();
        BufferedReader wssXML = new BufferedReader(
            new InputStreamReader(wssCon.getInputStream()));

        FileWriter outFile = new FileWriter(args[1]);
        BufferedWriter wssOut = new BufferedWriter(outFile);

        String nextLine;
        while ((nextLine = wssXML.readLine()) != null) {
            wssOut.write(nextLine);
            wssOut.newLine();
        }

        wssXML.close();
        wssOut.close();
    }
}
