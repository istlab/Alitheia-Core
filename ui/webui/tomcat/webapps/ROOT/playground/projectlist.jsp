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


<jsp:useBean id="projects" class="sqo.ProjectList" scope="session"/> 
<jsp:setProperty name="projects" property="*"/>

<div>
    The currently selected project is
    <strong>
        <%= projects.getCurrentProject() %>
    </strong>.
</div>


<div id="footer">
Copyright 2007 by members of the 
<a href="http://www.sqo-oss.eu/">SQO-OSS consortium</a>.
</div>

</body>

</html>
