<%@ page import="java.util.*" %>
<%@ include file="/inc/header.jsp" %>

<jsp :useBean id="user" class="sqo.User" scope="session"/> 
<jsp :setProperty name="user" property="*"/>

<div>


    Available users are:
    <ul>
    
    <% 
    String uid = request.getParameter("uid");
    if ( (uid!=null) && uid.length() > 0) {
        //user.setCurrentUserId(new Integer(request.getParameter("uid")));
        //out.println("<h2>Hello " + user.getCurrentUser() + "!<h2>");
        out.println("Bla");
    } else {
        out.println("Foo");
        // Let's list all users.
        //out.println("Your User ID is not known to the system yet.");
        //out.println("<h2>You are " + user.getCurrentUser() + "!</h2>");
        
        /*
        Dictionary allUsers = user.getAllUsers();
    
        for (Integer i = 0; i < allUsers.size(); i++) {
            out.println("<li>" + allUsers.get(i) + "</li>");
        }
        */
    }
    %>
    </ul>
</div>


<%@ include file="/inc/footer.jsp" %>
