<%!

public static String versionSelector(Long projectId, Long currentVer) {
    String form =
        "<form id=\"selectversion\">"
        + "Current version:"
        + "&nbsp;"
        + "<input type=\"text\" name=\"version" + projectId
            + "\" value=\"" + currentVer+ "\" class=\"form\"/>"
        + "&nbsp;"
        + "<input type=\"submit\" value=\"Change\"  class=\"form\"/>"
        + "</form>";
    //String form = "bl\'\"a";
    return form;
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
