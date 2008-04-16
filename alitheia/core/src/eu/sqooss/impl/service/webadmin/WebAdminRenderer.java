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

import java.util.Collection;
import java.util.List;

import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.util.StringUtils;


public class WebAdminRenderer {
    public static String renderList(String[] names) {
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
    public static String getUptime(long startTime, long currentTime) {
        long remainder;
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

    // private String renderProjects(List<StoredProject> projects, Collection<PluginInfo> metrics) {
//         if (projects == null || metrics == null) {
//             return null;
//         }

//         StringBuilder s = new StringBuilder();
        
//         s.append("<table border=\"1\">");
//         s.append("<tr>");
//         s.append("<td><b>Project</b></td>");
        
//         for(PluginInfo m : metrics) {
//             s.append("<td><b>");
//             s.append(m.getPluginName());
//             s.append("</b></td>");
//         }
//         s.append("</tr>");
       
//         for (int i=0; i<projects.size(); i++) {
//             s.append("<tr>");
//             StoredProject p = (StoredProject) projects.get(i);
//             s.append("<td><font size=\"-2\"><b>");
//             s.append(p.getName());
//             s.append("</b> ([id=");
//             s.append(p.getId());
//             s.append("]) <br/>Update:");
//             for (String updTarget: UpdaterService.UpdateTarget.toStringArray()) {
//                 s.append("<a href=\"http://localhost:8088/updater?project=");
//                 s.append(p.getName());
//                 s.append("&target=");
//                 s.append(updTarget);
//                 s.append("\" title=\"Tell the updater to check for new data in this category.\">");
//                 s.append(updTarget);
//                 s.append("</a>&nbsp");
//             }
//             s.append("<br/>Sites: <a href=\"");
//             s.append(p.getWebsite());
//             s.append("\">Website</a>&nbsp;Alitheia Reports");
//             s.append("</font></td>");
//             for(PluginInfo m : metrics) {
//                 s.append("<td>");
//                 s.append(sobjMetricActivator.getLastAppliedVersion(sobjPluginAdmin.getPlugin(m), p));
//                 s.append("</td>");
//             }
//             s.append("</tr>");
//         }
//         s.append("</table>");
//         return s.toString();
//     }
}
