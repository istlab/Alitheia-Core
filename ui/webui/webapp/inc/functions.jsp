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
%>
