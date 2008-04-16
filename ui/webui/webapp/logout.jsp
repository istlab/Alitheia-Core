<%@ include file="/inc/init.jsp" %>
<%@ page session="true" %>

<%
    title = "Logout";
%>

<%@ include file="/inc/header.jsp" %>

<%
    // Display page's title and accumulated errors (if any)
    out.println("<h1>Logout from the Alitheia System</h1>");
    errorMsg += "<tr />" + terrier.getError();
    out.println("<font color=\"red\">" + errorMsg + "</font>");
    out.println("<br />");
    
    if (user.getLoggedIn()) {
      terrier.logoutUser(user.getName());
      response.sendRedirect("/login.jsp");
    }
    
    out.println ("You must login first!");
    out.println ("<br />");
%>

<%@ include file="/inc/footer.jsp" %>