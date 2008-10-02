<%@ include file="/inc/init.jsp"
%><%@ page session="true"
%><%
//============================================================================
// Check is there is a connection with the SQO-OSS framework
//============================================================================
if (terrier.isConnected()) {
    title = "Homepage";
%><%@ include file="/inc/header.jsp"
%><%@ include file="/inc/news.jsp"
%><%@ include file="/views/ProjectCloud.jsp"
%><%@ include file="/views/ProjectIntro.jsp"
%><%
}
//============================================================================
//Can not establish a connection with the SQO-OSS framework
//============================================================================
else {
%><%@ include file="/inc/Disconnected.jsp"
%><%
}
%><%@ include file="/inc/footer.jsp" %>
