<%@ include file="/inc/init.jsp" %>
<%
    title = "Metric Result";
%>
<%@ page session="true" %>
<%@ include file="/inc/header.jsp" %>

<%
Long resultId = null;
if (request.getParameter("rid") != null) {
    String req = request.getParameter("rid");
    if (!"none".equals(req)) {
        resultId = getId(req);
    }
}

if (resultId == null) {
    out.println(error("No id specified in request. Ain't got nothing to show."));
} else {
    out.println("<h2>Results for " + resultId + "</h2>");
    Result r = new Result(resultId, terrier);
    out.println(r.getHtml());
}

%>
<%@ include file="/inc/footer.jsp" %>
