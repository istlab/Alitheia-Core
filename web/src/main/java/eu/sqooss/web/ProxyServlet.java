package eu.sqooss.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

public class ProxyServlet extends HttpServlet {

    private String targetServer = "";

    @Override
    public void init() throws ServletException {
        super.init();
        targetServer = getInitParameter("forwardTo");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String[] pathParts = req.getRequestURL().toString().split("proxy/");

        if (pathParts.length <= 1)
            return;
        
        HttpClient httpclient = new DefaultHttpClient();
        String url = targetServer + "/" + pathParts[1];
        HttpRequestBase targetRequest = new HttpGet(url);

        Enumeration<String> headerNames = req.getHeaderNames();
        String headerName = null;
        while (headerNames.hasMoreElements()) {
            headerName = headerNames.nextElement();
            targetRequest.addHeader(headerName, req.getHeader(headerName));
        }

        HttpResponse targetResponse = httpclient.execute(targetRequest);
        HttpEntity entity = targetResponse.getEntity();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()));
        String line = reader.readLine();

        while (line != null) {
            writer.write(line + "\n");
            line = reader.readLine();
        }

        reader.close();
        writer.close();
        httpclient.getConnectionManager().shutdown();
    }
}