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

import org.osgi.framework.Bundle;
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
        bundlecontext = bc;
    }

    /**
    * Concatenate the strings in @p names, placing @p sep
    * between each (except at the end) and return the resulting
    * string.
    *
    * @see QStringList::join
    */
    public String join(String[] names, String sep) {
        int i = 0;
        int l = names.length;

        if (l<1) {
            return "";
        }

        StringBuilder b = new StringBuilder( l * sep.length() + l + 1 );;
        for ( i=0; i<l; i++ ) {
            b.append(names[i]);
            if ( i < (l-1) ) {
                b.append(sep);
            }
        }
        return b.toString();
    }
    
    /**
    * Given a bitfield value @p value, and an array that names
    * each bit position, return a comma-separated string that
    * names each bit position that is set in @p value.
    */
    public String bitfieldToString(String[] statenames, int value) {
        StringBuilder b = new StringBuilder();
        for ( int statebit = 0; statebit < statenames.length; statebit++ ) {
            int statebitvalue = 1 << statebit ;
            if ( (value & statebitvalue) != 0 ) {
                b.append(statenames[statebit]);
                value -= statebitvalue;
                if ( value != 0 ) {
                    b.append(", ");
                }
            }
        }
        return b.toString();
    }

    protected String[] getServiceNames() {
        if ( bundlecontext != null ) {
            try {
                ServiceReference servicerefs[] = bundlecontext.getServiceReferences(
                    null,null);

                String names[] = new String[servicerefs.length];
                int i = 0;
                for (ServiceReference r : servicerefs) {
                    String s;
                    Object clazz = r.getProperty( org.osgi.framework.Constants.OBJECTCLASS );
                    if (clazz != null) {
                        s = join( (String[])clazz, ", ");
                    } else {
                        s = "No class defined";
                    }
                    names[i++]=s;
                }

                return names;
            } catch (org.osgi.framework.InvalidSyntaxException e) {
                System.out.println("! Invalid request syntax");
                return null;
            }
        } else {
            return null;
        }
    }

    protected String[] getBundleNames() {
        if ( bundlecontext != null ) {
            Bundle[] bundles = bundlecontext.getBundles();
            String names[] = new String[bundles.length];
            int i = 0;
            String[] statenames = { "uninstalled",
                "installed",
                "resolved",
                "starting",
                "stopping",
                "active" } ;
            String s;
            for (Bundle b : bundles) {
                int state = b.getState();
                s = b.getSymbolicName() + " = " + state + " (" + bitfieldToString(statenames,state) + ")";;
                names[i++] = s;
            }
            return names;
        } else {
            return null;
        }
    }

    protected void printServices(PrintWriter print) {
        String[] names = getServiceNames();
        printList(print,names);
    }

    protected void printBundles(PrintWriter print) {
        String[] names = getBundleNames();
        printList(print, names);
    }

    public void printList(PrintWriter print, String[] names) {
        if (names.length > 0) {
            print.println("<ol style='margin-left: 2em;'>");
            for (String s : names) {
                print.println("<li>" + s + "</li>");
            }
            print.println("</ol>");
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
        print.println(pageBundleStats);
        printBundles(print);
        print.println(pageServiceStats);
        printServices(print);
        print.println(pageFooter);
    }
}


