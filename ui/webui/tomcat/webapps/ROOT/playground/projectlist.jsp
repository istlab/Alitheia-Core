<%@ include file="/inc/header.jsp" %>

<jsp:useBean id="projects" class="sqo.ProjectList" scope="session"/> 
<jsp:setProperty name="projects" property="*"/>

<div>
    The currently selected project is
    <strong>
        <%= projects.getCurrentProject() %>
    </strong>.
</div>


<%@ include file="/inc/footer.jsp" %>
