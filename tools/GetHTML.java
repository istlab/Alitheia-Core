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

import java.net.*;
import java.io.*;

public class GetHTML {

    public static void main(String[] args) {
        if ((args.length < 2) || (args[0] == null) || (args[1] == null)) {
          System.out.println ("[ERROR] Wrong number of parameteres!");
          System.exit(1);
        }

        try {
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

            wssXML.close(); wssXML = null;
            wssOut.close(); wssOut = null;
        }
        catch (MalformedURLException ex) {
            System.out.println ("[ERROR]: Mailformed URL!");
            System.exit(2);
        }
        catch (IOException ex) {
            System.out.println ("[ERROR]: I/O Error!\n" + ex);
            System.exit(3);
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
