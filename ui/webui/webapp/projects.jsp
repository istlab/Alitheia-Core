<%@ include file="/inc/init.jsp" %>
<%
    title = "Projects";
    if (selectedProject != null) {
        title = "Project: " + selectedProject.getName();
    }
%>
<%@ page session="true" %>
<%@ include file="/inc/header.jsp" %>

<%
if (selectedProject != null && selectedProject.isValid()) {
    %>Project <%@ include file="/views/Project.jsp" %> <%
} else {
    %>NoProject <%@ include file="/views/EvaluatedProjectsList.jsp" %> <%

}
%>
<%@ include file="/inc/footer.jsp" %>
