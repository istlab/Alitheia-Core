<%@ include file="/inc/init.jsp"
%><%@ page session="true"
%><%
// Indentation depth
in = 6;
// Check is there is a connection with the SQO-OSS framework
if (terrier.isConnected()) {
    title = "Homepage";
%><%@ include file="/inc/header.jsp"
%><%@ include file="/inc/news.jsp"
%><%@ include file="/views/ProjectCloud.jsp"
%><%@ include file="/views/ProjectIntro.jsp"
%><%
}
// Can not establish a connection with the SQO-OSS framework
else {
    title = "Disconnected";
%><%@ include file="/inc/header.jsp"
%><%
    out.print(sp(in) + Functions.error("Alitheia is offline."));
}
%><%@ include file="/inc/footer.jsp" %>
