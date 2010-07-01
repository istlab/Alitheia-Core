/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProject.ConfigOption;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.tds.BTSAccessor;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.tds.ProjectAccessor;
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
                        //result.append(j.getClass().getPackage().getName());
                        //result.append(". " + j.getClass().getSimpleName());
			result.append(j.toString());
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
        Properties p = new Properties();
        p.put(ConfigOption.PROJECT_NAME, request.getParameter("name"));
        p.put(ConfigOption.PROJECT_WEBSITE, request.getParameter("website"));
        p.put(ConfigOption.PROJECT_CONTACT, request.getParameter("contact"));
        p.put(ConfigOption.PROJECT_BTS_URL, request.getParameter("bts"));
        p.put(ConfigOption.PROJECT_ML_URL, request.getParameter("mail"));
        p.put(ConfigOption.PROJECT_SCM_URL, request.getParameter("scm"));
        
        addProject(p);
    }

    public void addProject(Properties p) {
        final String returnToList = "<p><a href=\"/projects\">Try again</a>.</p>";
        
        String name = p.getProperty(ConfigOption.PROJECT_NAME.getName());
        String bts = p.getProperty(ConfigOption.PROJECT_BTS_URL.getName());
        String scm = p.getProperty(ConfigOption.PROJECT_SCM_URL.getName());
        String mail = p.getProperty(ConfigOption.PROJECT_ML_URL.getName());
        
        // Avoid missing-entirely kinds of parameters.
        if ( (name == null) || (bts == null) || 
             (mail == null) || (scm == null) ) {
            projectFailed("", "Missing information" , 
            		"Add project failed because some of the required information was missing.");
            return;
        }

        // Avoid adding projects with empty names or SVN.
        if (name.trim().length() == 0 || scm.trim().length() == 0) {
            projectFailed("", "Missing information" , "Add project failed because the project name or repository URL were missing.");
            return;
        }

        /* Run a few checks before actually storing the project */
        // 1. Duplicate project
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("name",  name);
        if (!sobjDB.findObjectsByProperties(StoredProject.class, props).isEmpty()) {
            projectFailed(name, "Name Exists", "A project with the same name already exists");
            return;
        }

        // 2. Check for data handlers Add accessor and try to access project resources
        if (!sobjTDS.isURLSupported(scm)) {
            projectFailed(name, "SCM failed", "No appropriate accessor for repository URI: &lt;"
                    + scm + "&gt;");
            return;
        }
        
        if (!sobjTDS.isURLSupported(mail)) {
            projectFailed(name, "Mailing Lists failed", "No appropriate accessor for URI: &lt;"
                    + mail + "&gt;");
            return;
        }
        
        if (!sobjTDS.isURLSupported(bts)) {
            projectFailed(name, "BTS failed", "No appropriate accessor for bug data URI: &lt;"
                    + bts + "&gt;");
            return;
        }

        sobjTDS.addAccessor(Integer.MAX_VALUE, name, bts, mail, scm);
        ProjectAccessor a = sobjTDS.getAccessor(Integer.MAX_VALUE);
        
        try{
            a.getSCMAccessor().getHeadRevision();
            BTSAccessor ba = a.getBTSAccessor(); 
            if (ba == null) {
            	projectFailed(name, "BTS failed",
                    "Bug Accessor failed initialization for URI: &lt;"
                            + bts + "&gt;");
            	return;
            }
        
            MailAccessor ma = a.getMailAccessor();
            if (ma == null) {
            	projectFailed(name, "Mailing Lists failed",
            			"Mailing lists accessor failed initialization for URI: &lt;"
            			+ mail + "&gt;");
            return;
            }
        } catch (InvalidRepositoryException e) {
            projectFailed(name, "SCM failed", "SCM accessor failed initialization for repository URI: &lt;"
                    + scm + "&gt;");
            return;
        } catch (Exception e) {
        	projectFailed(name, "Accessor failed", e.getMessage()); 
        	return;
        } finally {
        	sobjTDS.releaseAccessor(a);
        }
        
        StoredProject sp = new StoredProject(name);
        //The project is now ready to be added 
        sobjDB.addRecord(sp);
        
        //Store all known properties to the database
        for (ConfigOption co : ConfigOption.values()) {
        	String s = p.getProperty(co.getName());
        	
        	if (s == null)
        		continue;
        	
        	String[] subopts = s.split(" ");
        	
        	for (String subopt : subopts) {
        		if (subopt.trim().length() > 0)
        			sp.addConfig(co, subopt.trim());
        	}
        }
       
        sobjTDS.addAccessor(sp.getId(), sp.getName(), sp.getBtsUrl(), sp.getMailUrl(), 
                sp.getScmUrl());
        
        sobjLogger.info("Added a new project <" + name + "> with ID "
                + sp.getId());
        vc.put("RESULTS", "<p>New project added successfully.</p>"
                + returnToList);
        
        sobjUpdater.update(sp, UpdaterService.UpdateTarget.ALL);
    }
    
    private void projectFailed (String project, String error, String reason) {
        final String tryAgain = "<p><p><a href=\"/projects\">Try again</a>.</p></p>";
        
        
        sobjLogger.warn("Error adding project " + project);
        vc.put("RESULTS", "<p><b>ERROR:</b> " + error + "</p>" +
        		  "<p><b>REASON:</b> "  + reason + "</p>"
        		+ tryAgain);
    }

    public void addProjectDir(HttpServletRequest request) {
        String info = request.getParameter("properties");

        if (info == null || info.length() == 0) {
            vc.put("RESULTS",
                    "<p>Add project failed because some of the required information was missing.</p>"
                    + "<b>" + info + "</b>");
            return;
        }
        
        File infoFile = new File(info);
        
        if (!infoFile.exists()) {
        	vc.put("RESULTS","<p>The entered path does exist</p> <br/>");
            return;
        }
        
        File f = null;
        
        if (infoFile.isDirectory()) {
        	File[] contents = infoFile.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					if (name.contentEquals("project.properties"))
						return true;
					return false;
				}
			});
        
        	if (contents.length <= 0) {
        		vc.put("RESULTS",
                        "<p>The entered path does not include a project.properties file</p> <br/>"
                        + "<b>" + info + "</b>");
                return;
        	}
        	
        	f = contents[0];
        	
        } else {
        	if (!info.endsWith("project.properties")) {
        		vc.put("RESULTS",
        				"<p>The entered path does not include a project.properties file</p> <br/>"
        				+ "<b>" + info + "</b>");
        		return;
        	}
        	f = infoFile;
        }


        Properties p = new Properties();
        try {
			p.load(new FileInputStream(f));
		} catch (Exception e1) {
			projectFailed("RESULTS", e1.getMessage(),
                    "<p>The provided path does not exist or is not a file</p> <br/>"
                    + "<b>" + info + "</b>");
			return;
		} 

		if (p.getProperty(ConfigOption.PROJECT_NAME.getName()) == null)
			p.setProperty(ConfigOption.PROJECT_NAME.getName(), 
					f.getParentFile().getName());
		
		String parent = f.getParentFile().getAbsolutePath();
		parent = parent.replace('\\', '/'); //Hack for windows paths
		
		try {
			URI scm = new URI("svn-file", "//" + parent + "/svn", null );
			URI bugs = new URI("bugzilla-xml", "//" + parent + "/bugs", null);
			URI mail = new URI("maildir", "//" + parent + "/mail", null);

			p.setProperty(ConfigOption.PROJECT_BTS_URL.getName(), 
					bugs.toString());
			p.setProperty(ConfigOption.PROJECT_ML_URL.getName(), 
					mail.toString());
			p.setProperty(ConfigOption.PROJECT_SCM_URL.getName(), 
					scm.toString());
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		addProject(p);
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
