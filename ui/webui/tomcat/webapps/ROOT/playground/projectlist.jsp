<%@ include file="/inc/header.jsp" %>
<%@ include file="/inc/menu.jsp" %>

<jsp:useBean id="projects" class="sqo.ProjectList" scope="session"/> 
<jsp:setProperty name="projects" property="*"/>

<div>
    The currently selected project is
    <strong>
        <%= projects.getCurrentProject() %>
    </strong>.
    <br />
    
    Other available projects are:
    <ul>
    
    <% // Let's list all projects.
    
    projects.getCurrentProject();
    String[] allProjects;
    allProjects = projects.getAllProjects();
    
    for (int i = 0; i < allProjects.length; i++) {
        out.println("<li>" + allProjects[i] + "</li>");
    }

    %>
    </ul>
</div>

<%@ include file="/inc/footer.jsp" %>
