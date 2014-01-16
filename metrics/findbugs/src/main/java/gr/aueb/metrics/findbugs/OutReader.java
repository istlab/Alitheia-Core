package gr.aueb.metrics.findbugs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OutReader extends Thread {
    String name;
    InputStream input;

    public OutReader(InputStream in, String name) {
        this.name = name;
        this.input = in;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            FileWriter out = new FileWriter(new File(name), true);

            char[] buf = new char[8192];
            while (true) {
                int length = in.read(buf);
                if (length < 0)
                    break;
                out.write(buf, 0, length);
                out.flush();
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
