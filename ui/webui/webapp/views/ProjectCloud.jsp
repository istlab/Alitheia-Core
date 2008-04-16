<%@ page import="eu.sqooss.webui.*" %>

<%
ProjectsListView.retrieveData(terrier);
%>
<div id="tagcloud" class="group">
<%
if (ProjectsListView.hasProjects()) {
    java.util.Vector<Project> v = ProjectsListView.getCloudProjects();
    // This wants to print the vector v (max. 13 elements)
    // in a 5x5 matrix.
    String m[][] = new String[5][5];
    for (int i=0; i<5; ++i) {
        for (int j=0; j<5; ++j) {
             m[i][j] = null;
        }
    }
    java.util.Random r = new java.util.Random();
    double fontsize = 2.0; /* em */
    for (Project p : v) {
        int i = r.nextInt(5);
        int j = r.nextInt(5);

        if (m[i][j] == null) {
             m[i][j] = new String("<a href=" +
                            "\"projects.jsp?pid=" + p.getId() + "\" " +
                            "style=\"font-size: " + fontsize + "em;\" " +
                            ">" + p.getName() + "</a>");
        }

        fontsize = fontsize - 0.1;
    }

    out.println("<table cellspacing=0 cellpadding=4>");
    for (int i=0; i<5; ++i) {
        out.println("<tr>");
        for (int j=0; j<5; ++j) {
             if (m[i][j] == null) {
                 out.println("<td>&nbsp;</td>");
             } else {
                 out.println("<td width=\"20%\">" + m[i][j] + "</td>");
             }
        }
        out.println("</tr>");
    }
    out.println("</table>");
} else {
    out.println("The project tag cloud is not available right now.");
}
%>
</div>

