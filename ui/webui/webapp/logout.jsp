<%@ include file="/inc/init.jsp" %>
<%
    title = "Logout";
    user.logout();
    response.sendRedirect("/login.jsp");
%>