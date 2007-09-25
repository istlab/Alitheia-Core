package eu.sqooss.impl.service.webui;

import java.io.*;

// Java Extensions
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class WebUIServer extends HttpServlet {
    public WebUIServer() { 
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException,
                                                              IOException {
        response.setContentType("text/html");
        PrintWriter print = response.getWriter();
        print.print("<html><head><title>SQO-OSS Web UI</title></head>");
        print.print("<body><h1>It Works!</h1></body>");
        print.print("</html>");
    }
}


