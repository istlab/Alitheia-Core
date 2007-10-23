<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page session="true" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
    <title><%= application.getServerInfo() %></title>
    <link rel='stylesheet' type='text/css' href='alitheia.css' />
</head>

<body>
<div id="banner">
<img class="sqologo" src="sqo-oss-logo.jpg" alt="SQO-OSS Project Logo" />
<img class="alitheialogo" src="alitheia.png" alt="Alitheia Logo" />
</div>

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

<div id="footer">
Copyright 2007 by members of the 
<a href="http://www.sqo-oss.eu/">SQO-OSS consortium</a>.
</div>

</body>

</html>

