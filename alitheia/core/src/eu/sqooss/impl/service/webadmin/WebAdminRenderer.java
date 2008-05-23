/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 by Paul J. Adams <paul.adams@siriusit.co.uk>
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.TDAccessor;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.util.StringUtils;
import eu.sqooss.service.webadmin.WebadminService;

/**
 * The WebAdminRender class provdies functions for rendering content
 * to be displayed within the WebAdmin interface.
 *
 * @author, Paul J. Adams <paul.adams@siriusit.co.uk>
 * @author, Boryan Yotov <b.yotov@prosyst.com>
 */
public class WebAdminRenderer {
    // Core components
    private static AlitheiaCore sobjAlitheiaCore = null;
    private static ServiceReference srefCore = null;

    // Critical logging components
    private static LogManager sobjLogManager = null;
    private static Logger sobjLogger = null;

    // Service components
    private static DBService sobjDB = null;
    private static MetricActivator sobjMetricActivator = null;
    private static PluginAdmin sobjPA = null;
    private static Scheduler sobjSched = null;
    private static TDSService sobjTDS = null;
    private static UpdaterService sobjUpdater = null;
    private static SecurityManager sobjSecurity = null;

    // Velocity stuff
    private VelocityContext vc = null;

    // Current time
    private static long startTime = new Date().getTime();

    public WebAdminRenderer(BundleContext bundlecontext, VelocityContext vc) {
        srefCore = bundlecontext.getServiceReference(AlitheiaCore.class.getName());
        this.vc = vc;

        if (srefCore != null) {
            sobjAlitheiaCore = (AlitheiaCore) bundlecontext.getService(srefCore);
        }
        else {
            System.out.println("AdminServlet: No Alitheia Core found.");
        }

        if (sobjAlitheiaCore != null) {
            //Get the LogManager and Logger objects
            sobjLogManager = sobjAlitheiaCore.getLogManager();
            if (sobjLogManager != null) {
                sobjLogger = sobjLogManager.createLogger(Logger.NAME_SQOOSS_WEBADMIN);
            }

            // Get the DB Service object
            sobjDB = sobjAlitheiaCore.getDBService();
            if (sobjDB != null) {
                sobjLogger.debug("WebAdmin got DB Service object.");
            }

            // Get the Plugin Administration object
            sobjPA = sobjAlitheiaCore.getPluginAdmin();
            if (sobjPA != null) {
                sobjLogger.debug("WebAdmin got Plugin Admin object.");
            }

            // Get the scheduler
            sobjSched = sobjAlitheiaCore.getScheduler();
            if (sobjSched != null) {
                sobjLogger.debug("WebAdmin got Scheduler Service object.");
            }

            // Get the metric activator, whatever that is
            sobjMetricActivator = sobjAlitheiaCore.getMetricActivator();
            if (sobjMetricActivator != null) {
                sobjLogger.debug("WebAdmin got Metric Activator object.");
            }

            // Get the TDS Service object
            sobjTDS = sobjAlitheiaCore.getTDSService();
            if (sobjTDS != null) {
                sobjLogger.debug("WebAdmin got TDS Service object.");
            }

            // Get the Updater Service object
            sobjUpdater = sobjAlitheiaCore.getUpdater();
            if (sobjUpdater != null) {
                sobjLogger.debug("WebAdmin got Updater Service object.");
            }

            // Get the Security Manager's object
            sobjSecurity = sobjAlitheiaCore.getSecurityManager();
            if (sobjSecurity != null) {
                sobjLogger.debug("WebAdmin got the Security Manager's object.");
            }
        }

        // Do some stuffing
        Stuffer myStuffer = new Stuffer(sobjDB, sobjLogger, sobjTDS);
        myStuffer.run();
    }

