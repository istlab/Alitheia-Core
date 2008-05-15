<%@ include file="/inc/init.jsp" %>
<%
    title = "Evaluated Projects";
    if (selectedProject.isValid()) {
        title = "Project Dashboard <em>" + selectedProject.getName() + "</em>";
    }
%>
<%@ page session="true" %>
<%@ include file="/inc/header.jsp" %>

<%
if (selectedProject.isValid()) {
    %> <%@ include file="/views/Project.jsp" %> <%
} else {
    %>
        <h2>The following projects have been evaluated:</h2>
        <%@ include file="/views/EvaluatedProjectsList.jsp" %> <%

}
%>
<%@ include file="/inc/footer.jsp" %>
