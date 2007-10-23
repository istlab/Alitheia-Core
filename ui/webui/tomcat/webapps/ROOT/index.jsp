<%@ include file="/inc/header.jsp" %>
<%@ include file="/inc/menu.jsp" %>

<%@ include file="/inc/news.jsp" %>



<div>
    <h2>Here's this playground's testing area</h2>

<%

out.println("Playground");
java.util.Date date = new java.util.Date();

out.println( String.valueOf( date ));
out.println( "Your IP is " );
out.println( request.getRemoteHost());

//response.sendRedirect( "http://www.kde.org");


%>

</div>

<%@ include file="/inc/footer.jsp" %>
