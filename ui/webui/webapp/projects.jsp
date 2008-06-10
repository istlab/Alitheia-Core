<%@ include file="/inc/init.jsp" %>
<%
    title = "Evaluated Projects";
    if (selectedProject.isValid()) {
        title = "Project Dashboard " + selectedProject.getName();
    }
%>
<%@ page session="true" %>
<%@ include file="/inc/header.jsp" %>

<%
if (selectedProject.isValid()) {
    %> <%@ include file="/views/Project.jsp" %> <%
} else {
    %>
    <div id="projectslist" class="group">
        <h2>Evaluated Projects</h2>
        <%@ include file="/views/EvaluatedProjectsList.jsp" %> 
    </div>
<%

}
%>
<%@ include file="/inc/footer.jsp" %>
