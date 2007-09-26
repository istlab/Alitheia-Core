package eu.sqooss.impl.service.webui;

import java.io.*;

// Java Extensions
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class WebUIServer extends HttpServlet {
    public static final String page = "<html><head><title>SQO-OSS Web UI</title></head><body><h1>It Works!</h1></body></html>";

    public WebUIServer() { 
        System.out.println("# WebUIServer ok.");
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException,
                                                              IOException {
        response.setContentType("text/html");
        PrintWriter print = response.getWriter();
        print.print(page);
    }
}


