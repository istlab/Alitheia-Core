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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.TDAccessor;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.util.StringUtils;
import eu.sqooss.service.webadmin.WebadminService;

/**
 * The WebAdminRender class provides functions for rendering content
 * to be displayed within the WebAdmin interface.
 *
 * @author, Paul J. Adams <paul.adams@siriusit.co.uk>
 * @author, Boryan Yotov <b.yotov@prosyst.com>
 */
public class WebAdminRenderer  extends AbstractView {
    /**
     * Represents the system time at which the WebAdminRender (and
     * thus the system) was started. This is required for the system
     * uptime display.
     */
    private static long startTime = new Date().getTime();

    public WebAdminRenderer(BundleContext bundlecontext, VelocityContext vc) {
        super(bundlecontext, vc);
    }

    /**
     * Creates and HTML table displaying the details of all the jobs
     * that have failed whilst the system has been up
     *
     * @return a String representing the HTML table
     */
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

        String[] jobfailures = fjobs.keySet().toArray(new String[1]);
        for(String key : jobfailures) {
            result.append("\t\t<tr>\n\t\t\t<td>");
            result.append(key==null ? "No failures" : key);
            result.append("</td>\n\t\t\t<td>");
            result.append(key==null ? "&nbsp;" : fjobs.get(key));
            result.append("\t\t\t</td>\n\t\t</tr>");
        }
        result.append("\t</tbody>\n");
        result.append("</table>");
        return result.toString();
    }

    /**
     * Creates an HTML table displaying the details of all the jobs
     * that are waiting whilst the system is up
     *
     * @return a String representing the HTML table
     */
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

        if ((jobs != null) && (jobs.length > 0)) {
            for(Job j: jobs) {
                if (j == null) continue;
                result.append("\t\t<tr>\n\t\t\t<td>");
                if (j.getClass() != null) {
                    try {
                        result.append(j.getClass().getPackage().getName());
                        result.append(". " + j.getClass().getSimpleName());
                    }
                    catch (NullPointerException ex) {
                        result.append("<b>NA<b>");
                    }
                }
                else {
                    result.append("<b>NA<b>");
                }
                result.append("</td>\n\t\t\t<td>");
                Exception e = j.getErrorException();
                if (e != null) {
                    try {
                        result.append(e.getClass().getPackage().getName());
                        result.append(". " + e.getClass().getSimpleName());
                    }
                    catch (NullPointerException ex) {
                        result.append("<b>NA<b>");
                    }
                }
                else {
                    result.append("<b>NA</b>");
                }
                result.append("</td>\n\t\t\t<td>");
                try {
                    result.append(e.getMessage());
                }
                catch (NullPointerException ex) {
                    result.append("<b>NA<b>");
                }
                result.append("</td>\n\t\t\t<td>");
                if ((e != null)
                        && (e.getStackTrace() != null)) {
                    for(StackTraceElement m: e.getStackTrace()) {
                        if (m == null) continue;
                        result.append(m.getClassName());
                        result.append(". ");
                        result.append(m.getMethodName());
                        result.append("(), (");
                        result.append(m.getFileName());
                        result.append(":");
                        result.append(m.getLineNumber());
                        result.append(")<br/>");
                    }
                }
                else {
                    result.append("<b>NA</b>");
                }
                result.append("\t\t\t</td>\n\t\t</tr>");
            }
        }
        else {
            result.append ("<tr><td colspan=\"4\">No failed jobs.</td></tr>");
        }
        result.append("\t</tbody>\n");
        result.append("</table>");

        return result.toString();
    }

    /**
     * Creates an HTML unordered list displaying the contents of the current system log
     *
     * @return a String representing the HTML unordered list items
     */
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

        StoredProject p = new StoredProject();
        p.setName(name);
        p.setWebsite(website);
        p.setContact(contact);
        p.setBugs(bts);
        p.setRepository(scm);
        p.setMail(mail);

        /* Run a few checks before actually storing the project */
        // 1. Duplicate project
        
        HashMap<String, Object> pname = new HashMap<String, Object>();
        pname.put("name", (Object) p.getName());
        if (!sobjDB.findObjectsByProperties(StoredProject.class, pname).isEmpty()) {
            sobjLogger.warn("A project with the same name already exists");
            vc.put("RESULTS", "<p>ERROR: A project" + " with the same name ("
                    + p.getName() + ") already exists. "
                    + "Project not added.</p>" + tryAgain);
            return;
        }

        // 2. Add accessor and try to access project resources
        sobjTDS.addAccessor(p.getId(), p.getName(), p.getBugs(), p.getMail(), 
                p.getRepository());
        TDAccessor a = sobjTDS.getAccessor(p.getId());

        try {
            a.getSCMAccessor().getHeadRevision();
            // FIXME: fix this when we have a proper bug accessor
            if (bts != null) {
                // Bug b = a.getBTSAccessor().getBug(1);
            }
            if (mail != null) {
                // FIXME: fix this when the TDS supports returning
                // list information
                // a.getMailAccessor().getNewMessages(0);
            }
        } catch (InvalidRepositoryException e) {
            sobjLogger.warn("Error accessing repository. Project not added");
            vc.put("RESULTS", "<p>ERROR: Can not access " + "repository: &lt;"
                    + p.getRepository() + "&gt;," + " project not added.</p>"
                    + tryAgain);
            // Invalid repository, remove and remove accessor
            sobjTDS.releaseAccessor(a);
        } 
        
        //The project is now ready to be added 
        sobjDB.addRecord(p);
        //Remove accessor for unregistered project
        sobjTDS.releaseAccessor(a);
        sobjTDS.addAccessor(p.getId(), p.getName(), p.getBugs(), p.getMail(), 
                p.getRepository());
        
        sobjLogger.info("Added a new project <" + name + "> with ID "
                + p.getId());
        vc.put("RESULTS", "<p>New project added successfully.</p>"
                + returnToList);
        
        sobjUpdater.update(p, UpdaterService.UpdateTarget.ALL, null);
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
        Pattern ctnPattern = Pattern.compile("^Contact:?\\s*(.*)$");

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

    /**
     * Allows the user to set the message of the day to be displayed within the WebUI
     */
    public void setMOTD(WebadminService webadmin, HttpServletRequest request) {
        webadmin.setMessageOfTheDay(request.getParameter("motdtext"));
        vc.put("RESULTS",
               "<p>The Message Of The Day was successfully updated with: <i>" +
               request.getParameter("motdtext") + "</i></p>");
    }

    public static String renderJobWaitStats() {
        StringBuilder result = new StringBuilder();
        HashMap<String,Integer> wjobs = sobjSched.getSchedulerStats().getWaitingJobTypes();
        result.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n");
        result.append("\t<thead>\n");
        result.append("\t\t<tr>\n");
        result.append("\t\t\t<td>Job Type</td>\n");
        result.append("\t\t\t<td>Num Jobs Waiting</td>\n");
        result.append("\t\t</tr>\n");
        result.append("\t</thead>\n");
        result.append("\t<tbody>\n");

        String[] jobfailures = wjobs.keySet().toArray(new String[1]);
        for(String key : jobfailures) {
            result.append("\t\t<tr>\n\t\t\t<td>");
            result.append(key==null ? "No failures" : key);
            result.append("</td>\n\t\t\t<td>");
            result.append(key==null ? "&nbsp;" : wjobs.get(key));
            result.append("\t\t\t</td>\n\t\t</tr>");
        }
        result.append("\t</tbody>\n");
        result.append("</table>");
        return result.toString();
    }

    public static String renderJobRunStats() {
        StringBuilder result = new StringBuilder();
        List<String> rjobs = sobjSched.getSchedulerStats().getRunJobs();
        if (rjobs.size() == 0) {
            return "No running jobs";
        }
        result.append("<ul>\n");
        for(String s : rjobs) {
            result.append("\t<li>");
            result.append(s);
            result.append("\t</li>\n");
        }
        result.append("</ul>\n");
        return result.toString();
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
