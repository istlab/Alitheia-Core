<%@ include file="/inc/init.jsp" %>
<%
    title = "Evaluated Projects";
    if (selectedProject.isValid()) {
        title = "Project: " + selectedProject.getName();
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
