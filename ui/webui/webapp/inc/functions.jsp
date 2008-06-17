<%!

public static String error(String e) {
    return "<p><font color=\"red\">" + e + "</font></p>";
}

public static String versionSelector(Project p) {
    StringBuilder html = new StringBuilder("\n");
    html.append("<form id=\"selectversion\">\n");
    html.append("\t<input type=\"text\" class=\"form\""
        + " name=\"version" + p.getId() + "\""
        + " value=\"\"/>\n");
    html.append("\t<input type=\"submit\" class=\"form\""
        + " value=\"Apply\"/>\n");
    html.append("</form>\n");
    // Shortcuts
    html.append("<br/><strong>Jump to:</strong>\n");
    html.append("<ul>\n");
    html.append("<li><a href=\"?version" + p.getId()
        + "=" + p.getFirstVersion().getNumber() + "\">"
        + "First version</a>\n");
    if (p.countVersions() > 1)
        html.append("<li><a href=\"?version" + p.getId()
            + "=" + p.getLastVersion().getNumber() + "\">"
            + "Last version</a>\n");
    html.append("</ul>\n");
    return html.toString();
}

public static Long getId(String string_id) {
    try {
        return new Long(string_id);
    }
    catch (NumberFormatException e) {
        return null;
    }
}

/**
 * Constructs a list of all parameters contained in the given servlet request.
 *
 * @param request the servlet's request object
 *
 * @return The list of request parameters.
 */
public static String debugRequest (HttpServletRequest request) {
    StringBuilder b = new StringBuilder();
    Enumeration<?> e = request.getParameterNames();
    while (e.hasMoreElements()) {
        String key = (String) e.nextElement();
        b.append(key + "=" + request.getParameter(key) + "<br/>\n");
    }
    return b.toString();
}
%>
