<%@ include file="/inc/init.jsp"
%><%@ page session="true"
%><%
//============================================================================
// Check is there is a connection with the SQO-OSS framework
//============================================================================
if (terrier.isConnected()) {
    title = "Metrics";
%><%@ include file="/inc/header.jsp"
%><%@ include file="/views/MetricData.jsp"
%><%
}
//============================================================================
// Can not establish a connection with the SQO-OSS framework
//============================================================================
else {
%><%@ include file="/inc/Disconnected.jsp"
%><%
}
%><%@ include file="/inc/footer.jsp"
%>
