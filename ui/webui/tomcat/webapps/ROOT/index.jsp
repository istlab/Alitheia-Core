<%@ include file="/inc/header.jsp" %>

<div id="status">
<!-- First call to cruncher to obtain status -->
The cruncher is offline.
</div>

<div id="menu">
<ul>
<li>About</li>
</ul>
</div>

<div id="news">
News items would be here
for this specific deployment.
</div>
<hr />

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
