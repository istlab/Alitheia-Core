<%!

public static String error(String e) {
    return "<p><font color=\"red\">" + e + "</font></p>";
}

public static String versionSelector(Project p) {
    StringBuilder html = new StringBuilder("\n<form id=\"selectversion\">");
    html.append("\n\t<select name=\"version" + p.getId() + "\" class=\"form\">");
    SortedMap<Long, Version>versions = p.getVersions();
    for (Long k: versions.keySet()) {
        Version v = versions.get(k);
        if (k.equals(p.getCurrentVersionId())) {
            html.append("\n\t\t<option  class=\"form\" value=\""
                + v.getId() + "\" selected=\"true\">" + v.shortName() + "</option>");
        } else {
            html.append("\n\t\t<option  class=\"form\" value=\""
                + v.getId() + "\">" + v.shortName() +  "</option>");
        }
    }
    html.append("\n\t</select>");
    html.append("\n\t<input type=\"submit\" value=\"Go\" class=\"form\" /> ");
    html.append("\n</form>");
    return html.toString();
}

public static Long getId(String string_id) {
    Long id = null;
    try {
        id = new Long(string_id);
    }
    catch (NumberFormatException e) {
        //inputError = new String(string_id + " doesn't make sense!");
        return null;
    }
    return id;
}

%>
