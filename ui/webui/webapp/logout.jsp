<%@ include file="/inc/init.jsp" %>
<%
    title = "Logout";
    if (user.isLoggedIn) {
      terrier.logoutUser(user.getName());
      response.sendRedirect("/login.jsp");
    }
    out.println ("You must login first!");
    out.println ("<br />");
%>