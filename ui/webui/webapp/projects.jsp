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
if (selectedProject != null && selectedProject.isValid()) {
    %> <%@ include file="/views/Project.jsp" %> <%
} else {
    %> <%@ include file="/views/EvaluatedProjectsList.jsp" %> <%

}
%>
<%@ include file="/inc/footer.jsp" %>
