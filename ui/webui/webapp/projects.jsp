<%@ include file="/inc/init.jsp" %>
<%
    title = "Projects";
    //title = "Project: " + selectedProject.getName();
%>
<%@ page session="true" %>
<%@ include file="/inc/header.jsp" %>

<%
if (projectId != null) {
    %> <%@ include file="/views/Project.jsp" %> <%
} else {
    %> <%@ include file="/views/EvaluatedProjectsList.jsp" %> <%
}
%>

<%@ include file="/inc/footer.jsp" %>
