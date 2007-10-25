<%@ page import="java.util.*" %>
<%@ include file="/inc/header.jsp" %>

<jsp :useBean id="user" class="eu.sqooss.webui.User" scope="session"/> 
<jsp :setProperty name="user" property="*"/>

<h1>Users</h1>

    <%
    String uid = request.getParameter("uid");
    if ( (uid!=null) && uid.length() > 0 ) {
        //user.setCurrentUserId(new Integer(request.getParameter("uid")));
        //out.println("<h2>Hello " + user.getCurrentUser() + "!<h2>");
        //String currentUser = user.getCurrentUser();
        out.println("Available users:");
        out.println("<ul><li> Foobar </li></ul>");
        out.println("<br />[ UID supplied ]");
    } else {
        //out.println("Foo");
        // Let's list all users.
        out.println("Your User ID is not known to the system yet.");
        //out.println("<h2>You are " + user.getCurrentUser() + "!</h2>");
        
        /*
        Dictionary allUsers = user.getAllUsers();
    
        for (Integer i = 0; i < allUsers.size(); i++) {
            out.println("<li>" + allUsers.get(i) + "</li>");
        }
        */
    }
    %>

<%@ include file="/inc/footer.jsp" %>
