/*
This file is part of the Alitheia system, developed by the SQO-OSS
consortium as part of the IST FP6 SQO-OSS project, number 033331.

Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
Copyright 2007 by Adriaan de Groot <groot@kde.org>


Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package eu.sqooss.impl.service.webui;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.IOException;
import java.io.PrintWriter;

// Java Extensions
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class WebUIServer extends HttpServlet {
    public static final String pageHead = "<html><head><title>SQO-OSS Web UI</title></head><body style='padding: 1ex 1em;'>";
    public static final String pageIntro = "<p style='margin: 1ex 1em; padding: 1ex 1em; border: 3px solid pink;'>This is the administrative interface page for the Alitheia system. System stats are shown below.</p>";
    public static final String pageBundleStats = "<h1 style='margin-left: 1em; padding-left: 3px; color: green;'>Available Bundles</h1>";
    public static final String pageServiceStats = "<h1 style='margin-left: 1em; padding-left: 3px; color: green;'>Available Services</h1>";
    public static final String pageFooter = "</body></html>";

    private BundleContext bundlecontext = null;

    public WebUIServer(BundleContext bc) { 
        System.out.println("# WebUIServer ok.");
        bundlecontext = bc;
    }

    protected String[] getServiceNames() {
        if ( bundlecontext != null ) {
            try {
                ServiceReference servicerefs[] = bundlecontext.getServiceReferences(
                    "ServiceObject","");
                String[] names = servicerefs[0].getPropertyKeys();

/*
int i = 0;
for (ServiceReference r : servicerefs) {
Service service = (Service) bundlecontext.getService(r);
String s = service.getName();
names[i++]=s;
}
*/
                return names;
            } catch (org.osgi.framework.InvalidSyntaxException e) {
                System.out.println("! Invalid request syntax");
                return null;
            }
        } else {
            return null;
        }
    }

    protected void printServices(PrintWriter print) {
        String[] names = getServiceNames();
        printList(print,names);
    }

    public void printList(PrintWriter print, String[] names) {
        if (names.length > 0) {
            print.println("<ul>");
            for (String s : names) {
                print.println("<li>" + s + "</li>");
            }
            print.println("</ul>");
        } else {
            print.println("<p>&lt;none&gt;</p>");
        }
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException,
                                                              IOException {
        response.setContentType("text/html");
        PrintWriter print = response.getWriter();
        print.println(pageHead);
        print.println(pageIntro);
        print.println(pageServiceStats);
        printServices(print);
        print.println(pageFooter);
    }
}