    public static String renderJobFailStats() {
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

    public static String renderWaitJobs() {
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

    /**
     * Creates and HTML table with information about the jobs that
     * failed and the recorded exceptions
     * @return
     */
    public static String renderFailedJobs() {
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

        Class<?> tmpClass;
        for(Job j: jobs) {
            result.append("\t\t<tr>\n\t\t\t<td>");
            tmpClass = j.getClass();
            result.append(tmpClass.getPackage().getName());
            result.append(". " + tmpClass.getSimpleName());
            result.append("</td>\n\t\t\t<td>");
            if (j.getErrorException().getClass().toString() != null) {
                tmpClass = j.getErrorException().getClass();
                result.append(tmpClass.getPackage().getName());
                result.append(". " + tmpClass.getSimpleName());
                result.append("</td>\n\t\t\t<td>");
            } else {
                result.append("null");
                result.append("</td>\n\t\t\t<td>");    
            }
            result.append(j.getErrorException().getMessage());
            result.append("</td>\n\t\t\t<td>");
            for(StackTraceElement m: j.getErrorException().getStackTrace()) {
                result.append(m.getClassName());
                result.append(". ");
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

    public static String renderLogs() {
        String[] names = sobjLogManager.getRecentEntries();

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

    public static String getSchedulerDetails(String attribute) {
        if (attribute.equals("WAITING")) {
            return String.valueOf(sobjSched.getSchedulerStats().getWaitingJobs());
        }
        else if (attribute.equals("RUNNING")) {
            return String.valueOf(sobjSched.getSchedulerStats().getRunningJobs());
        }
        else if (attribute.equals("WORKER")) {
            return String.valueOf(sobjSched.getSchedulerStats().getWorkerThreads());
        }
        else if (attribute.equals("FAILED")) {
            return String.valueOf(sobjSched.getSchedulerStats().getFailedJobs());
        }
        else if (attribute.equals("TOTAL")) {
            return String.valueOf(sobjSched.getSchedulerStats().getTotalJobs());
        }

        return "";
    }

    /**
     * Returns a string representing the uptime of the Alitheia core
     * in dd:hh:mm:ss format
     */
    public static String getUptime() {
        long remainder;
        long currentTime = new Date().getTime();
        long timeRunning = currentTime - startTime;

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

    public static String renderProjects() {
        sobjDB.startDBSession();
        List projects = sobjDB.doHQL("from StoredProject");
        Collection<PluginInfo> metrics = sobjPA.listPlugins();

        if (projects == null || metrics == null) {
            sobjDB.commitDBSession();
            return "<b>No projects installed</b>";
        }

        StringBuilder s = new StringBuilder();
        
        s.append("<table border=\"1\">");
        s.append("<tr>");
        s.append("<td><b>Project</b></td>");
        
        for(PluginInfo m : metrics) {
            if(m.installed) {
                s.append("<td><b>");
                s.append(m.getPluginName());
                s.append("</b></td>");
            }
        }
        s.append("</tr>\n");
       
        for (int i=0; i<projects.size(); i++) {
            s.append("\t<tr>\n");
            StoredProject p = (StoredProject) projects.get(i);
            s.append("\t\t<!--project--><td><font size=\"-2\"><b>");
            s.append(p.getName());
            s.append("</b> ([id=");
            s.append(p.getId());
            s.append("]) <br/>\nUpdate:");
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
            s.append("</font></td>\n");
            for(PluginInfo m : metrics) {
                if(m.installed) {
                    s.append("\n<td>\n");
                    s.append(sobjMetricActivator.getLastAppliedVersion(sobjPA.getPlugin(m), p));
                    s.append("\n</td>\n");
                }
            }
            s.append("</tr>\n");
        }
        s.append("</table>");
        sobjDB.commitDBSession();
        return s.toString();
    }

    public void addProject(HttpServletRequest request) {        
        
        String name = request.getParameter("name");
        String website = request.getParameter("website");
        String contact = request.getParameter("contact");
        String bts = request.getParameter("bts");
        String mail = request.getParameter("mail");
        String scm = request.getParameter("scm");
        
        addProject(name, website, contact, bts, mail, scm); 
    }
    
    private void addProject(String name, String website, String contact, 
            String bts, String mail, String scm) {
        final String tryAgain = "<p><a href=\"/projects\">Try again</a>.</p>";
        final String returnToList = "<p><a href=\"/projects\">Try again</a>.</p>";
       
        // Avoid missing-entirely kinds of parameters.
        if ( (name == null) ||
             (website == null) ||
             (contact == null) ||
             /*  (bts == null) || FIXME: For now, BTS and Mailing lists can be empty
                 (mail == null) || */
             (scm == null) ) {
            vc.put("RESULTS",
                   "<p>Add project failed because some of the required information was missing.</p>" 
                   + tryAgain);
            return;
        }

        // Avoid adding projects with empty names or SVN.
        if (name.trim().length() == 0 || scm.trim().length() == 0) {
            vc.put("RESULTS", "<p>Add project failed because the project name or Subversion repository were missing.</p>" 
                   + tryAgain);
            return;
        }

        if (sobjDB != null && sobjDB.startDBSession()) {
            StoredProject p = new StoredProject();
            p.setName(name);
            p.setWebsite(website);
            p.setContact(contact);
            p.setBugs(bts);
            p.setRepository(scm);
            p.setMail(mail);

            sobjDB.addRecord(p);
            sobjDB.commitDBSession();

            /* Run a few checks before actually storing the project */
            //1. Duplicate project
            sobjDB.startDBSession();
            HashMap<String, Object> pname = new HashMap<String, Object>();
            pname.put("name", (Object)p.getName());
            if(sobjDB.findObjectsByProperties(StoredProject.class, pname).size() > 1) {
                //Duplicate project, remove
                sobjDB.deleteRecord(sobjDB.findObjectById(StoredProject.class, p.getId()));
                sobjDB.commitDBSession();
                sobjLogger.warn("A project with the same name already exists");
                vc.put("RESULTS","<p>ERROR: A project" +
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
                vc.put("RESULTS","<p>ERROR: Can not access " +
                                         "repository: &lt;" + p.getRepository() + "&gt;," +
                                         " project not added.</p>" + tryAgain);
                //Invalid repository, remove and remove accessor
                sobjDB.deleteRecord(sobjDB.findObjectById(StoredProject.class, p.getId()));
                sobjDB.commitDBSession();
                sobjTDS.releaseAccessor(a);
                return;
            }
            
            sobjTDS.releaseAccessor(a);
            
            // 3. Call the updater and check if it starts
            if (sobjUpdater.update(p, UpdaterService.UpdateTarget.ALL, null)) {
                sobjLogger.info("Added a new project <" + name + "> with ID " +
                                p.getId());
                vc.put("RESULTS",
                                         "<p>New project added successfully.</p>" +
                                         returnToList);
            }
            else {
                sobjLogger.warn("The updater failed to start while adding project");
                sobjDB.deleteRecord(sobjDB.findObjectById(StoredProject.class, p.getId()));
                vc.put("RESULTS","<p>ERROR: The updater failed " +
                                         "to start while adding project. Project was not added.</p>" +
                                         tryAgain);
            }
            sobjDB.commitDBSession();
        }
    }
    
    public void addProjectDir(HttpServletRequest request) {
        String info = request.getParameter("info");
        
        if(info == null || info.length() == 0) {
            vc.put("RESULTS",
                    "<p>Add project failed because some of the required information was missing.</p>"
                    + "<b>" + info + "</b>");
            return;
        }
        
        if (!info.endsWith("info.txt")) {
            vc.put("RESULTS",
                    "<p>The entered path does not include an info.txt file</p> <br/>"
                    + "<b>" + info + "</b>");
            return;
        }
        
        File f = new File(info);
        
        if (!f.exists() || !f.isFile()) {
            vc.put("RESULTS",
                    "<p>The provided path does not exist or is not a file</p> <br/>"
                    + "<b>" + info + "</b>");
            return;
        }
        
        String name = f.getParentFile().getName();
        String bts = "bts:" + f.getParentFile().getAbsolutePath() + "/bugs";
        String mail = "maildir:" + f.getParentFile().getAbsolutePath() + "/mail";
        String scm = "file://" + f.getParentFile().getAbsolutePath() + "/svn";
        
        Pattern wsPattern = Pattern.compile("^Website:?\\s*(http.*)$");
        Pattern ctnPattern = Pattern.compile("^Contact:?\\s*(http.*)$");
        
        String website = "", contact = "";
        
        try {
            LineNumberReader lnr = new LineNumberReader(new FileReader(f));
            String line = null;
            while((line = lnr.readLine()) != null) {
                Matcher m = wsPattern.matcher(line);
                if(m.matches()){
                    website = m.group(1);
                }
                m = ctnPattern.matcher(line);
                if(m.matches()){
                    contact = m.group(1);
                }
            }
        } catch (FileNotFoundException fnfe) {
            vc.put("RESULTS",
                    "<p>Error opeing file info.txt, file vanished?</p> <br/>"
                    + "<b>" + fnfe.getMessage() + "</b>");
            return;
        } catch (IOException e) {
            vc.put("RESULTS",
                    "<p>The provided path does not exist or is not a file</p> <br/>"
                    + "<b>" + info + "</b>");
            return;
        }
        
        addProject(name, website, contact, bts, mail, scm);        
    }

    public void setMOTD(WebadminService webadmin, HttpServletRequest request) {
        webadmin.setMessageOfTheDay(request.getParameter("motdtext"));
        vc.put("RESULTS", 
               "<p>The Message Of The Day was successfully updated with: <i>" +
               request.getParameter("motdtext") + "</i></p>");
    }

    public static void logRequest(String request) {
        sobjLogger.info(request);
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
