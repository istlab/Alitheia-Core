/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Adriaan de Groot <groot@kde.org>
 *
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

package eu.sqooss.impl.service.webadmin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.TDAccessor;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.util.Pair;
import eu.sqooss.service.util.StringUtils;

public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private long startTime = new Date().getTime();

    private BundleContext bundlecontext = null;

    // AlitheiaCore reference and object
    private ServiceReference srefCore = null;
    private AlitheiaCore sobjAlitheiaCore = null;

    // LogManager and Logger objects
    private LogManager sobjLogManager = null;
    private Logger sobjLogger = null;

    private DBService sobjDB = null;
    private TDSService sobjTDS = null;
    private Scheduler sobjSched = null;
    private UpdaterService sobjUpdater = null;

    private Hashtable<String,Pair<String,String>> staticContentMap;
    private Hashtable<String,String> dynamicContentMap;
    private Hashtable<String,String> dynamicSubstitutions;

    /**
     * Returns a string representing the uptime of the Alitheia core
     * in dd:hh:mm:ss format
     */
    private String getUptime() {
        long remainder;
        long timeRunning = new Date().getTime() - startTime;

        // Get the elapsed time in days, hours, mins, secs
        int days = new Long(timeRunning / 86400000).intValue();
        remainder = timeRunning % 86400000;
        int hours = new Long(remainder / 3600000).intValue();
        remainder = remainder % 3600000;
        int mins = new Long(remainder / 60000).intValue();
        remainder = remainder % 60000;
        int secs = new Long(remainder / 1000).intValue();

        return String.format("%d:%02d:%02d:%02d", days, hours, mins, secs);
    }

    public void addStaticContent(String path, String type) {
        Pair < String, String > p = new Pair < String, String > (path,type);
        staticContentMap.put(path, p);
    }

    public AdminServlet(BundleContext bc) {
        bundlecontext = bc;

        srefCore = bc.getServiceReference(AlitheiaCore.class.getName());
        if (srefCore != null) {
            sobjAlitheiaCore = (AlitheiaCore) bc.getService(srefCore);
        }
        else {
            System.out.println("No CORE");
        }

        if (sobjAlitheiaCore != null) {
            //Get the LogManager and Logger objects
            sobjLogManager = sobjAlitheiaCore.getLogManager();
            if (sobjLogManager != null) {
                sobjLogger = sobjLogManager.createLogger(
                    Logger.NAME_SQOOSS_WEBADMIN);
            }

            // Get the DB Service object
            sobjDB = sobjAlitheiaCore.getDBService();
            if (sobjDB != null) {
                sobjLogger.debug("WebAdmin got DB Service object.");
            }

            // Get the TDS Service object
            sobjTDS = sobjAlitheiaCore.getTDSService();
            if (sobjTDS != null) {
                sobjLogger.debug("WebAdmin got TDS Service object.");
            }

            sobjSched = sobjAlitheiaCore.getScheduler();
            if (sobjSched != null) {
                sobjLogger.debug("WebAdmin got Scheduler Service object.");
            }
            
            // Get the Updater Service object
            sobjUpdater = sobjAlitheiaCore.getUpdater();
            if (sobjUpdater != null) {
                sobjLogger.debug("WebAdmin got Updater Service object.");
            }

            Stuffer myStuffer = new Stuffer(sobjDB, sobjLogger, sobjTDS);
            myStuffer.run();

            staticContentMap = new Hashtable < String, Pair < String, String > >();

            // Images and CSS
            addStaticContent("/screen.css", "text/css");
            addStaticContent("/sqo-oss.png", "image/x-png");
            addStaticContent("/queue.png", "image/x-png");
            addStaticContent("/uptime.png", "image/x-png");
            addStaticContent("/greyBack.jpg", "image/x-jpg");
            addStaticContent("/projects.png", "image/x-png");
            addStaticContent("/logs.png", "image/x-png");
            addStaticContent("/bundles.png", "image/x-png");
            addStaticContent("/gear.png", "image/x-png");
            addStaticContent("/header-repeat.png", "image/x-png");

            // Pages
            dynamicContentMap = new Hashtable<String,String>();
            dynamicContentMap.put("/about", "/about.html");
            dynamicContentMap.put("/status", "/index.html");
            dynamicContentMap.put("/index", "/index.html");
            dynamicContentMap.put("/projects", "/projects.html");
            dynamicContentMap.put("/logs", "/logs.html");
            dynamicContentMap.put("/jobs", "/jobs.html");
            dynamicContentMap.put("/alljobs", "/alljobs.html");
            dynamicSubstitutions = new Hashtable<String,String>();
        }
    }

    protected String[] getServiceNames(ServiceReference[] servicerefs) {
        if( servicerefs != null ){
            String s, names[] = new String[servicerefs.length];
            int i = 0;

            for (ServiceReference r : servicerefs) {
                Object clazz =
                    r.getProperty( org.osgi.framework.Constants.OBJECTCLASS );
                if (clazz != null) {
                    s = StringUtils.join( (String[])clazz, ", ");
                } else {
                    s = "No class defined";
                }

                names[i++] = s;
            }

            return names;
        } else {
            return null;
        }
    }
    
    /**
     * Creates and HTML table with information about the jobs that 
     * failed and the recorded exceptions
     * @return
     */
    protected String renderFailedJobs() {
        StringBuilder result = new StringBuilder();
        Job[] jobs = sobjSched.getFailedQueue();
        result.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n");
        result.append("\t<thead>\n");
        result.append("\t\t<tr>\n");
        result.append("\t\t\t<td>Job Type</td>\n");
        result.append("\t\t\t<td>Exception type</td>\n");
        result.append("\t\t\t<td>Exception text</td>\n");
        result.append("\t\t\t<td>Exception backtrace</td>\n");
        result.append("\t\t</tr>\n");
        result.append("\t</thead>\n");
        result.append("\t<tbody>\n");

        for(Job j: jobs) {
            result.append("\t\t<tr>\n\t\t\t<td>");
            result.append(j.getClass().toString());
            result.append("</td>\n\t\t\t<td>");
            result.append(j.getErrorException().getClass().toString());
            result.append("</td>\n\t\t\t<td>");
            result.append(j.getErrorException().getMessage());
            result.append("</td>\n\t\t\t<td>");
            for(StackTraceElement m: j.getErrorException().getStackTrace()) {
                result.append(m.getClassName());
                result.append(".");
                result.append(m.getMethodName());
                result.append("(), (");
                result.append(m.getFileName());
                result.append(":");
                result.append(m.getLineNumber());
                result.append(")<br/>");
            }
            
            result.append("\t\t\t</td>\n\t\t</tr>");
        }
        
        result.append("\t</tbody>\n");
        result.append("</table>");
        
        return result.toString();
    }

    protected String renderWaitJobs() {
        StringBuilder result = new StringBuilder();
        Job[] jobs = sobjSched.getWaitQueue();
        result.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n");
        result.append("\t<thead>\n");
        result.append("\t\t<tr>\n");
        result.append("\t\t\t<td>Queue pos</td>\n");
        result.append("\t\t\t<td>Job Type</td>\n");
        result.append("\t\t\t<td>Job depedencies</td>\n");
        result.append("\t\t</tr>\n");
        result.append("\t</thead>\n");
        result.append("\t<tbody>\n");

        int i = 0;
        for(Job j: jobs) {
            i++;
            result.append("\t\t<tr>\n\t\t\t<td>");
            result.append(i);
            result.append("</td>\n\t\t\t<td>");
            result.append(j.getClass().toString());
            result.append("</td>\n\t\t\t<td>");
            Iterator<Job> ji = j.dependencies().iterator();
            
            while(ji.hasNext()) {
                result.append(ji.next().getClass().toString());
                if(ji.hasNext())
                    result.append(",");
            }
            result.append("</td>\n\t\t\t<td>");
            
            result.append("\t\t\t</td>\n\t\t</tr>");
        }
        
        result.append("\t</tbody>\n");
        result.append("</table>");
        
        return result.toString();
    }

    protected String renderJobFailStats() {
        StringBuilder result = new StringBuilder();
        HashMap<String,Integer> fjobs = sobjSched.getSchedulerStats().getFailedJobTypes();
        result.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n");
        result.append("\t<thead>\n");
        result.append("\t\t<tr>\n");
        result.append("\t\t\t<td>Job Type</td>\n");
        result.append("\t\t\t<td>Num Jobs Failed</td>\n");
        result.append("\t\t</tr>\n");
        result.append("\t</thead>\n");
        result.append("\t<tbody>\n");

        for(String key : fjobs.keySet().toArray(new String[1])) {
            result.append("\t\t<tr>\n\t\t\t<td>");
            result.append(key);
            result.append("</td>\n\t\t\t<td>");
            result.append(fjobs.get(key));
            result.append("\t\t\t</td>\n\t\t</tr>");
        }
        result.append("\t</tbody>\n");
        result.append("</table>");
        return result.toString();
    }

    /**
     * Creates an HTML table displaying the key information on
     * bundles and the services that they supply
    */
    protected String renderBundles() {
        if( bundlecontext != null ) {
            Bundle[] bundles = bundlecontext.getBundles();
            String[] statenames = {
                "uninstalled",
                "installed",
                "resolved",
                "starting",
                "stopping",
                "active"
            };

            StringBuilder resultString = new StringBuilder();
            resultString.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n");
            resultString.append("\t<thead>\n");
            resultString.append("\t\t<tr>\n");
            resultString.append("\t\t\t<td>Bundle</td>\n");
            resultString.append("\t\t\t<td>Status</td>\n");
            resultString.append("\t\t\t<td>Services Utilised</td>\n");
            resultString.append("\t\t</tr>\n");
            resultString.append("\t</thead>\n");
            resultString.append("\t<tbody>\n");

            for( Bundle b : bundles ){
                String[] names = getServiceNames(b.getRegisteredServices());
		String symbolicName = b.getSymbolicName();
		if (symbolicName == null) {
			symbolicName = "<none>";
		}

                resultString.append("\t\t<tr>\n\t\t\t<td>");
                resultString.append(StringUtils.makeXHTMLSafe(symbolicName));
                resultString.append("</td>\n\t\t\t<td>");
                resultString.append(StringUtils.bitfieldToString(statenames,b.getState()));
                resultString.append("</td>\n\t\t\t<td>\n\t\t\t\t<ul>\n");
                resultString.append(renderList(names));
                resultString.append("\t\t\t\t</ul>\n\t\t\t</td>\n\t\t</tr>\n");
            }

            resultString.append("\t</tbody>\n");
            resultString.append("</table>");

            return resultString.toString();
        } else {
            return null;
        }
    }

    public String renderList(String[] names) {
        if ((names != null) && (names.length > 0)) {
            StringBuilder b = new StringBuilder();
            for (String s : names) {
                b.append("\t\t\t\t\t<li>" + StringUtils.makeXHTMLSafe(s) + "</li>\n");
            }

            return b.toString();
        } else {
            return "\t\t\t\t\t<li>&lt;none&gt;</li>\n";
        }
    }

    /**
    * Sends a resource (stored in the jar file) as a response. The mime-type
    * is set to @p mimeType . The @p path to the resource should start
    * with a / .
    *
    * Test cases:
    *   - null mimetype, null path, bad path, relative path, path not found,
    *   - null response
    *
    * TODO: How to simulate conditions that will cause IOException
    */
    protected void sendResource(HttpServletResponse response, Pair<String,String> source)
        throws ServletException, IOException {
        InputStream istream = getClass().getResourceAsStream(source.first);
        if ( istream == null ) {
            // TODO: Is there a more specific exception?
            throw new IOException( "Path not found: " + source.first);
        }

        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        int totalBytes = 0;

        response.setContentType(source.second);
        ServletOutputStream ostream = response.getOutputStream();
        while ( (bytesRead = istream.read(buffer)) > 0 ) {
            ostream.write(buffer,0,bytesRead);
            totalBytes += bytesRead;
        }

        // TODO: Check that the bytes written were as many as the
        //  file size in the JAR (how? it's an InputStream).
        if ( sobjLogger != null ) {
            sobjLogger.debug("Wrote " + totalBytes + " from " + source.first);
        }
    }

    protected void sendTemplate(HttpServletResponse response, String path, Hashtable<String,String> subs)
        throws ServletException, IOException {
        BufferedReader istream = new BufferedReader(
            new InputStreamReader(getClass().getResourceAsStream(path)));
        if ( istream == null ) {
            // TODO: Is there a more specific exception?
            throw new IOException( "Path not found: " + path );
        }

        response.setContentType("text/html");
        PrintWriter print = response.getWriter();
        while ( istream.ready() ) {
            String line = istream.readLine().trim();
            if ( line.startsWith("@@") && subs.containsKey(line) ) {
                print.println(subs.get(line));
            } else {
                print.println(line);
            }
        }
    }

    private void resetSubstitutions() {
        dynamicSubstitutions.clear();
        dynamicSubstitutions.put("@@ABOUT","<p class='box'>This is the administrative interface.</p>");
        dynamicSubstitutions.put("@@STATUS","The cruncher is offline.");
        dynamicSubstitutions.put("@@LOGO","<img src='/logo' id='logo' alt='Logo' />");
        dynamicSubstitutions.put("@@COPYRIGHT","Copyright 2007-2008 <a href=\"http://www.sqo-oss.eu/about/\">SQO-OSS Consortium Members</a>");
        dynamicSubstitutions.put("@@GETLOGS", renderList(sobjLogManager.getRecentEntries()));
        dynamicSubstitutions.put("@@PROJECTS", renderProjects());
        dynamicSubstitutions.put("@@BUNDLE", renderBundles());
        dynamicSubstitutions.put("@@UPTIME",getUptime());
        dynamicSubstitutions.put("@@QUEUE_LENGTH", String.valueOf(sobjSched.getSchedulerStats().getWaitingJobs()));
        dynamicSubstitutions.put("@@JOB_EXEC", String.valueOf(sobjSched.getSchedulerStats().getRunningJobs()));
        dynamicSubstitutions.put("@@JOB_WAIT", String.valueOf(sobjSched.getSchedulerStats().getWaitingJobs()));
        dynamicSubstitutions.put("@@JOB_WORKTHR", String.valueOf(sobjSched.getSchedulerStats().getWorkerThreads()));
        dynamicSubstitutions.put("@@JOB_FAILED", String.valueOf(sobjSched.getSchedulerStats().getFailedJobs()));
        dynamicSubstitutions.put("@@JOB_TOTAL", String.valueOf(sobjSched.getSchedulerStats().getTotalJobs()));
        dynamicSubstitutions.put("@@WAITJOBS", renderWaitJobs());
        dynamicSubstitutions.put("@@FAILJOBS", renderFailedJobs());
        dynamicSubstitutions.put("@@JOBFAILSTATS", renderJobFailStats());
    }

    private void doServletException(HttpServletRequest request,
        HttpServletResponse response, Throwable e)
        throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter print = response.getWriter();
        print.println("<html><head><title>Alitheia Exception</title></head>");
        print.println("<body>An exception was encountered. The message is:");
        print.println("<blockquote>" + e.getMessage() + "</blockquote>");
        print.println("The stack trace is:");
        print.println("<blockquote>");
        e.printStackTrace(print);
        print.println("</blockquote>");
        print.println("</body></html>");
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException,
                                                              IOException {
        try {
            sobjLogger.debug("GET path=" + request.getPathInfo());

            String query = request.getPathInfo();
            if (query.startsWith("/sqooss/services")) {
                sobjLogger.warn("Web services query <" + request.getPathInfo() + "> reached the Webadmin servlet.");
            }
            if ( (query != null) && (staticContentMap.containsKey(query)) ) {
                sendResource(response, staticContentMap.get(query));
            } else {
                resetSubstitutions();
                if ( (query != null) && dynamicContentMap.containsKey(query) ) {
                    sendTemplate(response,dynamicContentMap.get(query),dynamicSubstitutions);
                } else {
                    sendTemplate(response,"/index.html",dynamicSubstitutions);
                }
            }
        } catch (Exception e) {
            doServletException(request,response,e);
        }
    }

    private String renderProjects() {
        List<StoredProject> l = sobjDB.doHQL("from StoredProject");
        
        if (l == null) {
            return null;
        }
        
        StringBuilder s = new StringBuilder();
        
        for (int i=0; i<l.size(); i++) {
            StoredProject p = (StoredProject) l.get(i);
            s.append("<li>");
            s.append(p.getName());
            s.append(" ([id=");
            s.append(p.getId());
            s.append("]) <br/>Update:");
            for (String updTarget: UpdaterService.UpdateTarget.toStringArray()) {
                s.append("<a href=\"http://localhost:8088/updater?project=");
                s.append(p.getName());
                s.append("&target=");
                s.append(updTarget);
                s.append("\" title=\"Tell the updater to check for new data in this category.\">");
                s.append(updTarget);
                s.append("</a>&nbsp");
            }
	    s.append("<br/>Sites: <a href=\"");
	    s.append(p.getWebsite());
	    s.append("\">Website</a>&nbsp;Alitheia Reports");
            s.append("</li>");
        }
        return s.toString();
    }

    private void addProject(HttpServletRequest request) {
        resetSubstitutions();

        final String tryAgain = "<p><a href=\"/projects\">Try again</a>.</p>";
        final String returnToList = "<p><a href=\"/projects\">Try again</a>.</p>";

        String name = request.getParameter("name");
        String website = request.getParameter("website");
        String contact = request.getParameter("contact");
        String bts = request.getParameter("bts");
        String mail = request.getParameter("mail");
        String scm = request.getParameter("scm");

        // Avoid missing-entirely kinds of parameters.
        if ( (name == null) ||
            (website == null) ||
            (contact == null) ||
          /*  (bts == null) || FIXME: For now, BTS and Mailing lists can be empty
            (mail == null) || */
            (scm == null) ) {
            dynamicSubstitutions.put("@@RESULTS","<p>Add project failed because some of the required information was missing.</p>" + tryAgain);
            return;
        }

        // Avoid adding projects with empty names or SVN.
        if (name.trim().length() == 0 || scm.trim().length() == 0) {
            dynamicSubstitutions.put("@@RESULTS","<p>Add project failed because the project name or Subversion repository were missing.</p>" + tryAgain);
            return;
        }

        if (sobjDB != null) {
            StoredProject p = new StoredProject();
            p.setName(name);
            p.setWebsite(website);
            p.setContact(contact);
            p.setBugs(bts);
            p.setRepository(scm);
            p.setMail(mail);

            sobjDB.addRecord(p);

            /* Run a few checks before actually storing the project */
            
            //1. Duplicate project
            HashMap<String, Object> pname = new HashMap<String, Object>();
            pname.put("name", (Object)p.getName());
            if(sobjDB.findObjectByProperties(StoredProject.class, pname).size() > 1) {
                //Duplicate project, remove
                sobjDB.deleteRecord(sobjDB.findObjectById(StoredProject.class, p.getId()));
                sobjLogger.warn("A project with the same name already exists");
                dynamicSubstitutions.put("@@RESULTS","<p>ERROR: A project" +
                        " with the same name (" + p.getName() + ") already exists. " +
                        "Project not added.</p>" + tryAgain);
                return;
            }
            
            //2. Add accessor and try to access project resources
            sobjTDS.addAccessor(p.getId(), p.getName(), p.getBugs(), 
                    p.getMail(), p.getRepository());
            TDAccessor a = sobjTDS.getAccessor(p.getId());
            
            try {
                a.getSCMAccessor().getHeadRevision();
                //FIXME: fix this when we have a proper bug accessor
                if(bts != null) {
                    //Bug b = a.getBTSAccessor().getBug(1);
                }
                if(mail != null) {
                    //FIXME: fix this when the TDS supports returning 
                    // list information
                    //a.getMailAccessor().getNewMessages(0);
                }
            } catch (InvalidRepositoryException e) {
                sobjLogger.warn("Error accessing repository. Project not added");
                dynamicSubstitutions.put("@@RESULTS","<p>ERROR: Can not access " +
                		"repository: &lt;" + p.getRepository() + "&gt;," +
                		" project not added.</p>" + tryAgain);
                //Invalid repository, remove and remove accessor
                sobjDB.deleteRecord(sobjDB.findObjectById(StoredProject.class, p.getId()));
                sobjTDS.releaseAccessor(a);
                return;
            }
            
            sobjTDS.releaseAccessor(a);
            
            // 3. Call the updater and check if it starts
            if (sobjUpdater.update(p, UpdaterService.UpdateTarget.ALL, null)) {
                sobjLogger.info("Added a new project <" + name + "> with ID " +
                        p.getId());
                dynamicSubstitutions.put("@@RESULTS","<p>New project added successfully.</p>" +
                    returnToList);
            }
            else {
                sobjLogger.warn("The updater failed to start while adding project");
                sobjDB.deleteRecord(sobjDB.findObjectById(StoredProject.class, p.getId()));
                dynamicSubstitutions.put("@@RESULTS","<p>ERROR: The updater failed " +
                        "to start while adding project. Project was not added.</p>" +
                        tryAgain);
            }
        }
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
                                                               IOException {
        try {
            sobjLogger.debug("POST path=" + request.getPathInfo());
            if ("/addproject".equals(request.getPathInfo())) {
                addProject(request);
                // addProject() has filled in the substitutions by now
                dynamicSubstitutions.put("@@ACTIVE","class=\"section-3\"");
                sendTemplate(response,"/results.html",dynamicSubstitutions);
            } else if ("/stop".equals(request.getPathInfo())) {
                dynamicSubstitutions.put("@@RESULTS", "<p>Alitheia Core is now shutdown.</p>");
                dynamicSubstitutions.put("@@ACTIVE","class=\"section-1\"");
                sendTemplate(response,"/results.html",dynamicSubstitutions);
                // Stop the system
                sobjLogger.info("System stopped by user request to webadmin.");
                bundlecontext.getBundle(0).stop();
            } else if ("/restart".equals(request.getPathInfo())) {
                dynamicSubstitutions.put("@@RESULTS", "<p>Alitheia Core is now restarting. Please wait.</p>");
                dynamicSubstitutions.put("@@ACTIVE","class=\"section-1\"");
                startTime = new Date().getTime();
                sobjLogger.warn("BOGUS system restart by user request to webadmin.");
                sendTemplate(response,"/results.html",dynamicSubstitutions);
            } else {
                doGet(request,response);
            }
        } catch (Exception e) {
            doServletException(request,response,e);
        }
    }
}


// vi: ai nosi sw=4 ts=4 expandtab
//
